TRUNCATE temp.tmp_ref_listing_data;
\cd data
\copy temp.tmp_ref_listing_data (ref_listing_data_id, ref_data_type, ref_data_value, created_by, last_updated_by, creation_date, last_update_date, obs_ind, version) FROM 'XHB_REF_LISTING_DATA_DATA_TABLE.csv' DELIMITER ',' CSV HEADER
\cd ..

ALTER TABLE xhb_ref_listing_data DISABLE TRIGGER ALL;

WITH upsert as (
	update public.xhb_ref_listing_data t2
	set ref_data_type=t1.ref_data_type,
	ref_data_value=t1.ref_data_value,
	created_by=t1.created_by,
	last_updated_by=t1.last_updated_by,
	creation_date=t1.creation_date,
	last_update_date=t1.last_update_date,
	obs_ind=t1.obs_ind,
	version=t1.version
	from temp.tmp_ref_listing_data t1
	where t2.ref_listing_data_id=t1.ref_listing_data_id
	RETURNING t2.*)
insert into public.xhb_ref_listing_data
select p.ref_listing_data_id, p.ref_data_type, p.ref_data_value, p.created_by, p.last_updated_by, p.creation_date, p.last_update_date, p.obs_ind, p.version
from temp.tmp_ref_listing_data p
where p.ref_listing_data_id not in (select q.ref_listing_data_id from upsert q);

SELECT setval('xhb_ref_listing_data_seq', COALESCE(MAX(ref_listing_data_id)+1, 1), FALSE) FROM xhb_ref_listing_data;

ALTER TABLE xhb_ref_listing_data ENABLE TRIGGER ALL;
