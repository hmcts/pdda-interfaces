SET client_encoding TO 'UTF8';

CREATE UNIQUE INDEX IF NOT EXISTS xhb_cath_document_link_idx ON xhb_cath_document_link (cath_document_link_id);
