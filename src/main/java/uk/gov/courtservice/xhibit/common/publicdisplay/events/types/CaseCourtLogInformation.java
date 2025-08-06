package uk.gov.courtservice.xhibit.common.publicdisplay.events.types;

import uk.gov.hmcts.pdda.courtlog.vos.CourtLogSubscriptionValue;

/**

 * Title: Case Court Log Information.

 * Description: An object that holds information about a court log change for a case

 * Copyright: Copyright (c) 2003

 * Company: EDS

 * @author Rakesh Lakhani
 * @version $Id: CaseCourtLogInformation.java,v 1.3 2004/04/08 15:41:16 pznwc5 Exp $
 */
public class CaseCourtLogInformation extends CaseChangeInformation {

    static final long serialVersionUID = -8381188256896691570L;

    private CourtLogSubscriptionValue clSubscriptionValue;

    /**
     * Create using a court log subscription value.

     * @param courtLogSubsValue The court log event that has been added, changed or removed
     * @param caseActive Whether the case is active.
     */
    public CaseCourtLogInformation(CourtLogSubscriptionValue courtLogSubsValue,
        boolean caseActive) {
        super(caseActive);
        setCourtLogSubscriptionValue(courtLogSubsValue);
    }

    /**
     * Set a new court log subscription value.

     * @param clSubscriptionValue The court log event that has been added, changed or removed
     */
    public final void setCourtLogSubscriptionValue(CourtLogSubscriptionValue clSubscriptionValue) {
        this.clSubscriptionValue = clSubscriptionValue;
    }

    /**
     * Get the court log subscription value.

     * @return CourtLogSubscriptionValue
     */
    public CourtLogSubscriptionValue getCourtLogSubscriptionValue() {
        return clSubscriptionValue;
    }
}
