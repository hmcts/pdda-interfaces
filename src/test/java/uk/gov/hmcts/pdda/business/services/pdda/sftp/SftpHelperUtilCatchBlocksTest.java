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
import uk.gov.hmcts.pdda.business.services.pdda.PddaMessageHelper;

import static org.junit.jupiter.api.Assertions.assertFalse;
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
class SftpHelperUtilCatchBlocksTest {

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

    @Test
    void testCheckWhetherToUseKeyVault_whenConfigValueNull_returnsFalse() {
        SftpHelperUtil spy = spy(classUnderTest);
        // cause getMandatoryConfigValue to fail via getConfigValue -> null -> InvalidConfigException
        doReturn(null).when(spy).getConfigValue("USE_KEY_VAULT_PROPERTIES");

        boolean result = spy.checkWhetherToUseKeyVault();

        assertFalse(result);
    }

    @Test
    void testGetConfigParams_dbPath_missingConfig_setsErrorMsg() {
        SftpHelperUtil spy = spy(classUnderTest);

        // Return null for *most* DB config lookups, but return a valid host so parsing doesn't blow up.
        Answer<String> dbAnswer = invocation -> {
            String key = invocation.getArgument(0, String.class);
            if (key == null) {
                return null;
            }
            // If the keyed property name looks like the DB host name, return a valid host:port
            if (key.toLowerCase().contains("host")) {
                return "localhost:22";
            }
            return null; // simulate missing other properties
        };

        doAnswer(dbAnswer).when(spy).getConfigValue(anyString());

        SftpConfig cfg = new SftpConfig();
        cfg.setUseKeyVault(false);

        SftpConfig result = spy.getConfigParams(cfg);

        assertNotNull(result.getErrorMsg());
    }

    
    @Test
    void testGetConfigParams_kvPath_missingEnv_setsErrorMsg_withoutCrashing() {
        SftpHelperUtil spy = spy(classUnderTest);

        // Stub the mandatory env method to throw for everything (simulate missing values).
        doAnswer((Answer<String>) invocation -> {
            throw classUnderTest.new InvalidConfigException();
        }).when(spy).getMandatoryEnvValue(anyString());

        // Stub validateAndSetHostAndPort to avoid substring crash - no-op but keep any existing errorMsg.
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
        assertNotNull(result.getErrorMsg(), "KV-path missing env should set an error message");
        assertTrue(result.getErrorMsg().toLowerCase().contains("not found"),
            "Expected error message to indicate a missing property");
    }


    @Test
    void testValidateAndSetHostAndPort_invalidPort_setsErrorMsg() {
        SftpConfig cfg = new SftpConfig();

        SftpConfig result = classUnderTest.validateAndSetHostAndPort(cfg, "localhost:notanumber");

        assertNotNull(result.getErrorMsg());
    }

    @Test
    void testPopulateSftpConfig_testMode_ioExceptionHandled() {
        // use a very high/unlikely port to provoke connection failure inside getTestSftpConfig
        SftpConfig returned = classUnderTest.populateSftpConfig(65000);

        assertNotNull(returned);
        assertNull(returned.getSshClient());
    }
}
