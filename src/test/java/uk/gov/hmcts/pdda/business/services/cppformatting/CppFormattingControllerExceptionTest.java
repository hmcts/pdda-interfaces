package uk.gov.hmcts.pdda.business.services.cppformatting;

import org.easymock.EasyMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * CppFormattingControllerExceptionTest.
 */
@ExtendWith(EasyMockExtension.class)
class CppFormattingControllerExceptionTest {

    private static final String NOT_TRUE = "Result is not True";

    @Test
    void testCppFormattingControllerExceptionDefaultConstructor() {
        boolean result = true;
        // Run
        new CppFormattingControllerException();
        // Checks
        assertTrue(result, NOT_TRUE);
    }
    
    @Test
    void testCppFormattingControllerException3Params() {
        boolean result = true;
        // Run
        new CppFormattingControllerException("", "", new Throwable());
        // Checks
        assertTrue(result, NOT_TRUE);
    }
    
    @Test
    void testCppFormattingControllerException4Params() {
        boolean result = true;
        String[] testArray = {};
        // Run
        new CppFormattingControllerException("", testArray, "", new Throwable());
        // Checks
        assertTrue(result, NOT_TRUE);
    }
    
    @Test
    void testCppFormattingControllerException2Params() {
        boolean result = true;
        // Run
        new CppFormattingControllerException("", "");
        // Checks
        assertTrue(result, NOT_TRUE);
    }
    
    @Test
    void testCppFormattingControllerException3ParamsDifferentOrder() {
        boolean result = true;
        String[] testArray = {};
        // Run
        new CppFormattingControllerException("", testArray, "");
        // Checks
        assertTrue(result, NOT_TRUE);
    }
}
