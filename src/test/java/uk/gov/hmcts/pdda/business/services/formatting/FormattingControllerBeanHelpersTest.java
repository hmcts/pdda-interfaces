package uk.gov.hmcts.pdda.business.services.formatting;

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

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * <p>
 * Title: FormattingControllerBeanHelpersTest.
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
 * @author Luke Gittins
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("PMD.LawOfDemeter")
class FormattingControllerBeanHelpersTest {

    private static final String NULL = "Result is Null";

    @Mock
    private EntityManager mockEntityManager;
    
    @Mock
    private FormattingServices mockFormattingServices;
    
    private FormattingControllerBean classUnderTest;

    @BeforeEach
    public void setup() {
        classUnderTest = new FormattingControllerBean();
    }

    @AfterEach
    public void teardown() {
        Mockito.clearAllCaches();
    }

    @Test
    void testGetFormattingServices() {
        expectEntityManager();
        FormattingServices result =
            classUnderTest.getFormattingServices();
        assertNotNull(result, NULL);

        ReflectionTestUtils.setField(classUnderTest, "formattingServices",
            mockFormattingServices);
        result = classUnderTest.getFormattingServices();
        assertNotNull(result, NULL);
    }
    
    private void expectEntityManager() {
        ReflectionTestUtils.setField(classUnderTest, "entityManager", mockEntityManager);
        Mockito.when(mockEntityManager.isOpen()).thenReturn(true);
    }
}
