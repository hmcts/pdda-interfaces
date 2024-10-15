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
import uk.gov.hmcts.DummyDisplayUtil;
import uk.gov.hmcts.DummyServicesUtil;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropDao;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcrlivedisplay.XhbCrLiveDisplayDao;
import uk.gov.hmcts.pdda.business.entities.xhbcrlivedisplay.XhbCrLiveDisplayRepository;
import uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled.DateUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * <p>
 * Title: ClearDown Helper Bean Test.
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
class ClearDownHelperTest {

    private static final String EQUAL = "Result is not Equal";
    private static final String FALSE = "Result is not False";
    private static final String NOTNULL = "Result is Null";
    private static final String TRUE = "Result is not True";

    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private XhbCrLiveDisplayRepository mockXhbCrLiveDisplayRepository;

    @Mock
    private XhbConfigPropRepository mockXhbConfigPropRepository;

    @InjectMocks
    private ClearDownHelper classUnderTest;

    @BeforeEach
    public void setUp() {
        classUnderTest =
            new ClearDownHelper(mockXhbCrLiveDisplayRepository, mockXhbConfigPropRepository);
    }

    @AfterEach
    public void tearDown() {
        classUnderTest = new ClearDownHelper(mockEntityManager);
    }

    @Test
    void testIsClearDownRequiredFalse() {
        // Run (empty list)
        boolean result = testIsClearDownRequired(null);
        assertFalse(result, FALSE);
        // Run (invalid time)
        result = testIsClearDownRequired(DummyServicesUtil
            .getXhbConfigPropDao(ClearDownHelper.RESET_DISPLAY_IWP_TIME, "Invalid"));
        assertFalse(result, FALSE);
        // Run (future time)
        String time = DateUtils.getTime(LocalDateTime.now().plusHours(1));
        result = testIsClearDownRequired(
            DummyServicesUtil.getXhbConfigPropDao(ClearDownHelper.RESET_DISPLAY_IWP_TIME, time));
        assertTrue(result, TRUE);
    }

    @Test
    void testIsClearDownRequiredTrue() {
        // Setup a valid time
        String time = DateUtils.getTime(LocalDateTime.now().minusMinutes(1));
        // Run
        boolean result = testIsClearDownRequired(
            DummyServicesUtil.getXhbConfigPropDao(ClearDownHelper.RESET_DISPLAY_IWP_TIME, time));
        assertTrue(result, TRUE);
    }

    private boolean testIsClearDownRequired(XhbConfigPropDao xhbConfigPropDao) {
        // Setup
        List<XhbConfigPropDao> xhbConfigPropDaoList = DummyServicesUtil.getNewArrayList();
        if (xhbConfigPropDao != null) {
            xhbConfigPropDaoList.add(xhbConfigPropDao);
        }
        // Expects
        Mockito.when(mockXhbConfigPropRepository.findByPropertyName(Mockito.isA(String.class)))
            .thenReturn(xhbConfigPropDaoList);
        // Run
        return classUnderTest.isClearDownRequired();
    }

    @Test
    void testGetClearDownTime() {
        // Setup
        LocalDateTime timeMinusOneMin = LocalDateTime.now().minusMinutes(1);
        String time = DateUtils.getTime(timeMinusOneMin);
        // Run
        LocalDateTime result = classUnderTest.getClearDownTime(time);
        assertNotNull(result, NOTNULL);
        assertEquals(timeMinusOneMin.format(ClearDownHelper.DATETIME_FORMAT),
            result.format(ClearDownHelper.DATETIME_FORMAT), EQUAL);
    }

    @Test
    void testResetLiveDisplays() {
        // Setup
        List<XhbCrLiveDisplayDao> xhbCrLiveDisplayDaoList = new ArrayList<>();
        // Run (with an empty list)
        boolean result = testResetLiveDisplays(xhbCrLiveDisplayDaoList);
        assertTrue(result, TRUE);
        // Run (with an populated list)
        xhbCrLiveDisplayDaoList.add(DummyDisplayUtil.getXhbCrLiveDisplayDao());
        xhbCrLiveDisplayDaoList.add(DummyDisplayUtil.getXhbCrLiveDisplayDao());
        result = testResetLiveDisplays(xhbCrLiveDisplayDaoList);
        assertTrue(result, TRUE);
    }

    private boolean testResetLiveDisplays(List<XhbCrLiveDisplayDao> xhbCrLiveDisplayDaoList) {
        // Expects
        Mockito.when(mockXhbCrLiveDisplayRepository.findLiveDisplaysWhereStatusNotNull())
            .thenReturn(xhbCrLiveDisplayDaoList);
        mockXhbCrLiveDisplayRepository.update(Mockito.isA(XhbCrLiveDisplayDao.class));
        Mockito.atLeast(xhbCrLiveDisplayDaoList.size());
        // Run
        try {
            classUnderTest.resetLiveDisplays();
            return true;
        } catch (Exception ex) {
            fail(ex.getMessage());
            return false;
        }
    }
}
