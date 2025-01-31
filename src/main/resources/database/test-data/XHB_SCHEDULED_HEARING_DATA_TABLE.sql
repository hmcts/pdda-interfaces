insert into xhb_scheduled_hearing (SCHEDULED_HEARING_ID,SITTING_ID,HEARING_ID,IS_CASE_ACTIVE,MOVED_FROM_COURT_ROOM_ID,LAST_UPDATE_DATE,CREATION_DATE,CREATED_BY,LAST_UPDATED_BY,VERSION) values (
1,1,1,'Y',8105,'16-MARCH-20 13:36:08','16-MARCH-20 13:36:08','XHIBIT','XHIBIT',1);

-- Reset the sequence after insert --
SELECT setval('xhb_scheduled_hearing_seq', COALESCE(MAX(scheduled_hearing_id)+1, 1), FALSE) FROM xhb_scheduled_hearing;
