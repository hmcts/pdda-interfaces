package uk.gov.hmcts.pdda.business.services.dailylistnotifier;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.DummyCourtUtil;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtDao;
import uk.gov.hmcts.pdda.business.services.pdda.PddaDlNotifierHelper;
import uk.gov.hmcts.pdda.business.services.publicdisplay.PdConfigurationControllerBean;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * <p>
 * Title: DailyListNotifierControllerBean Test.
 * </p>
 * <p>
 * Description: Unit tests for the DailyListNotifierControllerBean class
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
class DailyListNotifierControllerBeanTest {

    private static final String EQUALS = "Results are not Equal";
    private static final String FALSE = "Result is not False";
    private static final String TRUE = "Result is not True";

    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private PdConfigurationControllerBean mockPdConfigurationControllerBean;

    @Mock
    private PddaDlNotifierHelper mockPddaDlNotifierHelper;

    @InjectMocks
    private final DailyListNotifierControllerBean classUnderTest = new DailyListNotifierControllerBean(
        mockEntityManager, mockPdConfigurationControllerBean, mockPddaDlNotifierHelper);


    @Test
    void testCallDailyListNotifierHelper() {
        // Setup
        int[] courtIds = {1,2};
        
        // Expects
        Mockito.when(mockPdConfigurationControllerBean.getCourtsForPublicDisplay()).thenReturn(courtIds);

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
}
