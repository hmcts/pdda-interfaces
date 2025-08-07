package uk.gov.hmcts.framework.services.audittrail;

/**
 * <p>
 * Title: AuditProvider.
 * </p>
 * <p>
 * Description: This interface defines an AuditProvider which is used to log a message
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
public interface AuditProvider {
    void sendMessage(String message);
}
