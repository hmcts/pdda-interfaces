package uk.gov.hmcts;

import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.CourtRoomIdentifier;
import uk.gov.courtservice.xhibit.courtlog.vos.CourtLogSubscriptionValue;
import uk.gov.courtservice.xhibit.courtlog.vos.CourtLogViewValue;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtlogentry.XhbCourtLogEntryDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtlogeventdesc.XhbCourtLogEventDescDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.AllCourtStatusValue;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.CourtDetailValue;
import uk.gov.hmcts.pdda.common.publicdisplay.vos.publicdisplay.CourtSitePdComplexValue;
import uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled.AllCourtStatusCompiledRendererDelegate;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class DummyCourtUtil {

    private static final String EQUALS = "Results are not Equal";
    private static final String NOTEQUALS = "Results are Equal";
    private static final String NOTNULL = "Result is Null";
    private static final String TRUE = "Result is not True";
    private static final String FALSE = "Result is not False";
    private static final String TEST1 = "Test1";
    private static final String TEST2 = "Test2";
    private static final String ONETWOTHREE = "123";
    private static final String CASENUMBER = "20230525";

    private DummyCourtUtil() {
        // Do nothing
    }

    public static XhbCourtSiteDao getXhbCourtSiteDao() {
        Integer courtSiteId = -1;
        String courtSiteName = "SWANSEA";
        String courtSiteCode = "courtSiteCode";
        Integer courtId = -1;
        Integer addressId = -1;
        String displayName = "displayName";
        String crestCourtId = "crestCourtId";
        String shortName = "shortName";
        Integer siteGroup = -1;
        String floaterText = "floaterText";
        String listName = "listName";
        String tier = "tier";
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = TEST2;
        String createdBy = TEST1;
        Integer version = 3;
        String obsInd = "N";
        XhbCourtSiteDao result = new XhbCourtSiteDao();
        result.setCourtSiteId(courtSiteId);
        result.setCourtSiteName(courtSiteName);
        result.setCourtSiteCode(courtSiteCode);
        result.setCourtId(courtId);
        result.setAddressId(addressId);
        result.setDisplayName(displayName);
        result.setCrestCourtId(crestCourtId);
        result.setShortName(shortName);
        result.setSiteGroup(siteGroup);
        result.setFloaterText(floaterText);
        result.setListName(listName);
        result.setTier(tier);
        result.setObsInd(obsInd);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);
        return result;
    }

    public static XhbCourtRoomDao getXhbCourtRoomDao() {
        Integer courtRoomId = -1;
        String courtRoomName = "courtRoomName";
        String description = "description";
        Integer crestCourtRoomNo = -1;
        Integer courtSiteId = -1;
        String displayName = "displayName";
        String securityInd = "securityInd";
        String videoInd = "videoInd";
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = TEST2;
        String createdBy = TEST1;
        Integer version = 3;
        String obsInd = "N";
        XhbCourtRoomDao result = new XhbCourtRoomDao();
        result.setCourtRoomId(courtRoomId);
        result.setCourtRoomName(courtRoomName);
        result.setDescription(description);
        result.setCrestCourtRoomNo(crestCourtRoomNo);
        result.setCourtSiteId(courtSiteId);
        result.setDisplayName(displayName);
        result.setSecurityInd(securityInd);
        result.setVideoInd(videoInd);
        result.setObsInd(obsInd);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);
        courtRoomId = result.getPrimaryKey();
        assertNotNull(courtRoomId, NOTNULL);
        result.setXhbCrLiveDisplays(result.getXhbCrLiveDisplays());
        result.setXhbConfiguredPublicNotices(result.getXhbConfiguredPublicNotices());
        return new XhbCourtRoomDao(result);
    }

    public static XhbCourtLogEntryDao getXhbCourtLogEntryDao() {
        Integer entryId = -1;
        Integer caseId = -1;
        Integer defendantOnCaseId = -2;
        Integer defendantOnOffenceId = -3;
        Integer scheduledHearingId = -4;
        Integer eventDescId = -5;
        String logEntryXml = "logEntryXml";
        LocalDateTime dateTime = LocalDateTime.now();
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = "Test2";
        String createdBy = "Test1";
        Integer version = 3;
        XhbCourtLogEntryDao result = new XhbCourtLogEntryDao();
        result.setEntryId(entryId);
        result.setCaseId(caseId);
        result.setDefendantOnCaseId(defendantOnCaseId);
        result.setDefendantOnOffenceId(defendantOnOffenceId);
        result.setScheduledHearingId(scheduledHearingId);
        result.setEventDescId(eventDescId);
        result.setLogEntryXml(logEntryXml);
        result.setDateTime(dateTime);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);
        entryId = result.getPrimaryKey();
        assertNotNull(entryId, NOTNULL);
        return new XhbCourtLogEntryDao(result);
    }


    public static XhbCourtLogEventDescDao getXhbCourtLogEventDescDao() {
        XhbCourtLogEventDescDao dao = new XhbCourtLogEventDescDao();
        dao.setEventDescId(-5);
        dao.setEventDescription("Test Description");
        dao.setCreationDate(LocalDateTime.now().minusMinutes(5));
        dao.setLastUpdateDate(LocalDateTime.now());
        dao.setCreatedBy("Test1");
        dao.setLastUpdatedBy("Test2");
        dao.setVersion(1);
        return dao;
    }


    public static XhbCourtDao getXhbCourtDao(final Integer courtId, final String courtName) {
        String shortName = "SHORT";
        String probationOfficeName = courtName + " PROBATION OFFICE";
        String internetCourtName = courtName;
        String displayName = courtName + " CROWN COURT";
        String country = "GB";
        String language = "en";

        XhbCourtDao result = new XhbCourtDao();
        result.setCourtId(courtId);
        result.setCourtType("CROWN");
        result.setCircuit("Midlands");
        result.setCourtName(courtName);
        result.setCrestCourtId(courtId.toString());
        result.setCourtPrefix("CROWN COURT");
        result.setShortName(shortName);
        result.setAddressId(DummyServicesUtil.getDummyId());
        result.setCrestIpAddress(null);
        result.setInServiceFlag("Y");
        result.setProbationOfficeName(probationOfficeName);
        result.setInternetCourtName(internetCourtName);
        result.setDisplayName(displayName);
        result.setCourtCode(courtId.toString());
        result.setCountry(country);
        result.setLanguage(language);
        result.setPoliceForceCode(null);
        result.setFlRepSort("C");
        result.setCourtStartTime("10:00");
        result.setWlRepSort("C");
        result.setWlRepPeriod(null);
        result.setWlRepTime(null);
        result.setWlFreeText(null);
        result.setIsPilot("N");
        result.setDxRef(null);
        result.setCountyLocCode(null);
        result.setTier(null);
        result.setCppCourt("Y");
        result.setObsInd("N");
        result.setLastUpdateDate(LocalDateTime.now());
        result.setCreationDate(LocalDateTime.now().minusMinutes(15));
        result.setLastUpdatedBy(TEST2);
        result.setCreatedBy(TEST1);
        result.setVersion(2);
        return new XhbCourtDao(result);
    }

    public static CourtLogViewValue getCourtLogViewValue() {
        CourtLogViewValue result = new CourtLogViewValue(1);
        result.setEntryDate(LocalDateTime.now());
        result.setLogEntry(result.getLogEntry());
        result.setCaseId(result.getCaseId());
        result.setDefendantOnOffenceId(result.getDefendantOnOffenceId());
        result.setScheduledHearingId(result.getScheduledHearingId());
        result.setEventType(result.getEventType());
        result.setEntryDate(result.getEntryDate());
        result.setLastUpdateDate(result.getLastUpdateDate());
        result.setLogEntryId(result.getLogEntryId());
        result.setDefendantOnCaseId(result.getDefendantOnCaseId());
        result.setDateAmended(result.isDateAmended());
        assertFalse(result.isBwHistoryIssueWarrantEvent(), FALSE);
        assertFalse(result.isBwHistoryEndWarrantEvent(), FALSE);
        assertFalse(result.isCrackedIneffectiveEvent(), FALSE);
        assertNull(result.getId(), "Result is not Null");
        CourtLogViewValue compareTo = new CourtLogViewValue(2);
        compareTo.setEntryDate(result.getEntryDate());
        assertEquals(0, result.compareTo(compareTo), EQUALS);
        Boolean isEqual = result.equals(compareTo);
        assertFalse(isEqual, FALSE);
        assertNotEquals(compareTo.hashCode() * -1, result.hashCode(), NOTEQUALS);
        return result;
    }

    public static CourtSitePdComplexValue getCourtSitePdComplexValue() {
        return new CourtSitePdComplexValue();
    }

    public static CourtLogSubscriptionValue getCourtLogSubscriptionValue() {
        return new CourtLogSubscriptionValue();
    }

    public static CourtRoomIdentifier getCourtRoomIdentifier() {
        return new CourtRoomIdentifier(-99, 81, "Test Court", 123);
    }

    public static CourtDetailValue getCourtDetailValue(boolean isHidden, String event) {
        CourtDetailValue result = (CourtDetailValue) DummyPublicDisplayValueUtil
            .populatePublicDisplayValue(new CourtDetailValue());
        if (event != null) {
            result.setEvent(DummyNodeUtil.getEvent(event));
        }
        assertFalse(result.hasDefendants(), FALSE);
        result.addDefendantName(DummyDefendantUtil.getDefendantName(isHidden, false));
        assertTrue(result.hasDefendants(), TRUE);
        assertFalse(result.hasPublicNotices(), FALSE);
        result
            .setPublicNotices(DummyPdNotifierUtil.getPublicNoticeValue());
        assertTrue(result.hasPublicNotices(), TRUE);
        assertFalse(result.hasCaseTitle(), FALSE);
        assertFalse(result.hasInformationForDisplay(), FALSE);
        result.setCaseTitle(result.getCaseTitle());
        result.setCaseNumber(result.getCaseNumber());
        result.setReportingRestricted(result.isReportingRestricted());
        result.setReportingRestricted(isHidden);
        result.setCaseNumber(CASENUMBER);
        assertTrue(result.hasInformationForDisplay(), TRUE);

        // Test compare
        AllCourtStatusValue compared = new AllCourtStatusValue();
        compared.setCourtSiteCode(ONETWOTHREE);
        assertNotEquals(0, result.compareTo(compared), NOTEQUALS);
        compared.setCourtSiteCode(result.getCourtSiteCode());

        compared.setCrestCourtRoomNo(123);
        assertNotEquals(0, result.compareTo(compared));
        compared.setCrestCourtRoomNo(result.getCrestCourtRoomNo());

        compared.setEventTime(LocalDateTime.now().minusHours(1));
        assertNotEquals(0, result.compareTo(compared), NOTEQUALS);
        compared.setEventTime(result.getEventTime());
        assertEquals(0, result.compareTo(compared), EQUALS);
        Boolean isEquals = result.equals(compared);
        assertTrue(isEquals, TRUE);

        return result;
    }

    public static AllCourtStatusCompiledRendererDelegate getNewAllCourtStatusCompiledRendererDelegate(
        Calendar calendar) {
        return new AllCourtStatusCompiledRendererDelegate(calendar.getTime());
    }

    public static XhbCourtSiteDao getXhbCourtSiteDaoWithRooms(List<XhbCourtRoomDao> courtRooms) {
        XhbCourtSiteDao site = getXhbCourtSiteDao();
        site.setXhbCourtRooms(courtRooms);
        return site;
    }

}
