package uk.gov.hmcts.pdda.business.services.pdda.data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.DummyCaseUtil;
import uk.gov.hmcts.DummyCourtUtil;
import uk.gov.hmcts.DummyDefendantUtil;
import uk.gov.hmcts.DummyDisplayUtil;
import uk.gov.hmcts.DummyHearingUtil;
import uk.gov.hmcts.DummyJudgeUtil;
import uk.gov.hmcts.pdda.business.entities.xhbcase.XhbCaseDao;
import uk.gov.hmcts.pdda.business.entities.xhbcase.XhbCaseRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcrlivedisplay.XhbCrLiveDisplayDao;
import uk.gov.hmcts.pdda.business.entities.xhbcrlivedisplay.XhbCrLiveDisplayRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdefendantoncase.XhbDefendantOnCaseDao;
import uk.gov.hmcts.pdda.business.entities.xhbdefendantoncase.XhbDefendantOnCaseRepository;
import uk.gov.hmcts.pdda.business.entities.xhbhearing.XhbHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbhearing.XhbHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbhearinglist.XhbHearingListDao;
import uk.gov.hmcts.pdda.business.entities.xhbhearinglist.XhbHearingListRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrefhearingtype.XhbRefHearingTypeDao;
import uk.gov.hmcts.pdda.business.entities.xhbrefhearingtype.XhbRefHearingTypeRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrefjudge.XhbRefJudgeDao;
import uk.gov.hmcts.pdda.business.entities.xhbrefjudge.XhbRefJudgeRepository;
import uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant.XhbSchedHearingDefendantDao;
import uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant.XhbSchedHearingDefendantRepository;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingDao;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings({"PMD.ExcessiveImports", "PMD.CouplingBetweenObjects", "PMD.TooManyMethods"})
class FinderHelperTest {

    private static final String NOTNULL = "Result is Null";

    @Mock
    private RepositoryHelper mockRepositoryHelper;

    @InjectMocks
    private final FinderHelper classUnderTest = new FinderHelper(mockRepositoryHelper);

    @Test
    void testDefaultConstructor() {
        FinderHelper result = new FinderHelper();
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testFindCourtSite() {
        Mockito.when(mockRepositoryHelper.getXhbCourtSiteRepository())
            .thenReturn(Mockito.mock(XhbCourtSiteRepository.class));

        XhbCourtSiteDao dao = DummyCourtUtil.getXhbCourtSiteDao();
        Optional<XhbCourtSiteDao> result =
            classUnderTest.findCourtSite(dao.getCourtSiteName(), dao.getCourtSiteCode());
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testFindCourtRoom() {
        Mockito.when(mockRepositoryHelper.getXhbCourtRoomRepository())
            .thenReturn(Mockito.mock(XhbCourtRoomRepository.class));

        XhbCourtRoomDao dao = DummyCourtUtil.getXhbCourtRoomDao();
        Optional<XhbCourtRoomDao> result =
            classUnderTest.findCourtRoom(dao.getCourtSiteId(), dao.getCrestCourtRoomNo());
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testFindHearingList() {
        Mockito.when(mockRepositoryHelper.getXhbHearingListRepository())
            .thenReturn(Mockito.mock(XhbHearingListRepository.class));

        XhbHearingListDao dao = DummyHearingUtil.getXhbHearingListDao();
        Optional<XhbHearingListDao> result =
            classUnderTest.findHearingList(dao.getCourtId(), dao.getStatus(), dao.getStartDate());
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testFindSitting() {
        Mockito.when(mockRepositoryHelper.getXhbSittingRepository())
            .thenReturn(Mockito.mock(XhbSittingRepository.class));

        XhbSittingDao dao = DummyHearingUtil.getXhbSittingDao();
        Optional<XhbSittingDao> result = classUnderTest.findSitting(dao.getCourtSiteId(),
            dao.getCourtRoomId(), dao.getSittingTime());
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testFindCase() {
        Mockito.when(mockRepositoryHelper.getXhbCaseRepository())
            .thenReturn(Mockito.mock(XhbCaseRepository.class));

        XhbCaseDao dao = DummyCaseUtil.getXhbCaseDao();
        Optional<XhbCaseDao> result =
            classUnderTest.findCase(dao.getCourtId(), dao.getCaseType(), dao.getCaseNumber());
        assertNotNull(result, NOTNULL);
    }
    
    @Test
    void testFindJudge() {
        Mockito.when(mockRepositoryHelper.getXhbRefJudgeRepository())
            .thenReturn(Mockito.mock(XhbRefJudgeRepository.class));

        XhbRefJudgeDao dao = DummyJudgeUtil.getXhbRefJudgeDao();
        Optional<XhbRefJudgeDao> result =
            classUnderTest.findJudge(dao.getCourtId(), dao.getFirstname(), dao.getSurname());
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testFindDefendantOnCase() {
        Mockito.when(mockRepositoryHelper.getXhbDefendantOnCaseRepository())
            .thenReturn(Mockito.mock(XhbDefendantOnCaseRepository.class));

        XhbDefendantOnCaseDao dao = DummyDefendantUtil.getXhbDefendantOnCaseDao();
        Optional<XhbDefendantOnCaseDao> result =
            classUnderTest.findDefendantOnCase(dao.getCaseId(), dao.getDefendantId());
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testFindHearingType() {
        Mockito.when(mockRepositoryHelper.getXhbRefHearingTypeRepository())
            .thenReturn(Mockito.mock(XhbRefHearingTypeRepository.class));

        XhbRefHearingTypeDao dao = DummyHearingUtil.getXhbRefHearingTypeDao();
        Optional<XhbRefHearingTypeDao> result = classUnderTest.findHearingType(dao.getCourtId(),
            dao.getHearingTypeCode(), dao.getHearingTypeDesc(), dao.getCategory());
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testFindHearing() {
        Mockito.when(mockRepositoryHelper.getXhbHearingRepository())
            .thenReturn(Mockito.mock(XhbHearingRepository.class));

        XhbHearingDao dao = DummyHearingUtil.getXhbHearingDao();
        Optional<XhbHearingDao> result = classUnderTest.findHearing(dao.getCourtId(),
            dao.getCaseId(), dao.getHearingStartDate());
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testFindScheduledHearing() {
        Mockito.when(mockRepositoryHelper.getXhbScheduledHearingRepository())
            .thenReturn(Mockito.mock(XhbScheduledHearingRepository.class));

        XhbScheduledHearingDao dao = DummyHearingUtil.getXhbScheduledHearingDao();
        Optional<XhbScheduledHearingDao> result = classUnderTest
            .findScheduledHearing(dao.getSittingId(), dao.getHearingId(), dao.getNotBeforeTime());
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testFindSchedHearingDefendant() {
        Mockito.when(mockRepositoryHelper.getXhbSchedHearingDefendantRepository())
            .thenReturn(Mockito.mock(XhbSchedHearingDefendantRepository.class));

        XhbSchedHearingDefendantDao dao = DummyHearingUtil.getXhbSchedHearingDefendantDao();
        Optional<XhbSchedHearingDefendantDao> result = classUnderTest
            .findSchedHearingDefendant(dao.getScheduledHearingId(), dao.getDefendantOnCaseId());
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testFindCrLiveDisplay() {
        Mockito.when(mockRepositoryHelper.getXhbCrLiveDisplayRepository())
            .thenReturn(Mockito.mock(XhbCrLiveDisplayRepository.class));

        XhbCrLiveDisplayDao dao = DummyDisplayUtil.getXhbCrLiveDisplayDao();
        Optional<XhbCrLiveDisplayDao> result =
            classUnderTest.findCrLiveDisplay(dao.getCourtRoomId());
        assertNotNull(result, NOTNULL);
    }
}
