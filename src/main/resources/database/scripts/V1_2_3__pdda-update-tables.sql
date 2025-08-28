SET client_encoding TO 'UTF8';


-- Create the XHB_INTERNET_HTML table where the HTML (IWP) files sent to CaTH will be picked up from  
CREATE TABLE IF NOT EXISTS pdda.xhb_internet_html (
    internet_html_id integer NOT NULL,
    status VARCHAR(20),
    court_id integer NOT NULL,
    html_blob_id bigint NOT NULL,
    last_update_date timestamp NOT NULL,
	creation_date timestamp NOT NULL,
	created_by varchar(30) NOT NULL,
	last_updated_by varchar(30) NOT NULL,
	version integer NOT NULL
);
ALTER TABLE pdda.xhb_internet_html ADD CONSTRAINT xhb_internet_html_pk PRIMARY KEY (internet_html_id);
