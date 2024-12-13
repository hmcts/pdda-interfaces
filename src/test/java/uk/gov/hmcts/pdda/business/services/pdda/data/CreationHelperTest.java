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
import uk.gov.hmcts.pdda.business.entities.xhbcase.XhbCaseDao;
import uk.gov.hmcts.pdda.business.entities.xhbcase.XhbCaseRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcrlivedisplay.XhbCrLiveDisplayDao;
import uk.gov.hmcts.pdda.business.entities.xhbcrlivedisplay.XhbCrLiveDisplayRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdefendant.XhbDefendantDao;
import uk.gov.hmcts.pdda.business.entities.xhbdefendant.XhbDefendantRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdefendantoncase.XhbDefendantOnCaseDao;
import uk.gov.hmcts.pdda.business.entities.xhbdefendantoncase.XhbDefendantOnCaseRepository;
import uk.gov.hmcts.pdda.business.entities.xhbhearing.XhbHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbhearing.XhbHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbhearinglist.XhbHearingListDao;
import uk.gov.hmcts.pdda.business.entities.xhbhearinglist.XhbHearingListRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrefhearingtype.XhbRefHearingTypeDao;
import uk.gov.hmcts.pdda.business.entities.xhbrefhearingtype.XhbRefHearingTypeRepository;
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
@SuppressWarnings({"PMD.TooManyMethods", "PMD.ExcessiveImports", "PMD.CouplingBetweenObjects"})
class CreationHelperTest {

    private static final String NOTNULL = "Result is Null";

    @Mock
    private RepositoryHelper mockRepositoryHelper;

    @InjectMocks
    private final CreationHelper classUnderTest = new CreationHelper(mockRepositoryHelper);

    @Test
    void testDefaultConstructor() {
        CreationHelper localClassUnderTest = new CreationHelper() {
            @Override
            public RepositoryHelper getRepositoryHelper() {
                return super.getRepositoryHelper();
            }
        };
        RepositoryHelper result = localClassUnderTest.getRepositoryHelper();
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testCreateCourtSite() {
        Mockito.when(mockRepositoryHelper.getXhbCourtSiteRepository())
            .thenReturn(Mockito.mock(XhbCourtSiteRepository.class));

        XhbCourtSiteDao dao = DummyCourtUtil.getXhbCourtSiteDao();
        Optional<XhbCourtSiteDao> result = classUnderTest.createCourtSite(dao.getCourtId(),
            dao.getCourtSiteName(), dao.getCourtSiteCode());
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testCreateCourtRoom() {
        Mockito.when(mockRepositoryHelper.getXhbCourtRoomRepository())
            .thenReturn(Mockito.mock(XhbCourtRoomRepository.class));

        XhbCourtRoomDao dao = DummyCourtUtil.getXhbCourtRoomDao();
        Optional<XhbCourtRoomDao> result = classUnderTest.createCourtRoom(dao.getCourtSiteId(),
            dao.getCourtRoomName(), dao.getDescription(), dao.getCrestCourtRoomNo());
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testCreateHearingList() {
        Mockito.when(mockRepositoryHelper.getXhbHearingListRepository())
            .thenReturn(Mockito.mock(XhbHearingListRepository.class));

        XhbHearingListDao dao = DummyHearingUtil.getXhbHearingListDao();
        Optional<XhbHearingListDao> result =
            classUnderTest.createHearingList(dao.getCourtId(), dao.getCrestListId(),
                dao.getListType(), dao.getStatus(), dao.getStartDate(), dao.getPublishedTime(),
                dao.getPrintReference(), dao.getEditionNo(), dao.getListCourtType());
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testCreateSitting() {
        Mockito.when(mockRepositoryHelper.getXhbSittingRepository())
            .thenReturn(Mockito.mock(XhbSittingRepository.class));

        XhbSittingDao dao = DummyHearingUtil.getXhbSittingDao();
        Optional<XhbSittingDao> result = classUnderTest.createSitting(dao.getCourtSiteId(),
            dao.getCourtRoomId(), dao.getIsFloating(), dao.getSittingTime(), dao.getListId());
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testCreateCase() {
        Mockito.when(mockRepositoryHelper.getXhbCaseRepository())
            .thenReturn(Mockito.mock(XhbCaseRepository.class));

        XhbCaseDao dao = DummyCaseUtil.getXhbCaseDao();
        Optional<XhbCaseDao> result =
            classUnderTest.createCase(dao.getCourtId(), dao.getCaseType(), dao.getCaseNumber());
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testCreateDefendantOnCase() {
        Mockito.when(mockRepositoryHelper.getXhbDefendantOnCaseRepository())
            .thenReturn(Mockito.mock(XhbDefendantOnCaseRepository.class));

        XhbDefendantOnCaseDao dao = DummyDefendantUtil.getXhbDefendantOnCaseDao();
        Optional<XhbDefendantOnCaseDao> result =
            classUnderTest.createDefendantOnCase(dao.getCaseId(), dao.getDefendantId());
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testCreateDefendant() {
        Mockito.when(mockRepositoryHelper.getXhbDefendantRepository())
            .thenReturn(Mockito.mock(XhbDefendantRepository.class));

        XhbDefendantDao dao = DummyDefendantUtil.getXhbDefendantDao();
        Optional<XhbDefendantDao> result =
            classUnderTest.createDefendant(dao.getCourtId(), dao.getFirstName(),
                dao.getMiddleName(), dao.getSurname(), dao.getGender(), dao.getDateOfBirth());
        assertNotNull(result, NOTNULL);
    }


    @Test
    void testCreateHearingType() {
        Mockito.when(mockRepositoryHelper.getXhbRefHearingTypeRepository())
            .thenReturn(Mockito.mock(XhbRefHearingTypeRepository.class));

        XhbRefHearingTypeDao dao = DummyHearingUtil.getXhbRefHearingTypeDao();
        Optional<XhbRefHearingTypeDao> result = classUnderTest.createHearingType(dao.getCourtId(),
            dao.getHearingTypeCode(), dao.getHearingTypeDesc(), dao.getCategory());
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testCreateHearing() {
        Mockito.when(mockRepositoryHelper.getXhbHearingRepository())
            .thenReturn(Mockito.mock(XhbHearingRepository.class));

        XhbHearingDao dao = DummyHearingUtil.getXhbHearingDao();
        Optional<XhbHearingDao> result = classUnderTest.createHearing(dao.getCourtId(),
            dao.getCaseId(), dao.getRefHearingTypeId(), dao.getHearingStartDate());
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testCreateScheduledHearing() {
        Mockito.when(mockRepositoryHelper.getXhbScheduledHearingRepository())
            .thenReturn(Mockito.mock(XhbScheduledHearingRepository.class));

        XhbScheduledHearingDao dao = DummyHearingUtil.getXhbScheduledHearingDao();
        Optional<XhbScheduledHearingDao> result = classUnderTest
            .createScheduledHearing(dao.getSittingId(), dao.getHearingId(), dao.getNotBeforeTime());
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testCreateSchedHearingDefendant() {
        Mockito.when(mockRepositoryHelper.getXhbSchedHearingDefendantRepository())
            .thenReturn(Mockito.mock(XhbSchedHearingDefendantRepository.class));

        XhbSchedHearingDefendantDao dao = DummyHearingUtil.getXhbSchedHearingDefendantDao();
        Optional<XhbSchedHearingDefendantDao> result = classUnderTest
            .createSchedHearingDefendant(dao.getScheduledHearingId(), dao.getDefendantOnCaseId());
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testCreateCrLiveDisplay() {
        Mockito.when(mockRepositoryHelper.getXhbCrLiveDisplayRepository())
            .thenReturn(Mockito.mock(XhbCrLiveDisplayRepository.class));

        XhbCrLiveDisplayDao dao = DummyDisplayUtil.getXhbCrLiveDisplayDao();
        Optional<XhbCrLiveDisplayDao> result = classUnderTest.createCrLiveDisplay(
            dao.getCourtRoomId(), dao.getScheduledHearingId(), dao.getTimeStatusSet());
        assertNotNull(result, NOTNULL);
        result = classUnderTest.updateCrLiveDisplay(dao);
        assertNotNull(result, NOTNULL);
    }
}
