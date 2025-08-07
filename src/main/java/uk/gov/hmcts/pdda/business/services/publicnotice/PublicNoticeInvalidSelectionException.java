package uk.gov.hmcts.pdda.business.services.publicnotice;

/**
 * <p>
 * Title:PublicNoticeInvalidSelectionException, thrown when the public notices selected as Active
 * break the Validation rules.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: EDS
 * </p>
 * 
 * @authors Pat Fox
 * @version 1.0
 */

public class PublicNoticeInvalidSelectionException extends PublicNoticeException {
    static final long serialVersionUID = 1985744394271724507L;
    private static final String MAX_PN_EXCEEDED = "publicnotice.selection.maxexceeded";

    /**
     * PublicNoticeInvalidSelectionException.
     * 
     * @param maxAllowed Integer
     * @param logMessage error message for log
     */
    public PublicNoticeInvalidSelectionException(Integer maxAllowed, String logMessage) {
        super(MAX_PN_EXCEEDED, new Integer[] {maxAllowed}, logMessage);
    }
}
