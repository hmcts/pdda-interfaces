SET client_encoding TO 'UTF8';

SELECT setval('xhb_blob_seq', COALESCE(MAX(blob_id)+1, 1), FALSE) FROM xhb_blob;
SELECT setval('xhb_case_diary_fixture_seq', COALESCE(MAX(case_diary_fixture_id)+1, 1), FALSE) FROM xhb_case_diary_fixture;
SELECT setval('xhb_case_listing_entry_seq', COALESCE(MAX(case_listing_entry_id)+1, 1), FALSE) FROM xhb_case_listing_entry;
SELECT setval('xhb_case_on_list_seq', COALESCE(MAX(case_on_list_id)+1, 1), FALSE) FROM xhb_case_on_list;
SELECT setval('xhb_case_seq', COALESCE(MAX(case_id)+1, 1), FALSE) FROM xhb_case;
SELECT setval('xhb_cc_info_seq', COALESCE(MAX(cc_info_id)+1, 1), FALSE) FROM xhb_cc_info;
SELECT setval('xhb_clob_seq', COALESCE(MAX(clob_id)+1, 1), FALSE) FROM xhb_clob;
SELECT setval('xhb_config_prop_seq', COALESCE(MAX(config_prop_id)+1, 1), FALSE) FROM xhb_config_prop;
SELECT setval('xhb_court_room_seq', COALESCE(MAX(court_room_id)+1, 1), FALSE) FROM xhb_court_room;
SELECT setval('xhb_court_satellite_seq', COALESCE(MAX(court_satellite_id)+1, 1), FALSE) FROM xhb_court_satellite;
SELECT setval('xhb_court_seq', COALESCE(MAX(court_id)+1, 1), FALSE) FROM xhb_court;
SELECT setval('xhb_court_site_seq', COALESCE(MAX(court_site_id)+1, 1), FALSE) FROM xhb_court_site;
SELECT setval('xhb_cpp_formatting_merge_seq', COALESCE(MAX(cpp_formatting_merge_id)+1, 1), FALSE) FROM xhb_cpp_formatting_merge;
SELECT setval('xhb_cpp_formatting_seq', COALESCE(MAX(cpp_formatting_id)+1, 1), FALSE) FROM xhb_cpp_formatting;
SELECT setval('xhb_cpp_list_seq', COALESCE(MAX(cpp_list_id)+1, 1), FALSE) FROM xhb_cpp_list;
SELECT setval('xhb_cpp_staging_inbound_seq', COALESCE(MAX(cpp_staging_inbound_id)+1, 1), FALSE) FROM xhb_cpp_staging_inbound;
SELECT setval('xhb_formatting_seq', COALESCE(MAX(formatting_id)+1, 1), FALSE) FROM xhb_formatting;
SELECT setval('xhb_hearing_list_seq', COALESCE(MAX(list_id)+1, 1), FALSE) FROM xhb_hearing_list;
SELECT setval('xhb_hearing_seq', COALESCE(MAX(hearing_id)+1, 1), FALSE) FROM xhb_hearing;
SELECT setval('xhb_list_seq', COALESCE(MAX(list_id)+1, 1), FALSE) FROM xhb_list;
SELECT setval('xhb_ref_cracked_effective_seq', COALESCE(MAX(ref_cracked_effective_id)+1, 1), FALSE) FROM xhb_ref_cracked_effective;
SELECT setval('xhb_ref_hearing_type_seq', COALESCE(MAX(ref_hearing_type_id)+1, 1), FALSE) FROM xhb_ref_hearing_type;
SELECT setval('xhb_ref_judge_seq', COALESCE(MAX(ref_judge_id)+1, 1), FALSE) FROM xhb_ref_judge;
SELECT setval('xhb_ref_justice_seq', COALESCE(MAX(ref_justice_id)+1, 1), FALSE) FROM xhb_ref_justice;
SELECT setval('xhb_ref_listing_data_seq', COALESCE(MAX(ref_listing_data_id)+1, 1), FALSE) FROM xhb_ref_listing_data;
SELECT setval('xhb_ref_system_code_seq', COALESCE(MAX(ref_system_code_id)+1, 1), FALSE) FROM xhb_ref_system_code;
SELECT setval('xhb_scheduled_hearing_def_seq', COALESCE(MAX(sched_hear_def_id)+1, 1), FALSE) FROM xhb_sched_hearing_defendant;
SELECT setval('xhb_scheduled_hearing_seq', COALESCE(MAX(scheduled_hearing_id)+1, 1), FALSE) FROM xhb_scheduled_hearing;
SELECT setval('xhb_sched_hearing_attend_seq', COALESCE(MAX(sh_attendee_id)+1, 1), FALSE) FROM xhb_sched_hearing_attendee;
SELECT setval('xhb_sh_judge_seq', COALESCE(MAX(sh_judge_id)+1, 1), FALSE) FROM xhb_sh_judge;
SELECT setval('xhb_sh_justice_seq', COALESCE(MAX(sh_justice_id)+1, 1), FALSE) FROM xhb_sh_justice;
SELECT setval('xhb_sh_staff_seq', COALESCE(MAX(sh_staff_id)+1, 1), FALSE) FROM xhb_sh_staff;
SELECT setval('xhb_sitting_on_list_seq', COALESCE(MAX(sitting_on_list_id)+1, 1), FALSE) FROM xhb_sitting_on_list;
SELECT setval('xhb_sitting_seq', COALESCE(MAX(sitting_id)+1, 1), FALSE) FROM xhb_sitting;
SELECT setval('xhb_sys_audit_seq', COALESCE(MAX(sys_audit_id)+1, 1), FALSE) FROM xhb_sys_audit;
SELECT setval('hk_cpp_run_id_seq', COALESCE(MAX(hk_cpp_run_id)+1, 1), FALSE) FROM xhb_hk_cpp_results;
SELECT setval('hk_run_id_seq', COALESCE(MAX(hk_run_id)+1, 1), FALSE) FROM xhb_hk_results;
SELECT setval('xhb_pdda_message_seq', COALESCE(MAX(pdda_message_id)+1, 1), FALSE) FROM xhb_pdda_message;
SELECT setval('xhb_ref_pdda_message_type_seq', COALESCE(MAX(pdda_message_type_id)+1, 1), FALSE) FROM xhb_ref_pdda_message_type;
SELECT setval('xhb_case_reference_seq', COALESCE(MAX(case_reference_id)+1, 1), FALSE) FROM xhb_case_reference;
SELECT setval('xhb_configured_public_not_seq', COALESCE(MAX(configured_public_notice_id)+1, 1), FALSE) FROM xhb_configured_public_notice;
SELECT setval('xhb_court_log_entry_seq', COALESCE(MAX(entry_id)+1, 1), FALSE) FROM xhb_court_log_entry;
SELECT setval('xhb_court_log_event_desc_seq', COALESCE(MAX(event_desc_id)+1, 1), FALSE) FROM xhb_court_log_event_desc;
SELECT setval('xhb_cr_live_display_seq', COALESCE(MAX(cr_live_display_id)+1, 1), FALSE) FROM xhb_cr_live_display;
SELECT setval('xhb_defendant_on_case_seq', COALESCE(MAX(defendant_on_case_id)+1, 1), FALSE) FROM xhb_defendant_on_case;
SELECT setval('xhb_defendant_seq', COALESCE(MAX(defendant_id)+1, 1), FALSE) FROM xhb_defendant;
SELECT setval('xhb_definitive_pub_notice_seq', COALESCE(MAX(definitive_pn_id)+1, 1), FALSE) FROM xhb_definitive_public_notice;
SELECT setval('xhb_display_document_seq', COALESCE(MAX(display_document_id)+1, 1), FALSE) FROM xhb_display_document;
SELECT setval('xhb_display_location_seq', COALESCE(MAX(display_location_id)+1, 1), FALSE) FROM xhb_display_location;
SELECT setval('xhb_display_seq', COALESCE(MAX(display_id)+1, 1), FALSE) FROM xhb_display;
SELECT setval('xhb_display_type_seq', COALESCE(MAX(display_type_id)+1, 1), FALSE) FROM xhb_display_type;
SELECT setval('xhb_public_notice_seq', COALESCE(MAX(public_notice_id)+1, 1), FALSE) FROM xhb_public_notice;
SELECT setval('xhb_rotation_sets_seq', COALESCE(MAX(rotation_set_dd_id)+1, 1), FALSE) FROM xhb_rotation_set_dd;
SELECT setval('xhb_rotation_set_dd_seq', COALESCE(MAX(rotation_set_id)+1, 1), FALSE) FROM xhb_rotation_sets;
SELECT setval('xhb_pdda_dl_notifier_seq', COALESCE(MAX(pdda_dl_notifier_id)+1, 1), FALSE) FROM xhb_pdda_dl_notifier;
SELECT setval('xhb_display_store_seq', COALESCE(MAX(display_store_id)+1, 1), FALSE) FROM xhb_display_store;
SELECT setval('xhb_xml_document_seq', COALESCE(MAX(xml_document_id)+1, 1), FALSE) FROM xhb_xml_document;
