package uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant;

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
@SuppressWarnings({"PMD.LawOfDemeter", "PMD.AvoidDuplicateLiterals"})
public class XhbSchedHearingDefendantRepository
    extends AbstractRepository<XhbSchedHearingDefendantDao> implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG =
        LoggerFactory.getLogger(XhbSchedHearingDefendantRepository.class);

    public XhbSchedHearingDefendantRepository(EntityManager em) {
        super(em);
    }

    @Override
    public Class<XhbSchedHearingDefendantDao> getDaoClass() {
        return XhbSchedHearingDefendantDao.class;
    }

    /**
     * findByScheduledHearingId.

     * @param scheduledHearingId INteger
     * @return List
     */
    @SuppressWarnings("unchecked")
    public List<XhbSchedHearingDefendantDao> findByScheduledHearingId(Integer scheduledHearingId) {
        LOG.debug("findByScheduledHearingId()");
        Query query = getEntityManager()
            .createNamedQuery("XHB_SCHED_HEARING_DEFENDANT.findByScheduledHearingId");
        query.setParameter("scheduledHearingId", scheduledHearingId);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<XhbSchedHearingDefendantDao> findByScheduledHearingIdSafe(
        Integer scheduledHearingId) {
        LOG.debug("findByScheduledHearingIdSafe(scheduledHearingId: {})", scheduledHearingId);

        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query =
                em.createNamedQuery("XHB_SCHED_HEARING_DEFENDANT.findByScheduledHearingId");
            query.setParameter("scheduledHearingId", scheduledHearingId);

            return query.getResultList();
        } catch (Exception e) {
            LOG.error("Error in findByScheduledHearingIdSafe({}): {}", scheduledHearingId,
                e.getMessage(), e);
            return List.of(); // Return empty list as safe fallback
        }
    }


    /**
     * findByHearingAndDefendant.

     * @return XhbSchedHearingDefendantDao
     */
    public Optional<XhbSchedHearingDefendantDao> findByHearingAndDefendant(
        final Integer scheduledHearingId, final Integer defendantOnCaseId) {
        LOG.debug("findByHearingAndDefendant()");
        Query query = getEntityManager()
            .createNamedQuery("XHB_SCHED_HEARING_DEFENDANT.findByHearingAndDefendant");
        query.setParameter("scheduledHearingId", scheduledHearingId);
        query.setParameter("defendantOnCaseId", defendantOnCaseId);
        XhbSchedHearingDefendantDao dao = query.getResultList().isEmpty() ? null
            : (XhbSchedHearingDefendantDao) query.getSingleResult();
        return dao != null ? Optional.of(dao) : Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public Optional<XhbSchedHearingDefendantDao> findByHearingAndDefendantSafe(
        final Integer scheduledHearingId, final Integer defendantOnCaseId) {

        LOG.debug("findByHearingAndDefendantSafe(scheduledHearingId: {}, defendantOnCaseId: {})",
            scheduledHearingId, defendantOnCaseId);

        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query =
                em.createNamedQuery("XHB_SCHED_HEARING_DEFENDANT.findByHearingAndDefendant");
            query.setParameter("scheduledHearingId", scheduledHearingId);
            query.setParameter("defendantOnCaseId", defendantOnCaseId);

            List<?> resultList = query.getResultList();

            if (resultList == null || resultList.isEmpty()) {
                LOG.debug("findByHearingAndDefendantSafe - No results found");
                return Optional.empty();
            }

            Object result = resultList.get(0);
            if (result instanceof XhbSchedHearingDefendantDao) {
                LOG.debug("findByHearingAndDefendantSafe - Returning result");
                return Optional.of((XhbSchedHearingDefendantDao) result);
            } else {
                LOG.warn("findByHearingAndDefendantSafe - Unexpected result type: {}",
                    result.getClass().getName());
                return Optional.empty();
            }

        } catch (Exception e) {
            LOG.error("Error in findByHearingAndDefendantSafe({}, {}): {}", scheduledHearingId,
                defendantOnCaseId, e.getMessage(), e);
            return Optional.empty();
        }
    }

}
