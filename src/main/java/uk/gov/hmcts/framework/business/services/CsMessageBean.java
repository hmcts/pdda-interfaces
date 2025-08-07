package uk.gov.hmcts.framework.business.services;

import jakarta.ejb.CreateException;
import jakarta.ejb.MessageDrivenBean;
import jakarta.ejb.MessageDrivenContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Message driven bean template class. Removes the common code of all message beans into a
 * well-defined framework class.
 * 
 * @author tz0d5m
 * @version $Id: CSMessageBean.java,v 1.4 2006/06/05 12:30:14 bzjrnl Exp $
 */
public class CsMessageBean implements MessageDrivenBean {
    private static final long serialVersionUID = 1L;

    /** The log4j <code>Logger</code> instance. */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private MessageDrivenContext messageDrivenContext;

    /**
     * Required by the EJB specification. Called upon message bean creation.
     * 
     * @throws CreateException Required as per the EJB spec, in case ant overriding classes require
     *         creation exceptions.
     */
    public void ejbCreate() throws CreateException {
        if (log.isDebugEnabled()) {
            log.debug("ejbCreate()");
        }
    }

    /**
     * Accessor method for the <code>MessageDrivenContext</code>.
     * 
     * @return The current <code>MessageDrivenContext</code>.
     */
    public MessageDrivenContext getMessageDrivenContext() {
        return this.messageDrivenContext;
    }

    /**
     * Sets the message context.
     * 
     * @param messageDrivenContext The <code>MessageDrivenContext</code>
     * 
     * @see jakarta.ejb.MessageDrivenBean#setMesssageDrivenContext (jakarta.ejb.MessageDrivenContext)
     */
    @Override
    public void setMessageDrivenContext(MessageDrivenContext messageDrivenContext) {
        if (log.isDebugEnabled()) {
            log.debug("setMessageDrivenContext(..)");
        }

        this.messageDrivenContext = messageDrivenContext;
    }

    /**
     * Required by the EJB specification, this is a stubbed implementation that does nothing but log
     * access.
     * 
     * @see jakarta.ejb.MessageDrivenBean#ejbRemove()
     */
    @Override
    public void ejbRemove() {
        if (log.isDebugEnabled()) {
            log.debug("ejbRemove()");
        }
    }
}
