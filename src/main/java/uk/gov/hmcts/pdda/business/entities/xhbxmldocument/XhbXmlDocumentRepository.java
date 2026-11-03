package uk.gov.hmcts.pdda.business.entities.xhbxmldocument;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.pdda.business.entities.AbstractRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;



@Repository
public class XhbXmlDocumentRepository extends AbstractRepository<XhbXmlDocumentDao> {

    private static final Logger LOG = LoggerFactory.getLogger(XhbXmlDocumentRepository.class);
    private static final String UNCHECKED = "unchecked";
    private static final String XML_DOCUMENT_CLOB_ID = "xmlDocumentClobId";

    public XhbXmlDocumentRepository(EntityManager em) {
        super(em);
    }

    @Override
    public Class<XhbXmlDocumentDao> getDaoClass() {
        return XhbXmlDocumentDao.class;
    }

    /**
     * findDocumentByClobId.

     * @return list
     */
    @SuppressWarnings(UNCHECKED)
    public List<XhbXmlDocumentDao> findDocumentByClobId(Long xmlDocumentClobId,
        LocalDateTime timeDelay) {
        LOG.debug("In XhbXmlDocumentRepository.findDocumentByClobId");
        Query query = getEntityManager().createNamedQuery("XHB_XML_DOCUMENT.findDocumentByClobId");
        query.setParameter(XML_DOCUMENT_CLOB_ID, xmlDocumentClobId);
        query.setParameter("timeDelay", timeDelay);
        return query.getResultList();
    }

    /**
     * findByXmlDocumentClobIdSafe.

     * @return XhbXmlDocumentDao
     */
    public Optional<XhbXmlDocumentDao> findByXmlDocumentClobIdSafe(final Long xmlDocumentClobId) {
        LOG.debug("In XhbXmlDocumentRepository.findByXmlDocumentClobIdSafe");
        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query = em.createNamedQuery("XHB_XML_DOCUMENT.findByXmlDocumentClobId");
            query.setParameter(XML_DOCUMENT_CLOB_ID, xmlDocumentClobId);
            @SuppressWarnings("unchecked")
            List<XhbXmlDocumentDao> resultList = query.getResultList();
            return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.get(0));
        } catch (Exception e) {
            LOG.error("Error in findByXmlDocumentClobIdSafe({}): {}", xmlDocumentClobId,
                e.getMessage(), e);
            return Optional.empty(); // Defensive fallback
        }
    }
    
    /**
     * findJsonDocuments.

     * @return XhbXmlDocumentDao
     */
    @SuppressWarnings(UNCHECKED)
    public List<XhbXmlDocumentDao> findJsonDocumentsSafe(String status) {
        LOG.debug("In XhbXmlDocumentRepository.findJsonDocumentsSafe");
        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query = em.createNamedQuery("XHB_XML_DOCUMENT.findJsonDocuments");
            query.setParameter("status", status);
            return query.getResultList();
        } catch (Exception e) {
            LOG.error("Error in findJsonDocumentsSafe({}): {}", status, e.getMessage(), e);
            return List.of(); // Defensive fallback
        }
    }
    
    @SuppressWarnings(UNCHECKED)
    public List<XhbXmlDocumentDao> findListByClobIdSafe(Long xmlDocumentClobId,
        LocalDateTime timeDelay) {
        // LOG.debug("findListByClobIdSafe({}, {})", xmlDocumentClobId, timeDelay);
        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query = em.createNamedQuery("XHB_XML_DOCUMENT.findListByClobId");
            query.setParameter(XML_DOCUMENT_CLOB_ID, xmlDocumentClobId);
            query.setParameter("timeDelay", timeDelay);
            return query.getResultList();
        } catch (Exception e) {
            LOG.error("Error in findListByClobIdSafe({}, {}): {}", xmlDocumentClobId, timeDelay,
                e.getMessage(), e);
            return List.of(); // Defensive fallback
        }
    }
    
    @SuppressWarnings(UNCHECKED)
    public List<XhbXmlDocumentDao> findByDocTypeCourtIdAndClobIdSafe(
        String documentType, Integer courtId, Long clobId) {
        LOG.debug("findByDocTypeCourtIdAndClobIdSafe({}, {}, {})", documentType, courtId, clobId);
        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query = em.createNamedQuery("XHB_XML_DOCUMENT.findByDocTypeCourtIdAndClobId");
            query.setParameter("documentType", documentType);
            query.setParameter("courtId", courtId);
            query.setParameter(XML_DOCUMENT_CLOB_ID, clobId);
            return query.getResultList();
        } catch (Exception e) {
            LOG.error("Error in findByDocTypeCourtIdAndClobIdSafe({}, {}, {}): {}",
                documentType, courtId, clobId, e.getMessage());
            return List.of(); // Return empty list to avoid nulls and maintain stability
        }
    }

}
