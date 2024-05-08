package uk.gov.hmcts.pdda.business.services.courtellist;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * <p>
 * Title: CourtelListControllerBean Test.
 * </p>
 * <p>
 * Description: Unit tests for the CourtelListControllerBean class
 * </p>
 * <p>
 * Copyright: Copyright (c) 2024
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @author Nathan Toft
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CourtelListControllerBeanTest {

    private static final String TRUE = "Result is not True";
    private static final String NOT_NULL = "Result is Not Null";


    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private final CourtelListControllerBean classUnderTest = new CourtelListControllerBean(
        mockEntityManager);

    @Test
    void testDoTask() {
        // Run method
        boolean result;
        try {
            classUnderTest.doTask();
            result = true;
        } catch (Exception exception) {
            result = false;
        }
        // Check results
        assertTrue(result, TRUE); 
    }

    @Test
    void testCallCourtelListHelper() {
        // Run method
        boolean result;
        try {
            classUnderTest.callCourtelListHelper();
            result = true;
        } catch (Exception exception) {
            result = false;
        }
        // Check results
        assertTrue(result, TRUE);
    }
    
    @Test
    void testDefaultConstructorEntityManager() {
        CourtelListControllerBean testConstructor = new CourtelListControllerBean(mockEntityManager);
        assertNotNull(testConstructor, NOT_NULL);
    }
    
    @Test
    void testDefaultConstructor() {
        CourtelListControllerBean testConstructor = new CourtelListControllerBean();
        assertNotNull(testConstructor, NOT_NULL);
    }
}
