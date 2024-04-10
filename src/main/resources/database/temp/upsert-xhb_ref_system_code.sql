TRUNCATE temp.tmp_ref_system_code;
\cd data
\copy temp.tmp_ref_system_code (ref_system_code_id, code, code_type, code_title, de_code, ref_code_order, last_update_date, creation_date, created_by, last_updated_by, version, court_id, obs_ind) FROM 'XHB_REF_SYSTEM_CODE_DATA_TABLE.csv' DELIMITER ',' CSV HEADER
\cd ..

ALTER TABLE xhb_ref_system_code DISABLE TRIGGER ALL;

WITH upsert as (
	update public.xhb_ref_system_code t2
	set code=t1.code,
	code_type=t1.code_type,
	code_title=t1.code_title,
	de_code=t1.de_code,
	ref_code_order=t1.ref_code_order,
	last_update_date=t1.last_update_date,
	creation_date=t1.creation_date,
	created_by=t1.created_by,
	last_updated_by=t1.last_updated_by,
	version=t1.version,
	court_id=t1.court_id,
	obs_ind=t1.obs_ind
	from temp.tmp_ref_system_code t1
	where t2.ref_system_code_id=t1.ref_system_code_id
	RETURNING t2.*)
insert into public.xhb_ref_system_code
select p.ref_system_code_id, p.code, p.code_type, p.code_title, p.de_code, p.ref_code_order, p.last_update_date, p.creation_date, p.created_by, p.last_updated_by, p.version, p.court_id, p.obs_ind
from temp.tmp_ref_system_code p
where p.ref_system_code_id not in (select q.ref_system_code_id from upsert q);

SELECT setval('xhb_ref_system_code_seq', COALESCE(MAX(ref_system_code_id)+1, 1), FALSE) FROM xhb_ref_system_code;

ALTER TABLE xhb_ref_system_code ENABLE TRIGGER ALL;
