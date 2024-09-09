package uk.gov.hmcts.pdda.business.services.pdda;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * <p>
 * Title: ClearDown Controller Bean Test.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2024
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @author Mark Harris
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ClearDownControllerBeanTest {

    private static final String TRUE = "Result is not True";

    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private ClearDownHelper mockClearDownHelper;

    @InjectMocks
    private ClearDownControllerBean classUnderTest;

    @BeforeEach
    public void setUp() {
        classUnderTest = new ClearDownControllerBean(mockEntityManager);
        
        ReflectionTestUtils.setField(classUnderTest, "clearDownHelper", mockClearDownHelper);
    }

    @AfterEach
    public void tearDown() {
        classUnderTest = new ClearDownControllerBean();
    }

    @Test
    void testDoTask() {
        boolean result = testDoTask(true);
        assertTrue(result, TRUE);
        result = testDoTask(false);
        assertTrue(result, TRUE);
    }
        
    private boolean testDoTask(boolean run) {    
        // Setup
        Mockito.when(mockClearDownHelper.isClearDownRequired()).thenReturn(run);
        // Run
        try {
            classUnderTest.doTask();
            return true;
        } catch (Exception exception) {
            fail(exception);
            return false;
        }
    }

}
