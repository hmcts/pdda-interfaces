package uk.gov.hmcts.pdda.business.entities.xhbcppformatting;

import com.pdda.hb.jpa.EntityManagerUtil;
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
@SuppressWarnings("unchecked")
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

    @SuppressWarnings("unchecked")
    public XhbCppFormattingDao findLatestByCourtDateInDocSafe(final Integer courtCode,
        final String documentType, final LocalDateTime dateIn) {

        String methodName = "findLatestByCourtDateInDocSafe";
        LOG.debug("{} - courtCode: {}", methodName, courtCode);
        LOG.debug("{} - documentType: {}", methodName, documentType);
        LOG.debug("{} - dateIn: {}", methodName, dateIn);

        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query = em.createNamedQuery("XHB_CPP_FORMATTING.findLatestByCourtDateInDoc");
            query.setParameter(COURT_ID, courtCode);
            query.setParameter(DOCUMENT_TYPE, documentType);
            query.setParameter("dateIn", dateIn);

            LOG.debug("{} - Query has been created", methodName);
            List<?> resultList = query.getResultList();

            if (resultList == null || resultList.isEmpty()) {
                LOG.debug("{} - No results found", methodName);
                return null;
            }

            Object result = resultList.get(0);
            if (result instanceof XhbCppFormattingDao) {
                LOG.debug("{} - Returning first result", methodName);
                return (XhbCppFormattingDao) result;
            } else {
                LOG.warn("{} - Unexpected result type: {}", methodName,
                    result.getClass().getName());
                return null;
            }

        } catch (Exception e) {
            LOG.error("{} - Error during query execution: {}", methodName, e.getMessage(), e);
            return null;
        }
    }


    /**
     * findAllNewByDocType.
     * @param documentType String
     * @param creationDate LocalDateTime
     * @return List
     */
    public List<XhbCppFormattingDao> findAllNewByDocType(String documentType,
        LocalDateTime creationDate) {
        LOG.debug("findAllNewByDocType()");
        Query query = getEntityManager().createNamedQuery("XHB_CPP_FORMATTING.findAllNewByDocType");
        query.setParameter(DOCUMENT_TYPE, documentType);
        query.setParameter(CREATION_DATE, creationDate);
        return query.getResultList();
    }

    public List<XhbCppFormattingDao> findAllNewByDocTypeSafe(String documentType,
        LocalDateTime creationDate) {
        LOG.debug("findAllNewByDocTypeSafe(documentType={}, creationDate={})", documentType,
            creationDate);
        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query = em.createNamedQuery("XHB_CPP_FORMATTING.findAllNewByDocType");
            query.setParameter("documentType", documentType);
            query.setParameter("creationDate", creationDate);
            return query.getResultList();
        } catch (Exception e) {
            LOG.error("Error in findAllNewByDocTypeSafe({}, {}): {}", documentType, creationDate,
                e.getMessage(), e);
            return List.of(); // Avoid nulls and maintain calling code stability
        }
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
        List<XhbCppFormattingDao> resultList = query.getResultList();
        return resultList.isEmpty() ? null : resultList.get(0);
    }

    @SuppressWarnings("unchecked")
    public XhbCppFormattingDao getLatestDocumentByCourtIdAndTypeSafe(Integer courtId,
        String documentType, LocalDateTime creationDate) {

        LOG.debug("getLatestDocumentByCourtIdAndTypeSafe({}, {}, {})", courtId, documentType,
            creationDate);

        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query = em.createNamedQuery("XHB_CPP_FORMATTING.findByCourtAndDocType");
            query.setParameter("courtId", courtId);
            query.setParameter("documentType", documentType);
            query.setParameter("creationDate", creationDate);

            List<XhbCppFormattingDao> resultList = query.getResultList();
            return resultList.isEmpty() ? null : resultList.get(0);
        } catch (Exception e) {
            LOG.error("Error in getLatestDocumentByCourtIdAndTypeSafe({}, {}, {}): {}", courtId,
                documentType, creationDate, e.getMessage(), e);
            return null; // Preserves original method contract
        }
    }

}
