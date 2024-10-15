package uk.gov.hmcts.pdda.business.services.pdda.sftp;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import de.ppi.fakesftpserver.extension.FakeSftpServerExtension;
import jakarta.persistence.EntityManager;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import uk.gov.hmcts.DummyServicesUtil;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropDao;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.services.pdda.PddaMessageHelper;
import uk.gov.hmcts.pdda.business.services.pdda.PddaSftpHelper;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * <p>
 * Title: PddaHelperBaisTest.
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
 */
@ExtendWith(EasyMockExtension.class)
@SuppressWarnings("PMD.ExcessiveImports, PMD.CouplingBetweenObjects")
class SftpConfigHelperTest {

    private static final Logger LOG = LoggerFactory.getLogger(SftpConfigHelperTest.class);

    @RegisterExtension
    public final FakeSftpServerExtension sftpServer = new FakeSftpServerExtension();

    private static final String NOT_NULL = "Result is Null";

    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private PddaMessageHelper mockPddaMessageHelper;

    @Mock
    private PddaSftpHelper mockPddaSftpHelper;

    @Mock
    private SftpConfigHelper mockSftpConfigHelper;

    @Mock
    private SftpConfig mockSftpConfig;

    @Mock
    private XhbConfigPropRepository mockXhbConfigPropRepository;

    @Mock
    private XhbCourtRepository mockXhbCourtRepository;

    @Mock
    private XhbClobRepository mockXhbClobRepository;

    @Mock
    private Session mockSession;

    @Mock
    private SftpService mockSftpService;

    @Mock
    private Environment mockEnvironment;

    private final SftpConfig sftpConfig = new SftpConfig();

    @TestSubject
    private final SftpConfigHelper classUnderTest =
        new SftpConfigHelper(EasyMock.createMock(EntityManager.class), mockXhbConfigPropRepository,
            mockEnvironment, mockPddaMessageHelper, mockXhbClobRepository, mockXhbCourtRepository);

    private static final class Config {

        // Database values
        public static final String DB_SFTP_HOST = "PDDA_BAIS_SFTP_HOSTNAME";
        public static final String DB_SFTP_PASSWORD = "PDDA_BAIS_SFTP_PASSWORD";
        public static final String DB_SFTP_UPLOAD_LOCATION = "PDDA_BAIS_SFTP_UPLOAD_LOCATION";
        public static final String DB_SFTP_USERNAME = "PDDA_BAIS_SFTP_USERNAME";
        public static final String DB_CP_SFTP_USERNAME = "PDDA_BAIS_CP_SFTP_USERNAME";
        public static final String DB_CP_SFTP_PASSWORD = "PDDA_BAIS_CP_SFTP_PASSWORD";
        public static final String DB_CP_SFTP_UPLOAD_LOCATION = "PDDA_BAIS_CP_SFTP_UPLOAD_LOCATION";

        // Key vault values
        public static final String KV_SFTP_HOST = "pdda.bais_sftp_hostname";
        public static final String KV_SFTP_PASSWORD = "pdda.bais_sftp_password";
        public static final String KV_SFTP_UPLOAD_LOCATION = "PDDA_BAIS_SFTP_UPLOAD_LOCATION";
        public static final String KV_SFTP_USERNAME = "pdda.bais_sftp_username";
        public static final String KV_CP_SFTP_USERNAME = "pdda.bais_cp_sftp_username";
        public static final String KV_CP_SFTP_PASSWORD = "pdda.bais_cp_sftp_password";
        public static final String KV_CP_SFTP_UPLOAD_LOCATION = "PDDA_BAIS_CP_SFTP_UPLOAD_LOCATION";
    }

    @Test
    void testValidateAndSetHostAndPort() {
        // Setup
        sftpConfig.setHost("localhost");
        sftpConfig.setPort(22);
        // Run
        classUnderTest.validateAndSetHostAndPort(sftpConfig,
            sftpConfig.getHost() + ":" + sftpConfig.getPort());
        // Checks
        assertNotNull(sftpConfig.getHost(), NOT_NULL);
        assertNotNull(sftpConfig.getPort(), NOT_NULL);
    }

    @Test
    void testGetConfigParams() {
        // Setup
        testGetBaisConfigs(null);
        EasyMock.expect(mockEnvironment.getProperty("PDDA_BAIS_SFTP_USERNAME"))
            .andReturn("this_user");
        EasyMock.expect(mockEnvironment.getProperty("PDDA_BAIS_SFTP_PASSWORD"))
            .andReturn("this_pass");
        EasyMock.expect(mockSftpConfigHelper.getJschSession(EasyMock.isA(SftpConfig.class)))
            .andReturn(mockSftpConfig);
        try {
            EasyMock.expect(mockPddaSftpHelper.createSession(EasyMock.isA(String.class),
                EasyMock.isA(String.class), EasyMock.isA(String.class),
                EasyMock.isA(Integer.class))).andReturn(mockSession);
        } catch (JSchException e) {
            LOG.error("Error setting up Jsch session", e);
        }

        EasyMock.replay(mockSftpConfigHelper);
        EasyMock.replay(mockSftpConfig);
        EasyMock.replay(mockXhbConfigPropRepository);
        EasyMock.replay(mockPddaSftpHelper);
        EasyMock.replay(mockSession);
        EasyMock.replay(mockPddaMessageHelper);
        EasyMock.replay(mockXhbCourtRepository);
        EasyMock.replay(mockXhbClobRepository);
        EasyMock.replay(mockEnvironment);

        setupSftpConfig();
        setupFiles();
        sftpConfig.setUseKeyVault(false);

        // Run
        SftpConfig result = classUnderTest.getConfigParams(sftpConfig);
        assertNotNull(result, NOT_NULL);
        sftpConfig.setUseKeyVault(true);
        result = classUnderTest.getConfigParams(sftpConfig);
        assertNotNull(result, NOT_NULL);

        sftpConfig.setHost("localhost");
        sftpConfig.setPort(sftpServer.getPort());
        result = classUnderTest.getJschSession(sftpConfig);
        assertNotNull(result, NOT_NULL);
    }


    private void testGetBaisConfigs(String failOn) {
        // DB first then KV

        // DB values need to be set in Config Prop Repository
        // Username
        String propertyName = Config.DB_CP_SFTP_USERNAME;
        EasyMock.expect(mockEnvironment.getProperty(propertyName))
            .andReturn(propertyName.toLowerCase(Locale.getDefault())).anyTimes();
        List<XhbConfigPropDao> usernameList = getXhbConfigPropDaoList(propertyName);
        EasyMock.expect(mockXhbConfigPropRepository.findByPropertyName(propertyName))
            .andReturn(usernameList).anyTimes();
        // Password
        propertyName = Config.DB_CP_SFTP_PASSWORD;
        EasyMock.expect(mockEnvironment.getProperty(propertyName))
            .andReturn(propertyName.toLowerCase(Locale.getDefault())).anyTimes();
        List<XhbConfigPropDao> passwordList = getXhbConfigPropDaoList(propertyName);
        EasyMock.expect(mockXhbConfigPropRepository.findByPropertyName(propertyName))
            .andReturn(passwordList).anyTimes();
        // Location
        propertyName = Config.DB_CP_SFTP_UPLOAD_LOCATION;
        List<XhbConfigPropDao> locationList = getXhbConfigPropDaoList(propertyName);
        EasyMock.expect(mockXhbConfigPropRepository.findByPropertyName(propertyName))
            .andReturn(locationList).anyTimes();
        propertyName = Config.DB_SFTP_USERNAME;
        usernameList = getXhbConfigPropDaoList(propertyName);
        EasyMock.expect(mockXhbConfigPropRepository.findByPropertyName(propertyName))
            .andReturn(usernameList).anyTimes();
        // Password
        propertyName = Config.DB_SFTP_PASSWORD;
        passwordList = getXhbConfigPropDaoList(propertyName);
        EasyMock.expect(mockXhbConfigPropRepository.findByPropertyName(propertyName))
            .andReturn(passwordList).anyTimes();
        // Location
        propertyName = Config.DB_SFTP_UPLOAD_LOCATION;
        locationList = getXhbConfigPropDaoList(propertyName);
        EasyMock.expect(mockXhbConfigPropRepository.findByPropertyName(propertyName))
            .andReturn(locationList).anyTimes();
        // Host
        propertyName = Config.DB_SFTP_HOST;
        String hostAndPort = propertyName.toLowerCase(Locale.getDefault());
        List<XhbConfigPropDao> hostList = getXhbConfigPropDaoList(propertyName);
        if (failOn == null || !propertyName.equals(failOn)) {
            hostAndPort = hostList.get(0).getPropertyValue() + ":22";
            hostList = getXhbConfigPropDaoList(hostAndPort);
        }
        EasyMock.expect(mockXhbConfigPropRepository.findByPropertyName(propertyName))
            .andReturn(hostList).anyTimes();
        EasyMock.expect(mockEnvironment.getProperty(propertyName)).andReturn(hostAndPort)
            .anyTimes();


        // KV values need to be set in environment
        // Username
        propertyName = Config.KV_CP_SFTP_USERNAME;
        EasyMock.expect(mockEnvironment.getProperty(propertyName))
            .andReturn(propertyName.toLowerCase(Locale.getDefault())).anyTimes();
        // Password
        propertyName = Config.KV_CP_SFTP_PASSWORD;
        EasyMock.expect(mockEnvironment.getProperty(propertyName))
            .andReturn(propertyName.toLowerCase(Locale.getDefault())).anyTimes();
        // Username
        propertyName = Config.KV_SFTP_USERNAME;
        EasyMock.expect(mockEnvironment.getProperty(propertyName))
            .andReturn(propertyName.toLowerCase(Locale.getDefault())).anyTimes();
        // Password
        propertyName = Config.KV_SFTP_PASSWORD;
        EasyMock.expect(mockEnvironment.getProperty(propertyName))
            .andReturn(propertyName.toLowerCase(Locale.getDefault())).anyTimes();
        // Location
        propertyName = Config.KV_SFTP_UPLOAD_LOCATION; // Already done above
        EasyMock.expect(mockEnvironment.getProperty(propertyName))
            .andReturn(propertyName.toLowerCase(Locale.getDefault())).anyTimes();
        propertyName = Config.KV_CP_SFTP_UPLOAD_LOCATION; // Already done above
        EasyMock.expect(mockEnvironment.getProperty(propertyName))
            .andReturn(propertyName.toLowerCase(Locale.getDefault())).anyTimes();
        // Host
        propertyName = Config.KV_SFTP_HOST;
        hostAndPort = propertyName.toLowerCase(Locale.getDefault());
        if (failOn == null || !propertyName.equals(failOn)) {
            hostAndPort = hostAndPort + ":22";
        }
        EasyMock.expect(mockEnvironment.getProperty(propertyName)).andReturn(hostAndPort)
            .anyTimes();
    }

    private List<XhbConfigPropDao> getXhbConfigPropDaoList(String propertyName) {
        List<XhbConfigPropDao> result = new ArrayList<>();
        result.add(DummyServicesUtil.getXhbConfigPropDao(propertyName,
            propertyName.toLowerCase(Locale.getDefault())));
        return result;
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
        sftpConfig.setHost("localhost");
        sftpConfig.setPort(sftpServer.getPort());
        sftpConfig.setCpUsername("cpUsername");
        sftpConfig.setCpPassword("cpPassword");
        sftpConfig.setCpRemoteFolder("/directory/");
        sftpConfig.setXhibitUsername("xhibitUsername");
        sftpConfig.setXhibitPassword("xhibitPassword");
        sftpConfig.setXhibitRemoteFolder("/directory/");
        sftpConfig.setActiveRemoteFolder("/directory/");

        try {
            SSHClient ssh = new SftpConfigHelper(mockEntityManager).getNewSshClient();
            ssh.connect(sftpConfig.getHost(), sftpConfig.getPort());
            ssh.authPassword(sftpConfig.getCpUsername(), sftpConfig.getCpPassword());
            sftpConfig.setSshClient(ssh);

            SFTPClient sftpClient = new SFTPClient(ssh);
            sftpConfig.setSshjSftpClient(sftpClient);

            assertNotNull(ssh, "SSHClient is not null");
            assertNotNull(sftpClient, "SFTPClient is not null");
        } catch (IOException e) {
            LOG.error("Error setting up SFTP config", e);
        }
    }

}
