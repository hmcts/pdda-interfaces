SET client_encoding TO 'UTF8';

DROP SCHEMA IF EXISTS xhb_housekeeping_pkg CASCADE;
CREATE SCHEMA IF NOT EXISTS xhb_housekeeping_pkg;

CREATE OR REPLACE FUNCTION xhb_housekeeping_pkg.get_config_property (p_property_name XHB_CONFIG_PROP.PROPERTY_NAME%TYPE) RETURNS XHB_CONFIG_PROP.PROPERTY_VALUE%TYPE AS $body$
DECLARE

    v_result XHB_CONFIG_PROP.PROPERTY_VALUE%TYPE;
    C_cp CURSOR FOR
    SELECT PROPERTY_VALUE
      FROM XHB_CONFIG_PROP
     WHERE PROPERTY_NAME = p_property_name;

BEGIN
    OPEN C_cp;
    FETCH C_cp INTO v_result;
    CLOSE C_cp;
    RETURN v_result;
END;

$body$
LANGUAGE PLPGSQL
;
-- REVOKE ALL ON FUNCTION xhb_housekeeping_pkg.get_config_property (p_property_name XHB_CONFIG_PROP.PROPERTY_NAME%TYPE) FROM PUBLIC;


CREATE OR REPLACE FUNCTION xhb_housekeeping_pkg.get_max_cases_to_delete () RETURNS bigint AS $body$
BEGIN
	RETURN coalesce((xhb_housekeeping_pkg.get_config_property(p_property_name => 'BATCH_CASES_ALLOWED_TO_DELETE'))::numeric ,0);
END;

$body$
LANGUAGE PLPGSQL
;
-- REVOKE ALL ON FUNCTION xhb_housekeeping_pkg.get_max_cases_to_delete () FROM PUBLIC;


CREATE OR REPLACE PROCEDURE xhb_housekeeping_pkg.update_case_status (p_case_id XHB_CASE.CASE_ID%TYPE, p_case_status XHB_CASE.CASE_STATUS%TYPE) AS $body$
BEGIN
	UPDATE XHB_CASE
	SET case_status = p_case_status
	WHERE case_id = p_case_id;
END;

$body$
LANGUAGE PLPGSQL
;
-- REVOKE ALL ON PROCEDURE xhb_housekeeping_pkg.update_case_status (p_case_id XHB_CASE.CASE_ID%TYPE, p_case_status XHB_CASE.CASE_STATUS%TYPE) FROM PUBLIC;


CREATE OR REPLACE FUNCTION xhb_housekeeping_pkg.get_next_hk_run_id () RETURNS XHB_HK_ERROR_LOG.HK_RUN_ID%TYPE AS $body$
DECLARE

	v_hk_run_id XHB_HK_ERROR_LOG.HK_RUN_ID%TYPE;

BEGIN
	SELECT nextval('hk_run_id_seq') INTO STRICT v_hk_run_id;
	RETURN v_hk_run_id;
END;

$body$
LANGUAGE PLPGSQL
;
-- REVOKE ALL ON FUNCTION xhb_housekeeping_pkg.get_next_hk_run_id () FROM PUBLIC;


CREATE OR REPLACE FUNCTION xhb_housekeeping_pkg.get_next_hk_cpp_run_id () RETURNS XHB_HK_CPP_RESULTS.HK_CPP_RUN_ID%TYPE AS $body$
DECLARE

     v_hk_cpp_run_id XHB_HK_CPP_RESULTS.HK_CPP_RUN_ID%TYPE;

BEGIN
	SELECT nextval('hk_cpp_run_id_seq') INTO STRICT v_hk_cpp_run_id;
	RETURN v_hk_cpp_run_id;
END;

$body$
LANGUAGE PLPGSQL
;
-- REVOKE ALL ON FUNCTION xhb_housekeeping_pkg.get_next_hk_cpp_run_id () FROM PUBLIC;


CREATE OR REPLACE PROCEDURE xhb_housekeeping_pkg.log_case_deletion_error (p_hk_run_id XHB_HK_ERROR_LOG.HK_RUN_ID%TYPE, p_court_id XHB_HK_ERROR_LOG.COURT_ID%TYPE, p_case_id XHB_HK_ERROR_LOG.CASE_ID%TYPE, p_case_type XHB_HK_ERROR_LOG.CASE_TYPE%TYPE, p_case_no XHB_HK_ERROR_LOG.CASE_NO%TYPE, p_error_message XHB_HK_ERROR_LOG.ERROR_MESSAGE%TYPE) AS $body$
BEGIN
     -- Log the case as failed deletion
	CALL xhb_housekeeping_pkg.update_case_status(
		p_case_id => p_case_id,
		p_case_status => 'F');

     -- this will log an error for an individual case
     INSERT INTO XHB_HK_ERROR_LOG(hk_run_id,   case_no,   case_type,   court_id,   case_id,   error_message)
          VALUES (p_hk_run_id, p_case_no, p_case_type, p_court_id, p_case_id, p_error_message);

END;

$body$
LANGUAGE PLPGSQL
;
-- REVOKE ALL ON PROCEDURE xhb_housekeeping_pkg.log_case_deletion_error (p_hk_run_id XHB_HK_ERROR_LOG.HK_RUN_ID%TYPE, p_court_id XHB_HK_ERROR_LOG.COURT_ID%TYPE, p_case_id XHB_HK_ERROR_LOG.CASE_ID%TYPE, p_case_type XHB_HK_ERROR_LOG.CASE_TYPE%TYPE, p_case_no XHB_HK_ERROR_LOG.CASE_NO%TYPE, p_error_message XHB_HK_ERROR_LOG.ERROR_MESSAGE%TYPE) FROM PUBLIC;


CREATE OR REPLACE FUNCTION xhb_housekeeping_pkg.execute_deletion (p_sql text, p_param1 bigint, p_param2 bigint DEFAULT NULL) RETURNS integer AS $body$
DECLARE

     l_delete	varchar(4000);
	 rowcount	integer;

BEGIN
     -- Delete the main record
     l_delete := p_sql;
     IF p_param2 IS NOT NULL THEN
	   EXECUTE l_delete USING p_param1, p_param2;
	 ELSE
	   EXECUTE l_delete USING p_param1;
	 END IF;
	 GET DIAGNOSTICS rowcount = ROW_COUNT;

     -- Delete the audit record
     l_delete := REPLACE(l_delete, 'XHB', 'AUD');
     IF p_param2 IS NOT NULL THEN
	   EXECUTE l_delete USING p_param1, p_param2;
	 ELSE
	   EXECUTE l_delete USING p_param1;
	 END IF;

	 RETURN rowcount;
END;

$body$
LANGUAGE PLPGSQL
;
-- REVOKE ALL ON FUNCTION xhb_housekeeping_pkg.execute_deletion (p_sql text, p_param1 bigint, p_param2 bigint DEFAULT NULL) FROM PUBLIC;


CREATE OR REPLACE PROCEDURE xhb_housekeeping_pkg.insert_hk_log (p_run_type text,p_case_limit bigint DEFAULT 0,p_running_list bigint  DEFAULT NULL,p_warned_list bigint  DEFAULT NULL,p_firm_list bigint  DEFAULT NULL,p_daily_list bigint  DEFAULT NULL) AS $body$
BEGIN
    PERFORM set_config('l_hk_run_results.l_hk_run_id', xhb_housekeeping_pkg.get_next_hk_run_id()::text, false);
	PERFORM set_config('l_hk_run_results.case_status', NULL, false);
	PERFORM set_config('l_hk_run_results.case_error_message', NULL, false);
	PERFORM set_config('l_hk_run_results.cases_deleted', '0', false);
	PERFORM set_config('l_hk_run_results.cases_error', '0', false);

	INSERT INTO xhb_hk_results(hk_run_id
    ,run_type
    ,run_start_date
    ,cases_deleted
    ,lists_deleted
    ,cases_error
    ,case_limit
    ,running_list
    ,warned_list
    ,firm_list
    ,daily_list)
    VALUES (current_setting('l_hk_run_results.l_hk_run_id')::bigint
    ,UPPER(p_run_type)
    ,clock_timestamp()
    ,0
    ,0
    ,0
    ,p_case_limit
    ,p_running_list
    ,p_warned_list
    ,p_firm_list
    ,p_daily_list);

	--COMMIT;
END;

$body$
LANGUAGE PLPGSQL
;
-- REVOKE ALL ON PROCEDURE xhb_housekeeping_pkg.insert_hk_log (p_run_type text,p_case_limit bigint DEFAULT 0,p_running_list bigint  DEFAULT NULL,p_warned_list bigint  DEFAULT NULL,p_firm_list bigint  DEFAULT NULL,p_daily_list bigint  DEFAULT NULL) FROM PUBLIC;


CREATE OR REPLACE PROCEDURE xhb_housekeeping_pkg.update_log () AS $body$
BEGIN

	UPDATE xhb_hk_results SET
    case_status        = coalesce(current_setting('l_hk_run_results.case_status')::varchar(1), case_status),
    case_error_message = coalesce(current_setting('l_hk_run_results.case_error_message')::varchar(2000), case_error_message),
    cases_error		  = coalesce(current_setting('l_hk_run_results.cases_error')::integer, cases_error),
    cases_deleted	  = coalesce(current_setting('l_hk_run_results.cases_deleted')::integer, cases_deleted)
    WHERE  hk_run_id = current_setting('l_hk_run_results.l_hk_run_id')::bigint;

    --COMMIT;

END;

$body$
LANGUAGE PLPGSQL
;
-- REVOKE ALL ON PROCEDURE xhb_housekeeping_pkg.update_log () FROM PUBLIC;


CREATE OR REPLACE PROCEDURE xhb_housekeeping_pkg.insert_hk_cpp_log () AS $body$
BEGIN
    PERFORM set_config('l_hk_cpp_run_results.l_hk_cpp_run_id', xhb_housekeeping_pkg.get_next_hk_cpp_run_id()::text, false);
	PERFORM set_config('l_hk_cpp_run_results.list_status', NULL, false);
	PERFORM set_config('l_hk_cpp_run_results.list_error_message', NULL, false);
	PERFORM set_config('l_hk_cpp_run_results.list_error', '0', false);
	PERFORM set_config('l_hk_cpp_run_results.list_deleted', '0', false);
	PERFORM set_config('l_hk_cpp_run_results.iwp_status', NULL, false);
	PERFORM set_config('l_hk_cpp_run_results.iwp_error_message', NULL, false);
	PERFORM set_config('l_hk_cpp_run_results.iwp_error', '0', false);
	PERFORM set_config('l_hk_cpp_run_results.iwp_deleted', '0', false);
	PERFORM set_config('l_hk_cpp_run_results.pd_status', NULL, false);
	PERFORM set_config('l_hk_cpp_run_results.pd_error_message', NULL, false);
	PERFORM set_config('l_hk_cpp_run_results.pd_error', '0', false);
	PERFORM set_config('l_hk_cpp_run_results.pd_deleted', '0', false);
	PERFORM set_config('l_hk_cpp_run_results.staging_status', NULL, false);
	PERFORM set_config('l_hk_cpp_run_results.staging_error_message', NULL, false);
	PERFORM set_config('l_hk_cpp_run_results.staging_error', '0', false);
	PERFORM set_config('l_hk_cpp_run_results.staging_deleted', '0', false);

    INSERT INTO XHB_HK_CPP_RESULTS(hk_cpp_run_id, run_start_date)
    VALUES (current_setting('l_hk_cpp_run_results.l_hk_cpp_run_id')::bigint, clock_timestamp());
    --COMMIT;
END;

$body$
LANGUAGE PLPGSQL
;
-- REVOKE ALL ON PROCEDURE xhb_housekeeping_pkg.insert_hk_cpp_log () FROM PUBLIC;


CREATE OR REPLACE PROCEDURE xhb_housekeeping_pkg.update_hk_cpp_log () AS $body$
BEGIN
    UPDATE XHB_HK_CPP_RESULTS SET
    list_status        = coalesce(current_setting('l_hk_cpp_run_results.list_status')::varchar(1), LIST_STATUS),
    list_error_message = coalesce(current_setting('l_hk_cpp_run_results.list_error_message')::varchar(2000), LIST_ERROR_MESSAGE),
    list_error		  = coalesce(current_setting('l_hk_cpp_run_results.list_error')::integer, LIST_ERROR),
    list_deleted		  = coalesce(current_setting('l_hk_cpp_run_results.list_deleted')::integer, LIST_DELETED),
    iwp_status		  = coalesce(current_setting('l_hk_cpp_run_results.iwp_status')::varchar(1), IWP_STATUS),
    iwp_error_message  = coalesce(current_setting('l_hk_cpp_run_results.iwp_error_message')::varchar(2000), IWP_ERROR_MESSAGE),
    iwp_error		  = coalesce(current_setting('l_hk_cpp_run_results.iwp_error')::integer, IWP_ERROR),
    iwp_deleted		  = coalesce(current_setting('l_hk_cpp_run_results.iwp_deleted')::integer, IWP_DELETED),
    pd_status		  = coalesce(current_setting('l_hk_cpp_run_results.pd_status')::varchar(1), PD_STATUS),
    pd_error_message   = coalesce(current_setting('l_hk_cpp_run_results.pd_error_message')::varchar(2000), PD_ERROR_MESSAGE),
    pd_error			  = coalesce(current_setting('l_hk_cpp_run_results.pd_error')::integer, PD_ERROR),
    pd_deleted		  = coalesce(current_setting('l_hk_cpp_run_results.pd_deleted')::integer, PD_DELETED),
	staging_status	  = coalesce(current_setting('l_hk_cpp_run_results.staging_status')::varchar(1), STAGING_STATUS),
    staging_error_message = coalesce(current_setting('l_hk_cpp_run_results.staging_error_message')::varchar(2000), STAGING_ERROR_MESSAGE),
    staging_error	  = coalesce(current_setting('l_hk_cpp_run_results.staging_error')::integer, STAGING_ERROR),
    staging_deleted	  = coalesce(current_setting('l_hk_cpp_run_results.staging_deleted')::integer, STAGING_DELETED)
    WHERE  hk_cpp_run_id = current_setting('l_hk_cpp_run_results.l_hk_cpp_run_id')::bigint;
    --COMMIT;
END;

$body$
LANGUAGE PLPGSQL
;
-- REVOKE ALL ON PROCEDURE xhb_housekeeping_pkg.update_hk_cpp_log () FROM PUBLIC;


CREATE OR REPLACE PROCEDURE xhb_housekeeping_pkg.log_cpp_hk_error (p_hk_cpp_run_id xhb_hk_cpp_error_log.HK_CPP_RUN_ID%TYPE, p_error_message xhb_hk_cpp_error_log.ERROR_MESSAGE%TYPE) AS $body$
BEGIN

     INSERT INTO xhb_hk_cpp_error_log(hk_cpp_run_id, error_message)
     VALUES (p_hk_cpp_run_id, p_error_message);

END;

$body$
LANGUAGE PLPGSQL
;
-- REVOKE ALL ON PROCEDURE xhb_housekeeping_pkg.log_cpp_hk_error (p_hk_cpp_run_id xhb_hk_cpp_error_log.HK_CPP_RUN_ID%TYPE, p_error_message xhb_hk_cpp_error_log.ERROR_MESSAGE%TYPE) FROM PUBLIC;


CREATE OR REPLACE PROCEDURE xhb_housekeeping_pkg.initialise_arrays () AS $body$
DECLARE

	bigint_array bigint[] := '{}';
	varchar1_array varchar(1)[] := '{}';
	text_array text[] := '{}';

BEGIN

	PERFORM set_config(
		'xhb_housekeeping_pkg.l_log_case_id',
		bigint_array::text,
		false);

	PERFORM set_config(
		'xhb_housekeeping_pkg.l_log_case_no',
		bigint_array::text,
		false);

	PERFORM set_config(
		'xhb_housekeeping_pkg.l_log_case_type',
		varchar1_array::text,
		false);

	PERFORM set_config(
		'xhb_housekeeping_pkg.l_log_court_id',
		bigint_array::text,
		false);

	PERFORM set_config(
		'xhb_housekeeping_pkg.l_log_table',
		text_array::text,
		false);

END;

$body$
LANGUAGE PLPGSQL
;
-- REVOKE ALL ON PROCEDURE xhb_housekeeping_pkg.initialise_arrays () FROM PUBLIC;


CREATE OR REPLACE PROCEDURE xhb_housekeeping_pkg.delete_record (p_table_name text, p_where_clause text, p_error_point text) AS $body$
BEGIN
       PERFORM set_config('xhb_housekeeping_pkg.l_error_point', p_error_point || ' ' || p_table_name || ' ', false);
       EXECUTE 'DELETE FROM '||Upper(p_table_name)||' WHERE '||p_where_clause;
END;

$body$
LANGUAGE PLPGSQL
;
-- REVOKE ALL ON PROCEDURE xhb_housekeeping_pkg.delete_record (p_table_name text, p_where_clause text, p_error_point text) FROM PUBLIC;


CREATE OR REPLACE PROCEDURE xhb_housekeeping_pkg.delete_cpplist (p_no_of_days bigint, p_status xhb_cpp_list.status%TYPE) AS $body$
DECLARE

  rec RECORD;

BEGIN
	  FOR rec IN (SELECT cpp_list_id, list_clob_id, merged_clob_id
					FROM xhb_cpp_list
				  WHERE list_end_date < date_trunc('day', clock_timestamp()) - make_interval(days=>1)
					 AND EXTRACT(day FROM age(date_trunc('day', clock_timestamp()),date_trunc('day', last_update_date))) > p_no_of_days
					 AND status = p_status)  LOOP
		  BEGIN
			CALL xhb_housekeeping_pkg.delete_record(p_table_name => 'xhb_cpp_list', p_where_clause => 'CPP_LIST_ID = '||rec.cpp_list_id::varchar, p_error_point => 'process_cpp_listing');
			CALL xhb_housekeeping_pkg.delete_record(p_table_name => 'AUD_CPP_LIST', p_where_clause => 'CPP_LIST_ID = '||rec.cpp_list_id::varchar, p_error_point => 'process_cpp_listing');
			IF rec.list_clob_id IS NOT NULL THEN
			   -- Try and delete the CLOBs although they may be referenced by xhb_cpp_staging_inbound so if that happens then leave the CLOB alone, it will
					 -- be cleaned up when xhb_cpp_staging_inbound is processed by housekeeping.
				BEGIN
					CALL xhb_housekeeping_pkg.delete_record(p_table_name => 'XHB_CLOB', p_where_clause => 'CLOB_ID = '||rec.list_clob_id::varchar, p_error_point => 'process_cpp_listing');
					CALL xhb_housekeeping_pkg.delete_record(p_table_name => 'AUD_CLOB', p_where_clause => 'CLOB_ID = '||rec.list_clob_id::varchar, p_error_point => 'process_cpp_listing');
				EXCEPTION
					WHEN OTHERS THEN
						RAISE NOTICE 'delete_cpplist exception #1 = %', SQLERRM;
				END;
			END IF;
			IF rec.merged_clob_id IS NOT NULL THEN
			   -- Try and delete the CLOBs although they may be referenced by xhb_cpp_staging_inbound so if that happens then leave the CLOB alone, it will
					 -- be cleaned up when xhb_cpp_staging_inbound is processed by housekeeping.
					 BEGIN
			   CALL xhb_housekeeping_pkg.delete_record(p_table_name => 'XHB_CLOB', p_where_clause => 'CLOB_ID = '||rec.merged_clob_id::varchar, p_error_point => 'process_cpp_listing');
			   CALL xhb_housekeeping_pkg.delete_record(p_table_name => 'AUD_CLOB', p_where_clause => 'CLOB_ID = '||rec.merged_clob_id::varchar, p_error_point => 'process_cpp_listing');
			   EXCEPTION
					WHEN OTHERS THEN
						RAISE NOTICE 'delete_cpplist exception #2 = %', SQLERRM;
			   END;
			END IF;

			PERFORM set_config(
				'l_hk_cpp_run_results.list_deleted',
				(coalesce(current_setting('l_hk_cpp_run_results.list_deleted')::integer,0) + 1)::text,
				false);

			--COMMIT;
		  EXCEPTION WHEN OTHERS THEN
			--ROLLBACK;

			RAISE NOTICE 'delete_cpplist exception #3 = %', SQLERRM;

			PERFORM set_config(
				'l_hk_cpp_run_results.list_deleted',
				(coalesce(current_setting('l_hk_cpp_run_results.list_error')::integer,0) + 1)::text,
				false);
			PERFORM set_config(
				'l_hk_cpp_run_results.list_error_message',
				SUBSTR(current_setting('xhb_housekeeping_pkg.l_error_point')::varchar(100)||' '||SQLERRM, 1,500),
				false);

			CALL xhb_housekeeping_pkg.log_cpp_hk_error(
				p_hk_cpp_run_id => current_setting('l_hk_cpp_run_results.l_hk_cpp_run_id')::bigint,
				p_error_message => current_setting('l_hk_cpp_run_results.list_error_message')::varchar(2000));
		  END;
	  END LOOP;
	END;

$body$
LANGUAGE PLPGSQL
;
-- REVOKE ALL ON PROCEDURE xhb_housekeeping_pkg.delete_cpplist (p_no_of_days bigint, p_status xhb_cpp_list.status%TYPE) FROM PUBLIC;


CREATE OR REPLACE PROCEDURE xhb_housekeeping_pkg.process_cpp_listing (p_MS_days bigint DEFAULT 7, p_MF_days bigint DEFAULT 30) AS $body$
BEGIN
    PERFORM set_config('l_hk_cpp_run_results.list_status','I', false);
	PERFORM set_config('l_hk_cpp_run_results.list_error','0', false);
	PERFORM set_config('l_hk_cpp_run_results.list_deleted','0', false);

    CALL xhb_housekeeping_pkg.update_hk_cpp_log();

    CALL xhb_housekeeping_pkg.delete_cpplist(p_no_of_days => p_MS_days, p_status => 'MS');
    CALL xhb_housekeeping_pkg.delete_cpplist(p_no_of_days => p_MF_days, p_status => 'MF');
    CALL xhb_housekeeping_pkg.delete_cpplist(p_no_of_days => p_MF_days, p_status => 'NP');

	IF coalesce(current_setting('l_hk_cpp_run_results.list_error')::integer,0) > 0 THEN
		PERFORM set_config('l_hk_cpp_run_results.list_status','E', false);
	ELSE
		PERFORM set_config('l_hk_cpp_run_results.list_status','S', false);
	END IF;

    CALL xhb_housekeeping_pkg.update_hk_cpp_log();
END;

$body$
LANGUAGE PLPGSQL
;
-- REVOKE ALL ON PROCEDURE xhb_housekeeping_pkg.process_cpp_listing (p_MS_days bigint DEFAULT 7, p_MF_days bigint DEFAULT 30) FROM PUBLIC;


CREATE OR REPLACE PROCEDURE xhb_housekeeping_pkg.log_delete (p_table_name text) AS $body$
BEGIN

    -- Delete statement may not have actually deleted any records
    IF current_setting('xhb_housekeeping_pkg.l_success_log')::boolean THEN

		PERFORM set_config(
			'xhb_housekeeping_pkg.l_log_case_id',
			array_append(
				current_setting('xhb_housekeeping_pkg.l_log_case_id')::bigint[],
				current_setting('l_case_history.case_id')::bigint)::text,
			false);

		PERFORM set_config(
			'xhb_housekeeping_pkg.l_log_case_no',
			array_append(
				current_setting('xhb_housekeeping_pkg.l_log_case_no')::bigint[],
				current_setting('l_case_history.case_no')::bigint)::text,
			false);

		PERFORM set_config(
			'xhb_housekeeping_pkg.l_log_case_type',
			array_append(
				current_setting('xhb_housekeeping_pkg.l_log_case_type')::varchar(1)[],
				current_setting('l_case_history.case_type')::varchar(1))::text,
			false);

		PERFORM set_config(
			'xhb_housekeeping_pkg.l_log_court_id',
			array_append(
				current_setting('xhb_housekeeping_pkg.l_log_court_id')::bigint[],
				current_setting('l_case_history.court_id')::bigint)::text,
			false);

		PERFORM set_config(
			'xhb_housekeeping_pkg.l_log_table',
			array_append(
				current_setting('xhb_housekeeping_pkg.l_log_table')::text[],
				p_table_name)::text,
			false);

    END IF;

EXCEPTION
	WHEN OTHERS THEN
		RAISE NOTICE 'log_delete exception = %', SQLERRM;

END;

$body$
LANGUAGE PLPGSQL
;
-- REVOKE ALL ON PROCEDURE xhb_housekeeping_pkg.log_delete (p_table_name text) FROM PUBLIC;


CREATE OR REPLACE PROCEDURE xhb_housekeeping_pkg.log_deletion (p_table_name text, p_log_delete boolean DEFAULT TRUE) AS $body$
BEGIN
	IF p_log_delete THEN
		CALL xhb_housekeeping_pkg.log_delete(p_table_name => p_table_name);
	END IF;
END;

$body$
LANGUAGE PLPGSQL
;
-- REVOKE ALL ON PROCEDURE xhb_housekeeping_pkg.log_deletion (p_table_name text, p_log_delete boolean DEFAULT TRUE) FROM PUBLIC;


CREATE OR REPLACE PROCEDURE xhb_housekeeping_pkg.delete_hearing (p_case_id bigint, p_log_delete boolean DEFAULT TRUE) AS $body$
DECLARE

    l_delete varchar(2000);
	rowcount integer;

BEGIN

    PERFORM set_config('xhb_housekeeping_pkg.l_error_point', 'DELETE HEARING ', false);

    -- 2.1.1
    l_delete := '
    DELETE FROM xhb_sh_judge
    WHERE  sh_attendee_id IN (SELECT sh_attendee_id
                              FROM   xhb_sched_hearing_attendee
                              WHERE  sh_justice_id IN (SELECT sh_justice_id
                                                       FROM   xhb_sh_justice
                                                       WHERE  hearing_id IN (SELECT hearing_id
                                                                             FROM   xhb_hearing
                                                                             WHERE  case_id = $1
                                                                            )
                                                      )
                             )';

    EXECUTE l_delete USING p_case_id;
	GET DIAGNOSTICS rowcount = ROW_COUNT;
	IF rowcount > 0 THEN
		CALL xhb_housekeeping_pkg.log_deletion(p_table_name      => 'xhb_sh_judge', p_log_delete => p_log_delete);
	END IF;
    l_delete := REPLACE(l_delete,'xhb','aud');
    EXECUTE l_delete USING p_case_id;
    PERFORM set_config('xhb_housekeeping_pkg.l_error_point', 'DELETE HEARING 1 ', false);

    -- 2.1
    l_delete := '
    DELETE FROM xhb_sched_hearing_attendee
    WHERE  sh_justice_id IN (SELECT sh_justice_id
                             FROM   xhb_sh_justice
                             WHERE  hearing_id IN (SELECT hearing_id
                                                   FROM   xhb_hearing
                                                   WHERE  case_id = $1
                                                  )
                            )';
    EXECUTE l_delete USING p_case_id;
	GET DIAGNOSTICS rowcount = ROW_COUNT;
	IF rowcount > 0 THEN
		CALL xhb_housekeeping_pkg.log_deletion(p_table_name      => 'xhb_sched_hearing_attendee', p_log_delete => p_log_delete);
	END IF;
    l_delete := REPLACE(l_delete,'xhb','aud');
    EXECUTE l_delete USING p_case_id;
    PERFORM set_config('xhb_housekeeping_pkg.l_error_point', 'DELETE HEARING 2 ', false);

    -- 2
    l_delete := '
    DELETE FROM xhb_sh_justice
    WHERE  hearing_id IN (SELECT hearing_id
                          FROM   xhb_hearing
                          WHERE  case_id = $1
                         )';
    EXECUTE l_delete USING p_case_id;
	GET DIAGNOSTICS rowcount = ROW_COUNT;
	IF rowcount > 0 THEN
		CALL xhb_housekeeping_pkg.log_deletion(p_table_name      => 'xhb_sh_justice', p_log_delete => p_log_delete);
	END IF;
    l_delete := REPLACE(l_delete,'xhb','aud');
    EXECUTE l_delete USING p_case_id;
    PERFORM set_config('xhb_housekeeping_pkg.l_error_point', 'DELETE HEARING 3 ', false);

	-- 1.1
    l_delete := '
    DELETE FROM xhb_cr_live_display
    WHERE  scheduled_hearing_id IN (SELECT scheduled_hearing_id
                                    FROM   xhb_scheduled_hearing
                                    WHERE  hearing_id IN (SELECT hearing_id
                                                          FROM   xhb_hearing
                                                          WHERE  case_id = $1
                                                          )
                                   )';
	EXECUTE l_delete USING p_case_id;
	GET DIAGNOSTICS rowcount = ROW_COUNT;
	IF rowcount > 0 THEN
		CALL xhb_housekeeping_pkg.log_deletion(p_table_name => 'XHB_CR_LIVE_DISPLAY', p_log_delete => p_log_delete);
	END IF;
	l_delete := REPLACE(l_delete,'xhb','aud');
    EXECUTE l_delete USING p_case_id;
    PERFORM set_config('xhb_housekeeping_pkg.l_error_point', 'DELETE HEARING 4 ', false);

	-- 1.4
    l_delete := '
    DELETE FROM xhb_court_log_entry
    WHERE  scheduled_hearing_id IN (SELECT scheduled_hearing_id
                                    FROM   xhb_scheduled_hearing
                                    WHERE  hearing_id IN (SELECT hearing_id
                                                          FROM   xhb_hearing
                                                          WHERE  case_id = $1
                                                          )
                                   )';
	EXECUTE l_delete USING p_case_id;
	GET DIAGNOSTICS rowcount = ROW_COUNT;
	IF rowcount > 0 THEN
		CALL xhb_housekeeping_pkg.log_deletion(p_table_name => 'xhb_court_log_entry', p_log_delete => p_log_delete);
	END IF;
	l_delete := REPLACE(l_delete,'xhb','aud');
    EXECUTE l_delete USING p_case_id;
    PERFORM set_config('xhb_housekeeping_pkg.l_error_point', 'DELETE HEARING 5 ', false);

    PERFORM set_config('xhb_housekeeping_pkg.l_error_point', 'DELETE HEARING 6 ', false);

    -- 1.6
    l_delete := '
    DELETE FROM xhb_sched_hearing_defendant
    WHERE  scheduled_hearing_id IN (SELECT scheduled_hearing_id
                                    FROM   xhb_scheduled_hearing
                                    WHERE  hearing_id IN (SELECT hearing_id
                                                          FROM   xhb_hearing
                                                          WHERE  case_id = $1
                                                          )
                                   )';
    EXECUTE l_delete USING p_case_id;
	GET DIAGNOSTICS rowcount = ROW_COUNT;
	IF rowcount > 0 THEN
		CALL xhb_housekeeping_pkg.log_deletion(p_table_name      => 'xhb_sched_hearing_defendant', p_log_delete => p_log_delete );
	END IF;
    l_delete := REPLACE(l_delete,'xhb','aud');
    EXECUTE l_delete USING p_case_id;
    PERFORM set_config('xhb_housekeeping_pkg.l_error_point', 'DELETE HEARING 7 ', false);

    -- 1.7.1
    l_delete := '
    DELETE FROM xhb_sh_judge
    WHERE  sh_attendee_id IN (SELECT sh_attendee_id
                              FROM   xhb_sched_hearing_attendee
                              WHERE  scheduled_hearing_id IN (SELECT scheduled_hearing_id
                                                              FROM   xhb_scheduled_hearing
                                                              WHERE  hearing_id IN (SELECT hearing_id
                                                                                    FROM   xhb_hearing
                                                                                    WHERE  case_id = $1
                                                                                    )
                                                             )
                              )';
    EXECUTE l_delete USING p_case_id;
	GET DIAGNOSTICS rowcount = ROW_COUNT;
	IF rowcount > 0 THEN
		CALL xhb_housekeeping_pkg.log_deletion(p_table_name      => 'xhb_sh_judge', p_log_delete => p_log_delete );
	END IF;
    l_delete := REPLACE(l_delete,'xhb','aud');
    EXECUTE l_delete USING p_case_id;
    PERFORM set_config('xhb_housekeeping_pkg.l_error_point', 'DELETE HEARING 8 ', false);

    -- 1.7
    l_delete := '
    DELETE FROM xhb_sched_hearing_attendee
    WHERE  scheduled_hearing_id IN (SELECT scheduled_hearing_id
                                    FROM   xhb_scheduled_hearing
                                    WHERE  hearing_id IN (SELECT hearing_id
                                                          FROM   xhb_hearing
                                                          WHERE  case_id = $1
                                                          )
                                   )';
    EXECUTE l_delete USING p_case_id;
	GET DIAGNOSTICS rowcount = ROW_COUNT;
	IF rowcount > 0 THEN
		CALL xhb_housekeeping_pkg.log_deletion(p_table_name      => 'xhb_sched_hearing_attendee', p_log_delete => p_log_delete);
	END IF;
    l_delete := REPLACE(l_delete,'xhb','aud');
    EXECUTE l_delete USING p_case_id;
    PERFORM set_config('xhb_housekeeping_pkg.l_error_point', 'DELETE HEARING 9 ', false);

    -- 1
    l_delete := '
    DELETE FROM xhb_scheduled_hearing
    WHERE  hearing_id IN (SELECT hearing_id
                          FROM   xhb_hearing
                          WHERE  case_id = $1
                         )';
    EXECUTE l_delete USING p_case_id;
	GET DIAGNOSTICS rowcount = ROW_COUNT;
	IF rowcount > 0 THEN
		CALL xhb_housekeeping_pkg.log_deletion(p_table_name      => 'xhb_scheduled_hearing', p_log_delete => p_log_delete );
	END IF;
    l_delete := REPLACE(l_delete,'xhb','aud');
    EXECUTE l_delete USING p_case_id;
    PERFORM set_config('xhb_housekeeping_pkg.l_error_point', 'DELETE HEARING 10 ', false);

    -- 0
    l_delete := '
    DELETE FROM xhb_hearing
    WHERE  case_id = $1';
    EXECUTE l_delete USING p_case_id;
	GET DIAGNOSTICS rowcount = ROW_COUNT;
	IF rowcount > 0 THEN
		CALL xhb_housekeeping_pkg.log_deletion(p_table_name      => 'xhb_hearing', p_log_delete => p_log_delete );
	END IF;
    l_delete := REPLACE(l_delete,'xhb','aud');
    EXECUTE l_delete USING p_case_id;
    PERFORM set_config('xhb_housekeeping_pkg.l_error_point', 'DELETE HEARING 11 ', false);

END;

$body$
LANGUAGE PLPGSQL
;
-- REVOKE ALL ON PROCEDURE xhb_housekeeping_pkg.delete_hearing (p_case_id bigint, p_log_delete boolean DEFAULT TRUE) FROM PUBLIC;


CREATE OR REPLACE PROCEDURE xhb_housekeeping_pkg.delete_records (p_table_name text, p_case_id bigint, p_log_delete boolean DEFAULT TRUE, p_proc_name text DEFAULT NULL) AS $body$
DECLARE
	rowcount	integer;
BEGIN
	PERFORM set_config('xhb_housekeeping_pkg.l_error_point', p_proc_name || ' ' || p_table_name || ' ', false);
	rowcount := xhb_housekeeping_pkg.execute_deletion(p_sql => 'DELETE FROM '||Upper(p_table_name)||' WHERE case_id = $1 ', p_param1 => p_case_id);
	IF p_log_delete AND rowcount > 0 THEN
		CALL xhb_housekeeping_pkg.log_delete(p_table_name => p_table_name);
	END IF;
END;

$body$
LANGUAGE PLPGSQL
;
-- REVOKE ALL ON PROCEDURE xhb_housekeeping_pkg.delete_records (p_table_name text, p_case_id bigint, p_log_delete boolean DEFAULT TRUE, p_proc_name text DEFAULT NULL) FROM PUBLIC;


CREATE OR REPLACE PROCEDURE xhb_housekeeping_pkg.delete_records (p_table_name text, p_sql text, p_param1 bigint DEFAULT NULL, p_param2 bigint DEFAULT NULL, p_log_delete boolean DEFAULT TRUE, p_proc_name text DEFAULT NULL) AS $body$
DECLARE
	rowcount	integer;
BEGIN
	PERFORM set_config('xhb_housekeeping_pkg.l_error_point', p_proc_name || ' ' || p_table_name || ' ', false);
	rowcount := xhb_housekeeping_pkg.execute_deletion(p_sql => p_sql, p_param1 => p_param1, p_param2 => p_param2);
	IF p_log_delete AND rowcount > 0 THEN
		CALL xhb_housekeeping_pkg.log_delete(p_table_name => p_table_name);
	END IF;
END;

$body$
LANGUAGE PLPGSQL
;
-- REVOKE ALL ON PROCEDURE xhb_housekeeping_pkg.delete_records (p_table_name text, p_sql text, p_param1 bigint DEFAULT NULL, p_param2 bigint DEFAULT NULL, p_log_delete boolean DEFAULT TRUE, p_proc_name text DEFAULT NULL) FROM PUBLIC;


CREATE OR REPLACE PROCEDURE xhb_housekeeping_pkg.delete_defendant(p_case_id bigint, p_log_delete boolean DEFAULT TRUE) AS $body$
DECLARE

    l_delete varchar(4000);
	rowcount integer;

    -- Returns defendants on case which are not on any other cases
	c_defendant_on_case CURSOR FOR
	SELECT doc.defendant_id, doc.defendant_on_case_id
	FROM   xhb_defendant_on_case  doc
	WHERE  doc.case_id = p_case_id
	AND    NOT EXISTS (	SELECT NULL
						FROM   xhb_defendant_on_case doc2
						WHERE  doc2.case_id != doc.case_id
						AND    doc2.defendant_id = doc.defendant_id);

BEGIN

    PERFORM set_config('xhb_housekeeping_pkg.l_error_point', 'DELETE DEFENDANT ', false);

    -- 13
    l_delete := 'DELETE FROM xhb_court_log_entry
                 WHERE  case_id = $1';
	EXECUTE l_delete USING p_case_id;
	GET DIAGNOSTICS rowcount = ROW_COUNT;
	IF rowcount > 0 THEN
		CALL xhb_housekeeping_pkg.log_deletion(p_table_name => 'XHB_COURT_LOG_ENTRY', p_log_delete => p_log_delete );
	END IF;
	l_delete := REPLACE (l_delete,'xhb','aud');
	EXECUTE l_delete USING p_case_id;
	PERFORM set_config('xhb_housekeeping_pkg.l_error_point', 'DELETE DEFENDANT 1 ', false);

    -- 5
    l_delete := 'DELETE FROM xhb_court_log_entry
                 WHERE  defendant_on_case_id IN (SELECT defendant_on_case_id
                                                 FROM   xhb_defendant_on_case
                                                 WHERE  case_id = $1
                                                )';
	EXECUTE l_delete USING p_case_id;
	GET DIAGNOSTICS rowcount = ROW_COUNT;
	IF rowcount > 0 THEN
		CALL xhb_housekeeping_pkg.log_deletion(p_table_name => 'XHB_COURT_LOG_ENTRY', p_log_delete => p_log_delete );
	END IF;
	l_delete := REPLACE (l_delete,'xhb','aud');
	EXECUTE l_delete USING p_case_id;
    PERFORM set_config('xhb_housekeeping_pkg.l_error_point', 'DELETE DEFENDANT 2 ', false);

    -- loop through all defendants on case which are not on any other cases
    FOR l_def IN c_defendant_on_case LOOP

		-- 0.1
		l_delete := 'DELETE FROM xhb_defendant_on_case
                   WHERE  defendant_on_case_id = $1';
		EXECUTE l_delete USING l_def.defendant_on_case_id;
		GET DIAGNOSTICS rowcount = ROW_COUNT;
		IF rowcount > 0 THEN
			CALL xhb_housekeeping_pkg.log_deletion(p_table_name => 'XHB_DEFENDANT_ON_CASE', p_log_delete => p_log_delete );
		END IF;
		l_delete := REPLACE (l_delete,'xhb','aud');
		EXECUTE l_delete USING l_def.defendant_on_case_id;
		PERFORM set_config('xhb_housekeeping_pkg.l_error_point', 'DELETE DEFENDANT 3 ', false);

		-- 0.2
		l_delete := 'DELETE FROM xhb_defendant
                   WHERE  defendant_id = $1';
		EXECUTE l_delete USING l_def.defendant_id;
		GET DIAGNOSTICS rowcount = ROW_COUNT;
		IF rowcount > 0 THEN
			CALL xhb_housekeeping_pkg.log_deletion(p_table_name => 'XHB_DEFENDANT', p_log_delete => p_log_delete );
		END IF;
		l_delete := REPLACE (l_delete,'xhb','aud');
		EXECUTE l_delete USING l_def.defendant_id;
		PERFORM set_config('xhb_housekeeping_pkg.l_error_point', 'DELETE DEFENDANT 4 ', false);

    END LOOP;

    l_delete := 'DELETE FROM xhb_defendant_on_case
                 WHERE  case_id = $1';
	EXECUTE l_delete USING p_case_id;
	GET DIAGNOSTICS rowcount = ROW_COUNT;
	IF rowcount > 0 THEN
		CALL xhb_housekeeping_pkg.log_deletion(p_table_name => 'XHB_DEFENDANT_ON_CASE', p_log_delete => p_log_delete );
	END IF;
	l_delete := REPLACE (l_delete,'xhb','aud');
	EXECUTE l_delete USING p_case_id;
    PERFORM set_config('xhb_housekeeping_pkg.l_error_point', 'DELETE DEFENDANT 5 ', false);

END;

$body$
LANGUAGE PLPGSQL
;
-- REVOKE ALL ON PROCEDURE xhb_housekeeping_pkg.delete_defendant(p_case_id bigint, p_log_delete boolean DEFAULT TRUE) FROM PUBLIC;


CREATE OR REPLACE PROCEDURE xhb_housekeeping_pkg.delete_listing (p_case_id bigint, p_log_delete boolean DEFAULT TRUE) AS $body$
DECLARE

    l_proc_name varchar(14) := 'DELETE_LISTING';

BEGIN

    CALL xhb_housekeeping_pkg.delete_records(p_table_name => 'XHB_CASE_ON_LIST',
                  p_sql         =>
      'UPDATE XHB_CASE_ON_LIST SET PARENT_CASE_ON_LIST_ID = NULL '||
                 'WHERE PARENT_CASE_ON_LIST_ID IS NOT NULL'||
                 ' AND PARENT_CASE_ON_LIST_ID IN'||
                 ' (SELECT CASE_ON_LIST_ID '||
                     'FROM XHB_CASE_ON_LIST '||
                     'WHERE CASE_ID = $1)',
                  p_param1      => p_case_id,
				   p_log_delete => p_log_delete,
				   p_proc_name => l_proc_name);

    CALL xhb_housekeeping_pkg.delete_records(p_table_name => 'XHB_CASE_ON_LIST',
				   p_case_id => p_case_id,
				   p_log_delete => p_log_delete,
				   p_proc_name => l_proc_name);

    CALL xhb_housekeeping_pkg.delete_records(p_table_name => 'XHB_CASE_DIARY_FIXTURE',
                   p_sql        =>
      'DELETE FROM XHB_CASE_DIARY_FIXTURE '||
         'WHERE CASE_LISTING_ENTRY_ID IN (SELECT CASE_LISTING_ENTRY_ID '||
                                           'FROM XHB_CASE_LISTING_ENTRY '||
                                           'WHERE CASE_ID = $1)',
                   p_param1     => p_case_id,
				   p_log_delete => p_log_delete,
				   p_proc_name => l_proc_name);

    CALL xhb_housekeeping_pkg.delete_records(p_table_name => 'XHB_CASE_LISTING_ENTRY',
				   p_case_id => p_case_id,
				   p_log_delete => p_log_delete,
				   p_proc_name => l_proc_name);
END;

$body$
LANGUAGE PLPGSQL
;
-- REVOKE ALL ON PROCEDURE xhb_housekeeping_pkg.delete_listing (p_case_id bigint, p_log_delete boolean DEFAULT TRUE) FROM PUBLIC;


CREATE OR REPLACE PROCEDURE xhb_housekeeping_pkg.delete_case (p_case_id bigint, p_log_delete boolean DEFAULT TRUE) AS $body$
DECLARE

    l_delete varchar(4000);
	rowcount integer;

BEGIN

    PERFORM set_config('xhb_housekeeping_pkg.l_error_point', 'DELETE CASE ', false);

	-- 3
    l_delete := 'DELETE FROM xhb_case_reference
                 WHERE  case_id = $1';
	EXECUTE l_delete USING p_case_id;
	GET DIAGNOSTICS rowcount = ROW_COUNT;
	IF rowcount > 0 THEN
		CALL xhb_housekeeping_pkg.log_deletion(p_table_name => 'XHB_CASE_REFERENCE', p_log_delete => p_log_delete );
	END IF;
	l_delete := REPLACE (l_delete,'xhb','aud');
	EXECUTE l_delete USING p_case_id;
	PERFORM set_config('xhb_housekeeping_pkg.l_error_point', 'DELETE CASE 1 ', false);

    -- 0
    l_delete := 'DELETE FROM XHB_CASE
                 WHERE  case_id = $1';
    EXECUTE l_delete USING p_case_id;
	GET DIAGNOSTICS rowcount = ROW_COUNT;
	IF rowcount > 0 THEN
		CALL xhb_housekeeping_pkg.log_deletion(p_table_name      => 'XHB_CASE', p_log_delete => p_log_delete );
	END IF;
    l_delete := REPLACE(l_delete,'xhb','aud');
    EXECUTE l_delete USING p_case_id;
    PERFORM set_config('xhb_housekeeping_pkg.l_error_point', 'DELETE CASE 2 ', false);

END;

$body$
LANGUAGE PLPGSQL
;
-- REVOKE ALL ON PROCEDURE xhb_housekeeping_pkg.delete_case (p_case_id bigint, p_log_delete boolean DEFAULT TRUE) FROM PUBLIC;


CREATE OR REPLACE PROCEDURE xhb_housekeeping_pkg.process_cases (p_case_limit bigint ,p_total_streams bigint ,p_stream_number bigint ) AS $body$
DECLARE

    l_error_message xhb_hk_results.error_message%TYPE;
	l_case_history_records	bigint;

	c_mtbl_case_history CURSOR FOR
		SELECT cas.case_number case_no
		,      cas.case_type
		,      cas.court_id
		,      cas.case_id
		FROM   mtbl_case_history  mtb
		,      XHB_CASE           cas
		WHERE  mtb.case_no   = cas.case_number
		AND    mtb.case_type = cas.case_type
		AND    mtb.court_id  = cas.court_id
		AND    MOD(mtb.case_no,p_total_streams) + 1 = p_stream_number;

BEGIN

    PERFORM set_config('xhb_housekeeping_pkg.l_error_point', 'PROCESS CASES 1', false);

    -- Update log record with case metrics
	UPDATE xhb_hk_results SET case_start_date = clock_timestamp() WHERE hk_run_id = current_setting('l_hk_run_results.l_hk_run_id')::bigint;
	--COMMIT;
	PERFORM set_config('l_hk_run_results.case_status', 'I', false);
    CALL xhb_housekeeping_pkg.update_log();

    -- loop through all cases due for deletion
    FOR l_cases IN c_mtbl_case_history LOOP

      BEGIN
        -- A case limit of zero means there is no limit
        IF p_case_limit > 0 THEN

          -- exit out of cursor loop if limit reached
          IF current_setting('xhb_housekeeping_pkg.l_cases_deleted')::integer + current_setting('xhb_housekeeping_pkg.l_cases_error')::integer >= p_case_limit THEN
            EXIT;
          END IF;

        END IF;

        PERFORM set_config('l_case_history.case_id', l_cases.case_id::text, false);
		PERFORM set_config('l_case_history.case_no', l_cases.case_no::text, false);
		PERFORM set_config('l_case_history.case_type', l_cases.case_type, false);
		PERFORM set_config('l_case_history.court_id', l_cases.court_id::text, false);

        CALL xhb_housekeeping_pkg.delete_hearing(p_case_id => current_setting('l_case_history.case_id')::integer);

        CALL xhb_housekeeping_pkg.delete_listing(p_case_id => current_setting('l_case_history.case_id')::integer);

		CALL xhb_housekeeping_pkg.delete_defendant(p_case_id => current_setting('l_case_history.case_id')::integer);

        CALL xhb_housekeeping_pkg.delete_case(p_case_id => current_setting('l_case_history.case_id')::integer);

        --COMMIT;

        PERFORM set_config(
			'xhb_housekeeping_pkg.l_cases_deleted',
			(current_setting('xhb_housekeeping_pkg.l_cases_deleted')::integer + 1)::text,
			false);

      EXCEPTION
        WHEN OTHERS THEN

			RAISE NOTICE 'process_cases exception #1 = %', SQLERRM;

          -- Rollback all the deletes for this case
          --ROLLBACK;

          l_error_message := SUBSTR(current_setting('xhb_housekeeping_pkg.l_error_point')::varchar(100)||' '||SQLERRM,1,2000);

          -- this will log an error for an individual case
          INSERT INTO XHB_HK_ERROR_LOG(hk_run_id
          ,case_no
          ,case_type
          ,court_id
          ,case_id
          ,error_message
          )
          VALUES (current_setting('l_hk_run_results.l_hk_run_id')::bigint
          ,l_cases.case_no
          ,l_cases.case_type
          ,l_cases.court_id
          ,l_cases.case_id
          ,l_error_message
          );

          --COMMIT;

          PERFORM set_config(
			'xhb_housekeeping_pkg.l_cases_error',
			(current_setting('xhb_housekeeping_pkg.l_cases_error')::integer + 1)::text,
			false);

      END;

      PERFORM set_config('xhb_housekeeping_pkg.l_error_point', 'PROCESS CASES 2', false);

      --COMMIT;

    END LOOP;

    IF current_setting('xhb_housekeeping_pkg.l_cases_error')::integer > 0 THEN
		PERFORM set_config('l_hk_run_results.case_status', 'E', false);
    ELSE
		PERFORM set_config('l_hk_run_results.case_status', 'S', false);
    END IF;

	PERFORM set_config(
		'l_hk_run_results.cases_deleted',
		current_setting('xhb_housekeeping_pkg.l_cases_deleted'),
		false);
	PERFORM set_config(
		'l_hk_run_results.cases_error',
		current_setting('xhb_housekeeping_pkg.l_cases_error'),
		false);
	UPDATE xhb_hk_results SET case_end_date = clock_timestamp() WHERE hk_run_id = current_setting('l_hk_run_results.l_hk_run_id')::bigint;
	--COMMIT;

    CALL xhb_housekeeping_pkg.update_log();

  EXCEPTION

    WHEN OTHERS THEN

	RAISE NOTICE 'process_cases exception #2 = %', SQLERRM;

    -- Update log record with case metrics
	PERFORM set_config('l_hk_run_results.case_status', 'F', false);
	PERFORM set_config('l_hk_run_results.case_error_message', SUBSTR(current_setting('xhb_housekeeping_pkg.l_error_point')::varchar(100) ||' '||SQLERRM,1,2000), false);
	PERFORM set_config(
		'l_hk_run_results.cases_deleted',
		current_setting('xhb_housekeeping_pkg.l_cases_deleted'),
		false);
	PERFORM set_config(
		'l_hk_run_results.cases_error',
		current_setting('xhb_housekeeping_pkg.l_cases_error'),
		false);
	UPDATE xhb_hk_results SET case_end_date = clock_timestamp() WHERE hk_run_id = current_setting('l_hk_run_results.l_hk_run_id')::bigint;
	--COMMIT;

	CALL xhb_housekeeping_pkg.update_log();

END;

$body$
LANGUAGE PLPGSQL
;
-- REVOKE ALL ON PROCEDURE xhb_housekeeping_pkg.process_cases (p_case_limit bigint ,p_total_streams bigint ,p_stream_number bigint ) FROM PUBLIC;


CREATE OR REPLACE PROCEDURE xhb_housekeeping_pkg.write_error_log_file (p_hk_run_id bigint) AS $body$
DECLARE

    l_file utl_file.file_type;
	l_row_count	integer := 0;

    c_xhb_hk_error_log CURSOR(b_hk_run_id bigint) FOR
      SELECT *
      FROM   XHB_HK_ERROR_LOG
      WHERE  hk_run_id = b_hk_run_id;

BEGIN

    PERFORM set_config('xhb_housekeeping_pkg.l_error_point', 'WRITE ERROR LOG FILE ', false);

    FOR l_errors IN c_xhb_hk_error_log(p_hk_run_id)  LOOP

		IF l_row_count = 0 THEN
			-- Create the file for the first row
			l_file := utl_file.fopen('HK_LOGS','HK_'||LPAD(''||p_hk_run_id||'',10,'0')||'_ERROR.log','a');
			PERFORM utl_file.put_line(l_file,'CASE_ID  CASE_NO  T COURT_ID ERROR_MESSAGE',TRUE);
		END IF;

		l_row_count := l_row_count + 1;

		PERFORM utl_file.put_line(l_file,LPAD(''||l_errors.case_id||'',8)||','||
							   LPAD(''||l_errors.case_no||'',8)||','||
							   l_errors.case_type||','||
							   LPAD(''||l_errors.court_id||'',8)||','||
							   l_errors.error_message
							   ,TRUE);

    END LOOP;

    -- close file
    IF utl_file.is_open(l_file) THEN
      PERFORM utl_file.fclose(l_file);
    END IF;

  EXCEPTION
    -- any exceptions for file handling will go here
    WHEN OTHERS THEN
		RAISE NOTICE 'write_error_log_file exception = %', SQLERRM;

		-- close file
		IF utl_file.is_open(l_file) THEN
			PERFORM utl_file.fclose(l_file);
		END IF;

END;

$body$
LANGUAGE PLPGSQL
;
-- REVOKE ALL ON PROCEDURE xhb_housekeeping_pkg.write_error_log_file (p_hk_run_id bigint) FROM PUBLIC;


CREATE OR REPLACE PROCEDURE xhb_housekeeping_pkg.write_hk_cpp_error_log_file (p_hk_cpp_run_id bigint) AS $body$
DECLARE

    l_file utl_file.file_type;

	l_row_count	integer := 0;

    c_xhb_hk_cpp_error_log CURSOR(b_hk_cpp_run_id bigint) FOR
      SELECT *
      FROM   xhb_hk_cpp_error_log
      WHERE  hk_cpp_run_id = b_hk_cpp_run_id;

BEGIN

    PERFORM set_config('xhb_housekeeping_pkg.l_error_point', 'WRITE ERROR LOG FILE ', false);

    FOR l_errors IN c_xhb_hk_cpp_error_log(p_hk_cpp_run_id)  LOOP

		IF l_row_count = 0 THEN
			l_file := utl_file.fopen('HK_LOGS','HK_CPP_'||LPAD(''||p_hk_cpp_run_id||'',10,'0')||'_ERROR.log','a');
			PERFORM utl_file.put_line(l_file,'ERROR_MESSAGE',TRUE);
		END IF;

		l_row_count := l_row_count + 1;

		PERFORM utl_file.put_line(l_file, l_errors.error_message, TRUE);

    END LOOP;

    -- close file
	IF utl_file.is_open(l_file) THEN
		PERFORM utl_file.fclose(l_file);
	END IF;

EXCEPTION
    WHEN OTHERS THEN

		RAISE NOTICE 'write_hk_cpp_error_log_file exception = %', SQLERRM;

		-- close file
		IF utl_file.is_open(l_file) THEN
			PERFORM utl_file.fclose(l_file);
		END IF;

END;

$body$
LANGUAGE PLPGSQL
;
-- REVOKE ALL ON PROCEDURE xhb_housekeeping_pkg.write_hk_cpp_error_log_file (p_hk_cpp_run_id bigint) FROM PUBLIC;


CREATE OR REPLACE PROCEDURE xhb_housekeeping_pkg.write_success_log_file (p_hk_run_id bigint) AS $body$
DECLARE

    l_file utl_file.file_type;
	l_log_case_id bigint[] := current_setting('xhb_housekeeping_pkg.l_log_case_id')::bigint[];
	l_log_case_no bigint[] := current_setting('xhb_housekeeping_pkg.l_log_case_no')::bigint[];
	l_log_case_type varchar(1)[] := current_setting('xhb_housekeeping_pkg.l_log_case_type')::varchar(1)[];
	l_log_court_id bigint[] := current_setting('xhb_housekeeping_pkg.l_log_court_id')::bigint[];
	l_log_table text[] := current_setting('xhb_housekeeping_pkg.l_log_table')::text[];

BEGIN

    PERFORM set_config('xhb_housekeeping_pkg.l_error_point', 'WRITE SUCCESS LOG FILE 2', false);

    IF current_setting('xhb_housekeeping_pkg.l_success_log')::boolean AND array_length(l_log_case_id, 1) > 0 THEN

      l_file := utl_file.fopen('HK_LOGS','HK_'||LPAD(''||p_hk_run_id||'',10,'0')||'_SUCCESS.log','a');
      PERFORM utl_file.put_line(l_file,'CASE_ID  CASE_NO  T COURT_ID TABLE',TRUE);

      FOR l_success IN 1..array_length(l_log_case_id, 1)  LOOP

        PERFORM utl_file.put_line(l_file,LPAD(''|| l_log_case_id[l_success] || '',8)||' '||
                                 LPAD(''|| l_log_case_no[l_success] || '',8)||','||
                                 l_log_case_type[l_success]||','||
                                 LPAD(''|| l_log_court_id[l_success] || '',8)||','||
                                 l_log_table[l_success]
                                 ,TRUE);

      END LOOP;

      -- close file
      IF utl_file.is_open(l_file) THEN
        PERFORM utl_file.fclose(l_file);
      END IF;

    END IF;

  EXCEPTION

    -- any exceptions for file handling will go here
    WHEN OTHERS THEN

		RAISE NOTICE 'write_success_log_file exception = %', SQLERRM;

		-- close file
		IF utl_file.is_open(l_file) THEN
			PERFORM utl_file.fclose(l_file);
		END IF;

END;

$body$
LANGUAGE PLPGSQL
;
-- REVOKE ALL ON PROCEDURE xhb_housekeeping_pkg.write_success_log_file (p_hk_run_id bigint) FROM PUBLIC;


CREATE OR REPLACE PROCEDURE xhb_housekeeping_pkg.write_metrics_log_file (p_hk_run_id bigint) AS $body$
DECLARE

    l_file utl_file.file_type;

    c_xhb_hk_results CURSOR(b_hk_run_id bigint) FOR
      SELECT *
      FROM   xhb_hk_results
      WHERE  hk_run_id = b_hk_run_id;

    l_rec_results record;

    l_status_desc varchar(100);
    l_run_type    varchar(100);

BEGIN

    PERFORM set_config('xhb_housekeeping_pkg.l_error_point', 'WRITE METRICS LOG FILE ', false);

    l_file := utl_file.fopen('HK_LOGS','HK_'||LPAD(''||p_hk_run_id||'',10,'0')||'_METRICS.log','a');

    OPEN  c_xhb_hk_results(p_hk_run_id);
    FETCH c_xhb_hk_results INTO l_rec_results;
    IF NOT FOUND THEN
      PERFORM utl_file.put_line(l_file,'No record of HK run '||p_hk_run_id||' can be found',TRUE);
    ELSE
      PERFORM utl_file.put_line(l_file,'Log file produced on '||TO_CHAR(clock_timestamp(),'DD-MON-YYYY HH24:MI:SS'),TRUE);
      PERFORM utl_file.new_line(l_file,1);
      PERFORM utl_file.put_line(l_file,'Housekeeping Run Id. '||p_hk_run_id,TRUE);
      PERFORM utl_file.put_line(l_file,'Started On   : '||TO_CHAR(l_rec_results.run_start_date,'DD-MON-YYYY HH24:MI:SS'),TRUE);
      PERFORM utl_file.put_line(l_file,'Completed On : '||TO_CHAR(l_rec_results.run_end_date  ,'DD-MON-YYYY HH24:MI:SS'),TRUE);
      PERFORM utl_file.new_line(l_file,1);

      IF l_rec_results.run_type = 'C' THEN
        l_run_type := 'Cases only';
      ELSIF l_rec_results.run_type = 'L' THEN
        l_run_type := 'Listings only';
      ELSIF l_rec_results.run_type = 'A' THEN
        l_run_type := 'Cases and Listings';
      ELSE
        l_run_type := 'UNKNOWN';
      END IF;

      PERFORM utl_file.put_line(l_file,'Run Type : '||l_run_type);
      PERFORM utl_file.new_line(l_file,1);

      IF l_rec_results.error_message IS NOT NULL THEN
        PERFORM utl_file.put_line(l_file,'ERROR ENCOUNTERED DURING HK RUN');
        PERFORM utl_file.put_line(l_file,l_rec_results.error_message);
      END IF;

      IF l_rec_results.run_type IN ('C','A') THEN
        PERFORM utl_file.put_line(l_file,'Cases Started On   : '||TO_CHAR(l_rec_results.case_start_date,'DD-MON-YYYY HH24:MI:SS'),TRUE);
        PERFORM utl_file.put_line(l_file,'Cases Completed On : '||TO_CHAR(l_rec_results.case_end_date,'DD-MON-YYYY HH24:MI:SS'),TRUE);

        IF l_rec_results.case_status = 'S' THEN
          l_status_desc := ' - Success';
        ELSIF l_rec_results.case_status = 'F' THEN
          l_status_desc := ' - Failure - see error message below';
        ELSIF l_rec_results.case_status = 'E' THEN
          l_status_desc := ' - Some cases not deleted - see count below';
        ELSE
          l_status_desc := ' - Unknown status';
        END IF;

        PERFORM utl_file.put_line(l_file,'Cases Status : '||l_rec_results.case_status||l_status_desc,TRUE);
        IF l_rec_results.case_status = 'F' THEN
          PERFORM utl_file.put_line(l_file,l_rec_results.case_error_message,TRUE);
        END IF;

        PERFORM utl_file.put_line(l_file,'Cases Deleted :'||l_rec_results.cases_deleted,TRUE);
        PERFORM utl_file.put_line(l_file,'Cases Error   :'||l_rec_results.cases_error,TRUE);
        PERFORM utl_file.new_line(l_file,1);
        PERFORM utl_file.put_line(l_file,'Cases Limit   :'||l_rec_results.case_limit,TRUE);
        PERFORM utl_file.new_line(l_file,1);

      END IF;

      IF l_rec_results.run_type IN ('L','A') THEN
        PERFORM utl_file.put_line(l_file,'Lists Started On   : '||TO_CHAR(l_rec_results.list_start_date,'DD-MON-YYYY HH24:MI:SS'),TRUE);
        PERFORM utl_file.put_line(l_file,'Lists Completed On : '||TO_CHAR(l_rec_results.list_end_date,'DD-MON-YYYY HH24:MI:SS'),TRUE);

        IF l_rec_results.list_status = 'S' THEN
          l_status_desc := ' - Success';
        ELSIF l_rec_results.list_status = 'F' THEN
          l_status_desc := ' - Failure - see error message below';
        ELSE
          l_status_desc := ' - Unknown status';
        END IF;

        PERFORM utl_file.put_line(l_file,'Lists Status : '||l_rec_results.list_status||l_status_desc,TRUE);
        IF l_rec_results.list_status = 'F' THEN
          PERFORM utl_file.put_line(l_file,l_rec_results.list_error_message,TRUE);
        END IF;

        PERFORM utl_file.new_line(l_file,1);
        PERFORM utl_file.put_line(l_file,'Running Parameter :'||l_rec_results.running_list,TRUE);
        PERFORM utl_file.put_line(l_file,'Warned Parameter  :'||l_rec_results.warned_list,TRUE);
        PERFORM utl_file.put_line(l_file,'Firm Parameter    :'||l_rec_results.firm_list,TRUE);
        PERFORM utl_file.put_line(l_file,'Daily Parameter   :'||l_rec_results.daily_list,TRUE);

        PERFORM utl_file.new_line(l_file,1);
        PERFORM utl_file.put_line(l_file,'Lists Deleted :'||l_rec_results.lists_deleted,TRUE);

      END IF;

      -- show oracle error message for a failure
      PERFORM utl_file.new_line(l_file,1);
      IF l_rec_results.case_status = 'F' THEN
        PERFORM utl_file.put_line(l_file,l_rec_results.error_message,TRUE);
        PERFORM utl_file.new_line(l_file,1);
      END IF;

      PERFORM utl_file.new_line(l_file,1);
      PERFORM utl_file.put_line(l_file,'End of Report',TRUE);

    END IF;

    CLOSE c_xhb_hk_results;

    PERFORM utl_file.fclose(l_file);

  EXCEPTION

    -- any exceptions for file handling will go here
    WHEN OTHERS THEN

		RAISE NOTICE 'write_metrics_log_file exception = %', SQLERRM;

		-- close cursor
		IF EXISTS(SELECT * FROM pg_cursors WHERE name = 'c_xhb_hk_results') THEN
			CLOSE c_xhb_hk_results;
		END IF;

		-- close filew
		IF utl_file.is_open(l_file) THEN
			PERFORM utl_file.fclose(l_file);
		END IF;

END;

$body$
LANGUAGE PLPGSQL
;
-- REVOKE ALL ON PROCEDURE xhb_housekeeping_pkg.write_metrics_log_file (p_hk_run_id bigint) FROM PUBLIC;


CREATE OR REPLACE PROCEDURE xhb_housekeeping_pkg.write_hk_cpp_metrics_log_file (p_hk_cpp_run_id bigint ,p_MS_days bigint ,p_MF_days bigint) AS $body$
DECLARE

	l_file utl_file.file_type;

	c_xhb_hk_cpp_results CURSOR(b_hk_cpp_run_id bigint) FOR
	SELECT *
	FROM   XHB_HK_CPP_RESULTS
	WHERE  hk_cpp_run_id = b_hk_cpp_run_id;

	l_rec_results record;

	l_status_desc varchar(100);

BEGIN

		l_file := utl_file.fopen('HK_LOGS','HK_CPP_'||LPAD(''||p_hk_cpp_run_id||'',10,'0')||'_METRICS.log','a');

		OPEN  c_xhb_hk_cpp_results(p_hk_cpp_run_id);
		FETCH c_xhb_hk_cpp_results INTO l_rec_results;
		IF NOT FOUND THEN
			-- No results found
			PERFORM utl_file.put_line(l_file,'No record of CPP HK run '||p_hk_cpp_run_id||' can be found',TRUE);
		ELSE
			-- Header
			PERFORM utl_file.put_line(l_file,'Log file produced on '||TO_CHAR(clock_timestamp(),'DD-MON-YYYY HH24:MI:SS'),TRUE);
			PERFORM utl_file.new_line(l_file,1);
			PERFORM utl_file.put_line(l_file,'CPP Housekeeping Run Id. '||p_hk_cpp_run_id,TRUE);
			PERFORM utl_file.put_line(l_file,'Started On   : '||TO_CHAR(l_rec_results.run_start_date,'DD-MON-YYYY HH24:MI:SS'),TRUE);
			PERFORM utl_file.put_line(l_file,'Completed On : '||TO_CHAR(l_rec_results.run_end_date  ,'DD-MON-YYYY HH24:MI:SS'),TRUE);
			PERFORM utl_file.new_line(l_file,1);
			PERFORM utl_file.put_line(l_file,'Retention Periods (days) : ',TRUE);
			PERFORM utl_file.put_line(l_file,'Successful records (MS) : '||p_MS_days,TRUE);
			PERFORM utl_file.put_line(l_file,'Failed records (MF) : '||p_MF_days,TRUE);
			PERFORM utl_file.new_line(l_file,1);

			-- List Statistics
			PERFORM utl_file.put_line(l_file,'List Statistics',TRUE);
			IF l_rec_results.list_status = 'S' THEN
			l_status_desc := ' - Success';
			ELSIF l_rec_results.list_status = 'F' THEN
			l_status_desc := ' - Failure - see error message below';
			ELSIF l_rec_results.list_status = 'E' THEN
			l_status_desc := ' - Some records not deleted - see count below';
			ELSE
			l_status_desc := ' - Unknown status';
			END IF;

			PERFORM utl_file.put_line(l_file,'Run Status : '||l_rec_results.list_status||l_status_desc,TRUE);
			IF l_rec_results.list_status = 'F' THEN
			PERFORM utl_file.put_line(l_file,l_rec_results.list_error_message,TRUE);
			END IF;

			PERFORM utl_file.put_line(l_file,'Records Deleted : '||l_rec_results.list_deleted,TRUE);
			PERFORM utl_file.put_line(l_file,'Records Error   : '||l_rec_results.list_error,TRUE);
			PERFORM utl_file.new_line(l_file,1);

			-- IWP Statistics
			PERFORM utl_file.put_line(l_file,'IWP Statistics',TRUE);
			IF l_rec_results.iwp_status = 'S' THEN
			l_status_desc := ' - Success';
			ELSIF l_rec_results.iwp_status = 'F' THEN
			l_status_desc := ' - Failure - see error message below';
			ELSIF l_rec_results.iwp_status = 'E' THEN
			l_status_desc := ' - Some records not deleted - see count below';
			ELSE
			l_status_desc := ' - Unknown status';
			END IF;

			PERFORM utl_file.put_line(l_file,'Run Status : '||l_rec_results.iwp_status||l_status_desc,TRUE);
			IF l_rec_results.iwp_status = 'F' THEN
			PERFORM utl_file.put_line(l_file,l_rec_results.iwp_error_message,TRUE);
			END IF;

			PERFORM utl_file.put_line(l_file,'Records Deleted : '||l_rec_results.iwp_deleted,TRUE);
			PERFORM utl_file.put_line(l_file,'Records Error   : '||l_rec_results.iwp_error,TRUE);
			PERFORM utl_file.new_line(l_file,1);

			-- Public Display Statistics
			PERFORM utl_file.put_line(l_file,'Public Display Statistics',TRUE);
			IF l_rec_results.pd_status = 'S' THEN
			l_status_desc := ' - Success';
			ELSIF l_rec_results.pd_status = 'F' THEN
			l_status_desc := ' - Failure - see error message below';
			ELSIF l_rec_results.pd_status = 'E' THEN
			l_status_desc := ' - Some records not deleted - see count below';
			ELSE
			l_status_desc := ' - Unknown status';
			END IF;

			PERFORM utl_file.put_line(l_file,'Run Status : '||l_rec_results.pd_status||l_status_desc,TRUE);
			IF l_rec_results.pd_status = 'F' THEN
			PERFORM utl_file.put_line(l_file,l_rec_results.pd_error_message,TRUE);
			END IF;

			PERFORM utl_file.put_line(l_file,'Records Deleted : '||l_rec_results.pd_deleted,TRUE);
			PERFORM utl_file.put_line(l_file,'Records Error   : '||l_rec_results.pd_error,TRUE);
			PERFORM utl_file.new_line(l_file,1);

			-- CPP Staging Inbound Statistics
			PERFORM utl_file.put_line(l_file,'CPP Staging Inbound Statistics',TRUE);
			IF l_rec_results.staging_status = 'S' THEN
			l_status_desc := ' - Success';
			ELSIF l_rec_results.staging_status = 'F' THEN
			l_status_desc := ' - Failure - see error message below';
			ELSIF l_rec_results.staging_status = 'E' THEN
			l_status_desc := ' - Some records not deleted - see count below';
			ELSE
			l_status_desc := ' - Unknown status';
			END IF;

			PERFORM utl_file.put_line(l_file,'Run Status : '||l_rec_results.staging_status||l_status_desc,TRUE);
			IF l_rec_results.staging_status = 'F' THEN
			PERFORM utl_file.put_line(l_file,l_rec_results.staging_error_message,TRUE);
			END IF;

			PERFORM utl_file.put_line(l_file,'Records Deleted : '||l_rec_results.staging_deleted,TRUE);
			PERFORM utl_file.put_line(l_file,'Records Error   : '||l_rec_results.staging_error,TRUE);
			PERFORM utl_file.new_line(l_file,1);

			-- End of Report
			PERFORM utl_file.new_line(l_file,1);
			PERFORM utl_file.put_line(l_file,'End of Report',TRUE);

		END IF;

		CLOSE c_xhb_hk_cpp_results;

		PERFORM utl_file.fclose(l_file);

	EXCEPTION
		-- any exceptions for file handling will go here
		WHEN OTHERS THEN

			RAISE NOTICE 'write_hk_cpp_metrics_log_file exception = %', SQLERRM;

			-- close cursor
			IF EXISTS(SELECT * FROM pg_cursors WHERE name = 'c_xhb_hk_cpp_results') THEN
				CLOSE c_xhb_hk_cpp_results;
			END IF;

			-- close file
			IF utl_file.is_open(l_file) THEN
				PERFORM utl_file.fclose(l_file);
			END IF;

	END;

$body$
LANGUAGE PLPGSQL
;
-- REVOKE ALL ON PROCEDURE xhb_housekeeping_pkg.write_hk_cpp_metrics_log_file (p_hk_cpp_run_id bigint ,p_MS_days bigint ,p_MF_days bigint) FROM PUBLIC;


CREATE OR REPLACE PROCEDURE xhb_housekeeping_pkg.end_delete_run () AS $body$
BEGIN

	-- write errors to external file
    CALL xhb_housekeeping_pkg.write_error_log_file(current_setting('l_hk_run_results.l_hk_run_id')::bigint);

    -- write success to external file
    CALL xhb_housekeeping_pkg.write_success_log_file(current_setting('l_hk_run_results.l_hk_run_id')::bigint);

    -- Update log record with metrics
	UPDATE xhb_hk_results SET run_end_date = clock_timestamp() WHERE hk_run_id = current_setting('l_hk_run_results.l_hk_run_id')::bigint;
	--COMMIT;

    CALL xhb_housekeeping_pkg.update_log();

    -- write metrics to external file
    CALL xhb_housekeeping_pkg.write_metrics_log_file(current_setting('l_hk_run_results.l_hk_run_id')::bigint);

END;

$body$
LANGUAGE PLPGSQL
;
-- REVOKE ALL ON PROCEDURE xhb_housekeeping_pkg.end_delete_run () FROM PUBLIC;


CREATE OR REPLACE PROCEDURE xhb_housekeeping_pkg.initiate_run (p_run_type text ,p_case_limit bigint DEFAULT 0,p_running_list bigint  DEFAULT NULL,p_warned_list bigint  DEFAULT NULL,p_firm_list bigint  DEFAULT NULL,p_daily_list bigint  DEFAULT NULL,p_success_log boolean DEFAULT FALSE,p_total_streams bigint DEFAULT 1,p_stream_number bigint DEFAULT 1) AS $body$
BEGIN
    PERFORM set_config('xhb_housekeeping_pkg.l_error_point', 'INITIATE RUN ', false);
    PERFORM set_config('xhb_housekeeping_pkg.l_cases_deleted', '0', false);
    PERFORM set_config('xhb_housekeeping_pkg.l_lists_deleted', '0', false);
    PERFORM set_config('xhb_housekeeping_pkg.l_cases_error', '0', false);
	CALL xhb_housekeeping_pkg.initialise_arrays ();

	CALL xhb_housekeeping_pkg.insert_hk_log(
		p_run_type => p_run_type,
		p_case_limit => p_case_limit,
		p_running_list => p_running_list,
		p_warned_list => p_warned_list,
		p_firm_list => p_firm_list,
		p_daily_list => p_daily_list);

    PERFORM set_config('xhb_housekeeping_pkg.l_success_log', p_success_log::text, false);

    -- (A)ll or (C)ases
    IF UPPER(p_run_type) IN ('A','C') THEN
      CALL xhb_housekeeping_pkg.process_cases(p_case_limit
                   ,p_total_streams
                   ,p_stream_number
                   );

      PERFORM set_config('xhb_housekeeping_pkg.l_error_point', 'INITIATE RUN 2 ', false);

      -- remove Cases from interface table which have have been deleted
      DELETE FROM mtbl_case_history mch
      WHERE  MOD(mch.case_no,p_total_streams) + 1 = p_stream_number
      AND NOT EXISTS (SELECT 1
                      FROM   XHB_CASE  cas
                      WHERE  cas.case_number = mch.case_no
                      AND    cas.case_type   = mch.case_type
                      AND    cas.court_id    = mch.court_id
                      );

      --COMMIT;

      PERFORM set_config('xhb_housekeeping_pkg.l_error_point', 'INITIATE RUN 3 ', false);

    END IF;

	-- Write logs and end the deletion run
    CALL xhb_housekeeping_pkg.end_delete_run();

EXCEPTION
    WHEN OTHERS THEN

		RAISE NOTICE 'initiate_run exception = %', SQLERRM;

		-- Update log record with metrics
		PERFORM set_config('l_hk_run_results.error_message', SUBSTR(current_setting('xhb_housekeeping_pkg.l_error_point')::varchar(100) ||SQLERRM,1,2000), false);
		UPDATE xhb_hk_results SET run_end_date = clock_timestamp() WHERE hk_run_id = current_setting('l_hk_run_results.l_hk_run_id')::bigint;
		--COMMIT;

		CALL xhb_housekeeping_pkg.update_log();

END;

$body$
LANGUAGE PLPGSQL
;
-- REVOKE ALL ON PROCEDURE xhb_housekeeping_pkg.initiate_run (p_run_type text ,p_case_limit bigint DEFAULT 0,p_running_list bigint  DEFAULT NULL,p_warned_list bigint  DEFAULT NULL,p_firm_list bigint  DEFAULT NULL,p_daily_list bigint  DEFAULT NULL,p_success_log boolean DEFAULT FALSE,p_total_streams bigint DEFAULT 1,p_stream_number bigint DEFAULT 1) FROM PUBLIC;


CREATE OR REPLACE PROCEDURE xhb_housekeeping_pkg.process_cases_ctx () AS $body$
DECLARE

    l_delete         varchar(4000);     -- Holds SQL string for delete statement
    l_proc_name      varchar(30)  := 'PROCESS_CASES_CTX';   -- Procedure name - used for error reporting
    v_case_no        XHB_CASE.case_number%type := 0;
    v_case_type      XHB_CASE.case_type%type   := '';
    v_court_id       XHB_CASE.court_id%type    := 0;
    v_case_title     XHB_CASE.case_title%type  := '';
    v_error_message  varchar(2000);
	v_no_of_cases_allowed bigint := xhb_housekeeping_pkg.get_max_cases_to_delete();
	v_cases_processed bigint := 0;

	c_cases_for_deletion CURSOR FOR
    SELECT case_id
	FROM XHB_CASE
	WHERE case_status = 'D'
	ORDER BY creation_date;

	rec RECORD;

BEGIN

	PERFORM set_config('xhb_housekeeping_pkg.l_error_point', l_proc_name, false);
	PERFORM set_config('xhb_housekeeping_pkg.l_cases_deleted', '0', false);
	PERFORM set_config('xhb_housekeeping_pkg.l_cases_error', '0', false);
	CALL xhb_housekeeping_pkg.initialise_arrays ();

	-- Update log record with case metrics
    UPDATE xhb_hk_results SET case_start_date = clock_timestamp() WHERE hk_run_id = current_setting('l_hk_run_results.l_hk_run_id')::bigint;
	--COMMIT;
	PERFORM set_config('l_hk_run_results.case_status', 'I', false);

    CALL xhb_housekeeping_pkg.update_log();

    -- Loop through the cases for deletion
    FOR rec IN c_cases_for_deletion LOOP

		BEGIN
			-- Indicate have started delete
			CALL xhb_housekeeping_pkg.update_case_status(	p_case_id     => rec.case_id,
								p_case_status => 'I');
			--COMMIT;

			SELECT case_number, case_type, court_id, case_title
			INTO STRICT   v_case_no, v_case_type, v_court_id, v_case_title
			FROM   XHB_CASE
			WHERE  case_id = rec.case_id;

			PERFORM set_config('l_case_history.case_id', rec.case_id::text, false);
			PERFORM set_config('l_case_history.case_no', v_case_no::text, false);
			PERFORM set_config('l_case_history.case_type', v_case_type, false);
			PERFORM set_config('l_case_history.court_id', v_court_id::text, false);

			-- Delete the case from the various areas of the system
			CALL xhb_housekeeping_pkg.delete_hearing(p_case_id   => rec.case_id);
			CALL xhb_housekeeping_pkg.delete_listing(p_case_id   => rec.case_id);
			CALL xhb_housekeeping_pkg.delete_defendant(p_case_id => rec.case_id);
			CALL xhb_housekeeping_pkg.delete_case(p_case_id      => rec.case_id);

			--COMMIT;
			PERFORM set_config(
				'xhb_housekeeping_pkg.l_cases_deleted',
				(current_setting('xhb_housekeeping_pkg.l_cases_deleted')::integer + 1)::text,
				false);

		EXCEPTION
			WHEN OTHERS THEN
				RAISE NOTICE 'process_cases_ctx exception #1 = %', SQLERRM;

				--ROLLBACK;
				PERFORM set_config(
					'xhb_housekeeping_pkg.l_cases_error',
					(current_setting('xhb_housekeeping_pkg.l_cases_error')::integer + 1)::text,
					false);

				CALL xhb_housekeeping_pkg.log_case_deletion_error(p_hk_run_id     => current_setting('l_hk_run_results.l_hk_run_id')::bigint,
                                  p_court_id      => v_court_id,
                                  p_case_id       => rec.case_id,
                                  p_case_type     => v_case_type,
                                  p_case_no       => v_case_no,
                                  p_error_message => SUBSTR(current_setting('xhb_housekeeping_pkg.l_error_point')::varchar(100) || ' ' || SQLERRM, 1, 2000));
		END;

		v_cases_processed := v_cases_processed + 1;
		EXIT WHEN v_cases_processed >= v_no_of_cases_allowed;

    END LOOP;

    IF current_setting('xhb_housekeeping_pkg.l_cases_error')::integer > 0 THEN
		PERFORM set_config('l_hk_run_results.case_status', 'E', false);
    ELSE
		PERFORM set_config('l_hk_run_results.case_status', 'S', false);
    END IF;

	PERFORM set_config(
		'l_hk_run_results.cases_deleted',
		current_setting('xhb_housekeeping_pkg.l_cases_deleted'),
		false);
	PERFORM set_config(
		'l_hk_run_results.cases_error',
		current_setting('xhb_housekeeping_pkg.l_cases_error'),
		false);

	UPDATE xhb_hk_results SET case_end_date = clock_timestamp() WHERE hk_run_id = current_setting('l_hk_run_results.l_hk_run_id')::bigint;
	--COMMIT;

    CALL xhb_housekeeping_pkg.update_log();

  EXCEPTION

    WHEN OTHERS THEN

		RAISE NOTICE 'process_cases_ctx exception #2 = %', SQLERRM;

		-- Update log record with case metrics
		PERFORM set_config('l_hk_run_results.case_status', 'F', false);
		PERFORM set_config('l_hk_run_results.case_error_message', SUBSTR(current_setting('xhb_housekeeping_pkg.l_error_point')::varchar(100) ||' '||SQLERRM,1,2000), false);
		PERFORM set_config(
			'l_hk_run_results.cases_deleted',
			current_setting('xhb_housekeeping_pkg.l_cases_deleted'),
			false);
		PERFORM set_config(
			'l_hk_run_results.cases_error',
			current_setting('xhb_housekeeping_pkg.l_cases_error'),
			false);

		UPDATE xhb_hk_results SET case_end_date = clock_timestamp() WHERE hk_run_id = current_setting('l_hk_run_results.l_hk_run_id')::bigint;
		--COMMIT;

		CALL xhb_housekeeping_pkg.update_log();

END;

$body$
LANGUAGE PLPGSQL
;
-- REVOKE ALL ON PROCEDURE xhb_housekeeping_pkg.process_cases_ctx () FROM PUBLIC;


CREATE OR REPLACE PROCEDURE xhb_housekeeping_pkg.delete_sh_justice (p_justice_id xhb_sh_justice.SH_JUSTICE_ID%TYPE) AS $body$
BEGIN
	  DELETE FROM xhb_sched_hearing_attendee
	  WHERE SH_JUSTICE_ID = p_justice_id;

	  DELETE FROM xhb_sh_justice
	  WHERE SH_JUSTICE_ID = p_justice_id;

  EXCEPTION
	WHEN OTHERS THEN
		RAISE NOTICE 'delete_sh_justice exception = %', SQLERRM;
		--ROLLBACK;

END;

$body$
LANGUAGE PLPGSQL
;
-- REVOKE ALL ON PROCEDURE xhb_housekeeping_pkg.delete_sh_justice (p_justice_id xhb_sh_justice.SH_JUSTICE_ID%TYPE) FROM PUBLIC;


CREATE OR REPLACE PROCEDURE xhb_housekeeping_pkg.delete_case_ctx (p_success_log boolean DEFAULT FALSE) AS $body$
BEGIN
	CALL xhb_housekeeping_pkg.insert_hk_log(
		p_run_type => 'C',
		p_case_limit => 0,
		p_running_list => 0,
		p_warned_list => 0,
		p_firm_list => 0,
		p_daily_list => 0);

	PERFORM set_config('xhb_housekeeping_pkg.l_success_log', p_success_log::text, false);

	CALL xhb_housekeeping_pkg.process_cases_ctx();

	-- Write logs and end the deletion run
    CALL xhb_housekeeping_pkg.end_delete_run();
END;
$body$
LANGUAGE PLPGSQL
;
-- REVOKE ALL ON PROCEDURE xhb_housekeeping_pkg.delete_case_ctx (p_success_log boolean DEFAULT FALSE) FROM PUBLIC;


CREATE OR REPLACE PROCEDURE xhb_housekeeping_pkg.delete_obsolete_judges (p_judge_limit bigint DEFAULT 0) AS $body$
DECLARE

	c_get_ref_judges CURSOR FOR
	SELECT ref_judge_id
	FROM xhb_ref_judge
	WHERE coalesce(obs_ind,'N') = 'Y'
	AND date_trunc('day', last_update_date) < clock_timestamp() + -84*'1 month'::interval;

	v_total_error	bigint := 0;
	v_total_deleted	bigint := 0;

BEGIN

	FOR rec IN c_get_ref_judges LOOP

		IF p_judge_limit > 0 THEN
          -- exit out of cursor loop if limit reached
          IF v_total_deleted + v_total_error >= p_judge_limit THEN
            EXIT;
          END IF;
        END IF;

		BEGIN

			DELETE FROM xhb_ref_judge WHERE ref_judge_id = rec.ref_judge_id;
			DELETE FROM AUD_ref_judge WHERE ref_judge_id = rec.ref_judge_id;
			--COMMIT;

			v_total_deleted := v_total_deleted + 1;

		EXCEPTION
			WHEN OTHERS THEN
				--ROLLBACK;
				RAISE NOTICE 'delete_obsolete_judges exception #1 = %', SQLERRM;
				v_total_error := v_total_error + 1;
		END;

	END LOOP;

END;
$body$
LANGUAGE PLPGSQL
;
-- REVOKE ALL ON PROCEDURE xhb_housekeeping_pkg.delete_obsolete_judges (p_judge_limit bigint DEFAULT 0) FROM PUBLIC;


CREATE OR REPLACE PROCEDURE xhb_housekeeping_pkg.set_case_to_historic (p_case_id XHB_CASE.CASE_ID%TYPE) AS $body$
DECLARE

     v_error_message varchar(1000);

BEGIN
     CALL xhb_housekeeping_pkg.update_case_status(p_case_id     => p_case_id,
                        p_case_status => 'H');

  EXCEPTION WHEN OTHERS THEN
      RAISE NOTICE 'set_case_to_historic exception = %', SQLERRM;
      RAISE;
END;

$body$
LANGUAGE PLPGSQL
;
-- REVOKE ALL ON PROCEDURE xhb_housekeeping_pkg.set_case_to_historic (p_case_id XHB_CASE.CASE_ID%TYPE) FROM PUBLIC;


CREATE OR REPLACE PROCEDURE xhb_housekeeping_pkg.find_all_cases_eligible_for_hk (p_court_id xhb_court.court_id%type DEFAULT NULL, p_upper_limit bigint DEFAULT 10000) AS $body$
DECLARE

       v_court_id bigint;
       v_upper_limit bigint;
       v_number_of_years bigint := coalesce((xhb_housekeeping_pkg.get_config_property(p_property_name => 'XHB_HK_NO_OF_YEARS'))::numeric ,7);

  eligible_cases CURSOR FOR
         SELECT court_id,
                case_id,
                case_number,
                case_type,
                last_update_date,
                date_trans_to,
                case_status
          from XHB_CASE
         where court_id = coalesce(v_court_id,court_id) and  -- check for court_id if supplied ONLY
               case_status <> 'H' and  -- eliminate previously marked HK Cases
               -- 1.Look for all cases in selected court(s) where the case number is more than 6 years previous:
               to_char(clock_timestamp(),'YYYY')::integer - substr(case_number::text,1,4)::integer > v_number_of_years-1 and
               -- 2. Is the XHB_CASE.LAST_UPDATE_DATE > 7 years ago?  b. If so then this case is eligible. Otherwise not.
               EXTRACT(year FROM age(date_trunc('day', clock_timestamp()),date_trunc('day', last_update_date))) > v_number_of_years
               order by court_id,case_id LIMIT (v_upper_limit);

       v_get_case_status varchar(250) := null;

BEGIN

      select CASE WHEN p_court_id=0 THEN NULL  ELSE p_court_id END ,
             CASE WHEN p_upper_limit=0 THEN 10000  ELSE p_upper_limit END
        into STRICT v_court_id,v_upper_limit;

   FOR i in eligible_cases loop
       CALL xhb_housekeeping_pkg.set_case_to_historic(p_case_id => i.CASE_ID);
   END LOOP;
   --COMMIT;

EXCEPTION
	WHEN OTHERS THEN
		RAISE NOTICE 'xhb_housekeeping_pkg.find_all_cases_eligible_for_hk exception = %', SQLERRM;

END;

$body$
LANGUAGE PLPGSQL
;
-- REVOKE ALL ON PROCEDURE xhb_housekeeping_pkg.find_all_cases_eligible_for_hk (p_court_id xhb_court.court_id%type DEFAULT NULL, p_upper_limit bigint DEFAULT 10000) FROM PUBLIC;


CREATE OR REPLACE PROCEDURE xhb_housekeeping_pkg.find_all_hk_cases_for_deletion (p_court_id xhb_court.court_id%type DEFAULT NULL, p_upper_limit bigint DEFAULT 10000) AS $body$
DECLARE

	v_court_id bigint;
	v_upper_limit bigint;
	v_number_of_days bigint := coalesce((xhb_housekeeping_pkg.get_config_property(p_property_name => 'XHB_HK_NO_OF_DAYS_TO_HOUSEKEEP'))::numeric ,30);

  hk_cases_for_deletion CURSOR FOR
  SELECT court_id,
         case_id,
         case_number,
         case_type,
         last_update_date,
         case_status
    from XHB_CASE
   where court_id = coalesce(v_court_id,court_id) and case_status = 'H' and
		 EXTRACT(day FROM age(date_trunc('day', clock_timestamp()),date_trunc('day', last_update_date))) >= v_number_of_days and
         not exists (SELECT 'X' from mtbl_case_history mh
           where mh.court_id = court_id and
                 mh.case_no = case_number and
                 mh.case_type = case_type)
         order by court_id,case_id LIMIT (v_upper_limit);

BEGIN

       select CASE WHEN p_court_id=0 THEN NULL  ELSE p_court_id END ,
              CASE WHEN p_upper_limit=0 THEN 10000  ELSE p_upper_limit END
        into STRICT v_court_id,v_upper_limit;

   --  Move to mtbl_case_history
   FOR i in hk_cases_for_deletion loop

       INSERT INTO mtbl_case_history(
                                      COURT_ID,
                                      CASE_TYPE,
                                      CASE_NO,
                                      LAST_UPDATE_DATE)
                              VALUES (i.court_id,
                                      i.case_type,
                                      i.case_number,
                                      clock_timestamp());
   END LOOP;
   --COMMIT;

EXCEPTION
	WHEN OTHERS THEN
		RAISE NOTICE 'xhb_housekeeping_pkg.find_all_hk_cases_for_deletion exception = %', SQLERRM;

END;

$body$
LANGUAGE PLPGSQL
;
-- REVOKE ALL ON PROCEDURE xhb_housekeeping_pkg.find_all_hk_cases_for_deletion (p_court_id xhb_court.court_id%type DEFAULT NULL, p_upper_limit bigint DEFAULT 10000) FROM PUBLIC;


CREATE OR REPLACE PROCEDURE xhb_housekeeping_pkg.del_list_data_part1 (p_court_id xhb_court.court_id%type DEFAULT NULL, p_upper_limit bigint DEFAULT 10000) AS $body$
DECLARE

	l_err_message xhb_hk_results.error_message%TYPE;
	l_err_stage    varchar(1000)  := NULL;
	l_run_start_date  timestamp := clock_timestamp();
	v_no_of_lists bigint := 0;
	v_no_of_sittings bigint := 0;
	v_hk_list_days bigint := 180;
	v_court_id XHB_CASE.court_id%type;
	l_court_id XHB_CASE.court_id%type;
	v_list_id xhb_list.list_id%type;
	v_sitting_id xhb_sitting.sitting_id%type;
	v_upper_limit bigint;

	del_xhb_sitting CURSOR FOR
	SELECT xl.court_id,xs.list_id,xs.sitting_id
	FROM xhb_sitting xs , xhb_list xl
	WHERE xl.court_id = coalesce(v_court_id,xl.court_id)
	AND xs.list_id = xl.list_id
	AND NOT EXISTS (SELECT 'X' from xhb_scheduled_hearing xsh where xs.sitting_id = xsh.sitting_id)
	ORDER BY xl.court_id,xs.sitting_id  LIMIT (v_upper_limit);

	del_xhb_hearing_list CURSOR FOR
	SELECT xhl.court_id,xhl.list_id
	FROM xhb_hearing_list xhl
	WHERE xhl.court_id = coalesce(v_court_id,xhl.court_id)
	AND NOT EXISTS (SELECT 'X' from xhb_sitting xs where xs.list_id = xhl.list_id)
	ORDER BY xhl.court_id,xhl.list_id  LIMIT (v_upper_limit);

BEGIN

    BEGIN
       select CASE WHEN p_court_id=0 THEN NULL  ELSE p_court_id END ,
              CASE WHEN p_upper_limit=0 THEN 10000  ELSE p_upper_limit END
        into STRICT v_court_id,v_upper_limit;

    EXCEPTION WHEN OTHERS THEN
		RAISE NOTICE 'del_list_data_part1 exception #1 = %', SQLERRM;
        NULL;
    END;

      -- get hk_run_id
      PERFORM set_config('l_hk_run_results.l_hk_run_id', xhb_housekeeping_pkg.get_next_hk_run_id()::text, false);

      l_run_start_date := clock_timestamp(); -- record start time to log info in xhb_hk_results
   v_no_of_lists := 0;
   v_no_of_sittings := 0;

   --  DELETE from xhb_sitting
   FOR i in del_xhb_sitting
   LOOP
      l_court_id := i.court_id;
      v_sitting_id := i.sitting_id;
      v_list_id := i.list_id;

    BEGIN
       -- delete from both XHB and AUD tables
        DELETE FROM xhb_sitting WHERE SITTING_ID = i.sitting_id;
        DELETE FROM AUD_SITTING WHERE SITTING_ID = i.sitting_id;

    EXCEPTION  WHEN OTHERS THEN

		RAISE NOTICE 'del_list_data_part1 exception #2 = %', SQLERRM;

       l_err_message := 'XHB_HK_LIST_DELETE_SITTING: sitting_id: '||v_sitting_id||' - '||substr(SQLERRM,1,500);
          -- Rollback transaction and record error
          --ROLLBACK;

        BEGIN

          -- this will log an error
          INSERT INTO XHB_HK_ERROR_LOG(hk_run_id
          ,court_id
          ,error_message
          )
          VALUES (current_setting('l_hk_run_results.l_hk_run_id')::bigint
          ,l_court_id
          ,l_err_message
          );

           --COMMIT;
         END;
      END;

      --COMMIT; -- Commit the sitting deletion
      v_no_of_sittings := v_no_of_sittings + 1;

   END LOOP;

  --  DELETE from xhb_hearing
   FOR i in del_xhb_hearing_list
   LOOP
      l_court_id := i.court_id;
      v_list_id := i.list_id;

    BEGIN
       -- delete from both XHB and AUD tables
        DELETE FROM xhb_hearing_LIST WHERE LIST_ID = i.list_id;
        DELETE FROM AUD_HEARING_LIST WHERE LIST_ID = i.list_id;

    EXCEPTION  WHEN OTHERS THEN

		RAISE NOTICE 'del_list_data_part1 exception #3 = %', SQLERRM;

       l_err_message := 'XHB_HK_LIST_DELETE_HEARING_LIST: list_id: '||v_list_id||' - '||substr(SQLERRM,1,500);
          -- Rollback transaction and record error
          --ROLLBACK;

        BEGIN

          -- this will log an error
          INSERT INTO XHB_HK_ERROR_LOG(hk_run_id
          ,court_id
          ,error_message
          )
          VALUES (current_setting('l_hk_run_results.l_hk_run_id')::bigint
          ,l_court_id
          ,l_err_message
          );

           --COMMIT;
         END;
      END;

     --COMMIT; -- Commit the sitting deletion
      v_no_of_lists := v_no_of_lists + 1;

   END LOOP;

       RAISE NOTICE 'No of sitting recs deleted : %', v_no_of_sittings;
       RAISE NOTICE 'No of hearing_list recs deleted : %', v_no_of_lists;
END;

$body$
LANGUAGE PLPGSQL
;
-- REVOKE ALL ON PROCEDURE xhb_housekeeping_pkg.del_list_data_part1 (p_court_id xhb_court.court_id%type DEFAULT NULL, p_upper_limit bigint DEFAULT 10000) FROM PUBLIC;


CREATE OR REPLACE PROCEDURE xhb_housekeeping_pkg.del_list_data_part2 (p_court_id xhb_court.court_id%type DEFAULT NULL, p_upper_limit bigint DEFAULT 10000) AS $body$
DECLARE

	l_err_message xhb_hk_results.error_message%TYPE;
	l_err_stage    varchar(1000)  := NULL;
	l_run_start_date  timestamp := clock_timestamp();
	v_no_of_lists bigint := 0;
	v_no_of_sittings bigint := 0;
	v_hk_list_days bigint := 180;
	v_court_id XHB_CASE.court_id%type;
	l_court_id XHB_CASE.court_id%type;
	v_list_id xhb_list.list_id%type;
	v_sitting_on_list_id xhb_sitting_on_list.sitting_on_list_id%type;
	v_upper_limit bigint;

	del_xhb_sitting_on_list CURSOR FOR
	SELECT xl.court_id,xl.list_id,xsl.sitting_on_list_id
	FROM xhb_sitting_on_list xsl,xhb_list xl
	WHERE xl.list_id = xsl.list_id
	AND xl.court_id = coalesce(v_court_id,xl.court_id)
	AND date_trunc('day', clock_timestamp())-date_trunc('day', xl.list_end_date) >= v_hk_list_days
	AND NOT EXISTS (SELECT 'X' from XHB_CASE_ON_LIST xcol where xcol.list_id = xl.list_id and xcol.sitting_on_list_id = xsl.sitting_on_list_id)
	ORDER BY xl.court_id,xl.list_id,xsl.sitting_on_list_id  LIMIT (v_upper_limit);

	del_xhb_list CURSOR FOR
	SELECT xl.court_id,xl.list_id
	FROM xhb_list xl
	WHERE xl.court_id = coalesce(v_court_id,xl.court_id)
	AND date_trunc('day', clock_timestamp())-date_trunc('day', xl.list_end_date) >= v_hk_list_days
	AND NOT EXISTS (SELECT 'X' from XHB_CASE_ON_LIST xcol where xcol.list_id = xl.list_id)
	AND NOT EXISTS (SELECT 'X' from xhb_sitting_on_list xsol where xsol.list_id = xl.list_id)
	ORDER BY xl.court_id,xl.list_id  LIMIT (v_upper_limit);

BEGIN

     BEGIN
       select CASE WHEN p_court_id=0 THEN NULL  ELSE p_court_id END ,
              CASE WHEN p_upper_limit=0 THEN 10000  ELSE p_upper_limit END
        into STRICT v_court_id,v_upper_limit;

      SELECT PROPERTY_VALUE INTO STRICT v_hk_list_days
         FROM XHB_CONFIG_PROP WHERE PROPERTY_NAME = 'XHB_HK_NO_OF_DAYS_LISTS_RETAINED_AFTER_CASE_DEALT';

     EXCEPTION WHEN OTHERS THEN
		RAISE NOTICE 'del_list_data_part2 exception #1 = %', SQLERRM;
          v_hk_list_days := 180;
     END;

      -- get hk_run_id
      PERFORM set_config('l_hk_run_results.l_hk_run_id', xhb_housekeeping_pkg.get_next_hk_run_id()::text, false);

      l_run_start_date := clock_timestamp(); -- record start time to log info in xhb_hk_results
   v_no_of_lists := 0;
   v_no_of_sittings := 0;

   --  DELETE from xhb_sitting_on_list
   FOR i in del_xhb_sitting_on_list
   LOOP
      l_court_id := i.court_id;
      v_sitting_on_list_id := i.sitting_on_list_id;
      v_list_id := i.list_id;

    BEGIN
       -- delete from both XHB and AUD tables
        DELETE FROM xhb_sitting_on_list WHERE SITTING_ON_LIST_ID = i.sitting_on_list_id;
        DELETE FROM AUD_SITTING_ON_LIST WHERE SITTING_ON_LIST_ID = i.sitting_on_list_id;

    EXCEPTION  WHEN OTHERS THEN

		RAISE NOTICE 'del_list_data_part2 exception #2 = %', SQLERRM;

       l_err_message := 'XHB_HK_LIST_DELETE_SITTING_ON_LIST: sitting_on_list_id: '||v_sitting_on_list_id||' - '||substr(SQLERRM,1,500);
          -- Rollback transaction and record error
          --ROLLBACK;

        BEGIN

          -- this will log an error
          INSERT INTO XHB_HK_ERROR_LOG(hk_run_id
          ,court_id
          ,error_message
          )
          VALUES (current_setting('l_hk_run_results.l_hk_run_id')::bigint
          ,l_court_id
          ,l_err_message
          );

           --COMMIT;
         END;
      END;

      --COMMIT; -- Commit the sitting_on_list deletion
      v_no_of_sittings := v_no_of_sittings + 1;

   END LOOP;

  --  DELETE from xhb_list
   FOR i in del_xhb_list
   LOOP
      l_court_id := i.court_id;
      v_list_id := i.list_id;

      RAISE NOTICE 'DELETE_XHB_LIST: v_court_id : %,%', l_court_id, v_list_id;
    BEGIN
       -- delete from both XHB and AUD tables
        DELETE FROM xhb_list WHERE court_id = l_court_id AND LIST_ID = i.list_id;
        DELETE FROM AUD_LIST WHERE court_id = l_court_id AND LIST_ID = i.list_id;

    EXCEPTION  WHEN OTHERS THEN

		RAISE NOTICE 'del_list_data_part2 exception #3 = %', SQLERRM;

       l_err_message := 'XHB_HK_LIST_DELETE_LIST: list_id: '||v_list_id||' - '||substr(SQLERRM,1,500);
          -- Rollback transaction and record error
          --ROLLBACK;

        BEGIN

          -- this will log an error
          INSERT INTO XHB_HK_ERROR_LOG(hk_run_id
          ,court_id
          ,error_message
          )
          VALUES (current_setting('l_hk_run_results.l_hk_run_id')::bigint
          ,l_court_id
          ,l_err_message
          );

           --COMMIT;
         END;
      END;

     --COMMIT; -- Commit the sitting deletion
      v_no_of_lists := v_no_of_lists + 1;

   END LOOP;

       RAISE NOTICE 'No of sitting_on_list recs deleted : %', v_no_of_sittings;
       RAISE NOTICE 'No of list recs deleted : %', v_no_of_lists;
END;

$body$
LANGUAGE PLPGSQL
;
-- REVOKE ALL ON PROCEDURE xhb_housekeeping_pkg.del_list_data_part2 (p_court_id xhb_court.court_id%type DEFAULT NULL, p_upper_limit bigint DEFAULT 10000) FROM PUBLIC;


CREATE OR REPLACE PROCEDURE xhb_housekeeping_pkg.delete_cpp_publicdisplay (p_no_of_days bigint, p_status xhb_cpp_formatting.format_status%TYPE) AS $body$
DECLARE

  rec RECORD;

BEGIN
      FOR rec IN (SELECT cpp_formatting_id, xml_document_clob_id
                    FROM xhb_cpp_formatting
                   WHERE document_type = 'PD'
                     AND EXTRACT(day FROM age(date_trunc('day', clock_timestamp()),date_trunc('day', last_update_date))) > p_no_of_days
                     AND format_status = p_status)  LOOP
          BEGIN
            CALL xhb_housekeeping_pkg.delete_record(p_table_name => 'xhb_cpp_formatting', p_where_clause => 'CPP_FORMATTING_ID = '||rec.cpp_formatting_id::varchar, p_error_point => 'process_cpp_formatting');
            CALL xhb_housekeeping_pkg.delete_record(p_table_name => 'AUD_CPP_FORMATTING', p_where_clause => 'CPP_FORMATTING_ID = '||rec.cpp_formatting_id::varchar, p_error_point => 'process_cpp_formatting');
            IF rec.xml_document_clob_id IS NOT NULL THEN
			   -- Try and delete the CLOBs although they may be referenced by xhb_cpp_staging_inbound so if that happens then leave the CLOB alone, it will
			   -- be cleaned up when xhb_cpp_staging_inbound is processed by housekeeping.
			   BEGIN
				   CALL xhb_housekeeping_pkg.delete_record(p_table_name => 'XHB_CLOB', p_where_clause => 'CLOB_ID = '||rec.xml_document_clob_id::varchar, p_error_point => 'process_cpp_formatting');
				   CALL xhb_housekeeping_pkg.delete_record(p_table_name => 'AUD_CLOB', p_where_clause => 'CLOB_ID = '||rec.xml_document_clob_id::varchar, p_error_point => 'process_cpp_formatting');
			   EXCEPTION
					WHEN OTHERS THEN
						RAISE NOTICE 'delete_cpp_publicdisplay exception #1 = %', SQLERRM;
			   END;
            END IF;

			PERFORM set_config(
				'l_hk_cpp_run_results.pd_deleted',
				(coalesce(current_setting('l_hk_cpp_run_results.pd_deleted')::integer,0) + 1)::text,
				false);

            --COMMIT;
          EXCEPTION WHEN OTHERS THEN
            --ROLLBACK;
			RAISE NOTICE 'delete_cpp_publicdisplay exception #2 = %', SQLERRM;

			PERFORM set_config(
				'l_hk_cpp_run_results.pd_error',
				(coalesce(current_setting('l_hk_cpp_run_results.pd_error')::integer,0) + 1)::text,
				false);
			PERFORM set_config(
				'l_hk_cpp_run_results.pd_error_message',
				SUBSTR(current_setting('xhb_housekeeping_pkg.l_error_point')::varchar(100)||' '||SQLERRM, 1,500),
				false);

            CALL xhb_housekeeping_pkg.log_cpp_hk_error(
				p_hk_cpp_run_id => current_setting('l_hk_cpp_run_results.l_hk_cpp_run_id')::bigint,
				p_error_message => current_setting('l_hk_cpp_run_results.pd_error_message')::varchar(2000));
          END;
      END LOOP;
END;

$body$
LANGUAGE PLPGSQL
;
-- REVOKE ALL ON PROCEDURE xhb_housekeeping_pkg.delete_cpp_publicdisplay (p_no_of_days bigint, p_status xhb_cpp_formatting.format_status%TYPE) FROM PUBLIC;


CREATE OR REPLACE PROCEDURE xhb_housekeeping_pkg.delete_cpp_iwp (p_no_of_days bigint, p_status xhb_cpp_formatting.format_status%TYPE) AS $body$
DECLARE

  rec RECORD;
  merge_rec RECORD;

BEGIN
      FOR rec IN (SELECT cpp_formatting_id, xml_document_clob_id
                    FROM xhb_cpp_formatting
                   WHERE document_type = 'IWP'
                     AND EXTRACT(day FROM age(date_trunc('day', clock_timestamp()),date_trunc('day', last_update_date))) > p_no_of_days
                     AND format_status = p_status)  LOOP
          BEGIN
            -- Delete the xhb_cpp_formatting_merge records and any associated XHB_CLOB records
            FOR merge_rec IN (SELECT xcfm.cpp_formatting_merge_id, xcfm.xhibit_clob_id
                    FROM xhb_cpp_formatting_merge xcfm
                    WHERE xcfm.cpp_formatting_id = rec.cpp_formatting_id)  LOOP
               CALL xhb_housekeeping_pkg.delete_record(p_table_name => 'xhb_cpp_formatting_merge', p_where_clause => 'CPP_FORMATTING_MERGE_ID = '||merge_rec.cpp_formatting_merge_id::varchar, p_error_point => 'process_cpp_formatting');
               CALL xhb_housekeeping_pkg.delete_record(p_table_name => 'AUD_CPP_FORMATTING_MERGE', p_where_clause => 'CPP_FORMATTING_MERGE_ID = '||merge_rec.cpp_formatting_merge_id::varchar, p_error_point => 'process_cpp_formatting');
               IF merge_rec.xhibit_clob_id IS NOT NULL THEN
                  -- Try and delete the CLOBs although they may be referenced by XHB_FORMATTING so if that happens then leave the CLOB alone, it will
				  -- be cleaned up when XHB_FORMATTING is processed by housekeeping.
				  BEGIN
					  CALL xhb_housekeeping_pkg.delete_record(p_table_name => 'XHB_CLOB', p_where_clause => 'CLOB_ID = '||merge_rec.xhibit_clob_id::varchar, p_error_point => 'process_cpp_formatting');
					  CALL xhb_housekeeping_pkg.delete_record(p_table_name => 'AUD_CLOB', p_where_clause => 'CLOB_ID = '||merge_rec.xhibit_clob_id::varchar, p_error_point => 'process_cpp_formatting');
				  EXCEPTION
					WHEN OTHERS THEN
						RAISE NOTICE 'delete_cpp_iwp exception #1 = %', SQLERRM;
				  END;
               END IF;
            END LOOP;

            -- Delete the xhb_cpp_formatting records and any associated XHB_CLOB records
            CALL xhb_housekeeping_pkg.delete_record(p_table_name => 'xhb_cpp_formatting', p_where_clause => 'CPP_FORMATTING_ID = '||rec.cpp_formatting_id::varchar, p_error_point => 'process_cpp_formatting');
            CALL xhb_housekeeping_pkg.delete_record(p_table_name => 'AUD_CPP_FORMATTING', p_where_clause => 'CPP_FORMATTING_ID = '||rec.cpp_formatting_id::varchar, p_error_point => 'process_cpp_formatting');
            IF rec.xml_document_clob_id IS NOT NULL THEN
			   -- Try and delete the CLOBs although they may be referenced by XHB_FORMATTING so if that happens then leave the CLOB alone, it will
			   -- be cleaned up when XHB_FORMATTING is processed by housekeeping.
			   BEGIN
					CALL xhb_housekeeping_pkg.delete_record(p_table_name => 'XHB_CLOB', p_where_clause => 'CLOB_ID = '||rec.xml_document_clob_id::varchar, p_error_point => 'process_cpp_formatting');
					CALL xhb_housekeeping_pkg.delete_record(p_table_name => 'AUD_CLOB', p_where_clause => 'CLOB_ID = '||rec.xml_document_clob_id::varchar, p_error_point => 'process_cpp_formatting');
			   EXCEPTION
					WHEN OTHERS THEN
						RAISE NOTICE 'delete_cpp_iwp exception #2 = %', SQLERRM;
			   END;
            END IF;

			PERFORM set_config(
				'l_hk_cpp_run_results.iwp_deleted',
				coalesce(current_setting('l_hk_cpp_run_results.iwp_deleted')::integer,0) + 1,
				false);

            --COMMIT;
          EXCEPTION WHEN OTHERS THEN
            --ROLLBACK;
			RAISE NOTICE 'delete_cpp_iwp exception #3 = %', SQLERRM;

			PERFORM set_config(
				'l_hk_cpp_run_results.iwp_error',
				coalesce(current_setting('l_hk_cpp_run_results.iwp_error')::integer,0) + 1,
				false);
			PERFORM set_config(
				'l_hk_cpp_run_results.iwp_error_message',
				SUBSTR(current_setting('xhb_housekeeping_pkg.l_error_point')::varchar(100)||' '||SQLERRM, 1,500),
				false);

            CALL xhb_housekeeping_pkg.log_cpp_hk_error(
				p_hk_cpp_run_id => current_setting('l_hk_cpp_run_results.l_hk_cpp_run_id')::bigint,
				p_error_message => current_setting('l_hk_cpp_run_results.iwp_error_message')::varchar(2000));
          END;
      END LOOP;
END;

$body$
LANGUAGE PLPGSQL
;
-- REVOKE ALL ON PROCEDURE xhb_housekeeping_pkg.delete_cpp_iwp (p_no_of_days bigint, p_status xhb_cpp_formatting.format_status%TYPE) FROM PUBLIC;


CREATE OR REPLACE PROCEDURE xhb_housekeeping_pkg.process_cpp_formatting (p_MS_days bigint DEFAULT 7, p_MF_days bigint DEFAULT 30) AS $body$
BEGIN
    -- Start PD process
	PERFORM set_config('l_hk_cpp_run_results.pd_status','I', false);
	PERFORM set_config('l_hk_cpp_run_results.pd_error','0', false);
	PERFORM set_config('l_hk_cpp_run_results.pd_deleted','0', false);

    CALL xhb_housekeeping_pkg.update_hk_cpp_log();

    CALL xhb_housekeeping_pkg.delete_cpp_publicdisplay(p_no_of_days => p_MS_days, p_status => 'MS');
    CALL xhb_housekeeping_pkg.delete_cpp_publicdisplay(p_no_of_days => p_MF_days, p_status => 'MF');
    CALL xhb_housekeeping_pkg.delete_cpp_publicdisplay(p_no_of_days => p_MF_days, p_status => 'ND');

    -- End PD process
	IF coalesce(current_setting('l_hk_cpp_run_results.pd_error')::integer,0) > 0 THEN
		PERFORM set_config('l_hk_cpp_run_results.pd_status','E', false);
	ELSE
		PERFORM set_config('l_hk_cpp_run_results.pd_status','S', false);
	END IF;

	-- Start IWP process
	PERFORM set_config('l_hk_cpp_run_results.iwp_status','I', false);
	PERFORM set_config('l_hk_cpp_run_results.iwp_error','0', false);
	PERFORM set_config('l_hk_cpp_run_results.iwp_deleted','0', false);

    CALL xhb_housekeeping_pkg.update_hk_cpp_log();

    CALL xhb_housekeeping_pkg.delete_cpp_iwp(p_no_of_days => p_MS_days, p_status => 'MS');
    CALL xhb_housekeeping_pkg.delete_cpp_iwp(p_no_of_days => p_MF_days, p_status => 'MF');
    CALL xhb_housekeeping_pkg.delete_cpp_iwp(p_no_of_days => p_MF_days, p_status => 'ND');

    -- End IWP process
	IF coalesce(current_setting('l_hk_cpp_run_results.iwp_error')::integer,0) > 0 THEN
		PERFORM set_config('l_hk_cpp_run_results.iwp_status','E', false);
	ELSE
		PERFORM set_config('l_hk_cpp_run_results.iwp_status','S', false);
	END IF;

    CALL xhb_housekeeping_pkg.update_hk_cpp_log();
END;

$body$
LANGUAGE PLPGSQL
;
-- REVOKE ALL ON PROCEDURE xhb_housekeeping_pkg.process_cpp_formatting (p_MS_days bigint DEFAULT 7, p_MF_days bigint DEFAULT 30) FROM PUBLIC;


CREATE OR REPLACE PROCEDURE xhb_housekeeping_pkg.delete_cpp_staging (p_vs_no_of_days bigint, p_other_no_of_days bigint) AS $body$
DECLARE

  rec RECORD;

BEGIN
	  -- Delete the records with a validation status of 'VS'
      FOR rec IN (SELECT cpp_staging_inbound_id, clob_id
                    FROM xhb_cpp_staging_inbound
                   WHERE validation_status = 'VS'
				     AND acknowledgment_status = 'AS'
                     AND EXTRACT(day FROM age(date_trunc('day', clock_timestamp()),date_trunc('day', time_loaded))) > p_vs_no_of_days
                     AND cpp_staging_inbound_id NOT IN (SELECT staging_table_id FROM xhb_cpp_formatting))  LOOP
          BEGIN
            CALL xhb_housekeeping_pkg.delete_record(p_table_name => 'xhb_cpp_staging_inbound', p_where_clause => 'CPP_STAGING_INBOUND_ID = '||rec.cpp_staging_inbound_id::varchar, p_error_point => 'process_cpp_staging');
            CALL xhb_housekeeping_pkg.delete_record(p_table_name => 'AUD_CPP_STAGING_INBOUND', p_where_clause => 'CPP_STAGING_INBOUND_ID = '||rec.cpp_staging_inbound_id::varchar, p_error_point => 'process_cpp_staging');
            IF rec.clob_id IS NOT NULL THEN
			   -- Try and delete the CLOBs although they may be referenced by XHB_FORMATTING so if that happens then leave the CLOB alone, it will
			   -- be cleaned up when XHB_FORMATTING is processed by housekeeping.
			   BEGIN
				   CALL xhb_housekeeping_pkg.delete_record(p_table_name => 'XHB_CLOB', p_where_clause => 'CLOB_ID = '||rec.clob_id::varchar, p_error_point => 'process_cpp_staging');
				   CALL xhb_housekeeping_pkg.delete_record(p_table_name => 'AUD_CLOB', p_where_clause => 'CLOB_ID = '||rec.clob_id::varchar, p_error_point => 'process_cpp_staging');
			   EXCEPTION
					WHEN OTHERS THEN
						RAISE NOTICE 'delete_cpp_staging exception #1 = %', SQLERRM;
			   END;
            END IF;

			PERFORM set_config(
				'l_hk_cpp_run_results.staging_deleted',
				(coalesce(current_setting('l_hk_cpp_run_results.staging_deleted')::integer,0) + 1)::text,
				false);

            --COMMIT;
          EXCEPTION WHEN OTHERS THEN
            --ROLLBACK;
			RAISE NOTICE 'delete_cpp_staging exception #2 = %', SQLERRM;

			PERFORM set_config(
				'l_hk_cpp_run_results.staging_error',
				(coalesce(current_setting('l_hk_cpp_run_results.staging_error')::integer,0) + 1)::text,
				false);
			PERFORM set_config(
				'l_hk_cpp_run_results.staging_error_message',
				SUBSTR(current_setting('xhb_housekeeping_pkg.l_error_point')::varchar(100)||' '||SQLERRM, 1,500),
				false);

            CALL xhb_housekeeping_pkg.log_cpp_hk_error(
				p_hk_cpp_run_id => current_setting('l_hk_cpp_run_results.l_hk_cpp_run_id')::bigint,
				p_error_message => current_setting('l_hk_cpp_run_results.staging_error_message')::varchar(2000));
          END;
      END LOOP;

	  -- Delete the other records
	  FOR rec IN (SELECT cpp_staging_inbound_id, clob_id
                    FROM xhb_cpp_staging_inbound
                   WHERE acknowledgment_status = 'AS'
                     AND EXTRACT(day FROM age(date_trunc('day', clock_timestamp()),date_trunc('day', time_loaded))) > p_other_no_of_days
                    AND cpp_staging_inbound_id NOT IN (SELECT staging_table_id FROM xhb_cpp_formatting))  LOOP
          BEGIN
            CALL xhb_housekeeping_pkg.delete_record(p_table_name => 'xhb_cpp_staging_inbound', p_where_clause => 'CPP_STAGING_INBOUND_ID = '||rec.cpp_staging_inbound_id::varchar, p_error_point => 'process_cpp_staging');
            CALL xhb_housekeeping_pkg.delete_record(p_table_name => 'AUD_CPP_STAGING_INBOUND', p_where_clause => 'CPP_STAGING_INBOUND_ID = '||rec.cpp_staging_inbound_id::varchar, p_error_point => 'process_cpp_staging');
            IF rec.clob_id IS NOT NULL THEN
			   -- Try and delete the CLOBs although they may be referenced by XHB_FORMATTING so if that happens then leave the CLOB alone, it will
			   -- be cleaned up when XHB_FORMATTING is processed by housekeeping.
			   BEGIN
				   CALL xhb_housekeeping_pkg.delete_record(p_table_name => 'XHB_CLOB', p_where_clause => 'CLOB_ID = '||rec.clob_id::varchar, p_error_point => 'process_cpp_staging');
				   CALL xhb_housekeeping_pkg.delete_record(p_table_name => 'AUD_CLOB', p_where_clause => 'CLOB_ID = '||rec.clob_id::varchar, p_error_point => 'process_cpp_staging');
			   EXCEPTION
					WHEN OTHERS THEN
						RAISE NOTICE 'delete_cpp_staging exception #3 = %', SQLERRM;
			   END;
            END IF;

			PERFORM set_config(
				'l_hk_cpp_run_results.staging_deleted',
				(coalesce(current_setting('l_hk_cpp_run_results.staging_deleted')::integer,0) + 1)::text,
				false);

            --COMMIT;
          EXCEPTION WHEN OTHERS THEN
            --ROLLBACK;
			RAISE NOTICE 'delete_cpp_staging exception #4 = %', SQLERRM;

			PERFORM set_config(
				'l_hk_cpp_run_results.staging_error',
				(coalesce(current_setting('l_hk_cpp_run_results.staging_error')::integer,0) + 1)::text,
				false);
			PERFORM set_config(
				'l_hk_cpp_run_results.staging_error_message',
				SUBSTR(current_setting('xhb_housekeeping_pkg.l_error_point')::varchar(100)||' '||SQLERRM, 1,500),
				false);

            CALL xhb_housekeeping_pkg.log_cpp_hk_error(
				p_hk_cpp_run_id => current_setting('l_hk_cpp_run_results.l_hk_cpp_run_id')::bigint,
				p_error_message => current_setting('l_hk_cpp_run_results.staging_error_message')::varchar(2000));

          END;
      END LOOP;

END;

$body$
LANGUAGE PLPGSQL
;
-- REVOKE ALL ON PROCEDURE xhb_housekeeping_pkg.delete_cpp_staging (p_vs_no_of_days bigint, p_other_no_of_days bigint) FROM PUBLIC;


CREATE OR REPLACE PROCEDURE xhb_housekeeping_pkg.process_cpp_staging (p_vs_no_of_days bigint DEFAULT 7, p_other_no_of_days bigint DEFAULT 30) AS $body$
BEGIN
    -- Start process
	PERFORM set_config('l_hk_cpp_run_results.staging_status','I', false);
	PERFORM set_config('l_hk_cpp_run_results.staging_error','0', false);
	PERFORM set_config('l_hk_cpp_run_results.staging_deleted','0', false);

    CALL xhb_housekeeping_pkg.update_hk_cpp_log();

    CALL xhb_housekeeping_pkg.delete_cpp_staging(p_vs_no_of_days => p_vs_no_of_days, p_other_no_of_days => p_other_no_of_days);

    -- End IWP process
	IF coalesce(current_setting('l_hk_cpp_run_results.staging_error')::integer,0) > 0 THEN
		PERFORM set_config('l_hk_cpp_run_results.staging_status','E', false);
	ELSE
		PERFORM set_config('l_hk_cpp_run_results.staging_status','S', false);
	END IF;

    CALL xhb_housekeeping_pkg.update_hk_cpp_log();
END;

$body$
LANGUAGE PLPGSQL
;
-- REVOKE ALL ON PROCEDURE xhb_housekeeping_pkg.process_cpp_staging (p_vs_no_of_days bigint DEFAULT 7, p_other_no_of_days bigint DEFAULT 30) FROM PUBLIC;


CREATE OR REPLACE PROCEDURE xhb_housekeeping_pkg.process_cpp (p_MS_days bigint DEFAULT 7, p_MF_days bigint DEFAULT 30) AS $body$
BEGIN
    CALL xhb_housekeeping_pkg.insert_hk_cpp_log();

    CALL xhb_housekeeping_pkg.process_cpp_listing(p_MS_days => p_MS_days, p_MF_days => p_MF_days);
    CALL xhb_housekeeping_pkg.process_cpp_formatting(p_MS_days => p_MS_days, p_MF_days => p_MF_days);
	CALL xhb_housekeeping_pkg.process_cpp_staging(p_vs_no_of_days => p_MS_days, p_other_no_of_days => p_MF_days);

	-- write errors to external file
    CALL xhb_housekeeping_pkg.write_hk_cpp_error_log_file(current_setting('l_hk_cpp_run_results.l_hk_cpp_run_id')::bigint);

	UPDATE XHB_HK_CPP_RESULTS SET run_end_date = clock_timestamp() WHERE hk_cpp_run_id = current_setting('l_hk_cpp_run_results.l_hk_cpp_run_id')::bigint;
	--COMMIT;

    CALL xhb_housekeeping_pkg.update_hk_cpp_log();

	-- write metrics to external file
    CALL xhb_housekeeping_pkg.write_hk_cpp_metrics_log_file(current_setting('l_hk_cpp_run_results.l_hk_cpp_run_id')::bigint, p_MS_days, p_MF_days);
END;

$body$
LANGUAGE PLPGSQL
;
-- REVOKE ALL ON PROCEDURE xhb_housekeeping_pkg.process_cpp (p_MS_days bigint DEFAULT 7, p_MF_days bigint DEFAULT 30) FROM PUBLIC;
-- End of Oracle package 'xhb_housekeeping_pkg' declaration
