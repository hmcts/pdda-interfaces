package uk.gov.hmcts.framework.jdbc.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.framework.services.CsServices;

import java.io.IOException;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialClob;



/**

 * Title: DefaultStatementCreator.


 * Description: The default implementation for the statement creator


 * Copyright: Copyright (c) 2003


 * Company: Electronic Data Systems

 * @author XHIBIT User
 * @version 1.0
 */

@SuppressWarnings("PMD.LawOfDemeter")
public class DefaultStatementCreator implements StatementCreator {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultStatementCreator.class);

    private static final String IS_CLOB_PROCESSING_ENABLED = "IS_CLOB_PROCESSING_ENABLED";

    private static final String IS_CLOB_PROCESSING_DEFAULT = "TRUE";

    private static final String IS_BLOB_PROCESSING_ENABLED = "IS_BLOB_PROCESSING_ENABLED";

    private static final String IS_BLOB_PROCESSING_DEFAULT = "FALSE";

    private static final String CHARACTER_ENCODING = "CHARACTER_ENCODING";

    private static final String DEFAULT_CHARACTER_ENCODING = "UTF-8";

    // Singleton
    private static DefaultStatementCreator me = new DefaultStatementCreator();

    /**
     * Returns a statement creator instance.

     * @return StatementCreator
     */
    public static StatementCreator getInstance() {
        return me;
    }

    /**
     * Creates a prepared statement.

     * @connection con
     * @param sql String
     * @param params ParameterArray
     * @return PreparedStatement
     * @throws SQLException Exception
     */
    @Override
    public PreparedStatement createPreparedStatement(Connection con, String sql, Parameter... params)
        throws SQLException {

        // Create the prepared statement
        PreparedStatement ps = con.prepareStatement(sql);
        // Set the parameters
        try {
            setParameters(ps, params);
        } catch (IOException io) {
            LOG.error("IOException during CLOB or BLOB processing for prepared statement" + io, io);
            throw new SQLException("IOException setting CLOB or BLOB", io);
        }
        // Set the query timeout
        ps.setQueryTimeout(0);

        return ps;

    }

    /**
     * Creates a callable statement.

     * @connection con
     * @param call String
     * @param params ParameterArray
     * @return CallableStatement
     * @throws SQLException Exception
     */
    @Override
    public CallableStatement createCallableStatement(Connection con, String call,
        Parameter... params) throws SQLException {

        // Create the prepared statement
        CallableStatement cs = con.prepareCall(call);
        // Set the in parameters
        try {
            setParameters(cs, params);
        } catch (IOException io) {
            LOG.error("IOException during CLOB or BLOB processing for callable statement" + io, io);
            throw new SQLException("IOException setting CLOB or BLOB", io);
        }
        // Set the query timeout
        cs.setQueryTimeout(0);

        // Return the callable statement
        return cs;

    }

    /**
     * Sets the in parameters.

     * Note: CLOBs and BLOBs have different processing as going via the normal mechanism leads to
     * database exceptions (ORA-01460) when inserting CLOBs or BLOBs of larger than 3k that contain
     * National Characters (eg �) due to an Oracle bug

     * This CLOB and BLOB processing can be enabled via configurable properties, however only the
     * CLOB processing is enabled by default (as this is required for XHIBIT 8.1 requirements)
     * whereas BLOB processing is not enabled by default although it can be if required

     * @param ps Prepared statement
     * @param params ParametersArray
     * @throws SQLException Exception
     */
    private void setParameters(PreparedStatement ps, Parameter... params)
        throws SQLException, IOException {

        // Set the parameters
        for (int i = 0; params != null && i < params.length; i++) {
            setParameter(ps, params, i);
        }

    }
    
    private void setParameter(PreparedStatement ps, Parameter[] params, int position)
        throws SQLException, IOException {
        // Set the IN params
        if (params[position].isIn() && params[position].getValue() != null) {
            addParameter(ps, params, position);
        } else if (params[position].isIn() && params[position].getValue() == null) {
            ps.setNull(position + 1, params[position].getSqlType());
            // Set the OUT param for callable statements
        } else if (params[position].isOut() && ps instanceof CallableStatement) {
            ((CallableStatement) ps).registerOutParameter(position + 1, params[position].getSqlType());
        } else {
            throw new IllegalArgumentException("Unsupported parameter type.");
        }  
    }
    
    private void addParameter(PreparedStatement ps, Parameter[] params, int position)
        throws SQLException, IOException {
        if (isClob(params[position])) {
            Clob clob = getClob((String) params[position].getValue());
            ps.setClob(position + 1, clob);
        } else if (isBlob(params[position])) {
            Blob blob = getBlob((String) params[position].getValue());
            ps.setBlob(position + 1, blob);
        } else {
            ps.setObject(position + 1, params[position].getValue());
        } 
    }

    private boolean isClob(Parameter parameter) {
        return parameter.getSqlType() == Types.CLOB && isClobProcessingEnabled();
    }

    private boolean isBlob(Parameter parameter) {
        return parameter.getSqlType() == Types.BLOB && isBlobProcessingEnabled();
    }

    /**
     * Builds the CLOB.

     * @param xmlData String
     * @return Clob
     * @throws SQLException Exception
     * @throws IOException Exception
     */
    private Clob getClob(String xmlData) throws SQLException {
        LOG.info("Start getCLOB");

        Clob tempClob = null;

        try {

            tempClob = new SerialClob(xmlData.toCharArray());
        } catch (SQLException sqle) {
            LOG.error("SQLException during CLOB processing for callable statement" + sqle, sqle);
            throw sqle;
        } catch (Exception e) {
            LOG.error("Unexpected Exception during CLOB processing for callable statement" + e, e);
            throw new SQLException("Unexpected Exception setting CLOB", e);
        } finally {
            if (tempClob != null) {
                tempClob.free();
            }
        }

        LOG.info("End getClob");

        return tempClob;
    }

    /**
     * Builds the BLOB.

     * @param xmlData String
     * @return Blob
     * @throws SQLException Exception
     * @throws IOException Exception
     */
    private Blob getBlob(String xmlData) throws SQLException, IOException {
        LOG.info("Start getBlob");

        Blob tempBlob = null;

        try {
            tempBlob = new SerialBlob(xmlData.getBytes(getCharacterEncoding()));

        } catch (SQLException sqle) {
            LOG.error("SQLException during CLOB processing for callable statement" + sqle, sqle);
            throw sqle;
        } catch (Exception e) {
            LOG.error("Unexpected Exception during BLOB processing for callable statement" + e, e);
            throw new SQLException("Unexpected Exception setting BLOB", e);
        } finally {
            if (tempBlob != null) {
                tempBlob.free();
            }
        }

        LOG.info("End getBlob");

        return tempBlob;
    }


    /**
     * Returns the encoding required. Defaults to UTF-8 which is used for ORACLE 9i.

     * @return String
     */
    private String getCharacterEncoding() {
        String characterEncoding = CsServices.getConfigServices().getProperty(CHARACTER_ENCODING,
            DEFAULT_CHARACTER_ENCODING);

        LOG.debug("<< characterEncoding: {} >>", characterEncoding);

        return characterEncoding;
    }

    /**
     * Return true if we require additional CLOB processing to occur to ensure National Characters
     * are processed and do not error (CLOBs larger than 3k that contain National Characters (eg �)
     * fail with an ORA-01460 error due to an Oracle bug).

     * Will be TRUE by default as this functionality is definitely required in XHIBIT Rel 8.1
     * onwards (due to Reqs 1492 and 1669 which have direct JDBC commits of CLOBs to an Oracle 9i
     * database)

     * @return boolean
     */
    private boolean isClobProcessingEnabled() {
        String clobProcessingEnabled = CsServices.getConfigServices()
            .getProperty(IS_CLOB_PROCESSING_ENABLED, IS_CLOB_PROCESSING_DEFAULT);

        LOG.debug("<< isClobProcessingEnabled: {} >>", clobProcessingEnabled);

        return clobProcessingEnabled != null && "TRUE".equalsIgnoreCase(clobProcessingEnabled);
    }

    /**
     * Return true if we require additional BLOB processing to occur to ensure National Characters
     * are processed and do not error.

     * Will be FALSE by default as this functionality is not definitely required in Rel 8.1 onwards
     * and so BLOBs will be processing like normal fields

     * If BLOBs are inserted in future requirements and they are < 3k and contain national
     * characters (eg �) then this property should be enabled and the BLOBs tested using this
     * functionality

     * @return boolean
     */
    private boolean isBlobProcessingEnabled() {
        String blobProcessingEnabled = CsServices.getConfigServices()
            .getProperty(IS_BLOB_PROCESSING_ENABLED, IS_BLOB_PROCESSING_DEFAULT);

        LOG.debug("<< isBlobProcessingEnabled: {} >>", blobProcessingEnabled);

        return blobProcessingEnabled != null && "TRUE".equalsIgnoreCase(blobProcessingEnabled);
    }
}
