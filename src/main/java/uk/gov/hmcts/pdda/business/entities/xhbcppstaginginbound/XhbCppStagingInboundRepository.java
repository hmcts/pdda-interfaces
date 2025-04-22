package uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.framework.jdbc.core.Parameter;
import uk.gov.hmcts.pdda.business.entities.AbstractRepository;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@SuppressWarnings("unchecked")
@Repository
public class XhbCppStagingInboundRepository extends AbstractRepository<XhbCppStagingInboundDao>
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(XhbCppStagingInboundRepository.class);

    private static final String TIME_LOADED = "timeLoaded";
    private static final String VALIDATION_STATUS = "validationStatus";
    private static final String PROCESSING_STATUS = "processingStatus";

    public XhbCppStagingInboundRepository(EntityManager em) {
        super(em);
    }

    @Override
    public Class<XhbCppStagingInboundDao> getDaoClass() {
        return XhbCppStagingInboundDao.class;
    }

    public List<XhbCppStagingInboundDao> findNextDocumentByValidationAndProcessingStatus(
        LocalDateTime timeLoaded, String validationStatus, String processingStatus) {
        LOG.debug("findNextDocumentByValidationAndProcessingStatus({},{},{})", timeLoaded,
            validationStatus, processingStatus);
        Query query = getEntityManager().createNamedQuery(
            "XHB_CPP_STAGING_INBOUND.findNextDocumentByValidationAndProcessingStatus");
        query.setParameter(TIME_LOADED, timeLoaded);
        query.setParameter(VALIDATION_STATUS, Parameter.getPostgresInParameter(validationStatus));
        query.setParameter(PROCESSING_STATUS, Parameter.getPostgresInParameter(processingStatus));
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<XhbCppStagingInboundDao> findNextDocumentByValidationAndProcessingStatusSafe(
        LocalDateTime timeLoaded, String validationStatus, String processingStatus) {

        LOG.debug("findNextDocumentByValidationAndProcessingStatusSafe({},{},{})", timeLoaded,
            validationStatus, processingStatus);

        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query = em.createNamedQuery(
                "XHB_CPP_STAGING_INBOUND.findNextDocumentByValidationAndProcessingStatus");

            query.setParameter("timeLoaded", timeLoaded);
            query.setParameter("validationStatus",
                Parameter.getPostgresInParameter(validationStatus));
            query.setParameter("processingStatus",
                Parameter.getPostgresInParameter(processingStatus));

            return query.getResultList();
        } catch (Exception e) {
            LOG.error(
                "Error in findNextDocumentByValidationAndProcessingStatusSafe({}, {}, {}): {}",
                timeLoaded, validationStatus, processingStatus, e.getMessage(), e);
            return List.of(); // Safe fallback to prevent nulls or crashes
        }
    }


    public List<XhbCppStagingInboundDao> findNextDocumentByProcessingStatus(
        LocalDateTime timeLoaded, String processingStatus) {
        LOG.debug("findNextDocumentByProcessingStatus({},{})", timeLoaded, processingStatus);
        Query query = getEntityManager()
            .createNamedQuery("XHB_CPP_STAGING_INBOUND.findNextDocumentByProcessingStatus");
        query.setParameter(TIME_LOADED, timeLoaded);
        query.setParameter(PROCESSING_STATUS, Parameter.getPostgresInParameter(processingStatus));
        return query.getResultList();
    }

    public List<XhbCppStagingInboundDao> findNextDocument(String validationStatus,
        String processingStatus) {
        LOG.debug("findNextDocument({},{})", validationStatus, processingStatus);
        Query query =
            getEntityManager().createNamedQuery("XHB_CPP_STAGING_INBOUND.findNextDocument");
        query.setParameter(VALIDATION_STATUS, Parameter.getPostgresInParameter(validationStatus));
        query.setParameter(PROCESSING_STATUS, Parameter.getPostgresInParameter(processingStatus));
        return query.getResultList();
    }

    /**
     * findNextDocumentByValidationStatus.
     * 
     * @param timeLoaded LocalDateTime
     * @param validationStatus String
     * @return List
     */
    public List<XhbCppStagingInboundDao> findNextDocumentByValidationStatus(
        LocalDateTime timeLoaded, String validationStatus) {
        LOG.debug("findNextDocumentByValidationStatus({},{})", timeLoaded, validationStatus);
        Query query = getEntityManager()
            .createNamedQuery("XHB_CPP_STAGING_INBOUND.findNextDocumentByValidationStatus");
        query.setParameter(TIME_LOADED, timeLoaded);
        query.setParameter(VALIDATION_STATUS, Parameter.getPostgresInParameter(validationStatus));
        return query.getResultList();
    }

    public List<XhbCppStagingInboundDao> findNextDocumentTest(LocalDateTime timeLoaded,
        String validationStatus) {
        LOG.debug("findNextDocumentTest({},{})", timeLoaded, validationStatus);
        Query query =
            getEntityManager().createQuery("SELECT o from XHB_CPP_STAGING_INBOUND o WHERE "
                + "o.timeLoaded >= :timeLoaded AND (o.obsInd IS NULL OR o.obsInd='N') "
                + "AND o.validationStatus = :validationStatus ORDER by o.timeLoaded");
        query.setParameter(TIME_LOADED, timeLoaded);
        query.setParameter(VALIDATION_STATUS, Parameter.getPostgresInParameter(validationStatus));
        return query.getResultList();
    }

    public List<XhbCppStagingInboundDao> findUnrespondedCppMessages() {
        LOG.debug("findUnrespondedCppMessages()");
        Query query = getEntityManager()
            .createNamedQuery("XHB_CPP_STAGING_INBOUND.findUnrespondedCPPMessages");
        return query.getResultList();
    }
    
    public List<XhbCppStagingInboundDao> findDocumentByDocumentName(String documentName) {
        LOG.debug("findDocumentByDocumentName({})", documentName);
        Query query =
            getEntityManager().createNamedQuery("XHB_CPP_STAGING_INBOUND.findDocumentByDocumentName");
        query.setParameter("documentName", Parameter.getPostgresInParameter(documentName));
        return query.getResultList();
    }
}
