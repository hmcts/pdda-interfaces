package uk.gov.hmcts.pdda.business.services.pdda.sftp;

import jakarta.ejb.EJBException;
import jakarta.persistence.EntityManager;
import javassist.NotFoundException;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicDisplayEvent;
import uk.gov.hmcts.framework.services.CsServices;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobDao;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbpddamessage.XhbPddaMessageDao;
import uk.gov.hmcts.pdda.business.entities.xhbrefpddamessagetype.XhbRefPddaMessageTypeDao;
import uk.gov.hmcts.pdda.business.services.pdda.BaisValidation;
import uk.gov.hmcts.pdda.business.services.pdda.PddaMessageHelper;
import uk.gov.hmcts.pdda.business.services.pdda.PddaMessageUtil;
import uk.gov.hmcts.pdda.business.services.pdda.PddaSerializationUtils;
import uk.gov.hmcts.pdda.business.services.pdda.PddaSftpHelper;
import uk.gov.hmcts.pdda.business.services.pdda.PddaSftpValidationUtil;
import uk.gov.hmcts.pdda.business.services.pdda.XhibitPddaHelper;
import uk.gov.hmcts.pdda.web.publicdisplay.initialization.servlet.InitializationService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class SftpHelper extends XhibitPddaHelper {

    private static final Logger LOG = LoggerFactory.getLogger(SftpHelper.class);

    private static final String TWO_PARAMS = "{}{}";

    private static final String NO = "N";
    private static final String INVALID_MESSAGE_TYPE = "Invalid";
    private static final String SFTP_ERROR = "SFTP Error:";

    protected static final String LOG_CALLED = " called";
    protected static final String CP_CONNECTION_TYPE = "CP";
    protected static final String XHIBIT_CONNECTION_TYPE = "XHIBIT";
    protected static final String TEST_CONNECTION_TYPE = "TEST";

    private static final String USE_KEY_VAULT_PROPERTIES = "USE_KEY_VAULT_PROPERTIES";


    // Instance of the PddaSftpHelper
    private PddaSftpHelperSshj pddaSftpHelperSshj;

    // Instance of the SftpConfigHelper
    private SftpConfigHelper sftpConfigHelper;


    /**
     * Constructor.
     * 
     * @param entityManager The EntityManager
     */
    public SftpHelper(EntityManager entityManager) {
        super(entityManager, InitializationService.getInstance().getEnvironment());
    }

    /**
     * JUnit constructor.
     * 
     * @param entityManager The EntityManager
     * @param xhbConfigPropRepository The XhbConfigPropRepository
     * @param environment The Environment
     * @param sftpHelper PddaSftpHelper
     */
    public SftpHelper(EntityManager entityManager, XhbConfigPropRepository xhbConfigPropRepository,
        Environment environment, PddaSftpHelper sftpHelper) {
        super(entityManager, xhbConfigPropRepository, environment, sftpHelper);
    }


    /**
     * Retrieve messages from BAIS.
     */
    @SuppressWarnings("PMD")
    public void processBaisMessages() {

        methodName = "processBaisMessages()";
        LOG.debug(methodName, LOG_CALLED);

        SftpConfig sftpConfig = populateSftpConfig();

        try (SSHClient ssh = sftpConfigHelper.getNewSshClient()) {
            ssh.connect(sftpConfig.getHost(), sftpConfig.getPort());

            // Do the authentication based on the configuration; CP first, then XHIBIT
            ssh.authPassword(sftpConfig.getCpUsername(), sftpConfig.getCpPassword());
            sftpConfig.setSshClient(ssh);
            try (SFTPClient sftpClient = ssh.newSFTPClient()) {
                try {
                    sftpConfig.setActiveRemoteFolder(sftpConfig.getCpRemoteFolder());
                    sftpConfig.setSshjSftpClient(sftpClient);
                    retrieveFromBais(sftpConfig, new BaisCpValidation(getCourtRepository()));
                } catch (IOException e) {
                    LOG.error("Error processing file: {}", ExceptionUtils.getStackTrace(e));
                }
            }
        } catch (IOException e) {
            LOG.error("Error processing files from BAIS CP: {}", ExceptionUtils.getStackTrace(e));
        }

        try (SSHClient ssh = sftpConfigHelper.getNewSshClient()) {
            ssh.connect(sftpConfig.getHost(), sftpConfig.getPort());

            // Do the authentication based on the configuration; CP first, then XHIBIT
            ssh.authPassword(sftpConfig.getXhibitUsername(), sftpConfig.getXhibitPassword());
            sftpConfig.setSshClient(ssh);
            try (SFTPClient sftpClient = ssh.newSFTPClient()) {
                try {
                    sftpConfig.setActiveRemoteFolder(sftpConfig.getXhibitRemoteFolder());
                    sftpConfig.setSshjSftpClient(sftpClient);
                    retrieveFromBais(sftpConfig, new BaisXhibitValidation(getCourtRepository()));
                } catch (IOException e) {
                    LOG.error("Error processing file: {}", ExceptionUtils.getStackTrace(e));
                }
            }

        } catch (IOException e) {
            LOG.error("Error processing files from BAIS XHIBIT: {}",
                ExceptionUtils.getStackTrace(e));
        }
    }


    /**
     * Populate the SFTP configuration.
     * 
     * @return The SFTP configuration
     */
    private SftpConfig populateSftpConfig() {

        methodName = "populateSftpConfig()";
        LOG.debug(methodName, LOG_CALLED);

        SftpConfig sftpConfig = new SftpConfig();

        // Firstly, are we using Database or Key Vault to lookup credentials?
        // This will be a database lookup. If it is indeterminate, we will use the Database.
        if (checkWhetherToUseKeyVault()) {
            sftpConfig.setUseKeyVault(true);
        } else {
            sftpConfig.setUseKeyVault(false);
        }

        // Set the rest of the params
        sftpConfig = getSftpConfigHelper().getConfigParams(sftpConfig);

        return sftpConfig;
    }


    /**
     * Check whether to use Key Vault.
     * 
     * @return True if using Key Vault, false otherwise
     */
    private boolean checkWhetherToUseKeyVault() {
        
        methodName = "retrieveFromBaisCp()";
        LOG.debug(methodName, LOG_CALLED);

        String methodName = "checkWhetherToUseKeyVault()";
        LOG.debug(TWO_PARAMS, methodName, LOG_CALLED);

        // Fetch and validate the properties
        try {
            String useKeyVault = getMandatoryConfigValue(USE_KEY_VAULT_PROPERTIES);
            LOG.debug("Use key Vault?: {}", useKeyVault);
            return Boolean.parseBoolean(useKeyVault);
        } catch (InvalidConfigException ex) {
            LOG.error("Error fetching properties: {}", ExceptionUtils.getStackTrace(ex));
            return false;
        }
    }


    /**
     * Retrieve events from BAIS.
     * 
     * @param config The SFTP configuration
     * @param baisXhibitValidation The validation class
     * @throws IOException The IOException
     */
    private void retrieveFromBais(SftpConfig config, BaisValidation baisXhibitValidation)
        throws IOException {
        
        methodName = "retrieveFromBais()";
        LOG.debug(methodName, LOG_CALLED);

        try {
            LOG.debug("retrieveFromBais({},{})", config, baisXhibitValidation);
            Map<String, String> files = getBaisFileList(config, baisXhibitValidation);
            LOG.debug("Total of {}{}", files.size(),
                " files, before processing, in this transaction.");
            if (!files.isEmpty()) {
                // Process the files we have retrieved.
                for (Map.Entry<String, String> entry : files.entrySet()) {
                    String filename = entry.getKey();
                    String clobData = entry.getValue();
                    processBaisFile(config, baisXhibitValidation, filename, clobData);
                }
            }
        } finally {
            if (config.getSshjSftpClient() == null) {
                // Check the contents of BAIS to verify all processed files are deleted
                LOG.debug("Checking contents of Bais prior to closing the connection");
                Map<String, String> files = getBaisFileList(config, baisXhibitValidation);
                if (files.isEmpty()) {
                    LOG.debug("Contents of Bais is now empty");
                } else {
                    LOG.debug("Contents of Bais is still not empty - is that correct??");
                }
            }
        }
    }


    // This method is used for reference to SftpConfig in the unit tests
    /*
     * public SftpConfig getSftpConfigsForTest(SftpConfig sftpConfig) { methodName =
     * "getSftpConfigsForTest()"; LOG.debug(methodName, LOG_CALLED);
     * 
     * SftpConfigHelper sftpConfigHelper = new SftpConfigHelper(entityManager); return
     * sftpConfigHelper.setSftpConfigData(TEST_CONNECTION_TYPE, sftpConfig); }
     */


    /**
     * Get a list of files on BAIS.
     * 
     * @param config The SFTP configuration
     * @param validation The validation class
     * @return The list of files
     */
    private Map<String, String> getBaisFileList(SftpConfig config,
        BaisValidation validation) {
        methodName = "getBaisFileList()";
        LOG.debug(methodName, LOG_CALLED);

        try {
            // Do something
            return getPddaSftpHelperSshj().sftpFetch(config.getSshjSftpClient(),
                config.getActiveRemoteFolder(), validation);
        } catch (Exception ex) {
            LOG.error(SFTP_ERROR + " {} ", ExceptionUtils.getStackTrace(ex));
            return Collections.emptyMap();
        }
    }


    /**
     * Process a file from BAIS.
     * 
     * @param config The SFTP configuration
     * @param validation The validation class
     * @param filename The filename
     * @param clobData The CLOB data
     * @throws IOException The IOException
     */
    private void processBaisFile(SftpConfig config, BaisValidation validation,
        String filename, String clobData) throws IOException {

        methodName = "processBaisFile()";
        LOG.debug(methodName, LOG_CALLED);

        try {
            LOG.debug("Processing filename {}", filename);

            // Validate this filename hasn't been processed previously
            Optional<XhbPddaMessageDao> pddaMessageDao =
                validation.getPddaMessageDao(getPddaMessageHelper(), filename);
            if (pddaMessageDao.isPresent()) {
                LOG.debug("Filename {}{}", filename, " already processed");
                return;
            }

            // Get the event (if from Xhibit. CP will be null)
            PublicDisplayEvent event = validation.getPublicDisplayEvent(filename, clobData);

            // Validate the filename
            String errorMessage = validation.validateFilename(filename, event);

            // Validate messageType
            String messageType = validation.getMessageType(filename, event);
            if (EMPTY_STRING.equals(messageType)) {
                messageType = INVALID_MESSAGE_TYPE;
            }

            // Get the crestCourtId (should have already been validated by this point)
            Integer courtId = validation.getCourtId(filename, event);

            // Write the pddaMessage
            createBaisMessage(courtId, messageType, filename, clobData, errorMessage);

            getPddaSftpHelperSshj().sftpDeleteFile(config.getSshjSftpClient(),
                config.getActiveRemoteFolder(), filename);
            LOG.debug("Removed file from bais after processing: {}", filename);

        } catch (IOException | NotFoundException ex) {
            LOG.error("Error processing BAIS file {} ", ExceptionUtils.getStackTrace(ex));
            CsServices.getDefaultErrorHandler().handleError(ex, getClass());
            throw new EJBException(ex);
        }
    }


    /**
     * Add a message retrieved from BAIS into the PDDA database.
     * 
     * @param courtId The court ID
     * @param messageType The message type
     * @param filename The filename
     * @param clobData The CLOB data
     * @param errorMessage The error message
     * @throws NotFoundException The NotFoundException
     */
    private void createBaisMessage(final Integer courtId, final String messageType,
        final String filename, final String clobData, String errorMessage)
        throws NotFoundException {

        methodName = "createBaisMessage(" + filename + ")";
        LOG.debug(methodName, LOG_CALLED);

        // Call to createMessageType
        Optional<XhbRefPddaMessageTypeDao> messageTypeDao =
            getPddaMessageHelper().findByMessageType(messageType);
        if (messageTypeDao.isEmpty()) {
            messageTypeDao = PddaMessageUtil.createMessageType(getPddaMessageHelper(), messageType,
                LocalDateTime.now());
        }
        if (messageTypeDao.isEmpty()) {
            // This should never occur
            throw new NotFoundException("Message Type");
        }

        // Create the clob data for the message
        Optional<XhbClobDao> clob = PddaMessageUtil.createClob(getClobRepository(), clobData);
        Long pddaMessageDataId = clob.isPresent() ? clob.get().getClobId() : null;
        // Call createMessage
        PddaMessageUtil.createMessage(getPddaMessageHelper(), courtId, null,
            messageTypeDao.get().getPddaMessageTypeId(), pddaMessageDataId, null, filename, NO,
            errorMessage);
    }


    /**
     * Get a new instance of PddaSftpHelperSshj.
     * 
     * @return The PddaSftpHelperSshj
     */
    private PddaSftpHelperSshj getPddaSftpHelperSshj() {
        if (pddaSftpHelperSshj == null) {
            pddaSftpHelperSshj = new PddaSftpHelperSshj();
        }
        return pddaSftpHelperSshj;
    }

    private SftpConfigHelper getSftpConfigHelper() {
        if (sftpConfigHelper == null) {
            sftpConfigHelper = new SftpConfigHelper(entityManager);
        }
        return sftpConfigHelper;
    }


    /**
     * A class to validate XHIBIT messages retrieved from BAIS.
     */
    public static class BaisXhibitValidation extends BaisValidation {

        private static final String PDDA = "PDDA";

        public BaisXhibitValidation(XhbCourtRepository courtRepository) {
            super(courtRepository, false, 4);
        }

        @Override
        public String validateFilename(String filename, PublicDisplayEvent event) {
            String debugErrorPrefix = filename + " error: {}";
            String errorMessage = super.validateFilename(filename);
            if (errorMessage != null) {
                LOG.debug(debugErrorPrefix, errorMessage);
                return errorMessage;
            }

            // Check the file has the right overall format of 4 parts
            if (isValidNoOfParts(filename)) {
                LOG.debug("Valid filename - No. Of Parts");
            } else {
                errorMessage = "Invalid filename - No. Of Parts";
                LOG.debug(debugErrorPrefix, errorMessage);
                return errorMessage;
            }
            // Check Title is right format
            if (PDDA.equalsIgnoreCase(getFilenamePart(filename, 0))) {
                LOG.debug("Valid filename - Title");
            } else {
                errorMessage = "Invalid filename - Title";
                LOG.debug(debugErrorPrefix, errorMessage);
                return errorMessage;
            }

            // Check we have the event from the file contents
            if (event == null) {
                errorMessage = "Invalid filename - Invalid event in filecontents";
                LOG.debug(debugErrorPrefix, errorMessage);
                return errorMessage;

                // Check the crest court Id
            } else if (getCourtId(filename, event) == null) {
                errorMessage = "Invalid filename - CrestCourtId";
                LOG.debug(debugErrorPrefix, errorMessage);
                return errorMessage;
            }


            return null;
        }

        @Override
        public Optional<XhbPddaMessageDao> getPddaMessageDao(PddaMessageHelper pddaMessageHelper,
            String filename) {
            return Optional.empty();
        }

        @Override
        public boolean validateTitle(String filename) {
            return PddaSftpValidationUtil.validateTitle(getFilenamePart(filename, 0), PDDA);
        }

        @Override
        public String getMessageType(String filename, PublicDisplayEvent event) {
            if (event != null) {
                return event.getClass().getSimpleName().replace("Event", "");
            }
            return EMPTY_STRING;
        }

        @Override
        public Integer getCourtId(String filename, PublicDisplayEvent event) {
            if (event != null) {
                return event.getCourtId();
            }
            return null;
        }

        @Override
        public PublicDisplayEvent getPublicDisplayEvent(String filename, String fileContents) {
            if (isValidNoOfParts(filename) && validateTitle(filename)) {
                byte[] decodedEvent = PddaSerializationUtils.decodePublicEvent(fileContents);
                return PddaSerializationUtils.deserializePublicEvent(decodedEvent);
            }
            return null;
        }
    }


    /**
     * A class to validate CP messages retrieved from BAIS.
     */
    public static class BaisCpValidation extends BaisValidation {

        private static final String[] POSSIBLETITLES =
            {"DailyList", "FirmList", "WarnedList", "WebPage", "PublicDisplay"};

        public BaisCpValidation(XhbCourtRepository courtRespository) {
            super(courtRespository, false, 3);
        }

        @Override
        public String validateFilename(String filename, PublicDisplayEvent event) {
            String debugErrorPrefix = filename + " error: {}";
            String errorMessage = super.validateFilename(filename);
            if (errorMessage != null) {
                LOG.debug(debugErrorPrefix, errorMessage);
                return errorMessage;
            }

            // First check file extension is an xml file
            if (!PddaSftpValidationUtil.validateExtension(filename)) {
                errorMessage = "Invalid filename - Extension";
                LOG.debug(debugErrorPrefix, errorMessage);
                return errorMessage;
            }

            // Check the file has the right overall format of 3 parts
            if (!isValidNoOfParts(filename)) {
                errorMessage = "Invalid filename - No. Of Parts";
                LOG.debug(debugErrorPrefix, errorMessage);
                return errorMessage;
            }

            // Check Title is right format
            if (!validateTitle(filename)) {
                errorMessage = "Invalid filename - Title";
                LOG.debug(debugErrorPrefix, errorMessage);
                return errorMessage;
            }

            // Check CrestCourtID is valid and exists in the database
            if (getCourtId(filename, event) == null) {
                errorMessage = "Invalid filename - CrestCourtId";
                LOG.debug(debugErrorPrefix, errorMessage);
                return errorMessage;
            }

            // Check dateTime is valid format
            if (!validateDateTime(getFilenamePart(filename, 2))) {
                errorMessage = "Invalid filename - DateTime";
                LOG.debug(debugErrorPrefix, errorMessage);
                return errorMessage;
            }
            return null;
        }

        @Override
        public boolean validateTitle(String filename) {
            return PddaSftpValidationUtil.validateTitle(getFilenamePart(filename, 0),
                POSSIBLETITLES);
        }

        @Override
        public Optional<XhbPddaMessageDao> getPddaMessageDao(PddaMessageHelper pddaMessageHelper,
            String filename) {
            return pddaMessageHelper.findByCpDocumentName(filename);
        }

        @Override
        public String getMessageType(String filename, PublicDisplayEvent event) {
            return getFilenamePart(filename, 0);
        }

        public String getCrestCourtId(String filename) {
            return getFilenamePart(filename, 1);
        }

        @Override
        public Integer getCourtId(String filename, PublicDisplayEvent event) {
            LOG.debug("getCourtId({},{})", filename, event);
            Integer courtId = null;
            String crestCourtId = getCrestCourtId(filename);
            if (!EMPTY_STRING.equals(crestCourtId)) {
                List<XhbCourtDao> courtDao =
                    xhbCourtRepository.findByCrestCourtIdValue(crestCourtId);
                if (courtDao.isEmpty()) {
                    LOG.debug("No court exists for crestCourtId {}", crestCourtId);
                } else {
                    courtId = courtDao.get(0).getCourtId();
                }
            }
            return courtId;
        }

        @Override
        public PublicDisplayEvent getPublicDisplayEvent(String filename, String fileContents) {
            // Not required
            return null;
        }
    }

}
