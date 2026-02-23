package uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.pdda.business.entities.AbstractRepository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Repository
@SuppressWarnings({"PMD.LawOfDemeter", "PMD.AvoidDuplicateLiterals", "PMD.TooManyMethods"})
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
     * @param scheduledHearingId Integer
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
     * findByScheduledHearingIdsSafe - find defendants for multiple scheduled hearing ids.
     * @param scheduledHearingIds list of scheduled hearing ids
     * @return list of defendants or empty list on error
     */
    @SuppressWarnings("unchecked")
    public List<XhbSchedHearingDefendantDao> findByScheduledHearingIdsSafe(
        List<Integer> scheduledHearingIds) {
        LOG.debug("findByScheduledHearingIdsSafe(scheduledHearingIds: {})", scheduledHearingIds);
        if (scheduledHearingIds == null || scheduledHearingIds.isEmpty()) {
            LOG.debug("findByScheduledHearingIdsSafe - no ids provided");
            return List.of();
        }

        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query = em.createQuery(
                "SELECT o from XHB_SCHED_HEARING_DEFENDANT o WHERE o.scheduledHearingId IN :ids");
            query.setParameter("ids", scheduledHearingIds);
            return query.getResultList();
        } catch (Exception e) {
            LOG.error("Error in findByScheduledHearingIdsSafe({}): {}",
                scheduledHearingIds, e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * deleteByScheduledHearingIds - bulk delete scheduled hearing defendants for the provided ids.
     * @param scheduledHearingIds list of scheduled hearing ids
     */
    public void deleteByScheduledHearingIds(List<Integer> scheduledHearingIds) {
        LOG.debug("deleteByScheduledHearingIds(scheduledHearingIds: {})", scheduledHearingIds);
        if (scheduledHearingIds == null || scheduledHearingIds.isEmpty()) {
            LOG.debug("deleteByScheduledHearingIds - no ids provided, nothing to delete");
            return;
        }

        EntityTransaction tx = null;

        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            tx = beginTransactionIfPossible(em);

            int deleted = executeDeleteQuery(em, scheduledHearingIds);

            commitIfActive(tx);
            logDeleteResult(scheduledHearingIds, deleted);
        } catch (Exception e) {
            rollbackIfActive(tx);
            LOG.error("Error in deleteByScheduledHearingIds({}): {}",
                scheduledHearingIds, e.getMessage(), e);
            throw e;
        }
    }

    private EntityTransaction beginTransactionIfPossible(EntityManager em) {
        try {
            EntityTransaction tx = em.getTransaction();
            tx.begin();
            return tx;
        } catch (UnsupportedOperationException | IllegalStateException ex) {
            LOG.debug("EntityManager does not support transactions, continuing without explicit tx", ex);
            return null;
        }
    }

    private int executeDeleteQuery(EntityManager em, List<Integer> scheduledHearingIds) {
        Query query = em.createQuery(
            "DELETE FROM XHB_SCHED_HEARING_DEFENDANT o WHERE o.scheduledHearingId IN :ids");
        query.setParameter("ids", scheduledHearingIds);
        return query.executeUpdate();
    }

    private void commitIfActive(EntityTransaction tx) {
        if (tx != null && tx.isActive()) {
            tx.commit();
        }
    }

    private void rollbackIfActive(EntityTransaction tx) {
        if (tx != null && tx.isActive()) {
            try {
                tx.rollback();
            } catch (Exception rbEx) {
                LOG.error("Failed to rollback transaction after error: {}",
                    rbEx.getMessage(), rbEx);
            }
        }
    }

    private void logDeleteResult(List<Integer> scheduledHearingIds, int deleted) {
        if (deleted == 0) {
            LOG.debug("deleteByScheduledHearingIds - No defendants deleted for ids: {}",
                scheduledHearingIds);
        } else {
            LOG.debug("deleteByScheduledHearingIds - Deleted {} defendants for ids: {}",
                deleted, scheduledHearingIds);
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
            LOG.error("Error in findByHearingAndDefendantSafe({}, {}): {}",
                scheduledHearingId, defendantOnCaseId, e.getMessage(), e);
            return Optional.empty();
        }
    }
}
