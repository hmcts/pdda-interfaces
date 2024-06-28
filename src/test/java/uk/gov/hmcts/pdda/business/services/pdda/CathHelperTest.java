package uk.gov.hmcts.pdda.business.services.pdda;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * <p>
 * Title: CathHelperTest Test.
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
 * @author Mark Harris
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CathHelperTest {

    private static final String TRUE = "Result is False";

    @Mock
    private BlobHelper mockBlobHelper;

    @InjectMocks
    private CathHelper classUnderTest;

    @BeforeEach
    public void setUp() throws Exception {
        classUnderTest = new CathHelper(mockBlobHelper);
    }

    @AfterEach
    public void tearDown() throws Exception {
        // Do nothing
    }

    @Test
    void testSend() {
        // Setup
        String json = "";
        // Run
        boolean result = false;
        try {
            classUnderTest.send(json);
            result = true;
        } catch (Exception exception) {
            fail(exception.getMessage());
        }
        // Checks
        assertTrue(result, TRUE);
    }
}
