package uk.gov.hmcts.pdda.business.entities.xhbsitting;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.pdda.business.entities.AbstractRepository;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;



@Repository
@SuppressWarnings({"PMD.LawOfDemeter", "PMD.AvoidDuplicateLiterals"})
public class XhbSittingRepository extends AbstractRepository<XhbSittingDao>
    implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(XhbSittingRepository.class);

    public XhbSittingRepository(EntityManager em) {
        super(em);
    }

    @Override
    public Class<XhbSittingDao> getDaoClass() {
        return XhbSittingDao.class;
    }

    /**
     * findByNonFloatingHearingList.

     * @param listId Integer
     * @return List
     */
    @SuppressWarnings("unchecked")
    public List<XhbSittingDao> findByNonFloatingHearingList(Integer listId) {
        LOG.debug("In XhbSittingRepository.findByNonFloatingHearingList");
        Query query =
            getEntityManager().createNamedQuery("XHB_SITTING.findByNonFloatingHearingList");
        query.setParameter("listId", listId);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<XhbSittingDao> findByNonFloatingHearingListSafe(Integer listId) {
        LOG.debug("findByNonFloatingHearingListSafe(listId: {})", listId);

        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query = em.createNamedQuery("XHB_SITTING.findByNonFloatingHearingList");
            query.setParameter("listId", listId);

            return query.getResultList();
        } catch (Exception e) {
            LOG.error("Error in findByNonFloatingHearingListSafe({}): {}", listId, e.getMessage(),
                e);
            return List.of(); // Return empty list as a safe fallback
        }
    }


    /**
     * findByListId.

     * @param listId Integer
     * @return List
     */
    @SuppressWarnings("unchecked")
    public List<XhbSittingDao> findByListId(Integer listId) {
        LOG.debug("In XhbSittingRepository.findByListId");
        Query query = getEntityManager().createNamedQuery("XHB_SITTING.findByListId");
        query.setParameter("listId", listId);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<XhbSittingDao> findByListIdSafe(Integer listId) {
        LOG.debug("findByListIdSafe(listId: {})", listId);

        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query = em.createNamedQuery("XHB_SITTING.findByListId");
            query.setParameter("listId", listId);

            return query.getResultList();
        } catch (Exception e) {
            LOG.error("Error in findByListIdSafe({}): {}", listId, e.getMessage(), e);
            return List.of(); // Safe fallback to avoid nulls
        }
    }


    /**
     * findByCourtRoomAndSittingTime.

     * @param courtSiteId Integer
     * @param courtRoomId Integer
     * @param sittingTime LocalDateTime
     * @return XhbSittingDao
     */
    public Optional<XhbSittingDao> findByCourtRoomAndSittingTime(Integer courtSiteId,
        Integer courtRoomId, LocalDateTime sittingTime) {
        LOG.debug("In XhbSittingRepository.findByListId");
        Query query =
            getEntityManager().createNamedQuery("XHB_SITTING.findByCourtRoomAndSittingTime");
        query.setParameter("courtSiteId", courtSiteId);
        query.setParameter("courtRoomId", courtRoomId);
        query.setParameter("sittingTime", sittingTime);
        XhbSittingDao dao =
            query.getResultList().isEmpty() ? null : (XhbSittingDao) query.getSingleResult();
        return dao != null ? Optional.of(dao) : Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public Optional<XhbSittingDao> findByCourtRoomAndSittingTimeSafe(Integer courtSiteId,
        Integer courtRoomId, LocalDateTime sittingTime) {
        LOG.debug(
            "findByCourtRoomAndSittingTimeSafe(courtSiteId: {}, courtRoomId: {}, sittingTime: {})",
            courtSiteId, courtRoomId, sittingTime);

        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query = em.createNamedQuery("XHB_SITTING.findByCourtRoomAndSittingTime");
            query.setParameter("courtSiteId", courtSiteId);
            query.setParameter("courtRoomId", courtRoomId);
            query.setParameter("sittingTime", sittingTime);

            List<?> resultList = query.getResultList();

            if (resultList == null || resultList.isEmpty()) {
                LOG.debug(
                    "findByCourtRoomAndSittingTimeSafe - No results found for courtSiteId: {},"
                        + " courtRoomId: {}, sittingTime: {}",
                    courtSiteId, courtRoomId, sittingTime);
                return Optional.empty();
            }

            Object result = resultList.get(0);
            if (result instanceof XhbSittingDao) {
                LOG.debug(
                    "findByCourtRoomAndSittingTimeSafe - Returning result for courtSiteId: {},"
                        + " courtRoomId: {}, sittingTime: {}",
                    courtSiteId, courtRoomId, sittingTime);
                return Optional.of((XhbSittingDao) result);
            } else {
                LOG.warn("findByCourtRoomAndSittingTimeSafe - Unexpected result type: {}",
                    result.getClass().getName());
                return Optional.empty();
            }

        } catch (Exception e) {
            LOG.error("Error in findByCourtRoomAndSittingTimeSafe({}, {}, {}): {}", courtSiteId,
                courtRoomId, sittingTime, e.getMessage(), e);
            return Optional.empty();
        }
    }

}
