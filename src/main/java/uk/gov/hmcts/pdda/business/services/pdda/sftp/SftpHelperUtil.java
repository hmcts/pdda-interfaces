package uk.gov.hmcts.pdda.business.services.pdda.sftp;

import jakarta.persistence.EntityManager;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.services.pdda.PddaMessageHelper;

import java.io.IOException;

public class SftpHelperUtil extends SftpService {

    private static final Logger LOG = LoggerFactory.getLogger(SftpHelperUtil.class);

    private static final String TWO_PARAMS = "{}{}";

    private static final String USE_KEY_VAULT_PROPERTIES = "USE_KEY_VAULT_PROPERTIES";
    private static final String TEST_SFTP_DIRECTORY = "/directory/";
    private static final String NOT_FOUND = " not found";
    private static final String SFTP_LOG_STRING = "SFTP Host and port: {}";


    public SftpHelperUtil(EntityManager entityManager,
        XhbConfigPropRepository xhbConfigPropRepository, Environment environment,
        PddaMessageHelper pddaMessageHelper, XhbClobRepository clobRepository,
        XhbCourtRepository courtRepository) {
        super(entityManager, xhbConfigPropRepository, environment, pddaMessageHelper,
            clobRepository, courtRepository);
    }

    public SftpHelperUtil(EntityManager entityManager) {
        super(entityManager);
    }


    /**
     * Populate the SFTP configuration.

     * @return The SFTP configuration
     */
    protected SftpConfig populateSftpConfig(int sftpPort) {

        methodName = "populateSftpConfig()";
        LOG.debug(methodName, LOG_CALLED);

        SftpConfig sftpConfig = new SftpConfig();

        if (sftpPort > 0) { // Testing
            sftpConfig.setPort(sftpPort);
            sftpConfig = getTestSftpConfig(sftpConfig);
        } else {
            // Are we using Database or Key Vault to lookup credentials?
            // This will be a database lookup. If it is indeterminate, we will use the Database.
            sftpConfig.setUseKeyVault(checkWhetherToUseKeyVault());
            
            // Get excluded court IDs for CP messages
            try {
                sftpConfig
                    .setCpExcludedCourtIds(getMandatoryConfigValue(Config.DB_CP_EXCLUDED_COURT_IDS));
                LOG.debug("SFTP CP Excluded Court Ids: {}", sftpConfig.getCpExcludedCourtIds());
            } catch (InvalidConfigException ex) {
                sftpConfig.setErrorMsg(Config.DB_CP_EXCLUDED_COURT_IDS + NOT_FOUND);
            }

            // Set the rest of the params
            sftpConfig = getConfigParams(sftpConfig);
        }

        return sftpConfig;
    }


    /**
     * Get and set the data for testing.

     * @param sftpConfig The SFTP Config
     * @return Updated SFTP Config
     */
    @SuppressWarnings({"PMD.LawOfDemeter", "PMD.CloseResource"})
    private SftpConfig getTestSftpConfig(SftpConfig sftpConfig) {
        sftpConfig.setHost("localhost");
        sftpConfig.setCpUsername("cpUsername");
        sftpConfig.setCpPassword("cpPassword");
        sftpConfig.setCpRemoteFolder(TEST_SFTP_DIRECTORY);
        sftpConfig.setXhibitUsername("xhibitUsername");
        sftpConfig.setXhibitPassword("xhibitPassword");
        sftpConfig.setXhibitRemoteFolder(TEST_SFTP_DIRECTORY);
        sftpConfig.setActiveRemoteFolder(TEST_SFTP_DIRECTORY);

        try {
            SSHClient ssh = new SftpConfigHelper(entityManager).getNewSshClient();
            ssh.connect(sftpConfig.getHost(), sftpConfig.getPort());
            ssh.authPassword(sftpConfig.getCpUsername(), sftpConfig.getCpPassword());
            sftpConfig.setSshClient(ssh);

            SFTPClient sftpClient = new SFTPClient(ssh);
            sftpConfig.setSshjSftpClient(sftpClient);

        } catch (IOException e) {
            LOG.error("Error setting up SFTP config", e);
        }

        return sftpConfig;
    }

    /**
     * Check whether to use Key Vault.

     * @return True if using Key Vault, false otherwise
     */
    private boolean checkWhetherToUseKeyVault() {

        methodName = "checkWhetherToUseKeyVault()";
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
     * Set the SFTP config params.

     * @param sftpConfig The SFTP configuration
     * @return The SFTP configuration
     */
    public SftpConfig getConfigParams(SftpConfig sftpConfig) {

        // Now get the rest of the details not already set
        if (sftpConfig.isUseKeyVault()) {
            return getKvConfigParams(sftpConfig);
        } else {
            return getDbConfigParams(sftpConfig);
        }
    }


    /**
     * Set the SFTP config params from the DB.

     * @return The SFTP configuration
     */
    private SftpConfig getDbConfigParams(SftpConfig sftpConfig) {

        methodName = "getDbConfigParams()";
        LOG.debug(TWO_PARAMS, methodName, LOG_CALLED);

        // Firstly CP BAIS properties
        try {
            sftpConfig.setCpUsername(getMandatoryConfigValue(Config.DB_CP_SFTP_USERNAME));
            LOG.debug("SFTP Cp Username: {}", sftpConfig.getCpUsername());
        } catch (InvalidConfigException ex) {
            sftpConfig.setErrorMsg(Config.DB_CP_SFTP_USERNAME + NOT_FOUND);
        }
        try {
            sftpConfig.setCpPassword(getMandatoryConfigValue(Config.DB_CP_SFTP_PASSWORD));
        } catch (Exception ex) {
            sftpConfig.setErrorMsg(Config.DB_CP_SFTP_PASSWORD + NOT_FOUND);
        }
        try {
            sftpConfig
                .setCpRemoteFolder(getMandatoryConfigValue(Config.DB_CP_SFTP_UPLOAD_LOCATION));
            LOG.debug("SFTP Cp Remote Folder: {}", sftpConfig.getCpRemoteFolder());
        } catch (InvalidConfigException ex) {
            sftpConfig.setErrorMsg(Config.DB_CP_SFTP_UPLOAD_LOCATION + NOT_FOUND);
        }


        // Next the XHIBIT BAIS properties
        try {
            sftpConfig.setXhibitUsername(getMandatoryConfigValue(Config.DB_SFTP_USERNAME));
            LOG.debug("SFTP XHIBIT Username: {}", sftpConfig.getCpUsername());
        } catch (InvalidConfigException ex) {
            sftpConfig.setErrorMsg(Config.DB_SFTP_USERNAME + NOT_FOUND);
        }
        try {
            sftpConfig.setXhibitPassword(getMandatoryConfigValue(Config.DB_SFTP_PASSWORD));
        } catch (InvalidConfigException ex) {
            sftpConfig.setErrorMsg(Config.DB_SFTP_PASSWORD + NOT_FOUND);
        }
        try {
            sftpConfig
                .setXhibitRemoteFolder(getMandatoryConfigValue(Config.DB_SFTP_UPLOAD_LOCATION));
            LOG.debug("SFTP XHIBIT Remote Folder: {}", sftpConfig.getXhibitRemoteFolder());
        } catch (InvalidConfigException ex) {
            sftpConfig.setErrorMsg(Config.DB_SFTP_UPLOAD_LOCATION + NOT_FOUND);
        }

        // Now get the host and port
        String hostAndPort = "";
        try {
            hostAndPort = getMandatoryConfigValue(Config.DB_SFTP_HOST);
            LOG.debug(SFTP_LOG_STRING, hostAndPort);
        } catch (InvalidConfigException ex) {
            sftpConfig.setErrorMsg(Config.DB_SFTP_HOST + NOT_FOUND);
        }

        // Validate the host and port
        return validateAndSetHostAndPort(sftpConfig, hostAndPort);
    }

    /**
     * Validate and set the host and port.

     * @param sftpConfig The SFTP configuration
     * @param hostAndPort The host and port
     * @return The SFTP configuration
     */
    public SftpConfig validateAndSetHostAndPort(SftpConfig sftpConfig, String hostAndPort) {

        LOG.debug("Validating host and port");
        String portDelimiter = ":";
        Integer pos = hostAndPort.indexOf(portDelimiter);
        if (pos <= 0) {
            sftpConfig.setErrorMsg("Host and port syntax is <Host>" + portDelimiter + "<Port>");
        }
        sftpConfig.setHost(hostAndPort.substring(0, pos));
        try {
            String strPort = hostAndPort.substring(pos + 1, hostAndPort.length());
            sftpConfig.setPort(Integer.valueOf(strPort));
        } catch (Exception ex) {
            sftpConfig.setErrorMsg("Host and port contains invalid port number");
            LOG.error("Stacktrace2:: {}", ExceptionUtils.getStackTrace(ex));
        }

        return sftpConfig;
    }


    /**
     * Set the SFTP config params from the Key Vault.

     * @return The SFTP configuration
     */
    @SuppressWarnings("PMD.UnusedAssignment")
    private SftpConfig getKvConfigParams(SftpConfig sftpConfig) {

        methodName = "getKvConfigParams()";
        LOG.debug(TWO_PARAMS, methodName, LOG_CALLED);

        // Firstly CP BAIS properties
        String currentProperty = "";
        String hostAndPort = "";
        try {
            currentProperty = Config.KV_CP_SFTP_USERNAME;
            sftpConfig.setCpUsername(getMandatoryEnvValue(currentProperty));
            LOG.debug("SFTP Cp Username: {}", sftpConfig.getCpUsername());

            currentProperty = Config.KV_CP_SFTP_PASSWORD;
            sftpConfig.setCpPassword(getMandatoryEnvValue(currentProperty));

            currentProperty = Config.KV_CP_SFTP_UPLOAD_LOCATION;
            sftpConfig.setCpRemoteFolder(getMandatoryEnvValue(currentProperty));
            LOG.debug("SFTP Cp Remote Folder: {}", sftpConfig.getCpRemoteFolder());

            // Next the XHIBIT BAIS properties
            currentProperty = Config.KV_SFTP_USERNAME;
            sftpConfig.setXhibitUsername(getMandatoryEnvValue(currentProperty));
            LOG.debug("SFTP XHIBIT Username: {}", sftpConfig.getCpUsername());

            currentProperty = Config.KV_SFTP_PASSWORD;
            sftpConfig.setXhibitPassword(getMandatoryEnvValue(currentProperty));

            currentProperty = Config.KV_SFTP_UPLOAD_LOCATION;
            sftpConfig.setXhibitRemoteFolder(getMandatoryEnvValue(currentProperty));
            LOG.debug("SFTP XHIBIT Remote Folder: {}", sftpConfig.getXhibitRemoteFolder());

            // Now get the host and port
            currentProperty = Config.KV_SFTP_HOST;
            hostAndPort = getMandatoryEnvValue(currentProperty);
            LOG.debug(SFTP_LOG_STRING, hostAndPort);
        } catch (InvalidConfigException ex) {
            sftpConfig.setErrorMsg(currentProperty + NOT_FOUND);
        }

        // Validate the host and port
        return validateAndSetHostAndPort(sftpConfig, hostAndPort);
    }
}
