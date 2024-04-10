\COPY xhb_address (address_id, address_1, address_2, address_3, address_4, town, county, postcode, country, last_update_date, creation_date, created_by, last_updated_by, version) FROM 'XHB_ADDRESS_DATA_TABLE.csv' DELIMITER ',' CSV HEADER;

\COPY xhb_ref_legal_representative (ref_legal_rep_id, first_name, middle_name, surname, title, initials, legal_rep_type, last_update_date, creation_date, created_by, last_updated_by, version, court_id, obs_ind) FROM 'XHB_REF_LEGAL_REPRESENTATIVE_DATA_TABLE.csv' DELIMITER ',' CSV HEADER;

\COPY xhb_ref_listing_data (ref_listing_data_id, ref_data_type, ref_data_value, created_by, last_updated_by, creation_date, last_update_date, obs_ind, version) FROM 'XHB_REF_LISTING_DATA_DATA_TABLE.csv' DELIMITER ',' CSV HEADER;

\COPY xhb_ref_monitoring_category (ref_monitoring_category_id, monitoring_category_code, monitoring_category_name, created_by, last_updated_by, creation_date, last_update_date, version) FROM 'XHB_REF_MONITORING_CATEGORY_DATA_TABLE.csv' DELIMITER ',' CSV HEADER;

\COPY xhb_ref_pdda_message_type (pdda_message_type_id, pdda_message_type, pdda_message_description, obs_ind, last_update_date, creation_date, created_by, last_updated_by, version) FROM 'XHB_REF_PDDA_MESSAGE_TYPE_TABLE.csv' DELIMITER ',' CSV HEADER;

\COPY xhb_ref_solicitor_firm (ref_solicitor_firm_id, solicitor_firm_name, crest_sof_id, court_id, obs_ind, short_name, dx_ref, vat_no, last_update_date, creation_date, created_by, last_updated_by, version, address_id, la_code) FROM 'XHB_REF_SOLICITOR_FIRM_DATA_TABLE.csv' DELIMITER ',' CSV HEADER;

\COPY xhb_ref_system_code (ref_system_code_id, code, code_type, code_title, de_code, ref_code_order, last_update_date, creation_date, created_by, last_updated_by, version, court_id, obs_ind) FROM 'XHB_REF_SYSTEM_CODE_DATA_TABLE.csv' DELIMITER ',' CSV HEADER;

\COPY xhb_sys_audit (sys_audit_id, table_to_audit, audit_table, auditable) FROM 'XHB_SYS_AUDIT_DATA_TABLE.csv' DELIMITER ',' CSV HEADER;

\COPY xhb_sys_user_information (mercator_user_name, connection_pool_user_name) FROM 'XHB_SYS_USER_INFORMATION_DATA_TABLE.csv' DELIMITER ',' CSV HEADER;

\COPY xhb_configured_public_notice (configured_public_notice_id, is_active, court_room_id, public_notice_id, last_update_date, creation_date, created_by, last_updated_by, version) FROM 'XHB_CONFIGURED_PUBLIC_NOTICE_DATA_TABLE.csv' DELIMITER ',' CSV HEADER;

\COPY xhb_court_log_event_desc (event_desc_id, flagged_event, editable, send_to_mercator, update_linked_cases, publish_to_subscribers, clear_public_displays, e_inform, public_display, linked_case_text, event_description, version, last_updated_by, event_type, created_by, creation_date, last_update_date, public_notice, short_description) FROM 'XHB_COURT_LOG_EVENT_DESC_DATA_TABLE.csv' DELIMITER ',' CSV HEADER;

\COPY xhb_cr_live_display (cr_live_display_id, court_room_id, scheduled_hearing_id, time_status_set, status, created_by, creation_date, last_updated_by, last_update_date, version) FROM 'XHB_CR_LIVE_DISPLAY_DATA_TABLE.csv' DELIMITER ',' CSV HEADER;

\COPY xhb_definitive_public_notice (definitive_pn_id, definitive_pn_desc, last_update_date, creation_date, created_by, last_updated_by, version, priority) FROM 'XHB_DEFINITIVE_PUBLIC_NOTICE_DATA_TABLE.csv' DELIMITER ',' CSV HEADER;

\COPY xhb_display (display_id, display_type_id, display_location_id, rotation_set_id, description_code, locale, created_by, creation_date, last_updated_by, last_update_date, version, show_unassigned_yn) FROM 'XHB_DISPLAY_DATA_TABLE.csv' DELIMITER ',' CSV HEADER;

\COPY xhb_display_court_room (display_id, court_room_id, created_by, creation_date, last_updated_by, last_update_date, version) FROM 'XHB_DISPLAY_COURT_ROOM_DATA_TABLE.csv' DELIMITER ',' CSV HEADER;

\COPY xhb_display_document (display_document_id, description_code, default_page_delay, multiple_court_yn, created_by, creation_date, last_updated_by, last_update_date, version, country, language) FROM 'XHB_DISPLAY_DOCUMENT_DATA_TABLE.csv' DELIMITER ',' CSV HEADER;

\COPY xhb_display_location (display_location_id, description_code, court_site_id, created_by, creation_date, last_updated_by, last_update_date, version) FROM 'XHB_DISPLAY_LOCATION_DATA_TABLE.csv' DELIMITER ',' CSV HEADER;

\COPY xhb_display_type (display_type_id, description_code, created_by, creation_date, last_updated_by, last_update_date, version) FROM 'XHB_DISPLAY_TYPE_DATA_TABLE.csv' DELIMITER ',' CSV HEADER;

\COPY xhb_public_notice (public_notice_id, public_notice_desc, court_id, last_update_date, creation_date, created_by, last_updated_by, version, definitive_pn_id) FROM 'XHB_PUBLIC_NOTICE_DATA_TABLE.csv' DELIMITER ',' CSV HEADER;

\COPY xhb_rotation_set_dd (rotation_set_dd_id, rotation_set_id, display_document_id, page_delay, ordering, created_by, creation_date, last_updated_by, last_update_date, version) FROM 'XHB_ROTATION_SET_DD_DATA_TABLE.csv' DELIMITER ',' CSV HEADER;

\COPY xhb_rotation_sets (rotation_set_id, court_id, description, default_yn, created_by, creation_date, last_updated_by, last_update_date, version) FROM 'XHB_ROTATION_SETS_DATA_TABLE.csv' DELIMITER ',' CSV HEADER;
