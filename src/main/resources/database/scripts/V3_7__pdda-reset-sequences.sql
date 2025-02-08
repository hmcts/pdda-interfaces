SET client_encoding TO 'UTF8';

SELECT setval('xhb_courtel_list_seq', COALESCE(MAX(courtel_list_id)+1, 1), FALSE) FROM xhb_courtel_list;
SELECT setval('xhb_config_prop_seq', COALESCE(MAX(config_prop_id)+1, 1), FALSE) FROM xhb_config_prop;
