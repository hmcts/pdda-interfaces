insert into xhb_sched_hearing_defendant (SCHED_HEAR_DEF_ID,SCHEDULED_HEARING_ID,DEFENDANT_ON_CASE_ID,LAST_UPDATE_DATE,CREATION_DATE,CREATED_BY,LAST_UPDATED_BY,VERSION) values (
100,1,200,'16-MARCH-20 13:36:08','16-MARCH-20 13:36:08','XHIBIT','XHIBIT',1);

-- Reset the sequence after insert --
SELECT setval('xhb_scheduled_hearing_def_seq', COALESCE(MAX(sched_hear_def_id)+1, 1), FALSE) FROM xhb_sched_hearing_defendant;
