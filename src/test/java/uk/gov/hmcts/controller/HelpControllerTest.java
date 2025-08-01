package uk.gov.hmcts.controller;

import org.easymock.EasyMockExtension;
import org.easymock.TestSubject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(EasyMockExtension.class)
class HelpControllerTest {

    @TestSubject
    private final HelpController classUnderTest = new HelpController();

    @BeforeAll
    public static void setUp() {
        // Do nothing
    }

    @AfterAll
    public static void tearDown() {
        // Do nothing
    }

    @Test
    void testHelp() {
        ModelAndView result = classUnderTest.help();
        assertNotNull(result, "Result is Null");
    }

    @Test
    void testConfig() {
        ModelAndView result = classUnderTest.config();
        assertNotNull(result, "Result is Null");
    }

    @Test
    void testStatus() {
        ModelAndView result = classUnderTest.status();
        assertNotNull(result, "Result is Null");
    }
}
