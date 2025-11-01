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
import uk.gov.hmcts.pdda.business.entities.xhbconfiguredpublicnotice.XhbConfiguredPublicNoticeRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtlogentry.XhbCourtLogEntryDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtlogentry.XhbCourtLogEntryRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdefendant.XhbDefendantRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdefendantoncase.XhbDefendantOnCaseRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdefinitivepublicnotice.XhbDefinitivePublicNoticeRepository;
import uk.gov.hmcts.pdda.business.entities.xhbhearing.XhbHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbhearing.XhbHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbhearinglist.XhbHearingListDao;
import uk.gov.hmcts.pdda.business.entities.xhbhearinglist.XhbHearingListRepository;
import uk.gov.hmcts.pdda.business.entities.xhbpublicnotice.XhbPublicNoticeRepository;
import uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant.XhbSchedHearingDefendantDao;
import uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant.XhbSchedHearingDefendantRepository;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingDao;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(EasyMockExtension.class)
@SuppressWarnings({"PMD"})
class AllCourtStatusQueryTest extends AbstractQueryTest {

    private static final String TRUE = "Result is not True";

    @Mock private EntityManager mockEntityManager;
    @Mock private XhbCaseRepository mockXhbCaseRepository;
    @Mock private XhbCaseReferenceRepository mockXhbCaseReferenceRepository;
    @Mock private XhbHearingListRepository mockXhbHearingListRepository;
    @Mock private XhbSittingRepository mockXhbSittingRepository;
    @Mock private XhbScheduledHearingRepository mockXhbScheduledHearingRepository;
    @Mock private XhbCourtSiteRepository mockXhbCourtSiteRepository;
    @Mock private XhbCourtRoomRepository mockXhbCourtRoomRepository;
    @Mock private XhbSchedHearingDefendantRepository mockXhbSchedHearingDefendantRepository;
    @Mock private XhbHearingRepository mockXhbHearingRepository;
    @Mock private XhbDefendantOnCaseRepository mockXhbDefendantOnCaseRepository;
    @Mock private XhbDefendantRepository mockXhbDefendantRepository;
    @Mock private XhbCourtLogEntryRepository mockXhbCourtLogEntryRepository;
    @Mock private XhbConfiguredPublicNoticeRepository mockXhbConfiguredPublicNoticeRepository;
    @Mock private XhbPublicNoticeRepository mockXhbPublicNoticeRepository;
    @Mock private XhbDefinitivePublicNoticeRepository mockXhbDefinitivePublicNoticeRepository;

    @TestSubject
    private AllCourtStatusQuery classUnderTest = new AllCourtStatusQuery(
        mockEntityManager, mockXhbCaseRepository, mockXhbCaseReferenceRepository,
        mockXhbHearingListRepository, mockXhbSittingRepository,
        mockXhbScheduledHearingRepository, mockXhbCourtSiteRepository,
        mockXhbCourtRoomRepository, mockXhbSchedHearingDefendantRepository,
        mockXhbHearingRepository, mockXhbDefendantOnCaseRepository,
        mockXhbDefendantRepository, mockXhbCourtLogEntryRepository);

    @BeforeAll
    static void setUp() {
        /* no-op */
    }

    @BeforeEach
    void setupEntityManager() {
        EasyMock.expect(mockEntityManager.isOpen()).andReturn(true).anyTimes();
        EasyMock.replay(mockEntityManager);
    }

    @AfterAll
    static void tearDown() {
        /* no-op */
    }

    @Test
    void testDefaultConstructor() {
        boolean result = false;
        try {
            classUnderTest = new AllCourtStatusQuery(mockEntityManager);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }

    @Test
    void testGetDataNoListEmpty() {
        boolean result = testGetDataNoList(new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(), Optional.empty());
        assertTrue(result, TRUE);
    }

    @Test
    void testGetDataNoListNoSittings() {
        List<XhbHearingListDao> lists = new ArrayList<>();
        lists.add(DummyHearingUtil.getXhbHearingListDao());
        boolean result = testGetDataNoList(lists, new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(), Optional.empty());
        assertTrue(result, TRUE);
    }

    @Test
    void testGetDataNoListNoScheduledHearings() {
        List<XhbHearingListDao> lists = new ArrayList<>();
        lists.add(DummyHearingUtil.getXhbHearingListDao());
        List<XhbSittingDao> sittings = new ArrayList<>();
        sittings.add(DummyHearingUtil.getXhbSittingDao());
        boolean result = testGetDataNoList(lists, sittings,
            new ArrayList<>(), new ArrayList<>(), Optional.empty());
        assertTrue(result, TRUE);
    }

    @Test
    void testGetDataNoListNoScheduledHearingDefendants() {
        List<XhbHearingListDao> lists = new ArrayList<>();
        lists.add(DummyHearingUtil.getXhbHearingListDao());
        List<XhbSittingDao> sittings = new ArrayList<>();
        sittings.add(DummyHearingUtil.getXhbSittingDao());
        XhbSittingDao invalid = DummyHearingUtil.getXhbSittingDao();
        invalid.setCourtRoomId(-1);
        sittings.add(invalid);
        List<XhbScheduledHearingDao> scheds = new ArrayList<>();
        scheds.add(DummyHearingUtil.getXhbScheduledHearingDao());
        boolean result = testGetDataNoList(lists, sittings,
            scheds, new ArrayList<>(), Optional.empty());
        assertTrue(result, TRUE);
    }

    @Test
    void testGetDataNoListNoHearing() {
        List<XhbHearingListDao> lists = new ArrayList<>();
        lists.add(DummyHearingUtil.getXhbHearingListDao());
        List<XhbSittingDao> sittings = new ArrayList<>();
        sittings.add(DummyHearingUtil.getXhbSittingDao());
        List<XhbScheduledHearingDao> scheds = new ArrayList<>();
        scheds.add(DummyHearingUtil.getXhbScheduledHearingDao());
        List<XhbSchedHearingDefendantDao> shds = new ArrayList<>();
        shds.add(DummyHearingUtil.getXhbSchedHearingDefendantDao());
        boolean result = testGetDataNoList(lists, sittings,
            scheds, shds, Optional.empty());
        assertTrue(result, TRUE);
    }

    @Test
    void testGetDataNoListSuccess() {
        List<XhbHearingListDao> lists = new ArrayList<>();
        lists.add(DummyHearingUtil.getXhbHearingListDao());
        List<XhbSittingDao> sittings = new ArrayList<>();
        sittings.add(DummyHearingUtil.getXhbSittingDao());
        List<XhbScheduledHearingDao> scheds = new ArrayList<>();
        scheds.add(DummyHearingUtil.getXhbScheduledHearingDao());
        List<XhbSchedHearingDefendantDao> shds = new ArrayList<>();
        shds.add(DummyHearingUtil.getXhbSchedHearingDefendantDao());
        boolean result = testGetDataNoList(lists, sittings, scheds, shds,
            Optional.of(DummyHearingUtil.getXhbHearingDao()));
        assertTrue(result, TRUE);
    }

    private boolean testGetDataNoList(
        List<XhbHearingListDao> xhbHearingListDaoList,
        List<XhbSittingDao> xhbSittingDaoList,
        List<XhbScheduledHearingDao> xhbScheduledHearingDaoList,
        List<XhbSchedHearingDefendantDao> xhbSchedHearingDefendantDaoList,
        Optional<XhbHearingDao> xhbHearingDao) {

        // Setup
        LocalDateTime date = LocalDateTime.now();
        LocalDateTime startDate = DateTimeUtilities.stripTime(date);
        Integer courtId = 81;
        final int[] courtRoomIds = {8112, 8113, 8114};
        List<AbstractRepository<?>> replayArray = new ArrayList<>();

        // --- NEW expectations required by addEmptyCourtroomsIfMissing()/resolveSiteIds… ---
        XhbCourtSiteDao site = DummyCourtUtil.getXhbCourtSiteDao();
        XhbCourtRoomDao room = DummyCourtUtil.getXhbCourtRoomDao();

        expectFindSitesAndRooms(courtId,
            Collections.singletonList(site),
            Collections.singletonList(room),
            replayArray);
        // -------------------------------------------------------------------------------

        // Existing expectations
        boolean abortExpects;
        EasyMock.expect(mockXhbHearingListRepository.findByCourtIdAndDateSafe(courtId, startDate))
            .andReturn(xhbHearingListDaoList);
        replayArray.add(mockXhbHearingListRepository);
        abortExpects = xhbHearingListDaoList.isEmpty();

        if (!abortExpects) {
            EasyMock.expect(mockXhbSittingRepository.findByListIdSafe(EasyMock.isA(Integer.class)))
                .andReturn(xhbSittingDaoList);
            replayArray.add(mockXhbSittingRepository);
            abortExpects = xhbSittingDaoList.isEmpty();
        }
        if (!abortExpects) {
            EasyMock.expect(mockXhbScheduledHearingRepository.findBySittingIdSafe(EasyMock.isA(Integer.class)))
                .andReturn(xhbScheduledHearingDaoList);
            EasyMock.expectLastCall().anyTimes();
            replayArray.add(mockXhbScheduledHearingRepository);
            abortExpects = xhbScheduledHearingDaoList.isEmpty();
        }
        if (!abortExpects) {
            EasyMock.expect(mockXhbCourtSiteRepository.findByIdSafe(EasyMock.isA(Integer.class)))
                .andReturn(Optional.of(site));
            EasyMock.expectLastCall().anyTimes();
            if (!replayArray.contains(mockXhbCourtSiteRepository)) {
                replayArray.add(mockXhbCourtSiteRepository);
            }

            EasyMock.expect(mockXhbCourtRoomRepository.findByIdSafe(EasyMock.isA(Integer.class)))
                .andReturn(Optional.of(room));
            EasyMock.expectLastCall().anyTimes();
            if (!replayArray.contains(mockXhbCourtRoomRepository)) {
                replayArray.add(mockXhbCourtRoomRepository);
            }

            EasyMock.expect(mockXhbHearingRepository.findByIdSafe(EasyMock.isA(Integer.class)))
                .andReturn(xhbHearingDao);
            EasyMock.expectLastCall().anyTimes();
            replayArray.add(mockXhbHearingRepository);

            if (xhbHearingDao.isPresent()) {
                List<XhbCaseReferenceDao> refs = new ArrayList<>();
                refs.add(DummyCaseUtil.getXhbCaseReferenceDao());
                EasyMock.expect(mockXhbCaseReferenceRepository.findByCaseIdSafe(EasyMock.isA(Integer.class)))
                    .andReturn(refs);
                replayArray.add(mockXhbCaseReferenceRepository);

                EasyMock.expect(mockXhbCaseRepository.findByIdSafe(EasyMock.isA(Integer.class)))
                    .andReturn(Optional.of(DummyCaseUtil.getXhbCaseDao()));
                EasyMock.expectLastCall().anyTimes();
                replayArray.add(mockXhbCaseRepository);

                List<XhbCourtLogEntryDao> entries = new ArrayList<>();
                entries.add(DummyCourtUtil.getXhbCourtLogEntryDao());
                EasyMock.expect(mockXhbCourtLogEntryRepository.findByCaseIdSafe(EasyMock.isA(Integer.class)))
                    .andReturn(entries);
                replayArray.add(mockXhbCourtLogEntryRepository);
            }

            EasyMock.expect(mockXhbSchedHearingDefendantRepository
                    .findByScheduledHearingIdSafe(EasyMock.isA(Integer.class)))
                .andReturn(xhbSchedHearingDefendantDaoList);
            replayArray.add(mockXhbSchedHearingDefendantRepository);
            EasyMock.expectLastCall().anyTimes();

            if (!xhbSchedHearingDefendantDaoList.isEmpty()) {
                EasyMock.expect(mockXhbDefendantOnCaseRepository.findByIdSafe(EasyMock.isA(Integer.class)))
                    .andReturn(Optional.of(DummyCaseUtil.getXhbDefendantOnCaseDao()));
                EasyMock.expectLastCall().anyTimes();
                replayArray.add(mockXhbDefendantOnCaseRepository);

                EasyMock.expect(mockXhbDefendantRepository.findByIdSafe(EasyMock.isA(Integer.class)))
                    .andReturn(Optional.of(DummyDefendantUtil.getXhbDefendantDao()));
                EasyMock.expectLastCall().anyTimes();
                replayArray.add(mockXhbDefendantRepository);
            }
        }

        // Replays
        doReplayArray(replayArray);

        // Run
        classUnderTest.getData(date, courtId, courtRoomIds);

        // Verify
        verifyReplayArray(replayArray);
        return true;
    }

    /**
     * Expect the new “resolve sites → rooms” calls.
     */
    private void expectFindSitesAndRooms(
        int courtId,
        List<XhbCourtSiteDao> sites,
        List<XhbCourtRoomDao> rooms,
        List<AbstractRepository<?>> replayArray) {

        EasyMock.expect(mockXhbCourtSiteRepository.findByCourtIdSafe(courtId))
            .andReturn(sites).anyTimes();
        if (!replayArray.contains(mockXhbCourtSiteRepository)) {
            replayArray.add(mockXhbCourtSiteRepository);
        }

        for (XhbCourtSiteDao s : sites) {
            EasyMock.expect(mockXhbCourtRoomRepository.findByCourtSiteIdSafe(s.getCourtSiteId()))
                .andReturn(rooms).anyTimes();
        }
        if (!replayArray.contains(mockXhbCourtRoomRepository)) {
            replayArray.add(mockXhbCourtRoomRepository);
        }
    }
}
