package uk.gov.hmcts.framework.jdbc.core;

/**
 * <p>
 * Title: Parameter.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: Electronic Data Systems
 * </p>
 * 
 * @author XHIBIT User
 * @version $Id: Parameter.java,v 1.4 2006/06/05 12:30:16 bzjrnl Exp $
 */
public final class Parameter {
    // Enumerated constant for IN type
    private static final int IN_PARAM = 1;

    // Enumerated constant for OUT type
    private static final int OUT_PARAM = 2;

    // Enumerated constant for IN_OUT type
    private static final int IN_OUT_PARAM = 3;

    // SQL type of the parameter
    private final int sqlType;

    // Whether the parameter is IN, OUT or IN OUT
    private final int parameterType;

    // Value of the parameter
    private final Object value;

    /**
     * Private constructor.
     * 
     * @param sqlType int
     * @param parameterType int
     * @param value Object
     */
    private Parameter(int sqlType, int parameterType, Object value) {
        this.sqlType = sqlType;
        this.parameterType = parameterType;
        this.value = value;
    }

    /**
     * Creates an IN parameter for the SQL type.
     * 
     * @param sqlType int
     * @param value Object
     * @return Parameter
     */
    public static Parameter getInParameter(int sqlType, Object value) {
        return new Parameter(sqlType, IN_PARAM, value);
    }

    /**
     * Creates an OUT parameter for the SQL type.
     * 
     * @param sqlType int
     * @return Parameter
     */
    public static Parameter getOutParameter(int sqlType) {
        return new Parameter(sqlType, OUT_PARAM, null);
    }

    /**
     * Creates an IN OUT parameter for the SQL type.
     * 
     * @param sqlType int
     * @param value Object
     * @return Parameter
     */
    public static Parameter getInOutParameter(int sqlType, Object value) {
        return new Parameter(sqlType, IN_OUT_PARAM, value);
    }

    /**
     * Returns the SQL type.
     * 
     * @return int
     */
    public int getSqlType() {
        return sqlType;
    }

    /**
     * Returns the value.
     * 
     * @return Object
     */
    public Object getValue() {
        return value;
    }

    /**
     * Checks whether a parameter is in type.
     * 
     * @return boolean
     */
    public boolean isIn() {
        return parameterType == IN_PARAM || parameterType == IN_OUT_PARAM;
    }

    /**
     * Checks whether a parameter is out type.
     * 
     * @return boolean
     */
    public boolean isOut() {
        return parameterType == OUT_PARAM || parameterType == IN_OUT_PARAM;
    }

    /**
     * Hibernate needs to be able to handle the Postgres db equivalent of "IS (NOT) DISTINCT TO" so
     * that Strings that are null or empty can be handled correctly.
     * 
     * @param inParam String
     * @return String
     */
    public static String getPostgresInParameter(String inParam) {
        if (inParam != null && inParam.length() > 0) {
            return inParam;
        } else {
            return null;
        }
    }
}
