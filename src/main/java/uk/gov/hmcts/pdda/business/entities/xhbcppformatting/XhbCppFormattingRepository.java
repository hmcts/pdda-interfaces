package uk.gov.hmcts.pdda.business.entities.xhbcppformatting;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.pdda.business.entities.AbstractRepository;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;



@Repository
public class XhbCppFormattingRepository extends AbstractRepository<XhbCppFormattingDao> implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(XhbCppFormattingRepository.class);

    private static final String DOCUMENT_TYPE = "documentType";
    private static final String COURT_ID = "courtId";
    private static final String CREATION_DATE = "creationDate";

    public XhbCppFormattingRepository(EntityManager em) {
        super(em);
    }

    @Override
    public Class<XhbCppFormattingDao> getDaoClass() {
        return XhbCppFormattingDao.class;
    }

    /**
     * findLatestByCourtDateInDoc.
     * @param courtCode Integer
     * @param documentType String
     * @param dateIn LocalDateTime
     * @return XhbCppFormattingDao
     */
    public XhbCppFormattingDao findLatestByCourtDateInDoc(final Integer courtCode,
        final String documentType, final LocalDateTime dateIn) {
        String methodName = "findLatestByCourtDateInDoc";
        LOG.debug("{} - findLatestByCourtDateInDoc()", methodName);
        LOG.debug("{} - courtCode: {}", methodName, courtCode);
        LOG.debug("{} - documentType: {}", methodName, documentType);
        LOG.debug("{} - dateIn: {}", methodName, dateIn);
        Query query =
            getEntityManager().createNamedQuery("XHB_CPP_FORMATTING.findLatestByCourtDateInDoc");
        query.setParameter(COURT_ID, courtCode);
        query.setParameter(DOCUMENT_TYPE, documentType);
        query.setParameter("dateIn", dateIn);

        LOG.debug("{} - Query has been created: {}", methodName, query);
        @SuppressWarnings("unchecked")
        List<XhbCppFormattingDao> xcfList = query.getResultList();

        if (xcfList == null  || xcfList.isEmpty()) {
            LOG.debug("{} - Query has been executed, there are no results", methodName);
            return null;
        } else {
            LOG.debug("{} - Query has been executed, there are {} results", methodName,
                xcfList.size());
            return xcfList.get(0);
        }
    }

    /**
     * findAllNewByDocType.
     * @param documentType String
     * @param creationDate LocalDateTime
     * @return List
     */
    @SuppressWarnings("unchecked")
    public List<XhbCppFormattingDao> findAllNewByDocType(String documentType,
        LocalDateTime creationDate) {
        LOG.debug("findAllNewByDocType()");
        Query query = getEntityManager().createNamedQuery("XHB_CPP_FORMATTING.findAllNewByDocType");
        query.setParameter(DOCUMENT_TYPE, documentType);
        query.setParameter(CREATION_DATE, creationDate);
        return query.getResultList();
    }

    /**
     * getLatestDocumentByCourtIdAndType.
     * @param courtId Integer
     * @param documentType String
     * @param creationDate LocalDateTime
     * @return XhbCppFormattingDao
     */
    public XhbCppFormattingDao getLatestDocumentByCourtIdAndType(Integer courtId,
        String documentType, LocalDateTime creationDate) {
        LOG.debug("getLatestDocumentByCourtIdAndType()");
        Query query =
            getEntityManager().createNamedQuery("XHB_CPP_FORMATTING.findByCourtAndDocType");
        query.setParameter(COURT_ID, courtId);
        query.setParameter(DOCUMENT_TYPE, documentType);
        query.setParameter(CREATION_DATE, creationDate);
        @SuppressWarnings("unchecked")
        List<XhbCppFormattingDao> resultList = query.getResultList();
        return resultList.isEmpty() ? null : resultList.get(0);
    }
}
