TRUNCATE temp.tmp_config_prop;
\cd data
\copy temp.tmp_config_prop (config_prop_id, property_name, property_value) FROM 'XHB_CONFIG_PROP_DATA_TABLE.csv' DELIMITER ',' CSV HEADER
\cd ..

ALTER TABLE XHB_CONFIG_PROP DISABLE TRIGGER ALL;

WITH upsert as (
	update public.xhb_config_prop t2
	set property_name=t1.property_name,
	property_value=t1.property_value
	from temp.tmp_config_prop t1
	where t2.config_prop_id=t1.config_prop_id
	RETURNING t2.*)
insert into public.xhb_config_prop
select p.config_prop_id, p.property_name, p.property_value
from temp.tmp_config_prop p
where p.config_prop_id not in (select q.config_prop_id from upsert q);

SELECT setval('xhb_config_prop_seq', COALESCE(MAX(config_prop_id)+1, 1), FALSE) FROM xhb_config_prop;

ALTER TABLE XHB_CONFIG_PROP ENABLE TRIGGER ALL;
