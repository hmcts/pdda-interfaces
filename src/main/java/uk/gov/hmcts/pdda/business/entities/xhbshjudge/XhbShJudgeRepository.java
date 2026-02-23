package uk.gov.hmcts.pdda.business.entities.xhbshjudge;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.pdda.business.entities.AbstractRepository;

import java.io.Serializable;

@Repository
public class XhbShJudgeRepository extends AbstractRepository<XhbShJudgeDao> implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(XhbShJudgeRepository.class);

    public XhbShJudgeRepository(EntityManager em) {
        super(em);
    }

    @Override
    public Class<XhbShJudgeDao> getDaoClass() {
        return XhbShJudgeDao.class;
    }

    /**
     * deleteByShAttendeeId - remove any sh_judge rows referencing the provided shAttendeeId.
     * @param shAttendeeId Integer
     */
    public void deleteByShAttendeeId(Integer shAttendeeId) {
        LOG.debug("deleteByShAttendeeId(shAttendeeId: {})", shAttendeeId);
        if (shAttendeeId == null) {
            LOG.debug("deleteByShAttendeeId - null shAttendeeId provided, nothing to delete");
            return;
        }

        EntityTransaction tx = null;

        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            tx = beginTransactionIfPossible(em);

            Query query = em.createQuery(
                "DELETE FROM XHB_SH_JUDGE o WHERE o.shAttendeeId = :shAttendeeId");
            query.setParameter("shAttendeeId", shAttendeeId);
            int deleted = query.executeUpdate();

            commitIfActive(tx);
            logDeleteResult(shAttendeeId, deleted);
        } catch (Exception e) {
            rollbackIfActive(tx);
            LOG.error("Error in deleteByShAttendeeId({}): {}", shAttendeeId, e.getMessage(), e);
            throw e;
        }
    }

    @SuppressWarnings("PMD.LawOfDemeter") // em.getTransaction() is acceptable here
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

    private void logDeleteResult(Integer shAttendeeId, int deleted) {
        if (deleted == 0) {
            LOG.debug("deleteByShAttendeeId - no sh_judge rows deleted for shAttendeeId {}",
                shAttendeeId);
        } else {
            LOG.debug("deleteByShAttendeeId - deleted {} sh_judge rows for shAttendeeId {}",
                deleted, shAttendeeId);
        }
    }
}
