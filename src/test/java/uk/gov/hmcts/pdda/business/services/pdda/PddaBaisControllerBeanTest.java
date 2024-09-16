package uk.gov.hmcts.pdda.business.services.pdda;

import jakarta.persistence.EntityManager;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * <p>
 * Title: PDDA Bais Controller Bean Test.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2022
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @author Mark Harris
 */
@ExtendWith(EasyMockExtension.class)
class PddaBaisControllerBeanTest {

    private static final String TRUE = "Result is not True";

    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private PddaHelper mockPddaHelper;

    @TestSubject
    private final PddaBaisControllerBean classUnderTest = new PddaBaisControllerBean(mockEntityManager);

    @BeforeAll
    public static void setUp() {
        // Do nothing
    }

    @AfterAll
    public static void tearDown() {
        // Do nothing
    }

    @Test
    void testDoTask() {
        // Setup
        mockPddaHelper.retrieveFromBaisCp();
        mockPddaHelper.retrieveFromBaisXhibit();
        EasyMock.replay(mockPddaHelper);
        // Run
        boolean result = false;
        try {
            classUnderTest.doTask();
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        // Checks
        EasyMock.verify(mockPddaHelper);
        assertTrue(result, TRUE);
    }

    @Test
    void testRetrieveFromBaisCP() {
        // Setup
        mockPddaHelper.retrieveFromBaisCp();
        EasyMock.replay(mockPddaHelper);
        // Run
        boolean result = false;
        try {
            classUnderTest.retrieveFromBaisCP();
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        // Checks
        EasyMock.verify(mockPddaHelper);
        assertTrue(result, TRUE);
    }

    @Test
    void testRetrieveFromBaisXhibit() {
        mockPddaHelper.retrieveFromBaisXhibit();
        EasyMock.replay(mockPddaHelper);
        // Run
        boolean result = false;
        try {
            classUnderTest.retrieveFromBaisXhibit();
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        // Checks
        EasyMock.verify(mockPddaHelper);
        assertTrue(result, TRUE);
    }
}
