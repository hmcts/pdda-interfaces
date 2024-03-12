package uk.gov.hmcts.pdda.business.entities.xhbrefjudge;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.pdda.business.entities.AbstractRepository;

import java.util.Optional;



@Repository
public class XhbRefJudgeRepository extends AbstractRepository<XhbRefJudgeDao> {

    private static final Logger LOG = LoggerFactory.getLogger(XhbRefJudgeRepository.class);

    public XhbRefJudgeRepository(EntityManager em) {
        super(em);
    }

    @Override
    public Class<XhbRefJudgeDao> getDaoClass() {
        return XhbRefJudgeDao.class;
    }

    /**
     * findScheduledAttendeeJudge.
     * 
     * @param scheduledHearingId Integer
     * @return XhbRefJudgeDao
     */
    public Optional<XhbRefJudgeDao> findScheduledAttendeeJudge(Integer scheduledHearingId) {
        LOG.debug("findScheduledAttendeeJudge({})", scheduledHearingId);
        Query query =
            getEntityManager().createNamedQuery("XHB_REF_JUDGE.findScheduledAttendeeJudge");
        query.setParameter("scheduledHearingId", scheduledHearingId);
        XhbRefJudgeDao dao =
            query.getResultList().isEmpty() ? null : (XhbRefJudgeDao) query.getResultList().get(0);
        return dao != null ? Optional.of(dao) : Optional.empty();
    }

    /**
     * findScheduledSittingJudge.
     * 
     * @param scheduledHearingId Integer
     * @return XhbRefJudgeDao
     */
    public Optional<XhbRefJudgeDao> findScheduledSittingJudge(Integer scheduledHearingId) {
        LOG.debug("findScheduledSittingJudge({})", scheduledHearingId);
        Query query =
            getEntityManager().createNamedQuery("XHB_REF_JUDGE.findScheduledSittingJudge");
        query.setParameter("scheduledHearingId", scheduledHearingId);
        XhbRefJudgeDao dao =
            query.getResultList().isEmpty() ? null : (XhbRefJudgeDao) query.getResultList().get(0);
        return dao != null ? Optional.of(dao) : Optional.empty();
    }
}
