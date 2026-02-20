package uk.gov.hmcts.pdda.business.entities.xhbcrlivedisplay;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceContext;
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
public class XhbCrLiveDisplayRepository extends AbstractRepository<XhbCrLiveDisplayDao>
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(XhbCrLiveDisplayRepository.class);

    @PersistenceContext
    private EntityManager entityManager;

    public XhbCrLiveDisplayRepository(EntityManager em) {
        super(em);
    }

    @Override
    public Class<XhbCrLiveDisplayDao> getDaoClass() {
        return XhbCrLiveDisplayDao.class;
    }

    /**
     * findLiveDisplaysWhereStatusNotNull.

     * @return List
     */
    @SuppressWarnings("unchecked")
    public List<XhbCrLiveDisplayDao> findLiveDisplaysWhereStatusNotNull() {
        LOG.debug("findLiveDisplaysWhereStatusNotNull()");
        Query query = getEntityManager()
            .createNamedQuery("XHB_CR_LIVE_DISPLAY.findLiveDisplaysWhereStatusNotNull");
        return query.getResultList();
    }

    /**
     * findByCourtRoom.

     * @param courtRoomId Integer
     * @return XhbCrLiveDisplayDao
     */
    public Optional<XhbCrLiveDisplayDao> findByCourtRoom(final Integer courtRoomId) {
        LOG.debug("findByHearing()");
        Query query = getEntityManager().createNamedQuery("XHB_CR_LIVE_DISPLAY.findByCourtRoom");
        query.setParameter("courtRoomId", courtRoomId);
        XhbCrLiveDisplayDao dao =
            query.getResultList().isEmpty() ? null : (XhbCrLiveDisplayDao) query.getSingleResult();
        return dao != null ? Optional.of(dao) : Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public Optional<XhbCrLiveDisplayDao> findByCourtRoomSafe(final Integer courtRoomId) {
        LOG.debug("findByCourtRoomSafe(courtRoomId: {})", courtRoomId);

        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query = em.createNamedQuery("XHB_CR_LIVE_DISPLAY.findByCourtRoom");
            query.setParameter("courtRoomId", courtRoomId);

            List<?> resultList = query.getResultList();

            if (resultList == null || resultList.isEmpty()) {
                LOG.debug("findByCourtRoomSafe - No results found for courtRoomId: {}",
                    courtRoomId);
                return Optional.empty();
            }

            Object result = resultList.get(0);
            if (result instanceof XhbCrLiveDisplayDao) {
                LOG.debug("findByCourtRoomSafe - Returning result for courtRoomId: {}",
                    courtRoomId);
                return Optional.of((XhbCrLiveDisplayDao) result);
            } else {
                LOG.warn("findByCourtRoomSafe - Unexpected result type: {}",
                    result.getClass().getName());
                return Optional.empty();
            }

        } catch (Exception e) {
            LOG.error("Error in findByCourtRoomSafe({}): {}", courtRoomId, e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Given a scheduled hearing ID, find any records with that ID and set the value to null for those records.
     * This is used when a hearing is deleted, to ensure that the live display does not attempt to display
     * information for a hearing that no longer exists.
     * @param scheduledHearingId the ID of the scheduled hearing that has been deleted
     */
    public void updateScheduledHearingIdToNull(Integer scheduledHearingId) {
        // Guard against a null being passed in - nothing to do in that case.
        if (scheduledHearingId == null) {
            LOG.debug("updateScheduledHearingIdToNull called with null scheduledHearingId - no action taken");
            return;
        }

        String sql = "UPDATE PDDA.XHB_CR_LIVE_DISPLAY SET SCHEDULED_HEARING_ID = NULL WHERE SCHEDULED_HEARING_ID"
            + " = :scheduledHearingId";
        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            performUpdateWithOptionalTransaction(em, sql, scheduledHearingId);
        } catch (Exception e) {
            // If obtaining/closing the EntityManager itself failed, log and rethrow
            LOG.error("Error acquiring EntityManager in updateScheduledHearingIdToNull({}): {}", scheduledHearingId,
                e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Helper method to perform an update query with optional transaction management.
     * Handles both container-managed and application-managed EntityManager instances.
     * @param em EntityManager to use for the query
     * @param sql SQL update statement
     * @param scheduledHearingId the parameter value for the query
     */
    private void performUpdateWithOptionalTransaction(EntityManager em, String sql, Integer scheduledHearingId) {
        boolean hasTransaction = false;
        try {
            hasTransaction = tryBeginTransaction(em);
            Query query = em.createNativeQuery(sql);
            query.setParameter("scheduledHearingId", scheduledHearingId);
            int rowsUpdated = query.executeUpdate();
            commitTransaction(em, hasTransaction);
            logUpdateResult(rowsUpdated, scheduledHearingId);
        } catch (Exception e) {
            rollbackIfNeeded(em, hasTransaction);
            LOG.error("Error in updateScheduledHearingIdToNull({}): {}", scheduledHearingId,
                e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Attempt to begin a transaction if the EntityManager supports it.
     * @param em EntityManager to begin transaction on
     * @return true if transaction was successfully begun, false otherwise
     */
    @SuppressWarnings("PMD.EmptyCatchBlock")
    private boolean tryBeginTransaction(EntityManager em) {
        try {
            EntityTransaction tx = em.getTransaction();
            if (tx != null) {
                tx.begin();
                return true;
            }
        } catch (UnsupportedOperationException | IllegalStateException ex) {
            // Container-managed EM or transaction already active - no action needed
        }
        return false;
    }

    /**
     * Commit the transaction if one was started.
     * @param em EntityManager with active transaction
     * @param hasTransaction true if a transaction was started by tryBeginTransaction
     */
    private void commitTransaction(EntityManager em, boolean hasTransaction) {
        if (hasTransaction) {
            EntityTransaction tx = em.getTransaction();
            if (tx != null && tx.isActive()) {
                tx.commit();
            }
        }
    }

    /**
     * Rollback the transaction if one was started and is still active.
     * @param em EntityManager with active transaction
     * @param hasTransaction true if a transaction was started by tryBeginTransaction
     */
    private void rollbackIfNeeded(EntityManager em, boolean hasTransaction) {
        if (hasTransaction) {
            try {
                EntityTransaction tx = em.getTransaction();
                if (tx != null && tx.isActive()) {
                    tx.rollback();
                }
            } catch (Exception rbEx) {
                LOG.error("Failed to rollback transaction after error: {}", rbEx.getMessage(), rbEx);
            }
        }
    }

    /**
     * Log the result of an update operation.
     * @param rowsUpdated number of rows updated
     * @param scheduledHearingId the hearing ID that was updated
     */
    private void logUpdateResult(int rowsUpdated, Integer scheduledHearingId) {
        if (rowsUpdated == 0) {
            LOG.debug("updateScheduledHearingIdToNull - No rows found for scheduledHearingId: {}",
                scheduledHearingId);
        } else {
            LOG.debug("updateScheduledHearingIdToNull - Updated {} rows for scheduledHearingId: {}",
                rowsUpdated, scheduledHearingId);
        }
    }

}