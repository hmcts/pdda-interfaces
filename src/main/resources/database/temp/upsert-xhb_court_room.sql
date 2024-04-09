TRUNCATE temp.tmp_court_room;
\cd data
\copy temp.tmp_court_room (court_room_id, court_room_name, description, crest_court_room_no, court_site_id, last_update_date, creation_date, created_by, last_updated_by, version, obs_ind, display_name, security_ind, video_ind) FROM 'XHB_COURT_ROOM_DATA_TABLE.csv' DELIMITER ',' CSV HEADER
\cd ..

ALTER TABLE XHB_COURT_ROOM DISABLE TRIGGER ALL;

WITH upsert as (
	update public.xhb_court_room t2
	set court_room_name=t1.court_room_name,
	description=t1.description,
	crest_court_room_no=t1.crest_court_room_no,
	court_site_id=t1.court_site_id,
	last_update_date=t1.last_update_date,
	creation_date=t1.creation_date,
	created_by=t1.created_by,
	last_updated_by=t1.last_updated_by,
	version=t1.version,
	obs_ind=t1.obs_ind,
	display_name=t1.display_name,
	security_ind=t1.security_ind,
	video_ind=t1.video_ind
	from temp.tmp_court_room t1
	where t2.court_room_id=t1.court_room_id
	RETURNING t2.*)
insert into public.xhb_court_room
select p.court_room_id, p.court_room_name, p.description, p.crest_court_room_no, p.court_site_id, p.last_update_date, p.creation_date, p.created_by, p.last_updated_by, p.version, p.obs_ind, p.display_name, p.security_ind, p.video_ind
from temp.tmp_court_room p
where p.court_room_id not in (select q.court_room_id from upsert q);

SELECT setval('xhb_court_room_seq', COALESCE(MAX(court_room_id)+1, 1), FALSE) FROM xhb_court_room;

ALTER TABLE XHB_COURT_ROOM ENABLE TRIGGER ALL;
