package uk.gov.hmcts.pdda.business.services.pdda.sftp;

import com.jcraft.jsch.Session;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;

/**
 * <p>
 * Title: Sftp Config.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2023
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @author Mark Harris
 * @version 1.0
 */
public class SftpConfig {
    String cpUsername;
    String cpPassword;
    String cpRemoteFolder;
    String xhibitUsername;
    String xhibitPassword;
    String xhibitRemoteFolder;
    String activeRemoteFolder;
    String host;
    Integer port;
    String errorMsg;
    SFTPClient sshjSftpClient;
    SSHClient sshClient;
    Session session;
    boolean useKeyVault;
    String cpExcludedCourtIds;


    public String getCpExcludedCourtIds() {
        return cpExcludedCourtIds;
    }

    public void setCpExcludedCourtIds(String cpExcludedCourtIds) {
        this.cpExcludedCourtIds = cpExcludedCourtIds;
    }

    public boolean isUseKeyVault() {
        return useKeyVault;
    }

    public void setUseKeyVault(boolean useKeyVault) {
        this.useKeyVault = useKeyVault;
    }

    public String getCpUsername() {
        return cpUsername;
    }

    public void setCpUsername(String cpUsername) {
        this.cpUsername = cpUsername;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public void setSshjSftpClient(SFTPClient sshjSftpClient) {
        this.sshjSftpClient = sshjSftpClient;
    }

    public SFTPClient getSshjSftpClient() {
        if (this.sshjSftpClient == null) {
            return null;
        }
        return this.sshjSftpClient;
    }

    public void setSshClient(SSHClient sshClient) {
        this.sshClient = sshClient;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String getCpRemoteFolder() {
        return cpRemoteFolder;
    }

    public void setCpRemoteFolder(String cpRemoteFolder) {
        this.cpRemoteFolder = cpRemoteFolder;
    }

    public String getCpPassword() {
        return cpPassword;
    }

    public void setCpPassword(String cpPassword) {
        this.cpPassword = cpPassword;
    }

    public String getXhibitUsername() {
        return xhibitUsername;
    }

    public void setXhibitUsername(String xhibitUsername) {
        this.xhibitUsername = xhibitUsername;
    }

    public String getXhibitPassword() {
        return xhibitPassword;
    }

    public void setXhibitPassword(String xhibitPassword) {
        this.xhibitPassword = xhibitPassword;
    }

    public String getXhibitRemoteFolder() {
        return xhibitRemoteFolder;
    }

    public void setXhibitRemoteFolder(String xhibitRemoteFolder) {
        this.xhibitRemoteFolder = xhibitRemoteFolder;
    }

    public String getActiveRemoteFolder() {
        return activeRemoteFolder;
    }

    public void setActiveRemoteFolder(String activeRemoteFolder) {
        this.activeRemoteFolder = activeRemoteFolder;
    }

    public SSHClient getSshClient() {
        return sshClient;
    }

}
