package uk.gov.hmcts.pdda.web.publicdisplay.initialization.servlet;

import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
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
import org.springframework.core.env.Environment;
import uk.gov.hmcts.pdda.business.vos.translation.TranslationBundles;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;


/**

 * Title: InitServlet Test.


 * Description: Tests for the InitServlet class


 * Copyright: Copyright (c) 2022


 * Company: CGI

 * @author Chris Vincent
 */
@SuppressWarnings("PMD")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class InitServletTest {

    private final Locale dummyLocale = new Locale("en", "GB");

    private static final String TRUE = "Result is not True";

    @Mock
    private TranslationBundles mockTranslationBundles;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ServletConfig config;

    @InjectMocks
    private final InitServlet classUnderTest =
        new InitServlet(Mockito.mock(EntityManagerFactory.class), Mockito.mock(Environment.class));

    @BeforeAll
    public static void setUp() {
        // Do nothing
    }

    @AfterAll
    public static void tearDown() {
        // Do nothing
    }

    @Test
    void testInit() {

        final String retryPeriod = "1000";
        final String initializationDelay = "5000";
        final String initilizationWorkers = "20";

        InitializationService mockInitializationService = Mockito.mock(InitializationService.class);
        Mockito.mockStatic(InitializationService.class);
        Mockito.when(InitializationService.getInstance()).thenReturn(mockInitializationService);
        mockInitializationService.setDefaultLocale(dummyLocale);
        Mockito.when(config.getInitParameter("retry.period")).thenReturn(retryPeriod);
        mockInitializationService.setRetryPeriod(Long.parseLong(retryPeriod));
        Mockito.when(config.getInitParameter("initialization.delay"))
            .thenReturn(initializationDelay);
        mockInitializationService.setInitializationDelay(Long.parseLong(initializationDelay));
        Mockito.when(config.getInitParameter("num.initialization.workers"))
            .thenReturn(initilizationWorkers);
        mockInitializationService
            .setNumInitializationWorkers(Integer.parseInt(initilizationWorkers));
        mockInitializationService.initialize();

        boolean result = false;
        try {
            classUnderTest.init(config);
            result = true;
        } catch (ServletException e) {
            fail(e.getMessage());
        }
        assertTrue(result, TRUE);
        Mockito.clearAllCaches();
    }

    @Test
    void testService() {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        Mockito.atLeastOnce();
        boolean result = false;
        try {
            classUnderTest.service(request, response);
            result = true;
        } catch (Exception e) {
            fail(e.getMessage());
        }
        assertTrue(result, TRUE);
    }
}
