package uk.gov.hmcts.pdda.business.services.pdda.lighthouse;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.AbstractControllerBean;
import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundRepository;
import uk.gov.hmcts.pdda.business.entities.xhbpddamessage.XhbPddaMessageRepository;

@SuppressWarnings("PMD.NullAssignment")
public class LighthousePddaControllerBeanHelper extends AbstractControllerBean {

    protected XhbPddaMessageRepository xhbPddaMessageRepository;
    protected XhbCppStagingInboundRepository xhbCppStagingInboundRepository;
    protected EntityManager entityManager;
    private static final Logger LOG = LoggerFactory.getLogger(LighthousePddaControllerBeanHelper.class);

    @Override
    protected void clearRepositories() {
        LOG.info("clearRepositories()");
        xhbPddaMessageRepository = null;
        xhbCppStagingInboundRepository = null;
    }

    public XhbPddaMessageRepository getXhbPddaMessageRepository() {
        if (xhbPddaMessageRepository == null || !isEntityManagerActive()) {
            xhbPddaMessageRepository = new XhbPddaMessageRepository(getEntityManager());
        }
        return xhbPddaMessageRepository;
    }
}
