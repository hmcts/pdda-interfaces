package uk.gov.hmcts.controller;

import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(EasyMockExtension.class)
class SetupControllerTest {

    @Mock
    private ModelAndView mockModelAndView;

    @TestSubject
    private SetupController classUnderTest;

    @BeforeEach
    public void setUp() {
        mockModelAndView = EasyMock.createMock(ModelAndView.class);
        
        classUnderTest = new SetupController();
    }

    @AfterEach
    public void tearDown() {
        // Do nothing
    }

    @Test
    void testDisplaySelectorServlet() {
        ModelAndView result = classUnderTest.displaySelectorServlet(mockModelAndView);
        assertNotNull(result, "Result is Null");
    }
}
