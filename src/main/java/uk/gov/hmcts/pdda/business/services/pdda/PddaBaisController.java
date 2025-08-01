package uk.gov.hmcts.pdda.business.services.pdda;

import jakarta.ejb.Remote;

@Remote
public interface PddaBaisController {

    /**

     * Scheduler task wrapper to retrieve Bais events.

     */
    void doTask(String taskName);

    /**

     * Retrieve Bais events from CP.

     */
    void retrieveFromBaisCP();

    /**

     * Retrieve Bais events from Xhibit.

     */
    void retrieveFromBaisXhibit();
}
