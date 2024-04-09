TRUNCATE temp.tmp_court_log_event_desc;
\cd data
\copy temp.tmp_court_log_event_desc (event_desc_id, flagged_event, editable, send_to_mercator, update_linked_cases, publish_to_subscribers, clear_public_displays, e_inform, public_display, linked_case_text, event_description, version, last_updated_by, event_type, created_by, creation_date, last_update_date, public_notice, short_description) FROM 'XHB_COURT_LOG_EVENT_DESC_DATA_TABLE.csv' DELIMITER ',' CSV HEADER
\cd ..

ALTER TABLE xhb_court_log_event_desc DISABLE TRIGGER ALL;

WITH upsert as (
	update public.xhb_court_log_event_desc t2
	set flagged_event=t1.flagged_event,
	editable=t1.editable,
	send_to_mercator=t1.send_to_mercator,
	update_linked_cases=t1.update_linked_cases,
	publish_to_subscribers=t1.publish_to_subscribers,
	clear_public_displays=t1.clear_public_displays,
	e_inform=t1.e_inform,
	public_display=t1.public_display,
	linked_case_text=t1.linked_case_text,
	event_description=t1.event_description,
	version=t1.version,
	last_updated_by=t1.last_updated_by,
	event_type=t1.event_type,
	created_by=t1.created_by,
	creation_date=t1.creation_date,
	last_update_date=t1.last_update_date,
	public_notice=t1.public_notice,
	short_description=t1.short_description
	from temp.tmp_court_log_event_desc t1
	where t2.event_desc_id=t1.event_desc_id
	RETURNING t2.*)
insert into public.xhb_court_log_event_desc
select p.event_desc_id, p.flagged_event, p.editable, p.send_to_mercator, p.update_linked_cases, p.publish_to_subscribers, p.clear_public_displays, p.e_inform, p.public_display, p.linked_case_text, p.event_description, p.version, p.last_updated_by, p.event_type, p.created_by, p.creation_date, p.last_update_date, p.public_notice, p.short_description
from temp.tmp_court_log_event_desc p
where p.event_desc_id not in (select q.event_desc_id from upsert q);

SELECT setval('xhb_court_log_event_desc_seq', COALESCE(MAX(event_desc_id)+1, 1), FALSE) FROM xhb_court_log_event_desc;

ALTER TABLE xhb_court_log_event_desc ENABLE TRIGGER ALL;
