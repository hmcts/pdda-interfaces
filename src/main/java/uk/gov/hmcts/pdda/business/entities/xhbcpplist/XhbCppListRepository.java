package uk.gov.hmcts.pdda.business.entities.xhbcpplist;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.pdda.business.entities.AbstractRepository;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;



@Repository
public class XhbCppListRepository extends AbstractRepository<XhbCppListDao>
    implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(XhbCppListRepository.class);

    public XhbCppListRepository(EntityManager em) {
        super(em);
    }

    @Override
    public Class<XhbCppListDao> getDaoClass() {
        return XhbCppListDao.class;
    }

    /**
     * findByCourtCodeAndListTypeAndListDate.
     * 
     * @param courtCode Integer
     * @param listType String
     * @param listStartDate LocalDateTime
     * @return List
     */
    @SuppressWarnings("unchecked")
    public List<XhbCppListDao> findByCourtCodeAndListTypeAndListDate(final Integer courtCode,
        final String listType, final LocalDateTime listStartDate) {
        LOG.debug("findByCourtCodeAndListTypeAndListDate({},{},{})", courtCode, listType, listStartDate);
        Query query = getEntityManager()
            .createNamedQuery("XHB_CPP_LIST.findByCourtCodeAndListTypeAndListDate");
        query.setParameter("courtCode", courtCode);
        query.setParameter("listType", listType);
        query.setParameter("listStartDate", listStartDate);
        return query.getResultList();
    }

    /**
     * findByCourtCodeAndListTypeAndListStartDateAndListEndDate.
     * 
     * @param courtCode Integer
     * @param listType String
     * @param listStartDate LocalDateTime
     * @param listEndDate LocalDateTime
     * @return List
     */
    @SuppressWarnings("unchecked")
    public List<XhbCppListDao> findByCourtCodeAndListTypeAndListStartDateAndListEndDate(
        final Integer courtCode, final String listType, final LocalDateTime listStartDate,
        final LocalDateTime listEndDate) {
        LOG.debug("findByCourtCodeAndListTypeAndListStartDateAndListEndDate({},{},{},{})",
            courtCode, listType, listStartDate, listEndDate);
        Query query = getEntityManager().createNamedQuery(
            "XHB_CPP_LIST.findByCourtCodeAndListTypeAndListStartDateAndListEndDate");
        query.setParameter("courtCode", courtCode);
        query.setParameter("listType", listType);
        query.setParameter("listStartDate", listStartDate);
        query.setParameter("listEndDate", listEndDate);
        return query.getResultList();
    }

    /**
     * findByClobId.
     * 
     * @param listClobId Long
     * @return XhbCppListDao
     */
    @SuppressWarnings("unchecked")
    public XhbCppListDao findByClobId(final Long listClobId) {
        LOG.debug("findByClobId({})", listClobId);
        Query query = getEntityManager().createNamedQuery("XHB_CPP_LIST.findByClobId");
        query.setParameter("listClobId", listClobId);
        List<XhbCppListDao> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
}
