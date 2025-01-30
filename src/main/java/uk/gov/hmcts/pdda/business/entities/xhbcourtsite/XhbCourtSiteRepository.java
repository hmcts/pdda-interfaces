package uk.gov.hmcts.pdda.business.entities.xhbcourtsite;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.pdda.business.entities.AbstractRepository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;



@Repository
@SuppressWarnings("PMD.LawOfDemeter")
public class XhbCourtSiteRepository extends AbstractRepository<XhbCourtSiteDao>
    implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(XhbCourtSiteRepository.class);

    public XhbCourtSiteRepository(EntityManager em) {
        super(em);
    }

    @Override
    public Class<XhbCourtSiteDao> getDaoClass() {
        return XhbCourtSiteDao.class;
    }

    /**
     * findByCrestCourtIdValue.
     * 
     * @param crestCourtId String
     * @return List
     */
    @SuppressWarnings("unchecked")
    public List<XhbCourtSiteDao> findByCrestCourtIdValue(String crestCourtId) {
        LOG.debug("findByCrestCourtIdValue()");
        Query query = getEntityManager().createNamedQuery("XHB_COURT_SITE.findByCrestCourtIdValue");
        query.setParameter("crestCourtId", crestCourtId);
        return query.getResultList();
    }

    /**
     * findByCourtSiteName.
     * 
     * @param courtSiteName String
     * @param crestCourtId String
     * @return XhbCourtSiteDao
     */
    public Optional<XhbCourtSiteDao> findByCourtSiteName(final String courtSiteName, final String crestCourtId) {
        LOG.debug("findByCourtSiteName()");
        Query query =
            getEntityManager().createNamedQuery("XHB_COURT_SITE.findByCourtSiteName");
        query.setParameter("courtSiteName", courtSiteName);
        query.setParameter("crestCourtId", crestCourtId);
        XhbCourtSiteDao dao =
            query.getResultList().isEmpty() ? null : (XhbCourtSiteDao) query.getSingleResult();
        return dao != null ? Optional.of(dao) : Optional.empty();
    }

    /**
     * findByCourtId.
     * 
     * @param courtId Integer
     * @return List
     */
    @SuppressWarnings("unchecked")
    public List<XhbCourtSiteDao> findByCourtId(final Integer courtId) {
        LOG.debug("findByCourtId()");
        Query query = getEntityManager().createNamedQuery("XHB_COURT_SITE.findByCourtId");
        query.setParameter("courtId", courtId);
        return query.getResultList();
    }


}
