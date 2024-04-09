TRUNCATE temp.tmp_ref_court_reporter;
\cd data
\copy temp.tmp_ref_court_reporter (ref_court_reporter_id, first_name, middle_name, surname, crest_court_reporter_id, initials, report_method, obs_ind, ref_court_reporter_firm_id, last_update_date, creation_date, created_by, last_updated_by, version, court_id) FROM 'XHB_REF_COURT_REPORTER_DATA_TABLE.csv' DELIMITER ',' CSV HEADER
\cd ..

ALTER TABLE XHB_REF_COURT_REPORTER DISABLE TRIGGER ALL;

WITH upsert as (
	update public.xhb_ref_court_reporter t2
	set first_name=t1.first_name,
	middle_name=t1.middle_name,
	surname=t1.surname,
	crest_court_reporter_id=t1.crest_court_reporter_id,
	initials=t1.initials,
	report_method=t1.report_method,
	obs_ind=t1.obs_ind,
	ref_court_reporter_firm_id=t1.ref_court_reporter_firm_id,
	last_update_date=t1.last_update_date,
	creation_date=t1.creation_date,
	created_by=t1.created_by,
	last_updated_by=t1.last_updated_by,
	version=t1.version,
	court_id=t1.court_id
	from temp.tmp_ref_court_reporter t1
	where t2.ref_court_reporter_id=t1.ref_court_reporter_id
	RETURNING t2.*)
insert into public.xhb_ref_court_reporter
select p.ref_court_reporter_id, p.first_name, p.middle_name, p.surname, p.crest_court_reporter_id, p.initials, p.report_method, p.obs_ind, p.ref_court_reporter_firm_id, p.last_update_date, p.creation_date, p.created_by, p.last_updated_by, p.version, p.court_id
from temp.tmp_ref_court_reporter p
where p.ref_court_reporter_id not in (select q.ref_court_reporter_id from upsert q);

SELECT setval('xhb_ref_court_reporter_seq', COALESCE(MAX(ref_court_reporter_id)+1, 1), FALSE) FROM xhb_ref_court_reporter;

ALTER TABLE XHB_REF_COURT_REPORTER ENABLE TRIGGER ALL;
