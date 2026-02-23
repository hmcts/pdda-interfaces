package uk.gov.hmcts.pdda.business.services.cpp;

import jakarta.ejb.ApplicationException;
import jakarta.ejb.EJBException;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import uk.gov.hmcts.framework.scheduler.RemoteTask;
import uk.gov.hmcts.framework.services.CsServices;
import uk.gov.hmcts.framework.services.conversion.DateConverter;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import uk.gov.hmcts.pdda.business.entities.xhbcppformatting.XhbCppFormattingDao;
import uk.gov.hmcts.pdda.business.entities.xhbcpplist.XhbCppListDao;
import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundDao;
import uk.gov.hmcts.pdda.business.entities.xhbformatting.XhbFormattingDao;
import uk.gov.hmcts.pdda.business.entities.xhbhearinglist.XhbHearingListDao;
import uk.gov.hmcts.pdda.business.entities.xhbschedhearingattendee.XhbSchedHearingAttendeeDao;
import uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant.XhbSchedHearingDefendantDao;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingDao;
import uk.gov.hmcts.pdda.business.entities.xhbxmldocument.XhbXmlDocumentDao;
import uk.gov.hmcts.pdda.business.services.cppformatting.CppFormattingHelper;
import uk.gov.hmcts.pdda.business.services.cpplist.CppListHelper;
import uk.gov.hmcts.pdda.business.services.cppstaginginboundejb3.CppDocumentTypes;
import uk.gov.hmcts.pdda.business.services.cppstaginginboundejb3.CppStagingInboundControllerException;
import uk.gov.hmcts.pdda.business.services.formatting.AbstractListXmlMergeUtils;
import uk.gov.hmcts.pdda.business.services.formatting.FormattingServices;
import uk.gov.hmcts.pdda.business.services.formatting.MergeDocumentUtils;
import uk.gov.hmcts.pdda.business.services.pdda.data.ListNodesHelper;
import uk.gov.hmcts.pdda.business.services.pdda.data.RepositoryHelper;
import uk.gov.hmcts.pdda.business.services.validation.ValidationException;
import uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled.DocumentUtils;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Controller Bean For Starting Timers for dealing with documents that have been inserted into
 * XHB_CPP_STAGING_INBOUND sets the status to IP (In Process) and validates the XML versus the XSD
 * that it is valid.
 */
@Stateless
@Service 
@Transactional
@LocalBean
@ApplicationException(rollback = true)
@SuppressWarnings({"PMD"})
public class CppInitialProcessingControllerBean extends AbstractCppInitialProcessingControllerBean
    implements RemoteTask, Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG =
        LoggerFactory.getLogger(CppInitialProcessingControllerBean.class);

    private static final String BATCH_USERNAME = "CPPX_SCHEDULED_JOB";
    private static final String ROLLBACK_MSG = ": failed! Transaction Rollback";
    private static final String IWP = "IWP";
    private ListNodesHelper listNodesHelper;
    
    private RepositoryHelper repositoryHelper;

    public CppInitialProcessingControllerBean() {
        super();
    }

    public CppInitialProcessingControllerBean(EntityManager entityManager) {
        super(entityManager);
    }

    /**
     * Implementation of RemoteTask so that this process is called by the timer process. This method
     * must have the same transactional behaviour as processFormattingDocument.
     */
    @Override
    public void doTask() {
        LOG.debug("CppStaging -- doTask() - entered");
        handleNewDocuments();
        handleStuckDocuments();
        LOG.debug("CppStaging -- doTask() - exiting");
    }

    /**
     * Process any completely unprocessed (generally these will be new records unless manually
     * amended status) documents that have appeared in XHB_CPP_STAGING_INBOUND.
     */
    public void handleNewDocuments() {
        String methodName = "handleNewDocuments()";
        LOG.debug(TWO_PARAMS, methodName, ENTERED);

        List<XhbCppStagingInboundDao> docs = null;
        try {
            // Step 1: Get the next unprocessed document
            docs = getCppStagingInboundControllerBean().getLatestUnprocessedDocument();
            if (docs != null) {
                LOG.info("{} - Number of unprocessed documents found: {}", methodName, docs.size());
            } else {
                LOG.info("{} - No unprocessed documents found", methodName);
            }
        } catch (CppStagingInboundControllerException e) {
            LOG.error("CPP Staging Inbound Controller Exception when obtaining the next "
                + "document received from CPP that has not been processed at all");
            LOG.error("Error:{}", e.getMessage());
        }

        if (docs != null) {
            LOG.debug(
                "{} - # About to process unprocessed documents; there are ## {} ## documents to process",
                methodName, docs.size());
            for (XhbCppStagingInboundDao doc : docs) {
                LOG.debug("{} - ## Processing document: {}", methodName, doc.getDocumentName());
                processDocument(doc);
                LOG.debug("{} - ## Finished processing document: {}", methodName,
                    doc.getDocumentName());
            }
            LOG.debug("{} - # Finished processing unprocessed documents", methodName);
        } else {
            // There are no documents currently to validate
            LOG.debug(
                "{} - There are no unprocessed inbound documents in XHB_CPP_STAGING_INBOUND at this time",
                methodName);
        }
        LOG.debug(TWO_PARAMS, methodName, EXITED);
    }

    /**
     * Process a document from XHB_CPP_STAGING_INBOUND that has been added but not yet validated or
     * processed.

     * @param xcsi The cpp staging inbound record to process
     */
    private void processDocument(XhbCppStagingInboundDao xcsi) {
        String methodName = "processDocument(" + xcsi + ")";
        LOG.debug(TWO_PARAMS, methodName, ENTERED);

        try {
            LOG.debug("Document to validate:: {}", xcsi);
            XhbCppStagingInboundDao updatedXcsi = xcsi;

            // Set the status to IN_PROCESS so that no other incoming process can pick up
            // this document
            Optional<XhbCppStagingInboundDao> savedXcsi = getCppStagingInboundControllerBean()
                .updateStatusInProcess(updatedXcsi, BATCH_USERNAME);
            if (savedXcsi.isPresent()) {
                updatedXcsi = savedXcsi.get();
            }

            // Step 2: Validate a document which has the VALIDATION_STATUS='IP'
            // Performing a validation will also check that the DOCUMENT_NAME and
            // DOCUMENT_TYPE values are valid.
            boolean docIsValid =
                getCppStagingInboundControllerBean().validateDocument(updatedXcsi, BATCH_USERNAME);

            if (docIsValid) {
                LOG.debug("{} - The document has been successfully validated", methodName);

                // Fetch the document with the new validation status and version from the DB
                Optional<XhbCppStagingInboundDao> validatedXcsi = getCppStagingInboundControllerBean()
                    .getXhbCppStagingInboundRepository()
                    .findByIdSafe(updatedXcsi.getCppStagingInboundId());
                
                // Now attempt to process the validated document - i.e. examine XML and insert
                // into downstream database tables
                if (validatedXcsi.isPresent() && processValidatedDocument(validatedXcsi.get())) {
                    LOG.debug("{} - The validated document has been successfully processed",
                        methodName);
                }
            } else {
                // Logging a validation failure as an error at this time
                LOG.error("The document is not valid. Check XHB_CPP_STAGING_INBOUND.ERROR_MESSAGE "
                    + "for more details. Document details are: {}", updatedXcsi.toString());
            }

            // Not throwing any exceptions here, as a failure processing one document should
            // not impact others
        } catch (ValidationException e) {
            LOG.error(
                "Validation error when validating document. Turn debugging on for more info. Error: {}",
                e.getMessage());
        } catch (CppInitialProcessingControllerException e) {
            LOG.error("Error validating document. Turn debugging on for more info. Error: {}",
                e.getMessage());
        }
        LOG.debug(TWO_PARAMS, methodName, EXITED);
    }

    /**
     * Check to see if there are any "stuck" records that have been validated but not moved on for
     * processing and process.
     */
    public void handleStuckDocuments() {
        String methodName = "handleStuckDocuments()";
        LOG.debug(TWO_PARAMS, methodName, ENTERED);

        List<XhbCppStagingInboundDao> docs = null;
        try {
            docs = getCppStagingInboundControllerBean().getNextValidatedDocument();
        } catch (CppStagingInboundControllerException cppsie) {
            LOG.error("Error in EJB when processing a document that has already been validated. "
                + "Turn debugging on for more info. Error: {}", cppsie.getMessage());
        }

        if (docs != null) {
            LOG.debug(
                "{} - # About to process unprocessed documents; there are ## {} ## documents to process",
                methodName, docs.size());
            for (XhbCppStagingInboundDao doc : docs) {
                LOG.debug("{} - ## Processing document: {}", methodName, doc.getDocumentName());
                handleStuckDocuments(doc);
                LOG.debug("{} - ## Finished processing document: {}", methodName,
                    doc.getDocumentName());
            }
            LOG.debug("{} - # Finished processing unprocessed documents", methodName);
        }

        LOG.debug(TWO_PARAMS, methodName, EXITED);
    }

    /**
     * Get the earliest document that has been validated but not yet processed and attempt to
     * process it. Processing means that based on the document type data is extracted from the XML
     * and used to populate records in XHB_CPP_LIST or XHB_CPP_FORMATTING.

     * If a process fails then the status needs to be updated accordingly so that it doesn't get
     * picked up again until it has been fixed

     * @throws CppInitialProcessingControllerException Exception
     */
    @Override
    public boolean processValidatedDocument(XhbCppStagingInboundDao thisDoc) {
        String methodName = "processValidatedDocument() - thisDoc: " + thisDoc.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(TWO_PARAMS, methodName, ENTERED);
        }

        String clobXml =
            getCppStagingInboundControllerBean().getClobXmlAsString(thisDoc.getClobId());
        if (clobXml == null) {
            LOG.error("There was a problem obtaining the clob data: clob_id={}",
                thisDoc.getClobId());
            return false;
        }

        // Have found the need to clear cached entites that have already been merged (updated)
        // before a persist (insert) is performed to prevent duplicate updates.
        getEntityManager().clear();

        // Which type of document is this?
        final String documentType = thisDoc.getDocumentType(); // Will be valid at this stage
        if (CppDocumentTypes.DL == CppDocumentTypes.fromString(documentType)
            || CppDocumentTypes.FL == CppDocumentTypes.fromString(documentType)
            || CppDocumentTypes.WL == CppDocumentTypes.fromString(documentType)) {

            // If there is an existing list in XHB_CPP_LIST then the record will be updated
            // otherwise a new record will be created
            createUpdateListRecords(thisDoc, clobXml);

        } else if (CppDocumentTypes.PD == CppDocumentTypes.fromString(documentType)
            || CppDocumentTypes.WP == CppDocumentTypes.fromString(documentType)) {

            createUpdateNonListRecords(thisDoc);

        } else {
            LOG.error("Not a valid document type");
            getCppStagingInboundControllerBean().updateStatusProcessingFail(thisDoc,
                "Problem reconciling document type after successful validation", BATCH_USERNAME);
            LOG.debug(TWO_PARAMS, methodName, EXITED);
            return false;
        }

        LOG.debug(TWO_PARAMS, methodName, EXITED);
        return true;
    }


    /**
     * getListStartDate.

     * @param xml String
     * @param documentType String
     * @return LocalDateTime
     * @throws ParserConfigurationException Exception
     * @throws IOException Exception
     * @throws SAXException Exception
     */
    public LocalDateTime getListStartDate(String xml, String documentType)
        throws ParserConfigurationException, IOException, SAXException {
        String methodName = "getListStartDate(" + xml + "," + documentType + ")";
        if (LOG.isDebugEnabled()) {
            LOG.debug(TWO_PARAMS, methodName, ENTERED);
        }
        Document xhibitDocument;
        Date listStartDate;

        setXmlUtils(FormattingServices.getXmlUtils(documentType));
        try {
            xhibitDocument = DocumentUtils.createInputDocument(xml);
            MergeDocumentUtils.getDocumentBuilder().parse(new InputSource(new StringReader(xml)));
            listStartDate = ((AbstractListXmlMergeUtils) getXmlUtils())
                .getListStartDateFromDocument(xhibitDocument);
        } catch (SAXException | IOException | ParserConfigurationException e) {
            LOG.error("SAX Error whilst parsing XML to find list start date");
            CsServices.getDefaultErrorHandler().handleError(e, getClass());
            LOG.debug(TWO_PARAMS, methodName, ROLLBACK_MSG);
            throw e;
        }

        return DateConverter.convertDateToLocalDateTime(listStartDate);
    }

    /**
     * getListEndDate.

     * @param xml String
     * @param documentType String
     * @return LocalDateTime
     * @throws ParserConfigurationException Exception
     * @throws IOException Exception
     * @throws SAXException Exception
     */
    public LocalDateTime getListEndDate(String xml, String documentType)
        throws ParserConfigurationException, IOException, SAXException {
        String methodName = "getListEndDate(" + xml + "," + documentType + ")";
        if (LOG.isDebugEnabled()) {
            LOG.debug(TWO_PARAMS, methodName, ENTERED);
        }
        Document xhibitDocument;
        Date listEndDate;

        setXmlUtils(FormattingServices.getXmlUtils(documentType));
        try {
            DocumentBuilder docBuilder = MergeDocumentUtils.getDocumentBuilder();
            xhibitDocument = DocumentUtils.createInputDocument(docBuilder, xml);
            listEndDate = ((AbstractListXmlMergeUtils) getXmlUtils())
                .getListEndDateFromDocument(xhibitDocument);
        } catch (SAXException | IOException | ParserConfigurationException e) {
            LOG.error("SAX Error whilst parsing XML to find list end date");
            CsServices.getDefaultErrorHandler().handleError(e, getClass());
            LOG.debug(TWO_PARAMS, methodName, ROLLBACK_MSG);
            throw e;
        }

        return DateConverter.convertDateToLocalDateTime(listEndDate);
    }

    /**
     * getCourtHouseCode.

     * @param xml String
     * @param documentType String
     * @return String
     * @throws ParserConfigurationException Exception
     * @throws IOException Exception
     * @throws SAXException Exception
     */
    public String getCourtHouseCode(String xml, String documentType)
        throws ParserConfigurationException, IOException, SAXException {
        String methodName = "getCourtHouseCode(" + xml + "," + documentType + ")";
        if (LOG.isDebugEnabled()) {
            LOG.debug(TWO_PARAMS, methodName, ENTERED);
        }
        Document xhibitDocument;
        String courtCode;

        setXmlUtils(FormattingServices.getXmlUtils(documentType));
        try {
            DocumentBuilder docBuilder = MergeDocumentUtils.getDocumentBuilder();
            xhibitDocument = DocumentUtils.createInputDocument(docBuilder, xml);
            courtCode = ((AbstractListXmlMergeUtils) getXmlUtils())
                .getCourtHouseCodeFromDocument(xhibitDocument);
        } catch (SAXException | IOException | ParserConfigurationException e) {
            LOG.error("SAX Error whilst parsing XML to find court code");
            CsServices.getDefaultErrorHandler().handleError(e, getClass());
            LOG.debug(TWO_PARAMS, methodName, ROLLBACK_MSG);
            throw e;
        }

        return courtCode;
    }

    /**
     * createUpdateNonListRecords.

     * @param thisDoc XhbCppStagingInboundDao
     */
    public void createUpdateNonListRecords(XhbCppStagingInboundDao thisDoc) {
        String methodName = "createUpdateNonListRecords() - thisDoc: " + thisDoc.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(TWO_PARAMS, methodName, ENTERED);
        }
        try {
            int courtId = getCppStagingInboundControllerBean()
                .getCourtId(Integer.valueOf(thisDoc.getCourtCode()));

            // If court id is not > 0 then the court could not be found and processing of
            // this record should stop
            if (courtId > 0) {
                LOG.debug("{} - Court found, going to create or update", methodName);
                String documentType = thisDoc.getDocumentType();
                if (CppDocumentTypes.WP == CppDocumentTypes.fromString(documentType)) {
                    documentType = IWP;
                }
                LOG.debug("{} - Doc type found = {}, going to create or update", methodName,
                    documentType);
                XhbCppFormattingDao docToUpdate =
                    getXhbCppFormattingRepository().findLatestByCourtDateInDocSafe(courtId,
                        documentType, LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT));

                LOG.debug("{} - Formatting doc to update query has been run, docToUpdate={}",
                    methodName, docToUpdate);
                if (docToUpdate != null) {
                    // Update - directly rather through maintainer as that
                    // was generating CMP/CMR relationship errors
                    docToUpdate.setFormatStatus(CppFormattingHelper.FORMAT_STATUS_NOT_PROCESSED);
                    docToUpdate.setXmlDocumentClobId(thisDoc.getClobId());
                    docToUpdate.setDateIn(thisDoc.getTimeLoaded());
                    docToUpdate.setStagingTableId(thisDoc.getCppStagingInboundId());
                    docToUpdate.setLastUpdatedBy(BATCH_USERNAME);
                    LOG.debug("{} - Updating doc in XHB_CPP_FORMATTING", methodName);
                    getXhbCppFormattingRepository().update(docToUpdate);
                    LOG.debug("{} - Updated doc in XHB_CPP_FORMATTING", methodName);


                } else { // Insert
                    // Create a record to insert
                    XhbCppFormattingDao docToCreate = new XhbCppFormattingDao();
                    docToCreate.setCourtId(courtId);
                    docToCreate.setFormatStatus(CppFormattingHelper.FORMAT_STATUS_NOT_PROCESSED);
                    docToCreate.setDateIn(thisDoc.getTimeLoaded());
                    docToCreate.setDocumentType(documentType);
                    docToCreate.setStagingTableId(thisDoc.getCppStagingInboundId());
                    docToCreate.setXmlDocumentClobId(thisDoc.getClobId());
                    docToCreate.setObsInd("N");
                    LOG.debug("{} - Creating doc in XHB_CPP_FORMATTING", methodName);
                    getXhbCppFormattingRepository().save(docToCreate);
                    LOG.debug("{} - Created doc in XHB_CPP_FORMATTING", methodName);
                    
                    // Create the xhbXmlDocument record
                    if (IWP.equals(documentType)) {
                        // Create the "en" version
                        XhbXmlDocumentDao xhbXmlDocumentDaoEn =
                            CppFormattingHelper.createWebPageXhbXmlDDocumentRecord(docToCreate, thisDoc, "_en");
                        getXhbXmlDocumentRepository().save(xhbXmlDocumentDaoEn);
                        
                        // Create the "cy" version
                        XhbXmlDocumentDao xhbXmlDocumentDaoCy =
                            CppFormattingHelper.createWebPageXhbXmlDDocumentRecord(docToCreate, thisDoc, "_cy");
                        getXhbXmlDocumentRepository().save(xhbXmlDocumentDaoCy);
                    }
                }

                if (IWP.equalsIgnoreCase(documentType)) {
                    // Also need to add 2 new records to XHB_FORMATTING
                    // Create the "en" version
                    XhbFormattingDao xfbv = CppFormattingHelper.createXhbFormattingRecord(courtId,
                        thisDoc, documentType, "en");
                    getXhbFormattingRepository().save(xfbv);

                    // Create the "cy" version
                    xfbv = CppFormattingHelper.createXhbFormattingRecord(courtId,
                        thisDoc, documentType, "cy");
                    getXhbFormattingRepository().save(xfbv);
                }

                LOG.debug("{} - Updating the status to success", methodName);
                // If all successful then we need to set record in XHB_STAGING_INBOUND to
                // indicate this
                getCppStagingInboundControllerBean().updateStatusProcessingSuccess(thisDoc,
                    BATCH_USERNAME);
            } else {
                // Court id is invalid
                getCppStagingInboundControllerBean().updateStatusProcessingFail(thisDoc,
                    "Court id was invalid", BATCH_USERNAME);
            }

        } catch (EJBException e) {
            CsServices.getDefaultErrorHandler().handleError(e, getClass());
            LOG.debug(TWO_PARAMS, methodName, ROLLBACK_MSG);
            throw e;
        }

        LOG.debug(TWO_PARAMS, methodName, EXITED);

    }

    /**
     * createUpdateListRecords.

     * @param thisDoc XhbCppStagingInboundDao
     */
    public void createUpdateListRecords(XhbCppStagingInboundDao thisDoc, String clobXml) {
        String methodName = "createUpdateListRecords() - thisDoc: " + thisDoc.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(TWO_PARAMS, methodName, ENTERED);
        }
        try {
            String documentType = thisDoc.getDocumentType();
            String listType = documentType.substring(0, 1); // Only want first letter
            LocalDateTime listStartDate = getListStartDate(clobXml, documentType);
            LocalDateTime listEndDate = getListEndDate(clobXml, documentType);
            XhbCppListDao docToUpdate = getCppListControllerBean().checkForExistingCppListRecord(
                Integer.valueOf(thisDoc.getCourtCode()), listType, listStartDate, listEndDate);
            if (docToUpdate != null) { // Update
                // Set updated values
                docToUpdate.setStatus(CppListHelper.NOT_PROCESSED);
                docToUpdate.setTimeLoaded(thisDoc.getTimeLoaded());
                docToUpdate.setListStartDate(listStartDate);
                docToUpdate.setListEndDate(listEndDate);
                docToUpdate.setListClobId(thisDoc.getClobId());
                docToUpdate.setMergedClobId(thisDoc.getClobId());
                docToUpdate.setLastUpdatedBy(BATCH_USERNAME);
                getCppListControllerBean().updateCppList(docToUpdate);
                
                // Create the records for the updated list
                createRecordsForProcessing(thisDoc, documentType, docToUpdate);

            } else { // Insert
                // Create a record to insert
                XhbCppListDao docToCreate = new XhbCppListDao();
                docToCreate.setCourtCode(Integer.valueOf(thisDoc.getCourtCode()));
                docToCreate.setStatus(CppListHelper.NOT_PROCESSED);
                docToCreate.setTimeLoaded(thisDoc.getTimeLoaded());
                docToCreate.setListType(listType);
                docToCreate.setListStartDate(listStartDate);
                docToCreate.setListEndDate(listEndDate);
                docToCreate.setListClobId(thisDoc.getClobId());
                docToCreate.setObsInd("N");
                getXhbCppListRepository().save(docToCreate);

                // Create the records for the new list
                createRecordsForProcessing(thisDoc, documentType, docToCreate);
            }

            // If all successful then we need to set record in XHB_STAGING_INBOUND to
            // indicate this
            getCppStagingInboundControllerBean().updateStatusProcessingSuccess(thisDoc,
                BATCH_USERNAME);
            
            // As this is a new list then clear any existing hearings in the database
            // for previous lists for this court for today's date.
            // This is to ensure that any hearings that have been removed from the list
            // are not included in the public display.
            List<XhbCourtSiteDao> courtIds = getRepositoryHelper()
                .getXhbCourtSiteRepository().findByCrestCourtIdValueSafe(thisDoc.getCourtCode());
            if (courtIds == null || courtIds.isEmpty()) {
                LOG.error("{} - No court site found for crest court id value: {}. "
                    + "Unable to remove existing hearings for this court and date.",
                    methodName, thisDoc.getCourtCode());
            } else {
                LOG.debug("{} - Found {} court site(s) for crest court id value: {}",
                    methodName, courtIds.size(), thisDoc.getCourtCode());
                for (XhbCourtSiteDao courtSite : courtIds) {
                    LOG.debug("{} - Removing existing hearings for court site id: {} and date: {}",
                        methodName, courtSite.getCourtSiteId(), listStartDate.toLocalDate());
                    removeExistingHearingsForCourtAndDate(courtSite.getCourtId(), listStartDate.toLocalDate());
                }
            }
            
            // Process the xml nodes and store the data contained in it
            getListNodesHelper().processClobData(clobXml);

        } catch (ParserConfigurationException | IOException | SAXException e) {
            CsServices.getDefaultErrorHandler().handleError(e, getClass());
            LOG.debug(TWO_PARAMS, methodName, ROLLBACK_MSG);
            throw new CppInitialProcessingControllerException("cpp.initial.processing.controller",
                methodName + ": " + e.getMessage(), e);
        }
        
        LOG.debug(TWO_PARAMS, methodName, EXITED);
    }
    
    /**
     * Given a court and date, remove any existing hearings for that court and date.
     * .
     * Remove the previous entry from XHB_HEARING_LIST.
     * Remove the previous entries from XHB_SITTING.
     * Remove the previous entries from XHB_SCHEULED_HEARING.
     * Remove the previous entries from XHB_SCHED_HEARING_DEFENDANT.
     * Remove the previous entries from XHB_SCHED_HEARING_ATTENDEE.
     * Remove the previous entries from XHB_SH_LEG_REP.
     * For entries in XHB_CR_LIVE_DISPLAY and XHB_CR_LIVE_INTERNET where this
     * scheduled hearing exists then set the scheduled hearing id to null.
     * For each entry in XHB_SCHED_HEARING_ATTENDEE find any entries in 
     * XHB_SH_JUDGE for this SH_ATTENDEE_ID and remove those entries as well.
     * .
     * -------------------------------------
     * In Postgres it would need to do this, and in this order to avoid foreign key constraint issues:
     * -------------------------------------
     * update pdda.xhb_cr_live_display
     * set scheduled_hearing_id = null
     * where scheduled_hearing_id in (
     *      select scheduled_hearing_id from pdda.xhb_scheduled_hearing
     *      where sitting_id in (select sitting_id from pdda.xhb_sitting where list_id=23317)
     * );
     * .
     * delete from pdda.xhb_sched_hearing_defendant
     * where scheduled_hearing_id in (
     *      select scheduled_hearing_id from pdda.xhb_scheduled_hearing
     *      where sitting_id in (select sitting_id from pdda.xhb_sitting where list_id=23317)
     * );
     * .
     * delete from pdda.xhb_sh_judge
     * where sh_attendee_id in (
     *      select sh_attendee_id from pdda.xhb_sched_hearing_attendee
     *      where scheduled_hearing_id in (
     *          select scheduled_hearing_id from pdda.xhb_scheduled_hearing
     *          where sitting_id in (select sitting_id from pdda.xhb_sitting where list_id=23317)
     *      )
     * );
     * .
     * delete from pdda.xhb_sched_hearing_attendee
     * where scheduled_hearing_id in (
     *      select scheduled_hearing_id from pdda.xhb_scheduled_hearing
     *      where sitting_id in (select sitting_id from pdda.xhb_sitting where list_id=23317)
     * );
     * .
     * delete from pdda.xhb_scheduled_hearing
     * where sitting_id in (select sitting_id from pdda.xhb_sitting where list_id=23317);
     * .
     * delete from pdda.xhb_sitting where list_id=23317;
     * .
     * delete from pdda.xhb_hearing_list where list_id=23317;

     * @param courtId Integer id of the court for which existing hearings should be removed
     * @param listDate LocalDate the date of the list for which to remove existing hearings
     */
    private void removeExistingHearingsForCourtAndDate(Integer courtId, LocalDate listDate) {
        String methodName = "removeExistingHearingsForCourtAndDate() - courtId: " + courtId
            + ", listDate: " + listDate;
        if (LOG.isDebugEnabled()) {
            LOG.debug(TWO_PARAMS, methodName, ENTERED);
        }
        
        // Find the XHB_HEARING_LIST entries for this court and date
        LocalDateTime listStartDateTime = LocalDateTime.of(listDate, LocalTime.MIDNIGHT);
        List<XhbHearingListDao> hearingListsToRemove =
            getRepositoryHelper().getXhbHearingListRepository().findByCourtIdAndDateSafe(
                courtId, listStartDateTime);
        
        if (hearingListsToRemove == null || hearingListsToRemove.isEmpty()) {
            LOG.debug("{} - No existing hearing list found for court id {} and date {}",
                methodName, courtId, listDate);
            LOG.debug(TWO_PARAMS, methodName, EXITED);
            return;
        } else {
            LOG.debug(
                "{} - Found {} hearing list(s) to remove for court id {} and date {}",
                methodName, hearingListsToRemove.size(), courtId, listDate
            );
        }
        
        // Loop through the hearing lists and remove the associated sittings,
        // scheduled hearings, defendants, attendees, judge relationships and then the hearing list itself
        for (XhbHearingListDao hearingList : hearingListsToRemove) {
            LOG.debug("{} - Removing hearing list with id: {}", methodName, hearingList.getListId());
            
            // in removeExistingHearingsForCourtAndDate
            removeSittingsForHearingList(hearingList);
            getRepositoryHelper().getXhbHearingListRepository().deleteById(hearingList.getListId());

            LOG.debug("{} - Removed hearing list with id: {}", methodName, hearingList.getListId());
        }
        LOG.debug(TWO_PARAMS, methodName, EXITED);
    }

    /**
     * For a given hearing list, remove the associated sittings.
     * @param hearingList the hearing list to remove sittings for
     */
    private void removeSittingsForHearingList(XhbHearingListDao hearingList) {
        String methodName = "removeSittingsForHearingList() - hearingListId: "
            + hearingList.getListId();
        if (LOG.isDebugEnabled()) {
            LOG.debug(TWO_PARAMS, methodName, ENTERED);
        }

        // Find the sittings for this hearing list
        List<XhbSittingDao> sittings =
            getRepositoryHelper().getXhbSittingRepository().findByListIdSafe(hearingList.getListId());
        if (sittings == null || sittings.isEmpty()) {
            LOG.debug("{} - No sittings found for hearing list with id: {}",
                methodName, hearingList.getListId());
        } else {
            LOG.debug("{} - Found {} sitting(s) to remove for hearing list with id: {}",
                methodName, sittings.size(), hearingList.getListId());
        }

        // Remove scheduled hearings (and their related data) for the found sittings
        removeScheduledHearingsForSittings(sittings);

        // Now delete the sitting rows for the hearing list
        getRepositoryHelper().getXhbSittingRepository().deleteByListId(hearingList.getListId());
        LOG.debug("{} - Removed sittings for hearing list with id: {}", methodName, hearingList.getListId());
        LOG.debug(TWO_PARAMS, methodName, EXITED);
    }

    /**
     * For a given list of sittings, remove the associated scheduled hearings.
     * @param sittings list of sittings for which scheduled hearings should be removed
     */
    private void removeScheduledHearingsForSittings(List<XhbSittingDao> sittings) {
        // Prepare a safe method name and sitting id list without risking NPE or IndexOutOfBounds
        List<Integer> sittingIds = (sittings == null)
            ? List.of()
            : sittings.stream()
                .map(XhbSittingDao::getSittingId)
                .toList();
        String methodName = "removeScheduledHearingsForSittings() - sittingIds: "
            + (sittingIds.isEmpty() ? "N/A" : sittingIds);

        if (LOG.isDebugEnabled()) {
            LOG.debug(TWO_PARAMS, methodName, ENTERED);
        }

        if (sittingIds.isEmpty()) {
            LOG.debug("{} - No sitting ids provided, nothing to remove", methodName);
            LOG.debug(TWO_PARAMS, methodName, EXITED);
            return;
        }

        // Find the scheduled hearings for these sittings
        List<XhbScheduledHearingDao> scheduledHearings =
            getRepositoryHelper().getXhbScheduledHearingRepository()
                .findBySittingIdsSafe(sittingIds);
        if (scheduledHearings == null || scheduledHearings.isEmpty()) {
            LOG.debug("{} - No scheduled hearings found for sitting ids: {}", methodName, sittingIds);
        } else {
            LOG.debug(
                "{} - Found {} scheduled hearing(s) to remove for sitting ids: {}",
                methodName,
                scheduledHearings.size(),
                sittingIds
            );
        }

        // Use a non-null safe list for downstream calls to avoid potential NPEs or static analysis warnings
        List<XhbScheduledHearingDao> safeScheduledHearings =
            scheduledHearings == null ? List.of() : scheduledHearings;

        // Remove related data for scheduled hearings (defendants, attendees, and live display)
        removeScheduledHearingDefendants(safeScheduledHearings);
        removeScheduledHearingAttendees(safeScheduledHearings);
        updateLiveDisplayForScheduledHearings(safeScheduledHearings);

        // Delete scheduled hearings themselves
        getRepositoryHelper().getXhbScheduledHearingRepository().deleteBySittingIds(sittingIds);
        LOG.debug("{} - Removed scheduled hearings for sitting ids: {}", methodName, sittingIds);
        LOG.debug(TWO_PARAMS, methodName, EXITED);
    }

    /**
     * For a given list of scheduled hearings, set the scheduled hearing id to null
     * in any associated live display records.
     * @param scheduledHearings list of scheduled hearings to update in live display
     */
    private void updateLiveDisplayForScheduledHearings(List<XhbScheduledHearingDao> scheduledHearings) {
        // Defensive: compute scheduled ids once and handle null/empty safely
        List<Integer> scheduledIds = (scheduledHearings == null) ? List.of() : scheduledHearings.stream()
            .map(XhbScheduledHearingDao::getScheduledHearingId).toList();
        String methodName = "updateLiveDisplayForScheduledHearings() - scheduledHearingIds: "
            + (scheduledIds.isEmpty() ? "N/A" : scheduledIds);
        if (LOG.isDebugEnabled()) {
            LOG.debug(TWO_PARAMS, methodName, ENTERED);
        }

        if (scheduledIds.isEmpty()) {
            LOG.debug("{} - No scheduled hearing ids provided, nothing to update", methodName);
            LOG.debug(TWO_PARAMS, methodName, EXITED);
            return;
        }

        LOG.debug(
            "{} - Setting scheduled hearing id to null in live display for scheduled hearing ids: {}",
            methodName,
            scheduledIds
        );
        for (Integer schedId : scheduledIds) {
            try {
                getRepositoryHelper().getXhbCrLiveDisplayRepository()
                    .updateScheduledHearingIdToNull(schedId);
            } catch (Exception e) {
                // Log per-id failure and continue with others
                LOG.error(
                    "{} - Error updating live display for scheduledHearingId {}: {}",
                    methodName, schedId, e.getMessage(), e
                );
            }
        }
        LOG.debug(TWO_PARAMS, methodName, EXITED);
    }

    /**
     * For a given list of scheduled hearings, remove the associated defendants.
     * @param scheduledHearings list of scheduled hearings to remove defendants for
     */
    private void removeScheduledHearingDefendants(List<XhbScheduledHearingDao> scheduledHearings) {
        // Defensive: compute scheduled ids once and handle null/empty safely
        List<Integer> scheduledIds = (scheduledHearings == null) ? List.of() : scheduledHearings.stream()
            .map(XhbScheduledHearingDao::getScheduledHearingId).toList();
        String methodName = "removeScheduledHearingDefendants() - scheduledHearingIds: "
            + (scheduledIds.isEmpty() ? "N/A" : scheduledIds);
        if (LOG.isDebugEnabled()) {
            LOG.debug(TWO_PARAMS, methodName, ENTERED);
        }

        if (scheduledIds.isEmpty()) {
            LOG.debug("{} - No scheduled hearing ids provided, nothing to remove", methodName);
            LOG.debug(TWO_PARAMS, methodName, EXITED);
            return;
        }

        // Find the scheduled hearing defendants for these scheduled hearings
        List<XhbSchedHearingDefendantDao> defendants = getRepositoryHelper().getXhbSchedHearingDefendantRepository()
            .findByScheduledHearingIdsSafe(scheduledIds);

        if (defendants != null && !defendants.isEmpty()) {
            LOG.debug(
                "{} - Found {} scheduled hearing defendant(s) to remove for scheduled hearing ids: {}",
                methodName,
                defendants.size(),
                scheduledIds
            );
            getRepositoryHelper().getXhbSchedHearingDefendantRepository()
                .deleteByScheduledHearingIds(scheduledIds);
            LOG.debug("{} - Removed scheduled hearing defendants for scheduled hearing ids: {}",
                methodName, scheduledIds);
        } else {
            LOG.debug("{} - No scheduled hearing defendants found for scheduled hearing ids: {}",
                methodName, scheduledIds);
        }

        LOG.debug(TWO_PARAMS, methodName, EXITED);
    }

    /**
     * For a given list of scheduled hearings, remove the associated attendees and judge relationships.
     * @param scheduledHearings list of scheduled hearings to remove attendees for
     */
    private void removeScheduledHearingAttendees(List<XhbScheduledHearingDao> scheduledHearings) {
        // Defensive: compute scheduled ids once and handle null/empty safely
        List<Integer> scheduledIds = (scheduledHearings == null) ? List.of() : scheduledHearings.stream()
            .map(XhbScheduledHearingDao::getScheduledHearingId).toList();
        String methodName = "removeScheduledHearingAttendees() - scheduledHearingIds: "
            + (scheduledIds.isEmpty() ? "N/A" : scheduledIds);
        if (LOG.isDebugEnabled()) {
            LOG.debug(TWO_PARAMS, methodName, ENTERED);
        }

        if (scheduledIds.isEmpty()) {
            LOG.debug("{} - No scheduled hearing ids provided, nothing to remove", methodName);
            LOG.debug(TWO_PARAMS, methodName, EXITED);
            return;
        }

        // Loop round each scheduled hearing and find the attendees to remove
        for (XhbScheduledHearingDao scheduledHearing : scheduledHearings) {
            Integer schedId = scheduledHearing == null ? null : scheduledHearing.getScheduledHearingId();
            LOG.debug("{} - Processing scheduled hearing with id: {}", methodName, schedId);

            if (schedId == null) {
                LOG.debug("{} - scheduled hearing entry was null - skipping", methodName);
                continue;
            }

            // Find the scheduled hearing attendees for this scheduled hearing id
            List<XhbSchedHearingAttendeeDao> attendees = getRepositoryHelper().getXhbSchedHearingAttendeeRepository()
                .findByScheduledHearingIdSafe(schedId);

            if (attendees != null && !attendees.isEmpty()) {
                LOG.debug(
                    "{} - Found {} scheduled hearing attendee(s) to remove for scheduled hearing id: {}",
                    methodName,
                    attendees.size(),
                    schedId
                );
                // For each attendee we need to find any judge relationships and remove those as well
                for (XhbSchedHearingAttendeeDao attendee : attendees) {
                    if (attendee != null) {
                        getRepositoryHelper().getXhbShJudgeRepository()
                            .deleteByShAttendeeId(attendee.getShAttendeeId());
                        LOG.debug(
                            "{} - Removed judge relationships for scheduled hearing attendee id: {}",
                            methodName, attendee.getShAttendeeId()
                        );
                    }
                }
            } else {
                LOG.debug("{} - No scheduled hearing attendees found for scheduled hearing id: {}",
                    methodName, schedId);
            }
        }

        // Bulk-delete attendees for all scheduled ids (safe no-op if none exist)
        getRepositoryHelper().getXhbSchedHearingAttendeeRepository().deleteByScheduledHearingIds(scheduledIds);
        LOG.debug("{} - Removed scheduled hearing attendees for scheduled hearing ids: {}", methodName, scheduledIds);

        LOG.debug(TWO_PARAMS, methodName, EXITED);
    }

    /**
     * Create the records in XHB_CPP_LIST or XHB_CPP_FORMATTING that are required for processing
     * the document and then processing the document based on the document type.
     * For a list document (DL, FL, WL) a record is created/updated in XHB_CPP_LIST and then a record
     * is created in XHB_FORMATTING for each language (en and cy) and a record is created in XHB_XML_DOCUMENT
     * for each language. For a non-list document (PD, WP) a record is created/updated in XHB_CPP_FORMATTING
     * and then if it is a WP document a record is also created in XHB_FORMATTING
     * for each language (en and cy) and a record is created in XHB_XML_DOCUMENT for each language.

     * @param cppStagingInboundDao the record from XHB_STAGING_INBOUND for which to create the processing records
     * @param documentType the type of document being processed (DL, FL, WL, PD, WP)
     * @param cppListDao the record from XHB_CPP_LIST that was created/updated for this document
     *     (only applicable for list documents, will be null for non-list documents).
     */
    private void createRecordsForProcessing(XhbCppStagingInboundDao cppStagingInboundDao, String documentType, 
        XhbCppListDao cppListDao) {
        // Get the court id
        List<XhbCourtDao> xhbCourtDaoList =
            getXhbCourtRepository().findByCrestCourtIdValueSafe(cppStagingInboundDao.getCourtCode());
        Integer courtId = xhbCourtDaoList.get(0).getCourtId();
        // Create the xhbFormatting record
        XhbFormattingDao xfbv = CppFormattingHelper.createXhbFormattingRecord(courtId,
            cppStagingInboundDao, documentType, "en");
        getXhbFormattingRepository().save(xfbv);

        // Create the xhbXmlDocument record
        XhbXmlDocumentDao xhbXmlDocumentDao =
            CppFormattingHelper.createXhbXmlDDocumentRecord(xfbv, cppListDao, cppStagingInboundDao);
        getXhbXmlDocumentRepository().save(xhbXmlDocumentDao);
    }
    
    private ListNodesHelper getListNodesHelper() {
        if (listNodesHelper == null) {
            listNodesHelper = new ListNodesHelper();
        }
        return listNodesHelper;
    }
    
    protected RepositoryHelper getRepositoryHelper() {
        if (repositoryHelper == null) {
            repositoryHelper = new RepositoryHelper();
        }
        return repositoryHelper;
    }
}
