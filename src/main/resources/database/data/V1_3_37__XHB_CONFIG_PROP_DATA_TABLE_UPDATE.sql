-- The following data is needed for using the scheduler to run the DB schedulerjobs since the in-built postgres pg_cron cannot be used

UPDATE XHB_CONFIG_PROP
SET PROPERTY_VALUE='Error obsoleting message' where PROPERTY_NAME='job__clear_obsolete_messages__param1';

UPDATE XHB_CONFIG_PROP
SET PROPERTY_VALUE=5000 where PROPERTY_NAME='job__clear_obsolete_messages__param2';

COMMIT;
