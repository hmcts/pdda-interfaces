TRUNCATE temp.tmp_ref_court_reporter_firm;
\cd data
\copy temp.tmp_ref_court_reporter_firm (ref_court_reporter_firm_id, obs_ind, display_first, dx_ref, vat_no, firm_name, address_id, court_id, last_updated_by, created_by, creation_date, last_update_date, version, crest_court_reporter_firm_id) FROM 'XHB_REF_COURT_REPORTER_FIRM_DATA_TABLE.csv' DELIMITER ',' CSV HEADER
\cd ..

ALTER TABLE XHB_REF_COURT_REPORTER_FIRM DISABLE TRIGGER ALL;

WITH upsert as (
	update public.xhb_ref_court_reporter_firm t2
	set obs_ind=t1.obs_ind,
	display_first=t1.display_first,
	dx_ref=t1.dx_ref,
	vat_no=t1.vat_no,
	firm_name=t1.firm_name,
	address_id=t1.address_id,
	court_id=t1.court_id,
	last_updated_by=t1.last_updated_by,
	created_by=t1.created_by,
	creation_date=t1.creation_date,
	last_update_date=t1.last_update_date,
	version=t1.version,
	crest_court_reporter_firm_id=t1.crest_court_reporter_firm_id
	from temp.tmp_ref_court_reporter_firm t1
	where t2.ref_court_reporter_firm_id=t1.ref_court_reporter_firm_id
	RETURNING t2.*)
insert into public.xhb_ref_court_reporter_firm
select p.ref_court_reporter_firm_id, p.obs_ind, p.display_first, p.dx_ref, p.vat_no, p.firm_name, p.address_id, p.court_id, p.last_updated_by, p.created_by, p.creation_date, p.last_update_date, p.version, p.crest_court_reporter_firm_id
from temp.tmp_ref_court_reporter_firm p
where p.ref_court_reporter_firm_id not in (select q.ref_court_reporter_firm_id from upsert q);

SELECT setval('xhb_ref_court_report_f_seq', COALESCE(MAX(ref_court_reporter_firm_id)+1, 1), FALSE) FROM xhb_ref_court_reporter_firm;

ALTER TABLE XHB_REF_COURT_REPORTER_FIRM ENABLE TRIGGER ALL;
