package uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing;

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
public class XhbScheduledHearingRepository extends AbstractRepository<XhbScheduledHearingDao>
    implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(XhbScheduledHearingRepository.class);

    private static final String LIST_ID = "listId";
    private static final String COURT_ROOM_ID = "courtId";
    private static final String SCHEDULED_HEARING_ID = "scheduledHearingId";
    private static final String SITTING_ID = "sittingId";

    public XhbScheduledHearingRepository(EntityManager em) {
        super(em);
    }

    @Override
    public Class<XhbScheduledHearingDao> getDaoClass() {
        return XhbScheduledHearingDao.class;
    }

    /**
     * findActiveCasesInRoom.

     * @param listId Integer
     * @param courtRoomId Integer
     * @param scheduledHearingId INteger
     * @return List
     */
    @SuppressWarnings("unchecked")
    public List<XhbScheduledHearingDao> findActiveCasesInRoom(Integer listId, Integer courtRoomId,
        Integer scheduledHearingId) {
        LOG.debug("findActiveCasesInRoom({},{},{})", listId, courtRoomId, scheduledHearingId);
        Query query =
            getEntityManager().createNamedQuery("XHB_SCHEDULED_HEARING.findActiveCasesInRoom");
        query.setParameter(COURT_ROOM_ID, courtRoomId);
        query.setParameter(LIST_ID, listId);
        query.setParameter(SCHEDULED_HEARING_ID, scheduledHearingId);
        return (List<XhbScheduledHearingDao>) query.getResultList();
    }

    /**
     * findBySittingId.

     * @param sittingId Integer
     * @return List
     */
    @SuppressWarnings("unchecked")
    public List<XhbScheduledHearingDao> findBySittingId(Integer sittingId) {
        LOG.debug("findBySittingId({})", sittingId);
        Query query = getEntityManager().createNamedQuery("XHB_SCHEDULED_HEARING.findBySittingId");
        query.setParameter(SITTING_ID, sittingId);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<XhbScheduledHearingDao> findBySittingIdSafe(Integer sittingId) {
        LOG.debug("findBySittingIdSafe(sittingId: {})", sittingId);

        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query = em.createNamedQuery("XHB_SCHEDULED_HEARING.findBySittingId");
            query.setParameter(SITTING_ID, sittingId);

            return query.getResultList();
        } catch (Exception e) {
            LOG.error("Error in findBySittingIdSafe({}): {}", sittingId, e.getMessage(), e);
            return List.of(); // Safe fallback to avoid null or exception
        }
    }


    /**
     * findBySittingDate.

     * @return XhbScheduledHearingDao
     */
    public Optional<XhbScheduledHearingDao> findBySittingDate(final Integer sittingId,
        final Integer hearingId, final LocalDateTime notBeforeTime) {
        LOG.debug("In XhbHearingRepository.findBySitting");
        Query query =
            getEntityManager().createNamedQuery("XHB_SCHEDULED_HEARING.findBySittingDate");
        query.setParameter(SITTING_ID, sittingId);
        query.setParameter("hearingId", hearingId);
        query.setParameter("notBeforeTime", notBeforeTime);
        XhbScheduledHearingDao dao = query.getResultList().isEmpty() ? null
            : (XhbScheduledHearingDao) query.getSingleResult();
        return dao != null ? Optional.of(dao) : Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public Optional<XhbScheduledHearingDao> findBySittingDateSafe(final Integer sittingId,
        final Integer hearingId, final LocalDateTime notBeforeTime) {

        LOG.debug("findBySittingDateSafe(sittingId: {}, hearingId: {}, notBeforeTime: {})",
            sittingId, hearingId, notBeforeTime);

        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query = em.createNamedQuery("XHB_SCHEDULED_HEARING.findBySittingDate");
            query.setParameter(SITTING_ID, sittingId);
            query.setParameter("hearingId", hearingId);
            query.setParameter("notBeforeTime", notBeforeTime);

            List<?> resultList = query.getResultList();

            if (resultList == null || resultList.isEmpty()) {
                LOG.debug(
                    "findBySittingDateSafe - No results found for sittingId: {}, hearingId: {}, notBeforeTime: {}",
                    sittingId, hearingId, notBeforeTime);
                return Optional.empty();
            }

            Object result = resultList.get(0);
            if (result instanceof XhbScheduledHearingDao) {
                LOG.debug("findBySittingDateSafe - Returning first result");
                return Optional.of((XhbScheduledHearingDao) result);
            } else {
                LOG.warn("findBySittingDateSafe - Unexpected result type: {}",
                    result.getClass().getName());
                return Optional.empty();
            }

        } catch (Exception e) {
            LOG.error("Error in findBySittingDateSafe({}, {}, {}): {}", sittingId, hearingId,
                notBeforeTime, e.getMessage(), e);
            return Optional.empty();
        }
    }

}
