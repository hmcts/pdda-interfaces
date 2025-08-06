package uk.gov.hmcts.pdda.business.services.publicnotice;

/**

 * Title:PublicNoticeInvalidSelectionException, thrown when the public notices selected as Active
 * break the Validation rules.


 * Description:


 * Copyright: Copyright (c) 2002


 * Company: EDS

 * @authors Pat Fox
 * @version 1.0
 */

public class PublicNoticeInvalidSelectionException extends PublicNoticeException {
    static final long serialVersionUID = 1985744394271724507L;
    private static final String MAX_PN_EXCEEDED = "publicnotice.selection.maxexceeded";

    /**
     * PublicNoticeInvalidSelectionException.

     * @param maxAllowed Integer
     * @param logMessage error message for log
     */
    public PublicNoticeInvalidSelectionException(Integer maxAllowed, String logMessage) {
        super(MAX_PN_EXCEEDED, new Integer[] {maxAllowed}, logMessage);
    }
}
