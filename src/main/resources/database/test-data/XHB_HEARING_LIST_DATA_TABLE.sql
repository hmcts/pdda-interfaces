insert into xhb_hearing_list (LIST_ID,LIST_TYPE,START_DATE,END_DATE,STATUS,COURT_ID,CREST_LIST_ID,LAST_UPDATE_DATE,CREATION_DATE,CREATED_BY,LAST_UPDATED_BY,VERSION) values (
1,'D','16-MARCH-23 00:00:00','16-MARCH-23 23:59:59','OPEN',80,1,'16-MARCH-20 13:36:08','16-MARCH-20 13:36:08','XHIBIT','XHIBIT',1);

-- Reset the sequence after insert --
SELECT setval('xhb_hearing_list_seq', COALESCE(MAX(list_id)+1, 1), FALSE) FROM xhb_hearing_list;
