TRUNCATE temp.tmp_ref_solicitor_firm;
\cd data
\copy temp.tmp_ref_solicitor_firm (ref_solicitor_firm_id, solicitor_firm_name, crest_sof_id, court_id, obs_ind, short_name, dx_ref, vat_no, last_update_date, creation_date, created_by, last_updated_by, version, address_id, la_code) FROM 'XHB_REF_SOLICITOR_FIRM_DATA_TABLE.csv' DELIMITER ',' CSV HEADER
\cd ..

ALTER TABLE xhb_ref_solicitor_firm DISABLE TRIGGER ALL;

WITH upsert as (
	update public.xhb_ref_solicitor_firm t2
	set solicitor_firm_name=t1.solicitor_firm_name,
	crest_sof_id=t1.crest_sof_id,
	court_id=t1.court_id,
	obs_ind=t1.obs_ind,
	short_name=t1.short_name,
	dx_ref=t1.dx_ref,
	vat_no=t1.vat_no,
	last_update_date=t1.last_update_date,
	creation_date=t1.creation_date,
	created_by=t1.created_by,
	last_updated_by=t1.last_updated_by,
	version=t1.version,
	address_id=t1.address_id,
	la_code=t1.la_code
	from temp.tmp_ref_solicitor_firm t1
	where t2.ref_solicitor_firm_id=t1.ref_solicitor_firm_id
	RETURNING t2.*)
insert into public.xhb_ref_solicitor_firm
select p.ref_solicitor_firm_id, p.solicitor_firm_name, p.crest_sof_id, p.court_id, p.obs_ind, p.short_name, p.dx_ref, p.vat_no, p.last_update_date, p.creation_date, p.created_by, p.last_updated_by, p.version, p.address_id, p.la_code
from temp.tmp_ref_solicitor_firm p
where p.ref_solicitor_firm_id not in (select q.ref_solicitor_firm_id from upsert q);

SELECT setval('xhb_ref_solicitor_firm_seq', COALESCE(MAX(ref_solicitor_firm_id)+1, 1), FALSE) FROM xhb_ref_solicitor_firm;

ALTER TABLE xhb_ref_solicitor_firm ENABLE TRIGGER ALL;
