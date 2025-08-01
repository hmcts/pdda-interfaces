INSERT INTO xhb_courtel_list
(courtel_list_id,xml_document_id,xml_document_clob_id,blob_id,sent_to_courtel,num_send_attempts,last_attempt_datetime,message_text,last_update_date,creation_date,created_by,last_updated_by,"version") VALUES
	 (1,491000,421000,1,'N',5,'2018-11-07 00:00:00.000','Filename is invalid :Warned List from 29/10/18 to 02/11/18 DRAFT v2 2018-10-31 10:30:39','2024-05-14 13:31:06.747','2024-05-14
13:31:06.747','PDDA','PDDA',1),
	 (2,491000,421001,1,'N',5,'2018-11-07 00:00:00.000','Date in filename is invalid','2024-05-14 13:31:06.747','2024-05-14 13:31:06.747','PDDA','PDDA',1),
	 (3,491000,421000,1,'N',1,NULL,'Filename is invalid :Warned List from 29/10/18 to 02/11/18 DRAFT v2 2018-10-31 10:30:39','2024-05-14 13:35:01.524','2024-05-14 13:32:28.532','PDDA','PDDA',2),
	 (4,491000,421001,1,'N',1,NULL,'Date in filename is invalid','2024-05-14 13:35:57.311','2024-05-14 13:35:57.311','PDDA','PDDA',1);

-- Reset the sequence after insert --
SELECT setval('xhb_courtel_list_seq', COALESCE(MAX(courtel_list_id)+1, 1), FALSE) FROM xhb_courtel_list;
