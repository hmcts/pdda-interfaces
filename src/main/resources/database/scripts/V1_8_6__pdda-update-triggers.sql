-- Create/update a new xhb_configuredpublicnot_bur_tr trigger and its function

-- Remove the trigger if it already exists
DROP TRIGGER IF EXISTS xhb_configuredpublicnot_bur_tr ON pdda.xhb_configured_public_notice CASCADE;

-- (Optional) remove old function if you prefer a clean create instead of CREATE OR REPLACE
-- DROP FUNCTION IF EXISTS pdda.trigger_fct_xhb_configuredpublicnot_bur_tr() CASCADE;

CREATE OR REPLACE FUNCTION pdda.trigger_fct_xhb_configuredpublicnot_bur_tr()
RETURNS trigger
LANGUAGE plpgsql
AS $$
DECLARE
    l_trig_event varchar(1) := NULL;
BEGIN

    /* Determine whether UPDATING or DELETING */
    IF TG_OP = 'UPDATE' THEN
        l_trig_event := 'U';

        -- Detect who is managing the version
        IF XHB_CUSTOM_PKG.IS_CONNECTION_POOL_USER() = 1 THEN
            IF NEW.VERSION = OLD.VERSION THEN
                -- Manual (non-Hibernate) update
                NEW.VERSION := OLD.VERSION + 1;
            ELSIF NEW.VERSION = OLD.VERSION + 1 THEN
                -- Hibernate-managed update - OK
                NULL;
            ELSE
                RAISE EXCEPTION
                    'optimistic_lock_prob: DB version %, provided version %',
                    OLD.VERSION, NEW.VERSION
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


    /* Is auditing on this table required? */
    IF XHB_CUSTOM_PKG.IS_AUDIT_REQUIRED('XHB_CONFIGURED_PUBLIC_NOTICE') = 1 THEN

        INSERT INTO pdda.AUD_CONFIGURED_PUBLIC_NOTICE
        VALUES (
            OLD.CONFIGURED_PUBLIC_NOTICE_ID,
            OLD.IS_ACTIVE,
            OLD.COURT_ROOM_ID,
            OLD.PUBLIC_NOTICE_ID,
            OLD.LAST_UPDATE_DATE,
            OLD.CREATION_DATE,
            OLD.CREATED_BY,
            OLD.LAST_UPDATED_BY,
            OLD.VERSION,
            l_trig_event
        );

    END IF;


    -- Final return
    IF TG_OP = 'DELETE' THEN
        RETURN OLD;
    ELSE
        RETURN NEW;
    END IF;

END;
$$;

-- Create the trigger (function must exist before this)
CREATE TRIGGER xhb_configuredpublicnot_bur_tr
    BEFORE UPDATE OR DELETE
    ON pdda.xhb_configured_public_notice
    FOR EACH ROW
    EXECUTE FUNCTION pdda.trigger_fct_xhb_configuredpublicnot_bur_tr();

