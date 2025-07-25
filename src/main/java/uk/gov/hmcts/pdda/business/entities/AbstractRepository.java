package uk.gov.hmcts.pdda.business.entities;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@SuppressWarnings({"PMD.LawOfDemeter"})
public abstract class AbstractRepository<T extends AbstractDao> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractRepository.class);
    private static final String ERROR = "Error: {}";

    @PersistenceContext
    private EntityManager entityManager;

    
    protected AbstractRepository() {
        super();
    }
    
    protected AbstractRepository(EntityManager entityManager) {
        this();
        this.entityManager = entityManager;
    }

    protected abstract Class<T> getDaoClass();
    
    public Optional<T> findByIdSafe(Integer id) {
        LOG.debug("findByIdSafe({})", id);
        try (EntityManager em = createEntityManager()) {
            T dao = em.find(getDaoClass(), id);
            return dao != null ? Optional.of(dao) : Optional.empty();
        } catch (Exception e) {
            LOG.error("Error in findByIdSafe({}): {}", id, e.getMessage(), e);
            return Optional.empty();
        }
    }
    
    public Optional<T> findByIdSafe(Long id) {
        LOG.debug("findByIdSafe({})", id);
        try (EntityManager em = createEntityManager()) {
            T dao = em.find(getDaoClass(), id);
            return dao != null ? Optional.of(dao) : Optional.empty();
        } catch (Exception e) {
            LOG.error("Error in findByIdSafe({}): {}", id, e.getMessage(), e);
            return Optional.empty();
        }
    }

    @SuppressWarnings("unchecked")
    private List<T> findAll(String sql) {
        LOG.debug("findAll({})", sql);
        Query query = getEntityManager().createQuery(sql);
        return query.getResultList();
    }
    
    /**
     * findAll.
     * 
     * @return List
     */
    public List<T> findAll() {
        LOG.debug("findAll()");
        return findAll("from " + getDaoClass().getName());
    }

    @SuppressWarnings("unchecked")
    public List<T> findAllSafe() {
        try (EntityManager em = createEntityManager()) {
            Query query = em.createQuery("from " + getDaoClass().getName());
            return query.getResultList();
        }
    }

    /**
     * save.
     * @param dao Dao
     */
    public void save(T dao) {
        try (EntityManager localEntityManager = createEntityManager()) {
            try {
                LOG.debug("save({})", dao);
                localEntityManager.getTransaction().begin();
                localEntityManager.merge(dao);
                localEntityManager.getTransaction().commit();
            } catch (Exception e) {
                LOG.error(ERROR, e.getMessage());
                LOG.error("Stacktrace doing a database save; dao: {}: {}", dao,
                    ExceptionUtils.getStackTrace(e));
                if (localEntityManager != null && localEntityManager.getTransaction().isActive()) {
                    localEntityManager.getTransaction().rollback();
                }
            }
        }
    }

    /**
     * update.
     * @param dao Dao
     * @return dao
     */
    public Optional<T> update(T dao) {
        LOG.debug("Attempting update for DAO: {}", dao);

        try (EntityManager em = createEntityManager()) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();

            T updatedDao = em.merge(dao);
            transaction.commit();

            clearEntityManager(); // optional but useful
            return Optional.of(updatedDao);

        } catch (Exception e) {
            LOG.error("Error during update for DAO {}: {}", dao, e.getMessage(), e);
            throw new IllegalStateException("Update failed for DAO: " + dao.getPrimaryKey(), e);
        }
    }



    /**
     * delete.
     * @param xds Dao
     */
    public void delete(Optional<T> xds) {
        try (EntityManager localEntityManager = createEntityManager()) {
            try {
                LOG.debug("delete({})", xds);
                if (xds.isPresent()) {
                    localEntityManager.getTransaction().begin();
                    localEntityManager.remove(xds.stream().findFirst().orElse(null));
                    localEntityManager.getTransaction().commit();
                }
            } catch (Exception e) {
                LOG.error(ERROR, e.getMessage());
                LOG.error("Stacktrace doing a database delete; xds: {}: {}", xds,
                    ExceptionUtils.getStackTrace(e));
                if (localEntityManager != null && localEntityManager.getTransaction().isActive()) {
                    localEntityManager.getTransaction().rollback();
                }
            }
        }
    }

    /*
     * Main EntityManager for reads, etc.
     */
    public EntityManager getEntityManager() {
        return entityManager;
    }

    /*
     * Create local one off EntityManager for save, update, delete.
     */
    protected EntityManager createEntityManager() {
        return EntityManagerUtil.getEntityManager();
    }

    public void clearEntityManager() {
        getEntityManager().clear();
    }
}
