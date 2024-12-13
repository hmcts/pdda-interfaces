package uk.gov.hmcts.pdda.business.entities.xhbcourtroom;

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
public class XhbCourtRoomRepository extends AbstractRepository<XhbCourtRoomDao>
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(XhbCourtRoomRepository.class);
    private static final String UNCHECKED = "unchecked";

    public XhbCourtRoomRepository(EntityManager em) {
        super(em);
    }

    @Override
    public Class<XhbCourtRoomDao> getDaoClass() {
        return XhbCourtRoomDao.class;
    }

    /**
     * findByCourtSiteId.
     * 
     * @param courtSiteId Integer
     * @return List
     */
    @SuppressWarnings(UNCHECKED)
    public List<XhbCourtRoomDao> findByCourtSiteId(Integer courtSiteId) {
        LOG.debug("findByCourtSiteId({})", courtSiteId);
        Query query = getEntityManager().createNamedQuery("XHB_COURT_ROOM.findByCourtSiteId");
        query.setParameter("courtSiteId", courtSiteId);
        return query.getResultList();
    }

    /**
     * findByDisplayId.
     * 
     * @param displayId Integer
     * @return List
     */
    @SuppressWarnings(UNCHECKED)
    public List<XhbCourtRoomDao> findByDisplayId(Integer displayId) {
        LOG.debug("findByDisplayId({})", displayId);
        Query query = getEntityManager().createNamedQuery("XHB_COURT_ROOM.findByDisplayId");
        query.setParameter("displayId", displayId);
        return query.getResultList();
    }

    /**
     * findVIPMultiSite.
     * 
     * @param courtId Integer
     * @return List
     */
    @SuppressWarnings(UNCHECKED)
    public List<XhbCourtRoomDao> findVipMultiSite(Integer courtId) {
        LOG.debug("findVipMultiSite({})", courtId);
        Query query = getEntityManager().createNamedQuery("XHB_COURT_ROOM.findVIPMultiSite");
        query.setParameter("courtId", courtId);
        return query.getResultList();
    }

    /**
     * findVIPMNoSite.
     * 
     * @param courtId Integer
     * @return List
     */
    @SuppressWarnings(UNCHECKED)
    public List<XhbCourtRoomDao> findVipMNoSite(Integer courtId) {
        LOG.debug("findVipMNoSite({})", courtId);
        Query query = getEntityManager().createNamedQuery("XHB_COURT_ROOM.findVIPMNoSite");
        query.setParameter("courtId", courtId);
        return query.getResultList();
    }

    /**
     * findByCourtRoomNo.
     * 
     * @param courtSiteId Integer
     * @param crestCourtRoomNo Integer
     * @return XhbCourtRoomDao
     */
    public Optional<XhbCourtRoomDao> findByCourtRoomNo(Integer courtSiteId,
        Integer crestCourtRoomNo) {
        LOG.debug("findByCourtRoomNo({})", crestCourtRoomNo);
        Query query = getEntityManager().createNamedQuery("XHB_COURT_ROOM.findByCourtRoomNo");
        query.setParameter("courtSiteId", courtSiteId);
        query.setParameter("crestCourtRoomNo", crestCourtRoomNo);
        XhbCourtRoomDao dao =
            query.getResultList().isEmpty() ? null : (XhbCourtRoomDao) query.getSingleResult();
        return dao != null ? Optional.of(dao) : Optional.empty();
    }
}
