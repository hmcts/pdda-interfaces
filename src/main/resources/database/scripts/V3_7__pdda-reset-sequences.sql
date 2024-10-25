SET client_encoding TO 'UTF8';

SELECT setval('xhb_courtel_list_seq', COALESCE(MAX(courtel_list_id)+1, 1), FALSE) FROM xhb_courtel_list;
SELECT setval('xhb_config_prop_seq', COALESCE(MAX(config_prop_id)+1, 1), FALSE) FROM xhb_config_prop;
SELECT setval('xhb_cath_document_link_seq', COALESCE(MAX(cath_document_link_id)+1, 1), FALSE) FROM xhb_cath_document_link;
