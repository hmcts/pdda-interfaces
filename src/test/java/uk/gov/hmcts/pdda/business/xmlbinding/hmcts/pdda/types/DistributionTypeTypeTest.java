package uk.gov.hmcts.pdda.business.xmlbinding.hmcts.pdda.types;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DistributionTypeTypeTest {

    private static final String TRUE = "Result is not True";
    private static final String NOTEQUALS = "Result is not Equals";

    @BeforeEach
    public void setup() {
        // Do nothing
    }

    @AfterEach
    public void teardown() {
        // Do nothing
    }

    @Test
    void testInvalidType() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            DistributionTypeType.valueOf("Invalid");
        });
    }

    @Test
    void testDistributionTypeTypeEmail() {
        boolean result = testType(DistributionTypeType.EMAIL, DistributionTypeType.EMAIL_TYPE);
        assertTrue(result, TRUE);
    }

    @Test
    void testDistributionTypeTypeFax() {
        boolean result = testType(DistributionTypeType.FAX, DistributionTypeType.FAX_TYPE);
        assertTrue(result, TRUE);
    }

    @Test
    void testDistributionTypeTypeFtp() {
        boolean result = testType(DistributionTypeType.FTP, DistributionTypeType.FTP_TYPE);
        assertTrue(result, TRUE);
    }

    @Test
    void testDistributionTypeTypePost() {
        boolean result = testType(DistributionTypeType.POST, DistributionTypeType.POST_TYPE);
        assertTrue(result, TRUE);
    }

    @SuppressWarnings("static-access")
    private boolean testType(DistributionTypeType classUnderTest, int type) {
        assertEquals(type, classUnderTest.getType(), NOTEQUALS);
        assertNotNull(classUnderTest.enumerate(), "Result is Null");
        assertNotNull(classUnderTest.toString(), "Result is Null");

        return true;
    }
}
