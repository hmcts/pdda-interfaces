SET client_encoding TO 'UTF8';

DROP SCHEMA IF EXISTS temp CASCADE;
CREATE SCHEMA IF NOT EXISTS temp;


CREATE TABLE temp.tmp_config_prop (
	config_prop_id integer NOT NULL,
	property_name varchar(128) NOT NULL,
	property_value varchar(300) NOT NULL
) ;
ALTER TABLE temp.tmp_config_prop ADD CONSTRAINT tmp_config_prop_pk PRIMARY KEY (config_prop_id);



CREATE TABLE temp.tmp_court (
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
) ;
ALTER TABLE temp.tmp_court ADD CONSTRAINT tmp_court_pk PRIMARY KEY (court_id);



CREATE TABLE temp.tmp_court_room (
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
) ;
ALTER TABLE temp.tmp_court_room ADD CONSTRAINT tmp_court_room_pk PRIMARY KEY (court_room_id);



CREATE TABLE temp.tmp_court_satellite (
	court_satellite_id integer NOT NULL,
	court_site_id integer NOT NULL,
	internet_satellite_name varchar(255) NOT NULL,
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL,
	obs_ind varchar(1) DEFAULT 'N'
) ;
ALTER TABLE temp.tmp_court_satellite ADD CONSTRAINT tmp_court_satellite_pk PRIMARY KEY (court_satellite_id);


CREATE TABLE temp.tmp_court_site (
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
) ;
ALTER TABLE temp.tmp_court_site ADD CONSTRAINT tmp_court_site_pk PRIMARY KEY (court_site_id);



CREATE TABLE temp.tmp_ref_court (
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
) ;
ALTER TABLE temp.tmp_ref_court ADD CONSTRAINT tmp_ref_court_pk PRIMARY KEY (ref_court_id);



CREATE TABLE temp.tmp_ref_court_reporter (
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
) ;
ALTER TABLE temp.tmp_ref_court_reporter ADD CONSTRAINT tmp_ref_court_reporter_pk PRIMARY KEY (ref_court_reporter_id);


CREATE TABLE temp.tmp_ref_court_reporter_firm (
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
) ;
ALTER TABLE temp.tmp_ref_court_reporter_firm ADD CONSTRAINT tmp_ref_court_reporter_firm_pk PRIMARY KEY (ref_court_reporter_firm_id);



CREATE TABLE temp.tmp_ref_cracked_effective (
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
) ;
ALTER TABLE temp.tmp_ref_cracked_effective ADD CONSTRAINT tmp_ref_cracked_effective_pk PRIMARY KEY (ref_cracked_effective_id);



CREATE TABLE temp.tmp_ref_hearing_type (
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
) ;
ALTER TABLE temp.tmp_ref_hearing_type ADD CONSTRAINT tmp_ref_hearing_type_pk PRIMARY KEY (ref_hearing_type_id);


CREATE TABLE temp.tmp_ref_judge (
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
) ;
ALTER TABLE temp.tmp_ref_judge ADD CONSTRAINT tmp_ref_judge_pk PRIMARY KEY (ref_judge_id);



CREATE TABLE temp.tmp_ref_justice (
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
) ;
ALTER TABLE temp.tmp_ref_justice ADD CONSTRAINT tmp_ref_justice_pk PRIMARY KEY (ref_justice_id);



CREATE TABLE temp.tmp_ref_listing_data (
	ref_listing_data_id integer NOT NULL,
	ref_data_type varchar(50) NOT NULL,
	ref_data_value varchar(100) NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	creation_date timestamp NOT NULL,
	last_update_date timestamp NOT NULL,
	obs_ind varchar(1),
	version integer NOT NULL DEFAULT 1
) ;
ALTER TABLE temp.tmp_ref_listing_data ADD CONSTRAINT tmp_ref_listing_data_pk PRIMARY KEY (ref_listing_data_id);



CREATE TABLE temp.tmp_ref_system_code (
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
) ;
ALTER TABLE temp.tmp_ref_system_code ADD CONSTRAINT tmp_ref_system_code_pk PRIMARY KEY (ref_system_code_id);



CREATE TABLE temp.tmp_ref_pdda_message_type (
	pdda_message_type_id integer NOT NULL,
	pdda_message_type varchar(20) NOT NULL,
	pdda_message_description varchar(255),
	obs_ind varchar(1),
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL DEFAULT 1
) ;
ALTER TABLE temp.tmp_ref_pdda_message_type ADD CONSTRAINT tmp_ref_pdda_message_type_pk PRIMARY KEY (pdda_message_type_id);



CREATE TABLE temp.tmp_configured_public_notice (
	configured_public_notice_id integer NOT NULL,
	is_active varchar(1),
	court_room_id integer NOT NULL,
	public_notice_id integer NOT NULL,
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL
) ;
ALTER TABLE temp.tmp_configured_public_notice ADD CONSTRAINT tmp_configured_public_notice_pk PRIMARY KEY (configured_public_notice_id);



CREATE TABLE temp.tmp_court_log_event_desc (
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
) ;
ALTER TABLE temp.tmp_court_log_event_desc ADD CONSTRAINT tmp_court_log_event_desc_pk PRIMARY KEY (event_desc_id);



CREATE TABLE temp.tmp_cr_live_display (
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
) ;
ALTER TABLE temp.tmp_cr_live_display ADD CONSTRAINT tmp_cr_live_display_pk PRIMARY KEY (cr_live_display_id);


CREATE TABLE temp.tmp_definitive_public_notice (
	definitive_pn_id integer NOT NULL,
	definitive_pn_desc varchar(255) NOT NULL,
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL,
	priority integer DEFAULT 1
) ;
ALTER TABLE temp.tmp_definitive_public_notice ADD CONSTRAINT tmp_definitive_public_notice_pk PRIMARY KEY (definitive_pn_id);



CREATE TABLE temp.tmp_display (
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
) ;
ALTER TABLE temp.tmp_display ADD CONSTRAINT tmp_display_pk PRIMARY KEY (display_id);



CREATE TABLE temp.tmp_display_court_room (
	display_id bigint NOT NULL,
	court_room_id bigint NOT NULL
) ;
ALTER TABLE temp.tmp_display_court_room ADD CONSTRAINT tmp_display_court_room_pk PRIMARY KEY (display_id);


CREATE TABLE temp.tmp_display_document (
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
) ;
ALTER TABLE temp.tmp_display_document ADD CONSTRAINT tmp_display_document_pk PRIMARY KEY (display_document_id);


CREATE TABLE temp.tmp_display_location (
	display_location_id bigint NOT NULL,
	description_code varchar(30) NOT NULL,
	court_site_id integer NOT NULL,
	created_by varchar(30) NOT NULL,
	creation_date timestamp NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	last_update_date timestamp NOT NULL,
	version integer NOT NULL
) ;
ALTER TABLE temp.tmp_display_location ADD CONSTRAINT tmp_display_location_pk PRIMARY KEY (display_location_id);



CREATE TABLE temp.tmp_display_type (
	display_type_id bigint NOT NULL,
	description_code varchar(30) NOT NULL,
	created_by varchar(30) NOT NULL,
	creation_date timestamp NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	last_update_date timestamp NOT NULL,
	version integer NOT NULL
) ;
ALTER TABLE temp.tmp_display_type ADD CONSTRAINT tmp_display_type_pk PRIMARY KEY (display_type_id);


CREATE TABLE temp.tmp_public_notice (
	public_notice_id integer NOT NULL,
	public_notice_desc varchar(255),
	court_id integer NOT NULL,
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL,
	definitive_pn_id integer NOT NULL
) ;
ALTER TABLE temp.tmp_public_notice ADD CONSTRAINT tmp_public_notice_pk PRIMARY KEY (public_notice_id);



CREATE TABLE temp.tmp_rotation_set_dd (
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
) ;
ALTER TABLE temp.tmp_rotation_set_dd ADD CONSTRAINT tmp_rotation_set_dd_pk PRIMARY KEY (rotation_set_dd_id);



CREATE TABLE temp.tmp_rotation_sets (
	rotation_set_id bigint NOT NULL,
	court_id bigint NOT NULL,
	description varchar(255) NOT NULL,
	default_yn char(1) NOT NULL,
	created_by varchar(30) NOT NULL,
	creation_date timestamp NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	last_update_date timestamp NOT NULL,
	version integer NOT NULL
) ;
ALTER TABLE temp.tmp_rotation_sets ADD CONSTRAINT tmp_rotation_sets_pk PRIMARY KEY (rotation_set_id);
