package uk.gov.hmcts.pdda.business.services.pdda.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbcase.XhbCaseDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import uk.gov.hmcts.pdda.business.entities.xhbdefendant.XhbDefendantDao;
import uk.gov.hmcts.pdda.business.entities.xhbdefendantoncase.XhbDefendantOnCaseDao;
import uk.gov.hmcts.pdda.business.entities.xhbhearinglist.XhbHearingListDao;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingDao;

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

    /**
     * Create XhbHearingListDao.
     */
    public Optional<XhbHearingListDao> createHearingList(final Integer courtId,
        final Integer crestListId, final String listType) {
        LOG.info("createHearingList({})", listType);
        XhbHearingListDao dao = new XhbHearingListDao();
        dao.setCourtId(courtId);
        dao.setCrestListId(crestListId);
        dao.setListType(listType);
        return getRepositoryHelper().getXhbHearingListRepository().update(dao);
    }

    /**
     * Create XhbSittingDao.
     */
    public Optional<XhbSittingDao> createSitting(final Integer courtSiteId,
        final Integer courtRoomId, final String isFloating) {
        LOG.info("createSitting()");
        XhbSittingDao dao = new XhbSittingDao();
        dao.setCourtSiteId(courtSiteId);
        dao.setCourtRoomId(courtRoomId);
        dao.setIsFloating(isFloating);
        return getRepositoryHelper().getXhbSittingRepository().update(dao);
    }

    /**
     * Create XhbCaseDao.
     */
    public Optional<XhbCaseDao> createCase(final Integer courtId, final String caseType,
        final Integer caseNumber) {
        LOG.info("createCase({}{})", caseType, caseNumber);
        XhbCaseDao dao = new XhbCaseDao();
        dao.setCourtId(courtId);
        dao.setCaseType(caseType);
        dao.setCaseNumber(caseNumber);
        return getRepositoryHelper().getXhbCaseRepository().update(dao);
    }

    /**
     * Create XhbDefendantOnCaseDao.
     */
    public Optional<XhbDefendantOnCaseDao> createDefendantOnCase(final Integer caseId,
        final Integer defendantId) {
        LOG.info("createDefendantOnCase()");
        XhbDefendantOnCaseDao dao = new XhbDefendantOnCaseDao();
        dao.setCaseId(caseId);
        dao.setDefendantId(defendantId);
        return getRepositoryHelper().getXhbDefendantOnCaseRepository().update(dao);
    }

    /**
     * Create XhbDefendantDao.
     */
    public Optional<XhbDefendantDao> createDefendant(// final Integer courtId,
        final String firstName, final String middleName, final String surname) {
        LOG.info("createDefendant({},{},{})", firstName, middleName, surname);
        XhbDefendantDao dao = new XhbDefendantDao();
        dao.setFirstName(firstName);
        dao.setMiddleName(middleName);
        dao.setSurname(surname);
        //dao.setCourtId(courtId);
        return getRepositoryHelper().getXhbDefendantRepository().update(dao);
    }

    protected RepositoryHelper getRepositoryHelper() {
        if (repositoryHelper == null) {
            repositoryHelper = new RepositoryHelper();
        }
        return repositoryHelper;
    }
}
