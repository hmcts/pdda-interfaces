package uk.gov.hmcts.pdda.business.entities.xhbdisplaystore;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.pdda.business.entities.AbstractRepository;

import java.util.List;
import java.util.Optional;



@Repository
public class XhbDisplayStoreRepository extends AbstractRepository<XhbDisplayStoreDao> {

    private static final Logger LOG = LoggerFactory.getLogger(XhbDisplayStoreRepository.class);

    public XhbDisplayStoreRepository(EntityManager em) {
        super(em);
    }

    @Override
    public Class<XhbDisplayStoreDao> getDaoClass() {
        return XhbDisplayStoreDao.class;
    }

    /**
     * findByRetrievalCode.
     * 
     * @param retrievalCode String
     * @return XhbDisplayStoreDao
     */
    public Optional<XhbDisplayStoreDao> findByRetrievalCode(final String retrievalCode) {
        LOG.debug("findByRetrievalCode()");
        Query query = getEntityManager().createNamedQuery("XHB_DISPLAY_STORE.findByRetrievalCode");
        query.setParameter("retrievalCode", retrievalCode);
        XhbDisplayStoreDao xds =
            query.getResultList().isEmpty() ? null : (XhbDisplayStoreDao) query.getResultList().get(0);
        return xds == null ? Optional.empty() : Optional.of(xds);
    }


    public Optional<XhbDisplayStoreDao> findByRetrievalCodeSafe(final String retrievalCode) {
        LOG.debug("findByRetrievalCodeSafe()");
        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query = em.createNamedQuery("XHB_DISPLAY_STORE.findByRetrievalCode");
            query.setParameter("retrievalCode", retrievalCode);
            List<?> resultList = query.getResultList();
            if (resultList.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of((XhbDisplayStoreDao) resultList.get(0));
        } catch (Exception e) {
            LOG.error("Error in findByRetrievalCodeSafe({}): {}", retrievalCode, e.getMessage(), e);
            return Optional.empty();
        }
    }

}
