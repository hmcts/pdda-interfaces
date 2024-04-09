TRUNCATE temp.tmp_ref_legal_representative;
\cd data
\copy temp.tmp_ref_legal_representative (ref_legal_rep_id, first_name, middle_name, surname, title, initials, legal_rep_type, last_update_date, creation_date, created_by, last_updated_by, version, court_id, obs_ind) FROM 'XHB_REF_LEGAL_REPRESENTATIVE_DATA_TABLE.csv' DELIMITER ',' CSV HEADER
\cd ..

ALTER TABLE xhb_ref_legal_representative DISABLE TRIGGER ALL;

WITH upsert as (
	update public.xhb_ref_legal_representative t2
	set first_name=t1.first_name,
	middle_name=t1.middle_name,
	surname=t1.surname,
	title=t1.title,
	initials=t1.initials,
	legal_rep_type=t1.legal_rep_type,
	last_update_date=t1.last_update_date,
	creation_date=t1.creation_date,
	created_by=t1.created_by,
	last_updated_by=t1.last_updated_by,
	version=t1.version,
	court_id=t1.court_id,
	obs_ind=t1.obs_ind
	from temp.tmp_ref_legal_representative t1
	where t2.ref_legal_rep_id=t1.ref_legal_rep_id
	RETURNING t2.*)
insert into public.xhb_ref_legal_representative
select p.ref_legal_rep_id, p.first_name, p.middle_name, p.surname, p.title, p.initials, p.legal_rep_type, p.last_update_date, p.creation_date, p.created_by, p.last_updated_by, p.version, p.court_id, p.obs_ind
from temp.tmp_ref_legal_representative p
where p.ref_legal_rep_id not in (select q.ref_legal_rep_id from upsert q);

SELECT setval('xhb_ref_legal_rep_seq', COALESCE(MAX(ref_legal_rep_id)+1, 1), FALSE) FROM xhb_ref_legal_representative;

ALTER TABLE xhb_ref_legal_representative ENABLE TRIGGER ALL;
