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


/**
 * SFTP Configuration Helper class to handle getting the SFTP configuration.
 */
public class SftpConfigHelper extends SftpService {

    private static final Logger LOG = LoggerFactory.getLogger(SftpConfigHelper.class);

    protected static final String LOG_CALLED = " called";
    private static final String NOT_FOUND = " not found";
    private static final String TWO_PARAMS = "{}{}";

    private static final String SFTP_ERROR = "SFTP Error:";


    /**
     * JUnit constructor.
     * 
     * @param entityManager The EntityManager
     * @param xhbConfigPropRepository The XhbConfigPropRepository
     * @param environment The Environment
     */
    public SftpConfigHelper(EntityManager entityManager,
        XhbConfigPropRepository xhbConfigPropRepository, Environment environment,
        PddaMessageHelper pddaMessageHelper, XhbClobRepository clobRepository,
        XhbCourtRepository courtRepository) {
        super(entityManager, xhbConfigPropRepository, environment,
            pddaMessageHelper, clobRepository, courtRepository);
    }

    /**
     * JUnit constructor.
     * 
     * @param entityManager The EntityManager
     */
    @SuppressWarnings("PMD.UnusedFormalParameter")
    public SftpConfigHelper(EntityManager entityManager) {
        super(entityManager);
        this.entityManager = entityManager;
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
    @SuppressWarnings("PMD.UnusedAssignment")
    private SftpConfig getDbConfigParams(SftpConfig sftpConfig) {
        
        methodName = "setDBConfigParams()";
        LOG.debug(TWO_PARAMS, methodName, LOG_CALLED);

        // Firstly CP BAIS properties
        String currentProperty = "";
        String hostAndPort = "";
        try {
            currentProperty = Config.DB_CP_SFTP_USERNAME;
            sftpConfig.setCpUsername(getMandatoryConfigValue(currentProperty));
            LOG.debug("SFTP Cp Username: {}", sftpConfig.getCpUsername());

            currentProperty = Config.DB_CP_SFTP_PASSWORD;
            sftpConfig.setCpPassword(getMandatoryConfigValue(currentProperty));

            currentProperty = Config.DB_CP_SFTP_UPLOAD_LOCATION;
            sftpConfig
                .setCpRemoteFolder(getMandatoryConfigValue(currentProperty));
            LOG.debug("SFTP Cp Remote Folder: {}", sftpConfig.getCpRemoteFolder());

            // Next the XHIBIT BAIS properties
            currentProperty = Config.DB_SFTP_USERNAME;
            sftpConfig.setXhibitUsername(getMandatoryConfigValue(currentProperty));
            LOG.debug("SFTP XHIBIT Username: {}", sftpConfig.getCpUsername());

            currentProperty = Config.DB_SFTP_PASSWORD;
            sftpConfig.setXhibitPassword(getMandatoryConfigValue(currentProperty));

            currentProperty = Config.DB_SFTP_UPLOAD_LOCATION;
            sftpConfig
                .setXhibitRemoteFolder(getMandatoryConfigValue(currentProperty));
            LOG.debug("SFTP XHIBIT Remote Folder: {}", sftpConfig.getXhibitRemoteFolder());

            // Now get the host and port
            currentProperty = Config.DB_SFTP_HOST;
            hostAndPort = getMandatoryConfigValue(currentProperty);
            LOG.debug("SFTP Host and port: {}", hostAndPort);
        } catch (InvalidConfigException ex) {
            sftpConfig.setErrorMsg(currentProperty + NOT_FOUND);
        }

        // Validate the host and port
        return validateAndSetHostAndPort(sftpConfig, hostAndPort);
    }


    /**
     * Set the SFTP config params from the Key Vault.
     * 
     * @return The SFTP configuration
     */
    @SuppressWarnings("PMD.UnusedAssignment")
    private SftpConfig getKvConfigParams(SftpConfig sftpConfig) {

        methodName = "setKVConfigParams()";
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
            sftpConfig.setCpUsername(getMandatoryEnvValue(currentProperty));
            LOG.debug("SFTP XHIBIT Username: {}", sftpConfig.getCpUsername());

            currentProperty = Config.KV_SFTP_PASSWORD;
            sftpConfig.setCpPassword(getMandatoryEnvValue(currentProperty));

            currentProperty = Config.KV_SFTP_UPLOAD_LOCATION;
            sftpConfig.setXhibitRemoteFolder(getMandatoryEnvValue(currentProperty));
            LOG.debug("SFTP XHIBIT Remote Folder: {}", sftpConfig.getXhibitRemoteFolder());

            // Now get the host and port
            currentProperty = Config.KV_SFTP_HOST;
            hostAndPort = getMandatoryEnvValue(currentProperty);
            LOG.debug("SFTP Host and port: {}", hostAndPort);
        } catch (InvalidConfigException ex) {
            sftpConfig.setErrorMsg(currentProperty + NOT_FOUND);
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
            sftpConfig.setSession(getPddaSftpHelper().createSession(sftpConfig.getXhibitUsername(),
                sftpConfig.getXhibitPassword(), sftpConfig.getHost(), sftpConfig.getPort()));
            LOG.debug("A session has been established");
        } catch (Exception ex) {
            sftpConfig.setErrorMsg(SFTP_ERROR + ex.getMessage());
            LOG.error("Stacktrace2:: {}", ExceptionUtils.getStackTrace(ex));
        }

        return sftpConfig;
    }

}
