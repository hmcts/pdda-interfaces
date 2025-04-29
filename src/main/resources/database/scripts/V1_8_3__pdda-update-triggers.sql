SET client_encoding TO 'UTF8';

/* Update XHB_CPP_STAGING_INBOUND trigger */
/* Replace the trigger for pdda.trigger_fct_xhb_cppstaginginbound_bur_tr */
DROP TRIGGER IF EXISTS pdda.trigger_fct_xhb_cppstaginginbound_bur_tr ON xhb_cpp_staging_inbound CASCADE;
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
-- REVOKE ALL ON FUNCTION trigger_fct_xhb_cppstaginginbound_bur_tr() FROM PUBLIC;

CREATE TRIGGER trigger_fct_xhb_cppstaginginbound_bur_tr
        BEFORE UPDATE OR DELETE ON xhb_cpp_staging_inbound FOR EACH ROW
        EXECUTE PROCEDURE trigger_fct_xhb_cppstaginginbound_bur_tr();

/* End of XHB_CPP_STAGING_INBOUND trigger */



/* Update XHB_PDDA_MESSAGE trigger */
/* Replace the trigger for pdda.trigger_fct_xhb_pdda_message_bur_tr */
DROP TRIGGER IF EXISTS pdda.trigger_fct_xhb_pdda_message_bur_tr ON xhb_pdda_message CASCADE;
CREATE OR REPLACE FUNCTION pdda.trigger_fct_xhb_pdda_message_bur_tr()
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
    IF (XHB_CUSTOM_PKG.IS_AUDIT_REQUIRED('XHB_PDDA_MESSAGE') = 1) THEN
    INSERT INTO pdda.AUD_PDDA_MESSAGE
    	VALUES (
	  OLD.PDDA_MESSAGE_ID,
          OLD.COURT_ID,
          OLD.COURT_ROOM_ID,
          OLD.PDDA_MESSAGE_GUID,
          OLD.PDDA_MESSAGE_TYPE_ID,
          OLD.PDDA_MESSAGE_DATA_ID,
          OLD.PDDA_BATCH_ID,
          OLD.TIME_SENT,
          OLD.CP_DOCUMENT_NAME,
          OLD.CP_DOCUMENT_STATUS,
          OLD.CP_RESPONSE_GENERATED,
          OLD.CPP_STAGING_INBOUND_ID,
          OLD.ERROR_MESSAGE,
          OLD.OBS_IND,
          OLD.LAST_UPDATE_DATE,
          OLD.CREATION_DATE,
          OLD.CREATED_BY,
          OLD.LAST_UPDATED_BY,
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
-- REVOKE ALL ON FUNCTION trigger_fct_xhb_pdda_message_bur_tr() FROM PUBLIC;

CREATE TRIGGER trigger_fct_xhb_pdda_message_bur_tr
        BEFORE UPDATE OR DELETE ON xhb_pdda_message FOR EACH ROW
        EXECUTE PROCEDURE trigger_fct_xhb_pdda_message_bur_tr();

/* End of XHB_PDDA_MESSAGE trigger */



/* Update XHB_DISPLAY_STORE trigger */
/* Replace the trigger for pdda.trigger_fct_xhb_display_store_bur_tr */
DROP TRIGGER IF EXISTS pdda.trigger_fct_xhb_display_store_bur_tr ON xhb_display_store CASCADE;
CREATE OR REPLACE FUNCTION pdda.trigger_fct_xhb_display_store_bur_tr()
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
    IF (XHB_CUSTOM_PKG.IS_AUDIT_REQUIRED('XHB_DISPLAY_STORE') = 1) THEN
    INSERT INTO pdda.AUD_DISPLAY_STORE
    VALUES (OLD.DISPLAY_STORE_ID,
          OLD.RETRIEVAL_CODE,
          OLD.CONTENT,
          OLD.OBS_IND,
          OLD.LAST_UPDATE_DATE,
          OLD.CREATION_DATE,
          OLD.CREATED_BY,
          OLD.LAST_UPDATED_BY,
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
-- REVOKE ALL ON FUNCTION trigger_fct_xhb_display_store_bur_tr() FROM PUBLIC;

CREATE TRIGGER trigger_fct_xhb_display_store_bur_tr
        BEFORE UPDATE OR DELETE ON xhb_display_store FOR EACH ROW
        EXECUTE PROCEDURE trigger_fct_xhb_display_store_bur_tr();

/* End of XHB_DISPLAY_STORE trigger */




/* Update XHB_CASE trigger */
/* Replace the trigger for pdda.trigger_fct_xhb_case_bur_tr */
DROP TRIGGER IF EXISTS pdda.trigger_fct_xhb_case_bur_tr ON xhb_case CASCADE;
CREATE OR REPLACE FUNCTION pdda.trigger_fct_xhb_case_bur_tr()
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
    IF (XHB_CUSTOM_PKG.IS_AUDIT_REQUIRED('XHB_CASE') = 1) THEN
    INSERT INTO pdda.AUD_CASE(
	  CASE_ID,
	  CASE_NUMBER,
	  CASE_TYPE,
	  MAG_CONVICTION_DATE,
	  CASE_SUB_TYPE,
	  CASE_TITLE,
	  CASE_DESCRIPTION,
	  LINKED_CASE_ID,
	  BAIL_MAG_CODE,
	  REF_COURT_ID,
	  COURT_ID,
	  CHARGE_IMPORT_INDICATOR,
	  SEVERED_IND,
	  INDICT_RESP,
	  DATE_IND_REC,
	  PROS_AGENCY_REFERENCE,
	  LAST_UPDATE_DATE,
	  CREATION_DATE,
	  CREATED_BY,
	  LAST_UPDATED_BY,
	  VERSION,
	  CASE_CLASS,
	  JUDGE_REASON_FOR_APPEAL,
	  RESULTS_VERIFIED,
	  LENGTH_TAPE,
	  NO_PAGE_PROS_EVIDENCE,
	  NO_PROS_WITNESS,
	  EST_PDH_TRIAL_LENGTH,
	  INDICTMENT_INFO_1,
	  INDICTMENT_INFO_2,
	  INDICTMENT_INFO_3,
	  INDICTMENT_INFO_4,
	  INDICTMENT_INFO_5,
	  INDICTMENT_INFO_6,
	  POLICE_OFFICER_ATTENDING,
	  CPS_CASE_WORKER,
	  EXPORT_CHARGES,
	  IND_CHANGE_STATUS,
	  MAGISTRATES_CASE_REF,
	  CLASS_CODE,
	  OFFENCE_GROUP_UPDATE,
	  CCC_TRANS_TO_REF_COURT_ID,
	  RECEIPT_TYPE,
	  INSERT_EVENT,
	  CCC_TRANS_FROM_REF_COURT_ID,
	  DATE_TRANS_FROM,
	  RETRIAL,
	  ORIGINAL_CASE_NUMBER,
	  LC_SENT_DATE,
	  NO_CB_PROS_WITNESS,
	  NO_OTHER_PROS_WITNESS,
	  VULNERABLE_VICTIM_INDICATOR,
	  PUBLIC_DISPLAY_HIDE,
	  TRANSFERRED_CASE,
	  TRANSFER_DEFERRED_SENTENCE,
	  MONITORING_CATEGORY_ID,
          APPEAL_LODGED_DATE,
  	  RECEIVED_DATE,
  	  EITHER_WAY_TYPE,
  	  TICKET_REQUIRED,
  	  TICKET_TYPE_CODE,
  	  COURT_ID_RECEIVING_SITE,
  	  COMMITTAL_DATE,
  	  SENT_FOR_TRIAL_DATE,
  	  NO_DEFENDANTS_FOR_CASE,
  	  SECURE_COURT,
  	  PRELIMINARY_DATE_OF_HEARING,
  	  ORIGINAL_JPS_1,
  	  ORIGINAL_JPS_2,
  	  ORIGINAL_JPS_3,
  	  ORIGINAL_JPS_4,
  	  POLICE_FORCE_CODE,
 	  MAGCOURT_HEARINGTYPE_REF_ID,
	  CASE_LISTED,
	  ORIG_BODY_DECISION_DATE,
          CASE_STATUS,
          VIDEO_LINK_REQUIRED,
          CRACKED_INEFFECTIVE_ID,
          DEFAULT_HEARING_TYPE,
          SECTION28_NAME1,
          SECTION28_NAME2,
          SECTION28_PHONE1,
          SECTION28_PHONE2,
          DATE_TRANS_TO,
          CASE_GROUP_NUMBER,
          PUB_RUNNING_LIST_ID,
	  DATE_CTL_REMINDER_PRINTED,
          DATE_TRANS_RECORDED_TO,
          S28_ELIGIBLE,
          S28_ORDER_MADE,
	  TELEVISED_APPLICATION_MADE,
	  TELEVISED_APP_MADE_DATE,
	  TELEVISED_APP_GRANTED,
	  TELEVISED_APP_REFUSED_FREETEXT,
	  TELEVISED_REMARKS_FILMED,
	  DAR_RETENTION_POLICY_ID,
	  CRP_LAST_UPDATE_DATE,
	  CIVIL_UNREST
    ) VALUES (
        OLD.CASE_ID,
        OLD.CASE_NUMBER,
        OLD.CASE_TYPE,
        OLD.MAG_CONVICTION_DATE,
        OLD.CASE_SUB_TYPE,
        OLD.CASE_TITLE,
        OLD.CASE_DESCRIPTION,
        OLD.LINKED_CASE_ID,
        OLD.BAIL_MAG_CODE,
        OLD.REF_COURT_ID,
        OLD.COURT_ID,
        OLD.CHARGE_IMPORT_INDICATOR,
        OLD.SEVERED_IND,
        OLD.INDICT_RESP,
        OLD.DATE_IND_REC,
        OLD.PROS_AGENCY_REFERENCE,
        OLD.LAST_UPDATE_DATE,
        OLD.CREATION_DATE,
        OLD.CREATED_BY,
        OLD.LAST_UPDATED_BY,
        OLD.VERSION,
        OLD.CASE_CLASS,
        OLD.JUDGE_REASON_FOR_APPEAL,
        OLD.RESULTS_VERIFIED,
        OLD.LENGTH_TAPE,
        OLD.NO_PAGE_PROS_EVIDENCE,
        OLD.NO_PROS_WITNESS,
        OLD.EST_PDH_TRIAL_LENGTH,
        OLD.indictment_info_1,
        OLD.indictment_info_2,
        OLD.indictment_info_3,
        OLD.indictment_info_4,
        OLD.indictment_info_5,
        OLD.indictment_info_6,
        OLD.POLICE_OFFICER_ATTENDING,
        OLD.CPS_CASE_WORKER,
        OLD.EXPORT_CHARGES,
        OLD.IND_CHANGE_STATUS,
        OLD.MAGISTRATES_CASE_REF,
        OLD.CLASS_CODE,
        OLD.OFFENCE_GROUP_UPDATE,
        OLD.CCC_TRANS_TO_REF_COURT_ID,
        OLD.RECEIPT_TYPE,
        l_trig_event,
        OLD.ccc_trans_from_ref_court_id,
        OLD.date_trans_from,
        OLD.retrial,
        OLD.original_case_number,
        OLD.lc_sent_date,
        OLD.no_cb_pros_witness,
        OLD.no_other_pros_witness,
        OLD.Vulnerable_victim_indicator,
        OLD.public_display_hide,
        OLD.TRANSFERRED_CASE,
        OLD.TRANSFER_DEFERRED_SENTENCE,
        OLD.MONITORING_CATEGORY_ID,
        OLD.APPEAL_LODGED_DATE,
        OLD.RECEIVED_DATE,
        OLD.EITHER_WAY_TYPE,
        OLD.TICKET_REQUIRED,
        OLD.TICKET_TYPE_CODE,
        OLD.COURT_ID_RECEIVING_SITE,
        OLD.COMMITTAL_DATE,
        OLD.SENT_FOR_TRIAL_DATE,
        OLD.NO_DEFENDANTS_FOR_CASE,
        OLD.SECURE_COURT,
        OLD.PRELIMINARY_DATE_OF_HEARING,
        OLD.ORIGINAL_JPS_1,
        OLD.ORIGINAL_JPS_2,
        OLD.ORIGINAL_JPS_3,
        OLD.ORIGINAL_JPS_4,
        OLD.POLICE_FORCE_CODE,
        OLD.MAGCOURT_HEARINGTYPE_REF_ID,
        OLD.CASE_LISTED,
        OLD.ORIG_BODY_DECISION_DATE,
        OLD.CASE_STATUS,
        OLD.VIDEO_LINK_REQUIRED,
        OLD.CRACKED_INEFFECTIVE_ID,
        OLD.DEFAULT_HEARING_TYPE,
        OLD.SECTION28_NAME1,
        OLD.SECTION28_NAME2,
        OLD.SECTION28_PHONE1,
        OLD.SECTION28_PHONE2,
        OLD.DATE_TRANS_TO,
        OLD.CASE_GROUP_NUMBER,
        OLD.PUB_RUNNING_LIST_ID,
        OLD.DATE_CTL_REMINDER_PRINTED,
        OLD.DATE_TRANS_RECORDED_TO,
        OLD.S28_ELIGIBLE,
        OLD.S28_ORDER_MADE,
        OLD.TELEVISED_APPLICATION_MADE,
        OLD.TELEVISED_APP_MADE_DATE,
        OLD.TELEVISED_APP_GRANTED,
        OLD.TELEVISED_APP_REFUSED_FREETEXT,
        OLD.TELEVISED_REMARKS_FILMED,
        OLD.DAR_RETENTION_POLICY_ID,
        OLD.CRP_LAST_UPDATE_DATE,
	OLD.CIVIL_UNREST);

    END IF;

    -- Return correct row depending on operation
    IF TG_OP = 'DELETE' THEN
        RETURN OLD;
    ELSE
        RETURN NEW;
    END IF;

END;
$$ LANGUAGE plpgsql;
-- REVOKE ALL ON FUNCTION trigger_fct_xhb_case_bur_tr() FROM PUBLIC;

CREATE TRIGGER trigger_fct_xhb_case_bur_tr
        BEFORE UPDATE OR DELETE ON xhb_case FOR EACH ROW
        EXECUTE PROCEDURE trigger_fct_xhb_case_bur_tr();

/* End of XHB_CASE trigger */




/* Update XHB_CR_LIVE_DISPLAY trigger */
/* Replace the trigger for pdda.trigger_fct_xhb_cr_live_display_bur_tr */
DROP TRIGGER IF EXISTS pdda.trigger_fct_xhb_cr_live_display_bur_tr ON xhb_cr_live_display CASCADE;
CREATE OR REPLACE FUNCTION pdda.trigger_fct_xhb_cr_live_display_bur_tr()
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
    IF (XHB_CUSTOM_PKG.IS_AUDIT_REQUIRED('XHB_CR_LIVE_DISPLAY') = 1) THEN
    INSERT INTO pdda.AUD_CR_LIVE_DISPLAY(
	CR_LIVE_DISPLAY_ID,
	COURT_ROOM_ID,
	SCHEDULED_HEARING_ID,
	TIME_STATUS_SET,
	STATUS,
	CREATED_BY,
	CREATION_DATE,
	LAST_UPDATED_BY,
 	LAST_UPDATE_DATE,
	VERSION,
	INSERT_EVENT)
    VALUES (OLD.CR_LIVE_DISPLAY_ID,
            OLD.COURT_ROOM_ID,
            OLD.SCHEDULED_HEARING_ID,
            OLD.TIME_STATUS_SET,
            OLD.STATUS,
            OLD.CREATED_BY,
            OLD.CREATION_DATE,
            OLD.LAST_UPDATED_BY,
            OLD.LAST_UPDATE_DATE,
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
-- REVOKE ALL ON FUNCTION trigger_fct_xhb_cr_live_display_bur_tr() FROM PUBLIC;

CREATE TRIGGER trigger_fct_xhb_cr_live_display_bur_tr
        BEFORE UPDATE OR DELETE ON xhb_cr_live_display FOR EACH ROW
        EXECUTE PROCEDURE trigger_fct_xhb_cr_live_display_bur_tr();

/* End of XHB_CR_LIVE_DISPLAY trigger */

