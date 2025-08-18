SET client_encoding TO 'UTF8';

ALTER TABLE xhb_cath_document_link ADD CONSTRAINT xhb_cath_document_link_orig_doc_fk FOREIGN KEY (orig_courtel_list_doc_id) REFERENCES xhb_courtel_list(courtel_list_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_cath_document_link ADD CONSTRAINT xhb_cath_document_link_cath_xml_fk FOREIGN KEY (cath_xml_id) REFERENCES xhb_xml_document(xml_document_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE xhb_cath_document_link ADD CONSTRAINT xhb_cath_document_link_cath_json_fk FOREIGN KEY (cath_json_id) REFERENCES xhb_xml_document(xml_document_id) ON DELETE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;
