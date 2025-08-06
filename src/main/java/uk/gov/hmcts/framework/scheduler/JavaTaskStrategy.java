package uk.gov.hmcts.framework.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;



/**

 * Title: TaskStrategy implementation allowing the execution of plain java classes implementing the
 * JavaTask interface.


 * Description: Handles the execution of a java class that implements the JavaTask interface and has
 * a no arguments constructor. In future, more complex TaskStrategy implementation that support
 * complex constructor may be implemented.


 * To use this strategy it must be supplied with the required property defined in JAVA_TASK_CLASS,
 * otherwise it will not be valid after initialisation.


 * Copyright: Copyright (c) 2003


 * Company: EDS

 * @author Bob Boothby
 */
public class JavaTaskStrategy implements TaskStrategy {
    private static Logger log = LoggerFactory.getLogger(JavaTaskStrategy.class);

    /**
     * Property to set in order to define the class of the task. It must be an implementation of
     * JavaTask. There can be no default for this.
     */
    public static final String JAVA_TASK_CLASS = "class";
    public static final String CLASS_DEFINED = "Class defined in ";

    private JavaTask task;

    /**
     * Initializer that instantiates the class implementing JavaTask and makes it available for
     * scheduling.
     */
    @Override
    public void init(Properties props, Schedulable schedulable) {
        // Assume invalid unless otherwise specified...
        boolean valid = false;

        String name = schedulable.getName();

        String classname = props.getProperty(JAVA_TASK_CLASS);
        if (classname != null) {
            try {
                Class<?> taskClass = Class.forName(classname);
                Object obj = taskClass.newInstance();
                task = (JavaTask) obj;
                // Only here can we be sure that initialisation has been
                // successful.
                valid = true;
            } catch (ClassNotFoundException cnfe) {
                log.error(
                    CLASS_DEFINED + name + "." + JAVA_TASK_CLASS + " property does not exist.",
                    cnfe);
            } catch (InstantiationException ie) {
                log.error(CLASS_DEFINED + name + "." + JAVA_TASK_CLASS
                    + " property could not be instantiated.", ie);
            } catch (IllegalAccessException iae) {
                log.error(CLASS_DEFINED + name + "." + JAVA_TASK_CLASS
                    + " property could not be accessed.", iae);
            } catch (ClassCastException cce) {
                log.error(CLASS_DEFINED + name + "." + JAVA_TASK_CLASS
                    + " property is not an instance of ScheduledTask.", cce);
            }
        } else {
            log.error(
                "There is no class defined by " + name + "." + JAVA_TASK_CLASS + " property.");
        }
        schedulable.setValid(valid);
    }

    /**
     * Actually executes the ScheduledTask implementation.
     */
    @Override
    public void executeTask() {
        task.doTask();
    }

    /**
     * Cleans up.
     */
    @Override
    public void cleanup() {
        if (task instanceof Stoppable) {
            ((Stoppable) task).stop();
        }
    }

}
