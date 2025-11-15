package uk.gov.hmcts.pdda.business.services.pdda.sftp;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import de.ppi.fakesftpserver.extension.FakeSftpServerExtension;
import jakarta.persistence.EntityManager;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import uk.gov.hmcts.DummyServicesUtil;
import uk.gov.hmcts.pdda.business.entities.xhbcase.XhbCaseRepository;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropDao;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbconfiguredpublicnotice.XhbConfiguredPublicNoticeRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.entities.xhbhearing.XhbHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbpublicnotice.XhbPublicNoticeRepository;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingRepository;
import uk.gov.hmcts.pdda.business.services.pdda.PddaMessageHelper;
import uk.gov.hmcts.pdda.business.services.pdda.PddaSftpHelper;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**

 * Title: PddaHelperBaisTest.


 * Description:


 * Copyright: Copyright (c) 2023


 * Company: CGI

 * @author Mark Harris
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings({"PMD"})
class SftpHelperUtilTest {

    private static final Logger LOG = LoggerFactory.getLogger(SftpHelperUtilTest.class);

    private static final String LOCALHOST_STRING = "localhost";
    private static final String TEST_SFTP_DIRECTORY = "/directory/";
    private static final String USE_KEY_VAULT_PROPERTIES = "USE_KEY_VAULT_PROPERTIES";

    @RegisterExtension
    public final FakeSftpServerExtension sftpServer = new FakeSftpServerExtension();

    private static final String NOT_NULL = "Result is Not Null";

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
    private XhbCourtRoomRepository mockXhbCourtRoomRepository;
    
    @Mock
    private XhbCourtSiteRepository mockXhbCourtSiteRepository;
    
    @Mock
    private XhbCaseRepository mockXhbCaseRepository;
    
    @Mock
    private XhbHearingRepository mockXhbHearingRepository;
    
    @Mock
    private XhbSittingRepository mockXhbSittingRepository;
    
    @Mock
    private XhbScheduledHearingRepository mockXhbScheduledHearingRepository;
    
    @Mock
    private XhbPublicNoticeRepository mockXhbPublicNoticeRepository;
    
    @Mock
    private XhbConfiguredPublicNoticeRepository mockXhbConfiguredPublicNoticeRepository;

    @Mock
    private Session mockSession;

    @Mock
    private SftpService mockSftpService;

    @Mock
    private Environment mockEnvironment;

    private final SftpConfig sftpConfig = new SftpConfig();
    
    private SftpHelperUtil classUnderTest;
    
    @BeforeEach
    void setup() {
        classUnderTest = new SftpHelperUtil(
            mockEntityManager,
            mockXhbConfigPropRepository,
            mockEnvironment,
            mockPddaMessageHelper,
            mockXhbClobRepository,
            mockXhbCourtRepository,
            mockXhbCourtRoomRepository,
            mockXhbCourtSiteRepository,
            mockXhbCaseRepository,
            mockXhbHearingRepository,
            mockXhbSittingRepository,
            mockXhbScheduledHearingRepository,
            mockXhbPublicNoticeRepository,
            mockXhbConfiguredPublicNoticeRepository
        );
    }
    
    private void mockAllRequiredConfigProperties() {
        mockProperty("PDDA_CP_EXCLUDED_COURT_IDS");
        mockProperty("PDDA_BAIS_CP_SFTP_USERNAME");
        mockProperty("PDDA_BAIS_CP_SFTP_PASSWORD");
        mockProperty("PDDA_BAIS_CP_SFTP_UPLOAD_LOCATION");
        mockProperty("PDDA_BAIS_SFTP_USERNAME");
        mockProperty("PDDA_BAIS_SFTP_PASSWORD");
        mockProperty("PDDA_BAIS_SFTP_UPLOAD_LOCATION");
        when(mockXhbConfigPropRepository.findByPropertyNameSafe("PDDA_BAIS_SFTP_HOSTNAME"))
            .thenReturn(getXhbConfigPropDaoListWithValue("PDDA_BAIS_SFTP_HOSTNAME", "localhost:22"));
        when(mockEnvironment.getProperty("PDDA_BAIS_SFTP_HOSTNAME")).thenReturn("localhost:22");

        mockProperty("pdda.bais_cp_sftp_username");
        mockProperty("pdda.bais_cp_sftp_password");
        mockProperty("pdda.bais_cp_sftp_upload_location");
        mockProperty("pdda.bais_sftp_username");
        mockProperty("pdda.bais_sftp_password");
        mockProperty("pdda.bais_sftp_upload_location");
        mockProperty("pdda.bais_sftp_hostname");

        when(mockEnvironment.getProperty("PDDA_CP_EXCLUDED_COURT_IDS")).thenReturn("1,2");
        when(mockEnvironment.getProperty("PDDA_BAIS_SFTP_HOSTNAME")).thenReturn("localhost:22");
        when(mockEnvironment.getProperty("pdda.bais_sftp_hostname")).thenReturn("localhost:22");
    }

    private void mockProperty(String propertyName) {
        when(mockXhbConfigPropRepository.findByPropertyNameSafe(propertyName))
            .thenReturn(getXhbConfigPropDaoList(propertyName));
        when(mockEnvironment.getProperty(propertyName))
            .thenReturn(propertyName.toLowerCase(Locale.getDefault()));
    }


    @Test
    void testPopulateSftpConfig() {
        mockAllRequiredConfigProperties();
        
        when(mockXhbConfigPropRepository.findByPropertyNameSafe(USE_KEY_VAULT_PROPERTIES))
            .thenReturn(getXhbConfigPropDaoList(USE_KEY_VAULT_PROPERTIES));
        
        sftpConfig.setHost(LOCALHOST_STRING);
        sftpConfig.setPort(22);

        classUnderTest.populateSftpConfig(sftpConfig.getPort());

        sftpConfig.setPort(0);
        classUnderTest.populateSftpConfig(sftpConfig.getPort());

        assertNotNull(sftpConfig.getHost(), NOT_NULL);
        assertNotNull(sftpConfig.getPort(), NOT_NULL);
    }


    @Test
    void testValidateAndSetHostAndPort() {
        // Setup
        sftpConfig.setHost(LOCALHOST_STRING);
        sftpConfig.setPort(22);
        // Run
        classUnderTest.validateAndSetHostAndPort(sftpConfig,
            sftpConfig.getHost() + ":" + sftpConfig.getPort());
        // Checks
        assertNotNull(sftpConfig.getHost(), NOT_NULL);
        assertNotNull(sftpConfig.getPort(), NOT_NULL);
    }


    @Test
    void testGetConfigParams() throws JSchException {
        mockAllRequiredConfigProperties();
        
        when(mockEnvironment.getProperty("PDDA_BAIS_SFTP_USERNAME")).thenReturn("this_user");
        when(mockEnvironment.getProperty("PDDA_BAIS_SFTP_PASSWORD")).thenReturn("this_pass");
        when(mockSftpConfigHelper.getJschSession(any(SftpConfig.class))).thenReturn(mockSftpConfig);
        when(mockPddaSftpHelper.createSession(any(), any(), any(), any())).thenReturn(mockSession);

        sftpConfig.setUseKeyVault(false);
        setupFiles();
        setupSftpConfig();

        SftpConfig result = classUnderTest.getConfigParams(sftpConfig);
        assertNotNull(result, NOT_NULL);
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
        sftpConfig.setHost(LOCALHOST_STRING);
        sftpConfig.setPort(sftpServer.getPort());
        sftpConfig.setCpUsername("cpUsername");
        sftpConfig.setCpPassword("cpPassword");
        sftpConfig.setCpRemoteFolder(TEST_SFTP_DIRECTORY);
        sftpConfig.setXhibitUsername("xhibitUsername");
        sftpConfig.setXhibitPassword("xhibitPassword");
        sftpConfig.setXhibitRemoteFolder(TEST_SFTP_DIRECTORY);
        sftpConfig.setActiveRemoteFolder(TEST_SFTP_DIRECTORY);
        sftpConfig.setCpExcludedCourtIds("123,456");

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


    private List<XhbConfigPropDao> getXhbConfigPropDaoList(String propertyName) {
        List<XhbConfigPropDao> result = new ArrayList<>();
        result.add(DummyServicesUtil.getXhbConfigPropDao(propertyName,
            propertyName.toLowerCase(Locale.getDefault())));
        return result;
    }
    
    private List<XhbConfigPropDao> getXhbConfigPropDaoListWithValue(String propertyName, String value) {
        List<XhbConfigPropDao> result = new ArrayList<>();
        result.add(DummyServicesUtil.getXhbConfigPropDao(propertyName, value));
        return result;
    }


}
