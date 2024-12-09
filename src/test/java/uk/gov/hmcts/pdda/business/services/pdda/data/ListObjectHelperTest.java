package uk.gov.hmcts.pdda.business.services.pdda.data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.DummyCaseUtil;
import uk.gov.hmcts.DummyCourtUtil;
import uk.gov.hmcts.DummyHearingUtil;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings({"static-access", "PMD.ExcessiveImports", "PMD.CouplingBetweenObjects",
    "PMD.TooManyMethods", "PMD.UseConcurrentHashMap"})
class ListObjectHelperTest {

    private static final String XHBCOURTSITEDAO = "xhbCourtSiteDao";
    private static final String EMPTY_STRING = "";
    private static final String TRUE = "Result is False";

    @Mock
    private DataHelper mockDataHelper;

    @InjectMocks
    private final ListObjectHelper classUnderTest = new ListObjectHelper(mockDataHelper);

    @Test
    void testCourtSite() {
        Map<String, String> nodesMap = new LinkedHashMap<>();
        nodesMap.put(classUnderTest.COURTHOUSECODE, "404");
        nodesMap.put(classUnderTest.COURTHOUSENAME, "Birmingham");
        boolean result = testNodeMap(nodesMap, true);
        assertTrue(result, TRUE);
        result = testNodeMap(nodesMap, false);
        assertTrue(result, TRUE);
    }

    private boolean testNodeMap(Map<String, String> nodesMap, boolean populate) {
        String lastEntryName = null;
        for (Map.Entry<String, String> entry : nodesMap.entrySet()) {
            if (!populate) {
                entry.setValue(EMPTY_STRING);
            }
            lastEntryName = entry.getKey();
        }
        classUnderTest.validateNodeMap(nodesMap, lastEntryName);
        return true;
    }

    @Test
    void testCourtRoom() {
        Map<String, String> nodesMap = new LinkedHashMap<>();
        nodesMap.put(classUnderTest.COURTROOMNO, "1");
        ReflectionTestUtils.setField(classUnderTest, XHBCOURTSITEDAO,
            Optional.of(DummyCourtUtil.getXhbCourtSiteDao()));
        boolean result = testNodeMap(nodesMap, true);
        assertTrue(result, TRUE);
        result = testNodeMap(nodesMap, false);
        assertTrue(result, TRUE);
    }

    @Test
    void testHearingList() {
        // Setup
        Map<String, String> nodesMap = new LinkedHashMap<>();
        nodesMap.put(classUnderTest.COURTHOUSECODE, "404");
        nodesMap.put(classUnderTest.COURTHOUSENAME, "Birmingham");
        nodesMap.put(classUnderTest.PUBLISHEDTIME, "23:40");
        nodesMap.put(classUnderTest.STARTDATE, "2024-10-31");
        nodesMap.put(classUnderTest.VERSION, "1");
        // Expects
        Mockito
            .when(mockDataHelper.validateCourtSite(Mockito.isA(String.class),
                Mockito.isA(String.class)))
            .thenReturn(Optional.of(DummyCourtUtil.getXhbCourtSiteDao()));
        // Run
        boolean result = testNodeMap(nodesMap, true);
        assertTrue(result, TRUE);
        result = testNodeMap(nodesMap, false);
        assertTrue(result, TRUE);
    }

    @Test
    void testSitting() {
        // Setup
        Map<String, String> nodesMap = new LinkedHashMap<>();
        nodesMap.put(classUnderTest.SITTINGTIME, "23:40");
        // Set
        ReflectionTestUtils.setField(classUnderTest, "xhbCourtRoomDao",
            Optional.of(DummyCourtUtil.getXhbCourtRoomDao()));
        // Run
        boolean result = testNodeMap(nodesMap, true);
        assertTrue(result, TRUE);
        result = testNodeMap(nodesMap, false);
        assertTrue(result, TRUE);
    }

    @Test
    void testCase() {
        // Setup
        Map<String, String> nodesMap = new LinkedHashMap<>();
        nodesMap.put(classUnderTest.CASENUMBER, "T123321");
        // Set
        ReflectionTestUtils.setField(classUnderTest, XHBCOURTSITEDAO,
            Optional.of(DummyCourtUtil.getXhbCourtSiteDao()));
        // Run
        boolean result = testNodeMap(nodesMap, true);
        assertTrue(result, TRUE);
        result = testNodeMap(nodesMap, false);
        assertTrue(result, TRUE);
    }

    @Test
    void testHearingType() {
        // Setup
        Map<String, String> nodesMap = new LinkedHashMap<>();
        nodesMap.put(classUnderTest.HEARINGTYPECODE, "XXX");
        nodesMap.put(classUnderTest.HEARINGTYPEDESC, "Description");
        nodesMap.put(classUnderTest.CATEGORY, "Criminal");
        // Set
        ReflectionTestUtils.setField(classUnderTest, XHBCOURTSITEDAO,
            Optional.of(DummyCourtUtil.getXhbCourtSiteDao()));
        // Run
        boolean result = testNodeMap(nodesMap, true);
        assertTrue(result, TRUE);
        result = testNodeMap(nodesMap, false);
        assertTrue(result, TRUE);
    }

    @Test
    void testHearing() {
        // Setup
        Map<String, String> nodesMap = new LinkedHashMap<>();
        nodesMap.put(classUnderTest.STARTDATE, "2024-10-31");
        // Set
        ReflectionTestUtils.setField(classUnderTest, "xhbCaseDao",
            Optional.of(DummyCaseUtil.getXhbCaseDao()));
        ReflectionTestUtils.setField(classUnderTest, "xhbRefHearingTypeDao",
            Optional.of(DummyHearingUtil.getXhbRefHearingTypeDao()));
        // Run
        boolean result = testNodeMap(nodesMap, true);
        assertTrue(result, TRUE);
        result = testNodeMap(nodesMap, false);
        assertTrue(result, TRUE);
    }

    @Test
    void testScheduledHearing() {
        // Setup
        Map<String, String> nodesMap = new LinkedHashMap<>();
        nodesMap.put(classUnderTest.NOTBEFORETIME, "SITTING AT  10:30 pm");
        // Set
        ReflectionTestUtils.setField(classUnderTest, "xhbHearingDao",
            Optional.of(DummyHearingUtil.getXhbHearingDao()));
        ReflectionTestUtils.setField(classUnderTest, "xhbSittingDao",
            Optional.of(DummyHearingUtil.getXhbSittingDao()));
        // Run
        boolean result = testNodeMap(nodesMap, true);
        assertTrue(result, TRUE);
        ReflectionTestUtils.setField(classUnderTest, "xhbSittingDao", Optional.empty());
        result = testNodeMap(nodesMap, false);
        assertTrue(result, TRUE);
    }

    @Test
    void testDefendant() {
        // Setup
        Map<String, String> nodesMap = new LinkedHashMap<>();
        nodesMap.put(classUnderTest.FIRSTNAME, "John");
        nodesMap.put(classUnderTest.FIRSTNAME, "Fitzgerald");
        nodesMap.put(classUnderTest.SURNAME, "Kennedy");
        nodesMap.put(classUnderTest.DATEOFBIRTH, "1917-05-29");
        nodesMap.put(classUnderTest.GENDER, "male");
        // Set
        ReflectionTestUtils.setField(classUnderTest, XHBCOURTSITEDAO,
            Optional.of(DummyCourtUtil.getXhbCourtSiteDao()));
        // Run
        boolean result = testNodeMap(nodesMap, true);
        assertTrue(result, TRUE);
        result = testNodeMap(nodesMap, false);
        assertTrue(result, TRUE);
    }
}
