package uk.gov.hmcts.pdda.business.services.pdda.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbcase.XhbCaseDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import uk.gov.hmcts.pdda.business.entities.xhbdefendant.XhbDefendantDao;
import uk.gov.hmcts.pdda.business.entities.xhbdefendantoncase.XhbDefendantOnCaseDao;
import uk.gov.hmcts.pdda.business.entities.xhbhearing.XhbHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbrefhearingtype.XhbRefHearingTypeDao;
import uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant.XhbSchedHearingDefendantDao;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingDao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * Title: ListObjectHelper.
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
@SuppressWarnings({"PMD.NullAssignment", "PMD.TooManyMethods", "PMD.ExcessiveParameterList"})
public class ListObjectHelper {

    private static final Logger LOG = LoggerFactory.getLogger(ListObjectHelper.class);
    protected static final String CASENUMBER = "cs:CaseNumber";
    protected static final String CATEGORY = "cs:ListCategory";
    protected static final String COURTHOUSECODE = "cs:CourtHouseCode";
    protected static final String COURTHOUSENAME = "cs:CourtHouseName";
    protected static final String COURTROOMNO = "cs:CourtRoomNumber";
    protected static final String DATEOFBIRTH = "apd:BirthDate";
    protected static final String FEMALE = "FEMALE";
    protected static final String FIRSTNAME = "apd:CitizenNameForename";
    protected static final String GENDER = "cs:Sex";
    protected static final String HEARINGTYPECODE = "cs:HearingDetails.HearingType";
    protected static final String HEARINGTYPEDESC = "cs:HearingDescription";
    protected static final String MALE = "MALE";
    protected static final String NOTBEFORETIME = "cs:TimeMarkingNote";
    protected static final String PUBLISHEDTIME = "cs:PublishedTime";
    protected static final String SITTINGTIME = "cs:SittingAt";
    protected static final String STARTDATE = "cs:StartDate";
    protected static final String SURNAME = "apd:CitizenNameSurname";
    protected static final String VERSION = "cs:Version";
    private static final String EMPTY_STRING = "";
    private static final String DECIMALS_REGEX = "\\d+";
    private static final String TWELVEHOURTIME = "hh:mma";
    private static final String WHITESPACE_REGEX = "\\s";
    private static final String[] CASE_NODES = {CASENUMBER};
    private static final String[] COURTSITE_NODES = {COURTHOUSECODE, COURTHOUSENAME};
    private static final String[] COURTROOM_NODES = {COURTROOMNO};
    private static final String[] DEFENDANT_NODES = {GENDER};
    private static final String[] SITTING_NODES = {SITTINGTIME};
    private static final DateTimeFormatter TWELVEHOURTIMEFORMAT =
        DateTimeFormatter.ofPattern(TWELVEHOURTIME);

    private final DataHelper dataHelper = new DataHelper();
    private Optional<XhbCourtSiteDao> xhbCourtSiteDao = Optional.empty();
    private Optional<XhbCourtRoomDao> xhbCourtRoomDao = Optional.empty();
    private Optional<XhbCaseDao> xhbCaseDao = Optional.empty();
    private Optional<XhbDefendantDao> xhbDefendantDao = Optional.empty();
    private Optional<XhbDefendantOnCaseDao> xhbDefendantOnCaseDao = Optional.empty();
    private Optional<XhbRefHearingTypeDao> xhbRefHearingTypeDao = Optional.empty();
    private Optional<XhbHearingDao> xhbHearingDao = Optional.empty();
    private Optional<XhbSittingDao> xhbSittingDao = Optional.empty();
    private Optional<XhbScheduledHearingDao> xhbScheduledHearingDao = Optional.empty();

    public void validateNodeMap(Map<String, String> nodesMap, String lastEntryName) {
        if (Arrays.asList(COURTSITE_NODES).contains(lastEntryName)) {
            xhbCourtSiteDao = validateCourtSite(nodesMap);
            validateHearingList(nodesMap);
        } else if (Arrays.asList(COURTROOM_NODES).contains(lastEntryName)) {
            xhbCourtRoomDao = validateCourtRoom(nodesMap);
        } else if (Arrays.asList(SITTING_NODES).contains(lastEntryName)) {
            xhbSittingDao = validateSitting(nodesMap);
        } else if (Arrays.asList(CASE_NODES).contains(lastEntryName)) {
            xhbCaseDao = validateCase(nodesMap);
            xhbRefHearingTypeDao = validateHearingType(nodesMap);
            xhbHearingDao = validateHearing(nodesMap);
            xhbScheduledHearingDao = validateScheduledHearing(nodesMap);
            validateCrLiveDisplay();
        } else if (Arrays.asList(DEFENDANT_NODES).contains(lastEntryName)) {
            xhbDefendantDao = validateDefendant(nodesMap);
            xhbDefendantOnCaseDao = validateDefendantOnCase();
            validateSchedHearingDefendant();
        }
    }

    private Optional<XhbCourtSiteDao> validateCourtSite(Map<String, String> nodesMap) {
        LOG.info("validateCourtSite()");
        String courtHouseName = nodesMap.get(COURTHOUSENAME);
        String courtHouseCode = nodesMap.get(COURTHOUSECODE);
        if (courtHouseName != null && courtHouseCode != null) {
            return dataHelper.validateCourtSite(courtHouseName, courtHouseCode);
        }
        return Optional.empty();
    }

    private Optional<XhbCourtRoomDao> validateCourtRoom(Map<String, String> nodesMap) {
        LOG.info("validateCourtRoom()");
        if (xhbCourtSiteDao.isPresent()) {
            Integer courtSiteId = xhbCourtSiteDao.get().getCourtSiteId();
            Integer crestCourtRoomNo = Integer.valueOf(nodesMap.get(COURTROOMNO));
            if (courtSiteId != null && crestCourtRoomNo != null) {
                return dataHelper.validateCourtRoom(courtSiteId, crestCourtRoomNo);
            }
        }
        return Optional.empty();
    }

    private void validateHearingList(Map<String, String> nodesMap) {
        LOG.info("validateHearingList()");
        if (xhbCourtSiteDao.isPresent()) {
            Integer courtId = xhbCourtSiteDao.get().getCourtId();
            Integer crestListId = 1;
            String listType = "D";
            String status = nodesMap.get(VERSION).substring(0, 5);
            String startDateString = nodesMap.get(STARTDATE);
            LocalDateTime startDate = parseDateTime(startDateString, DateTimeFormatter.ISO_DATE);
            String publishedTimeString = nodesMap.get(PUBLISHEDTIME);
            LocalDateTime publishedTime =
                parseDateTime(publishedTimeString, DateTimeFormatter.ISO_DATE_TIME);
            final String printReference = "/";
            if (courtId != null && status != null && startDate != null) {
                dataHelper.validateHearingList(courtId, crestListId, listType, status, startDate,
                    publishedTime, printReference);
            }
        }
    }

    private LocalDateTime parseDateTime(String dateAsString, DateTimeFormatter dateFormat) {
        try {
            if (DateTimeFormatter.ISO_TIME.equals(dateFormat)
                || TWELVEHOURTIMEFORMAT.equals(dateFormat)) {
                return LocalTime.parse(dateAsString, dateFormat).atDate(LocalDate.now());
            } else if (DateTimeFormatter.ISO_DATE.equals(dateFormat)) {
                return LocalDate.parse(dateAsString, dateFormat).atStartOfDay();
            } else {
                return LocalDateTime.parse(dateAsString, dateFormat);
            }
        } catch (DateTimeParseException ex) {
            LOG.error("Unable to format date string {} to {}", dateAsString, dateFormat);
            return null;
        }
    }

    private Optional<XhbSittingDao> validateSitting(Map<String, String> nodesMap) {
        LOG.info("validateSitting()");
        if (xhbCourtRoomDao.isPresent()) {
            Integer courtSiteId = xhbCourtRoomDao.get().getCourtSiteId();
            Integer courtRoomId = xhbCourtRoomDao.get().getCourtRoomId();
            String floating = "N";
            String sittingTimeString = nodesMap.get(SITTINGTIME);
            LocalDateTime sittingTime =
                parseDateTime(sittingTimeString, DateTimeFormatter.ISO_TIME);
            if (sittingTime != null) {
                return dataHelper.validateSitting(courtSiteId, courtRoomId, floating, sittingTime);
            }
        }
        return Optional.empty();
    }

    private Optional<XhbCaseDao> validateCase(Map<String, String> nodesMap) {
        LOG.info("validateCase()");
        if (xhbCourtSiteDao.isPresent()) {
            Integer courtId = xhbCourtSiteDao.get().getCourtId();
            String caseType = nodesMap.get(CASENUMBER).substring(0, 1);
            String caseNumber = nodesMap.get(CASENUMBER).substring(1);
            if (caseType != null && caseNumber != null) {
                return dataHelper.validateCase(courtId, caseType, Integer.valueOf(caseNumber));
            }
        }
        return Optional.empty();
    }

    private Optional<XhbDefendantDao> validateDefendant(Map<String, String> nodesMap) {
        LOG.info("validateDefendant()");
        if (xhbCourtSiteDao.isPresent()) {
            Integer courtId = xhbCourtSiteDao.get().getCourtId();
            String firstName = nodesMap.get(FIRSTNAME);
            String middleName = nodesMap.get(FIRSTNAME);
            String surname = nodesMap.get(SURNAME);
            String genderAsString = nodesMap.get(GENDER);
            Integer gender = null;
            if (MALE.equalsIgnoreCase(genderAsString)) {
                gender = 1;
            } else if (FEMALE.equalsIgnoreCase(genderAsString)) {
                gender = 2;
            }
            String dateOfBirthAsString = nodesMap.get(DATEOFBIRTH);
            LocalDateTime dateOfBirth =
                parseDateTime(dateOfBirthAsString, DateTimeFormatter.ISO_DATE);
            if (firstName != null && surname != null) {
                return dataHelper.validateDefendant(courtId, firstName, middleName, surname, gender,
                    dateOfBirth);
            }
        }
        return Optional.empty();
    }

    private Optional<XhbDefendantOnCaseDao> validateDefendantOnCase() {
        LOG.info("validateDefendantOnCase()");
        if (xhbCaseDao.isPresent() && xhbDefendantDao.isPresent()) {
            Integer caseId = xhbCaseDao.get().getCaseId();
            Integer defendantId = xhbDefendantDao.get().getDefendantId();
            if (caseId != null && defendantId != null) {
                return dataHelper.validateDefendantOnCase(caseId, defendantId);
            }
        }
        return Optional.empty();
    }

    private Optional<XhbRefHearingTypeDao> validateHearingType(Map<String, String> nodesMap) {
        LOG.info("validateHearingType()");
        if (xhbCourtSiteDao.isPresent()) {
            Integer courtId = xhbCourtSiteDao.get().getCourtId();
            String hearingTypeCode = nodesMap.get(HEARINGTYPECODE);
            String hearingTypeDesc = nodesMap.get(HEARINGTYPEDESC);
            String category = nodesMap.get(CATEGORY).substring(0, 1);
            if (hearingTypeCode != null && hearingTypeDesc != null && category != null) {
                return dataHelper.validateHearingType(courtId, hearingTypeCode, hearingTypeDesc,
                    category);
            }
        }
        return Optional.empty();
    }

    private Optional<XhbHearingDao> validateHearing(Map<String, String> nodesMap) {
        LOG.info("validateHearing()");
        if (xhbRefHearingTypeDao.isPresent() && xhbCaseDao.isPresent()) {
            Integer courtId = xhbCaseDao.get().getCourtId();
            Integer caseId = xhbCaseDao.get().getCaseId();
            Integer refHearingTypeId = xhbRefHearingTypeDao.get().getRefHearingTypeId();
            String hearingStartDateString = nodesMap.get(STARTDATE);
            LocalDateTime hearingStartDate =
                parseDateTime(hearingStartDateString, DateTimeFormatter.ISO_DATE);
            if (hearingStartDate != null) {
                return dataHelper.validateHearing(courtId, caseId, refHearingTypeId,
                    hearingStartDate);
            }
        }
        return Optional.empty();
    }

    private Optional<XhbScheduledHearingDao> validateScheduledHearing(
        Map<String, String> nodesMap) {
        LOG.info("validateScheduledHearing()");
        if (xhbSittingDao.isPresent() && xhbHearingDao.isPresent()) {
            Integer sittingId = xhbSittingDao.get().getSittingId();
            Integer hearingId = xhbHearingDao.get().getHearingId();
            String notBeforeTimeString = getTime(nodesMap.get(NOTBEFORETIME)
                .toLowerCase(Locale.getDefault()).replaceAll(WHITESPACE_REGEX, EMPTY_STRING));
            LocalDateTime notBeforeTime = parseDateTime(notBeforeTimeString, TWELVEHOURTIMEFORMAT);
            if (notBeforeTime != null) {
                return dataHelper.validateScheduledHearing(sittingId, hearingId, notBeforeTime);
            }
        }
        return Optional.empty();
    }

    private String getTime(String notBeforeTimeString) {
        // Find the first digit in the string
        Matcher matcher = Pattern.compile(DECIMALS_REGEX).matcher(notBeforeTimeString);
        // Return the string from the point of the first digit onwards
        return matcher.find() ? notBeforeTimeString.substring(matcher.start()) : null;
    }

    private Optional<XhbSchedHearingDefendantDao> validateSchedHearingDefendant() {
        LOG.info("validateSchedHearingDefendant()");
        if (xhbScheduledHearingDao.isPresent() && xhbDefendantOnCaseDao.isPresent()) {
            Integer scheduledHearingId = xhbScheduledHearingDao.get().getScheduledHearingId();
            Integer defendantOnCaseId = xhbDefendantOnCaseDao.get().getDefendantOnCaseId();
            if (scheduledHearingId != null && defendantOnCaseId != null) {
                return dataHelper.validateSchedHearingDefendant(scheduledHearingId,
                    defendantOnCaseId);
            }
        }
        return Optional.empty();
    }

    private void validateCrLiveDisplay() {
        LOG.info("validateCrLiveDisplay()");
        if (xhbScheduledHearingDao.isPresent() && xhbCourtRoomDao.isPresent()) {
            Integer courtRoomId = xhbCourtRoomDao.get().getCourtRoomId();
            Integer scheduledHearingId = xhbScheduledHearingDao.get().getScheduledHearingId();
            if (courtRoomId != null && scheduledHearingId != null) {
                dataHelper.validateCrLiveDisplay(courtRoomId, scheduledHearingId,
                    LocalDateTime.now());
            }
        }
    }
}
