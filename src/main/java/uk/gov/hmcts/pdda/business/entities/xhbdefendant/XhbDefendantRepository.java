package uk.gov.hmcts.pdda.business.entities.xhbdefendant;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.pdda.business.entities.AbstractRepository;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Optional;



@Repository
@SuppressWarnings({"PMD.LawOfDemeter", "PMD.UseObjectForClearerAPI"})
public class XhbDefendantRepository extends AbstractRepository<XhbDefendantDao>
    implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(XhbDefendantRepository.class);

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
        query.setParameter("middleName", middleName);
        query.setParameter("surname", surname);
        query.setParameter("gender", gender);
        query.setParameter("dateOfBirth", dateOfBirth);
        XhbDefendantDao dao =
            query.getResultList().isEmpty() ? null : (XhbDefendantDao) query.getSingleResult();
        return dao != null ? Optional.of(dao) : Optional.empty();
    }
}
