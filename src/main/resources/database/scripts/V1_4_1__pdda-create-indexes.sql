SET client_encoding TO 'UTF8';

-- Function used in indexes must be immutable, use immutable_to_char() instead of to_char()
CREATE OR REPLACE FUNCTION immutable_to_char(timestamp, fmt text) RETURNS text AS
$$ SELECT to_char($1, $2); $$
LANGUAGE sql immutable;

CREATE INDEX pdda_message_status_date_idx ON XHB_PDDA_MESSAGE (cp_document_status, obs_ind, creation_date);
CREATE INDEX cpp_staging_inbound_name_date_idx ON xhb_cpp_staging_inbound (document_name, creation_date);
