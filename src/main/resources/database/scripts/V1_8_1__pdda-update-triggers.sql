CREATE OR REPLACE FUNCTION pdda.trigger_fct_xhb_cppstaginginbound_bur_tr()
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
  	IF (XHB_CUSTOM_PKG.IS_AUDIT_REQUIRED('XHB_CPP_STAGING_INBOUND') = 1) THEN
 
    INSERT INTO pdda.AUD_CPP_STAGING_INBOUND
    VALUES (OLD.CPP_STAGING_INBOUND_ID,
            OLD.DOCUMENT_NAME,
            OLD.COURT_CODE,
            OLD.DOCUMENT_TYPE,
            OLD.TIME_LOADED,
            OLD.CLOB_ID,
            OLD.VALIDATION_STATUS,
            OLD.ACKNOWLEDGMENT_STATUS,
            OLD.PROCESSING_STATUS,
            OLD.VALIDATION_ERROR_MESSAGE,
            OLD.OBS_IND,
            OLD.LAST_UPDATE_DATE,
            OLD.CREATION_DATE,
            OLD.LAST_UPDATED_BY,
            OLD.CREATED_BY,
            OLD.VERSION,
            l_trig_event);
 
  	END IF;
 
	-- Return correct row depending on operation
	IF TG_OP = 'DELETE' THEN
		RETURN OLD;
	ELSE
		RETURN NEW;
	END IF;
 
END;
$$ LANGUAGE plpgsql;