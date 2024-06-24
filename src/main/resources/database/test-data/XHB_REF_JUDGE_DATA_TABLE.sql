insert into xhb_ref_judge (REF_JUDGE_ID,JUDGE_TYPE,FULL_LIST_TITLE1,FIRST_NAME,MIDDLE_NAME,SURNAME,COURT_ID,LAST_UPDATE_DATE,CREATION_DATE,CREATED_BY,LAST_UPDATED_BY,VERSION) values (
1,'CJ','Before Judge : DEED','JOHN','','DEED',80,'16-MARCH-20 13:36:08','16-MARCH-20 13:36:08','XHIBIT','XHIBIT',1);

-- Reset the sequence after insert --
SELECT setval('xhb_ref_judge_seq', COALESCE(MAX(ref_judge_id)+1, 1), FALSE) FROM xhb_ref_judge;
