package uk.gov.hmcts.pdda.business.services.pdda;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import jakarta.ejb.EJBException;
import jakarta.persistence.EntityManager;
import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.framework.services.CsServices;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobDao;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundDao;
import uk.gov.hmcts.pdda.business.entities.xhbpddamessage.XhbPddaMessageDao;
import uk.gov.hmcts.pdda.business.entities.xhbrefpddamessagetype.XhbRefPddaMessageTypeDao;
import uk.gov.hmcts.pdda.business.services.formatting.FormattingServiceUtils;
import uk.gov.hmcts.pdda.common.publicdisplay.events.PublicDisplayEvent;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * Title: PDDAHelper.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2022
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @author Mark Harris
 * @version 1.0
 */
@SuppressWarnings("PMD.GodClass")
public class PddaHelper extends XhibitPddaHelper {
    private static final Logger LOG = LoggerFactory.getLogger(PddaHelper.class);

    private final DateFormat cpResponseFileDateFormat = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault());
    private static final String NO = "N";
    private static final String INVALID_MESSAGE_TYPE = "Invalid";
    private static final String CP_FILE_EXTENSION = ".xml";

    private static final String INVALID = "INV";
    private static final String VALIDATION_FAIL = "VF";

    private static final String SFTP_ERROR = "SFTP Error:";
    private static final String NOT_FOUND = " not found";

    public PddaHelper(EntityManager entityManager) {
        super(entityManager);
    }

    // Junit constructor
    public PddaHelper(EntityManager entityManager, XhbConfigPropRepository xhbConfigPropRepository) {
        super(entityManager, xhbConfigPropRepository);
    }

    /**
     * Retrieve events from Bais (processed by CP).
     */
    public void retrieveFromBaisCp() {
        methodName = "retrieveFromBaisCp()";
        LOG.debug(methodName + LOG_CALLED);

        SftpConfig config = getBaisCpConfigs();
        if (config.errorMsg != null) {
            LOG.error(config.errorMsg);
            return;
        }

        retrieveFromBais(config, new BaisCpValidation(getCourtRepository()));
    }

    /**
     * Retrieve events from Bais (processed by Xhibit).
     */
    public void retrieveFromBaisXhibit() {
        methodName = "retrieveFromBiasXhibit()";
        LOG.debug(methodName + LOG_CALLED);

        SftpConfig config = getSftpConfigs();
        if (config.errorMsg != null) {
            LOG.error(config.errorMsg);
            return;
        }

        retrieveFromBais(config, new BaisXhibitValidation(getCourtRepository()));
    }

    private void retrieveFromBais(SftpConfig config, BaisValidation validation) {
        // Get the file list and then disconnect the session
        try {
            LOG.debug("retrieveFromBais({},{})", config, validation);
            Map<String, String> files = getBaisFileList(config, validation);
            if (!files.isEmpty()) {

                // Process the files we have retrieved.
                for (Map.Entry<String, String> entry : files.entrySet()) {
                    String filename = entry.getKey();
                    String clobData = entry.getValue();

                    processBaisFile(config, validation, filename, clobData);
                }
            }
        } finally {
            if (config.session != null) {
                PddaSftpUtil.disconnectSession(config.session);
                config.setSession(null);
            }
        }
    }

    private void processBaisFile(SftpConfig config, BaisValidation validation, String filename, String clobData) {
        try {
            LOG.debug("Processing filename " + filename);

            // Validate this filename hasn't been processed previously
            Optional<XhbPddaMessageDao> pddaMessageDao = validation.getPddaMessageDao(getPddaMessageHelper(), filename);
            if (pddaMessageDao.isPresent()) {
                LOG.debug("Filename " + filename + " already processed");
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

            getSftpHelper().sftpDeleteFile(config.session, config.remoteFolder, filename);
        } catch (JSchException | SftpException | NotFoundException ex) {
            CsServices.getDefaultErrorHandler().handleError(ex, getClass());
            throw new EJBException(ex);
        }
    }

    private void createBaisMessage(final Integer courtId, final String messageType, final String filename,
        final String clobData, String errorMessage) throws NotFoundException {
        methodName = "createBaisMessage(" + filename + ")";
        LOG.debug(methodName + LOG_CALLED);

        // Call to createMessageType
        Optional<XhbRefPddaMessageTypeDao> messageTypeDao = getPddaMessageHelper().findByMessageType(messageType);
        if (messageTypeDao.isEmpty()) {
            messageTypeDao =
                PddaMessageUtil.createMessageType(getPddaMessageHelper(), messageType, LocalDateTime.now());
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
            messageTypeDao.get().getPddaMessageTypeId(), pddaMessageDataId, null, filename, NO, errorMessage);
    }

    private Map<String, String> getBaisFileList(SftpConfig config, BaisValidation validation) {
        try {
            return getSftpHelper().sftpFetch(config.session, config.remoteFolder, validation);
        } catch (Exception ex) {
            LOG.error(SFTP_ERROR + ex.getMessage());
            return Collections.emptyMap();
        }
    }

    private SftpConfig getSftpConfigs() {
        return getSftpConfigs(Config.SFTP_USERNAME, Config.SFTP_PASSWORD, Config.SFTP_UPLOAD_LOCATION);
    }

    private SftpConfig getSftpConfigs(String configUsername, String configPassword, String configLocation) {
        SftpConfig sftpConfig = new SftpConfig();

        // Fetch and validate the properties
        try {
            sftpConfig.username = getMandatoryConfigValue(configUsername);
        } catch (InvalidConfigException ex) {
            sftpConfig.errorMsg = configUsername + NOT_FOUND;
            return sftpConfig;
        }
        try {
            sftpConfig.password = getMandatoryConfigValue(configPassword);
        } catch (InvalidConfigException ex) {
            sftpConfig.errorMsg = configPassword + NOT_FOUND;
            return sftpConfig;
        }
        try {
            sftpConfig.remoteFolder = getMandatoryConfigValue(configLocation);
        } catch (InvalidConfigException ex) {
            sftpConfig.errorMsg = configLocation + NOT_FOUND;
            return sftpConfig;
        }
        String hostAndPort;
        try {
            hostAndPort = getMandatoryConfigValue(Config.SFTP_HOST);
        } catch (InvalidConfigException ex) {
            sftpConfig.errorMsg = Config.SFTP_HOST + NOT_FOUND;
            return sftpConfig;
        }

        // Validate the host and port
        String portDelimiter = ":";
        Integer pos = hostAndPort.indexOf(portDelimiter);
        if (pos <= 0) {
            sftpConfig.errorMsg = Config.SFTP_HOST + " syntax is <Host>" + portDelimiter + "<Port>";
            return sftpConfig;
        }
        sftpConfig.host = hostAndPort.substring(0, pos);
        try {
            String strPort = hostAndPort.substring(pos + 1, hostAndPort.length());
            sftpConfig.port = Integer.valueOf(strPort);
        } catch (Exception ex) {
            sftpConfig.errorMsg = Config.SFTP_HOST + " contains invalid port number";
            return sftpConfig;
        }

        // Create a session
        try {
            sftpConfig.setSession(getSftpHelper().createSession(sftpConfig.username, sftpConfig.password,
                sftpConfig.host, sftpConfig.port));
        } catch (Exception ex) {
            sftpConfig.errorMsg = SFTP_ERROR + ex.getMessage();
            return sftpConfig;
        }

        return sftpConfig;
    }

    // This method is used for reference to SftpConfig in the unit tests
    public SftpConfig getSftpConfigsForTest() {
        return getSftpConfigs(Config.SFTP_USERNAME, Config.SFTP_PASSWORD, Config.SFTP_UPLOAD_LOCATION);
    }

    private SftpConfig getBaisCpConfigs() {
        return getSftpConfigs(Config.CP_SFTP_USERNAME, Config.CP_SFTP_PASSWORD, Config.CP_SFTP_UPLOAD_LOCATION);
    }

    public void checkForCpMessages(String userDisplayName) throws IOException {
        // Find Messages
        LOG.debug("checkForCpMessages({})", userDisplayName);
        List<XhbPddaMessageDao> messages = getPddaMessageHelper().findUnrespondedCpMessages();
        List<XhbCppStagingInboundDao> cppMessages = getCppStagingInboundHelper().findUnrespondedCppMessages();

        Map<String, InputStream> responses = new ConcurrentHashMap<>();
        Map<String, InputStream> cppResponses = new ConcurrentHashMap<>();

        // Build Messages
        if (!messages.isEmpty()) {
            responses = respondToPddaMessage(messages);
        }

        if (!cppMessages.isEmpty()) {
            cppResponses = respondToCppStagingInbound(cppMessages);
        }

        // Add both Maps together so all responses are in one Map
        responses.putAll(cppResponses);

        // Send responses to bais via sftp
        boolean sftpSuccess = sendMessageRepsonses(responses);

        // Update database records
        if (sftpSuccess) {
            PddaMessageUtil.updatePddaMessageRecords(getPddaMessageHelper(), messages, userDisplayName);
            PddaMessageUtil.updateCppStagingInboundRecords(getCppStagingInboundHelper(), cppMessages, userDisplayName);
        } else {
            LOG.debug("SFTP Error: No records have been updated");
        }
    }

    public Map<String, InputStream> respondToPddaMessage(List<XhbPddaMessageDao> messages) throws IOException {
        Map<String, InputStream> files = new ConcurrentHashMap<>();
        LOG.debug("respondToPddaMessage({})", messages);
        for (XhbPddaMessageDao message : messages) {
            // Set Filename
            String fileName = (message.getCpDocumentName().replaceAll(CP_FILE_EXTENSION, "")) + "_Response_"
                + cpResponseFileDateFormat.format(getNow()) + CP_FILE_EXTENSION;

            // Set File contents
            String msg;

            if (INVALID.equals(message.getCpDocumentStatus())) {
                msg = "Invalid document filename";
            } else {
                msg = "Valid Document Filename";
            }
            try (InputStream msgContents = FormattingServiceUtils.getByteArrayInputStream(msg)) {

                // Add the file to the Map
                files.put(fileName, msgContents);
            }
        }
        return files;
    }

    public Map<String, InputStream> respondToCppStagingInbound(List<XhbCppStagingInboundDao> cppMessages)
        throws IOException {
        LOG.debug("respondToCppStagingInbound({})", cppMessages);
        Map<String, InputStream> files = new ConcurrentHashMap<>();

        for (XhbCppStagingInboundDao cppMessage : cppMessages) {
            // Set Filename
            String fileName = (cppMessage.getDocumentName().replaceAll(CP_FILE_EXTENSION, "")) + "_Response_"
                + cpResponseFileDateFormat.format(getNow()) + CP_FILE_EXTENSION;

            // Set File contents
            String msg;

            // Add Text to file to signify failed message
            if (VALIDATION_FAIL.equals(cppMessage.getValidationStatus())) {
                msg = "Schema validation failed for document";
            } else {
                msg = "Schema validation Successful";
            }
            try (InputStream msgContents = FormattingServiceUtils.getByteArrayInputStream(msg)) {
                // Add the file to the Map
                files.put(fileName, msgContents);
            }
        }
        return files;
    }

    public boolean sendMessageRepsonses(Map<String, InputStream> responses) {
        // Sending responses off to bais
        SftpConfig sftpConfig = getSftpConfigs();
        if (!responses.isEmpty()) {
            try {
                getSftpHelper().sftpFiles(sftpConfig.session, sftpConfig.remoteFolder, responses);
                return true;
            } catch (Exception ex) {
                LOG.error(SFTP_ERROR + ex);
            }
        }
        return false;
    }

    private Date getNow() {
        return new Date();
    }

    public static class BaisXhibitValidation extends BaisValidation {

        private static final String PDDA = "PDDA";

        public BaisXhibitValidation(XhbCourtRepository courtRepository) {
            super(courtRepository, false, Integer.valueOf(4));
        }

        @Override
        public String validateFilename(String filename, PublicDisplayEvent event) {
            String debugErrorPrefix = filename + " error: ";
            String errorMessage = super.validateFilename(filename);
            if (errorMessage != null) {
                LOG.debug(debugErrorPrefix + errorMessage);
                return errorMessage;
            }

            // Check the file has the right overall format of 4 parts
            if (isValidNoOfParts(filename)) {
                LOG.debug("Valid filename - No. Of Parts");
            } else {
                errorMessage = "Invalid filename - No. Of Parts";
                LOG.debug(debugErrorPrefix + errorMessage);
                return errorMessage;
            }
            // Check Title is right format
            if (PDDA.equalsIgnoreCase(getFilenamePart(filename, 0))) {
                LOG.debug("Valid filename - Title");
            } else {
                errorMessage = "Invalid filename - Title";
                LOG.debug(debugErrorPrefix + errorMessage);
                return errorMessage;
            }

            // Check we have the event from the file contents
            if (event == null) {
                errorMessage = "Invalid filename - Invalid event in filecontents";
                LOG.debug(debugErrorPrefix + errorMessage);
                return errorMessage;

                // Check the crest court Id
            } else if (getCourtId(filename, event) == null) {
                errorMessage = "Invalid filename - CrestCourtId";
                LOG.debug(debugErrorPrefix + errorMessage);
                return errorMessage;
            }


            return null;
        }

        @Override
        public Optional<XhbPddaMessageDao> getPddaMessageDao(PddaMessageHelper pddaMessageHelper, String filename) {
            return Optional.empty();
        }

        @Override
        public boolean validateTitle(String filename) {
            return PddaSftpValidationUtil.validateTitle(getFilenamePart(filename, 0), new String[] {PDDA});
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
                return PddaHelper.deserializePublicEvent(fileContents);
            }
            return null;
        }
    }

    public static class BaisCpValidation extends BaisValidation {

        private static final String[] POSSIBLETITLES =
            {"DailyList", "FirmList", "WarnedList", "WebPage", "PublicDisplay"};

        public BaisCpValidation(XhbCourtRepository courtRespository) {
            super(courtRespository, false, Integer.valueOf(3));
        }

        @Override
        public String validateFilename(String filename, PublicDisplayEvent event) {
            String debugErrorPrefix = filename + " error: ";
            String errorMessage = super.validateFilename(filename);
            if (errorMessage != null) {
                LOG.debug(debugErrorPrefix + errorMessage);
                return errorMessage;
            }

            // First check file extension is an xml file
            if (!PddaSftpValidationUtil.validateExtension(filename)) {
                errorMessage = "Invalid filename - Extension";
                LOG.debug(debugErrorPrefix + errorMessage);
                return errorMessage;
            }

            // Check the file has the right overall format of 3 parts
            if (!isValidNoOfParts(filename)) {
                errorMessage = "Invalid filename - No. Of Parts";
                LOG.debug(debugErrorPrefix + errorMessage);
                return errorMessage;
            }

            // Check Title is right format
            if (!validateTitle(filename)) {
                errorMessage = "Invalid filename - Title";
                LOG.debug(debugErrorPrefix + errorMessage);
                return errorMessage;
            }

            // Check CrestCourtID is valid and exists in the database
            if (getCourtId(filename, event) == null) {
                errorMessage = "Invalid filename - CrestCourtId";
                LOG.debug(debugErrorPrefix + errorMessage);
                return errorMessage;
            }

            // Check dateTime is valid format
            if (!validateDateTime(getFilenamePart(filename, 2))) {
                errorMessage = "Invalid filename - DateTime";
                LOG.debug(debugErrorPrefix + errorMessage);
                return errorMessage;
            }
            return null;
        }

        @Override
        public boolean validateTitle(String filename) {
            return PddaSftpValidationUtil.validateTitle(getFilenamePart(filename, 0), POSSIBLETITLES);
        }

        @Override
        public Optional<XhbPddaMessageDao> getPddaMessageDao(PddaMessageHelper pddaMessageHelper, String filename) {
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
                List<XhbCourtDao> courtDao = xhbCourtRepository.findByCrestCourtIdValue(crestCourtId);
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
