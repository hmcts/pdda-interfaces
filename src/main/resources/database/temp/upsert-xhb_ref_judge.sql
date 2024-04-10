TRUNCATE temp.tmp_ref_judge;
\cd data
\copy temp.tmp_ref_judge (ref_judge_id, judge_type, crest_judge_id, title, first_name, middle_name, surname, full_list_title1, full_list_title2, full_list_title3, stats_code, initials, honours, jud_vers, obs_ind, source_table, last_update_date, creation_date, created_by, last_updated_by, version, court_id) FROM 'XHB_REF_JUDGE_DATA_TABLE.csv' DELIMITER ',' CSV HEADER
\cd ..

ALTER TABLE XHB_REF_JUDGE DISABLE TRIGGER ALL;

WITH upsert as (
	update public.xhb_ref_judge t2
	set judge_type=t1.judge_type,
	crest_judge_id=t1.crest_judge_id,
	title=t1.title,
	first_name=t1.first_name,
	middle_name=t1.middle_name,
	surname=t1.surname,
	full_list_title1=t1.full_list_title1,
	full_list_title2=t1.full_list_title2,
	full_list_title3=t1.full_list_title3,
	stats_code=t1.stats_code,
	initials=t1.initials,
	honours=t1.honours,
	jud_vers=t1.jud_vers,
	obs_ind=t1.obs_ind,
	source_table=t1.source_table,
	last_update_date=t1.last_update_date,
	creation_date=t1.creation_date,
	created_by=t1.created_by,
	last_updated_by=t1.last_updated_by,
	version=t1.version,
	court_id=t1.court_id
	from temp.tmp_ref_judge t1
	where t2.ref_judge_id=t1.ref_judge_id
	RETURNING t2.*)
insert into public.xhb_ref_judge
select p.ref_judge_id, p.judge_type, p.crest_judge_id, p.title, p.first_name, p.middle_name, p.surname, p.full_list_title1, p.full_list_title2, p.full_list_title3, p.stats_code, p.initials, p.honours, p.jud_vers, p.obs_ind, p.source_table, p.last_update_date, p.creation_date, p.created_by, p.last_updated_by, p.version, p.court_id
from temp.tmp_ref_judge p
where p.ref_judge_id not in (select q.ref_judge_id from upsert q);

SELECT setval('xhb_ref_judge_seq', COALESCE(MAX(ref_judge_id)+1, 1), FALSE) FROM xhb_ref_judge;

ALTER TABLE XHB_REF_JUDGE ENABLE TRIGGER ALL;
