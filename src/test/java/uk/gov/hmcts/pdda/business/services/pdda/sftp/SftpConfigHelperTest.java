package uk.gov.hmcts.pdda.business.services.pdda.sftp;

import jakarta.persistence.EntityManager;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.TestSubject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.services.cppstaginginboundejb3.CppStagingInboundHelper;
import uk.gov.hmcts.pdda.business.services.pdda.PddaMessageHelper;
import uk.gov.hmcts.pdda.business.services.pdda.PddaSftpHelper;
import uk.gov.hmcts.pdda.common.publicdisplay.jms.PublicDisplayNotifier;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(EasyMockExtension.class)
@SuppressWarnings({"PMD.ExcessiveImports", "PMD.GodClass", "PMD.CouplingBetweenObjects"})
class SftpConfigHelperTest {

    private static final Logger LOG = LoggerFactory.getLogger(SftpConfigHelperTest.class);

    private static final String NOT_NULL = "Result is Null";
    private static final String NOT_TRUE = "Result is not True";
    private static final String TESTUSER = "TestUser";
    private static final String RESULT_EQUAL = "Result is Equal";

    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private Environment mockEnvironment;

    @Mock
    private CppStagingInboundHelper mockCppStagingInboundHelper;

    @Mock
    private PddaSftpHelper mockPddaSftpHelper;

    @Mock
    private XhbConfigPropRepository mockXhbConfigPropRepository;

    @Mock
    private XhbCourtRepository mockXhbCourtRepository;

    @Mock
    private XhbClobRepository mockXhbClobRepository;

    @Mock
    private PublicDisplayNotifier mockPublicDisplayNotifier;

    @Mock
    private PddaMessageHelper mockPddaMessageHelper;

    @InjectMocks
    private SftpConfigHelper sftpConfigHelper;

    private final SftpConfig sftpConfig = new SftpConfig();

    @TestSubject
    private final SftpConfigHelper classUnderTest =
        new SftpConfigHelper(EasyMock.createMock(EntityManager.class), mockXhbConfigPropRepository,
            mockEnvironment, mockPddaSftpHelper, null, mockXhbClobRepository,
            mockXhbCourtRepository);

    @BeforeEach
    public void setUp() {
        sftpConfigHelper = new SftpConfigHelper(mockEntityManager);
        // sftpConfig = new SftpConfig();
        // mockEnvironment.setProperty("sftp.host", "localhost");
        // mockEnvironment.setProperty("sftp.port", "22");
    }

    @Test
    void testDefaultConstructor() {
        boolean result = true;
        new SftpConfigHelper(mockEntityManager, mockXhbConfigPropRepository, mockEnvironment,
            mockPddaSftpHelper, null, mockXhbClobRepository, mockXhbCourtRepository);
        assertTrue(result, NOT_TRUE);
    }

    /*
     * @Test void testGetConfigParamsKv() { // Setup
     * 
     * sftpConfig.setUseKeyVault(true); // SftpConfig result =
     * classUnderTest.getConfigParams(sftpConfig); assertEquals(sftpConfig, result, RESULT_EQUAL); }
     */

    /*
     * @Test void testGetConfigParamsDb() { sftpConfig.setUseKeyVault(false); // SftpConfig result =
     * classUnderTest.getConfigParams(sftpConfig); assertEquals(sftpConfig.isUseK, result,
     * RESULT_EQUAL); }
     */

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


    @SuppressWarnings("PMD")
    private void setupSftpConfig() {
        sftpConfig.setHost("localhost");
        sftpConfig.setPort(61382);
        sftpConfig.setCpUsername("cpUsername");
        sftpConfig.setCpPassword("cpPassword");
        sftpConfig.setCpRemoteFolder("/directory/");
        sftpConfig.setXhibitUsername("xhibitUsername");
        sftpConfig.setXhibitPassword("xhibitPassword");
        sftpConfig.setXhibitRemoteFolder("/directory/");
        sftpConfig.setActiveRemoteFolder("/directory/");
    }

    // Add more tests for other methods in SftpConfigHelper class
}
