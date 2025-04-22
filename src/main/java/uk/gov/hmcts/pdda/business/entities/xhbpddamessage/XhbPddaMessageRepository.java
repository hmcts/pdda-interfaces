package uk.gov.hmcts.pdda.business.entities.xhbpddamessage;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.framework.jdbc.core.Parameter;
import uk.gov.hmcts.pdda.business.entities.AbstractRepository;

import java.util.List;


@Repository
@SuppressWarnings("unchecked")
public class XhbPddaMessageRepository extends AbstractRepository<XhbPddaMessageDao> {

    private static final Logger LOG = LoggerFactory.getLogger(XhbPddaMessageRepository.class);
    private static final String CP_DOCUMENT_NAME = "cpDocumentName";

    public XhbPddaMessageRepository(EntityManager em) {
        super(em);
    }

    @Override
    public Class<XhbPddaMessageDao> getDaoClass() {
        return XhbPddaMessageDao.class;
    }

    /**
     * findByCpDocumentName.
     * 
     * @return List
     */
    public List<XhbPddaMessageDao> findByCpDocumentName(String cpDocumentName) {
        LOG.debug("findByCpDocumentName({})", cpDocumentName);
        Query query = getEntityManager().createNamedQuery("XHB_PDDA_MESSAGE.findByCpDocumentName");
        query.setParameter(CP_DOCUMENT_NAME, Parameter.getPostgresInParameter(cpDocumentName));
        return query.getResultList();
    }

    /**
     * findByLighthouse.
     * 
     * @return List
     */
    public List<XhbPddaMessageDao> findByLighthouse() {
        LOG.debug("findByLighthouse()");
        return getEntityManager().createNamedQuery("XHB_PDDA_MESSAGE.findByLighthouse")
            .getResultList();
    }

    public List<XhbPddaMessageDao> findByLighthouseSafe() {
        LOG.debug("findByLighthouseSafe()");
        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            return em.createNamedQuery("XHB_PDDA_MESSAGE.findByLighthouse").getResultList();
        } catch (Exception e) {
            LOG.error("Error in findByLighthouseSafe(): {}", e.getMessage(), e);
            return List.of(); // Defensive: avoids nulls or crashes
        }
    }


    /**
     * findUnrespondedCPMessages.
     * 
     * @return List
     */
    public List<XhbPddaMessageDao> findUnrespondedCpMessages() {
        LOG.debug("findUnrespondedCpMessages()");
        return getEntityManager().createNamedQuery("XHB_PDDA_MESSAGE.findUnrespondedCPMessages")
            .getResultList();
    }
}
