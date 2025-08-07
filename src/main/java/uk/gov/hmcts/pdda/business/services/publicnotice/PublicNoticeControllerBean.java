package uk.gov.hmcts.pdda.business.services.publicnotice;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.ejb.ApplicationException;
import jakarta.ejb.LocalBean;
import jakarta.ejb.SessionBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.framework.business.services.CsSessionBean;
import uk.gov.hmcts.pdda.business.vos.services.publicnotice.DisplayablePublicNoticeValue;
import uk.gov.hmcts.pdda.courtlog.vos.CourtLogSubscriptionValue;


/**
 * <p>
 * Title: Thin delgation layer to the PublicNoticeWorkFlow.
 * </p>
 * <p>
 * Description: see title
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: Electronic Data Systems
 * </p>
 * 
 * 
 * @author Pat Fox, RLakhani
 * @created 20 February 2003
 */
@Stateless
@Service
@Transactional
@LocalBean
@ApplicationException(rollback = true)
public class PublicNoticeControllerBean extends CsSessionBean implements SessionBean {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(PublicNoticeControllerBean.class);
    private static final String TRANSACTION_ROLLBACK = "Marking the Transaction for RollBack";
    private EntityManager entityManager;

    public PublicNoticeControllerBean(EntityManager entityManager) {
        this();
        this.entityManager = entityManager;
    }

    public PublicNoticeControllerBean() {
        super();
    }

    /**
     * Gets the allPublicNoticesForCourtRoom attribute of the PublicNoticeControllerBean object.
     * 
     * 
     * @param courtRoomID Description of the Parameter
     * @return The allPublicNoticesForCourtRoom value
     */
    public DisplayablePublicNoticeValue[] getAllPublicNoticesForCourtRoom(int courtRoomID) {

        LOG.info("Entering getAllPublicNoticesForCourtRoom()");

        try {
            return PublicNoticeWorkFlow.getAllPublicNoticesForCourtRoom(courtRoomID);
        } catch (PublicNoticeCourtRoomUnknownException ex) {

            LOG.info(TRANSACTION_ROLLBACK);
            this.ctx.setRollbackOnly();
            throw ex;
        }
    }

    /**
     * Sets the allPublicNoticesForCourtRoom attribute of the PublicNoticeControllerBean object.
     * 
     * 
     * @param publicNotices The new allPublicNoticesForCourtRoom value
     * @param courtRoomID The new allPublicNoticesForCourtRoom value
     * @exception PublicNoticeCourtRoomUnknownException Description of the Exception
     * @throws PublicNoticeInvalidSelectionException Exception
     * @exception PublicNoticeInvalidSelectionException Description of the Exception
     */
    public void setAllPublicNoticesForCourtRoom(DisplayablePublicNoticeValue[] publicNotices,
        int courtRoomID) {
        LOG.info("Entered setAllPublicNoticesForCourtRoom");
        try {
            PublicNoticeWorkFlow.setAllPublicNoticesForCourtRoom(publicNotices, courtRoomID);
        } catch (PublicNoticeCourtRoomUnknownException | PublicNoticeInvalidSelectionException ex) {
            LOG.info(TRANSACTION_ROLLBACK);
            this.ctx.setRollbackOnly();
            // rethrow the exception
            throw ex;
        }
        LOG.info("Exit setAllPublicNoticesForCourtRoom");
    }

    /**
     * Sets the publicNoticeforCourtRoom attribute of the PublicNoticeControllerBean object.
     * 
     * 
     * @param courtLogSubscriptionValue The new publicNoticeforCourtRoom value
     * @exception PublicNoticeInvalidSelectionException Description of the Exception
     * @exception PublicNoticeException Description of the Exception
     */
    public void setPublicNoticeforCourtRoom(CourtLogSubscriptionValue courtLogSubscriptionValue) {
        LOG.info("Entered setPublicNoticeforCourtRoom");

        try {
            PublicNoticeWorkFlow.setPublicNoticeforCourtRoom(courtLogSubscriptionValue);
        } catch (PublicNoticeInvalidSelectionException ex) {
            LOG.info(TRANSACTION_ROLLBACK);
            this.ctx.setRollbackOnly();
            // re-throw the exception
            throw ex;
        }

        LOG.info("Exit setPublicNoticeforCourtRoom");
    }

    protected EntityManager getEntityManager() {
        if (entityManager == null) {
            LOG.debug("getEntityManager() - Creating new entityManager");
            entityManager = EntityManagerUtil.getEntityManager();
        }
        return entityManager;
    }
}
