package uk.gov.hmcts.framework.jdbc.core;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * <p>
 * Title: StatementCreator.
 * </p>
 * <p>
 * Description: A factory for creating prepared and callable statements
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: Electronic Data Systems
 * </p>
 * 
 * @author XHIBIT User
 * @version 1.0
 */
public interface StatementCreator {

    /**
     * Creates a prepared statement.
     * 
     * @connection con
     * @param sql String
     * @param params ParameterArray
     * @return PreparedStatement
     * @throws SQLException Exception
     */
    PreparedStatement createPreparedStatement(Connection con, String sql, Parameter... params)
        throws SQLException;

    /**
     * Creates a callable statement.
     * 
     * @connection con
     * @param call String
     * @param params ParameterArray
     * @return CallableStatement
     * @throws SQLException Exception
     */
    CallableStatement createCallableStatement(Connection con, String call,
        Parameter... params) throws SQLException;
}
