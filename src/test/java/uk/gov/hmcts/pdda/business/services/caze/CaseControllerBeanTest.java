package uk.gov.hmcts.pdda.business.services.caze;

import jakarta.persistence.EntityManager;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.pdda.business.entities.xhbhearing.XhbHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbhearing.XhbHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbhearinglist.XhbHearingListDao;
import uk.gov.hmcts.pdda.business.entities.xhbhearinglist.XhbHearingListRepository;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingDao;
import uk.gov.hmcts.pdda.business.vos.services.caze.ScheduledHearingValue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**

 * Title: CaseControllerBean Test.


 * Description:


 * Copyright: Copyright (c) 2022


 * Company: CGI

 * @author Chris Vincent
 */
@ExtendWith(EasyMockExtension.class)
@SuppressWarnings("PMD")
class CaseControllerBeanTest {

    private static final String EQUALS = "Results are not Equal";
    private static final String FALSE = "Results are False";
    private static final String NOT_INSTANCE = "Result is Not An Instance of";
    private static final Integer COURT_ROOM_ID = 30;
    private static final Integer COURT_SITE_ID = 40;
    private static final Integer COURT_ID = 50;
    private static final Integer CASE_ID = 60;
    private static final Integer LIST_ID = 70;
    private static final String YES = "Y";
    private static final String USERNAME = "TestUser1";

    @Mock
    private XhbHearingRepository mockXhbHearingRepository;

    @Mock
    private XhbHearingListRepository mockXhbHearingListRepository;
    
    @Mock
    private EntityManager mockEntityManager;

    @TestSubject
    private final CaseControllerBean classUnderTest =
        new CaseControllerBean(EasyMock.createMock(EntityManager.class));

    @BeforeAll
    public static void setUp() {
        // Do nothing
    }

    @AfterAll
    public static void tearDown() {
        new CaseControllerBean();
    }

    /**
     * Test to invoke the getScheduledHearingsForCaseOnDay method.
     */
    @Test
    void testGetScheduledHearingsForCaseOnDay() {
        // Setup
        final Calendar today = Calendar.getInstance();
        LocalDateTime ldt = LocalDateTime.now();
        List<XhbHearingDao> xhList = getDummyHearingDaoList();
        Optional<XhbHearingListDao> list =
            Optional.of(getDummyXhbHearingListDao(LIST_ID, ldt, ldt));
        Optional<XhbHearingListDao> oldList =
            Optional.of(getDummyXhbHearingListDao(LIST_ID, ldt.minusDays(1), ldt.minusDays(1)));

        EasyMock.expect(mockXhbHearingRepository.getEntityManager()).andReturn(mockEntityManager).anyTimes();
        EasyMock.expect(mockEntityManager.isOpen()).andReturn(true).anyTimes();
        
        EasyMock.expect(mockXhbHearingRepository.findByCaseIdSafe(CASE_ID)).andReturn(xhList);

        EasyMock.expect(mockXhbHearingListRepository.findByIdSafe(EasyMock.isA(Integer.class)))
            .andReturn(list);
        EasyMock.expect(mockXhbHearingListRepository.findByIdSafe(EasyMock.isA(Integer.class)))
            .andReturn(list);
        EasyMock.expect(mockXhbHearingListRepository.findByIdSafe(EasyMock.isA(Integer.class)))
            .andReturn(oldList);
        EasyMock.expect(mockXhbHearingListRepository.findByIdSafe(EasyMock.isA(Integer.class)))
            .andReturn(list);

        replayMocks();

        // Run method
        ScheduledHearingValue[] result =
            classUnderTest.getScheduledHearingsForCaseOnDay(CASE_ID, today);

        // Checks
        verifyMocks();
        assertEquals(3, result.length, EQUALS);
    }
    
    @Test
    void testGetXhbHearingRepository() {
        EasyMock.expect(mockEntityManager.isOpen()).andReturn(true).anyTimes();
        EasyMock.replay(mockEntityManager);
        assertInstanceOf(XhbHearingRepository.class, classUnderTest.getXhbHearingRepository(),
            NOT_INSTANCE);
        EasyMock.verify(mockEntityManager);
    }
    
    @Test
    void testGetXhbHearingListRepository() {
        EasyMock.expect(mockEntityManager.isOpen()).andReturn(true).anyTimes();
        EasyMock.replay(mockEntityManager);
        assertInstanceOf(XhbHearingListRepository.class, classUnderTest.getXhbHearingListRepository(),
            NOT_INSTANCE);
        EasyMock.verify(mockEntityManager);
    }
    
    @Test
    void testClearRepos() {
        boolean result = true;
        classUnderTest.clearRepositories();
        assertTrue(result, FALSE);
    }

    private XhbHearingDao getDummyXhbHearingDao(Integer hearingId, Integer caseId) {
        Integer refHearingTypeId = Double.valueOf(Math.random()).intValue();
        Integer courtId = COURT_ID;
        String mpHearingType = null;
        Double lastCalculatedDuration = Math.random();
        LocalDateTime hearingStartDate = LocalDateTime.now();
        LocalDateTime hearingEndDate = LocalDateTime.now();
        Integer linkedHearingId = Double.valueOf(Math.random()).intValue();
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(15);
        String lastUpdatedBy = "Test2";
        String createdBy = "Test1";
        Integer version = 2;

        XhbHearingDao result = new XhbHearingDao();
        result.setHearingId(hearingId);
        result.setCaseId(caseId);
        result.setRefHearingTypeId(refHearingTypeId);
        result.setCourtId(courtId);
        result.setMpHearingType(mpHearingType);
        result.setLastCalculatedDuration(lastCalculatedDuration);
        result.setHearingStartDate(hearingStartDate);
        result.setHearingEndDate(hearingEndDate);
        result.setLinkedHearingId(linkedHearingId);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);
        XhbHearingDao returnObject = new XhbHearingDao(result);
        returnObject.setScheduledHearings(getDummyScheduledHearingDaoList());
        return returnObject;
    }

    private List<XhbHearingDao> getDummyHearingDaoList() {
        List<XhbHearingDao> xhList = new ArrayList<>();
        xhList.add(getDummyXhbHearingDao(1, CASE_ID));
        xhList.add(getDummyXhbHearingDao(2, CASE_ID));
        return xhList;
    }

    private XhbHearingListDao getDummyXhbHearingListDao(Integer listId, LocalDateTime startDate,
        LocalDateTime endDate) {
        String listType = "D";
        String status = "FINAL";
        Integer editionNo = 1;
        LocalDateTime publishedTime = LocalDateTime.now();
        String printReference = "NGU/123456";
        Integer crestListId = Double.valueOf(Math.random()).intValue();
        Integer courtId = COURT_ID;
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(15);
        String lastUpdatedBy = "Test2";
        String createdBy = "Test1";
        Integer version = 2;

        XhbHearingListDao result = new XhbHearingListDao();
        result.setListId(listId);
        result.setListType(listType);
        result.setStartDate(startDate);
        result.setEndDate(endDate);
        result.setStatus(status);
        result.setEditionNo(editionNo);
        result.setPublishedTime(publishedTime);
        result.setPrintReference(printReference);
        result.setCrestListId(crestListId);
        result.setCourtId(courtId);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);
        return new XhbHearingListDao(result);
    }

    private XhbScheduledHearingDao getDummyScheduledHearingDao(final Integer scheduledHearingId) {
        Integer sequenceNo = 1;
        LocalDateTime notBeforeTime = LocalDateTime.now();
        LocalDateTime originalTime = LocalDateTime.now();
        String listingNote = null;
        Integer hearingProgress = 0;
        Integer sittingId = Double.valueOf(Math.random()).intValue();
        Integer hearingId = Double.valueOf(Math.random()).intValue();
        String caseActive = YES;
        String movedFrom = null;
        Integer movedFromCourtRoomId = null;
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(15);
        String lastUpdatedBy = USERNAME;
        String createdBy = USERNAME;
        Integer version = 2;

        XhbScheduledHearingDao result = new XhbScheduledHearingDao();
        result.setScheduledHearingId(scheduledHearingId);
        result.setSequenceNo(sequenceNo);
        result.setNotBeforeTime(notBeforeTime);
        result.setOriginalTime(originalTime);
        result.setListingNote(listingNote);
        result.setHearingProgress(hearingProgress);
        result.setSittingId(sittingId);
        result.setHearingId(hearingId);
        result.setIsCaseActive(caseActive);
        result.setMovedFrom(movedFrom);
        result.setMovedFromCourtRoomId(movedFromCourtRoomId);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);
        XhbScheduledHearingDao returnObject = new XhbScheduledHearingDao(result);
        returnObject.setXhbSitting(getDummySittingDao(sittingId));
        return returnObject;
    }

    private List<XhbScheduledHearingDao> getDummyScheduledHearingDaoList() {
        List<XhbScheduledHearingDao> xshList = new ArrayList<>();
        xshList.add(getDummyScheduledHearingDao(1));
        xshList.add(getDummyScheduledHearingDao(2));
        return xshList;
    }

    private XhbSittingDao getDummySittingDao(final Integer sittingId) {
        Integer sittingSequenceNo = 1;
        String sittingJudge = null;
        LocalDateTime sittingTime = LocalDateTime.now();
        String sittingNote = null;
        Integer refJustice1Id = null;
        Integer refJustice2Id = null;
        Integer refJustice3Id = null;
        Integer refJustice4Id = null;
        String floating = "0";
        Integer listId = LIST_ID;
        Integer refJudgeId = Double.valueOf(Math.random()).intValue();
        Integer courtRoomId = COURT_ROOM_ID;
        Integer courtSiteId = COURT_SITE_ID;
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(15);
        String lastUpdatedBy = USERNAME;
        String createdBy = USERNAME;
        Integer version = 2;

        XhbSittingDao result = new XhbSittingDao();
        result.setSittingId(sittingId);
        result.setSittingSequenceNo(sittingSequenceNo);
        result.setIsSittingJudge(sittingJudge);
        result.setSittingTime(sittingTime);
        result.setSittingNote(sittingNote);
        result.setRefJustice1Id(refJustice1Id);
        result.setRefJustice2Id(refJustice2Id);
        result.setRefJustice3Id(refJustice3Id);
        result.setRefJustice4Id(refJustice4Id);
        result.setIsFloating(floating);
        result.setListId(listId);
        result.setRefJudgeId(refJudgeId);
        result.setCourtRoomId(courtRoomId);
        result.setCourtSiteId(courtSiteId);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);

        return new XhbSittingDao(result);
    }

    /**
     * Replay the mocked objects.
     */
    private void replayMocks() {
        EasyMock.replay(mockEntityManager);
        EasyMock.replay(mockXhbHearingRepository);
        EasyMock.replay(mockXhbHearingListRepository);
    }

    /**
     * Verify the mocked objects.
     */
    private void verifyMocks() {
        EasyMock.verify(mockEntityManager);
        EasyMock.verify(mockXhbHearingRepository);
        EasyMock.verify(mockXhbHearingListRepository);
    }

}
