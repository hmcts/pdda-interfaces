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
import java.util.List;
import java.util.Optional;


@Repository
@SuppressWarnings({"PMD.LawOfDemeter", "PMD.AvoidDuplicateLiterals"})
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
     * findByCourtIdAndDate.
     * 
     * @return list
     */
    @SuppressWarnings("unchecked")
    public List<XhbHearingListDao> findByCourtIdAndDate(Integer courtId, LocalDateTime startDate) {
        LOG.debug("In XhbHearingRepository.findByCourtIdAndDate");
        Query query = getEntityManager().createNamedQuery("XHB_HEARING_LIST.findByCourtIdAndDate");
        query.setParameter("courtId", courtId);
        query.setParameter("startDate", startDate);
        return query.getResultList();
    }

    /**
     * findByCourtIdStatusAndDate.
     * 
     * @return XhbHearingListDao
     */
    public Optional<XhbHearingListDao> findByCourtIdStatusAndDate(Integer courtId, String status,
        LocalDateTime startDate) {
        LOG.debug("In XhbHearingRepository.findByCourtIdStatusAndDate");
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
    public Optional<XhbHearingListDao> findByCourtIdStatusAndDateSafe(Integer courtId,
        String status, LocalDateTime startDate) {
        LOG.debug("findByCourtIdStatusAndDateSafe(courtId: {}, status: {}, startDate: {})", courtId,
            status, startDate);

        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query = em.createNamedQuery("XHB_HEARING_LIST.findByCourtIdStatusAndDate");
            query.setParameter("courtId", courtId);
            query.setParameter("status", status);
            query.setParameter("startDate", startDate);

            List<?> resultList = query.getResultList();

            if (resultList == null || resultList.isEmpty()) {
                LOG.debug(
                    "findByCourtIdStatusAndDateSafe - No results for courtId: {}, status: {}, startDate: {}",
                    courtId, status, startDate);
                return Optional.empty();
            }

            Object result = resultList.get(0);
            if (result instanceof XhbHearingListDao) {
                LOG.debug(
                    "findByCourtIdStatusAndDateSafe - Returning result for courtId: {}, status: {}, startDate: {}",
                    courtId, status, startDate);
                return Optional.of((XhbHearingListDao) result);
            } else {
                LOG.warn("findByCourtIdStatusAndDateSafe - Unexpected result type: {}",
                    result.getClass().getName());
                return Optional.empty();
            }

        } catch (Exception e) {
            LOG.error("Error in findByCourtIdStatusAndDateSafe({}, {}, {}): {}", courtId, status,
                startDate, e.getMessage(), e);
            return Optional.empty();
        }
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

}
