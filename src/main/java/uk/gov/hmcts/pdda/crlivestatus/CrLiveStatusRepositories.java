package uk.gov.hmcts.pdda.crlivestatus;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcrlivedisplay.XhbCrLiveDisplayRepository;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingRepository;

/**
 * A class to hold the repositories needed for CR Live Status functionality.

 * @author Luke Gittins
 */

@SuppressWarnings("PMD.NullAssignment")
public class CrLiveStatusRepositories {
    private static final Logger LOG = LoggerFactory.getLogger(CrLiveStatusRepositories.class);
    
    private EntityManager entityManager;
    private XhbScheduledHearingRepository xhbScheduledHearingRepository;
    private XhbCourtRoomRepository xhbCourtRoomRepository;
    private XhbSittingRepository xhbSittingRepository;
    private XhbCrLiveDisplayRepository xhbCrLiveDisplayRepository;
    
    public CrLiveStatusRepositories(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    private boolean isEntityManagerActive() {
        return EntityManagerUtil.isEntityManagerActive(entityManager);
    }
    
    private EntityManager getEntityManager() {
        if (entityManager == null) {
            LOG.debug("getEntityManager() - Creating new entityManager");
            clearRepositories();
            entityManager = EntityManagerUtil.getEntityManager();
        }
        return entityManager;
    }
    
    private void clearRepositories() {
        LOG.info("clearRepositories()");
        xhbScheduledHearingRepository = null;
        xhbCourtRoomRepository = null;
        xhbSittingRepository = null;
        xhbCrLiveDisplayRepository = null;
    }
    
    protected XhbScheduledHearingRepository getXhbScheduledHearingRepository() {
        if (xhbScheduledHearingRepository == null || !isEntityManagerActive()) {
            xhbScheduledHearingRepository = new XhbScheduledHearingRepository(getEntityManager());
        }
        return xhbScheduledHearingRepository;
    }
    
    protected XhbCourtRoomRepository getXhbCourtRoomRepository() {
        if (xhbCourtRoomRepository == null || !isEntityManagerActive()) {
            xhbCourtRoomRepository = new XhbCourtRoomRepository(getEntityManager());
        }
        return xhbCourtRoomRepository;
    }
    
    protected XhbSittingRepository getXhbSittingRepository() {
        if (xhbSittingRepository == null || !isEntityManagerActive()) {
            xhbSittingRepository = new XhbSittingRepository(getEntityManager());
        }
        return xhbSittingRepository;
    }
    
    protected XhbCrLiveDisplayRepository getXhbCrLiveDisplayRepository() {
        if (xhbCrLiveDisplayRepository == null || !isEntityManagerActive()) {
            xhbCrLiveDisplayRepository = new XhbCrLiveDisplayRepository(getEntityManager());
        }
        return xhbCrLiveDisplayRepository;
    }
}
