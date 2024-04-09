TRUNCATE temp.tmp_address;
\cd data
\copy temp.tmp_address (address_id, address_1, address_2, address_3, address_4, town, county, postcode, country, last_update_date, creation_date, created_by, last_updated_by, version) FROM 'XHB_ADDRESS_DATA_TABLE.csv' DELIMITER ',' CSV HEADER
\cd ..

ALTER TABLE XHB_ADDRESS DISABLE TRIGGER ALL;

WITH upsert as (
	update public.xhb_address t2
	set address_1=t1.address_1,
	address_2=t1.address_2,
	address_3=t1.address_3,
	address_4=t1.address_4,
	town=t1.town,
	county=t1.county,
	postcode=t1.postcode,
	country=t1.country,
	last_update_date=t1.last_update_date,
	creation_date=t1.creation_date,
	created_by=t1.created_by,
	last_updated_by=t1.last_updated_by,
	version=t1.version
	from temp.tmp_address t1
	where t2.address_id=t1.address_id
	RETURNING t2.*)
insert into public.xhb_address
select p.address_id, p.address_1, p.address_2, p.address_3, p.address_4, p.town, p.county, p.postcode, p.country, p.last_update_date, p.creation_date, p.created_by, p.last_updated_by, p.version
from temp.tmp_address p
where p.address_id not in (select q.address_id from upsert q);

SELECT setval('xhb_address_seq', COALESCE(MAX(address_id)+1, 1), FALSE) FROM xhb_address;

ALTER TABLE XHB_ADDRESS ENABLE TRIGGER ALL;
