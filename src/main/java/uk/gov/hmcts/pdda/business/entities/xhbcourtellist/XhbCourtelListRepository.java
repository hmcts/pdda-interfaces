package uk.gov.hmcts.pdda.business.entities.xhbcourtellist;

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
public class XhbCourtelListRepository extends AbstractRepository<XhbCourtelListDao> implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(XhbCourtelListRepository.class);

    public XhbCourtelListRepository(EntityManager em) {
        super(em);
    }

    @Override
    public Class<XhbCourtelListDao> getDaoClass() {
        return XhbCourtelListDao.class;
    }

    public Optional<XhbCourtelListDao> findByXmlDocumentId(final Integer xmlDocumentId) {
        LOG.debug("findByXmlDocumentId()");
        Query query = getEntityManager().createNamedQuery("XHB_COURTEL_LIST.findByXmlDocumentId");
        query.setParameter("xmlDocumentId", xmlDocumentId);
        @SuppressWarnings("unchecked")
        List<XhbCourtelListDao> resultList = query.getResultList();
        return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.get(0));
    }
}