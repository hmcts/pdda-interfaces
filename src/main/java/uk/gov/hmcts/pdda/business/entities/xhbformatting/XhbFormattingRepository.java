package uk.gov.hmcts.pdda.business.entities.xhbformatting;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.pdda.business.entities.AbstractRepository;

import java.io.Serializable;
import java.util.List;



@Repository
public class XhbFormattingRepository extends AbstractRepository<XhbFormattingDao> implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(XhbFormattingRepository.class);

    public XhbFormattingRepository(EntityManager em) {
        super(em);
    }

    @Override
    public Class<XhbFormattingDao> getDaoClass() {
        return XhbFormattingDao.class;
    }

    /**
     * findByFormatStatus.
     * 
     * @return list
     */
    @SuppressWarnings("unchecked")
    public List<XhbFormattingDao> findByFormatStatus(String formatStatus) {
        LOG.debug("findByFormatStatus({})", formatStatus);
        Query query = getEntityManager().createNamedQuery("XHB_FORMATTING.findByFormatStatus");
        query.setParameter("formatStatus", formatStatus);
        return query.getResultList();
    }

    /**
     * findByDocumentAndClob.
     * 
     * @return List
     */
    @SuppressWarnings("unchecked")
    public List<XhbFormattingDao> findByDocumentAndClob(Integer courtId, String documentType,
        String language, String courtSiteName) {
        LOG.debug("findByDocumentAndClob({},{},{},{})", courtId, documentType, language, courtSiteName);
        Query query = getEntityManager().createNamedQuery("XHB_FORMATTING.findByDocumentAndClob");
        query.setParameter("courtId", courtId);
        query.setParameter("docType", documentType);
        query.setParameter("language", language);
        query.setParameter("courtSiteName", courtSiteName);
        return query.getResultList();
    }
}
