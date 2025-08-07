SET client_encoding TO 'UTF8';

DROP TABLE IF EXISTS xhb_courtel_list CASCADE;
CREATE TABLE xhb_courtel_list (
	courtel_list_id integer NOT NULL,
	xml_document_id integer,
	xml_document_clob_id bigint,
	blob_id bigint,
    sent_to_courtel varchar(1) NOT NULL,
    num_send_attempts integer,
    last_attempt_datetime timestamp,
    message_text varchar(255),
	last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL
);
ALTER TABLE xhb_courtel_list ADD CONSTRAINT xhb_courtel_list_pk PRIMARY KEY (courtel_list_id);
