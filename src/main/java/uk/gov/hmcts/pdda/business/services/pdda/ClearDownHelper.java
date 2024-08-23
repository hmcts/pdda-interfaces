package uk.gov.hmcts.pdda.business.services.pdda;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbcrlivedisplay.XhbCrLiveDisplayDao;
import uk.gov.hmcts.pdda.business.entities.xhbcrlivedisplay.XhbCrLiveDisplayRepository;

import java.util.List;

/**
 * <p>
 * Title: ClearDownHelper.
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
 * @version 1.0
 */
public class ClearDownHelper {

    private static final Logger LOG = LoggerFactory.getLogger(ClearDownHelper.class);
    private static final String LOG_CALLED = " called";

    private final EntityManager entityManager;
    private XhbCrLiveDisplayRepository crLiveDisplayRepository;
    
    public ClearDownHelper(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    /**
     * findLiveDisplaysWhereStatusNotNull.
     */
    public void resetLiveDisplays() {
        String methodName = "resetLiveDisplays()";
        LOG.debug(methodName + LOG_CALLED);
        List<XhbCrLiveDisplayDao> liveDisplays =
            getCrLiveDisplayRepository().findLiveDisplaysWhereStatusNotNull();
        if (!liveDisplays.isEmpty()) {
            LOG.debug("Found {} live displays to be reset", liveDisplays.size());
            for (XhbCrLiveDisplayDao liveDisplay : liveDisplays) {
                LOG.debug("Setting display {} status {} to null", liveDisplay.getCrLiveDisplayId(),
                    liveDisplay.getStatus());
                liveDisplay.setStatus(null);
                getCrLiveDisplayRepository().update(liveDisplay);
            }
        }
    }
    
    private XhbCrLiveDisplayRepository getCrLiveDisplayRepository() {
        if (crLiveDisplayRepository == null) {
            crLiveDisplayRepository = new XhbCrLiveDisplayRepository(entityManager);
        }
        return crLiveDisplayRepository;
    }
    
}
