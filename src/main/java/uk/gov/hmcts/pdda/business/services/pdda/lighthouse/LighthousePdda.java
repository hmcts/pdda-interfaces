package uk.gov.hmcts.pdda.business.services.pdda.lighthouse;

//import jakarta.persistence.EntityManager;
//import org.apache.commons.configuration2.PropertiesConfiguration;
//import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
//import org.apache.commons.configuration2.builder.fluent.Parameters;
//import org.apache.commons.configuration2.ex.ConfigurationException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundDao;
//import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundRepository;
//import uk.gov.hmcts.pdda.business.entities.xhbpddamessage.XhbPddaMessageDao;
//import uk.gov.hmcts.pdda.business.entities.xhbpddamessage.XhbPddaMessageRepository;
//import uk.gov.hmcts.pdda.business.services.pdda.lighthouse.pojo.GeneralProperties;
//
//import java.sql.SQLException;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//import java.util.Optional;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
///**
// * This class is the main class for dealing with inserting CPP data into XHB_CPP_STAGING_INBOUND and
// * updating XHB_PDDA_MESSAGE.
// *
// * <p>When running it will delegate as needed to: - initially setup a database connection pool to be
// * used throughout its lifetime - check for the existence of (unprocessed) records in the database -
// * threads, up to a defined maximum allowed, will be spawned to deal with each record - for each
// * record it will add an entry to XHB_CPP_STAGING_INBOUND - once processed it will then update the
// * message record to show it has been processed.
// */
//@SuppressWarnings("PMD.DoNotUseThreads")
//public class LighthousePdda {

//    // cppProperties can be found in /pdda/support/scripts/resources
//    private static final String PROPERTIES_FILENAME = "cppProperties.properties";
//    private static final DateTimeFormatter DATETIMEFORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
//    private static final Integer FILE_PARTS = 3;
//    private GeneralProperties genProps;
//    private final EntityManager entityManager;
//    private PropertiesConfiguration config;
//    private XhbPddaMessageRepository xhbPddaMessageRepository;
//    private XhbCppStagingInboundRepository xhbCppStagingInboundRepository;
//    static final Logger LOG = LoggerFactory.getLogger(LighthousePdda.class);
//
//    public LighthousePdda(EntityManager entityManager) {
//        this.entityManager = entityManager;
//    }
//
//    /**
//     * Do all setup tasks.
//     */
//    protected PropertiesConfiguration getConfiguration() throws ConfigurationException {
//        LOG.debug("{} :: METHOD ENTRY:: getConfiguration", System.currentTimeMillis());
//
//        // Get all properties
//        if (config == null) {
//            config = getConfigBuilder().getConfiguration();
//        }
//
//        LOG.debug("{} :: METHOD EXIT:: getConfiguration", System.currentTimeMillis());
//        return config;
//    }
//
//    protected FileBasedConfigurationBuilder<PropertiesConfiguration> getBuilder() {
//        return new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class);
//    }
//
//    protected FileBasedConfigurationBuilder<PropertiesConfiguration> getConfigBuilder() {
//        return getBuilder().configure(new Parameters().properties().setFileName(PROPERTIES_FILENAME));
//    }
//
//    /**
//     * Process all the messages held in the database that have been validated and are awaiting
//     * processing.
//     */
//    public void processFiles() {
//        LOG.debug("{} :: METHOD ENTRY:: processFiles", System.currentTimeMillis());
//
//        try {
//            // Get the properties file
//            setGeneralProperties(getConfiguration());
//
//            ExecutorService executor = Executors.newFixedThreadPool(genProps.getNumThreads());
//            // Fetch the messages and process them
//            List<XhbPddaMessageDao> daoList = getXhbPddaMessageRepository().findByLighthouse();
//            for (XhbPddaMessageDao dao : daoList) {
//                Runnable worker = getRunnable(dao);
//                executor.execute(worker);
//            }
//
//            executor.shutdown();
//            while (!executor.isTerminated()) {
//                // Wait until all threads are finished
//                doNothing();
//            }
//        } catch (ConfigurationException ce) {
//            LOG.error("Failed to load config " + PROPERTIES_FILENAME);
//        }
//        LOG.debug("{} :: METHOD EXIT:: processFiles", System.currentTimeMillis());
//    }
//
//    private Runnable getRunnable(XhbPddaMessageDao dao) {
//        return new RunPddaJob(getXhbPddaMessageRepository(), getXhbCppStagingInboundRepository(), dao);
//    }
//
//    private void doNothing() {
//        // do nothing
//    }
//
//    /**
//     * Set the General properties.
//     *
//     * @param config Configuration object to read from
//     */
//    private void setGeneralProperties(final PropertiesConfiguration config) {
//        genProps = new GeneralProperties(config.getInt("gen.numThreads"));
//    }
//
//    protected XhbPddaMessageRepository getXhbPddaMessageRepository() {
//        if (xhbPddaMessageRepository == null) {
//            xhbPddaMessageRepository = new XhbPddaMessageRepository(entityManager);
//        }
//        return xhbPddaMessageRepository;
//    }
//
//    protected XhbCppStagingInboundRepository getXhbCppStagingInboundRepository() {
//        if (xhbCppStagingInboundRepository == null) {
//            xhbCppStagingInboundRepository = new XhbCppStagingInboundRepository(entityManager);
//        }
//        return xhbCppStagingInboundRepository;
//    }
//
//
//    /**
//     * A class that does the actual work, adds a record the xhb_cpp_staging_inbound table and updates
//     * the xhb_pdda_message table. Each message is picked up by its own thread. With a maximum number of
//     * threads running at a time (defined in the property file).
//     */
//    class RunPddaJob implements Runnable {
//
//        private final XhbPddaMessageDao dao;
//        private final XhbPddaMessageRepository xhbPddaMessageRepository;
//        private final XhbCppStagingInboundRepository xhbCppStagingInboundRepository;
//
//        private static final String NEW_STAGING_INBOUND_STATUS = "NP";
//        private static final String MESSAGE_STATUS_INPROGRESS = "IP";
//        private static final String MESSAGE_STATUS_PROCESSED = "VP";
//        private static final String MESSAGE_STATUS_INVALID = "INV";
//
//        /**
//         * Constructor.
//         *
//         * @param xhbPddaMessageRepository XhbPddaMessage Repository
//         * @param xhbCppStagingInboundRepository XhbCppStagingInboundRepository
//         * @param dao XhbPddaMessage dao
//         */
//        RunPddaJob(final XhbPddaMessageRepository xhbPddaMessageRepository,
//            final XhbCppStagingInboundRepository xhbCppStagingInboundRepository, final XhbPddaMessageDao dao) {
//            writeToLog("Setting up the RunPDDAJob to process a file");
//            this.xhbPddaMessageRepository = xhbPddaMessageRepository;
//            this.xhbCppStagingInboundRepository = xhbCppStagingInboundRepository;
//            this.dao = dao;
//        }
//
//        /**
//         * Overridden run method to be invoked for each instance  in a Thread.
//         */
//        @Override
//        public void run() {
//            writeToLog("About to process file " + dao.getCpDocumentName());
//
//            try {
//                // split up the filename into its 3 parts : type_courtCode_dateTime
//                String[] fileParts = dao.getCpDocumentName().split("_");
//                if (fileParts.length == FILE_PARTS) {
//                    // Update the status to indicate it is being processed
//                    updatePddaMessageInProgress();
//
//                    String strTimeLoaded = fileParts[2].replaceAll(".xml", "");
//                    LocalDateTime timeLoaded = LocalDateTime.parse(strTimeLoaded.trim(), DATETIMEFORMAT);
//
//                    // Insert XHB_CPP_STAGING_INBOUND row, returning PK
//                    writeToLog("About to add " + dao.getPddaMessageDataId() + " to the cpp staging inbound table");
//                    Integer stagingInboundId = insertStaging(dao.getCpDocumentName(), fileParts[1],
//                        getDocType(fileParts[0]), timeLoaded, dao.getPddaMessageDataId());
//                    writeToLog("Successfully added  " + stagingInboundId + " to the cpp staging inbound table");
//
//                    // Update XHB_PDDA_MESSAGE record to indicate success
//                    updatePddaMessage(stagingInboundId);
//
//                    writeToLog("Processing of " + dao.getCpDocumentName() + " completed");
//                } else {
//                    LOG.error("Filename is not valid : " + dao.getCpDocumentName());
//                }
//            } catch (RuntimeException e) {
//                LOG.error(
//                    "Error adding data to the database for file " + dao.getCpDocumentName()
//                    + " :" + e.getStackTrace());
//                // Change the status of the XHB_PDDA_MESSAGE record to invalid
//                updatePddaMessageForError();
//            }
//        }
//
//        /**
//         * Write to the debug log if debug is enabled.
//         *
//         * @param string Debug message to write to the log
//         */
//        private void writeToLog(final String string) {
//            if (LOG.isDebugEnabled()) {
//                LOG.debug(System.currentTimeMillis() + " :: " + string);
//            }
//        }
//
//        private XhbPddaMessageDao fetchLatestXhbPddaMessageDao() {
//            LOG.debug("fetchLatestXhbPddaMessageDao()");
//            Optional<XhbPddaMessageDao> opt = xhbPddaMessageRepository.findById(dao.getPrimaryKey());
//            return opt.isPresent() ? opt.get() : dao;
//        }
//
//        /**
//         * Updates the XHB_PDDA_MESSAGE record to show it is in progress.
//         */
//        private void updatePddaMessageInProgress() {
//            XhbPddaMessageDao latest = fetchLatestXhbPddaMessageDao();
//            latest.setCpDocumentStatus(MESSAGE_STATUS_INPROGRESS);
//            xhbPddaMessageRepository.save(latest);
//        }
//
//        /**
//         * Insert the row into XHB_CPP_STAGING_INBOUND.
//         *
//         * @param docName - Document name e.g. DailyList_453_20200101123213.xml
//         * @param courtCode from the document name
//         * @param documentType e.g. DL
//         * @param timeLoaded which is the 3rd part of the document name
//         * @param clobId that was created pre this insert
//         * @return the staging inbound id for debugging/logging purposes
//         * @throws SQLException Exception
//         */
//        private Integer insertStaging(final String docName, final String courtCode, final String documentType,
//            final LocalDateTime timeLoaded, final Long clobId) {
//
//            writeToLog(
//                "doc " + docName + " courtCode: " + courtCode + " documentType: " + documentType + " timeLoaded: "
//                    + timeLoaded + " clobId :" + clobId + " validationStatus :" + NEW_STAGING_INBOUND_STATUS);
//
//            XhbCppStagingInboundDao xhbCppStagingInboundDao = new XhbCppStagingInboundDao();
//            xhbCppStagingInboundDao.setDocumentName(docName);
//            xhbCppStagingInboundDao.setCourtCode(courtCode);
//            xhbCppStagingInboundDao.setDocumentType(documentType);
//            xhbCppStagingInboundDao.setTimeLoaded(timeLoaded);
//            xhbCppStagingInboundDao.setClobId(clobId);
//            Optional<XhbCppStagingInboundDao> opt = xhbCppStagingInboundRepository.update(xhbCppStagingInboundDao);
//            return opt.isPresent() ? opt.get().getCppStagingInboundId() : null;
//        }
//
//        /**
//         * Update the XHB_PDA_MESSAGE record.
//         *
//         * @param stagingInboundId - cpp staging inbound id
//         * @throws SQLException Exception
//         */
//        private void updatePddaMessage(final Integer stagingInboundId) {
//
//            writeToLog("doc " + dao.getCpDocumentName() + " docStatus: " + MESSAGE_STATUS_PROCESSED + " messageId: "
//                + dao.getPddaMessageId());
//
//            XhbPddaMessageDao latest = fetchLatestXhbPddaMessageDao();
//            latest.setCpDocumentStatus(MESSAGE_STATUS_PROCESSED);
//            latest.setCppStagingInboundId(stagingInboundId);
//            xhbPddaMessageRepository.save(latest);
//
//        }
//
//        /**
//         * In the event of an error performing the normal database updates, this method updates the status
//         * of the XHB_PDDA_MESSAGE record to invalid.
//         */
//        private void updatePddaMessageForError() {
//            LOG.debug("updatePddaMessageForError()");
//            XhbPddaMessageDao latest = fetchLatestXhbPddaMessageDao();
//            latest.setCpDocumentStatus(MESSAGE_STATUS_INVALID);
//            xhbPddaMessageRepository.save(latest);
//        }
//
//        /**
//         * Return the document type depending on what's been used in the file name.
//         *
//         * @param fileType portion of the filename
//         * @return shorthand documentType
//         */
//        private String getDocType(final String fileType) {
//            writeToLog("METHOD ENTRY: getDocType");
//
//            DocumentType docType = DocumentType.fromString(fileType);
//            if (docType == null) {
//                return null;
//            } else {
//                return docType.name();
//            }
//        }
//    }

//}
