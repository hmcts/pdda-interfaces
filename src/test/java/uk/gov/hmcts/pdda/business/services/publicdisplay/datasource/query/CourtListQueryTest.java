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
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.CourtListValue;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**

 * Title: CourtListQuery Test.


 * Description:


 * Copyright: Copyright (c) 2023


 * Company: CGI

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
    
    @Mock
    private XhbRefHearingTypeRepository mockXhbRefHearingTypeRepository;

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
        
        // Inject the mock into your test subject
        injectRefHearingTypeRepository(classUnderTest, mockXhbRefHearingTypeRepository);
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
    
    private void injectRefHearingTypeRepository(CourtListQuery query, XhbRefHearingTypeRepository mockRepo) {
        try {
            Class<?> c = query.getClass();
            Field field = null;
            while (c != null && field == null) {
                try {
                    field = c.getDeclaredField("xhbRefHearingTypeRepository");
                } catch (NoSuchFieldException ignore) {
                    c = c.getSuperclass();
                }
            }
            if (field == null) {
                throw new NoSuchFieldException("xhbRefHearingTypeRepository not found in class hierarchy");
            }
            field.setAccessible(true);
            field.set(query, mockRepo);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock repository", e);
        }
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
                
                EasyMock.expect(mockXhbRefHearingTypeRepository.findByIdSafe(EasyMock.isA(Integer.class)))
                    .andReturn(Optional.empty())
                    .anyTimes();
                addReplayArray(replayArray, mockXhbRefHearingTypeRepository);
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
    
    
    @SuppressWarnings("unchecked")
    private List<CourtListValue> runHappyPathAndReturn(LocalDateTime date,
                                                       XhbScheduledHearingDao scheduledHearingDaoOverride,
                                                       XhbSchedHearingDefendantDao schedHearingDefendantOverride,
                                                       Optional<XhbDefendantDao> defendantDaoOpt,
                                                       Optional<XhbRefHearingTypeDao> refTypeDaoOpt) {
        // Data graph
        List<XhbHearingListDao> hearingLists = new ArrayList<>();
        hearingLists.add(DummyHearingUtil.getXhbHearingListDao());

        List<XhbSittingDao> sittings = new ArrayList<>();
        XhbSittingDao sitting = DummyHearingUtil.getXhbSittingDao();
        sittings.add(sitting);

        List<XhbScheduledHearingDao> scheduled = new ArrayList<>();
        XhbScheduledHearingDao sh = scheduledHearingDaoOverride != null
            ? scheduledHearingDaoOverride
            : DummyHearingUtil.getXhbScheduledHearingDao();
        scheduled.add(sh);

        List<XhbSchedHearingDefendantDao> shds = new ArrayList<>();
        XhbSchedHearingDefendantDao shd = schedHearingDefendantOverride != null
            ? schedHearingDefendantOverride
            : DummyHearingUtil.getXhbSchedHearingDefendantDao();
        shds.add(shd);

        // Stubs
        LocalDateTime startDate = DateTimeUtilities.stripTime(date);
        int courtId = 81;
        
        // Hearing list
        EasyMock.expect(mockXhbHearingListRepository
            .findByCourtIdAndDateSafe(courtId, startDate)).andReturn(hearingLists).anyTimes();

        // Sittings
        EasyMock.expect(mockXhbSittingRepository
            .findByNonFloatingHearingListSafe(EasyMock.isA(Integer.class)))
            .andReturn(sittings).anyTimes();

        // Scheduled hearings
        EasyMock.expect(mockXhbScheduledHearingRepository
            .findBySittingIdSafe(EasyMock.isA(Integer.class)))
            .andReturn(scheduled).anyTimes();

        // Site/room (anyTimes to satisfy repeated lookups)
        EasyMock.expect(mockXhbCourtSiteRepository.findByIdSafe(EasyMock.isA(Integer.class)))
            .andReturn(Optional.of(DummyCourtUtil.getXhbCourtSiteDao())).anyTimes();
        EasyMock.expect(mockXhbCourtRoomRepository.findByIdSafe(EasyMock.isA(Integer.class)))
            .andReturn(Optional.of(DummyCourtUtil.getXhbCourtRoomDao())).anyTimes();

        // Sched hearing defendants
        EasyMock.expect(mockXhbSchedHearingDefendantRepository
            .findByScheduledHearingIdSafe(EasyMock.isA(Integer.class)))
            .andReturn(shds).anyTimes();

        // Hearing (present)
        Optional<XhbHearingDao> hearingDao = Optional.of(DummyHearingUtil.getXhbHearingDao());
        EasyMock.expect(mockXhbHearingRepository
            .findByIdSafe(EasyMock.isA(Integer.class))).andReturn(hearingDao).anyTimes();

        // Case + reference (used by getCourtListValue)
        EasyMock.expect(mockXhbCaseRepository.findByIdSafe(EasyMock.isA(Integer.class)))
            .andReturn(Optional.of(DummyCaseUtil.getXhbCaseDao())).anyTimes();
        List<XhbCaseReferenceDao> refs = new ArrayList<>();
        refs.add(DummyCaseUtil.getXhbCaseReferenceDao());
        EasyMock.expect(mockXhbCaseReferenceRepository
            .findByCaseIdSafe(EasyMock.isA(Integer.class))).andReturn(refs).anyTimes();

        // DefendantOnCase
        EasyMock.expect(mockXhbDefendantOnCaseRepository.findByIdSafe(EasyMock.isA(Integer.class)))
            .andReturn(Optional.of(DummyDefendantUtil.getXhbDefendantOnCaseDao())).anyTimes();

        // Defendant (customizable per test)
        EasyMock.expect(mockXhbDefendantRepository.findByIdSafe(EasyMock.isA(Integer.class)))
            .andReturn(defendantDaoOpt).anyTimes();

        // Ref hearing type (customizable per test)
        EasyMock.expect(mockXhbRefHearingTypeRepository.findByIdSafe(EasyMock.isA(Integer.class)))
            .andReturn(refTypeDaoOpt != null ? refTypeDaoOpt : Optional.empty())
            .anyTimes();


        // Replay all repos (mockEntityManager already replayed in @BeforeEach)
        EasyMock.replay(
            mockXhbCaseRepository,
            mockXhbCaseReferenceRepository,
            mockXhbHearingListRepository,
            mockXhbSittingRepository,
            mockXhbScheduledHearingRepository,
            mockXhbCourtSiteRepository,
            mockXhbCourtRoomRepository,
            mockXhbSchedHearingDefendantRepository,
            mockXhbHearingRepository,
            mockXhbDefendantOnCaseRepository,
            mockXhbDefendantRepository,
            mockXhbRefHearingTypeRepository
        );


        // Use the testable subclass to supply the ref-type repo
        CourtListQuery testable = new CourtListQuery(
            mockEntityManager, mockXhbCaseRepository, mockXhbCaseReferenceRepository, mockXhbHearingListRepository,
            mockXhbSittingRepository, mockXhbScheduledHearingRepository, mockXhbCourtSiteRepository,
            mockXhbCourtRoomRepository, mockXhbSchedHearingDefendantRepository, mockXhbHearingRepository,
            mockXhbDefendantOnCaseRepository, mockXhbDefendantRepository
        );
        injectRefHearingTypeRepository(testable, mockXhbRefHearingTypeRepository);


        int[] courtRoomIds = { sitting.getCourtRoomId() };
        
        @SuppressWarnings("rawtypes")
        Collection data = testable.getData(date, courtId, courtRoomIds);
        return (List<CourtListValue>) data;
    }
    
    
    @Test
    void testPopulateResultWithDefendants_addsDefendant_and_setsProgress() {
        // Arrange: scheduled hearing with progress set
        XhbScheduledHearingDao sh = DummyHearingUtil.getXhbScheduledHearingDao();
        sh.setHearingProgress(7); // non-null so we exercise setter path

        // Defendant present and visible
        Optional<XhbDefendantDao> defOpt = Optional.of(DummyDefendantUtil.getXhbDefendantDao());
        // Ref type present → description returned
        XhbRefHearingTypeDao ref = new XhbRefHearingTypeDao();
        ref.setRefHearingTypeId(1);
        ref.setHearingTypeDesc("Plea Hearing");
        Optional<XhbRefHearingTypeDao> refOpt = Optional.of(ref);

        // Act
        List<CourtListValue> results = runHappyPathAndReturn(LocalDateTime.now(), sh, null, defOpt, refOpt);

        // Assert
        assertFalse(results.isEmpty(), "Expected at least one result");
        CourtListValue v = results.get(0);
        // getRefHearingTypeDesc covered (present branch)
        assertEquals("Plea Hearing", v.getHearingDescription());
        // populateResultWithDefendants -> populateResultWithDefendant adds exactly one defendant
        assertEquals(1, v.getDefendantNames().size());
        // hearing progress set from scheduled hearing
        assertEquals(7, v.getHearingProgress());
    }

    @Test
    void testPopulateResultWithDefendants_skipsWhenObsIndY() {
        LocalDateTime date = LocalDateTime.now();

        // Prime all common expectations (hearing list, sittings, scheduled, site/room, etc.)
        // These are all .anyTimes() in runHappyPathAndReturn(..) after your edits.
        XhbRefHearingTypeDao ref = new XhbRefHearingTypeDao();
        ref.setRefHearingTypeId(2);
        ref.setHearingTypeDesc("Directions");
        runHappyPathAndReturn(
            date,
            null, // default scheduled hearing
            null, // default SHD
            Optional.of(DummyDefendantUtil.getXhbDefendantDao()),
            Optional.of(ref)
        );

        // Now override DOC to obsInd='Y'
        XhbDefendantOnCaseDao doc = DummyDefendantUtil.getXhbDefendantOnCaseDao();
        doc.setObsInd("Y");
        EasyMock.reset(mockXhbDefendantOnCaseRepository);
        EasyMock.expect(mockXhbDefendantOnCaseRepository.findByIdSafe(EasyMock.isA(Integer.class)))
            .andReturn(Optional.of(doc))
            .anyTimes();
        EasyMock.replay(mockXhbDefendantOnCaseRepository);

        CourtListQuery testable = new CourtListQuery(
            mockEntityManager, mockXhbCaseRepository, mockXhbCaseReferenceRepository, mockXhbHearingListRepository,
            mockXhbSittingRepository, mockXhbScheduledHearingRepository, mockXhbCourtSiteRepository,
            mockXhbCourtRoomRepository, mockXhbSchedHearingDefendantRepository, mockXhbHearingRepository,
            mockXhbDefendantOnCaseRepository, mockXhbDefendantRepository
        );
        injectRefHearingTypeRepository(testable, mockXhbRefHearingTypeRepository);

        @SuppressWarnings("unchecked")
        List<CourtListValue> results2 =
            (List<CourtListValue>) testable.getData(date, 81, DummyHearingUtil.getXhbSittingDao().getCourtRoomId());

        assertFalse(results2.isEmpty(), "Expected result row");
        assertEquals(0, results2.get(0).getDefendantNames().size(), "obsInd='Y' should skip adding defendant");
    }


    @Test
    void testPopulateResultWithDefendant_missingDefendant_noAdd() {
        // Arrange: Defendant repository returns empty → no name added
        Optional<XhbDefendantDao> emptyDef = Optional.empty();
        Optional<XhbRefHearingTypeDao> refEmpty = Optional.empty(); // also cover null return from getRefHearingTypeDesc

        List<CourtListValue> results = runHappyPathAndReturn(
            LocalDateTime.now(), null, null, emptyDef, refEmpty);

        // Assert: still get a row but no defendants, and hearing description is null
        assertFalse(results.isEmpty(), "Expected at least one result row");
        CourtListValue v = results.get(0);
        assertEquals(0, v.getDefendantNames().size(), "No defendant should be added when lookup is empty");
        assertEquals(null, v.getHearingDescription(), "Ref hearing type missing should yield null description");
    }

    @Test
    void testPopulateResultWithDefendants_emptyList_noChanges() {
        // Arrange: force scheduled hearing defendants to be empty
        // Do this by stubbing scheduledHearingDefendantRepository before running
        LocalDateTime now = LocalDateTime.now();

        // Build the standard graph first
        XhbRefHearingTypeDao ref = new XhbRefHearingTypeDao();
        ref.setRefHearingTypeId(3);
        ref.setHearingTypeDesc("First Appearance");

        // Prime all expectations once via helper
        List<CourtListValue> primed = runHappyPathAndReturn(
            now, null, null, Optional.of(DummyDefendantUtil.getXhbDefendantDao()), Optional.of(ref));
        assertFalse(primed.isEmpty(), "Sanity: have baseline");

        // Now override to return an empty SHD list and re-run
        EasyMock.reset(mockXhbSchedHearingDefendantRepository);
        EasyMock.expect(mockXhbSchedHearingDefendantRepository
            .findByScheduledHearingIdSafe(EasyMock.isA(Integer.class)))
            .andReturn(new ArrayList<>()).anyTimes();
        EasyMock.replay(mockXhbSchedHearingDefendantRepository);

        CourtListQuery testable = new CourtListQuery(
            mockEntityManager, mockXhbCaseRepository, mockXhbCaseReferenceRepository, mockXhbHearingListRepository,
            mockXhbSittingRepository, mockXhbScheduledHearingRepository, mockXhbCourtSiteRepository,
            mockXhbCourtRoomRepository, mockXhbSchedHearingDefendantRepository, mockXhbHearingRepository,
            mockXhbDefendantOnCaseRepository, mockXhbDefendantRepository
        );
        injectRefHearingTypeRepository(testable, mockXhbRefHearingTypeRepository);


        @SuppressWarnings("unchecked")
        List<CourtListValue> results =
            (List<CourtListValue>) testable.getData(now, 81, DummyHearingUtil.getXhbSittingDao().getCourtRoomId());

        // Assert: still a row, but no defendants and progress remains default (0)
        CourtListValue v = results.get(0);
        assertEquals(0, v.getDefendantNames().size(), "Empty SHD list should add no defendants");
        assertEquals(0, v.getHearingProgress(), "With no defendants processed, progress remains default (0)");
    }


}
