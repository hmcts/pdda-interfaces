package uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.ruleengine.rules;

import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicDisplayEvent;

/**
 * <p>
 * Title: Rule.
 * </p>
 * <p>
 * Description: This is the interface for a rule. A Rule will check for a condition or state based
 * on the event generated. The outcome will be used to decide if a document needs to be rerendered.
 * A rule is not specifically linked to a document and can be re-used for multiple events and
 * documents
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: EDS
 * </p>
 * 
 * @author Rakesh Lakhani
 * @version $Id: Rule.java,v 1.3 2006/06/05 12:32:37 bzjrnl Exp $
 */
@SuppressWarnings("PMD.ShortClassName")
public interface Rule {
    /**
     * isValid.
     * 
     * @return true if the rule passes.
     */
    boolean isValid(PublicDisplayEvent event);
}
