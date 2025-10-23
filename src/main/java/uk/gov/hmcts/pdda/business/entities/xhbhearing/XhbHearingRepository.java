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
    
    private static final String HEARING_START_DATE = "hearingStartDate";
    private static final String CASE_ID = "caseId";
    

    public XhbHearingRepository(EntityManager em) {
        super(em);
    }

    @Override
    public Class<XhbHearingDao> getDaoClass() {
        return XhbHearingDao.class;
    }

    /**
     * findByCaseId.

     * @return list
     */
    @SuppressWarnings("unchecked")
    public List<XhbHearingDao> findByCaseId(Integer caseId) {
        LOG.debug("In XhbHearingRepository.findByCaseId");
        Query query = getEntityManager().createNamedQuery("XHB_HEARING.findByCaseId");
        query.setParameter(CASE_ID, caseId);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<XhbHearingDao> findByCaseIdSafe(Integer caseId) {
        LOG.debug("findByCaseIdSafe(caseId: {})", caseId);

        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query = em.createNamedQuery("XHB_HEARING.findByCaseId");
            query.setParameter(CASE_ID, caseId);

            return query.getResultList(); // Safe as-is when wrapped in try-catch
        } catch (Exception e) {
            LOG.error("Error in findByCaseIdSafe({}): {}", caseId, e.getMessage(), e);
            return List.of(); // Return empty list instead of null to avoid NPE
        }
    }


    /**
     * findByCaseIdAndStartDate.

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
        query.setParameter(CASE_ID, caseId);
        query.setParameter(HEARING_START_DATE, hearingStartDate);
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
            query.setParameter(CASE_ID, caseId);
            query.setParameter(HEARING_START_DATE, hearingStartDate);

            List<?> resultList = query.getResultList();

            if (resultList == null || resultList.isEmpty()) {
                LOG.debug(
                    "findByCaseIdAndStartDateSafe - No results found for courtId: {}, caseId: {}, hearingStartDate: {}",
                    courtId, caseId, hearingStartDate);
                return Optional.empty();
            }

            Object result = resultList.get(0);
            if (result instanceof XhbHearingDao xhbHearingDao) {
                LOG.debug("findByCaseIdAndStartDateSafe - Returning result for caseId: {}", caseId);
                return Optional.of(xhbHearingDao);
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
    
    /**
     * findByCaseIdWithTodaysStartDateSafe.

     * @param caseId Integer
     * @param hearingStartDate LocalDateTime
     * @return XhbHearingDao
     */
    @SuppressWarnings("unchecked")
    public Optional<XhbHearingDao> findByCaseIdWithTodaysStartDateSafe(Integer caseId, LocalDateTime hearingStartDate) {
        LOG.debug("findByCaseIdWithTodaysStartDateSafe({}, {})", caseId, hearingStartDate);
        // Get tomorrow's date by adding one day to today's date
        LocalDateTime hearingDateTomorrow = hearingStartDate.plusDays(1);
        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query = em.createNamedQuery("XHB_HEARING.findByCaseIdWithTodaysStartDateSafe");
            query.setParameter(CASE_ID, caseId);
            query.setParameter(HEARING_START_DATE, hearingStartDate);
            query.setParameter("hearingDateTomorrow", hearingDateTomorrow);

            List<?> resultList = query.getResultList();

            if (resultList == null || resultList.isEmpty()) {
                LOG.debug(
                    "findByCaseIdWithTodaysStartDateSafe - No results found for caseId: {}, hearingStartDate: {}",
                    caseId, hearingStartDate);
                return Optional.empty();
            }

            Object result = resultList.get(0);
            if (result instanceof XhbHearingDao xhbHearingDao) {
                LOG.debug("findByCaseIdWithTodaysStartDateSafe - Returning result for caseId: {} hearingStartDate: {}",
                    caseId, hearingStartDate);
                return Optional.of(xhbHearingDao);
            } else {
                LOG.warn("findByCaseIdWithTodaysStartDateSafe - Unexpected result type: {}",
                    result.getClass().getName());
                return Optional.empty();
            }

        } catch (Exception e) {
            LOG.error("Error in findByCaseIdWithTodaysStartDateSafe({}, {}): {}", caseId,
                hearingStartDate, e.getMessage(), e);
            return Optional.empty();
        }
    }

}
