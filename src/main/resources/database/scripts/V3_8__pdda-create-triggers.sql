SET client_encoding TO 'UTF8';

DROP TRIGGER IF EXISTS xhb_courtel_list_bir_tr ON xhb_courtel_list CASCADE;
CREATE OR REPLACE FUNCTION trigger_fct_xhb_courtel_list_bir_tr() RETURNS trigger AS $BODY$
BEGIN

  IF NEW.COURTEL_LIST_ID IS NULL THEN

    SELECT nextval('xhb_courtel_list_seq')
    INTO STRICT   NEW.COURTEL_LIST_ID
;

  END IF;

  IF ((NEW.LAST_UPDATED_BY IS NULL) OR (NEW.CREATED_BY IS NULL)) THEN

    SELECT coalesce(current_setting('SESSION_USER', true),'PDDA'),
           coalesce(current_setting('SESSION_USER', true),'PDDA')
    INTO STRICT   NEW.LAST_UPDATED_BY,
           NEW.CREATED_BY
;

  END IF;

  SELECT LOCALTIMESTAMP,
         LOCALTIMESTAMP,
         1
  INTO STRICT   NEW.LAST_UPDATE_DATE,
         NEW.CREATION_DATE,
         NEW.VERSION
;

RETURN NEW;
END
$BODY$
 LANGUAGE 'plpgsql' SECURITY DEFINER;
-- REVOKE ALL ON FUNCTION trigger_fct_xhb_courtel_list_bir_tr() FROM PUBLIC;

CREATE TRIGGER xhb_courtel_list_bir_tr
	BEFORE INSERT ON xhb_courtel_list FOR EACH ROW
	EXECUTE PROCEDURE trigger_fct_xhb_courtel_list_bir_tr();

DROP TRIGGER IF EXISTS xhb_courtel_list_bur_tr ON xhb_courtel_list CASCADE;
CREATE OR REPLACE FUNCTION trigger_fct_xhb_courtel_list_bur_tr() RETURNS trigger AS $BODY$
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

    SELECT OLD.VERSION + 1,
           LOCALTIMESTAMP
    INTO STRICT   NEW.VERSION,
           NEW.LAST_UPDATE_DATE
;

    /* If the user is not the connection pool user as defined in XHB_SYS_USER_INFORMATION */

    IF (XHB_CUSTOM_PKG.IS_CONNECTION_POOL_USER() = 0) THEN

      SELECT coalesce(current_setting('SESSION_USER', true),'PDDA')
      INTO STRICT   NEW.LAST_UPDATED_BY
;

    END IF;

  ELSE -- Must be DELETING
    l_trig_event := 'D';

  END IF;

  /* Is Auditing on this table required */

  IF (XHB_CUSTOM_PKG.IS_AUDIT_REQUIRED('XHB_COURTEL_LIST') = 1) THEN

    INSERT INTO pdda.AUD_COURTEL_LIST
    VALUES (OLD.COURTEL_LIST_ID,
            OLD.XML_DOCUMENT_ID,
            OLD.XML_DOCUMENT_CLOB_ID,
            OLD.BLOB_ID,
            OLD.SENT_TO_COURTEL,
            OLD.NUM_SEND_ATTEMPTS,
            OLD.LAST_ATTEMPT_DATETIME,
            OLD.MESSAGE_TEXT,
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
-- REVOKE ALL ON FUNCTION trigger_fct_xhb_courtel_list_bur_tr() FROM PUBLIC;

CREATE TRIGGER xhb_courtel_list_bur_tr
	BEFORE UPDATE OR DELETE ON xhb_courtel_list FOR EACH ROW
	EXECUTE PROCEDURE trigger_fct_xhb_courtel_list_bur_tr();
