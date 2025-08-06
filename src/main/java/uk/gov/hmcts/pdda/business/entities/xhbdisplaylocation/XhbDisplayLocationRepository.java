package uk.gov.hmcts.pdda.business.entities.xhbdisplaylocation;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.pdda.business.entities.AbstractRepository;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
@Repository
public class XhbDisplayLocationRepository extends AbstractRepository<XhbDisplayLocationDao> implements Serializable  {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(XhbDisplayLocationRepository.class);

    public XhbDisplayLocationRepository(EntityManager em) {
        super(em);
    }

    @Override
    public Class<XhbDisplayLocationDao> getDaoClass() {
        return XhbDisplayLocationDao.class;
    }

    /**
     * findByVIPCourtSite.

     * @return List
     */
    @SuppressWarnings("unchecked")
    public List<XhbDisplayLocationDao> findByVipCourtSite(Integer courtSiteId) {
        LOG.debug("findByVIPCourtSite()");
        Query query =
            getEntityManager().createNamedQuery("XHB_DISPLAY_LOCATION.findByVIPCourtSite");
        query.setParameter("courtSiteId", courtSiteId);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<XhbDisplayLocationDao> findByVipCourtSiteSafe(Integer courtSiteId) {
        LOG.debug("findByVipCourtSiteSafe(courtSiteId: {})", courtSiteId);

        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query = em.createNamedQuery("XHB_DISPLAY_LOCATION.findByVIPCourtSite");
            query.setParameter("courtSiteId", courtSiteId);

            return query.getResultList();
        } catch (Exception e) {
            LOG.error("Error in findByVipCourtSiteSafe({}): {}", courtSiteId, e.getMessage(), e);
            return List.of(); // Safe fallback
        }
    }


    /**
     * findByCourtSite.

     * @return List
     */
    @SuppressWarnings("unchecked")
    public List<XhbDisplayLocationDao> findByCourtSite(Integer courtSiteId) {
        LOG.debug("findByCourtSite()");
        Query query = getEntityManager().createNamedQuery("XHB_DISPLAY_LOCATION.findByCourtSite");
        query.setParameter("courtSiteId", courtSiteId);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<XhbDisplayLocationDao> findByCourtSiteSafe(Integer courtSiteId) {
        LOG.debug("findByCourtSiteSafe(courtSiteId: {})", courtSiteId);

        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query = em.createNamedQuery("XHB_DISPLAY_LOCATION.findByCourtSite");
            query.setParameter("courtSiteId", courtSiteId);

            return query.getResultList();
        } catch (Exception e) {
            LOG.error("Error in findByCourtSiteSafe({}): {}", courtSiteId, e.getMessage(), e);
            return List.of(); // Safe fallback
        }
    }

}
