TRUNCATE temp.tmp_ref_justice;
\cd data
\copy temp.tmp_ref_justice (ref_justice_id, justice_name, crest_justice_id, court_id, psd_court_code, title, initials, last_update_date, creation_date, created_by, last_updated_by, version, obs_ind) FROM 'XHB_REF_JUSTICE_DATA_TABLE.csv' DELIMITER ',' CSV HEADER
\cd ..

ALTER TABLE xhb_ref_justice DISABLE TRIGGER ALL;

WITH upsert as (
	update public.xhb_ref_justice t2
	set justice_name=t1.justice_name,
	crest_justice_id=t1.crest_justice_id,
	court_id=t1.court_id,
	psd_court_code=t1.psd_court_code,
	title=t1.title,
	initials=t1.initials,
	last_update_date=t1.last_update_date,
	creation_date=t1.creation_date,
	created_by=t1.created_by,
	last_updated_by=t1.last_updated_by,
	version=t1.version,
	obs_ind=t1.obs_ind
	from temp.tmp_ref_justice t1
	where t2.ref_justice_id=t1.ref_justice_id
	RETURNING t2.*)
insert into public.xhb_ref_justice
select p.ref_justice_id, p.justice_name, p.crest_justice_id, p.court_id, p.psd_court_code, p.title, p.initials, p.last_update_date, p.creation_date, p.created_by, p.last_updated_by, p.version, p.obs_ind
from temp.tmp_ref_justice p
where p.ref_justice_id not in (select q.ref_justice_id from upsert q);

SELECT setval('xhb_ref_justice_seq', COALESCE(MAX(ref_justice_id)+1, 1), FALSE) FROM xhb_ref_justice;

ALTER TABLE xhb_ref_justice ENABLE TRIGGER ALL;
