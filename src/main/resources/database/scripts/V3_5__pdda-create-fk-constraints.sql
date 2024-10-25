SET client_encoding TO 'UTF8';

ALTER TABLE xhb_courtel_list ADD CONSTRAINT xhb_courtel_list_xml_doc_fk FOREIGN KEY (xml_document_id) REFERENCES xhb_xml_document(xml_document_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_courtel_list ADD CONSTRAINT xhb_courtel_list_clob_fk FOREIGN KEY (xml_document_clob_id) REFERENCES xhb_clob(clob_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_courtel_list ADD CONSTRAINT xhb_courtel_list_blob_fk FOREIGN KEY (blob_id) REFERENCES xhb_blob(blob_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE xhb_cath_document_link ADD CONSTRAINT xhb_cath_document_link_orig_doc_fk FOREIGN KEY (orig_courtel_list_doc_id) REFERENCES xhb_courtel_list(courtel_list_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_cath_document_link ADD CONSTRAINT xhb_cath_document_link_cath_xml_fk FOREIGN KEY (cath_xml_id) REFERENCES xhb_xml_document(xml_document_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_cath_document_link ADD CONSTRAINT xhb_cath_document_link_cath_json_fk FOREIGN KEY (cath_json_id) REFERENCES xhb_xml_document(xml_document_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
