TRUNCATE temp.tmp_court_site;
\cd data
\copy temp.tmp_court_site (court_site_id, court_site_name, court_site_code, court_id, address_id, last_update_date, creation_date, created_by, last_updated_by, version, obs_ind, display_name, crest_court_id, short_name, site_group, floater_text, list_name, tier) FROM 'XHB_COURT_SITE_DATA_TABLE.csv' DELIMITER ',' CSV HEADER
\cd ..

ALTER TABLE XHB_COURT_SITE DISABLE TRIGGER ALL;

WITH upsert as (
	update public.xhb_court_site t2
	set court_site_name=t1.court_site_name,
	court_site_code=t1.court_site_code,
	court_id=t1.court_id,
	address_id=t1.address_id,
	last_update_date=t1.last_update_date,
	creation_date=t1.creation_date,
	created_by=t1.created_by,
	last_updated_by=t1.last_updated_by,
	version=t1.version,
	obs_ind=t1.obs_ind,
	display_name=t1.display_name,
	crest_court_id=t1.crest_court_id,
	short_name=t1.short_name,
	site_group=t1.site_group,
	floater_text=t1.floater_text,
	list_name=t1.list_name,
	tier=t1.tier
	from temp.tmp_court_site t1
	where t2.court_site_id=t1.court_site_id
	RETURNING t2.*)
insert into public.xhb_court_site
select p.court_site_id, p.court_site_name, p.court_site_code, p.court_id, p.address_id, p.last_update_date, p.creation_date, p.created_by, p.last_updated_by, p.version, p.obs_ind, p.display_name, p.crest_court_id, p.short_name, p.site_group, p.floater_text, p.list_name, p.tier
from temp.tmp_court_site p
where p.court_site_id not in (select q.court_site_id from upsert q);

SELECT setval('xhb_court_site_seq', COALESCE(MAX(court_site_id)+1, 1), FALSE) FROM xhb_court_site;

ALTER TABLE XHB_COURT_SITE ENABLE TRIGGER ALL;
