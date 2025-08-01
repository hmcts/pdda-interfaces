package uk.gov.hmcts.pdda.business.entities.xhbrefhearingtype;

import com.pdda.hb.jpa.EntityManagerUtil;
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
@SuppressWarnings({"PMD.LawOfDemeter"})
public class XhbRefHearingTypeRepository extends AbstractRepository<XhbRefHearingTypeDao>
    implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(XhbRefHearingTypeRepository.class);

    public XhbRefHearingTypeRepository(EntityManager em) {
        super(em);
    }

    @Override
    public Class<XhbRefHearingTypeDao> getDaoClass() {
        return XhbRefHearingTypeDao.class;
    }

    /**
     * findByHearingType.

     * @return XhbRefHearingTypeDao
     */
    public Optional<XhbRefHearingTypeDao> findByHearingType(Integer courtId, String hearingTypeCode,
        String hearingTypeDesc, String category) {
        LOG.debug("findByHearingType()");
        Query query = getEntityManager().createNamedQuery("XHB_REF_HEARING_TYPE.findByHearingType");
        query.setParameter("courtId", courtId);
        query.setParameter("hearingTypeCode", hearingTypeCode);
        query.setParameter("category", category);
        XhbRefHearingTypeDao dao =
            query.getResultList().isEmpty() ? null : (XhbRefHearingTypeDao) query.getSingleResult();
        return dao != null ? Optional.of(dao) : Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public Optional<XhbRefHearingTypeDao> findByHearingTypeSafe(Integer courtId,
        String hearingTypeCode, String hearingTypeDesc, String category) {

        LOG.debug(
            "findByHearingTypeSafe(courtId: {}, hearingTypeCode: {}, hearingTypeDesc: {}, category: {})",
            courtId, hearingTypeCode, hearingTypeDesc, category);

        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query = em.createNamedQuery("XHB_REF_HEARING_TYPE.findByHearingType");
            query.setParameter("courtId", courtId);
            query.setParameter("hearingTypeCode", hearingTypeCode);
            query.setParameter("category", category);

            List<?> resultList = query.getResultList();

            if (resultList == null || resultList.isEmpty()) {
                LOG.debug(
                    "findByHearingTypeSafe - No results found for courtId: {}, hearingTypeCode: {}, category: {}",
                    courtId, hearingTypeCode, category);
                return Optional.empty();
            }

            Object result = resultList.get(0);
            if (result instanceof XhbRefHearingTypeDao) {
                LOG.debug(
                    "findByHearingTypeSafe - Returning result for courtId: {}, hearingTypeCode: {}, category: {}",
                    courtId, hearingTypeCode, category);
                return Optional.of((XhbRefHearingTypeDao) result);
            } else {
                LOG.warn("findByHearingTypeSafe - Unexpected result type: {}",
                    result.getClass().getName());
                return Optional.empty();
            }

        } catch (Exception e) {
            LOG.error("Error in findByHearingTypeSafe({}, {}, {}, {}): {}", courtId,
                hearingTypeCode, hearingTypeDesc, category, e.getMessage(), e);
            return Optional.empty();
        }
    }

}
