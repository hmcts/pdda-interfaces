SET client_encoding TO 'UTF8';


DROP TABLE IF EXISTS aud_blob CASCADE;
CREATE TABLE aud_blob (
	blob_id bigint,
	blob_data bytea NOT NULL,
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL,
	insert_event varchar(1) NOT NULL
);
ALTER TABLE aud_blob ADD CONSTRAINT aud_blob_pk PRIMARY KEY (blob_id, last_update_date);


DROP TABLE IF EXISTS aud_case CASCADE;
CREATE TABLE aud_case (
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
	insert_event varchar(1) NOT NULL DEFAULT 'X',
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
	video_link_required varchar(1) NOT NULL DEFAULT 'N',
	cracked_ineffective_id integer,
	default_hearing_type integer,
	section28_name1 varchar(50),
	section28_name2 varchar(50),
	section28_phone1 varchar(15),
	section28_phone2 varchar(15),
	case_group_number integer,
	date_ctl_reminder_printed timestamp,
	pub_running_list_id integer,
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
ALTER TABLE aud_case ADD CONSTRAINT aud_case_pk PRIMARY KEY (case_id, last_update_date);


DROP TABLE IF EXISTS aud_case_diary_fixture CASCADE;
CREATE TABLE aud_case_diary_fixture (
	case_diary_fixture_id integer NOT NULL,
	case_listing_entry_id integer NOT NULL,
	listing_date timestamp NOT NULL,
	fixture_notice_required varchar(1) NOT NULL,
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
	insert_event varchar(1),
	court_site_id integer,
	date_vacated timestamp
);
ALTER TABLE aud_case_diary_fixture ADD CONSTRAINT aud_case_diary_fixture_pk PRIMARY KEY (case_diary_fixture_id, last_update_date);


DROP TABLE IF EXISTS aud_case_listing_entry CASCADE;
CREATE TABLE aud_case_listing_entry (
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
	insert_event varchar(1),
	obs_ind varchar(1),
	court_id integer NOT NULL
);
ALTER TABLE aud_case_listing_entry ADD CONSTRAINT aud_case_listing_entry_pk PRIMARY KEY (case_listing_entry_id, last_update_date);


DROP TABLE IF EXISTS aud_case_on_list CASCADE;
CREATE TABLE aud_case_on_list (
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
	insert_event varchar(1) NOT NULL,
	seq_no smallint NOT NULL,
	case_diary_fixture_id integer,
	date_of_removal timestamp,
	list_note_predefined_id integer,
	list_note_text varchar(200),
	parent_case_on_list_id integer,
	nha_firm_list varchar(1),
	vacation_pre_defined_rson_id integer
);
ALTER TABLE aud_case_on_list ADD CONSTRAINT aud_case_on_list_pk PRIMARY KEY (case_on_list_id, last_update_date);


DROP TABLE IF EXISTS aud_cc_info CASCADE;
CREATE TABLE aud_cc_info (
	cc_info_id integer NOT NULL,
	cc_info_text varchar(255),
	version integer NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	created_by varchar(30) NOT NULL,
	creation_date timestamp NOT NULL,
	last_update_date timestamp NOT NULL,
	insert_event varchar(1) NOT NULL
);
ALTER TABLE aud_cc_info ADD CONSTRAINT aud_cc_info_pk PRIMARY KEY (cc_info_id, last_update_date);



DROP TABLE IF EXISTS aud_clob CASCADE;
CREATE TABLE aud_clob (
	clob_id bigint,
	clob_data text NOT NULL,
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL,
	insert_event varchar(1) NOT NULL
);
ALTER TABLE aud_clob ADD CONSTRAINT aud_clob_pk PRIMARY KEY (clob_id, last_update_date);



DROP TABLE IF EXISTS aud_court CASCADE;
CREATE TABLE aud_court (
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
	in_service_flag varchar(1) NOT NULL,
	obs_ind varchar(1),
	probation_office_name varchar(50) NOT NULL,
	internet_court_name varchar(255) NOT NULL,
	display_name varchar(100),
	court_code char(4),
	insert_event varchar(1) NOT NULL,
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
ALTER TABLE aud_court ADD CONSTRAINT aud_court_pk PRIMARY KEY (court_id, last_update_date);



DROP TABLE IF EXISTS aud_court_room CASCADE;
CREATE TABLE aud_court_room (
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
	insert_event varchar(1) NOT NULL,
	security_ind varchar(1),
	video_ind varchar(1)
);
ALTER TABLE aud_court_room ADD CONSTRAINT aud_court_room_pk PRIMARY KEY (court_room_id, last_update_date);


DROP TABLE IF EXISTS aud_court_satellite CASCADE;
CREATE TABLE aud_court_satellite (
	court_satellite_id integer NOT NULL,
	court_site_id integer NOT NULL,
	internet_satellite_name varchar(255) NOT NULL,
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL,
	insert_event varchar(1) NOT NULL,
	obs_ind varchar(1) DEFAULT 'N'
);
ALTER TABLE aud_court_satellite ADD CONSTRAINT aud_court_satellite_pk PRIMARY KEY (court_satellite_id, last_update_date);



DROP TABLE IF EXISTS aud_court_site CASCADE;
CREATE TABLE aud_court_site (
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
	insert_event varchar(1) NOT NULL DEFAULT 'X',
	site_group integer,
	floater_text varchar(100),
	list_name varchar(39),
	tier varchar(1)
);
ALTER TABLE aud_court_site ADD CONSTRAINT aud_court_site_pk PRIMARY KEY (court_site_id, last_update_date);



DROP TABLE IF EXISTS aud_cpp_formatting CASCADE;
CREATE TABLE aud_cpp_formatting (
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
	created_by varchar(30) NOT NULL,
	insert_event varchar(1)
);
ALTER TABLE aud_cpp_formatting ADD CONSTRAINT aud_cpp_formatting_pk PRIMARY KEY (cpp_formatting_id, last_update_date);


DROP TABLE IF EXISTS aud_cpp_formatting_merge CASCADE;
CREATE TABLE aud_cpp_formatting_merge (
	cpp_formatting_merge_id integer NOT NULL,
	cpp_formatting_id integer NOT NULL,
	formatting_id integer NOT NULL,
	xhibit_clob_id bigint,
	court_id integer NOT NULL,
	creation_date timestamp NOT NULL,
	language varchar(2),
	obs_ind varchar(1),
	version integer DEFAULT 1,
	last_update_date timestamp NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	created_by varchar(30) NOT NULL,
	insert_event varchar(1)
);
ALTER TABLE aud_cpp_formatting_merge ADD CONSTRAINT aud_cpp_formatting_merge_pk PRIMARY KEY (cpp_formatting_merge_id, last_update_date);



DROP TABLE IF EXISTS aud_cpp_list CASCADE;
CREATE TABLE aud_cpp_list (
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
	version integer NOT NULL DEFAULT 1,
	insert_event varchar(1)
);
ALTER TABLE aud_cpp_list ADD CONSTRAINT aud_cpp_list_pk PRIMARY KEY (cpp_list_id, last_update_date);



DROP TABLE IF EXISTS aud_cpp_staging_inbound CASCADE;
CREATE TABLE aud_cpp_staging_inbound (
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
	version integer DEFAULT 1,
	insert_event varchar(1)
);
ALTER TABLE aud_cpp_staging_inbound ADD CONSTRAINT aud_cpp_staging_inbound_pk PRIMARY KEY (cpp_staging_inbound_id, last_update_date);



DROP TABLE IF EXISTS aud_formatting CASCADE;
CREATE TABLE aud_formatting (
	formatting_id integer NOT NULL,
	date_in timestamp NOT NULL,
	xml_document text,
	formatted_document bytea,
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
	insert_event varchar(1) NOT NULL,
	formatted_document_blob_id bigint,
	xml_document_clob_id bigint,
	language varchar(2),
	country varchar(2),
	major_schema_version smallint,
	minor_schema_version smallint
);
ALTER TABLE aud_formatting ADD CONSTRAINT aud_formatting_pk PRIMARY KEY (formatting_id, last_update_date);



DROP TABLE IF EXISTS aud_hearing CASCADE;
CREATE TABLE aud_hearing (
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
	linked_hearing_id integer,
	insert_event varchar(1) NOT NULL
);
ALTER TABLE aud_hearing ADD CONSTRAINT aud_hearing_pk PRIMARY KEY (hearing_id, last_update_date);



DROP TABLE IF EXISTS aud_hearing_list CASCADE;
CREATE TABLE aud_hearing_list (
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
	version integer NOT NULL,
	insert_event varchar(1) NOT NULL
);
ALTER TABLE aud_hearing_list ADD CONSTRAINT aud_hearing_list_pk PRIMARY KEY (list_id, last_update_date);



DROP TABLE IF EXISTS aud_list CASCADE;
CREATE TABLE aud_list (
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
	version bigint NOT NULL DEFAULT 1,
	insert_event varchar(1)
);
ALTER TABLE aud_list ADD CONSTRAINT aud_list_pk PRIMARY KEY (list_id, last_update_date);



DROP TABLE IF EXISTS aud_ref_cracked_effective CASCADE;
CREATE TABLE aud_ref_cracked_effective (
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
	insert_event varchar(1),
	trial_code_type varchar(1)
);
COMMENT ON COLUMN aud_ref_cracked_effective.code IS E'The entry code for the cracked / ineffective entry.  Cannot be null';
COMMENT ON COLUMN aud_ref_cracked_effective.created_by IS E'Standard field. Which user created this row.';
COMMENT ON COLUMN aud_ref_cracked_effective.creation_date IS E'Standard field. The date this row was created.';
COMMENT ON COLUMN aud_ref_cracked_effective.description IS E'The description of the cracked / ineffective entry.';
COMMENT ON COLUMN aud_ref_cracked_effective.last_update_date IS E'Standard field. The date the last time this row was updated.';
COMMENT ON COLUMN aud_ref_cracked_effective.last_updated_by IS E'Standard field. Which user last updated this row.';
COMMENT ON COLUMN aud_ref_cracked_effective.obs_ind IS E'Obsolete indicator';
COMMENT ON COLUMN aud_ref_cracked_effective.party_responsible IS E'The party responsible for the cracked codes.';
COMMENT ON COLUMN aud_ref_cracked_effective.ref_cracked_effective_id IS E'PK. cannot be null';
COMMENT ON COLUMN aud_ref_cracked_effective.version IS E'Standard field. Every time this row is changed the version will increment.';
ALTER TABLE aud_ref_cracked_effective ADD CONSTRAINT aud_ref_cracked_effective_pk PRIMARY KEY (ref_cracked_effective_id, last_update_date);



DROP TABLE IF EXISTS aud_ref_hearing_type CASCADE;
CREATE TABLE aud_ref_hearing_type (
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
	obs_ind varchar(1),
	insert_event varchar(1) NOT NULL
);
ALTER TABLE aud_ref_hearing_type ADD CONSTRAINT aud_ref_hearing_type_pk PRIMARY KEY (ref_hearing_type_id, last_update_date);



DROP TABLE IF EXISTS aud_ref_judge CASCADE;
CREATE TABLE aud_ref_judge (
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
	court_id integer NOT NULL,
	insert_event varchar(1) NOT NULL
);
ALTER TABLE aud_ref_judge ADD CONSTRAINT aud_ref_judge_pk PRIMARY KEY (ref_judge_id, last_update_date);


DROP TABLE IF EXISTS aud_ref_justice CASCADE;
CREATE TABLE aud_ref_justice (
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
	obs_ind varchar(1),
	insert_event varchar(1) NOT NULL
);
ALTER TABLE aud_ref_justice ADD CONSTRAINT aud_ref_justice_pk PRIMARY KEY (ref_justice_id, last_update_date);



DROP TABLE IF EXISTS aud_ref_listing_data CASCADE;
CREATE TABLE aud_ref_listing_data (
	ref_listing_data_id integer NOT NULL,
	ref_data_type varchar(50) NOT NULL,
	ref_data_value varchar(100) NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	creation_date timestamp NOT NULL,
	last_update_date timestamp NOT NULL,
	insert_event varchar(1),
	obs_ind varchar(1),
	version integer NOT NULL DEFAULT 1
);
ALTER TABLE aud_ref_listing_data ADD CONSTRAINT aud_ref_listing_data_pk PRIMARY KEY (ref_listing_data_id, last_update_date);



DROP TABLE IF EXISTS aud_ref_system_code CASCADE;
CREATE TABLE aud_ref_system_code (
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
	obs_ind varchar(1),
	insert_event varchar(1) NOT NULL
);
ALTER TABLE aud_ref_system_code ADD CONSTRAINT aud_ref_system_code_pk PRIMARY KEY (ref_system_code_id, last_update_date);



DROP TABLE IF EXISTS aud_sched_hearing_attendee CASCADE;
CREATE TABLE aud_sched_hearing_attendee (
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
	ref_justice_id integer,
	insert_event varchar(1) NOT NULL DEFAULT 'X'
);
ALTER TABLE aud_sched_hearing_attendee ADD CONSTRAINT aud_sched_hearing_attendee_pk PRIMARY KEY (sh_attendee_id, last_update_date);



DROP TABLE IF EXISTS aud_sched_hearing_defendant CASCADE;
CREATE TABLE aud_sched_hearing_defendant (
	sched_hear_def_id integer NOT NULL,
	scheduled_hearing_id integer NOT NULL,
	defendant_on_case_id integer NOT NULL,
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL,
	insert_event varchar(1) NOT NULL
);
ALTER TABLE aud_sched_hearing_defendant ADD CONSTRAINT aud_sched_hearing_defendant_pk PRIMARY KEY (sched_hear_def_id, last_update_date);



DROP TABLE IF EXISTS aud_scheduled_hearing CASCADE;
CREATE TABLE aud_scheduled_hearing (
	scheduled_hearing_id integer NOT NULL,
	sequence_no smallint,
	not_before_time timestamp,
	original_time timestamp,
	listing_note varchar(200),
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
	insert_event varchar(1) NOT NULL DEFAULT 'X',
	add_hearing_used varchar(1),
	ref_cracked_effective_id integer
);
ALTER TABLE aud_scheduled_hearing ADD CONSTRAINT aud_scheduled_hearing_pk PRIMARY KEY (scheduled_hearing_id, last_update_date);



DROP TABLE IF EXISTS aud_sh_judge CASCADE;
CREATE TABLE aud_sh_judge (
	sh_judge_id integer NOT NULL,
	deputy_hcj varchar(1),
	ref_judge_id integer NOT NULL,
	version integer NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	created_by varchar(30) NOT NULL,
	creation_date timestamp NOT NULL,
	last_update_date timestamp NOT NULL,
	sh_attendee_id integer NOT NULL,
	insert_event varchar(1) NOT NULL
);
ALTER TABLE aud_sh_judge ADD CONSTRAINT aud_sh_judge_pk PRIMARY KEY (sh_judge_id, last_update_date);



DROP TABLE IF EXISTS aud_sh_justice CASCADE;
CREATE TABLE aud_sh_justice (
	sh_justice_id integer NOT NULL,
	justice_name varchar(50),
	version integer NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	created_by varchar(30) NOT NULL,
	creation_date timestamp NOT NULL,
	last_update_date timestamp NOT NULL,
	hearing_id integer,
	insert_event varchar(1) NOT NULL
);
ALTER TABLE aud_sh_justice ADD CONSTRAINT aud_sh_justice_pk PRIMARY KEY (sh_justice_id, last_update_date);


DROP TABLE IF EXISTS aud_sh_staff CASCADE;
CREATE TABLE aud_sh_staff (
	sh_staff_id integer NOT NULL,
	staff_role varchar(2) NOT NULL,
	staff_name varchar(255) NOT NULL,
	version integer NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	created_by varchar(30) NOT NULL,
	creation_date timestamp NOT NULL,
	last_update_date timestamp NOT NULL,
	insert_event varchar(1) NOT NULL
);
ALTER TABLE aud_sh_staff ADD CONSTRAINT aud_sh_staff_pk PRIMARY KEY (sh_staff_id, last_update_date);



DROP TABLE IF EXISTS aud_sitting CASCADE;
CREATE TABLE aud_sitting (
	sitting_id integer NOT NULL,
	sitting_sequence_no smallint,
	is_sitting_judge varchar(1),
	sitting_time timestamp,
	sitting_note varchar(80),
	ref_justice1_id numeric(38),
	ref_justice2_id numeric(38),
	ref_justice3_id numeric(38),
	ref_justice4_id numeric(38),
	is_floating varchar(1) NOT NULL,
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
	justicename1 varchar(35),
	insert_event varchar(1) NOT NULL
);
ALTER TABLE aud_sitting ADD CONSTRAINT aud_sitting_pk PRIMARY KEY (sitting_id, last_update_date);



DROP TABLE IF EXISTS aud_sitting_on_list CASCADE;
CREATE TABLE aud_sitting_on_list (
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
	insert_event varchar(1),
	court_room_id integer NOT NULL,
	court_site_id integer NOT NULL
);
ALTER TABLE aud_sitting_on_list ADD CONSTRAINT aud_sitting_on_list_pk PRIMARY KEY (sitting_on_list_id, last_update_date);



DROP TABLE IF EXISTS aud_pdda_message CASCADE;
CREATE TABLE aud_pdda_message (
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
	version integer NOT NULL DEFAULT 1,
	insert_event varchar(1)
);
ALTER TABLE aud_pdda_message ADD CONSTRAINT aud_pdda_message_pk PRIMARY KEY (pdda_message_id, last_update_date);



DROP TABLE IF EXISTS aud_ref_pdda_message_type CASCADE;
CREATE TABLE aud_ref_pdda_message_type (
	pdda_message_type_id integer NOT NULL,
	pdda_message_type varchar(20) NOT NULL,
	pdda_message_description varchar(255),
	obs_ind varchar(1),
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL DEFAULT 1,
	insert_event varchar(1)
);
ALTER TABLE aud_ref_pdda_message_type ADD CONSTRAINT aud_ref_pdda_message_type_pk PRIMARY KEY (pdda_message_type_id, last_update_date);



DROP TABLE IF EXISTS aud_case_reference CASCADE;
CREATE TABLE aud_case_reference (
	case_reference_id integer NOT NULL,
	reporting_restrictions smallint NOT NULL,
	case_id integer,
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL,
	insert_event varchar(1) NOT NULL
);
ALTER TABLE aud_case_reference ADD CONSTRAINT aud_case_reference_pk PRIMARY KEY (case_reference_id, last_update_date);


DROP TABLE IF EXISTS aud_configured_public_notice CASCADE;
CREATE TABLE aud_configured_public_notice (
	configured_public_notice_id integer NOT NULL,
	is_active varchar(1),
	court_room_id integer NOT NULL,
	public_notice_id integer NOT NULL,
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL,
	insert_event varchar(1) NOT NULL
);
ALTER TABLE aud_configured_public_notice ADD CONSTRAINT aud_configured_public_notice_pk PRIMARY KEY (configured_public_notice_id, last_update_date);



DROP TABLE IF EXISTS aud_court_log_entry CASCADE;
CREATE TABLE aud_court_log_entry (
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
	scheduled_hearing_id integer,
	insert_event varchar(1) NOT NULL DEFAULT 'X'
);
ALTER TABLE aud_court_log_entry ADD CONSTRAINT aud_court_log_entry_pk PRIMARY KEY (entry_id, last_update_date);



DROP TABLE IF EXISTS aud_court_log_event_desc CASCADE;
CREATE TABLE aud_court_log_event_desc (
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
	short_description varchar(255) NOT NULL,
	insert_event varchar(1) NOT NULL
);
ALTER TABLE aud_court_log_event_desc ADD CONSTRAINT aud_court_log_event_desc_pk PRIMARY KEY (event_desc_id, last_update_date);



DROP TABLE IF EXISTS aud_cr_live_display CASCADE;
CREATE TABLE aud_cr_live_display (
	cr_live_display_id integer NOT NULL,
	court_room_id integer NOT NULL,
	scheduled_hearing_id integer,
	time_status_set timestamp NOT NULL,
	status varchar(4000),
	created_by varchar(30) NOT NULL,
	creation_date timestamp NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	last_update_date timestamp NOT NULL,
	version integer NOT NULL,
	insert_event varchar(1) NOT NULL
);
ALTER TABLE aud_cr_live_display ADD CONSTRAINT aud_cr_live_display_pk PRIMARY KEY (cr_live_display_id, last_update_date);



DROP TABLE IF EXISTS aud_defendant CASCADE;
CREATE TABLE aud_defendant (
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
	insert_event varchar(1) NOT NULL,
	public_display_hide varchar(1),
	parent_guardian_name varchar(35),
	ethnic_appearance_code varchar(10),
	ethnicity_self_defined varchar(10),
	current_prison_status varchar(1) NOT NULL DEFAULT 'N'
);
ALTER TABLE aud_defendant ADD CONSTRAINT aud_defendant_pk PRIMARY KEY (defendant_id, last_update_date);



DROP TABLE IF EXISTS aud_defendant_on_case CASCADE;
CREATE TABLE aud_defendant_on_case (
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
	insert_event varchar(1) NOT NULL DEFAULT 'X',
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
	first_fixed_trial timestamp,
	first_hearing_type varchar(10),
	nationality varchar(3),
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
ALTER TABLE aud_defendant_on_case ADD CONSTRAINT aud_defendant_on_case_pk PRIMARY KEY (defendant_on_case_id, last_update_date);



DROP TABLE IF EXISTS aud_definitive_public_notice CASCADE;
CREATE TABLE aud_definitive_public_notice (
	definitive_pn_id integer NOT NULL,
	definitive_pn_desc varchar(255) NOT NULL,
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL,
	priority integer,
	insert_event varchar(1) NOT NULL
);
ALTER TABLE aud_definitive_public_notice ADD CONSTRAINT aud_definitive_public_notice_pk PRIMARY KEY (definitive_pn_id, last_update_date);



DROP TABLE IF EXISTS aud_display CASCADE;
CREATE TABLE aud_display (
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
	show_unassigned_yn char(1) NOT NULL,
	insert_event varchar(1) NOT NULL DEFAULT 'X'
);
ALTER TABLE aud_display ADD CONSTRAINT aud_display_pk PRIMARY KEY (display_id, last_update_date);



DROP TABLE IF EXISTS aud_display_court_room CASCADE;
CREATE TABLE aud_display_court_room (
	display_id bigint NOT NULL,
	court_room_id bigint NOT NULL,
	modifying_user varchar(30) NOT NULL DEFAULT 'X',
	created_by varchar(30) NOT NULL,
	creation_date timestamp NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	last_update_date timestamp NOT NULL,
	insert_event varchar(1) NOT NULL DEFAULT 'X',
	version integer NOT NULL
);
ALTER TABLE aud_display_court_room ADD CONSTRAINT aud_display_court_room_pk PRIMARY KEY (display_id, court_room_id, last_update_date);



DROP TABLE IF EXISTS aud_display_document CASCADE;
CREATE TABLE aud_display_document (
	display_document_id bigint NOT NULL,
	description_code varchar(30) NOT NULL,
	default_page_delay bigint NOT NULL,
	multiple_court_yn char(1) NOT NULL,
	created_by varchar(30) NOT NULL,
	creation_date timestamp NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	last_update_date timestamp NOT NULL,
	version integer NOT NULL,
	insert_event varchar(1) NOT NULL DEFAULT 'X',
	country varchar(2),
	language varchar(2)
);
ALTER TABLE aud_display_document ADD CONSTRAINT aud_display_document_pk PRIMARY KEY (display_document_id, last_update_date);



DROP TABLE IF EXISTS aud_display_location CASCADE;
CREATE TABLE aud_display_location (
	display_location_id bigint NOT NULL,
	description_code varchar(30) NOT NULL,
	court_site_id integer NOT NULL,
	created_by varchar(30) NOT NULL,
	creation_date timestamp NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	last_update_date timestamp NOT NULL,
	version integer NOT NULL,
	insert_event varchar(1) NOT NULL DEFAULT 'X'
);
ALTER TABLE aud_display_location ADD CONSTRAINT aud_display_location_pk PRIMARY KEY (display_location_id, last_update_date);



DROP TABLE IF EXISTS aud_display_type CASCADE;
CREATE TABLE aud_display_type (
	display_type_id bigint NOT NULL,
	description_code varchar(30) NOT NULL,
	created_by varchar(30) NOT NULL,
	creation_date timestamp NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	last_update_date timestamp NOT NULL,
	version integer NOT NULL,
	insert_event varchar(1) NOT NULL DEFAULT 'X'
);
ALTER TABLE aud_display_type ADD CONSTRAINT aud_display_type_pk PRIMARY KEY (display_type_id, last_update_date);



DROP TABLE IF EXISTS aud_public_notice CASCADE;
CREATE TABLE aud_public_notice (
	public_notice_id integer NOT NULL,
	public_notice_desc varchar(255),
	court_id integer NOT NULL,
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL,
	definitive_pn_id integer NOT NULL,
	insert_event varchar(1) NOT NULL
);
ALTER TABLE aud_public_notice ADD CONSTRAINT aud_public_notice_pk PRIMARY KEY (public_notice_id, last_update_date);



DROP TABLE IF EXISTS aud_rotation_set_dd CASCADE;
CREATE TABLE aud_rotation_set_dd (
	rotation_set_dd_id bigint NOT NULL,
	rotation_set_id bigint NOT NULL,
	display_document_id bigint NOT NULL,
	page_delay bigint NOT NULL,
	ordering integer NOT NULL,
	created_by varchar(30) NOT NULL,
	creation_date timestamp NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	last_update_date timestamp NOT NULL,
	version integer NOT NULL,
	insert_event varchar(1) NOT NULL DEFAULT 'X'
);
ALTER TABLE aud_rotation_set_dd ADD CONSTRAINT aud_rotation_set_dd_pk PRIMARY KEY (rotation_set_dd_id, last_update_date);



DROP TABLE IF EXISTS aud_rotation_sets CASCADE;
CREATE TABLE aud_rotation_sets (
	rotation_set_id bigint NOT NULL,
	court_id bigint NOT NULL,
	description varchar(255) NOT NULL,
	default_yn char(1) NOT NULL,
	created_by varchar(30) NOT NULL,
	creation_date timestamp NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	last_update_date timestamp NOT NULL,
	version integer NOT NULL,
	insert_event varchar(1) NOT NULL DEFAULT 'X'
);
ALTER TABLE aud_rotation_sets ADD CONSTRAINT aud_rotation_sets_pk PRIMARY KEY (rotation_set_id, last_update_date);



DROP TABLE IF EXISTS aud_pdda_dl_notifier CASCADE;
CREATE TABLE aud_pdda_dl_notifier (
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
	version integer NOT NULL,
	insert_event varchar(1) NOT NULL DEFAULT 'X'
);
ALTER TABLE aud_pdda_dl_notifier ADD CONSTRAINT aud_pdda_dl_notifier_pk PRIMARY KEY (pdda_dl_notifier_id, last_update_date);



DROP TABLE IF EXISTS aud_display_store CASCADE;
CREATE TABLE aud_display_store (
	display_store_id bigint NOT NULL,
	retrieval_code text NOT NULL,
	content text,
	obs_ind varchar(1),
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL,
	insert_event varchar(1) NOT NULL DEFAULT 'X'
);
ALTER TABLE aud_display_store ADD CONSTRAINT aud_display_store_pk PRIMARY KEY (display_store_id, last_update_date);


DROP TABLE IF EXISTS aud_xml_document CASCADE;
CREATE TABLE aud_xml_document (
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
	version integer NOT NULL,
	insert_event varchar(1) NOT NULL DEFAULT 'X'
);
ALTER TABLE aud_xml_document ADD CONSTRAINT aud_xml_document_pk PRIMARY KEY (xml_document_id, last_update_date);
