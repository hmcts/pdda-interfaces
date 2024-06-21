package uk.gov.hmcts.pdda.business.services.pdda.lighthouse;

import jakarta.ejb.ApplicationException;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.framework.scheduler.RemoteTask;
import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundDao;
import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundRepository;
import uk.gov.hmcts.pdda.business.entities.xhbpddamessage.XhbPddaMessageDao;
import uk.gov.hmcts.pdda.business.entities.xhbpddamessage.XhbPddaMessageRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * This class is the controller bean for dealing with inserting CPP data into XHB_CPP_STAGING_INBOUND and
 * updating XHB_PDDA_MESSAGE.
 *
 * <p>It will check for the existence of (unprocessed) records in the database -
 *  for each record it will add an entry to XHB_CPP_STAGING_INBOUND - once processed it will then update the
 * message record to show it has been processed.
 */
@Stateless
@Service
@Transactional
@LocalBean
@ApplicationException(rollback = true)
public class LighthousePddaControllerBean extends LighthousePddaControllerBeanHelper implements RemoteTask {

    private static final DateTimeFormatter DATETIMEFORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final Integer FILE_PARTS = 3;
    private static final String NEW_STAGING_INBOUND_STATUS = "NP";
    private static final String MESSAGE_STATUS_INPROGRESS = "IP";
    private static final String MESSAGE_STATUS_PROCESSED = "VP";
    private static final String MESSAGE_STATUS_INVALID = "INV";
    private static final Logger LOG = LoggerFactory.getLogger(LighthousePddaControllerBean.class);

    @Autowired
    public LighthousePddaControllerBean(XhbPddaMessageRepository xhbPddaMessageRepository,
            XhbCppStagingInboundRepository xhbCppStagingInboundRepository, EntityManager entityManager) {
        super();
        this.xhbPddaMessageRepository = xhbPddaMessageRepository;
        this.xhbCppStagingInboundRepository = xhbCppStagingInboundRepository;
        this.entityManager = entityManager;
    }

    public LighthousePddaControllerBean(EntityManager entityManager) {
        super();
        this.entityManager = entityManager;
    }

    public LighthousePddaControllerBean() {
        super();
    }

    @Override
    public void doTask() {

        List<XhbPddaMessageDao> xhbPddaMessageDaos = getXhbPddaMessageRepository().findByLighthouse();
        xhbPddaMessageDaos.forEach(this::processFile);
    }

    public void processFile(XhbPddaMessageDao dao) {

        writeToLog("About to process file " + dao.getCpDocumentName());

        try {
            // split up the filename into its 3 parts : type_courtCode_dateTime
            String[] fileParts = dao.getCpDocumentName().split("_");
            if (fileParts.length == FILE_PARTS) {
                // Update the status to indicate it is being processed
                updatePddaMessageStatus(dao, MESSAGE_STATUS_INPROGRESS);

                String strTimeLoaded = fileParts[2].replaceAll(".xml", "");
                LocalDateTime timeLoaded = LocalDateTime.parse(strTimeLoaded.trim(), DATETIMEFORMAT);

                // Insert XHB_CPP_STAGING_INBOUND row, returning PK
                writeToLog("About to add " + dao.getPddaMessageDataId() + " to the cpp staging inbound table");
                Integer stagingInboundId = insertStaging(dao.getCpDocumentName(), fileParts[1],
                        getDocType(fileParts[0]), timeLoaded, dao.getPddaMessageDataId());
                writeToLog("Successfully added  " + stagingInboundId + " to the cpp staging inbound table");

                // Update XHB_PDDA_MESSAGE record to indicate success
                updatePddaMessage(stagingInboundId, dao);

                writeToLog("Processing of " + dao.getCpDocumentName() + " completed");
            } else {
                LOG.error("Filename is not valid : {}", dao.getCpDocumentName());
            }
        } catch (RuntimeException e) {
            LOG.error(
                    "Error adding data to the database for file {} :{}",
                    dao.getCpDocumentName(), e.getStackTrace());
            // Change the status of the XHB_PDDA_MESSAGE record to invalid
            updatePddaMessageStatus(dao, MESSAGE_STATUS_INVALID);
        }
    }

    /**
     * Write to the debug log if debug is enabled.
     *
     * @param string Debug message to write to the log
     */
    private void writeToLog(final String string) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("{} :: {}", System.currentTimeMillis(), string);
        }
    }

    private XhbPddaMessageDao fetchLatestXhbPddaMessageDao(XhbPddaMessageDao dao) {
        LOG.debug("fetchLatestXhbPddaMessageDao()");
        Optional<XhbPddaMessageDao> opt = getXhbPddaMessageRepository().findById(dao.getPrimaryKey());
        return opt.isPresent() ? opt.get() : dao;
    }

    /**
     * Insert the row into XHB_CPP_STAGING_INBOUND.
     *
     * @param docName - Document name e.g. DailyList_453_20200101123213.xml
     * @param courtCode from the document name
     * @param documentType e.g. DL
     * @param timeLoaded which is the 3rd part of the document name
     * @param clobId that was created pre this insert
     * @return the staging inbound id for debugging/logging purposes
     * @throws SQLException Exception
     */
    private Integer insertStaging(final String docName, final String courtCode, final String documentType,
            final LocalDateTime timeLoaded, final Long clobId) {

        writeToLog(
                "doc " + docName + " courtCode: " + courtCode + " documentType: " + documentType + " timeLoaded: "
                        + timeLoaded + " clobId :" + clobId + " validationStatus :" + NEW_STAGING_INBOUND_STATUS);

        XhbCppStagingInboundDao xhbCppStagingInboundDao = new XhbCppStagingInboundDao();
        xhbCppStagingInboundDao.setDocumentName(docName);
        xhbCppStagingInboundDao.setCourtCode(courtCode);
        xhbCppStagingInboundDao.setDocumentType(documentType);
        xhbCppStagingInboundDao.setTimeLoaded(timeLoaded);
        xhbCppStagingInboundDao.setClobId(clobId);
        xhbCppStagingInboundDao.setValidationStatus(NEW_STAGING_INBOUND_STATUS);
        Optional<XhbCppStagingInboundDao> opt = getXhbCppStagingInboundRepository().update(xhbCppStagingInboundDao);
        return opt.isPresent() ? opt.get().getCppStagingInboundId() : null;
    }

    /**
     * Update the XHB_PDA_MESSAGE record.
     *
     * @param stagingInboundId - cpp staging inbound id
     * @throws SQLException Exception
     */
    private void updatePddaMessage(final Integer stagingInboundId, XhbPddaMessageDao dao) {

        writeToLog("doc " + dao.getCpDocumentName() + " docStatus: " + MESSAGE_STATUS_PROCESSED + " messageId: "
                + dao.getPddaMessageId());

        XhbPddaMessageDao latest = fetchLatestXhbPddaMessageDao(dao);
        latest.setCpDocumentStatus(MESSAGE_STATUS_PROCESSED);
        latest.setCppStagingInboundId(stagingInboundId);
        getXhbPddaMessageRepository().update(latest);

    }

    /**
     * Updates the XHB_PDDA_MESSAGE record.
     */
    private void updatePddaMessageStatus(XhbPddaMessageDao dao, String messageStatus) {
        LOG.debug("updatePddaMessageStatus");
        XhbPddaMessageDao latest = fetchLatestXhbPddaMessageDao(dao);
        latest.setCpDocumentStatus(messageStatus);
        getXhbPddaMessageRepository().update(latest);
    }

    /**
     * Return the document type depending on what's been used in the file name.
     *
     * @param fileType portion of the filename
     * @return shorthand documentType
     */
    private String getDocType(final String fileType) {
        writeToLog("METHOD ENTRY: getDocType");

        DocumentType docType = DocumentType.fromString(fileType);
        if (docType == null) {
            return null;
        } else {
            return docType.name();
        }
    }

}
