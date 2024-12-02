package uk.gov.hmcts.pdda.business.entities.xhbsitting;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.pdda.business.entities.AbstractRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;



@Repository
@SuppressWarnings("PMD.LawOfDemeter")
public class XhbSittingRepository extends AbstractRepository<XhbSittingDao> {

    private static final Logger LOG = LoggerFactory.getLogger(XhbSittingRepository.class);

    public XhbSittingRepository(EntityManager em) {
        super(em);
    }

    @Override
    public Class<XhbSittingDao> getDaoClass() {
        return XhbSittingDao.class;
    }

    /**
     * findByNonFloatingHearingList.
     * 
     * @param listId Integer
     * @return List
     */
    @SuppressWarnings("unchecked")
    public List<XhbSittingDao> findByNonFloatingHearingList(Integer listId) {
        LOG.debug("In XhbSittingRepository.findByNonFloatingHearingList");
        Query query =
            getEntityManager().createNamedQuery("XHB_SITTING.findByNonFloatingHearingList");
        query.setParameter("listId", listId);
        return query.getResultList();
    }

    /**
     * findByListId.
     * 
     * @param listId Integer
     * @return List
     */
    @SuppressWarnings("unchecked")
    public List<XhbSittingDao> findByListId(Integer listId) {
        LOG.debug("In XhbSittingRepository.findByListId");
        Query query = getEntityManager().createNamedQuery("XHB_SITTING.findByListId");
        query.setParameter("listId", listId);
        return query.getResultList();
    }

    /**
     * findByCourtRoomAndSittingTime.
     * 
     * @param courtSiteId Integer
     * @param courtRoomId Integer
     * @param sittingTime LocalDateTime
     * @return XhbSittingDao
     */
    public Optional<XhbSittingDao> findByCourtRoomAndSittingTime(Integer courtSiteId,
        Integer courtRoomId, LocalDateTime sittingTime) {
        LOG.debug("In XhbSittingRepository.findByListId");
        Query query = getEntityManager().createNamedQuery("XHB_SITTING.findByListId");
        query.setParameter("courtSiteId", courtSiteId);
        query.setParameter("courtRoomId", courtRoomId);
        query.setParameter("sittingTime", sittingTime);
        XhbSittingDao dao =
            query.getResultList().isEmpty() ? null : (XhbSittingDao) query.getSingleResult();
        return dao != null ? Optional.of(dao) : Optional.empty();
    }
}
