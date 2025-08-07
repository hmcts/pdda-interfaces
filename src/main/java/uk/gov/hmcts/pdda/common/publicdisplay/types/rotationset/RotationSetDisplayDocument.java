package uk.gov.hmcts.pdda.common.publicdisplay.types.rotationset;

import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.DisplayDocumentUri;

import java.io.Serializable;

/**
 * <p>
 * Title: A Display Document as part of a Rotation Set.
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * When a Display Document 'becomes' part of a rotation set it has a page delay associated with it.
 * This page delay is how long that the page(s) of the Display Document are (each) displayed on the
 * browser when it is part of the Rotation Set.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * 
 * <p>
 * Company: EDS
 * </p>
 * 
 * @author Bob Boothby
 * @version 1.0
 */
public class RotationSetDisplayDocument implements Serializable {

    static final long serialVersionUID = 4314382381407407835L;

    private static final String EOL = "\n";
    
    private final DisplayDocumentUri displayDocumentUri;

    private final long pageDelay;

    /**
     * Construct an instance of this class for inclusion in a <code>DisplayRotationSetData</code>
     * instance.
     * 
     * @param displayDocumentUri The display document.
     * @param pageDelay The time that a page of the document is displayed.
     */
    public RotationSetDisplayDocument(final DisplayDocumentUri displayDocumentUri, final long pageDelay) {
        this.displayDocumentUri = displayDocumentUri;
        this.pageDelay = pageDelay;
    }

    /**
     * Get details of the Display Document.
     * 
     * @return An instance of DisplayDocumentURI with details of the Display Document as part of the
     *         overall Display Rotation Set.
     */
    public DisplayDocumentUri getDisplayDocumentUri() {
        return displayDocumentUri;
    }

    /**
     * Get how long each page of the document as rendered should be shown.
     * 
     * @return the time in ?seconds?
     */
    public long getPageDelay() {
        return pageDelay;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * 
     * @param obj the reference object with which to compare.
     * 
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RotationSetDisplayDocument) {
            RotationSetDisplayDocument testable = (RotationSetDisplayDocument) obj;
            return this.pageDelay == testable.pageDelay && this.displayDocumentUri.equals(testable.displayDocumentUri);
        }

        return false;
    }

    /**
     * Get the hashcode for this object.
     * 
     * @return This object's hashcode.
     * 
     * @see java.lang.Object.hashCode
     */
    @Override
    public int hashCode() {
        return (int) (pageDelay + displayDocumentUri.hashCode());
    }

    /**
     * Get an human readable string representation of the rotation set display document.
     * 
     * @return String
     */
    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        appendToBuffer(buffer, "");
        return buffer.toString();
    }

    /**
     * For the purposes of more efficient debugging, appends the string representation to the supplied
     * buffer.
     * 
     * @param buffer The buffer to append to.
     * @param lineIndent The indent to prepend to the string representation.
     */
    public void appendToBuffer(StringBuilder buffer, String lineIndent) {
        buffer.append(lineIndent).append("Page Delay: ").append(pageDelay).append(" URI: ")
            .append(displayDocumentUri).append(EOL);

    }
}
