package uk.gov.hmcts.framework.business.services;

import jakarta.ejb.CreateException;
import jakarta.ejb.SessionBean;
import jakarta.ejb.SessionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Session bean template class. Removes the common code of all session beans into a well-defined
 * framework class.
 * 
 * @author Unknown
 * @version $Id: CSSessionBean.java,v 1.5 2006/06/05 12:30:14 bzjrnl Exp $
 */
public abstract class CsSessionBean implements SessionBean {
    private static final long serialVersionUID = 1L;

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(CsSessionBean.class);

    /** The context. */
    protected SessionContext ctx;

    /**
     * <p>
     * Required by the EJB specification, this is a stubbed implementation that does nothing but log
     * access.
     * </p>
     * 
     * <p>
     * Note that it is actually illegal for an EJB container to invoke this method, but it is
     * required that we implement it. Also, although no reason to override in any subclass, cannot
     * declare as final as this would violate the EJB spec.
     * </p>
     * 
     * @see jakarta.ejb.SessionBean#ejbActivate()
     */
    @Override
    public void ejbActivate() {
        // perhaps throw an IllegalAccessException instead?
        if (LOG.isDebugEnabled()) {
            LOG.debug("ejbActivate()");
        }
    }

    /**
     * <p>
     * Required by the EJB specification, this is a stubbed implementation that does nothing but log
     * access.
     * </p>
     * 
     * <p>
     * Note that it is actually illegal for an EJB container to invoke this method, but it is
     * required that we implement it. Also, although no reason to override in any subclass, cannot
     * declare as final as this would violate the EJB spec.
     * </p>
     * 
     * @see jakarta.ejb.SessionBean#ejbPassivate()
     */
    @Override
    public void ejbPassivate() {
        // perhaps throw an IllegalAccessException instead?
        if (LOG.isDebugEnabled()) {
            LOG.debug("ejbPassivate()");
        }
    }

    /**
     * Required by the EJB specification, this is a stubbed implementation that does nothing but log
     * access.
     * 
     * @see jakarta.ejb.SessionBean#ejbRemove()
     */
    @Override
    public void ejbRemove() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("ejbRemove()");
        }
    }

    /**
     * Sets the session context.
     * 
     * @param ctx SessionContext Context for session
     * @see jakarta.ejb.SessionBean#setSessionContext(jakarta.ejb.SessionContext)
     */
    @Override
    public void setSessionContext(SessionContext ctx) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("setSessionContext(SessionContext ctx)");
        }

        this.ctx = ctx;
    }

    public void ejbCreate() throws CreateException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("ejbCreate()");
        }
    }
}
