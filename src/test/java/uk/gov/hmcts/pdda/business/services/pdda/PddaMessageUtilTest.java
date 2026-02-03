package uk.gov.hmcts.pdda.business.services.pdda;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import uk.gov.courtservice.xhibit.business.vos.services.publicnotice.DisplayablePublicNoticeValue;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.ActivateCaseEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.AddCaseEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.CaseStatusEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.ConfigurationChangeEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.HearingStatusEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.MoveCaseEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicDisplayEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicNoticeEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.UpdateCaseEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.pdda.PddaHearingProgressEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.CourtRoomIdentifier;
import uk.gov.courtservice.xhibit.common.publicdisplay.types.configuration.CourtConfigurationChange;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobDao;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundDao;
import uk.gov.hmcts.pdda.business.entities.xhbpddamessage.XhbPddaMessageDao;
import uk.gov.hmcts.pdda.business.entities.xhbrefpddamessagetype.XhbRefPddaMessageTypeDao;
import uk.gov.hmcts.pdda.business.services.cppstaginginboundejb3.CppStagingInboundHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Tests for PddaMessageUtil.
 */
@SuppressWarnings("PMD")
class PddaMessageUtilTest {

    @Mock private PddaMessageHelper pddaMessageHelper;
    @Mock private XhbClobRepository clobRepository;
    @Mock private XhbCourtRepository courtRepository;
    @Mock private XhbCourtRoomRepository courtRoomRepository;
    @Mock private XhbCourtSiteRepository courtSiteRepository;
    @Mock private CppStagingInboundHelper cppStagingInboundHelper;

    @Captor private ArgumentCaptor<XhbPddaMessageDao> pddaMessageDaoCaptor;
    @Captor private ArgumentCaptor<XhbRefPddaMessageTypeDao> messageTypeDaoCaptor;
    @Captor private ArgumentCaptor<XhbClobDao> clobDaoCaptor;
    @Captor private ArgumentCaptor<CourtRoomIdentifier> criCaptor;
    @Captor private ArgumentCaptor<CourtConfigurationChange> configChangeCaptor;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    // ---------- createMessage ----------
    @Test
    void createMessage_setsValidStatusAndSaves_whenNoError() {
        // For void methods, use doNothing().when(...)
        doNothing().when(pddaMessageHelper).savePddaMessage(any());

        PddaMessageUtil.createMessage(
            pddaMessageHelper, 1, 2, 3, 4L, 5, "doc.xml", "N", null);

        verify(pddaMessageHelper).savePddaMessage(pddaMessageDaoCaptor.capture());
        XhbPddaMessageDao dao = pddaMessageDaoCaptor.getValue();
        assertEquals(1, dao.getCourtId());
        assertEquals(2, dao.getCourtRoomId());
        assertEquals(3, dao.getPddaMessageTypeId());
        assertEquals(4L, dao.getPddaMessageDataId());
        assertEquals(5, dao.getPddaBatchId());
        assertNull(dao.getTimeSent());
        assertEquals("doc.xml", dao.getCpDocumentName());
        assertEquals("VN", dao.getCpDocumentStatus());
        assertEquals("N", dao.getCpResponseGenerated());
        assertNull(dao.getErrorMessage());
    }

    @Test
    void createMessage_setsInvalidStatusAndSaves_whenErrorPresent() {
        doNothing().when(pddaMessageHelper).savePddaMessage(any());

        PddaMessageUtil.createMessage(
            pddaMessageHelper, 10, 20, 30, 40L, 50, "bad.xml", "N", "boom");

        verify(pddaMessageHelper).savePddaMessage(pddaMessageDaoCaptor.capture());
        XhbPddaMessageDao dao = pddaMessageDaoCaptor.getValue();
        assertEquals("INV", dao.getCpDocumentStatus());
        assertEquals("boom", dao.getErrorMessage());
    }

    // ---------- createMessageType ----------
    @Test
    void createMessageType_populatesTypeAndDescription() {
        when(pddaMessageHelper.savePddaMessageType(any()))
            .thenAnswer(inv -> Optional.of(inv.getArgument(0)));

        LocalDateTime ts = LocalDateTime.of(2025, 1, 2, 3, 4, 5);

        Optional<XhbRefPddaMessageTypeDao> saved =
            PddaMessageUtil.createMessageType(pddaMessageHelper, "ABC", ts);

        assertTrue(saved.isPresent());
        verify(pddaMessageHelper).savePddaMessageType(messageTypeDaoCaptor.capture());
        XhbRefPddaMessageTypeDao dao = messageTypeDaoCaptor.getValue();
        assertEquals("ABC", dao.getPddaMessageType());
        assertEquals("02/01/2025 03:04:05", dao.getPddaMessageDescription());
    }

    @Test
    void translate_handlesPddaHearingProgressEvent_setsCourtId() {
        // Arrange
        PddaHearingProgressEvent evt = mock(PddaHearingProgressEvent.class);
        when(evt.getCourtName()).thenReturn("Alpha Court");

        XhbCourtDao courtDao = mock(XhbCourtDao.class);
        when(courtDao.getCourtId()).thenReturn(321);

        when(courtRepository.findByCourtNameValueSafe("Alpha Court"))
            .thenReturn(List.of(courtDao));

        // Act
        PddaMessageUtil.translatePublicDisplayEvent(evt, courtRepository, courtRoomRepository, courtSiteRepository);

        // Assert - method called to set the resolved court id
        verify(evt).setCourtId(321);
    }


    // ---------- createClob ----------
    @Test
    void createClob_savesWhenDataNotNull() {
        when(clobRepository.update(any())).thenAnswer(inv -> Optional.of(inv.getArgument(0)));

        Optional<XhbClobDao> out = PddaMessageUtil.createClob(clobRepository, "payload");

        assertTrue(out.isPresent());
        verify(clobRepository).update(clobDaoCaptor.capture());
        assertEquals("payload", clobDaoCaptor.getValue().getClobData());
    }

    @Test
    void createClob_returnsEmptyAndDoesNotSave_whenDataNull() {
        Optional<XhbClobDao> out = PddaMessageUtil.createClob(clobRepository, null);
        assertTrue(out.isEmpty());
        verify(clobRepository, never()).update(any());
    }

    // ---------- updatePddaMessageRecords ----------
    @Test
    void updatePddaMessageRecords_setsResponseGeneratedAndCallsUpdate() {
        XhbPddaMessageDao m1 = new XhbPddaMessageDao();
        XhbPddaMessageDao m2 = new XhbPddaMessageDao();

        PddaMessageUtil.updatePddaMessageRecords(pddaMessageHelper, List.of(m1, m2), "Alice");

        InOrder inOrder = Mockito.inOrder(pddaMessageHelper);
        assertEquals("Y", m1.getCpResponseGenerated());
        assertEquals("Y", m2.getCpResponseGenerated());
        inOrder.verify(pddaMessageHelper).updatePddaMessage(same(m1), eq("Alice"));
        inOrder.verify(pddaMessageHelper).updatePddaMessage(same(m2), eq("Alice"));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void updatePddaMessageRecords_noopWhenEmpty() {
        PddaMessageUtil.updatePddaMessageRecords(pddaMessageHelper, List.of(), "Bob");
        verifyNoInteractions(pddaMessageHelper);
    }

    // ---------- updateCppStagingInboundRecords ----------
    @Test
    void updateCppStagingInboundRecords_setsAckSuccessAndCallsUpdate() {
        XhbCppStagingInboundDao d1 = new XhbCppStagingInboundDao();
        XhbCppStagingInboundDao d2 = new XhbCppStagingInboundDao();

        PddaMessageUtil.updateCppStagingInboundRecords(
            cppStagingInboundHelper, List.of(d1, d2), "Carol");

        assertEquals("AS", d1.getAcknowledgmentStatus());
        assertEquals("AS", d2.getAcknowledgmentStatus());
        verify(cppStagingInboundHelper).updateCppStagingInbound(same(d1), eq("Carol"));
        verify(cppStagingInboundHelper).updateCppStagingInbound(same(d2), eq("Carol"));
        verifyNoMoreInteractions(cppStagingInboundHelper);
    }

    @Test
    void updateCppStagingInboundRecords_noopWhenEmpty() {
        PddaMessageUtil.updateCppStagingInboundRecords(
            cppStagingInboundHelper, List.of(), "Dave");
        verifyNoInteractions(cppStagingInboundHelper);
    }

    // ---------- translatePublicDisplayEvent ----------
    @Nested
    class TranslatePublicDisplayEventTests {

        private final String courtName = "Alpha Court";
        private final Integer courtRoomNo = 3;
        private final Integer resolvedCourtId = 100;
        private final Integer resolvedCourtSiteId = 2000;
        private final Integer resolvedCourtRoomId = 200;

        @BeforeEach
        void wireRepositoriesForHappyPath() {
            // Court
            XhbCourtDao courtDao = mock(XhbCourtDao.class);
            when(courtDao.getCourtId()).thenReturn(resolvedCourtId);
            when(courtRepository.findByCourtNameValueSafe(courtName))
                .thenReturn(List.of(courtDao));

            // Court Site
            XhbCourtSiteDao siteDao = mock(XhbCourtSiteDao.class);
            when(siteDao.getCourtSiteId()).thenReturn(resolvedCourtSiteId);
            when(courtSiteRepository.findByCourtIdSafe(resolvedCourtId))
                .thenReturn(List.of(siteDao));

            // Court Room
            XhbCourtRoomDao roomDao = mock(XhbCourtRoomDao.class);
            when(roomDao.getCourtRoomId()).thenReturn(resolvedCourtRoomId);
            when(courtRoomRepository.findByCourtRoomNoSafe(resolvedCourtSiteId, courtRoomNo))
                .thenReturn(Optional.of(roomDao));
        }

        private CourtRoomIdentifier startCri() {
            return new CourtRoomIdentifier(null, null, courtName, courtRoomNo);
        }

        private void assertCriUpdated(CourtRoomIdentifier updated) {
            assertEquals(resolvedCourtId, updated.getCourtId());
            assertEquals(resolvedCourtRoomId, updated.getCourtRoomId());
            assertEquals(courtName, updated.getCourtName());
            assertEquals(courtRoomNo, updated.getCourtRoomNo());
        }

        @Test
        void translate_handlesActivateCaseEvent_andRemapsIdentifiers() {
            ActivateCaseEvent evt = mock(ActivateCaseEvent.class);
            when(evt.getCourtRoomIdentifier()).thenReturn(startCri());

            PddaMessageUtil.translatePublicDisplayEvent(
                evt, courtRepository, courtRoomRepository, courtSiteRepository);

            verify(evt).setCourtRoomIdentifier(criCaptor.capture());
            assertCriUpdated(criCaptor.getValue());
        }

        @Test
        void translate_handlesAddCaseEvent() {
            AddCaseEvent evt = mock(AddCaseEvent.class);
            when(evt.getCourtRoomIdentifier()).thenReturn(startCri());

            PddaMessageUtil.translatePublicDisplayEvent(
                evt, courtRepository, courtRoomRepository, courtSiteRepository);

            verify(evt).setCourtRoomIdentifier(criCaptor.capture());
            assertCriUpdated(criCaptor.getValue());
        }

        @Test
        void translate_handlesCaseStatusEvent() {
            CaseStatusEvent evt = mock(CaseStatusEvent.class);
            when(evt.getCourtRoomIdentifier()).thenReturn(startCri());

            PddaMessageUtil.translatePublicDisplayEvent(
                evt, courtRepository, courtRoomRepository, courtSiteRepository);

            verify(evt).setCourtRoomIdentifier(criCaptor.capture());
            assertCriUpdated(criCaptor.getValue());
        }

        @Test
        void translate_handlesHearingStatusEvent() {
            HearingStatusEvent evt = mock(HearingStatusEvent.class);
            when(evt.getCourtRoomIdentifier()).thenReturn(startCri());

            PddaMessageUtil.translatePublicDisplayEvent(
                evt, courtRepository, courtRoomRepository, courtSiteRepository);

            verify(evt).setCourtRoomIdentifier(criCaptor.capture());
            assertCriUpdated(criCaptor.getValue());
        }

        @Test
        void translate_handlesMoveCaseEvent() {
            MoveCaseEvent evt = mock(MoveCaseEvent.class);
            when(evt.getCourtRoomIdentifier()).thenReturn(startCri());

            PddaMessageUtil.translatePublicDisplayEvent(
                evt, courtRepository, courtRoomRepository, courtSiteRepository);

            verify(evt).setCourtRoomIdentifier(criCaptor.capture());
            assertCriUpdated(criCaptor.getValue());
        }

        @Test
        void translate_handlesPublicNoticeEvent() {
            PublicNoticeEvent evt = mock(PublicNoticeEvent.class);
            when(evt.getCourtRoomIdentifier()).thenReturn(startCri());

            PddaMessageUtil.translatePublicDisplayEvent(
                evt, courtRepository, courtRoomRepository, courtSiteRepository);

            verify(evt).setCourtRoomIdentifier(criCaptor.capture());
            assertCriUpdated(criCaptor.getValue());
        }

        @Test
        void translate_handlesUpdateCaseEvent() {
            UpdateCaseEvent evt = mock(UpdateCaseEvent.class);
            when(evt.getCourtRoomIdentifier()).thenReturn(startCri());

            PddaMessageUtil.translatePublicDisplayEvent(
                evt, courtRepository, courtRoomRepository, courtSiteRepository);

            verify(evt).setCourtRoomIdentifier(criCaptor.capture());
            assertCriUpdated(criCaptor.getValue());
        }

        @Test
        void translate_handlesConfigurationChangeEvent_setsNewConfiguration() {
            ConfigurationChangeEvent evt = mock(ConfigurationChangeEvent.class);
            CourtConfigurationChange incoming = mock(CourtConfigurationChange.class);
            when(incoming.getCourtName()).thenReturn(courtName);
            when(evt.getChange()).thenReturn(incoming);

            PddaMessageUtil.translatePublicDisplayEvent(
                evt, courtRepository, courtRoomRepository, courtSiteRepository);

            verify(evt).setConfigurationChange(configChangeCaptor.capture());
            CourtConfigurationChange applied = configChangeCaptor.getValue();
            // Assuming getters exist on CourtConfigurationChange:
            assertEquals(resolvedCourtId, applied.getCourtId());
            assertEquals(courtName, applied.getCourtName());
        }

        @Test
        void translate_unknownEventType_noChanges() {
            PublicDisplayEvent evt = mock(PublicDisplayEvent.class);

            PublicDisplayEvent returned = PddaMessageUtil.translatePublicDisplayEvent(
                evt, courtRepository, courtRoomRepository, courtSiteRepository);

            assertSame(evt, returned);
            verifyNoInteractions(courtRepository, courtRoomRepository, courtSiteRepository);
        }

        @Test
        void translate_handlesMissingCourtSite_thenNullRoomId() {
            // Court resolves; site list is empty
            XhbCourtDao courtDao = mock(XhbCourtDao.class);
            when(courtDao.getCourtId()).thenReturn(resolvedCourtId);
            when(courtRepository.findByCourtNameValueSafe(courtName))
                .thenReturn(List.of(courtDao));
            when(courtSiteRepository.findByCourtIdSafe(resolvedCourtId))
                .thenReturn(List.of()); // no sites

            MoveCaseEvent evt = mock(MoveCaseEvent.class);
            when(evt.getCourtRoomIdentifier()).thenReturn(startCri());

            PddaMessageUtil.translatePublicDisplayEvent(
                evt, courtRepository, courtRoomRepository, courtSiteRepository);

            verify(evt).setCourtRoomIdentifier(criCaptor.capture());
            CourtRoomIdentifier cri = criCaptor.getValue();
            assertEquals(resolvedCourtId, cri.getCourtId());
            assertNull(cri.getCourtRoomId()); // no site -> no room
        }

        @Test
        void translate_handlesMissingCourtRoom_thenNullRoomId() {
            // Court and site resolve; room optional empty
            XhbCourtDao courtDao = mock(XhbCourtDao.class);
            when(courtDao.getCourtId()).thenReturn(resolvedCourtId);
            when(courtRepository.findByCourtNameValueSafe(courtName))
                .thenReturn(List.of(courtDao));

            XhbCourtSiteDao siteDao = mock(XhbCourtSiteDao.class);
            when(siteDao.getCourtSiteId()).thenReturn(resolvedCourtSiteId);
            when(courtSiteRepository.findByCourtId(resolvedCourtId))
                .thenReturn(List.of(siteDao));

            when(courtRoomRepository.findByCourtRoomNoSafe(resolvedCourtSiteId, courtRoomNo))
                .thenReturn(Optional.empty());

            PublicNoticeEvent evt = mock(PublicNoticeEvent.class);
            when(evt.getCourtRoomIdentifier()).thenReturn(startCri());

            PddaMessageUtil.translatePublicDisplayEvent(
                evt, courtRepository, courtRoomRepository, courtSiteRepository);

            verify(evt).setCourtRoomIdentifier(criCaptor.capture());
            CourtRoomIdentifier cri = criCaptor.getValue();
            assertEquals(resolvedCourtId, cri.getCourtId());
            assertNull(cri.getCourtRoomId()); // no room found
        }

        @Test
        void translate_handlesMissingCourt_thenNullCourtIdAndRoomId() {
            when(courtRepository.findByCourtNameValueSafe(courtName))
                .thenReturn(List.of()); // no court

            HearingStatusEvent evt = mock(HearingStatusEvent.class);
            when(evt.getCourtRoomIdentifier()).thenReturn(startCri());

            PddaMessageUtil.translatePublicDisplayEvent(
                evt, courtRepository, courtRoomRepository, courtSiteRepository);

            verify(evt).setCourtRoomIdentifier(criCaptor.capture());
            CourtRoomIdentifier cri = criCaptor.getValue();
            assertNull(cri.getCourtId());
            assertNull(cri.getCourtRoomId());
            assertEquals(courtName, cri.getCourtName());
            assertEquals(courtRoomNo, cri.getCourtRoomNo());
        }

        @Test
        void translate_preservesPublicNotices_defensiveCopy() {
            // Use the normal wiring so court/site/room resolve
            wireRepositoriesForHappyPath();

            AddCaseEvent evt = mock(AddCaseEvent.class);

            CourtRoomIdentifier original = new CourtRoomIdentifier(null, null, courtName, courtRoomNo);
            DisplayablePublicNoticeValue notice = mock(DisplayablePublicNoticeValue.class);
            DisplayablePublicNoticeValue[] notices = new DisplayablePublicNoticeValue[] { notice };
            original.setPublicNotices(notices);

            when(evt.getCourtRoomIdentifier()).thenReturn(original);

            PddaMessageUtil.translatePublicDisplayEvent(evt, courtRepository, courtRoomRepository, courtSiteRepository);

            verify(evt).setCourtRoomIdentifier(criCaptor.capture());
            CourtRoomIdentifier updated = criCaptor.getValue();

            // content preserved
            assertNotNull(updated.getPublicNotices());
            assertEquals(1, updated.getPublicNotices().length);
            assertEquals(notice, updated.getPublicNotices()[0]);

            // but it's a defensive copy (different array instance)
            assertTrue(updated.getPublicNotices() != notices, "Expected a different array instance (defensive copy)");
        }
    }

    // ---------- constructor (utility class) ----------
    @Test
    void constructor_throwsIllegalStateException() throws Exception {
        Constructor<PddaMessageUtil> ctor = PddaMessageUtil.class.getDeclaredConstructor();
        assertTrue(ctor.canAccess(null) || !ctor.isAccessible());
        ctor.setAccessible(true);

        Executable exec = () -> {
            try {
                ctor.newInstance();
            } catch (InvocationTargetException e) {
                throw (e.getCause() instanceof RuntimeException)
                    ? (RuntimeException) e.getCause()
                    : new RuntimeException(e.getCause());
            }
        };
        IllegalStateException ex = assertThrows(IllegalStateException.class, exec);
        assertEquals("Utility class", ex.getMessage());
    }
}
