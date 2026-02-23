package uk.gov.hmcts.pdda.business.entities.xhbhearinglist;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.pdda.business.entities.AbstractRepository;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Repository
@SuppressWarnings("PMD.LawOfDemeter")
public class XhbHearingListRepository extends AbstractRepository<XhbHearingListDao>
    implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(XhbHearingListRepository.class);

    public XhbHearingListRepository(EntityManager em) {
        super(em);
    }

    @Override
    public Class<XhbHearingListDao> getDaoClass() {
        return XhbHearingListDao.class;
    }

    /**
     * findByCourtIdStatusAndDate.

     * @return XhbHearingListDao
     */
    public Optional<XhbHearingListDao> findByCourtIdStatusAndDate(Integer courtId, String status,
        LocalDateTime startDate) {
        LOG.debug("In XhbHearingListRepository.findByCourtIdStatusAndDate");
        Query query =
            getEntityManager().createNamedQuery("XHB_HEARING_LIST.findByCourtIdStatusAndDate");
        query.setParameter("courtId", courtId);
        query.setParameter("status", status);
        query.setParameter("startDate", startDate);
        XhbHearingListDao dao =
            query.getResultList().isEmpty() ? null : (XhbHearingListDao) query.getSingleResult();
        return dao != null ? Optional.of(dao) : Optional.empty();
    }
    
    
    @SuppressWarnings("unchecked")
    private List<XhbHearingListDao> findByCourtIdStatusAndDateSharedLogic(Integer courtId,
            String status, LocalDateTime startDate) {

        LOG.debug("findByCourtIdStatusAndDate(courtId: {}, status: {}, startDate: {})",
                courtId, status, startDate);

        try (EntityManager em = EntityManagerUtil.getEntityManager()) {

            Query query = em.createNamedQuery("XHB_HEARING_LIST.findByCourtIdStatusAndDate");
            query.setParameter("courtId", courtId);
            query.setParameter("status", status);
            query.setParameter("startDate", startDate);

            List<?> resultList = query.getResultList();

            if (resultList == null || resultList.isEmpty()) {
                LOG.debug("No results for courtId: {}, status: {}, startDate: {}",
                        courtId, status, startDate);
                return Collections.emptyList();
            }

            return resultList.stream()
                    .filter(XhbHearingListDao.class::isInstance)
                    .map(XhbHearingListDao.class::cast)
                    .toList();

        } catch (Exception e) {
            LOG.error("Error in findByCourtIdStatusAndDate({}, {}, {}): {}",
                    courtId, status, startDate, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    
    public List<XhbHearingListDao> findAllByCourtIdStatusAndDate(Integer courtId,
        String status, LocalDateTime startDate) {

        return findByCourtIdStatusAndDateSharedLogic(courtId, status, startDate);
    }
    
    public Optional<XhbHearingListDao> findByCourtIdStatusAndDateSafe(Integer courtId,
        String status, LocalDateTime startDate) {

        List<XhbHearingListDao> results =
                findByCourtIdStatusAndDateSharedLogic(courtId, status, startDate);
    
        if (results.isEmpty()) {
            return Optional.empty();
        }
    
        return Optional.of(results.get(0));
    }


    @SuppressWarnings("unchecked")
    public List<XhbHearingListDao> findByCourtIdAndDateSafe(Integer courtId,
        LocalDateTime startDate) {
        LOG.debug("In XhbHearingListRepository.findByCourtIdAndDateSafe");
        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query = em.createNamedQuery("XHB_HEARING_LIST.findByCourtIdAndDate");
            query.setParameter("courtId", courtId);
            query.setParameter("startDate", startDate);
            return query.getResultList();
        } catch (Exception e) {
            LOG.error("Error in findByCourtIdAndDateSafe: {}", e.getMessage(), e);
            return List.of();
        }
    }
    
    // XhbHearingListRepository.java
    public void deleteById(Integer listId) {
        if (listId == null) {
            return;
        }
        String sql = "DELETE FROM PDDA.XHB_HEARING_LIST WHERE LIST_ID = :listId";
        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            em.getTransaction().begin();
            Query query = em.createNativeQuery(sql);
            query.setParameter("listId", listId);
            query.executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            LOG.error("Error deleting hearing list id {}: {}", listId, e.getMessage(), e);
            throw e;
        }
    }


}
