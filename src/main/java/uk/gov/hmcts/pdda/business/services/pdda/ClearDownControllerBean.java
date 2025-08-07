package uk.gov.hmcts.pdda.business.services.pdda;

import com.pdda.hb.jpa.EntityManagerUtil;
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
 * Copyright: Copyright (c) 2024
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

    private ClearDownHelper clearDownHelper;

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
        LOG.debug("ClearDown -- doTask() - entered");
        if (getClearDownHelper().isClearDownRequired()) {
            resetLiveDisplays();
        }
        LOG.debug("ClearDown -- doTask() - exited");
    }

    /**
     * <p>
     * Reset the records in XHB_CR_LIVE_DISPLAY.
     * </p>
     */
    public void resetLiveDisplays() {
        LOG.debug("Calling resetLiveDisplays()");
        getClearDownHelper().resetLiveDisplays();
        
    }
    
    /**
     * Returns a reference to the pddaDlNotifierHelper object.
     * 
     * @return pddaDlNotifierHelper
     */
    private ClearDownHelper getClearDownHelper() {
        if (clearDownHelper == null) {
            clearDownHelper = new ClearDownHelper(EntityManagerUtil.getEntityManager());
        }
        return clearDownHelper;
    }
}
