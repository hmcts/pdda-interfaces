package uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.query;

import jakarta.persistence.EntityManager;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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
import uk.gov.hmcts.pdda.business.entities.xhbcase.XhbCaseRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcasereference.XhbCaseReferenceDao;
import uk.gov.hmcts.pdda.business.entities.xhbcasereference.XhbCaseReferenceRepository;
import uk.gov.hmcts.pdda.business.entities.xhbconfiguredpublicnotice.XhbConfiguredPublicNoticeDao;
import uk.gov.hmcts.pdda.business.entities.xhbconfiguredpublicnotice.XhbConfiguredPublicNoticeRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtlogentry.XhbCourtLogEntryDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtlogentry.XhbCourtLogEntryRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdefendant.XhbDefendantRepository;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * <p>
 * Title: CourtDetailQuery Test.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2023
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @author Mark Harris
 */
@ExtendWith(EasyMockExtension.class)
@SuppressWarnings({"PMD.ExcessiveImports", "PMD.TooManyFields"})
class CourtDetailQueryTest extends AbstractQueryTest {

    private static final String TRUE = "Result is not True";
    private static final Integer COURTID = Integer.valueOf(81);
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
        new PublicNoticeQuery(mockEntityManager, mockXhbConfiguredPublicNoticeRepository, mockXhbPublicNoticeRepository,
            mockXhbDefinitivePublicNoticeRepository);

    @TestSubject
    private CourtDetailQuery classUnderTest = new CourtDetailQuery(mockEntityManager, mockXhbCaseRepository,
        mockXhbCaseReferenceRepository, mockXhbHearingListRepository, mockXhbSittingRepository,
        mockXhbScheduledHearingRepository, mockXhbCourtSiteRepository, mockXhbCourtRoomRepository,
        mockXhbSchedHearingDefendantRepository, mockXhbHearingRepository, mockXhbDefendantOnCaseRepository,
        mockXhbDefendantRepository, mockXhbCourtLogEntryRepository, mockXhbRefHearingTypeRepository,
        mockXhbRefJudgeRepository, mockPublicNoticeQuery);

    @BeforeAll
    public static void setUp() {
        // Do nothing
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
        invalidXhbSittingDao.setCourtRoomId(Integer.valueOf(-1));
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

    @SuppressWarnings("unused")
    private boolean testGetDataNoList(List<XhbHearingListDao> xhbHearingListDaoList,
        List<XhbSittingDao> xhbSittingDaoList, List<XhbScheduledHearingDao> xhbScheduledHearingDaoList,
        List<XhbSchedHearingDefendantDao> xhbSchedHearingDefendantDaoList, Optional<XhbHearingDao> xhbHearingDao) {
        // Setup
        LocalDateTime date = LocalDateTime.now();
        LocalDateTime startDate = DateTimeUtilities.stripTime(date);
        List<AbstractRepository<?>> replayArray = new ArrayList<>();

        // Expects
        boolean abortExpects;
        EasyMock.expect(mockXhbHearingListRepository.findByCourtIdAndDate(COURTID, startDate))
            .andReturn(xhbHearingListDaoList);
        addReplayArray(replayArray, mockXhbHearingListRepository);
        abortExpects = xhbHearingListDaoList.isEmpty();
        if (!abortExpects) {
            EasyMock.expect(mockXhbSittingRepository.findByListId(EasyMock.isA(Integer.class)))
                .andReturn(xhbSittingDaoList);
            addReplayArray(replayArray, mockXhbSittingRepository);
            abortExpects = xhbSittingDaoList.isEmpty();
        }
        if (!abortExpects) {
            for (XhbSittingDao xhbSittingDao : xhbSittingDaoList) {
                expectSitting(xhbScheduledHearingDaoList, xhbSchedHearingDefendantDaoList, xhbHearingDao, replayArray);
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
        List<XhbSchedHearingDefendantDao> xhbSchedHearingDefendantDaoList, Optional<XhbHearingDao> xhbHearingDao,
        List<AbstractRepository<?>> replayArray) {
        EasyMock.expect(mockXhbScheduledHearingRepository.findBySittingId(EasyMock.isA(Integer.class)))
            .andReturn(xhbScheduledHearingDaoList);
        addReplayArray(replayArray, mockXhbScheduledHearingRepository);
        boolean abortExpects = xhbScheduledHearingDaoList.isEmpty();
        if (!abortExpects) {
            EasyMock.expect(mockXhbHearingRepository.findById(EasyMock.isA(Integer.class))).andReturn(xhbHearingDao);
            EasyMock.expectLastCall().anyTimes();
            addReplayArray(replayArray, mockXhbHearingRepository);
            EasyMock.expect(mockXhbCourtSiteRepository.findById(EasyMock.isA(Integer.class)))
                .andReturn(Optional.of(DummyCourtUtil.getXhbCourtSiteDao()));
            EasyMock.expectLastCall().anyTimes();
            addReplayArray(replayArray, mockXhbCourtSiteRepository);
            EasyMock.expect(mockXhbCourtRoomRepository.findById(EasyMock.isA(Integer.class)))
                .andReturn(Optional.of(DummyCourtUtil.getXhbCourtRoomDao()));
            EasyMock.expectLastCall().anyTimes();
            addReplayArray(replayArray, mockXhbCourtRoomRepository);
            abortExpects = xhbHearingDao.isEmpty();
        }
        if (!abortExpects) {
            EasyMock.expect(mockXhbCaseRepository.findById(EasyMock.isA(Integer.class)))
                .andReturn(Optional.of(DummyCaseUtil.getXhbCaseDao()));
            EasyMock.expectLastCall().anyTimes();
            addReplayArray(replayArray, mockXhbCaseRepository);
            List<XhbCaseReferenceDao> xhbCaseReferenceDaoList = DummyServicesUtil.getNewArrayList();
            xhbCaseReferenceDaoList.add(DummyCaseUtil.getXhbCaseReferenceDao());
            EasyMock.expect(mockXhbCaseReferenceRepository.findByCaseId(EasyMock.isA(Integer.class)))
                .andReturn(xhbCaseReferenceDaoList);
            addReplayArray(replayArray, mockXhbCaseReferenceRepository);
            List<XhbCourtLogEntryDao> xhbCourtLogEntryDaoList = DummyServicesUtil.getNewArrayList();
            xhbCourtLogEntryDaoList.add(DummyCourtUtil.getXhbCourtLogEntryDao());
            EasyMock.expect(mockXhbCourtLogEntryRepository.findByCaseId(EasyMock.isA(Integer.class)))
                .andReturn(xhbCourtLogEntryDaoList);
            addReplayArray(replayArray, mockXhbCourtLogEntryRepository);
            EasyMock.expect(mockXhbRefHearingTypeRepository.findById(EasyMock.isA(Integer.class)))
                .andReturn(Optional.of(DummyHearingUtil.getXhbRefHearingTypeDao()));
            addReplayArray(replayArray, mockXhbRefHearingTypeRepository);
        }

        EasyMock.expect(mockXhbSchedHearingDefendantRepository.findByScheduledHearingId(EasyMock.isA(Integer.class)))
            .andReturn(xhbSchedHearingDefendantDaoList);
        EasyMock.expectLastCall().anyTimes();
        addReplayArray(replayArray, mockXhbSchedHearingDefendantRepository);
        abortExpects = xhbSchedHearingDefendantDaoList.isEmpty();
        if (!abortExpects) {
            EasyMock.expect(mockXhbDefendantOnCaseRepository.findById(EasyMock.isA(Integer.class)))
                .andReturn(Optional.of(DummyDefendantUtil.getXhbDefendantOnCaseDao()));
            EasyMock.expectLastCall().anyTimes();
            addReplayArray(replayArray, mockXhbDefendantOnCaseRepository);
            EasyMock.expect(mockXhbDefendantRepository.findById(EasyMock.isA(Integer.class)))
                .andReturn(Optional.of(DummyDefendantUtil.getXhbDefendantDao()));
            EasyMock.expectLastCall().anyTimes();
            addReplayArray(replayArray, mockXhbDefendantRepository);
        }

        EasyMock.expect(mockXhbRefJudgeRepository.findScheduledAttendeeJudge(EasyMock.isA(Integer.class)))
            .andReturn(Optional.of(DummyJudgeUtil.getXhbRefJudgeDao()));
        EasyMock.expectLastCall().anyTimes();
        addReplayArray(replayArray, mockXhbRefJudgeRepository);

        List<XhbConfiguredPublicNoticeDao> xhbConfiguredPublicNoticeDaoList = DummyServicesUtil.getNewArrayList();
        xhbConfiguredPublicNoticeDaoList.add(DummyPdNotifierUtil.getXhbConfiguredPublicNoticeDao("0"));
        EasyMock.expect(mockXhbConfiguredPublicNoticeRepository.findActiveCourtRoomNotices(EasyMock.isA(Integer.class)))
            .andReturn(xhbConfiguredPublicNoticeDaoList);

        EasyMock.expect(mockXhbPublicNoticeRepository.findById(EasyMock.isA(Integer.class)))
            .andReturn(Optional.of(DummyPublicDisplayUtil.getXhbPublicNoticeDao()));
        EasyMock.expect(mockXhbDefinitivePublicNoticeRepository.findById(EasyMock.isA(Integer.class)))
            .andReturn(Optional.of(DummyPublicDisplayUtil.getXhbDefinitivePublicNoticeDao()));
    }
}
