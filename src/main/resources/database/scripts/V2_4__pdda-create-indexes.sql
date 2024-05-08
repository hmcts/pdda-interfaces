SET client_encoding TO 'UTF8';

CREATE INDEX xhb_courtel_list_xml_doc_idx ON xhb_courtel_list (xml_document_id, courtel_list_id);
CREATE INDEX xhb_document_clob_idx ON xhb_xml_document (xml_document_clob_id, xml_document_id);