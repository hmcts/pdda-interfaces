package uk.gov.hmcts.framework.jdbc.exception;

import uk.gov.hmcts.framework.exception.CsUnrecoverableException;

/**
 * <p>
 * Title: Data Access Exception.
 * </p>
 * <p>
 * Description: This is an unchecked exception used by the JDBC framework
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: Electronic Data Systems
 * </p>
 * 
 * @author Meeraj Kunnumpurath
 * @version 1.0
 */

public class DataAccessException extends CsUnrecoverableException {
    private static final long serialVersionUID = 1L;

    private static final String TOSTRING_COLUMN = "column: ";
    private static final String TOSTRING_EOL = "\r\n";
    // Column
    private String column;

    /**
     * Creates an instance with the message.
     * 
     * @param msg String
     */
    public DataAccessException(String msg) {
        super(msg);
        setColumn(null);
    }

    /**
     * Creates an instance with the message and root cause.
     * 
     * @param msg String
     * @param cause Root cause
     */
    public DataAccessException(String msg, Throwable cause) {
        super(msg, cause);
        setColumn(null);
    }

    /**
     * Creates an instance with the message.
     * 
     * @param msg String
     * @param column String
     */
    public DataAccessException(String msg, String column) {
        super(msg);
        this.column = column;
    }

    /**
     * Creates an instance with the message and root cause.
     * 
     * @param msg String
     * @param cause Root cause
     */
    public DataAccessException(String msg, Throwable cause, String column) {
        super(msg, cause);
        this.column = column;
    }

    /**
     * Gets the column.
     * 
     * @return String
     */
    public String getColumn() {
        return column;
    }

    /**
     * Returns the message.
     */
    @Override
    public String getMessage() {

        StringBuilder sb = new StringBuilder();
        if (column != null) {
            sb.append(TOSTRING_COLUMN).append(column).append(TOSTRING_EOL);
        }
        sb.append(super.getMessage());

        return sb.toString();

    }

    private void setColumn(String column) {
        this.column = column;
    }
}
