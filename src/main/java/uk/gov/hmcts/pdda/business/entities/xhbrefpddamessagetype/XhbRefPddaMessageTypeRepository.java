package uk.gov.hmcts.pdda.business.entities.xhbrefpddamessagetype;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.framework.jdbc.core.Parameter;
import uk.gov.hmcts.pdda.business.entities.AbstractRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Repository
public class XhbRefPddaMessageTypeRepository extends AbstractRepository<XhbRefPddaMessageTypeDao> {

    private static final Logger LOG =
        LoggerFactory.getLogger(XhbRefPddaMessageTypeRepository.class);
    private static final String PDDA_MESSAGE_TYPE = "pddaMessageType";

    public XhbRefPddaMessageTypeRepository(EntityManager em) {
        super(em);
    }

    @Override
    public Class<XhbRefPddaMessageTypeDao> getDaoClass() {
        return XhbRefPddaMessageTypeDao.class;
    }

    /**
     * findByMessageType.

     * @return List
     */
    @SuppressWarnings("unchecked")
    public List<XhbRefPddaMessageTypeDao> findByMessageType(String pddaMessageType) {
        LOG.debug("findByMessageType");
        Query query =
            getEntityManager().createNamedQuery("XHB_REF_PDDA_MESSAGE_TYPE.findByMessageType");
        query.setParameter(PDDA_MESSAGE_TYPE, Parameter.getPostgresInParameter(pddaMessageType));
        return query.getResultList();
    }

    public List<XhbRefPddaMessageTypeDao> findByMessageTypeSafe(final String pddaMessageType) {
        LOG.debug("findByMessageTypeSafe()");
        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query = em.createNamedQuery("XHB_REF_PDDA_MESSAGE_TYPE.findByMessageType");
            query.setParameter(PDDA_MESSAGE_TYPE,
                Parameter.getPostgresInParameter(pddaMessageType));
            List<?> resultList = query.getResultList();

            List<XhbRefPddaMessageTypeDao> safeResultList = new ArrayList<>();
            for (Object result : resultList) {
                if (result instanceof XhbRefPddaMessageTypeDao) {
                    safeResultList.add((XhbRefPddaMessageTypeDao) result);
                } else {
                    LOG.warn("Unexpected result type in findByMessageTypeSafe: {}",
                        result.getClass().getName());
                }
            }
            return safeResultList;
        } catch (Exception e) {
            LOG.error("Error in findByMessageTypeSafe({}): {}", pddaMessageType, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

}
