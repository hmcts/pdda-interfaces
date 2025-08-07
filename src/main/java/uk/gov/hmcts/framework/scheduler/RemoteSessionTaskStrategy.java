package uk.gov.hmcts.framework.scheduler;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;



/**
 * <p>
 * Title: TaskStrategy implementation that allows the execution of a RemoteTask implemented by a
 * session bean with a no arguments create method.
 * </p>
 * <p>
 * Description: Handles the execution of a Session Bean that implements the RemoteTask interface
 * with a no arguments constructor. In future an implementation can easily be written to allow more
 * complex create methods to be used.
 * </p>
 * <p>
 * To use this class, it must be supplied with the required properties. It will not initialise
 * correctly if the session bean's remote interface does not implement the RemoteTask interface.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: EDS
 * </p>
 * 
 * @author Bob Boothby
 */
public final class RemoteSessionTaskStrategy implements TaskStrategy {
    private static Logger log = LoggerFactory.getLogger(RemoteSessionTaskStrategy.class);

    // ControllerBean object.
    private Object controllerBean;

    // Entity Manager required to instantiate the controller bean object.
    private EntityManager entityManager;

    /**
     * Property to set in order to define the home interface class of the RemoteTask implementing
     * session bean. There can be no default for this.
     */
    public static final String REMOTE_HOME_CLASS = "remotehome";

    public RemoteSessionTaskStrategy(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public RemoteSessionTaskStrategy() {
        // public constructor
    }

    /**
     * Initialiser providing additional configuration capabilities, runtime checking and preparation
     * for execution.
     */
    @Override
    public void init(Properties props, Schedulable schedulable) {
        // Assume invalid unless otherwise specified...
        boolean valid = false;

        // Get the appropriate properties
        String remoteHomeClassname = props.getProperty(REMOTE_HOME_CLASS);
        log.info("remoteHomeClassname=" + remoteHomeClassname);

        String name = schedulable.getName();
        log.info("name=" + name);

        // Check that the properties are set.
        if (remoteHomeClassname != null) {
            try {
                // Instantiate a controller bean object with the appropriate class specified in
                // the properties
                Class<?> remoteHomeClass = Class.forName(remoteHomeClassname);
                Constructor<?> beanConstructor =
                    remoteHomeClass.getConstructor(EntityManager.class);
                controllerBean = beanConstructor.newInstance(entityManager);
                valid = true;
            } catch (ClassNotFoundException e) {
                log.error("Class defined in " + name + "." + REMOTE_HOME_CLASS
                    + " property does not exist.", e);
            } catch (InstantiationException e) {
                log.error("Object with class defined in " + name + "." + REMOTE_HOME_CLASS
                    + " cannot be instantiated.", e);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
                log.error(e.getClass().getSimpleName() + " for " + name + "." + REMOTE_HOME_CLASS,
                    e);
            }
        } else {
            log.error(
                "There is no class defined by " + name + "." + REMOTE_HOME_CLASS + " property.");
        }

        schedulable.setValid(valid);
    }

    /**
     * Actually execute the task.
     */
    @Override
    public void executeTask() {
        executeTask((RemoteTask) controllerBean);
    }

    /**
     * Actually execute the task.
     */
    public void executeTask(RemoteTask rt) {
        try {
            log.debug("RemoteSessionTaskName: About to execute task ");
            rt.doTask();
        } catch (java.rmi.RemoteException re) {
            log.error("A problem occurred in executing the remote task.", re);
        }
    }

    /**
     * Cleans up.
     */
    @Override
    public void cleanup() {
        // No clean up required.
    }

    /**
     * Returns the classname of the controller bean. Used for unit testing.
     * 
     * @return Name of the controllerBean's class
     */
    public Object getControllerBeanClassName() {
        return controllerBean.getClass().getName();
    }
}
