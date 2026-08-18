SET client_encoding TO 'UTF8';

DROP TRIGGER IF EXISTS dm_local_proxy_bur_tr ON pdda.xhb_disp_mgr_local_proxy CASCADE;
CREATE OR REPLACE FUNCTION pdda.trigger_fct_dm_local_proxy_bur_tr()
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
    IF XHB_CUSTOM_PKG.IS_AUDIT_REQUIRED('XHB_DISP_MGR_LOCAL_PROXY') = 1 THEN
        IF NOT EXISTS (
                SELECT 1 FROM pdda.AUD_DISP_MGR_LOCAL_PROXY
                WHERE local_proxy_id = OLD.local_proxy_id
                AND last_update_date = OLD.last_update_date
        ) THEN
            INSERT INTO pdda.AUD_DISP_MGR_LOCAL_PROXY (
                LOCAL_PROXY_ID,
                IP_ADDRESS,
                HOSTNAME,
                RAG_STATUS,
                RAG_STATUS_DATE,
                COURT_SITE_ID,
                LAST_UPDATE_DATE,
                CREATION_DATE,
                CREATED_BY,
                LAST_UPDATED_BY,
                VERSION,
                INSERT_EVENT
            )
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
      END IF;

    -- Return the correct row depending on the operation
    IF TG_OP = 'DELETE' THEN
        RETURN OLD;
    ELSE
        RETURN NEW;
    END IF;

END;
$$ LANGUAGE plpgsql;
-- REVOKE ALL ON FUNCTION trigger_fct_dm_local_proxy_bur_tr() FROM PUBLIC;

CREATE TRIGGER dm_local_proxy_bur_tr
        BEFORE UPDATE OR DELETE ON pdda.xhb_disp_mgr_local_proxy FOR EACH ROW
        EXECUTE PROCEDURE trigger_fct_dm_local_proxy_bur_tr();

/* End of XHB_DISP_MGR_LOCAL_PROXY trigger */
