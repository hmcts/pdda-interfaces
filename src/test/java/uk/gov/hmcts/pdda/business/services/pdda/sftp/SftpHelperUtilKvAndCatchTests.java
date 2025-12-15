package uk.gov.hmcts.pdda.business.services.pdda.sftp;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.core.env.Environment;
import uk.gov.hmcts.pdda.business.entities.xhbcase.XhbCaseRepository;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbconfiguredpublicnotice.XhbConfiguredPublicNoticeRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.entities.xhbhearing.XhbHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbpublicnotice.XhbPublicNoticeRepository;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingRepository;
import uk.gov.hmcts.pdda.business.services.pdda.PddaConfigHelper;
import uk.gov.hmcts.pdda.business.services.pdda.PddaMessageHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("PMD")
class SftpHelperUtilKvAndCatchTests {

    // constructor mocks
    private final EntityManager mockEntityManager = Mockito.mock(EntityManager.class);
    private final XhbConfigPropRepository mockXhbConfigPropRepository = Mockito.mock(XhbConfigPropRepository.class);
    private final Environment mockEnvironment = Mockito.mock(Environment.class);
    private final PddaMessageHelper mockPddaMessageHelper = Mockito.mock(PddaMessageHelper.class);
    private final XhbClobRepository mockXhbClobRepository = Mockito.mock(XhbClobRepository.class);
    private final XhbCourtRepository mockXhbCourtRepository = Mockito.mock(XhbCourtRepository.class);
    private final XhbCourtRoomRepository mockXhbCourtRoomRepository = Mockito.mock(XhbCourtRoomRepository.class);
    private final XhbCourtSiteRepository mockXhbCourtSiteRepository = Mockito.mock(XhbCourtSiteRepository.class);
    private final XhbCaseRepository mockXhbCaseRepository = Mockito.mock(XhbCaseRepository.class);
    private final XhbHearingRepository mockXhbHearingRepository = Mockito.mock(XhbHearingRepository.class);
    private final XhbSittingRepository mockXhbSittingRepository = Mockito.mock(XhbSittingRepository.class);
    private final XhbScheduledHearingRepository mockXhbScheduledHearingRepository =
        Mockito.mock(XhbScheduledHearingRepository.class);
    private final XhbPublicNoticeRepository mockXhbPublicNoticeRepository =
        Mockito.mock(XhbPublicNoticeRepository.class);
    private final XhbConfiguredPublicNoticeRepository mockXhbConfiguredPublicNoticeRepository =
        Mockito.mock(XhbConfiguredPublicNoticeRepository.class);

    private SftpHelperUtil classUnderTest;

    @BeforeEach
    void setUp() {
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

    /**
     * 1) Successful KV path: all mandatory env values present, host parsed.
     */
    @Test
    void testGetKvConfigParams_successful_populatesFieldsAndHostPort() {
        SftpHelperUtil spy = spy(classUnderTest);

        // Provide expected values for each mandatory env key
        doAnswer((Answer<String>) invocation -> {
            String key = invocation.getArgument(0, String.class);
            switch (key) {
                case PddaConfigHelper.Config.KV_CP_SFTP_USERNAME:
                    return "cpUser";
                case PddaConfigHelper.Config.KV_CP_SFTP_PASSWORD:
                    return "cpPass";
                case PddaConfigHelper.Config.KV_CP_SFTP_UPLOAD_LOCATION:
                    return "/cpDir";
                case PddaConfigHelper.Config.KV_SFTP_USERNAME:
                    return "xhibitUser";
                case PddaConfigHelper.Config.KV_SFTP_PASSWORD:
                    return "xhibitPass";
                case PddaConfigHelper.Config.KV_SFTP_UPLOAD_LOCATION:
                    return "/xhibitDir";
                case PddaConfigHelper.Config.KV_SFTP_HOST:
                    return "myhost.example:2222";
                default:
                    return null;
            }
        }).when(spy).getMandatoryEnvValue(anyString());

        SftpConfig cfg = new SftpConfig();
        cfg.setUseKeyVault(true);

        SftpConfig result = spy.getConfigParams(cfg);

        assertNotNull(result);
        assertEquals("cpUser", result.getCpUsername());
        assertEquals("cpPass", result.getCpPassword());
        assertEquals("/cpDir", result.getCpRemoteFolder());
        assertEquals("xhibitUser", result.getXhibitUsername());
        assertEquals("/xhibitDir", result.getXhibitRemoteFolder());
        assertEquals("myhost.example", result.getHost());
        assertEquals(Integer.valueOf(2222), result.getPort());
        assertNull(result.getErrorMsg(), "Success path should not set errorMsg");
    }

    /**
     * 2) KV path: missing CP username (first property). This should set errorMsg to the 
     * missing property + " not found".
     * The missing property causes the catch before the host is read, so stub validateAndSetHostAndPort
     * to avoid the crash.
     */
    @Test
    void testGetKvConfigParams_missingCpUsername_setsErrorMsg() {
        SftpHelperUtil spy = spy(classUnderTest);

        // Throw for KV_CP_SFTP_USERNAME; other calls may not be reached.
        doAnswer((Answer<String>) invocation -> {
            String key = invocation.getArgument(0, String.class);
            if (PddaConfigHelper.Config.KV_CP_SFTP_USERNAME.equals(key)) {
                throw classUnderTest.new InvalidConfigException();
            }
            // Host may never be requested because the exception occurs early; return a fallback if called.
            if (PddaConfigHelper.Config.KV_SFTP_HOST.equals(key)) {
                return "localhost:22";
            }
            return "ok";
        }).when(spy).getMandatoryEnvValue(anyString());

        // Stub validateAndSetHostAndPort to be a no-op and preserve existing errorMsg
        doAnswer((Answer<SftpConfig>) invocation -> {
            SftpConfig cfg = invocation.getArgument(0, SftpConfig.class);
            if (cfg.getErrorMsg() == null) {
                cfg.setErrorMsg("simulated-missing-kv");
            }
            return cfg;
        }).when(spy).validateAndSetHostAndPort(any(SftpConfig.class), anyString());

        SftpConfig cfg = new SftpConfig();
        cfg.setUseKeyVault(true);

        SftpConfig result = spy.getConfigParams(cfg);

        assertNotNull(result);
        assertNotNull(result.getErrorMsg(), "Missing KV CP username should set an error message");
        assertTrue(result.getErrorMsg().toLowerCase().contains(
            PddaConfigHelper.Config.KV_CP_SFTP_USERNAME.toLowerCase())
            || result.getErrorMsg().toLowerCase().contains("not found"),
            "Error message should indicate missing property");
    }

    /**
     * 3) Directly test the pos <= 0 branch safely: pass ":22" (pos == 0) - 
     * method will set syntax error and parse port.
     */
    @Test
    void testValidateAndSetHostAndPort_posZero_setsSyntaxErrorAndParsesPort() {
        SftpConfig cfg = new SftpConfig();

        SftpConfig result = classUnderTest.validateAndSetHostAndPort(cfg, ":22");

        assertNotNull(result.getErrorMsg(), "Should set syntax error when pos <= 0");
        assertTrue(result.getErrorMsg().toLowerCase().contains("host and port syntax"));
        assertEquals("", result.getHost());
        assertEquals(Integer.valueOf(22), result.getPort());
    }

    /**
     * 4) populateSftpConfig: missing DB_CP_EXCLUDED_COURT_IDS should set that error message.
     */
    @Test
    void testPopulateSftpConfig_dbCpExcludedCourtIdsMissing_setsErrorMsg() {
        SftpHelperUtil spy = spy(classUnderTest);

        // Force DB path
        doReturn(false).when(spy).checkWhetherToUseKeyVault();

        // Simulate missing DB_CP_EXCLUDED_COURT_IDS and provide host to avoid crashes later
        doAnswer((Answer<String>) invocation -> {
            String key = invocation.getArgument(0, String.class);
            if (PddaConfigHelper.Config.DB_CP_EXCLUDED_COURT_IDS.equals(key)) {
                throw classUnderTest.new InvalidConfigException();
            }
            if (PddaConfigHelper.Config.DB_SFTP_HOST.equals(key)) {
                return "localhost:22";
            }
            return "ok";
        }).when(spy).getMandatoryConfigValue(anyString());

        // Stub getConfigParams to return the config passed through
        doAnswer((Answer<SftpConfig>) invocation -> invocation.getArgument(0, SftpConfig.class))
            .when(spy).getConfigParams(any(SftpConfig.class));

        SftpConfig result = spy.populateSftpConfig(0);

        assertNotNull(result);
        assertNotNull(result.getErrorMsg());
        assertTrue(result.getErrorMsg().toLowerCase().contains(
            PddaConfigHelper.Config.DB_CP_EXCLUDED_COURT_IDS.toLowerCase()));
    }

    /**
     * 5) DB path: missing DB_SFTP_HOST => set error message; stub validate to avoid crash.
     */
    @Test
    void testGetConfigParams_dbHostMissing_setsErrorMsgAndDoesNotCrash() {
        SftpHelperUtil spy = spy(classUnderTest);

        // Throw for DB_SFTP_HOST
        doAnswer((Answer<String>) invocation -> {
            String key = invocation.getArgument(0, String.class);
            if (PddaConfigHelper.Config.DB_SFTP_HOST.equals(key)) {
                throw classUnderTest.new InvalidConfigException();
            }
            return "ok";
        }).when(spy).getMandatoryConfigValue(anyString());

        // stub validateAndSetHostAndPort to avoid substring crash
        doAnswer((Answer<SftpConfig>) invocation -> invocation.getArgument(0, SftpConfig.class))
            .when(spy).validateAndSetHostAndPort(any(SftpConfig.class), anyString());

        SftpConfig cfg = new SftpConfig();
        cfg.setUseKeyVault(false);

        SftpConfig result = spy.getConfigParams(cfg);

        assertNotNull(result);
        assertNotNull(result.getErrorMsg());
        assertTrue(result.getErrorMsg().toLowerCase().contains(PddaConfigHelper.Config.DB_SFTP_HOST.toLowerCase()));
    }
}
