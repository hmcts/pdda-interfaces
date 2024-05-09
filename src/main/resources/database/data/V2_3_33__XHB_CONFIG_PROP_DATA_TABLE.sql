insert into xhb_config_prop 
(config_prop_id, property_name, property_value)
values 
(6, 'scheduledtasks.pdda', 'courtellisttask')
on conflict (property_name)
do update 
set property_value = xhb_config_prop.property_value||','||EXCLUDED.property_value
where POSITION(EXCLUDED.property_value in xhb_config_prop.property_value) = 0;
