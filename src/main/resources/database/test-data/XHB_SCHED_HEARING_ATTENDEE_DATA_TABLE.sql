insert into xhb_sched_hearing_attendee (SH_ATTENDEE_ID,SCHEDULED_HEARING_ID,REF_JUDGE_ID,ATTENDEE_TYPE,LAST_UPDATE_DATE,CREATION_DATE,CREATED_BY,LAST_UPDATED_BY,VERSION) values (
1,1,1,'J','16-MARCH-20 13:36:08','16-MARCH-20 13:36:08','XHIBIT','XHIBIT',1);

-- Reset the sequence after insert --
SELECT setval('xhb_sched_hearing_attend_seq', COALESCE(MAX(sh_attendee_id)+1, 1), FALSE) FROM xhb_sched_hearing_attendee;
