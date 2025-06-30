package uk.gov.hmcts.framework.services.audittrail;

/**
 * <p>
 * Title: AuditTrailEventFactory.
 * </p>
 * <p>
 * Description: This is the interface for an AuditTrailEventFactory, which has one method to accept
 * an arbitrary argument and return an appropriate AuditTrailEvent
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
@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface AuditTrailEventFactory {

    /**
     * This method accepts an arbitrary event and is responsible for construcitng and returning an
     * appropriate AuditTrailEvent.
     * 
     * @param argument - Arbitrary argument to create AuditTrailEvent for
     * @return AuditTrailEvent
     */
    AuditTrailEvent getAuditTrailEvent(Object argument);

}

