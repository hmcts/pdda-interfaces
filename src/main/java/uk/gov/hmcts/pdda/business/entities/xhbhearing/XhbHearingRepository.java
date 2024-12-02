package uk.gov.hmcts.pdda.business.entities.xhbhearing;

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
@SuppressWarnings({"PMD.LawOfDemeter", "PMD.UseObjectForClearerAPI"})
public class XhbHearingRepository extends AbstractRepository<XhbHearingDao> {

    private static final Logger LOG = LoggerFactory.getLogger(XhbHearingRepository.class);

    public XhbHearingRepository(EntityManager em) {
        super(em);
    }

    @Override
    public Class<XhbHearingDao> getDaoClass() {
        return XhbHearingDao.class;
    }

    /**
     * findByCaseId.
     * 
     * @return list
     */
    @SuppressWarnings("unchecked")
    public List<XhbHearingDao> findByCaseId(Integer caseId) {
        LOG.debug("In XhbHearingRepository.findByCaseId");
        Query query = getEntityManager().createNamedQuery("XHB_HEARING.findByCaseId");
        query.setParameter("caseId", caseId);
        return query.getResultList();
    }

    /**
     * findByCaseIdAndStartDate.
     * 
     * @param courtId Integer
     * @param caseId Integer
     * @param hearingStartDate LocalDateTime
     * @return XhbHearingDao
     */
    public Optional<XhbHearingDao> findByCaseIdAndStartDate(final Integer courtId,
        final Integer caseId, final LocalDateTime hearingStartDate) {
        LOG.debug("findByDefendantAndCase()");
        Query query = getEntityManager().createNamedQuery("XHB_HEARING.findByCaseIdAndStartDate");
        query.setParameter("courtId", courtId);
        query.setParameter("caseId", caseId);
        query.setParameter("hearingStartDate", hearingStartDate);
        XhbHearingDao dao = (XhbHearingDao) query.getSingleResult();
        return dao != null ? Optional.of(dao) : Optional.empty();
    }
}
