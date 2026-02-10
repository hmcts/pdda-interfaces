insert into pdda.xhb_config_prop (config_prop_id, property_name, property_value)
values (((select max(config_prop_id) from pdda.xhb_config_prop)+1), 'RECENT_LISTS_LOOKUP_TIMEFRAME','5');

insert into pdda.xhb_config_prop (config_prop_id, property_name, property_value)
values (((select max(config_prop_id) from pdda.xhb_config_prop)+1), 'MAX_ON_HOLD_TIMEFRAME','10');

COMMIT;
