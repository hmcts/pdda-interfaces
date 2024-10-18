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
@ExtendWith(EasyMockExtension.class)
@SuppressWarnings({"PMD.ExcessiveImports", "PMD.GodClass", "PMD.CouplingBetweenObjects"})
class SftpServiceTest {

    @RegisterExtension
    public final FakeSftpServerExtension sftpServer = new FakeSftpServerExtension();

    private static final Logger LOG = LoggerFactory.getLogger(SftpServiceTest.class);

    private static final String NOT_TRUE = "Result is not True";
    private static final String TESTUSER = "TestUser";
    private static final String ALL_GOOD = "All good";

    private static final String TEST_SFTP_DIRECTORY = "/directory/";
    private static final String COURT1 = "Court1";


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

    // @Mock
    // private SftpConfigHelper mockSftpConfigHelper;

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
        new SftpService(EasyMock.createMock(EntityManager.class), mockXhbConfigPropRepository,
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
                    .findByCpDocumentName("PublicDisplay_453_20241009130506_1.xml"))
            .andReturn(pddaMessageDao);

        Optional<XhbRefPddaMessageTypeDao> pddaRefMessageTypeDao =
            Optional.of(DummyPdNotifierUtil.getXhbPddaMessageTypeDao());

        EasyMock.expect(mockPddaMessageHelper.findByMessageType("PublicDisplay"))
            .andReturn(pddaRefMessageTypeDao);

        List<XhbCourtDao> courtDaos = new ArrayList<>();
        courtDaos.add(DummyCourtUtil.getXhbCourtDao(-453, COURT1));
        EasyMock.expect(mockXhbCourtRepository.findByCrestCourtIdValue(EasyMock.isA(String.class)))
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
        EasyMock.expect(mockPddaMessageHelper.findByCpDocumentName("PDDA_34_1_2024101409000.xml"))
            .andReturn(pddaMessageDao);

        EasyMock.expect(mockXhbPddaMessageRepository.update(EasyMock.isA(XhbPddaMessageDao.class)))
            .andReturn(Optional.of(DummyPdNotifierUtil.getXhbPddaMessageDao()));

        Optional<XhbRefPddaMessageTypeDao> pddaRefMessageTypeDao =
            Optional.of(DummyPdNotifierUtil.getXhbPddaMessageTypeDao());

        EasyMock.expect(mockPddaMessageHelper.findByMessageType("HearingStatus"))
            .andReturn(pddaRefMessageTypeDao);

        EasyMock
            .expect(mockPddaMessageHelper.savePddaMessage(EasyMock.isA(XhbPddaMessageDao.class)))
            .andReturn(Optional.of(DummyPdNotifierUtil.getXhbPddaMessageDao()));

        List<XhbCourtDao> courtDaos = new ArrayList<>();
        courtDaos.add(DummyCourtUtil.getXhbCourtDao(-453, COURT1));
        EasyMock.expect(mockXhbCourtRepository.findByCrestCourtIdValue(EasyMock.isA(String.class)))
            .andStubReturn(courtDaos);

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
    @SuppressWarnings("PMD")
    void testProcessBaisMessages() {

        List<XhbCourtDao> courtDaos = new ArrayList<>();
        courtDaos.add(DummyCourtUtil.getXhbCourtDao(-453, COURT1));
        EasyMock.expect(mockXhbCourtRepository.findByCrestCourtIdValue(EasyMock.isA(String.class)))
            .andStubReturn(courtDaos);

        EasyMock
            .expect(mockPddaMessageHelper.savePddaMessage(EasyMock.isA(XhbPddaMessageDao.class)))
            .andReturn(Optional.of(DummyPdNotifierUtil.getXhbPddaMessageDao()));

        EasyMock.replay(mockXhbCourtRepository);
        EasyMock.replay(mockPddaMessageHelper);

        boolean result = classUnderTest.processBaisMessages(sftpServer.getPort());

        assertFalse(result, "Expected processBaisMessages to return false");
    }

    @Test
    void testProcessDataFromBais() {

        List<XhbCourtDao> courtDaos = new ArrayList<>();
        courtDaos.add(DummyCourtUtil.getXhbCourtDao(-453, COURT1));
        EasyMock.expect(mockXhbCourtRepository.findByCrestCourtIdValue(EasyMock.isA(String.class)))
            .andStubReturn(courtDaos);

        EasyMock.replay(mockXhbCourtRepository);

        CourtConfigurationChange courtConfigurationChange = new CourtConfigurationChange(1, true);

        ConfigurationChangeEvent publicDisplayEvent =
            new ConfigurationChangeEvent(courtConfigurationChange);
        BaisXhibitValidation bxv = new BaisXhibitValidation(mockXhbCourtRepository);
        BaisCpValidation bcv = new BaisCpValidation(mockXhbCourtRepository);

        // Run multiple tests
        // Test 1 - invalid number of parts in filename
        String cpFilename = "PublicDisplay_453_20241009130506_1.xml";
        String xhibitFilename = "PDDA_34_2024101409000.xml";

        String result = bxv.validateFilename(xhibitFilename, publicDisplayEvent);
        assertTrue(result.length() > 0, ALL_GOOD); // There is an error
        result = bcv.validateFilename(cpFilename, publicDisplayEvent);
        assertTrue(result.length() > 0, ALL_GOOD); // There is an error


        // Test 2 - valid number of parts but invalid filename
        cpFilename = "NotWorkingFilenamePublicDisplay_453_20241009130506.xml";
        xhibitFilename = "NotWorkingFilenamePDDA_34_1_2024101409000.xml";
        result = bxv.validateFilename(xhibitFilename, publicDisplayEvent);
        assertTrue(result.length() > 0, ALL_GOOD); // There is an error
        result = bcv.validateFilename(cpFilename, publicDisplayEvent);
        assertTrue(result.length() > 0, ALL_GOOD); // There is an error


        // Test 3 - valid number of parts and valid filename and valid event
        cpFilename = "PublicDisplay_453_20241009130506.xml";
        xhibitFilename = "PDDA_34_1_2024101409000.xml";

        result = bxv.validateFilename(xhibitFilename, publicDisplayEvent);
        assertNull(result, ALL_GOOD);
        result = bcv.validateFilename(cpFilename, publicDisplayEvent);
        assertNull(result, ALL_GOOD);


        // Test 4 - Cp only - check the file extension
        cpFilename = "PublicDisplay_453_20241009130506.txt";

        result = bcv.validateFilename(cpFilename, publicDisplayEvent);
        assertTrue(result.length() > 0, ALL_GOOD); // There is an error


        // Test 5 - Event is an error
        cpFilename = "PublicDisplay_453_20241009130506.xml";
        xhibitFilename = "PDDA_34_1_2024101409000.xml";
        HearingStatusEvent hearingStatusEvent = new HearingStatusEvent(null, null);
        result = bxv.validateFilename(xhibitFilename, hearingStatusEvent);
        assertNotNull(result, ALL_GOOD);
        result = bcv.validateFilename(cpFilename, hearingStatusEvent);
        assertNull(result, ALL_GOOD);
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

            sftpServer.putFile("/directory/PDDA_34_1_2024101409000.xml", encoded,
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
