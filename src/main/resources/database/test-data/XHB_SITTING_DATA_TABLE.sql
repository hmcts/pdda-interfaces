insert into xhb_sitting (SITTING_ID,LIST_ID,COURT_SITE_ID,COURT_ROOM_ID,IS_FLOATING,LAST_UPDATE_DATE,CREATION_DATE,CREATED_BY,LAST_UPDATED_BY,VERSION) values (
1,1,1606,8104,'0','16-MARCH-20 13:36:08','16-MARCH-20 13:36:08','XHIBIT','XHIBIT',1);

-- Reset the sequence after insert --
SELECT setval('xhb_sitting_seq', COALESCE(MAX(sitting_id)+1, 1), FALSE) FROM xhb_sitting;
