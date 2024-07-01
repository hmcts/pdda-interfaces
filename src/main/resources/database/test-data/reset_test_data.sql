-- Set Court 80 as CPP
update xhb_court
set cpp_court = 'Y'
where court_id = 80;

-- Reset the cppStagingINbound test data
update XHB_CPP_STAGING_INBOUND
set validation_Status = 'NP', processing_Status = null, time_loaded = current_timestamp
where cpp_staging_inbound_id = 321001;
delete from XHB_CPP_FORMATTING
where staging_table_id = 321001;

-- Reset cppFormattingControllerBean test data
update XHB_CPP_FORMATTING
set format_Status = 'ND', creation_date = current_timestamp
where cpp_formatting_id = 621000;

-- Reset formattingControllerBean test data
update XHB_FORMATTING
set format_Status = 'ND', creation_date = current_timestamp, formatted_document_blob_id = null
where formatting_id in (621000, 621001, 621003);

-- Reset CourtListQuery / AllCaseStatusQuery
update xhb_hearing_list
set start_date = current_date, end_date = current_date
where list_id = 1;

-- Clear display store
delete from xhb_display_store
where created_by = 'PDDA';

commit;
