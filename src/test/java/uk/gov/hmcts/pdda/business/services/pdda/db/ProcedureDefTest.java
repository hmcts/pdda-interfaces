package uk.gov.hmcts.pdda.business.services.pdda.db;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.SqlParameter;

import java.sql.Types;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("PMD")
class ProcedureDefTest {

    @Test
    void resolve_isCaseAndFormatTolerant() {
        assertEquals(Optional.of(ProcedureDef.CLEAR_AUDIT_TABLES),
                ProcedureDef.resolve("CLEAR_AUDIT_TABLES"));
        assertEquals(Optional.of(ProcedureDef.CLEAR_AUDIT_TABLES),
                ProcedureDef.resolve("clearaudittables"));

        assertEquals(Optional.of(ProcedureDef.CLEAR_OBSOLETE_MESSAGES),
                ProcedureDef.resolve("CLEAR_OBSOLETE_MESSAGES"));
        assertEquals(Optional.of(ProcedureDef.CLEAR_OBSOLETE_MESSAGES),
                ProcedureDef.resolve("clearobsoletemessages"));

        assertEquals(Optional.of(ProcedureDef.CLEAR_OLD_RECORDS),
                ProcedureDef.resolve("CLEAR_OLD_RECORDS"));
        assertEquals(Optional.of(ProcedureDef.CLEAR_OLD_RECORDS),
                ProcedureDef.resolve("clearoldrecords"));

        assertEquals(Optional.of(ProcedureDef.NULLIFY_LIVE_DISPLAY_FIELDS),
                ProcedureDef.resolve("NULLIFY_LIVE_DISPLAY_FIELDS"));
        assertEquals(Optional.of(ProcedureDef.NULLIFY_LIVE_DISPLAY_FIELDS),
                ProcedureDef.resolve("nullifylivedisplayfields"));

        assertTrue(ProcedureDef.resolve("does_not_exist").isEmpty());
    }

    @Test
    void getters_matchDefinitions() {
        assertEquals("clear_audit_tables", ProcedureDef.CLEAR_AUDIT_TABLES.getDbName());
        assertEquals(2, ProcedureDef.CLEAR_AUDIT_TABLES.getInParams().size());
        SqlParameter p0 = ProcedureDef.CLEAR_AUDIT_TABLES.getInParams().get(0);
        SqlParameter p1 = ProcedureDef.CLEAR_AUDIT_TABLES.getInParams().get(1);
        assertEquals(Types.VARCHAR, p0.getSqlType());
        assertEquals(Types.INTEGER, p1.getSqlType());

        assertEquals("nullify_live_display_fields", ProcedureDef.NULLIFY_LIVE_DISPLAY_FIELDS.getDbName());
        assertTrue(ProcedureDef.NULLIFY_LIVE_DISPLAY_FIELDS.getInParams().isEmpty());
    }
}
