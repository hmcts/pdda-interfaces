package uk.gov.hmcts.pdda.business.entities.xhbcourtroom;

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
public class XhbCourtRoomRepository extends AbstractRepository<XhbCourtRoomDao>
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(XhbCourtRoomRepository.class);
    private static final String UNCHECKED = "unchecked";

    public XhbCourtRoomRepository(EntityManager em) {
        super(em);
    }

    @Override
    public Class<XhbCourtRoomDao> getDaoClass() {
        return XhbCourtRoomDao.class;
    }

    /**
     * findByCourtSiteId.

     * @param courtSiteId Integer
     * @return List
     */
    @SuppressWarnings(UNCHECKED)
    public List<XhbCourtRoomDao> findByCourtSiteId(Integer courtSiteId) {
        LOG.debug("findByCourtSiteId({})", courtSiteId);
        Query query = getEntityManager().createNamedQuery("XHB_COURT_ROOM.findByCourtSiteId");
        query.setParameter("courtSiteId", courtSiteId);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<XhbCourtRoomDao> findByCourtSiteIdSafe(Integer courtSiteId) {
        LOG.debug("findByCourtSiteIdSafe({})", courtSiteId);
        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query = em.createNamedQuery("XHB_COURT_ROOM.findByCourtSiteId");
            query.setParameter("courtSiteId", courtSiteId);
            return query.getResultList();
        } catch (Exception e) {
            LOG.error("Error in findByCourtSiteIdSafe({}): {}", courtSiteId, e.getMessage(), e);
            return List.of(); // Return empty list on failure
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<XhbCourtRoomDao> findByCourtSiteIdAndCourtRoomNameSafe(Integer courtSiteId, String courtRoomName) {
        LOG.debug("findByCourtSiteIdAndCourtRoomNameSafe({},{})", courtSiteId, courtRoomName);
        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query = em.createNamedQuery("XHB_COURT_ROOM.findByCourtSiteIdAndCourtRoomName");
            query.setParameter("courtSiteId", courtSiteId);
            query.setParameter("courtRoomName", courtRoomName);
            return query.getResultList();
        } catch (Exception e) {
            LOG.error("Error in findByCourtSiteIdAndCourtRoomNameSafe({},{}): {}",
                courtSiteId, courtRoomName, e.getMessage(), e);
            return List.of(); // Return empty list on failure
        }
    }


    /**
     * findByDisplayId.

     * @param displayId Integer
     * @return List
     */
    /*
     * @SuppressWarnings(UNCHECKED) public List<XhbCourtRoomDao> findByDisplayId(Integer displayId)
     * { LOG.debug("findByDisplayId({})", displayId); Query query =
     * getEntityManager().createNamedQuery("XHB_COURT_ROOM.findByDisplayId");
     * query.setParameter("displayId", displayId); return query.getResultList(); }
     */

    @SuppressWarnings(UNCHECKED)
    public List<XhbCourtRoomDao> findByDisplayIdSafe(Integer displayId) {
        LOG.debug("findByDisplayIdSafe({})", displayId);
        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query = em.createNamedQuery("XHB_COURT_ROOM.findByDisplayId");
            query.setParameter("displayId", displayId);
            return query.getResultList();
        } catch (Exception e) {
            LOG.error("Error in findByDisplayIdSafe({}): {}", displayId, e.getMessage(), e);
            return List.of(); // Return empty list on failure
        }
    }


    /**
     * findVIPMultiSite.

     * @param courtId Integer
     * @return List
     */
    @SuppressWarnings(UNCHECKED)
    public List<XhbCourtRoomDao> findVipMultiSite(Integer courtId) {
        LOG.debug("findVipMultiSite({})", courtId);
        Query query = getEntityManager().createNamedQuery("XHB_COURT_ROOM.findVIPMultiSite");
        query.setParameter("courtId", courtId);
        return query.getResultList();
    }

    /**
     * findVIPMNoSite.

     * @param courtId Integer
     * @return List
     */
    @SuppressWarnings(UNCHECKED)
    public List<XhbCourtRoomDao> findVipMNoSite(Integer courtId) {
        LOG.debug("findVipMNoSite({})", courtId);
        Query query = getEntityManager().createNamedQuery("XHB_COURT_ROOM.findVIPMNoSite");
        query.setParameter("courtId", courtId);
        return query.getResultList();
    }

    /**
     * findByCourtRoomNo.

     * @param courtSiteId Integer
     * @param crestCourtRoomNo Integer
     * @return XhbCourtRoomDao
     */
    public Optional<XhbCourtRoomDao> findByCourtRoomNo(Integer courtSiteId,
        Integer crestCourtRoomNo) {
        LOG.debug("findByCourtRoomNo({})", crestCourtRoomNo);
        Query query = getEntityManager().createNamedQuery("XHB_COURT_ROOM.findByCourtRoomNo");
        query.setParameter("courtSiteId", courtSiteId);
        query.setParameter("crestCourtRoomNo", crestCourtRoomNo);
        XhbCourtRoomDao dao =
            query.getResultList().isEmpty() ? null : (XhbCourtRoomDao) query.getSingleResult();
        return dao != null ? Optional.of(dao) : Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public Optional<XhbCourtRoomDao> findByCourtRoomNoSafe(Integer courtSiteId,
        Integer crestCourtRoomNo) {
        LOG.debug("findByCourtRoomNoSafe(courtSiteId: {}, crestCourtRoomNo: {})", courtSiteId,
            crestCourtRoomNo);

        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query = em.createNamedQuery("XHB_COURT_ROOM.findByCourtRoomNo");
            query.setParameter("courtSiteId", courtSiteId);
            query.setParameter("crestCourtRoomNo", crestCourtRoomNo);

            List<?> resultList = query.getResultList();

            if (resultList == null || resultList.isEmpty()) {
                LOG.debug(
                    "findByCourtRoomNoSafe - No results found for courtSiteId: {}, crestCourtRoomNo: {}",
                    courtSiteId, crestCourtRoomNo);
                return Optional.empty();
            }

            Object result = resultList.get(0);
            if (result instanceof XhbCourtRoomDao) {
                LOG.debug(
                    "findByCourtRoomNoSafe - Returning first result for courtSiteId: {}, crestCourtRoomNo: {}",
                    courtSiteId, crestCourtRoomNo);
                return Optional.of((XhbCourtRoomDao) result);
            } else {
                LOG.warn("findByCourtRoomNoSafe - Unexpected result type: {}",
                    result.getClass().getName());
                return Optional.empty();
            }

        } catch (Exception e) {
            LOG.error("Error in findByCourtRoomNoSafe({}, {}): {}", courtSiteId, crestCourtRoomNo,
                e.getMessage(), e);
            return Optional.empty();
        }
    }

}
