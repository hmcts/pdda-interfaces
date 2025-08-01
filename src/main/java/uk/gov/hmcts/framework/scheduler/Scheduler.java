package uk.gov.hmcts.framework.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;



/**

 * Title: Scheduler class that configures, initialises and controls a set of tasks using
 * Schedulables.


 * Description: This class expects properties of very specific format. The property defined by
 * SCHEDULED_TASK contains a comma separated list of task names for scheduling. The properties for
 * each of the tasks as defined in schedulable and in the defined task strategies are to prefixed by
 * the task name and a full stop.


 * For example:

 * <pre>
 *   ##############################
 *   #Current tasks for scheduling.
 *   ##############################
 *   scheduledtasks=javatask,sessionbeantask

 *   ############################
 *   #Configuration for javatask.
 *   ############################
 *   javatask.strategy=uk.gov.hmcts.framework.scheduler.JavaTaskStrategy
 *   javatask.class=uk.gov.hmcts.pdda.business.tasks.ATask
 *   javatask.fixedrate=false
 *   javatask.delay=0
 *   javatask.period=10000
 *   ###################################
 *   #Configuration for sessionbeantask.
 *   ###################################
 *   sessionbeantask.strategy=uk.gov.hmcts.framework.scheduler.RemoteSessionTaskStrategy
 *   sessionbeantask.remotehome=uk.gov.hmcts.pdda.business.services.ASessionRemoteHome
 *   sessionbeantask.lookup=ASession
 *   sessionbeantask.fixedrate=false
 *   sessionbeantask.delay=0
 *   sessionbeantask.period=10000
 * </pre>

 * Copyright: Copyright (c) 2003


 * Company: EDS

 * @author Bob Boothby
 */
public class Scheduler {
    private static Logger log = LoggerFactory.getLogger(Scheduler.class);

    /**
     * Optional System property for defining which tasks are scheduled to run.
     */
    public static final String SYSTEM_SCHEDULED_TASKS = "scheduler.scheduledtasks";

    /**
     * If system property is set to this value use file.
     */
    public static final String SYSTEM_SCHEDULED_USE_FILE = "default";

    /**
     * Mandatory property for defining which tasks are scheduled to run.
     */
    public static final String SCHEDULED_TASKS = "scheduledtasks";

    private final Map<String, Schedulable> schedulableMap;

    private final String[] scheduledTasks;

    /**
     * Initialise the scheduler, passing it's configuration properties in.

     * @param props the properties to use to configure the scheduler and it's tasks.
     */
    public Scheduler(Properties props, String taskList) {

        // Sort out what tasks have been scheduled.
        scheduledTasks =
            getArrayFromDelimitedList(getScheduledTaskDelimitedList(props, taskList), ",");

        schedulableMap = Collections.synchronizedMap(new ConcurrentHashMap<>());

        // Sort out the properties file.
        Map<String, Properties> splitPropertiesMap = splitPropertiesByPrefix(props, ".");

        // Initialise the Schedulables.
        Properties splitProperties;
        for (String scheduledTaskName : scheduledTasks) {

            // Split the properties out for each Schedulable and task.
            splitProperties = splitPropertiesMap.get(scheduledTaskName);

            schedulableMap.put(scheduledTaskName,
                getSchedulable(scheduledTaskName, splitProperties));
        }
    }
    
    private Schedulable getSchedulable(String scheduledTaskName, Properties splitProperties) {
        return new Schedulable(scheduledTaskName, splitProperties == null ? new Properties() : splitProperties);
    }

    private String getScheduledTaskDelimitedList(Properties props, String taskList) {

        String scheduledTaskDelimitedList = System.getProperty(SYSTEM_SCHEDULED_TASKS);
        if (scheduledTaskDelimitedList != null
            && !SYSTEM_SCHEDULED_USE_FILE.equals(scheduledTaskDelimitedList)) {
            return scheduledTaskDelimitedList;
        }

        String serverName = "pdda";
        String taskListName = SCHEDULED_TASKS + "." + serverName;
        log.info("Task list name::" + taskListName);
        // get the string from the database matching up to the list

        // if nothing in the database then check the prop file
        String results = taskList;
        if (results == null) {
            log.debug("task lisk is null so looking in the properties file for taskListName="
                + taskListName);
            results = props.getProperty(taskListName, "");
        }

        if (log.isDebugEnabled()) {
            log.debug("Retrieved tasks " + taskList + " for " + serverName + ".");
        }
        return results;
    }

    /**
     * Start all scheduled tasks.
     */
    public void start() {
        for (String scheduledTask : scheduledTasks) {
            boolean success = schedulableMap.get(scheduledTask).start();
            if (success) {
                log.info("Succesfully started the " + scheduledTask + " task.");
            } else {
                log.error("Failed to start the " + scheduledTask + " task.");
            }
        }
    }

    /**
     * Stop the Scheduler tidily.
     */
    public void cleanup() {
        for (String scheduledTask : scheduledTasks) {
            schedulableMap.get(scheduledTask).stop();
        }
    }

    /**
     * Utility method to parse a list into a String[].

     * @param delimitedList The list to be split.
     * @param delimiter The delimiter to use.
     * @return a String array containing the split list.
     */
    private String[] getArrayFromDelimitedList(String delimitedList, String delimiter) {
        if (delimitedList != null) {
            // Set up a StringTokenizer that does not return the delimiters
            // as tokens.
            StringTokenizer st = new StringTokenizer(delimitedList, delimiter, false);

            // Set up the return array.
            String[] returnArray = new String[st.countTokens()];

            // Populate the return array.
            for (int i = 0; st.hasMoreTokens(); i++) {
                returnArray[i] = st.nextToken().trim();
            }
            return returnArray;
        } else {
            return new String[0];
        }
    }

    /**
     * Utility method that splits a Properties object by prefix, separation is determined by the
     * passed in delimiter. The prefix is stripped from the new properties.

     * @param props Properties
     * @param delimiter The delimiter used to determine the boundary betwen the prefix and the rest
     *        of the property name.
     * @return HashMap of properties objects keyed to their original prefixes
     */
    private Map<String, Properties> splitPropertiesByPrefix(Properties props,
        String delimiter) {
        Map<String, Properties> split = new ConcurrentHashMap<>();
        int delimiterLength = delimiter.length();

        for (Enumeration<?> enumeration = props.propertyNames(); enumeration.hasMoreElements();) {
            String propertyName = (String) enumeration.nextElement();

            // Assume no prefix.
            String prefix = "";
            int splitPropertyPos = 0;

            // Find Prefix.
            int prefixPos = propertyName.indexOf(delimiter);

            // If there is a prefix
            if (prefixPos > -1) {
                prefix = propertyName.substring(0, prefixPos);
                splitPropertyPos = prefixPos + delimiterLength;
            }

            String splitPropertyName = propertyName.substring(splitPropertyPos);

            // See if there is a properties file in existence for prefix. - If not create one.
            Properties propertiesForPrefix = split.computeIfAbsent(prefix, k -> getBlankProperties());

            // Set the split property.
            propertiesForPrefix.setProperty(splitPropertyName, props.getProperty(propertyName));
        }

        return split;
    }
    
    private Properties getBlankProperties() {
        return new Properties();
    }
}
