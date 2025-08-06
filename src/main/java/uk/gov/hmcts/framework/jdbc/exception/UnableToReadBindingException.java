package uk.gov.hmcts.framework.jdbc.exception;

/**

 * Title: Unable to read binding exception.


 * Description: This is an unchecked exception thrown when an IOEXception occurs trying to read the
 * binding


 * Copyright: Copyright (c) 2003


 * Company: Electronic Data Systems

 * @author Meeraj Kunnumpurath
 * @version 1.0
 */
public class UnableToReadBindingException extends DataAccessException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an instance with the message.

     * @param msg String
     */
    public UnableToReadBindingException(String msg) {
        super(msg);
    }

    /**
     * Creates an instance with the message and root cause.

     * @param msg String
     * @param cause Throwable
     */
    public UnableToReadBindingException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
