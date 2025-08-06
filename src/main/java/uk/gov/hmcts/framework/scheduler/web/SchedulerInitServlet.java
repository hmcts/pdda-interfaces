package uk.gov.hmcts.framework.scheduler.web;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.http.HttpServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.framework.scheduler.Scheduler;
import uk.gov.hmcts.pdda.business.services.cppstaginginboundejb3.CppStagingInboundControllerBean;

import java.io.InputStream;
import java.util.Properties;

/**

 * Title: Initialisation servlet for a scheduler web application.


 * Description: Servlet called at instantiation of a scheduler web application. Responsible for
 * retrieving properties and initialising the scheduler. In future can be configured to provide much
 * more control over the scheduler.


 * This class requires complex configuration as it is used both as a context listener and as a
 * servlet. This is required as it needs to create the scheduler with a valid subject and not
 * destroy it untill the context is destroyed.


 * The first of these criteria is met by starting it as a servlet (use load-on-start and run-as tags
 * in config).


 * The second of these criteria is met by destroying it in the destoryContext handler of the context
 * listener. The servlet destroy method is unsafe as a servlet can be destroyed before the context
 * to free up resources.

 * Copyright: Copyright (c) 2003


 * Company: EDS

 * @see Scheduler
 * @author Bob Boothby, Will Fardell
 */
@SuppressWarnings("PMD.AvoidSynchronizedStatement")
public class SchedulerInitServlet extends HttpServlet implements ServletContextListener {
    private static final long serialVersionUID = 1L;

    private static final String DISABLE_SCHEDULER_SERVLET_KEY = "disableSchedulerServlet";

    private static final Logger LOG = LoggerFactory.getLogger(SchedulerInitServlet.class);

    /**
     * Lock used to synchronise creation and destruction of scheduler.
     */
    private static final Object SCHEDULERLOCK = new Object();

    /**
     * The position in the web application where this servlet expects to find the configuration
     * file.
     */
    public static final String SCHEDULER_PROPERTIES_PATH = "/WEB-INF/scheduler.properties";

    /**
     * The key the scheduler is stored under in the context.
     */
    public static final String SCHEDULER_CONTEXT_KEY = "SchedulerInitServlet.scheduler";

    private final CppStagingInboundControllerBean csicb;
    
    public SchedulerInitServlet(CppStagingInboundControllerBean csicb) {
        super();
        this.csicb = csicb;
    }
    
    /**
     * Called by the servlet container to indicate to a servlet that the servlet is being placed
     * into service.
     */
    @Override
    public void init() {
        if (schedulerServletDisabled()) {
            LOG.info("Scheduler has been disabled (system property " + DISABLE_SCHEDULER_SERVLET_KEY
                + " = true).");
        } else {
            ServletContext sc = getServletContext();
            String taskListFromDb = "";
            try {
                LOG.info("CS1");
                taskListFromDb = csicb.findConfigEntryByPropertyName("scheduledTasks.pdda");
                LOG.info("CS2");
            } catch (Exception e) {
                LOG.info("CS4 - {}", e.getMessage());
            }

            synchronized (SCHEDULERLOCK) {
                Scheduler scheduler = getScheduler(sc);
                if (scheduler == null) {
                    scheduler = new Scheduler(loadProperties(), taskListFromDb);
                    scheduler.start();
                    setScheduler(sc, scheduler);
                }
            }
        }
    }

    /**
     * Called by the servlet container to indicate to a servlet that the servlet is being taken out
     * of service.
     */
    @Override
    public void destroy() {
        // Do nothing
    }

    /**
     * ServletContextListener implementation: Notification that the web application is ready to
     * process requests.
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Do nothing
    }

    /**
     * ServletContextListener implementation: Notification that the servlet context is about to be
     * shut down.
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext sc = sce.getServletContext();

        synchronized (SCHEDULERLOCK) {
            Scheduler scheduler = getScheduler(sc);
            if (scheduler != null) {
                scheduler.cleanup();
                setScheduler(sc, null);
            }
        }
    }

    /**
     * Get the Scheduler from the servlet context.
     */
    private Scheduler getScheduler(ServletContext sc) {
        return (Scheduler) sc.getAttribute(SCHEDULER_CONTEXT_KEY);
    }

    /**
     * Set the Scheduler in the servlet context.
     */
    private void setScheduler(ServletContext sc, Scheduler scheduler) {
        sc.setAttribute(SCHEDULER_CONTEXT_KEY, scheduler);
    }

    /**
     * Load the properties from the specified resource.
     */
    private Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream in = getServletContext().getResourceAsStream(SCHEDULER_PROPERTIES_PATH)) {
            if (in != null) {
                properties.load(in);
            }
        } catch (java.io.IOException ioe) {
            LOG.info("Failed to load scheduling properties file. No scheduled tasks will occur.",
                ioe);
        }
        return properties;
    }

    /**
     * Return true if the scheduler servlet has been disabled by system property.
     */
    private boolean schedulerServletDisabled() {
        String property = System.getProperty(DISABLE_SCHEDULER_SERVLET_KEY);
        return property != null && "TRUE".equalsIgnoreCase(property);
    }

}
