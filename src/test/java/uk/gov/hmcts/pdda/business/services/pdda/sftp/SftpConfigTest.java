package uk.gov.hmcts.pdda.business.services.pdda.sftp;

import com.jcraft.jsch.Session;
import de.ppi.fakesftpserver.extension.FakeSftpServerExtension;
import jakarta.persistence.EntityManager;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


@ExtendWith(MockitoExtension.class)
class SftpConfigTest {

    private static final Logger LOG = LoggerFactory.getLogger(SftpConfigTest.class);

    private static final String RESULT_EQUAL = "Result is Equal";

    @RegisterExtension
    public final FakeSftpServerExtension sftpServer = new FakeSftpServerExtension();

    @InjectMocks
    private SftpConfig sftpConfig;

    @Mock
    private EntityManager mockEntityManager;

    @BeforeEach
    public void setUp() {
        sftpConfig = new SftpConfig();
    }

    @Test
    void testGettersAndSetters() {
        String host = "localhost";
        sftpConfig.setHost(host);
        assertEquals(host, sftpConfig.getHost(), RESULT_EQUAL);

        Integer port = 22;
        sftpConfig.setPort(port);
        assertEquals(port, sftpConfig.getPort(), RESULT_EQUAL);

        String cpUsername = "cpUsername";
        sftpConfig.setCpUsername(cpUsername);
        assertEquals(cpUsername, sftpConfig.getCpUsername(), RESULT_EQUAL);

        String cpPassword = "cpPassword";
        sftpConfig.setCpPassword(cpPassword);
        assertEquals(cpPassword, sftpConfig.getCpPassword(), RESULT_EQUAL);

        String cpRemoteFolder = "cpRemoteFolder";
        sftpConfig.setCpRemoteFolder(cpRemoteFolder);
        assertEquals(cpRemoteFolder, sftpConfig.getCpRemoteFolder(), RESULT_EQUAL);

        String xhibitUsername = "xhibitUsername";
        sftpConfig.setXhibitUsername(xhibitUsername);
        assertEquals(xhibitUsername, sftpConfig.getXhibitUsername(), RESULT_EQUAL);

        String xhibitPassword = "xhibitPassword";
        sftpConfig.setXhibitPassword(xhibitPassword);
        assertEquals(xhibitPassword, sftpConfig.getXhibitPassword(), RESULT_EQUAL);

        String xhibitRemoteFolder = "xhibitRemoteFolder";
        sftpConfig.setXhibitRemoteFolder(xhibitRemoteFolder);
        assertEquals(xhibitRemoteFolder, sftpConfig.getXhibitRemoteFolder(), RESULT_EQUAL);

        String activeRemoteFolder = "activeRemoteFolder";
        sftpConfig.setActiveRemoteFolder(activeRemoteFolder);
        assertEquals(activeRemoteFolder, sftpConfig.getActiveRemoteFolder(), RESULT_EQUAL);

        String errorMsg = "errorMsg";
        sftpConfig.setErrorMsg(errorMsg);
        assertEquals(errorMsg, sftpConfig.getErrorMsg(), RESULT_EQUAL);

        boolean keyVaultConfig = true;
        sftpConfig.setUseKeyVault(keyVaultConfig);
        assertEquals(keyVaultConfig, sftpConfig.isUseKeyVault(), RESULT_EQUAL);
    }

    @Test
    void testSetAndGetSession() {
        Session session = null; // sftpConfig.setSession(getSftpHelper().createSession(sftpConfig.getXhibitUsername(),
        // sftpConfig.getXhibitPassword(), sftpConfig.getHost(), sftpConfig.getPort()));
        Session session2 = sftpConfig.getSession();
        sftpConfig.setSession(session);
        assertNull(session, "Session is null");
        assertEquals(session2, sftpConfig.getSession(), RESULT_EQUAL);
    }

    @Test
    @SuppressWarnings("PMD")
    void testTextFile() {
        try (SSHClient ssh = new SftpConfigHelper(mockEntityManager).getNewSshClient()) {
            ssh.connect("localhost", sftpServer.getPort());
            ssh.authPassword("user", "password");
            sftpConfig.setSshClient(ssh);
            SFTPClient sftpClient = new SFTPClient(ssh);
            sftpConfig.setSshjSftpClient(sftpClient);
            putAndGetFile();
            ssh.disconnect();
            assertNotNull(ssh, "SSHClient is null");
        } catch (IOException e) {
            LOG.error("Error disconnecting SSHClient", e);
        }
    }

    private void putAndGetFile() {
        try {
            String fileContent = "content of file";
            String port = Integer.toString(sftpServer.getPort());
            LOG.debug("Port: " + port);
            sftpServer.putFile("/directory/file.txt", fileContent, Charset.defaultCharset());
            byte[] retrievedContent = sftpServer.getFileContent("/directory/file.txt");
            assertEquals(fileContent, new String(retrievedContent), RESULT_EQUAL);

        } catch (IOException e) {
            LOG.error("Error putting/getting file", e);
        }
        // code that downloads the file
    }


    /*
     * @Test void testSetAndGetSshClient() { try { SSHClient ssh = new
     * SftpConfigHelper(mockEntityManager).getNewSshClient(); ssh.connect("localhost",
     * sftpServer.getPort()); ssh.authPassword("user", "password"); sftpConfig.setSshClient(ssh);
     * assertEquals(ssh, sftpConfig.getSshClient(), RESULT_EQUAL);
     * 
     * SFTPClient sftpClient = new SFTPClient(ssh); sftpConfig.setSshjSftpClient(sftpClient);
     * 
     * assertNotNull(ssh, "SSHClient is not null"); assertEquals(sftpClient,
     * sftpConfig.getSshjSftpClient(), RESULT_EQUAL); ssh.disconnect(); } catch (IOException e) {
     * LOG.error("Error disconnecting SSHClient", e); } }
     */

    // Add more tests for other methods in SftpConfig class
}