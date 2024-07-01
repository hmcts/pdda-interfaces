CREATE UNIQUE INDEX IF NOT EXISTS xhb_config_prop_idx ON xhb_config_prop (property_name);
insert into xhb_config_prop
(config_prop_id, property_name, property_value)
values
(6, 'scheduledtasks.pdda', 'courtellisttask'),
(19, 'COURTEL_LIST_AMOUNT', '5'),
(20, 'COURTEL_MAX_RETRY', '5'),
(21, 'MESSAGE_LOOKUP_DELAY', '60'),
(22, 'CPPX_SchemaWL', 'WarnedList-v1-0.xsd'),
(23, 'CPPX_SchemaFL', 'FirmList-v1-0.xsd'),
(24, 'CPPX_SchemaWP', 'CPPX_InternetWebPage-v1-0.xsd')
on conflict (property_name)
do update
set property_value = xhb_config_prop.property_value||','||EXCLUDED.property_value
where POSITION(EXCLUDED.property_value in xhb_config_prop.property_value) = 0;
