SET client_encoding TO 'UTF8';

CREATE INDEX IF NOT EXISTS xhb_courtel_list_xml_doc_idx ON xhb_courtel_list (xml_document_id, courtel_list_id);
CREATE INDEX IF NOT EXISTS xhb_document_clob_idx ON xhb_xml_document (xml_document_clob_id, xml_document_id);
CREATE UNIQUE INDEX IF NOT EXISTS xhb_config_prop_idx ON xhb_config_prop (property_name);
CREATE UNIQUE INDEX IF NOT EXISTS xhb_cath_document_link_idx ON xhb_cath_document_link (cath_document_link_id);
