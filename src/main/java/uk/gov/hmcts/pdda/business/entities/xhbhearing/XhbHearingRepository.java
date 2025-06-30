package uk.gov.hmcts.pdda.business.entities.xhbhearing;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.pdda.business.entities.AbstractRepository;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@SuppressWarnings({"PMD"})
public class XhbHearingRepository extends AbstractRepository<XhbHearingDao>
    implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(XhbHearingRepository.class);

    public XhbHearingRepository(EntityManager em) {
        super(em);
    }

    @Override
    public Class<XhbHearingDao> getDaoClass() {
        return XhbHearingDao.class;
    }

    /**
     * findByCaseId.
     * 
     * @return list
     */
    @SuppressWarnings("unchecked")
    public List<XhbHearingDao> findByCaseId(Integer caseId) {
        LOG.debug("In XhbHearingRepository.findByCaseId");
        Query query = getEntityManager().createNamedQuery("XHB_HEARING.findByCaseId");
        query.setParameter("caseId", caseId);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<XhbHearingDao> findByCaseIdSafe(Integer caseId) {
        LOG.debug("findByCaseIdSafe(caseId: {})", caseId);

        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query = em.createNamedQuery("XHB_HEARING.findByCaseId");
            query.setParameter("caseId", caseId);

            return query.getResultList(); // Safe as-is when wrapped in try-catch
        } catch (Exception e) {
            LOG.error("Error in findByCaseIdSafe({}): {}", caseId, e.getMessage(), e);
            return List.of(); // Return empty list instead of null to avoid NPE
        }
    }


    /**
     * findByCaseIdAndStartDate.
     * 
     * @param courtId Integer
     * @param caseId Integer
     * @param hearingStartDate LocalDateTime
     * @return XhbHearingDao
     */
    public Optional<XhbHearingDao> findByCaseIdAndStartDate(final Integer courtId,
        final Integer caseId, final LocalDateTime hearingStartDate) {
        LOG.debug("findByDefendantAndCase()");
        Query query = getEntityManager().createNamedQuery("XHB_HEARING.findByCaseIdAndStartDate");
        query.setParameter("courtId", courtId);
        query.setParameter("caseId", caseId);
        query.setParameter("hearingStartDate", hearingStartDate);
        XhbHearingDao dao =
            query.getResultList().isEmpty() ? null : (XhbHearingDao) query.getSingleResult();
        return dao != null ? Optional.of(dao) : Optional.empty();
    }

    public Optional<XhbHearingDao> findByCaseIdAndStartDateSafe(final Integer courtId,
        final Integer caseId, final LocalDateTime hearingStartDate) {

        LOG.debug("findByCaseIdAndStartDateSafe(courtId: {}, caseId: {}, hearingStartDate: {})",
            courtId, caseId, hearingStartDate);

        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query = em.createNamedQuery("XHB_HEARING.findByCaseIdAndStartDate");
            query.setParameter("courtId", courtId);
            query.setParameter("caseId", caseId);
            query.setParameter("hearingStartDate", hearingStartDate);

            List<?> resultList = query.getResultList();

            if (resultList == null || resultList.isEmpty()) {
                LOG.debug(
                    "findByCaseIdAndStartDateSafe - No results found for courtId: {}, caseId: {}, hearingStartDate: {}",
                    courtId, caseId, hearingStartDate);
                return Optional.empty();
            }

            Object result = resultList.get(0);
            if (result instanceof XhbHearingDao) {
                LOG.debug("findByCaseIdAndStartDateSafe - Returning result for caseId: {}", caseId);
                return Optional.of((XhbHearingDao) result);
            } else {
                LOG.warn("findByCaseIdAndStartDateSafe - Unexpected result type: {}",
                    result.getClass().getName());
                return Optional.empty();
            }

        } catch (Exception e) {
            LOG.error("Error in findByCaseIdAndStartDateSafe({}, {}, {}): {}", courtId, caseId,
                hearingStartDate, e.getMessage(), e);
            return Optional.empty();
        }
    }

}
