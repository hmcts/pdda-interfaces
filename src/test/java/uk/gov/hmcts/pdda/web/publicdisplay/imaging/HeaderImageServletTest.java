package uk.gov.hmcts.pdda.web.publicdisplay.imaging;

import jakarta.servlet.ServletContext;
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

import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings("PMD")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class HeaderImageServletTest {

    private static final String TRUE = "Result is not True";

    @Mock
    private HttpServletResponse mockHttpServletResponse;

    @Mock
    private HttpServletRequest mockHttpServletRequest;

    @Mock
    private ServletOutputStream mockOutputStream;

    @Mock
    private Image mockImage;

    @Mock
    private ServletContext mockServletContext;

    @InjectMocks
    private final HeaderImageServlet classUnderTest = new HeaderImageServlet() {

        private static final long serialVersionUID = 1L;

        @Override
        protected void drawImageWithWait(Graphics graphics, final Image image) {
            // Do Nothing for test
        }

        @Override
        public ServletContext getServletContext() {
            return mockServletContext;
        }

        @Override
        Image obtainImage(final URL url) {
            return mockImage;
        }
    };

    @BeforeAll
    public static void setUp() {
        Mockito.mockStatic(ImageIO.class);
    }

    @AfterAll
    public static void tearDown() {
        Mockito.clearAllCaches();
    }

    @Test
    void testGetLastModified() {
        boolean result;
        classUnderTest.getLastModified(mockHttpServletRequest);
        result = true;

        assertTrue(result, TRUE);
    }

    @Test
    void testDoGet() throws ServletException {
        String path = "/test";
        Mockito.when(mockHttpServletRequest.getPathInfo()).thenReturn(path);
        Mockito.when(mockServletContext.getRealPath(path)).thenReturn(path);
        mockProcessImage();
        boolean result = false;
        try {
            classUnderTest.doGet(mockHttpServletRequest, mockHttpServletResponse);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }

        assertTrue(result, TRUE);
    }

    @Test
    void testProcessImage() throws ServletException, IOException {
        boolean result;
        mockProcessImage();
        classUnderTest.processImage(mockImage, mockHttpServletRequest);
        result = true;

        assertTrue(result, TRUE);
    }

    private void mockProcessImage() {
        String[] paramNames = {"width", "height", "x", "y", "fontSize", "text", "size"};
        String[] paramValues = {"100", "100", "-1", "-1", "11", "Test Image", "100"};
        for (int i = 0; i < paramNames.length; i++) {
            Mockito.when(mockHttpServletRequest.getParameter(paramNames[i])).thenReturn(paramValues[i]);
        }
        Mockito.when(mockImage.getHeight(null)).thenReturn(100);
    }
}
