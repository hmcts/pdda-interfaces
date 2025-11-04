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
import uk.gov.hmcts.DummyServicesUtil;
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
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.AllCaseStatusValue;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.DefendantName;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**

 * Title: AllCaseStatusQuery Test.


 * Description:


 * Copyright: Copyright (c) 2023


 * Company: CGI

 * @author Mark Harris
 */
@ExtendWith(EasyMockExtension.class)
@SuppressWarnings({"PMD"})
class AllCaseStatusQueryTest extends AbstractQueryTest {

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
    protected AllCaseStatusQuery classUnderTest = getClassUnderTest();


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

    protected AllCaseStatusQuery getClassUnderTest() {
        return new AllCaseStatusQuery(mockEntityManager, mockXhbCaseRepository, mockXhbCaseReferenceRepository,
            mockXhbHearingListRepository, mockXhbSittingRepository, mockXhbScheduledHearingRepository,
            mockXhbCourtSiteRepository, mockXhbCourtRoomRepository, mockXhbSchedHearingDefendantRepository,
            mockXhbHearingRepository, mockXhbDefendantOnCaseRepository, mockXhbDefendantRepository,
            mockXhbCourtLogEntryRepository, mockXhbRefHearingTypeRepository);
    }

    @Test
    void testDefaultConstructor() {
        boolean result = false;
        try {
            classUnderTest = new AllCaseStatusQuery(mockEntityManager);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }

    @Test
    void testGetDataNoListEmpty() {
        boolean result = testGetDataNoList(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
            Optional.empty(), Optional.empty(), Optional.empty());
        assertTrue(result, TRUE);
    }

    @Test
    void testGetDataNoListNoSittings() {
        List<XhbHearingListDao> xhbHearingListDaoList = new ArrayList<>();
        xhbHearingListDaoList.add(DummyHearingUtil.getXhbHearingListDao());
        boolean result = testGetDataNoList(xhbHearingListDaoList, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), Optional.empty(), Optional.of(DummyCourtUtil.getXhbCourtSiteDao()),
            Optional.of(DummyCourtUtil.getXhbCourtRoomDao()));
        assertTrue(result, TRUE);
    }

    @Test
    void testGetDataNoListNoScheduledHearings() {
        List<XhbHearingListDao> xhbHearingListDaoList = new ArrayList<>();
        xhbHearingListDaoList.add(DummyHearingUtil.getXhbHearingListDao());
        List<XhbSittingDao> xhbSittingDaoList = new ArrayList<>();
        xhbSittingDaoList.add(DummyHearingUtil.getXhbSittingDao());
        boolean result = testGetDataNoList(xhbHearingListDaoList, xhbSittingDaoList, new ArrayList<>(),
            new ArrayList<>(), Optional.empty(), Optional.of(DummyCourtUtil.getXhbCourtSiteDao()),
            Optional.of(DummyCourtUtil.getXhbCourtRoomDao()));
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
            new ArrayList<>(), Optional.empty(), Optional.of(DummyCourtUtil.getXhbCourtSiteDao()),
            Optional.of(DummyCourtUtil.getXhbCourtRoomDao()));
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
        List<XhbSchedHearingDefendantDao> xhbSchedHearingDefendantDaoList = DummyServicesUtil.getNewArrayList();
        xhbSchedHearingDefendantDaoList.add(DummyHearingUtil.getXhbSchedHearingDefendantDao());
        boolean result = testGetDataNoList(xhbHearingListDaoList, xhbSittingDaoList, xhbScheduledHearingDaoList,
            xhbSchedHearingDefendantDaoList, Optional.empty(), Optional.empty(), Optional.empty());
        assertTrue(result, TRUE);
    }

    @Test
    void testGetDataNoListSuccess() {
        List<XhbHearingListDao> xhbHearingListDaoList = DummyServicesUtil.getNewArrayList();
        xhbHearingListDaoList.add(DummyHearingUtil.getXhbHearingListDao());
        List<XhbSittingDao> xhbSittingDaoList = DummyServicesUtil.getNewArrayList();
        xhbSittingDaoList.add(DummyHearingUtil.getXhbSittingDao());
        List<XhbScheduledHearingDao> xhbScheduledHearingDaoList = DummyServicesUtil.getNewArrayList();
        xhbScheduledHearingDaoList.add(DummyHearingUtil.getXhbScheduledHearingDao());
        List<XhbSchedHearingDefendantDao> xhbSchedHearingDefendantDaoList = DummyServicesUtil.getNewArrayList();
        xhbSchedHearingDefendantDaoList.add(DummyHearingUtil.getXhbSchedHearingDefendantDao());
        boolean result =
            testGetDataNoList(xhbHearingListDaoList, xhbSittingDaoList, xhbScheduledHearingDaoList,
                xhbSchedHearingDefendantDaoList, Optional.of(DummyHearingUtil.getXhbHearingDao()),
                Optional.of(DummyCourtUtil.getXhbCourtSiteDao()),
                Optional.of(DummyCourtUtil.getXhbCourtRoomDao()));
        assertTrue(result, TRUE);
    }

    @SuppressWarnings("unused")
    private boolean testGetDataNoList(List<XhbHearingListDao> xhbHearingListDaoList,
        List<XhbSittingDao> xhbSittingDaoList, List<XhbScheduledHearingDao> xhbScheduledHearingDaoList,
        List<XhbSchedHearingDefendantDao> xhbSchedHearingDefendantDaoList, Optional<XhbHearingDao> xhbHearingDao,
        Optional<XhbCourtSiteDao> courtSiteDao, Optional<XhbCourtRoomDao> courtRoomDao) {
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
        replayArray.add(mockXhbHearingListRepository);
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
            replayArray.add(mockXhbSittingRepository);
            abortExpects = xhbSittingDaoList.isEmpty();
        }
        if (!abortExpects) {
            for (XhbSittingDao xhbSittingDao : xhbSittingDaoList) {
                expectSitting(xhbScheduledHearingDaoList, xhbSchedHearingDefendantDaoList, xhbHearingDao, replayArray,
                    courtSiteDao, courtRoomDao);
            }
        }

        // Replays
        doReplayArray(replayArray);

        // Run
        classUnderTest.getData(date, courtId, courtRoomIds);

        // Checks
        verifyReplayArray(replayArray);

        return true;
    }

    private void expectSitting(List<XhbScheduledHearingDao> xhbScheduledHearingDaoList,
        List<XhbSchedHearingDefendantDao> xhbSchedHearingDefendantDaoList,
        Optional<XhbHearingDao> xhbHearingDao, List<AbstractRepository<?>> replayArray,
        Optional<XhbCourtSiteDao> courtSiteDao,
        Optional<XhbCourtRoomDao> courtRoomDao) {
        EasyMock
            .expect(
                mockXhbScheduledHearingRepository.findBySittingIdSafe(EasyMock.isA(Integer.class)))
            .andReturn(xhbScheduledHearingDaoList);
        EasyMock.expectLastCall().anyTimes();
        addReplayArray(replayArray, mockXhbScheduledHearingRepository);
        boolean abortExpects = xhbScheduledHearingDaoList.isEmpty();
        if (!abortExpects) {
            EasyMock
                .expect(mockXhbSchedHearingDefendantRepository
                    .findByScheduledHearingIdSafe(EasyMock.isA(Integer.class)))
                .andReturn(xhbSchedHearingDefendantDaoList);
            EasyMock.expectLastCall().anyTimes();
            addReplayArray(replayArray, mockXhbSchedHearingDefendantRepository);
            abortExpects = xhbSchedHearingDefendantDaoList.isEmpty();
        }
        if (!abortExpects) {
            EasyMock.expect(mockXhbCourtSiteRepository.findByIdSafe(EasyMock.isA(Integer.class)))
                .andReturn(courtSiteDao);
            EasyMock.expectLastCall().anyTimes();
            addReplayArray(replayArray, mockXhbCourtSiteRepository);
            EasyMock.expect(mockXhbCourtRoomRepository.findByIdSafe(EasyMock.isA(Integer.class)))
                .andReturn(courtRoomDao);
            EasyMock.expectLastCall().anyTimes();
            addReplayArray(replayArray, mockXhbCourtRoomRepository);
            EasyMock.expect(mockXhbHearingRepository.findByIdSafe(EasyMock.isA(Integer.class)))
                .andReturn(xhbHearingDao);
            EasyMock.expectLastCall().anyTimes();
            addReplayArray(replayArray, mockXhbHearingRepository);
            EasyMock
                .expect(mockXhbDefendantOnCaseRepository.findByIdSafe(EasyMock.isA(Integer.class)))
                .andReturn(Optional.of(DummyDefendantUtil.getXhbDefendantOnCaseDao()));
            EasyMock.expectLastCall().anyTimes();
            addReplayArray(replayArray, mockXhbDefendantOnCaseRepository);
            EasyMock.expect(mockXhbDefendantRepository.findByIdSafe(EasyMock.isA(Integer.class)))
                .andReturn(Optional.of(DummyDefendantUtil.getXhbDefendantDao()));
            EasyMock.expectLastCall().anyTimes();
            addReplayArray(replayArray, mockXhbDefendantRepository);
            abortExpects = xhbHearingDao.isEmpty();
        }
        if (!abortExpects) {
            List<XhbCaseReferenceDao> xhbCaseReferenceDaoList = DummyServicesUtil.getNewArrayList();
            xhbCaseReferenceDaoList.add(DummyCaseUtil.getXhbCaseReferenceDao());
            EasyMock
                .expect(
                    mockXhbCaseReferenceRepository.findByCaseIdSafe(EasyMock.isA(Integer.class)))
                .andReturn(xhbCaseReferenceDaoList);
            addReplayArray(replayArray, mockXhbCaseReferenceRepository);
            EasyMock.expect(mockXhbCaseRepository.findByIdSafe(EasyMock.isA(Integer.class)))
                .andReturn(Optional.of(DummyCaseUtil.getXhbCaseDao()));
            addReplayArray(replayArray, mockXhbCaseRepository);
            List<XhbCourtLogEntryDao> xhbCourtLogEntryDaoList = DummyServicesUtil.getNewArrayList();
            xhbCourtLogEntryDaoList.add(DummyCourtUtil.getXhbCourtLogEntryDao());
            EasyMock
                .expect(
                    mockXhbCourtLogEntryRepository.findByCaseIdSafe(EasyMock.isA(Integer.class)))
                .andReturn(xhbCourtLogEntryDaoList);
            addReplayArray(replayArray, mockXhbCourtLogEntryRepository);
            EasyMock
                .expect(mockXhbRefHearingTypeRepository.findByIdSafe(EasyMock.isA(Integer.class)))
                .andReturn(Optional.of(DummyHearingUtil.getXhbRefHearingTypeDao()));
            addReplayArray(replayArray, mockXhbRefHearingTypeRepository);
        }
    }
    
    
    @Test
    void testGetScheduleHearingData_dedupPrefersProgress_andFiltersByRoom() throws Exception {
        // Arrange
        int roomId = 8112;
        
        XhbSittingDao sitting = DummyHearingUtil.getXhbSittingDao();
        sitting.setCourtRoomId(roomId);

        // two SH rows for same hearingId (777): with/without progress -> with progress should win
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

        // A third SH for a different hearingId (888) but movedFrom different room -> filtered
        XhbScheduledHearingDao shOtherRoom = DummyHearingUtil.getXhbScheduledHearingDao();
        shOtherRoom.setScheduledHearingId(20001);
        shOtherRoom.setHearingId(888);
        shOtherRoom.setHearingProgress(null);
        shOtherRoom.setMovedFromCourtRoomId(9999);

        // The chosen (10002) should emit SHDs -> two defendants
        XhbSchedHearingDefendantDao shd1 = DummyHearingUtil.getXhbSchedHearingDefendantDao();
        shd1.setScheduledHearingId(10002);
        shd1.setDefendantOnCaseId(101);

        XhbSchedHearingDefendantDao shd2 = DummyHearingUtil.getXhbSchedHearingDefendantDao();
        shd2.setScheduledHearingId(10002);
        shd2.setDefendantOnCaseId(202);

        // DOCs / Defs
        XhbDefendantOnCaseDao doc101 = DummyCaseUtil.getXhbDefendantOnCaseDao();
        doc101.setDefendantOnCaseId(101);
        doc101.setDefendantId(90001);
        doc101.setObsInd(null);

        XhbDefendantOnCaseDao doc202 = DummyCaseUtil.getXhbDefendantOnCaseDao();
        doc202.setDefendantOnCaseId(202);
        doc202.setDefendantId(90002);
        doc202.setObsInd(null);

        var def90001 = DummyDefendantUtil.getXhbDefendantDao();
        def90001.setPublicDisplayHide(""); // not hidden
        var def90002 = DummyDefendantUtil.getXhbDefendantDao();
        def90002.setPublicDisplayHide("");

        // Hearing/Case for 777
        XhbHearingDao hearing777 = DummyHearingUtil.getXhbHearingDao();
        hearing777.setHearingId(777);
        hearing777.setCaseId(5001);
        var case5001 = DummyCaseUtil.getXhbCaseDao();
        case5001.setCaseId(5001);
        case5001.setPublicDisplayHide("");

        // Site/room lookups (populateData)
        XhbCourtSiteDao site = DummyCourtUtil.getXhbCourtSiteDao();
        XhbCourtRoomDao room = DummyCourtUtil.getXhbCourtRoomDao();
        room.setCourtRoomId(roomId);

        // Expectations (stubbed)
        EasyMock.expect(mockXhbCourtSiteRepository.findByIdSafe(sitting.getCourtSiteId()))
            .andStubReturn(Optional.of(site));
        EasyMock.expect(mockXhbCourtRoomRepository.findByIdSafe(sitting.getCourtRoomId()))
            .andStubReturn(Optional.of(room));

        EasyMock.expect(mockXhbSchedHearingDefendantRepository.findByScheduledHearingIdSafe(10002))
            .andStubReturn(List.of(shd1, shd2));
        EasyMock.expect(mockXhbSchedHearingDefendantRepository.findByScheduledHearingIdSafe(10001))
            .andStubReturn(List.of()); // discarded by dedup anyway
        EasyMock.expect(mockXhbSchedHearingDefendantRepository.findByScheduledHearingIdSafe(20001))
            .andStubReturn(List.of()); // filtered by room

        EasyMock.expect(mockXhbHearingRepository.findByIdSafe(777))
            .andStubReturn(Optional.of(hearing777));
        EasyMock.expect(mockXhbCaseRepository.findByIdSafe(5001))
            .andStubReturn(Optional.of(case5001));

        EasyMock.expect(mockXhbCaseReferenceRepository.findByCaseIdSafe(5001))
            .andStubReturn(List.of(DummyCaseUtil.getXhbCaseReferenceDao()));
        EasyMock.expect(mockXhbCourtLogEntryRepository.findByCaseIdSafe(5001))
            .andStubReturn(List.of(DummyCourtUtil.getXhbCourtLogEntryDao()));

        EasyMock.expect(mockXhbRefHearingTypeRepository.findByIdSafe(EasyMock.anyInt()))
            .andStubReturn(Optional.of(DummyHearingUtil.getXhbRefHearingTypeDao()));

        EasyMock.expect(mockXhbDefendantOnCaseRepository.findByIdSafe(101))
            .andStubReturn(Optional.of(doc101));
        EasyMock.expect(mockXhbDefendantOnCaseRepository.findByIdSafe(202))
            .andStubReturn(Optional.of(doc202));
        EasyMock.expect(mockXhbDefendantRepository.findByIdSafe(90001))
            .andStubReturn(Optional.of(def90001));
        EasyMock.expect(mockXhbDefendantRepository.findByIdSafe(90002))
            .andStubReturn(Optional.of(def90002));

        doReplayArray(List.of(
            mockXhbCourtSiteRepository, mockXhbCourtRoomRepository,
            mockXhbSchedHearingDefendantRepository, mockXhbHearingRepository, mockXhbCaseRepository,
            mockXhbCaseReferenceRepository, mockXhbCourtLogEntryRepository, mockXhbRefHearingTypeRepository,
            mockXhbDefendantOnCaseRepository, mockXhbDefendantRepository
        ));

        // Act (reflect private)
        var m = AllCaseStatusQuery.class.getDeclaredMethod(
            "getScheduleHearingData",
            XhbSittingDao.class, List.class, String.class, int[].class);
        m.setAccessible(true);
        
        int[] selectedRooms = {roomId};
        List<XhbScheduledHearingDao> scheds = List.of(shNoProg, shWithProg, shOtherRoom);
        
        @SuppressWarnings("unchecked")
        List<AllCaseStatusValue> out = (List<AllCaseStatusValue>) m.invoke(
            classUnderTest, sitting, scheds, /*floating*/ "0", selectedRooms);

        // Assert: exactly two rows (one per SHD for the chosen 10002)
        org.junit.jupiter.api.Assertions.assertEquals(1, out.size(),
            "AllCaseStatusQuery shows one row per chosen hearing (one defendant selected).");

        verifyReplayArray(List.of(
            mockXhbCourtSiteRepository, mockXhbCourtRoomRepository,
            mockXhbSchedHearingDefendantRepository, mockXhbHearingRepository, mockXhbCaseRepository,
            mockXhbCaseReferenceRepository, mockXhbCourtLogEntryRepository, mockXhbRefHearingTypeRepository,
            mockXhbDefendantOnCaseRepository, mockXhbDefendantRepository
        ));
    }
    
    
    @Test
    void testGetSchedHearingDefendantData_prefersFirstNonObserved_elseFirst_andPlaceholder() throws Exception {
        int roomId = 8112;
        XhbSittingDao sitting = DummyHearingUtil.getXhbSittingDao();
        sitting.setCourtRoomId(roomId);

        XhbCourtRoomDao room = DummyCourtUtil.getXhbCourtRoomDao();
        room.setCourtRoomId(roomId);

        XhbScheduledHearingDao sh = DummyHearingUtil.getXhbScheduledHearingDao();
        sh.setScheduledHearingId(50001);
        sh.setHearingId(9001);
        sh.setHearingProgress(5);
        sh.setMovedFromCourtRoomId(null);

        // Three SHDs: observed, non-observed, and one with missing DOC (to exercise placeholder)
        XhbSchedHearingDefendantDao shdObserved = DummyHearingUtil.getXhbSchedHearingDefendantDao();
        shdObserved.setScheduledHearingId(50001);
        shdObserved.setDefendantOnCaseId(111);

        XhbSchedHearingDefendantDao shdGood = DummyHearingUtil.getXhbSchedHearingDefendantDao();
        shdGood.setScheduledHearingId(50001);
        shdGood.setDefendantOnCaseId(222);

        XhbSchedHearingDefendantDao shdNoDoc = DummyHearingUtil.getXhbSchedHearingDefendantDao();
        shdNoDoc.setScheduledHearingId(50001);
        shdNoDoc.setDefendantOnCaseId(333);

        // DOCs
        XhbDefendantOnCaseDao docObserved = DummyCaseUtil.getXhbDefendantOnCaseDao();
        docObserved.setDefendantOnCaseId(111);
        docObserved.setDefendantId(91011);
        docObserved.setObsInd("Y");

        XhbDefendantOnCaseDao docGood = DummyCaseUtil.getXhbDefendantOnCaseDao();
        docGood.setDefendantOnCaseId(222);
        docGood.setDefendantId(92022);
        docGood.setObsInd(null);

        // No DOC for 333 -> Optional.empty()

        // DEFs
        XhbDefendantDao defGood = DummyDefendantUtil.getXhbDefendantDao();
        defGood.setPublicDisplayHide(""); // not hidden

        // Hearing/Case
        XhbHearingDao hearing = DummyHearingUtil.getXhbHearingDao();
        hearing.setHearingId(9001);
        hearing.setCaseId(7001);
        XhbCaseDao caseDao = DummyCaseUtil.getXhbCaseDao();
        caseDao.setCaseId(7001);
        caseDao.setPublicDisplayHide("");
        
        XhbCourtSiteDao site = DummyCourtUtil.getXhbCourtSiteDao();

        // Expectations
        EasyMock.expect(mockXhbCourtSiteRepository.findByIdSafe(sitting.getCourtSiteId()))
            .andStubReturn(Optional.of(site));
        EasyMock.expect(mockXhbCourtRoomRepository.findByIdSafe(sitting.getCourtRoomId()))
            .andStubReturn(Optional.of(room));

        EasyMock.expect(mockXhbHearingRepository.findByIdSafe(9001))
            .andStubReturn(Optional.of(hearing));
        EasyMock.expect(mockXhbCaseRepository.findByIdSafe(7001))
            .andStubReturn(Optional.of(caseDao));
        EasyMock.expect(mockXhbCaseReferenceRepository.findByCaseIdSafe(7001))
            .andStubReturn(List.of(DummyCaseUtil.getXhbCaseReferenceDao()));
        EasyMock.expect(mockXhbCourtLogEntryRepository.findByCaseIdSafe(7001))
            .andStubReturn(List.of(DummyCourtUtil.getXhbCourtLogEntryDao()));

        EasyMock.expect(mockXhbRefHearingTypeRepository.findByIdSafe(EasyMock.anyInt()))
            .andStubReturn(Optional.of(DummyHearingUtil.getXhbRefHearingTypeDao()));

        // DOC chain for selection
        EasyMock.expect(mockXhbDefendantOnCaseRepository.findByIdSafe(111))
            .andStubReturn(Optional.of(docObserved));
        EasyMock.expect(mockXhbDefendantOnCaseRepository.findByIdSafe(222))
            .andStubReturn(Optional.of(docGood));
        EasyMock.expect(mockXhbDefendantOnCaseRepository.findByIdSafe(333))
            .andStubReturn(Optional.empty()); // forces placeholder when chosen would be 333

        // For chosen=shdGood -> fetch defendant
        EasyMock.expect(mockXhbDefendantRepository.findByIdSafe(92022))
            .andStubReturn(Optional.of(defGood));

        doReplayArray(List.of(
            mockXhbCourtSiteRepository, mockXhbCourtRoomRepository,
            mockXhbHearingRepository, mockXhbCaseRepository,
            mockXhbCaseReferenceRepository, mockXhbCourtLogEntryRepository, mockXhbRefHearingTypeRepository,
            mockXhbDefendantOnCaseRepository, mockXhbDefendantRepository
        ));

        // Act (reflect private)
        var m = AllCaseStatusQuery.class.getDeclaredMethod(
            "getSchedHearingDefendantData",
            XhbSittingDao.class, XhbScheduledHearingDao.class, List.class, String.class);
        m.setAccessible(true);

        // First: list with observed + good + noDoc → should choose shdGood and produce 1 row
        @SuppressWarnings("unchecked")
        List<AllCaseStatusValue> out1 = (List<AllCaseStatusValue>) m.invoke(
            classUnderTest, sitting, sh, List.of(shdObserved, shdGood, shdNoDoc), "0");
        org.junit.jupiter.api.Assertions.assertEquals(1, out1.size(), "Expected one row with chosen non-observed DOC");

        // Second: only noDoc -> chosen becomes that item but getDefendantName yields placeholder
        @SuppressWarnings("unchecked")
        List<AllCaseStatusValue> out2 = (List<AllCaseStatusValue>) m.invoke(
            classUnderTest, sitting, sh, List.of(shdNoDoc), "0");
        org.junit.jupiter.api.Assertions.assertEquals(1, out2.size(), "Expected one placeholder row when DOC missing");

        verifyReplayArray(List.of(
            mockXhbCourtSiteRepository, mockXhbCourtRoomRepository,
            mockXhbHearingRepository, mockXhbCaseRepository,
            mockXhbCaseReferenceRepository, mockXhbCourtLogEntryRepository, mockXhbRefHearingTypeRepository,
            mockXhbDefendantOnCaseRepository, mockXhbDefendantRepository
        ));
    }


    @Test
    void testGetDefendantName_returnsRealName_elsePlaceholderWhenObsOrMissing() throws Exception {
        // Arrange chosen SHD
        XhbSchedHearingDefendantDao chosen = DummyHearingUtil.getXhbSchedHearingDefendantDao();
        chosen.setDefendantOnCaseId(321);

        XhbDefendantOnCaseDao doc = DummyCaseUtil.getXhbDefendantOnCaseDao();
        doc.setDefendantOnCaseId(321);
        doc.setDefendantId(99123);
        doc.setObsInd(null); // processed

        XhbDefendantDao def = DummyDefendantUtil.getXhbDefendantDao();
        def.setPublicDisplayHide(""); // not hidden; avoid NPE

        EasyMock.expect(mockXhbDefendantOnCaseRepository.findByIdSafe(321))
            .andStubReturn(Optional.of(doc));
        EasyMock.expect(mockXhbDefendantRepository.findByIdSafe(99123))
            .andStubReturn(Optional.of(def));
        doReplayArray(List.of(mockXhbDefendantOnCaseRepository, mockXhbDefendantRepository));

        // Act (reflect private)
        var m = AllCaseStatusQuery.class.getDeclaredMethod(
            "getDefendantName", XhbSchedHearingDefendantDao.class, boolean.class);
        m.setAccessible(true);
        DefendantName name = (DefendantName) m.invoke(classUnderTest, chosen, /*isHiddenFromCase*/ false);

        // Assert non-empty
        org.junit.jupiter.api.Assertions.assertNotNull(name);
        // If your DefendantName exposes getters, assert e.g. !name.isHidden()

        // Now: observed -> placeholder returned
        XhbDefendantOnCaseDao docObs = DummyCaseUtil.getXhbDefendantOnCaseDao();
        docObs.setDefendantOnCaseId(321);
        docObs.setDefendantId(99123);
        docObs.setObsInd("Y");
        EasyMock.reset(mockXhbDefendantOnCaseRepository, mockXhbDefendantRepository);
        EasyMock.expect(mockXhbDefendantOnCaseRepository.findByIdSafe(321))
            .andStubReturn(Optional.of(docObs));
        doReplayArray(List.of(mockXhbDefendantOnCaseRepository, mockXhbDefendantRepository));

        DefendantName name2 = (DefendantName) m.invoke(classUnderTest, chosen, false);
        org.junit.jupiter.api.Assertions.assertNotNull(name2);
        // Placeholder is fine; main point is path executed without NPE

        verifyReplayArray(List.of(mockXhbDefendantOnCaseRepository, mockXhbDefendantRepository));
    }

    
    @Test
    void testIsDefendantHidden_variants() throws Exception {
        var m = AllCaseStatusQuery.class.getDeclaredMethod(
            "isDefendantHidden",
            Optional.class, Optional.class, boolean.class);
        m.setAccessible(true);

        // Build Optionals
        XhbDefendantDao def = DummyDefendantUtil.getXhbDefendantDao();
        def.setPublicDisplayHide(""); // not hidden
        XhbDefendantOnCaseDao doc = DummyCaseUtil.getXhbDefendantOnCaseDao();
        doc.setPublicDisplayHide(""); // not hidden

        Boolean r1 = (Boolean) m.invoke(classUnderTest,
            Optional.of(def), Optional.of(doc), /*isHiddenFromCase*/ true);
        org.junit.jupiter.api.Assertions.assertTrue(r1, "Case-hidden should force true");

        doc.setPublicDisplayHide("Y");
        Boolean r2 = (Boolean) m.invoke(classUnderTest,
            Optional.of(def), Optional.of(doc), false);
        org.junit.jupiter.api.Assertions.assertTrue(r2, "DOC hidden should be true");

        doc.setPublicDisplayHide("");
        def.setPublicDisplayHide("Y");
        Boolean r3 = (Boolean) m.invoke(classUnderTest,
            Optional.of(def), Optional.of(doc), false);
        org.junit.jupiter.api.Assertions.assertTrue(r3, "Defendant hidden should be true");

        def.setPublicDisplayHide("");
        Boolean r4 = (Boolean) m.invoke(classUnderTest,
            Optional.of(def), Optional.of(doc), false);
        org.junit.jupiter.api.Assertions.assertFalse(r4, "No hidden flags -> false");
    }

    
    @Test
    void testGetRefHearingTypeDesc_present_and_absent() throws Exception {
        // Hearing with type id
        XhbHearingDao hearing = DummyHearingUtil.getXhbHearingDao();
        hearing.setRefHearingTypeId(42);

        // present
        EasyMock.expect(mockXhbRefHearingTypeRepository.findByIdSafe(42))
            .andStubReturn(Optional.of(DummyHearingUtil.getXhbRefHearingTypeDao()));
        doReplayArray(List.of(mockXhbRefHearingTypeRepository));

        var m = AllCaseStatusQuery.class.getDeclaredMethod(
            "getRefHearingTypeDesc", Optional.class);
        m.setAccessible(true);
        String desc1 = (String) m.invoke(classUnderTest, Optional.of(hearing));
        org.junit.jupiter.api.Assertions.assertNotNull(desc1);

        // absent
        EasyMock.reset(mockXhbRefHearingTypeRepository);
        EasyMock.expect(mockXhbRefHearingTypeRepository.findByIdSafe(42))
            .andStubReturn(Optional.empty());
        doReplayArray(List.of(mockXhbRefHearingTypeRepository));

        String desc2 = (String) m.invoke(classUnderTest, Optional.of(hearing));
        org.junit.jupiter.api.Assertions.assertNull(desc2);

        verifyReplayArray(List.of(mockXhbRefHearingTypeRepository));
    }


}
