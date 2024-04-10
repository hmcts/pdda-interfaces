\COPY xhb_address (address_id, address_1, address_2, address_3, address_4, town, county, postcode, country, last_update_date, creation_date, created_by, last_updated_by, version) FROM 'XHB_ADDRESS_DATA_TABLE.csv' DELIMITER ',' CSV HEADER;

\COPY xhb_config_prop (config_prop_id, property_name, property_value) FROM 'XHB_CONFIG_PROP_DATA_TABLE.csv' DELIMITER ',' CSV HEADER;

\COPY xhb_court (court_id, court_type, circuit, court_name, crest_court_id, court_prefix, short_name, last_update_date, creation_date, created_by, last_updated_by, version, address_id, crest_ip_address, in_service_flag, obs_ind, probation_office_name, internet_court_name, display_name, court_code, country, language, police_force_code, fl_rep_sort, court_start_time, wl_rep_sort, wl_rep_period, wl_rep_time, wl_free_text, is_pilot, dx_ref, county_loc_code, tier, cpp_court) FROM 'XHB_COURT_DATA_TABLE.csv' DELIMITER ',' CSV HEADER;

\COPY xhb_court_room (court_room_id, court_room_name, description, crest_court_room_no, court_site_id, last_update_date, creation_date, created_by, last_updated_by, version, obs_ind, display_name, security_ind, video_ind) FROM 'XHB_COURT_ROOM_DATA_TABLE.csv' DELIMITER ',' CSV HEADER;

\COPY xhb_court_satellite (court_satellite_id, court_site_id, internet_satellite_name, last_update_date, creation_date, created_by, last_updated_by, version, obs_ind) FROM 'XHB_COURT_SATELLITE_DATA_TABLE.csv' DELIMITER ',' CSV HEADER;

\COPY xhb_court_site (court_site_id, court_site_name, court_site_code, court_id, address_id, last_update_date, creation_date, created_by, last_updated_by, version, obs_ind, display_name, crest_court_id, short_name, site_group, floater_text, list_name, tier) FROM 'XHB_COURT_SITE_DATA_TABLE.csv' DELIMITER ',' CSV HEADER;

\COPY xhb_ref_court (ref_court_id, court_full_name, court_short_name, name_prefix, court_type, crest_code, obs_ind, is_psd, dx_ref, last_update_date, creation_date, created_by, last_updated_by, version, address_id, court_id) FROM 'XHB_REF_COURT_DATA_TABLE.csv' DELIMITER ',' CSV HEADER;

\COPY xhb_ref_court_reporter (ref_court_reporter_id, first_name, middle_name, surname, crest_court_reporter_id, initials, report_method, obs_ind, ref_court_reporter_firm_id, last_update_date, creation_date, created_by, last_updated_by, version, court_id) FROM 'XHB_REF_COURT_REPORTER_DATA_TABLE.csv' DELIMITER ',' CSV HEADER;

\COPY xhb_ref_court_reporter_firm (ref_court_reporter_firm_id, obs_ind, display_first, dx_ref, vat_no, firm_name, address_id, court_id, last_updated_by, created_by, creation_date, last_update_date, version, crest_court_reporter_firm_id) FROM 'XHB_REF_COURT_REPORTER_FIRM_DATA_TABLE.csv' DELIMITER ',' CSV HEADER;

\COPY xhb_ref_cracked_effective (ref_cracked_effective_id, code, description, party_responsible, obs_ind, last_update_date, creation_date, last_updated_by, created_by, version, trial_code_type) FROM 'XHB_REF_CRACKED_EFFECTIVE_DATA_TABLE.csv' DELIMITER ',' CSV HEADER;

\COPY xhb_ref_hearing_type (ref_hearing_type_id, hearing_type_code, hearing_type_desc, category, seq_no, list_sequence, last_update_date, creation_date, created_by, last_updated_by, version, court_id, obs_ind) FROM 'XHB_REF_HEARING_TYPE_DATA_TABLE.csv' DELIMITER ',' CSV HEADER;

\COPY xhb_ref_judge (ref_judge_id, judge_type, crest_judge_id, title, first_name, middle_name, surname, full_list_title1, full_list_title2, full_list_title3, stats_code, initials, honours, jud_vers, obs_ind, source_table, last_update_date, creation_date, created_by, last_updated_by, version, court_id) FROM 'XHB_REF_JUDGE_DATA_TABLE.csv' DELIMITER ',' CSV HEADER;

\COPY xhb_ref_justice (ref_justice_id, justice_name, crest_justice_id, court_id, psd_court_code, title, initials, last_update_date, creation_date, created_by, last_updated_by, version, obs_ind) FROM 'XHB_REF_JUSTICE_DATA_TABLE.csv' DELIMITER ',' CSV HEADER;

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
