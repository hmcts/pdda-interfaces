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
import uk.gov.hmcts.framework.util.DateTimeUtilities;
import uk.gov.hmcts.pdda.business.entities.AbstractRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcase.XhbCaseRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcasereference.XhbCaseReferenceDao;
import uk.gov.hmcts.pdda.business.entities.xhbcasereference.XhbCaseReferenceRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtlogentry.XhbCourtLogEntryDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtlogentry.XhbCourtLogEntryRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdefendant.XhbDefendantRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdefendantoncase.XhbDefendantOnCaseRepository;
import uk.gov.hmcts.pdda.business.entities.xhbhearing.XhbHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbhearing.XhbHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbhearinglist.XhbHearingListDao;
import uk.gov.hmcts.pdda.business.entities.xhbhearinglist.XhbHearingListRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrefhearingtype.XhbRefHearingTypeRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrefjudge.XhbRefJudgeDao;
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
 * Title: JuryStatusDailyListQuery Test.
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
@SuppressWarnings({"PMD.ExcessiveImports", "PMD.TooManyFields", "PMD.CouplingBetweenObjects",
    "PMD.TooManyMethods"})
class JuryStatusDailyListQueryTest extends AbstractQueryTest {

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

    @Mock
    protected XhbRefJudgeRepository mockXhbRefJudgeRepository;

    @TestSubject
    protected JuryStatusDailyListQuery classUnderTest = getClassUnderTest();

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

    protected JuryStatusDailyListQuery getClassUnderTest() {
        return new JuryStatusDailyListQuery(mockEntityManager, mockXhbCaseRepository, mockXhbCaseReferenceRepository,
            mockXhbHearingListRepository, mockXhbSittingRepository, mockXhbScheduledHearingRepository,
            mockXhbCourtSiteRepository, mockXhbCourtRoomRepository, mockXhbSchedHearingDefendantRepository,
            mockXhbHearingRepository, mockXhbDefendantOnCaseRepository, mockXhbDefendantRepository,
            mockXhbCourtLogEntryRepository, mockXhbRefHearingTypeRepository, mockXhbRefJudgeRepository);
    }

    @Test
    void testDefaultConstructor() {
        boolean result = false;
        try {
            classUnderTest = new JuryStatusDailyListQuery(mockEntityManager);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }

    @Test
    void testGetDataNoListEmpty() {
        boolean result = testGetDataNoList(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
            Optional.empty(), Optional.empty());
        assertTrue(result, TRUE);
    }

    @Test
    void testGetDataNoListNoSittings() {
        List<XhbHearingListDao> xhbHearingListDaoList = new ArrayList<>();
        xhbHearingListDaoList.add(DummyHearingUtil.getXhbHearingListDao());
        boolean result = testGetDataNoList(xhbHearingListDaoList, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), Optional.empty(), Optional.of(DummyJudgeUtil.getXhbRefJudgeDao()));
        assertTrue(result, TRUE);
    }

    @Test
    void testGetDataNoListNoScheduledHearings() {
        List<XhbHearingListDao> xhbHearingListDaoList = new ArrayList<>();
        xhbHearingListDaoList.add(DummyHearingUtil.getXhbHearingListDao());
        List<XhbSittingDao> xhbSittingDaoList = new ArrayList<>();
        xhbSittingDaoList.add(DummyHearingUtil.getXhbSittingDao());
        boolean result = testGetDataNoList(xhbHearingListDaoList, xhbSittingDaoList, new ArrayList<>(),
            new ArrayList<>(), Optional.empty(), Optional.of(DummyJudgeUtil.getXhbRefJudgeDao()));
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
            new ArrayList<>(), Optional.empty(), Optional.of(DummyJudgeUtil.getXhbRefJudgeDao()));
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
            xhbSchedHearingDefendantDaoList, Optional.empty(), Optional.empty());
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
            xhbSchedHearingDefendantDaoList, Optional.of(DummyHearingUtil.getXhbHearingDao()),
            Optional.of(DummyJudgeUtil.getXhbRefJudgeDao()));
        assertTrue(result, TRUE);
    }

    private boolean testGetDataNoList(List<XhbHearingListDao> xhbHearingListDaoList,
        List<XhbSittingDao> xhbSittingDaoList, List<XhbScheduledHearingDao> xhbScheduledHearingDaoList,
        List<XhbSchedHearingDefendantDao> xhbSchedHearingDefendantDaoList, Optional<XhbHearingDao> xhbHearingDao,
        Optional<XhbRefJudgeDao> refJudgeDao) {
        // Setup
        LocalDateTime date = LocalDateTime.now();
        LocalDateTime startDate = DateTimeUtilities.stripTime(date);
        final Integer courtId = 81;
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
                EasyMock.expect(mockXhbSittingRepository.findByListId(EasyMock.isA(Integer.class)))
                    .andReturn(xhbSittingDaoList);
            } else {
                EasyMock.expect(mockXhbSittingRepository.findByNonFloatingHearingList(EasyMock.isA(Integer.class)))
                    .andReturn(xhbSittingDaoList);
            }
            addReplayArray(replayArray, mockXhbSittingRepository);
            abortExpects = xhbSittingDaoList.isEmpty();
        }
        if (!abortExpects) {
            EasyMock.expect(mockXhbScheduledHearingRepository.findBySittingId(EasyMock.isA(Integer.class)))
                .andReturn(xhbScheduledHearingDaoList);
            EasyMock.expectLastCall().anyTimes();
            addReplayArray(replayArray, mockXhbScheduledHearingRepository);
            abortExpects = xhbScheduledHearingDaoList.isEmpty();
        }
        if (!abortExpects) {
            expectCourtSite(xhbSchedHearingDefendantDaoList, xhbHearingDao, replayArray, refJudgeDao);
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
        Optional<XhbHearingDao> xhbHearingDao, List<AbstractRepository<?>> replayArray,
        Optional<XhbRefJudgeDao> refJudgeDao) {
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
            EasyMock.expect(mockXhbCaseReferenceRepository.findByCaseId(EasyMock.isA(Integer.class)))
                .andReturn(xhbCaseReferenceDaoList);
            addReplayArray(replayArray, mockXhbCaseReferenceRepository);
            List<XhbCourtLogEntryDao> xhbCourtLogEntryDaoList = new ArrayList<>();
            xhbCourtLogEntryDaoList.add(DummyCourtUtil.getXhbCourtLogEntryDao());
            EasyMock.expect(mockXhbCourtLogEntryRepository.findByCaseId(EasyMock.isA(Integer.class)))
                .andReturn(xhbCourtLogEntryDaoList);
            EasyMock.expectLastCall().anyTimes();
            addReplayArray(replayArray, mockXhbCourtLogEntryRepository);
            EasyMock
                .expect(mockXhbRefHearingTypeRepository.findByIdSafe(EasyMock.isA(Integer.class)))
                .andReturn(Optional.of(DummyHearingUtil.getXhbRefHearingTypeDao()));
            EasyMock.expectLastCall().anyTimes();
            addReplayArray(replayArray, mockXhbRefHearingTypeRepository);
        }
        EasyMock.expect(mockXhbSchedHearingDefendantRepository.findByScheduledHearingId(EasyMock.isA(Integer.class)))
            .andReturn(xhbSchedHearingDefendantDaoList);
        EasyMock.expectLastCall().anyTimes();
        addReplayArray(replayArray, mockXhbSchedHearingDefendantRepository);
        if (!xhbSchedHearingDefendantDaoList.isEmpty()) {
            EasyMock
                .expect(mockXhbDefendantOnCaseRepository.findByIdSafe(EasyMock.isA(Integer.class)))
                .andReturn(Optional.of(DummyDefendantUtil.getXhbDefendantOnCaseDao()));
            EasyMock.expectLastCall().anyTimes();
            addReplayArray(replayArray, mockXhbDefendantOnCaseRepository);
            EasyMock.expect(mockXhbDefendantRepository.findByIdSafe(EasyMock.isA(Integer.class)))
                .andReturn(Optional.of(DummyDefendantUtil.getXhbDefendantDao()));
            EasyMock.expectLastCall().anyTimes();
            addReplayArray(replayArray, mockXhbDefendantRepository);
        }
        EasyMock.expect(mockXhbRefJudgeRepository.findScheduledAttendeeJudge(EasyMock.isA(Integer.class)))
            .andReturn(refJudgeDao);
        EasyMock.expectLastCall().anyTimes();
        if (refJudgeDao.isEmpty()) {
            EasyMock.expect(mockXhbRefJudgeRepository.findScheduledSittingJudge(EasyMock.isA(Integer.class)))
                .andReturn(refJudgeDao);
        }
        addReplayArray(replayArray, mockXhbRefJudgeRepository);
    }
}
