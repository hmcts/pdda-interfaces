package uk.gov.hmcts.pdda.business.entities.xhbcase;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.pdda.business.entities.AbstractRepository;

import java.util.Optional;



@Repository
@SuppressWarnings("PMD.LawOfDemeter")
public class XhbCaseRepository extends AbstractRepository<XhbCaseDao> {

    private static final Logger LOG = LoggerFactory.getLogger(XhbCaseRepository.class);

    public XhbCaseRepository(EntityManager em) {
        super(em);
    }

    @Override
    public Class<XhbCaseDao> getDaoClass() {
        return XhbCaseDao.class;
    }

    /**
     * findByNumberTypeAndCourt.
     * 
     * @param courtId Integer
     * @param caseType String
     * @param caseNumber Integer
     * @return XhbCaseDao
     */
    public Optional<XhbCaseDao> findByNumberTypeAndCourt(final Integer courtId,
        final String caseType, final Integer caseNumber) {
        LOG.debug("findByNumberTypeAndCourt({}{})", caseType, caseNumber);
        Query query = getEntityManager().createNamedQuery("XHB_CASE.findByNumberTypeAndCourt");
        query.setParameter("courtId", courtId);
        query.setParameter("caseType", caseType);
        query.setParameter("caseNumber", caseNumber);
        XhbCaseDao dao =
            query.getResultList().isEmpty() ? null : (XhbCaseDao) query.getSingleResult();
        return dao != null ? Optional.of(dao) : Optional.empty();
    }
}
