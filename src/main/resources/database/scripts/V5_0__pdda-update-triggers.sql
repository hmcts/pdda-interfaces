SET client_encoding TO 'UTF8';

DROP TRIGGER IF EXISTS dm_local_proxy_bur_tr ON xhb_disp_mgr_local_proxy CASCADE;
CREATE OR REPLACE FUNCTION trigger_fct_dm_local_proxy_bur_tr() RETURNS trigger AS $BODY$
DECLARE

  l_trig_event varchar(1) := NULL;
BEGIN
  BEGIN

	/* Determine whether UPDATING or DELETING */

	IF TG_OP = 'UPDATE' THEN

		l_trig_event := 'U';

		/* If the user is the connection pool user as defined in XHB_SYS_USER_INFORMATION */

		IF (XHB_CUSTOM_PKG.IS_CONNECTION_POOL_USER() = 1) THEN
		  IF (OLD.VERSION != NEW.VERSION) THEN
		    /* Someone has pulled the rug out from below! */

		    RAISE EXCEPTION 'optimistic_lock_prob' USING ERRCODE = '50011';
		  END IF;
		END IF;

		/* Increment version and set updated date to now */

		SELECT OLD.VERSION + 1,
			   LOCALTIMESTAMP
		INTO STRICT   NEW.VERSION,
			   NEW.LAST_UPDATE_DATE
		;

		/* If the user is not the connection pool user as defined in XHB_SYS_USER_INFORMATION */

		IF (XHB_CUSTOM_PKG.IS_CONNECTION_POOL_USER() = 0) THEN
		  SELECT coalesce(current_setting('SESSION_USER', true),'PDM')
		  INTO STRICT   NEW.LAST_UPDATED_BY
		;
		END IF;

	ELSE -- Must be DELETING
		l_trig_event := 'D';

	END IF;

	/* Is Auditing on this table required */

	IF (XHB_CUSTOM_PKG.IS_AUDIT_REQUIRED('XHB_DISP_MGR_LOCAL_PROXY') = 1) THEN
		INSERT INTO pdda.AUD_DISP_MGR_LOCAL_PROXY
		VALUES (OLD.LOCAL_PROXY_ID,
				OLD.IP_ADDRESS,
				OLD.HOSTNAME,
				OLD.RAG_STATUS,
				OLD.RAG_STATUS_DATE,
				OLD.COURT_SITE_ID,
				OLD.LAST_UPDATE_DATE,
				OLD.CREATION_DATE,
				OLD.CREATED_BY,
				OLD.LAST_UPDATED_BY,
				OLD.VERSION,
				l_trig_event);
	END IF;

  END;
IF TG_OP = 'DELETE' THEN
	RETURN OLD;
ELSE
	RETURN NEW;
END IF;

END
$BODY$
 LANGUAGE 'plpgsql' SECURITY DEFINER;
-- REVOKE ALL ON FUNCTION trigger_fct_dm_local_proxy_bur_tr() FROM PUBLIC;

CREATE TRIGGER dm_local_proxy_bur_tr
	BEFORE UPDATE OR DELETE ON xhb_disp_mgr_local_proxy FOR EACH ROW
	EXECUTE PROCEDURE trigger_fct_dm_local_proxy_bur_tr();
