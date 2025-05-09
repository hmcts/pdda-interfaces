package uk.gov.hmcts.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import uk.gov.hmcts.pdda.web.publicdisplay.imaging.HeaderImageServlet;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ServletConfigTest {

    @Test
    void shouldCreateHeaderImageServletBean() {
        ServletConfig config = new ServletConfig();
        ServletRegistrationBean<HeaderImageServlet> bean = config.headerImageServlet();
        assertNotNull(bean, "Not null");
    }
}
