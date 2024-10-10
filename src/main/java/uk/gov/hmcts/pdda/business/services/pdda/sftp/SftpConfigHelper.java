package uk.gov.hmcts.pdda.business.services.pdda.sftp;

import jakarta.persistence.EntityManager;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * SFTP Configuration Helper class to handle getting the SFTP configuration.
 */
public class SftpConfigHelper extends SftpHelper {

    private static final Logger LOG = LoggerFactory.getLogger(SftpConfigHelper.class);

    protected static final String LOG_CALLED = " called";
    // private static final String SFTP_ERROR = "SFTP Error:";
    private static final String NOT_FOUND = " not found";
    private static final String TWO_PARAMS = "{}{}";

    private static final String SFTP_ERROR = "SFTP Error:";

    // Instance of the SftpConfig
    // private SftpConfig sftpConfigInstance;


    public SftpConfigHelper(EntityManager entityManager) {
        super(entityManager);
    }


    /**
     * Get a new instance of the SFTP configuration.
     * 
     * @return A new instance of the SFTP configuration
     */
    // public SftpConfig getSftpConnectionData(String baisConnectionType) {
    // // Create a new SSHClient and SFTPClient
    // setSftpConfigData(baisConnectionType);
    // return sftpConfig;
    // }

    /**
     * Set the SFTP config data.
     * 
     * @return The SFTP configuration
     */
    /*
     * public SftpConfig setSftpConfigData(String baisConnectionType, SftpConfig sftpConfig) {
     * 
     * if (TEST_CONNECTION_TYPE.equals(baisConnectionType)) { setConfigParamsTest(sftpConfig); }
     * 
     * // Now get an SFTP connection sftpConfig = createSftpConnection(sftpConfig);
     * 
     * return sftpConfig; }
     */


    /**
     * Set the SFTP config params.
     */
    /*
     * private SftpConfig setConfigParamsTest(SftpConfig sftpConfig) { // Validate the incoming data
     * methodName = "setConfigParams()"; LOG.debug(TWO_PARAMS, methodName, LOG_CALLED);
     * 
     * sftpConfig.setCpUsername("scottatwell"); LOG.debug("SFTP Username: {}",
     * sftpConfig.getCpUsername()); sftpConfig.setCpPassword("PXT379_mkt");
     * sftpConfig.setCpRemoteFolder("/tmp/sftptestfolder/"); LOG.debug("SFTP Remote Folder: {}",
     * sftpConfig.getCpRemoteFolder()); String hostAndPort; hostAndPort = "127.0.0.1:22";
     * LOG.debug("SFTP Host and port: {}", hostAndPort);
     * 
     * // Validate the host and port LOG.debug("Validating host and port"); String portDelimiter =
     * ":"; Integer pos = hostAndPort.indexOf(portDelimiter); if (pos <= 0) { sftpConfig.errorMsg =
     * "127.0.0.1:22" + " syntax is <Host>" + portDelimiter + "<Port>"; } sftpConfig.host =
     * hostAndPort.substring(0, pos); try { String strPort = hostAndPort.substring(pos + 1,
     * hostAndPort.length()); sftpConfig.port = Integer.valueOf(strPort); } catch (Exception ex) {
     * sftpConfig.errorMsg = "127.0.0.1:22" + " contains invalid port number";
     * LOG.error("Stacktrace1:: {}", ExceptionUtils.getStackTrace(ex)); }
     * 
     * return sftpConfig; }
     */


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
     * Get the SFTP configs.
     * 
     * @return The SFTP configuration
     */
    /*
     * private SftpConfig createSftpConnection(SftpConfig sftpConfig) { methodName =
     * "getSftpConnection()"; LOG.debug(TWO_PARAMS, methodName, LOG_CALLED);
     * 
     * // Create a SSHClient that will be valid for the lifetime of this iteration try {
     * LOG.debug("Details validated successfully");
     * 
     * LOG.debug("Creating a new SSHClient"); SSHClient ssh = getNewSshClient();
     * sftpConfig.setSshClient(ssh);
     * 
     * sftpConfig = createSshjSftpClient(sftpConfig); LOG.debug("A session has been established"); }
     * catch (Exception ex) { sftpConfig.errorMsg = SFTP_ERROR + ex.getMessage();
     * LOG.error("Stacktrace3:: {}", ExceptionUtils.getStackTrace(ex)); }
     * 
     * LOG.debug("Connected successfully");
     * 
     * return sftpConfig; }
     */

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

    /**
     * Uses SSHJ to create a session.
     * 
     * @param sftpConfig The SFTP configuration
     * @throws IOException The IO Exception
     */
    /*
     * public SftpConfig createSshjSftpClient(SftpConfig sftpConfig) throws IOException { methodName
     * = "createSSHJClient()"; LOG.debug(TWO_PARAMS, methodName, LOG_CALLED);
     * 
     * SSHClient ssh = sftpConfig.getSshClient(); LOG.debug("Connecting to host: {}:{} ",
     * sftpConfig.getHost(), sftpConfig.getPort()); ssh.connect(sftpConfig.getHost(),
     * sftpConfig.getPort()); LOG.debug("Verifying username: {} ", sftpConfig.getCpUsername());
     * ssh.authPassword(sftpConfig.getCpUsername(), sftpConfig.getCpPassword());
     * sftpConfig.setSshClient(ssh);
     * 
     * LOG.debug("Get SFTP Client"); SFTPClient sftpClient = ssh.newSFTPClient();
     * LOG.debug("SFTP Client: {}", sftpClient); sftpConfig.setSshjSftpClient(sftpClient);
     * 
     * return sftpConfig; }
     */


    /**
     * Close the SFTP connection.
     * 
     * @param sftpConfig The SFTP configuration
     * @throws IOException The IO Exception
     */
    /*
     * public void closeSftpConnection(SftpConfig sftpConfig) throws IOException { methodName =
     * "closeSftpConnection()"; LOG.debug(TWO_PARAMS, methodName, LOG_CALLED);
     * 
     * SFTPClient sftpClient = null; try { sftpClient = sftpConfig.getSshjSftpClient();
     * sftpClient.close(); } catch (NullPointerException | IOException e) {
     * LOG.error("Error closing SFTP connection: {}", ExceptionUtils.getStackTrace(e));
     * LOG.error("Stacktrace4:: {}", ExceptionUtils.getStackTrace(e));
     * 
     * if (sftpClient != null) { sftpClient.close(); } throw e; } }
     */


    /**
     * Reset the SFTP configuration and close the connection.
     * 
     * @param sftpConfig The SFTP configuration
     * @return The reset SftpConfig
     * @throws NullPointerException The Null Pointer Exception
     * @throws IOException The IO Exception
     */
    /*
     * public SftpConfig resetAndClose(SftpConfig sftpConfig) throws IOException {
     * 
     * try { closeSftpConnection(sftpConfig); } catch (NullPointerException | IOException e) {
     * LOG.error("Error closing SFTP connection: {}", ExceptionUtils.getStackTrace(e));
     * LOG.error("Stacktrace5:: {}", ExceptionUtils.getStackTrace(e)); }
     * 
     * try { sftpConfig.getSshClient().disconnect(); } catch (NullPointerException | IOException e)
     * { LOG.error("Error disconnecting SSH Client: {}", ExceptionUtils.getStackTrace(e));
     * LOG.error("Stacktrace6:: {}", ExceptionUtils.getStackTrace(e)); }
     * 
     * sftpConfig.setSshjSftpClient(null); sftpConfig.setSshClient(null);
     * sftpConfig.setErrorMsg(null); sftpConfig.setCpRemoteFolder(null);
     * sftpConfig.setCpUsername(null); sftpConfig.setCpPassword(null);
     * sftpConfig.setXhibitRemoteFolder(null); sftpConfig.setXhibitUsername(null);
     * sftpConfig.setXhibitPassword(null); sftpConfig.setHost(null); sftpConfig.setPort(null);
     * 
     * return sftpConfig; }
     */


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
