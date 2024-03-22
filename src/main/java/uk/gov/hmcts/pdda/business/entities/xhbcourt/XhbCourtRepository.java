package uk.gov.hmcts.pdda.business.entities.xhbcourt;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.pdda.business.entities.AbstractRepository;

import java.io.Serializable;
import java.util.List;


@SuppressWarnings("unchecked")
@Repository
public class XhbCourtRepository extends AbstractRepository<XhbCourtDao> implements Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(XhbCourtRepository.class);
    
    private static final long serialVersionUID = 1L;

    public XhbCourtRepository(EntityManager em) {
        super(em);
    }

    @Override
    public Class<XhbCourtDao> getDaoClass() {
        return XhbCourtDao.class;
    }

    /**
     * findByCrestCourtIdValue.
     * @param crestCourtId String
     * @return List
     */
    public List<XhbCourtDao> findByCrestCourtIdValue(String crestCourtId) {
        LOG.debug("findByCrestCourtIdValue({})", crestCourtId);
        Query query = getEntityManager().createNamedQuery("XHB_COURT.findByCrestCourtIdValue");
        query.setParameter("crestCourtId", crestCourtId);
        return query.getResultList();
    }

    /**
     * findNonObsoleteByCrestCourtIdValue.
     * @param crestCourtId String
     * @return List
     */
    public List<XhbCourtDao> findNonObsoleteByCrestCourtIdValue(String crestCourtId) {
        LOG.debug("findNonObsoleteByCrestCourtIdValue({})", crestCourtId);
        Query query =
            getEntityManager().createNamedQuery("XHB_COURT.findNonObsoleteByCrestCourtIdValue");
        query.setParameter("crestCourtId", crestCourtId);
        return query.getResultList();
    }

    /**
     * findByShortNameValue.
     * @param shortName String
     * @return List
     */
    public List<XhbCourtDao> findByShortNameValue(String shortName) {
        LOG.debug("findByShortNameValue({})", shortName);
        Query query = getEntityManager().createNamedQuery("XHB_COURT.findByShortNameValue");
        query.setParameter("shortName", shortName);
        return query.getResultList();
    }

    /**
     * findNonObsoleteByShortNameValue.
     * @param shortName String
     * @return List
     */
    public List<XhbCourtDao> findNonObsoleteByShortNameValue(String shortName) {
        LOG.debug("findNonObsoleteByShortNameValue({})", shortName);
        Query query =
            getEntityManager().createNamedQuery("XHB_COURT.findNonObsoleteByShortNameValue");
        query.setParameter("shortName", shortName);
        return query.getResultList();
    }
}
