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
 * Title: Clear Down Controller Bean.
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
 * @author Nathan Toft
 */
@Stateless
@Service
@Transactional
@LocalBean
@ApplicationException(rollback = true)
public class ClearDownControllerBean extends AbstractControllerBean implements RemoteTask {

    private static final Logger LOG = LoggerFactory.getLogger(ClearDownControllerBean.class);
    private static final String LOG_CALLED = " called";

    private String methodName;

    public ClearDownControllerBean(EntityManager entityManager) {
        super(entityManager);
    }

    public ClearDownControllerBean() {
        super();
    }

    /**
     * Implementation of RemoteTask so that this process is called by the timer process. This scheduled
     * job resets the IWP data.
     * 
     */
    @Override
    public void doTask() {
        resetCrLiveDisplay();
        resetCrLiveInternet();
    }

    /**
     * <p>
     * Reset the records in XHB_CR_LIVE_DISPLAY.
     * </p>
     */
    public void resetCrLiveDisplay() {
        methodName = "resetCrLiveDisplay()";
        LOG.debug(methodName + LOG_CALLED);
    }

    /**
     * <p>
     * Reset the records in XHB_CR_LIVE_INTERNET.
     * </p>
     */
    public void resetCrLiveInternet() {
        methodName = "resetCrLiveInternet()";
        LOG.debug(methodName + LOG_CALLED);
    }
}
