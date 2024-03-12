SET client_encoding TO 'UTF8';

ALTER TABLE xhb_case ADD CONSTRAINT case_ccc_trans_fr_ref_ct_id_fk FOREIGN KEY (ccc_trans_from_ref_court_id) REFERENCES xhb_ref_court(ref_court_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_case ADD CONSTRAINT case_ccc_trans_to_ref_ct_id_fk FOREIGN KEY (ccc_trans_to_ref_court_id) REFERENCES xhb_ref_court(ref_court_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_case ADD CONSTRAINT case_court_id_fk FOREIGN KEY (court_id) REFERENCES xhb_court(court_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_case ADD CONSTRAINT case_cracked_ineffective_id_fk FOREIGN KEY (cracked_ineffective_id) REFERENCES xhb_ref_system_code(ref_system_code_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_case ADD CONSTRAINT case_default_hearing_type_fk FOREIGN KEY (default_hearing_type) REFERENCES xhb_ref_hearing_type(ref_hearing_type_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_case ADD CONSTRAINT case_ref_court_id_fk FOREIGN KEY (ref_court_id) REFERENCES xhb_ref_court(ref_court_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_case ADD CONSTRAINT xhb_court_receiving_code_fk FOREIGN KEY (court_id_receiving_site) REFERENCES xhb_court_site(court_site_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_case ADD CONSTRAINT xhb_magcourt_hearing_fk FOREIGN KEY (magcourt_hearingtype_ref_id) REFERENCES xhb_ref_system_code(ref_system_code_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_case ADD CONSTRAINT xhb_monitoringcategory_id_fk FOREIGN KEY (monitoring_category_id) REFERENCES xhb_ref_monitoring_category(ref_monitoring_category_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_case ADD CONSTRAINT xhb_policeforce_code_fk FOREIGN KEY (police_force_code) REFERENCES xhb_ref_system_code(ref_system_code_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_case ADD CONSTRAINT xhb_tickettype_code_fk FOREIGN KEY (ticket_type_code) REFERENCES xhb_ref_system_code(ref_system_code_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_case_diary_fixture ADD CONSTRAINT cdf_case_listing_entry_id_fk FOREIGN KEY (case_listing_entry_id) REFERENCES xhb_case_listing_entry(case_listing_entry_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_case_diary_fixture ADD CONSTRAINT cdf_free_text_note_class_id_fk FOREIGN KEY (free_text_note_class_id) REFERENCES xhb_ref_listing_data(ref_listing_data_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_case_diary_fixture ADD CONSTRAINT cdf_hearing_type_id_fk FOREIGN KEY (hearing_type_id) REFERENCES xhb_ref_hearing_type(ref_hearing_type_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_case_diary_fixture ADD CONSTRAINT cdf_listnotepredefinedid_fk FOREIGN KEY (list_note_pre_defined_id) REFERENCES xhb_ref_listing_data(ref_listing_data_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_case_diary_fixture ADD CONSTRAINT cdf_pre_def_note_class_id_fk FOREIGN KEY (pre_def_note_class_id) REFERENCES xhb_ref_listing_data(ref_listing_data_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_case_diary_fixture ADD CONSTRAINT cdf_vacation_predef_rson_id_fk FOREIGN KEY (vacation_pre_defined_rson_id) REFERENCES xhb_ref_system_code(ref_system_code_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_case_diary_fixture ADD CONSTRAINT xhb_case_diary_fixture_cs_id FOREIGN KEY (court_site_id) REFERENCES xhb_court_site(court_site_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_case_listing_entry ADD CONSTRAINT case_listing_court_site_id_fk FOREIGN KEY (court_site_id) REFERENCES xhb_court_site(court_site_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_case_listing_entry ADD CONSTRAINT case_listing_judge_id_fk FOREIGN KEY (judge_id) REFERENCES xhb_ref_judge(ref_judge_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_case_listing_entry ADD CONSTRAINT case_listing_judge_type_id_fk FOREIGN KEY (ref_judge_type_id) REFERENCES xhb_ref_system_code(ref_system_code_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_case_listing_entry ADD CONSTRAINT xhb_case_list_entry_case_id_fk FOREIGN KEY (case_id) REFERENCES xhb_case(case_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_case_listing_entry ADD CONSTRAINT xhb_case_list_entry_crt_id_fk FOREIGN KEY (court_id) REFERENCES xhb_court(court_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_case_on_list ADD CONSTRAINT xhb_case_on_list_case_id_fk FOREIGN KEY (case_id) REFERENCES xhb_case(case_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_case_on_list ADD CONSTRAINT xhb_case_on_list_cdf_fk FOREIGN KEY (case_diary_fixture_id) REFERENCES xhb_case_diary_fixture(case_diary_fixture_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_case_on_list ADD CONSTRAINT xhb_case_on_list_col_fk FOREIGN KEY (parent_case_on_list_id) REFERENCES xhb_case_on_list(case_on_list_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_case_on_list ADD CONSTRAINT xhb_case_on_list_cr_id_fk FOREIGN KEY (court_room_id) REFERENCES xhb_court_room(court_room_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_case_on_list ADD CONSTRAINT xhb_case_on_list_cr_iid_id_fk FOREIGN KEY (cracked_ineffective_id) REFERENCES xhb_ref_cracked_effective(ref_cracked_effective_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_case_on_list ADD CONSTRAINT xhb_case_on_list_cs_id_fk FOREIGN KEY (court_site_id) REFERENCES xhb_court_site(court_site_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_case_on_list ADD CONSTRAINT xhb_case_on_list_list_id_fk FOREIGN KEY (list_id) REFERENCES xhb_list(list_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_case_on_list ADD CONSTRAINT xhb_case_on_list_lnpd_fk FOREIGN KEY (list_note_predefined_id) REFERENCES xhb_ref_listing_data(ref_listing_data_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_case_on_list ADD CONSTRAINT xhb_case_on_list_rht_id_fk FOREIGN KEY (hearing_type_id) REFERENCES xhb_ref_hearing_type(ref_hearing_type_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_case_on_list ADD CONSTRAINT xhb_case_on_list_sol_fk FOREIGN KEY (sitting_on_list_id) REFERENCES xhb_sitting_on_list(sitting_on_list_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_case_on_list ADD CONSTRAINT xhb_case_on_list_tm_id_fk FOREIGN KEY (time_marking_id) REFERENCES xhb_ref_system_code(ref_system_code_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_case_on_list ADD CONSTRAINT xhb_case_on_list_vpdri_fk FOREIGN KEY (vacation_pre_defined_rson_id) REFERENCES xhb_ref_system_code(ref_system_code_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_court ADD CONSTRAINT court_address_id_fk FOREIGN KEY (address_id) REFERENCES xhb_address(address_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_court ADD CONSTRAINT xhb_police_force_code_fk FOREIGN KEY (police_force_code) REFERENCES xhb_ref_system_code(ref_system_code_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_court_room ADD CONSTRAINT court_room_court_site_id_fk FOREIGN KEY (court_site_id) REFERENCES xhb_court_site(court_site_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_court_satellite ADD CONSTRAINT sys_c0012884 FOREIGN KEY (court_site_id) REFERENCES xhb_court_site(court_site_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_court_site ADD CONSTRAINT court_site_address_id_fk FOREIGN KEY (address_id) REFERENCES xhb_address(address_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_court_site ADD CONSTRAINT court_site_court_id_fk FOREIGN KEY (court_id) REFERENCES xhb_court(court_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_cpp_formatting ADD CONSTRAINT cpp_clob_id_fk FOREIGN KEY (xml_document_clob_id) REFERENCES xhb_clob(clob_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_cpp_formatting ADD CONSTRAINT cpp_court_id_fk FOREIGN KEY (court_id) REFERENCES xhb_court(court_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_cpp_formatting ADD CONSTRAINT cpp_staging_id_fk FOREIGN KEY (staging_table_id) REFERENCES xhb_cpp_staging_inbound(cpp_staging_inbound_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_cpp_formatting_merge ADD CONSTRAINT cpp_merge_clob_id_fk FOREIGN KEY (xhibit_clob_id) REFERENCES xhb_clob(clob_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_cpp_formatting_merge ADD CONSTRAINT cpp_merge_court_id_fk FOREIGN KEY (court_id) REFERENCES xhb_court(court_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_cpp_formatting_merge ADD CONSTRAINT cpp_merge_cpp_formatting_id_fk FOREIGN KEY (cpp_formatting_id) REFERENCES xhb_cpp_formatting(cpp_formatting_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_cpp_formatting_merge ADD CONSTRAINT cpp_merge_formatting_id_fk FOREIGN KEY (formatting_id) REFERENCES xhb_formatting(formatting_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_cpp_list ADD CONSTRAINT list_clob_id_fk FOREIGN KEY (list_clob_id) REFERENCES xhb_clob(clob_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_cpp_list ADD CONSTRAINT merged_clob_id_fk FOREIGN KEY (merged_clob_id) REFERENCES xhb_clob(clob_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_cpp_staging_inbound ADD CONSTRAINT cpp_staging_clob_id_fk FOREIGN KEY (clob_id) REFERENCES xhb_clob(clob_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_formatting ADD CONSTRAINT formatting_court_id_fk FOREIGN KEY (court_id) REFERENCES xhb_court(court_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_formatting ADD CONSTRAINT xhb_format_format_blob_id_fk FOREIGN KEY (formatted_document_blob_id) REFERENCES xhb_blob(blob_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_hearing ADD CONSTRAINT hearing_case_id_fk FOREIGN KEY (case_id) REFERENCES xhb_case(case_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_hearing ADD CONSTRAINT hearing_court_id_fk FOREIGN KEY (court_id) REFERENCES xhb_court(court_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_hearing ADD CONSTRAINT hearing_ref_hearing_type_id_fk FOREIGN KEY (ref_hearing_type_id) REFERENCES xhb_ref_hearing_type(ref_hearing_type_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_hearing_list ADD CONSTRAINT hearing_list_court_id_fk FOREIGN KEY (court_id) REFERENCES xhb_court(court_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_list ADD CONSTRAINT xhb_list_court_id_fk FOREIGN KEY (court_id) REFERENCES xhb_court(court_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_list ADD CONSTRAINT xhb_list_list_type_id_fk FOREIGN KEY (list_type_id) REFERENCES xhb_ref_listing_data(ref_listing_data_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_list ADD CONSTRAINT xhb_list_parent_fk FOREIGN KEY (list_parent_id) REFERENCES xhb_list(list_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_ref_court ADD CONSTRAINT ref_court_address_id_fk FOREIGN KEY (address_id) REFERENCES xhb_address(address_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_ref_court ADD CONSTRAINT ref_court_court_id_fk FOREIGN KEY (court_id) REFERENCES xhb_court(court_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_ref_court_reporter ADD CONSTRAINT ref_ct_rep_court_id_fk FOREIGN KEY (court_id) REFERENCES xhb_court(court_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_ref_court_reporter ADD CONSTRAINT ref_ct_rep_ref_ct_rep_f_id_fk FOREIGN KEY (ref_court_reporter_firm_id) REFERENCES xhb_ref_court_reporter_firm(ref_court_reporter_firm_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_ref_court_reporter_firm ADD CONSTRAINT ref_ct_rep_firm_address_id_fk FOREIGN KEY (address_id) REFERENCES xhb_address(address_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_ref_court_reporter_firm ADD CONSTRAINT ref_ct_rep_firm_court_id_fk FOREIGN KEY (court_id) REFERENCES xhb_court(court_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_ref_hearing_type ADD CONSTRAINT ref_hearing_type_court_id_fk FOREIGN KEY (court_id) REFERENCES xhb_court(court_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_ref_judge ADD CONSTRAINT ref_judge_court_id_fk FOREIGN KEY (court_id) REFERENCES xhb_court(court_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_ref_justice ADD CONSTRAINT ref_justice_court_id_fk FOREIGN KEY (court_id) REFERENCES xhb_court(court_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_ref_legal_representative ADD CONSTRAINT ref_legal_rep_court_id_fk FOREIGN KEY (court_id) REFERENCES xhb_court(court_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_ref_solicitor_firm ADD CONSTRAINT ref_sol_firm_address_id_fk FOREIGN KEY (address_id) REFERENCES xhb_address(address_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_ref_solicitor_firm ADD CONSTRAINT ref_sol_firm_court_id_fk FOREIGN KEY (court_id) REFERENCES xhb_court(court_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_ref_system_code ADD CONSTRAINT ref_system_code_court_id_fk FOREIGN KEY (court_id) REFERENCES xhb_court(court_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_scheduled_hearing ADD CONSTRAINT sched_hear_hearing_id_fk FOREIGN KEY (hearing_id) REFERENCES xhb_hearing(hearing_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_scheduled_hearing ADD CONSTRAINT sched_hear_moved_f_ct_rm_id_fk FOREIGN KEY (moved_from_court_room_id) REFERENCES xhb_court_room(court_room_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_scheduled_hearing ADD CONSTRAINT sched_hear_sitting_id_fk FOREIGN KEY (sitting_id) REFERENCES xhb_sitting(sitting_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_scheduled_hearing ADD CONSTRAINT xhb_scheduled_hearing_rce_fk FOREIGN KEY (ref_cracked_effective_id) REFERENCES xhb_ref_cracked_effective(ref_cracked_effective_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_sched_hearing_attendee ADD CONSTRAINT sched_h_att_ref_ct_rep_id_fk FOREIGN KEY (ref_court_reporter_id) REFERENCES xhb_ref_court_reporter(ref_court_reporter_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_sched_hearing_attendee ADD CONSTRAINT sched_h_att_ref_judge_id_fk FOREIGN KEY (ref_judge_id) REFERENCES xhb_ref_judge(ref_judge_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_sched_hearing_attendee ADD CONSTRAINT sched_h_att_ref_justice_id_fk FOREIGN KEY (ref_justice_id) REFERENCES xhb_ref_justice(ref_justice_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_sched_hearing_attendee ADD CONSTRAINT sched_h_att_sched_hear_id_fk FOREIGN KEY (scheduled_hearing_id) REFERENCES xhb_scheduled_hearing(scheduled_hearing_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_sched_hearing_attendee ADD CONSTRAINT sched_h_att_sh_justice_id_fk FOREIGN KEY (sh_justice_id) REFERENCES xhb_sh_justice(sh_justice_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_sched_hearing_attendee ADD CONSTRAINT sched_h_att_sh_staff_id_fk FOREIGN KEY (sh_staff_id) REFERENCES xhb_sh_staff(sh_staff_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_sched_hearing_defendant ADD CONSTRAINT sched_h_def_sched_hear_id_fk FOREIGN KEY (scheduled_hearing_id) REFERENCES xhb_scheduled_hearing(scheduled_hearing_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_sh_judge ADD CONSTRAINT sh_judge_ref_judge_id_fk FOREIGN KEY (ref_judge_id) REFERENCES xhb_ref_judge(ref_judge_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_sh_judge ADD CONSTRAINT sh_judge_sh_attendee_id_fk FOREIGN KEY (sh_attendee_id) REFERENCES xhb_sched_hearing_attendee(sh_attendee_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_sh_justice ADD CONSTRAINT sh_justice_hearing_id_fk FOREIGN KEY (hearing_id) REFERENCES xhb_hearing(hearing_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_sh_leg_rep ADD CONSTRAINT sh_leg_rep_cc_info_id_fk FOREIGN KEY (cc_info_id) REFERENCES xhb_cc_info(cc_info_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_sh_leg_rep ADD CONSTRAINT sh_leg_rep_ref_def_cat_id_fk FOREIGN KEY (ref_defence_category_id) REFERENCES xhb_ref_system_code(ref_system_code_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_sh_leg_rep ADD CONSTRAINT sh_leg_rep_ref_legal_rep_id_fk FOREIGN KEY (ref_legal_rep_id) REFERENCES xhb_ref_legal_representative(ref_legal_rep_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_sh_leg_rep ADD CONSTRAINT sh_leg_rep_ref_sol_firm_id_fk FOREIGN KEY (ref_solicitor_firm_id) REFERENCES xhb_ref_solicitor_firm(ref_solicitor_firm_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_sh_leg_rep ADD CONSTRAINT sh_leg_rep_sched_hearing_id_fk FOREIGN KEY (scheduled_hearing_id) REFERENCES xhb_scheduled_hearing(scheduled_hearing_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_sh_leg_rep ADD CONSTRAINT sh_leg_rep_sched_h_def_id_fk FOREIGN KEY (sched_hear_def_id) REFERENCES xhb_sched_hearing_defendant(sched_hear_def_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_sh_leg_rep ADD CONSTRAINT sh_leg_rep_sub_ref_leg_rep_fk FOREIGN KEY (substituted_ref_legal_rep_id) REFERENCES xhb_ref_legal_representative(ref_legal_rep_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_sitting ADD CONSTRAINT sitting_court_room_id_fk FOREIGN KEY (court_room_id) REFERENCES xhb_court_room(court_room_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_sitting ADD CONSTRAINT sitting_court_site_id_fk FOREIGN KEY (court_site_id) REFERENCES xhb_court_site(court_site_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_sitting ADD CONSTRAINT sitting_list_id_fk FOREIGN KEY (list_id) REFERENCES xhb_hearing_list(list_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_sitting ADD CONSTRAINT sitting_ref_judge_id_fk FOREIGN KEY (ref_judge_id) REFERENCES xhb_ref_judge(ref_judge_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_sitting_on_list ADD CONSTRAINT sitt_on_list_court_room_id_fk FOREIGN KEY (court_room_id) REFERENCES xhb_court_room(court_room_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_sitting_on_list ADD CONSTRAINT sitt_on_list_court_site_id_fk FOREIGN KEY (court_site_id) REFERENCES xhb_court_site(court_site_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_sitting_on_list ADD CONSTRAINT sol_free_text_note_class_id_fk FOREIGN KEY (free_text_note_class_id) REFERENCES xhb_ref_listing_data(ref_listing_data_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_sitting_on_list ADD CONSTRAINT sol_judge_ref_id_fk FOREIGN KEY (judge_ref_id) REFERENCES xhb_ref_judge(ref_judge_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_sitting_on_list ADD CONSTRAINT sol_listnotepredefinedid_fk FOREIGN KEY (list_note_pre_defined_id) REFERENCES xhb_ref_listing_data(ref_listing_data_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_sitting_on_list ADD CONSTRAINT sol_list_id_fk FOREIGN KEY (list_id) REFERENCES xhb_list(list_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_sitting_on_list ADD CONSTRAINT sol_pre_def_note_class_id_fk FOREIGN KEY (pre_def_note_classification_id) REFERENCES xhb_ref_listing_data(ref_listing_data_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_sitting_on_list ADD CONSTRAINT sol_time_marking_id_fk FOREIGN KEY (time_marking_id) REFERENCES xhb_ref_system_code(ref_system_code_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_pdda_message ADD CONSTRAINT pddamsg_court_fk FOREIGN KEY (court_id) REFERENCES xhb_court(court_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_pdda_message ADD CONSTRAINT pddamsg_court_room_fk FOREIGN KEY (court_room_id) REFERENCES xhb_court_room(court_room_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_pdda_message ADD CONSTRAINT pddamsg_message_type_fk FOREIGN KEY (pdda_message_type_id) REFERENCES xhb_ref_pdda_message_type(pdda_message_type_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_pdda_message ADD CONSTRAINT pddamsg_clob_fk FOREIGN KEY (pdda_message_data_id) REFERENCES xhb_clob(clob_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_pdda_message ADD CONSTRAINT pddamsg_cppstaging_fk FOREIGN KEY (cpp_staging_inbound_id) REFERENCES xhb_cpp_staging_inbound(cpp_staging_inbound_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_case_reference ADD CONSTRAINT case_reference_case_id_fk FOREIGN KEY (case_id) REFERENCES xhb_case(case_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_configured_public_notice ADD CONSTRAINT con_pub_not_court_room_id_fk FOREIGN KEY (court_room_id) REFERENCES xhb_court_room(court_room_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_configured_public_notice ADD CONSTRAINT con_pub_not_public_not_id_fk FOREIGN KEY (public_notice_id) REFERENCES xhb_public_notice(public_notice_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_court_log_entry ADD CONSTRAINT ct_log_entry_case_id_fk FOREIGN KEY (case_id) REFERENCES xhb_case(case_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_court_log_entry ADD CONSTRAINT ct_log_entry_def_on_cas_id_fk FOREIGN KEY (defendant_on_case_id) REFERENCES xhb_defendant_on_case(defendant_on_case_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_court_log_entry ADD CONSTRAINT ct_log_entry_event_desc_id_fk FOREIGN KEY (event_desc_id) REFERENCES xhb_court_log_event_desc(event_desc_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_court_log_entry ADD CONSTRAINT ct_log_entry_sched_hear_id_fk FOREIGN KEY (scheduled_hearing_id) REFERENCES xhb_scheduled_hearing(scheduled_hearing_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_cr_live_display ADD CONSTRAINT cr_live_disp_court_room_id_fk FOREIGN KEY (court_room_id) REFERENCES xhb_court_room(court_room_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_cr_live_display ADD CONSTRAINT cr_live_disp_sched_hear_id_fk FOREIGN KEY (scheduled_hearing_id) REFERENCES xhb_scheduled_hearing(scheduled_hearing_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_defendant ADD CONSTRAINT defendant_address_id_fk FOREIGN KEY (address_id) REFERENCES xhb_address(address_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_defendant ADD CONSTRAINT defendant_court_id_fk FOREIGN KEY (court_id) REFERENCES xhb_court(court_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_defendant_on_case ADD CONSTRAINT def_on_case_case_id_fk FOREIGN KEY (case_id) REFERENCES xhb_case(case_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_defendant_on_case ADD CONSTRAINT def_on_case_col_mag_ct_id_fk FOREIGN KEY (collect_magistrate_court_id) REFERENCES xhb_ref_court(ref_court_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_defendant_on_case ADD CONSTRAINT def_on_case_defendant_id_fk FOREIGN KEY (defendant_id) REFERENCES xhb_defendant(defendant_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_display ADD CONSTRAINT display_display_location_id_fk FOREIGN KEY (display_location_id) REFERENCES xhb_display_location(display_location_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_display ADD CONSTRAINT display_display_type_id_fk FOREIGN KEY (display_type_id) REFERENCES xhb_display_type(display_type_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_display ADD CONSTRAINT display_rotation_set_id_fk FOREIGN KEY (rotation_set_id) REFERENCES xhb_rotation_sets(rotation_set_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_display_court_room ADD CONSTRAINT display_court_rm_ct_rm_id_fk FOREIGN KEY (court_room_id) REFERENCES xhb_court_room(court_room_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_display_court_room ADD CONSTRAINT display_court_rm_display_id_fk FOREIGN KEY (display_id) REFERENCES xhb_display(display_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_display_location ADD CONSTRAINT display_loc_court_site_id_fk FOREIGN KEY (court_site_id) REFERENCES xhb_court_site(court_site_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_public_notice ADD CONSTRAINT public_not_court_id_fk FOREIGN KEY (court_id) REFERENCES xhb_court(court_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_public_notice ADD CONSTRAINT public_not_definitive_pn_id_fk FOREIGN KEY (definitive_pn_id) REFERENCES xhb_definitive_public_notice(definitive_pn_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_rotation_sets ADD CONSTRAINT rotation_sets_court_id_fk FOREIGN KEY (court_id) REFERENCES xhb_court(court_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_rotation_set_dd ADD CONSTRAINT rot_set_dd_display_doc_id_fk FOREIGN KEY (display_document_id) REFERENCES xhb_display_document(display_document_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_rotation_set_dd ADD CONSTRAINT rot_set_dd_rotation_set_id_fk FOREIGN KEY (rotation_set_id) REFERENCES xhb_rotation_sets(rotation_set_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_pdda_dl_notifier ADD CONSTRAINT pdda_dl_notifier_court_id_fk FOREIGN KEY (court_id) REFERENCES xhb_court(court_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_xml_document ADD CONSTRAINT xhb_xml_document_clob_id_fk FOREIGN KEY (xml_document_clob_id) REFERENCES xhb_clob(clob_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
