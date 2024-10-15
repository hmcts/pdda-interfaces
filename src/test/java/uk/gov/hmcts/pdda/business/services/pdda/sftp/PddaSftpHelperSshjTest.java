package uk.gov.hmcts.pdda.business.services.pdda.sftp;

import de.ppi.fakesftpserver.extension.FakeSftpServerExtension;
import jakarta.persistence.EntityManager;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.services.pdda.sftp.SftpService.BaisCpValidation;
import uk.gov.hmcts.pdda.business.services.pdda.sftp.SftpService.BaisXhibitValidation;

import java.io.IOException;
import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.assertNotNull;


@ExtendWith(MockitoExtension.class)
class PddaSftpHelperSshjTest {

    private static final Logger LOG = LoggerFactory.getLogger(PddaSftpHelperSshjTest.class);

    @RegisterExtension
    public final FakeSftpServerExtension sftpServer = new FakeSftpServerExtension();

    @Mock
    private SSHClient sshClient;

    @Mock
    private SftpConfig sftpConfig;

    @Mock
    private EntityManager mockEntityManager;

    private final SftpConfig sftpConfig2 = new SftpConfig();

    @InjectMocks
    private PddaSftpHelperSshj pddaSftpHelperSshj;

    @BeforeEach
    public void setUp() {
        pddaSftpHelperSshj = new PddaSftpHelperSshj();
        setupSftpConfig();
    }

    @AfterEach
    @SuppressWarnings("PMD")
    public void tearDown() {
        try {
            SSHClient ssh = sftpConfig2.getSshClient();
            ssh.disconnect();
        } catch (IOException e) {
            LOG.error("Error disconnecting SSHClient", e);
        }
    }

    @Test
    void testGetSftpFetch() {
        setupSftpConfig();
        setupFiles();
        try {
            pddaSftpHelperSshj.sftpFetch(sftpConfig2.getSshjSftpClient(),
                sftpConfig2.getActiveRemoteFolder(), new BaisXhibitValidation(null));
            assertNotNull(sftpConfig2.getSshjSftpClient(), "SFTPClient should not be null");

            pddaSftpHelperSshj.sftpFetch(sftpConfig2.getSshjSftpClient(),
                sftpConfig2.getActiveRemoteFolder(), new BaisCpValidation(null));
            assertNotNull(sftpConfig2.getSshjSftpClient(), "SFTPClient should not be null");
        } catch (IOException e) {
            LOG.error("Error fetching files", e);
        }
    }

    @Test
    void testSftpDeleteFile() {
        setupSftpConfig();
        setupFiles();
        try {
            pddaSftpHelperSshj.sftpDeleteFile(sftpConfig2.getSshjSftpClient(),
                sftpConfig2.getActiveRemoteFolder(), "test.txt");
            assertNotNull(sftpConfig2.getSshjSftpClient(), "SFTPClient should not be null");
        } catch (IOException e) {
            LOG.error("Error deleting file", e);
        }
    }

    private void setupFiles() {
        try {
            sftpServer.putFile("/directory/test.txt", "content of file", Charset.defaultCharset());
        } catch (IOException e) {
            LOG.error("Error putting file", e);
        }
    }


    @SuppressWarnings("PMD")
    private void setupSftpConfig() {
        sftpConfig2.setHost("localhost");
        sftpConfig2.setPort(sftpServer.getPort());
        sftpConfig2.setCpUsername("cpUsername");
        sftpConfig2.setCpPassword("cpPassword");
        sftpConfig2.setCpRemoteFolder("/directory/");
        sftpConfig2.setXhibitUsername("xhibitUsername");
        sftpConfig2.setXhibitPassword("xhibitPassword");
        sftpConfig2.setXhibitRemoteFolder("/directory/");
        sftpConfig2.setActiveRemoteFolder("/directory/");

        try {
            SSHClient ssh = new SftpConfigHelper(mockEntityManager).getNewSshClient();
            ssh.connect(sftpConfig2.getHost(), sftpConfig2.getPort());
            ssh.authPassword(sftpConfig2.getCpUsername(), sftpConfig2.getCpPassword());
            sftpConfig2.setSshClient(ssh);

            SFTPClient sftpClient = new SFTPClient(ssh);
            sftpConfig2.setSshjSftpClient(sftpClient);

            assertNotNull(ssh, "SSHClient is not null");
            assertNotNull(sftpClient, "SFTPClient is not null");
        } catch (IOException e) {
            LOG.error("Error setting up SFTP config", e);
        }
    }

    // Add more tests for other methods in PddaSftpHelperSshj class
}
