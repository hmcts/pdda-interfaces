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

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(DataHelper.class);

    public Optional<XhbCourtSiteDao> validateCourtSite(final String courtHouseName,
        final String courtHouseCode) {
        LOG.debug("validateCourtSite({})", courtHouseName);
        Optional<XhbCourtSiteDao> result = findCourtSite(courtHouseName, courtHouseCode);
        if (result.isEmpty()) {
            LOG.error("No XhbCourtSite found for name:{},  code:{}", courtHouseName, courtHouseCode);
        }
        return result;
    }

    public Optional<XhbCourtRoomDao> validateCourtRoom(final Integer courtSiteId,
        final Integer crestCourtRoomNo) {
        LOG.debug("validateCourtRoom({})", crestCourtRoomNo);
        Optional<XhbCourtRoomDao> result = findCourtRoom(courtSiteId, crestCourtRoomNo);
        if (result.isEmpty()) {
            LOG.error("No XhbCourtRoom, found for courtSiteId:{},  crestCourtRoomNo:{}",
                courtSiteId, crestCourtRoomNo);
        }
        return result;
    }

    public Optional<XhbHearingListDao> validateHearingList(final Integer courtId,
        final Integer crestListId, final String listType, final String status,
        final LocalDateTime startDate, final LocalDateTime publishedTime, String printReference,
        final Integer editionNo, final String listCourtType) {
        LOG.debug("validateHearingList({},{})", status, startDate);
        Optional<XhbHearingListDao> result = findHearingList(courtId, status, startDate);
        if (result.isEmpty()) {
            result = createHearingList(courtId, crestListId, listType, status, startDate,
                publishedTime, printReference, editionNo, listCourtType);
        }
        return result;
    }

    public Optional<XhbSittingDao> validateSitting(final Integer courtSiteId,
        final Integer courtRoomId, final String isFloating, final LocalDateTime sittingTime,
        final Integer listId) {
        LOG.debug("validateSitting({})", sittingTime);
        Optional<XhbSittingDao> result = findSitting(courtSiteId, courtRoomId, sittingTime);
        if (result.isEmpty()) {
            result = createSitting(courtSiteId, courtRoomId, isFloating, sittingTime, listId);
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

    public Optional<XhbRefJudgeDao> validateJudge(final Integer courtId,
        final String judgeTitle, final String judgeFirstname, final String judgeSurname) {
        LOG.debug("validateJudge()");
        Optional<XhbRefJudgeDao> result =
            findJudge(courtId, judgeFirstname, judgeSurname);
        if (result.isEmpty()) {
            result = createJudge(courtId, judgeTitle, judgeFirstname, judgeSurname);
        }
        return result;
    }

    public Optional<XhbDefendantOnCaseDao> validateDefendantOnCase(final Integer caseId,
        final Integer defendantId, final String isMasked) {
        LOG.debug("validateDefendantOnCase()");
        Optional<XhbDefendantOnCaseDao> result = findDefendantOnCase(caseId, defendantId);
        if (result.isEmpty()) {
            result = createDefendantOnCase(caseId, defendantId, isMasked);
        }
        return result;
    }

    public Optional<XhbDefendantDao> validateDefendant(final Integer courtId,
        final String firstName, final String middleName, final String surname, final Integer gender,
        final LocalDateTime dateOfBirth, final String publicDisplayHide) {
        LOG.debug("validateDefendant()");
        Optional<XhbDefendantDao> result =
            findDefendant(courtId, firstName, middleName, surname, gender, dateOfBirth);
        if (result.isEmpty()) {
            result = createDefendant(courtId, firstName, middleName, surname, gender, dateOfBirth,
                publicDisplayHide);
        }
        return result;
    }

    public Optional<XhbRefHearingTypeDao> validateHearingType(final Integer courtId,
        final String hearingTypeCode, final String hearingTypeDesc, final String category) {
        LOG.debug("validateHearingType()");
        Optional<XhbRefHearingTypeDao> result =
            findHearingType(courtId, hearingTypeCode, hearingTypeDesc, category);
        if (result.isEmpty()) {
            result = createHearingType(courtId, hearingTypeCode, hearingTypeDesc, category);
        }
        return result;
    }

    public Optional<XhbHearingDao> validateHearing(final Integer courtId, final Integer caseId,
        final Integer refHearingTypeId, final LocalDateTime hearingStartDate,
        final LocalDateTime hearingEndDate) {
        LOG.debug("validateHearing()");
        Optional<XhbHearingDao> result = findHearing(courtId, caseId, hearingStartDate);
        if (result.isEmpty()) {
            result =
                createHearing(courtId, caseId, refHearingTypeId, hearingStartDate, hearingEndDate);
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
        Optional<XhbCrLiveDisplayDao> result = findCrLiveDisplay(courtRoomId);
        if (result.isEmpty()) {
            result = createCrLiveDisplay(courtRoomId, scheduledHearingId, timeStatusSet);
        } else if (!scheduledHearingId.equals(result.get().getScheduledHearingId())) {
            result.get().setScheduledHearingId(scheduledHearingId);
            result.get().setTimeStatusSet(timeStatusSet);
            result = updateCrLiveDisplay(result.get());
        }
        return result;
    }


}
