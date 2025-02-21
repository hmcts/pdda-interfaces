package uk.gov.hmcts.pdda.business.entities;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("PMD.LawOfDemeter")
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
    
    /**
     * findById.
     * 
     * @param id Integer
     * @return dao
     */
    public Optional<T> findById(Integer id) {
        LOG.debug("findById({})", id);
        T dao = getEntityManager().find(getDaoClass(), id);
        return dao != null ? Optional.of(dao) : Optional.empty();
    }

    /**
     * findById.
     * @param id Long
     * @return dao
     */
    public Optional<T> findById(Long id) {
        LOG.debug("findById({})", id);
        T dao = getEntityManager().find(getDaoClass(), id);
        return dao != null ? Optional.of(dao) : Optional.empty();
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

    /**
     * save.
     * @param dao Dao
     */
    public void save(T dao) {
        try (EntityManager localEntityManager = createEntityManager()) {
            try {
                LOG.debug("save({})", dao);
                localEntityManager.getTransaction().begin();
                localEntityManager.persist(dao);
                localEntityManager.getTransaction().commit();
                // Clear the cache on the EntityManager after saving
                clearEntityManager();
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
        try (EntityManager localEntityManager = createEntityManager()) {
            try {
                LOG.debug("update({})", dao);
                T updatedDao = dao;
                localEntityManager.getTransaction().begin();
                updatedDao = localEntityManager.merge(updatedDao);
                localEntityManager.getTransaction().commit();
                updatedDao.setVersion(updatedDao.getVersion() != null ? updatedDao.getVersion() + 1 : 1);
                // Clear the cache on the EntityManager after updating
                clearEntityManager();
                return Optional.of(updatedDao);
            } catch (Exception e) {
                LOG.error(ERROR, e.getMessage());
                LOG.error("Stacktrace doing a database update; dao: {}: {}", dao,
                    ExceptionUtils.getStackTrace(e));
                if (localEntityManager != null && localEntityManager.getTransaction().isActive()) {
                    localEntityManager.getTransaction().rollback();
                }
            }
        }
        return Optional.empty();
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
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    /*
     * Create local one off EntityManager for save, update, delete.
     */
    private EntityManager createEntityManager() {
        return EntityManagerUtil.getEntityManager();
    }

    public void clearEntityManager() {
        getEntityManager().clear();
    }
}
