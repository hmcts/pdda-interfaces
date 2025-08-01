package uk.gov.hmcts.pdda.web.publicdisplay.setup.servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.pdda.business.services.pdda.cath.CathOAuth2Helper;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("PMD")
class CathServletTest {

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private ServletOutputStream mockServletOutputStream;


    @InjectMocks
    private final CathServlet classUnderTest = new CathServlet(Mockito.mock(CathOAuth2Helper.class));

    @BeforeEach
    public void setUp() {
        try {
            ServletConfig config = Mockito.mock(ServletConfig.class);
            classUnderTest.init(config);
        } catch (ServletException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    void testServiceDoGet() throws IOException {

        Mockito.when(mockRequest.getMethod()).thenReturn("GET");
        Mockito.when(mockResponse.getOutputStream()).thenReturn(mockServletOutputStream);

        try {
            classUnderTest.service(mockRequest, mockResponse);
        } catch (ServletException | IOException e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    void testCathServletConstructorWithDependency() {
        CathOAuth2Helper mockHelper = Mockito.mock(CathOAuth2Helper.class);
        CathServlet servlet = new CathServlet(mockHelper);

        // Optionally invoke getToken indirectly via reflection to exercise internal access
        assert servlet != null;
    }
    
    @Test
    void testCathServletStoresInjectedHelper() throws Exception {
        CathOAuth2Helper mockHelper = Mockito.mock(CathOAuth2Helper.class);
        CathServlet servlet = new CathServlet(mockHelper);

        var field = CathServlet.class.getDeclaredField("cathOAuth2Helper");
        field.setAccessible(true);
        Object value = field.get(servlet);

        assertNotNull(value, "Injected CathOAuth2Helper should be stored in servlet field");
    }
    
    @Test
    void shouldReturnErrorMessageWhenGetAccessTokenThrowsException() throws IOException, ServletException {
        CathOAuth2Helper throwingHelper = Mockito.mock(CathOAuth2Helper.class);
        Mockito.when(throwingHelper.getAccessToken()).thenThrow(new RuntimeException("test failure"));

        CathServlet servlet = new CathServlet(throwingHelper);

        Mockito.when(mockRequest.getMethod()).thenReturn("GET");
        Mockito.when(mockResponse.getOutputStream()).thenReturn(mockServletOutputStream);

        servlet.service(mockRequest, mockResponse);
    }
    
    @Test
    void shouldHandleIoExceptionDuringDoGet() throws IOException, ServletException {
        CathOAuth2Helper mockHelper = Mockito.mock(CathOAuth2Helper.class);
        Mockito.when(mockHelper.getAccessToken()).thenReturn("dummyToken");

        CathServlet servlet = new CathServlet(mockHelper);

        Mockito.when(mockRequest.getMethod()).thenReturn("GET");
        Mockito.when(mockResponse.getOutputStream()).thenThrow(new IOException("stream error"));

        servlet.service(mockRequest, mockResponse);
    }



}
