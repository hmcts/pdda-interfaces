package uk.gov.hmcts.pdda.business.entities.xhbconfiguredpublicnotice;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.pdda.business.entities.AbstractRepository;

import java.util.List;



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
            .createNamedQuery("XHB_CONFIGURED_PUBLIC.findByDefinitivePnCourtRoomValue");
        query.setParameter("courtRoomId", courtRoomId);
        query.setParameter("publicNoticeId", publicNoticeId);
        return query.getResultList();
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
}
