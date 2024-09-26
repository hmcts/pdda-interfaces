package uk.gov.hmcts.config;

import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRegistration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.env.Environment;
import uk.gov.hmcts.framework.scheduler.web.SchedulerInitServlet;
import uk.gov.hmcts.pdda.web.publicdisplay.initialization.servlet.InitServlet;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class WebAppInitializerTest {

    protected static final String TRUE = "Result is not True";

    @Mock
    private EntityManagerFactory mockEntityManagerFactory;

    @Mock
    private ServletContext mockServletContext;

    @Mock
    private ServletRegistration.Dynamic mockInitServlet;

    @Mock
    private ServletRegistration.Dynamic mockSchedulerInitServlet;
    
    @Mock
    private Environment mockEnvironment;

    @Test
    void testDefaultConstructor() {
        Mockito.when(mockServletContext.addServlet(Mockito.isA(String.class), Mockito.isA(InitServlet.class)))
            .thenReturn(mockInitServlet);
        Mockito.atMostOnce();
        Mockito.when(mockServletContext.addServlet(Mockito.isA(String.class), Mockito.isA(SchedulerInitServlet.class)))
            .thenReturn(mockSchedulerInitServlet);
        Mockito.atMostOnce();
        boolean result = false;
        try {
            new WebAppInitializer(mockEntityManagerFactory, mockEnvironment).onStartup(mockServletContext);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }
}
