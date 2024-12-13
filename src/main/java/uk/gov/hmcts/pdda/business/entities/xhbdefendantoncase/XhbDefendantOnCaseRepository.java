package uk.gov.hmcts.pdda.business.entities.xhbdefendantoncase;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.pdda.business.entities.AbstractRepository;

import java.io.Serializable;
import java.util.Optional;



@Repository
@SuppressWarnings("PMD.LawOfDemeter")
public class XhbDefendantOnCaseRepository extends AbstractRepository<XhbDefendantOnCaseDao>
    implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(XhbDefendantOnCaseRepository.class);

    public XhbDefendantOnCaseRepository(EntityManager em) {
        super(em);
    }

    @Override
    public Class<XhbDefendantOnCaseDao> getDaoClass() {
        return XhbDefendantOnCaseDao.class;
    }

    /**
     * findByDefendantAndCase.
     * 
     * @param caseId Integer
     * @param defendantId Integer
     * @return XhbDefendantOnCaseDao
     */
    public Optional<XhbDefendantOnCaseDao> findByDefendantAndCase(final Integer caseId,
        final Integer defendantId) {
        LOG.debug("findByDefendantAndCase()");
        Query query =
            getEntityManager().createNamedQuery("XHB_DEFENDANT_ON_CASE.findByDefendantAndCase");
        query.setParameter("caseId", caseId);
        query.setParameter("defendantId", defendantId);
        XhbDefendantOnCaseDao dao = query.getResultList().isEmpty() ? null
            : (XhbDefendantOnCaseDao) query.getSingleResult();
        return dao != null ? Optional.of(dao) : Optional.empty();
    }
}
