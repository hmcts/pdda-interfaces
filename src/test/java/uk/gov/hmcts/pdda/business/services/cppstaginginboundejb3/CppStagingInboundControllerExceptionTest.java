package uk.gov.hmcts.pdda.business.services.cppstaginginboundejb3;

import org.easymock.EasyMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * CppStagingInboundControllerExceptionTest.
 */
@ExtendWith(EasyMockExtension.class)
class CppStagingInboundControllerExceptionTest {

    private static final String NOT_TRUE = "Result is not True";

    @Test
    void testCppStagingInboundControllerException3Params() {
        boolean result = true;
        // Run
        new CppStagingInboundControllerException("", "", new Throwable());
        // Checks
        assertTrue(result, NOT_TRUE);
    }
    
    @Test
    void testCppStagingInboundControllerException4Params() {
        boolean result = true;
        String[] testArray = {};
        // Run
        new CppStagingInboundControllerException("", testArray, "", new Throwable());
        // Checks
        assertTrue(result, NOT_TRUE);
    }
    
    @Test
    void testCppStagingInboundControllerException2Params() {
        boolean result = true;
        // Run
        new CppStagingInboundControllerException("", "");
        // Checks
        assertTrue(result, NOT_TRUE);
    }
    
    @Test
    void testCppStagingInboundControllerException3ParamsDifferentOrder() {
        boolean result = true;
        String[] testArray = {};
        // Run
        new CppStagingInboundControllerException("", testArray, "");
        // Checks
        assertTrue(result, NOT_TRUE);
    }
}
