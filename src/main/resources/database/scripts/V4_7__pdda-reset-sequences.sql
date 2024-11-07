SET client_encoding TO 'UTF8';

SELECT setval('xhb_cath_document_link_seq', COALESCE(MAX(cath_document_link_id)+1, 1), FALSE) FROM xhb_cath_document_link;
