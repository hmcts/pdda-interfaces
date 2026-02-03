SET client_encoding TO 'UTF8';

  DROP TRIGGER IF EXISTS xhb_cath_document_link_bir_tr ON xhb_cath_document_link CASCADE;
  CREATE OR REPLACE FUNCTION trigger_fct_xhb_cath_document_link_bir_tr() RETURNS trigger AS $BODY$
  BEGIN

    IF NEW.CATH_DOCUMENT_LINK_ID IS NULL THEN

      SELECT nextval('xhb_cath_document_link_seq')
      INTO STRICT   NEW.CATH_DOCUMENT_LINK_ID
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
  -- REVOKE ALL ON FUNCTION trigger_fct_xhb_cath_document_link_bir_tr() FROM PUBLIC;

  CREATE TRIGGER xhb_cath_document_link_bir_tr
  	BEFORE INSERT ON xhb_cath_document_link FOR EACH ROW
  	EXECUTE PROCEDURE trigger_fct_xhb_cath_document_link_bir_tr();

  /* Update XHB_CATH_DOCUMENT_LINK trigger */
  DROP TRIGGER IF EXISTS xhb_cath_document_link_bur_tr ON pdda.xhb_cath_document_link CASCADE;
  DROP TRIGGER IF EXISTS trigger_fct_xhb_cath_document_link_bur_tr ON pdda.xhb_cath_document_link CASCADE;
  CREATE OR REPLACE FUNCTION pdda.xhb_cath_document_link_bur_tr()
  RETURNS TRIGGER AS $$
  DECLARE
      l_trig_event TEXT := NULL;
  BEGIN
      IF TG_OP = 'UPDATE' THEN
          l_trig_event := 'U';

          -- Detect who is managing the version
          IF XHB_CUSTOM_PKG.IS_CONNECTION_POOL_USER() = 1 THEN
              -- Hibernate increments before update: NEW.VERSION should be OLD.VERSION + 1
              -- Manual apps might pass the OLD.VERSION (expecting DB to increment)

              IF NEW.VERSION = OLD.VERSION THEN
                  -- Manual (non-Hibernate) update
                  NEW.VERSION := OLD.VERSION + 1;
              ELSIF NEW.VERSION = OLD.VERSION + 1 THEN
                  -- Hibernate-managed update - OK
                  NULL;
              ELSE
                  -- Mismatch
                  RAISE EXCEPTION 'optimistic_lock_prob: DB version %, provided version %', OLD.VERSION, NEW.VERSION
                      USING ERRCODE = '50011';
              END IF;
          END IF;

          -- Update LAST_UPDATE_DATE
          NEW.LAST_UPDATE_DATE := LOCALTIMESTAMP;

          -- Set LAST_UPDATED_BY if not connection pool user
          IF XHB_CUSTOM_PKG.IS_CONNECTION_POOL_USER() = 0 THEN
              SELECT COALESCE(current_setting('SESSION_USER', true), 'PDDA')
              INTO NEW.LAST_UPDATED_BY;
          END IF;

      ELSIF TG_OP = 'DELETE' THEN
          l_trig_event := 'D';
      END IF;

      /* Is Auditing on this table required */
      IF (XHB_CUSTOM_PKG.IS_AUDIT_REQUIRED('XHB_CATH_DOCUMENT_LINK') = 1) THEN
          IF NOT EXISTS (
                  SELECT 1 FROM pdda.AUD_CATH_DOCUMENT_LINK
                  WHERE cath_document_link_id = OLD.cath_document_link_id
                  AND last_update_date = OLD.last_update_date
          ) THEN
      	    INSERT INTO pdda.AUD_CATH_DOCUMENT_LINK(
  		CATH_DOCUMENT_LINK_ID,
  		ORIG_COURTEL_LIST_DOC_ID,
  		CATH_XML_ID,
  		CATH_JSON_ID,
  		CREATED_BY,
  		CREATION_DATE,
  		LAST_UPDATED_BY,
   		LAST_UPDATE_DATE,
  		VERSION,
  		INSERT_EVENT)
      	    VALUES (OLD.CATH_DOCUMENT_LINK_ID,
                  OLD.ORIG_COURTEL_LIST_DOC_ID,
                  OLD.CATH_XML_ID,
                  OLD.CATH_JSON_ID,
                  OLD.CREATED_BY,
                  OLD.CREATION_DATE,
                  OLD.LAST_UPDATED_BY,
                  OLD.LAST_UPDATE_DATE,
                  OLD.VERSION,
                  l_trig_event);
  	END IF;
      END IF;

      -- Return correct row depending on operation
      IF TG_OP = 'DELETE' THEN
          RETURN OLD;
      ELSE
          RETURN NEW;
      END IF;

  END;
  $$ LANGUAGE plpgsql;
  -- REVOKE ALL ON FUNCTION xhb_cath_document_link_bur_tr() FROM PUBLIC;

  CREATE TRIGGER xhb_cath_document_link_bur_tr
          BEFORE UPDATE OR DELETE ON pdda.xhb_cath_document_link FOR EACH ROW
          EXECUTE PROCEDURE xhb_cath_document_link_bur_tr();

  /* End of XHB_CATH_DOCUMENT_LINK trigger */
