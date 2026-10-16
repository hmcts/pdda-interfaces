insert into pdda.xhb_disp_mgr_property (property_id, property_name, property_value)
values (((select max(property_id) from pdda.xhb_disp_mgr_property)+1), 'cdu.restart.job.cron','0 7 * * *');

COMMIT;
