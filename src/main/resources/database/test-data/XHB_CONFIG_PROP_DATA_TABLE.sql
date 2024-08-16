update xhb_config_prop set property_value = '127.0.0.1:22' where property_name = 'PDDA_BAIS_SFTP_HOSTNAME' and property_value = 'TO_BE_SET';
update xhb_config_prop set property_value = 'testuser' where property_name = 'PDDA_BAIS_CP_SFTP_USERNAME' and property_value = 'BAISXhibittoPDDA';
update xhb_config_prop set property_value = 'password' where property_name = 'PDDA_BAIS_CP_SFTP_PASSWORD' and property_value = 'somepass';
update xhb_config_prop set property_value = '/Users/testuser/temp/' where property_name = 'PDDA_BAIS_CP_SFTP_UPLOAD_LOCATION' and property_value = '/';
