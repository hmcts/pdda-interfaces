insert into xhb_ref_hearing_type (REF_HEARING_TYPE_ID,COURT_ID,HEARING_TYPE_CODE,HEARING_TYPE_DESC,CATEGORY,SEQ_NO,LIST_SEQUENCE,LAST_UPDATE_DATE,CREATION_DATE,CREATED_BY,LAST_UPDATED_BY,VERSION) values (
1,1,'MEN','For Mention','S',510,1030,'16-MARCH-20 13:36:08','16-MARCH-20 13:36:08','XHIBIT','XHIBIT',1);

-- Reset the sequence after insert --
SELECT setval('xhb_ref_hearing_type_seq', COALESCE(MAX(ref_hearing_type_id)+1, 1), FALSE) FROM xhb_ref_hearing_type;
