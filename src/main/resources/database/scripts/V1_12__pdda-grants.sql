SET client_encoding TO 'UTF8';

-- Grants needed so that jobs registered in pg_cron in the postgres schema can reference these procedures
-- pg_cron jobs must be hosted in the postgres schema
GRANT EXECUTE ON PROCEDURE pdda_housekeeping_pkg.clear_obsolete_messages(character varying, integer) TO pgadmin;
GRANT EXECUTE ON PROCEDURE pdda_housekeeping_pkg.clear_audit_tables(character varying, integer) TO pgadmin;
GRANT EXECUTE ON PROCEDURE pdda_housekeeping_pkg.clear_old_records(integer, integer) TO pgadmin;
GRANT EXECUTE ON PROCEDURE pdda_housekeeping_pkg.nullify_live_display_fields() TO pgadmin;
