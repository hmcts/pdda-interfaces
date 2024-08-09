insert into xhb_defendant_on_case (DEFENDANT_ON_CASE_ID,DEFENDANT_ID,CASE_ID,LAST_UPDATE_DATE,CREATION_DATE,CREATED_BY,LAST_UPDATED_BY,VERSION) values (
200,1,621487,'16-MARCH-20 13:36:08','16-MARCH-20 13:36:08','XHIBIT','XHIBIT',1);

-- Reset the sequence after insert --
SELECT setval('xhb_defendant_on_case_seq', COALESCE(MAX(defendant_on_case_id)+1, 1), FALSE) FROM xhb_defendant_on_case;
