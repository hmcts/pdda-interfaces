package uk.gov.hmcts.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.pdda.web.publicdisplay.imaging.HeaderImageServlet;
import uk.gov.hmcts.pdda.web.publicdisplay.setup.servlet.CathServlet;

@Configuration
public class ServletConfig {

    @Bean
    public ServletRegistrationBean<HeaderImageServlet> headerImageServlet() {
        ServletRegistrationBean<HeaderImageServlet> srb =
            new ServletRegistrationBean<>(new HeaderImageServlet(), "/header-image/*");
        srb.setLoadOnStartup(1);
        return srb;
    }
    
    @Bean
    public ServletRegistrationBean<CathServlet> cathServlet() {
        ServletRegistrationBean<CathServlet> srb =
            new ServletRegistrationBean<>(new CathServlet(), "/Cath");
        srb.setLoadOnStartup(1);
        return srb;
    }
}
