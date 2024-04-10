TRUNCATE temp.tmp_ref_cracked_effective;
\cd data
\copy temp.tmp_ref_cracked_effective (ref_cracked_effective_id, code, description, party_responsible, obs_ind, last_update_date, creation_date, last_updated_by, created_by, version, trial_code_type) FROM 'XHB_REF_CRACKED_EFFECTIVE_DATA_TABLE.csv' DELIMITER ',' CSV HEADER
\cd ..

ALTER TABLE XHB_REF_CRACKED_EFFECTIVE DISABLE TRIGGER ALL;

WITH upsert as (
	update public.xhb_ref_cracked_effective t2
	set code=t1.code,
	description=t1.description,
	party_responsible=t1.party_responsible,
	obs_ind=t1.obs_ind,
	last_update_date=t1.last_update_date,
	creation_date=t1.creation_date,
	last_updated_by=t1.last_updated_by,
	created_by=t1.created_by,
	version=t1.version,
	trial_code_type=t1.trial_code_type
	from temp.tmp_ref_cracked_effective t1
	where t2.ref_cracked_effective_id=t1.ref_cracked_effective_id
	RETURNING t2.*)
insert into public.xhb_ref_cracked_effective
select p.ref_cracked_effective_id, p.code, p.description, p.party_responsible, p.obs_ind, p.last_update_date, p.creation_date, p.last_updated_by, p.created_by, p.version, p.trial_code_type
from temp.tmp_ref_cracked_effective p
where p.ref_cracked_effective_id not in (select q.ref_cracked_effective_id from upsert q);

SELECT setval('xhb_ref_cracked_effective_seq', COALESCE(MAX(ref_cracked_effective_id)+1, 1), FALSE) FROM xhb_ref_cracked_effective;

ALTER TABLE XHB_REF_CRACKED_EFFECTIVE ENABLE TRIGGER ALL;
