TRUNCATE temp.tmp_ref_hearing_type;
\cd data
\copy temp.tmp_ref_hearing_type (ref_hearing_type_id, hearing_type_code, hearing_type_desc, category, seq_no, list_sequence, last_update_date, creation_date, created_by, last_updated_by, version, court_id, obs_ind) FROM 'XHB_REF_HEARING_TYPE_DATA_TABLE.csv' DELIMITER ',' CSV HEADER
\cd ..

ALTER TABLE xhb_ref_hearing_type DISABLE TRIGGER ALL;

WITH upsert as (
	update public.xhb_ref_hearing_type t2
	set hearing_type_code=t1.hearing_type_code,
	hearing_type_desc=t1.hearing_type_desc,
	category=t1.category,
	seq_no=t1.seq_no,
	list_sequence=t1.list_sequence,
	last_update_date=t1.last_update_date,
	creation_date=t1.creation_date,
	created_by=t1.created_by,
	last_updated_by=t1.last_updated_by,
	version=t1.version,
	court_id=t1.court_id,
	obs_ind=t1.obs_ind
	from temp.tmp_ref_hearing_type t1
	where t2.ref_hearing_type_id=t1.ref_hearing_type_id
	RETURNING t2.*)
insert into public.xhb_ref_hearing_type
select p.ref_hearing_type_id, p.hearing_type_code, p.hearing_type_desc, p.category, p.seq_no, p.list_sequence, p.last_update_date, p.creation_date, p.created_by, p.last_updated_by, p.version, p.court_id, p.obs_ind
from temp.tmp_ref_hearing_type p
where p.ref_hearing_type_id not in (select q.ref_hearing_type_id from upsert q);

SELECT setval('xhb_ref_hearing_type_seq', COALESCE(MAX(ref_hearing_type_id)+1, 1), FALSE) FROM xhb_ref_hearing_type;

ALTER TABLE xhb_ref_hearing_type ENABLE TRIGGER ALL;
