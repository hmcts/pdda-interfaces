TRUNCATE temp.tmp_court;
\cd data
\copy temp.tmp_court (court_id, court_type, circuit, court_name, crest_court_id, court_prefix, short_name, last_update_date, creation_date, created_by, last_updated_by, version, address_id, crest_ip_address, in_service_flag, obs_ind, probation_office_name, internet_court_name, display_name, court_code, country, language, police_force_code, fl_rep_sort, court_start_time, wl_rep_sort, wl_rep_period, wl_rep_time, wl_free_text, is_pilot, dx_ref, county_loc_code, tier, cpp_court) FROM 'XHB_COURT_DATA_TABLE.csv' DELIMITER ',' CSV HEADER
\cd ..

ALTER TABLE XHB_COURT DISABLE TRIGGER ALL;

WITH upsert as (
	update public.xhb_court t2
	set court_type=t1.court_type,
	circuit=t1.circuit,
	court_name=t1.court_name,
	crest_court_id=t1.crest_court_id,
	court_prefix=t1.court_prefix,
	short_name=t1.short_name,
	last_update_date=t1.last_update_date,
	creation_date=t1.creation_date,
	created_by=t1.created_by,
	last_updated_by=t1.last_updated_by,
	version=t1.version,
	address_id=t1.address_id,
	crest_ip_address=t1.crest_ip_address,
	in_service_flag=t1.in_service_flag,
	obs_ind=t1.obs_ind,
	probation_office_name=t1.probation_office_name,
	internet_court_name=t1.internet_court_name,
	display_name=t1.display_name,
	court_code=t1.court_code,
	country=t1.country,
	language=t1.language,
	police_force_code=t1.police_force_code,
	fl_rep_sort=t1.fl_rep_sort,
	court_start_time=t1.court_start_time,
	wl_rep_sort=t1.wl_rep_sort,
	wl_rep_period=t1.wl_rep_period,
	wl_rep_time=t1.wl_rep_time,
	wl_free_text=t1.wl_free_text,
	is_pilot=t1.is_pilot,
	dx_ref=t1.dx_ref,
	county_loc_code=t1.county_loc_code,
	tier=t1.tier,
	cpp_court=t1.cpp_court
	from temp.tmp_court t1
	where t2.court_id=t1.court_id
	RETURNING t2.*)
insert into public.xhb_court
select p.court_id, p.court_type, p.circuit, p.court_name, p.crest_court_id, p.court_prefix, p.short_name, p.last_update_date, p.creation_date, p.created_by, p.last_updated_by, p.version, p.address_id, p.crest_ip_address, p.in_service_flag, p.obs_ind, p.probation_office_name, p.internet_court_name, p.display_name, p.court_code, p.country, p.language, p.police_force_code, p.fl_rep_sort, p.court_start_time, p.wl_rep_sort, p.wl_rep_period, p.wl_rep_time, p.wl_free_text, p.is_pilot, p.dx_ref, p.county_loc_code, p.tier, p.cpp_court
from temp.tmp_court p
where p.court_id not in (select q.court_id from upsert q);

SELECT setval('xhb_court_seq', COALESCE(MAX(court_id)+1, 1), FALSE) FROM xhb_court;

ALTER TABLE XHB_COURT ENABLE TRIGGER ALL;
