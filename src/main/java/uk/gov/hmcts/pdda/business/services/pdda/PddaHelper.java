package uk.gov.hmcts.pdda.business.services.pdda;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import jakarta.ejb.EJBException;
import jakarta.persistence.EntityManager;
import javassist.NotFoundException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicDisplayEvent;
import uk.gov.hmcts.framework.services.CsServices;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobDao;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundDao;
import uk.gov.hmcts.pdda.business.entities.xhbpddamessage.XhbPddaMessageDao;
import uk.gov.hmcts.pdda.business.entities.xhbrefpddamessagetype.XhbRefPddaMessageTypeDao;
import uk.gov.hmcts.pdda.business.services.formatting.FormattingServiceUtils;
import uk.gov.hmcts.pdda.business.services.pdda.sftp.SftpConfig;
import uk.gov.hmcts.pdda.business.services.pdda.sftp.SftpHelperUtil;
import uk.gov.hmcts.pdda.business.services.pdda.sftp.SftpService.BaisCpValidation;
import uk.gov.hmcts.pdda.business.services.pdda.sftp.SftpService.BaisXhibitValidation;
import uk.gov.hmcts.pdda.common.publicdisplay.jms.PublicDisplayNotifier;
import uk.gov.hmcts.pdda.web.publicdisplay.initialization.servlet.InitializationService;

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
@SuppressWarnings({"PMD.GodClass", "PMD.TooManyMethods", "PMD.ExcessiveImports",
    "PMD.CouplingBetweenObjects"})
public class PddaHelper extends XhibitPddaHelper {
    private static final Logger LOG = LoggerFactory.getLogger(PddaHelper.class);

    private final DateFormat cpResponseFileDateFormat =
        new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault());
    private static final String NO = "N";
    private static final String INVALID_MESSAGE_TYPE = "Invalid";
    private static final String CP_FILE_EXTENSION = ".xml";
    private static final String INVALID = "INV";
    private static final String VALIDATION_FAIL = "VF";

    private static final String SFTP_ERROR = "SFTP Error:";
    private static final String NOT_FOUND = " not found";
    private static final String SFTP_LOG_STRING = "SFTP Host and port: {}";

    public PddaHelper(EntityManager entityManager) {
        super(entityManager, InitializationService.getInstance().getEnvironment());
    }

    // Junit constructor
    public PddaHelper(EntityManager entityManager, XhbConfigPropRepository xhbConfigPropRepository,
        Environment environment, PddaSftpHelper sftpHelper,
        PddaMessageHelper pddaMessageHelper, XhbClobRepository xhbClobRepository,
        XhbCourtRepository xhbCourtRepository) {
        super(entityManager, xhbConfigPropRepository, environment, sftpHelper, pddaMessageHelper,
            xhbClobRepository, xhbCourtRepository);
    }

    /**
     * Retrieve events from Bais (processed by CP).
     */
    public void retrieveFromBaisCp() {
        methodName = "retrieveFromBaisCp()";
        LOG.debug(methodName, LOG_CALLED);

        SftpConfig config = getBaisCpConfigs();
        if (config.getErrorMsg() != null) {
            LOG.debug(config.getErrorMsg());
            return;
        }

        retrieveFromBais(config, new BaisCpValidation(getCourtRepository()));
    }

    /**
     * Retrieve events from Bais (processed by Xhibit).
     */
    public void retrieveFromBaisXhibit() {
        methodName = "retrieveFromBaisXhibit()";
        LOG.debug(methodName, LOG_CALLED);

        SftpConfig config = getSftpConfigs();
        if (config.getErrorMsg() != null) {
            LOG.error(config.getErrorMsg());
            return;
        }

        retrieveFromBais(config, new BaisXhibitValidation(getCourtRepository()));
    }

    private void retrieveFromBais(SftpConfig config, BaisValidation validation) {
        // Get the file list and then disconnect the session
        try {
            LOG.debug("retrieveFromBais({},{})", config, validation);
            Map<String, String> files = getBaisFileList(config, validation);
            LOG.debug("Total of {}{}", files.size(),
                " files, before processing, in this transaction.");
            if (!files.isEmpty()) {
                // Process the files we have retrieved.
                for (Map.Entry<String, String> entry : files.entrySet()) {
                    String filename = entry.getKey();
                    String clobData = entry.getValue();
                    processBaisFile(config, validation, filename, clobData);
                }
            }
        } finally {
            if (config.getSession() != null) {
                // Check the contents of Bais to verify all processed files are deleted
                LOG.debug("Checking contents of Bais prior to closing the connection");
                Map<String, String> files = getBaisFileList(config, validation);
                if (files.isEmpty()) {
                    LOG.debug("Contents of Bais is now empty");
                }
                PddaSftpUtil.disconnectSession(config.getSession());
                config.setSession(null);
            }
        }
    }

    private void processBaisFile(SftpConfig config, BaisValidation validation, String filename,
        String clobData) {
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

            // If this is a PublicDisplayEvent from XHIBIT...
            if (event != null) {
                // Send the event to the EventStore for processing
                PublicDisplayNotifier publicDisplayNotifier = new PublicDisplayNotifier();
                publicDisplayNotifier.sendMessage(event);
            }

            getPddaSftpHelper().sftpDeleteFile(config.getSession(), config.getActiveRemoteFolder(),
                filename);
            LOG.debug("Removed file from bais after processing: {}", filename);
        } catch (JSchException | SftpException | NotFoundException ex) {
            CsServices.getDefaultErrorHandler().handleError(ex, getClass());
            throw new EJBException(ex);
        }
    }

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

    private Map<String, String> getBaisFileList(SftpConfig config, BaisValidation validation) {
        try {
            return getPddaSftpHelper().sftpFetch(config.getSession(),
                config.getActiveRemoteFolder(),
                validation);
        } catch (Exception ex) {
            LOG.error(SFTP_ERROR + " {} ", ExceptionUtils.getStackTrace(ex));
            return Collections.emptyMap();
        }
    }

    private SftpConfig getSftpConfigs() {
        return getSftpConfigs(Config.DB_CP_SFTP_USERNAME, Config.DB_CP_SFTP_PASSWORD,
            Config.DB_CP_SFTP_UPLOAD_LOCATION, Config.DB_SFTP_USERNAME, Config.DB_SFTP_PASSWORD,
            Config.DB_SFTP_UPLOAD_LOCATION);
    }

    @SuppressWarnings("PMD.InefficientStringBuffering")
    private SftpConfig getSftpConfigs(String cpConfigUsername, String cpConfigPassword,
        String cpConfigLocation, String xhibitConfigUsername, String xhibitConfigPassword,
        String xhibitConfigLocation) {
        methodName = "getSftpConfigs()";
        LOG.debug(methodName, LOG_CALLED);
        SftpConfig sftpConfig = new SftpConfig();
        StringBuilder errorMessage = new StringBuilder();

        // Fetch and validate the properties
        try {
            sftpConfig.setCpUsername(getMandatoryEnvValue(cpConfigUsername));
            LOG.debug("SFTP Username: {}", sftpConfig.getCpUsername());
        } catch (InvalidConfigException ex) {
            errorMessage.append(cpConfigUsername + NOT_FOUND);
        }
        try {
            sftpConfig.setCpPassword(getMandatoryEnvValue(cpConfigPassword));
        } catch (InvalidConfigException ex) {
            errorMessage.append("\n" + cpConfigPassword + NOT_FOUND);
        }
        try {
            sftpConfig.setCpRemoteFolder(getMandatoryConfigValue(cpConfigLocation));
            LOG.debug("SFTP Remote Folder: {}", sftpConfig.getCpRemoteFolder());
        } catch (InvalidConfigException ex) {
            errorMessage.append("\n" + cpConfigLocation + NOT_FOUND);
        }
        try {
            sftpConfig.setXhibitUsername(getMandatoryEnvValue(xhibitConfigUsername));
            LOG.debug("SFTP Username: {}", sftpConfig.getXhibitUsername());
        } catch (InvalidConfigException ex) {
            errorMessage.append("\n" + xhibitConfigUsername + NOT_FOUND);
        }
        try {
            sftpConfig.setXhibitPassword(getMandatoryEnvValue(xhibitConfigPassword));
        } catch (InvalidConfigException ex) {
            errorMessage.append("\n" + xhibitConfigPassword + NOT_FOUND);
        }
        try {
            sftpConfig.setXhibitRemoteFolder(getMandatoryConfigValue(xhibitConfigLocation));
            LOG.debug("SFTP Remote Folder: {}", sftpConfig.getXhibitRemoteFolder());
        } catch (InvalidConfigException ex) {
            errorMessage.append("\n" + xhibitConfigLocation + NOT_FOUND);
        }
        String hostAndPort;
        try {
            hostAndPort = getMandatoryEnvValue(Config.DB_SFTP_HOST);
            LOG.debug(SFTP_LOG_STRING, hostAndPort);
        } catch (InvalidConfigException ex) {
            sftpConfig.setErrorMsg(errorMessage + Config.DB_SFTP_HOST + NOT_FOUND);
            return sftpConfig;
        }

        LOG.debug("Validating host and port");
        sftpConfig =
            new SftpHelperUtil(entityManager).validateAndSetHostAndPort(sftpConfig, hostAndPort);
        sftpConfig = getSftpConfigHelper().getJschSession(sftpConfig);

        LOG.debug("Connected successfully");
        return sftpConfig;
    }


    private SftpConfig getBaisCpConfigs() {
        methodName = "getBaisCpConfigs()";
        LOG.debug(methodName, LOG_CALLED);
        return getSftpConfigs(Config.DB_CP_SFTP_USERNAME, Config.DB_CP_SFTP_PASSWORD,
            Config.DB_CP_SFTP_UPLOAD_LOCATION, Config.DB_SFTP_USERNAME, Config.DB_SFTP_PASSWORD,
            Config.DB_SFTP_UPLOAD_LOCATION);
    }


    public void checkForCpMessages(String userDisplayName) throws IOException {
        // Find Messages
        LOG.debug("checkForCpMessages({})", userDisplayName);
        List<XhbPddaMessageDao> messages = getPddaMessageHelper().findUnrespondedCpMessages();
        List<XhbCppStagingInboundDao> cppMessages =
            getCppStagingInboundHelper().findUnrespondedCppMessages();

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
            PddaMessageUtil.updatePddaMessageRecords(getPddaMessageHelper(), messages,
                userDisplayName);
            PddaMessageUtil.updateCppStagingInboundRecords(getCppStagingInboundHelper(),
                cppMessages, userDisplayName);
        } else {
            LOG.debug("SFTP Error: No records have been updated");
        }
    }

    public Map<String, InputStream> respondToPddaMessage(List<XhbPddaMessageDao> messages)
        throws IOException {
        Map<String, InputStream> files = new ConcurrentHashMap<>();
        LOG.debug("respondToPddaMessage({})", messages);
        for (XhbPddaMessageDao message : messages) {
            // Set Filename
            String fileName = message.getCpDocumentName().replaceAll(CP_FILE_EXTENSION, "")
                + "_Response_" + cpResponseFileDateFormat.format(getNow()) + CP_FILE_EXTENSION;

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

    public Map<String, InputStream> respondToCppStagingInbound(
        List<XhbCppStagingInboundDao> cppMessages) throws IOException {
        LOG.debug("respondToCppStagingInbound({})", cppMessages);
        Map<String, InputStream> files = new ConcurrentHashMap<>();

        for (XhbCppStagingInboundDao cppMessage : cppMessages) {
            // Set Filename
            String fileName = cppMessage.getDocumentName().replaceAll(CP_FILE_EXTENSION, "")
                + "_Response_" + cpResponseFileDateFormat.format(getNow()) + CP_FILE_EXTENSION;

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
        LOG.debug("sendMessageRepsonses({})", responses);
        // Sending responses off to bais
        SftpConfig sftpConfig = getSftpConfigs();
        if (!responses.isEmpty()) {
            try {
                getPddaSftpHelper().sftpFiles(sftpConfig.getSession(),
                    sftpConfig.getActiveRemoteFolder(), responses);
                return true;
            } catch (Exception ex) {
                LOG.error(SFTP_ERROR + " {} ", ExceptionUtils.getStackTrace(ex));
                LOG.error("Stacktrace3:: {}", ExceptionUtils.getStackTrace(ex));
            }
        }
        return false;
    }

    private Date getNow() {
        return new Date();
    }

}
