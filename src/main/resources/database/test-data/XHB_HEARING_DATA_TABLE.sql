insert into xhb_hearing (HEARING_ID,CASE_ID,REF_HEARING_TYPE_ID,COURT_ID,LAST_UPDATE_DATE,CREATION_DATE,CREATED_BY,LAST_UPDATED_BY,VERSION) values (
1,621487,1,1,'16-MARCH-20 13:36:08','16-MARCH-20 13:36:08','XHIBIT','XHIBIT',1);

-- Reset the sequence after insert --
SELECT setval('xhb_hearing_seq', COALESCE(MAX(hearing_id)+1, 1), FALSE) FROM xhb_hearing;
