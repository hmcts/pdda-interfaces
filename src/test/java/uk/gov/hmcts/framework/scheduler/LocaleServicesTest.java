package uk.gov.hmcts.framework.scheduler;

import jakarta.persistence.EntityManager;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.pdda.business.services.cpp.CppInitialProcessingControllerBean;
import uk.gov.hmcts.pdda.business.services.cppformatting.CppFormattingControllerBean;
import uk.gov.hmcts.pdda.business.services.formatting.FormattingControllerBean;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

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
class LocaleServicesTest {

    private static final String TESTSCHEDULENAME = "TestScheduleName";
    private static final String TRUE = "Result is not True";

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

    /**
     * Tests RemoteSessionTaskStrategy.init to ensure that the controllerBean is setup as a
     * FormattingControllerBean
     */
    @Test
    void testInitFormattingControllerBean() {

        Properties testProperties = createTestProperties(FormattingControllerBean.class.getName(), Schedulable.ONCE_A_DAY_DEFAULT);
        Schedulable testSchedulable = new Schedulable(TESTSCHEDULENAME, testProperties, mockEntityManager);

        classUnderTest.init(testProperties, testSchedulable);

        assertEquals(true, testSchedulable.isValid(), "Result is not True");
        assertEquals(FormattingControllerBean.class.getName(), classUnderTest.getControllerBeanClassName(),
            "Result are not Equal");
    }

    /**
     * Tests RemoteSessionTaskStrategy.executeTask to ensure that doTask on the FormattingControllerBean
     * is invoked.
     */
    @Test
    void testExecuteTaskFormattingControllerBean() {
        RemoteTask rt = EasyMock.createMock(FormattingControllerBean.class);
        boolean result = testExecuteTask(rt);
        assertTrue(result, TRUE);
    }

    /**
     * Tests RemoteSessionTaskStrategy.init to ensure that the controllerBean is setup as a
     * CPPInitialProcessingControllerBean
     */
    @Test
    void testInitCppInitialProcessingControllerBean() {

        Properties testProperties =
            createTestProperties(CppInitialProcessingControllerBean.class.getName(), Schedulable.ONCE_A_DAY_DEFAULT);
        Schedulable testSchedulable = new Schedulable(TESTSCHEDULENAME, testProperties, mockEntityManager);

        classUnderTest.init(testProperties, testSchedulable);

        assertEquals(true, testSchedulable.isValid(), "Result not True");
        assertEquals(CppInitialProcessingControllerBean.class.getName(), classUnderTest.getControllerBeanClassName(),
            "Results are not Equal");
    }

    /**
     * Tests RemoteSessionTaskStrategy.executeTask to ensure that doTask on the
     * CPPInitialProcessingControllerBean is invoked.
     */
    @Test
    void testExecuteTaskCppInitialProcessingControllerBean() {
        RemoteTask rt = EasyMock.createMock(CppInitialProcessingControllerBean.class);
        boolean result = testExecuteTask(rt);
        assertTrue(result, TRUE);
    }

    /**
     * Tests RemoteSessionTaskStrategy.init to ensure that the controllerBean is setup as a
     * CppFormattingControllerBean
     */
    @Test
    void testInitCppFormattingControllerBean() {

        Properties testProperties =
            createTestProperties(CppFormattingControllerBean.class.getName(), Schedulable.ONCE_A_DAY_DEFAULT);
        Schedulable testSchedulable = new Schedulable(TESTSCHEDULENAME, testProperties, mockEntityManager);

        classUnderTest.init(testProperties, testSchedulable);

        assertFalse(testSchedulable.isRunning(), "Result is not False");
        assertEquals(true, testSchedulable.isValid(), "Result not True");
        assertEquals(CppFormattingControllerBean.class.getName(), classUnderTest.getControllerBeanClassName(),
            "Results are not Equal");
    }

    /**
     * Tests RemoteSessionTaskStrategy.executeTask to ensure that doTask on the
     * CppFormattingControllerBean is invoked.
     */
    @Test
    void testExecuteTaskCppFormattingControllerBean() {
        RemoteTask rt = EasyMock.createMock(CppFormattingControllerBean.class);
        boolean result = testExecuteTask(rt);
        assertTrue(result, TRUE);
    }

    @Test
    void testSchedulable() {
        Properties testProperties =
            createTestProperties(CppFormattingControllerBean.class.getName(), "true");
        Calendar timeNow = Calendar.getInstance();
        String hour = Integer.valueOf(timeNow.get(Calendar.HOUR_OF_DAY)).toString();
        String minute = Integer.valueOf(timeNow.get(Calendar.MINUTE)).toString();
        testProperties.setProperty(Schedulable.HOUR, hour);
        testProperties.setProperty(Schedulable.MINUTE, minute);
        Schedulable testSchedulable = new Schedulable(TESTSCHEDULENAME, testProperties, mockEntityManager);
        testSchedulable.start();
        testSchedulable.stop();
        assertNotNull(testSchedulable, "Reult is Null");
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
        testProperties.setProperty(Schedulable.TASK_STRATEGY, RemoteSessionTaskStrategy.class.getName());
        testProperties.setProperty(RemoteSessionTaskStrategy.REMOTE_HOME_CLASS, remoteHomeClass);
        return testProperties;
    }

    /**
     * Runs the mock tests for executeTask taking in different mocked objects.
     * 
     * @param rt Mocked RemoteTask object.
     */
    private boolean testExecuteTask(RemoteTask rt) {
        // Setup
        try {
            rt.doTask();
            EasyMock.expectLastCall();
        } catch (RemoteException e) {
            fail(e.getMessage());
            return false;
        }
        EasyMock.replay(rt);

        // Run method
        classUnderTest.executeTask(rt);

        // Checks
        EasyMock.verify(rt);
        return true;
    }

}
