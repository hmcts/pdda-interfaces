insert into pdda.xhb_config_prop (config_prop_id, property_name, property_value)
values (((select max(config_prop_id) from pdda.xhb_config_prop)+1), 'SITES_TO_RESTART_CDUS','0');

COMMIT;
