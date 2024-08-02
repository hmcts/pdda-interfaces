-- Drop foreign key constraints to prevent violations during data load
\i 'pdda-drop-fk-constraints.sql'

-- Handle 'upsert' of each table.  Each script handles loading of data into staging tables,
-- disabling triggers and merge of data from staging tables into the main tables before
-- re-enabling the triggers.
\cd temp
\i 'upsert-xhb_config_prop.sql'
\i 'upsert-xhb_court.sql'
\i 'upsert-xhb_court_room.sql'
\i 'upsert-xhb_court_satellite.sql'
\i 'upsert-xhb_court_site.sql'
\i 'upsert-xhb_ref_court.sql'
\i 'upsert-xhb_ref_court_reporter.sql'
\i 'upsert-xhb_ref_court_reporter_firm.sql'
\i 'upsert-xhb_ref_cracked_effective.sql'
\i 'upsert-xhb_ref_hearing_type.sql'
\i 'upsert-xhb_ref_judge.sql'
\i 'upsert-xhb_ref_justice.sql'
\i 'upsert-xhb_ref_listing_data.sql'
\i 'upsert-xhb_ref_pdda_message_type.sql'
\i 'upsert-xhb_ref_system_code.sql'
\i 'upsert-xhb_configured_public_notice.sql'
\i 'upsert-xhb_court_log_event_desc.sql'
\cd ..

-- Re-instate foreign key constraints
\i 'pdda-create-fk-constraints.sql'

-- Analyze to refresh the database statistics
ANALYZE;
