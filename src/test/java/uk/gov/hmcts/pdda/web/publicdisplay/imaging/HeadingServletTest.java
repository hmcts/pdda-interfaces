package uk.gov.hmcts.pdda.web.publicdisplay.imaging;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings("PMD")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class HeadingServletTest {

    private static final String TRUE = "Result is not True";

    @Mock
    private HttpServletRequest mockHttpServletRequest;

    @Mock
    private HttpServletResponse mockHttpServletResponse;

    @Mock
    private ServletOutputStream mockOutputStream;

    @InjectMocks
    private final HeadingServlet classUnderTest = new HeadingServlet();

    @BeforeAll
    public static void setUp() {
        // Do nothing
    }

    @AfterAll
    public static void tearDown() {
        // Do nothing
    }

    @Test
    void testDoImage() throws ServletException, IOException {
        String[] paramNames = {"width", "height", "x", "y", "fontSize", "text"};
        String[] paramValues = {"100", "100", "1", "1", "11", "Test Image"};
        for (int i = 0; i < paramNames.length; i++) {
            Mockito.when(mockHttpServletRequest.getParameter(paramNames[i])).thenReturn(paramValues[i]);
        }
        Mockito.when(mockHttpServletResponse.getOutputStream()).thenReturn(mockOutputStream);
        boolean result = false;
        try {
            classUnderTest.doImage(mockHttpServletRequest, mockHttpServletResponse);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }

        assertTrue(result, TRUE);
    }

    @Test
    void testColors() {
        assertNotNull(Colors.TRANSLUCENT_GREY, "Result is Null");
        assertNotNull(Colors.TRANSLUCENT_WHITE, "Result is Null");
        assertNotNull(Colors.TEXT_FOREGROUND, "Result is Null");
    }
}
