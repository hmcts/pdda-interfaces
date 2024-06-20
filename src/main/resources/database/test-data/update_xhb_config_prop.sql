--Update the scheduler tasks to include pddabaistask
update xhb_config_prop
set property_value = 'transformtask,cppstagingtask,cppformattingtask,pddabaistask'
where property_name = 'scheduledtasks.pdda'
and property_value != 'transformtask,cppstagingtask,cppformattingtask,pddabaistask';

--Update sftpConfig properties
update xhb_config_prop
	set property_value = case property_name
		when 'PDDA_BAIS_SFTP_HOSTNAME' then 'x.x.x.x:22'
		when 'PDDA_BAIS_SFTP_USERNAME' then '*macUsername*'
		when 'PDDA_BAIS_SFTP_PASSWORD' then '*macAccountPassword*'
		when 'PDDA_BAIS_SFTP_UPLOAD_LOCATION' then '/Users/testUser/temp_bais_events_folder/'
		when 'PDDA_BAIS_CP_SFTP_USERNAME' then '*macUsername*'
		when 'PDDA_BAIS_CP_SFTP_PASSWORD' then '*macAccountPassword*'
		when 'PDDA_BAIS_CP_SFTP_UPLOAD_LOCATION' then '/Users/testUser/temp_bais_events_folder/'
		else property_value
		end
where property_name in('PDDA_BAIS_SFTP_HOSTNAME',
					   'PDDA_BAIS_SFTP_USERNAME',
					   'PDDA_BAIS_SFTP_PASSWORD',
					   'PDDA_BAIS_SFTP_UPLOAD_LOCATION',
					   'PDDA_BAIS_CP_SFTP_USERNAME',
					   'PDDA_BAIS_CP_SFTP_PASSWORD',
					   'PDDA_BAIS_CP_SFTP_UPLOAD_LOCATION');
