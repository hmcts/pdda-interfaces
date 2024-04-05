package uk.gov.hmcts.config;

import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class WebAppInitializerTest {

    protected static final String TRUE = "Result is not True";
    
    @Mock
    private EntityManagerFactory mockEntityManagerFactory;

    @Test
    void testDefaultConstructor() {
        boolean result = false;
        try {
            new WebAppInitializer(mockEntityManagerFactory);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }
}
