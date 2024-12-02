package uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.pdda.business.entities.AbstractRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;



@Repository
@SuppressWarnings("PMD.LawOfDemeter")
public class XhbScheduledHearingRepository extends AbstractRepository<XhbScheduledHearingDao> {

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
     * 
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
     * 
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

    /**
     * findBySittingDate.
     * 
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
}
