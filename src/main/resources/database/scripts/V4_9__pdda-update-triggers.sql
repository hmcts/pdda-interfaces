SET client_encoding TO 'UTF8';

/* Update XHB_FORMATTING trigger */
/* Replace the trigger for xhb_formatting_bur_tr */
DROP TRIGGER IF EXISTS xhb_formatting_bur_tr ON pdda.xhb_formatting CASCADE;
DROP TRIGGER IF EXISTS trigger_fct_xhb_formatting_bur_tr ON pdda.xhb_formatting CASCADE;
CREATE OR REPLACE FUNCTION pdda.xhb_formatting_bur_tr()
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
    IF (XHB_CUSTOM_PKG.IS_AUDIT_REQUIRED('XHB_FORMATTING') = 1) THEN
        IF NOT EXISTS (
                SELECT 1 FROM pdda.AUD_FORMATTING
                WHERE formatting_id = OLD.formatting_id
                AND last_update_date = OLD.last_update_date
        ) THEN
    	    INSERT INTO pdda.AUD_FORMATTING(
              formatting_id,
              date_in,
              format_status,
              distribution_type,
              mime_type,
              document_type,
              last_update_date,
              creation_date,
              created_by,
              last_updated_by,
              version,
              court_id,
        	    formatted_document_blob_id,
        	    xml_document_clob_id,
        	    language,
        	    country,
         	    major_schema_version,
        	    minor_schema_version,
              insert_event)
    	    VALUES (OLD.formatting_id,
                  OLD.date_in,
                  OLD.format_status,
                  OLD.distribution_type,
                  OLD.mime_type,
                  OLD.document_type,
                  OLD.last_update_date,
                  OLD.creation_date,
                  OLD.created_by,
                  OLD.last_updated_by,
                  OLD.version,
                  OLD.court_id,
      	          OLD.formatted_document_blob_id,
      	          OLD.xml_document_clob_id,
      	          OLD.language,
      	          OLD.country,
      	          OLD.major_schema_version,
      	          OLD.minor_schema_version,
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
-- REVOKE ALL ON FUNCTION xhb_formatting_bur_tr() FROM PUBLIC;

CREATE TRIGGER xhb_formatting_bur_tr
        BEFORE UPDATE OR DELETE ON pdda.xhb_formatting FOR EACH ROW
        EXECUTE PROCEDURE xhb_formatting_bur_tr();

/* End of XHB_FORMATTING trigger */
