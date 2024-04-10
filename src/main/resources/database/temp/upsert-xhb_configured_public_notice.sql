TRUNCATE temp.tmp_configured_public_notice;
\cd data
\copy temp.tmp_configured_public_notice (configured_public_notice_id, is_active, court_room_id, public_notice_id, last_update_date, creation_date, created_by, last_updated_by, version) FROM 'XHB_CONFIGURED_PUBLIC_NOTICE_DATA_TABLE.csv' DELIMITER ',' CSV HEADER
\cd ..

ALTER TABLE xhb_configured_public_notice DISABLE TRIGGER ALL;

WITH upsert as (
	update public.xhb_configured_public_notice t2
	set is_active=t1.is_active,
	court_room_id=t1.court_room_id,
	public_notice_id=t1.public_notice_id,
	last_update_date=t1.last_update_date,
	creation_date=t1.creation_date,
	created_by=t1.created_by,
	last_updated_by=t1.last_updated_by,
	version=t1.version
	from temp.tmp_configured_public_notice t1
	where t2.configured_public_notice_id=t1.configured_public_notice_id
	RETURNING t2.*)
insert into public.xhb_configured_public_notice
select p.configured_public_notice_id, p.is_active, p.court_room_id, p.public_notice_id, p.last_update_date, p.creation_date, p.created_by, p.last_updated_by, p.version
from temp.tmp_configured_public_notice p
where p.configured_public_notice_id not in (select q.configured_public_notice_id from upsert q);

SELECT setval('xhb_configured_public_not_seq', COALESCE(MAX(configured_public_notice_id)+1, 1), FALSE) FROM xhb_configured_public_notice;

ALTER TABLE xhb_configured_public_notice ENABLE TRIGGER ALL;
