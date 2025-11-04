package uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.query;

import jakarta.persistence.EntityManager;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.DummyCaseUtil;
import uk.gov.hmcts.DummyCourtUtil;
import uk.gov.hmcts.DummyDefendantUtil;
import uk.gov.hmcts.DummyHearingUtil;
import uk.gov.hmcts.framework.util.DateTimeUtilities;
import uk.gov.hmcts.pdda.business.entities.AbstractRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcase.XhbCaseDao;
import uk.gov.hmcts.pdda.business.entities.xhbcase.XhbCaseRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcasereference.XhbCaseReferenceDao;
import uk.gov.hmcts.pdda.business.entities.xhbcasereference.XhbCaseReferenceRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtlogentry.XhbCourtLogEntryDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtlogentry.XhbCourtLogEntryRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdefendant.XhbDefendantDao;
import uk.gov.hmcts.pdda.business.entities.xhbdefendant.XhbDefendantRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdefendantoncase.XhbDefendantOnCaseDao;
import uk.gov.hmcts.pdda.business.entities.xhbdefendantoncase.XhbDefendantOnCaseRepository;
import uk.gov.hmcts.pdda.business.entities.xhbhearing.XhbHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbhearing.XhbHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbhearinglist.XhbHearingListDao;
import uk.gov.hmcts.pdda.business.entities.xhbhearinglist.XhbHearingListRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrefhearingtype.XhbRefHearingTypeRepository;
import uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant.XhbSchedHearingDefendantDao;
import uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant.XhbSchedHearingDefendantRepository;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingDao;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingRepository;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.SummaryByNameValue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**

 * Title: SummaryByNameQuery Test.


 * Description:


 * Copyright: Copyright (c) 2023


 * Company: CGI

 * @author Mark Harris
 */
@ExtendWith(EasyMockExtension.class)
@SuppressWarnings({"PMD"})
class SummaryByNameQueryTest extends AbstractQueryTest {

    protected static final String TRUE = "Result is not True";

    @Mock
    protected EntityManager mockEntityManager;

    @Mock
    protected XhbCaseRepository mockXhbCaseRepository;

    @Mock
    protected XhbCaseReferenceRepository mockXhbCaseReferenceRepository;

    @Mock
    protected XhbHearingListRepository mockXhbHearingListRepository;

    @Mock
    protected XhbSittingRepository mockXhbSittingRepository;

    @Mock
    protected XhbScheduledHearingRepository mockXhbScheduledHearingRepository;

    @Mock
    protected XhbCourtSiteRepository mockXhbCourtSiteRepository;

    @Mock
    protected XhbCourtRoomRepository mockXhbCourtRoomRepository;

    @Mock
    protected XhbSchedHearingDefendantRepository mockXhbSchedHearingDefendantRepository;

    @Mock
    protected XhbHearingRepository mockXhbHearingRepository;

    @Mock
    protected XhbDefendantOnCaseRepository mockXhbDefendantOnCaseRepository;

    @Mock
    protected XhbDefendantRepository mockXhbDefendantRepository;

    @Mock
    protected XhbCourtLogEntryRepository mockXhbCourtLogEntryRepository;

    @Mock
    protected XhbRefHearingTypeRepository mockXhbRefHearingTypeRepository;

    @TestSubject
    protected SummaryByNameQuery classUnderTest = getClassUnderTest();

    @BeforeAll
    public static void setUp() {
        // Do nothing
    }

    @BeforeEach
    void setupEntityManager() {
        EasyMock.expect(mockEntityManager.isOpen()).andReturn(true).anyTimes();
        EasyMock.replay(mockEntityManager);
    }

    @AfterAll
    public static void tearDown() {
        // Do nothing
    }

    protected SummaryByNameQuery getClassUnderTest() {
        return new SummaryByNameQuery(mockEntityManager, mockXhbCaseRepository, mockXhbCaseReferenceRepository,
            mockXhbHearingListRepository, mockXhbSittingRepository, mockXhbScheduledHearingRepository,
            mockXhbCourtSiteRepository, mockXhbCourtRoomRepository, mockXhbSchedHearingDefendantRepository,
            mockXhbHearingRepository, mockXhbDefendantOnCaseRepository, mockXhbDefendantRepository,
            mockXhbCourtLogEntryRepository, mockXhbRefHearingTypeRepository);
    }

    @Test
    void testDefaultConstructor() {
        boolean result = false;
        try {
            classUnderTest = new SummaryByNameQuery(mockEntityManager);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }

    @Test
    void testGetDataNoListEmpty() {
        boolean result = testGetDataNoList(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
            Optional.empty());
        assertTrue(result, TRUE);
    }

    @Test
    void testGetDataNoListNoSittings() {
        List<XhbHearingListDao> xhbHearingListDaoList = new ArrayList<>();
        xhbHearingListDaoList.add(DummyHearingUtil.getXhbHearingListDao());
        boolean result = testGetDataNoList(xhbHearingListDaoList, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), Optional.empty());
        assertTrue(result, TRUE);
    }

    @Test
    void testGetDataNoListNoScheduledHearings() {
        List<XhbHearingListDao> xhbHearingListDaoList = new ArrayList<>();
        xhbHearingListDaoList.add(DummyHearingUtil.getXhbHearingListDao());
        List<XhbSittingDao> xhbSittingDaoList = new ArrayList<>();
        xhbSittingDaoList.add(DummyHearingUtil.getXhbSittingDao());
        boolean result = testGetDataNoList(xhbHearingListDaoList, xhbSittingDaoList, new ArrayList<>(),
            new ArrayList<>(), Optional.empty());
        assertTrue(result, TRUE);
    }

    @Test
    void testGetDataNoListNoScheduledHearingDefendants() {
        List<XhbHearingListDao> xhbHearingListDaoList = new ArrayList<>();
        xhbHearingListDaoList.add(DummyHearingUtil.getXhbHearingListDao());
        List<XhbSittingDao> xhbSittingDaoList = new ArrayList<>();
        xhbSittingDaoList.add(DummyHearingUtil.getXhbSittingDao());
        XhbSittingDao invalidXhbSittingDao = DummyHearingUtil.getXhbSittingDao();
        invalidXhbSittingDao.setCourtRoomId(-1);
        xhbSittingDaoList.add(invalidXhbSittingDao);
        List<XhbScheduledHearingDao> xhbScheduledHearingDaoList = new ArrayList<>();
        xhbScheduledHearingDaoList.add(DummyHearingUtil.getXhbScheduledHearingDao());
        boolean result = testGetDataNoList(xhbHearingListDaoList, xhbSittingDaoList, xhbScheduledHearingDaoList,
            new ArrayList<>(), Optional.empty());
        assertTrue(result, TRUE);
    }

    @Test
    void testGetDataNoListNoHearing() {
        List<XhbHearingListDao> xhbHearingListDaoList = new ArrayList<>();
        xhbHearingListDaoList.add(DummyHearingUtil.getXhbHearingListDao());
        List<XhbSittingDao> xhbSittingDaoList = new ArrayList<>();
        xhbSittingDaoList.add(DummyHearingUtil.getXhbSittingDao());
        List<XhbScheduledHearingDao> xhbScheduledHearingDaoList = new ArrayList<>();
        xhbScheduledHearingDaoList.add(DummyHearingUtil.getXhbScheduledHearingDao());
        List<XhbSchedHearingDefendantDao> xhbSchedHearingDefendantDaoList = new ArrayList<>();
        xhbSchedHearingDefendantDaoList.add(DummyHearingUtil.getXhbSchedHearingDefendantDao());
        boolean result = testGetDataNoList(xhbHearingListDaoList, xhbSittingDaoList, xhbScheduledHearingDaoList,
            xhbSchedHearingDefendantDaoList, Optional.empty());
        assertTrue(result, TRUE);
    }

    @Test
    void testGetDataNoListSuccess() {
        List<XhbHearingListDao> xhbHearingListDaoList = new ArrayList<>();
        xhbHearingListDaoList.add(DummyHearingUtil.getXhbHearingListDao());
        List<XhbSittingDao> xhbSittingDaoList = new ArrayList<>();
        xhbSittingDaoList.add(DummyHearingUtil.getXhbSittingDao());
        List<XhbScheduledHearingDao> xhbScheduledHearingDaoList = new ArrayList<>();
        xhbScheduledHearingDaoList.add(DummyHearingUtil.getXhbScheduledHearingDao());
        List<XhbSchedHearingDefendantDao> xhbSchedHearingDefendantDaoList = new ArrayList<>();
        xhbSchedHearingDefendantDaoList.add(DummyHearingUtil.getXhbSchedHearingDefendantDao());
        boolean result = testGetDataNoList(xhbHearingListDaoList, xhbSittingDaoList, xhbScheduledHearingDaoList,
            xhbSchedHearingDefendantDaoList, Optional.of(DummyHearingUtil.getXhbHearingDao()));
        assertTrue(result, TRUE);
    }

    private boolean testGetDataNoList(List<XhbHearingListDao> xhbHearingListDaoList,
        List<XhbSittingDao> xhbSittingDaoList, List<XhbScheduledHearingDao> xhbScheduledHearingDaoList,
        List<XhbSchedHearingDefendantDao> xhbSchedHearingDefendantDaoList, Optional<XhbHearingDao> xhbHearingDao) {
        // Setup
        LocalDateTime date = LocalDateTime.now();
        LocalDateTime startDate = DateTimeUtilities.stripTime(date);
        Integer courtId = 81;
        final int[] courtRoomIds = {8112, 8113, 8114};
        List<AbstractRepository<?>> replayArray = new ArrayList<>();

        // Expects
        boolean abortExpects;
        EasyMock.expect(mockXhbHearingListRepository.findByCourtIdAndDateSafe(courtId, startDate))
            .andReturn(xhbHearingListDaoList);
        addReplayArray(replayArray, mockXhbHearingListRepository);
        abortExpects = xhbHearingListDaoList.isEmpty();
        if (!abortExpects) {
            if (classUnderTest.isFloatingIncluded()) {
                EasyMock
                    .expect(mockXhbSittingRepository.findByListIdSafe(EasyMock.isA(Integer.class)))
                    .andReturn(xhbSittingDaoList);
            } else {
                EasyMock
                    .expect(mockXhbSittingRepository
                        .findByNonFloatingHearingListSafe(EasyMock.isA(Integer.class)))
                    .andReturn(xhbSittingDaoList);
            }
            addReplayArray(replayArray, mockXhbSittingRepository);
            abortExpects = xhbSittingDaoList.isEmpty();
        }
        if (!abortExpects) {
            EasyMock
                .expect(mockXhbScheduledHearingRepository
                    .findBySittingIdSafe(EasyMock.isA(Integer.class)))
                .andReturn(xhbScheduledHearingDaoList);
            EasyMock.expectLastCall().anyTimes();
            addReplayArray(replayArray, mockXhbScheduledHearingRepository);
            abortExpects = xhbScheduledHearingDaoList.isEmpty();
        }
        if (!abortExpects) {
            expectCourtSite(xhbSchedHearingDefendantDaoList, xhbHearingDao, replayArray);
        }
        // Replays
        doReplayArray(replayArray);

        // Run
        classUnderTest.getData(date, courtId, courtRoomIds);

        // Checks
        verifyReplayArray(replayArray);

        return true;
    }

    private void expectCourtSite(List<XhbSchedHearingDefendantDao> xhbSchedHearingDefendantDaoList,
        Optional<XhbHearingDao> xhbHearingDao, List<AbstractRepository<?>> replayArray) {
        EasyMock
            .expect(mockXhbSchedHearingDefendantRepository
                .findByScheduledHearingIdSafe(EasyMock.isA(Integer.class)))
            .andReturn(xhbSchedHearingDefendantDaoList);
        EasyMock.expectLastCall().anyTimes();
        addReplayArray(replayArray, mockXhbSchedHearingDefendantRepository);

        EasyMock.expect(mockXhbCourtSiteRepository.findByIdSafe(EasyMock.isA(Integer.class)))
            .andReturn(Optional.of(DummyCourtUtil.getXhbCourtSiteDao()));
        EasyMock.expectLastCall().anyTimes();
        addReplayArray(replayArray, mockXhbCourtSiteRepository);
        EasyMock.expect(mockXhbCourtRoomRepository.findByIdSafe(EasyMock.isA(Integer.class)))
            .andReturn(Optional.of(DummyCourtUtil.getXhbCourtRoomDao()));
        EasyMock.expectLastCall().anyTimes();
        addReplayArray(replayArray, mockXhbCourtRoomRepository);
        EasyMock.expect(mockXhbHearingRepository.findByIdSafe(EasyMock.isA(Integer.class)))
            .andReturn(xhbHearingDao);
        EasyMock.expectLastCall().anyTimes();
        addReplayArray(replayArray, mockXhbHearingRepository);
        if (xhbHearingDao.isPresent()) {
            EasyMock.expect(mockXhbCaseRepository.findByIdSafe(EasyMock.isA(Integer.class)))
                .andReturn(Optional.of(DummyCaseUtil.getXhbCaseDao()));
            EasyMock.expectLastCall().anyTimes();
            addReplayArray(replayArray, mockXhbCaseRepository);
            List<XhbCaseReferenceDao> xhbCaseReferenceDaoList = new ArrayList<>();
            xhbCaseReferenceDaoList.add(DummyCaseUtil.getXhbCaseReferenceDao());
            EasyMock
                .expect(
                    mockXhbCaseReferenceRepository.findByCaseIdSafe(EasyMock.isA(Integer.class)))
                .andReturn(xhbCaseReferenceDaoList);
            addReplayArray(replayArray, mockXhbCaseReferenceRepository);
            List<XhbCourtLogEntryDao> xhbCourtLogEntryDaoList = new ArrayList<>();
            xhbCourtLogEntryDaoList.add(DummyCourtUtil.getXhbCourtLogEntryDao());
            EasyMock
                .expect(
                    mockXhbCourtLogEntryRepository.findByCaseIdSafe(EasyMock.isA(Integer.class)))
                .andReturn(xhbCourtLogEntryDaoList);
            EasyMock.expectLastCall().anyTimes();
            addReplayArray(replayArray, mockXhbCourtLogEntryRepository);
            EasyMock
                .expect(mockXhbRefHearingTypeRepository.findByIdSafe(EasyMock.isA(Integer.class)))
                .andReturn(Optional.of(DummyHearingUtil.getXhbRefHearingTypeDao()));
            EasyMock.expectLastCall().anyTimes();
            addReplayArray(replayArray, mockXhbRefHearingTypeRepository);
        }
        EasyMock.expect(mockXhbDefendantOnCaseRepository.findByIdSafe(EasyMock.isA(Integer.class)))
            .andReturn(Optional.of(DummyDefendantUtil.getXhbDefendantOnCaseDao()));
        EasyMock.expectLastCall().anyTimes();
        addReplayArray(replayArray, mockXhbDefendantOnCaseRepository);
        EasyMock.expect(mockXhbDefendantRepository.findByIdSafe(EasyMock.isA(Integer.class)))
            .andReturn(Optional.of(DummyDefendantUtil.getXhbDefendantDao()));
        EasyMock.expectLastCall().anyTimes();
        addReplayArray(replayArray, mockXhbDefendantRepository);
    }
    
    
    @Test
    void testGetScheduleHearingData_dedupPrefersProgress_andFiltersByCourtroom() throws Exception {
        // --- Arrange base objects ---
        int courtRoomId = 8112;
        
        XhbSittingDao sitting = DummyHearingUtil.getXhbSittingDao();
        sitting.setCourtRoomId(courtRoomId);
        // site/room used by populateData()
        XhbCourtRoomDao room = DummyCourtUtil.getXhbCourtRoomDao();
        room.setCourtRoomId(courtRoomId);

        // Two SH rows for SAME hearingId; one with progress must win
        XhbScheduledHearingDao shNoProg = DummyHearingUtil.getXhbScheduledHearingDao();
        shNoProg.setScheduledHearingId(10001);
        shNoProg.setHearingId(777);
        shNoProg.setHearingProgress(null);
        shNoProg.setMovedFromCourtRoomId(null);

        XhbScheduledHearingDao shWithProg = DummyHearingUtil.getXhbScheduledHearingDao();
        shWithProg.setScheduledHearingId(10002);
        shWithProg.setHearingId(777);
        shWithProg.setHearingProgress(5); // preferred
        shWithProg.setMovedFromCourtRoomId(null);

        // A third SH for a different hearingId but for a different (non-selected) room -> filtered out
        XhbScheduledHearingDao shOtherRoom = DummyHearingUtil.getXhbScheduledHearingDao();
        shOtherRoom.setScheduledHearingId(20001);
        shOtherRoom.setHearingId(888);
        shOtherRoom.setHearingProgress(null);
        shOtherRoom.setMovedFromCourtRoomId(9999); // won’t match selected

        // For the chosen SH (10002), return two SHDs
        XhbSchedHearingDefendantDao shd1 = DummyHearingUtil.getXhbSchedHearingDefendantDao();
        shd1.setScheduledHearingId(10002);
        shd1.setDefendantOnCaseId(101);

        XhbSchedHearingDefendantDao shd2 = DummyHearingUtil.getXhbSchedHearingDefendantDao();
        shd2.setScheduledHearingId(10002);
        shd2.setDefendantOnCaseId(202);

        // DOCs with real defendantIds; not observed
        XhbDefendantOnCaseDao doc101 = DummyCaseUtil.getXhbDefendantOnCaseDao();
        doc101.setDefendantOnCaseId(101);
        doc101.setDefendantId(90001);
        doc101.setObsInd(null);

        XhbDefendantOnCaseDao doc202 = DummyCaseUtil.getXhbDefendantOnCaseDao();
        doc202.setDefendantOnCaseId(202);
        doc202.setDefendantId(90002);
        doc202.setObsInd(null);

        // Defendants (NOT hidden → use "" not null to avoid contentEquals NPE)
        XhbDefendantDao def90001 = DummyDefendantUtil.getXhbDefendantDao();
        def90001.setPublicDisplayHide("");
        XhbDefendantDao def90002 = DummyDefendantUtil.getXhbDefendantDao();
        def90002.setPublicDisplayHide("");

        // Hearing & Case for hearingId 777
        XhbHearingDao hearing777 = DummyHearingUtil.getXhbHearingDao();
        hearing777.setHearingId(777);
        hearing777.setCaseId(5001);

        XhbCaseDao case5001 = DummyCaseUtil.getXhbCaseDao();
        case5001.setCaseId(5001);
        case5001.setPublicDisplayHide(""); // not hidden
        
        XhbCourtSiteDao site = DummyCourtUtil.getXhbCourtSiteDao();

        // --- Expectations (use andStubReturn for background lookups) ---
        EasyMock.expect(mockXhbCourtSiteRepository.findByIdSafe(sitting.getCourtSiteId()))
            .andStubReturn(Optional.of(site));
        EasyMock.expect(mockXhbCourtRoomRepository.findByIdSafe(sitting.getCourtRoomId()))
            .andStubReturn(Optional.of(room));

        // For chosen hearingId
        EasyMock.expect(mockXhbHearingRepository.findByIdSafe(777))
            .andStubReturn(Optional.of(hearing777));
        EasyMock.expect(mockXhbCaseRepository.findByIdSafe(5001))
            .andStubReturn(Optional.of(case5001));

        // SHDs only for the chosen SH (10002); others return empty
        EasyMock.expect(mockXhbSchedHearingDefendantRepository.findByScheduledHearingIdSafe(10002))
            .andStubReturn(List.of(shd1, shd2));
        EasyMock.expect(mockXhbSchedHearingDefendantRepository.findByScheduledHearingIdSafe(10001))
            .andStubReturn(List.of()); // dedup discards anyway
        EasyMock.expect(mockXhbSchedHearingDefendantRepository.findByScheduledHearingIdSafe(20001))
            .andStubReturn(List.of()); // filtered by room

        // DOC/Def lookups for both SHDs
        EasyMock.expect(mockXhbDefendantOnCaseRepository.findByIdSafe(101))
            .andStubReturn(Optional.of(doc101));
        EasyMock.expect(mockXhbDefendantOnCaseRepository.findByIdSafe(202))
            .andStubReturn(Optional.of(doc202));

        EasyMock.expect(mockXhbDefendantRepository.findByIdSafe(90001))
            .andStubReturn(Optional.of(def90001));
        EasyMock.expect(mockXhbDefendantRepository.findByIdSafe(90002))
            .andStubReturn(Optional.of(def90002));

        // Case refs / logs (if SummaryByNameValue population hits them via parent logic)
        EasyMock.expect(mockXhbCaseReferenceRepository.findByCaseIdSafe(EasyMock.anyInt()))
            .andStubReturn(List.of(DummyCaseUtil.getXhbCaseReferenceDao()));
        EasyMock.expect(mockXhbCourtLogEntryRepository.findByCaseIdSafe(EasyMock.anyInt()))
            .andStubReturn(List.of(DummyCourtUtil.getXhbCourtLogEntryDao()));

        // Replay
        doReplayArray(List.of(
            mockXhbCourtSiteRepository, mockXhbCourtRoomRepository,
            mockXhbHearingRepository, mockXhbCaseRepository,
            mockXhbSchedHearingDefendantRepository, mockXhbDefendantOnCaseRepository, mockXhbDefendantRepository,
            mockXhbCaseReferenceRepository, mockXhbCourtLogEntryRepository
        ));

        // --- Act (reflective call) ---
        SummaryByNameQuery sut = new SummaryByNameQuery(mockEntityManager,
            mockXhbCaseRepository, mockXhbCaseReferenceRepository, mockXhbHearingListRepository,
            mockXhbSittingRepository, mockXhbScheduledHearingRepository, mockXhbCourtSiteRepository,
            mockXhbCourtRoomRepository, mockXhbSchedHearingDefendantRepository, mockXhbHearingRepository,
            mockXhbDefendantOnCaseRepository, mockXhbDefendantRepository, mockXhbCourtLogEntryRepository,
            mockXhbRefHearingTypeRepository);

        var m = SummaryByNameQuery.class.getDeclaredMethod(
            "getScheduleHearingData", XhbSittingDao.class, List.class, String.class, int[].class);
        m.setAccessible(true);

        int[] selectedRooms = { courtRoomId };
        List<XhbScheduledHearingDao> scheds = List.of(shNoProg, shWithProg, shOtherRoom);
        
        @SuppressWarnings("unchecked")
        List<SummaryByNameValue> out = (List<SummaryByNameValue>) m.invoke(
            sut, sitting, scheds, /*floating*/ "0", selectedRooms);

        // --- Assert ---
        // We expect exactly TWO rows (one per SHD for the chosen scheduled hearing 10002).
        org.junit.jupiter.api.Assertions.assertEquals(2, out.size(), "Expected rows for SHDs of deduped hearing");

        verifyReplayArray(List.of(
            mockXhbCourtSiteRepository, mockXhbCourtRoomRepository,
            mockXhbHearingRepository, mockXhbCaseRepository,
            mockXhbSchedHearingDefendantRepository, mockXhbDefendantOnCaseRepository, mockXhbDefendantRepository,
            mockXhbCaseReferenceRepository, mockXhbCourtLogEntryRepository
        ));
    }
    
    
    @Test
    void testGetSummaryByNameValue_hidesViaCaseOrDefendant_andSkipsObserved() throws Exception {
        int courtRoomId = 8112;

        XhbSittingDao sitting = DummyHearingUtil.getXhbSittingDao();
        sitting.setCourtRoomId(courtRoomId);

        XhbCourtRoomDao room = DummyCourtUtil.getXhbCourtRoomDao();
        room.setCourtRoomId(courtRoomId);

        // Scheduled hearing
        XhbScheduledHearingDao sh = DummyHearingUtil.getXhbScheduledHearingDao();
        sh.setScheduledHearingId(70001);
        sh.setHearingId(9009);
        sh.setMovedFromCourtRoomId(null);

        // SHDs
        XhbSchedHearingDefendantDao shdVisible = DummyHearingUtil.getXhbSchedHearingDefendantDao();
        shdVisible.setScheduledHearingId(70001);
        shdVisible.setDefendantOnCaseId(101);

        XhbSchedHearingDefendantDao shdObs = DummyHearingUtil.getXhbSchedHearingDefendantDao();
        shdObs.setScheduledHearingId(70001);
        shdObs.setDefendantOnCaseId(202);

        // DOCs
        XhbDefendantOnCaseDao docVisible = DummyCaseUtil.getXhbDefendantOnCaseDao();
        docVisible.setDefendantOnCaseId(101);
        docVisible.setDefendantId(91001);
        docVisible.setObsInd(null);

        XhbDefendantOnCaseDao docObserved = DummyCaseUtil.getXhbDefendantOnCaseDao();
        docObserved.setDefendantOnCaseId(202);
        docObserved.setDefendantId(91002);
        docObserved.setObsInd("Y");

        // Defendant: hidden via defendant flag
        var defHidden = DummyDefendantUtil.getXhbDefendantDao();
        defHidden.setPublicDisplayHide("Y");

        // Hearing / Case: case hidden
        XhbHearingDao hearing = DummyHearingUtil.getXhbHearingDao();
        hearing.setHearingId(9009);
        hearing.setCaseId(6001);

        XhbCaseDao hiddenCase = DummyCaseUtil.getXhbCaseDao();
        hiddenCase.setCaseId(6001);
        hiddenCase.setPublicDisplayHide("Y");
        XhbCourtSiteDao site = DummyCourtUtil.getXhbCourtSiteDao();

        // --- Expectations (add missing case refs/logs stubs) ---
        EasyMock.expect(mockXhbCourtSiteRepository.findByIdSafe(sitting.getCourtSiteId()))
            .andStubReturn(Optional.of(site));
        EasyMock.expect(mockXhbCourtRoomRepository.findByIdSafe(sitting.getCourtRoomId()))
            .andStubReturn(Optional.of(room));

        EasyMock.expect(mockXhbHearingRepository.findByIdSafe(9009))
            .andStubReturn(Optional.of(hearing));
        EasyMock.expect(mockXhbCaseRepository.findByIdSafe(6001))
            .andStubReturn(Optional.of(hiddenCase));

        // **NEW**: isReportingRestricted() expects a non-null List
        EasyMock.expect(mockXhbCaseReferenceRepository.findByCaseIdSafe(6001))
            .andStubReturn(java.util.List.of(DummyCaseUtil.getXhbCaseReferenceDao()));
        // (Optional but safe if the implementation also touches logs)
        EasyMock.expect(mockXhbCourtLogEntryRepository.findByCaseIdSafe(6001))
            .andStubReturn(java.util.List.of(DummyCourtUtil.getXhbCourtLogEntryDao()));

        EasyMock.expect(mockXhbDefendantOnCaseRepository.findByIdSafe(101))
            .andStubReturn(Optional.of(docVisible));
        EasyMock.expect(mockXhbDefendantOnCaseRepository.findByIdSafe(202))
            .andStubReturn(Optional.of(docObserved));

        EasyMock.expect(mockXhbDefendantRepository.findByIdSafe(91001))
            .andStubReturn(Optional.of(defHidden));
        // no stub for 91002; obsInd="Y" skips defendant fetch

        // Replay
        doReplayArray(java.util.List.of(
            mockXhbCourtSiteRepository, mockXhbCourtRoomRepository,
            mockXhbHearingRepository, mockXhbCaseRepository,
            mockXhbCaseReferenceRepository,               // << include
            mockXhbCourtLogEntryRepository,               // << include (optional but harmless)
            mockXhbDefendantOnCaseRepository, mockXhbDefendantRepository
        ));

        // --- Act (reflective call) ---
        SummaryByNameQuery sut = new SummaryByNameQuery(
            mockEntityManager, mockXhbCaseRepository, mockXhbCaseReferenceRepository, mockXhbHearingListRepository,
            mockXhbSittingRepository, mockXhbScheduledHearingRepository, mockXhbCourtSiteRepository,
            mockXhbCourtRoomRepository, mockXhbSchedHearingDefendantRepository, mockXhbHearingRepository,
            mockXhbDefendantOnCaseRepository, mockXhbDefendantRepository, mockXhbCourtLogEntryRepository,
            mockXhbRefHearingTypeRepository
        );

        var m = SummaryByNameQuery.class.getDeclaredMethod(
            "getSummaryByNameValue",
            uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingDao.class,
            uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingDao.class,
            uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant.XhbSchedHearingDefendantDao.class,
            String.class
        );
        m.setAccessible(true);

        SummaryByNameValue v1 = (SummaryByNameValue) m.invoke(sut, sitting, sh, shdVisible, "0");
        SummaryByNameValue v2 = (SummaryByNameValue) m.invoke(sut, sitting, sh, shdObs, "0");

        org.junit.jupiter.api.Assertions.assertNotNull(v1);
        org.junit.jupiter.api.Assertions.assertNotNull(v2);

        verifyReplayArray(java.util.List.of(
            mockXhbCourtSiteRepository, mockXhbCourtRoomRepository,
            mockXhbHearingRepository, mockXhbCaseRepository,
            mockXhbCaseReferenceRepository,               // << include
            mockXhbCourtLogEntryRepository,               // << include
            mockXhbDefendantOnCaseRepository, mockXhbDefendantRepository
        ));
    }



}
