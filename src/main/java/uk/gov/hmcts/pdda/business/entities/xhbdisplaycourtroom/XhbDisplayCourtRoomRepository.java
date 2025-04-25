package uk.gov.hmcts.pdda.business.entities.xhbdisplaycourtroom;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.pdda.business.entities.AbstractRepository;

import java.util.List;



@Repository
public class XhbDisplayCourtRoomRepository extends AbstractRepository<XhbDisplayCourtRoomDao> {

    private static final Logger LOG = LoggerFactory.getLogger(XhbDisplayCourtRoomRepository.class);

    public XhbDisplayCourtRoomRepository(EntityManager em) {
        super(em);
    }

    @Override
    public Class<XhbDisplayCourtRoomDao> getDaoClass() {
        return XhbDisplayCourtRoomDao.class;
    }

    /**
     * findByDisplayId.
     * 
     * @return List
     */
    @SuppressWarnings("unchecked")
    public List<XhbDisplayCourtRoomDao> findByDisplayId(Integer displayId) {
        LOG.debug("findByDisplayId()");
        Query query = getEntityManager().createNamedQuery("XHB_DISPLAY_COURT_ROOM.findByDisplayId");
        query.setParameter("displayId", displayId);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<XhbDisplayCourtRoomDao> findByDisplayIdSafe(Integer displayId) {
        LOG.debug("findByDisplayIdSafe(displayId: {})", displayId);

        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query = em.createNamedQuery("XHB_DISPLAY_COURT_ROOM.findByDisplayId");
            query.setParameter("displayId", displayId);

            return query.getResultList();
        } catch (Exception e) {
            LOG.error("Error in findByDisplayIdSafe({}): {}", displayId, e.getMessage(), e);
            return List.of(); // Return empty list to prevent null-related issues
        }
    }

}
