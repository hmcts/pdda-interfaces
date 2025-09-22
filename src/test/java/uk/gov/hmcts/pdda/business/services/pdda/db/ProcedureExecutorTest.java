package uk.gov.hmcts.pdda.business.services.pdda.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.stubbing.Answer;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.gov.hmcts.pdda.business.services.pdda.dbscheduler.JobToRun;

import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@SuppressWarnings("PMD")
class ProcedureExecutorTest {

    private JdbcTemplate jdbc;
    private ProcedureExecutor executor;

    // JDBC mocks used inside jdbc.execute callbacks
    private Connection conn;
    private Statement stmt;
    private CallableStatement cstmt;

    @BeforeEach
    void setup() throws Exception {
        jdbc = mock(JdbcTemplate.class);
        executor = new ProcedureExecutor(jdbc);

        conn = mock(Connection.class);
        stmt = mock(Statement.class);
        cstmt = mock(CallableStatement.class);

        when(conn.createStatement()).thenReturn(stmt);
        when(conn.prepareCall(any(String.class))).thenReturn(cstmt);
        when(cstmt.executeUpdate()).thenReturn(0);

        // Stubs jdbc.execute(ConnectionCallback<T>) to invoke the callback with our mock Connection.
        stubJdbcExecuteReturnCallbackResult();
    }

    /**
     * Stub JdbcTemplate.execute(ConnectionCallback) to call the callback with the mock Connection
     * and return its result. We locally suppress the unchecked warning so builds with -Werror pass.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void stubJdbcExecuteReturnCallbackResult() {
        when(jdbc.execute((ConnectionCallback) any(ConnectionCallback.class)))
            .thenAnswer((Answer) inv -> {
                ConnectionCallback cb = inv.getArgument(0);
                return cb.doInConnection(conn);
            });
    }

    @Test
    void execute_unknownProcedure_throws() {
        JobToRun job = new JobToRun("not_a_known_proc", null);
        job.setProcedureParams(new String[] {});
        assertThrows(IllegalArgumentException.class, () -> executor.execute(job));
    }

    @Test
    void execute_paramCountMismatch_returnsFalse_andSetsFailed() {
        JobToRun job = new JobToRun("clear_audit_tables", null);
        // clear_audit_tables expects 2 params; provide only 1
        job.setProcedureParams(new String[] { "ENV_ONLY" });

        boolean ok = executor.execute(job);

        assertFalse(ok);
        assertEquals("FAILED", job.getJobStatus());
        // Should return before hitting JDBC
        verifyNoInteractions(conn);
    }

    @Test
    void execute_success_honorsSchemaBuildsSql_setsTimeout_andCallsProcedure() throws Exception {
        JobToRun job = new JobToRun("clear_audit_tables", null);
        job.setSchema("my_schema");
        job.setTimeout(Duration.ofSeconds(5));
        job.setProcedureParams(new String[] {"NON_PROD", "5000"}); // VARCHAR, INTEGER

        ArgumentCaptor<String> callSqlCaptor = ArgumentCaptor.forClass(String.class);
        when(conn.prepareCall(callSqlCaptor.capture())).thenReturn(cstmt);

        boolean ok = executor.execute(job);

        assertTrue(ok);
        assertEquals("SUCCESS", job.getJobStatus());

        // JDBC call-escape syntax
        assertEquals("{ call my_schema.clear_audit_tables(?,?) }", callSqlCaptor.getValue());

        // Timeout now applied on the CallableStatement
        verify(cstmt).setQueryTimeout(5);

        // Coercion & SQL types unchanged
        verify(cstmt).setObject(eq(1), eq("NON_PROD"), eq(Types.VARCHAR));
        verify(cstmt).setObject(eq(2), eq(5000), eq(Types.INTEGER));
        verify(cstmt).executeUpdate();
    }


    @Test
    void execute_success_withNullSchema_usesDefaultHousekeepingSchema() throws Exception {
        JobToRun job = new JobToRun("nullify_live_display_fields", null);
        job.setSchema(null);
        job.setProcedureParams(new String[] {});

        ArgumentCaptor<String> callSqlCaptor = ArgumentCaptor.forClass(String.class);
        when(conn.prepareCall(callSqlCaptor.capture())).thenReturn(cstmt);

        boolean ok = executor.execute(job);

        assertTrue(ok);
        assertEquals("SUCCESS", job.getJobStatus());
        assertEquals("{ call pdda_housekeeping_pkg.nullify_live_display_fields() }", callSqlCaptor.getValue());
    }


    @Test
    void execute_setsZeroTimeout_whenNegativeProvided() throws Exception {
        JobToRun job = new JobToRun("nullify_live_display_fields", null);
        job.setTimeout(Duration.ofMillis(-123)); // becomes 0
        job.setProcedureParams(new String[] {});

        when(conn.prepareCall(any(String.class))).thenReturn(cstmt);

        boolean ok = executor.execute(job);

        assertTrue(ok);
        verify(cstmt).setQueryTimeout(0);
    }


    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    void execute_catchesDataAccessException_setsFailed_andReturnsFalse() {
        when(jdbc.execute((ConnectionCallback) any(ConnectionCallback.class)))
            .thenThrow(new DataAccessResourceFailureException("boom"));

        JobToRun job = new JobToRun("clear_old_records", null);
        job.setProcedureParams(new String[] {"30", "100"});

        boolean ok = executor.execute(job);

        assertFalse(ok);
        assertEquals("FAILED", job.getJobStatus());
    }


    // ---- Focused unit tests for the private coerce(...) helper via reflection ----

    @Test
    void coerce_variousSqlTypes() throws Exception {
        Method coerce = ProcedureExecutor.class.getDeclaredMethod("coerce", String.class, int.class);
        coerce.setAccessible(true);

        assertEquals("abc", coerce.invoke(null, " abc ", Types.VARCHAR));
        assertEquals(123, coerce.invoke(null, "123", Types.INTEGER));
        assertEquals(123L, coerce.invoke(null, "123", Types.BIGINT));
        assertEquals(new java.math.BigDecimal("12.34"), coerce.invoke(null, "12.34", Types.DECIMAL));
        assertEquals(Boolean.TRUE, coerce.invoke(null, "true", Types.BOOLEAN));
        assertEquals(Date.valueOf(java.time.LocalDate.parse("2025-01-02")),
                coerce.invoke(null, "2025-01-02", Types.DATE));

        Timestamp ts = (Timestamp) coerce.invoke(null, "2025-01-02T03:04:05Z", Types.TIMESTAMP_WITH_TIMEZONE);
        assertNotNull(ts);

        // empty and null -> null
        assertNull(coerce.invoke(null, null, Types.VARCHAR));
        assertNull(coerce.invoke(null, "   ", Types.VARCHAR));

        // OTHER -> PGobject jsonb
        Object other = coerce.invoke(null, "{\"k\":1}", Types.OTHER);
        assertEquals("org.postgresql.util.PGobject", other.getClass().getName());
        // Try to check type/value reflectively (works on common PG versions)
        try {
            var getType = other.getClass().getMethod("getType");
            var getValue = other.getClass().getMethod("getValue");
            assertEquals("jsonb", getType.invoke(other));
            assertEquals("{\"k\":1}", getValue.invoke(other));
        } catch (NoSuchMethodException ignored) {
            // On some PG versions, methods differ; class check above is sufficient
        }
    }
}
