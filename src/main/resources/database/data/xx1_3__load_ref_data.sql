\COPY xhb_address (address_id, address_1, address_2, address_3, address_4, town, county, postcode, country, last_update_date, creation_date, created_by, last_updated_by, version) FROM 'XHB_ADDRESS_DATA_TABLE.csv' DELIMITER ',' CSV HEADER;

\COPY xhb_public_notice (public_notice_id, public_notice_desc, court_id, last_update_date, creation_date, created_by, last_updated_by, version, definitive_pn_id) FROM 'XHB_PUBLIC_NOTICE_DATA_TABLE.csv' DELIMITER ',' CSV HEADER;

\COPY xhb_rotation_sets (rotation_set_id, court_id, description, default_yn, created_by, creation_date, last_updated_by, last_update_date, version) FROM 'XHB_ROTATION_SETS_DATA_TABLE.csv' DELIMITER ',' CSV HEADER;
