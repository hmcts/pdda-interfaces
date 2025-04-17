package uk.gov.hmcts.pdda.business.services.publicdisplay.database.query;

import jakarta.persistence.EntityManager;
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



    private boolean testGetDataNoList(List<XhbScheduledHearingDao> xhbScheduledHearingDaoList) {
        // Setup
        Integer listId = -1;
        Integer courtRoomId = 81;
        Integer scheduledHearingId = -1;

        // Expectations
        EasyMock.expect(mockEntityManager.isOpen()).andReturn(true); // 👈 Fixes the error
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

}
