package uk.gov.hmcts.pdda.business.services.pdda.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbcase.XhbCaseDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import uk.gov.hmcts.pdda.business.entities.xhbcrlivedisplay.XhbCrLiveDisplayDao;
import uk.gov.hmcts.pdda.business.entities.xhbdefendant.XhbDefendantDao;
import uk.gov.hmcts.pdda.business.entities.xhbdefendantoncase.XhbDefendantOnCaseDao;
import uk.gov.hmcts.pdda.business.entities.xhbhearing.XhbHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbhearinglist.XhbHearingListDao;
import uk.gov.hmcts.pdda.business.entities.xhbrefhearingtype.XhbRefHearingTypeDao;
import uk.gov.hmcts.pdda.business.entities.xhbrefjudge.XhbRefJudgeDao;
import uk.gov.hmcts.pdda.business.entities.xhbschedhearingattendee.XhbSchedHearingAttendeeDao;
import uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant.XhbSchedHearingDefendantDao;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbshjudge.XhbShJudgeDao;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingDao;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * <p>
 * Title: CreationHelper.
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
@SuppressWarnings({"PMD.CouplingBetweenObjects", "PMD.LawOfDemeter", "PMD.TooManyMethods",
    "PMD.UseObjectForClearerAPI"})
public class CreationHelper implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(CreationHelper.class);
    private static final Integer ADDRESS_ID = -1;
    private static final String NO = "N";
    private static final String OPEN = "O";
    private static final String YES = "Y";

    private RepositoryHelper repositoryHelper;

    public CreationHelper() {
        // Default constructor.
    }

    // JUnit constructor
    public CreationHelper(RepositoryHelper repositoryHelper) {
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
        dao.setCrestCourtId(courtSiteCode);
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
        final Integer crestListId, final String listType, final String status,
        final LocalDateTime startDate, final LocalDateTime publishedTime,
        final String printReference, final Integer editionNo, final String listCourtType) {
        LOG.info("createHearingList({},{})", status, startDate);
        XhbHearingListDao dao = new XhbHearingListDao();
        dao.setCourtId(courtId);
        dao.setCrestListId(crestListId);
        dao.setListType(listType);
        dao.setStatus(status);
        dao.setStartDate(startDate);
        dao.setEndDate(startDate);
        dao.setEditionNo(editionNo);
        dao.setPublishedTime(publishedTime);
        dao.setPrintReference(printReference);
        dao.setListCourtType(listCourtType);
        return getRepositoryHelper().getXhbHearingListRepository().update(dao);
    }

    /**
     * Create XhbSittingDao.
     */
    public Optional<XhbSittingDao> createSitting(final Integer courtSiteId,
        final Integer courtRoomId, final String isFloating, final LocalDateTime sittingTime,
        final Integer listId) {
        LOG.info("createSitting()");
        XhbSittingDao dao = new XhbSittingDao();
        dao.setCourtSiteId(courtSiteId);
        dao.setCourtRoomId(courtRoomId);
        dao.setIsFloating(isFloating);
        dao.setSittingTime(sittingTime);
        dao.setListId(listId);
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
        dao.setVideoLinkRequired(NO);
        dao.setCaseListed(YES);
        dao.setCaseStatus(OPEN);
        return getRepositoryHelper().getXhbCaseRepository().update(dao);
    }

    public Optional<XhbCaseDao> updateCase(final XhbCaseDao dao, final String caseTitle) {
        LOG.debug("updateCase({}{})", dao.getCaseType(), dao.getCaseNumber());
        Optional<XhbCaseDao> freshDao = repositoryHelper.getXhbCaseRepository().findByIdSafe(dao.getCaseId());
        if (freshDao.isPresent()) {
            freshDao.get().setCaseTitle(caseTitle);
            return repositoryHelper.getXhbCaseRepository().update(freshDao.get());
        }
        return Optional.empty();
    }
    
    /**
     * Create XhbRefJudgeDao.
     */
    public Optional<XhbRefJudgeDao> createRefJudge(final Integer courtId,
        final String judgeTitle, final String judgeFirstname, final String judgeSurname) {
        LOG.info("createRefJudge({},{},{},{})", courtId, judgeTitle, judgeFirstname, judgeSurname);
        XhbRefJudgeDao dao = new XhbRefJudgeDao();
        dao.setTitle(judgeTitle);
        dao.setFirstname(judgeFirstname);
        dao.setSurname(judgeSurname);
        dao.setCourtId(courtId);
        return getRepositoryHelper().getXhbRefJudgeRepository().update(dao);
    }
    
    /**
     * Create XhbShJudgeDao.
     */
    public Optional<XhbShJudgeDao> createShJudge(final String deputyHcj, final Integer refJudgeId,
        final Integer shAttendeeId) {
        LOG.info("createShJudge({},{},{})", deputyHcj, refJudgeId, shAttendeeId);
        XhbShJudgeDao dao = new XhbShJudgeDao();
        dao.setDeputyHcj(deputyHcj);
        dao.setRefJudgeId(refJudgeId);
        dao.setShAttendeeId(shAttendeeId);
        return getRepositoryHelper().getXhbShJudgeRepository().update(dao);
    }
    
    /**
     * Create XhbSchedHearingAttendeeDao.
     */
    public Optional<XhbSchedHearingAttendeeDao> createSchedHearingAttendee(final String attendeeType,
        final Integer scheduledHearingId, final Integer refJudgeId) {
        LOG.info("createSchedHearingAttendee({},{})", scheduledHearingId, refJudgeId);
        XhbSchedHearingAttendeeDao dao = new XhbSchedHearingAttendeeDao();
        dao.setAttendeeType(attendeeType);
        dao.setScheduledHearingId(scheduledHearingId);
        dao.setRefJudgeId(refJudgeId);
        return getRepositoryHelper().getXhbSchedHearingAttendeeRepository().update(dao);
    }
    
    /**
     * Create XhbDefendantOnCaseDao.
     */
    public Optional<XhbDefendantOnCaseDao> createDefendantOnCase(final Integer caseId,
        final Integer defendantId, final String isMasked) {
        LOG.info("createDefendantOnCase()");
        XhbDefendantOnCaseDao dao = new XhbDefendantOnCaseDao();
        dao.setCaseId(caseId);
        dao.setDefendantId(defendantId);
        dao.setIsMasked(NO);
        return getRepositoryHelper().getXhbDefendantOnCaseRepository().update(dao);
    }

    /**
     * Create XhbDefendantDao.
     */
    public Optional<XhbDefendantDao> createDefendant(final Integer courtId, final String firstName,
        final String middleName, final String surname, final Integer gender,
        final LocalDateTime dateOfBirth, final String publicDisplayHide) {
        LOG.info("createDefendant({},{},{})", firstName, middleName, surname);
        XhbDefendantDao dao = new XhbDefendantDao();
        dao.setFirstName(firstName);
        dao.setMiddleName(middleName);
        dao.setSurname(surname);
        dao.setCourtId(courtId);
        dao.setGender(gender);
        dao.setDateOfBirth(dateOfBirth);
        dao.setPublicDisplayHide(publicDisplayHide);
        return getRepositoryHelper().getXhbDefendantRepository().update(dao);
    }

    /**
     * Create XhbRefHearingTypeDao.
     */
    public Optional<XhbRefHearingTypeDao> createHearingType(final Integer courtId,
        final String hearingTypeCode, final String hearingTypeDesc, final String category) {
        LOG.info("createHearingType()");
        XhbRefHearingTypeDao dao = new XhbRefHearingTypeDao();
        dao.setCourtId(courtId);
        dao.setHearingTypeCode(hearingTypeCode);
        dao.setHearingTypeDesc(hearingTypeDesc);
        dao.setCategory(category);
        return getRepositoryHelper().getXhbRefHearingTypeRepository().update(dao);
    }

    /**
     * Create XhbHearingDao.
     */
    public Optional<XhbHearingDao> createHearing(final Integer courtId, final Integer caseId,
        final Integer refHearingTypeId, final LocalDateTime hearingStartDate,
        final LocalDateTime hearingEndDate) {
        LOG.info("createHearing()");
        XhbHearingDao dao = new XhbHearingDao();
        dao.setCourtId(courtId);
        dao.setCaseId(caseId);
        dao.setRefHearingTypeId(refHearingTypeId);
        dao.setHearingStartDate(hearingStartDate);
        dao.setHearingEndDate(hearingEndDate);
        return getRepositoryHelper().getXhbHearingRepository().update(dao);
    }

    /**
     * Create XhbScheduledHearingDao.
     */
    public Optional<XhbScheduledHearingDao> createScheduledHearing(final Integer sittingId,
        final Integer hearingId, final LocalDateTime notBeforeTime) {
        LOG.info("createScheduledHearing()");
        XhbScheduledHearingDao dao = new XhbScheduledHearingDao();
        dao.setSittingId(sittingId);
        dao.setHearingId(hearingId);
        dao.setNotBeforeTime(notBeforeTime);
        dao.setIsCaseActive(YES);
        return getRepositoryHelper().getXhbScheduledHearingRepository().update(dao);
    }

    /**
     * Create XhbScheduledHearingDao.
     */
    public Optional<XhbSchedHearingDefendantDao> createSchedHearingDefendant(
        final Integer scheduledHearingId, final Integer defendantOnCaseId) {
        LOG.info("createSchedHearingDefendant()");
        XhbSchedHearingDefendantDao dao = new XhbSchedHearingDefendantDao();
        dao.setScheduledHearingId(scheduledHearingId);
        dao.setDefendantOnCaseId(defendantOnCaseId);
        return getRepositoryHelper().getXhbSchedHearingDefendantRepository().update(dao);
    }

    /**
     * Create XhbCrLiveDisplayDao.
     */
    public Optional<XhbCrLiveDisplayDao> createCrLiveDisplay(final Integer courtRoomId,
        final Integer scheduledHearingId, final LocalDateTime timeStatusSet) {
        LOG.info("createCrLiveDisplay()");
        XhbCrLiveDisplayDao dao = new XhbCrLiveDisplayDao();
        dao.setCourtRoomId(courtRoomId);
        dao.setScheduledHearingId(scheduledHearingId);
        dao.setTimeStatusSet(timeStatusSet);
        return getRepositoryHelper().getXhbCrLiveDisplayRepository().update(dao);
    }
    
    /**
     * Update XhbCrLiveDisplayDao.
     */
    public Optional<XhbCrLiveDisplayDao> updateCrLiveDisplay(final XhbCrLiveDisplayDao dao) {
        LOG.info("updateCrLiveDisplay()");
        return getRepositoryHelper().getXhbCrLiveDisplayRepository().update(dao);
    }

    protected RepositoryHelper getRepositoryHelper() {
        if (repositoryHelper == null) {
            repositoryHelper = new RepositoryHelper();
        }
        return repositoryHelper;
    }
}
