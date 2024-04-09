TRUNCATE temp.tmp_court_satellite;
\cd data
\copy temp.tmp_court_satellite (court_satellite_id, court_site_id, internet_satellite_name, last_update_date, creation_date, created_by, last_updated_by, version, obs_ind) FROM 'XHB_COURT_SATELLITE_DATA_TABLE.csv' DELIMITER ',' CSV HEADER
\cd ..

ALTER TABLE XHB_COURT_SATELLITE DISABLE TRIGGER ALL;

WITH upsert as (
	update public.xhb_court_satellite t2
	set court_site_id=t1.court_site_id,
	internet_satellite_name=t1.internet_satellite_name,
	last_update_date=t1.last_update_date,
	creation_date=t1.creation_date,
	created_by=t1.created_by,
	last_updated_by=t1.last_updated_by,
	version=t1.version,
	obs_ind=t1.obs_ind
	from temp.tmp_court_satellite t1
	where t2.court_satellite_id=t1.court_satellite_id
	RETURNING t2.*)
insert into public.xhb_court_satellite
select p.court_satellite_id, p.court_site_id, p.internet_satellite_name, p.last_update_date, p.creation_date, p.created_by, p.last_updated_by, p.version, p.obs_ind
from temp.tmp_court_satellite p
where p.court_satellite_id not in (select q.court_satellite_id from upsert q);

SELECT setval('xhb_court_satellite_seq', COALESCE(MAX(court_satellite_id)+1, 1), FALSE) FROM xhb_court_satellite;

ALTER TABLE XHB_COURT_SATELLITE ENABLE TRIGGER ALL;
