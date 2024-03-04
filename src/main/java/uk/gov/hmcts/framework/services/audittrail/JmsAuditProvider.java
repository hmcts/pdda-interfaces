package uk.gov.hmcts.framework.services.audittrail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.framework.exception.CsConfigurationException;
import uk.gov.hmcts.framework.services.CsServices;


/**
 * <p>
 * Title: JMSAuditProvider.
 * </p>
 * <p>
 * Description: This AuditProvider sends the message to a JMS queue for asynchronous processing
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
public class JmsAuditProvider implements AuditProvider {

    public static final Logger LOG = LoggerFactory.getLogger(JmsAuditProvider.class);

    private static final String FACTORY_CLASS =
        "uk.gov.hmcts.pdda.business.audittrail.AuditTrailMessageFactoryImpl";

    /**
     * Send the message to a JMS queue.
     */
    @Override
    public void sendMessage(String message) {
        LOG.debug("Sending message to xhibit/jms/AuditTrailQueue");
        try {
            /*
             * Need to lookup the class at runtime to avoid circulare reference issues at compile
             * time
             */
            AuditTrailMessageFactory auditTrailMessageFactory = (AuditTrailMessageFactory) Class.forName(FACTORY_CLASS)
                .getDeclaredConstructor(String.class, String.class)
                .newInstance("xhibit/jms/AuditTrailQueue", message);
            CsServices.getJmsServices().send(auditTrailMessageFactory);
            LOG.debug("finished Sending message to xhibit/jms/AuditTrailQueue");
        } catch (Exception ex) {
            LOG.debug("sendMessage({})", ex.getMessage());
            CsConfigurationException exception = new CsConfigurationException(ex);
            CsServices.getDefaultErrorHandler().handleError(exception, CsServices.class);
            throw exception;
        }
    }
}
