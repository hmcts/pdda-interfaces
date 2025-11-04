package uk.gov.hmcts.pdda.business.services.pdda.sftp;

import de.ppi.fakesftpserver.extension.FakeSftpServerExtension;
import jakarta.persistence.EntityManager;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.CaseStatusEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.ConfigurationChangeEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.HearingStatusEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicDisplayEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.pdda.PddaHearingProgressEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.CaseCourtLogInformation;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.CourtRoomIdentifier;
import uk.gov.courtservice.xhibit.common.publicdisplay.types.configuration.CourtConfigurationChange;
import uk.gov.courtservice.xhibit.courtlog.vos.CourtLogSubscriptionValue;
import uk.gov.courtservice.xhibit.courtlog.vos.CourtLogViewValue;
import uk.gov.hmcts.DummyCaseUtil;
import uk.gov.hmcts.DummyCourtUtil;
import uk.gov.hmcts.DummyFormattingUtil;
import uk.gov.hmcts.DummyHearingUtil;
import uk.gov.hmcts.DummyPdNotifierUtil;
import uk.gov.hmcts.pdda.business.entities.xhbcase.XhbCaseDao;
import uk.gov.hmcts.pdda.business.entities.xhbcase.XhbCaseRepository;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobDao;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundDao;
import uk.gov.hmcts.pdda.business.entities.xhbhearing.XhbHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbhearing.XhbHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbpddamessage.XhbPddaMessageDao;
import uk.gov.hmcts.pdda.business.entities.xhbpddamessage.XhbPddaMessageRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrefpddamessagetype.XhbRefPddaMessageTypeDao;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingDao;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingRepository;
import uk.gov.hmcts.pdda.business.services.cppstaginginboundejb3.CppStagingInboundHelper;
import uk.gov.hmcts.pdda.business.services.pdda.BaisValidation;
import uk.gov.hmcts.pdda.business.services.pdda.PddaMessageHelper;
import uk.gov.hmcts.pdda.business.services.pdda.PddaMessageUtil;
import uk.gov.hmcts.pdda.business.services.pdda.PddaSerializationUtils;
import uk.gov.hmcts.pdda.business.services.pdda.sftp.SftpService.BaisCpValidation;
import uk.gov.hmcts.pdda.business.services.pdda.sftp.SftpService.BaisXhibitValidation;
import uk.gov.hmcts.pdda.common.publicdisplay.jms.PublicDisplayNotifier;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Title: PDDA Helper Test.
 * Description:
 * Copyright: Copyright (c) 2024
 * Company: CGI
 * @author Scott Atwell
 */
@SuppressWarnings("PMD")
@ExtendWith(EasyMockExtension.class)
class SftpServiceTest {

    @RegisterExtension
    public final FakeSftpServerExtension sftpServer = new FakeSftpServerExtension();

    private static final Logger LOG = LoggerFactory.getLogger(SftpServiceTest.class);

    private static final String NOT_TRUE = "Result is not True";
    private static final String TESTUSER = "TestUser";
    private static final String ALL_GOOD = "All good";

    private static final String TEST_SFTP_DIRECTORY = "/directory/";
    private static final String COURT1 = "Court1";
    private static final String COURTROOM1 = "CourtRoom1";

    private static final String DAILY_LIST = "DailyList";
    private static final String FIRM_LIST = "FirmList";
    private static final String WARNED_LIST = "WarnedList";
    private static final String UNKNOWN = "Unknown";

    // Files from CP
    private static final String FILENAME_CP_INVALID_PARTS =
        "PublicDisplay_453_20241009130506_1.xml";
    private static final String FILENAME_CP_INVALID_TITLE = "PublicDplay_453_20241209130506.xml";
    private static final String FILENAME_CP_INVALID_EXTENSION =
        "PublicDisplay_453_20241009130506.xl";

    private static final String FILENAME_CP_PD_VALID = "PublicDisplay_453_20241209130506.xml";

    private static final String FILENAME_CP_WP_VALID = "WebPage_453_20241209130506.xml";

    private static final String FILENAME_CP_DL_VALID = "DailyList_453_20241209130506.xml";

    private static final String FILENAME_CP_FL_VALID = "FirmList_453_20241209130506.xml";

    private static final String FILENAME_CP_WL_VALID = "WarnedList_453_20241209130506.xml";

    // Files from XHIBIT
    private static final String FILENAME_XHB_INVALID_PARTS = "PDDA_XPD_1_453_20241209130506";
    private static final String FILENAME_XHB_INVALID_MESSAGE_TYPE =
        "PDDA_LPD_34_1_457_20241209130506";
    private static final String FILENAME_XHB_INVALID_TITLE = "PDA_XPD_34_1_453_20241209130506";
    private static final String FILENAME_XHB_INVALID_COURT = "PDA_XPD_34_1_391_20241209130506";

    private static final String FILENAME_XHB_DL_VALID = "PDDA_XDL_34_1_453_20241209130506";

    private static final String FILENAME_XHB_PD_VALID = "PDDA_XPD_34_1_453_20241209130506";

    private static final String FILENAME_XHB_PD_FROMCP_VALID = "PDDA_CPD_34_1_453_20241209130506";


    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private PddaMessageHelper mockPddaMessageHelper;

    @Mock
    private CppStagingInboundHelper mockCppStagingInboundHelper;

    @Mock
    private XhbConfigPropRepository mockXhbConfigPropRepository;

    @Mock
    private XhbCourtRepository mockXhbCourtRepository;
    
    @Mock
    private XhbCourtRoomRepository mockXhbCourtRoomRepository;
    
    @Mock
    private XhbCourtSiteRepository mockXhbCourtSiteRepository;
    
    @Mock
    private XhbCaseRepository mockXhbCaseRepository;
    
    @Mock
    private XhbHearingRepository mockXhbHearingRepository;
    
    @Mock
    private XhbSittingRepository mockXhbSittingRepository;
    
    @Mock
    private XhbScheduledHearingRepository mockXhbScheduledHearingRepository;
    
    @Mock
    private XhbClobRepository mockXhbClobRepository;

    @Mock
    private XhbPddaMessageRepository mockXhbPddaMessageRepository;

    @Mock
    private PublicDisplayNotifier mockPublicDisplayNotifier;

    @Mock
    private SFTPClient mockSftpClient;

    @Mock
    private SftpConfig mockSftpConfig;

    @Mock
    private SSHClient mockSshClient;

    @Mock
    private Environment mockEnvironment;

    @Mock
    private SftpService mockSftpService;

    @Mock
    private BaisValidation mockBaisValidation;

    @TestSubject
    private final SftpService classUnderTest =
        new SftpService(mockEntityManager, mockXhbConfigPropRepository,
            mockEnvironment, mockPddaMessageHelper, mockXhbClobRepository, mockXhbCourtRepository,
            mockXhbCourtRoomRepository, mockXhbCourtSiteRepository, mockXhbCaseRepository,
            mockXhbHearingRepository, mockXhbSittingRepository, mockXhbScheduledHearingRepository);


    @TestSubject
    private final SftpHelperUtil subClassUnderTest = new SftpHelperUtil(mockEntityManager);

    private final SftpConfig sftpConfig = new SftpConfig();


    @Test
    void testDefaultConstructor() {
        boolean result = true;
        new SftpService(mockEntityManager, mockXhbConfigPropRepository, mockEnvironment,
            mockPddaMessageHelper, mockXhbClobRepository, mockXhbCourtRepository,
            mockXhbCourtRoomRepository, mockXhbCourtSiteRepository, mockXhbCaseRepository,
            mockXhbHearingRepository, mockXhbSittingRepository, mockXhbScheduledHearingRepository);
        assertTrue(result, NOT_TRUE);
    }

    @BeforeEach
    public void setUp() {
        setupSftpConfig();
    }


    @Test
    void testProcessBaisMessagesCp() {
        // Setup
        setupCpFiles();
        Optional<XhbPddaMessageDao> pddaMessageDao =
            Optional.of(DummyPdNotifierUtil.getXhbPddaMessageDao());
        EasyMock
            .expect(
                mockPddaMessageHelper
                    .findByCpDocumentName(FILENAME_CP_INVALID_PARTS))
            .andReturn(pddaMessageDao);

        Optional<XhbRefPddaMessageTypeDao> pddaRefMessageTypeDao =
            Optional.of(DummyPdNotifierUtil.getXhbPddaMessageTypeDao());

        EasyMock.expect(mockPddaMessageHelper.findByMessageType("PublicDisplay"))
            .andReturn(pddaRefMessageTypeDao);

        List<XhbCourtDao> courtDaos = new ArrayList<>();
        courtDaos.add(DummyCourtUtil.getXhbCourtDao(-453, COURT1));
        EasyMock
            .expect(mockXhbCourtRepository.findByCrestCourtIdValueSafe(EasyMock.isA(String.class)))
            .andReturn(courtDaos);

        EasyMock.replay(mockXhbCourtRepository);
        EasyMock.replay(mockPddaMessageHelper);


        // Run
        boolean result = false;
        try {
            classUnderTest.setupSftpClientAndProcessBaisData(sftpConfig,
                sftpConfig.getSshClient(), true);
        } catch (Exception exception) {
            fail(exception);
        }

        // Checks
        assertFalse(result, NOT_TRUE);
    }


    @Test
    @SuppressWarnings("PMD")
    void testProcessBaisMessagesXhibit() {
        // --- Arrange ---
        setupXhibitFiles();

        // Message type lookup
        Optional<XhbRefPddaMessageTypeDao> pddaRefMessageTypeDao =
            Optional.of(DummyPdNotifierUtil.getXhbPddaMessageTypeDao());
        EasyMock.expect(mockPddaMessageHelper.findByMessageType("HearingStatus"))
                .andReturn(pddaRefMessageTypeDao);

        // Save call
        mockPddaMessageHelper.savePddaMessage(EasyMock.isA(XhbPddaMessageDao.class));
        EasyMock.expectLastCall();

        // Court lookups
        List<XhbCourtDao> byCrest = List.of(DummyCourtUtil.getXhbCourtDao(-453, COURT1));
        EasyMock.expect(mockXhbCourtRepository.findByCrestCourtIdValueSafe(EasyMock.isA(String.class)))
                .andStubReturn(byCrest);

        List<XhbCourtDao> byName = List.of(DummyCourtUtil.getXhbCourtDao(1, "Test Court"));
        EasyMock.expect(mockXhbCourtRepository.findByCourtNameValueSafe(EasyMock.isA(String.class)))
                .andStubReturn(byName);

        // Duplicate check inside createBaisMessage(...)
        EasyMock.expect(mockPddaMessageHelper.findByCpDocumentName(EasyMock.isA(String.class)))
                .andReturn(Optional.empty());

        // CLOB update
        XhbClobDao clob = DummyFormattingUtil.getXhbClobDao(0L, "<blob>");
        EasyMock.expect(mockXhbClobRepository.update(EasyMock.isA(XhbClobDao.class)))
                .andStubReturn(Optional.of(clob));

        // EM guard
        EasyMock.expect(mockEntityManager.isOpen()).andReturn(true).anyTimes();

        // NOTE: removed any expectation on mockXhbPddaMessageRepository.update(...)

        // Replay (drop mockXhbPddaMessageRepository here too)
        EasyMock.replay(
            mockXhbCourtRepository,
            mockPddaMessageHelper,
            mockXhbClobRepository,
            mockEntityManager
        );

        // Stub the static remapper so it doesn't hit room/site lookups
        try (MockedStatic<PddaMessageUtil> mocked =
                 Mockito.mockStatic(PddaMessageUtil.class, Mockito.CALLS_REAL_METHODS)) {
            mocked.when(() -> PddaMessageUtil.translatePublicDisplayEvent(
                        Mockito.any(PublicDisplayEvent.class),
                        Mockito.any(XhbCourtRepository.class),
                        Mockito.any(XhbCourtRoomRepository.class),
                        Mockito.any(XhbCourtSiteRepository.class)))
                  .thenAnswer(inv -> inv.getArgument(0));

            // --- Act ---
            classUnderTest.setupSftpClientAndProcessBaisData(
                sftpConfig, sftpConfig.getSshClient(), false);
        } catch (Exception e) {
            fail(e);
        }

        // Verify (drop mockXhbPddaMessageRepository here too)
        EasyMock.verify(
            mockXhbCourtRepository,
            mockPddaMessageHelper,
            mockXhbClobRepository,
            mockEntityManager
        );
    }
    
    @Test
    void testProcessBaisPddaHearingProgressEvent() {
        // --- Arrange ---
        setupPddaHearingProgressEventFile();

        // EM guard
        EasyMock.expect(mockEntityManager.isOpen()).andReturn(true).anyTimes();
        
        // Entered processHearingProgressEvent(...)
        List<XhbCourtDao> xhbCourtDao = List.of(DummyCourtUtil.getXhbCourtDao(1, COURT1));
        EasyMock.expect(mockXhbCourtRepository.findByCourtNameValueSafe(EasyMock.isA(String.class)))
                .andStubReturn(xhbCourtDao);
        
        List<XhbCourtSiteDao> xhbCourtSiteDao = List.of(DummyCourtUtil.getXhbCourtSiteDao());
        EasyMock.expect(mockXhbCourtSiteRepository.findByCourtIdSafe(EasyMock.isA(Integer.class)))
                .andStubReturn(xhbCourtSiteDao);
        
        Optional<XhbCaseDao> xhbCaseDao = Optional.of(DummyCaseUtil.getXhbCaseDao());
        EasyMock.expect(mockXhbCaseRepository.findByNumberTypeAndCourtSafe(
            EasyMock.isA(Integer.class), EasyMock.isA(String.class), EasyMock.isA(Integer.class)))
                .andStubReturn(xhbCaseDao);
        
        Optional<XhbHearingDao> xhbHearingDao = Optional.of(DummyHearingUtil.getXhbHearingDao());
        EasyMock.expect(mockXhbHearingRepository.findByCaseIdWithTodaysStartDateSafe(
            EasyMock.isA(Integer.class), EasyMock.isA(LocalDateTime.class)))
                .andStubReturn(xhbHearingDao);
        
        List<XhbCourtRoomDao> xhbCourtRoomDao = List.of(DummyCourtUtil.getXhbCourtRoomDao());
        EasyMock.expect(mockXhbCourtRoomRepository.findByCourtSiteIdAndCourtRoomNameSafe(
            EasyMock.isA(Integer.class), EasyMock.isA(String.class)))
                .andStubReturn(xhbCourtRoomDao);
        
        List<XhbSittingDao> xhbSittingDao = List.of(DummyHearingUtil.getXhbSittingDao());
        EasyMock.expect(mockXhbSittingRepository.findByCourtRoomIdAndCourtSiteIdWithTodaysSittingDateSafe(
            EasyMock.isA(Integer.class), EasyMock.isA(Integer.class), EasyMock.isA(LocalDateTime.class)))
                .andStubReturn(xhbSittingDao);
        
        Optional<XhbScheduledHearingDao> xhbScheduledHearingDao = 
            Optional.of(DummyHearingUtil.getXhbScheduledHearingDao());
        EasyMock.expect(mockXhbScheduledHearingRepository.findBySittingIdAndHearingIdSafe(
            EasyMock.isA(Integer.class), EasyMock.isA(Integer.class)))
                .andStubReturn(xhbScheduledHearingDao);
        
        EasyMock.expect(mockXhbScheduledHearingRepository.update(xhbScheduledHearingDao.get()))
                .andStubReturn(xhbScheduledHearingDao);
        
        // Court lookups
        List<XhbCourtDao> byCrest = List.of(DummyCourtUtil.getXhbCourtDao(-453, COURT1));
        EasyMock.expect(mockXhbCourtRepository.findByCrestCourtIdValueSafe(EasyMock.isA(String.class)))
                .andStubReturn(byCrest);
        
        // Message type lookup
        Optional<XhbRefPddaMessageTypeDao> pddaRefMessageTypeDao =
            Optional.of(DummyPdNotifierUtil.getXhbPddaMessageTypeDao());
        EasyMock.expect(mockPddaMessageHelper.findByMessageType("PddaHearingProgress"))
                .andReturn(pddaRefMessageTypeDao);

        // Duplicate check inside createBaisMessage(...)
        EasyMock.expect(mockPddaMessageHelper.findByCpDocumentName(EasyMock.isA(String.class)))
                .andReturn(Optional.empty());
        
        // CLOB update
        XhbClobDao clob = DummyFormattingUtil.getXhbClobDao(0L, "<clob>");
        EasyMock.expect(mockXhbClobRepository.update(EasyMock.isA(XhbClobDao.class)))
                .andStubReturn(Optional.of(clob));

        // Save call
        mockPddaMessageHelper.savePddaMessage(EasyMock.isA(XhbPddaMessageDao.class));
        EasyMock.expectLastCall();

        EasyMock.replay(
            mockXhbCourtRepository,
            mockXhbCourtSiteRepository,
            mockXhbCaseRepository,
            mockXhbHearingRepository,
            mockXhbCourtRoomRepository,
            mockXhbSittingRepository,
            mockXhbScheduledHearingRepository,
            mockPddaMessageHelper,
            mockXhbClobRepository,
            mockEntityManager
        );

        // Run
        classUnderTest.setupSftpClientAndProcessBaisData(sftpConfig, sftpConfig.getSshClient(), false);
        
        // Verify
        EasyMock.verify(
            mockXhbCourtRepository,
            mockXhbCourtSiteRepository,
            mockXhbCaseRepository,
            mockXhbHearingRepository,
            mockXhbCourtRoomRepository,
            mockXhbSittingRepository,
            mockXhbScheduledHearingRepository,
            mockPddaMessageHelper,
            mockXhbClobRepository,
            mockEntityManager
        );
    }
    
    @Test
    void testProcessBaisEmptyPddaHearingProgressEvent() {
        // --- Arrange ---
        setupEmptyPddaHearingProgressEventFile();

        // EM guard
        EasyMock.expect(mockEntityManager.isOpen()).andReturn(true).anyTimes();
        
        // Court lookups
        List<XhbCourtDao> byCrest = List.of(DummyCourtUtil.getXhbCourtDao(-453, COURT1));
        EasyMock.expect(mockXhbCourtRepository.findByCrestCourtIdValueSafe(EasyMock.isA(String.class)))
                .andStubReturn(byCrest);
        
        // Message type lookup
        Optional<XhbRefPddaMessageTypeDao> pddaRefMessageTypeDao =
            Optional.of(DummyPdNotifierUtil.getXhbPddaMessageTypeDao());
        EasyMock.expect(mockPddaMessageHelper.findByMessageType("PddaHearingProgress"))
                .andReturn(pddaRefMessageTypeDao);
        
        // Duplicate check inside createBaisMessage(...)
        EasyMock.expect(mockPddaMessageHelper.findByCpDocumentName(EasyMock.isA(String.class)))
                .andReturn(Optional.empty());
        
        // CLOB update
        XhbClobDao clob = DummyFormattingUtil.getXhbClobDao(0L, "<clob>");
        EasyMock.expect(mockXhbClobRepository.update(EasyMock.isA(XhbClobDao.class)))
                .andStubReturn(Optional.of(clob));
        
        // Save call
        mockPddaMessageHelper.savePddaMessage(EasyMock.isA(XhbPddaMessageDao.class));
        EasyMock.expectLastCall();
        
        EasyMock.replay(
            mockEntityManager,
            mockXhbCourtRepository,
            mockPddaMessageHelper,
            mockXhbClobRepository
        );

        // Stub the static remapper so it doesn't hit room/site lookups
        try (MockedStatic<PddaMessageUtil> mocked =
                 Mockito.mockStatic(PddaMessageUtil.class, Mockito.CALLS_REAL_METHODS)) {
            mocked.when(() -> PddaMessageUtil.translatePublicDisplayEvent(
                        Mockito.any(PublicDisplayEvent.class),
                        Mockito.any(XhbCourtRepository.class),
                        Mockito.any(XhbCourtRoomRepository.class),
                        Mockito.any(XhbCourtSiteRepository.class)))
                  .thenAnswer(inv -> inv.getArgument(0));

            // --- Act ---
            classUnderTest.setupSftpClientAndProcessBaisData(
                sftpConfig, sftpConfig.getSshClient(), false);
        } catch (Exception e) {
            fail(e);
        }
        
        // Verify
        EasyMock.verify(
            mockEntityManager,
            mockXhbCourtRepository,
            mockPddaMessageHelper,
            mockXhbClobRepository
        );
    }
    
    @Test
    void testProcessBaisCaseStatusEvent() {
        // --- Arrange ---
        setupCaseStatusEventFile();

        // EM guard
        EasyMock.expect(mockEntityManager.isOpen()).andReturn(true).anyTimes();
        
        // Entered processCaseStatusEvent(...)
        List<XhbCourtSiteDao> xhbCourtSiteDao = List.of(DummyCourtUtil.getXhbCourtSiteDao());
        EasyMock.expect(mockXhbCourtSiteRepository.findByCourtIdSafe(EasyMock.isA(Integer.class)))
                .andStubReturn(xhbCourtSiteDao);
        
        Optional<XhbCaseDao> xhbCaseDao = Optional.of(DummyCaseUtil.getXhbCaseDao());
        EasyMock.expect(mockXhbCaseRepository.findByNumberTypeAndCourtSafe(
            EasyMock.isA(Integer.class), EasyMock.isA(String.class), EasyMock.isA(Integer.class)))
                .andStubReturn(xhbCaseDao);
        
        Optional<XhbHearingDao> xhbHearingDao = Optional.of(DummyHearingUtil.getXhbHearingDao());
        EasyMock.expect(mockXhbHearingRepository.findByCaseIdWithTodaysStartDateSafe(
            EasyMock.isA(Integer.class), EasyMock.isA(LocalDateTime.class)))
                .andStubReturn(xhbHearingDao);
        
        List<XhbSittingDao> xhbSittingDao = List.of(DummyHearingUtil.getXhbSittingDao());
        EasyMock.expect(mockXhbSittingRepository.findByCourtRoomIdAndCourtSiteIdWithTodaysSittingDateSafe(
            EasyMock.isA(Integer.class), EasyMock.isA(Integer.class), EasyMock.isA(LocalDateTime.class)))
                .andStubReturn(xhbSittingDao);
        
        Optional<XhbScheduledHearingDao> xhbScheduledHearingDao = 
            Optional.of(DummyHearingUtil.getXhbScheduledHearingDao());
        EasyMock.expect(mockXhbScheduledHearingRepository.findBySittingIdAndHearingIdSafe(
            EasyMock.isA(Integer.class), EasyMock.isA(Integer.class)))
                .andStubReturn(xhbScheduledHearingDao);
        
        // Court lookups
        List<XhbCourtDao> byCrest = List.of(DummyCourtUtil.getXhbCourtDao(-453, COURT1));
        EasyMock.expect(mockXhbCourtRepository.findByCrestCourtIdValueSafe(EasyMock.isA(String.class)))
                .andStubReturn(byCrest);
        
        // Message type lookup
        Optional<XhbRefPddaMessageTypeDao> pddaRefMessageTypeDao =
            Optional.of(DummyPdNotifierUtil.getXhbPddaMessageTypeDao());
        EasyMock.expect(mockPddaMessageHelper.findByMessageType("CaseStatus"))
                .andReturn(pddaRefMessageTypeDao);

        // Duplicate check inside createBaisMessage(...)
        EasyMock.expect(mockPddaMessageHelper.findByCpDocumentName(EasyMock.isA(String.class)))
                .andReturn(Optional.empty());
        
        // CLOB update
        XhbClobDao clob = DummyFormattingUtil.getXhbClobDao(0L, "<clob>");
        EasyMock.expect(mockXhbClobRepository.update(EasyMock.isA(XhbClobDao.class)))
                .andStubReturn(Optional.of(clob));

        // Save call
        mockPddaMessageHelper.savePddaMessage(EasyMock.isA(XhbPddaMessageDao.class));
        EasyMock.expectLastCall();

        EasyMock.replay(
            mockXhbCourtRepository,
            mockXhbCourtSiteRepository,
            mockXhbCaseRepository,
            mockXhbHearingRepository,
            mockXhbCourtRoomRepository,
            mockXhbSittingRepository,
            mockXhbScheduledHearingRepository,
            mockPddaMessageHelper,
            mockXhbClobRepository,
            mockEntityManager
        );

        // Stub the static remapper so it doesn't hit room/site lookups
        try (MockedStatic<PddaMessageUtil> mocked =
                 Mockito.mockStatic(PddaMessageUtil.class, Mockito.CALLS_REAL_METHODS)) {
            mocked.when(() -> PddaMessageUtil.translatePublicDisplayEvent(
                        Mockito.any(PublicDisplayEvent.class),
                        Mockito.any(XhbCourtRepository.class),
                        Mockito.any(XhbCourtRoomRepository.class),
                        Mockito.any(XhbCourtSiteRepository.class)))
                  .thenAnswer(inv -> inv.getArgument(0));

            // --- Act ---
            classUnderTest.setupSftpClientAndProcessBaisData(
                sftpConfig, sftpConfig.getSshClient(), false);
        } catch (Exception e) {
            fail(e);
        }
        
        // Verify
        EasyMock.verify(
            mockXhbCourtRepository,
            mockXhbCourtSiteRepository,
            mockXhbCaseRepository,
            mockXhbHearingRepository,
            mockXhbCourtRoomRepository,
            mockXhbSittingRepository,
            mockXhbScheduledHearingRepository,
            mockPddaMessageHelper,
            mockXhbClobRepository,
            mockEntityManager
        );
    }
    
    @Test
    void testUpdateCppStagingInboundRecords() {
        // Setup
        XhbCppStagingInboundDao xhbCppStagingInboundDao =
            DummyPdNotifierUtil.getXhbCppStagingInboundDao();
        List<XhbCppStagingInboundDao> cppStagingInboundList = new ArrayList<>();
        cppStagingInboundList.add(xhbCppStagingInboundDao);
        String userDisplayName = TESTUSER;
        for (XhbCppStagingInboundDao dao : cppStagingInboundList) {
            EasyMock
                .expect(mockCppStagingInboundHelper.updateCppStagingInbound(dao, userDisplayName))
                .andReturn(Optional.of(xhbCppStagingInboundDao));
        }
        EasyMock.replay(mockCppStagingInboundHelper);
        // Run
        boolean result = false;
        try {
            PddaMessageUtil.updateCppStagingInboundRecords(mockCppStagingInboundHelper,
                cppStagingInboundList, userDisplayName);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        // Checks
        EasyMock.verify(mockCppStagingInboundHelper);
        assertTrue(result, NOT_TRUE);
    }


    @Test
    void testProcessBaisMessages() {
        List<XhbCourtDao> courtDaos = new ArrayList<>();
        courtDaos.add(DummyCourtUtil.getXhbCourtDao(-453, COURT1));
        EasyMock
            .expect(mockXhbCourtRepository.findByCrestCourtIdValueSafe(EasyMock.isA(String.class)))
            .andStubReturn(courtDaos);
        
        mockPddaMessageHelper.savePddaMessage(EasyMock.isA(XhbPddaMessageDao.class));
        EasyMock.expectLastCall();

        EasyMock.expect(mockEntityManager.isOpen()).andReturn(true).anyTimes();
        EasyMock.replay(mockEntityManager);
        EasyMock.replay(mockXhbCourtRepository);
        EasyMock.replay(mockPddaMessageHelper);

        boolean result = classUnderTest.processBaisMessages(sftpServer.getPort());

        assertFalse(result, "Expected processBaisMessages to return false");
    }


    @Test
    void testProcessDataFromBais() {

        List<XhbCourtDao> courtDaos = new ArrayList<>();
        courtDaos.add(DummyCourtUtil.getXhbCourtDao(-453, COURT1));
        EasyMock
            .expect(mockXhbCourtRepository.findByCrestCourtIdValueSafe(EasyMock.isA(String.class)))
            .andStubReturn(courtDaos);

        EasyMock.replay(mockXhbCourtRepository);

        CourtConfigurationChange courtConfigurationChange = new CourtConfigurationChange(1, COURT1, true);

        ConfigurationChangeEvent publicDisplayEvent =
            new ConfigurationChangeEvent(courtConfigurationChange);
        BaisXhibitValidation bxv = new BaisXhibitValidation(mockXhbCourtRepository);
        BaisCpValidation bcv = new BaisCpValidation(mockXhbCourtRepository);

        // Run multiple tests
        testProcessDataFromBaisXhibit(bxv, publicDisplayEvent);

        testProcessDataFromBaisCp(bcv, publicDisplayEvent);
    }

    private void testProcessDataFromBaisXhibit(BaisXhibitValidation bxv,
        ConfigurationChangeEvent publicDisplayEvent) {

        // Test 1 - invalid number of parts in filename
        String xhibitFilename = FILENAME_XHB_INVALID_PARTS;

        String result = bxv.validateFilename(xhibitFilename, publicDisplayEvent);
        assertTrue(result.length() > 0, ALL_GOOD); // There is an error

        // Test 2 - valid number of parts but invalid filename
        xhibitFilename = FILENAME_XHB_INVALID_TITLE;
        result = bxv.validateFilename(xhibitFilename, publicDisplayEvent);
        assertTrue(result.length() > 0, ALL_GOOD); // There is an error

        // Test 3 - valid number of parts and valid filename and valid event
        xhibitFilename = FILENAME_XHB_PD_VALID;
        result = bxv.validateFilename(xhibitFilename, publicDisplayEvent);
        assertNull(result, ALL_GOOD);

        // Test 4 - Event is an error
        xhibitFilename = FILENAME_XHB_PD_VALID;
        HearingStatusEvent hearingStatusEvent = new HearingStatusEvent(null, null);
        result = bxv.validateFilename(xhibitFilename, hearingStatusEvent);
        assertNull(result, ALL_GOOD);

        // Test 5 - Public Display CP messages from XHIBIT
        // Valid
        xhibitFilename = FILENAME_XHB_PD_FROMCP_VALID;
        result = bxv.validateFilename(xhibitFilename, null);
        assertNull(result, ALL_GOOD);
        // Invalid
        xhibitFilename = FILENAME_XHB_INVALID_MESSAGE_TYPE; // Invalid message type
        result = bxv.validateFilename(xhibitFilename, null);
        assertTrue(result.length() > 0, ALL_GOOD); // There is an error
        // Invalid
        xhibitFilename = FILENAME_XHB_INVALID_COURT; // Invalid court id in filename
        result = bxv.validateFilename(xhibitFilename, null);
        assertTrue(result.length() > 0, ALL_GOOD); // There is an error

        // Test 6 - Daily Lists from XHIBIT
        xhibitFilename = FILENAME_XHB_DL_VALID;
        result = bxv.validateFilename(xhibitFilename, null);
        assertNull(result, ALL_GOOD);
    }

    private void testProcessDataFromBaisCp(BaisCpValidation bcv,
        ConfigurationChangeEvent publicDisplayEvent) {

        // Test 1 - invalid number of parts in filename
        String cpFilename = FILENAME_CP_INVALID_PARTS;
        String result = bcv.validateFilename(cpFilename, publicDisplayEvent);
        assertTrue(result.length() > 0, ALL_GOOD); // There is an error

        // Test 2 - valid number of parts but invalid filename
        cpFilename = FILENAME_CP_INVALID_TITLE;
        result = bcv.validateFilename(cpFilename, publicDisplayEvent);
        assertTrue(result.length() > 0, ALL_GOOD); // There is an error

        // Test 3 - valid number of parts and valid filename and valid event
        cpFilename = FILENAME_CP_PD_VALID;
        result = bcv.validateFilename(cpFilename, publicDisplayEvent);
        assertNull(result, ALL_GOOD);

        // Test 4 - Cp only - check the file extension
        cpFilename = FILENAME_CP_INVALID_EXTENSION;
        result = bcv.validateFilename(cpFilename, publicDisplayEvent);
        assertTrue(result.length() > 0, ALL_GOOD); // There is an error

        // Test 5 - Event is an error
        cpFilename = FILENAME_CP_PD_VALID;
        HearingStatusEvent hearingStatusEvent = new HearingStatusEvent(null, null);
        result = bcv.validateFilename(cpFilename, hearingStatusEvent);
        assertNull(result, ALL_GOOD);

        // Test 6 - Daily Lists from CP
        cpFilename = FILENAME_CP_DL_VALID;
        result = bcv.validateFilename(cpFilename, null);
        assertNull(result, ALL_GOOD);

        // Test 7 - Firm Lists from CP
        cpFilename = FILENAME_CP_FL_VALID;
        result = bcv.validateFilename(cpFilename, null);
        assertNull(result, ALL_GOOD);

        // Test 8 - Warned Lists from CP
        cpFilename = FILENAME_CP_WL_VALID;
        result = bcv.validateFilename(cpFilename, null);
        assertNull(result, ALL_GOOD);

        // Test 9 - Web Pages from CP
        cpFilename = FILENAME_CP_WP_VALID;
        result = bcv.validateFilename(cpFilename, null);
        assertNull(result, ALL_GOOD);

    }

    @Test
    void testGetUpdatedFilename() {
        // Setup
        String filenameXhb = FILENAME_XHB_DL_VALID;
        String filenameCp = FILENAME_CP_DL_VALID;
        String updatedFilename = filenameXhb + " list_filename = " + filenameCp;
        String listType = "DailyList";
        String result = classUnderTest.getUpdatedFilename(filenameXhb, listType);
        assertTrue(updatedFilename.equals(result), ALL_GOOD);
    }

    @Test
    void testGetFilenameMessageType() {
        // Setup
        List<XhbCourtDao> courtDaos = new ArrayList<>();
        courtDaos.add(DummyCourtUtil.getXhbCourtDao(-453, COURT1));
        EasyMock
            .expect(mockXhbCourtRepository.findByCrestCourtIdValueSafe(EasyMock.isA(String.class)))
            .andStubReturn(courtDaos);

        EasyMock.replay(mockXhbCourtRepository);

        BaisXhibitValidation bxv = new BaisXhibitValidation(mockXhbCourtRepository);

        String filenamePart = "XPD";
        String result = bxv.getFilenameMessageType(filenamePart);
        assertTrue("XhibitPublicDisplay".equals(result), ALL_GOOD);

        filenamePart = "XDL";
        result = bxv.getFilenameMessageType(filenamePart);
        assertTrue("XhibitDailyList".equals(result), ALL_GOOD);

        filenamePart = "CPD";
        result = bxv.getFilenameMessageType(filenamePart);
        assertTrue("CpPublicDisplay".equals(result), ALL_GOOD);

        filenamePart = "INV";
        result = bxv.getFilenameMessageType(filenamePart);
        assertFalse("DailyList".equals(result), ALL_GOOD);
    }


    @Test
    void testGetListType() {
        // Setup
        String clobData1 =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><cs:DailyList xmlns:cs=\"http://www.courtservice.gov.uk/schemas/courtservice\"";
        String result = classUnderTest.getListType(clobData1);
        assertTrue(DAILY_LIST.equals(result), ALL_GOOD);

        String clobData2 =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><cs:FirmList xmlns:cs=\"http://www.courtservice.gov.uk/schemas/courtservice\"";
        result = classUnderTest.getListType(clobData2);
        assertTrue(FIRM_LIST.equals(result), ALL_GOOD);

        String clobData3 =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><cs:WarnedList xmlns:cs=\"http://www.courtservice.gov.uk/schemas/courtservice\"";
        result = classUnderTest.getListType(clobData3);
        assertTrue(WARNED_LIST.equals(result), ALL_GOOD);

        String clobData4 =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><cs:AnotherList xmlns:cs=\"http://www.courtservice.gov.uk/schemas/courtservice\"";
        result = classUnderTest.getListType(clobData4);
        assertTrue(UNKNOWN.equals(result), ALL_GOOD);
    }


    private void setupCpFiles() {
        try {
            sftpServer.putFile("/directory/PublicDisplay_453_20241009130506_1.xml",
                "content of file",
                Charset.defaultCharset());
        } catch (IOException e) {
            LOG.error("Error putting file", e);
        }
    }


    @SuppressWarnings("PMD")
    private void setupXhibitFiles() {
        try {
            CourtRoomIdentifier courtRoomIdentifier = new CourtRoomIdentifier(1, 1, "Test Court", 123);
            CourtConfigurationChange courtConfigurationChange =
                new CourtConfigurationChange(1, COURT1, true);

            ConfigurationChangeEvent publicDisplayEvent =
                new ConfigurationChangeEvent(courtConfigurationChange);
            HearingStatusEvent hearingStatusEvent =
                new HearingStatusEvent(courtRoomIdentifier, null);

            byte[] serializedObject =
                PddaSerializationUtils.serializePublicEvent(hearingStatusEvent);
            String encoded = PddaSerializationUtils.encodePublicEvent(serializedObject);

            sftpServer.putFile("/directory/PDDA_XPD_34_1_457_2024101409000.xml", encoded,
                Charset.defaultCharset());
        } catch (IOException e) {
            LOG.error("Error putting file", e);
        }
    }
    
    private void setupPddaHearingProgressEventFile() {
        try {
            PddaHearingProgressEvent pddaHearingProgressEvent = new PddaHearingProgressEvent();
            pddaHearingProgressEvent.setCourtId(1);
            pddaHearingProgressEvent.setCourtName(COURT1);
            pddaHearingProgressEvent.setCourtRoomName(COURTROOM1);
            pddaHearingProgressEvent.setCaseType("T");
            pddaHearingProgressEvent.setCaseNumber(20250001);
            pddaHearingProgressEvent.setHearingProgressIndicator(0);
            pddaHearingProgressEvent.setIsCaseActive("Y");
            
            byte[] serializedObject =
                PddaSerializationUtils.serializePublicEvent(pddaHearingProgressEvent);
            String encoded = PddaSerializationUtils.encodePublicEvent(serializedObject);

            sftpServer.putFile("/directory/PDDA_XPD_34_1_457_20251020090501.xml", encoded,
                Charset.defaultCharset());
        } catch (IOException e) {
            LOG.error("Error putting file", e);
        }
    }
    
    private void setupEmptyPddaHearingProgressEventFile() {
        try {
            PddaHearingProgressEvent pddaHearingProgressEvent = new PddaHearingProgressEvent();
            
            byte[] serializedObject =
                PddaSerializationUtils.serializePublicEvent(pddaHearingProgressEvent);
            String encoded = PddaSerializationUtils.encodePublicEvent(serializedObject);

            sftpServer.putFile("/directory/PDDA_XPD_34_1_457_20251023090501.xml", encoded,
                Charset.defaultCharset());
        } catch (IOException e) {
            LOG.error("Error putting file", e);
        }
    }
    
    private void setupCaseStatusEventFile() {
        try {
            CourtRoomIdentifier courtRoomIdentifier =
                new CourtRoomIdentifier(81, 8112, "Court Name", 1234);
            CourtLogSubscriptionValue courtLogSubscriptionValue = new CourtLogSubscriptionValue();
            CourtLogViewValue courtLogViewValue = new CourtLogViewValue();
            courtLogViewValue.setEntryDate(new Date());
            courtLogSubscriptionValue.setCourtLogViewValue(courtLogViewValue);
            CaseCourtLogInformation caseCourtLogInformation =
                new CaseCourtLogInformation(courtLogSubscriptionValue, true);
            CaseStatusEvent caseStatusEvent = new CaseStatusEvent(courtRoomIdentifier, caseCourtLogInformation);
            
            byte[] serializedObject =
                PddaSerializationUtils.serializePublicEvent(caseStatusEvent);
            String encoded = PddaSerializationUtils.encodePublicEvent(serializedObject);

            sftpServer.putFile("/directory/PDDA_XPD_34_1_457_20251104090501.xml", encoded,
                Charset.defaultCharset());
        } catch (IOException e) {
            LOG.error("Error putting file", e);
        }
    }

    @SuppressWarnings("PMD")
    private void setupSftpConfig() {
        sftpConfig.setHost("localhost");
        sftpConfig.setPort(sftpServer.getPort());
        sftpConfig.setCpUsername("cpUsername");
        sftpConfig.setCpPassword("cpPassword");
        sftpConfig.setCpRemoteFolder(TEST_SFTP_DIRECTORY);
        sftpConfig.setXhibitUsername("xhibitUsername");
        sftpConfig.setXhibitPassword("xhibitPassword");
        sftpConfig.setXhibitRemoteFolder(TEST_SFTP_DIRECTORY);
        sftpConfig.setActiveRemoteFolder(TEST_SFTP_DIRECTORY);

        try {
            SSHClient ssh = new SftpConfigHelper(mockEntityManager).getNewSshClient();
            ssh.connect(sftpConfig.getHost(), sftpConfig.getPort());
            ssh.authPassword(sftpConfig.getCpUsername(), sftpConfig.getCpPassword());
            sftpConfig.setSshClient(ssh);

            SFTPClient sftpClient = new SFTPClient(ssh);
            sftpConfig.setSshjSftpClient(sftpClient);

            assertNotNull(ssh, "SSHClient is not null");
            assertNotNull(sftpClient, "SFTPClient is not null");
        } catch (IOException e) {
            LOG.error("Error setting up SFTP config", e);
        }
    }
    
    
    @Test
    void testProcessBaisMessages_ConnectionErrors_setsErrorTrue() {
        int badPort = 1; // valid port, almost certainly closed
        boolean error = classUnderTest.processBaisMessages(badPort);
        assertTrue(error, "Expected error=true when both CP and XHIBIT connects fail");
    }

    
    
    @Test
    void testSetupSftpClientAndProcessBaisData_NewSftpClientThrows_isCaught() throws Exception {
        SSHClient ssh = EasyMock.createMock(SSHClient.class);
        EasyMock.expect(ssh.newSFTPClient()).andThrow(new IOException("boom"));
        EasyMock.replay(ssh);

        SftpConfig cfg = new SftpConfig();
        // exercise: nothing should bubble out
        classUnderTest.setupSftpClientAndProcessBaisData(cfg, ssh, true);

        EasyMock.verify(ssh);
    }
    
    
    @Test
    void testProcessDataFromBais_RetrieveThrows_isCaught() throws Exception {
        // Partial mock with BOTH methods mocked: retrieveFromBais(...) and getCourtRepository()
        SftpService spy = EasyMock.partialMockBuilder(SftpService.class)
            .withConstructor(
                mockEntityManager,
                mockXhbConfigPropRepository,
                mockEnvironment,
                mockPddaMessageHelper,
                mockXhbClobRepository,
                mockXhbCourtRepository,
                mockXhbCourtRoomRepository,
                mockXhbCourtSiteRepository,
                mockXhbCaseRepository,
                mockXhbHearingRepository,
                mockXhbSittingRepository,
                mockXhbScheduledHearingRepository)
            .addMockedMethod("retrieveFromBais", SftpConfig.class, BaisValidation.class)
            .addMockedMethod("getCourtRepository")
            .createMock();

        // When processDataFromBais(...) runs, it first calls getCourtRepository()
        EasyMock.expect(spy.getCourtRepository()).andReturn(mockXhbCourtRepository).anyTimes();

        // Force retrieveFromBais(...) to throw so the catch in processDataFromBais is exercised.
        SftpConfig cfg = new SftpConfig();
        spy.retrieveFromBais(EasyMock.eq(cfg), EasyMock.anyObject(BaisValidation.class));
        EasyMock.expectLastCall().andThrow(new IOException("retrieve failed"));

        EasyMock.replay(spy);

        var m = SftpService.class.getDeclaredMethod("processDataFromBais", SftpConfig.class, boolean.class);
        m.setAccessible(true);

        // Should not throw; IOException is caught inside processDataFromBais
        m.invoke(spy, cfg, true);

        EasyMock.verify(spy);
    }

    
    @Test
    void testRetrieveFromBais_FinallyBlockRunsWhenClientNull() throws Exception {
        // Fake helper that tolerates null client and returns empty maps
        PddaSftpHelperSshj fake = EasyMock.createMock(PddaSftpHelperSshj.class);
        EasyMock.expect(fake.sftpFetch(EasyMock.isNull(), EasyMock.anyString(),
                EasyMock.anyObject(BaisValidation.class), EasyMock.anyString()))
            .andReturn(Collections.emptyMap()).times(2); // once in try, once in finally
        // listFilesInFolder never reached because files map is empty
        EasyMock.replay(fake);

        SftpService svc = new SftpService(mockEntityManager, mockXhbConfigPropRepository, mockEnvironment,
            mockPddaMessageHelper, mockXhbClobRepository, mockXhbCourtRepository,
            mockXhbCourtRoomRepository, mockXhbCourtSiteRepository, mockXhbCaseRepository,
            mockXhbHearingRepository, mockXhbSittingRepository, mockXhbScheduledHearingRepository) {
            @Override
            protected PddaSftpHelperSshj getPddaSftpHelperSshj() { 
                return fake;
            }
        };

        SftpConfig cfg = new SftpConfig(); // note: cfg.getSshjSftpClient() is null
        svc.retrieveFromBais(cfg, new SftpService.BaisCpValidation(mockXhbCourtRepository));

        EasyMock.verify(fake);
    }

    
    @Test
    void testGetBaisFileList_HelperThrows_returnsEmptyMap() throws Exception {
        PddaSftpHelperSshj throwing = EasyMock.createMock(PddaSftpHelperSshj.class);
        EasyMock.expect(throwing.sftpFetch(EasyMock.anyObject(), EasyMock.anyString(),
                EasyMock.anyObject(BaisValidation.class), EasyMock.anyString()))
            .andThrow(new RuntimeException("fetch fail"));
        EasyMock.replay(throwing);

        SftpService svc = new SftpService(mockEntityManager, mockXhbConfigPropRepository, mockEnvironment,
            mockPddaMessageHelper, mockXhbClobRepository, mockXhbCourtRepository,
            mockXhbCourtRoomRepository, mockXhbCourtSiteRepository, mockXhbCaseRepository,
            mockXhbHearingRepository, mockXhbSittingRepository, mockXhbScheduledHearingRepository) {
            @Override
            protected PddaSftpHelperSshj getPddaSftpHelperSshj() {
                return throwing;
            }
        };

        var m = SftpService.class.getDeclaredMethod("getBaisFileList", SftpConfig.class, BaisValidation.class);
        m.setAccessible(true);

        @SuppressWarnings("unchecked")
        Map<String,String> out = (Map<String,String>) m.invoke(
            svc, new SftpConfig(), new SftpService.BaisCpValidation(mockXhbCourtRepository));

        assertTrue(out.isEmpty(), "Expected empty map when helper throws");
        EasyMock.verify(throwing);
    }
    
    
    private SftpService prepServiceForCreateBaisMessage() {
        // Courts: crestCourtId -> court exists
        List<XhbCourtDao> courts = new ArrayList<>();
        courts.add(DummyCourtUtil.getXhbCourtDao(-453, "Court1"));
        EasyMock.expect(mockXhbCourtRepository.findByCrestCourtIdValueSafe(EasyMock.isA(String.class)))
            .andStubReturn(courts);

        // Message type lookup succeeds (no NotFoundException)
        EasyMock.expect(mockPddaMessageHelper.findByMessageType(EasyMock.anyString()))
            .andReturn(Optional.of(DummyPdNotifierUtil.getXhbPddaMessageTypeDao())).anyTimes();

        // No duplicate PDDA message entry
        EasyMock.expect(mockPddaMessageHelper.findByCpDocumentName(EasyMock.anyString()))
            .andReturn(Optional.of(DummyPdNotifierUtil.getXhbPddaMessageDao())).anyTimes();

        EasyMock.replay(mockXhbCourtRepository, mockPddaMessageHelper, mockXhbClobRepository);

        // Fake helper for the 'finally' sftpDeleteFile
        PddaSftpHelperSshj fake = EasyMock.createMock(PddaSftpHelperSshj.class);
        try {
            fake.sftpDeleteFile(EasyMock.anyObject(SFTPClient.class), EasyMock.anyString(),
                EasyMock.anyString(), EasyMock.anyObject(BaisValidation.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        EasyMock.expectLastCall().anyTimes();
        EasyMock.replay(fake);

        return new SftpService(mockEntityManager, mockXhbConfigPropRepository, mockEnvironment,
            mockPddaMessageHelper, mockXhbClobRepository, mockXhbCourtRepository,
            mockXhbCourtRoomRepository, mockXhbCourtSiteRepository, mockXhbCaseRepository,
            mockXhbHearingRepository, mockXhbSittingRepository, mockXhbScheduledHearingRepository) {
            @Override
            protected PddaSftpHelperSshj getPddaSftpHelperSshj() {
                return fake;
            }
        };
    }

    @Test
    void testProcessBaisFile_Cpd_branch_executes() throws Exception {
        SftpService svc = prepServiceForCreateBaisMessage();

        SftpConfig cfg = new SftpConfig();
        cfg.setSshjSftpClient(EasyMock.createMock(SFTPClient.class)); // used by finally-delete
        String filename = "PDDA_CPD_34_1_453_20241209130506";
        String clobData = "<whatever/>";

        var m = SftpService.class.getDeclaredMethod(
            "processBaisFile", SftpConfig.class, BaisValidation.class, String.class, String.class);
        m.setAccessible(true);
        // should run through CPD branch without sending a message (but still creating DB message)
        m.invoke(svc, cfg, new SftpService.BaisXhibitValidation(mockXhbCourtRepository), filename, clobData);
    }

    @Test
    void testProcessBaisFile_Xwp_branch_executes() throws Exception {
        SftpService svc = prepServiceForCreateBaisMessage();

        SftpConfig cfg = new SftpConfig();
        cfg.setSshjSftpClient(EasyMock.createMock(SFTPClient.class));
        String filename = "PDDA_XWP_34_1_453_20241209130506";
        String clobData = "<whatever/>";

        var m = SftpService.class.getDeclaredMethod(
            "processBaisFile", SftpConfig.class, BaisValidation.class, String.class, String.class);
        m.setAccessible(true);
        m.invoke(svc, cfg, new SftpService.BaisXhibitValidation(mockXhbCourtRepository), filename, clobData);
    }
    
    
    @Test
    void testProcessBaisFile_ListBranch_InvalidMessageType_and_InvalidFilename() throws Exception {
        // Fake helper used by finally{} delete (do nothing)
        PddaSftpHelperSshj fake = EasyMock.createMock(PddaSftpHelperSshj.class);
        fake.sftpDeleteFile(EasyMock.anyObject(SFTPClient.class), EasyMock.anyString(),
            EasyMock.anyString(), EasyMock.anyObject(BaisValidation.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.replay(fake);

        // Make message-type lookup succeed (so createBaisMessage won't throw)
        EasyMock.expect(mockPddaMessageHelper.findByMessageType(EasyMock.anyString()))
            .andReturn(Optional.of(DummyPdNotifierUtil.getXhbPddaMessageTypeDao())).anyTimes();
        // Mark as duplicate so createBaisMessage returns early (no CLOB / EMF)
        EasyMock.expect(mockPddaMessageHelper.findByCpDocumentName(EasyMock.anyString()))
            .andReturn(Optional.of(DummyPdNotifierUtil.getXhbPddaMessageDao())).anyTimes();
        EasyMock.replay(mockPddaMessageHelper);

        // Custom validation: force invalid filename and INVALID message type,
        // but still provide a court id so flow reaches createBaisMessage().
        BaisValidation v = new SftpService.BaisXhibitValidation(mockXhbCourtRepository) {
            @Override
            public String validateFilename(String f,
                uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicDisplayEvent e, boolean isList) {
                    return "bad filename";
            }
            
            @Override
            public String getMessageType(String f,
                uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicDisplayEvent e) {
                    return SftpService.INVALID_MESSAGE_TYPE;
            }
            
            @Override
            public Integer getCourtId(String f,
                uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicDisplayEvent e) {
                    return 453;
            }
            
            @Override
            public Optional<XhbPddaMessageDao> getPddaMessageDao(PddaMessageHelper h, String f) {
                return Optional.empty();
            }
        };

        SftpConfig cfg = new SftpConfig();
        cfg.setSshjSftpClient(EasyMock.createMock(SFTPClient.class));
        String filename = "PDDA_XDL_34_1_453_20241209130506"; // triggers list branch (else{})
        String clobData = "<?xml version=\"1.0\"?><cs:DailyList xmlns:cs=\"http://www.courtservice.gov.uk/schemas/courtservice\"/>";

        // Service with injected helper
        SftpService svc = new SftpService(mockEntityManager, mockXhbConfigPropRepository, mockEnvironment,
            mockPddaMessageHelper, mockXhbClobRepository, mockXhbCourtRepository,
            mockXhbCourtRoomRepository, mockXhbCourtSiteRepository, mockXhbCaseRepository,
            mockXhbHearingRepository, mockXhbSittingRepository, mockXhbScheduledHearingRepository) {
            @Override
            protected PddaSftpHelperSshj getPddaSftpHelperSshj() {
                return fake;
            }
        };
        
        var m = SftpService.class.getDeclaredMethod(
            "processBaisFile", SftpConfig.class, BaisValidation.class, String.class, String.class);
        m.setAccessible(true);
        m.invoke(svc, cfg, v, filename, clobData); // no exception expected
    }


    @Test
    void testProcessBaisFile_AlreadyProcessed_DeleteThrowsIoException_Caught() throws Exception {
        PddaSftpHelperSshj throwing = EasyMock.createMock(PddaSftpHelperSshj.class);

        // 1st delete (inside try): throws
        throwing.sftpDeleteFile(EasyMock.anyObject(SFTPClient.class), EasyMock.anyString(),
            EasyMock.anyString(), EasyMock.anyObject(BaisValidation.class));
        EasyMock.expectLastCall().andThrow(new IOException("boom")).once();
        
        // 2nd delete (inside finally): allowed, no throw
        throwing.sftpDeleteFile(EasyMock.anyObject(SFTPClient.class), EasyMock.anyString(),
            EasyMock.anyString(), EasyMock.anyObject(BaisValidation.class));
        EasyMock.expectLastCall().anyTimes();
        
        EasyMock.replay(throwing);

        EasyMock.expect(mockPddaMessageHelper.findByCpDocumentName("PublicDisplay_453_20241209130506.xml"))
            .andReturn(Optional.of(DummyPdNotifierUtil.getXhbPddaMessageDao()));
        EasyMock.replay(mockPddaMessageHelper);

        SftpConfig cfg = new SftpConfig();
        cfg.setSshjSftpClient(EasyMock.createMock(SFTPClient.class));

        SftpService svc = new SftpService(mockEntityManager, mockXhbConfigPropRepository, mockEnvironment,
            mockPddaMessageHelper, mockXhbClobRepository, mockXhbCourtRepository,
            mockXhbCourtRoomRepository, mockXhbCourtSiteRepository, mockXhbCaseRepository,
            mockXhbHearingRepository, mockXhbSittingRepository, mockXhbScheduledHearingRepository) {
            @Override
            protected PddaSftpHelperSshj getPddaSftpHelperSshj() {
                return throwing;
            }
        };
        
        var m = SftpService.class.getDeclaredMethod(
            "processBaisFile", SftpConfig.class, BaisValidation.class, String.class, String.class);
        m.setAccessible(true);

        m.invoke(svc, cfg, new SftpService.BaisCpValidation(mockXhbCourtRepository),
            "PublicDisplay_453_20241209130506.xml", "<ignored/>");
    }

    
    @Test
    void testCreateBaisMessage_MessageTypeCreatedWhenMissing() throws Exception {
        PddaSftpHelperSshj fake = EasyMock.createMock(PddaSftpHelperSshj.class);
        fake.sftpDeleteFile(EasyMock.anyObject(SFTPClient.class), EasyMock.anyString(),
            EasyMock.anyString(), EasyMock.anyObject(BaisValidation.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.replay(fake);

        SftpService svc = new SftpService(mockEntityManager, mockXhbConfigPropRepository, mockEnvironment,
            mockPddaMessageHelper, mockXhbClobRepository, mockXhbCourtRepository,
            mockXhbCourtRoomRepository, mockXhbCourtSiteRepository, mockXhbCaseRepository,
            mockXhbHearingRepository, mockXhbSittingRepository, mockXhbScheduledHearingRepository) {
            @Override
            protected PddaSftpHelperSshj getPddaSftpHelperSshj() {
                return fake;
            }
        };

        EasyMock.expect(mockPddaMessageHelper.findByMessageType(EasyMock.anyString()))
            .andReturn(Optional.empty()).anyTimes();
        // short-circuit after message-type creation (duplicate)
        EasyMock.expect(mockPddaMessageHelper.findByCpDocumentName(EasyMock.anyString()))
            .andReturn(Optional.of(DummyPdNotifierUtil.getXhbPddaMessageDao())).anyTimes();
        EasyMock.replay(mockPddaMessageHelper);
        
        // before invoking processBaisFile(...)
        List<XhbCourtDao> courts = new ArrayList<>();
        courts.add(DummyCourtUtil.getXhbCourtDao(-453, "Court1")); // ok if negative in Dummy util
        EasyMock.expect(mockXhbCourtRepository.findByCrestCourtIdValueSafe(EasyMock.isA(String.class)))
            .andStubReturn(courts);
        EasyMock.replay(mockXhbCourtRepository);


        try (MockedStatic<uk.gov.hmcts.pdda.business.services.pdda.PddaMessageUtil> mocked =
                 Mockito.mockStatic(uk.gov.hmcts.pdda.business.services.pdda.PddaMessageUtil.class)) {
            mocked.when(() -> uk.gov.hmcts.pdda.business.services.pdda.PddaMessageUtil.createMessageType(
                    Mockito.any(PddaMessageHelper.class),
                    Mockito.anyString(),
                    Mockito.any(LocalDateTime.class)))
                  .thenReturn(Optional.of(DummyPdNotifierUtil.getXhbPddaMessageTypeDao()));

            SftpConfig cfg = new SftpConfig();
            cfg.setSshjSftpClient(EasyMock.createMock(SFTPClient.class));

            String filename = "PDDA_XDL_34_1_453_20241209130506";
            String clobData = """
                <?xml version="1.0"?>
                <cs:DailyList xmlns:cs="http://www.courtservice.gov.uk/schemas/courtservice"/>""";

            var m = SftpService.class.getDeclaredMethod(
                "processBaisFile", SftpConfig.class, BaisValidation.class, String.class, String.class);
            m.setAccessible(true);
            m.invoke(svc, cfg, new SftpService.BaisXhibitValidation(mockXhbCourtRepository), filename, clobData);
        }
    }

    
    @Test
    void testCreateBaisMessage_StillMissing_ThrowsNotFound_Caught() throws Exception {
        PddaSftpHelperSshj fake = EasyMock.createMock(PddaSftpHelperSshj.class);
        fake.sftpDeleteFile(EasyMock.anyObject(SFTPClient.class), EasyMock.anyString(),
            EasyMock.anyString(), EasyMock.anyObject(BaisValidation.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.replay(fake);

        SftpService svc = new SftpService(mockEntityManager, mockXhbConfigPropRepository, mockEnvironment,
            mockPddaMessageHelper, mockXhbClobRepository, mockXhbCourtRepository,
            mockXhbCourtRoomRepository, mockXhbCourtSiteRepository, mockXhbCaseRepository,
            mockXhbHearingRepository, mockXhbSittingRepository, mockXhbScheduledHearingRepository) {
            @Override
            protected PddaSftpHelperSshj getPddaSftpHelperSshj() {
                return fake;
            }
        };

        EasyMock.expect(mockPddaMessageHelper.findByMessageType(EasyMock.anyString()))
            .andReturn(Optional.empty()).anyTimes();
        EasyMock.replay(mockPddaMessageHelper);
        
        // before invoking processBaisFile(...)
        List<XhbCourtDao> courts = new ArrayList<>();
        courts.add(DummyCourtUtil.getXhbCourtDao(-453, "Court1")); // ok if negative in Dummy util
        EasyMock.expect(mockXhbCourtRepository.findByCrestCourtIdValueSafe(EasyMock.isA(String.class)))
            .andStubReturn(courts);
        EasyMock.replay(mockXhbCourtRepository);


        try (MockedStatic<uk.gov.hmcts.pdda.business.services.pdda.PddaMessageUtil> mocked =
                 Mockito.mockStatic(uk.gov.hmcts.pdda.business.services.pdda.PddaMessageUtil.class)) {
            mocked.when(() -> uk.gov.hmcts.pdda.business.services.pdda.PddaMessageUtil.createMessageType(
                    Mockito.any(PddaMessageHelper.class),
                    Mockito.anyString(),
                    Mockito.any(LocalDateTime.class)))
                  .thenReturn(Optional.empty()); // force NotFoundException path

            SftpConfig cfg = new SftpConfig();
            cfg.setSshjSftpClient(EasyMock.createMock(SFTPClient.class));

            String filename = "PDDA_XDL_34_1_453_20241209130506";
            String clobData = """
                <?xml version="1.0"?>
                <cs:DailyList xmlns:cs="http://www.courtservice.gov.uk/schemas/courtservice"/>""";

            var m = SftpService.class.getDeclaredMethod(
                "processBaisFile", SftpConfig.class, BaisValidation.class, String.class, String.class);
            m.setAccessible(true);
            // NotFoundException is caught inside processBaisFile, so no throw to the test
            m.invoke(svc, cfg, new SftpService.BaisXhibitValidation(mockXhbCourtRepository), filename, clobData);
        }
    }

    
    @Test
    void testRetrieveFromBais_FinallyBlock_WhenFilesRemain_logsNotEmpty() throws Exception {
        // Helper returns empty on first call (try{}), non-empty on second (finally{})
        PddaSftpHelperSshj fake = EasyMock.createMock(PddaSftpHelperSshj.class);
        EasyMock.expect(fake.sftpFetch(EasyMock.isNull(), EasyMock.anyString(),
                EasyMock.anyObject(BaisValidation.class), EasyMock.anyString()))
            .andReturn(Collections.emptyMap()); // try{}

        EasyMock.expect(fake.sftpFetch(EasyMock.isNull(), EasyMock.anyString(),
                EasyMock.anyObject(BaisValidation.class), EasyMock.anyString()))
            .andReturn(Collections.singletonMap("leftover.xml", "<xml/>")); // finally{}

        // listFilesInFolder is never reached because the first fetch returns empty
        EasyMock.replay(fake);

        SftpService svc = new SftpService(mockEntityManager, mockXhbConfigPropRepository, mockEnvironment,
            mockPddaMessageHelper, mockXhbClobRepository, mockXhbCourtRepository,
            mockXhbCourtRoomRepository, mockXhbCourtSiteRepository, mockXhbCaseRepository,
            mockXhbHearingRepository, mockXhbSittingRepository, mockXhbScheduledHearingRepository) {
            @Override
            protected PddaSftpHelperSshj getPddaSftpHelperSshj() {
                return fake;
            }
        };

        // sshj client intentionally null to trigger the finally{} path in retrieveFromBais
        SftpConfig cfg = new SftpConfig();
        svc.retrieveFromBais(cfg, new SftpService.BaisCpValidation(mockXhbCourtRepository));

        EasyMock.verify(fake);
    }
    
    
    @Test
    void testGetFilenameMessageType_allCases() {
        BaisXhibitValidation bxv = new SftpService.BaisXhibitValidation(mockXhbCourtRepository);
        assertTrue("XhibitPublicDisplay".equals(bxv.getFilenameMessageType("XPD")));
        assertTrue("CpPublicDisplay".equals(bxv.getFilenameMessageType("CPD")));
        assertTrue("XhibitDailyList".equals(bxv.getFilenameMessageType("XDL")));
        assertTrue("XhibitWebPage".equals(bxv.getFilenameMessageType("XWP")));
        assertTrue("CpDailyList".equals(bxv.getFilenameMessageType("CDL")));
        assertTrue("CpFirmList".equals(bxv.getFilenameMessageType("CFL")));
        assertTrue("CpWarnedList".equals(bxv.getFilenameMessageType("CWL")));
        assertTrue(SftpService.INVALID_MESSAGE_TYPE.equals(bxv.getFilenameMessageType("ZZZ")));
    }

    
    @Test
    void testBaisXhibit_getMessageType_eventAndFilename() {
        BaisXhibitValidation bxv = new SftpService.BaisXhibitValidation(mockXhbCourtRepository);

        // Event present wins
        var event = new HearingStatusEvent(new CourtRoomIdentifier(1,1, "Test Court", 123), null);
        assertTrue("HearingStatus".equals(bxv.getMessageType("anything", event)));

        // From filename (event == null)
        assertTrue(SftpService.DAILY_LIST_DOCUMENT_TYPE
            .equals(bxv.getMessageType("...XDL...", null)));
        assertTrue(SftpService.WEB_PAGE_DOCUMENT_TYPE
            .equals(bxv.getMessageType("...XWP...", null)));
        assertTrue(SftpService.DAILY_LIST_DOCUMENT_TYPE
            .equals(bxv.getMessageType("...CDL...", null)));
        assertTrue(SftpService.FIRM_LIST_DOCUMENT_TYPE
            .equals(bxv.getMessageType("...CFL...", null)));
        assertTrue(SftpService.WARNED_LIST_DOCUMENT_TYPE
            .equals(bxv.getMessageType("...CWL...", null)));
        assertTrue(SftpService.PUBLIC_DISPLAY_DOCUMENT_TYPE
            .equals(bxv.getMessageType("...CPD...", null)));
        assertTrue(SftpService.INVALID_MESSAGE_TYPE
            .equals(bxv.getMessageType("...XXX...", null)));
    }

    
    @Test
    void testBaisXhibit_getCourtId_NumberFormat_returnsNull() {
        // Force the NumberFormatException branch regardless of repository behavior
        SftpService.BaisXhibitValidation bxv =
            new SftpService.BaisXhibitValidation(mockXhbCourtRepository) {
                @Override
                protected int getCourtIdFromCrestCourtId(String crestCourtId) {
                    throw new NumberFormatException("not numeric");
                }
            };

        // crestCourtId is non-numeric (“ABC”) — we expect the catch block to return null
        String bad = "PDDA_XDL_34_1_ABC_20241209130506";
        Integer out = bxv.getCourtId(bad, null);

        assertNull(out, "Expect null when crestCourtId isn't numeric");
    }


    
    @Test
    void testBaisXhibit_validateFilename_booleanOverload_delegates() {
        BaisXhibitValidation bxv = new SftpService.BaisXhibitValidation(mockXhbCourtRepository);
        String invalid = "PDDA_XPD_1_453_20241209130506"; // too few parts
        String twoArg = bxv.validateFilename(invalid, null);           // uses full logic
        String threeArg = bxv.validateFilename(invalid, null, true);   // delegates
        assertTrue(twoArg.length() > 0 && twoArg.equals(threeArg),
            "boolean overload should delegate to 2-arg version");
    }

    
    @Test
    void testBaisCp_validateFilename_booleanOverload_delegates() {
        // Return a real court so the only error we see is the extension (keeps it simple)
        List<XhbCourtDao> courts = new ArrayList<>();
        courts.add(DummyCourtUtil.getXhbCourtDao(-453, "Court1"));
        EasyMock.expect(mockXhbCourtRepository.findByCrestCourtIdValueSafe(EasyMock.isA(String.class)))
            .andStubReturn(courts);
        EasyMock.replay(mockXhbCourtRepository);

        BaisCpValidation bcv = new SftpService.BaisCpValidation(mockXhbCourtRepository);
        String badExt = "PublicDisplay_453_20240101120000.xl"; // triggers "Extension" error
        assertTrue(bcv.validateFilename(badExt, null)
                .equals(bcv.validateFilename(badExt, null, true)));
    }

    
    @Test
    void testBaisCp_validateFilename_missingCourtId_appendsError() {
        // Force getCourtId(...) to return null to cover the "append CrestCourtId" branch
        SftpService.BaisCpValidation bcv = new SftpService.BaisCpValidation(mockXhbCourtRepository) {
            @Override public Integer getCourtId(String filename, PublicDisplayEvent event) {
                return null;
            }
        };

        String filename = "PublicDisplay_999_20240101120000.xml"; // syntactically valid
        String out = bcv.validateFilename(filename, null);

        assertTrue(out != null && out.contains("CrestCourtId"),
            "Should include CrestCourtId error when court not found");
    }

    
    @Test
    void testBaisCp_getMessageType_and_getPublicDisplayEvent() {
        BaisCpValidation bcv = new SftpService.BaisCpValidation(mockXhbCourtRepository);
        assertTrue("PublicDisplay".equals(bcv.getMessageType("PublicDisplay_453_20240101120000.xml", null)));
        assertNull(bcv.getPublicDisplayEvent("anything", "<ignored/>"));
    }



}
