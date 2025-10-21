package uk.gov.hmcts.pdda.business.services.pdda;

import jakarta.persistence.EntityManager;
import org.easymock.TestSubject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicDisplayEvent;
import uk.gov.hmcts.pdda.business.entities.xhbcase.XhbCaseRepository;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbhearing.XhbHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingRepository;
import uk.gov.hmcts.pdda.common.publicdisplay.jms.PublicDisplayNotifier;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@SuppressWarnings({"PMD"})
class XhibitPddaHelperTest {

    private static final String CLEAR_REPOSITORIES_MESSAGE = "Repositories have been cleared";
    private EntityManager mockEntityManager;
    private Environment mockEnvironment;
    private XhbConfigPropRepository mockConfigPropRepo;
    private XhbClobRepository mockClobRepo;
    private XhbCourtRepository mockCourtRepo;
    private XhbCaseRepository mockCaseRepo;
    private XhbHearingRepository mockHearingRepo;
    private XhbSittingRepository mockSittingRepo;
    private XhbScheduledHearingRepository mockScheduledHearingRepo;
    private PddaMessageHelper mockPddaMessageHelper;
    private PddaSftpHelper mockPddaSftpHelper;
    private PublicDisplayNotifier mockPublicDisplayNotifier;

    private TestableXhibitPddaHelper helper;

    @TestSubject
    protected XhibitPddaHelper classUnderTest;

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
                mockPddaSftpHelper, mockPddaMessageHelper, mockClobRepo, mockCourtRepo, mockCaseRepo,
                mockHearingRepo,  mockSittingRepo, mockScheduledHearingRepo);

        helper.setPublicDisplayNotifier(mockPublicDisplayNotifier);

        classUnderTest = helper;
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

    @SuppressWarnings({"PMD.UseExplicitTypes", "PMD.AvoidAccessibilityAlteration"})
    @Test
    void testClearRepositoriesSetsRepositoryToNull() throws Exception {
        // Given
        classUnderTest.clearRepositories();

        // Use reflection to check the private field
        var field = XhibitPddaHelper.class.getDeclaredField("clobRepository");
        field.setAccessible(true);
        Object repository = field.get(classUnderTest);

        // Then
        assertTrue(repository == null, CLEAR_REPOSITORIES_MESSAGE);

        // Use reflection to check the private field
        field = XhibitPddaHelper.class.getDeclaredField("courtRepository");
        field.setAccessible(true);
        repository = field.get(classUnderTest);

        // Then
        assertTrue(repository == null, CLEAR_REPOSITORIES_MESSAGE);
    }

    // Add more tests for the other helper getters if needed

    // Define a minimal concrete subclass for testing
    @SuppressWarnings({"PMD.TestClassWithoutTestCases", "PMD.UselessOverridingMethod"})
    private static class TestableXhibitPddaHelper extends XhibitPddaHelper {
        public TestableXhibitPddaHelper(EntityManager em, XhbConfigPropRepository configRepo,
            Environment env, PddaSftpHelper sftpHelper, PddaMessageHelper msgHelper,
            XhbClobRepository clobRepo, XhbCourtRepository courtRepo, XhbCaseRepository caseRepo,
            XhbHearingRepository hearingRepo, XhbSittingRepository sittingRepo,
            XhbScheduledHearingRepository scheduledHearingRepo) {
            super(em, configRepo, env, sftpHelper, msgHelper, clobRepo, courtRepo, caseRepo,
                hearingRepo, sittingRepo, scheduledHearingRepo);
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
