package uk.gov.hmcts.controller;

import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(EasyMockExtension.class)
class SetupControllerTest {

    @Mock
    private Map<String, Object> mockModelMap;

    @TestSubject
    private SetupController classUnderTest;

    @BeforeEach
    public void setUp() {
        mockModelMap = EasyMock.createMock(ModelMap.class);
        
        classUnderTest = new SetupController();
    }

    @AfterEach
    public void tearDown() {
        // Do nothing
    }

    @Test
    void testDisplaySelectorServlet() {
        ModelAndView result = classUnderTest.displaySelectorServlet((ModelMap) mockModelMap);
        assertNotNull(result, "Result is Null");
    }
}
