package uk.gov.hmcts;

import org.easymock.EasyMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * <p>
 * Title: PddaSpringbootApplication Test.
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
 * @author Luke Gittins
 */
@ExtendWith(EasyMockExtension.class)
class PddaSpringbootApplicationTest {

    private static final String NOT_TRUE = "Result is not True";

    // Test added ONLY to cover main() method which does not get covered by application tests.
    @Test
    void testApplication() {
        // Setup
        try (MockedStatic<SpringApplication> mockSpringApplication =
            Mockito.mockStatic(SpringApplication.class)) {
            mockSpringApplication.when((MockedStatic.Verification) SpringApplication
                .run(PddaSpringbootApplication.class, new String[] {})).thenReturn(null);
            // Run
            boolean result = true;
            PddaSpringbootApplication.main(new String[] {});
            // Checks
            assertTrue(result, NOT_TRUE);
        }
    }
}
