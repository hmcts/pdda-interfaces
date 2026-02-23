package uk.gov.hmcts.pdda.business.entities.xhbschedhearingattendee;

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

@Repository
public class XhbSchedHearingAttendeeRepository
    extends AbstractRepository<XhbSchedHearingAttendeeDao> implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(XhbSchedHearingAttendeeRepository.class);

    public XhbSchedHearingAttendeeRepository(EntityManager em) {
        super(em);
    }

    @Override
    public Class<XhbSchedHearingAttendeeDao> getDaoClass() {
        return XhbSchedHearingAttendeeDao.class;
    }

    /**
     * Find all attendees for a given scheduled hearing id.
     * @param scheduledHearingId scheduled hearing id
     * @return list of attendees, empty list if none found or error occurs
     */
    @SuppressWarnings("unchecked")
    public List<XhbSchedHearingAttendeeDao> findByScheduledHearingIdSafe(
        Integer scheduledHearingId) {
        LOG.debug("findByScheduledHearingIdSafe(scheduledHearingId: {})", scheduledHearingId);

        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query = em.createQuery(
                "SELECT o from XHB_SCHED_HEARING_ATTENDEE o WHERE o.scheduledHearingId = :scheduledHearingId");
            query.setParameter("scheduledHearingId", scheduledHearingId);

            return query.getResultList();
        } catch (Exception e) {
            LOG.error("Error in findByScheduledHearingIdSafe({}): {}", scheduledHearingId, e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * Bulk delete attendees whose scheduled hearing id is in the provided list.
     * @param scheduledHearingIds list of ids
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
            LOG.error("Error in deleteByScheduledHearingIds({}): {}", scheduledHearingIds, e.getMessage(), e);
            throw e;
        }
    }

    @SuppressWarnings("PMD.LawOfDemeter") // em.getTransaction() is OK in this context
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
            "DELETE FROM XHB_SCHED_HEARING_ATTENDEE o WHERE o.scheduledHearingId IN :ids");
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
                LOG.error("Failed to rollback transaction after error: {}", rbEx.getMessage(), rbEx);
            }
        }
    }

    private void logDeleteResult(List<Integer> scheduledHearingIds, int deleted) {
        if (deleted == 0) {
            LOG.debug("deleteByScheduledHearingIds - No attendees deleted for ids: {}", scheduledHearingIds);
        } else {
            LOG.debug("deleteByScheduledHearingIds - Deleted {} attendees for ids: {}",
                deleted, scheduledHearingIds);
        }
    }
}
