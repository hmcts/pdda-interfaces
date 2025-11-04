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
import uk.gov.hmcts.DummyJudgeUtil;
import uk.gov.hmcts.DummyPdNotifierUtil;
import uk.gov.hmcts.DummyPublicDisplayUtil;
import uk.gov.hmcts.DummyServicesUtil;
import uk.gov.hmcts.framework.util.DateTimeUtilities;
import uk.gov.hmcts.pdda.business.entities.AbstractRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcase.XhbCaseDao;
import uk.gov.hmcts.pdda.business.entities.xhbcase.XhbCaseRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcasereference.XhbCaseReferenceDao;
import uk.gov.hmcts.pdda.business.entities.xhbcasereference.XhbCaseReferenceRepository;
import uk.gov.hmcts.pdda.business.entities.xhbconfiguredpublicnotice.XhbConfiguredPublicNoticeDao;
import uk.gov.hmcts.pdda.business.entities.xhbconfiguredpublicnotice.XhbConfiguredPublicNoticeRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtlogentry.XhbCourtLogEntryDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtlogentry.XhbCourtLogEntryRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdefendant.XhbDefendantDao;
import uk.gov.hmcts.pdda.business.entities.xhbdefendant.XhbDefendantRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdefendantoncase.XhbDefendantOnCaseDao;
import uk.gov.hmcts.pdda.business.entities.xhbdefendantoncase.XhbDefendantOnCaseRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdefinitivepublicnotice.XhbDefinitivePublicNoticeRepository;
import uk.gov.hmcts.pdda.business.entities.xhbhearing.XhbHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbhearing.XhbHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbhearinglist.XhbHearingListDao;
import uk.gov.hmcts.pdda.business.entities.xhbhearinglist.XhbHearingListRepository;
import uk.gov.hmcts.pdda.business.entities.xhbpublicnotice.XhbPublicNoticeRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrefhearingtype.XhbRefHearingTypeRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrefjudge.XhbRefJudgeRepository;
import uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant.XhbSchedHearingDefendantDao;
import uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant.XhbSchedHearingDefendantRepository;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingDao;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingRepository;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.CourtDetailValue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**

 * Title: CourtDetailQuery Test.


 * Description:


 * Copyright: Copyright (c) 2023


 * Company: CGI

 * @author Mark Harris
 */
@ExtendWith(EasyMockExtension.class)
@SuppressWarnings({"PMD"})
class CourtDetailQueryTest extends AbstractQueryTest {

    protected static final String TRUE = "Result is not True";
    private static final Integer COURTID = 81;
    private static final int[] COURTROOMIDS = {8112, 8113, 8114};

    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private XhbCaseRepository mockXhbCaseRepository;

    @Mock
    private XhbCaseReferenceRepository mockXhbCaseReferenceRepository;

    @Mock
    private XhbHearingListRepository mockXhbHearingListRepository;

    @Mock
    private XhbSittingRepository mockXhbSittingRepository;

    @Mock
    private XhbScheduledHearingRepository mockXhbScheduledHearingRepository;

    @Mock
    private XhbCourtSiteRepository mockXhbCourtSiteRepository;

    @Mock
    private XhbCourtRoomRepository mockXhbCourtRoomRepository;

    @Mock
    private XhbSchedHearingDefendantRepository mockXhbSchedHearingDefendantRepository;

    @Mock
    private XhbHearingRepository mockXhbHearingRepository;

    @Mock
    private XhbDefendantOnCaseRepository mockXhbDefendantOnCaseRepository;

    @Mock
    private XhbDefendantRepository mockXhbDefendantRepository;

    @Mock
    private XhbCourtLogEntryRepository mockXhbCourtLogEntryRepository;

    @Mock
    private XhbRefHearingTypeRepository mockXhbRefHearingTypeRepository;

    @Mock
    private XhbRefJudgeRepository mockXhbRefJudgeRepository;

    @Mock
    private XhbConfiguredPublicNoticeRepository mockXhbConfiguredPublicNoticeRepository;

    @Mock
    private XhbPublicNoticeRepository mockXhbPublicNoticeRepository;

    @Mock
    private XhbDefinitivePublicNoticeRepository mockXhbDefinitivePublicNoticeRepository;

    @TestSubject
    private final PublicNoticeQuery mockPublicNoticeQuery =
        new PublicNoticeQuery(mockEntityManager, mockXhbConfiguredPublicNoticeRepository,
            mockXhbPublicNoticeRepository, mockXhbDefinitivePublicNoticeRepository);

    @TestSubject
    private CourtDetailQuery classUnderTest = new CourtDetailQuery(mockEntityManager,
        mockXhbCaseRepository, mockXhbCaseReferenceRepository, mockXhbHearingListRepository,
        mockXhbSittingRepository, mockXhbScheduledHearingRepository, mockXhbCourtSiteRepository,
        mockXhbCourtRoomRepository, mockXhbSchedHearingDefendantRepository,
        mockXhbHearingRepository, mockXhbDefendantOnCaseRepository, mockXhbDefendantRepository,
        mockXhbCourtLogEntryRepository, mockXhbRefHearingTypeRepository, mockXhbRefJudgeRepository,
        mockPublicNoticeQuery);

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


    @Test
    void testDefaultConstructor() {
        boolean result = false;
        try {
            classUnderTest = new CourtDetailQuery(mockEntityManager);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }

    @Test
    void testGetDataNoListEmpty() {
        boolean result = testGetDataNoList(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), Optional.empty(), false, false, false, false, false, false);
        assertTrue(result, TRUE);
    }

    @Test
    void testGetDataNoListNoSittings() {
        List<XhbHearingListDao> xhbHearingListDaoList = new ArrayList<>();
        xhbHearingListDaoList.add(DummyHearingUtil.getXhbHearingListDao());
        boolean result =
            testGetDataNoList(xhbHearingListDaoList, new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), Optional.empty(), false, false, false, false, false, false);
        assertTrue(result, TRUE);
    }

    @Test
    void testGetDataNoListNoScheduledHearings() {
        List<XhbHearingListDao> xhbHearingListDaoList = new ArrayList<>();
        xhbHearingListDaoList.add(DummyHearingUtil.getXhbHearingListDao());
        List<XhbSittingDao> xhbSittingDaoList = new ArrayList<>();
        xhbSittingDaoList.add(DummyHearingUtil.getXhbSittingDao());
        boolean result =
            testGetDataNoList(xhbHearingListDaoList, xhbSittingDaoList, new ArrayList<>(),
                new ArrayList<>(), Optional.empty(), false, false, false, false, false, false);
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
        boolean result =
            testGetDataNoList(xhbHearingListDaoList, xhbSittingDaoList, xhbScheduledHearingDaoList,
                new ArrayList<>(), Optional.empty(), false, false, false, false, false, false);
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
        boolean result = testGetDataNoList(xhbHearingListDaoList, xhbSittingDaoList,
            xhbScheduledHearingDaoList, xhbSchedHearingDefendantDaoList, Optional.empty(), false,
            false, false, false, false, false);
        assertTrue(result, TRUE);
    }

    @SuppressWarnings("unused")
    protected boolean testGetDataNoList(List<XhbHearingListDao> xhbHearingListDaoList,
        List<XhbSittingDao> xhbSittingDaoList,
        List<XhbScheduledHearingDao> xhbScheduledHearingDaoList,
        List<XhbSchedHearingDefendantDao> xhbSchedHearingDefendantDaoList,
        Optional<XhbHearingDao> xhbHearingDao, boolean caseHidden,
        boolean defOnCasePublicDisplayHide, boolean defPublicDisplayHide, boolean defOnCaseEmpty,
        boolean defEmpty, boolean defOnCaseObsIndYes) {
        // Setup
        LocalDateTime date = LocalDateTime.now();
        LocalDateTime startDate = DateTimeUtilities.stripTime(date);
        List<AbstractRepository<?>> replayArray = new ArrayList<>();

        // Expects
        boolean abortExpects;
        EasyMock.expect(mockXhbHearingListRepository.findByCourtIdAndDateSafe(COURTID, startDate))
            .andReturn(xhbHearingListDaoList);
        addReplayArray(replayArray, mockXhbHearingListRepository);
        abortExpects = xhbHearingListDaoList.isEmpty();
        if (!abortExpects) {
            EasyMock.expect(mockXhbSittingRepository.findByListIdSafe(EasyMock.isA(Integer.class)))
                .andReturn(xhbSittingDaoList);
            addReplayArray(replayArray, mockXhbSittingRepository);
            abortExpects = xhbSittingDaoList.isEmpty();
        }
        if (!abortExpects) {
            for (XhbSittingDao xhbSittingDao : xhbSittingDaoList) {
                expectSitting(xhbScheduledHearingDaoList, xhbSchedHearingDefendantDaoList,
                    xhbHearingDao, replayArray, caseHidden, defOnCasePublicDisplayHide,
                    defPublicDisplayHide, defOnCaseEmpty, defEmpty, defOnCaseObsIndYes);
            }
        }

        // Replays
        doReplayArray(replayArray);
        EasyMock.replay(mockXhbConfiguredPublicNoticeRepository);
        EasyMock.replay(mockXhbPublicNoticeRepository);
        EasyMock.replay(mockXhbDefinitivePublicNoticeRepository);

        // Run
        classUnderTest.getData(date, COURTID, COURTROOMIDS);

        // Checks
        verifyReplayArray(replayArray);

        return true;
    }

    private void expectSitting(List<XhbScheduledHearingDao> xhbScheduledHearingDaoList,
        List<XhbSchedHearingDefendantDao> xhbSchedHearingDefendantDaoList,
        Optional<XhbHearingDao> xhbHearingDao, List<AbstractRepository<?>> replayArray,
        boolean caseHidden, boolean defOnCasePublicDisplayHide, boolean defPublicDisplayHide,
        boolean defOnCaseEmpty, boolean defEmpty, boolean defOnCaseObsIndYes) {
        EasyMock
            .expect(
                mockXhbScheduledHearingRepository.findBySittingIdSafe(EasyMock.isA(Integer.class)))
            .andReturn(xhbScheduledHearingDaoList);
        addReplayArray(replayArray, mockXhbScheduledHearingRepository);
        boolean abortExpects = xhbScheduledHearingDaoList.isEmpty();
        if (!abortExpects) {
            EasyMock.expect(mockXhbHearingRepository.findByIdSafe(EasyMock.isA(Integer.class)))
                .andReturn(xhbHearingDao);
            EasyMock.expectLastCall().anyTimes();
            addReplayArray(replayArray, mockXhbHearingRepository);
            EasyMock.expect(mockXhbCourtSiteRepository.findByIdSafe(EasyMock.isA(Integer.class)))
                .andReturn(Optional.of(DummyCourtUtil.getXhbCourtSiteDao()));
            EasyMock.expectLastCall().anyTimes();
            addReplayArray(replayArray, mockXhbCourtSiteRepository);
            EasyMock.expect(mockXhbCourtRoomRepository.findByIdSafe(EasyMock.isA(Integer.class)))
                .andReturn(Optional.of(DummyCourtUtil.getXhbCourtRoomDao()));
            EasyMock.expectLastCall().anyTimes();
            addReplayArray(replayArray, mockXhbCourtRoomRepository);
            abortExpects = xhbHearingDao.isEmpty();
        }
        if (!abortExpects) {
            XhbCaseDao xhbCaseDao = DummyCaseUtil.getXhbCaseDao();

            if (caseHidden) {
                xhbCaseDao.setPublicDisplayHide("Y");
            }

            EasyMock.expect(mockXhbCaseRepository.findByIdSafe(EasyMock.isA(Integer.class)))
                .andReturn(Optional.of(xhbCaseDao));
            EasyMock.expectLastCall().anyTimes();
            addReplayArray(replayArray, mockXhbCaseRepository);

            List<XhbCaseReferenceDao> xhbCaseReferenceDaoList = DummyServicesUtil.getNewArrayList();
            xhbCaseReferenceDaoList.add(DummyCaseUtil.getXhbCaseReferenceDao());
            EasyMock
                .expect(
                    mockXhbCaseReferenceRepository.findByCaseIdSafe(EasyMock.isA(Integer.class)))
                .andReturn(xhbCaseReferenceDaoList);
            addReplayArray(replayArray, mockXhbCaseReferenceRepository);
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

        EasyMock
            .expect(mockXhbSchedHearingDefendantRepository
                .findByScheduledHearingIdSafe(EasyMock.isA(Integer.class)))
            .andReturn(xhbSchedHearingDefendantDaoList);
        EasyMock.expectLastCall().anyTimes();
        addReplayArray(replayArray, mockXhbSchedHearingDefendantRepository);
        abortExpects = xhbSchedHearingDefendantDaoList.isEmpty();
        if (!abortExpects) {
            XhbDefendantOnCaseDao xhbDefendantOnCaseDao =
                DummyDefendantUtil.getXhbDefendantOnCaseDao();
            
            if (defOnCasePublicDisplayHide) {
                xhbDefendantOnCaseDao.setPublicDisplayHide("Y");
                EasyMock
                    .expect(
                        mockXhbDefendantOnCaseRepository.findByIdSafe(EasyMock.isA(Integer.class)))
                    .andReturn(Optional.of(xhbDefendantOnCaseDao));
            } else if (defOnCaseEmpty) {
                EasyMock
                    .expect(
                        mockXhbDefendantOnCaseRepository.findByIdSafe(EasyMock.isA(Integer.class)))
                    .andReturn(Optional.empty());
            } else if (defOnCaseObsIndYes) {
                xhbDefendantOnCaseDao.setObsInd("Y");
                EasyMock
                    .expect(
                        mockXhbDefendantOnCaseRepository.findByIdSafe(EasyMock.isA(Integer.class)))
                    .andReturn(Optional.of(xhbDefendantOnCaseDao));
            } else {
                EasyMock
                    .expect(
                        mockXhbDefendantOnCaseRepository.findByIdSafe(EasyMock.isA(Integer.class)))
                    .andReturn(Optional.of(xhbDefendantOnCaseDao));
            }

            EasyMock.expectLastCall().anyTimes();
            addReplayArray(replayArray, mockXhbDefendantOnCaseRepository);

            XhbDefendantDao xhbDefendantDao = DummyDefendantUtil.getXhbDefendantDao();
            if (defPublicDisplayHide) {
                xhbDefendantDao.setPublicDisplayHide("Y");
                EasyMock
                    .expect(mockXhbDefendantRepository.findByIdSafe(EasyMock.isA(Integer.class)))
                    .andReturn(Optional.of(xhbDefendantDao));
            } else if (defEmpty) {
                EasyMock
                    .expect(mockXhbDefendantRepository.findByIdSafe(EasyMock.isA(Integer.class)))
                    .andReturn(Optional.empty());
            } else {
                EasyMock
                    .expect(mockXhbDefendantRepository.findByIdSafe(EasyMock.isA(Integer.class)))
                    .andReturn(Optional.of(xhbDefendantDao));
            }
            EasyMock.expectLastCall().anyTimes();
            addReplayArray(replayArray, mockXhbDefendantRepository);
        }

        EasyMock
            .expect(
                mockXhbRefJudgeRepository
                    .findScheduledAttendeeJudgeSafe(EasyMock.isA(Integer.class)))
            .andReturn(Optional.of(DummyJudgeUtil.getXhbRefJudgeDao()));
        EasyMock.expectLastCall().anyTimes();
        addReplayArray(replayArray, mockXhbRefJudgeRepository);

        List<XhbConfiguredPublicNoticeDao> xhbConfiguredPublicNoticeDaoList =
            DummyServicesUtil.getNewArrayList();
        xhbConfiguredPublicNoticeDaoList
            .add(DummyPdNotifierUtil.getXhbConfiguredPublicNoticeDao("0"));
        EasyMock
            .expect(mockXhbConfiguredPublicNoticeRepository
                .findActiveCourtRoomNoticesSafe(EasyMock.isA(Integer.class)))
            .andReturn(xhbConfiguredPublicNoticeDaoList);

        EasyMock.expect(mockXhbPublicNoticeRepository.findByIdSafe(EasyMock.isA(Integer.class)))
            .andReturn(Optional.of(DummyPublicDisplayUtil.getXhbPublicNoticeDao()));
        EasyMock
            .expect(
                mockXhbDefinitivePublicNoticeRepository.findByIdSafe(EasyMock.isA(Integer.class)))
            .andReturn(Optional.of(DummyPublicDisplayUtil.getXhbDefinitivePublicNoticeDao()));
    }
    
    
    @Test
    void testGetSittingData_filtersAndDedups_prefersProgress_buildsRows() throws Exception {
        // --- Arrange ---
        int roomId = COURTROOMIDS[0];

        XhbSittingDao sitting = DummyHearingUtil.getXhbSittingDao();
        sitting.setCourtRoomId(roomId);

        // 3 scheduled hearings: two share same hearingId (prefer the one with progress),
        // and one is inactive (filtered out)
        XhbScheduledHearingDao sh1 = DummyHearingUtil.getXhbScheduledHearingDao();
        sh1.setScheduledHearingId(101);
        sh1.setHearingId(777);
        sh1.setHearingProgress(null);
        sh1.setIsCaseActive("Y");
        sh1.setMovedFromCourtRoomId(null);

        XhbScheduledHearingDao sh2 = DummyHearingUtil.getXhbScheduledHearingDao();
        sh2.setScheduledHearingId(102);
        sh2.setHearingId(777);
        sh2.setHearingProgress(5); // should be chosen for hearing 777
        sh2.setIsCaseActive("Y");
        sh2.setMovedFromCourtRoomId(null);

        XhbScheduledHearingDao shInactive = DummyHearingUtil.getXhbScheduledHearingDao();
        shInactive.setScheduledHearingId(103);
        shInactive.setHearingId(888);
        shInactive.setHearingProgress(null);
        shInactive.setIsCaseActive("N"); // filtered
        shInactive.setMovedFromCourtRoomId(null);

        // Site/room for populateData
        var room = DummyCourtUtil.getXhbCourtRoomDao();
        room.setCourtRoomId(roomId);

        // Hearing/case for 777
        XhbHearingDao hearing777 = DummyHearingUtil.getXhbHearingDao();
        hearing777.setHearingId(777);
        hearing777.setCaseId(5001);

        XhbCaseDao case5001 = DummyCaseUtil.getXhbCaseDao();
        case5001.setCaseId(5001);
        case5001.setPublicDisplayHide(""); // not hidden
        
        List<XhbScheduledHearingDao> shList = List.of(sh1, sh2, shInactive);
        var site = DummyCourtUtil.getXhbCourtSiteDao();

        // Repos used by getCourtDetailValue(...) chain
        EasyMock.expect(mockXhbScheduledHearingRepository.findBySittingIdSafe(sitting.getSittingId()))
            .andStubReturn(shList);
        EasyMock.expect(mockXhbCourtSiteRepository.findByIdSafe(sitting.getCourtSiteId()))
            .andStubReturn(Optional.of(site));
        EasyMock.expect(mockXhbCourtRoomRepository.findByIdSafe(sitting.getCourtRoomId()))
            .andStubReturn(Optional.of(room));

        EasyMock.expect(mockXhbHearingRepository.findByIdSafe(777))
            .andStubReturn(Optional.of(hearing777));
        EasyMock.expect(mockXhbCaseRepository.findByIdSafe(5001))
            .andStubReturn(Optional.of(case5001));

        // Needed by isReportingRestricted/populateEventData
        EasyMock.expect(mockXhbCaseReferenceRepository.findByCaseIdSafe(5001))
            .andStubReturn(List.of(DummyCaseUtil.getXhbCaseReferenceDao()));
        EasyMock.expect(mockXhbCourtLogEntryRepository.findByCaseIdSafe(5001))
            .andStubReturn(List.of(DummyCourtUtil.getXhbCourtLogEntryDao()));

        // Hearing type
        EasyMock.expect(mockXhbRefHearingTypeRepository.findByIdSafe(EasyMock.anyInt()))
            .andStubReturn(Optional.of(DummyHearingUtil.getXhbRefHearingTypeDao()));

        // No defendants needed for this test (empty list -> skip loop)
        EasyMock.expect(mockXhbSchedHearingDefendantRepository.findByScheduledHearingIdSafe(102))
            .andStubReturn(List.of());

        // Judge
        EasyMock.expect(mockXhbRefJudgeRepository.findScheduledAttendeeJudgeSafe(102))
            .andStubReturn(Optional.of(DummyJudgeUtil.getXhbRefJudgeDao()));
        
        // --- PublicNoticeQuery expectations (used inside getCourtDetailValue) ---
        EasyMock.expect(mockXhbConfiguredPublicNoticeRepository
                .findActiveCourtRoomNoticesSafe(roomId))
            .andStubReturn(java.util.List.of(
                uk.gov.hmcts.DummyPdNotifierUtil.getXhbConfiguredPublicNoticeDao("0")
            ));

        // Be permissive on IDs – the configured notice will reference these
        EasyMock.expect(mockXhbPublicNoticeRepository.findByIdSafe(EasyMock.anyInt()))
            .andStubReturn(java.util.Optional.of(
                uk.gov.hmcts.DummyPublicDisplayUtil.getXhbPublicNoticeDao()
            ));

        EasyMock.expect(mockXhbDefinitivePublicNoticeRepository.findByIdSafe(EasyMock.anyInt()))
            .andStubReturn(java.util.Optional.of(
                uk.gov.hmcts.DummyPublicDisplayUtil.getXhbDefinitivePublicNoticeDao()
            ));


        // Public notices (delegated to provided @TestSubject PublicNoticeQuery instance)
        EasyMock.replay(mockXhbConfiguredPublicNoticeRepository, mockXhbPublicNoticeRepository,
            mockXhbDefinitivePublicNoticeRepository);

        doReplayArray(List.of(
            mockXhbScheduledHearingRepository, mockXhbCourtSiteRepository, mockXhbCourtRoomRepository,
            mockXhbHearingRepository, mockXhbCaseRepository, mockXhbCaseReferenceRepository,
            mockXhbCourtLogEntryRepository, mockXhbRefHearingTypeRepository,
            mockXhbSchedHearingDefendantRepository, mockXhbRefJudgeRepository
        ));
        
        List<XhbSittingDao> sittings = List.of(sitting);

        // --- Act (reflect private) ---
        var m = CourtDetailQuery.class.getDeclaredMethod("getSittingData", List.class, int[].class);
        m.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<CourtDetailValue> out = (List<CourtDetailValue>) m.invoke(classUnderTest, sittings, new int[]{roomId});

        // --- Assert ---
        // Only one hearing (777) survives (dedup + active filter) → one row
        org.junit.jupiter.api.Assertions.assertEquals(1, out.size());

        verifyReplayArray(List.of(
            mockXhbScheduledHearingRepository, mockXhbCourtSiteRepository, mockXhbCourtRoomRepository,
            mockXhbHearingRepository, mockXhbCaseRepository, mockXhbCaseReferenceRepository,
            mockXhbCourtLogEntryRepository, mockXhbRefHearingTypeRepository,
            mockXhbSchedHearingDefendantRepository, mockXhbRefJudgeRepository
        ));
    }

    
    @Test
    void testIsDefendantHidden_allVariants() throws Exception {
        var m = CourtDetailQuery.class.getDeclaredMethod(
            "isDefendantHidden", Optional.class, Optional.class, boolean.class);
        m.setAccessible(true);

        var def = DummyDefendantUtil.getXhbDefendantDao();
        def.setPublicDisplayHide(""); // not hidden
        var doc = DummyDefendantUtil.getXhbDefendantOnCaseDao();
        doc.setPublicDisplayHide("");  // not hidden

        // Case hidden → true
        Boolean r1 = (Boolean) m.invoke(classUnderTest, Optional.of(def), Optional.of(doc), true);
        org.junit.jupiter.api.Assertions.assertTrue(r1);

        // DOC hidden → true
        doc.setPublicDisplayHide("Y");
        Boolean r2 = (Boolean) m.invoke(classUnderTest, Optional.of(def), Optional.of(doc), false);
        org.junit.jupiter.api.Assertions.assertTrue(r2);

        // DEF hidden → true
        doc.setPublicDisplayHide("");
        def.setPublicDisplayHide("Y");
        Boolean r3 = (Boolean) m.invoke(classUnderTest, Optional.of(def), Optional.of(doc), false);
        org.junit.jupiter.api.Assertions.assertTrue(r3);

        // None hidden → false
        def.setPublicDisplayHide("");
        Boolean r4 = (Boolean) m.invoke(classUnderTest, Optional.of(def), Optional.of(doc), false);
        org.junit.jupiter.api.Assertions.assertFalse(r4);
    }

    
    @Test
    void testPopulateJudgeData_setsJudgeName() throws Exception {
        XhbScheduledHearingDao sh = DummyHearingUtil.getXhbScheduledHearingDao();
        sh.setScheduledHearingId(555);
        
        EasyMock.expect(mockXhbRefJudgeRepository.findScheduledAttendeeJudgeSafe(555))
            .andStubReturn(Optional.of(DummyJudgeUtil.getXhbRefJudgeDao()));
        doReplayArray(List.of(mockXhbRefJudgeRepository));

        CourtDetailValue row = new CourtDetailValue();
        var m = CourtDetailQuery.class.getDeclaredMethod(
            "populateJudgeData", CourtDetailValue.class, XhbScheduledHearingDao.class);
        m.setAccessible(true);
        m.invoke(classUnderTest, row, sh);

        org.junit.jupiter.api.Assertions.assertNotNull(row.getJudgeName());
        verifyReplayArray(List.of(mockXhbRefJudgeRepository));
    }

    
    
    @Test
    void testGetHearingTypeDesc_presentAndAbsent() throws Exception {
        XhbHearingDao hearing = DummyHearingUtil.getXhbHearingDao();
        hearing.setRefHearingTypeId(42);

        // present
        EasyMock.expect(mockXhbRefHearingTypeRepository.findByIdSafe(42))
            .andStubReturn(Optional.of(DummyHearingUtil.getXhbRefHearingTypeDao()));
        doReplayArray(List.of(mockXhbRefHearingTypeRepository));

        var m = CourtDetailQuery.class.getDeclaredMethod("getHearingTypeDesc", Optional.class);
        m.setAccessible(true);
        String s1 = (String) m.invoke(classUnderTest, Optional.of(hearing));
        org.junit.jupiter.api.Assertions.assertNotNull(s1);

        // absent
        EasyMock.reset(mockXhbRefHearingTypeRepository);
        EasyMock.expect(mockXhbRefHearingTypeRepository.findByIdSafe(42))
            .andStubReturn(Optional.empty());
        doReplayArray(List.of(mockXhbRefHearingTypeRepository));

        String s2 = (String) m.invoke(classUnderTest, Optional.of(hearing));
        org.junit.jupiter.api.Assertions.assertNull(s2);

        verifyReplayArray(List.of(mockXhbRefHearingTypeRepository));
    }

    
    @Test
    void testGetPublicNoticeQuery_injectedVsDefault() throws Exception {
        // 1) With injected mockPublicNoticeQuery -> returns same instance
        var m = CourtDetailQuery.class.getDeclaredMethod("getPublicNoticeQuery");
        m.setAccessible(true);
        PublicNoticeQuery q1 = (PublicNoticeQuery) m.invoke(classUnderTest);
        org.junit.jupiter.api.Assertions.assertSame(mockPublicNoticeQuery, q1);

        // 2) Without injected instance -> returns a new, non-null PublicNoticeQuery
        CourtDetailQuery withoutPn =
            new CourtDetailQuery(mockEntityManager, mockXhbCaseRepository, mockXhbCaseReferenceRepository,
                mockXhbHearingListRepository, mockXhbSittingRepository, mockXhbScheduledHearingRepository,
                mockXhbCourtSiteRepository, mockXhbCourtRoomRepository, mockXhbSchedHearingDefendantRepository,
                mockXhbHearingRepository, mockXhbDefendantOnCaseRepository, mockXhbDefendantRepository,
                mockXhbCourtLogEntryRepository, mockXhbRefHearingTypeRepository, mockXhbRefJudgeRepository,
                null);
        PublicNoticeQuery q2 = (PublicNoticeQuery) m.invoke(withoutPn);
        org.junit.jupiter.api.Assertions.assertNotNull(q2);
        org.junit.jupiter.api.Assertions.assertNotSame(mockPublicNoticeQuery, q2);
    }

}
