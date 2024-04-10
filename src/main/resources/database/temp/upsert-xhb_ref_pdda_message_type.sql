TRUNCATE temp.tmp_ref_pdda_message_type;
\cd data
\copy temp.tmp_ref_pdda_message_type (pdda_message_type_id, pdda_message_type, pdda_message_description, obs_ind, last_update_date, creation_date, created_by, last_updated_by, version) FROM 'XHB_REF_PDDA_MESSAGE_TYPE_TABLE.csv' DELIMITER ',' CSV HEADER
\cd ..

ALTER TABLE xhb_ref_pdda_message_type DISABLE TRIGGER ALL;

WITH upsert as (
	update public.xhb_ref_pdda_message_type t2
	set pdda_message_type=t1.pdda_message_type,
	pdda_message_description=t1.pdda_message_description,
	obs_ind=t1.obs_ind,
	last_update_date=t1.last_update_date,
	creation_date=t1.creation_date,
	created_by=t1.created_by,
	last_updated_by=t1.last_updated_by,
	version=t1.version
	from temp.tmp_ref_pdda_message_type t1
	where t2.pdda_message_type_id=t1.pdda_message_type_id
	RETURNING t2.*)
insert into public.xhb_ref_pdda_message_type
select p.pdda_message_type_id, p.pdda_message_type, p.pdda_message_description, p.obs_ind, p.last_update_date, p.creation_date, p.created_by, p.last_updated_by, p.version
from temp.tmp_ref_pdda_message_type p
where p.pdda_message_type_id not in (select q.pdda_message_type_id from upsert q);

SELECT setval('xhb_ref_pdda_message_type_seq', COALESCE(MAX(pdda_message_type_id)+1, 1), FALSE) FROM xhb_ref_pdda_message_type;

ALTER TABLE xhb_ref_pdda_message_type ENABLE TRIGGER ALL;
