package uk.gov.hmcts.pdda.business.services.pdda.lighthouse;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundRepository;
import uk.gov.hmcts.pdda.business.entities.xhbpddamessage.XhbPddaMessageRepository;

@SuppressWarnings("PMD.NullAssignment")
public class LighthousePddaControllerBeanHelper {

    protected XhbPddaMessageRepository xhbPddaMessageRepository;
    protected XhbCppStagingInboundRepository xhbCppStagingInboundRepository;
    protected EntityManager entityManager;
    private static final Logger LOG = LoggerFactory.getLogger(LighthousePddaControllerBeanHelper.class);

    private void clearRepositories() {
        LOG.info("clearRepositories()");
        xhbPddaMessageRepository = null;
        xhbCppStagingInboundRepository = null;
    }
    
    public EntityManager getEntityManager() {
        if (!isEntityManagerActive()) {
            LOG.debug("getEntityManager() - Creating new entityManager");
            clearRepositories();
            entityManager = EntityManagerUtil.getEntityManager();
        }
        return entityManager;
    }

    public XhbPddaMessageRepository getXhbPddaMessageRepository() {
        if (xhbPddaMessageRepository == null || !isEntityManagerActive()) {
            xhbPddaMessageRepository = new XhbPddaMessageRepository(getEntityManager());
        }
        return xhbPddaMessageRepository;
    }

    public XhbCppStagingInboundRepository getXhbCppStagingInboundRepository() {
        if (xhbCppStagingInboundRepository == null || !isEntityManagerActive()) {
            xhbCppStagingInboundRepository = new XhbCppStagingInboundRepository(getEntityManager());
        }
        return xhbCppStagingInboundRepository;
    }
    
    private boolean isEntityManagerActive() {
        return EntityManagerUtil.isEntityManagerActive(entityManager);
    }
}
