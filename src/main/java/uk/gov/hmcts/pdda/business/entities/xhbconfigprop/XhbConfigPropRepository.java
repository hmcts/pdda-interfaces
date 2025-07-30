package uk.gov.hmcts.pdda.business.entities.xhbconfigprop;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.pdda.business.entities.AbstractRepository;

import java.io.Serializable;
import java.util.List;



@Repository
public class XhbConfigPropRepository extends AbstractRepository<XhbConfigPropDao> implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(XhbConfigPropRepository.class);

    public XhbConfigPropRepository(EntityManager em) {
        super(em);
    }

    @Override
    public Class<XhbConfigPropDao> getDaoClass() {
        return XhbConfigPropDao.class;
    }

    /**
     * findByPropertyName.
     * @param propertyName String
     * @return List
     */
    @SuppressWarnings("unchecked")
    public List<XhbConfigPropDao> findByPropertyName(String propertyName) {
        LOG.debug("findByPropertyName()");
        Query query = getEntityManager().createNamedQuery("XHB_CONFIG_PROP.findByPropertyName");
        query.setParameter("propertyName", propertyName);
        return query.getResultList();
    }


    @SuppressWarnings("unchecked")
    public List<XhbConfigPropDao> findByPropertyNameSafe(String propertyName) {
        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query = em.createNamedQuery("XHB_CONFIG_PROP.findByPropertyName");
            query.setParameter("propertyName", propertyName);
            return query.getResultList();
        }
    }
}
