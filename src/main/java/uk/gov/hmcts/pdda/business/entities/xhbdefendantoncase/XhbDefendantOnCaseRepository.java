package uk.gov.hmcts.pdda.business.entities.xhbdefendantoncase;

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
public class XhbDefendantOnCaseRepository extends AbstractRepository<XhbDefendantOnCaseDao>
    implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(XhbDefendantOnCaseRepository.class);

    public XhbDefendantOnCaseRepository(EntityManager em) {
        super(em);
    }

    @Override
    public Class<XhbDefendantOnCaseDao> getDaoClass() {
        return XhbDefendantOnCaseDao.class;
    }

    /**
     * findByDefendantAndCase.

     * @param caseId Integer
     * @param defendantId Integer
     * @return XhbDefendantOnCaseDao
     */
    public Optional<XhbDefendantOnCaseDao> findByDefendantAndCase(final Integer caseId,
        final Integer defendantId) {
        LOG.debug("findByDefendantAndCase()");
        Query query =
            getEntityManager().createNamedQuery("XHB_DEFENDANT_ON_CASE.findByDefendantAndCase");
        query.setParameter("caseId", caseId);
        query.setParameter("defendantId", defendantId);
        XhbDefendantOnCaseDao dao = query.getResultList().isEmpty() ? null
            : (XhbDefendantOnCaseDao) query.getSingleResult();
        return dao != null ? Optional.of(dao) : Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public Optional<XhbDefendantOnCaseDao> findByDefendantAndCaseSafe(final Integer caseId,
        final Integer defendantId) {
        LOG.debug("findByDefendantAndCaseSafe(caseId: {}, defendantId: {})", caseId, defendantId);

        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query = em.createNamedQuery("XHB_DEFENDANT_ON_CASE.findByDefendantAndCase");
            query.setParameter("caseId", caseId);
            query.setParameter("defendantId", defendantId);

            List<?> resultList = query.getResultList();

            if (resultList == null || resultList.isEmpty()) {
                LOG.debug(
                    "findByDefendantAndCaseSafe - No result found for caseId: {}, defendantId: {}",
                    caseId, defendantId);
                return Optional.empty();
            }

            Object result = resultList.get(0);
            if (result instanceof XhbDefendantOnCaseDao) {
                LOG.debug(
                    "findByDefendantAndCaseSafe - Returning result for caseId: {}, defendantId: {}",
                    caseId, defendantId);
                return Optional.of((XhbDefendantOnCaseDao) result);
            } else {
                LOG.warn("findByDefendantAndCaseSafe - Unexpected result type: {}",
                    result.getClass().getName());
                return Optional.empty();
            }

        } catch (Exception e) {
            LOG.error("Error in findByDefendantAndCaseSafe({}, {}): {}", caseId, defendantId,
                e.getMessage(), e);
            return Optional.empty();
        }
    }

}
