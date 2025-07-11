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
import uk.gov.hmcts.DummyDefendantUtil;
import uk.gov.hmcts.DummyHearingUtil;
import uk.gov.hmcts.DummyJudgeUtil;
import uk.gov.hmcts.pdda.business.entities.xhbschedhearingattendee.XhbSchedHearingAttendeeDao;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings({"static-access", "PMD"})
class ListObjectHelperTest {

    private static final String XHBCOURTSITEDAO = "xhbCourtSiteDao";
    private static final String EMPTY_STRING = "";
    private static final String FALSE = "Result is True";
    private static final String TRUE = "Result is False";

    @Mock
    private DataHelper mockDataHelper;

    @InjectMocks
    private final ListObjectHelper classUnderTest = new ListObjectHelper(mockDataHelper);

    @Test
    void testDefaultConstructor() {
        boolean result = false;
        try {
            new ListObjectHelper();
            result = true;
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
        assertTrue(result, TRUE);
    }

    @Test
    void testCourtSite() {
        Map<String, String> nodesMap = new LinkedHashMap<>();
        nodesMap.put(classUnderTest.COURTHOUSECODE, "404");
        boolean result = testNodeMap(nodesMap, false, ListObjectHelper.COURTLIST_NODE);
        assertTrue(result, TRUE);
        nodesMap.put(classUnderTest.COURTHOUSENAME, "Birmingham");
        result = testNodeMap(nodesMap, true, ListObjectHelper.COURTLIST_NODE);
        assertTrue(result, TRUE);
    }

    private boolean testNodeMap(Map<String, String> nodesMap, boolean populate, String breadcrumb) {
        for (Map.Entry<String, String> entry : nodesMap.entrySet()) {
            if (!populate) {
                entry.setValue(EMPTY_STRING);
            }
        }
        classUnderTest.validateNodeMap(nodesMap, breadcrumb);
        return true;
    }

    @Test
    void testCourtRoom() {
        Map<String, String> nodesMap = new LinkedHashMap<>();
        nodesMap.put(classUnderTest.COURTROOMNO, "1");
        ReflectionTestUtils.setField(classUnderTest, XHBCOURTSITEDAO,
            Optional.of(DummyCourtUtil.getXhbCourtSiteDao()));
        boolean result = testNodeMap(nodesMap, true, classUnderTest.SITTING_NODE);
        assertTrue(result, TRUE);
        result = testNodeMap(nodesMap, false, classUnderTest.SITTING_NODE);
        assertTrue(result, TRUE);
    }

    @Test
    void testHearingList() {
        // Setup
        Map<String, String> nodesMap = new LinkedHashMap<>();
        nodesMap.put(classUnderTest.PUBLISHEDTIME, "23:40");
        nodesMap.put(classUnderTest.STARTDATE, "2024-10-31");
        nodesMap.put(classUnderTest.VERSION, "1");
        nodesMap.put(classUnderTest.COURTHOUSECODE, "404");
        nodesMap.put(classUnderTest.COURTHOUSENAME, "Birmingham");
        // Expects
        Mockito
            .when(mockDataHelper.validateCourtSite(Mockito.isA(String.class),
                Mockito.isA(String.class)))
            .thenReturn(Optional.of(DummyCourtUtil.getXhbCourtSiteDao()));
        // Run
        boolean result = testNodeMap(nodesMap, true, ListObjectHelper.COURTLIST_NODE);
        assertTrue(result, TRUE);
        result = testNodeMap(nodesMap, false, ListObjectHelper.COURTLIST_NODE);
        assertTrue(result, TRUE);
    }

    @Test
    void testSitting() {
        // Setup
        Map<String, String> nodesMap = new LinkedHashMap<>();
        nodesMap.put(classUnderTest.COURTROOMNO, "1");
        nodesMap.put(classUnderTest.SITTINGTIME, "23:40");
        // Set
        ReflectionTestUtils.setField(classUnderTest, XHBCOURTSITEDAO,
            Optional.of(DummyCourtUtil.getXhbCourtSiteDao()));
        ReflectionTestUtils.setField(classUnderTest, "xhbHearingListDao",
            Optional.of(DummyHearingUtil.getXhbHearingListDao()));
        // Expects
        Mockito
            .when(mockDataHelper.validateCourtRoom(Mockito.isA(Integer.class),
                Mockito.isA(Integer.class)))
            .thenReturn(Optional.of(DummyCourtUtil.getXhbCourtRoomDao()));
        // Run
        boolean result = testNodeMap(nodesMap, true, ListObjectHelper.SITTING_NODE);
        assertTrue(result, TRUE);
        result = testNodeMap(nodesMap, false, ListObjectHelper.SITTING_NODE);
        assertTrue(result, TRUE);
    }

    @Test
    void testCase() {
        // Setup
        Map<String, String> nodesMap = new LinkedHashMap<>();
        expectCase(nodesMap);
        // Run
        boolean result = testNodeMap(nodesMap, true, classUnderTest.SITTING_NODE);
        assertTrue(result, TRUE);
        nodesMap.clear();
        nodesMap.put(classUnderTest.CASENUMBER, "T");
        result = testNodeMap(nodesMap, true, classUnderTest.SITTING_NODE);
        assertTrue(result, TRUE);
    }

    private void expectCase(Map<String, String> nodesMap) {
        // Setup
        nodesMap.put(classUnderTest.CASENUMBER, "T123321");
        // Set
        ReflectionTestUtils.setField(classUnderTest, XHBCOURTSITEDAO,
            Optional.of(DummyCourtUtil.getXhbCourtSiteDao()));
        // Expects
        Mockito.when(mockDataHelper.validateCase(Mockito.isA(Integer.class),
            Mockito.isA(String.class), Mockito.isA(Integer.class)))
            .thenReturn(Optional.of(DummyCaseUtil.getXhbCaseDao()));
    }

    @Test
    void testHearingType() {
        // Setup
        Map<String, String> nodesMap = new LinkedHashMap<>();
        expectHearingType(nodesMap);
        // Run
        boolean result = testNodeMap(nodesMap, true, ListObjectHelper.HEARING_NODE);
        assertTrue(result, TRUE);
        nodesMap.put(classUnderTest.CATEGORY, "Criminal");
        result = testNodeMap(nodesMap, false, ListObjectHelper.HEARING_NODE);
        assertTrue(result, TRUE);
    }

    private void expectHearingType(Map<String, String> nodesMap) {
        // Setup
        nodesMap.put(classUnderTest.HEARINGTYPECODE, "XXX");
        nodesMap.put(classUnderTest.HEARINGTYPEDESC, "Description");
        nodesMap.put(classUnderTest.CATEGORY, "Criminal");
        // Set
        ReflectionTestUtils.setField(classUnderTest, XHBCOURTSITEDAO,
            Optional.of(DummyCourtUtil.getXhbCourtSiteDao()));
        // Expects
        Mockito
            .when(mockDataHelper.validateHearingType(Mockito.isA(Integer.class),
                Mockito.isA(String.class), Mockito.isA(String.class), Mockito.isA(String.class)))
            .thenReturn(Optional.of(DummyHearingUtil.getXhbRefHearingTypeDao()));
    }

    @Test
    void testHearing() {
        // Setup
        Map<String, String> nodesMap = new LinkedHashMap<>();
        expectCase(nodesMap);
        expectHearingType(nodesMap);
        expectHearing(nodesMap);
        // Run
        boolean result = testNodeMap(nodesMap, true, ListObjectHelper.HEARING_NODE);
        assertTrue(result, TRUE);
        result = testNodeMap(nodesMap, false, ListObjectHelper.HEARING_NODE);
        assertTrue(result, TRUE);
    }

    private void expectHearing(Map<String, String> nodesMap) {
        // Setup
        nodesMap.put(classUnderTest.STARTDATE, "2024-10-31");
        // Expects
        Mockito
            .when(mockDataHelper.validateHearing(Mockito.isA(Integer.class),
                Mockito.isA(Integer.class), Mockito.isA(Integer.class),
                Mockito.isA(LocalDateTime.class), Mockito.isA(LocalDateTime.class)))
            .thenReturn(Optional.of(DummyHearingUtil.getXhbHearingDao()));
    }

    @Test
    void testScheduledHearing() {
        // Setup
        Map<String, String> nodesMap = new LinkedHashMap<>();
        expectHearingType(nodesMap);
        expectCase(nodesMap);
        expectHearing(nodesMap);
        expectScheduledHearing(nodesMap);
        // Run
        boolean result = testNodeMap(nodesMap, true, ListObjectHelper.HEARING_NODE);
        assertTrue(result, TRUE);
        nodesMap.put(classUnderTest.NOTBEFORETIME, "Invalid Time");
        result = testNodeMap(nodesMap, true, ListObjectHelper.HEARING_NODE);
        assertTrue(result, TRUE);
    }
    
    private void expectScheduledHearing(Map<String, String> nodesMap) {
        // Setup
        nodesMap.put(classUnderTest.NOTBEFORETIME, "SITTING AT  10:30 pm");
        // Set
        ReflectionTestUtils.setField(classUnderTest, "xhbHearingDao",
            Optional.of(DummyHearingUtil.getXhbHearingDao()));
        ReflectionTestUtils.setField(classUnderTest, "xhbSittingDao",
            Optional.of(DummyHearingUtil.getXhbSittingDao()));
        // Expects
        Mockito
            .when(mockDataHelper.validateScheduledHearing(Mockito.isA(Integer.class),
                Mockito.isA(Integer.class), Mockito.isA(LocalDateTime.class)))
            .thenReturn(Optional.of(DummyHearingUtil.getXhbScheduledHearingDao()));
    }

    @Test
    void testDefendant() {
        // Setup
        Map<String, String> nodesMap = new LinkedHashMap<>();
        expectDefendant(nodesMap);
        // Run
        boolean result = testNodeMap(nodesMap, true, ListObjectHelper.DEFENDANT_NODE);
        assertTrue(result, TRUE);
        nodesMap.clear();
        nodesMap.put(classUnderTest.GENDER, "Female");
        result = testNodeMap(nodesMap, true, ListObjectHelper.DEFENDANT_NODE);
        assertTrue(result, TRUE);
    }
    
    @Test
    void testDefendantInvalidData() {
        // Setup
        Map<String, String> nodesMap = new LinkedHashMap<>();
        expectDefendant(nodesMap);
        nodesMap.clear();
        nodesMap.put(classUnderTest.SURNAME, "Kennedy, John F");
        // Run
        boolean result = testNodeMap(nodesMap, true, ListObjectHelper.DEFENDANT_NODE);
        assertTrue(result, TRUE);
    }

    @Test
    void testDefendantOnCase() {
        // Setup
        Map<String, String> nodesMap = new LinkedHashMap<>();
        expectDefendant(nodesMap);
        // Run
        boolean result = testNodeMap(nodesMap, true, ListObjectHelper.DEFENDANT_NODE);
        assertTrue(result, TRUE);
        ReflectionTestUtils.setField(classUnderTest, "xhbCaseDao", Optional.empty());
        result = testNodeMap(nodesMap, true, ListObjectHelper.DEFENDANT_NODE);
        assertTrue(result, TRUE);
    }

    private void expectDefendant(Map<String, String> nodesMap) {
        // Setup
        nodesMap.put(classUnderTest.FIRSTNAME + ".1", "John");
        nodesMap.put(classUnderTest.FIRSTNAME + ".2", "Fitzgerald");
        nodesMap.put(classUnderTest.SURNAME, "Kennedy");
        nodesMap.put(classUnderTest.DATEOFBIRTH, "1917-05-29");
        nodesMap.put(classUnderTest.GENDER, "male");
        // Set
        ReflectionTestUtils.setField(classUnderTest, XHBCOURTSITEDAO,
            Optional.of(DummyCourtUtil.getXhbCourtSiteDao()));
        ReflectionTestUtils.setField(classUnderTest, "xhbCaseDao",
            Optional.of(DummyCaseUtil.getXhbCaseDao()));
        // Expects
        Mockito
            .when(mockDataHelper.validateDefendant(Mockito.isA(Integer.class),
                Mockito.isA(String.class), Mockito.isA(String.class), Mockito.isA(String.class),
                Mockito.isA(Integer.class), Mockito.isA(LocalDateTime.class), Mockito.isA(String.class)))
            .thenReturn(Optional.of(DummyDefendantUtil.getXhbDefendantDao()));
    }

    @Test
    void testSchedHearingDefendant() {
        // Setup
        Map<String, String> nodesMap = new LinkedHashMap<>();
        expectDefendant(nodesMap);
        // Sets
        ReflectionTestUtils.setField(classUnderTest, "xhbScheduledHearingDao",
            Optional.of(DummyHearingUtil.getXhbScheduledHearingDao()));
        // Expects
        Mockito
            .when(mockDataHelper.validateDefendantOnCase(Mockito.isA(Integer.class),
                Mockito.isA(Integer.class), Mockito.isA(String.class)))
            .thenReturn(Optional.of(DummyDefendantUtil.getXhbDefendantOnCaseDao()));

        // Run
        boolean result = testNodeMap(nodesMap, true, ListObjectHelper.DEFENDANT_NODE);
        assertTrue(result, TRUE);
        result = testNodeMap(nodesMap, false, ListObjectHelper.DEFENDANT_NODE);
        assertTrue(result, TRUE);
    }

    @Test
    void testCrLiveDisplay() {
        // Setup
        Map<String, String> nodesMap = new LinkedHashMap<>();
        expectHearingType(nodesMap);
        expectCase(nodesMap);
        expectHearing(nodesMap);
        expectScheduledHearing(nodesMap);
        // Set
        ReflectionTestUtils.setField(classUnderTest, "xhbCourtRoomDao",
            Optional.of(DummyCourtUtil.getXhbCourtRoomDao()));
        // Run
        boolean result = testNodeMap(nodesMap, true, ListObjectHelper.HEARING_NODE);
        assertTrue(result, TRUE);
        ReflectionTestUtils.setField(classUnderTest, "xhbSittingDao", Optional.empty());
        result = testNodeMap(nodesMap, false, ListObjectHelper.HEARING_NODE);
        assertTrue(result, TRUE);
    }
    
    @Test
    void testJudge() {
        // Setup
        Map<String, String> nodesMap = new LinkedHashMap<>();
        expectJudge(nodesMap);
        // Run
        boolean result = testNodeMap(nodesMap, true, ListObjectHelper.JUDGE_NODE);
        assertTrue(result, TRUE);
        result = testNodeMap(nodesMap, false, ListObjectHelper.JUDGE_NODE);
        assertTrue(result, TRUE);
    }
    
    private void expectJudge(Map<String, String> nodesMap) {
        // Setup
        final XhbSchedHearingAttendeeDao xhbSchedHearingAttendeeDao = new XhbSchedHearingAttendeeDao();
        nodesMap.put(classUnderTest.TITLE, "TEST_TITLE");
        nodesMap.put(classUnderTest.FIRSTNAME + ".1", "TEST_FIRSTNAME");
        nodesMap.put(classUnderTest.SURNAME, "TEST_SURNAME");
        
        // Set
        ReflectionTestUtils.setField(classUnderTest, XHBCOURTSITEDAO,
            Optional.of(DummyCourtUtil.getXhbCourtSiteDao()));
        ReflectionTestUtils.setField(classUnderTest, "xhbScheduledHearingDao",
            Optional.of(DummyHearingUtil.getXhbScheduledHearingDao()));
        
        // Expects
        Mockito
            .when(mockDataHelper.validateJudge(Mockito.isA(Integer.class),
                Mockito.isA(String.class), Mockito.isA(String.class),
                Mockito.isA(String.class)))
            .thenReturn(Optional.of(DummyJudgeUtil.getXhbRefJudgeDao()));
        Mockito.when(mockDataHelper.createSchedHearingAttendee(Mockito.isA(String.class),
            Mockito.isA(Integer.class), Mockito.isA(Integer.class))).thenReturn(
                Optional.of(xhbSchedHearingAttendeeDao));
    }

    @Test
    void testProcessJudgeRecords() {
        // Setup
        final XhbSchedHearingAttendeeDao xhbSchedHearingAttendeeDao = new XhbSchedHearingAttendeeDao();
        
        // Set
        ReflectionTestUtils.setField(classUnderTest, "xhbScheduledHearingDao",
            Optional.of(DummyHearingUtil.getXhbScheduledHearingDao()));
        ReflectionTestUtils.setField(classUnderTest, "xhbRefJudgeDao",
            Optional.of(DummyJudgeUtil.getXhbRefJudgeDao()));
        
        // Expects
        Mockito.when(mockDataHelper.createSchedHearingAttendee(Mockito.isA(String.class),
            Mockito.isA(Integer.class), Mockito.isA(Integer.class))).thenReturn(
                Optional.of(xhbSchedHearingAttendeeDao));
        
        boolean result = true;
        classUnderTest.processJudgeRecords();
        assertTrue(result, TRUE);
    }
    
    @Test
    void testIsNumberedNode() {
        boolean result = classUnderTest.isNumberedNode(classUnderTest.FIRSTNAME);
        assertTrue(result, TRUE);
        result = classUnderTest.isNumberedNode(EMPTY_STRING);
        assertFalse(result, FALSE);
    }
}
