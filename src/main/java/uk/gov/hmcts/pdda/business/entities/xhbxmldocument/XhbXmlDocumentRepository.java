package uk.gov.hmcts.pdda.business.entities.xhbxmldocument;

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

    public XhbXmlDocumentRepository(EntityManager em) {
        super(em);
    }

    @Override
    public Class<XhbXmlDocumentDao> getDaoClass() {
        return XhbXmlDocumentDao.class;
    }

    /**
     * findDocumentByClobId.
     * 
     * @return list
     */
    @SuppressWarnings(UNCHECKED)
    public List<XhbXmlDocumentDao> findDocumentByClobId(Long xmlDocumentClobId,
        LocalDateTime timeDelay) {
        LOG.debug("In XhbXmlDocumentRepository.findDocumentByClobId");
        Query query = getEntityManager().createNamedQuery("XHB_XML_DOCUMENT.findDocumentByClobId");
        query.setParameter("xmlDocumentClobId", xmlDocumentClobId);
        query.setParameter("timeDelay", timeDelay);
        return query.getResultList();
    }

    /**
     * findByXmlDocumentClobId.
     * 
     * @return XhbXmlDocumentDao
     */
    public Optional<XhbXmlDocumentDao> findByXmlDocumentClobId(final Long xmlDocumentClobId) {
        LOG.debug("In XhbXmlDocumentRepository.findByXmlDocumentClobId");
        Query query = getEntityManager().createNamedQuery("XHB_XML_DOCUMENT.findByXmlDocumentClobId");
        query.setParameter("xmlDocumentClobId", xmlDocumentClobId);
        @SuppressWarnings("unchecked")
        List<XhbXmlDocumentDao> resultList = query.getResultList();
        return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.get(0));
    }
    
    /**
     * findJsonDocuments.
     * 
     * @return XhbXmlDocumentDao
     */
    @SuppressWarnings(UNCHECKED)
    public List<XhbXmlDocumentDao> findJsonDocuments() {
        LOG.debug("In XhbXmlDocumentRepository.findJsonDocuments");
        Query query = getEntityManager().createNamedQuery("XHB_XML_DOCUMENT.findJsonDocuments");
        return query.getResultList();
    }
    
    /**
     * findJsonDocumentsF1.
     * 
     * @return XhbXmlDocumentDao
     */
    @SuppressWarnings(UNCHECKED)
    public List<XhbXmlDocumentDao> findJsonDocumentsF1() {
        LOG.debug("In XhbXmlDocumentRepository.findJsonDocumentsF1");
        Query query = getEntityManager().createNamedQuery("XHB_XML_DOCUMENT.findJsonDocumentsF1");
        return query.getResultList();
    }
    
    /**
     * findJsonDocumentsF2.
     * 
     * @return XhbXmlDocumentDao
     */
    @SuppressWarnings(UNCHECKED)
    public List<XhbXmlDocumentDao> findJsonDocumentsF2() {
        LOG.debug("In XhbXmlDocumentRepository.findJsonDocumentsF2");
        Query query = getEntityManager().createNamedQuery("XHB_XML_DOCUMENT.findJsonDocumentsF2");
        return query.getResultList();
    }
}
