-- Reset the value from 900 to 0 as no merging of lists takes place in PDDA so no delay is needed

UPDATE XHB_CONFIG_PROP
SET PROPERTY_VALUE='0' where PROPERTY_NAME='FORMATTING_LIST_DELAY';

COMMIT;
