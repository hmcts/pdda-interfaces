\copy xhb_hearing (hearing_id, case_id, ref_hearing_type_id, court_id, last_update_date, creation_date, created_by, last_updated_by, version) FROM 'xhb_hearing.csv'  DELIMITER ',' CSV HEADER
\copy xhb_hearing_list (list_id, list_type, start_date, end_date, status, court_id, crest_list_id, last_update_date, creation_date, created_by, last_updated_by, version) FROM 'xhb_hearing_list.csv'  DELIMITER ',' CSV HEADER
\copy xhb_sitting (sitting_id, list_id, court_site_id, court_room_id, is_floating, last_update_date, creation_date, created_by, last_updated_by, version) FROM 'xhb_sitting.csv'  DELIMITER ',' CSV HEADER
\copy xhb_scheduled_hearing (scheduled_hearing_id, sitting_id, hearing_id, is_case_active, moved_from_court_room_id, last_update_date, creation_date, created_by, last_updated_by, version) FROM 'xhb_scheduled_hearing.csv'  DELIMITER ',' CSV HEADER
\copy xhb_defendant (defendant_id, first_name, middle_name, surname, public_display_hide, court_id, last_update_date, creation_date, created_by, last_updated_by, version) FROM 'xhb_defendant.csv'  DELIMITER ',' CSV HEADER
\copy xhb_defendant_on_case (defendant_on_case_id, defendant_id, case_id, last_update_date, creation_date, created_by, last_updated_by, version) FROM 'xhb_defendant_on_case.csv'  DELIMITER ',' CSV HEADER
\copy xhb_sched_hearing_defendant (sched_hear_def_id, scheduled_hearing_id, defendant_on_case_id, last_update_date, creation_date, created_by, last_updated_by, version) FROM 'xhb_sched_hearing_defendant.csv'  DELIMITER ',' CSV HEADER
\copy xhb_court_log_entry (entry_id, case_id, event_desc_id, defendant_on_case_id, scheduled_hearing_id, log_entry_xml, date_time, last_update_date, creation_date, created_by, last_updated_by, version) FROM 'xhb_court_log_entry.csv'  DELIMITER ',' CSV HEADER
\copy xhb_ref_judge (ref_judge_id, judge_type, full_list_title1, first_name, middle_name, surname, court_id, last_update_date, creation_date, created_by, last_updated_by, version) FROM 'xhb_ref_judge.csv'  DELIMITER ',' CSV HEADER
\copy xhb_sched_hearing_attendee (sh_attendee_id, scheduled_hearing_id, ref_judge_id, attendee_type, last_update_date, creation_date, created_by, last_updated_by, version) FROM 'xhb_sched_hearing_attendee.csv'  DELIMITER ',' CSV HEADER


\i XHB_CASE_DATA_TABLE.sql
\i XHB_CLOB_DATA_TABLE.sql
\i XHB_FORMATTING_DATA_TABLE.sql
\i XHB_XML_DOCUMENT_DATA_TABLE.sql
\i XHB_CPP_LIST_DATA_TABLE.sql
\i XHB_CPP_STAGING_INBOUND_DATA_TABLE.sql
\i XHB_CPP_FORMATTING_DATA_TABLE.sql
\i XHB_REF_HEARING_TYPE_DATA_TABLE.sql
