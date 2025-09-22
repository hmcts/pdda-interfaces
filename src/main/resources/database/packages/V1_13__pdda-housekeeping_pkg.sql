/*
	Name: Do_PDDA_Housekeeping.sql
	Description: 	Housekeeping stored procedures for PDDA and PDM.
			See each individual stored procedure for details on what each one does.
	Author: Scott Atwell
	Date: Update: September 19 2025
*/

SET client_encoding TO 'UTF8';

DROP SCHEMA IF EXISTS pdda_housekeeping_pkg CASCADE;
CREATE SCHEMA IF NOT EXISTS pdda_housekeeping_pkg;



--------------------------------------
-- Procedure: pdda_housekeeping_pkg.clear_obsolete_messages
-- Description: Housekeeping for marking stale messages so that they will not be processed
--------------------------------------
CREATE OR REPLACE PROCEDURE pdda_housekeeping_pkg.clear_obsolete_messages (
	p_errortext_in VARCHAR DEFAULT 'MESSAGE OUT OF DATE - SET TO FAIL',
	p_limit_in INTEGER DEFAULT 5000
)
LANGUAGE plpgsql
AS $$
DECLARE
	v_start_time TIMESTAMP := clock_timestamp();
	v_end_time TIMESTAMP;
	v_updated_pdda_message INTEGER := 0;
	v_updated_cpp_inbound INTEGER := 0;
	v_hk_result_id INTEGER;
BEGIN
	RAISE NOTICE 'Procedure invoked';
	-- Log the housekeeping run start
	BEGIN
    	INSERT INTO pdda.pdda_hk_results (job_name, job_start, status)
		VALUES (
		  'CLEAR OBSOLETE MESSAGES (' || p_errortext_in || ', ' || COALESCE(p_limit_in::TEXT, 'NULL') || ')',
		  v_start_time,
		  'IN_PROGRESS'
		)
		RETURNING hk_result_id INTO v_hk_result_id;
	
		RAISE NOTICE 'Step 1: Inserted initial hk_result_id = %', v_hk_result_id;

  	EXCEPTION WHEN OTHERS THEN
		RAISE NOTICE 'Failed to insert into pdda_hk_results: %', SQLERRM;
		RETURN;
	END;

	-- Part 1 - Housekeep data on XHB_PDDA_MESSAGE
	WITH rows_to_update AS (
		SELECT pdda_message_id
		FROM pdda.xhb_pdda_message
		WHERE 
			cp_document_status = 'VN'
			AND creation_date < CURRENT_DATE
			AND (
				cp_document_name LIKE 'PublicDisplay_%' OR 
				cp_document_name LIKE 'WebPage_%'
			)
		ORDER BY creation_date ASC
		LIMIT p_limit_in
	)
	UPDATE pdda.xhb_pdda_message m
	SET 
		cp_document_status = 'VF',
		error_message = p_errortext_in
	FROM rows_to_update r
	WHERE m.pdda_message_id = r.pdda_message_id;

	GET DIAGNOSTICS v_updated_pdda_message = ROW_COUNT;
	RAISE NOTICE 'Step 2: Updated % pdda_message rows', v_updated_pdda_message;

	-- Part 2 - Housekeep data on XHB_CPP_STAGING_INBOUND
	WITH rows_to_update AS (
		SELECT cpp_staging_inbound_id
		FROM pdda.xhb_cpp_staging_inbound
		WHERE 
			validation_status = 'NP'
			AND time_loaded < CURRENT_DATE
			AND (
				document_name LIKE 'PublicDisplay_%' OR 
				document_name LIKE 'WebPage_%'
			)
		ORDER BY time_loaded ASC
		LIMIT p_limit_in
	)
	UPDATE pdda.xhb_cpp_staging_inbound c
	SET 
		validation_status = 'VF',
		validation_error_message = p_errortext_in
	FROM rows_to_update r
	WHERE c.cpp_staging_inbound_id = r.cpp_staging_inbound_id;

	GET DIAGNOSTICS v_updated_cpp_inbound = ROW_COUNT;
	RAISE NOTICE 'Step 3: Updated % cpp_staging rows', v_updated_cpp_inbound;

	-- Complete the housekeeping log
	v_end_time := clock_timestamp();
	UPDATE pdda.pdda_hk_results
	SET 
		job_end = v_end_time,
		job_text = 'Updated:: XHB_PDDA_MESSAGE: ' || v_updated_pdda_message || 
		           ' records; XHB_CPP_STAGING_INBOUND: ' || v_updated_cpp_inbound || ' records',
		error_message = NULL,
		status = 'SUCCESS'
	WHERE hk_result_id = v_hk_result_id;

EXCEPTION WHEN OTHERS THEN
	-- Log failure
	v_end_time := clock_timestamp();
	UPDATE pdda.pdda_hk_results
	SET 
		job_end = v_end_time,
		error_message = SQLERRM,
		status = 'FAILURE'
	WHERE hk_result_id = v_hk_result_id;
END;
$$;



--------------------------------------
-- Procedure: pdda_housekeeping_pkg.clear_audit_tables
-- Description: Housekeeping for clearing down audit tables
--------------------------------------
CREATE OR REPLACE PROCEDURE pdda_housekeeping_pkg.clear_audit_tables (
    p_env_in VARCHAR DEFAULT 'NON-PROD',
    p_limit_in INTEGER DEFAULT 5000
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_start_time TIMESTAMP := clock_timestamp();
    v_end_time TIMESTAMP;
    v_deleted_aud_pdda INTEGER := 0;
    v_deleted_aud_cpp_inbound INTEGER := 0;
    v_deleted_aud_cpp_formatting INTEGER := 0;
    v_deleted_aud_display_store INTEGER := 0;
    v_hk_result_id INTEGER;
BEGIN
    -- Insert housekeeping log entry
    BEGIN
        INSERT INTO pdda.pdda_hk_results (job_name, job_start, status)
        VALUES (
            'CLEAR AUDIT TABLES (' || p_env_in || ', ' || COALESCE(p_limit_in::TEXT, 'NULL') || ')',
            v_start_time,
            'IN_PROGRESS'
        )
        RETURNING hk_result_id INTO v_hk_result_id;
    EXCEPTION WHEN OTHERS THEN
        RAISE NOTICE 'Failed to insert into pdda_hk_results: %', SQLERRM;
        RETURN;
    END;

    -- Part 1: AUD_PDDA_MESSAGE
    DELETE FROM pdda.aud_pdda_message
    WHERE ctid IN (
        SELECT ctid FROM pdda.aud_pdda_message
        ORDER BY last_update_date ASC
        LIMIT p_limit_in
    );
    GET DIAGNOSTICS v_deleted_aud_pdda = ROW_COUNT;

    -- Part 2: AUD_CPP_STAGING_INBOUND
    DELETE FROM pdda.aud_cpp_staging_inbound
    WHERE ctid IN (
        SELECT ctid FROM pdda.aud_cpp_staging_inbound
        ORDER BY last_update_date ASC
        LIMIT p_limit_in
    );
    GET DIAGNOSTICS v_deleted_aud_cpp_inbound = ROW_COUNT;

    -- Part 3: AUD_CPP_FORMATTING
    DELETE FROM pdda.aud_cpp_formatting
    WHERE ctid IN (
        SELECT ctid FROM pdda.aud_cpp_formatting
        ORDER BY last_update_date ASC
        LIMIT p_limit_in
    );
    GET DIAGNOSTICS v_deleted_aud_cpp_formatting = ROW_COUNT;

    -- Part 4: AUD_DISPLAY_STORE
    DELETE FROM pdda.aud_display_store
	WHERE ctid IN (
	    SELECT ctid
	    FROM pdda.aud_display_store
	    ORDER BY last_update_date ASC
	    LIMIT p_limit_in
	);
    GET DIAGNOSTICS v_deleted_aud_display_store = ROW_COUNT;

    -- Finalize housekeeping log
    v_end_time := clock_timestamp();
    UPDATE pdda.pdda_hk_results
    SET
        job_end = v_end_time,
        job_text = 'Updated:: AUD_PDDA_MESSAGE: ' || v_deleted_aud_pdda ||
                   ' records; AUD_CPP_STAGING_INBOUND: ' || v_deleted_aud_cpp_inbound ||
                   ' records; AUD_CPP_FORMATTING: ' || v_deleted_aud_cpp_formatting ||
                   ' records; AUD_DISPLAY_STORE: ' || v_deleted_aud_display_store || ' records',
        error_message = NULL,
        status = 'SUCCESS'
    WHERE hk_result_id = v_hk_result_id;

EXCEPTION WHEN OTHERS THEN
    v_end_time := clock_timestamp();
    UPDATE pdda.pdda_hk_results
    SET
        job_end = v_end_time,
        error_message = SQLERRM,
        status = 'FAILURE'
    WHERE hk_result_id = v_hk_result_id;
END;
$$;



--------------------------------------
-- Procedure: pdda_housekeeping_pkg.clear_old_records
-- Description: Housekeeping for clearing records older than N days
--------------------------------------
CREATE OR REPLACE PROCEDURE pdda_housekeeping_pkg.clear_old_records (
    p_days_in INTEGER DEFAULT 14,
    p_limit_in INTEGER DEFAULT 100
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_start_time TIMESTAMP := clock_timestamp();
    v_end_time TIMESTAMP;
    v_threshold_date TIMESTAMP := CURRENT_DATE - COALESCE(p_days_in, p_days_in);
    v_hk_result_id INTEGER;
    v_deleted_xhb_pdda INTEGER := 0;
    v_deleted_xhb_cpp_inbound INTEGER := 0;
    v_deleted_xhb_clob INTEGER := 0;
    v_deleted_xhb_formatting INTEGER := 0;
    v_deleted_xhb_cpp_formatting INTEGER := 0;
    v_deleted_xhb_hearing_list INTEGER := 0;
    v_deleted_xhb_sitting INTEGER := 0;
    v_deleted_xhb_hearing INTEGER := 0;
	v_deleted_xhb_sched_hearing_defendant INTEGER := 0;
	v_deleted_xhb_scheduled_hearing INTEGER := 0;
	v_deleted_xhb_sched_hearing_attendee INTEGER := 0;
	v_deleted_xhb_cpp_list INTEGER := 0;
BEGIN
	RAISE NOTICE 'Invoked procedure';
    -- Insert housekeeping log entry
    BEGIN
        INSERT INTO pdda.pdda_hk_results (job_name, job_start, status)
        VALUES (
            'CLEAR OLD RECORDS (' || p_days_in || ', ' || COALESCE(p_limit_in::TEXT, 'NULL') || ')',
            v_start_time,
            'IN_PROGRESS'
        )
        RETURNING hk_result_id INTO v_hk_result_id;
    EXCEPTION WHEN OTHERS THEN
        RAISE NOTICE 'Failed to insert into pdda_hk_results: %', SQLERRM;
        RETURN;
    END;
	RAISE NOTICE 'Setup pdda_hk_results record';

    -- Delete order based on constraints
    
	-- 0. XHB_SH_JUDGE
	DELETE FROM pdda.xhb_sh_judge
    WHERE ctid IN (
        SELECT ctid FROM pdda.xhb_sh_judge
        WHERE last_update_date < v_threshold_date
        ORDER BY last_update_date ASC
        LIMIT p_limit_in
    );
	GET DIAGNOSTICS v_deleted_xhb_sched_hearing_attendee = ROW_COUNT;
	RAISE NOTICE 'Deleted data from XHB_SH_JUDGE';

	-- 1. XHB_SCHED_HEARING_ATTENDEE
    DELETE FROM pdda.xhb_sched_hearing_attendee
    WHERE ctid IN (
        SELECT ctid FROM pdda.xhb_sched_hearing_attendee xsha
        WHERE last_update_date < v_threshold_date
			AND NOT EXISTS (
				SELECT 1 FROM pdda.xhb_sh_judge xsj WHERE xsj.sh_attendee_id = xsha.sh_attendee_id
			)
        ORDER BY last_update_date ASC
        LIMIT p_limit_in
    );
    GET DIAGNOSTICS v_deleted_xhb_sched_hearing_attendee = ROW_COUNT;
	RAISE NOTICE 'Deleted data from XHB_SCHED_HEARING_ATTENDEE';

	-- 2. XHB_SCHED_HEARING_DEFENDANT
    DELETE FROM pdda.xhb_sched_hearing_defendant
    WHERE ctid IN (
        SELECT ctid FROM pdda.xhb_sched_hearing_defendant
        WHERE last_update_date < v_threshold_date
        ORDER BY last_update_date ASC
        LIMIT p_limit_in
    );
    GET DIAGNOSTICS v_deleted_xhb_sched_hearing_defendant = ROW_COUNT;
	RAISE NOTICE 'Deleted data from XHB_SCHED_HEARING_DEFENDANT';

	-- 3. XHB_SCHEDULED_HEARING
    DELETE FROM pdda.xhb_scheduled_hearing
    WHERE ctid IN (
        SELECT ctid FROM pdda.xhb_scheduled_hearing xsh
        WHERE last_update_date < v_threshold_date
		  AND NOT EXISTS (
	        SELECT 1 FROM pdda.xhb_sched_hearing_attendee xsha WHERE xsha.scheduled_hearing_id = xsh.scheduled_hearing_id
	      )
        ORDER BY last_update_date ASC
        LIMIT p_limit_in
    );
    GET DIAGNOSTICS v_deleted_xhb_scheduled_hearing = ROW_COUNT;
	RAISE NOTICE 'Deleted data from XHB_SCHEDULED_HEARING';

    -- 4. XHB_SITTING
    DELETE FROM pdda.xhb_sitting
    WHERE ctid IN (
        SELECT ctid FROM pdda.xhb_sitting xs
        WHERE last_update_date < v_threshold_date
		  AND NOT EXISTS (
	        SELECT 1 FROM pdda.xhb_scheduled_hearing xsh WHERE xsh.sitting_id = xs.sitting_id
	      )
        ORDER BY last_update_date ASC
        LIMIT p_limit_in
    );
    GET DIAGNOSTICS v_deleted_xhb_sitting = ROW_COUNT;
	RAISE NOTICE 'Deleted data from XHB_SITTING';

    -- 5. XHB_HEARING
    DELETE FROM pdda.xhb_hearing
    WHERE ctid IN (
        SELECT ctid FROM pdda.xhb_hearing xh
        WHERE last_update_date < v_threshold_date
		  AND NOT EXISTS (
	        SELECT 1 FROM pdda.xhb_scheduled_hearing xsh WHERE xsh.hearing_id = xh.hearing_id
	      )
        ORDER BY last_update_date ASC
        LIMIT p_limit_in
    );
    GET DIAGNOSTICS v_deleted_xhb_hearing = ROW_COUNT;
	RAISE NOTICE 'Deleted data from XHB_HEARING';

    -- 6. XHB_HEARING_LIST
	DELETE FROM pdda.xhb_hearing_list
	WHERE ctid IN (
	    SELECT ctid
	    FROM pdda.xhb_hearing_list hl
	    WHERE last_update_date < v_threshold_date
	      AND NOT EXISTS (
	        SELECT 1 FROM pdda.xhb_sitting s WHERE s.list_id = hl.list_id
	      )
	    ORDER BY last_update_date ASC
	    LIMIT p_limit_in
	);
    GET DIAGNOSTICS v_deleted_xhb_hearing_list = ROW_COUNT;
	RAISE NOTICE 'Deleted data from XHB_HEARING_LIST';

    -- 7. XHB_FORMATTING
    DELETE FROM pdda.xhb_formatting
    WHERE ctid IN (
        SELECT ctid FROM pdda.xhb_formatting
        WHERE last_update_date < v_threshold_date
        ORDER BY last_update_date ASC
        LIMIT p_limit_in
    );
    GET DIAGNOSTICS v_deleted_xhb_formatting = ROW_COUNT;
	RAISE NOTICE 'Deleted data from XHB_FORMATTING';

    -- 8. XHB_CPP_FORMATTING
    DELETE FROM pdda.xhb_cpp_formatting
    WHERE ctid IN (
	SELECT ctid FROM pdda.xhb_cpp_formatting xcf
        WHERE last_update_date < v_threshold_date
	  AND NOT EXISTS (
	    SELECT 1 FROM pdda.xhb_cpp_staging_inbound xcsi WHERE xcsi.cpp_staging_inbound_id = xcf.staging_table_id
	  )
        ORDER BY last_update_date ASC
        LIMIT p_limit_in
    );
    GET DIAGNOSTICS v_deleted_xhb_cpp_formatting = ROW_COUNT;
	RAISE NOTICE 'Deleted data from XHB_CPP_FORMATTING';

	-- 9. XHB_CPP_LIST
    DELETE FROM pdda.xhb_cpp_list
    WHERE ctid IN (
        SELECT ctid FROM pdda.xhb_cpp_list
        WHERE last_update_date < v_threshold_date
        ORDER BY last_update_date ASC
        LIMIT p_limit_in
    );
    GET DIAGNOSTICS v_deleted_xhb_cpp_list = ROW_COUNT;
	RAISE NOTICE 'Deleted data from XHB_CPP_LIST';

    -- 10. XHB_PDDA_MESSAGE
    DELETE FROM pdda.xhb_pdda_message
    WHERE ctid IN (
        SELECT ctid FROM pdda.xhb_pdda_message pm
        WHERE last_update_date < v_threshold_date
	  AND NOT EXISTS (
	    SELECT 1 FROM pdda.xhb_clob c WHERE c.clob_id = pm.pdda_message_data_id
	  )
        ORDER BY last_update_date ASC
        LIMIT p_limit_in
    );
    GET DIAGNOSTICS v_deleted_xhb_pdda = ROW_COUNT;
	RAISE NOTICE 'Deleted data from XHB_PDDA_MESSAGE';

    -- 11. XHB_CPP_STAGING_INBOUND
    DELETE FROM pdda.xhb_cpp_staging_inbound
    WHERE ctid IN (
        SELECT ctid FROM pdda.xhb_cpp_staging_inbound csi
        WHERE last_update_date < v_threshold_date
	  AND NOT EXISTS (
	    SELECT 1 FROM pdda.xhb_pdda_message pm WHERE pm.cpp_staging_inbound_id = csi.cpp_staging_inbound_id
	  )
	  AND NOT EXISTS (
	    SELECT 1 FROM pdda.xhb_cpp_formatting xcf WHERE xcf.staging_table_id = csi.cpp_staging_inbound_id
	  )
        ORDER BY last_update_date ASC
        LIMIT p_limit_in
    );
    GET DIAGNOSTICS v_deleted_xhb_cpp_inbound = ROW_COUNT;
    RAISE NOTICE 'Deleted data from XHB_CPP_STAGING_INBOUND';

    -- 12. XHB_CLOB
    DELETE FROM pdda.xhb_clob
    WHERE ctid IN (
        SELECT ctid FROM pdda.xhb_clob xc
        WHERE last_update_date < v_threshold_date
	  AND NOT EXISTS (
	    SELECT 1 FROM pdda.xhb_pdda_message pm WHERE pm.pdda_message_data_id = xc.clob_id
	  )
	  AND NOT EXISTS (
	    SELECT 1 FROM pdda.xhb_cpp_staging_inbound xcsi WHERE xcsi.clob_id = xc.clob_id
	  )
	  AND NOT EXISTS (
	    SELECT 1 FROM pdda.xhb_cpp_formatting xcf WHERE xcf.xml_document_clob_id = xc.clob_id
	  )
	  AND NOT EXISTS (
	    SELECT 1 FROM pdda.xhb_formatting xcf WHERE xcf.xml_document_clob_id = xc.clob_id
	  )
	  AND NOT EXISTS (
	    SELECT 1 FROM pdda.xhb_cpp_list xcl WHERE xcl.list_clob_id = xc.clob_id
	  )
	  AND NOT EXISTS (
	    SELECT 1 FROM pdda.xhb_cpp_list xcl WHERE xcl.merged_clob_id = xc.clob_id
	  )
        ORDER BY last_update_date ASC
        LIMIT p_limit_in
    );
    GET DIAGNOSTICS v_deleted_xhb_clob = ROW_COUNT;
    RAISE NOTICE 'Deleted data from XHB_CLOB';


    -- Finalize housekeeping log
    RAISE NOTICE 'About to update pdda_hk_results with final status';
    v_end_time := clock_timestamp();
    UPDATE pdda.pdda_hk_results
    SET
        job_end = v_end_time,
        job_text = 'Updated:: XHB_PDDA_MESSAGE: ' || v_deleted_xhb_pdda ||
                   '; XHB_CPP_STAGING_INBOUND: ' || v_deleted_xhb_cpp_inbound ||
                   '; XHB_CLOB: ' || v_deleted_xhb_clob ||
                   '; XHB_FORMATTING: ' || v_deleted_xhb_formatting ||
                   '; XHB_CPP_FORMATTING: ' || v_deleted_xhb_cpp_formatting ||
		   '; XHB_CPP_LIST: ' || v_deleted_xhb_cpp_list ||
                   '; XHB_HEARING_LIST: ' || v_deleted_xhb_hearing_list ||
                   '; XHB_SITTING: ' || v_deleted_xhb_sitting ||
                   '; XHB_HEARING: ' || v_deleted_xhb_hearing ||
		   '; XHB_SCHED_HEARING_DEFENDANT: ' || v_deleted_xhb_sched_hearing_defendant ||
		   '; XHB_SCHEDULED_HEARING: ' || v_deleted_xhb_scheduled_hearing ||
		   '; XHB_SCHED_HEARING_ATTENDEE: ' || v_deleted_xhb_sched_hearing_attendee,
        error_message = NULL,
        status = 'SUCCESS'
    WHERE hk_result_id = v_hk_result_id;
	RAISE NOTICE 'Updated PDDA_HK_RESULTS table';

EXCEPTION WHEN OTHERS THEN
    v_end_time := clock_timestamp();
    UPDATE pdda.pdda_hk_results
    SET
        job_end = v_end_time,
        error_message = SQLERRM,
        status = 'FAILURE'
    WHERE hk_result_id = v_hk_result_id;
END;
$$;




--------------------------------------
-- Procedure: pdda_housekeeping_pkg.nullify_live_display_fields
-- Description: Sets scheduled_hearing_id and status columns to NULL for all rows in xhb_cr_live_display
-- 		This should be run every night, and must be run before any housekeeping procedures
--------------------------------------
CREATE OR REPLACE PROCEDURE pdda_housekeeping_pkg.nullify_live_display_fields()
LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE pdda.xhb_cr_live_display
    SET 
        scheduled_hearing_id = NULL,
        status = NULL;

    RAISE NOTICE 'Nullified scheduled_hearing_id and status for all rows in xhb_cr_live_display.';
END;
$$;




