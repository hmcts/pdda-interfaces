package uk.gov.hmcts.framework.services.audittrail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;


/**

 * Title: AuditTrailMessage.


 * Description: This class is responsible for creating the formatted message from an AuditTrailEvent


 * Copyright: Copyright (c) 2008


 * Company: Logica

 * @author James Powell
 * @version 1.0
 */

public class AuditTrailMessage {
    private static final Logger LOG = LoggerFactory.getLogger(AuditTrailMessage.class);
    private final String messageFormat;

    /**
     * The constructor accepts a format String which will be used to format the message.

     * @param messageFormat - Format String
     */
    public AuditTrailMessage(String messageFormat) {
        this.messageFormat = messageFormat;
    }

    /**
     * This class is responsible for returning a formatted string for the given message using the
     * FormatString passed to the constructor.
     * <p/>
     * The field values are currently:
     * <ul>
     * <li>1. Timestamp</li>
     * <li>2. Workstation identifier</li>
     * <li>3. User ID</li>
     * <li>4. Court House ID</li>
     * <li>5. Event Type</li>
     * <li>6. Success or Failure</li>
     * <li>7. Case Identifier</li>
     * <li>8. Event Specific Data</li>
     * </ul>

     * @param evt - AuditTrailEvent
     * @return String - the formatted message
     */
    public String getFormattedMessage(AuditTrailEvent evt) {
        LOG.debug("Returning formatted message");
        Date timestamp = evt.getTimestamp();
        String workstationId = evt.getWorkstationId();
        String userId = evt.getUserId();
        String courtHouseId = evt.getCourtHouseId();
        String eventType = evt.getEvtType();
        Boolean success = evt.isSuccess();
        Integer caseId = evt.getCaseId();
        String evtSpecificData = evt.getEventSpecificData();

        String formattedMessage = String.format(messageFormat, timestamp, workstationId, userId,
            courtHouseId, eventType, success, caseId, evtSpecificData);

        LOG.debug("Returning - " + formattedMessage);
        return formattedMessage;
    }
}
