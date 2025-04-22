package uk.gov.hmcts.pdda.business.services.publicdisplay.database.query;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.DummyHearingUtil;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * <p>
 * Title: ActiveCasesInRoomQueryTest Test.
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
class ActiveCasesInRoomQueryTest {

    private static final String TRUE = "Result is not True";

    @Mock
    protected EntityManager mockEntityManager;

    @Mock
    private XhbScheduledHearingRepository mockXhbScheduledHearingRepository;

    @TestSubject
    protected ActiveCasesInRoomQuery classUnderTest =
        new ActiveCasesInRoomQuery(mockEntityManager, mockXhbScheduledHearingRepository);

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
            classUnderTest = new ActiveCasesInRoomQuery(mockEntityManager);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }

    @Test
    void testGetDataNoListEmpty() {
        boolean result = testGetDataNoList(new ArrayList<>());
        assertTrue(result, TRUE);
    }

    @Test
    void testGetDataNoListNoCourtSite() {
        List<XhbScheduledHearingDao> xhbScheduledHearingDaoList = new ArrayList<>();
        xhbScheduledHearingDaoList.add(DummyHearingUtil.getXhbScheduledHearingDao());
        boolean result = testGetDataNoList(xhbScheduledHearingDaoList);
        assertTrue(result, TRUE);
    }


    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    @Test
    void testRepositoryCreatedWhenNullAndEntityManagerIsOpen() {
        // Setup
        classUnderTest = new ActiveCasesInRoomQuery(mockEntityManager);

        // Mock the query that will be used inside the new repository instance
        Query mockQuery = EasyMock.createMock(Query.class);

        // This one is used by ActiveCasesInRoomQuery to decide whether to create new repo
        EasyMock.expect(mockEntityManager.isOpen()).andReturn(true);

        // Setup named query expectations
        EasyMock
            .expect(
                mockEntityManager.createNamedQuery("XHB_SCHEDULED_HEARING.findActiveCasesInRoom"))
            .andReturn(mockQuery);
        EasyMock.expect(mockQuery.setParameter("courtId", 81)).andReturn(mockQuery);
        EasyMock.expect(mockQuery.setParameter("listId", -1)).andReturn(mockQuery);
        EasyMock.expect(mockQuery.setParameter("scheduledHearingId", -1)).andReturn(mockQuery);
        EasyMock.expect(mockQuery.getResultList()).andReturn(new ArrayList<>());

        // Replay mocks
        EasyMock.replay(mockEntityManager, mockQuery);

        // Act
        classUnderTest.getData(-1, 81, -1);
    }


    private boolean testGetDataNoList(List<XhbScheduledHearingDao> xhbScheduledHearingDaoList) {
        // Setup
        Integer listId = -1;
        Integer courtRoomId = 81;
        Integer scheduledHearingId = -1;

        // Expectations
        EasyMock.expect(mockEntityManager.isOpen()).andReturn(true);
        EasyMock
            .expect(mockXhbScheduledHearingRepository.findActiveCasesInRoom(EasyMock.eq(listId),
                EasyMock.eq(courtRoomId), EasyMock.eq(scheduledHearingId)))
            .andReturn(xhbScheduledHearingDaoList);

        EasyMock.replay(mockEntityManager, mockXhbScheduledHearingRepository);

        // Run
        classUnderTest.getData(listId, courtRoomId, scheduledHearingId);

        // Verify
        EasyMock.verify(mockEntityManager, mockXhbScheduledHearingRepository);
        return true;
    }

    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    @Test
    void testGetXhbScheduledHearingRepositoryCreatesNewWhenNull() {
        // Simulate case where repo is null by using constructor without repo
        classUnderTest = new ActiveCasesInRoomQuery(mockEntityManager);

        // Mock EntityManager behavior
        EasyMock.expect(mockEntityManager.isOpen()).andReturn(true);

        // Mock the named query used internally
        Query mockQuery = EasyMock.createMock(Query.class);
        EasyMock
            .expect(
                mockEntityManager.createNamedQuery("XHB_SCHEDULED_HEARING.findActiveCasesInRoom"))
            .andReturn(mockQuery);
        EasyMock.expect(mockQuery.setParameter(EasyMock.eq("courtId"), EasyMock.anyInt()))
            .andReturn(mockQuery);
        EasyMock.expect(mockQuery.setParameter(EasyMock.eq("listId"), EasyMock.anyInt()))
            .andReturn(mockQuery);
        EasyMock
            .expect(mockQuery.setParameter(EasyMock.eq("scheduledHearingId"), EasyMock.anyInt()))
            .andReturn(mockQuery);
        EasyMock.expect(mockQuery.getResultList()).andReturn(new ArrayList<>());

        EasyMock.replay(mockEntityManager, mockQuery);

        // Call the public method which internally triggers the private method
        classUnderTest.getData(-1, 81, -1);
    }


    @Test
    void testGetXhbScheduledHearingRepositoryUsesExistingWhenSet() {
        // Given
        List<XhbScheduledHearingDao> daoList = new ArrayList<>();
        daoList.add(DummyHearingUtil.getXhbScheduledHearingDao());

        // Expectations
        EasyMock.expect(mockEntityManager.isOpen()).andReturn(true);
        EasyMock.expect(mockXhbScheduledHearingRepository.findActiveCasesInRoom(EasyMock.eq(-1),
            EasyMock.eq(81), EasyMock.eq(-1))).andReturn(daoList);

        EasyMock.replay(mockEntityManager, mockXhbScheduledHearingRepository);

        // When
        Collection<Integer> result = classUnderTest.getData(-1, 81, -1);

        // Then
        assertTrue(result.contains(daoList.get(0).getScheduledHearingId()), "result is true");
        EasyMock.verify(mockEntityManager, mockXhbScheduledHearingRepository);
    }

    @SuppressWarnings({"PMD.UseExplicitTypes", "PMD.AvoidAccessibilityAlteration"})
    @Test
    void testClearRepositoriesSetsRepositoryToNull() throws Exception {
        // Given
        classUnderTest.clearRepositories();

        // Use reflection to check the private field
        var field = ActiveCasesInRoomQuery.class.getDeclaredField("xhbScheduledHearingRepository");
        field.setAccessible(true);
        Object repository = field.get(classUnderTest);

        // Then
        assertTrue(repository == null, "Repository should be null after clearRepositories()");
    }

}
