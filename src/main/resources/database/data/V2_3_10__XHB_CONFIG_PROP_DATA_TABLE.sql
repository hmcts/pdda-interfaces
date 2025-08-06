insert into pdda.xhb_config_prop (config_prop_id, property_name, property_value)
values (((select max(config_prop_id) from pdda.xhb_config_prop)+1), 'PDDA_PDM_ENVIRONMENT_URL','https://pdda-public-display-manager.demo.platform.hmcts.net');
   
COMMIT;
