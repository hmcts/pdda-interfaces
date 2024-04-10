TRUNCATE temp.tmp_ref_monitoring_category;
\cd data
\copy temp.tmp_ref_monitoring_category (ref_monitoring_category_id, monitoring_category_code, monitoring_category_name, created_by, last_updated_by, creation_date, last_update_date, version) FROM 'XHB_REF_MONITORING_CATEGORY_DATA_TABLE.csv' DELIMITER ',' CSV HEADER
\cd ..

ALTER TABLE xhb_ref_monitoring_category DISABLE TRIGGER ALL;

WITH upsert as (
	update public.xhb_ref_monitoring_category t2
	set monitoring_category_code=t1.monitoring_category_code,
	monitoring_category_name=t1.monitoring_category_name,
	created_by=t1.created_by,
	last_updated_by=t1.last_updated_by,
	creation_date=t1.creation_date,
	last_update_date=t1.last_update_date,
	version=t1.version
	from temp.tmp_ref_monitoring_category t1
	where t2.ref_monitoring_category_id=t1.ref_monitoring_category_id
	RETURNING t2.*)
insert into public.xhb_ref_monitoring_category
select p.ref_monitoring_category_id, p.monitoring_category_code, p.monitoring_category_name, p.created_by, p.last_updated_by, p.creation_date, p.last_update_date, p.version
from temp.tmp_ref_monitoring_category p
where p.ref_monitoring_category_id not in (select q.ref_monitoring_category_id from upsert q);

SELECT setval('xhb_ref_monitoring_cat_seq', COALESCE(MAX(ref_monitoring_category_id)+1, 1), FALSE) FROM xhb_ref_monitoring_category;

ALTER TABLE xhb_ref_monitoring_category ENABLE TRIGGER ALL;
