package uk.gov.hmcts.framework.services.audittrail;

import java.util.Date;

/**
 * <p>
 * Title: AuditTrailEvent.
 * </p>
 * <p>
 * Description: This is the interface for an AuditTRailEvent, from which all events will inherit
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Logica
 * </p>
 * 
 * @author James Powell
 * @version 1.0
 */

public interface AuditTrailEvent {

    String USER_LOGON = "logon";
    String USER_LOGOFF = "logoff";
    String ROLE_CHANGE = "roleChange";
    String PLEA_UPDATED = "pleaUpdated";
    String VERDICT_UPDATED = "verdictUpdated";
    String DISPOSAL_UPDATED = "disposalUpdated";
    String COURT_UPDATED = "courtUpdated";
    String DEFENDANT_UPDATED = "defendantUpdated";
    String SYSTEM_ERROR = "systemError";

    void retrieveLoginData();

    void setEvtType(String evtType);

    String getEvtType();

    void setEvtData(Object evtData);

    Object getEvtData();

    void setSuccess(boolean success);

    boolean isSuccess();

    void setTimestamp(Date timestamp);

    Date getTimestamp();

    void setWorkstationId(String workstationId);

    String getWorkstationId();

    void setUserId(String userId);

    String getUserId();

    void setCourtHouseId(String courtHouseId);

    String getCourtHouseId();

    void setCaseId(Integer caseId);

    Integer getCaseId();

    void setEventSpecificData(String eventSpecificData);

    String getEventSpecificData();

}
