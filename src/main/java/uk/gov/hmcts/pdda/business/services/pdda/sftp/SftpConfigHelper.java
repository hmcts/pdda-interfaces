package uk.gov.hmcts.pdda.business.services.pdda.sftp;

import jakarta.persistence.EntityManager;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.services.pdda.PddaMessageHelper;
import uk.gov.hmcts.pdda.business.services.pdda.PddaSftpHelper;


/**
 * SFTP Configuration Helper class to handle getting the SFTP configuration.
 */
public class SftpConfigHelper extends SftpHelper {

    private static final Logger LOG = LoggerFactory.getLogger(SftpConfigHelper.class);

    protected static final String LOG_CALLED = " called";
    private static final String NOT_FOUND = " not found";
    private static final String TWO_PARAMS = "{}{}";

    private static final String SFTP_ERROR = "SFTP Error:";

    // private Environment environment;

    public SftpConfigHelper(EntityManager entityManager) {
        super(entityManager);
    }

    /**
     * JUnit constructor.
     * 
     * @param entityManager The EntityManager
     * @param xhbConfigPropRepository The XhbConfigPropRepository
     * @param environment The Environment
     * @param sftpHelper PddaSftpHelper
     */
    public SftpConfigHelper(EntityManager entityManager,
        XhbConfigPropRepository xhbConfigPropRepository, Environment environment,
        PddaSftpHelper sftpHelper, PddaMessageHelper pddaMessageHelper,
        XhbClobRepository clobRepository, XhbCourtRepository courtRepository) {
        super(entityManager, xhbConfigPropRepository, environment, sftpHelper, pddaMessageHelper,
            clobRepository, courtRepository);
    }

    /**
     * JUnit constructor.
     * 
     * @param entityManager The EntityManager
     * @param environment The Environment
     */
    @SuppressWarnings("PMD.UnusedFormalParameter")
    public SftpConfigHelper(EntityManager entityManager, Environment environment) {
        super(entityManager);
        this.entityManager = entityManager;
        // this.environment = environment;
    }


    /**
     * Set the SFTP config params.
     * 
     * @param sftpConfig The SFTP configuration
     * @return The SFTP configuration
     */
    public SftpConfig getConfigParams(SftpConfig sftpConfig) {

        if (sftpConfig.isUseKeyVault()) {
            return getKvConfigParams(sftpConfig);
        } else {
            return getDbConfigParams(sftpConfig);
        }
    }


    /**
     * Set the SFTP config params from the DB.
     * 
     * @return The SFTP configuration
     */
    private SftpConfig getDbConfigParams(SftpConfig sftpConfig) {
        
        methodName = "setDBConfigParams()";
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
        } catch (InvalidConfigException ex) {
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
            LOG.debug("SFTP Host and port: {}", hostAndPort);
        } catch (InvalidConfigException ex) {
            sftpConfig.setErrorMsg(Config.DB_SFTP_HOST + NOT_FOUND);
        }

        // Validate the host and port
        return validateAndSetHostAndPort(sftpConfig, hostAndPort);
    }


    /**
     * Set the SFTP config params from the Key Vault.
     * 
     * @return The SFTP configuration
     */
    private SftpConfig getKvConfigParams(SftpConfig sftpConfig) {

        methodName = "setKVConfigParams()";
        LOG.debug(TWO_PARAMS, methodName, LOG_CALLED);

        // Firstly CP BAIS properties
        try {
            sftpConfig.setCpUsername(getMandatoryEnvValue(Config.KV_CP_SFTP_USERNAME));
            LOG.debug("SFTP Cp Username: {}", sftpConfig.getCpUsername());
        } catch (InvalidConfigException ex) {
            sftpConfig.setErrorMsg(Config.KV_CP_SFTP_USERNAME + NOT_FOUND);
        }
        try {
            sftpConfig.setCpPassword(getMandatoryEnvValue(Config.KV_CP_SFTP_PASSWORD));
        } catch (InvalidConfigException ex) {
            sftpConfig.setErrorMsg(Config.KV_CP_SFTP_PASSWORD + NOT_FOUND);
        }
        try {
            sftpConfig.setCpRemoteFolder(getMandatoryEnvValue(Config.KV_CP_SFTP_UPLOAD_LOCATION));
            LOG.debug("SFTP Cp Remote Folder: {}", sftpConfig.getCpRemoteFolder());
        } catch (InvalidConfigException ex) {
            sftpConfig.setErrorMsg(Config.KV_CP_SFTP_UPLOAD_LOCATION + NOT_FOUND);
        }

        // Next the XHIBIT BAIS properties
        try {
            sftpConfig.setCpUsername(getMandatoryEnvValue(Config.KV_SFTP_USERNAME));
            LOG.debug("SFTP XHIBIT Username: {}", sftpConfig.getCpUsername());
        } catch (InvalidConfigException ex) {
            sftpConfig.setErrorMsg(Config.KV_SFTP_USERNAME + NOT_FOUND);
        }
        try {
            sftpConfig.setCpPassword(getMandatoryEnvValue(Config.KV_SFTP_PASSWORD));
        } catch (InvalidConfigException ex) {
            sftpConfig.setErrorMsg(Config.KV_SFTP_PASSWORD + NOT_FOUND);
        }
        try {
            sftpConfig.setXhibitRemoteFolder(getMandatoryEnvValue(Config.KV_SFTP_UPLOAD_LOCATION));
            LOG.debug("SFTP XHIBIT Remote Folder: {}", sftpConfig.getXhibitRemoteFolder());
        } catch (InvalidConfigException ex) {
            sftpConfig.setErrorMsg(Config.KV_SFTP_UPLOAD_LOCATION + NOT_FOUND);
        }

        // Now get the host and port
        String hostAndPort = "";
        try {
            hostAndPort = getMandatoryEnvValue(Config.KV_SFTP_HOST);
            LOG.debug("SFTP Host and port: {}", hostAndPort);
        } catch (InvalidConfigException ex) {
            sftpConfig.setErrorMsg(Config.KV_SFTP_HOST + NOT_FOUND);
        }

        // Validate the host and port
        return validateAndSetHostAndPort(sftpConfig, hostAndPort);
    }


    /**
     * Validate and set the host and port.
     * 
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
     * Get a new SSHClient.
     * 
     * @return The SSHClient
     */
    public SSHClient getNewSshClient() {
        SSHClient ssh = new SSHClient();
        ssh.addHostKeyVerifier(new PromiscuousVerifier());
        return ssh;
    }


    public SftpConfig getJschSession(SftpConfig sftpConfig) {
        // Create a session
        try {
            LOG.debug("Connection validated successfully");
            sftpConfig.setSession(getSftpHelper().createSession(sftpConfig.getXhibitUsername(),
                sftpConfig.getXhibitPassword(), sftpConfig.getHost(), sftpConfig.getPort()));
            LOG.debug("A session has been established");
        } catch (Exception ex) {
            sftpConfig.setErrorMsg(SFTP_ERROR + ex.getMessage());
            LOG.error("Stacktrace2:: {}", ExceptionUtils.getStackTrace(ex));
        }

        return sftpConfig;
    }

}
