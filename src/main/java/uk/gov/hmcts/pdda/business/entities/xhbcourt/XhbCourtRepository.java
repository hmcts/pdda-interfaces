package uk.gov.hmcts.pdda.business.entities.xhbcourt;

import com.pdda.hb.jpa.EntityManagerUtil;
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

    @SuppressWarnings("unchecked")
    public List<XhbCourtDao> findByCrestCourtIdValueSafe(String crestCourtId) {
        LOG.debug("findByCrestCourtIdValueSafe({})", crestCourtId);

        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query = em.createNamedQuery("XHB_COURT.findByCrestCourtIdValue");
            query.setParameter("crestCourtId", crestCourtId);

            return query.getResultList();
        } catch (Exception e) {
            LOG.error("Error in findByCrestCourtIdValueSafe({}): {}", crestCourtId, e.getMessage(),
                e);
            return List.of(); // Safe fallback to avoid nulls and ensure predictable behaviour
        }
    }

    /**
     * Overridden findAll method to return all XhbCourtDao objects that are not marked as obsolete.
     * @return List of XhbCourtDao objects.
     */
    @Override
    public List<XhbCourtDao> findAllSafe() {
        try (EntityManager em = createEntityManager()) {
            String hql = "FROM " + getDaoClass().getName() + " e WHERE e.obsInd IS NULL OR e.obsInd <> 'Y'";
            Query query = em.createQuery(hql);
            return query.getResultList();
        }
    }
}
