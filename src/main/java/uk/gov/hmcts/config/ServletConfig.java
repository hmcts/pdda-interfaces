package uk.gov.hmcts.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.pdda.business.services.pdda.cath.CathOAuth2Helper;
import uk.gov.hmcts.pdda.web.publicdisplay.imaging.HeaderImageServlet;
import uk.gov.hmcts.pdda.web.publicdisplay.setup.servlet.CathServlet;

@Configuration
public class ServletConfig {
    
    @Autowired
    private CathOAuth2Helper cathOAuth2Helper;

    @Bean
    public ServletRegistrationBean<HeaderImageServlet> headerImageServlet() {
        ServletRegistrationBean<HeaderImageServlet> srb =
            new ServletRegistrationBean<>(new HeaderImageServlet(), "/header-image/*");
        srb.setLoadOnStartup(1);
        return srb;
    }
    
    @Bean
    public ServletRegistrationBean<CathServlet> cathServlet() {
        CathServlet servlet = new CathServlet(cathOAuth2Helper); // Spring-injected instance
        return new ServletRegistrationBean<>(servlet, "/Cath");
    }
}
