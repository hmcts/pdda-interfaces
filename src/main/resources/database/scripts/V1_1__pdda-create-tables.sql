SET client_encoding TO 'UTF8';

DROP TABLE IF EXISTS xhb_address CASCADE;
CREATE TABLE xhb_address (
	address_id integer NOT NULL,
	address_1 varchar(30),
	address_2 varchar(30),
	address_3 varchar(30),
	address_4 varchar(30),
	town varchar(30),
	county varchar(30),
	postcode varchar(8),
	country varchar(255),
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL
);
ALTER TABLE xhb_address ADD CONSTRAINT xhb_address_pk PRIMARY KEY (address_id);


DROP TABLE IF EXISTS xhb_case CASCADE;
CREATE TABLE xhb_case (
	case_id integer NOT NULL,
	case_number integer,
	case_type varchar(1),
	mag_conviction_date timestamp,
	case_sub_type varchar(1),
	case_title varchar(72),
	case_description varchar(70),
	linked_case_id integer,
	bail_mag_code varchar(255),
	ref_court_id integer,
	court_id integer NOT NULL,
	charge_import_indicator varchar(2),
	severed_ind varchar(1),
	indict_resp varchar(50),
	date_ind_rec timestamp,
	pros_agency_reference varchar(255),
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL,
	case_class integer,
	judge_reason_for_appeal varchar(255),
	results_verified varchar(1),
	length_tape integer,
	no_page_pros_evidence integer,
	no_pros_witness smallint,
	est_pdh_trial_length integer,
	indictment_info_1 varchar(78),
	indictment_info_2 varchar(78),
	indictment_info_3 varchar(78),
	indictment_info_4 varchar(78),
	indictment_info_5 varchar(78),
	indictment_info_6 varchar(78),
	police_officer_attending varchar(70),
	cps_case_worker varchar(70),
	export_charges varchar(1),
	ind_change_status varchar(1),
	magistrates_case_ref varchar(30),
	class_code smallint,
	offence_group_update varchar(1),
	ccc_trans_to_ref_court_id integer,
	receipt_type varchar(2),
	ccc_trans_from_ref_court_id integer,
	date_trans_from timestamp,
	retrial varchar(1),
	original_case_number varchar(9),
	lc_sent_date timestamp,
	no_cb_pros_witness smallint,
	no_other_pros_witness smallint,
	vulnerable_victim_indicator varchar(1),
	public_display_hide varchar(1),
	transferred_case varchar(1),
	transfer_deferred_sentence varchar(1),
	monitoring_category_id bigint,
	appeal_lodged_date timestamp,
	received_date timestamp,
	either_way_type varchar(2),
	ticket_required varchar(1),
	ticket_type_code bigint,
	court_id_receiving_site bigint,
	committal_date timestamp,
	sent_for_trial_date timestamp,
	no_defendants_for_case bigint,
	secure_court varchar(1),
	preliminary_date_of_hearing timestamp,
	original_jps_1 varchar(35),
	original_jps_2 varchar(35),
	original_jps_3 varchar(35),
	original_jps_4 varchar(35),
	police_force_code bigint,
	magcourt_hearingtype_ref_id bigint,
	case_listed varchar(1),
	orig_body_decision_date timestamp,
	case_status varchar(1) DEFAULT ('O'),
	video_link_required varchar(1),
	cracked_ineffective_id integer,
	default_hearing_type integer,
	section28_name1 varchar(50),
	section28_name2 varchar(50),
	section28_phone1 varchar(15),
	section28_phone2 varchar(15),
	case_group_number integer,
	pub_running_list_id integer,
	date_ctl_reminder_printed timestamp,
	date_trans_to timestamp,
	date_trans_recorded_to timestamp,
	s28_eligible char(1),
	s28_order_made char(1),
	televised_application_made varchar(1),
	televised_app_made_date timestamp,
	televised_app_granted varchar(1),
	televised_app_refused_freetext varchar(280),
	televised_remarks_filmed varchar(1),
	dar_retention_policy_id integer,
	crp_last_update_date timestamp,
	civil_unrest varchar(1)
);
ALTER TABLE xhb_case ADD CONSTRAINT xhb_case_pk PRIMARY KEY (case_id);


DROP TABLE IF EXISTS xhb_case_diary_fixture CASCADE;
CREATE TABLE xhb_case_diary_fixture (
	case_diary_fixture_id integer NOT NULL,
	case_listing_entry_id integer NOT NULL,
	listing_date timestamp NOT NULL,
	fixture_notice_required varchar(1) NOT NULL DEFAULT 'N',
	hearing_type_id integer,
	list_note_pre_defined_id integer,
	list_note_text varchar(200),
	pre_def_note_class_id integer,
	free_text_note_class_id integer,
	vacation_pre_defined_rson_id integer,
	vacation_freetext_reason varchar(35),
	status varchar(1) NOT NULL,
	obs_ind varchar(1),
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	creation_date timestamp NOT NULL,
	last_update_date timestamp NOT NULL,
	version integer NOT NULL DEFAULT 1,
	court_site_id integer,
	fxl_run_date timestamp,
	date_vacated timestamp
);
ALTER TABLE xhb_case_diary_fixture ADD CONSTRAINT xhb_case_diary_fixture_pk PRIMARY KEY (case_diary_fixture_id);


DROP TABLE IF EXISTS xhb_case_listing_entry CASCADE;
CREATE TABLE xhb_case_listing_entry (
	case_listing_entry_id integer NOT NULL,
	case_id integer NOT NULL,
	ref_judge_type_id integer,
	court_site_id integer,
	judge_id integer,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	creation_date timestamp NOT NULL,
	last_update_date timestamp NOT NULL,
	version integer NOT NULL DEFAULT 1,
	obs_ind varchar(1),
	court_id integer NOT NULL
);
ALTER TABLE xhb_case_listing_entry ADD CONSTRAINT xhb_case_listing_entry_pk PRIMARY KEY (case_listing_entry_id);


DROP TABLE IF EXISTS xhb_case_on_list CASCADE;
CREATE TABLE xhb_case_on_list (
	case_on_list_id integer NOT NULL,
	case_id integer NOT NULL,
	list_id integer NOT NULL,
	sitting_on_list_id integer,
	court_site_id integer,
	court_room_id integer,
	reserved varchar(1),
	floater_case varchar(1),
	time_marking_id integer,
	time_listed timestamp,
	is_court_room_list_entry varchar(1),
	hearing_type_id integer NOT NULL,
	reason_for_removal varchar(100),
	cracked_ineffective_id integer,
	obs_ind varchar(1),
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	creation_date timestamp NOT NULL,
	last_update_date timestamp NOT NULL,
	version bigint NOT NULL DEFAULT 1,
	seq_no smallint NOT NULL,
	case_diary_fixture_id integer,
	date_of_removal timestamp,
	list_note_predefined_id integer,
	list_note_text varchar(200),
	parent_case_on_list_id integer,
	nha_firm_list varchar(1),
	vacation_pre_defined_rson_id integer
);
ALTER TABLE xhb_case_on_list ADD CONSTRAINT xhb_case_on_list_pk PRIMARY KEY (case_on_list_id);
ALTER TABLE xhb_case_on_list ADD CONSTRAINT is_court_room_list_entry_chk CHECK (is_court_room_list_entry IN ('Y','N'));


DROP TABLE IF EXISTS xhb_cc_info CASCADE;
CREATE TABLE xhb_cc_info (
	cc_info_id integer NOT NULL,
	cc_info_text varchar(255),
	version integer NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	created_by varchar(30) NOT NULL,
	creation_date timestamp NOT NULL,
	last_update_date timestamp NOT NULL
);
ALTER TABLE xhb_cc_info ADD CONSTRAINT xhb_cc_info_pk PRIMARY KEY (cc_info_id);


DROP TABLE IF EXISTS xhb_config_prop CASCADE;
CREATE TABLE xhb_config_prop (
	config_prop_id integer NOT NULL,
	property_name varchar(128) NOT NULL,
	property_value varchar(300) NOT NULL
);
ALTER TABLE xhb_config_prop ADD CONSTRAINT xhb_config_prop_pk PRIMARY KEY (config_prop_id);


DROP TABLE IF EXISTS xhb_court CASCADE;
CREATE TABLE xhb_court (
	court_id integer NOT NULL,
	court_type varchar(255) NOT NULL,
	circuit varchar(255),
	court_name varchar(255) NOT NULL,
	crest_court_id varchar(3) NOT NULL,
	court_prefix varchar(255) NOT NULL,
	short_name varchar(50),
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL,
	address_id integer,
	crest_ip_address varchar(25) NOT NULL,
	in_service_flag varchar(1) NOT NULL DEFAULT 'N',
	obs_ind varchar(1),
	probation_office_name varchar(50) NOT NULL,
	internet_court_name varchar(255) NOT NULL,
	display_name varchar(100),
	court_code char(4),
	country varchar(2),
	language varchar(2),
	police_force_code bigint,
	fl_rep_sort varchar(1) NOT NULL DEFAULT 'C',
	court_start_time varchar(8),
	wl_rep_sort varchar(1) NOT NULL DEFAULT 'C',
	wl_rep_period smallint,
	wl_rep_time varchar(8),
	wl_free_text varchar(240),
	is_pilot varchar(1) NOT NULL DEFAULT 'N',
	dx_ref varchar(35),
	county_loc_code varchar(5),
	tier varchar(1),
	cpp_court varchar(1)
);
ALTER TABLE xhb_court ADD CONSTRAINT xhb_court_pk PRIMARY KEY (court_id);


DROP TABLE IF EXISTS xhb_court_room CASCADE;
CREATE TABLE xhb_court_room (
	court_room_id integer NOT NULL,
	court_room_name varchar(255),
	description varchar(255),
	crest_court_room_no smallint NOT NULL,
	court_site_id integer NOT NULL,
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL,
	obs_ind varchar(1),
	display_name varchar(100),
	security_ind varchar(1),
	video_ind varchar(1)
);
ALTER TABLE xhb_court_room ADD CONSTRAINT xhb_court_room_pk PRIMARY KEY (court_room_id);


DROP TABLE IF EXISTS xhb_court_satellite CASCADE;
CREATE TABLE xhb_court_satellite (
	court_satellite_id integer NOT NULL,
	court_site_id integer NOT NULL,
	internet_satellite_name varchar(255) NOT NULL,
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL,
	obs_ind varchar(1) DEFAULT 'N'
);
ALTER TABLE xhb_court_satellite ADD CONSTRAINT xhb_court_satellite_pk PRIMARY KEY (court_satellite_id);


DROP TABLE IF EXISTS xhb_court_site CASCADE;
CREATE TABLE xhb_court_site (
	court_site_id integer NOT NULL,
	court_site_name varchar(255),
	court_site_code varchar(50),
	court_id integer NOT NULL,
	address_id integer NOT NULL,
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL,
	obs_ind varchar(1),
	display_name varchar(100),
	crest_court_id varchar(3),
	short_name varchar(6),
	site_group integer,
	floater_text varchar(100),
	list_name varchar(39),
	tier varchar(1)
);
ALTER TABLE xhb_court_site ADD CONSTRAINT xhb_court_site_pk PRIMARY KEY (court_site_id);
ALTER TABLE xhb_court_site ADD UNIQUE (court_id,short_name);


DROP TABLE IF EXISTS xhb_cpp_formatting CASCADE;
CREATE TABLE xhb_cpp_formatting (
	cpp_formatting_id integer NOT NULL,
	staging_table_id integer NOT NULL,
	date_in timestamp NOT NULL,
	format_status varchar(2) NOT NULL DEFAULT 'ND',
	document_type varchar(3) NOT NULL,
	court_id integer NOT NULL,
	xml_document_clob_id integer NOT NULL,
	error_message varchar(4000),
	obs_ind varchar(1),
	version integer DEFAULT 1,
	last_update_date timestamp NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL
);
ALTER TABLE xhb_cpp_formatting ADD CONSTRAINT xhb_cpp_formatting_pk PRIMARY KEY (cpp_formatting_id);


DROP TABLE IF EXISTS xhb_cpp_formatting_merge CASCADE;
CREATE TABLE xhb_cpp_formatting_merge (
	cpp_formatting_merge_id integer NOT NULL,
	cpp_formatting_id integer NOT NULL,
	formatting_id integer NOT NULL,
	xhibit_clob_id bigint,
	court_id integer NOT NULL,
	creation_date timestamp NOT NULL,
	language varchar(2) NOT NULL,
	obs_ind varchar(1),
	version integer DEFAULT 1,
	last_update_date timestamp NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	created_by varchar(30) NOT NULL
);
ALTER TABLE xhb_cpp_formatting_merge ADD CONSTRAINT xhb_cpp_formatting_merge_pk PRIMARY KEY (cpp_formatting_merge_id);


DROP TABLE IF EXISTS xhb_cpp_list CASCADE;
CREATE TABLE xhb_cpp_list (
	cpp_list_id integer NOT NULL,
	court_code smallint NOT NULL,
	list_type varchar(1) NOT NULL,
	time_loaded timestamp NOT NULL,
	list_start_date timestamp NOT NULL,
	list_end_date timestamp NOT NULL,
	list_clob_id integer,
	merged_clob_id integer,
	status varchar(2),
	error_message varchar(4000),
	obs_ind varchar(1),
	last_updated_by varchar(30) NOT NULL,
	creation_date timestamp NOT NULL,
	last_update_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	version integer NOT NULL DEFAULT 1
);
ALTER TABLE xhb_cpp_list ADD CONSTRAINT xhb_cpp_list_pk PRIMARY KEY (cpp_list_id);


DROP TABLE IF EXISTS xhb_cpp_staging_inbound CASCADE;
CREATE TABLE xhb_cpp_staging_inbound (
	cpp_staging_inbound_id integer NOT NULL,
	document_name varchar(50) NOT NULL,
	court_code varchar(3) NOT NULL,
	document_type varchar(2) NOT NULL,
	time_loaded timestamp NOT NULL,
	clob_id bigint NOT NULL,
	validation_status varchar(2) NOT NULL,
	acknowledgment_status varchar(2),
	processing_status varchar(2),
	validation_error_message varchar(4000),
	obs_ind varchar(1),
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	created_by varchar(30) NOT NULL,
	version integer DEFAULT 1
);
ALTER TABLE xhb_cpp_staging_inbound ADD CONSTRAINT xhb_cpp_staging_inbound_pk PRIMARY KEY (cpp_staging_inbound_id);


DROP TABLE IF EXISTS xhb_hearing CASCADE;
CREATE TABLE xhb_hearing (
	hearing_id integer NOT NULL,
	case_id integer NOT NULL,
	ref_hearing_type_id integer NOT NULL,
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL,
	court_id integer NOT NULL,
	mp_hearing_type varchar(1),
	last_calculated_duration numeric(20),
	hearing_start_date timestamp,
	hearing_end_date timestamp,
	linked_hearing_id integer
);
ALTER TABLE xhb_hearing ADD CONSTRAINT xhb_hearing_pk PRIMARY KEY (hearing_id);
ALTER TABLE xhb_hearing ADD CONSTRAINT hearing_last_calc_duration_chk CHECK (last_calculated_duration BETWEEN 0 AND 59999940000);


DROP TABLE IF EXISTS xhb_hearing_list CASCADE;
CREATE TABLE xhb_hearing_list (
	list_id integer NOT NULL,
	list_type varchar(1),
	start_date timestamp,
	end_date timestamp,
	status varchar(5),
	edition_no smallint,
	published_time timestamp,
	print_reference varchar(255),
	crest_list_id integer NOT NULL,
	court_id integer NOT NULL,
	list_court_type varchar(2),
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL
);
ALTER TABLE xhb_hearing_list ADD CONSTRAINT xhb_hearing_list_pk PRIMARY KEY (list_id);


DROP TABLE IF EXISTS xhb_list CASCADE;
CREATE TABLE xhb_list (
	list_id integer NOT NULL,
	list_type_id integer NOT NULL,
	list_parent_id integer,
	court_id integer NOT NULL,
	draft_or_final varchar(1) NOT NULL,
	list_number smallint NOT NULL,
	list_start_date timestamp NOT NULL,
	list_end_date timestamp NOT NULL,
	publish_date timestamp,
	publish_status varchar(8),
	publish_error_reason varchar(200),
	obs_ind varchar(1),
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	creation_date timestamp NOT NULL,
	last_update_date timestamp NOT NULL,
	version bigint NOT NULL DEFAULT 1
);
ALTER TABLE xhb_list ADD CONSTRAINT xhb_list_pk PRIMARY KEY (list_id);
ALTER TABLE xhb_list ADD CONSTRAINT draft_or_final_chk CHECK (draft_or_final IN ('D','F'));
ALTER TABLE xhb_list ADD CONSTRAINT publish_status_chk CHECK (publish_status IN ('SUCCESS','FAILURE','DELETED'));


DROP TABLE IF EXISTS xhb_ref_court CASCADE;
CREATE TABLE xhb_ref_court (
	ref_court_id integer NOT NULL,
	court_full_name varchar(255),
	court_short_name varchar(5),
	name_prefix varchar(11),
	court_type varchar(1),
	crest_code varchar(4),
	obs_ind varchar(1),
	is_psd varchar(1),
	dx_ref varchar(35),
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL,
	address_id integer,
	court_id integer
);
ALTER TABLE xhb_ref_court ADD CONSTRAINT xhb_ref_court_pk PRIMARY KEY (ref_court_id);


DROP TABLE IF EXISTS xhb_ref_court_reporter CASCADE;
CREATE TABLE xhb_ref_court_reporter (
	ref_court_reporter_id integer NOT NULL,
	first_name varchar(35),
	middle_name varchar(35),
	surname varchar(35),
	crest_court_reporter_id numeric(38),
	initials varchar(4),
	report_method varchar(1),
	obs_ind varchar(1),
	ref_court_reporter_firm_id integer NOT NULL,
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL,
	court_id integer NOT NULL
);
ALTER TABLE xhb_ref_court_reporter ADD CONSTRAINT xhb_ref_court_reporter_pk PRIMARY KEY (ref_court_reporter_id);


DROP TABLE IF EXISTS xhb_ref_court_reporter_firm CASCADE;
CREATE TABLE xhb_ref_court_reporter_firm (
	ref_court_reporter_firm_id integer NOT NULL,
	obs_ind varchar(1),
	display_first varchar(1) NOT NULL,
	dx_ref varchar(35),
	vat_no varchar(35),
	firm_name varchar(35),
	address_id integer,
	court_id integer NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	created_by varchar(30) NOT NULL,
	creation_date timestamp NOT NULL,
	last_update_date timestamp NOT NULL,
	version integer NOT NULL,
	crest_court_reporter_firm_id integer
);
ALTER TABLE xhb_ref_court_reporter_firm ADD CONSTRAINT xhb_ref_court_reporter_firm_pk PRIMARY KEY (ref_court_reporter_firm_id);


DROP TABLE IF EXISTS xhb_ref_cracked_effective CASCADE;
CREATE TABLE xhb_ref_cracked_effective (
	ref_cracked_effective_id integer NOT NULL,
	code varchar(2) NOT NULL,
	description varchar(150),
	party_responsible varchar(1),
	obs_ind varchar(1),
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	created_by varchar(30) NOT NULL,
	version integer NOT NULL DEFAULT 1,
	trial_code_type varchar(1)
);
COMMENT ON COLUMN xhb_ref_cracked_effective.code IS E'The entry code for the cracked / ineffective entry.  Cannot be null';
COMMENT ON COLUMN xhb_ref_cracked_effective.created_by IS E'Standard field. Which user created this row.';
COMMENT ON COLUMN xhb_ref_cracked_effective.creation_date IS E'Standard field. The date this row was created.';
COMMENT ON COLUMN xhb_ref_cracked_effective.description IS E'The description of the cracked / ineffective entry.';
COMMENT ON COLUMN xhb_ref_cracked_effective.last_update_date IS E'Standard field. The date the last time this row was updated.';
COMMENT ON COLUMN xhb_ref_cracked_effective.last_updated_by IS E'Standard field. Which user last updated this row.';
COMMENT ON COLUMN xhb_ref_cracked_effective.obs_ind IS E'Obsolete indicator';
COMMENT ON COLUMN xhb_ref_cracked_effective.party_responsible IS E'The party responsible for the cracked codes.';
COMMENT ON COLUMN xhb_ref_cracked_effective.ref_cracked_effective_id IS E'PK. cannot be null';
COMMENT ON COLUMN xhb_ref_cracked_effective.version IS E'Standard field. Every time this row is changed the version will increment.';
ALTER TABLE xhb_ref_cracked_effective ADD CONSTRAINT xhb_ref_cracked_effective_pk PRIMARY KEY (ref_cracked_effective_id);


DROP TABLE IF EXISTS xhb_ref_hearing_type CASCADE;
CREATE TABLE xhb_ref_hearing_type (
	ref_hearing_type_id integer NOT NULL,
	hearing_type_code varchar(3) NOT NULL,
	hearing_type_desc varchar(72),
	category varchar(1),
	seq_no integer,
	list_sequence integer,
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL,
	court_id integer NOT NULL,
	obs_ind varchar(1)
);
ALTER TABLE xhb_ref_hearing_type ADD CONSTRAINT xhb_ref_hearing_type_pk PRIMARY KEY (ref_hearing_type_id);


DROP TABLE IF EXISTS xhb_ref_judge CASCADE;
CREATE TABLE xhb_ref_judge (
	ref_judge_id integer NOT NULL,
	judge_type varchar(255),
	crest_judge_id integer,
	title varchar(25),
	first_name varchar(35),
	middle_name varchar(35),
	surname varchar(35),
	full_list_title1 varchar(80),
	full_list_title2 varchar(80),
	full_list_title3 varchar(80),
	stats_code varchar(5),
	initials varchar(4),
	honours varchar(35),
	jud_vers varchar(2),
	obs_ind varchar(1),
	source_table varchar(30),
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL,
	court_id integer NOT NULL
);
ALTER TABLE xhb_ref_judge ADD CONSTRAINT xhb_ref_judge_pk PRIMARY KEY (ref_judge_id);


DROP TABLE IF EXISTS xhb_ref_justice CASCADE;
CREATE TABLE xhb_ref_justice (
	ref_justice_id integer NOT NULL,
	justice_name varchar(70),
	crest_justice_id integer,
	court_id integer NOT NULL,
	psd_court_code varchar(4),
	title varchar(25),
	initials varchar(4),
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL,
	obs_ind varchar(1)
);
ALTER TABLE xhb_ref_justice ADD CONSTRAINT xhb_ref_justice_pk PRIMARY KEY (ref_justice_id);


DROP TABLE IF EXISTS xhb_ref_listing_data CASCADE;
CREATE TABLE xhb_ref_listing_data (
	ref_listing_data_id integer NOT NULL,
	ref_data_type varchar(50) NOT NULL,
	ref_data_value varchar(100) NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	creation_date timestamp NOT NULL,
	last_update_date timestamp NOT NULL,
	obs_ind varchar(1),
	version integer NOT NULL DEFAULT 1
);
ALTER TABLE xhb_ref_listing_data ADD CONSTRAINT xhb_ref_listing_data_pk PRIMARY KEY (ref_listing_data_id);


DROP TABLE IF EXISTS xhb_ref_monitoring_category CASCADE;
CREATE TABLE xhb_ref_monitoring_category (
	ref_monitoring_category_id bigint NOT NULL,
	monitoring_category_code varchar(5) NOT NULL,
	monitoring_category_name varchar(255) NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	creation_date timestamp NOT NULL,
	last_update_date timestamp NOT NULL,
	version bigint DEFAULT 1
);
ALTER TABLE xhb_ref_monitoring_category ADD CONSTRAINT xhb_ref_monitoring_category_pk PRIMARY KEY (ref_monitoring_category_id);


DROP TABLE IF EXISTS xhb_ref_solicitor_firm CASCADE;
CREATE TABLE xhb_ref_solicitor_firm (
	ref_solicitor_firm_id integer NOT NULL,
	solicitor_firm_name varchar(35),
	crest_sof_id integer,
	court_id integer NOT NULL,
	obs_ind varchar(1),
	short_name varchar(28),
	dx_ref varchar(35),
	vat_no varchar(9),
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL,
	address_id integer,
	la_code varchar(6)
);
ALTER TABLE xhb_ref_solicitor_firm ADD CONSTRAINT xhb_ref_solicitor_firm_pk PRIMARY KEY (ref_solicitor_firm_id);


DROP TABLE IF EXISTS xhb_ref_system_code CASCADE;
CREATE TABLE xhb_ref_system_code (
	ref_system_code_id integer NOT NULL,
	code varchar(10) NOT NULL,
	code_type varchar(20) NOT NULL,
	code_title varchar(30),
	de_code varchar(150) NOT NULL,
	ref_code_order numeric(38),
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL,
	court_id integer NOT NULL,
	obs_ind varchar(1)
);
ALTER TABLE xhb_ref_system_code ADD CONSTRAINT xhb_ref_system_code_pk PRIMARY KEY (ref_system_code_id);


DROP TABLE IF EXISTS xhb_sched_hearing_attendee CASCADE;
CREATE TABLE xhb_sched_hearing_attendee (
	sh_attendee_id integer NOT NULL,
	attendee_type varchar(2) NOT NULL,
	scheduled_hearing_id integer NOT NULL,
	version integer NOT NULL,
	sh_staff_id integer,
	sh_justice_id integer,
	ref_judge_id integer,
	ref_court_reporter_id integer,
	last_updated_by varchar(30) NOT NULL,
	created_by varchar(30) NOT NULL,
	creation_date timestamp NOT NULL,
	last_update_date timestamp NOT NULL,
	ref_justice_id integer
);
ALTER TABLE xhb_sched_hearing_attendee ADD CONSTRAINT xhb_sched_hearing_attendee_pk PRIMARY KEY (sh_attendee_id);


DROP TABLE IF EXISTS xhb_sched_hearing_defendant CASCADE;
CREATE TABLE xhb_sched_hearing_defendant (
	sched_hear_def_id integer NOT NULL,
	scheduled_hearing_id integer NOT NULL,
	defendant_on_case_id integer,
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL
);
ALTER TABLE xhb_sched_hearing_defendant ADD CONSTRAINT xhb_sched_hearing_defendant_pk PRIMARY KEY (sched_hear_def_id);


DROP TABLE IF EXISTS xhb_scheduled_hearing CASCADE;
CREATE TABLE xhb_scheduled_hearing (
	scheduled_hearing_id integer NOT NULL,
	sequence_no smallint,
	not_before_time timestamp,
	original_time timestamp,
	listing_note varchar(78),
	hearing_progress smallint,
	sitting_id integer NOT NULL,
	hearing_id integer NOT NULL,
	moved_from varchar(255),
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL,
	linked_sh_id integer,
	end_time timestamp,
	start_time timestamp,
	date_of_hearing timestamp,
	is_case_active varchar(1) NOT NULL,
	moved_from_court_room_id integer,
	add_hearing_used varchar(1),
	ref_cracked_effective_id integer
);
ALTER TABLE xhb_scheduled_hearing ADD CONSTRAINT xhb_scheduled_hearing_pk PRIMARY KEY (scheduled_hearing_id);


DROP TABLE IF EXISTS xhb_sh_judge CASCADE;
CREATE TABLE xhb_sh_judge (
	sh_judge_id integer NOT NULL,
	deputy_hcj varchar(1),
	ref_judge_id integer NOT NULL,
	version integer NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	created_by varchar(30) NOT NULL,
	creation_date timestamp NOT NULL,
	last_update_date timestamp NOT NULL,
	sh_attendee_id integer NOT NULL
);
ALTER TABLE xhb_sh_judge ADD CONSTRAINT xhb_sh_judge_pk PRIMARY KEY (sh_judge_id);


DROP TABLE IF EXISTS xhb_sh_justice CASCADE;
CREATE TABLE xhb_sh_justice (
	sh_justice_id integer NOT NULL,
	justice_name varchar(50),
	version integer NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	created_by varchar(30) NOT NULL,
	creation_date timestamp NOT NULL,
	last_update_date timestamp NOT NULL,
	hearing_id integer
);
ALTER TABLE xhb_sh_justice ADD CONSTRAINT xhb_sh_justice_pk PRIMARY KEY (sh_justice_id);


DROP TABLE IF EXISTS xhb_sh_staff CASCADE;
CREATE TABLE xhb_sh_staff (
	sh_staff_id integer NOT NULL,
	staff_role varchar(2) NOT NULL,
	staff_name varchar(255) NOT NULL,
	version integer NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	created_by varchar(30) NOT NULL,
	creation_date timestamp NOT NULL,
	last_update_date timestamp NOT NULL
);
ALTER TABLE xhb_sh_staff ADD CONSTRAINT xhb_sh_staff_pk PRIMARY KEY (sh_staff_id);


DROP TABLE IF EXISTS xhb_sitting CASCADE;
CREATE TABLE xhb_sitting (
	sitting_id integer NOT NULL,
	sitting_sequence_no smallint,
	is_sitting_judge varchar(1),
	sitting_time timestamp,
	sitting_note varchar(80),
	ref_justice1_id numeric(38),
	ref_justice2_id numeric(38),
	ref_justice3_id numeric(38),
	ref_justice4_id numeric(38),
	is_floating varchar(1) NOT NULL DEFAULT 0,
	list_id integer,
	ref_judge_id integer,
	court_room_id integer NOT NULL,
	court_site_id integer NOT NULL,
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL,
	justicename4 varchar(35),
	justicename3 varchar(35),
	justicename2 varchar(35),
	justicename1 varchar(35)
);
ALTER TABLE xhb_sitting ADD CONSTRAINT xhb_sitting_pk PRIMARY KEY (sitting_id);


DROP TABLE IF EXISTS xhb_sitting_on_list CASCADE;
CREATE TABLE xhb_sitting_on_list (
	sitting_on_list_id integer NOT NULL,
	sitting_number smallint NOT NULL,
	list_id integer NOT NULL,
	time_marking_id integer,
	time_listed timestamp,
	judge_ref_id integer,
	jp1 varchar(35),
	jp2 varchar(35),
	jp3 varchar(35),
	jp4 varchar(35),
	list_note_pre_defined_id integer,
	list_note_text varchar(200),
	pre_def_note_classification_id integer,
	free_text_note_class_id integer,
	obs_ind varchar(1),
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	created_by varchar(30) NOT NULL,
	version integer NOT NULL DEFAULT 1,
	court_room_id integer NOT NULL,
	court_site_id integer NOT NULL
);
ALTER TABLE xhb_sitting_on_list ADD CONSTRAINT xhb_sitting_on_list_pk PRIMARY KEY (sitting_on_list_id);


DROP TABLE IF EXISTS xhb_sys_audit CASCADE;
CREATE TABLE xhb_sys_audit (
	sys_audit_id bigint NOT NULL,
	table_to_audit varchar(255) NOT NULL,
	audit_table varchar(255) NOT NULL,
	auditable varchar(1) NOT NULL
);
ALTER TABLE xhb_sys_audit ADD CONSTRAINT xhb_sys_audit_pk PRIMARY KEY (sys_audit_id);


DROP TABLE IF EXISTS xhb_sys_user_information CASCADE;
CREATE TABLE xhb_sys_user_information (
	mercator_user_name varchar(255) NOT NULL,
	connection_pool_user_name varchar(255) NOT NULL
);
ALTER TABLE xhb_sys_user_information ADD CONSTRAINT xhb_sys_user_information_pk PRIMARY KEY (mercator_user_name,connection_pool_user_name);


DROP TABLE IF EXISTS xhb_clob CASCADE;
CREATE TABLE xhb_clob (
	clob_id bigint NOT NULL,
	clob_data text NOT NULL,
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL
);
ALTER TABLE xhb_clob ADD CONSTRAINT xhb_clob_pk PRIMARY KEY (clob_id);
ALTER TABLE xhb_clob ADD CONSTRAINT clob_pk_range_chk CHECK (clob_id BETWEEN -2147483648 AND 2147483647);


DROP TABLE IF EXISTS xhb_formatting CASCADE;
CREATE TABLE xhb_formatting (
	formatting_id integer NOT NULL,
	date_in timestamp NOT NULL,
	format_status varchar(2),
	distribution_type varchar(5) NOT NULL,
	mime_type varchar(3) NOT NULL,
	document_type varchar(3) NOT NULL,
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL,
	court_id integer NOT NULL,
	formatted_document_blob_id bigint,
	xml_document_clob_id bigint NOT NULL,
	language varchar(2),
	country varchar(2),
	major_schema_version smallint,
	minor_schema_version smallint
);
ALTER TABLE xhb_formatting ADD CONSTRAINT xhb_formatting_pk PRIMARY KEY (formatting_id);


DROP TABLE IF EXISTS xhb_blob CASCADE;
CREATE TABLE xhb_blob (
	blob_id bigint NOT NULL,
	blob_data bytea NOT NULL,
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL
);
ALTER TABLE xhb_blob ADD CONSTRAINT xhb_blob_pk PRIMARY KEY (blob_id);
ALTER TABLE xhb_blob ADD CONSTRAINT blob_pk_range_chk CHECK (blob_id BETWEEN -2147483648 AND 2147483647);


DROP TABLE IF EXISTS mtbl_case_history CASCADE;
CREATE TABLE mtbl_case_history (
	case_no integer NOT NULL,
	case_type varchar(1) NOT NULL,
	court_id integer NOT NULL,
	last_update_date timestamp
);
ALTER TABLE mtbl_case_history ADD CONSTRAINT mtbl_case_history_pk PRIMARY KEY (court_id,case_type,case_no);


DROP TABLE IF EXISTS xhb_hk_results CASCADE;
CREATE TABLE xhb_hk_results (
	hk_run_id bigint NOT NULL,
	run_type varchar(1),
	run_start_date timestamp,
	run_end_date timestamp,
	case_start_date timestamp,
	case_end_date timestamp,
	list_start_date timestamp,
	list_end_date timestamp,
	case_status varchar(1),
	case_error_message varchar(2000),
	list_status varchar(1),
	list_error_message varchar(2000),
	cases_deleted integer,
	lists_deleted integer,
	cases_error integer,
	error_message varchar(2000),
	case_limit bigint,
	running_list bigint,
	warned_list bigint,
	firm_list bigint,
	daily_list bigint
);
ALTER TABLE xhb_hk_results ADD CONSTRAINT xhb_hk_results_pk PRIMARY KEY (hk_run_id);


DROP TABLE IF EXISTS xhb_hk_cpp_results CASCADE;
CREATE TABLE xhb_hk_cpp_results (
	hk_cpp_run_id bigint NOT NULL,
	run_start_date timestamp,
	run_end_date timestamp,
	list_status varchar(1),
	list_error_message varchar(2000),
	list_error integer,
	list_deleted integer,
	iwp_status varchar(1),
	iwp_error_message varchar(2000),
	iwp_error integer,
	iwp_deleted integer,
	pd_status varchar(1),
	pd_error_message varchar(2000),
	pd_error integer,
	pd_deleted integer,
	staging_status varchar(1),
	staging_error_message varchar(2000),
	staging_error integer,
	staging_deleted integer
);
ALTER TABLE xhb_hk_cpp_results ADD CONSTRAINT xhb_hk_cpp_results_pk PRIMARY KEY (hk_cpp_run_id);


DROP TABLE IF EXISTS xhb_hk_error_log CASCADE;
CREATE TABLE xhb_hk_error_log (
	hk_run_id bigint NOT NULL,
	case_no integer,
	case_type varchar(1),
	court_id integer,
	case_id integer,
	error_message varchar(500)
);
ALTER TABLE xhb_hk_error_log ADD CONSTRAINT xhb_hk_error_log_pk PRIMARY KEY (hk_run_id);


DROP TABLE IF EXISTS xhb_hk_cpp_error_log CASCADE;
CREATE TABLE xhb_hk_cpp_error_log (
	hk_cpp_run_id bigint NOT NULL,
	error_message varchar(500)
);
ALTER TABLE xhb_hk_cpp_error_log ADD CONSTRAINT xhb_hk_cpp_error_log_pk PRIMARY KEY (hk_cpp_run_id);



DROP TABLE IF EXISTS xhb_pdda_message CASCADE;
CREATE TABLE xhb_pdda_message (
	pdda_message_id integer NOT NULL,
	court_id integer NOT NULL,
	court_room_id integer,
	pdda_message_guid varchar(36),
	pdda_message_type_id integer,
	pdda_message_data_id bigint,
	pdda_batch_id integer,
	time_sent timestamp,
  cp_document_name varchar(50),
	cp_document_status varchar(8),
	cp_response_generated varchar(1),
	cpp_staging_inbound_id integer,
	error_message varchar(4000),
	obs_ind varchar(1),
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL DEFAULT 1
);
ALTER TABLE xhb_pdda_message ADD CONSTRAINT xhb_pdda_message_pk PRIMARY KEY (pdda_message_id);


DROP TABLE IF EXISTS xhb_ref_pdda_message_type CASCADE;
CREATE TABLE xhb_ref_pdda_message_type (
	pdda_message_type_id integer NOT NULL,
	pdda_message_type varchar(20) NOT NULL,
	pdda_message_description varchar(255),
	obs_ind varchar(1),
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL DEFAULT 1
);
ALTER TABLE xhb_ref_pdda_message_type ADD CONSTRAINT xhb_ref_pdda_message_type_pk PRIMARY KEY (pdda_message_type_id);


DROP TABLE IF EXISTS xhb_case_reference CASCADE;
CREATE TABLE xhb_case_reference (
	case_reference_id integer NOT NULL,
	reporting_restrictions smallint NOT NULL DEFAULT 0,
	case_id integer,
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL
);
ALTER TABLE xhb_case_reference ADD CONSTRAINT xhb_case_reference_pk PRIMARY KEY (case_reference_id);


DROP TABLE IF EXISTS xhb_configured_public_notice CASCADE;
CREATE TABLE xhb_configured_public_notice (
	configured_public_notice_id integer NOT NULL,
	is_active varchar(1),
	court_room_id integer NOT NULL,
	public_notice_id integer NOT NULL,
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL
);
ALTER TABLE xhb_configured_public_notice ADD CONSTRAINT xhb_configured_public_notice_pk PRIMARY KEY (configured_public_notice_id);


DROP TABLE IF EXISTS xhb_court_log_entry CASCADE;
CREATE TABLE xhb_court_log_entry (
	entry_id integer NOT NULL,
	case_id integer NOT NULL,
	version integer NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	created_by varchar(30) NOT NULL,
	creation_date timestamp NOT NULL,
	last_update_date timestamp NOT NULL,
	date_time timestamp NOT NULL,
	event_desc_id integer NOT NULL,
	log_entry_xml text NOT NULL,
	defendant_on_case_id integer,
	defendant_on_offence_id integer,
	scheduled_hearing_id integer
);
ALTER TABLE xhb_court_log_entry ADD CONSTRAINT xhb_court_log_entry_pk PRIMARY KEY (entry_id);


DROP TABLE IF EXISTS xhb_court_log_event_desc CASCADE;
CREATE TABLE xhb_court_log_event_desc (
	event_desc_id integer NOT NULL,
	flagged_event smallint NOT NULL,
	editable smallint NOT NULL,
	send_to_mercator smallint NOT NULL,
	update_linked_cases smallint NOT NULL,
	publish_to_subscribers smallint NOT NULL,
	clear_public_displays smallint NOT NULL,
	e_inform smallint NOT NULL,
	public_display smallint NOT NULL,
	linked_case_text varchar(100) NOT NULL,
	event_description varchar(100) NOT NULL,
	version integer NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	event_type integer NOT NULL,
	created_by varchar(30) NOT NULL,
	creation_date timestamp NOT NULL,
	last_update_date timestamp NOT NULL,
	public_notice smallint NOT NULL,
	short_description varchar(255) NOT NULL
);
ALTER TABLE xhb_court_log_event_desc ADD CONSTRAINT xhb_court_log_event_desc_pk PRIMARY KEY (event_desc_id);


DROP TABLE IF EXISTS xhb_cr_live_display CASCADE;
CREATE TABLE xhb_cr_live_display (
	cr_live_display_id integer NOT NULL,
	court_room_id integer NOT NULL,
	scheduled_hearing_id integer,
	time_status_set timestamp NOT NULL,
	status varchar(4000),
	created_by varchar(30) NOT NULL,
	creation_date timestamp NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	last_update_date timestamp NOT NULL,
	version integer NOT NULL
);
ALTER TABLE xhb_cr_live_display ADD CONSTRAINT xhb_cr_live_display_pk PRIMARY KEY (cr_live_display_id);


DROP TABLE IF EXISTS xhb_defendant CASCADE;
CREATE TABLE xhb_defendant (
	defendant_id integer NOT NULL,
	crest_defendant_id integer,
	first_name varchar(255),
	middle_name varchar(35),
	surname varchar(35),
	initials varchar(4),
	date_of_birth timestamp,
	gender smallint,
	last_conviction_date timestamp,
	is_company varchar(1),
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL,
	address_id integer,
	court_id integer NOT NULL,
	prison_id varchar(4),
	public_display_hide varchar(1),
	parent_guardian_name varchar(35),
	ethnic_appearance_code varchar(10),
	ethnicity_self_defined varchar(10),
	current_prison_status varchar(1) DEFAULT 'N'
);
ALTER TABLE xhb_defendant ADD CONSTRAINT xhb_defendant_pk PRIMARY KEY (defendant_id);


DROP TABLE IF EXISTS xhb_defendant_on_case CASCADE;
CREATE TABLE xhb_defendant_on_case (
	defendant_on_case_id integer NOT NULL,
	no_of_tics smallint,
	final_driving_licence_status smallint,
	ptiurn varchar(50),
	is_juvenile varchar(1),
	is_masked varchar(1),
	masked_name varchar(255),
	case_id integer NOT NULL,
	defendant_id integer NOT NULL,
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL,
	obs_ind varchar(1),
	results_verified varchar(1),
	defendant_number integer,
	date_of_committal timestamp,
	pnc_id varchar(10),
	collect_magistrate_court_id integer,
	current_bc_status varchar(1),
	asn varchar(20),
	bench_warrant_exec_date timestamp,
	comm_bc_status varchar(1),
	bc_status_bw_executed varchar(1),
	special_cir_found varchar(1),
	date_exported timestamp,
	custodial varchar(1),
	suspended varchar(1),
	serious_drug_offence varchar(1),
	recommended_deportation varchar(1),
	nationality varchar(3),
	first_fixed_trial timestamp,
	first_hearing_type varchar(10),
	public_display_hide varchar(1),
	amended_date_exported timestamp,
	amended_reason varchar(100),
	hate_ind varchar(1),
	hate_type varchar(2),
	hate_sent_ind varchar(1),
	driving_disq_suspended_date timestamp,
	mag_court_first_hearing_date timestamp,
	mag_court_final_hearing_date timestamp,
	custody_time_limit timestamp,
	form_ng_sent_date timestamp,
	cacd_appeal_result_date timestamp,
	section28_name1 varchar(50),
	section28_name2 varchar(50),
	section28_phone1 varchar(15),
	section28_phone2 varchar(15),
	difference_report varchar(1),
	date_rcpt_notice_appeal timestamp,
	cacd_appeal_result varchar(240),
	coa_status varchar(1),
	ctl_applies char(1),
	dar_retention_policy_id integer
);
ALTER TABLE xhb_defendant_on_case ADD CONSTRAINT xhb_defendant_on_case_pk PRIMARY KEY (defendant_on_case_id);


DROP TABLE IF EXISTS xhb_definitive_public_notice CASCADE;
CREATE TABLE xhb_definitive_public_notice (
	definitive_pn_id integer NOT NULL,
	definitive_pn_desc varchar(255) NOT NULL,
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL,
	priority integer DEFAULT 1
);
ALTER TABLE xhb_definitive_public_notice ADD CONSTRAINT xhb_definitive_public_notice_pk PRIMARY KEY (definitive_pn_id);


DROP TABLE IF EXISTS xhb_display CASCADE;
CREATE TABLE xhb_display (
	display_id bigint NOT NULL,
	display_type_id bigint NOT NULL,
	display_location_id bigint NOT NULL,
	rotation_set_id bigint NOT NULL,
	description_code varchar(30) NOT NULL,
	locale varchar(30) NOT NULL,
	created_by varchar(30) NOT NULL,
	creation_date timestamp NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	last_update_date timestamp NOT NULL,
	version integer NOT NULL,
	show_unassigned_yn char(1) NOT NULL
);
ALTER TABLE xhb_display ADD CONSTRAINT xhb_display_pk PRIMARY KEY (display_id);
ALTER TABLE xhb_display ADD CONSTRAINT display_lower_under_chk CHECK (COALESCE(RTRIM(description_code, '0123465789abcdefghijklmnopqrstuvwxyz_'),'') = '');


DROP TABLE IF EXISTS xhb_display_court_room CASCADE;
CREATE TABLE xhb_display_court_room (
	display_id bigint NOT NULL,
	court_room_id bigint NOT NULL,
	created_by varchar(30) NOT NULL,
	creation_date timestamp NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	last_update_date timestamp NOT NULL,
	version integer NOT NULL
);
ALTER TABLE xhb_display_court_room ADD CONSTRAINT xhb_display_court_room_pk PRIMARY KEY (display_id,court_room_id);


DROP TABLE IF EXISTS xhb_display_document CASCADE;
CREATE TABLE xhb_display_document (
	display_document_id bigint NOT NULL,
	description_code varchar(30) NOT NULL,
	default_page_delay bigint NOT NULL,
	multiple_court_yn char(1) NOT NULL,
	created_by varchar(30) NOT NULL,
	creation_date timestamp NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	last_update_date timestamp NOT NULL,
	version integer NOT NULL,
	country varchar(2),
	language varchar(2)
);
ALTER TABLE xhb_display_document ADD CONSTRAINT xhb_display_document_pk PRIMARY KEY (display_document_id);


DROP TABLE IF EXISTS xhb_display_location CASCADE;
CREATE TABLE xhb_display_location (
	display_location_id bigint NOT NULL,
	description_code varchar(30) NOT NULL,
	court_site_id integer NOT NULL,
	created_by varchar(30) NOT NULL,
	creation_date timestamp NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	last_update_date timestamp NOT NULL,
	version integer NOT NULL
);
ALTER TABLE xhb_display_location ADD CONSTRAINT xhb_display_location_pk PRIMARY KEY (display_location_id);
ALTER TABLE xhb_display_location ADD CONSTRAINT display_loc_lower_under_chk CHECK (COALESCE(RTRIM(description_code, '0123465789abcdefghijklmnopqrstuvwxyz_'),'') = '');


DROP TABLE IF EXISTS xhb_display_type CASCADE;
CREATE TABLE xhb_display_type (
	display_type_id bigint NOT NULL,
	description_code varchar(30) NOT NULL,
	created_by varchar(30) NOT NULL,
	creation_date timestamp NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	last_update_date timestamp NOT NULL,
	version integer NOT NULL
);
ALTER TABLE xhb_display_type ADD CONSTRAINT xhb_display_type_pk PRIMARY KEY (display_type_id);


DROP TABLE IF EXISTS xhb_public_notice CASCADE;
CREATE TABLE xhb_public_notice (
	public_notice_id integer NOT NULL,
	public_notice_desc varchar(255),
	court_id integer NOT NULL,
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL,
	definitive_pn_id integer NOT NULL
);
ALTER TABLE xhb_public_notice ADD CONSTRAINT xhb_public_notice_pk PRIMARY KEY (public_notice_id);


DROP TABLE IF EXISTS xhb_rotation_set_dd CASCADE;
CREATE TABLE xhb_rotation_set_dd (
	rotation_set_dd_id bigint NOT NULL,
	rotation_set_id bigint NOT NULL,
	display_document_id bigint NOT NULL,
	page_delay bigint NOT NULL,
	ordering integer NOT NULL,
	created_by varchar(30) NOT NULL,
	creation_date timestamp NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	last_update_date timestamp NOT NULL,
	version integer NOT NULL
);
ALTER TABLE xhb_rotation_set_dd ADD CONSTRAINT xhb_rotation_set_dd_pk PRIMARY KEY (rotation_set_dd_id);


DROP TABLE IF EXISTS xhb_rotation_sets CASCADE;
CREATE TABLE xhb_rotation_sets (
	rotation_set_id bigint NOT NULL,
	court_id bigint NOT NULL,
	description varchar(255) NOT NULL,
	default_yn char(1) NOT NULL,
	created_by varchar(30) NOT NULL,
	creation_date timestamp NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	last_update_date timestamp NOT NULL,
	version integer NOT NULL
);
ALTER TABLE xhb_rotation_sets ADD CONSTRAINT xhb_rotation_sets_pk PRIMARY KEY (rotation_set_id);


DROP TABLE IF EXISTS xhb_pdda_dl_notifier CASCADE;
CREATE TABLE xhb_pdda_dl_notifier (
	pdda_dl_notifier_id bigint NOT NULL,
	court_id bigint NOT NULL,
	last_run_date timestamp,
	status varchar(20) NOT NULL,
	error_message varchar(255),
	obs_ind varchar(1),
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL
);
ALTER TABLE xhb_pdda_dl_notifier ADD CONSTRAINT xhb_pdda_dl_notifier_pk PRIMARY KEY (pdda_dl_notifier_id);


DROP TABLE IF EXISTS xhb_display_store CASCADE;
CREATE TABLE xhb_display_store (
	display_store_id bigint NOT NULL,
	retrieval_code text NOT NULL,
	content text,
	obs_ind varchar(1),
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL DEFAULT 1
);
ALTER TABLE xhb_display_store ADD CONSTRAINT xhb_display_store_pk PRIMARY KEY (display_store_id);

DROP TABLE IF EXISTS xhb_xml_document CASCADE;
CREATE TABLE xhb_xml_document (
	xml_document_id bigint NOT NULL,
    date_created timestamp,
	document_title varchar(255),
	xml_document_clob_id bigint,
	status varchar(2),
	document_type varchar(3),
	expiry_date timestamp,
	court_id integer NOT NULL,
	created_by varchar(30) NOT NULL,
	creation_date timestamp NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	last_update_date timestamp NOT NULL,
	version integer NOT NULL
);
ALTER TABLE xhb_xml_document ADD CONSTRAINT xhb_xml_document_pk PRIMARY KEY (xml_document_id);
