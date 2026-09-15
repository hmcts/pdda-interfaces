package uk.gov.hmcts.pdda.business.services.pdda;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import javax.xml.transform.TransformerException;

import static org.junit.jupiter.api.Assertions.assertSame;
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
    private static final String SAME = "Result is not the same";

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

    @Test
    void testGetCathHelperWhenNullCreatesNewCathHelper() throws TransformerException, IOException {
        ReflectionTestUtils.setField(classUnderTest, "cathHelper", null);

        try (MockedStatic<EntityManagerUtil> entityManagerUtil =
                 Mockito.mockStatic(EntityManagerUtil.class);
             MockedConstruction<CathHelper> cathHelperConstruction =
                 Mockito.mockConstruction(CathHelper.class)) {
            entityManagerUtil.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);

            classUnderTest.doTask();

            CathHelper newCathHelper = cathHelperConstruction.constructed().get(0);
            assertSame(newCathHelper, ReflectionTestUtils.getField(classUnderTest, "cathHelper"), SAME);
        }
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
