package uk.gov.hmcts.pdda.web.publicdisplay.setup.servlet;

import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(EasyMockExtension.class)
class CathServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @TestSubject
    private static CathServlet classUnderTest = new CathServlet();

    @BeforeAll
    public static void setUp() { 
        try {
            ServletConfig config = EasyMock.createMock(ServletConfig.class);
            classUnderTest.init(config);
        } catch (ServletException ex) {
            fail(ex.getMessage());
        }
    }

    @AfterAll
    public static void tearDown() {
        // Do nothing
    }

    @Test
    void testServiceDoGet() {

        EasyMock.expect(request.getMethod()).andReturn("GET");
        EasyMock.replay(request);
        
        try {
            classUnderTest.service(request, response);
        } catch (ServletException | IOException e) {
            fail(e.getMessage());
        }

        EasyMock.verify(request);
    }
}
