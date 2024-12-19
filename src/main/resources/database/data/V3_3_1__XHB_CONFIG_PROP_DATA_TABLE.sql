CREATE UNIQUE INDEX IF NOT EXISTS xhb_config_prop_idx ON xhb_config_prop (property_name);
insert into xhb_config_prop
(config_prop_id, property_name, property_value)
values
(nextval('xhb_config_prop_seq'), 'scheduledtasks.pdda', 'transformtask,cppformattingtask,cppstagingtask,pddabaistask,dlnotifiertask,lighthousetask,cleardowntask,courtellisttask,cathtask'),
(nextval('xhb_config_prop_seq'), 'COURTEL_LIST_AMOUNT', '5'),
(nextval('xhb_config_prop_seq'), 'COURTEL_MAX_RETRY', '5'),
(nextval('xhb_config_prop_seq'), 'MESSAGE_LOOKUP_DELAY', '60'),
(nextval('xhb_config_prop_seq'), 'CPPX_SchemaWL', 'WarnedList-v1-0.xsd'),
(nextval('xhb_config_prop_seq'), 'CPPX_SchemaFL', 'FirmList-v1-0.xsd'),
(nextval('xhb_config_prop_seq'), 'CPPX_SchemaWP', 'CPPX_InternetWebPage-v1-0.xsd')
on conflict (property_name)
do update
set property_value = EXCLUDED.property_value
where POSITION(EXCLUDED.property_value in xhb_config_prop.property_value) = 0;
