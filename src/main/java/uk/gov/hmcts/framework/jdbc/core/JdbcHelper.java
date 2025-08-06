package uk.gov.hmcts.framework.jdbc.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.framework.jdbc.exception.DataAccessException;
import uk.gov.hmcts.framework.jdbc.exception.ExceptionTranslator;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import javax.sql.DataSource;

/**

 * Title: JdbcHelper.


 * Description: This class uses inversion of control to hide JDBC API. This class has some Oracle
 * specific features in the way callable statements are executed


 * Copyright: Copyright (c) 2003


 * Company: Electronic Data Systems

 * @author Meeraj Kunnumpurath
 * @version $Id: JdbcHelper.java,v 1.13 2013/11/15 16:19:53 hingstb Exp $
 */
@SuppressWarnings("PMD.CheckResultSet")
public class JdbcHelper {
    // Logger
    private static final Logger LOG = LoggerFactory.getLogger(JdbcHelper.class);

    private static final String CONNECTION_CREATED = "Connection created";
    private static final String STATEMENT_CREATED = "Statement created";

    // Rowcount
    private int maxRowCount = -1;

    // Row processor for SQL queries
    private RowProcessor rowProcessor;

    // Datasource used for getting the connection
    private final DataSource ds;

    // SQL that is executed
    private final String sql;

    /**
     * Initializes the datasource and SQL.

     * @param newDs DataSource
     * @param newSql String
     */
    public JdbcHelper(DataSource newDs, String newSql) {
        if (newDs == null) {
            throw new IllegalArgumentException("constructor::newDs cannot be null");
        }

        if (newSql == null) {
            throw new IllegalArgumentException("constructor::sql cannot be null");
        }

        ds = newDs;
        sql = newSql;
    }

    /**
     * Set the maximum number of rows that should be processed.

     * @param maxRowCount int
     */
    public void setMaxRowCount(final int maxRowCount) {
        this.maxRowCount = maxRowCount;
    }

    /**
     * Sets the row processor for SQL queries.

     * @param newRowProcessor RowProcessor
     */
    public void setRowProcessor(RowProcessor newRowProcessor) {
        rowProcessor = newRowProcessor;
    }

    /**
     * Executes a SQL using prepared statement.

     * @param params ParameterArray
     * @return Number of records returned
     * @throws DataAccessException Exception
     */
    public int execute(Parameter... params) {
        try (
            // Get the connection
            Connection con = ds.getConnection()) {
            LOG.debug(CONNECTION_CREATED);

            try (// Prepare the statement
                PreparedStatement ps = DefaultStatementCreator.getInstance()
                    .createPreparedStatement(con, sql, params)) {
                LOG.debug(STATEMENT_CREATED);
    
                // Execute the query
                boolean hasResults = ps.execute();
    
                // Is this a query
                if (hasResults) {
                    try (ResultSet rs = ps.getResultSet()) {
                        // altered to call the common procedure
                        return processResultSet(rs);
                    }
                }
    
                return ps.getUpdateCount();
            }
        } catch (SQLException ex) {
            throw ExceptionTranslator.getInstance().translate(ex);
        }
    }

    /**
     * Executes a SQL update using prepared statement.

     * @param params ParameterArray
     * @throws DataAccessException Exception
     */
    public void executeUpdate(Parameter... params) {
        try (
            // Get the connection
            Connection con = ds.getConnection()) {
            LOG.debug(CONNECTION_CREATED);

            // Prepare the statement
            try (PreparedStatement ps = DefaultStatementCreator.getInstance()
                    .createPreparedStatement(con, sql, params)) {
                LOG.debug(STATEMENT_CREATED);
    
                // Execute the query and commit
                ps.execute();
            }

        } catch (SQLException ex) {
            throw ExceptionTranslator.getInstance().translate(ex);
        }
    }

    /**
     * Executes a stored procedure that return resultsets.

     * @param params ParameterArray
     * @return int The number of rows retrieved.
     * @throws DataAccessException Exception
     */
    public int executeStoredProcedure(Parameter... params) {
        try (
            // Get the connection
            Connection con = ds.getConnection()) {
            LOG.debug(CONNECTION_CREATED);

            // Prepare the statement
            try (CallableStatement cs = DefaultStatementCreator.getInstance()
                .createCallableStatement(con, sql, params)) {
                LOG.debug(STATEMENT_CREATED);
    
                // This is Oracle specific
                cs.execute();
                try (ResultSet rs = (ResultSet) cs.getObject(1)) {
    
                    // altered to call the common procedure
                    return processResultSet(rs);
                }
            }
        } catch (SQLException ex) {
            throw ExceptionTranslator.getInstance().translate(ex);
        }
    }

    /**
     * Executes a stored function that return any non-database related object.

     * @param params ParameterArray
     * @return Object That must be cast externally to the required type.
     * @throws DataAccessException Exception
     */
    public Object executeStoredFunction(Parameter... params) {
        try (
            // Get the connection
            Connection con = ds.getConnection()) {
            LOG.debug(CONNECTION_CREATED);

            try (// Prepare the statement
                CallableStatement cs = DefaultStatementCreator.getInstance()
                    .createCallableStatement(con, sql, params)) {
                LOG.debug(STATEMENT_CREATED);
    
                cs.execute();
    
                // only return the first parameter if it is an out parameter...
                if (params.length > 0 && params[0].isOut()) {
                    return cs.getObject(1);
                }
    
                return null;
            }
        } catch (SQLException ex) {
            throw ExceptionTranslator.getInstance().translate(ex);
        }
    }

    /**
     * A private common target for processing a <code>ResultSet</code>.

     * @param rset A <code>ResultSet</code> that processing is to be done against
     * @return An <code>int</code> value representing the number of rows processed
     * @throws A <code>SQLException</code> if any database related exception occurs
     */
    private int processResultSet(final ResultSet rset) throws SQLException {
        final PddaRow row = new PddaRow(rset);
        // store the limit here as boolean to prevent unnecessary int checks
        final boolean limitCount = this.maxRowCount != -1;

        LOG.debug("Query executed");

        int processedRows = 0;
        for (; rset.next(); processedRows++) {
            if (limitCount && processedRows >= this.maxRowCount) {
                break;
            }

            processRow(rowProcessor, row);
        }

        return processedRows;
    }

    /**
     * Processes a row.

     * @param processor RowProcessor
     * @param row Row
     */
    private void processRow(RowProcessor processor, PddaRow row) {
        if (!processor.getChildProcessors().isEmpty()) {
            // Ask all the children to process
            Iterator<?> it = processor.getChildProcessors().iterator();
            while (it.hasNext()) {
                processRow((RowProcessor) it.next(), row);
            }
        }

        // Have processed all descendants
        processor.processRow(row);
    }

    /**
     * A true helper method used to close the passed in <code>ResultSet</code> if it is not
     * <i>null</i>. If any errors occur whilst closing they will simply be logged and processing
     * allowed to continue.

     * @param rset The <code>ResultSet</code> to close.
     */
    public static final void closeResultSet(final ResultSet rset) {
        if (rset != null) {
            try {
                rset.close();
            } catch (final SQLException t) {
                LOG.error("Error closing ResultSet", t);
            }
        }
    }

    /**
     * A true helper method used to close the passed in <code>Statement</code> if it is not
     * <i>null</i>. If any errors occur whilst closing they will simply be logged and processing
     * allowed to continue.

     * @param stmt The <code>Statement</code> to close.
     */
    public static final void closeStatement(final Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (final SQLException t) {
                LOG.error("Error closing Statement", t);
            }
        }
    }

    /**
     * A true helper method used to close the passed in <code>Connection</code> if it is not
     * <i>null</i>. If any errors occur whilst closing they will simply be logged and processing
     * allowed to continue.

     * @param con The <code>Connection</code> to close.
     */
    public static final void closeConnection(final Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (final SQLException t) {
                LOG.error("Error closing Connection", t);
            }
        }
    }
}
