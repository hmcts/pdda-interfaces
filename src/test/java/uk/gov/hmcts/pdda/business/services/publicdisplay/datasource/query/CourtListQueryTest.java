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
import uk.gov.hmcts.pdda.business.entities.xhbcase.XhbCaseRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcasereference.XhbCaseReferenceDao;
import uk.gov.hmcts.pdda.business.entities.xhbcasereference.XhbCaseReferenceRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdefendant.XhbDefendantRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdefendantoncase.XhbDefendantOnCaseRepository;
import uk.gov.hmcts.pdda.business.entities.xhbhearing.XhbHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbhearing.XhbHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbhearinglist.XhbHearingListDao;
import uk.gov.hmcts.pdda.business.entities.xhbhearinglist.XhbHearingListRepository;
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
 * Title: CourtListQuery Test.
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
@SuppressWarnings({"PMD"})
class CourtListQueryTest extends AbstractQueryTest {

    private static final String TRUE = "Result is not True";

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

    @TestSubject
    private CourtListQuery classUnderTest =
        new CourtListQuery(mockEntityManager, mockXhbCaseRepository, mockXhbCaseReferenceRepository,
            mockXhbHearingListRepository, mockXhbSittingRepository, mockXhbScheduledHearingRepository,
            mockXhbCourtSiteRepository, mockXhbCourtRoomRepository, mockXhbSchedHearingDefendantRepository,
            mockXhbHearingRepository, mockXhbDefendantOnCaseRepository, mockXhbDefendantRepository);

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
            classUnderTest = new CourtListQuery(mockEntityManager);
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

    @SuppressWarnings("unused")
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
        EasyMock.expect(mockXhbHearingListRepository.findByCourtIdAndDateSafe(courtId, startDate))
            .andReturn(xhbHearingListDaoList);
        addReplayArray(replayArray, mockXhbHearingListRepository);
        if (!xhbHearingListDaoList.isEmpty()) {
            EasyMock
                .expect(mockXhbSittingRepository
                    .findByNonFloatingHearingListSafe(EasyMock.isA(Integer.class)))
                .andReturn(xhbSittingDaoList);
            addReplayArray(replayArray, mockXhbSittingRepository);
            if (!xhbSittingDaoList.isEmpty()) {
                for (XhbSittingDao xhbSittingDao : xhbSittingDaoList) {
                    expectSitting(xhbScheduledHearingDaoList, xhbSchedHearingDefendantDaoList, xhbHearingDao,
                        replayArray);
                }
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
        List<XhbSchedHearingDefendantDao> xhbSchedHearingDefendantDaoList, Optional<XhbHearingDao> xhbHearingDao,
        List<AbstractRepository<?>> replayArray) {
        EasyMock
            .expect(
                mockXhbScheduledHearingRepository.findBySittingIdSafe(EasyMock.isA(Integer.class)))
            .andReturn(xhbScheduledHearingDaoList);
        addReplayArray(replayArray, mockXhbScheduledHearingRepository);
        boolean abortExpects = xhbScheduledHearingDaoList.isEmpty();
        if (!abortExpects) {
            EasyMock.expect(mockXhbCourtSiteRepository.findByIdSafe(EasyMock.isA(Integer.class)))
                .andReturn(Optional.of(DummyCourtUtil.getXhbCourtSiteDao()));
            EasyMock.expectLastCall().anyTimes();
            addReplayArray(replayArray, mockXhbCourtSiteRepository);
            EasyMock.expect(mockXhbCourtRoomRepository.findByIdSafe(EasyMock.isA(Integer.class)))
                .andReturn(Optional.of(DummyCourtUtil.getXhbCourtRoomDao()));
            EasyMock.expectLastCall().anyTimes();
            addReplayArray(replayArray, mockXhbCourtRoomRepository);
            EasyMock
                .expect(mockXhbSchedHearingDefendantRepository
                    .findByScheduledHearingIdSafe(EasyMock.isA(Integer.class)))
                .andReturn(xhbSchedHearingDefendantDaoList);
            EasyMock.expectLastCall().anyTimes();
            addReplayArray(replayArray, mockXhbSchedHearingDefendantRepository);
            abortExpects = xhbSchedHearingDefendantDaoList.isEmpty();
        }
        if (!abortExpects) {
            EasyMock.expect(mockXhbHearingRepository.findByIdSafe(EasyMock.isA(Integer.class)))
                .andReturn(xhbHearingDao);
            EasyMock.expectLastCall().anyTimes();
            addReplayArray(replayArray, mockXhbHearingRepository);
            if (xhbHearingDao.isPresent()) {
                EasyMock.expect(mockXhbCaseRepository.findByIdSafe(EasyMock.isA(Integer.class)))
                    .andReturn(Optional.of(DummyCaseUtil.getXhbCaseDao()));
                addReplayArray(replayArray, mockXhbCaseRepository);
                List<XhbCaseReferenceDao> xhbCaseReferenceDaoList = new ArrayList<>();
                xhbCaseReferenceDaoList.add(DummyCaseUtil.getXhbCaseReferenceDao());
                EasyMock
                    .expect(mockXhbCaseReferenceRepository
                        .findByCaseIdSafe(EasyMock.isA(Integer.class)))
                    .andReturn(xhbCaseReferenceDaoList);
                addReplayArray(replayArray, mockXhbCaseReferenceRepository);
            }
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
    }
}
