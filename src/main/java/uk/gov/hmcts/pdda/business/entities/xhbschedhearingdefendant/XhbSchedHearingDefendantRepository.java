package uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.pdda.business.entities.AbstractRepository;

import java.util.List;
import java.util.Optional;

@Repository
@SuppressWarnings("PMD.LawOfDemeter")
public class XhbSchedHearingDefendantRepository
    extends AbstractRepository<XhbSchedHearingDefendantDao> {

    private static final Logger LOG =
        LoggerFactory.getLogger(XhbSchedHearingDefendantRepository.class);

    public XhbSchedHearingDefendantRepository(EntityManager em) {
        super(em);
    }

    @Override
    public Class<XhbSchedHearingDefendantDao> getDaoClass() {
        return XhbSchedHearingDefendantDao.class;
    }

    /**
     * findByScheduledHearingId.
     * 
     * @param scheduledHearingId INteger
     * @return List
     */
    @SuppressWarnings("unchecked")
    public List<XhbSchedHearingDefendantDao> findByScheduledHearingId(Integer scheduledHearingId) {
        LOG.debug("findByScheduledHearingId()");
        Query query = getEntityManager()
            .createNamedQuery("XHB_SCHED_HEARING_DEFENDANT.findByScheduledHearingId");
        query.setParameter("scheduledHearingId", scheduledHearingId);
        return query.getResultList();
    }

    /**
     * findByHearingAndDefendant.
     * 
     * @return XhbSchedHearingDefendantDao
     */
    public Optional<XhbSchedHearingDefendantDao> findByHearingAndDefendant(
        final Integer scheduledHearingId, final Integer defendantOnCaseId) {
        LOG.debug("findByHearingAndDefendant()");
        Query query = getEntityManager()
            .createNamedQuery("XHB_SCHED_HEARING_DEFENDANT.findByHearingAndDefendant");
        query.setParameter("scheduledHearingId", scheduledHearingId);
        query.setParameter("defendantOnCaseId", defendantOnCaseId);
        XhbSchedHearingDefendantDao dao = query.getResultList().isEmpty() ? null
            : (XhbSchedHearingDefendantDao) query.getSingleResult();
        return dao != null ? Optional.of(dao) : Optional.empty();
    }
}
