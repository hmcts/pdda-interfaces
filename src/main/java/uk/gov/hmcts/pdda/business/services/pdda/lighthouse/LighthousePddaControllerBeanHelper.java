package uk.gov.hmcts.pdda.business.services.pdda.lighthouse;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundRepository;
import uk.gov.hmcts.pdda.business.entities.xhbpddamessage.XhbPddaMessageRepository;

public class LighthousePddaControllerBeanHelper {

    protected XhbPddaMessageRepository xhbPddaMessageRepository;
    protected XhbCppStagingInboundRepository xhbCppStagingInboundRepository;
    protected EntityManager entityManager;
    private static final Logger LOG = LoggerFactory.getLogger(LighthousePddaControllerBeanHelper.class);

    public EntityManager getEntityManager() {
        if (entityManager == null) {
            LOG.debug("getEntityManager() - Creating new entityManager");
            entityManager = EntityManagerUtil.getEntityManager();
        }
        return entityManager;
    }

    public XhbPddaMessageRepository getXhbPddaMessageRepository() {
        if (xhbPddaMessageRepository == null) {
            xhbPddaMessageRepository = new XhbPddaMessageRepository(getEntityManager());
        }
        return xhbPddaMessageRepository;
    }

    public XhbCppStagingInboundRepository getXhbCppStagingInboundRepository() {
        if (xhbCppStagingInboundRepository == null) {
            xhbCppStagingInboundRepository = new XhbCppStagingInboundRepository(getEntityManager());
        }
        return xhbCppStagingInboundRepository;
    }
}
