package uk.gov.hmcts.framework.scheduler;

import jakarta.persistence.EntityManager;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.pdda.business.services.cppformatting.CppFormattingControllerBean;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * <p>
 * Title: RemoteSessionTaskStrategyTest Test.
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
 * @author Chris Vincent
 */
@ExtendWith(EasyMockExtension.class)
class LocaleServicesFailuresTest {

    private static final String TESTSCHEDULENAME = "TestScheduleName";
    private static final String NULL = "Result is Null";
    private static final String FALSE = "false";
    private static final String BAD_DATA_STRING = "abcdefg";

    @Mock
    private EntityManager mockEntityManager;

    @TestSubject
    private final RemoteSessionTaskStrategy classUnderTest = new RemoteSessionTaskStrategy(mockEntityManager);

    @BeforeAll
    public static void setUp() throws Exception {
        // Do nothing
    }

    @AfterAll
    public static void tearDown() throws Exception {
        // Do nothing
    }

    @Test
    void testSchedulableBadData() {
        Properties testProperties = createTestProperties(CppFormattingControllerBean.class.getName(), FALSE);
        testProperties.setProperty(Schedulable.DELAY, BAD_DATA_STRING);
        testProperties.setProperty(Schedulable.PERIOD, BAD_DATA_STRING);
        testProperties.setProperty(Schedulable.HOUR, BAD_DATA_STRING);
        testProperties.setProperty(Schedulable.MINUTE, BAD_DATA_STRING);
        Schedulable testSchedulable = new Schedulable(TESTSCHEDULENAME, testProperties);
        testSchedulable.start();
        testSchedulable.stop();
        assertNotNull(testSchedulable, NULL);
    }

    @Test
    void testSchedulableNoSuchMethodException() {
        Properties testProperties = createTestProperties(this.getClass().getName(), FALSE);
        Schedulable testSchedulable = new Schedulable(TESTSCHEDULENAME, testProperties, mockEntityManager);
        testSchedulable.start();
        testSchedulable.stop();
        assertNotNull(testSchedulable, NULL);
        assertFalse(testSchedulable.isValid(), FALSE);
    }

    @Test
    void testSchedulableClassNotFoundException() {
        Properties testProperties = createTestProperties("", FALSE);
        Schedulable testSchedulable = new Schedulable(TESTSCHEDULENAME, testProperties, mockEntityManager);
        testSchedulable.start();
        testSchedulable.stop();
        assertNotNull(testSchedulable, NULL);
        assertFalse(testSchedulable.isValid(), FALSE);
    }

    @Test
    void testSchedulableNoRemoteClass() {
        Properties testProperties = createTestProperties(null, FALSE);
        Schedulable testSchedulable = new Schedulable(TESTSCHEDULENAME, testProperties, mockEntityManager);
        testSchedulable.start();
        testSchedulable.stop();
        assertNotNull(testSchedulable, NULL);
        assertFalse(testSchedulable.isValid(), FALSE);
    }

    /**
     * Creates a Properties object for the purposes of testing.
     * 
     * @param remoteHomeClass Value to use for the remoteHome property
     * @return Properties object
     */
    private Properties createTestProperties(String remoteHomeClass, String onceADay) {
        Properties testProperties = new Properties();
        testProperties.setProperty(Schedulable.FIXED_RATE, Schedulable.FIXED_RATE_DEFAULT);
        testProperties.setProperty(Schedulable.DELAY, Schedulable.DELAY_DEFAULT);
        testProperties.setProperty(Schedulable.PERIOD, Schedulable.PERIOD_DEFAULT);
        testProperties.setProperty(Schedulable.ONCE_A_DAY, onceADay);
        testProperties.setProperty(Schedulable.HOUR, Schedulable.HOUR_DEFAULT);
        testProperties.setProperty(Schedulable.MINUTE, Schedulable.MINUTE_DEFAULT);
        testProperties.setProperty(Schedulable.TASK_STRATEGY, RemoteSessionTaskStrategy.class.getName());
        if (remoteHomeClass != null) {
            testProperties.setProperty(RemoteSessionTaskStrategy.REMOTE_HOME_CLASS, remoteHomeClass);
        }
        return testProperties;
    }

}
