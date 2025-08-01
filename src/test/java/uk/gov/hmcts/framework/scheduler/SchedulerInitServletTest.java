package uk.gov.hmcts.framework.scheduler;

import jakarta.servlet.ServletContextEvent;
import org.easymock.EasyMock;
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

 * Title: SchedulerInitServlet Test.


 * Description:


 * Copyright: Copyright (c) 2024


 * Company: CGI

 * @author Mark Harris
 */
@SuppressWarnings("PMD")
@ExtendWith(EasyMockExtension.class)
class SchedulerInitServletTest {

    private static final String TRUE = "Result is not True";

    @Mock
    private ServletContextEvent mockServletContextEvent;

    @TestSubject
    private final SchedulerInitServlet classUnderTest =
        new SchedulerInitServlet(EasyMock.createMock(CppStagingInboundControllerBean.class));

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
