package uk.gov.hmcts;

import uk.gov.hmcts.pdda.business.entities.xhbcrlivedisplay.XhbCrLiveDisplayDao;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaylocation.XhbDisplayLocationDao;
import uk.gov.hmcts.pdda.common.publicdisplay.types.rotationset.DisplayRotationSetData;
import uk.gov.hmcts.pdda.common.publicdisplay.types.rotationset.RotationSetDisplayDocument;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.DisplayDocumentUri;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.DisplayUri;
import uk.gov.hmcts.pdda.common.publicdisplay.vos.publicdisplay.DisplayBasicValueSortAdapter;
import uk.gov.hmcts.pdda.common.publicdisplay.vos.publicdisplay.DisplayLocationComplexValue;

import java.time.LocalDateTime;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public final class DummyDisplayUtil {
    
    private static final String EQUALS = "Results are not Equal";
    
    private static final String NOTNULL = "Result is Null";
   
    private static final long PAGE_DELAY = 1L;
    
    private static final String DISPLAY_URL = "pd://display/snaresbrook/453/reception/mainscreen";
    
    private DummyDisplayUtil() {
        // Do nothing
    }
    
    public static DisplayRotationSetData getDisplayRotationSetData(String displayUrl, String documentUrl) {
        DisplayUri displayUri = getDisplayUri(displayUrl);
        RotationSetDisplayDocument[] rotationSetDisplayDocumentArray =
            getRotationSetDisplayDocumentArray(documentUrl);
        int displayId = 100;
        int rotationSetId = 200;
        String displayType = "42in";
        DisplayRotationSetData result = new DisplayRotationSetData(displayUri, rotationSetDisplayDocumentArray,
            displayId, rotationSetId, displayType);
        assertNotNull(result.toString(), "Reuslt is Null");
        assertEquals(result, new DisplayRotationSetData(displayUri, rotationSetDisplayDocumentArray, displayId,
            rotationSetId, displayType), EQUALS);
        return result;
    }

    public static DisplayUri getDisplayUri(String displayUrl) {
        String courthouseName = "courthouseName";
        String courtsiteCode = "courtsiteCode";
        String location = "location";
        String display = "display";
        DisplayUri result = new DisplayUri(courthouseName, courtsiteCode, location, display);
        assertEquals(courthouseName, result.getCourthouseName(), EQUALS);
        assertEquals(courtsiteCode, result.getCourtsiteCode(), EQUALS);
        assertEquals(location, result.getLocation(), EQUALS);
        assertEquals(display, result.getDisplay(), EQUALS);
        result = new DisplayUri(displayUrl);
        assertEquals("display", result.getType(), EQUALS);
        assertEquals(Locale.getDefault(), result.getLocale(), EQUALS);
        DisplayUri compareTo = new DisplayUri(courthouseName, courtsiteCode, location, display);
        assertFalse(result.equals(compareTo), "Result is not False");
        return result;
    }
    
    public static DisplayUri getDisplayUri() {
        return new DisplayUri(DISPLAY_URL);
    }

    public static RotationSetDisplayDocument[] getRotationSetDisplayDocumentArray(String documentUrl) {
        return new RotationSetDisplayDocument[] {getRotationSetDisplayDocument(documentUrl)};
    }

    public static RotationSetDisplayDocument getRotationSetDisplayDocument(String documentUrl) {
        DisplayDocumentUri displayDocumentUri = DummyDisplayDocumentUtil.getDisplayDocumentUri(documentUrl);
        RotationSetDisplayDocument result = new RotationSetDisplayDocument(displayDocumentUri, PAGE_DELAY);
        assertNotNull(result.toString(), NOTNULL);
        assertEquals(result, new RotationSetDisplayDocument(displayDocumentUri, PAGE_DELAY), EQUALS);
        assertEquals(displayDocumentUri, result.getDisplayDocumentUri(), EQUALS);
        assertEquals(PAGE_DELAY, result.getPageDelay(), EQUALS);
        return result;
    }

    public static DisplayUri getDisplayUriWithParams() {
        String courtHouse = "snaresbrook";
        String courtsiteCode = "456";
        String location = "reception";
        String display = "42in";
        return new DisplayUri(courtHouse, courtsiteCode, location, display);
    }
    
    public static DisplayLocationComplexValue getDisplayLocationComplexValue() {
        DisplayLocationComplexValue displayLocationComplexValue = new DisplayLocationComplexValue();
        displayLocationComplexValue.setDisplayLocationDao(new XhbDisplayLocationDao());
        displayLocationComplexValue.setDisplayDaos(DummyPublicDisplayUtil.getXhbDisplayDao());
        return displayLocationComplexValue;
    }
    
    public static DisplayBasicValueSortAdapter getDisplayBasicValueSortAdapter() {
        return new DisplayBasicValueSortAdapter(DummyPublicDisplayUtil.getXhbDisplayDao(), "Test");
    }
    
    public static XhbCrLiveDisplayDao getXhbCrLiveDisplayDao() {
        Integer crLiveDisplayId = -1;
        Integer courtRoomId = -1;
        Integer scheduledHearingId = -1;
        LocalDateTime timeStatusSet = LocalDateTime.now();
        String status = "status";
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = "Test2";
        String createdBy = "Test1";
        Integer version = 3;
        XhbCrLiveDisplayDao result = new XhbCrLiveDisplayDao();
        result.setCrLiveDisplayId(crLiveDisplayId);
        result.setCourtRoomId(courtRoomId);
        result.setScheduledHearingId(scheduledHearingId);
        result.setTimeStatusSet(timeStatusSet);
        result.setStatus(status);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);
        return result;
    }
}
