package uk.gov.hmcts.pdda.business.services.courtellist;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropDao;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.XhbCourtelListRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
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
    private static final String FALSE = "Result is not False";
    private static final String NOT_NULL = "Result is Not Null";

    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private XhbConfigPropRepository mockXhbConfigPropRepository;

    @Mock
    private XhbCourtelListRepository mockXhbCourtelListRepository;

    @InjectMocks
    private final CourtelListControllerBean classUnderTest =
        new CourtelListControllerBean(mockEntityManager);

    @Test
    void testDoTask() {
        // Run method
        boolean result;
        try {
            // Setup
            XhbConfigPropDao xhbConfigPropDao = new XhbConfigPropDao();
            xhbConfigPropDao.setPropertyValue("1");
            List<XhbConfigPropDao> configPropDaos = new ArrayList<>();
            configPropDaos.add(xhbConfigPropDao);
            
            Mockito.when(mockXhbConfigPropRepository.findByPropertyName(Mockito.isA(String.class)))
                .thenReturn(configPropDaos);

            // Run
            classUnderTest.doTask();
            result = true;
        } catch (Exception exception) {
            result = false;
        }
        // Check results
        assertTrue(result, TRUE);
    }
    
    @Test
    void testDoTaskFail() {
        // Run method
        boolean result;
        try {
            // Run
            classUnderTest.doTask();
            result = true;
        } catch (Exception exception) {
            result = false;
        }
        // Check results
        assertFalse(result, FALSE);
    }

    @Test
    void testDefaultConstructorEntityManager() {
        CourtelListControllerBean testConstructor =
            new CourtelListControllerBean(mockEntityManager);
        assertNotNull(testConstructor, NOT_NULL);
    }

    @Test
    void testDefaultConstructor() {
        CourtelListControllerBean testConstructor = new CourtelListControllerBean();
        assertNotNull(testConstructor, NOT_NULL);
    }
}
