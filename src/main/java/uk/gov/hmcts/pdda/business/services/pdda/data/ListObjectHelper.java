package uk.gov.hmcts.pdda.business.services.pdda.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbcase.XhbCaseDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import uk.gov.hmcts.pdda.business.entities.xhbdefendant.XhbDefendantDao;
import uk.gov.hmcts.pdda.business.entities.xhbdefendantoncase.XhbDefendantOnCaseDao;
import uk.gov.hmcts.pdda.business.entities.xhbhearing.XhbHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbhearinglist.XhbHearingListDao;
import uk.gov.hmcts.pdda.business.entities.xhbrefhearingtype.XhbRefHearingTypeDao;
import uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant.XhbSchedHearingDefendantDao;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingDao;

import java.io.Serializable;
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
@SuppressWarnings({"PMD.NullAssignment", "PMD.TooManyMethods", "PMD.ExcessiveParameterList",
    "PMD.CyclomaticComplexity"})
public class ListObjectHelper implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(ListObjectHelper.class);

    public static final String ROOTNODE = "DailyList";
    public static final String LISTHEADER_NODE = "ListHeader";
    public static final String COURTLIST_NODE = "CourtLists/CourtList";
    public static final String COURTHOUSE_NODE = "CourtHouse";
    public static final String SITTING_NODE = "Sittings/Sitting";
    public static final String HEARING_NODE = "Hearings/Hearing";
    public static final String HEARINGDETAILS_NODE = "HearingDetails";
    public static final String DEFENDANT_NODE = "Defendants/Defendant/PersonalDetails";
    public static final String DEFENDANTNAME_NODE = "Name";

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
    protected static final String ENDDATE = "cs:EndDate";
    protected static final String SURNAME = "apd:CitizenNameSurname";
    protected static final String VERSION = "cs:Version";
    private static final String EMPTY_STRING = "";
    private static final String DECIMALS_REGEX = "\\d+";
    private static final String TWELVEHOURTIME = "hh:mma";
    private static final String WHITESPACE_REGEX = "\\s";

    private static final String CPP = "CPP";
    private static final String[] NUMBERED_NODES = {FIRSTNAME};
    private static final DateTimeFormatter TWELVEHOURTIMEFORMAT =
        DateTimeFormatter.ofPattern(TWELVEHOURTIME);

    private DataHelper dataHelper = new DataHelper();
    private transient Optional<XhbCourtSiteDao> xhbCourtSiteDao = Optional.empty();
    private transient Optional<XhbCourtRoomDao> xhbCourtRoomDao = Optional.empty();
    private transient Optional<XhbCaseDao> xhbCaseDao = Optional.empty();
    private transient Optional<XhbDefendantDao> xhbDefendantDao = Optional.empty();
    private transient Optional<XhbDefendantOnCaseDao> xhbDefendantOnCaseDao = Optional.empty();
    private transient Optional<XhbHearingListDao> xhbHearingListDao = Optional.empty();
    private transient Optional<XhbRefHearingTypeDao> xhbRefHearingTypeDao = Optional.empty();
    private transient Optional<XhbHearingDao> xhbHearingDao = Optional.empty();
    private transient Optional<XhbSittingDao> xhbSittingDao = Optional.empty();
    private transient Optional<XhbScheduledHearingDao> xhbScheduledHearingDao = Optional.empty();

    public ListObjectHelper() {
        // Default constructor
    }

    public ListObjectHelper(DataHelper dataHelper) {
        this.dataHelper = dataHelper;
    }

    public void validateNodeMap(Map<String, String> nodesMap, String breadcrumb) {
        if (breadcrumb.contains(COURTLIST_NODE)) {
            xhbCourtSiteDao = validateCourtSite(nodesMap);
            xhbHearingListDao = validateHearingList(nodesMap);
        } else if (breadcrumb.contains(SITTING_NODE)) {
            xhbCourtRoomDao = validateCourtRoom(nodesMap);
            xhbSittingDao = validateSitting(nodesMap);
        } else if (breadcrumb.contains(HEARING_NODE)) {
            xhbRefHearingTypeDao = validateHearingType(nodesMap);
            xhbCaseDao = validateCase(nodesMap);
            xhbHearingDao = validateHearing(nodesMap);
            xhbScheduledHearingDao = validateScheduledHearing(nodesMap);
            validateCrLiveDisplay();
        } else if (breadcrumb.contains(DEFENDANT_NODE)) {
            xhbDefendantDao = validateDefendant(nodesMap);
            xhbDefendantOnCaseDao = validateDefendantOnCase();
            updateCaseTitle(nodesMap);
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
            String courtRoomNoString = nodesMap.get(COURTROOMNO);
            Integer crestCourtRoomNo = getInteger(courtRoomNoString);
            if (courtSiteId != null && crestCourtRoomNo != null) {
                return dataHelper.validateCourtRoom(courtSiteId, crestCourtRoomNo);
            }
        }
        return Optional.empty();
    }

    private Integer getInteger(String string) {
        try {
            return Integer.valueOf(string);
        } catch (NumberFormatException ex) {
            if (string != null) {
                LOG.error("{} is not an Integer", string);
            }
            return null;
        }
    }

    private Optional<XhbHearingListDao> validateHearingList(Map<String, String> nodesMap) {
        LOG.info("validateHearingList()");
        if (xhbCourtSiteDao.isPresent()) {
            Integer courtId = xhbCourtSiteDao.get().getCourtId();
            final Integer crestListId = 1;
            final String listType = "D";
            String status = getSubstring(nodesMap.get(VERSION), 0, 5);
            String startDateString = nodesMap.get(STARTDATE);
            LocalDateTime startDate = parseDateTime(startDateString, DateTimeFormatter.ISO_DATE);
            String publishedTimeString = nodesMap.get(PUBLISHEDTIME);
            LocalDateTime publishedTime =
                parseDateTime(publishedTimeString, DateTimeFormatter.ISO_DATE_TIME);
            final String printReference = "/";
            final Integer editionNo = 1;
            final String listCourtType = "CR";
            if (courtId != null && status != null && startDate != null) {
                return dataHelper.validateHearingList(courtId, crestListId, listType, status,
                    startDate, publishedTime, printReference, editionNo, listCourtType);
            }
        }
        return Optional.empty();
    }

    private String getSubstring(String text, Integer start, Integer end) {
        if (text != null) {
            Integer startPos = Math.min(start, text.length());
            Integer endPos = end != null ? Math.min(end, text.length()) : text.length();
            if (!startPos.equals(endPos)) {
                return text.substring(startPos, endPos);
            }
        }
        return null;
    }

    private LocalDateTime parseDateTime(String dateAsString, DateTimeFormatter dateFormat) {
        try {
            if (dateAsString == null) {
                return null;
            } else if (DateTimeFormatter.ISO_TIME.equals(dateFormat)
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
        if (xhbCourtRoomDao.isPresent() && xhbHearingListDao.isPresent()) {
            Integer courtSiteId = xhbCourtRoomDao.get().getCourtSiteId();
            Integer courtRoomId = xhbCourtRoomDao.get().getCourtRoomId();
            final String floating = "N";
            final Integer listId = xhbHearingListDao.get().getListId();
            String sittingTimeString = nodesMap.get(SITTINGTIME);
            LocalDateTime sittingTime =
                parseDateTime(sittingTimeString, DateTimeFormatter.ISO_TIME);
            if (sittingTime != null) {
                return dataHelper.validateSitting(courtSiteId, courtRoomId, floating, sittingTime,
                    listId);
            }
        }
        return Optional.empty();
    }

    private Optional<XhbCaseDao> validateCase(Map<String, String> nodesMap) {
        LOG.info("validateCase()");
        if (xhbCourtSiteDao.isPresent()) {
            Integer courtId = xhbCourtSiteDao.get().getCourtId();
            String caseTypeAndNumber = nodesMap.get(CASENUMBER);
            if (!CPP.equals(caseTypeAndNumber)) {
                String caseType = getSubstring(caseTypeAndNumber, 0, 1);
                String caseNumber = getSubstring(caseTypeAndNumber, 1, null);
                if (caseType != null && caseNumber != null) {
                    return dataHelper.validateCase(courtId, caseType, getInteger(caseNumber));
                }
            }
        }
        return Optional.empty();
    }

    private Optional<XhbDefendantDao> validateDefendant(Map<String, String> nodesMap) {
        LOG.info("validateDefendant()");
        if (xhbCourtSiteDao.isPresent() && xhbCaseDao.isPresent()) {
            Integer courtId = xhbCourtSiteDao.get().getCourtId();
            String firstName = nodesMap.get(FIRSTNAME + ".1");
            String middleName = nodesMap.get(FIRSTNAME + ".2");
            String surname = nodesMap.get(SURNAME);
            // Correct any invalid data with only a surname populated
            if (firstName == null && surname != null && surname.contains(",")) {
                int commaPosition = surname.indexOf(',');
                firstName = surname.substring(commaPosition + 1).trim();
                surname = surname.substring(0, commaPosition);
            }
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

    private Optional<XhbCaseDao> updateCaseTitle(Map<String, String> nodesMap) {
        if (xhbCaseDao.isPresent()) {
            String firstName = nodesMap.get(FIRSTNAME + ".1");
            String surname = nodesMap.get(SURNAME);
            if (xhbCaseDao.get().getCaseTitle() == null && surname != null) {
                StringBuilder caseTitleSb = new StringBuilder();
                caseTitleSb.append(surname);
                if (firstName != null) {
                    if (!"U".equals(xhbCaseDao.get().getCaseType())
                        && !"B".equals(xhbCaseDao.get().getCaseType())) {
                        caseTitleSb.append(", ");
                    }
                    caseTitleSb.append(firstName);
                }
                return dataHelper.updateCase(xhbCaseDao.get(), caseTitleSb.toString());
            }
            return xhbCaseDao;
        }
        return Optional.empty();
    }

    private Optional<XhbRefHearingTypeDao> validateHearingType(Map<String, String> nodesMap) {
        LOG.info("validateHearingType()");
        if (xhbCourtSiteDao.isPresent()) {
            Integer courtId = xhbCourtSiteDao.get().getCourtId();
            String hearingTypeCode = nodesMap.get(HEARINGTYPECODE);
            String hearingTypeDesc = nodesMap.get(HEARINGTYPEDESC);
            String category = getSubstring(nodesMap.get(CATEGORY), 0, 1);
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
            String hearingEndDateString = nodesMap.get(ENDDATE);
            LocalDateTime hearingStartDate =
                parseDateTime(hearingStartDateString, DateTimeFormatter.ISO_DATE);
            LocalDateTime hearingEndDate =
                parseDateTime(hearingEndDateString, DateTimeFormatter.ISO_DATE);
            if (hearingStartDate != null) {
                return dataHelper.validateHearing(courtId, caseId, refHearingTypeId,
                    hearingStartDate, hearingEndDate);
            }
        }
        return Optional.empty();
    }

    private Optional<XhbScheduledHearingDao> validateScheduledHearing(
        Map<String, String> nodesMap) {
        LOG.info("validateScheduledHearing()");
        if (xhbCaseDao.isPresent() && xhbSittingDao.isPresent() && xhbHearingDao.isPresent()) {
            Integer sittingId = xhbSittingDao.get().getSittingId();
            Integer hearingId = xhbHearingDao.get().getHearingId();
            String notBeforeTimeString = getTime(nodesMap.get(NOTBEFORETIME));
            LocalDateTime notBeforeTime = parseDateTime(notBeforeTimeString, TWELVEHOURTIMEFORMAT);
            if (notBeforeTime != null) {
                return dataHelper.validateScheduledHearing(sittingId, hearingId, notBeforeTime);
            }
        }
        return Optional.empty();
    }

    private String getTime(String notBeforeTimeString) {
        if (notBeforeTimeString != null) {
            String result = notBeforeTimeString.toLowerCase(Locale.getDefault())
                .replaceAll(WHITESPACE_REGEX, EMPTY_STRING);
            // Find the first digit in the string
            Matcher matcher = Pattern.compile(DECIMALS_REGEX).matcher(result);
            // Return the string from the point of the first digit onwards
            if (matcher.find()) {
                return result.substring(matcher.start());
            }
        }
        return null;
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

    public boolean isNumberedNode(String nodeName) {
        return Arrays.asList(NUMBERED_NODES).contains(nodeName);
    }
}
