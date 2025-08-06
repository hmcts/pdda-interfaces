package uk.gov.hmcts.pdda.business.entities.xhbdisplay;

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
public class XhbDisplayRepository extends AbstractRepository<XhbDisplayDao> implements Serializable  {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(XhbDisplayRepository.class);

    public XhbDisplayRepository(EntityManager em) {
        super(em);
    }

    @Override
    public Class<XhbDisplayDao> getDaoClass() {
        return XhbDisplayDao.class;
    }

    /**
     * findByRotationSetId.

     * @param rotationSetId Integer
     * @return List
     */
    @SuppressWarnings("unchecked")
    public List<XhbDisplayDao> findByRotationSetId(Integer rotationSetId) {
        LOG.debug("findByRotationSetId()");
        Query query = getEntityManager().createNamedQuery("XHB_DISPLAY.findByRotationSetId");
        query.setParameter("rotationSetId", rotationSetId);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<XhbDisplayDao> findByRotationSetIdSafe(Integer rotationSetId) {
        LOG.debug("findByRotationSetIdSafe()");
        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query = em.createNamedQuery("XHB_DISPLAY.findByRotationSetId");
            query.setParameter("rotationSetId", rotationSetId);
            return query.getResultList();
        } catch (Exception e) {
            LOG.error("Error in findByRotationSetIdSafe({}): {}", rotationSetId, e.getMessage(), e);
            return List.of(); // Defensive empty list to avoid nulls or leaks
        }
    }


    /**
     * findByDisplayLocationId.

     * @param displayLocationId Integer
     * @return List
     */
    @SuppressWarnings("unchecked")
    public List<XhbDisplayDao> findByDisplayLocationId(Integer displayLocationId) {
        LOG.debug("findByDisplayLocationId()");
        Query query = getEntityManager().createNamedQuery("XHB_DISPLAY.findByDisplayLocationId");
        query.setParameter("displayLocationId", displayLocationId);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<XhbDisplayDao> findByDisplayLocationIdSafe(Integer displayLocationId) {
        LOG.debug("findByDisplayLocationIdSafe(displayLocationId: {})", displayLocationId);

        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query = em.createNamedQuery("XHB_DISPLAY.findByDisplayLocationId");
            query.setParameter("displayLocationId", displayLocationId);

            return query.getResultList();
        } catch (Exception e) {
            LOG.error("Error in findByDisplayLocationIdSafe({}): {}", displayLocationId,
                e.getMessage(), e);
            return List.of(); // Safe fallback to avoid nulls
        }
    }

}
