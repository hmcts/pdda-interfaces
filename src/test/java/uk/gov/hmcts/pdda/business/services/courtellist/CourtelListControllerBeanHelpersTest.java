package uk.gov.hmcts.pdda.business.services.courtellist;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.pdda.business.services.pdda.CourtelHelper;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * <p>
 * Title: CourtelListControllerBeanHelpersTest.
 * </p>
 * <p>
 * Description: Unit tests for the helpers in CourtelListControllerBean class.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2024
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @author Luke Gittins
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("PMD.LawOfDemeter")
class CourtelListControllerBeanHelpersTest {

    private static final String NULL = "Result is Not Null";

    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private CourtelHelper mockCourtelHelper;

    private CourtelListControllerBean classUnderTest;

    @BeforeEach
    public void setup() {
        classUnderTest = new CourtelListControllerBean(mockEntityManager);
    }
    
    @AfterEach
    public void teardown() {
        Mockito.clearAllCaches();
    }
    
    @Test
    void testGetCourtelHelper() {
        expectEntityManager();
        CourtelHelper result =
            classUnderTest.getCourtelHelper();
        assertNotNull(result, NULL);

        ReflectionTestUtils.setField(classUnderTest, "courtelHelper",
            mockCourtelHelper);
        result = classUnderTest.getCourtelHelper();
        assertNotNull(result, NULL);
    }
    
    private void expectEntityManager() {
        ReflectionTestUtils.setField(classUnderTest, "entityManager", mockEntityManager);
        Mockito.when(mockEntityManager.isOpen()).thenReturn(true);
    }
}
