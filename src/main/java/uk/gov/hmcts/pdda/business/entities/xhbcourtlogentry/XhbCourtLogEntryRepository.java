package uk.gov.hmcts.pdda.business.entities.xhbcourtlogentry;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.pdda.business.entities.AbstractRepository;

import java.util.List;



@Repository
public class XhbCourtLogEntryRepository extends AbstractRepository<XhbCourtLogEntryDao> {

    private static final Logger LOG = LoggerFactory.getLogger(XhbCourtLogEntryRepository.class);

    public XhbCourtLogEntryRepository(EntityManager em) {
        super(em);
    }

    @Override
    public Class<XhbCourtLogEntryDao> getDaoClass() {
        return XhbCourtLogEntryDao.class;
    }

    /**
     * findByCaseId.
     * 
     * @param caseId Integer
     * @return List
     */
    @SuppressWarnings("unchecked")
    public List<XhbCourtLogEntryDao> findByCaseId(final Integer caseId) {
        LOG.debug("findByCaseId()");
        Query query = getEntityManager().createNamedQuery("XHB_COURT_LOG_ENTRY.findByCaseId");
        query.setParameter("caseId", caseId);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<XhbCourtLogEntryDao> findByCaseIdSafe(final Integer caseId) {
        LOG.debug("findByCaseIdSafe(caseId: {})", caseId);

        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query = em.createNamedQuery("XHB_COURT_LOG_ENTRY.findByCaseId");
            query.setParameter("caseId", caseId);

            return query.getResultList();
        } catch (Exception e) {
            LOG.error("Error in findByCaseIdSafe({}): {}", caseId, e.getMessage(), e);
            return List.of(); // Return an empty list to prevent NPEs
        }
    }

}
