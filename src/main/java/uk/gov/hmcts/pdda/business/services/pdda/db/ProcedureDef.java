package uk.gov.hmcts.pdda.business.services.pdda.db;

import org.springframework.jdbc.core.SqlParameter;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public enum ProcedureDef {
    
    CLEAR_AUDIT_TABLES(
        "clear_audit_tables",
        List.of(
            new SqlParameter(ProcParamNames.ENV_IN, java.sql.Types.VARCHAR),
            new SqlParameter(ProcParamNames.LIMIT_IN, java.sql.Types.INTEGER)
        )
    ),
    
    CLEAR_OBSOLETE_MESSAGES(
        "clear_obsolete_messages",
        List.of(
            new SqlParameter(ProcParamNames.ERRORTEXT_IN, java.sql.Types.VARCHAR),
            new SqlParameter(ProcParamNames.LIMIT_IN, java.sql.Types.INTEGER)
        )
    ),
        
    CLEAR_OLD_RECORDS(
        "clear_old_records",
        List.of(
            new SqlParameter(ProcParamNames.DAYS_IN, java.sql.Types.INTEGER),
            new SqlParameter(ProcParamNames.LIMIT_IN, java.sql.Types.INTEGER)
        )
    ),
        
    NULLIFY_LIVE_DISPLAY_FIELDS(
        "nullify_live_display_fields",
        List.of()
    );

    private final String dbName;
    private final List<SqlParameter> inParams;

    ProcedureDef(String dbName, List<SqlParameter> inParams) {
        this.dbName = dbName;
        this.inParams = inParams;
    }

    public String getDbName() {
        return dbName;
    }

    public List<SqlParameter> getInParams() {
        return inParams;
    }

    public static Optional<ProcedureDef> resolve(String procName) {
        String procNameTrimmed = procName.trim().toUpperCase(Locale.ROOT);
        if ("CLEARAUDITTABLES".equals(procNameTrimmed)
                || "CLEAR_AUDIT_TABLES".equals(procNameTrimmed)) {
            return Optional.of(CLEAR_AUDIT_TABLES);
        } else if ("CLEAR_OBSOLETE_MESSAGES".equals(procNameTrimmed)
                || "CLEAROBSOLETEMESSAGES".equals(procNameTrimmed)) {
            return Optional.of(CLEAR_OBSOLETE_MESSAGES);
        } else if ("CLEAROLDRECORDS".equals(procNameTrimmed) 
                || "CLEAR_OLD_RECORDS".equals(procNameTrimmed)) {
            return Optional.of(CLEAR_OLD_RECORDS);
        } else if ("NULLIFYLIVEDISPLAYFIELDS".equals(procNameTrimmed) 
                || "NULLIFY_LIVE_DISPLAY_FIELDS".equals(procNameTrimmed)) {
            return Optional.of(NULLIFY_LIVE_DISPLAY_FIELDS);
        }
        return Optional.empty();
    }
    
}

