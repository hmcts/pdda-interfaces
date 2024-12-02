package uk.gov.hmcts.pdda.business.entities.xhbcrlivedisplay;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.pdda.business.entities.AbstractRepository;

import java.util.List;
import java.util.Optional;



@Repository
@SuppressWarnings("PMD.LawOfDemeter")
public class XhbCrLiveDisplayRepository extends AbstractRepository<XhbCrLiveDisplayDao> {

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
     * 
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
     * findByHearing.
     * 
     * @param courtRoomId Integer
     * @param scheduledHearingId Integer
     * @return XhbCrLiveDisplayDao
     */
    public Optional<XhbCrLiveDisplayDao> findByHearing(final Integer courtRoomId,
        final Integer scheduledHearingId) {
        LOG.debug("findByHearing()");
        Query query = getEntityManager().createNamedQuery("XHB_CR_LIVE_DISPLAY.findByHearing");
        query.setParameter("courtRoomId", courtRoomId);
        query.setParameter("scheduledHearingId", scheduledHearingId);
        XhbCrLiveDisplayDao dao = (XhbCrLiveDisplayDao) query.getSingleResult();
        return dao != null ? Optional.of(dao) : Optional.empty();
    }
}
