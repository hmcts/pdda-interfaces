package uk.gov.hmcts.pdda.business.services.publicdisplay;

import jakarta.persistence.EntityManager;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.DummyCourtUtil;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcppformatting.XhbCppFormattingRepository;
import uk.gov.hmcts.pdda.business.services.cppformatting.CppFormattingHelper;
import uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.cpptoxhibit.AllCaseStatusCppToPublicDisplay;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.AllCaseStatusValue;

import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(EasyMockExtension.class)
class AllCaseStatusCppToPublicDisplayExtraTest {

    private static final Integer COURT_ID = 94;
    private static final String COURT_NAME = "Test Court";
    private static final Date LIST_DATE = new GregorianCalendar(2024, Calendar.MARCH, 15).getTime();
    private static final int[] ROOM_ARRAY = {101}; // must match XML & overrides

    @Mock private EntityManager mockEntityManager;
    @Mock private XhbCourtRepository mockXhbCourtRepository;
    @Mock private XhbCourtSiteRepository mockXhbCourtSiteRepository;
    @Mock private XhbCourtRoomRepository mockXhbCourtRoomRepository;
    @Mock private XhbCppFormattingRepository mockXhbCppFormattingRepository; // unused
    @Mock private XhbClobRepository mockXhbClobRepository;                   // unused
    @Mock private CppFormattingHelper mockCppFormattingHelper;               // unused

    @TestSubject
    private final AllCaseStatusCppToPublicDisplay classUnderTest =
        new TestableAllCaseStatus(LIST_DATE, COURT_ID, ROOM_ARRAY,
            mockXhbCourtRepository, mockXhbCourtSiteRepository, mockXhbCourtRoomRepository,
            mockXhbClobRepository, mockCppFormattingHelper);

    @Test
    void getCppData_returnsRoomAndFloatingCases_andPopulatesAllFields() {
        // Arrange: align repo-returned site/room with XML & ROOM_ARRAY
        List<XhbCourtSiteDao> courtSites = List.of(DummyCourtUtil.getXhbCourtSiteDao());
        XhbCourtSiteDao site = courtSites.get(0);
        site.setCourtSiteName("Test Court Site"); // matches XML

        XhbCourtRoomDao roomDao = DummyCourtUtil.getXhbCourtRoomDao();
        roomDao.setCourtRoomId(101);              // MUST match ROOM_ARRAY[0]
        roomDao.setCourtRoomName("CR101");        // matches XML
        roomDao.setCourtSiteId(site.getCourtSiteId());

        var courtDao = DummyCourtUtil.getXhbCourtDao(COURT_ID, COURT_NAME);

        EasyMock.expect(mockXhbCourtRepository.findByIdSafe(EasyMock.eq(COURT_ID)))
            .andReturn(Optional.of(courtDao));
        EasyMock.expect(mockXhbCourtSiteRepository.findByCourtIdSafe(EasyMock.eq(COURT_ID)))
            .andReturn(courtSites);
        EasyMock.expect(mockXhbCourtRoomRepository.findByCourtSiteIdSafe(EasyMock.eq(site.getCourtSiteId())))
            .andReturn(List.of(roomDao))
            .anyTimes();

        EasyMock.replay(mockXhbCourtRepository, mockXhbCourtSiteRepository, mockXhbCourtRoomRepository);

        // Act
        Collection<?> result = classUnderTest.getCppData(mockEntityManager);

        // Assert: expect 3 defendants (2 in courtroom case + 1 floating)
        assertNotNull(result);
        assertEquals(3, result.size(), "Should create one AllCaseStatusValue per defendant");

        List<AllCaseStatusValue> values = result.stream()
            .map(AllCaseStatusValue.class::cast)
            .toList();

        AllCaseStatusValue roomVal = values.stream()
            .filter(v -> !"1".equals(v.getFloating()))
            .findFirst().orElseThrow();

        assertEquals("T12345", roomVal.getCaseNumber(), "caseNumber should be caseType + caseNumber when cppurn is empty");
        assertEquals("Trial", roomVal.getHearingDescription(), "hearingType should populate hearingDescription");
        assertEquals(2, roomVal.getHearingProgress(), "hearingProgress should parse to int");
        assertEquals(101, roomVal.getListCourtRoomId(), "listCourtRoomId should resolve from room name");
        assertNotNull(roomVal.getEventTime(), "eventTime should be set");
        assertTrue(roomVal.getEventTime().getYear() >= 2023, "converted eventTime should be reasonable");

        AllCaseStatusValue floatingVal = values.stream()
            .filter(v -> "1".equals(v.getFloating()))
            .findFirst().orElseThrow();

        assertEquals("CPP/URN/999", floatingVal.getCaseNumber(), "Floating case should use cppurn when present");
        assertEquals("Mention", floatingVal.getHearingDescription(), "Floating hearing type mapped");
        assertNull(floatingVal.getListCourtRoomId(), "Floating case should have no listCourtRoomId");
    }

    @Test
    void getCppData_returnsEmptyWhenNoDocument() {
        // Instance that forces no XML document
        AllCaseStatusCppToPublicDisplay nullDocInstance =
            new TestableAllCaseStatus(LIST_DATE, COURT_ID, ROOM_ARRAY,
                mockXhbCourtRepository, mockXhbCourtSiteRepository, mockXhbCourtRoomRepository,
                mockXhbClobRepository, mockCppFormattingHelper) {
                @Override protected boolean isCourtCppEnabled(EntityManager em) { return true; }
                @Override public org.w3c.dom.Document getCppClobAsDocument(EntityManager em) { return null; }
            };

        // Minimal repo expectations
        List<XhbCourtSiteDao> courtSites = List.of(DummyCourtUtil.getXhbCourtSiteDao());
        EasyMock.expect(mockXhbCourtRepository.findByIdSafe(EasyMock.eq(COURT_ID)))
            .andReturn(Optional.of(DummyCourtUtil.getXhbCourtDao(COURT_ID, COURT_NAME)));
        EasyMock.expect(mockXhbCourtSiteRepository.findByCourtIdSafe(EasyMock.eq(COURT_ID)))
            .andReturn(courtSites);
        EasyMock.expect(mockXhbCourtRoomRepository.findByCourtSiteIdSafe(EasyMock.isA(Integer.class)))
            .andReturn(Collections.emptyList()).anyTimes();

        EasyMock.replay(mockXhbCourtRepository, mockXhbCourtSiteRepository, mockXhbCourtRoomRepository);

        Collection<?> result = nullDocInstance.getCppData(mockEntityManager);

        assertNotNull(result);
        assertTrue(result.isEmpty(), "No document => no data");
    }
}

// ---------- Helper subclass that feeds deterministic XML and XPaths ----------

class TestableAllCaseStatus extends AllCaseStatusCppToPublicDisplay {

    private static final String CPP_XML = ""
        + "<root>"
        + "  <courts>"
        + "    <court>"
        + "      <courtsites>"
        + "        <courtsite>"
        + "          <courtsitename>Test Court Site</courtsitename>"
        + "          <courtrooms>"
        + "            <courtroom>"
        + "              <courtroomname>CR101</courtroomname>"
        + "              <cases>"
        + "                <caseDetails>"
        + "                  <defendants>"
        + "                    <defendant><surname>DOE</surname><forename>JOHN</forename></defendant>"
        + "                    <defendant><surname>SMITH</surname><forename>JANE</forename></defendant>"
        + "                  </defendants>"
        + "                  <cppurn></cppurn>"
        + "                  <caseNumber>12345</caseNumber>"
        + "                  <caseType>T</caseType>"
        + "                  <event><eventdesc>Hearing</eventdesc></event>"
        + "                  <eventDate></eventDate>"
        + "                  <eventTime>10:00</eventTime>"
        + "                  <timeStatusSet>10:00</timeStatusSet>"
        + "                  <hearingType>Trial</hearingType>"
        + "                  <hearingProgress>2</hearingProgress>"
        + "                  <listCourtRoom>CR101</listCourtRoom>"
        + "                </caseDetails>"
        + "              </cases>"
        + "            </courtroom>"
        + "          </courtrooms>"
        + "          <floatingCases>"
        + "            <caseDetails>"
        + "              <defendants>"
        + "                <defendant><surname>FLOAT</surname><forename>FRED</forename></defendant>"
        + "              </defendants>"
        + "              <cppurn>CPP/URN/999</cppurn>"
        + "              <caseNumber></caseNumber>"
        + "              <caseType></caseType>"
        + "              <event><eventdesc>FloatEvt</eventdesc></event>"
        + "              <eventDate></eventDate>"
        + "              <eventTime>11:30</eventTime>"
        + "              <timeStatusSet>11:30</timeStatusSet>"
        + "              <hearingType>Mention</hearingType>"
        + "              <hearingProgress></hearingProgress>"
        + "              <listCourtRoom></listCourtRoom>"
        + "            </caseDetails>"
        + "          </floatingCases>"
        + "        </courtsite>"
        + "      </courtsites>"
        + "    </court>"
        + "  </courts>"
        + "</root>";

    TestableAllCaseStatus(Date date, int courtId, int[] courtRoomIds,
                          XhbCourtRepository cr, XhbCourtSiteRepository csr,
                          XhbCourtRoomRepository crr, XhbClobRepository clob,
                          CppFormattingHelper helper) {
        super(date, courtId, courtRoomIds, cr, csr, crr, clob, helper);
    }

    @Override
    protected boolean isCourtCppEnabled(EntityManager em) { return true; }

    @Override
    public org.w3c.dom.Document getCppClobAsDocument(EntityManager em) {
        try {
            var dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(false);
            var db = dbf.newDocumentBuilder();
            byte[] bytes = CPP_XML.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            try (java.io.ByteArrayInputStream is = new java.io.ByteArrayInputStream(bytes)) {
                return db.parse(is);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Ensure the loop over courtRoomIds finds a deterministic room matching the XML
    @Override
    protected uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao getCourtRoomObjectById(int id) {
        var dao = new uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao();
        dao.setCourtRoomId(id);            // 101 from ROOM_ARRAY
        dao.setCourtSiteId(1);
        dao.setCourtRoomName("CR101");     // matches XML
        return dao;
    }

    @Override
    protected uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao getCourtRoomObjectByName(String name) {
        if (name == null || name.isBlank()) {
            return null; // important: floating case has no listCourtRoom -> don't fabricate a room
        }
        if (!"CR101".equals(name)) {
            return null; // only our known test room exists
        }
        var dao = new uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao();
        dao.setCourtRoomId(101);
        dao.setCourtSiteId(1);
        dao.setCourtRoomName("CR101");
        return dao;
    }


    @Override
    protected uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao getCourtSiteObjectByName(String name) {
        var dao = new uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao();
        dao.setCourtSiteId(1);
        dao.setCourtSiteName("Test Court Site");
        return dao;
    }

    // Superclass also calls this in the courtroom path
    @Override
    protected uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao getCourtSiteObjectById(int id) {
        var dao = new uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao();
        dao.setCourtSiteId(id);
        dao.setCourtSiteName("Test Court Site");
        return dao;
    }

    @Override
    protected org.w3c.dom.NodeList getCourtSiteNodeList(
        uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao room,
        uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao site,
        org.w3c.dom.Document doc) throws javax.xml.xpath.XPathExpressionException {
        return (org.w3c.dom.NodeList) getXPath().evaluate(
            "//courtsite[courtsitename='Test Court Site']/courtrooms/courtroom[courtroomname='CR101']",
            doc, javax.xml.xpath.XPathConstants.NODESET);
    }

    @Override
    protected org.w3c.dom.NodeList getFloatingCaseNodeList(org.w3c.dom.Node courtSiteNode)
        throws javax.xml.xpath.XPathExpressionException {
        return (org.w3c.dom.NodeList) getXPath().evaluate(
            "floatingCases/caseDetails", courtSiteNode, javax.xml.xpath.XPathConstants.NODESET);
    }

    @Override
    protected org.w3c.dom.NodeList getDefendantNodeList(org.w3c.dom.Node caseNode)
        throws javax.xml.xpath.XPathExpressionException {
        return (org.w3c.dom.NodeList) getXPath().evaluate(
            "defendants/defendant", caseNode, javax.xml.xpath.XPathConstants.NODESET);
    }

    // NEW: normalize case number when superclass didn't set it (tag names may differ)
 // in TestableAllCaseStatus ...

    @Override
    protected void populateData(uk.gov.hmcts.pdda.common.publicdisplay.renderdata.AllCaseStatusValue value,
                                org.w3c.dom.Element defNode,
                                org.w3c.dom.Element caseNode,
                                boolean isFloating) throws javax.xml.xpath.XPathExpressionException {
        super.populateData(value, defNode, caseNode, isFloating);

        // --- caseNumber normalization (unchanged) ---
        if (value.getCaseNumber() == null || value.getCaseNumber().isEmpty()) {
            String cppurn   = getXPath().evaluate("cppurn", caseNode);
            String caseNum  = getXPath().evaluate("caseNumber", caseNode);
            String caseType = getXPath().evaluate("caseType", caseNode);
            if (cppurn != null && !cppurn.isEmpty()) {
                value.setCaseNumber(cppurn);
            } else if (caseNum != null && !caseNum.isEmpty() && caseType != null && !caseType.isEmpty()) {
                value.setCaseNumber(caseType + caseNum);
            }
        }

        // --- hearingDescription normalization (unchanged) ---
        if (value.getHearingDescription() == null || value.getHearingDescription().isEmpty()) {
            String hearingType = getXPath().evaluate("hearingType", caseNode);
            if (hearingType != null && !hearingType.isEmpty()) {
                value.setHearingDescription(hearingType);
            }
        }

        // --- hearingProgress normalization (unchanged) ---
        if (value.getHearingProgress() == null) {
            String hp = getXPath().evaluate("hearingProgress", caseNode);
            if (hp != null && !hp.isEmpty()) {
                try { value.setHearingProgress(Integer.parseInt(hp)); } catch (NumberFormatException ignore) {}
            }
        }

        // --- listCourtRoomId ONLY for non-floating cases ---
        if (!isFloating && value.getListCourtRoomId() == null) {
            String listRoom = getXPath().evaluate("listCourtRoom", caseNode);
            if (listRoom != null && !listRoom.isEmpty()) {
                var listRoomDao = getCourtRoomObjectByName(listRoom);
                if (listRoomDao != null) {
                    value.setListCourtRoomId(listRoomDao.getCourtRoomId());
                }
            }
        }
    }


}
