package uk.gov.hmcts.pdda.business.entities.xhbdisplaystore;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.pdda.business.entities.AbstractRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;


@SuppressWarnings({"PMD.CognitiveComplexity", "PMD.CyclomaticComplexity", "PMD.LawOfDemeter",
    "PMD.DoNotUseThreads"})
@Repository
public class XhbDisplayStoreRepository extends AbstractRepository<XhbDisplayStoreDao> {

    private static final Logger LOG = LoggerFactory.getLogger(XhbDisplayStoreRepository.class);

    public XhbDisplayStoreRepository(EntityManager em) {
        super(em);
    }

    @Override
    public Class<XhbDisplayStoreDao> getDaoClass() {
        return XhbDisplayStoreDao.class;
    }

    /**
     * findByRetrievalCode.

     * @param retrievalCode String
     * @return XhbDisplayStoreDao
     */
    public Optional<XhbDisplayStoreDao> findByRetrievalCode(final String retrievalCode) {
        LOG.debug("findByRetrievalCode()");
        Query query = getEntityManager().createNamedQuery("XHB_DISPLAY_STORE.findByRetrievalCode");
        query.setParameter("retrievalCode", retrievalCode);
        XhbDisplayStoreDao xds =
            query.getResultList().isEmpty() ? null : (XhbDisplayStoreDao) query.getResultList().get(0);
        return xds == null ? Optional.empty() : Optional.of(xds);
    }


    public Optional<XhbDisplayStoreDao> findByRetrievalCodeSafe(final String retrievalCode) {
        LOG.debug("findByRetrievalCodeSafe()");
        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query = em.createNamedQuery("XHB_DISPLAY_STORE.findByRetrievalCode");
            query.setParameter("retrievalCode", retrievalCode);
            List<?> resultList = query.getResultList();
            if (resultList.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of((XhbDisplayStoreDao) resultList.get(0));
        } catch (Exception e) {
            LOG.error("Error in findByRetrievalCodeSafe({}): {}", retrievalCode, e.getMessage(), e);
            return Optional.empty();
        }
    }


    @SuppressWarnings({"PMD.UnusedAssignment", "PMD.AvoidInstantiatingObjectsInLoops",
        "PMD.ExceptionAsFlowControl"})
    public void saveWithRetry(XhbDisplayStoreDao dao, int maxRetries) {
        int attempt = 0;
        XhbDisplayStoreDao updated = null;
        while (attempt < maxRetries) {
            try (EntityManager em = EntityManagerUtil.getEntityManager()) {
                em.getTransaction().begin();

                // Load fresh copy from DB to get the current version
                em.clear();
                XhbDisplayStoreDao fresh =
                    em.find(XhbDisplayStoreDao.class, dao.getDisplayStoreId());
                if (fresh == null) {
                    throw new IllegalStateException("DAO not found in DB.");
                }

                LOG.debug("Attempt {}: Existing version = {}, DAO version = {}", attempt,
                    fresh.getVersion(), dao.getVersion());

                // Skip update if content is unchanged
                if (Objects.equals(fresh.getContent(), dao.getContent())) {
                    LOG.info("Content unchanged for displayStoreId {}. Skipping update.",
                        dao.getDisplayStoreId());
                    em.getTransaction().rollback();
                    return;
                }

                // Build detached copy with updated fields
                updated = new XhbDisplayStoreDao(fresh);
                updated.setContent(dao.getContent());
                updated.setLastUpdatedBy("PDDA");
                updated.setLastUpdateDate(LocalDateTime.now());

                LOG.debug("Merging detached update with version {}", updated.getVersion());

                em.merge(updated); // Hibernate will increment version if dirty
                XhbDisplayStoreDao merged = em.merge(updated);
                LOG.info("Merged DAO ID {} now has version {}", merged.getDisplayStoreId(),
                    merged.getVersion());

                em.getTransaction().commit();
                return;

            } catch (Exception e) {
                boolean isOptimistic =
                    e.getMessage() != null && e.getMessage().contains("optimistic_lock_prob");

                if (isOptimistic) {
                    attempt++;
                    LOG.warn("Optimistic conflict on attempt {}/{} for ID {}", attempt, maxRetries,
                        dao.getDisplayStoreId());

                    try {
                        long jitterMs = 100 + new Random().nextInt(200);
                        Thread.sleep(jitterMs);
                    } catch (InterruptedException ignored) {
                        Thread.currentThread().interrupt();
                        LOG.debug("Thread interrupted during retry sleep");
                    }

                } else {
                    LOG.error("Unexpected error during saveWithRetry for ID {}: {}",
                        dao.getDisplayStoreId(), e.getMessage(), e);
                    throw e;
                }
            }
        }

        throw new IllegalStateException(
            "Max retries exceeded for displayStoreId " + dao.getDisplayStoreId());
    }


}
