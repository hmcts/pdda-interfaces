package uk.gov.hmcts.pdda.business.entities.xhbcrlivedisplay;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.persistence.EntityManager;
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

}
