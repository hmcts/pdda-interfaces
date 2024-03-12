package uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.query;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class PublicNoticeQuery {

    /** Logger object. */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private EntityManager entityManager;

    private XhbConfiguredPublicNoticeRepository xhbConfiguredPublicNoticeRepository;

    private XhbPublicNoticeRepository xhbPublicNoticeRepository;

    private XhbDefinitivePublicNoticeRepository xhbDefinitivePublicNoticeRepository;

    /**
     * Creates a new PublicDisplayQuery object.
     * 
     * @param entityManager EntityManager
     */
    public PublicNoticeQuery(EntityManager entityManager) {
        setEntityManager(entityManager);
    }

    public PublicNoticeQuery(EntityManager entityManager,
        XhbConfiguredPublicNoticeRepository xhbConfiguredPublicNoticeRepository,
        XhbPublicNoticeRepository xhbPublicNoticeRepository,
        XhbDefinitivePublicNoticeRepository xhbDefinitivePublicNoticeRepository) {
        setEntityManager(entityManager);
        this.xhbConfiguredPublicNoticeRepository = xhbConfiguredPublicNoticeRepository;
        this.xhbPublicNoticeRepository = xhbPublicNoticeRepository;
        this.xhbDefinitivePublicNoticeRepository = xhbDefinitivePublicNoticeRepository;
    }

    private void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
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
            getXhbConfiguredPublicNoticeRepository().findActiveCourtRoomNotices(courtRoomId);
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
        Optional<XhbPublicNoticeDao> pnDao = getXhbPublicNoticeRepository().findById(cpnDao.getPublicNoticeId());
        if (pnDao.isPresent()) {
            Optional<XhbDefinitivePublicNoticeDao> dpnDao =
                getXhbDefinitivePublicNoticeRepository().findById(pnDao.get().getDefinitivePnId());
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
        if (xhbConfiguredPublicNoticeRepository == null) {
            xhbConfiguredPublicNoticeRepository = new XhbConfiguredPublicNoticeRepository(entityManager);
        }
        return xhbConfiguredPublicNoticeRepository;
    }

    private XhbPublicNoticeRepository getXhbPublicNoticeRepository() {
        if (xhbPublicNoticeRepository == null) {
            xhbPublicNoticeRepository = new XhbPublicNoticeRepository(entityManager);
        }
        return xhbPublicNoticeRepository;
    }

    private XhbDefinitivePublicNoticeRepository getXhbDefinitivePublicNoticeRepository() {
        if (xhbDefinitivePublicNoticeRepository == null) {
            xhbDefinitivePublicNoticeRepository = new XhbDefinitivePublicNoticeRepository(entityManager);
        }
        return xhbDefinitivePublicNoticeRepository;
    }
}
