package uk.gov.hmcts.pdda.business.entities.xhbcase;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.pdda.business.entities.AbstractRepository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;



@Repository
@SuppressWarnings("PMD.LawOfDemeter")
public class XhbCaseRepository extends AbstractRepository<XhbCaseDao> implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(XhbCaseRepository.class);

    public XhbCaseRepository(EntityManager em) {
        super(em);
    }

    @Override
    public Class<XhbCaseDao> getDaoClass() {
        return XhbCaseDao.class;
    }

    /**
     * findByNumberTypeAndCourt.

     * @param courtId Integer
     * @param caseType String
     * @param caseNumber Integer
     * @return XhbCaseDao
     */
    public Optional<XhbCaseDao> findByNumberTypeAndCourt(final Integer courtId,
        final String caseType, final Integer caseNumber) {
        LOG.debug("findByNumberTypeAndCourt({}{})", caseType, caseNumber);
        Query query = getEntityManager().createNamedQuery("XHB_CASE.findByNumberTypeAndCourt");
        query.setParameter("courtId", courtId);
        query.setParameter("caseType", caseType);
        query.setParameter("caseNumber", caseNumber);
        XhbCaseDao dao =
            query.getResultList().isEmpty() ? null : (XhbCaseDao) query.getSingleResult();
        return dao != null ? Optional.of(dao) : Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public Optional<XhbCaseDao> findByNumberTypeAndCourtSafe(final Integer courtId,
        final String caseType, final Integer caseNumber) {

        LOG.debug("findByNumberTypeAndCourtSafe(courtId: {}, caseType: {}, caseNumber: {})",
            courtId, caseType, caseNumber);

        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query = em.createNamedQuery("XHB_CASE.findByNumberTypeAndCourt");
            query.setParameter("courtId", courtId);
            query.setParameter("caseType", caseType);
            query.setParameter("caseNumber", caseNumber);

            List<?> resultList = query.getResultList();

            if (resultList == null || resultList.isEmpty()) {
                LOG.debug(
                    "findByNumberTypeAndCourtSafe - No results found for courtId: {}, caseType: {}, caseNumber: {}",
                    courtId, caseType, caseNumber);
                return Optional.empty();
            }

            Object result = resultList.get(0);
            if (result instanceof XhbCaseDao) {
                LOG.debug(
                    "findByNumberTypeAndCourtSafe - Returning result for courtId: {}, caseType: {}, caseNumber: {}",
                    courtId, caseType, caseNumber);
                return Optional.of((XhbCaseDao) result);
            } else {
                LOG.warn("findByNumberTypeAndCourtSafe - Unexpected result type: {}",
                    result.getClass().getName());
                return Optional.empty();
            }

        } catch (Exception e) {
            LOG.error("Error in findByNumberTypeAndCourtSafe({}, {}, {}): {}", courtId, caseType,
                caseNumber, e.getMessage(), e);
            return Optional.empty();
        }
    }

}
