SET client_encoding TO 'UTF8';

DROP SCHEMA IF EXISTS xhb_custom_pkg CASCADE;
CREATE SCHEMA IF NOT EXISTS xhb_custom_pkg;

CREATE OR REPLACE FUNCTION xhb_custom_pkg.is_connection_pool_user () RETURNS bigint AS $body$
DECLARE

    l_conn_user  varchar(255);
    l_curr_user  varchar(255);

BEGIN

    SELECT current_setting('SESSION_USER', true)
    INTO STRICT   l_curr_user;

    IF (l_curr_user IS NULL) THEN
		RAISE EXCEPTION 'no_sess_user' USING ERRCODE = '50009';
    END IF;

    SELECT connection_pool_user_name
    INTO STRICT   l_conn_user
    FROM   pdda.xhb_sys_user_information;

    IF (l_conn_user IS NULL) THEN
		RAISE EXCEPTION 'no_data' USING ERRCODE = '50008';
    END IF;

    IF ( l_conn_user != l_curr_user) THEN
		/* Not Conection Pool User */
		RETURN 0;
    ELSE
		/* Connection Pool User */
		RETURN 1;
    END IF;

EXCEPTION
	WHEN SQLSTATE '50009' THEN
		RAISE NOTICE 'CURRENT_SETTING DID NOT RETURN SESSION_USER - %', sqlerrm;
        RETURN 1;

	WHEN SQLSTATE '50008' THEN
		RAISE NOTICE 'XHB_SYS_USER_INFORMATION IS EMPTY - %', sqlerrm;
		RETURN 1;

	WHEN others THEN
		RAISE NOTICE 'ERROR ENCOUNTERED IN IS_CONNECTION_POOL_USER - %', sqlerrm;
		RETURN 1;

END;

$body$
LANGUAGE PLPGSQL
SECURITY DEFINER
 STABLE;
-- REVOKE ALL ON FUNCTION xhb_custom_pkg.is_connection_pool_user () FROM PUBLIC;


CREATE OR REPLACE FUNCTION xhb_custom_pkg.is_audit_required (table_name text) RETURNS bigint AS $body$
DECLARE

	c_audit CURSOR FOR
	SELECT auditable
	FROM   pdda.xhb_sys_audit
	WHERE  table_to_audit = table_name;

	l_audit varchar(1);

BEGIN

	IF EXISTS(SELECT * FROM pg_cursors WHERE name = 'c_audit') THEN
		CLOSE c_audit;
	END IF;

    OPEN c_audit;
    FETCH c_audit
    INTO  l_audit;
    CLOSE c_audit;

	IF l_audit = 'Y' THEN
		/* Audit is Required */
		RETURN 1;
	ELSE
		/* Audit is not Required */
		RETURN 0;
	END IF;

END;


$body$
LANGUAGE PLPGSQL
SECURITY DEFINER
;
-- REVOKE ALL ON FUNCTION xhb_custom_pkg.is_audit_required (table_name text) FROM PUBLIC;


CREATE OR REPLACE FUNCTION xhb_custom_pkg.get_ref_judge_id (arg0 bigint) RETURNS bigint AS $body$
DECLARE

    l_ref_judge_id  bigint;
    l_ref_judge_id1 bigint;

BEGIN

    SELECT ref_judge_id
    INTO STRICT   l_ref_judge_id
    FROM   pdda.XHB_SCHED_HEARING_ATTENDEE
    WHERE  attendee_type = 'J'
    AND    scheduled_hearing_id = arg0
    AND    sh_attendee_id = (SELECT MAX(sh_attendee_id)
                             FROM   pdda.XHB_SCHED_HEARING_ATTENDEE
                             WHERE  attendee_type = 'J'
                             AND scheduled_hearing_id = arg0 );

    RETURN l_ref_judge_id;

EXCEPTION
	WHEN no_data_found THEN
		BEGIN
			SELECT ref_judge_id
			INTO STRICT   l_ref_judge_id1
			FROM   pdda.XHB_SITTING xs, pdda.XHB_SCHEDULED_HEARING xsh
			WHERE  xs.sitting_id = xsh.sitting_id
			AND    scheduled_hearing_id = arg0;

			RETURN l_ref_judge_id1;

		EXCEPTION
			WHEN no_data_found THEN
				RETURN -1;
        END;

END;

$body$
LANGUAGE PLPGSQL
SECURITY DEFINER
 STABLE;
-- REVOKE ALL ON FUNCTION xhb_custom_pkg.get_ref_judge_id (arg0 bigint) FROM PUBLIC;
-- End of Oracle package 'XHB_CUSTOM_PKG' declaration
