package uk.gov.hmcts.controller;

import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(EasyMockExtension.class)
class SetupControllerTest {

    @Mock
    private ModelAndView mockModelAndView;

    @TestSubject
    private final SetupController classUnderTest = new SetupController();

    @BeforeAll
    public static void setUp() throws Exception {
        // Do nothing
    }

    @AfterAll
    public static void tearDown() throws Exception {
        // Do nothing
    }

    @Test
    void testDisplaySelectorServlet() {
        ModelAndView result = classUnderTest.displaySelectorServlet(mockModelAndView);
        assertNotNull(result, "Result is Null");
    }
}
