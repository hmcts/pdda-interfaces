package uk.gov.hmcts.pdda.business.services.pdda;

import jakarta.ejb.ApplicationException;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.framework.scheduler.RemoteTask;
import uk.gov.hmcts.pdda.business.AbstractControllerBean;

/**
 * <p>
 * Title: PDDA Bais Controller Bean.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2022
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @author Mark Harris
 */
@Stateless
@Service
@Transactional
@LocalBean
@ApplicationException(rollback = true)
public class PddaBaisControllerBean extends AbstractControllerBean implements RemoteTask {

    private static final Logger LOG = LoggerFactory.getLogger(PddaBaisControllerBean.class);
    private static final String LOG_CALLED = " called";
    private static final String TWO_PARAMS = "{}{}";

    private PddaHelper pddaHelper;

    public PddaBaisControllerBean(EntityManager entityManager) {
        super(entityManager);
    }

    public PddaBaisControllerBean() {
        super();
    }

    /**
     * <p>
     * Scheduler task wrapper to retrieve Bais events.
     * </p>
     */
    @Override
    public void doTask() {
        retrieveFromBaisCP();
        retrieveFromBaisXhibit();
    }

    /**
     * <p>
     * Retrieve Bais events from CP.
     * </p>
     */
    public void retrieveFromBaisCP() {
        String methodName = "retrieveFromBaisCP()";
        LOG.debug(TWO_PARAMS, methodName, LOG_CALLED);
        getPddaHelper().retrieveFromBaisCp();
    }

    /**
     * <p>
     * Retrieve Bais events from Xhibit.
     * </p>
     */
    public void retrieveFromBaisXhibit() {
        String methodName = "retrieveFromBaisXhibit()";
        LOG.debug(TWO_PARAMS, methodName, LOG_CALLED);
        getPddaHelper().retrieveFromBaisXhibit();
    }

    private PddaHelper getPddaHelper() {
        if (pddaHelper == null) {
            pddaHelper = new PddaHelper(getEntityManager());
        }
        return pddaHelper;
    }
}
