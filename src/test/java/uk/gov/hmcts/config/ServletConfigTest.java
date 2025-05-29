package uk.gov.hmcts.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import uk.gov.hmcts.pdda.business.services.pdda.cath.CathOAuth2Helper;
import uk.gov.hmcts.pdda.web.publicdisplay.imaging.HeaderImageServlet;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

@SuppressWarnings("PMD")
class ServletConfigTest {
    
    private ServletConfig config;

    @BeforeEach
    void setUp() {
        CathOAuth2Helper cathOAuth2Helper = mock(CathOAuth2Helper.class);
        config = new ServletConfig();

        // Inject the mock via reflection since cathOAuth2Helper is private and not set via constructor
        try {
            var field = ServletConfig.class.getDeclaredField("cathOAuth2Helper");
            field.setAccessible(true);
            field.set(config, cathOAuth2Helper);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock CathOAuth2Helper", e);
        }
    }

    @Test
    void shouldCreateHeaderImageServletBean() {
        ServletConfig config = new ServletConfig();
        ServletRegistrationBean<HeaderImageServlet> bean = config.headerImageServlet();
        assertNotNull(bean, "Not null");
    }
    
    @Test
    void shouldCreateCathServletBean() {
        ServletRegistrationBean<?> bean = config.cathServlet();
        assertNotNull(bean, "CathServlet bean should not be null");
    }
}
