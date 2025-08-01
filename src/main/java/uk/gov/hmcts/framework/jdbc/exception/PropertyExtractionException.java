package uk.gov.hmcts.framework.jdbc.exception;

/**

 * Title: Unable to extract property from resultset.


 * Description: This is an unchecked exception thrown when an reflection exception occurs trying to
 * map a column to a bean property


 * Copyright: Copyright (c) 2003


 * Company: Electronic Data Systems

 * @author Meeraj Kunnumpurath
 * @version 1.0
 */
public class PropertyExtractionException extends DataAccessException {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an instance with the message.

     * @param msg String
     */
    public PropertyExtractionException(String msg) {
        super(msg);
    }

    /**
     * Creates an instance with the message and root cause.

     * @param msg String
     * @param cause Root cause
     */
    public PropertyExtractionException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
