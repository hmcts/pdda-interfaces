package uk.gov.hmcts.pdda.business.services.dailylistnotifier;

import jakarta.persistence.EntityManager;
import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.pdda.business.services.pdda.PddaDlNotifierHelper;
import uk.gov.hmcts.pdda.business.services.publicdisplay.PdConfigurationControllerBean;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**

 * Title: DailyListNotifierControllerBean Test.


 * Description: Unit tests for the DailyListNotifierControllerBean class


 * Copyright: Copyright (c) 2024


 * Company: CGI

 * @author Nathan Toft
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DailyListNotifierControllerBeanTest {

    private static final String TRUE = "Result is not True";
    private static final String NOT_NULL = "Result is Not Null";


    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private PdConfigurationControllerBean mockPdConfigurationControllerBean;

    @InjectMocks
    private final DailyListNotifierControllerBean classUnderTest =
        new DailyListNotifierControllerBean(mockEntityManager, mockPdConfigurationControllerBean,
            EasyMock.createMock(PddaDlNotifierHelper.class));

    @Test
    void testDoTask() {
        // Setup
        int[] courtIds = {1, 2};

        // Expects
        Mockito.when(mockPdConfigurationControllerBean.getCourtsForPublicDisplay())
            .thenReturn(courtIds);

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
    void testCallDailyListNotifierHelper() {
        // Setup
        int[] courtIds = {1, 2};

        // Expects
        Mockito.when(mockPdConfigurationControllerBean.getCourtsForPublicDisplay())
            .thenReturn(courtIds);

        // Run method
        boolean result;
        try {
            classUnderTest.callDailyListNotifierHelper();
            result = true;
        } catch (Exception exception) {
            result = false;
        }
        // Check results
        assertTrue(result, TRUE);
    }

    @Test
    void testRefreshPublicDisplaysForCourt() {
        // Setup
        int courtId = 80;

        // Run method
        boolean result;
        try {
            classUnderTest.refreshPublicDisplaysForCourt(courtId);
            result = true;
        } catch (Exception exception) {
            result = false;
        }
        // Check results
        assertTrue(result, TRUE);
    }

    @Test
    void testDefaultConstructorEntityManager() {
        DailyListNotifierControllerBean testConstructor =
            new DailyListNotifierControllerBean(mockEntityManager);
        assertNotNull(testConstructor, NOT_NULL);
    }

    @Test
    void testDefaultConstructor() {
        DailyListNotifierControllerBean testConstructor = new DailyListNotifierControllerBean();
        assertNotNull(testConstructor, NOT_NULL);
    }
}
