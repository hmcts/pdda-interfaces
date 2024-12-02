package uk.gov.hmcts.pdda.business.services.pdda.data;

import uk.gov.hmcts.pdda.business.entities.xhbcase.XhbCaseDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import uk.gov.hmcts.pdda.business.entities.xhbcrlivedisplay.XhbCrLiveDisplayDao;
import uk.gov.hmcts.pdda.business.entities.xhbdefendant.XhbDefendantDao;
import uk.gov.hmcts.pdda.business.entities.xhbdefendantoncase.XhbDefendantOnCaseDao;
import uk.gov.hmcts.pdda.business.entities.xhbhearing.XhbHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbhearinglist.XhbHearingListDao;
import uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant.XhbSchedHearingDefendantDao;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingDao;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * <p>
 * Title: FinderHelper.
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
@SuppressWarnings({"PMD.LawOfDemeter", "PMD.UseObjectForClearerAPI", "PMD.TooManyMethods"})
public class FinderHelper extends CreationHelper {

    public FinderHelper() {
        super();
    }

    // JUnit constructor
    public FinderHelper(RepositoryHelper repositoryHelper) {
        super(repositoryHelper);
    }

    public Optional<XhbCourtSiteDao> findCourtSite(final Integer courtId,
        final String courtSiteName) {
        return getRepositoryHelper().getXhbCourtSiteRepository().findByCourtSiteName(courtId,
            courtSiteName);
    }

    public Optional<XhbCourtRoomDao> findCourtRoom(final Integer courtId,
        final Integer crestCourtRoomNo) {
        return getRepositoryHelper().getXhbCourtRoomRepository().findByCourtRoomNo(courtId,
            crestCourtRoomNo);
    }

    public Optional<XhbHearingListDao> findHearingList(final Integer courtId, final String status,
        final LocalDateTime startDate) {
        return getRepositoryHelper().getXhbHearingListRepository()
            .findByCourtIdStatusAndDate(courtId, status, startDate);
    }

    public Optional<XhbSittingDao> findSitting(final Integer courtSiteId, final Integer courtRoomId,
        final LocalDateTime sittingTime) {
        return getRepositoryHelper().getXhbSittingRepository()
            .findByCourtRoomAndSittingTime(courtSiteId, courtRoomId, sittingTime);
    }

    public Optional<XhbCaseDao> findCase(final Integer courtId, final String caseType,
        final Integer caseNumber) {
        return getRepositoryHelper().getXhbCaseRepository().findByNumberTypeAndCourt(courtId,
            caseType, caseNumber);
    }

    public Optional<XhbDefendantOnCaseDao> findDefendantOnCase(final Integer caseId,
        final Integer defendantId) {
        return getRepositoryHelper().getXhbDefendantOnCaseRepository()
            .findByDefendantAndCase(caseId, defendantId);
    }

    public Optional<XhbDefendantDao> findDefendant(final Integer courtId, final String firstName,
        final String middleName, final String surname, final Integer gender,
        final LocalDateTime dateOfBirth) {
        return getRepositoryHelper().getXhbDefendantRepository().findByDefendantName(courtId,
            firstName, middleName, surname, gender, dateOfBirth);
    }

    public Optional<XhbHearingDao> findHearing(final Integer courtId, final Integer caseId,
        final LocalDateTime hearingStartDate) {
        return getRepositoryHelper().getXhbHearingRepository().findByCaseIdAndStartDate(courtId,
            caseId, hearingStartDate);
    }

    public Optional<XhbScheduledHearingDao> findScheduledHearing(final Integer sittingId,
        final Integer hearingId, final LocalDateTime notBeforeTime) {
        return getRepositoryHelper().getXhbScheduledHearingRepository().findBySittingDate(sittingId,
            hearingId, notBeforeTime);
    }

    public Optional<XhbSchedHearingDefendantDao> findSchedHearingDefendant(
        final Integer scheduledHearingId, final Integer defendantOnCaseId) {
        return getRepositoryHelper().getXhbSchedHearingDefendantRepository()
            .findByHearingAndDefendant(scheduledHearingId, defendantOnCaseId);
    }

    public Optional<XhbCrLiveDisplayDao> findCrLiveDisplay(final Integer courtRoomId,
        final Integer scheduledHearingId) {
        return getRepositoryHelper().getXhbCrLiveDisplayRepository().findByHearing(courtRoomId,
            scheduledHearingId);
    }
}
