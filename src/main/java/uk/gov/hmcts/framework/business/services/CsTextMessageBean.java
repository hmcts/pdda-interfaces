package uk.gov.hmcts.framework.business.services;

import jakarta.jms.Message;
import jakarta.jms.TextMessage;

/**
 * Message driven bean template class. Removes the common code of all text message beans into a
 * well-defined framework class.
 * 
 * @author Will Fardell
 * @version $Id: CSTextMessageBean.java,v 1.1 2006/07/11 10:10:14 bzjrnl Exp $
 */
public abstract class CsTextMessageBean extends CsMessageBean {
    private static final long serialVersionUID = 1L;

    /**
     * Called when a message is delivered.
     * 
     * @param message Message
     */
    public void onMessage(Message message) {
        log.debug("CSTextMessageBean::onMessage: {}", message);
        try {
            if (message instanceof TextMessage) {
                onTextMessage((TextMessage) message);
            } else {
                getMessageDrivenContext().setRollbackOnly();
                log.info("Ignoring message of type {}, this bean only handles text messages.",
                    message.getJMSType());
            }
        } catch (Exception e) {
            log.debug("Message is not an instance of TextMessage. onMessage({})", e.getMessage());
            getMessageDrivenContext().setRollbackOnly();
            throw new CsMessageBeanException(e);
        }
    }

    /**
     * Called when a text message is delivered.
     * 
     * @param message the message delivered
     * @throws Exception if an error occures
     */
    protected abstract void onTextMessage(TextMessage message);
}
