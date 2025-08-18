package uk.gov.hmcts.pdda.business.services.pdda;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**

 * Title: Cath Controller Bean Test.


 * Description:


 * Copyright: Copyright (c) 2024


 * Company: CGI

 * @author Nathan Toft
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CathControllerBeanTest {

    private static final String TRUE = "Result is not True";

    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private CathHelper mockCathHelper;

    @InjectMocks
    private CathControllerBean classUnderTest;

    @BeforeEach
    public void setUp() {
        classUnderTest = new CathControllerBean(mockEntityManager);
        
        ReflectionTestUtils.setField(classUnderTest, "cathHelper", mockCathHelper);
    }

    @AfterEach
    public void tearDown() {
        classUnderTest = new CathControllerBean();
    }

    @Test
    void testDoTask() {
        boolean result = runDoTask();
        assertTrue(result, TRUE);
    }
        
    private boolean runDoTask() {    
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
