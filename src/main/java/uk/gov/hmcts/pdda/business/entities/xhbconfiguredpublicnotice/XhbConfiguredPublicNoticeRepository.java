package uk.gov.hmcts.pdda.business.entities.xhbconfiguredpublicnotice;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.pdda.business.entities.AbstractRepository;

import java.util.List;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
@Repository
public class XhbConfiguredPublicNoticeRepository
    extends AbstractRepository<XhbConfiguredPublicNoticeDao> {

    private static final Logger LOG =
        LoggerFactory.getLogger(XhbConfiguredPublicNoticeRepository.class);

    public XhbConfiguredPublicNoticeRepository(EntityManager em) {
        super(em);
    }

    @Override
    public Class<XhbConfiguredPublicNoticeDao> getDaoClass() {
        return XhbConfiguredPublicNoticeDao.class;
    }

    /**
     * findByDefinitivePnCourtRoomValue.
     * @return List
     */
    @SuppressWarnings("unchecked")
    public List<XhbConfiguredPublicNoticeDao> findByDefinitivePnCourtRoomValue(Integer courtRoomId,
        Integer publicNoticeId) {
        LOG.debug("findByDefinitivePnCourtRoomValue({},{})", courtRoomId, publicNoticeId);
        Query query = getEntityManager()
            .createNamedQuery("XHB_CONFIGURED_PUBLIC_NOTICE.findByDefinitivePnCourtRoomValue");
        query.setParameter("courtRoomId", courtRoomId);
        query.setParameter("publicNoticeId", publicNoticeId);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<XhbConfiguredPublicNoticeDao> findByDefinitivePnCourtRoomValueSafe(
        Integer courtRoomId, Integer publicNoticeId) {

        LOG.debug("findByDefinitivePnCourtRoomValueSafe(courtRoomId: {}, publicNoticeId: {})",
            courtRoomId, publicNoticeId);

        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query =
                em.createNamedQuery("XHB_CONFIGURED_PUBLIC_NOTICE.findByDefinitivePnCourtRoomValue");
            query.setParameter("courtRoomId", courtRoomId);
            query.setParameter("publicNoticeId", publicNoticeId);

            return query.getResultList();
        } catch (Exception e) {
            LOG.error("Error in findByDefinitivePnCourtRoomValueSafe({}, {}): {}", courtRoomId,
                publicNoticeId, e.getMessage(), e);
            return List.of(); // Safe fallback
        }
    }


    /**
     * findActiveCourtRoomNotices.
     * @return List
     */
    @SuppressWarnings("unchecked")
    public List<XhbConfiguredPublicNoticeDao> findActiveCourtRoomNotices(Integer courtRoomId) {
        LOG.debug("findActiveCourtRoomNotices({})", courtRoomId);
        Query query = getEntityManager()
            .createNamedQuery("XHB_CONFIGURED_PUBLIC_NOTICE.findActiveCourtRoomNotices");
        query.setParameter("courtRoomId", courtRoomId);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<XhbConfiguredPublicNoticeDao> findActiveCourtRoomNoticesSafe(Integer courtRoomId) {
        LOG.debug("findActiveCourtRoomNoticesSafe(courtRoomId: {})", courtRoomId);

        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query =
                em.createNamedQuery("XHB_CONFIGURED_PUBLIC_NOTICE.findActiveCourtRoomNotices");
            query.setParameter("courtRoomId", courtRoomId);

            return query.getResultList();
        } catch (Exception e) {
            LOG.error("Error in findActiveCourtRoomNoticesSafe({}): {}", courtRoomId,
                e.getMessage(), e);
            return List.of(); // Safe fallback
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<XhbConfiguredPublicNoticeDao> findByCourtRoomIdSafe(Integer courtRoomId) {
        LOG.debug("findByCourtRoomIdSafe(courtRoomId: {})", courtRoomId);

        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query =
                em.createNamedQuery("XHB_CONFIGURED_PUBLIC_NOTICE.findByCourtRoomId");
            query.setParameter("courtRoomId", courtRoomId);
            return query.getResultList();
        } catch (Exception e) {
            LOG.error("Error in findByCourtRoomIdSafe({}): {}", courtRoomId,
                e.getMessage(), e);
            return List.of(); // Safe fallback
        }
    }
}
