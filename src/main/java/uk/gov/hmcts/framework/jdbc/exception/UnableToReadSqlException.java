package uk.gov.hmcts.framework.jdbc.exception;

/**

 * Title: Unable to read SQL exception.


 * Description: This is an unchecked exception thrown when an IOEXception occurs trying to read the
 * SQL


 * Copyright: Copyright (c) 2003


 * Company: Electronic Data Systems

 * @author Meeraj Kunnumpurath
 * @version 1.0
 */
public class UnableToReadSqlException extends DataAccessException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an instance with the message.

     * @param msg String
     */
    public UnableToReadSqlException(String msg) {
        super(msg);
    }

    /**
     * Creates an instance with the message and root cause.

     * @param msg String
     * @param cause Root cause
     */
    public UnableToReadSqlException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
