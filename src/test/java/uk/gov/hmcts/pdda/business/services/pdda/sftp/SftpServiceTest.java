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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.ConfigurationChangeEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.HearingStatusEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.CourtRoomIdentifier;
import uk.gov.courtservice.xhibit.common.publicdisplay.types.configuration.CourtConfigurationChange;
import uk.gov.hmcts.DummyCourtUtil;
import uk.gov.hmcts.DummyFormattingUtil;
import uk.gov.hmcts.DummyPdNotifierUtil;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobDao;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundDao;
import uk.gov.hmcts.pdda.business.entities.xhbpddamessage.XhbPddaMessageDao;
import uk.gov.hmcts.pdda.business.entities.xhbpddamessage.XhbPddaMessageRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrefpddamessagetype.XhbRefPddaMessageTypeDao;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * <p>
 * Title: PDDA Helper Test.
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
            mockEnvironment, mockPddaMessageHelper, mockXhbClobRepository, mockXhbCourtRepository);


    @TestSubject
    private final SftpHelperUtil subClassUnderTest = new SftpHelperUtil(mockEntityManager);

    private final SftpConfig sftpConfig = new SftpConfig();


    @Test
    void testDefaultConstructor() {
        boolean result = true;
        new SftpService(mockEntityManager, mockXhbConfigPropRepository, mockEnvironment,
            mockPddaMessageHelper, mockXhbClobRepository, mockXhbCourtRepository);
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
        // Setup
        setupXhibitFiles();
        Optional<XhbPddaMessageDao> pddaMessageDao =
            Optional.of(DummyPdNotifierUtil.getXhbPddaMessageDao());
        EasyMock
            .expect(
                mockPddaMessageHelper.findByCpDocumentName(FILENAME_XHB_PD_VALID))
            .andReturn(pddaMessageDao);

        EasyMock.expect(mockXhbPddaMessageRepository.update(EasyMock.isA(XhbPddaMessageDao.class)))
            .andReturn(Optional.of(DummyPdNotifierUtil.getXhbPddaMessageDao()));

        Optional<XhbRefPddaMessageTypeDao> pddaRefMessageTypeDao =
            Optional.of(DummyPdNotifierUtil.getXhbPddaMessageTypeDao());

        EasyMock.expect(mockPddaMessageHelper.findByMessageType("HearingStatus"))
            .andReturn(pddaRefMessageTypeDao);

        mockPddaMessageHelper.savePddaMessage(EasyMock.isA(XhbPddaMessageDao.class));
        EasyMock.expectLastCall();

        List<XhbCourtDao> courtDaos = new ArrayList<>();
        courtDaos.add(DummyCourtUtil.getXhbCourtDao(-453, COURT1));
        EasyMock
            .expect(mockXhbCourtRepository.findByCrestCourtIdValueSafe(EasyMock.isA(String.class)))
            .andStubReturn(courtDaos);

        EasyMock.expect(mockPddaMessageHelper.findByCpDocumentName(EasyMock.isA(String.class)))
            .andReturn(Optional.empty());
        
        XhbClobDao xhbClobDao = DummyFormattingUtil.getXhbClobDao(0L,
            "rO0ABXNyAEl1ay5nb3YuY291cnRzZXJ2aWNlLnhoaWJpdC5jb21tb24ucHVibGljZGlzcGxheS5ldmVudHMuS"
                + "GVhcmluZ1N0YXR1c0V2ZW500UDp/kCipvgCAAB4cgBJdWsuZ292LmNvdXJ0c2VydmljZS54aGliaXQuY29t"
                + "bW9uLnB1YmxpY2Rpc3BsYXkuZXZlbnRzLkNhc2VDb3VydFJvb21FdmVudAlMRnIzeCUnAgABTAAVY2FzZUN"
                + "oYW5nZUluZm9ybWF0aW9udABUTHVrL2dvdi9jb3VydHNlcnZpY2UveGhpYml0L2NvbW1vbi9wdWJsaWNkaX"
                + "NwbGF5L2V2ZW50cy90eXBlcy9DYXNlQ2hhbmdlSW5mb3JtYXRpb247eHIARXVrLmdvdi5jb3VydHNlcnZpY"
                + "2UueGhpYml0LmNvbW1vbi5wdWJsaWNkaXNwbGF5LmV2ZW50cy5Db3VydFJvb21FdmVudHOacr0CVH1NAgAB"
                + "TAATY291cnRSb29tSWRlbnRpZmllcnQAUkx1ay9nb3YvY291cnRzZXJ2aWNlL3hoaWJpdC9jb21tb24vcHV"
                + "ibGljZGlzcGxheS9ldmVudHMvdHlwZXMvQ291cnRSb29tSWRlbnRpZmllcjt4cHNyAFB1ay5nb3YuY291cn"
                + "RzZXJ2aWNlLnhoaWJpdC5jb21tb24ucHVibGljZGlzcGxheS5ldmVudHMudHlwZXMuQ291cnRSb29tSWRlb"
                + "nRpZmllcpbQ5TLRSKc5AgACTAAHY291cnRJZHQAE0xqYXZhL2xhbmcvSW50ZWdlcjtMAAtjb3VydFJvb21J"
                + "ZHEAfgAHeHBzcgARamF2YS5sYW5nLkludGVnZXIS4qCk94GHOAIAAUkABXZhbHVleHIAEGphdmEubGFuZy5"
                + "OdW1iZXKGrJUdC5TgiwIAAHhwAAAAAXEAfgALcA==");

        EasyMock.expect(mockXhbClobRepository.update(EasyMock.isA(XhbClobDao.class)))
            .andStubReturn(Optional.of(xhbClobDao));

        EasyMock.replay(mockXhbCourtRepository);
        EasyMock.replay(mockPddaMessageHelper);
        EasyMock.replay(mockXhbClobRepository);
        EasyMock.replay(mockXhbPddaMessageRepository);

        EasyMock.expect(mockEntityManager.isOpen()).andReturn(true).anyTimes();
        EasyMock.replay(mockEntityManager);

        // Run
        boolean result = false;
        try {
            classUnderTest.setupSftpClientAndProcessBaisData(sftpConfig, sftpConfig.getSshClient(),
                false);
        } catch (Exception exception) {
            fail(exception);
        }

        // Checks
        assertFalse(result, NOT_TRUE);
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

        CourtConfigurationChange courtConfigurationChange = new CourtConfigurationChange(1, true);

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
            CourtRoomIdentifier courtRoomIdentifier = new CourtRoomIdentifier(1, 1);
            CourtConfigurationChange courtConfigurationChange =
                new CourtConfigurationChange(1, true);

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
}
