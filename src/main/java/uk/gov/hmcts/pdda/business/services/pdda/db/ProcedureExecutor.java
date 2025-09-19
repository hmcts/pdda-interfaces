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
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("PMD")
public class ProcedureExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(ProcedureExecutor.class);
    private final JdbcTemplate jdbc;

    public ProcedureExecutor(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean execute(JobToRun job) {
        var def = ProcedureDef.resolve(job.getProcedureName())
            .orElseThrow(() -> new IllegalArgumentException("Unknown procedure: " + job.getProcedureName()));

        String schema = job.getSchema() == null ? "pdda_housekeeping_pkg" : job.getSchema();
        var declared = def.getInParams();
        String[] args = job.getProcedureParams();
        int expected = declared.size();
        int provided = args == null ? 0 : args.length;
        if (provided != expected) {
            LOG.error("Param count mismatch for {}: expected={}, provided={}", def.getDbName(), expected, provided);
            job.setJobStatus("FAILED");
            return false;
        }

        // Transaction-scoped timeout
        applyStatementTimeout(jdbc, job.getTimeout() == null ? java.time.Duration.ofSeconds(30) : job.getTimeout());

        // Build: CALL schema.proc(?, ?, ...)
        String placeholders = String.join(",", java.util.Collections.nCopies(expected, "?"));
        String sql = "CALL " + schema + "." + def.getDbName() + "(" + placeholders + ")";

        long start = System.nanoTime();
        try {
            int updated = jdbc.execute((org.springframework.jdbc.core.ConnectionCallback<Integer>) con -> {
                try (java.sql.CallableStatement cs = con.prepareCall(sql)) {
                    for (int i = 0; i < expected; i++) {
                        int sqlType = declared.get(i).getSqlType();
                        Object v = coerce(args[i], sqlType);
                        if (v == null) {
                            cs.setNull(i + 1, sqlType);
                        } else {
                            cs.setObject(i + 1, v, sqlType);
                        }
                    }
                    return cs.executeUpdate();
                }
            });

            LOG.info("CALL {}.{} ok ({}), rows={}", schema, def.getDbName(),
                     (System.nanoTime() - start) / 1_000_000, updated);
            job.setJobStatus("SUCCESS");
            return true;

        } catch (org.springframework.dao.DataAccessException ex) {
            LOG.error("CALL {}.{} failed params={}", schema, def.getDbName(), java.util.Arrays.toString(args), ex);
            job.setJobStatus("FAILED");
            return false;
        }
    }

    
    private static void applyStatementTimeout(JdbcTemplate jdbc, Duration timeout) {
        int ms = Math.toIntExact(timeout.toMillis());
        // Guard against negatives; 0 means “no timeout”
        if (ms < 0) {
            ms = 0;
        }

        // Must be executed within an open transaction for SET LOCAL to stick
        final int literal = ms;
        jdbc.execute((ConnectionCallback<Void>) con -> {
            try (java.sql.Statement st = con.createStatement()) {
                st.execute("SET LOCAL statement_timeout = " + literal);
            }
            return null;
        });
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
            case Types.VARCHAR:
            case Types.CHAR:
            case Types.LONGVARCHAR:
                return rawTrimmed;

            case Types.INTEGER:
            case Types.SMALLINT:
            case Types.TINYINT:
                return Integer.valueOf(rawTrimmed);

            case Types.BIGINT:
                return Long.valueOf(rawTrimmed);

            case Types.NUMERIC:
            case Types.DECIMAL:
                return new java.math.BigDecimal(rawTrimmed);

            case Types.BOOLEAN:
            case Types.BIT:
                return Boolean.valueOf(rawTrimmed);

            case Types.DATE:
                // Accept ISO-8601 (yyyy-MM-dd)
                return java.sql.Date.valueOf(java.time.LocalDate.parse(rawTrimmed));

            case Types.TIMESTAMP:
            case Types.TIMESTAMP_WITH_TIMEZONE: // PG maps timestamptz to this
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

    @SuppressWarnings("PMD")
    private static Map<String, Object> safeParams(Map<String, Object> in) {
        // redact obvious secrets by key name; tweak as needed
        return in.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
            e -> e.getKey().toLowerCase().contains("password") ? "***" : e.getValue()));
    }
}

