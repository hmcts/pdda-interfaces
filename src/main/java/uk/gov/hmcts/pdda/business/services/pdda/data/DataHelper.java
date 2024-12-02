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
import uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant.XhbSchedHearingDefendantDao;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingDao;

import java.time.LocalDateTime;
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
@SuppressWarnings({"PMD.TooManyMethods", "PMD.UseObjectForClearerAPI"})
public class DataHelper extends FinderHelper {

    private static final Logger LOG = LoggerFactory.getLogger(DataHelper.class);

    public Optional<XhbCourtSiteDao> validateCourtSite(final Integer courtId,
        final String courtSiteName, final String courtSiteCode) {
        LOG.debug("validateCourtSite({})", courtSiteName);
        Optional<XhbCourtSiteDao> result = findCourtSite(courtId, courtSiteName);
        if (result.isEmpty()) {
            result = createCourtSite(courtId, courtSiteName, courtSiteCode);
        }
        return result;
    }

    public Optional<XhbCourtRoomDao> validateCourtRoom(final Integer courtSiteId,
        final String courtRoomName, final String description, final Integer crestCourtRoomNo) {
        LOG.debug("validateCourtRoom({})", crestCourtRoomNo);
        Optional<XhbCourtRoomDao> result = findCourtRoom(courtSiteId, crestCourtRoomNo);
        if (result.isEmpty()) {
            result = createCourtRoom(courtSiteId, courtRoomName, description, crestCourtRoomNo);
        }
        return result;
    }

    public Optional<XhbHearingListDao> validateHearingList(final Integer courtId,
        final Integer crestListId, final String listType, final String status,
        final LocalDateTime startDate) {
        LOG.debug("validateHearingList({},{})", status, startDate);
        Optional<XhbHearingListDao> result = findHearingList(courtId, status, startDate);
        if (result.isEmpty()) {
            result = createHearingList(courtId, crestListId, listType, status, startDate);
        }
        return result;
    }

    public Optional<XhbSittingDao> validateSitting(final Integer courtSiteId,
        final Integer courtRoomId, final String isFloating, final LocalDateTime sittingTime) {
        LOG.debug("validateSitting({})", sittingTime);
        Optional<XhbSittingDao> result = findSitting(courtSiteId, courtRoomId, sittingTime);
        if (result.isEmpty()) {
            result = createSitting(courtSiteId, courtRoomId, isFloating, sittingTime);
        }
        return result;
    }

    public Optional<XhbCaseDao> validateCase(final Integer courtId, final String caseType,
        final Integer caseNumber) {
        LOG.debug("validateCase({}{})", caseType, caseNumber);
        Optional<XhbCaseDao> result = findCase(courtId, caseType, caseNumber);
        if (result.isEmpty()) {
            result = createCase(courtId, caseType, caseNumber);
        }
        return result;
    }

    public Optional<XhbDefendantOnCaseDao> validateDefendantOnCase(final Integer caseId,
        final Integer defendantId) {
        LOG.debug("validateDefendantOnCase()");
        Optional<XhbDefendantOnCaseDao> result = findDefendantOnCase(caseId, defendantId);
        if (result.isEmpty()) {
            result = createDefendantOnCase(caseId, defendantId);
        }
        return result;
    }

    public Optional<XhbDefendantDao> validateDefendant(final Integer courtId,
        final String firstName, final String middleName, final String surname, final Integer gender,
        final LocalDateTime dateOfBirth) {
        LOG.debug("validateDefendant()");
        Optional<XhbDefendantDao> result =
            findDefendant(courtId, firstName, middleName, surname, gender, dateOfBirth);
        if (result.isEmpty()) {
            result = createDefendant(courtId, firstName, middleName, surname, gender, dateOfBirth);
        }
        return result;
    }

    public Optional<XhbHearingDao> validateHearing(final Integer courtId, final Integer caseId,
        final Integer refHearingTypeId, final LocalDateTime hearingStartDate) {
        LOG.debug("validateHearing()");
        Optional<XhbHearingDao> result = findHearing(courtId, caseId, hearingStartDate);
        if (result.isEmpty()) {
            result = createHearing(courtId, caseId, refHearingTypeId, hearingStartDate);
        }
        return result;
    }

    public Optional<XhbScheduledHearingDao> validateScheduledHearing(final Integer sittingId,
        final Integer hearingId, final LocalDateTime notBeforeTime) {
        LOG.debug("validateScheduledHearing()");
        Optional<XhbScheduledHearingDao> result =
            findScheduledHearing(sittingId, hearingId, notBeforeTime);
        if (result.isEmpty()) {
            result = createScheduledHearing(sittingId, hearingId, notBeforeTime);
        }
        return result;
    }

    public Optional<XhbSchedHearingDefendantDao> validateSchedHearingDefendant(
        final Integer scheduledHearingId, final Integer defendantId) {
        LOG.debug("validateSchedHearingDefendant()");
        Optional<XhbSchedHearingDefendantDao> result =
            findSchedHearingDefendant(scheduledHearingId, defendantId);
        if (result.isEmpty()) {
            result = createSchedHearingDefendant(scheduledHearingId, defendantId);
        }
        return result;
    }

    public Optional<XhbCrLiveDisplayDao> validateCrLiveDisplay(final Integer courtRoomId,
        final Integer scheduledHearingId, final LocalDateTime timeStatusSet) {
        LOG.debug("validateCrLiveDisplay()");
        Optional<XhbCrLiveDisplayDao> result = findCrLiveDisplay(courtRoomId, scheduledHearingId);
        if (result.isEmpty()) {
            result = createCrLiveDisplay(courtRoomId, scheduledHearingId, timeStatusSet);
        }
        return result;
    }


}
