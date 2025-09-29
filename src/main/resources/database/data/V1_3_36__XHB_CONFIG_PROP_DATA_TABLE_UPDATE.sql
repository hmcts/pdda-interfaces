-- The following data is needed for using the scheduler to run the DB schedulerjobs since the in-built postgres pg_cron cannot be used

INSERT INTO XHB_CONFIG_PROP (CONFIG_PROP_ID, PROPERTY_NAME, PROPERTY_VALUE)
VALUES (((select max(config_prop_id) from pdda.xhb_config_prop)+1), 'db_scheduler_jobs','clear_audit_tables,clear_obsolete_messages,clear_old_records,nullify_live_display_fields');

INSERT INTO XHB_CONFIG_PROP (CONFIG_PROP_ID, PROPERTY_NAME, PROPERTY_VALUE)
VALUES (((select max(config_prop_id) from pdda.xhb_config_prop)+1), 'job__clear_audit_tables__lastruntime','01/01/2025 00:00');

INSERT INTO XHB_CONFIG_PROP (CONFIG_PROP_ID, PROPERTY_NAME, PROPERTY_VALUE)
VALUES (((select max(config_prop_id) from pdda.xhb_config_prop)+1), 'job__clear_obsolete_messages__lastruntime','01/01/2025 00:00');

INSERT INTO XHB_CONFIG_PROP (CONFIG_PROP_ID, PROPERTY_NAME, PROPERTY_VALUE)
VALUES (((select max(config_prop_id) from pdda.xhb_config_prop)+1), 'job__clear_old_records__lastruntime','01/01/2025 00:00');

INSERT INTO XHB_CONFIG_PROP (CONFIG_PROP_ID, PROPERTY_NAME, PROPERTY_VALUE)
VALUES (((select max(config_prop_id) from pdda.xhb_config_prop)+1), 'job__nullify_live_display_fields__lastruntime','01/01/2025 00:00');

INSERT INTO XHB_CONFIG_PROP (CONFIG_PROP_ID, PROPERTY_NAME, PROPERTY_VALUE)
VALUES (((select max(config_prop_id) from pdda.xhb_config_prop)+1), 'job__clear_audit_tables__numparams','2');

INSERT INTO XHB_CONFIG_PROP (CONFIG_PROP_ID, PROPERTY_NAME, PROPERTY_VALUE)
VALUES (((select max(config_prop_id) from pdda.xhb_config_prop)+1), 'job__clear_obsolete_messages__numparams','2');

INSERT INTO XHB_CONFIG_PROP (CONFIG_PROP_ID, PROPERTY_NAME, PROPERTY_VALUE)
VALUES (((select max(config_prop_id) from pdda.xhb_config_prop)+1), 'job__clear_old_records__numparams','2');

INSERT INTO XHB_CONFIG_PROP (CONFIG_PROP_ID, PROPERTY_NAME, PROPERTY_VALUE)
VALUES (((select max(config_prop_id) from pdda.xhb_config_prop)+1), 'job__nullify_live_display_fields__numparams','0');

INSERT INTO XHB_CONFIG_PROP (CONFIG_PROP_ID, PROPERTY_NAME, PROPERTY_VALUE)
VALUES (((select max(config_prop_id) from pdda.xhb_config_prop)+1), 'job__clear_audit_tables__param1','NON_PROD');

INSERT INTO XHB_CONFIG_PROP (CONFIG_PROP_ID, PROPERTY_NAME, PROPERTY_VALUE)
VALUES (((select max(config_prop_id) from pdda.xhb_config_prop)+1), 'job__clear_audit_tables__param2','5000');

INSERT INTO XHB_CONFIG_PROP (CONFIG_PROP_ID, PROPERTY_NAME, PROPERTY_VALUE)
VALUES (((select max(config_prop_id) from pdda.xhb_config_prop)+1), 'job__clear_obsolete_messages__param1','');

INSERT INTO XHB_CONFIG_PROP (CONFIG_PROP_ID, PROPERTY_NAME, PROPERTY_VALUE)
VALUES (((select max(config_prop_id) from pdda.xhb_config_prop)+1), 'job__clear_obsolete_messages__param2','NON_PROD');

INSERT INTO XHB_CONFIG_PROP (CONFIG_PROP_ID, PROPERTY_NAME, PROPERTY_VALUE)
VALUES (((select max(config_prop_id) from pdda.xhb_config_prop)+1), 'job__clear_old_records__param1','30');

INSERT INTO XHB_CONFIG_PROP (CONFIG_PROP_ID, PROPERTY_NAME, PROPERTY_VALUE)
VALUES (((select max(config_prop_id) from pdda.xhb_config_prop)+1), 'job__clear_old_records__param2','100');

COMMIT;
