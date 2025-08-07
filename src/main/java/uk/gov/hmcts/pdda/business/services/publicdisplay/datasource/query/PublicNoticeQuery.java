package uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.query;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.AbstractControllerBean;
import uk.gov.hmcts.pdda.business.entities.xhbconfiguredpublicnotice.XhbConfiguredPublicNoticeDao;
import uk.gov.hmcts.pdda.business.entities.xhbconfiguredpublicnotice.XhbConfiguredPublicNoticeRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdefinitivepublicnotice.XhbDefinitivePublicNoticeDao;
import uk.gov.hmcts.pdda.business.entities.xhbdefinitivepublicnotice.XhbDefinitivePublicNoticeRepository;
import uk.gov.hmcts.pdda.business.entities.xhbpublicnotice.XhbPublicNoticeDao;
import uk.gov.hmcts.pdda.business.entities.xhbpublicnotice.XhbPublicNoticeRepository;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.PublicNoticeValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Abstract query class used by public display.
 * 
 * @author pznwc5
 */
@SuppressWarnings("PMD.NullAssignment")
public class PublicNoticeQuery extends AbstractControllerBean {

    /** Logger object. */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private XhbConfiguredPublicNoticeRepository xhbConfiguredPublicNoticeRepository;

    private XhbPublicNoticeRepository xhbPublicNoticeRepository;

    private XhbDefinitivePublicNoticeRepository xhbDefinitivePublicNoticeRepository;

    /**
     * Creates a new PublicDisplayQuery object.
     * 
     * @param entityManager EntityManager
     */
    public PublicNoticeQuery(EntityManager entityManager) {
        super(entityManager);
    }

    public PublicNoticeQuery(EntityManager entityManager,
        XhbConfiguredPublicNoticeRepository xhbConfiguredPublicNoticeRepository,
        XhbPublicNoticeRepository xhbPublicNoticeRepository,
        XhbDefinitivePublicNoticeRepository xhbDefinitivePublicNoticeRepository) {
        super(entityManager);
        this.xhbConfiguredPublicNoticeRepository = xhbConfiguredPublicNoticeRepository;
        this.xhbPublicNoticeRepository = xhbPublicNoticeRepository;
        this.xhbDefinitivePublicNoticeRepository = xhbDefinitivePublicNoticeRepository;
    }

    @Override
    protected void clearRepositories() {
        super.clearRepositories();
        xhbConfiguredPublicNoticeRepository = null;
        xhbPublicNoticeRepository = null;
        xhbDefinitivePublicNoticeRepository = null;
    }

    /**
     * Executes the stored procedure and returns the data.
     * 
     * @param courtRoomId room id for which the data is required
     * @return Public display data
     */
    public PublicNoticeValue[] execute(Integer courtRoomId) {
        log.debug("execute({})", courtRoomId);
        List<PublicNoticeValue> results = new ArrayList<>();
        List<XhbConfiguredPublicNoticeDao> cpnDaos =
            getXhbConfiguredPublicNoticeRepository().findActiveCourtRoomNoticesSafe(courtRoomId);
        if (!cpnDaos.isEmpty()) {
            for (XhbConfiguredPublicNoticeDao cpnDao : cpnDaos) {
                PublicNoticeValue pnValue = getPublicNoticeValue(cpnDao);
                if (pnValue != null) {
                    results.add(pnValue);
                }
            }
        }
        return results.toArray(new PublicNoticeValue[0]);
    }

    private PublicNoticeValue getPublicNoticeValue(XhbConfiguredPublicNoticeDao cpnDao) {
        Optional<XhbPublicNoticeDao> pnDao =
            getXhbPublicNoticeRepository().findByIdSafe(cpnDao.getPublicNoticeId());
        if (pnDao.isPresent()) {
            Optional<XhbDefinitivePublicNoticeDao> dpnDao =
                getXhbDefinitivePublicNoticeRepository()
                    .findByIdSafe(pnDao.get().getDefinitivePnId());
            if (dpnDao.isPresent()) {
                PublicNoticeValue pnValue = new PublicNoticeValue();
                pnValue.setPublicNoticeDesc(pnDao.get().getPublicNoticeDesc());
                pnValue.setPriority(dpnDao.get().getPriority());
                pnValue.setActive("1".contentEquals(cpnDao.getIsActive()));
                return pnValue;
            }
        }
        return null;
    }

    private XhbConfiguredPublicNoticeRepository getXhbConfiguredPublicNoticeRepository() {
        if (xhbConfiguredPublicNoticeRepository == null || !isEntityManagerActive()) {
            xhbConfiguredPublicNoticeRepository =
                new XhbConfiguredPublicNoticeRepository(getEntityManager());
        }
        return xhbConfiguredPublicNoticeRepository;
    }

    private XhbPublicNoticeRepository getXhbPublicNoticeRepository() {
        if (xhbPublicNoticeRepository == null || !isEntityManagerActive()) {
            xhbPublicNoticeRepository = new XhbPublicNoticeRepository(getEntityManager());
        }
        return xhbPublicNoticeRepository;
    }

    private XhbDefinitivePublicNoticeRepository getXhbDefinitivePublicNoticeRepository() {
        if (xhbDefinitivePublicNoticeRepository == null || !isEntityManagerActive()) {
            xhbDefinitivePublicNoticeRepository =
                new XhbDefinitivePublicNoticeRepository(getEntityManager());
        }
        return xhbDefinitivePublicNoticeRepository;
    }
}
