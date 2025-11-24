package uk.gov.hmcts.pdda.business.entities.xhbpublicnotice;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.pdda.business.entities.AbstractRepository;

import java.util.Optional;

@Repository
@SuppressWarnings("PMD")
public class XhbPublicNoticeRepository extends AbstractRepository<XhbPublicNoticeDao> {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(XhbPublicNoticeRepository.class);

    public XhbPublicNoticeRepository(EntityManager em) {
        super(em);
    }

    @Override
    public Class<XhbPublicNoticeDao> getDaoClass() {
        return XhbPublicNoticeDao.class;
    }
    
    public Optional<XhbPublicNoticeDao> findByCourtIdAndDefPublicNoticeId(final Integer courtId,
        final Integer definitivePnId) {
        LOG.debug("findByCourtIdAndDefPublicNoticeId({},{})", courtId, definitivePnId);
        Query query = getEntityManager()
            .createNamedQuery("XHB_PUBLIC_NOTICE.findByCourtIdAndDefPublicNoticeId");
        query.setParameter("courtId", courtId);
        query.setParameter("definitivePnId", definitivePnId);
        XhbPublicNoticeDao dao =
            query.getResultList().isEmpty() ? null : (XhbPublicNoticeDao) query.getSingleResult();
        return dao != null ? Optional.of(dao) : Optional.empty();
    }
    
    public Optional<XhbPublicNoticeDao> findByCourtIdAndDefPublicNoticeIdSafe(final Integer courtId,
        final Integer definitivePnId) {
        LOG.debug("findByCourtIdAndDefPublicNoticeIdSafe({},{})", courtId, definitivePnId);
        
        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query = em.createNamedQuery("XHB_PUBLIC_NOTICE.findByCourtIdAndDefPublicNoticeId");
            query.setParameter("courtId", courtId);
            query.setParameter("definitivePnId", definitivePnId);
            XhbPublicNoticeDao dao =
                query.getResultList().isEmpty() ? null : (XhbPublicNoticeDao) query.getSingleResult();
            return dao != null ? Optional.of(dao) : Optional.empty();
        } catch (Exception e) {
            LOG.error("Error in findByCourtIdAndDefPublicNoticeIdSafe({} {}): {}",
                courtId, definitivePnId, e.getMessage(),
                e);
            return Optional.empty(); // Return empty
        }
    }
}
