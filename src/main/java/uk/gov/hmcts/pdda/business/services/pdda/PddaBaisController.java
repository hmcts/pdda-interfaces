package uk.gov.hmcts.pdda.business.services.pdda;

import jakarta.ejb.Remote;

@Remote
public interface PddaBaisController {

    /**
     * <p>
     * Scheduler task wrapper to retrieve Bais events.
     * </p>
     */
    void doTask(String taskName);

    /**
     * <p>
     * Retrieve Bais events from CP.
     * </p>
     */
    void retrieveFromBaisCP();

    /**
     * <p>
     * Retrieve Bais events from Xhibit.
     * </p>
     */
    void retrieveFromBaisXhibit();
}
