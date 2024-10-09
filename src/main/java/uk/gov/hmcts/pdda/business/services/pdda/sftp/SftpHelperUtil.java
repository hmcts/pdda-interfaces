package uk.gov.hmcts.pdda.business.services.pdda.sftp;

import jakarta.persistence.EntityManager;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundDao;
import uk.gov.hmcts.pdda.business.entities.xhbpddamessage.XhbPddaMessageDao;
import uk.gov.hmcts.pdda.business.services.formatting.FormattingServiceUtils;
import uk.gov.hmcts.pdda.business.services.pdda.PddaMessageUtil;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SftpHelperUtil extends SftpHelper {

    private static final Logger LOG = LoggerFactory.getLogger(SftpHelperUtil.class);

    private static final String TWO_PARAMS = "{}{}";
    private static final String CP_FILE_EXTENSION = ".xml";

    private static final String INVALID = "INV";
    private static final String VALIDATION_FAIL = "VF";

    private static final String USE_KEY_VAULT_PROPERTIES = "USE_KEY_VAULT_PROPERTIES";

    private final DateFormat cpResponseFileDateFormat =
        new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault());

    // Constructor
    public SftpHelperUtil(EntityManager entityManager) {
        super(entityManager);
    }


    /**
     * Populate the SFTP configuration.
     * 
     * @return The SFTP configuration
     */
    protected SftpConfig populateSftpConfig() {

        methodName = "populateSftpConfig()";
        LOG.debug(methodName, LOG_CALLED);

        SftpConfig sftpConfig = new SftpConfig();

        // Firstly, are we using Database or Key Vault to lookup credentials?
        // This will be a database lookup. If it is indeterminate, we will use the Database.
        sftpConfig.setUseKeyVault(checkWhetherToUseKeyVault());

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

    //// Test helper methods ////
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

        String fileName;
        String msg;
        for (XhbPddaMessageDao message : messages) {
            // Set Filename
            fileName = message.getCpDocumentName().replaceAll(CP_FILE_EXTENSION, "")
                + "_Response_" + cpResponseFileDateFormat.format(getNow()) + CP_FILE_EXTENSION;

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
        SftpConfig sftpConfig = populateSftpConfig();

        if (!responses.isEmpty()) {
            try {
                getSftpHelper().sftpFiles(sftpConfig.getSession(),
                    sftpConfig.getActiveRemoteFolder(), responses);
                return true;
            } catch (Exception ex) {
                LOG.error(SFTP_ERROR + " {} ", ExceptionUtils.getStackTrace(ex));
                LOG.error("Stacktrace3:: {}", ExceptionUtils.getStackTrace(ex));
            }
        }
        return false;
    }

    protected Date getNow() {
        return new Date();
    }



}
