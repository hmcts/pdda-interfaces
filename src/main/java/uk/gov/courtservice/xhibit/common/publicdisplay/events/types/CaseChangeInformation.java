package uk.gov.courtservice.xhibit.common.publicdisplay.events.types;

import java.io.Serializable;

/**

 * Title: Case Change Information.

 * Description: When an event occurs for a case this class is created to contain information as to
 * whether the case is active.

 * Copyright: Copyright (c) 2003

 * Company: EDS

 * @author Rakesh Lakhani
 * @version $Id: CaseChangeInformation.java,v 1.4 2006/06/05 12:28:23 bzjrnl Exp $
 */
public class CaseChangeInformation implements Serializable {

    static final long serialVersionUID = -1112482926244912473L;

    private boolean caseActive;

    /**
     * Set whether the case is active.

     * @param caseActive Whether the case is active
     */
    public CaseChangeInformation(boolean caseActive) {
        setCaseActive(caseActive);
    }

    /**
     * Set whether the case is active.

     * @param caseActive Whether the case is active
     */
    public final void setCaseActive(boolean caseActive) {
        this.caseActive = caseActive;
    }

    /**
     * Whether the case is active.

     * @return Whether the case is active.
     */
    public boolean isCaseActive() {
        return caseActive;
    }
    
    @Override
    public String toString() {
        return "CaseChangeInformation[caseActive=" + caseActive + "]";
    }
}
