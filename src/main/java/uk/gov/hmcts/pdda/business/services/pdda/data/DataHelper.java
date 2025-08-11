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

 * Title: DataHelper.


 * Description:


 * Copyright: Copyright (c) 2024


 * Company: CGI

 * @author HarrisM
 * @version 1.0
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.UseObjectForClearerAPI"})
public class DataHelper extends FinderHelper {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(DataHelper.class);

    public Optional<XhbCourtSiteDao> validateCourtSite(final String courtHouseName,
        final String courtHouseCode) {
        LOG.debug("validateCourtSite(), courtHouseName:{}, courtHouseCode:{}",
            courtHouseName, courtHouseCode);
        Optional<XhbCourtSiteDao> result = findCourtSite(courtHouseName, courtHouseCode);
        if (result.isEmpty()) {
            LOG.error("No XhbCourtSite found for name:{},  code:{}", courtHouseName, courtHouseCode);
        }
        return result;
    }

    public Optional<XhbCourtRoomDao> validateCourtRoom(final Integer courtSiteId,
        final Integer crestCourtRoomNo) {
        LOG.debug("validateCourtRoom(), courtSiteId:{}, crestCourtRoomNo:{}",
            courtSiteId, crestCourtRoomNo);
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
        LOG.debug("validateHearingList(), courtId:{}, crestListId:{}, listType:{}, "
            + "status:{}, startDate:{}, publishedTime:{}, printReference:{}, editionNo:{}, listCourtType:{}",
            courtId, crestListId, listType, status, startDate, publishedTime, printReference,
            editionNo, listCourtType);
        Optional<XhbHearingListDao> result = findHearingList(courtId, status, startDate);
        if (result.isEmpty()) {
            LOG.debug("Creating new XhbHearingList for courtId:{}, crestListId:{}, listType:{},"
                + "status:{}, startDate:{}, publishedTime:{}, printReference:{}, editionNo:{}, listCourtType:{}",
                courtId, crestListId, listType, status, startDate, publishedTime, printReference,
                editionNo, listCourtType);
            result = createHearingList(courtId, crestListId, listType, status, startDate,
                publishedTime, printReference, editionNo, listCourtType);
        }
        return result;
    }

    public Optional<XhbSittingDao> validateSitting(final Integer courtSiteId,
        final Integer courtRoomId, final String isFloating, final LocalDateTime sittingTime,
        final Integer listId) {
        LOG.debug("validateSitting(), courtSiteId:{}, courtRoomId:{}, isFloating:{}, sittingTime:{}, listId:{}",
            courtSiteId, courtRoomId, isFloating, sittingTime, listId);
        Optional<XhbSittingDao> result = findSitting(courtSiteId, courtRoomId, sittingTime);
        if (result.isEmpty()) {
            LOG.debug("Creating new XhbSitting for courtSiteId:{}, courtRoomId:{}, isFloating:{},"
                + "sittingTime:{}, listId:{}",
                courtSiteId, courtRoomId, isFloating, sittingTime, listId);
            result = createSitting(courtSiteId, courtRoomId, isFloating, sittingTime, listId);
        }
        return result;
    }

    public Optional<XhbCaseDao> validateCase(final Integer courtId, final String caseType,
        final Integer caseNumber) {
        LOG.debug("validateCase(), courtId:{}, caseType:{}, caseNumber:{}",
            courtId, caseType, caseNumber);
        Optional<XhbCaseDao> result = findCase(courtId, caseType, caseNumber);
        if (result.isEmpty()) {
            LOG.debug("Creating new XhbCase for courtId:{}, caseType:{}, caseNumber:{}",
                courtId, caseType, caseNumber);
            result = createCase(courtId, caseType, caseNumber);
        }
        return result;
    }

    public Optional<XhbRefJudgeDao> validateJudge(final Integer courtId,
        final String judgeTitle, final String judgeFirstname, final String judgeSurname) {
        LOG.debug("validateJudge(), courtId:{}, judgeTitle:{}, "
            + "judgeFirstname:{}, judgeSurname:{}", courtId, judgeTitle, judgeFirstname, judgeSurname);
        Optional<XhbRefJudgeDao> result =
            findJudge(courtId, judgeFirstname, judgeSurname);
        if (result.isEmpty()) {
            LOG.debug("Creating new XhbRefJudge for courtId:{}, judgeTitle:{}, judgeFirstname:{}, judgeSurname:{}",
                courtId, judgeTitle, judgeFirstname, judgeSurname);
            result = createRefJudge(courtId, judgeTitle, judgeFirstname, judgeSurname);
        }
        return result;
    }

    public Optional<XhbDefendantOnCaseDao> validateDefendantOnCase(final Integer caseId,
        final Integer defendantId, final String isMasked) {
        LOG.debug("validateDefendantOnCase(), caseId:{}, defendantId:{}, isMasked:{}",
            caseId, defendantId, isMasked);
        Optional<XhbDefendantOnCaseDao> result = findDefendantOnCase(caseId, defendantId);
        if (result.isEmpty()) {
            LOG.debug("Creating new XhbDefendantOnCase for caseId:{}, defendantId:{}, isMasked:{}",
                caseId, defendantId, isMasked);
            result = createDefendantOnCase(caseId, defendantId, isMasked);
        }
        return result;
    }

    public Optional<XhbDefendantDao> validateDefendant(final Integer courtId,
        final String firstName, final String middleName, final String surname, final Integer gender,
        final LocalDateTime dateOfBirth, final String publicDisplayHide) {
        LOG.debug("validateDefendant(), courtId:{}, firstName:{}, middleName:{}, "
            + "surname:{}, gender:{}, dateOfBirth:{}, publicDisplayHide:{}",
            courtId, firstName, middleName, surname, gender, dateOfBirth, publicDisplayHide);
        Optional<XhbDefendantDao> result =
            findDefendant(courtId, firstName, middleName, surname, gender, dateOfBirth);
        if (result.isEmpty()) {
            LOG.debug("Creating new XhbDefendant for courtId:{}, firstName:{}, middleName:{}, surname:{}, "
                + "gender:{}, dateOfBirth:{}, publicDisplayHide:{}",
                courtId, firstName, middleName, surname, gender, dateOfBirth, publicDisplayHide);
            result = createDefendant(courtId, firstName, middleName, surname, gender, dateOfBirth,
                publicDisplayHide);
        }
        return result;
    }

    public Optional<XhbRefHearingTypeDao> validateHearingType(final Integer courtId,
        final String hearingTypeCode, final String hearingTypeDesc, final String category) {
        LOG.debug("validateHearingType(), courtId:{}, hearingTypeCode:{}, "
            + "hearingTypeDesc:{}, category:{}", courtId, hearingTypeCode, hearingTypeDesc, category);
        Optional<XhbRefHearingTypeDao> result =
            findHearingType(courtId, hearingTypeCode, hearingTypeDesc, category);
        if (result.isEmpty()) {
            LOG.debug("Creating new XhbRefHearingType for courtId:{}, hearingTypeCode:{}, "
                + "hearingTypeDesc:{}, category:{}", courtId, hearingTypeCode, hearingTypeDesc,
                category);
            result = createHearingType(courtId, hearingTypeCode, hearingTypeDesc, category);
        }
        return result;
    }

    public Optional<XhbHearingDao> validateHearing(final Integer courtId, final Integer caseId,
        final Integer refHearingTypeId, final LocalDateTime hearingStartDate,
        final LocalDateTime hearingEndDate) {
        LOG.debug("validateHearing(), courtId:{}, caseId:{}, refHearingTypeId:{}, "
            + "hearingStartDate:{}, hearingEndDate:{}", courtId, caseId, refHearingTypeId,
            hearingStartDate, hearingEndDate);
        Optional<XhbHearingDao> result = findHearing(courtId, caseId, hearingStartDate);
        if (result.isEmpty()) {
            LOG.debug("Creating new XhbHearing for courtId:{}, caseId:{}, refHearingTypeId:{}, "
                + "hearingStartDate:{}, hearingEndDate:{}", courtId, caseId, refHearingTypeId,
                hearingStartDate, hearingEndDate);
            result =
                createHearing(courtId, caseId, refHearingTypeId, hearingStartDate, hearingEndDate);
        }
        return result;
    }

    public Optional<XhbScheduledHearingDao> validateScheduledHearing(final Integer sittingId,
        final Integer hearingId, final LocalDateTime notBeforeTime) {
        LOG.debug("validateScheduledHearing(), sittingId:{}, hearingId:{}, notBeforeTime:{}",
            sittingId, hearingId, notBeforeTime);
        Optional<XhbScheduledHearingDao> result =
            findScheduledHearing(sittingId, hearingId, notBeforeTime);
        if (result.isEmpty()) {
            LOG.debug("Creating new XhbScheduledHearing for sittingId:{}, hearingId:{}, notBeforeTime:{}",
                sittingId, hearingId, notBeforeTime);
            result = createScheduledHearing(sittingId, hearingId, notBeforeTime);
        }
        return result;
    }

    public Optional<XhbSchedHearingDefendantDao> validateSchedHearingDefendant(
        final Integer scheduledHearingId, final Integer defendantId) {
        LOG.debug("validateSchedHearingDefendant(), scheduledHearingId:{}, defendantId:{}",
            scheduledHearingId, defendantId);
        Optional<XhbSchedHearingDefendantDao> result =
            findSchedHearingDefendant(scheduledHearingId, defendantId);
        if (result.isEmpty()) {
            LOG.debug("Creating new XhbSchedHearingDefendant for scheduledHearingId:{}, defendantId:{}",
                scheduledHearingId, defendantId);
            result = createSchedHearingDefendant(scheduledHearingId, defendantId);
        }
        return result;
    }

    public Optional<XhbCrLiveDisplayDao> validateCrLiveDisplay(final Integer courtRoomId,
        final Integer scheduledHearingId, final LocalDateTime timeStatusSet) {
        LOG.debug("validateCrLiveDisplay(), courtRoomId:{}, scheduledHearingId:{}, timeStatusSet:{}",
            courtRoomId, scheduledHearingId, timeStatusSet);
        Optional<XhbCrLiveDisplayDao> result = findCrLiveDisplay(courtRoomId);
        if (result.isEmpty()) {
            LOG.debug("Creating new XhbCrLiveDisplay for courtRoomId:{}, scheduledHearingId:{}, timeStatusSet:{}",
                courtRoomId, scheduledHearingId, timeStatusSet);
            result = createCrLiveDisplay(courtRoomId, scheduledHearingId, timeStatusSet);
        } else if (!scheduledHearingId.equals(result.get().getScheduledHearingId())) {
            LOG.debug("Updating existing XhbCrLiveDisplay for courtRoomId:{}, scheduledHearingId:{}, timeStatusSet:{}",
                courtRoomId, scheduledHearingId, timeStatusSet);
            result.get().setScheduledHearingId(scheduledHearingId);
            result.get().setTimeStatusSet(timeStatusSet);
            result = updateCrLiveDisplay(result.get());
        }
        return result;
    }


}
