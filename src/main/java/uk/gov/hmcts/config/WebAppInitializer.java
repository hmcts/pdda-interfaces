package uk.gov.hmcts.config;

import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.core.env.Environment;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import uk.gov.hmcts.framework.scheduler.web.SchedulerInitServlet;
import uk.gov.hmcts.pdda.business.services.cppstaginginboundejb3.CppStagingInboundControllerBean;
import uk.gov.hmcts.pdda.web.publicdisplay.initialization.servlet.InitServlet;

public class WebAppInitializer implements ServletContextInitializer {

    public static final String INIT_SERVLET_NAME = "InitServlet";

    public static final String SCHEDULER_INIT_SERVLET_NAME = "SchedulerInitServlet";

    @Autowired
    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    private Environment environment;

    @Autowired
    private CppStagingInboundControllerBean csicb;

    public WebAppInitializer(EntityManagerFactory entityManagerFactory, Environment environment) {
        this.entityManagerFactory = entityManagerFactory;
        this.environment = environment;
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        try (AnnotationConfigWebApplicationContext ctx =
            new AnnotationConfigWebApplicationContext()) {
            ctx.setServletContext(servletContext);

            ServletRegistration.Dynamic initServlet = servletContext.addServlet(INIT_SERVLET_NAME,
                new InitServlet(entityManagerFactory, environment));
            initServlet.setLoadOnStartup(1);

            ServletRegistration.Dynamic schedulerInitServlet = servletContext
                .addServlet(SCHEDULER_INIT_SERVLET_NAME, new SchedulerInitServlet(csicb));
            schedulerInitServlet.setLoadOnStartup(2);
        }
    }

}
