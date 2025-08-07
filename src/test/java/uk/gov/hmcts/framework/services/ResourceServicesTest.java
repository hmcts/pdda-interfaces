package uk.gov.hmcts.framework.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.framework.exception.CsUnrecoverableException;

@SuppressWarnings("PMD")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ResourceServicesTest {

    @InjectMocks
    private final ResourceServices classUnderTest = ResourceServices.getInstance();


    @BeforeEach
    public void setUp() {
        // Do nothing
    }

    @AfterEach
    public void tearDown() {
        // Do nothing
    }

    @Test
    void testGetResourceFailure() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            classUnderTest.getResourceAsStream(null);
        });

        Assertions.assertThrows(CsUnrecoverableException.class, () -> {
            classUnderTest.getResourceAsStream("name");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            classUnderTest.getResourceAsStream(null, null);
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            classUnderTest.getResourceAsStream("name", null);
        });
    }
}
