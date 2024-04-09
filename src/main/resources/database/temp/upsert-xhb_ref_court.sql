TRUNCATE temp.tmp_ref_court;
\cd data
\copy temp.tmp_ref_court (ref_court_id, court_full_name, court_short_name, name_prefix, court_type, crest_code, obs_ind, is_psd, dx_ref, last_update_date, creation_date, created_by, last_updated_by, version, address_id, court_id) FROM 'XHB_REF_COURT_DATA_TABLE.csv' DELIMITER ',' CSV HEADER
\cd ..

ALTER TABLE XHB_REF_COURT DISABLE TRIGGER ALL;

WITH upsert as (
	update public.xhb_ref_court t2
	set court_full_name=t1.court_full_name,
	court_short_name=t1.court_short_name,
	name_prefix=t1.name_prefix,
	court_type=t1.court_type,
	crest_code=t1.crest_code,
	obs_ind=t1.obs_ind,
	is_psd=t1.is_psd,
	dx_ref=t1.dx_ref,
	last_update_date=t1.last_update_date,
	creation_date=t1.creation_date,
	created_by=t1.created_by,
	last_updated_by=t1.last_updated_by,
	version=t1.version,
	address_id=t1.address_id,
	court_id=t1.court_id
	from temp.tmp_ref_court t1
	where t2.ref_court_id=t1.ref_court_id
	RETURNING t2.*)
insert into public.xhb_ref_court
select p.ref_court_id, p.court_full_name, p.court_short_name, p.name_prefix, p.court_type, p.crest_code, p.obs_ind, p.is_psd, p.dx_ref, p.last_update_date, p.creation_date, p.created_by, p.last_updated_by, p.version, p.address_id, p.court_id
from temp.tmp_ref_court p
where p.ref_court_id not in (select q.ref_court_id from upsert q);

SELECT setval('xhb_ref_court_seq', COALESCE(MAX(ref_court_id)+1, 1), FALSE) FROM xhb_ref_court;

ALTER TABLE XHB_REF_COURT ENABLE TRIGGER ALL;
