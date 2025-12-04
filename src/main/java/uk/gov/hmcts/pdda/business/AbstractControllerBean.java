package uk.gov.hmcts.pdda.business;

import com.pdda.hb.jpa.EntityManagerUtil;
import com.pdda.hb.jpa.RepositoryUtil;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbblob.XhbBlobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.XhbCourtelListRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcppformatting.XhbCppFormattingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcpplist.XhbCppListRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundRepository;
import uk.gov.hmcts.pdda.business.entities.xhbformatting.XhbFormattingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbinternethtml.XhbInternetHtmlRepository;
import uk.gov.hmcts.pdda.business.entities.xhbxmldocument.XhbXmlDocumentRepository;

@SuppressWarnings("PMD")
public class AbstractControllerBean {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractControllerBean.class);

    private EntityManager entityManager;
    private XhbClobRepository xhbClobRepository;
    private XhbBlobRepository xhbBlobRepository;
    private XhbCourtRepository xhbCourtRepository;
    private XhbConfigPropRepository xhbConfigPropRepository;
    private XhbCppFormattingRepository xhbCppFormattingRepository;
    private XhbCppListRepository xhbCppListRepository;
    private XhbFormattingRepository xhbFormattingRepository;
    private XhbCourtelListRepository xhbCourtelListRepository;
    private XhbXmlDocumentRepository xhbXmlDocumentRepository;
    private XhbCppStagingInboundRepository xhbCppStagingInboundRepository;
    private XhbInternetHtmlRepository xhbInternetHtmlRepository;

    // For unit tests.
    protected AbstractControllerBean(EntityManager entityManager,
        XhbClobRepository xhbClobRepository, XhbBlobRepository xhbBlobRepository,
        XhbCourtRepository xhbCourtRepository, XhbConfigPropRepository xhbConfigPropRepository,
        XhbCppFormattingRepository xhbCppFormattingRepository) {
        this(entityManager);
        this.xhbClobRepository = xhbClobRepository;
        this.xhbBlobRepository = xhbBlobRepository;
        this.xhbCourtRepository = xhbCourtRepository;
        this.xhbConfigPropRepository = xhbConfigPropRepository;
        this.xhbCppFormattingRepository = xhbCppFormattingRepository;
    }

    protected void clearRepositories() {
        LOG.info("clearRepositories()");
        xhbClobRepository = null;
        xhbBlobRepository = null;
        xhbCourtRepository = null;
        xhbConfigPropRepository = null;
        xhbCppFormattingRepository = null;
        xhbCppStagingInboundRepository = null;
        xhbInternetHtmlRepository = null;
    }

    protected AbstractControllerBean(EntityManager entityManager) {
        this();
        this.entityManager = entityManager;
    }

    protected AbstractControllerBean() {
        // protected constructor
    }

    protected EntityManager getEntityManager() {
        if (!EntityManagerUtil.isEntityManagerActive(entityManager)) {
            clearRepositories();
            LOG.debug("getEntityManager() - Creating new entityManager");
            entityManager = EntityManagerUtil.getEntityManager();
        }
        return entityManager;
    }

    protected XhbClobRepository getXhbClobRepository() {
        if (!RepositoryUtil.isRepositoryActive(xhbClobRepository)) {
            xhbClobRepository = new XhbClobRepository(getEntityManager());
        }
        return xhbClobRepository;
    }

    protected XhbBlobRepository getXhbBlobRepository() {
        if (!RepositoryUtil.isRepositoryActive(xhbBlobRepository)) {
            xhbBlobRepository = new XhbBlobRepository(getEntityManager());
        }
        return xhbBlobRepository;
    }

    protected XhbCourtelListRepository getXhbCourtelListRepository() {
        if (!RepositoryUtil.isRepositoryActive(xhbCourtelListRepository)) {
            xhbCourtelListRepository = new XhbCourtelListRepository(getEntityManager());
        }
        return xhbCourtelListRepository;
    }

    protected XhbXmlDocumentRepository getXhbXmlDocumentRepository() {
        if (!RepositoryUtil.isRepositoryActive(xhbXmlDocumentRepository)) {
            xhbXmlDocumentRepository = new XhbXmlDocumentRepository(getEntityManager());
        }
        return xhbXmlDocumentRepository;
    }

    protected XhbCourtRepository getXhbCourtRepository() {

        if ((xhbCourtRepository == null || !isEntityManagerActive()) && !isTransactionActive()) {
            xhbCourtRepository = new XhbCourtRepository(getEntityManager());
        }
        return xhbCourtRepository;
    }

    protected XhbConfigPropRepository getXhbConfigPropRepository() {
        if (xhbConfigPropRepository == null || !isEntityManagerActive()) {
            xhbConfigPropRepository = new XhbConfigPropRepository(getEntityManager());
        }
        return xhbConfigPropRepository;
    }

    protected XhbCppFormattingRepository getXhbCppFormattingRepository() {
        if (!RepositoryUtil.isRepositoryActive(xhbCppFormattingRepository)) {
            xhbCppFormattingRepository = new XhbCppFormattingRepository(getEntityManager());
        }
        return xhbCppFormattingRepository;
    }

    /**
     * Retrieves a reference to the xhbCppListRepository.
     * @return XhbCppListRepository
     */
    protected XhbCppListRepository getXhbCppListRepository() {
        if (!RepositoryUtil.isRepositoryActive(xhbCppListRepository)) {
            xhbCppListRepository = new XhbCppListRepository(getEntityManager());
        }
        return xhbCppListRepository;
    }

    /**
     * Retrieves a reference to the xhbFormattingRepository.
     * @return XhbFormattingRepository
     */
    protected XhbFormattingRepository getXhbFormattingRepository() {
        if (!RepositoryUtil.isRepositoryActive(xhbFormattingRepository)) {
            xhbFormattingRepository = new XhbFormattingRepository(getEntityManager());
        }
        return xhbFormattingRepository;
    }

    /**
     * Retrieves a reference to the xhbCppStagingInboundRepository.
     * @return XhbCppStagingInboundRepository
     */
    public XhbCppStagingInboundRepository getXhbCppStagingInboundRepository() {
        if (xhbCppStagingInboundRepository == null || !isEntityManagerActive()) {
            xhbCppStagingInboundRepository = new XhbCppStagingInboundRepository(getEntityManager());
        }
        return xhbCppStagingInboundRepository;
    }


    public XhbInternetHtmlRepository getXhbInternetHtmlRepository() {
        if (xhbInternetHtmlRepository == null || !isEntityManagerActive()) {
            xhbInternetHtmlRepository = new XhbInternetHtmlRepository(getEntityManager());
        }
        return xhbInternetHtmlRepository;
    }

    protected boolean isEntityManagerActive() {
        return EntityManagerUtil.isEntityManagerActive(entityManager);
    }

    protected boolean isTransactionActive() {
        return EntityManagerUtil.isTransactionActive(entityManager);
    }
}
