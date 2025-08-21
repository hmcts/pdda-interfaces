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
import uk.gov.hmcts.pdda.business.entities.xhbrefjudge.XhbRefJudgeDao;
import uk.gov.hmcts.pdda.business.entities.xhbschedhearingattendee.XhbSchedHearingAttendeeDao;
import uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant.XhbSchedHearingDefendantDao;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingDao;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**

 * Title: ListObjectHelper.


 * Description:


 * Copyright: Copyright (c) 2024


 * Company: CGI

 * @author HarrisM
 * @version 1.0
 */
@SuppressWarnings("PMD")
public class ListObjectHelper implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(ListObjectHelper.class);

    public static final String ROOTNODE = "DailyList";
    public static final String LISTHEADER_NODE = "ListHeader";
    public static final String COURTLIST_NODE = "CourtLists/CourtList";
    public static final String COURTHOUSE_NODE = "CourtHouse";
    public static final String SITTING_NODE = "Sittings/Sitting";
    public static final String JUDGE_NODE = "Judiciary/Judge";
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
    protected static final String ISMASKED = "cs:IsMasked";
    protected static final String VERSION = "cs:Version";
    protected static final String TITLE = "apd:CitizenNameTitle";
    
    //private static final String EMPTY_STRING = "";
    private static final String DECIMALS_REGEX = "\\d+";
    private static final String TWELVEHOURTIME = "hh:mma";
    //private static final String WHITESPACE_REGEX = "\\s";

    private static final String CPP = "CPP";
    private static final String[] NUMBERED_NODES = {FIRSTNAME};
    private static final DateTimeFormatter TWELVEHOURTIMEFORMAT =
        new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("hh:mm a")
            .toFormatter(Locale.ENGLISH);

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
    private transient Optional<XhbRefJudgeDao> xhbRefJudgeDao = Optional.empty();

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
            processJudgeRecords();
        } else if (breadcrumb.contains(JUDGE_NODE)) {
            xhbRefJudgeDao = validateJudge(nodesMap);
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
        LOG.debug("Court house name: {}, code: {}", courtHouseName, courtHouseCode);
        if (courtHouseName != null && courtHouseCode != null) {
            LOG.debug("Validating court site with name: {} and code: {}", courtHouseName, courtHouseCode);
            return dataHelper.validateCourtSite(courtHouseName, courtHouseCode);
        }
        return Optional.empty();
    }

    private Optional<XhbCourtRoomDao> validateCourtRoom(Map<String, String> nodesMap) {
        LOG.info("validateCourtRoom()");
        LOG.debug("xhbCourtSiteDao is present: {}", xhbCourtSiteDao.isPresent());
        if (xhbCourtSiteDao.isPresent()) {
            Integer courtSiteId = xhbCourtSiteDao.get().getCourtSiteId();
            String courtRoomNoString = nodesMap.get(COURTROOMNO);
            Integer crestCourtRoomNo = getInteger(courtRoomNoString);
            LOG.debug("Court site ID: {}, Court room number: {}", courtSiteId, crestCourtRoomNo);
            if (courtSiteId != null && crestCourtRoomNo != null) {
                LOG.debug("Validating court room with site ID: {} and room number: {}", courtSiteId, crestCourtRoomNo);
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
        LOG.debug("xhbCourtSiteDao is present: {}", xhbCourtSiteDao.isPresent());
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
            LOG.debug("Court ID: {}, Status: {}, Start Date: {}, Published Time: {}, Print Reference: {}, "
                + "Edition No: {}, List Court Type: {}",
                courtId, status, startDate, publishedTime, printReference, editionNo, listCourtType);
            if (courtId != null && status != null && startDate != null) {
                LOG.debug("Validating hearing list for court ID: {}, status: {}, start date: {}",
                    courtId, status, startDate);
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

    /*
     * private LocalDateTime parseDateTime(String dateAsString, DateTimeFormatter dateFormat) { try
     * { if (dateAsString == null) { return null; } else if
     * (DateTimeFormatter.ISO_TIME.equals(dateFormat) || TWELVEHOURTIMEFORMAT.equals(dateFormat)) {
     * return LocalTime.parse(dateAsString, dateFormat).atDate(LocalDate.now()); } else if
     * (DateTimeFormatter.ISO_DATE.equals(dateFormat)) { return LocalDate.parse(dateAsString,
     * dateFormat).atStartOfDay(); } else { return LocalDateTime.parse(dateAsString, dateFormat); }
     * } catch (DateTimeParseException ex) { LOG.error("Unable to format date string {} to {}",
     * dateAsString, dateFormat); return null; } }
     */

    private Optional<XhbSittingDao> validateSitting(Map<String, String> nodesMap) {
        LOG.info("validateSitting()");
        LOG.debug("xhbCourtRoomDao is present: {}, xhbHearingListDao is present: {}",
            xhbCourtRoomDao.isPresent(), xhbHearingListDao.isPresent());
        if (xhbCourtRoomDao.isPresent() && xhbHearingListDao.isPresent()) {
            Integer courtSiteId = xhbCourtRoomDao.get().getCourtSiteId();
            Integer courtRoomId = xhbCourtRoomDao.get().getCourtRoomId();
            final String floating = "N";
            final Integer listId = xhbHearingListDao.get().getListId();
            final LocalDateTime listDate = xhbHearingListDao.get().getStartDate();
            String sittingTimeString = nodesMap.get(SITTINGTIME);
            //LocalDateTime sittingTime =
            //    parseDateTime(sittingTimeString, DateTimeFormatter.ISO_TIME);
            LocalTime sittingTimeAsTime = LocalTime.parse(sittingTimeString.trim(), DateTimeFormatter.ISO_LOCAL_TIME);
            LocalDateTime sittingTime = LocalDateTime.of(listDate.toLocalDate(), sittingTimeAsTime);
            LOG.debug("Court site ID: {}, Court room ID: {}, Floating: {}, List ID: {}, Sitting time: {}",
                courtSiteId, courtRoomId, floating, listId, sittingTime);
            if (sittingTime != null) {
                LOG.debug("Validating sitting for court site ID: {}, court room ID: {}, time: {}",
                    courtSiteId, courtRoomId, sittingTime);
                return dataHelper.validateSitting(courtSiteId, courtRoomId, floating, sittingTime,
                    listId);
            }
        }
        return Optional.empty();
    }

    private Optional<XhbCaseDao> validateCase(Map<String, String> nodesMap) {
        LOG.info("validateCase()");
        LOG.debug("xhbCourtSiteDao is present: {}", xhbCourtSiteDao.isPresent());
        if (xhbCourtSiteDao.isPresent()) {
            Integer courtId = xhbCourtSiteDao.get().getCourtId();
            String caseTypeAndNumber = nodesMap.get(CASENUMBER);
            if (!CPP.equals(caseTypeAndNumber)) {
                LOG.debug("Case type and number: {}", caseTypeAndNumber);
                String caseType = getSubstring(caseTypeAndNumber, 0, 1);
                String caseNumber = getSubstring(caseTypeAndNumber, 1, null);
                if (caseType != null && caseNumber != null) {
                    LOG.debug("Validating case for court ID: {}, type: {}, number: {}", courtId, caseType, caseNumber);
                    return dataHelper.validateCase(courtId, caseType, getInteger(caseNumber));
                }
            }
        }
        return Optional.empty();
    }

    private Optional<XhbRefJudgeDao> validateJudge(Map<String, String> nodesMap) {
        LOG.info("validateJudge()");
        LOG.debug("xhbCourtSiteDao is present: {}", xhbCourtSiteDao.isPresent());
        if (xhbCourtSiteDao.isPresent()) {
            Integer courtId = xhbCourtSiteDao.get().getCourtId();
            String judgeTitle = nodesMap.get(TITLE);
            String judgeFirstname = nodesMap.get(FIRSTNAME + ".1");
            String judgeSurname = nodesMap.get(SURNAME);
            LOG.debug("Court ID: {}, Judge Title: {}, Firstname: {}, Surname: {}",
                courtId, judgeTitle, judgeFirstname, judgeSurname);
            return dataHelper.validateJudge(courtId, judgeTitle, judgeFirstname, judgeSurname);
        }
        return Optional.empty();
    }
    
    protected void processJudgeRecords() {
        LOG.debug("processJudgeRecords()");
        LOG.debug("xhbScheduledHearingDao is present: {}, xhbRefJudgeDao is present: {}",
            xhbScheduledHearingDao.isPresent(), xhbRefJudgeDao.isPresent());
        if (!xhbScheduledHearingDao.isEmpty() && !xhbRefJudgeDao.isEmpty()) {
            // Create the XhbSchedHearingAttendee record
            Optional<XhbSchedHearingAttendeeDao> xhbSchedHearingAttendeeDao =
                dataHelper.createSchedHearingAttendee("J",
                xhbScheduledHearingDao.get().getScheduledHearingId(),
                xhbRefJudgeDao.get().getRefJudgeId());
            if (!xhbSchedHearingAttendeeDao.isEmpty()) {
                LOG.debug("Creating new XhbSchedHearingJudge with ShAttendeeID: {}",
                    xhbSchedHearingAttendeeDao.get().getShAttendeeId());
                // Create the XhbShJudge record
                dataHelper.createShJudge("N", xhbRefJudgeDao.get().getRefJudgeId(),
                    xhbSchedHearingAttendeeDao.get().getShAttendeeId());
            }
        }
    }
    
    private Optional<XhbDefendantDao> validateDefendant(Map<String, String> nodesMap) {
        LOG.info("validateDefendant()");
        if (xhbCourtSiteDao.isPresent() && xhbCaseDao.isPresent()) {
            String firstName = nodesMap.get(FIRSTNAME + ".1");
            String surname = nodesMap.get(SURNAME);
            // Correct any invalid data with only a surname populated
            if (firstName == null && surname != null && surname.contains(",")) {
                int commaPosition = surname.indexOf(',');
                firstName = surname.substring(commaPosition + 1).trim();
                surname = surname.substring(0, commaPosition);
            }
            LOG.debug("Validating defendant with first name: {}, surname: {}", firstName, surname);
            String publicDisplayHide = "N";
            if (nodesMap.get(ISMASKED) != null) {
                publicDisplayHide = nodesMap.get(ISMASKED).toUpperCase().substring(0, 1);
            }
            LOG.debug("Public display hide: {}", publicDisplayHide);
            String genderAsString = nodesMap.get(GENDER);
            Integer gender = null;
            if (MALE.equalsIgnoreCase(genderAsString)) {
                gender = 1;
            } else if (FEMALE.equalsIgnoreCase(genderAsString)) {
                gender = 2;
            }
            LOG.debug("Gender: {}", genderAsString);
            String dateOfBirthAsString = nodesMap.get(DATEOFBIRTH);
            LocalDateTime dateOfBirth =
                parseDateTime(dateOfBirthAsString, DateTimeFormatter.ISO_DATE);
            LOG.debug("Date of birth: {}", dateOfBirthAsString);
            if (firstName != null && surname != null) {
                LOG.debug("First name: {}, Surname: {}", firstName, surname);
                Integer courtId = xhbCourtSiteDao.get().getCourtId();
                String middleName = nodesMap.get(FIRSTNAME + ".2");
                LOG.debug("CourtId {}, Middle name: {}", courtId, middleName);
                return dataHelper.validateDefendant(courtId, firstName, middleName, surname, gender,
                    dateOfBirth, publicDisplayHide);
            }
        }
        return Optional.empty();
    }

    private Optional<XhbDefendantOnCaseDao> validateDefendantOnCase() {
        LOG.info("validateDefendantOnCase()");
        LOG.debug("xhbCaseDao is present: {}, xhbDefendantDao is present: {}",
            xhbCaseDao.isPresent(), xhbDefendantDao.isPresent());
        if (xhbCaseDao.isPresent() && xhbDefendantDao.isPresent()) {
            Integer caseId = xhbCaseDao.get().getCaseId();
            Integer defendantId = xhbDefendantDao.get().getDefendantId();
            String publicDisplayHide = xhbDefendantDao.get().getPublicDisplayHide();
            LOG.debug("Case ID: {}, Defendant ID: {}, Public Display Hide: {}",
                caseId, defendantId, publicDisplayHide);
            if (caseId != null && defendantId != null) {
                LOG.debug("Validating defendant on case for case ID: {}, defendant ID: {},"
                    + "public display hide: {}",
                    caseId, defendantId, publicDisplayHide);
                return dataHelper.validateDefendantOnCase(caseId, defendantId, publicDisplayHide);
            }
        }
        return Optional.empty();
    }

    private Optional<XhbCaseDao> updateCaseTitle(Map<String, String> nodesMap) {
        LOG.info("updateCaseTitle()");
        try {
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
        } catch (Exception e) {
            // If this fails its not critical, just log it
            LOG.error("Failed to update case title for caseId: {}",
                xhbCaseDao.map(XhbCaseDao::getCaseId).orElse(null), e);
        }
        return Optional.empty();
    }

    private Optional<XhbRefHearingTypeDao> validateHearingType(Map<String, String> nodesMap) {
        LOG.info("validateHearingType()");
        LOG.debug("xhbCourtSiteDao is present: {}", xhbCourtSiteDao.isPresent());
        if (xhbCourtSiteDao.isPresent()) {
            Integer courtId = xhbCourtSiteDao.get().getCourtId();
            String hearingTypeCode = nodesMap.get(HEARINGTYPECODE);
            String hearingTypeDesc = nodesMap.get(HEARINGTYPEDESC).replaceAll("\s{2,}", " ");
            String category = getSubstring(nodesMap.get(CATEGORY), 0, 1);
            LOG.debug("Court ID: {}, Hearing Type Code: {}, Hearing Type Description: {}, Category: {}",
                courtId, hearingTypeCode, hearingTypeDesc, category);
            if (hearingTypeCode != null && hearingTypeDesc != null && category != null) {
                LOG.debug("Validating hearing type for court ID: {}, code: {}, description: {}, category: {}",
                    courtId, hearingTypeCode, hearingTypeDesc, category);
                return dataHelper.validateHearingType(courtId, hearingTypeCode, hearingTypeDesc,
                    category);
            }
        }
        return Optional.empty();
    }

    private Optional<XhbHearingDao> validateHearing(Map<String, String> nodesMap) {
        LOG.info("validateHearing()");
        LOG.debug("xhbRefHearingTypeDao is present: {}, xhbCaseDao is present: {}",
            xhbRefHearingTypeDao.isPresent(), xhbCaseDao.isPresent());
        // Validate hearing only if both refHearingType and case are present
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
            LOG.debug("Court ID: {}, Case ID: {}, Hearing Type ID: {}, Start Date: {}, End Date: {}",
                courtId, caseId, refHearingTypeId, hearingStartDate, hearingEndDate);
            if (hearingStartDate != null) {
                LOG.debug("Validating hearing for court ID: {}, case ID: {}, hearing type ID: {},"
                    + "start date: {}, end date: {}",
                    courtId, caseId, refHearingTypeId, hearingStartDate, hearingEndDate);
                return dataHelper.validateHearing(courtId, caseId, refHearingTypeId,
                    hearingStartDate, hearingEndDate);
            }
        }
        return Optional.empty();
    }

    private Optional<XhbScheduledHearingDao> validateScheduledHearing(
        Map<String, String> nodesMap) {
        LOG.info("validateScheduledHearing()");
        LOG.debug("xhbCaseDao is present: {}, xhbSittingDao is present: {}, xhbHearingDao is present: {}",
            xhbCaseDao.isPresent(), xhbSittingDao.isPresent(), xhbHearingDao.isPresent());
        // Validate scheduled hearing only if case, sitting and hearing are present
        if (xhbCaseDao.isPresent() && xhbSittingDao.isPresent() && xhbHearingDao.isPresent()) {
            Integer sittingId = xhbSittingDao.get().getSittingId();
            Integer hearingId = xhbHearingDao.get().getHearingId();
            String notBeforeTimeString = getTime(nodesMap.get(NOTBEFORETIME));
            
            LocalDateTime sittingDateTime = xhbSittingDao.get().getSittingTime();
            LocalDateTime notBeforeTime;
            if (notBeforeTimeString == null || notBeforeTimeString.isBlank()) {
                // no override → use the sitting time as-is
                notBeforeTime = sittingDateTime;
            } else {
                String s = notBeforeTimeString.trim();
                LocalTime nbTime;

                try {
                    // 24h formats like 10:00 or 10:00:00
                    nbTime = LocalTime.parse(s, DateTimeFormatter.ISO_LOCAL_TIME);
                } catch (DateTimeParseException ignore) {
                    // 12h formats like 11:00 am / 11:00am / 11 am
                    DateTimeFormatter twelveHour = new DateTimeFormatterBuilder()
                        .parseCaseInsensitive()
                        .appendPattern("h[:mm][:ss][.SSS]")
                        .optionalStart().appendLiteral(' ').optionalEnd()
                        .appendPattern("a") // AM/PM
                        .toFormatter(Locale.UK);
                    nbTime = LocalTime.parse(s, twelveHour);
                }

                notBeforeTime = sittingDateTime.toLocalDate().atTime(nbTime);
            }
            
            LOG.debug("Sitting ID: {}, Hearing ID: {}, Not Before Time: {}",
                sittingId, hearingId, notBeforeTime);
            if (notBeforeTime != null) {
                LOG.debug("Validating scheduled hearing for sitting ID: {}, hearing ID: {}, not before time: {}",
                    sittingId, hearingId, notBeforeTime);
                return dataHelper.validateScheduledHearing(sittingId, hearingId, notBeforeTime);
            }
        }
        return Optional.empty();
    }

    /*
     * private String getTime(String notBeforeTimeString) { if (notBeforeTimeString != null) {
     * String result = notBeforeTimeString.toLowerCase(Locale.getDefault())
     * .replaceAll(WHITESPACE_REGEX, EMPTY_STRING); // Find the first digit in the string Matcher
     * matcher = Pattern.compile(DECIMALS_REGEX).matcher(result); // Return the string from the
     * point of the first digit onwards if (matcher.find()) { return
     * result.substring(matcher.start()); } } return null; }
     */

    private Optional<XhbSchedHearingDefendantDao> validateSchedHearingDefendant() {
        LOG.info("validateSchedHearingDefendant()");
        LOG.debug("xhbScheduledHearingDao is present: {}, xhbDefendantOnCaseDao is present: {}",
            xhbScheduledHearingDao.isPresent(), xhbDefendantOnCaseDao.isPresent());
        // Validate scheduled hearing defendant only if both scheduled hearing and defendant on case are present
        if (xhbScheduledHearingDao.isPresent() && xhbDefendantOnCaseDao.isPresent()) {
            Integer scheduledHearingId = xhbScheduledHearingDao.get().getScheduledHearingId();
            Integer defendantOnCaseId = xhbDefendantOnCaseDao.get().getDefendantOnCaseId();
            LOG.debug("Scheduled Hearing ID: {}, Defendant On Case ID: {}",
                scheduledHearingId, defendantOnCaseId);
            if (scheduledHearingId != null && defendantOnCaseId != null) {
                LOG.debug("Validating scheduled hearing defendant for scheduled hearing ID: {}, "
                    + "defendant on case ID: {}",
                    scheduledHearingId, defendantOnCaseId);
                return dataHelper.validateSchedHearingDefendant(scheduledHearingId,
                    defendantOnCaseId);
            }
        }
        return Optional.empty();
    }

    private void validateCrLiveDisplay() {
        LOG.info("validateCrLiveDisplay()");
        LOG.debug("xhbScheduledHearingDao is present: {}, xhbCourtRoomDao is present: {}",
            xhbScheduledHearingDao.isPresent(), xhbCourtRoomDao.isPresent());
        // Validate CR Live Display only if both scheduled hearing and court room are present
        if (xhbScheduledHearingDao.isPresent() && xhbCourtRoomDao.isPresent()) {
            Integer courtRoomId = xhbCourtRoomDao.get().getCourtRoomId();
            Integer scheduledHearingId = xhbScheduledHearingDao.get().getScheduledHearingId();
            LOG.debug("Court Room ID: {}, Scheduled Hearing ID: {}",
                courtRoomId, scheduledHearingId);
            if (courtRoomId != null && scheduledHearingId != null) {
                LOG.debug("Validating CR Live Display for court room ID: {}, scheduled hearing ID: {}",
                    courtRoomId, scheduledHearingId);
                dataHelper.validateCrLiveDisplay(courtRoomId, scheduledHearingId,
                    LocalDateTime.now());
            }
        }
    }

    public boolean isNumberedNode(String nodeName) {
        return Arrays.asList(NUMBERED_NODES).contains(nodeName);
    }
    
    
    ///// METHODS FOR ASSISTING WITH TIME AND DATE PARSING /////
    /// These methods are used to extract and parse time and date strings
    
    private String getTime(String notBeforeTimeString) {
        if (notBeforeTimeString != null) {
            String result = notBeforeTimeString.toLowerCase(Locale.ENGLISH);
            Matcher matcher = Pattern.compile(DECIMALS_REGEX).matcher(result);
            if (matcher.find()) {
                return result.substring(matcher.start()).replaceAll("\\s+", " ");
            }
        }
        return null;
    }

    
    
    private LocalDateTime parseDateTime(String dateAsString, DateTimeFormatter dateFormat) {
        if (dateAsString == null) {
            return null;
        }

        try {
            // Normalize to "hh:mm a" format
            String normalized = normalizeTimeString(dateAsString.trim());

            if (isTimeOnlyFormatter(dateFormat)) {
                return LocalTime.parse(normalized, dateFormat).atDate(LocalDate.now());
            } else if (dateFormat == DateTimeFormatter.ISO_DATE) {
                return LocalDate.parse(normalized, dateFormat).atStartOfDay();
            } else {
                return LocalDateTime.parse(normalized, dateFormat);
            }
        } catch (DateTimeParseException ex) {
            System.err.printf("Unable to parse date string '%s' with format '%s'%n", dateAsString, dateFormat);
            return null;
        }
    }


    
    private boolean isTimeOnlyFormatter(DateTimeFormatter formatter) {
        return formatter.toString().contains("ClockHourOfAmPm")
            || formatter == DateTimeFormatter.ISO_TIME;
    }
    
    
    private String normalizeTimeString(String input) {
        // Normalize AM/PM spacing (e.g., 11:00AM → 11:00 AM)
        input = input.replaceAll("(?i)(\\d)(am|pm)$", "$1 $2");

        // Pattern: hh:mm with optional am/pm
        Matcher m = Pattern.compile("(?i)(\\d{1,2}):(\\d{1,2})(?:\\s*(am|pm))?").matcher(input);
        if (m.matches()) {
            int hour = Integer.parseInt(m.group(1));
            int minute = Integer.parseInt(m.group(2));
            String ampm = m.group(3);

            // Default AM/PM if not provided
            if (ampm == null) {
                if (hour >= 10 && hour < 12) {
                    ampm = "am";
                } else {
                    ampm = "pm";
                }
            } else {
                ampm = ampm.toLowerCase();
            }

            return String.format("%02d:%02d %s", hour, minute, ampm);
        }

        // fallback if it doesn't match expected structure
        return input;
    }
}
