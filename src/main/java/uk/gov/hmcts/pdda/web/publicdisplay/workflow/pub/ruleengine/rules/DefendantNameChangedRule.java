package uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.ruleengine.rules;

import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicDisplayEvent;

/**

 * Title: DefendantNameChangedRule.


 * Description: Checks if the event passed in is generated for a defendant name change


 * Copyright: Copyright (c) 2003


 * Company: EDS

 * @author Rakesh Lakhani
 * @version $Id: DefendantNameChangedRule.java,v 1.2 2004/02/05 14:36:56 sz0t7n Exp $
 */

public class DefendantNameChangedRule implements Rule {


    /**
     * This rule has been removed as it relied on a court log event to identify a change. The event
     * that is fed in is an UpdateCaseEvent. To re-enable this rule, a flag needs to be added to the
     * UpdateCaseEvent to identify the type of change. This rule can then identify the change and
     * return accordingly.

     * @param event A case status event
     * @return true if the court log event is a DefendantAmended event
     */
    @Override
    public boolean isValid(PublicDisplayEvent event) {
        return true;
    }
}
