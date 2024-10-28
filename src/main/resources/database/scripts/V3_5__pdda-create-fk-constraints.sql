SET client_encoding TO 'UTF8';

ALTER TABLE xhb_courtel_list ADD CONSTRAINT xhb_courtel_list_xml_doc_fk FOREIGN KEY (xml_document_id) REFERENCES xhb_xml_document(xml_document_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_courtel_list ADD CONSTRAINT xhb_courtel_list_clob_fk FOREIGN KEY (xml_document_clob_id) REFERENCES xhb_clob(clob_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_courtel_list ADD CONSTRAINT xhb_courtel_list_blob_fk FOREIGN KEY (blob_id) REFERENCES xhb_blob(blob_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
