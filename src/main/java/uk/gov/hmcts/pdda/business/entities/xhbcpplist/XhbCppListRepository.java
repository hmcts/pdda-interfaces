package uk.gov.hmcts.pdda.business.entities.xhbcpplist;

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

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
@Repository
public class XhbCppListRepository extends AbstractRepository<XhbCppListDao>
    implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(XhbCppListRepository.class);

    public XhbCppListRepository(EntityManager em) {
        super(em);
    }

    @Override
    public Class<XhbCppListDao> getDaoClass() {
        return XhbCppListDao.class;
    }

    /**
     * findByCourtCodeAndListTypeAndListDate.

     * @param courtCode Integer
     * @param listType String
     * @param listStartDate LocalDateTime
     * @return List
     */
    @SuppressWarnings("unchecked")
    public List<XhbCppListDao> findByCourtCodeAndListTypeAndListDate(final Integer courtCode,
        final String listType, final LocalDateTime listStartDate) {
        LOG.debug("findByCourtCodeAndListTypeAndListDate({},{},{})", courtCode, listType, listStartDate);
        Query query = getEntityManager()
            .createNamedQuery("XHB_CPP_LIST.findByCourtCodeAndListTypeAndListDate");
        query.setParameter("courtCode", courtCode);
        query.setParameter("listType", listType);
        query.setParameter("listStartDate", listStartDate);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<XhbCppListDao> findByCourtCodeAndListTypeAndListDateSafe(final Integer courtCode,
        final String listType, final LocalDateTime listStartDate) {

        LOG.debug(
            "findByCourtCodeAndListTypeAndListDateSafe(courtCode: {}, listType: {}, listStartDate: {})",
            courtCode, listType, listStartDate);

        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query = em.createNamedQuery("XHB_CPP_LIST.findByCourtCodeAndListTypeAndListDate");
            query.setParameter("courtCode", courtCode);
            query.setParameter("listType", listType);
            query.setParameter("listStartDate", listStartDate);

            return query.getResultList();
        } catch (Exception e) {
            LOG.error("Error in findByCourtCodeAndListTypeAndListDateSafe({}, {}, {}): {}",
                courtCode, listType, listStartDate, e.getMessage(), e);
            return List.of(); // Safe fallback to prevent NPE
        }
    }


    /**
     * findByCourtCodeAndListTypeAndListStartDateAndListEndDate.

     * @param courtCode Integer
     * @param listType String
     * @param listStartDate LocalDateTime
     * @param listEndDate LocalDateTime
     * @return List
     */
    @SuppressWarnings("unchecked")
    public List<XhbCppListDao> findByCourtCodeAndListTypeAndListStartDateAndListEndDate(
        final Integer courtCode, final String listType, final LocalDateTime listStartDate,
        final LocalDateTime listEndDate) {
        LOG.debug("findByCourtCodeAndListTypeAndListStartDateAndListEndDate({},{},{},{})",
            courtCode, listType, listStartDate, listEndDate);
        Query query = getEntityManager().createNamedQuery(
            "XHB_CPP_LIST.findByCourtCodeAndListTypeAndListStartDateAndListEndDate");
        query.setParameter("courtCode", courtCode);
        query.setParameter("listType", listType);
        query.setParameter("listStartDate", listStartDate);
        query.setParameter("listEndDate", listEndDate);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<XhbCppListDao> findByCourtCodeAndListTypeAndListStartDateAndListEndDateSafe(
        final Integer courtCode, final String listType, final LocalDateTime listStartDate,
        final LocalDateTime listEndDate) {

        LOG.debug("findByCourtCodeAndListTypeAndListStartDateAndListEndDateSafe({},{},{},{})",
            courtCode, listType, listStartDate, listEndDate);

        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query = em.createNamedQuery(
                "XHB_CPP_LIST.findByCourtCodeAndListTypeAndListStartDateAndListEndDate");

            query.setParameter("courtCode", courtCode);
            query.setParameter("listType", listType);
            query.setParameter("listStartDate", listStartDate);
            query.setParameter("listEndDate", listEndDate);

            return query.getResultList();
        } catch (Exception e) {
            LOG.error(
                "Error in findByCourtCodeAndListTypeAndListStartDateAndListEndDateSafe({},{},{},{}): {}",
                courtCode, listType, listStartDate, listEndDate, e.getMessage(), e);
            return List.of(); // Return empty list to safely handle failure
        }
    }


    /**
     * findByClobId.

     * @param listClobId Long
     * @return XhbCppListDao
     */
    @SuppressWarnings("unchecked")
    public XhbCppListDao findByClobId(final Long listClobId) {
        LOG.debug("findByClobId({})", listClobId);
        Query query = getEntityManager().createNamedQuery("XHB_CPP_LIST.findByClobId");
        query.setParameter("listClobId", listClobId);
        List<XhbCppListDao> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @SuppressWarnings("unchecked")
    public XhbCppListDao findByClobIdSafe(Long listClobId) {
        LOG.debug("findByClobIdSafe({})", listClobId);

        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query = em.createNamedQuery("XHB_CPP_LIST.findByClobId");
            query.setParameter("listClobId", listClobId);

            List<?> resultList = query.getResultList();

            if (resultList == null || resultList.isEmpty()) {
                LOG.debug("findByClobIdSafe({}) - No results found", listClobId);
                return null;
            }

            Object result = resultList.get(0);
            if (result instanceof XhbCppListDao) {
                LOG.debug("findByClobIdSafe({}) - Returning first result", listClobId);
                return (XhbCppListDao) result;
            } else {
                LOG.warn("findByClobIdSafe({}) - Unexpected result type: {}", listClobId,
                    result.getClass().getName());
                return null;
            }

        } catch (Exception e) {
            LOG.error("Error in findByClobIdSafe({}): {}", listClobId, e.getMessage(), e);
            return null;
        }
    }

}
