SET client_encoding TO 'UTF8';

DROP TABLE IF EXISTS aud_cath_document_link CASCADE;
CREATE TABLE aud_cath_document_link (
	cath_document_link_id integer NOT NULL,
	orig_courtel_list_doc_id integer,
	cath_xml_id integer,
	cath_json_id integer,
	created_by varchar(30) NOT NULL,
	creation_date timestamp NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	last_update_date timestamp NOT NULL,
	version integer NOT NULL
);
ALTER TABLE aud_cath_document_link ADD CONSTRAINT aud_cath_document_link_pk PRIMARY KEY (cath_document_link_id);
