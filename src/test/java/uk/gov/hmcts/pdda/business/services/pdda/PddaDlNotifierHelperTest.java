package uk.gov.hmcts.pdda.business.services.pdda;

import jakarta.persistence.EntityManager;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.DummyServicesUtil;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropDao;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbpddadlnotifier.XhbPddaDlNotifierRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * <p>
 * Title: PDDA Dl Notifier Test.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2022
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @author Mark Harris
 */
@ExtendWith(EasyMockExtension.class)
class PddaDlNotifierHelperTest {

    private static final String NOT_FALSE = "Result is not False";
    private static final String NOT_TRUE = "Result is not True";
    private static final DateTimeFormatter DL_NOTIFIER_EXECUTION_TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private XhbConfigPropRepository mockXhbConfigPropRepository;

    @Mock
    private XhbCourtRepository mockXhbCourtRepository;

    @Mock
    private XhbPddaDlNotifierRepository mockXhbPddaDlNotifierRepository;

    @TestSubject
    private final PddaDlNotifierHelper classUnderTest =
        new PddaDlNotifierHelper(mockEntityManager, mockXhbConfigPropRepository);


    private static class Config {
        static final String PDDA_SWITCHER = "PDDA_SWITCHER";
        static final String DL_NOTIFIER_EXECUTION_TIME = "DL_NOTIFIER_EXECUTION_TIME";
    }

    @Test
    void testDefaultConstructor() {
        boolean result = true;
        new PddaDlNotifierHelper(mockEntityManager);
        assertTrue(result, NOT_TRUE);
    }
    
    @Test
    void testIsDailyNotifierRequiredSuccess() {
        // Setup
        String minuteAgo = LocalDateTime.now().minusMinutes(1).format(DL_NOTIFIER_EXECUTION_TIME_FORMAT);
        expectXhbConfigPropDao(Config.DL_NOTIFIER_EXECUTION_TIME, minuteAgo);
        expectXhbConfigPropDao(Config.PDDA_SWITCHER, "1");
        EasyMock.replay(mockXhbConfigPropRepository);
        // Run
        boolean result = classUnderTest.isDailyNotifierRequired();
        // Checks
        EasyMock.verify(mockXhbConfigPropRepository);
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testIsDailyNotifierRequiredFailure() {
        // Setup
        expectXhbConfigPropDao(Config.DL_NOTIFIER_EXECUTION_TIME, "InvalidEntry");
        EasyMock.replay(mockXhbConfigPropRepository);
        // Run
        boolean result = classUnderTest.isDailyNotifierRequired();
        // Checks
        EasyMock.verify(mockXhbConfigPropRepository);
        assertFalse(result, NOT_FALSE);
    }

    private void expectXhbConfigPropDao(String propertyName, String propertyValue) {
        List<XhbConfigPropDao> dummyXhbConfigPropDaoList = DummyServicesUtil.getNewArrayList();
        dummyXhbConfigPropDaoList.add(DummyServicesUtil.getXhbConfigPropDao(propertyName, propertyValue));
        EasyMock.expect(mockXhbConfigPropRepository.findByPropertyName(propertyName))
            .andReturn(dummyXhbConfigPropDaoList);
    }
}
