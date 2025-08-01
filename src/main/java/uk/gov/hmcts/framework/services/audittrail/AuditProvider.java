package uk.gov.hmcts.framework.services.audittrail;

/**

 * Title: AuditProvider.


 * Description: This interface defines an AuditProvider which is used to log a message


 * Copyright: Copyright (c) 2008


 * Company: Logica

 * @author James Powell
 * @version 1.0
 */
@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface AuditProvider {
    void sendMessage(String message);
}
