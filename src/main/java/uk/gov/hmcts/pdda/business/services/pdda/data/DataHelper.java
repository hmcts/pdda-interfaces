package uk.gov.hmcts.pdda.business.services.pdda.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;

import java.util.Optional;

/**
 * <p>
 * Title: DataHelper.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2024
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @author HarrisM
 * @version 1.0
 */
@SuppressWarnings("PMD.LawOfDemeter")
public class DataHelper {

    private static final Logger LOG = LoggerFactory.getLogger(DataHelper.class);
    private static final Integer ADDRESS_ID = -1;

    private RepositoryHelper repositoryHelper;

    public DataHelper() {
        // Default constructor.
    }

    // JUnit constructor
    public DataHelper(RepositoryHelper repositoryHelper) {
        this.repositoryHelper = repositoryHelper;
    }

    /**
     * Create XhbCourtSiteDao.
     */
    public Optional<XhbCourtSiteDao> createCourtSite(final Integer courtId,
        final String courtSiteName, final String courtSiteCode) {
        LOG.info("createCourtSite({})", courtSiteName);
        XhbCourtSiteDao dao = new XhbCourtSiteDao();
        dao.setCourtId(courtId);
        dao.setCourtSiteName(courtSiteName);
        dao.setCourtSiteCode(courtSiteCode);
        dao.setAddressId(ADDRESS_ID);
        return getRepositoryHelper().getXhbCourtSiteRepository().update(dao);
    }

    /**
     * Create XhbCourtRoomDao.
     */
    public Optional<XhbCourtRoomDao> createCourtRoom(final Integer courtSiteId,
        final String courtRoomName, final String description, final Integer crestCourtRoomNo) {
        LOG.info("createCourtRoom({})", courtRoomName);
        XhbCourtRoomDao dao = new XhbCourtRoomDao();
        dao.setCourtSiteId(courtSiteId);
        dao.setCourtRoomName(courtRoomName);
        dao.setDescription(description);
        dao.setCrestCourtRoomNo(crestCourtRoomNo);
        return getRepositoryHelper().getXhbCourtRoomRepository().update(dao);
    }

    protected RepositoryHelper getRepositoryHelper() {
        if (repositoryHelper == null) {
            repositoryHelper = new RepositoryHelper();
        }
        return repositoryHelper;
    }
}
