package uk.gov.hmcts.pdda.business.services.pdda.data;

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
import uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant.XhbSchedHearingDefendantDao;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingDao;

import java.time.LocalDateTime;
import java.util.Optional;

/**

 * Title: FinderHelper.


 * Description:


 * Copyright: Copyright (c) 2024


 * Company: CGI

 * @author HarrisM
 * @version 1.0
 */
@SuppressWarnings({"PMD.TooManyMethods"})
public class FinderHelper extends CreationHelper {

    private static final long serialVersionUID = 1L;
    
    public FinderHelper() {
        super();
    }

    // JUnit constructor
    public FinderHelper(RepositoryHelper repositoryHelper) {
        super(repositoryHelper);
    }

    public Optional<XhbCourtSiteDao> findCourtSite(final String courtHouseName,
        final String courtHouseCode) {
        return getRepositoryHelper().getXhbCourtSiteRepository().findByCourtSiteNameSafe(
            courtHouseName,
            courtHouseCode);
    }

    public Optional<XhbCourtRoomDao> findCourtRoom(final Integer courtId,
        final Integer crestCourtRoomNo) {
        return getRepositoryHelper().getXhbCourtRoomRepository().findByCourtRoomNoSafe(courtId,
            crestCourtRoomNo);
    }

    public Optional<XhbHearingListDao> findHearingList(final Integer courtId, final String status,
        final LocalDateTime startDate) {
        return getRepositoryHelper().getXhbHearingListRepository()
            .findByCourtIdStatusAndDateSafe(courtId, status, startDate);
    }

    public Optional<XhbSittingDao> findSitting(final Integer courtSiteId, final Integer courtRoomId,
        final LocalDateTime sittingTime, final Integer listId) {
        return getRepositoryHelper().getXhbSittingRepository()
            .findByCourtRoomIdCourtSiteIdListIdAndSittingTimeSafe(courtSiteId, courtRoomId, sittingTime, listId);
    }

    public Optional<XhbCaseDao> findCase(final Integer courtId, final String caseType,
        final Integer caseNumber) {
        return getRepositoryHelper().getXhbCaseRepository().findByNumberTypeAndCourtSafe(courtId,
            caseType, caseNumber);
    }

    public Optional<XhbRefJudgeDao> findJudge(final Integer courtId,
        final String judgeFirstname, final String judgeSurname) {
        return getRepositoryHelper().getXhbRefJudgeRepository().findJudgeByCourtIdAndNameSafe(courtId,
            judgeFirstname, judgeSurname);
    }
    
    public Optional<XhbDefendantOnCaseDao> findDefendantOnCase(final Integer caseId,
        final Integer defendantId) {
        return getRepositoryHelper().getXhbDefendantOnCaseRepository()
            .findByDefendantAndCaseSafe(caseId, defendantId);
    }

    public Optional<XhbDefendantDao> findDefendant(final Integer courtId, final String firstName,
        final String middleName, final String surname, final Integer gender,
        final LocalDateTime dateOfBirth) {
        return getRepositoryHelper().getXhbDefendantRepository().findByDefendantNameSafe(courtId,
            firstName, middleName, surname, gender, dateOfBirth);
    }

    public Optional<XhbRefHearingTypeDao> findHearingType(final Integer courtId,
        final String hearingTypeCode, final String hearingTypeDesc, final String category) {
        return getRepositoryHelper().getXhbRefHearingTypeRepository().findByHearingTypeSafe(courtId,
            hearingTypeCode, hearingTypeDesc, category);
    }

    public Optional<XhbHearingDao> findHearing(final Integer courtId, final Integer caseId,
        final LocalDateTime hearingStartDate) {
        return getRepositoryHelper().getXhbHearingRepository().findByCaseIdAndStartDateSafe(courtId,
            caseId, hearingStartDate);
    }

    public Optional<XhbScheduledHearingDao> findScheduledHearing(final Integer sittingId,
        final Integer hearingId, final LocalDateTime notBeforeTime) {
        return getRepositoryHelper().getXhbScheduledHearingRepository().findBySittingDateSafe(
            sittingId,
            hearingId, notBeforeTime);
    }

    public Optional<XhbSchedHearingDefendantDao> findSchedHearingDefendant(
        final Integer scheduledHearingId, final Integer defendantOnCaseId) {
        return getRepositoryHelper().getXhbSchedHearingDefendantRepository()
            .findByHearingAndDefendantSafe(scheduledHearingId, defendantOnCaseId);
    }

    public Optional<XhbCrLiveDisplayDao> findCrLiveDisplay(final Integer courtRoomId) {
        return getRepositoryHelper().getXhbCrLiveDisplayRepository()
            .findByCourtRoomSafe(courtRoomId);
    }
}
