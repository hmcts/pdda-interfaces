package uk.gov.hmcts.framework.scheduler;

import jakarta.servlet.ServletContextEvent;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.framework.scheduler.web.SchedulerInitServlet;
import uk.gov.hmcts.pdda.business.services.cppstaginginboundejb3.CppStagingInboundControllerBean;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * <p>
 * Title: SchedulerInitServlet Test.
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
@ExtendWith(EasyMockExtension.class)
class SchedulerInitServletTest {

    private static final String TRUE = "Result is not True";

    @Mock
    private CppStagingInboundControllerBean mockCppStagingInboundControllerBean;

    @Mock
    private ServletContextEvent mockServletContextEvent;

    @TestSubject
    private final SchedulerInitServlet classUnderTest = new SchedulerInitServlet(mockCppStagingInboundControllerBean);

    @BeforeAll
    public static void setUp() {
        // Do nothing
    }

    @AfterAll
    public static void tearDown() {
        // Do nothing
    }

    @Test
    void testContextInitialized() {
        boolean result = false;
        try {
            classUnderTest.contextInitialized(mockServletContextEvent);
            result = true;
        } catch (Exception exception) {
            fail(exception.getMessage());
        }
        assertTrue(result, TRUE);
    }
}
