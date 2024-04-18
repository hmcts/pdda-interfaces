\copy xhb_sched_hearing_attendee (sh_attendee_id, scheduled_hearing_id, ref_judge_id, attendee_type, last_update_date, creation_date, created_by, last_updated_by, version) FROM 'xhb_sched_hearing_attendee.csv'  DELIMITER ',' CSV HEADER


\i XHB_CASE_DATA_TABLE.sql
\i XHB_CLOB_DATA_TABLE.sql
\i XHB_FORMATTING_DATA_TABLE.sql
\i XHB_XML_DOCUMENT_DATA_TABLE.sql
\i XHB_CPP_LIST_DATA_TABLE.sql
\i XHB_CPP_STAGING_INBOUND_DATA_TABLE.sql
\i XHB_CPP_FORMATTING_DATA_TABLE.sql
\i XHB_REF_HEARING_TYPE_DATA_TABLE.sql
\i XHB_HEARING_DATA_TABLE.sql
\i XHB_HEARING_LIST_DATA_TABLE.sql
\i XHB_SITTING_DATA_TABLE.sql
\i XHB_SCHEDULED_HEARING_DATA_TABLE.sql
\i XHB_DEFENDANT_DATA_TABLE.sql
\i XHB_DEFENDANT_ON_CASE_DATA_TABLE.sql
\i XHB_SCHED_HEARING_DEFENDANT_DATA_TABLE.sql
\i XHB_COURT_LOG_ENTRY_DATA_TABLE.sql
\i XHB_REF_JUDGE_DATA_TABLE.sql
