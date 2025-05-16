package uk.gov.hmcts.pdda.web.publicdisplay.setup.servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
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

import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CathServletTest {

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private ServletOutputStream mockServletOutputStream;


    @InjectMocks
    private CathServlet classUnderTest = new CathServlet(Mockito.mock(CathOAuth2Helper.class));

    @BeforeEach
    public void setUp() {
        try {
            ServletConfig config = Mockito.mock(ServletConfig.class);
            classUnderTest.init(config);
        } catch (ServletException ex) {
            fail(ex.getMessage());
        }
    }

    @AfterEach
    public void teardown() {
        classUnderTest = new CathServlet();
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
}
