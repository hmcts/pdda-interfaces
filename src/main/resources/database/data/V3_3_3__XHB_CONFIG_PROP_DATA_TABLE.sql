insert into pdda.xhb_config_prop (config_prop_id, property_name, property_value)
values (((select max(config_prop_id) from pdda.xhb_config_prop)+1), 'STOP_UNMERGED_LISTS','0');

COMMIT;
