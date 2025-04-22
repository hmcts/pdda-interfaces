package uk.gov.hmcts.pdda.business.services.pdda;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicDisplayEvent;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.common.publicdisplay.jms.PublicDisplayNotifier;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@SuppressWarnings({"PMD.SingularField", "PMD.JUnitTestsShouldIncludeAssert", "PMD.LawOfDemeter",
    "PMD.CompareObjectsWithEquals"})
class XhibitPddaHelperTest {

    private EntityManager mockEntityManager;
    private Environment mockEnvironment;
    private XhbConfigPropRepository mockConfigPropRepo;
    private XhbClobRepository mockClobRepo;
    private XhbCourtRepository mockCourtRepo;
    private PddaMessageHelper mockPddaMessageHelper;
    private PddaSftpHelper mockPddaSftpHelper;
    private PublicDisplayNotifier mockPublicDisplayNotifier;

    private TestableXhibitPddaHelper helper;

    @BeforeEach
    void setUp() {
        mockEntityManager = mock(EntityManager.class);
        mockEnvironment = mock(Environment.class);
        mockConfigPropRepo = mock(XhbConfigPropRepository.class);
        mockClobRepo = mock(XhbClobRepository.class);
        mockCourtRepo = mock(XhbCourtRepository.class);
        mockPddaMessageHelper = mock(PddaMessageHelper.class);
        mockPddaSftpHelper = mock(PddaSftpHelper.class);
        mockPublicDisplayNotifier = mock(PublicDisplayNotifier.class);

        helper =
            new TestableXhibitPddaHelper(mockEntityManager, mockConfigPropRepo, mockEnvironment,
                mockPddaSftpHelper, mockPddaMessageHelper, mockClobRepo, mockCourtRepo);

        helper.setPublicDisplayNotifier(mockPublicDisplayNotifier);
    }

    @Test
    void testSendMessage() {
        PublicDisplayEvent mockEvent = mock(PublicDisplayEvent.class);

        helper.sendMessage(mockEvent);

        verify(mockPublicDisplayNotifier).sendMessage(mockEvent);
    }

    @Test
    void testGetClobRepository() {
        XhbClobRepository result = helper.getClobRepository();
        // Same mock instance should be returned
        assert result == mockClobRepo;
    }

    @Test
    void testGetCourtRepository() {
        XhbCourtRepository result = helper.getCourtRepository();
        assert result == mockCourtRepo;
    }

    @Test
    void testGetPddaMessageHelper() {
        PddaMessageHelper result = helper.getPddaMessageHelper();
        assert result == mockPddaMessageHelper;
    }

    @Test
    void testGetPddaSftpHelper() {
        PddaSftpHelper result = helper.getPddaSftpHelper();
        assert result == mockPddaSftpHelper;
    }

    // Add more tests for the other helper getters if needed

    // Define a minimal concrete subclass for testing
    @SuppressWarnings({"PMD.TestClassWithoutTestCases", "PMD.UselessOverridingMethod"})
    private static class TestableXhibitPddaHelper extends XhibitPddaHelper {
        public TestableXhibitPddaHelper(EntityManager em, XhbConfigPropRepository configRepo,
            Environment env, PddaSftpHelper sftpHelper, PddaMessageHelper msgHelper,
            XhbClobRepository clobRepo, XhbCourtRepository courtRepo) {
            super(em, configRepo, env, sftpHelper, msgHelper, clobRepo, courtRepo);
        }

        // Allow test injection for notifier
        @Override
        public void setPublicDisplayNotifier(PublicDisplayNotifier notifier) {
            super.setPublicDisplayNotifier(notifier);
        }

        @Override
        protected boolean isEntityManagerActive() {
            return true; // Simplify for test
        }
    }
}
