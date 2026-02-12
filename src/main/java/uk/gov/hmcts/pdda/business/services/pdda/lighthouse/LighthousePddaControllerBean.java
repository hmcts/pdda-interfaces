package uk.gov.hmcts.pdda.business.services.pdda.lighthouse;

import jakarta.ejb.ApplicationException;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import uk.gov.hmcts.framework.scheduler.RemoteTask;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobDao;
import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundDao;
import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundRepository;
import uk.gov.hmcts.pdda.business.entities.xhbinternethtml.XhbInternetHtmlDao;
import uk.gov.hmcts.pdda.business.entities.xhbinternethtml.XhbInternetHtmlRepository;
import uk.gov.hmcts.pdda.business.entities.xhbpddamessage.XhbPddaMessageDao;
import uk.gov.hmcts.pdda.business.entities.xhbpddamessage.XhbPddaMessageRepository;
import uk.gov.hmcts.pdda.business.entities.xhbxmldocument.XhbXmlDocumentDao;
import uk.gov.hmcts.pdda.business.services.pdda.CpDocumentStatus;

import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * This class is the controller bean for dealing with inserting CPP data into XHB_CPP_STAGING_INBOUND and
 * updating XHB_PDDA_MESSAGE.

 * It will check for the existence of (unprocessed) records in the database -
 *  for each record it will add an entry to XHB_CPP_STAGING_INBOUND - once processed it will then update the
 * message record to show it has been processed.
 */
@Stateless
@Service
@LocalBean
@ApplicationException(rollback = true)
@SuppressWarnings("PMD")
public class LighthousePddaControllerBean extends LighthousePddaControllerBeanHelper implements RemoteTask {

    private static final DateTimeFormatter DATETIMEFORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final Integer FILE_PARTS = 3;
    private static final String NEW_STAGING_INBOUND_STATUS = "NP"; // Not (yet) Processed
    private static final String MESSAGE_STATUS_INPROGRESS = "IP"; // In Progress
    private static final String MESSAGE_STATUS_PROCESSED = "VP"; // Validated and Processed
    private static final String MESSAGE_STATUS_INVALID = "INV"; // Invalid
    private static final String MESSAGE_STATUS_VALID_NOT_PROCESSED = "VN"; // PddaMessage status valid not processed
    private static final String MESSAGE_STATUS_PROCESSING_UNNECESSARY = "PU"; // Processing Unnecessary
    private static final Logger LOG = LoggerFactory.getLogger(LighthousePddaControllerBean.class);
    private static final String IWP_STATUS_CREATED = "C"; // Created
    private static final String ND_STATUS_XML_DOC = "ND";
    private static final String IWP = "IWP";
    private static final String CPP_CASE = "CPP";
    private static final String RECENT_LISTS_LOOKUP_TIMEFRAME = "RECENT_LISTS_LOOKUP_TIMEFRAME";
    private static final String MAX_ON_HOLD_TIMEFRAME = "MAX_ON_HOLD_TIMEFRAME";
    private static final String DUPLICATE_DOCUMENT_ERROR_MESSAGE = "Duplicate document";

    @Autowired
    public LighthousePddaControllerBean(XhbPddaMessageRepository xhbPddaMessageRepository,
            XhbCppStagingInboundRepository xhbCppStagingInboundRepository, 
            XhbInternetHtmlRepository xhbInternetHtmlRepository,
            EntityManager entityManager) {
        super();
        this.xhbPddaMessageRepository = xhbPddaMessageRepository;
        this.xhbCppStagingInboundRepository = xhbCppStagingInboundRepository;
        this.xhbInternetHtmlRepository = xhbInternetHtmlRepository;
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
        LOG.debug("Lighthouse -- doTask() - entered");
        List<XhbPddaMessageDao> standardXhbPddaMessageDaos =
            getXhbPddaMessageRepository().findByLighthouseSafe();
        LOG.debug("Standard Messages to process: {}", standardXhbPddaMessageDaos.size());
        
        // Process standard documents
        AtomicInteger standardDocNumber = new AtomicInteger(1);
        standardXhbPddaMessageDaos.forEach(dao -> {
            LOG.debug("Lighthouse Processing file number: {}: {}, on thread: {}",
                standardDocNumber, dao.getCpDocumentName(), Thread.currentThread().getName());
            processFile(dao);
            standardDocNumber.incrementAndGet();
        });
        
        List<XhbPddaMessageDao> onHoldXhbPddaMessageDaos =
            getXhbPddaMessageRepository().findByLighthouseOnHoldSafe();
        LOG.debug("On Hold Messages to process: {}", onHoldXhbPddaMessageDaos.size());
        
        // Process On Hold documents
        AtomicInteger onHoldDocNumber = new AtomicInteger(1);
        onHoldXhbPddaMessageDaos.forEach(dao -> {
            LOG.debug("Lighthouse Processing file number: {}: {}, on thread: {}",
                onHoldDocNumber, dao.getCpDocumentName(), Thread.currentThread().getName());
            try {
                processOnHoldFile(dao);
            } catch (ParserConfigurationException | SAXException | IOException e) {
                LOG.error(e.getMessage());
            }
            onHoldDocNumber.incrementAndGet();
        });
        
        // Final check to see if theres any lists still on hold which have exceeded the on hold timeframe
        checkForListsExceedingOnHoldTimeframe();
        
        LOG.debug("Lighthouse -- doTask() - completed");
    }

    public void processFile(XhbPddaMessageDao dao) {

        writeToLog("About to process file " + dao.getCpDocumentName());

        List<XhbCppStagingInboundDao> xhbCppStagingInboundDaos = 
            getXhbCppStagingInboundRepository()
                .findDocumentByDocumentNameSafe(dao.getCpDocumentName());
        
        LOG.debug("Checked for existing entries in XHB_CPP_STAGING_INBOUND for file: {} - found {} entries",
            dao.getCpDocumentName(), xhbCppStagingInboundDaos.size());
        
        // Check the file hasn't already got a record in XHB_CPP_STAGING_INBOUND
        if (!xhbCppStagingInboundDaos.isEmpty()) {
            // Fetch the latest XHB_PDDA_MESSAGE record for this file
            XhbPddaMessageDao latest = fetchLatestXhbPddaMessageDao(dao);
            // If the status is VN then this document must be a duplicate
            if (MESSAGE_STATUS_VALID_NOT_PROCESSED.equals(latest.getCpDocumentStatus())) {
                LOG.warn("The file: {}{}{}", latest.getCpDocumentName(), 
                    " has already been sent for processing, setting it to: ", MESSAGE_STATUS_INVALID);
                updatePddaMessageStatus(latest, MESSAGE_STATUS_INVALID, 
                    "Duplicate document - already processed");
            }
            return;
        }
        
        // Update the status to indicate it is being processed
        updatePddaMessageStatus(dao, MESSAGE_STATUS_INPROGRESS, null);
        
        // Fetch the latest XHB_PDDA_MESSAGE record for this file
        
        LOG.debug("File {} has not been processed before, proceeding with processing", dao.getCpDocumentName());
        
        try {
            // First check the filename is valid
            if (!isDocumentNameValid(dao.getCpDocumentName())) {
                LOG.error("Filename is not valid : {}", dao.getCpDocumentName());
                // Change the status of the XHB_PDDA_MESSAGE record to invalid
                updatePddaMessageStatus(dao, MESSAGE_STATUS_INVALID, "Filename is not valid");
                return;
            }

            LOG.debug("Processing Filename : {}", dao.getCpDocumentName());
            
            // First we need to do a further filter on XHIBIT data to:
            // 1. not process public display events from XHIBIT
            // 2. ensure lists from XHIBIT get processed
            String documentName = getDocumentNameToProcess(dao.getCpDocumentName());

            LOG.debug("Document name to process: {}", documentName);
            
            if (documentName.length() > 0) {
                // Now add the data into the XHB_CPP_STAGING_INBOUND table

                // split up the filename into its 3 parts : type_courtCode_dateTime
                String[] fileParts = documentName.split("_");
                if (fileParts.length == FILE_PARTS) {
                    String strTimeLoaded = fileParts[2].replaceAll(".xml", "");
                    LocalDateTime timeLoaded =
                        LocalDateTime.parse(strTimeLoaded.trim(), DATETIMEFORMAT);

                    // Insert XHB_CPP_STAGING_INBOUND row, returning PK
                    writeToLog("About to add " + dao.getPddaMessageDataId()
                        + " to the cpp staging inbound table");
                    Integer stagingInboundId = insertStaging(documentName, fileParts[1],
                        getDocType(fileParts[0]), timeLoaded, dao.getPddaMessageDataId());
                    writeToLog("Successfully added  " + stagingInboundId
                        + " to the cpp staging inbound table");

                    // Update XHB_PDDA_MESSAGE record to indicate success
                    updatePddaMessage(stagingInboundId, dao);

                    // Insert record into xhb_internet_html if this is a web page
                    if (documentName.contains("WebPage_")) {
                        // Now publish the web page to XHB_INTERNET_HTML
                        insertWebPage(IWP_STATUS_CREATED, dao.getCourtId(), dao.getPddaMessageDataId());
                    }
                    
                    writeToLog("Processing of " + dao.getCpDocumentName() + " completed");
                } else {
                    LOG.error("Filename is not valid : {}", dao.getCpDocumentName());
                    // Change the status of the XHB_PDDA_MESSAGE record to invalid
                    updatePddaMessageStatus(dao, MESSAGE_STATUS_INVALID, "Filename is not valid");
                }
            } else if (dao.getCpDocumentName().contains("PDDA_XPD")) {
                // Otherwise this is a public display event so is processed separately
                // All we need to do is to set the message as processed already
                updatePddaMessageStatus(dao, MESSAGE_STATUS_PROCESSED,
                    "Public display XHIBIT event - no staging record created");
            } else if (dao.getCpDocumentName().contains("PDDA_XWP")) {
                // Otherwise this is a web page event so is processed separately
                // All we need to do is to set the message as processed already
                updatePddaMessageStatus(dao, MESSAGE_STATUS_PROCESSED,
                    "Web page XHIBIT HTML document - no staging record created");
                // Now publish the web page to XHB_INTERNET_HTML
                insertWebPage(IWP_STATUS_CREATED, dao.getCourtId(), dao.getPddaMessageDataId());
                // Now create a record in xhb_xml_document so it can be picked up and sent to cath
                insertXmlDocument(dao);
            } else {
                // This is an unknown document type so just log it and set the status to invalid
                LOG.debug("This is an unknown document type - no staging record created for file: {}",
                    dao.getCpDocumentName());
                updatePddaMessageStatus(dao, MESSAGE_STATUS_INVALID,
                    "Unknown document!!! - no staging record created");
            }
        } catch (RuntimeException e) {
            LOG.error(
                    "Error adding data to the database for file {} :{}",
                    dao.getCpDocumentName(), e.getStackTrace());
            // Change the status of the XHB_PDDA_MESSAGE record to invalid
            updatePddaMessageStatus(dao, MESSAGE_STATUS_INVALID, "Error adding data to the database: "
                    + e.getMessage());
        }
    }

    public void processOnHoldFile(XhbPddaMessageDao dao) 
        throws ParserConfigurationException, SAXException, IOException {
        
        writeToLog("About to process on hold file " + dao.getCpDocumentName());
        
        // Check this is a file thats not previously been set to PU (Processing Unnecessary)
        Optional<XhbPddaMessageDao> latestPddaMessageDao = getXhbPddaMessageRepository()
            .findByIdSafe(dao.getPrimaryKey());
        
        if (latestPddaMessageDao.isPresent() 
            && !latestPddaMessageDao.get().getCpDocumentStatus().equals(CpDocumentStatus.ON_HOLD.status)) {
            LOG.debug("This file: {} has previously been set to Processing Unnecessary, skipping processing",
                dao.getCpDocumentName());
            return;
            
        }
        
        // First get the recent lists lookup timeframe
        Integer recentListsLookUpTimeframe = Integer.parseInt(getXhbConfigPropRepository()
            .findByPropertyNameSafe(RECENT_LISTS_LOOKUP_TIMEFRAME).get(0).getPropertyValue());
        
        // Fetch the latest xhb_pdda_message list records for this court, within the timeframe and On Hold
        List<XhbPddaMessageDao> recentLists = getXhbPddaMessageRepository()
            .findLatestListsByCourtIdAndTimeframeSafe(dao.getCourtId(),
                LocalDateTime.now().minusMinutes(recentListsLookUpTimeframe));
        
        // Remove the current list from the recent lists to check against, we dont want to compare the list to itself
        recentLists.removeIf(recentList -> recentList.getPddaMessageId().equals(dao.getPddaMessageId()));
        
        LOG.debug("Found {} recent lists for court id: {} to check against", recentLists.size(), dao.getCourtId());
        
        // Get the clob data for the current list
        Optional<XhbClobDao> xhbClobDao =
            getXhbClobRepository().findByIdSafe(dao.getPddaMessageDataId());
        
        if (xhbClobDao.isPresent()) {
            if (xhbClobDao.get().getClobData().contains(CPP_CASE)) {
                // ------------------
                // List with CPP Case
                // ------------------
                
                // Check to see if any match the version, type and date of the current list
                checkRecentCourtListsAgainstMergedXhibitCppList(recentLists, getListData(xhbClobDao.get()));
            } else {
                // ---------------------
                // List without CPP Case
                // ---------------------
                
                // Check to see if there is a duplicate list which has CPP cases on it, if so
                // this list will be set to PU otherwise if a duplicate list is found without CPP cases
                // then that list will be set to PU
                checkRecentCourtListsAgainstXhibitList(recentLists, getListData(xhbClobDao.get()), dao);
            }
        }
    }
    
    private ListData getListData(XhbClobDao listClobDao) 
        throws ParserConfigurationException, SAXException, IOException {
        LOG.debug("Getting list data from clob for clob id: {}", listClobDao.getClobId());
        // Get the list data from the clob and return the data as an object
        ListData listData = new ListData(null, null, null);
        
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newDefaultInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        InputSource inputSource = new InputSource(new StringReader(listClobDao.getClobData()));
        Document document = documentBuilder.parse(inputSource);

        // Get the ListHeader nodes
        Node listHeaderNode = document.getElementsByTagName("cs:ListHeader").item(0);
        NodeList listHeaderChildNodes = listHeaderNode.getChildNodes();
        
        for (int i = 0; i < listHeaderChildNodes.getLength(); i++) {
            Node node = listHeaderChildNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE
                && Objects.equals("cs:Version", node.getNodeName())) {
                listData.setVersion(node.getTextContent());
            } else if (node.getNodeType() == Node.ELEMENT_NODE
                && Objects.equals("cs:PublishedTime", node.getNodeName())) {
                listData.setPublishedDateTime(node.getTextContent());
            }
        }
        
        // Get the Document Id nodes
        Node documentIdNode = document.getElementsByTagName("cs:DocumentID").item(0);
        NodeList documentIdChildNodes = documentIdNode.getChildNodes();
        
        for (int i = 0; i < documentIdChildNodes.getLength(); i++) {
            Node node = documentIdChildNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE
                && Objects.equals("cs:DocumentName", node.getNodeName())) {
                listData.setDocumentType(node.getTextContent());
            }
        }
        
        LOG.debug("Returning list data; version: {}, publishedDateTime: {} and documentType: {}", 
            listData.getVersion(), listData.getPublishedDateTime(), listData.getDocumentType());
        
        return listData;
    }
    
    private void checkRecentCourtListsAgainstMergedXhibitCppList(List<XhbPddaMessageDao> recentLists,
        ListData currentListData) 
            throws ParserConfigurationException, SAXException, IOException {
        
        LOG.debug("Checking recent lists against merged XHIBIT CPP list for version: {},"
            + " publishedDateTime: {} and documentType: {}", 
            currentListData.getVersion(),
            currentListData.getPublishedDateTime(),
            currentListData.getDocumentType());
        
        // Loop through the recent lists and check if any match the version, type and date of the current list
        for (XhbPddaMessageDao recentList : recentLists) {
            
            // Get the clob data for the recent list
            Optional<XhbClobDao> recentListClob =
                getXhbClobRepository().findByIdSafe(recentList.getPddaMessageDataId());
           
            if (recentListClob.isPresent()) {
                ListData recentListData = getListData(recentListClob.get());
                
                // Check recentListData with currentListData 
                if (recentListData.getVersion().equals(currentListData.getVersion())
                    && recentListData.getPublishedDateTime().equals(currentListData.getPublishedDateTime())
                    && recentListData.getDocumentType().equals(currentListData.getDocumentType())) {
                    
                    // This is a duplicate list, with or without cpp cases then set to PU
                    LOG.warn("The file: {}{}{}", recentList.getCpDocumentName(), 
                        " is a duplicate, setting it to: ", MESSAGE_STATUS_PROCESSING_UNNECESSARY);
                    
                    // Set the status to PU (Processing Unnecessary) to prevent further processing
                    updatePddaMessageStatus(recentList, MESSAGE_STATUS_PROCESSING_UNNECESSARY, 
                        DUPLICATE_DOCUMENT_ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void checkRecentCourtListsAgainstXhibitList(List<XhbPddaMessageDao> recentLists,
        ListData currentListData, XhbPddaMessageDao currentList) 
            throws ParserConfigurationException, SAXException, IOException {
        
        LOG.debug("Checking recent lists against XHIBIT list for version: {},"
            + " publishedDateTime: {} and documentType: {}", 
            currentListData.getVersion(),
            currentListData.getPublishedDateTime(),
            currentListData.getDocumentType());
        
        // Loop through the recent lists and check if any match the version, type and date of the current list
        for (XhbPddaMessageDao recentList : recentLists) {
            
            // Get the clob data for the recent list
            Optional<XhbClobDao> recentListClob =
                getXhbClobRepository().findByIdSafe(recentList.getPddaMessageDataId());
           
            if (recentListClob.isPresent()) {
                ListData recentListData = getListData(recentListClob.get());
                
                // Check recentListData with currentListData 
                if (recentListData.getVersion().equals(currentListData.getVersion())
                    && recentListData.getPublishedDateTime().equals(currentListData.getPublishedDateTime())
                    && recentListData.getDocumentType().equals(currentListData.getDocumentType())) {
                    
                    if (recentListClob.get().getClobData().contains(CPP_CASE)) {
                        LOG.debug("The duplicate list: {} contains CPP cases."
                            + " Setting the current list to Prosessing Unnecessary",
                            recentList.getCpDocumentName());
                        // If the recent duplicate list contains CPP cases then set the current list to PU
                        updatePddaMessageStatus(currentList, MESSAGE_STATUS_PROCESSING_UNNECESSARY, 
                            DUPLICATE_DOCUMENT_ERROR_MESSAGE);
                    } else {
                        LOG.debug("The duplicate list: {} does not contain CPP cases."
                            + " Setting this recent list to Prosessing Unnecessary",
                            recentList.getCpDocumentName());
                        // If the recent duplicate list contains no CPP cases then set the recent list to PU
                        updatePddaMessageStatus(recentList, MESSAGE_STATUS_PROCESSING_UNNECESSARY, 
                            DUPLICATE_DOCUMENT_ERROR_MESSAGE);
                    }
                }
            }
        }
    } 
    
    private void checkForListsExceedingOnHoldTimeframe() {
        
        LOG.debug("Processing On Hold files that have exceeded the maximum on hold time");
        
        // Get the max on hold timeframe
        Integer maxOnHoldTimeframe = Integer.parseInt(getXhbConfigPropRepository()
            .findByPropertyNameSafe(MAX_ON_HOLD_TIMEFRAME).get(0).getPropertyValue());
        
        // Fetch any pdda_message list which is still On Hold and has been created before the max on hold timeframe
        List<XhbPddaMessageDao> listsExceedingOnHoldTimeframe = getXhbPddaMessageRepository()
            .findListsExceedingOnHoldTimeframeSafe(LocalDateTime.now().minusMinutes(maxOnHoldTimeframe));
        
        LOG.debug("Found {} lists which have been on hold for over {} minutes", 
            listsExceedingOnHoldTimeframe.size(), maxOnHoldTimeframe);
        
        // Loop through the lists exceeding the on hold timeframe and set their status so they can be processed
        for (XhbPddaMessageDao exceededList : listsExceedingOnHoldTimeframe) {
            String status = CpDocumentStatus.VALID_NOT_PROCESSED.status;
            if (exceededList.getErrorMessage() != null || !exceededList.getErrorMessage().isBlank()) { 
                status = CpDocumentStatus.INVALID.status;
            }
            // Update the status of the list
            LOG.warn("The file: {} has been on hold for over {} minutes, setting it to: {}",
                exceededList.getCpDocumentName(), maxOnHoldTimeframe, status);
            updatePddaMessageStatus(exceededList, status, "On hold time exceeded, set to " + status);
        }
    }
    
    /**
     * Write to the debug log if debug is enabled.

     * @param string Debug message to write to the log
     */
    private void writeToLog(final String string) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("{} :: {}", System.currentTimeMillis(), string);
        }
    }

    private XhbPddaMessageDao fetchLatestXhbPddaMessageDao(XhbPddaMessageDao dao) {
        LOG.debug("fetchLatestXhbPddaMessageDao()");
        Optional<XhbPddaMessageDao> opt =
            getXhbPddaMessageRepository().findByIdSafe(dao.getPrimaryKey());

        if (opt.isPresent()) {
            return opt.get();
        } else {
            String message = "DAO not found in DB for ID: " + dao.getPrimaryKey()
                + ". Original document name: " + dao.getCpDocumentName();
            LOG.error(message);
            throw new IllegalStateException(message);
        }
    }


    /**
     * Insert the row into XHB_CPP_STAGING_INBOUND.

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
     * Insert the row into XHB_INTERNET_HTML.

     * @param status - set to 'C' for created.
     * @param courtId - court id.
     * @param blobId - blob id of the html content.
     * @return the internet html id for debugging/logging purposes
     * @throws SQLException Exception
     */
    private Integer insertWebPage(final String status, final Integer courtId, final Long blobId) {

        writeToLog("status " + status + " courtId: " + courtId + " blobId :" + blobId);
        
        XhbInternetHtmlDao xhbInternetHtmlDao = new XhbInternetHtmlDao();
        xhbInternetHtmlDao.setStatus(status);
        xhbInternetHtmlDao.setCourtId(courtId);
        xhbInternetHtmlDao.setHtmlBlobId(blobId);
        Optional<XhbInternetHtmlDao> opt = getXhbInternetHtmlRepository().update(xhbInternetHtmlDao);
        return opt.isPresent() ? opt.get().getInternetHtmlId() : null;
    }

    private void insertXmlDocument(final XhbPddaMessageDao xhbPddaMessageDao) {

        XhbXmlDocumentDao xhbXmlDocumentDao = new XhbXmlDocumentDao();
        xhbXmlDocumentDao.setDocumentTitle(xhbPddaMessageDao.getCpDocumentName());
        xhbXmlDocumentDao.setXmlDocumentClobId(xhbPddaMessageDao.getPddaMessageDataId());
        xhbXmlDocumentDao.setStatus(ND_STATUS_XML_DOC);
        xhbXmlDocumentDao.setDocumentType(IWP);
        xhbXmlDocumentDao.setExpiryDate(null);
        xhbXmlDocumentDao.setCourtId(xhbPddaMessageDao.getCourtId());
        
        getXhbXmlDocumentRepository().save(xhbXmlDocumentDao);
    }
    
    /**
     * Update the XHB_PDA_MESSAGE record.

     * @param stagingInboundId - cpp staging inbound id
     * @throws SQLException Exception
     */
    private void updatePddaMessage(final Integer stagingInboundId, XhbPddaMessageDao dao) {
        writeToLog("Updating doc " + dao.getCpDocumentName() + " with status: "
            + MESSAGE_STATUS_PROCESSED + " and messageId: " + dao.getPddaMessageId());

        XhbPddaMessageDao latest = fetchLatestXhbPddaMessageDao(dao);
        LOG.debug("Original DAO version: {}, Latest DB version: {}", dao.getVersion(),
            latest.getVersion());

        latest.setCpDocumentStatus(MESSAGE_STATUS_PROCESSED);
        latest.setCppStagingInboundId(stagingInboundId);

        getXhbPddaMessageRepository().update(latest)
            .ifPresentOrElse(updated -> LOG
                .debug("Successfully updated message with new staging ID: {}", stagingInboundId),
                () -> {
                    LOG.error("Failed to update DAO with staging ID: {}", stagingInboundId);
                    throw new IllegalStateException(
                        "Update failed for DAO: " + latest.getPrimaryKey());
                });
    }


    /**
     * Updates the XHB_PDDA_MESSAGE record.
     */
    void updatePddaMessageStatus(XhbPddaMessageDao dao, String messageStatus, String optionalError) {
        LOG.debug("updatePddaMessageStatus for DAO ID: {}", dao.getPrimaryKey());

        XhbPddaMessageDao latest = fetchLatestXhbPddaMessageDao(dao);
        LOG.debug("Original DAO version: {}, Latest DB version: {}", dao.getVersion(),
            latest.getVersion());

        latest.setCpDocumentStatus(messageStatus);
        if (optionalError != null && !optionalError.isBlank()) {
            latest.setErrorMessage(optionalError);
        }
        getXhbPddaMessageRepository().update(latest).ifPresentOrElse(
            updated -> LOG.debug("Successfully updated status to {} for DAO ID: {}", messageStatus,
                updated.getPrimaryKey()),
            () -> {
                LOG.error("Failed to update status for DAO ID: {}", latest.getPrimaryKey());
                throw new IllegalStateException("Update failed for DAO: " + latest.getPrimaryKey());
            });
    }


    /**
     * Return the document type depending on what's been used in the file name.

     * @param fileType portion of the filename
     * @return shorthand documentType
     */
    String getDocType(final String fileType) {
        writeToLog("METHOD ENTRY: getDocType");

        DocumentType docType = DocumentType.fromString(fileType);
        if (docType == null) {
            return null;
        } else {
            return docType.name();
        }
    }


    /**
     * Get the document name to process. If the document name is invalid then return an empty
     * string.

     * @param documentName document name
     * @return the document name to process
     */
    protected String getDocumentNameToProcess(String documentName) {
        writeToLog("METHOD ENTRY: getDocumentNameToProcess");
        String tempDocumentName = documentName;

        if (tempDocumentName.contains("PDDA_")) {
            if (tempDocumentName.contains("list_filename = ")) {
                // We only want the text after the first "list_filename = " in the document name
                tempDocumentName =
                    tempDocumentName.substring(tempDocumentName.indexOf("list_filename = ") + 16);
                return tempDocumentName;
            } else if (tempDocumentName.contains("pd_filename = ")) {
                // We only want the text after the first "pd_filename = " in the document name
                tempDocumentName =
                    tempDocumentName.substring(tempDocumentName.indexOf("pd_filename = ") + 14);
                return tempDocumentName;
            } else {
                return ""; // This is a public display event so will be processed separately
            }
        }
        return tempDocumentName;
    }


    /**
     * Use a regular expression to check if the document name is valid.

     * @param documentName The document name to check
     * @return true if the document name is valid
     */
    boolean isDocumentNameValid(String documentName) {
        writeToLog("METHOD ENTRY: checkDocumentNameIsValid");

        String regexPdda =
            "^PDDA_(XDL|CPD|XWP)_\\d{1,8}_\\d{1,6}_\\d{3}_\\d{14} "
                + "(list_filename = DailyList_\\d{3}_\\d{14}.xml|pd_filename = PublicDisplay_\\d{3}_\\d{14}.xml)$";
        String regexPddaXpd = "^PDDA_XPD_\\d{1,8}_\\d{1,6}_\\d{3}_\\d{14}$";
        String regexPddaXwp = "^PDDA_XWP_\\d{1,8}_\\d{1,6}_\\d{3}_\\d{14}$";
        String regexOther =
            "^(DailyList_|FirmList_|WarnedList_|PublicDisplay_|WebPage_).+\\d{14}.xml$";

        return documentName.matches(regexPdda) || documentName.matches(regexPddaXpd)
            || documentName.matches(regexPddaXwp) || documentName.matches(regexOther);
    }
    
    class ListData {
        private String version;
        private String publishedDateTime;
        private String documentType;

        public ListData(String version, String publishedDateTime, String type) {
            this.version = version;
            this.publishedDateTime = publishedDateTime;
            this.documentType = type;
        }

        public String getVersion() {
            return version;
        }
        
        public void setVersion(String version) {
            this.version = version;
        }

        public String getPublishedDateTime() {
            return publishedDateTime;
        }
        
        public void setPublishedDateTime(String publishedDateTime) {
            this.publishedDateTime = publishedDateTime;
        }

        public String getDocumentType() {
            return documentType;
        }
        
        public void setDocumentType(String type) {
            this.documentType = type;
        }
    }
}
