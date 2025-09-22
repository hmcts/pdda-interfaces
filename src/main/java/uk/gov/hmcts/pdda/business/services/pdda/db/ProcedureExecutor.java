package uk.gov.hmcts.pdda.business.services.pdda.db;

import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.pdda.business.services.pdda.dbscheduler.JobToRun;

import java.sql.Types;
import java.time.Duration;

@Service
@SuppressWarnings("PMD")
public class ProcedureExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(ProcedureExecutor.class);
    private final JdbcTemplate jdbc;

    public ProcedureExecutor(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }
    
    private static final String DEFAULT_SCHEMA = "pdda_housekeeping_pkg";
    private static final java.util.regex.Pattern SCHEMA_NAME =
            java.util.regex.Pattern.compile("[a-z_][a-z0-9_]{0,62}");

    private static String safeSchema(String input) {
        String s = (input == null || input.isBlank()) ? DEFAULT_SCHEMA : input.trim();
        if (!SCHEMA_NAME.matcher(s).matches()) {
            throw new IllegalArgumentException("Invalid schema name");
        }
        return s;
    }


    /**
     * Execute the given jobs stored procedure with parameters.
     * This runs in a new transaction, separate from any caller.
     * @param job The job to execute, containing procedure name and parameters.
     * @return true if the procedure executed successfully, false otherwise.
     */
    @SuppressWarnings("java:S2077")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean execute(JobToRun job) {
        var def = ProcedureDef.resolve(job.getProcedureName())
            .orElseThrow(() -> new IllegalArgumentException("Unknown procedure: " + job.getProcedureName()));

        String schema = safeSchema(job.getSchema());
        var declared = def.getInParams();
        String[] args = job.getProcedureParams();
        int expected = declared.size();
        int provided = (args == null) ? 0 : args.length;
        if (provided != expected) {
            LOG.error("Param count mismatch for {}: expected={}, provided={}", def.getDbName(), expected, provided);
            job.setJobStatus("FAILED");
            return false;
        }

        String placeholders = String.join(",", java.util.Collections.nCopies(expected, "?"));
        String sql = "CALL " + schema + "." + def.getDbName() + "(" + placeholders + ")";

        long start = System.nanoTime();
        try {
            int updated = jdbc.execute((ConnectionCallback<Integer>) con -> {
                try (java.sql.CallableStatement cs = con.prepareCall(sql)) {
                    cs.setQueryTimeout(toSeconds(job.getTimeout() == null ? Duration.ofSeconds(30) : job.getTimeout()));
                    for (int i = 0; i < expected; i++) {
                        int sqlType = declared.get(i).getSqlType();
                        Object v = coerce(args[i], sqlType);
                        if (v == null) {
                            cs.setNull(i + 1, sqlType);
                        } else {
                            cs.setObject(i + 1, v, sqlType);
                        }
                    }
                    cs.execute();           // <-- call procedures with execute()
                    return 0;               // you can keep an int result if the caller logs rows
                }
            });

            LOG.info("CALL {}.{} ok ({} ms), rows={}", schema, def.getDbName(),
                     (System.nanoTime() - start) / 1_000_000, updated);
            job.setJobStatus("SUCCESS");
            return true;

        } catch (org.springframework.dao.DataAccessException ex) {
            LOG.error("CALL {}.{} failed params={}", schema, def.getDbName(), java.util.Arrays.toString(args), ex);
            job.setJobStatus("FAILED");
            return false;
        }
    }
    
    // seconds, not ms
    private static int toSeconds(Duration d) {
        long s = d.toSeconds();
        if (s < 0) {
            s = 0;
        }
        if (s > Integer.MAX_VALUE) {
            s = Integer.MAX_VALUE;
        }
        return (int) s;
    }

    
    // Convert a String to a value matching the declared JDBC type
    @SuppressWarnings("PMD")
    private static Object coerce(String raw, int sqlType) {
        if (raw == null) {
            return null;
        }
        String rawTrimmed = raw.trim();
        if (rawTrimmed.isEmpty()) {
            return null; // treat empty as NULL; adjust if you prefer empty strings
        }

        switch (sqlType) {
            case Types.VARCHAR, Types.CHAR, Types.LONGVARCHAR:
                return rawTrimmed;

            case Types.INTEGER, Types.SMALLINT, Types.TINYINT:
                return Integer.valueOf(rawTrimmed);

            case Types.BIGINT:
                return Long.valueOf(rawTrimmed);

            case Types.NUMERIC, Types.DECIMAL:
                return new java.math.BigDecimal(rawTrimmed);

            case Types.BOOLEAN, Types.BIT:
                return Boolean.valueOf(rawTrimmed);

            case Types.DATE:
                // Accept ISO-8601 (yyyy-MM-dd)
                return java.sql.Date.valueOf(java.time.LocalDate.parse(rawTrimmed));

            case Types.TIMESTAMP,Types.TIMESTAMP_WITH_TIMEZONE: // PG maps timestamptz to this
                // Accept ISO-8601; supports '2025-01-02T03:04:05' (optionally with zone)
                return java.sql.Timestamp.from(java.time.OffsetDateTime.parse(rawTrimmed).toInstant());

            case Types.OTHER:
                // Common for Postgres JSON/JSONB if you declare as OTHER
                // If you specifically want JSON, use PGobject.
                PGobject json = new PGobject();
                json.setType("jsonb"); // or "json"
                try {
                    json.setValue(rawTrimmed);
                } catch (java.sql.SQLException e) {
                    throw new IllegalArgumentException(e);
                }
                return json;

            default:
                // Fallback: pass string as-is
                return rawTrimmed;
        }
    }
}

