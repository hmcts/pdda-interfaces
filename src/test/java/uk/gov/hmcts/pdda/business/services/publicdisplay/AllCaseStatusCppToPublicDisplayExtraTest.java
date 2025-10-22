package uk.gov.hmcts.pdda.business.services.publicdisplay;

import jakarta.persistence.EntityManager;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.DummyCourtUtil;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcppformatting.XhbCppFormattingRepository;
import uk.gov.hmcts.pdda.business.services.cppformatting.CppFormattingHelper;
import uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.cpptoxhibit.AllCaseStatusCppToPublicDisplay;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.AllCaseStatusValue;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("PMD")
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

        assertEquals("T12345", roomVal.getCaseNumber(), "caseNumber should be caseType +"
            + "caseNumber when cppurn is empty");
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
        // Minimal repo expectations
        List<XhbCourtSiteDao> courtSites = List.of(DummyCourtUtil.getXhbCourtSiteDao());
        EasyMock.expect(mockXhbCourtRepository.findByIdSafe(EasyMock.eq(COURT_ID)))
            .andReturn(Optional.of(DummyCourtUtil.getXhbCourtDao(COURT_ID, COURT_NAME)));
        EasyMock.expect(mockXhbCourtSiteRepository.findByCourtIdSafe(EasyMock.eq(COURT_ID)))
            .andReturn(courtSites);
        EasyMock.expect(mockXhbCourtRoomRepository.findByCourtSiteIdSafe(EasyMock.isA(Integer.class)))
            .andReturn(Collections.emptyList()).anyTimes();

        EasyMock.replay(mockXhbCourtRepository, mockXhbCourtSiteRepository, mockXhbCourtRoomRepository);

        // Instance that forces no XML document
        AllCaseStatusCppToPublicDisplay nullDocInstance =
            new TestableAllCaseStatus(LIST_DATE, COURT_ID, ROOM_ARRAY,
                mockXhbCourtRepository, mockXhbCourtSiteRepository, mockXhbCourtRoomRepository,
                mockXhbClobRepository, mockCppFormattingHelper) {
                
                @Override
                protected boolean isCourtCppEnabled(EntityManager em) {
                    return true;
                }
                
                @Override
                public org.w3c.dom.Document getCppClobAsDocument(EntityManager em) {
                    return null;
                }
            };
        Collection<?> result = nullDocInstance.getCppData(mockEntityManager);

        assertNotNull(result);
        assertTrue(result.isEmpty(), "No document => no data");
    }
}
