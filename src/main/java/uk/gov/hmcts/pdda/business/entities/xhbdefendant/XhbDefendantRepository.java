package uk.gov.hmcts.pdda.business.entities.xhbdefendant;

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
public class XhbDefendantRepository extends AbstractRepository<XhbDefendantDao>
    implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(XhbDefendantRepository.class);
    private static final String NO = "N";
    private static final String YES = "Y";
    
    public XhbDefendantRepository(EntityManager em) {
        super(em);
    }

    @Override
    public Class<XhbDefendantDao> getDaoClass() {
        return XhbDefendantDao.class;
    }

    /**
     * findByDefendantName.
     * 
     * @param firstName String
     * @param middleName String
     * @param surname String
     * @param gender Integer
     * @param dateOfBirth LocalDateTime
     * @return XhbDefendantDao
     */
    public Optional<XhbDefendantDao> findByDefendantName(final Integer courtId,
        final String firstName, final String middleName, final String surname, final Integer gender,
        final LocalDateTime dateOfBirth) {
        LOG.debug("findByDefendantAndCase()");
        Query query = getEntityManager().createNamedQuery("XHB_DEFENDANT.findByDefendantName");
        query.setParameter("courtId", courtId);
        query.setParameter("firstName", firstName);
        query.setParameter("middleNameIsNull", middleName == null ? YES : NO);
        query.setParameter("middleName", middleName);
        query.setParameter("surname", surname);
        query.setParameter("genderIsNull", gender == null ? YES : NO);
        query.setParameter("gender", gender);
        query.setParameter("dateOfBirthIsNull", dateOfBirth == null ? YES : NO);
        query.setParameter("dateOfBirth", dateOfBirth);
        XhbDefendantDao dao =
            query.getResultList().isEmpty() ? null : (XhbDefendantDao) query.getSingleResult();
        return dao != null ? Optional.of(dao) : Optional.empty();
    }

    public Optional<XhbDefendantDao> findByDefendantNameSafe(final Integer courtId,
        final String firstName, final String middleName, final String surname, final Integer gender,
        final LocalDateTime dateOfBirth) {

        LOG.debug(
            "findByDefendantNameSafe(courtId: {}, firstName: {}, middleName: {}, surname: {},"
                + " gender: {}, dateOfBirth: {})",
            courtId, firstName, middleName, surname, gender, dateOfBirth);

        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            Query query = em.createNamedQuery("XHB_DEFENDANT.findByDefendantName");
            query.setParameter("courtId", courtId);
            query.setParameter("firstName", firstName);
            query.setParameter("middleNameIsNull", middleName == null ? YES : NO);
            query.setParameter("middleName", middleName);
            query.setParameter("surname", surname);
            query.setParameter("genderIsNull", gender == null ? YES : NO);
            query.setParameter("gender", gender);
            query.setParameter("dateOfBirthIsNull", dateOfBirth == null ? YES : NO);
            query.setParameter("dateOfBirth", dateOfBirth);

            List<?> resultList = query.getResultList();

            if (resultList == null || resultList.isEmpty()) {
                LOG.debug("findByDefendantNameSafe - No results found for given parameters");
                return Optional.empty();
            }

            Object result = resultList.get(0);
            if (result instanceof XhbDefendantDao) {
                LOG.debug("findByDefendantNameSafe - Returning result for courtId: {}, surname: {}",
                    courtId, surname);
                return Optional.of((XhbDefendantDao) result);
            } else {
                LOG.warn("findByDefendantNameSafe - Unexpected result type: {}",
                    result.getClass().getName());
                return Optional.empty();
            }

        } catch (Exception e) {
            LOG.error("Error in findByDefendantNameSafe({}, {}, {}, {}, {}, {}): {}", courtId,
                firstName, middleName, surname, gender, dateOfBirth, e.getMessage(), e);
            return Optional.empty();
        }
    }

}
