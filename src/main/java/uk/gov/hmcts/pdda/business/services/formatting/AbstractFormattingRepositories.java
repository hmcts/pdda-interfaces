package uk.gov.hmcts.pdda.business.services.formatting;

import jakarta.persistence.EntityManager;
import uk.gov.hmcts.framework.services.CsServices;
import uk.gov.hmcts.framework.services.XslServices;
import uk.gov.hmcts.pdda.business.entities.xhbblob.XhbBlobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcathdocumentlink.XhbCathDocumentLinkRepository;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.XhbCourtelListRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcppformatting.XhbCppFormattingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcppformattingmerge.XhbCppFormattingMergeRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcpplist.XhbCppListRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundRepository;
import uk.gov.hmcts.pdda.business.entities.xhbformatting.XhbFormattingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbxmldocument.XhbXmlDocumentRepository;
import uk.gov.hmcts.pdda.business.xmlbinding.formatting.FormattingConfig;

/**
 * AbstractFormattingRepositories.
 */

@SuppressWarnings("PMD.TooManyMethods")
public class AbstractFormattingRepositories {

    private EntityManager entityManager;
    protected XhbClobRepository xhbClobRepository;
    protected XhbBlobRepository xhbBlobRepository;
    protected XhbCppListRepository xhbCppListRepository;
    protected XhbCppFormattingRepository xhbCppFormattingRepository;
    protected XhbCppStagingInboundRepository xhbCppStagingInboundRepository;
    protected XhbFormattingRepository xhbFormattingRepository;
    protected XhbXmlDocumentRepository xhbXmlDocumentRepository;
    protected XhbConfigPropRepository xhbConfigPropRepository;
    protected XhbCppFormattingMergeRepository xhbCppFormattingMergeRepository;
    protected XhbCourtelListRepository xhbCourtelListRepository;
    protected XhbCathDocumentLinkRepository xhbCathDocumentLinkRepository;
    private FormattingConfig formattingConfig;
    private XslServices xslServices;
    
    public AbstractFormattingRepositories(EntityManager entityManager) {
        setEntityManager(entityManager);
    }
    
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }
    
    protected final void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    protected XhbClobRepository getXhbClobRepository() {
        if (xhbClobRepository == null) {
            xhbClobRepository = new XhbClobRepository(getEntityManager());
        }
        return xhbClobRepository;
    }

    protected XhbBlobRepository getXhbBlobRepository() {
        if (xhbBlobRepository == null) {
            xhbBlobRepository = new XhbBlobRepository(getEntityManager());
        }
        return xhbBlobRepository;
    }

    protected XhbCppListRepository getXhbCppListRepository() {
        if (xhbCppListRepository == null) {
            xhbCppListRepository = new XhbCppListRepository(getEntityManager());
        }
        return xhbCppListRepository;
    }

    protected XhbCppFormattingRepository getXhbCppFormattingRepository() {
        if (xhbCppFormattingRepository == null) {
            xhbCppFormattingRepository = new XhbCppFormattingRepository(getEntityManager());
        }
        return xhbCppFormattingRepository;
    }
    
    protected XhbCppStagingInboundRepository getXhbCppStagingInboundRepository() {
        if (xhbCppStagingInboundRepository == null) {
            xhbCppStagingInboundRepository = new XhbCppStagingInboundRepository(getEntityManager());
        }
        return xhbCppStagingInboundRepository;
    }

    protected XhbFormattingRepository getXhbFormattingRepository() {
        if (xhbFormattingRepository == null) {
            xhbFormattingRepository = new XhbFormattingRepository(getEntityManager());
        }
        return xhbFormattingRepository;
    }

    protected XhbXmlDocumentRepository getXhbXmlDocumentRepository() {
        if (xhbXmlDocumentRepository == null) {
            xhbXmlDocumentRepository = new XhbXmlDocumentRepository(getEntityManager());
        }
        return xhbXmlDocumentRepository;
    }

    protected XhbConfigPropRepository getXhbConfigPropRepository() {
        if (xhbConfigPropRepository == null) {
            xhbConfigPropRepository = new XhbConfigPropRepository(getEntityManager());
        }
        return xhbConfigPropRepository;
    }

    protected XhbCppFormattingMergeRepository getXhbCppFormattingMergeRepository() {
        if (xhbCppFormattingMergeRepository == null) {
            xhbCppFormattingMergeRepository = new XhbCppFormattingMergeRepository(getEntityManager());
        }
        return xhbCppFormattingMergeRepository;
    }
    
    protected XhbCourtelListRepository getXhbCourtelListRepository() {
        if (xhbCourtelListRepository == null) {
            xhbCourtelListRepository = new XhbCourtelListRepository(getEntityManager());
        }
        return xhbCourtelListRepository;
    }
    
    protected XhbCathDocumentLinkRepository getXhbCathDocumentLinkRepository() {
        if (xhbCathDocumentLinkRepository == null) {
            xhbCathDocumentLinkRepository = new XhbCathDocumentLinkRepository(getEntityManager());
        }
        return xhbCathDocumentLinkRepository;
    }
    
    protected FormattingConfig getFormattingConfig() {
        if (formattingConfig == null) {
            formattingConfig = FormattingConfig.Singleton.INSTANCE;
        }
        return formattingConfig;
    }

    protected XslServices getXslServices() {
        if (xslServices == null) {
            xslServices = CsServices.getXslServices();
        }
        return xslServices;
    }
}
