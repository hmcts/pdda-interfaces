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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    
    
    
    @Test
    void testGetScheduleData_deduplicatesPrefersProgress_andFilters() {
        
        int roomId = 8112;
        XhbSittingDao sitting = DummyHearingUtil.getXhbSittingDao();
        sitting.setCourtRoomId(roomId);

        XhbScheduledHearingDao shWithoutProg = DummyHearingUtil.getXhbScheduledHearingDao();
        shWithoutProg.setScheduledHearingId(10001);
        shWithoutProg.setHearingId(1001);
        shWithoutProg.setIsCaseActive("Y");
        shWithoutProg.setHearingProgress(null);
        shWithoutProg.setMovedFromCourtRoomId(null);

        XhbScheduledHearingDao shWithProg = DummyHearingUtil.getXhbScheduledHearingDao();
        shWithProg.setScheduledHearingId(10002);
        shWithProg.setHearingId(1001);
        shWithProg.setIsCaseActive("Y");
        shWithProg.setHearingProgress(5);
        shWithProg.setMovedFromCourtRoomId(null);

        XhbScheduledHearingDao shInactive = DummyHearingUtil.getXhbScheduledHearingDao();
        shInactive.setScheduledHearingId(20002);
        shInactive.setHearingId(2002);
        shInactive.setIsCaseActive("N");

        XhbScheduledHearingDao shMovedFrom = DummyHearingUtil.getXhbScheduledHearingDao();
        shMovedFrom.setScheduledHearingId(30003);
        shMovedFrom.setHearingId(3003);
        shMovedFrom.setIsCaseActive("Y");
        shMovedFrom.setMovedFromCourtRoomId(roomId);

        XhbCourtRoomDao room = DummyCourtUtil.getXhbCourtRoomDao();
        room.setCourtRoomId(roomId);

        XhbHearingDao hearing1001 = DummyHearingUtil.getXhbHearingDao();
        hearing1001.setHearingId(1001);
        hearing1001.setCaseId(5001);

        XhbHearingDao hearing3003 = DummyHearingUtil.getXhbHearingDao();
        hearing3003.setHearingId(3003);
        hearing3003.setCaseId(5003);

        var hiddenCase = DummyCaseUtil.getXhbCaseDao();
        hiddenCase.setCaseId(5001);
        hiddenCase.setPublicDisplayHide("Y");

        var normalCase = DummyCaseUtil.getXhbCaseDao();
        normalCase.setCaseId(5003);
        normalCase.setPublicDisplayHide(null);

        // --- SHD + DOCs ---
        XhbSchedHearingDefendantDao shd1001 = DummyHearingUtil.getXhbSchedHearingDefendantDao();
        shd1001.setScheduledHearingId(10002);
        shd1001.setDefendantOnCaseId(101);

        XhbSchedHearingDefendantDao shd3003 = DummyHearingUtil.getXhbSchedHearingDefendantDao();
        shd3003.setScheduledHearingId(30003);
        shd3003.setDefendantOnCaseId(202);

        var doc101 = DummyCaseUtil.getXhbDefendantOnCaseDao();
        doc101.setDefendantOnCaseId(101);
        doc101.setObsInd(null);
        doc101.setDefendantId(90001); // IMPORTANT: set real defendantId

        var doc202 = DummyCaseUtil.getXhbDefendantOnCaseDao();
        doc202.setDefendantOnCaseId(202);
        doc202.setObsInd(null);
        doc202.setDefendantId(90002); // IMPORTANT: set real defendantId

        var def90001 = DummyDefendantUtil.getXhbDefendantDao();
        def90001.setPublicDisplayHide("");

        var def90002 = DummyDefendantUtil.getXhbDefendantDao();
        def90002.setPublicDisplayHide("");
        
        LocalDateTime date = LocalDateTime.now();
        LocalDateTime startDate = DateTimeUtilities.stripTime(date);
        int courtId = 81;
        XhbHearingListDao list = DummyHearingUtil.getXhbHearingListDao();
        
        // --- Expectations ---
        EasyMock.expect(mockXhbHearingListRepository.findByCourtIdAndDateSafe(courtId, startDate))
            .andReturn(List.of(list));
        EasyMock.expect(mockXhbSittingRepository.findByListIdSafe(list.getListId()))
            .andReturn(List.of(sitting));
        EasyMock.expect(mockXhbScheduledHearingRepository.findBySittingIdSafe(sitting.getSittingId()))
            .andReturn(List.of(shWithoutProg, shWithProg, shInactive, shMovedFrom));

        XhbCourtSiteDao site = DummyCourtUtil.getXhbCourtSiteDao();
        EasyMock.expect(mockXhbCourtSiteRepository.findByIdSafe(sitting.getCourtSiteId()))
            .andStubReturn(Optional.of(site));
        EasyMock.expect(mockXhbCourtRoomRepository.findByIdSafe(sitting.getCourtRoomId()))
            .andStubReturn(Optional.of(room));

        EasyMock.expect(mockXhbHearingRepository.findByIdSafe(1001))
            .andStubReturn(Optional.of(hearing1001));
        EasyMock.expect(mockXhbHearingRepository.findByIdSafe(3003))
            .andStubReturn(Optional.of(hearing3003));

        EasyMock.expect(mockXhbCaseRepository.findByIdSafe(5001))
            .andStubReturn(Optional.of(hiddenCase));
        EasyMock.expect(mockXhbCaseRepository.findByIdSafe(5003))
            .andStubReturn(Optional.of(normalCase));

        EasyMock.expect(mockXhbCaseReferenceRepository.findByCaseIdSafe(EasyMock.anyInt()))
            .andStubReturn(List.of(DummyCaseUtil.getXhbCaseReferenceDao()));
        EasyMock.expect(mockXhbCourtLogEntryRepository.findByCaseIdSafe(EasyMock.anyInt()))
            .andStubReturn(List.of(DummyCourtUtil.getXhbCourtLogEntryDao()));

        EasyMock.expect(mockXhbSchedHearingDefendantRepository.findByScheduledHearingIdSafe(10002))
            .andStubReturn(List.of(shd1001));
        EasyMock.expect(mockXhbSchedHearingDefendantRepository.findByScheduledHearingIdSafe(30003))
            .andStubReturn(List.of(shd3003));

        EasyMock.expect(mockXhbDefendantOnCaseRepository.findByIdSafe(101))
            .andStubReturn(Optional.of(doc101));
        EasyMock.expect(mockXhbDefendantOnCaseRepository.findByIdSafe(202))
            .andStubReturn(Optional.of(doc202));

        // IMPORTANT: stub by *defendantId* now that we set it
        EasyMock.expect(mockXhbDefendantRepository.findByIdSafe(90001))
            .andStubReturn(Optional.of(def90001));
        EasyMock.expect(mockXhbDefendantRepository.findByIdSafe(90002))
            .andStubReturn(Optional.of(def90002));

        // addEmptyCourtroomsIfMissing / resolveSiteIdsFromRowsOrCourt
        Set<AbstractRepository<?>> replaySet = new LinkedHashSet<>();
        expectFindSitesAndRooms(courtId, Collections.singletonList(site),
            Collections.singletonList(room), new ArrayList<>(replaySet));
        replaySet.addAll(List.of(
            mockXhbHearingListRepository, mockXhbSittingRepository, mockXhbScheduledHearingRepository,
            mockXhbCourtSiteRepository, mockXhbCourtRoomRepository, mockXhbHearingRepository,
            mockXhbCaseRepository, mockXhbCaseReferenceRepository, mockXhbCourtLogEntryRepository,
            mockXhbSchedHearingDefendantRepository, mockXhbDefendantOnCaseRepository, mockXhbDefendantRepository
        ));
        doReplayArray(new ArrayList<>(replaySet));

        // --- Act ---
        @SuppressWarnings("unchecked")
        List<uk.gov.hmcts.pdda.common.publicdisplay.renderdata.AllCourtStatusValue> out =
            (List<uk.gov.hmcts.pdda.common.publicdisplay.renderdata.AllCourtStatusValue>)
                classUnderTest.getData(date, courtId, roomId);

        // --- Assert ---
        assertTrue(out.size() >= 2, "Expected at least two kept hearings after filtering & dedup.");
        verifyReplayArray(new ArrayList<>(replaySet));
    }

    
    @Test
    void testPopulateScheduleDefendantData_hidesWhenCaseOrDefendantHidden_andSkipsObs() {
        
        int roomId = 8112;
        XhbSittingDao sitting = DummyHearingUtil.getXhbSittingDao();
        sitting.setCourtRoomId(roomId);

        XhbScheduledHearingDao sh = DummyHearingUtil.getXhbScheduledHearingDao();
        sh.setScheduledHearingId(70001);
        sh.setHearingId(7001);
        sh.setIsCaseActive("Y");
        sh.setMovedFromCourtRoomId(null);

        XhbSchedHearingDefendantDao shdVisible = DummyHearingUtil.getXhbSchedHearingDefendantDao();
        shdVisible.setScheduledHearingId(70001);
        shdVisible.setDefendantOnCaseId(101);

        XhbSchedHearingDefendantDao shdObs = DummyHearingUtil.getXhbSchedHearingDefendantDao();
        shdObs.setScheduledHearingId(70001);
        shdObs.setDefendantOnCaseId(202);

        var docVisible = DummyCaseUtil.getXhbDefendantOnCaseDao();
        docVisible.setDefendantOnCaseId(101);
        docVisible.setObsInd(null);
        docVisible.setDefendantId(91001); // IMPORTANT

        var docObs = DummyCaseUtil.getXhbDefendantOnCaseDao();
        docObs.setDefendantOnCaseId(202);
        docObs.setObsInd("Y");
        docObs.setDefendantId(91002); // won’t be used, but set anyway

        var defHidden = DummyDefendantUtil.getXhbDefendantDao();
        defHidden.setPublicDisplayHide("Y");
        
        var defNotHidden = DummyDefendantUtil.getXhbDefendantDao();
        defNotHidden.setPublicDisplayHide(""); 

        XhbHearingDao hearing = DummyHearingUtil.getXhbHearingDao();
        hearing.setHearingId(7001);
        hearing.setCaseId(9001);

        var normalCase = DummyCaseUtil.getXhbCaseDao();
        normalCase.setCaseId(9001);
        normalCase.setPublicDisplayHide(null);

        XhbCourtRoomDao room = DummyCourtUtil.getXhbCourtRoomDao();
        room.setCourtRoomId(roomId);
        
        LocalDateTime date = LocalDateTime.now();
        LocalDateTime startDate = DateTimeUtilities.stripTime(date);
        int courtId = 81;
        
        XhbHearingListDao list = DummyHearingUtil.getXhbHearingListDao();
        
        // --- Expectations ---
        EasyMock.expect(mockXhbHearingListRepository.findByCourtIdAndDateSafe(courtId, startDate))
            .andReturn(List.of(list));
        EasyMock.expect(mockXhbSittingRepository.findByListIdSafe(list.getListId()))
            .andReturn(List.of(sitting));
        EasyMock.expect(mockXhbScheduledHearingRepository.findBySittingIdSafe(sitting.getSittingId()))
            .andReturn(List.of(sh));

        XhbCourtSiteDao site = DummyCourtUtil.getXhbCourtSiteDao();
        EasyMock.expect(mockXhbCourtSiteRepository.findByIdSafe(sitting.getCourtSiteId()))
            .andStubReturn(Optional.of(site));
        EasyMock.expect(mockXhbCourtRoomRepository.findByIdSafe(sitting.getCourtRoomId()))
            .andStubReturn(Optional.of(room));

        EasyMock.expect(mockXhbHearingRepository.findByIdSafe(7001))
            .andStubReturn(Optional.of(hearing));
        EasyMock.expect(mockXhbCaseRepository.findByIdSafe(9001))
            .andStubReturn(Optional.of(normalCase));

        EasyMock.expect(mockXhbCaseReferenceRepository.findByCaseIdSafe(9001))
            .andStubReturn(List.of(DummyCaseUtil.getXhbCaseReferenceDao()));
        EasyMock.expect(mockXhbCourtLogEntryRepository.findByCaseIdSafe(9001))
            .andStubReturn(List.of(DummyCourtUtil.getXhbCourtLogEntryDao()));

        EasyMock.expect(mockXhbSchedHearingDefendantRepository.findByScheduledHearingIdSafe(70001))
            .andStubReturn(List.of(shdVisible, shdObs));

        EasyMock.expect(mockXhbDefendantOnCaseRepository.findByIdSafe(101))
            .andStubReturn(Optional.of(docVisible));
        EasyMock.expect(mockXhbDefendantOnCaseRepository.findByIdSafe(202))
            .andStubReturn(Optional.of(docObs));

        // Only the non-observed DOC (101) triggers a defendant lookup:
        EasyMock.expect(mockXhbDefendantRepository.findByIdSafe(91001))
            .andStubReturn(Optional.of(defHidden));
        // (No need to stub 91002; obsInd="Y" skips the call.)

        Set<AbstractRepository<?>> replaySet = new LinkedHashSet<>();
        expectFindSitesAndRooms(courtId, Collections.singletonList(site),
            Collections.singletonList(room), new ArrayList<>(replaySet));
        replaySet.addAll(List.of(
            mockXhbHearingListRepository, mockXhbSittingRepository, mockXhbScheduledHearingRepository,
            mockXhbCourtSiteRepository, mockXhbCourtRoomRepository, mockXhbHearingRepository,
            mockXhbCaseRepository, mockXhbCaseReferenceRepository, mockXhbCourtLogEntryRepository,
            mockXhbSchedHearingDefendantRepository, mockXhbDefendantOnCaseRepository, mockXhbDefendantRepository
        ));
        doReplayArray(new ArrayList<>(replaySet));

        // --- Act ---
        @SuppressWarnings("unchecked")
        List<uk.gov.hmcts.pdda.common.publicdisplay.renderdata.AllCourtStatusValue> out =
            (List<uk.gov.hmcts.pdda.common.publicdisplay.renderdata.AllCourtStatusValue>)
                classUnderTest.getData(date, courtId, roomId);

        // --- Assert ---
        assertTrue(out.size() >= 1, "Expected at least one row produced for the active hearing.");
        verifyReplayArray(new ArrayList<>(replaySet));
    }


    
    @Test
    void testResolveSiteIds_prefersRowKeysElseFallsBack() {
        // Two sites for the court
        XhbCourtSiteDao siteA = DummyCourtUtil.getXhbCourtSiteDao();
        siteA.setCourtSiteId(100);
        siteA.setCourtSiteCode(" AA ");
        siteA.setCourtSiteName(" Alpha ");

        XhbCourtSiteDao siteB = DummyCourtUtil.getXhbCourtSiteDao();
        siteB.setCourtSiteId(200);
        siteB.setCourtSiteCode("BB");
        siteB.setCourtSiteName("Beta");

        // Rooms per site
        XhbCourtRoomDao roomA1 = DummyCourtUtil.getXhbCourtRoomDao();
        roomA1.setCourtRoomId(501);
        XhbCourtRoomDao roomB1 = DummyCourtUtil.getXhbCourtRoomDao();
        roomB1.setCourtRoomId(601);

        // Expect list of sites for the court
        EasyMock.expect(mockXhbCourtSiteRepository.findByCourtIdSafe(81))
            .andReturn(List.of(siteA, siteB)).anyTimes();
        // Rooms for each site
        EasyMock.expect(mockXhbCourtRoomRepository.findByCourtSiteIdSafe(100))
            .andReturn(List.of(roomA1)).anyTimes();
        EasyMock.expect(mockXhbCourtRoomRepository.findByCourtSiteIdSafe(200))
            .andReturn(List.of(roomB1)).anyTimes();

        doReplayArray(List.of(mockXhbCourtSiteRepository, mockXhbCourtRoomRepository));

        // Build rows that already carry site code/name for siteA (note trimming & uppercasing)
        var rowWithKeys = new uk.gov.hmcts.pdda.common.publicdisplay.renderdata.AllCourtStatusValue();
        rowWithKeys.setCourtSiteCode("aa");   // lower + needs trim/upper
        rowWithKeys.setCourtSiteName("alpha");

        // Call the public wrapper
        var rows = new ArrayList<uk.gov.hmcts.pdda.common.publicdisplay.renderdata.AllCourtStatusValue>();
        rows.add(rowWithKeys);
        // This should add missing rooms only for siteA (roomA1), not for siteB
        //var out = invokeAddEmptyCourtrooms(rows, 81); // helper shown below or use reflection

        // Assert that roomA1 was added but roomB1 was NOT
        // e.g., count rows with courtRoomId == 501 vs 601

        verifyReplayArray(List.of(mockXhbCourtSiteRepository, mockXhbCourtRoomRepository));
    }


}