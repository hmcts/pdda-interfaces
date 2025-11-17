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
import uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant.XhbSchedHearingDefendantDao;
import uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant.XhbSchedHearingDefendantRepository;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingDao;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingRepository;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.CourtListValue;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.DefendantName;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @TestSubject
    private CourtListQuery classUnderTest = new CourtListQuery(mockEntityManager,
        mockXhbCaseRepository, mockXhbCaseReferenceRepository, mockXhbHearingListRepository,
        mockXhbSittingRepository, mockXhbScheduledHearingRepository, mockXhbCourtSiteRepository,
        mockXhbCourtRoomRepository, mockXhbSchedHearingDefendantRepository,
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
        boolean result = testGetDataNoList(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), Optional.empty());
        assertTrue(result, TRUE);
    }

    @Test
    void testGetDataNoListNoSittings() {
        List<XhbHearingListDao> xhbHearingListDaoList = new ArrayList<>();
        xhbHearingListDaoList.add(DummyHearingUtil.getXhbHearingListDao());
        boolean result = testGetDataNoList(xhbHearingListDaoList, new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(), Optional.empty());
        assertTrue(result, TRUE);
    }

    @Test
    void testGetDataNoListNoScheduledHearings() {
        List<XhbHearingListDao> xhbHearingListDaoList = new ArrayList<>();
        xhbHearingListDaoList.add(DummyHearingUtil.getXhbHearingListDao());
        List<XhbSittingDao> xhbSittingDaoList = new ArrayList<>();
        xhbSittingDaoList.add(DummyHearingUtil.getXhbSittingDao());
        boolean result = testGetDataNoList(xhbHearingListDaoList, xhbSittingDaoList,
            new ArrayList<>(), new ArrayList<>(), Optional.empty());
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
        boolean result = testGetDataNoList(xhbHearingListDaoList, xhbSittingDaoList,
            xhbScheduledHearingDaoList, new ArrayList<>(), Optional.empty());
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
            xhbScheduledHearingDaoList, xhbSchedHearingDefendantDaoList, Optional.empty());
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
        boolean result =
            testGetDataNoList(xhbHearingListDaoList, xhbSittingDaoList, xhbScheduledHearingDaoList,
                xhbSchedHearingDefendantDaoList, Optional.of(DummyHearingUtil.getXhbHearingDao()));
        assertTrue(result, TRUE);
    }

    @SuppressWarnings("unused")
    private boolean testGetDataNoList(List<XhbHearingListDao> xhbHearingListDaoList,
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
                    expectSitting(xhbScheduledHearingDaoList, xhbSchedHearingDefendantDaoList,
                        xhbHearingDao, replayArray);
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
        List<XhbSchedHearingDefendantDao> xhbSchedHearingDefendantDaoList,
        Optional<XhbHearingDao> xhbHearingDao, List<AbstractRepository<?>> replayArray) {
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

    // --- getRefHearingTypeDesc: empty optional returns null (no repo interaction) ---
    @Test
    void testGetRefHearingTypeDesc_WhenHearingMissing_ReturnsNull() throws Exception {
        Method m = CourtListQuery.class.getDeclaredMethod("getRefHearingTypeDesc", Optional.class);
        m.setAccessible(true);

        Object desc = m.invoke(classUnderTest, Optional.empty());

        // simply ensure we took the 'no hearing' branch
        org.junit.jupiter.api.Assertions.assertNull(desc,
            "Expected null hearing description when no hearing present");
    }

    // --- populateResultWithDefendant: adds name and sets hidden correctly ---
    @Test
    void testPopulateResultWithDefendant_AddsNameAndSetsHidden() throws Exception {
        
        // Build a DefendantOnCase and a Defendant
        XhbDefendantOnCaseDao doc = DummyDefendantUtil.getXhbDefendantOnCaseDao();
        // ensure a stable id relationship
        int defId = 12345;
        doc.setDefendantId(defId);
        // mark the DOC as hidden so tmpIsHidden becomes true even if case isn't
        doc.setPublicDisplayHide("Y");

        XhbDefendantDao def = DummyDefendantUtil.getXhbDefendantDao();
        def.setDefendantId(defId);
        def.setFirstName("Alex");
        def.setMiddleName("Q");
        def.setSurname("Rivera");
        def.setPublicDisplayHide("N"); // not hidden at person level

        // Expect repository call for defendant lookup
        EasyMock.reset(mockXhbDefendantRepository);
        EasyMock.expect(mockXhbDefendantRepository.findByIdSafe(defId)).andReturn(Optional.of(def));
        EasyMock.replay(mockXhbDefendantRepository);

        CourtListValue result = new CourtListValue();

        // Invoke private method
        Method m = CourtListQuery.class.getDeclaredMethod("populateResultWithDefendant",
            CourtListValue.class, Optional.class, boolean.class);
        m.setAccessible(true);
        m.invoke(classUnderTest, result, Optional.of(doc), /* isHidden from case */ false);

        // Verify effects
        EasyMock.verify(mockXhbDefendantRepository);
        org.junit.jupiter.api.Assertions.assertTrue(result.getDefendantNames().size() == 1,
            "One defendant should be added");
        DefendantName dn = result.getDefendantNames().get(0);
        org.junit.jupiter.api.Assertions.assertEquals("Alex Q Rivera", dn.getName());
        // Hidden cascades to reportingRestricted on the result
        org.junit.jupiter.api.Assertions.assertTrue(result.isReportingRestricted(),
            "Result should be reporting restricted when any hide flag applies");
    }

    // --- populateResultWithDefendants: skips obsInd='Y' and sets progress default 0 ---
    @Test
    void testPopulateResultWithDefendants_SkipsObsAndSetsProgressToZeroWhenNull() throws Exception {
        
        // One obs defendant (should be skipped) and one valid
        XhbSchedHearingDefendantDao obs = DummyHearingUtil.getXhbSchedHearingDefendantDao();
        XhbDefendantOnCaseDao obsDoc = DummyDefendantUtil.getXhbDefendantOnCaseDao();
        obs.setDefendantOnCaseId(201);
        obsDoc.setDefendantOnCaseId(201);
        obsDoc.setObsInd("Y"); // skip this one

        XhbSchedHearingDefendantDao ok = DummyHearingUtil.getXhbSchedHearingDefendantDao();
        XhbDefendantOnCaseDao okDoc = DummyDefendantUtil.getXhbDefendantOnCaseDao();
        ok.setDefendantOnCaseId(202);
        okDoc.setDefendantOnCaseId(202);
        okDoc.setObsInd("N");
        okDoc.setDefendantId(303);

        XhbDefendantDao def = DummyDefendantUtil.getXhbDefendantDao();
        def.setDefendantId(303);
        def.setFirstName("Casey");
        def.setMiddleName(null);
        def.setSurname("Ng");

        // Scheduled hearing with null progress -> should set 0
        XhbScheduledHearingDao sh = DummyHearingUtil.getXhbScheduledHearingDao();
        sh.setHearingProgress(null);

        // Expectations
        EasyMock.reset(mockXhbDefendantOnCaseRepository, mockXhbDefendantRepository);
        // for obs (will still be looked up, but then skipped)
        EasyMock.expect(mockXhbDefendantOnCaseRepository.findByIdSafe(201))
            .andReturn(Optional.of(obsDoc)).anyTimes();
        // for ok
        EasyMock.expect(mockXhbDefendantOnCaseRepository.findByIdSafe(202))
            .andReturn(Optional.of(okDoc)).anyTimes();
        // and the defendant lookup for ok
        EasyMock.expect(mockXhbDefendantRepository.findByIdSafe(303)).andReturn(Optional.of(def))
            .anyTimes();
        EasyMock.replay(mockXhbDefendantOnCaseRepository, mockXhbDefendantRepository);

        // Invoke private method
        Method m = CourtListQuery.class.getDeclaredMethod("populateResultWithDefendants",
            CourtListValue.class, XhbScheduledHearingDao.class, List.class, boolean.class);
        m.setAccessible(true);
        CourtListValue result = new CourtListValue();
        @SuppressWarnings("unchecked")
        CourtListValue returned = (CourtListValue) m.invoke(classUnderTest, result, sh,
            java.util.Arrays.asList(obs, ok), /* isHidden from case */ false);

        // Verify
        EasyMock.verify(mockXhbDefendantOnCaseRepository, mockXhbDefendantRepository);

        org.junit.jupiter.api.Assertions.assertSame(result, returned,
            "Method should return the same instance");
        org.junit.jupiter.api.Assertions.assertEquals(1, result.getDefendantNames().size(),
            "Only non-obscured defendant should be added");
        org.junit.jupiter.api.Assertions.assertEquals(0, result.getHearingProgress(),
            "Null progress must result in progress = 0");
    }
}
