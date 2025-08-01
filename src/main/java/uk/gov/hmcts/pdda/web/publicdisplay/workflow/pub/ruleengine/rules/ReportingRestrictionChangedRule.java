package uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.ruleengine.rules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicDisplayEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicNoticeEvent;

/**

 * Title: Reporting restrictions have changed rule.


 * Description: Checks whether the public notice event has a reporting restiction change


 * Copyright: Copyright (c) 2004


 * Company: EDS

 * @author Rakesh Lakhani
 * @version $Id: ReportingRestrictionChangedRule.java,v 1.1 2004/02/06 11:16:49 sz0t7n Exp $ $Log:
 *          ReportingRestrictionChangedRule.java,v $ sz0t7n Exp $ Revision 1.3 2006/06/05 12:32:37
 *          bzjrnl sz0t7n Exp $ Change: TI901 sz0t7n Exp $ Comment: Weblogic Upgrade - Standadise
 *          code formatting tab fix sz0t7n Exp $ sz0t7n Exp $ Revision 1.2 2006/05/31 14:27:09
 *          bzjrnl sz0t7n Exp $ Change: TI901 sz0t7n Exp $ Comment: Weblogic Upgrade - Standadise
 *          code formatting sz0t7n Exp $ Revision 1.1 2004/02/06 11:16:49 sz0t7n Issue with
 *          reporting restriction not rerendering

 */

public class ReportingRestrictionChangedRule implements Rule {
    private static final Logger LOG =
        LoggerFactory.getLogger(ReportingRestrictionChangedRule.class);

    /**
     * Rule is true if the case associated with the event being processed has its display turned on.

     * @param event An event that sub classes
     * @return boolean
     */
    @Override
    public boolean isValid(PublicDisplayEvent event) {
        if (event instanceof PublicNoticeEvent) {
            return ((PublicNoticeEvent) event).isReportingRestrictionsChanged();
        } else {
            LOG.error("Rule called with wrong error type:" + event.getClass().toString());
            return false;
        }
    }
}
