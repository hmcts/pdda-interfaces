package uk.gov.hmcts.framework.scheduler;

import java.util.Properties;

/**
 * Interface for Strategy Pattern implementation of task scheduler.

 * Title: Interface to be implemented to create a strategy for Schedulable execution.


 * Description: Classes implementing this interface configure and initialise tasks for execution.
 * Implementations can be written to perform a particular specialised task or be written more
 * generically such as JavaTaskStrategy.


 * Copyright: Copyright (c) 2003


 * Company: EDS

 * @author Bob Boothby
 */
public interface TaskStrategy {
    /**
     * This method is to be implemented to configure the strategy implementation with any additional
     * properties that it may need. The schedulable is passed int primarily to allow the validity of
     * initialisation to be recorded.
     */
    void init(Properties props, Schedulable schedulable);

    /**
     * Method to be overridden in concrete implementations of this class to actually execute the
     * task.
     */
    void executeTask();

    /**
     * Cleanup.
     */
    void cleanup();

}
