package uk.gov.hmcts;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import uk.gov.hmcts.config.WebAppInitializer;

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
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PddaSpringbootApplicationTest {

    private static final String NOT_TRUE = "Result is not True";

    @Mock
    private ConfigurableApplicationContext mockContext;


    @Test
    void testApplicationNoArgs() {
        boolean result = testApplication(null);
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testApplicationStaging() {
        boolean result = testApplication(true);
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testApplicationNonStaging() {
        boolean result = testApplication(false);
        assertTrue(result, NOT_TRUE);
    }

    // Test added ONLY to cover main() method which does not get covered by application tests.
    boolean testApplication(Boolean isStaging) {
        // Setup
        try (MockedStatic<SpringApplication> mockSpringApplication = Mockito.mockStatic(SpringApplication.class)) {
            mockSpringApplication
                .when((MockedStatic.Verification) SpringApplication
                    .run(new Class[] {PddaSpringbootApplication.class, WebAppInitializer.class}, new String[] {}))
                .thenReturn(mockContext);
            // Run
            try {
                if (isStaging == null) {
                    PddaSpringbootApplication.main(new String[] {});
                } else {
                    PddaSpringbootApplication.main(isStaging, new String[] {});
                }
                return true;
            } catch (Exception exception) {
                return false;
            }
        }
    }
}
