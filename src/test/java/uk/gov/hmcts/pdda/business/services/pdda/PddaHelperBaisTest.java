package uk.gov.hmcts.pdda.business.services.pdda;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import jakarta.persistence.EntityManager;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import uk.gov.hmcts.DummyCourtUtil;
import uk.gov.hmcts.DummyFileUtil;
import uk.gov.hmcts.DummyFileUtil.FileResults;
import uk.gov.hmcts.DummyFormattingUtil;
import uk.gov.hmcts.DummyPdNotifierUtil;
import uk.gov.hmcts.DummyServicesUtil;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobDao;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropDao;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbpddamessage.XhbPddaMessageDao;
import uk.gov.hmcts.pdda.business.entities.xhbrefpddamessagetype.XhbRefPddaMessageTypeDao;
import uk.gov.hmcts.pdda.business.services.pdda.sftp.SftpConfig;
import uk.gov.hmcts.pdda.business.services.pdda.sftp.SftpConfigHelper;
import uk.gov.hmcts.pdda.business.services.pdda.sftp.SftpService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * <p>
 * Title: PddaHelperBaisTest.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2023
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @author Mark Harris
 */
@ExtendWith(EasyMockExtension.class)
@SuppressWarnings("PMD.ExcessiveImports")
class PddaHelperBaisTest {

    private static final Logger LOG = LoggerFactory.getLogger(PddaHelperBaisTest.class);

    private static final String TRUE = "Result is not True";
    private static final String SAME = "Result is not Same";
    private static final String[] VALID_CP_MESSAGE_TYPE = {"DailyList", "WarnList"};
    private static final String INVALID_FILENAME = "NotaValidFilename.xml";
    private static final String INVALID_FILENAME_EXT = "Not_a_Valid_Extension.csv";
    private static final String FILE_CONTENTS = " file contents";

    @Mock
    private PddaMessageHelper mockPddaMessageHelper;

    @Mock
    private PddaSftpHelper mockPddaSftpHelper;

    @Mock
    private SftpConfigHelper mockSftpConfigHelper;

    @Mock
    private SftpConfig mockSftpConfig;

    @Mock
    private XhbConfigPropRepository mockXhbConfigPropRepository;

    @Mock
    private XhbCourtRepository mockXhbCourtRepository;

    @Mock
    private XhbClobRepository mockXhbClobRepository;

    @Mock
    private Session mockSession;

    @Mock
    private Environment mockEnvironment;

    @Mock
    private SftpService mockSftpService;

    @TestSubject
    private final PddaHelper classUnderTest = new PddaHelper(EasyMock.createMock(EntityManager.class),
        mockXhbConfigPropRepository, mockEnvironment, mockPddaSftpHelper,
        mockPddaMessageHelper, mockXhbClobRepository, mockXhbCourtRepository);

    private static final class Config {

        public static final String SFTP_HOST = "PDDA_BAIS_SFTP_HOSTNAME";
        public static final String SFTP_PASSWORD = "PDDA_BAIS_SFTP_PASSWORD";
        public static final String SFTP_UPLOAD_LOCATION = "PDDA_BAIS_SFTP_UPLOAD_LOCATION";
        public static final String SFTP_USERNAME = "PDDA_BAIS_SFTP_USERNAME";
        public static final String CP_SFTP_USERNAME = "PDDA_BAIS_CP_SFTP_USERNAME";
        public static final String CP_SFTP_PASSWORD = "PDDA_BAIS_CP_SFTP_PASSWORD";
        public static final String CP_SFTP_UPLOAD_LOCATION = "PDDA_BAIS_CP_SFTP_UPLOAD_LOCATION";
    }

    // @Test
    // void testRetrieveFromBaisCpFailure() {
    // // Setup
    // testGetBaisCpConfigs(Config.SFTP_HOST);
    // EasyMock.replay(mockXhbConfigPropRepository);
    // EasyMock.replay(mockEnvironment);
    //
    // // Run
    // boolean result = false;
    // try {
    // classUnderTest.retrieveFromBaisCp();
    // result = true;
    // } catch (Exception exception) {
    // fail(exception);
    // }
    //
    // // Checks
    // EasyMock.verify(mockXhbConfigPropRepository);
    // EasyMock.verify(mockEnvironment);
    // assertTrue(result, TRUE);
    // }

    @Test
    void testRetrieveFromBaisCpSuccess() {
        // Add Captured Values
        List<Capture<XhbPddaMessageDao>> capturedSaves = new ArrayList<>();
        // Setup
        List<FileResults> dummyFiles = getDummyFileMap(false);
        Map<String, String> dummyMap = new ConcurrentHashMap<>();
        Map<String, String> expectedStatusMap = new ConcurrentHashMap<>();
        for (FileResults dummyFile : dummyFiles) {
            dummyMap.put(dummyFile.filename, dummyFile.fileContents);
            String expectedStatus = (dummyFile.isValid ? CpDocumentStatus.VALID_NOT_PROCESSED
                : CpDocumentStatus.INVALID).status;
            expectedStatusMap.put(dummyFile.filename, expectedStatus);
        }
        testGetBaisCpConfigs(null);
        testGetBaisXhibitConfigs();
        List<XhbCourtDao> courtDaos = new ArrayList<>();
        courtDaos.add(DummyCourtUtil.getXhbCourtDao(-453, "Court1"));
        try {
            EasyMock
                .expect(mockSftpConfigHelper.validateAndSetHostAndPort(
                    EasyMock.isA(SftpConfig.class), EasyMock.isA(String.class)))
                .andReturn(mockSftpConfig);
            EasyMock.expect(mockSftpConfigHelper.getJschSession(EasyMock.isA(SftpConfig.class)))
                .andReturn(mockSftpConfig);
            EasyMock.expect(mockSftpConfig.getErrorMsg()).andReturn(null);
            EasyMock.expect(mockSftpConfig.getSession()).andReturn(mockSession).anyTimes();
            EasyMock.expect(mockSftpConfig.getActiveRemoteFolder()).andReturn("").anyTimes();
            mockSftpConfig.setSession(null);
            EasyMock.expect(mockPddaSftpHelper.createSession(EasyMock.isA(String.class),
                EasyMock.isA(String.class), EasyMock.isA(String.class),
                EasyMock.isA(Integer.class))).andReturn(mockSession);
            EasyMock
                .expect(mockPddaSftpHelper.sftpFetch(EasyMock.isA(Session.class),
                    EasyMock.isA(String.class), EasyMock.isA(PddaHelper.BaisCpValidation.class)))
                .andReturn(dummyMap);
            EasyMock.expectLastCall().times(2);
            mockSession.disconnect();
            EasyMock
                .expect(mockXhbCourtRepository.findByCrestCourtIdValue(EasyMock.isA(String.class)))
                .andReturn(courtDaos);
            EasyMock.expectLastCall().anyTimes();
            for (FileResults entry : dummyFiles) {
                EasyMock.expect(mockPddaMessageHelper.findByCpDocumentName(entry.filename))
                    .andReturn(entry.alreadyProcessedTest
                        ? Optional.of(DummyPdNotifierUtil.getXhbPddaMessageDao())
                        : Optional.empty());
                if (!entry.alreadyProcessedTest) {
                    EasyMock.expect(mockXhbClobRepository.update(EasyMock.isA(XhbClobDao.class)))
                        .andReturn(Optional
                            .of(DummyFormattingUtil.getXhbClobDao(Long.valueOf(1), "clobData")));
                    EasyMock
                        .expect(mockPddaMessageHelper.findByMessageType(EasyMock.isA(String.class)))
                        .andReturn(Optional.empty());
                    EasyMock
                        .expect(mockPddaMessageHelper
                            .savePddaMessageType(EasyMock.isA(XhbRefPddaMessageTypeDao.class)))
                        .andReturn(Optional.of(DummyPdNotifierUtil.getXhbRefPddaMessageTypeDao()));
                    Capture<XhbPddaMessageDao> capturedSave = EasyMock.newCapture();
                    capturedSaves.add(capturedSave);
                    EasyMock
                        .expect(mockPddaMessageHelper.savePddaMessage(EasyMock.and(
                            EasyMock.capture(capturedSave), EasyMock.isA(XhbPddaMessageDao.class))))
                        .andReturn(Optional.of(DummyPdNotifierUtil.getXhbPddaMessageDao()));
                    mockPddaSftpHelper.sftpDeleteFile(EasyMock.isA(Session.class),
                        EasyMock.isA(String.class), EasyMock.isA(String.class));
                }
            }
        } catch (JSchException | SftpException e) {
            fail("Failed in pddaSFTPHelper.sftpFetch");
        }
        EasyMock.replay(mockSftpConfigHelper);
        EasyMock.replay(mockSftpConfig);
        EasyMock.replay(mockXhbConfigPropRepository);
        EasyMock.replay(mockPddaSftpHelper);
        EasyMock.replay(mockSession);
        EasyMock.replay(mockPddaMessageHelper);
        EasyMock.replay(mockXhbCourtRepository);
        EasyMock.replay(mockXhbClobRepository);
        EasyMock.replay(mockEnvironment);
        // Run
        classUnderTest.retrieveFromBaisCp();

        // Checks
        EasyMock.verify(mockSftpConfigHelper);
        EasyMock.verify(mockSftpConfig);
        EasyMock.verify(mockXhbConfigPropRepository);
        // EasyMock.verify(mockPddaSftpHelper);
        EasyMock.verify(mockSession);
        EasyMock.verify(mockPddaMessageHelper);
        EasyMock.verify(mockXhbCourtRepository);
        EasyMock.verify(mockXhbClobRepository);
        EasyMock.verify(mockEnvironment);
        validateSavedValues(capturedSaves, expectedStatusMap);
    }


    @Test
    void testRetrieveFromBaisXhibitSuccess() {
        // Add Captured Values
        List<Capture<XhbPddaMessageDao>> capturedSaves = new ArrayList<>();
        // Setup
        List<FileResults> dummyFiles = getDummyFileMap(true);
        Map<String, String> dummyMap = new ConcurrentHashMap<>();
        Map<String, String> expectedStatusMap = new ConcurrentHashMap<>();
        for (FileResults dummyFile : dummyFiles) {
            dummyMap.put(dummyFile.filename, dummyFile.fileContents);
            String expectedStatus = (dummyFile.isValid ? CpDocumentStatus.VALID_NOT_PROCESSED
                : CpDocumentStatus.INVALID).status;
            expectedStatusMap.put(dummyFile.filename, expectedStatus);
        }
        testGetBaisCpConfigs(null);
        testGetBaisXhibitConfigs();
        try {
            EasyMock
                .expect(mockSftpConfigHelper.validateAndSetHostAndPort(
                    EasyMock.isA(SftpConfig.class), EasyMock.isA(String.class)))
                .andReturn(mockSftpConfig);
            EasyMock.expect(mockSftpConfigHelper.getJschSession(EasyMock.isA(SftpConfig.class)))
                .andReturn(mockSftpConfig);
            EasyMock.expect(mockSftpConfig.getErrorMsg()).andReturn(null);
            EasyMock.expect(mockSftpConfig.getSession()).andReturn(mockSession).anyTimes();
            EasyMock.expect(mockSftpConfig.getActiveRemoteFolder()).andReturn("").anyTimes();
            mockSftpConfig.setSession(null);
            EasyMock.expect(mockPddaSftpHelper.createSession(EasyMock.isA(String.class),
                EasyMock.isA(String.class), EasyMock.isA(String.class),
                EasyMock.isA(Integer.class))).andReturn(mockSession);
            EasyMock.expect(mockPddaSftpHelper.sftpFetch(EasyMock.isA(Session.class),
                EasyMock.isA(String.class), EasyMock.isA(PddaHelper.BaisXhibitValidation.class)))
                .andReturn(dummyMap);
            EasyMock.expectLastCall().times(2);
            mockSession.disconnect();
            for (FileResults entry : dummyFiles) {
                LOG.debug("Test:{}", entry);
                EasyMock.expect(mockXhbClobRepository.update(EasyMock.isA(XhbClobDao.class)))
                    .andReturn(Optional
                        .of(DummyFormattingUtil.getXhbClobDao(Long.valueOf(1), "clobData")));
                EasyMock.expect(mockPddaMessageHelper.findByMessageType(EasyMock.isA(String.class)))
                    .andReturn(Optional.empty());
                EasyMock
                    .expect(mockPddaMessageHelper
                        .savePddaMessageType(EasyMock.isA(XhbRefPddaMessageTypeDao.class)))
                    .andReturn(Optional.of(DummyPdNotifierUtil.getXhbRefPddaMessageTypeDao()));
                Capture<XhbPddaMessageDao> capturedSave = EasyMock.newCapture();
                capturedSaves.add(capturedSave);
                EasyMock
                    .expect(mockPddaMessageHelper.savePddaMessage(EasyMock.and(
                        EasyMock.capture(capturedSave), EasyMock.isA(XhbPddaMessageDao.class))))
                    .andReturn(Optional.of(DummyPdNotifierUtil.getXhbPddaMessageDao()));
                mockPddaSftpHelper.sftpDeleteFile(EasyMock.isA(Session.class),
                    EasyMock.isA(String.class), EasyMock.isA(String.class));
            }
        } catch (JSchException | SftpException e) {
            fail("Failed in pddaSFTPHelper.sftpFetch");
        }
        EasyMock.replay(mockSftpConfigHelper);
        EasyMock.replay(mockSftpConfig);
        EasyMock.replay(mockXhbConfigPropRepository);
        EasyMock.replay(mockPddaSftpHelper);
        EasyMock.replay(mockSession);
        EasyMock.replay(mockPddaMessageHelper);
        EasyMock.replay(mockXhbCourtRepository);
        EasyMock.replay(mockXhbClobRepository);
        EasyMock.replay(mockEnvironment);

        // Run
        boolean result = false;
        try {
            classUnderTest.retrieveFromBaisXhibit();
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }

        // Checks
        EasyMock.verify(mockSftpConfigHelper);
        EasyMock.verify(mockSftpConfig);
        EasyMock.verify(mockXhbConfigPropRepository);
        //EasyMock.verify(mockPddaSftpHelper);
        EasyMock.verify(mockSession);
        EasyMock.verify(mockPddaMessageHelper);
        EasyMock.verify(mockXhbCourtRepository);
        EasyMock.verify(mockXhbClobRepository);
        EasyMock.verify(mockEnvironment);
        assertTrue(result, TRUE);
        validateSavedValues(capturedSaves, expectedStatusMap);
    }

    // @Test
    // void testRetrieveFromBaisXhibitFailure() {
    // // Setup
    // testGetBaisConfigs(Config.SFTP_HOST);
    // EasyMock.replay(mockXhbConfigPropRepository);
    // EasyMock.replay(mockEnvironment);
    // // Run
    // boolean result = false;
    // try {
    // classUnderTest.retrieveFromBaisXhibit();
    // result = true;
    // } catch (Exception exception) {
    // fail(exception);
    // }
    //
    // // Checks
    // EasyMock.verify(mockXhbConfigPropRepository);
    // EasyMock.verify(mockEnvironment);
    // assertTrue(result, TRUE);
    // }

    private void testGetBaisCpConfigs(String failOn) {
        // Username
        String propertyName = Config.CP_SFTP_USERNAME;
        EasyMock.expect(mockEnvironment.getProperty(propertyName))
            .andReturn(propertyName.toLowerCase(Locale.getDefault())).anyTimes();
        // Password
        propertyName = Config.CP_SFTP_PASSWORD;
        EasyMock.expect(mockEnvironment.getProperty(propertyName))
            .andReturn(propertyName.toLowerCase(Locale.getDefault())).anyTimes();
        // Location
        propertyName = Config.CP_SFTP_UPLOAD_LOCATION;
        List<XhbConfigPropDao> locationList = getXhbConfigPropDaoList(propertyName);
        EasyMock.expect(mockXhbConfigPropRepository.findByPropertyName(propertyName))
            .andReturn(locationList).anyTimes();
        // Host
        propertyName = Config.SFTP_HOST;
        String hostAndPort = propertyName.toLowerCase(Locale.getDefault());
        if (failOn == null || !propertyName.equals(failOn)) {
            hostAndPort = hostAndPort + ":22";
        }
        EasyMock.expect(mockEnvironment.getProperty(propertyName)).andReturn(hostAndPort)
            .anyTimes();
    }


    private void testGetBaisXhibitConfigs() {
        // Username
        String propertyName = Config.SFTP_USERNAME;
        EasyMock.expect(mockEnvironment.getProperty(propertyName))
            .andReturn(propertyName.toLowerCase(Locale.getDefault())).anyTimes();
        // Password
        propertyName = Config.SFTP_PASSWORD;
        EasyMock.expect(mockEnvironment.getProperty(propertyName))
            .andReturn(propertyName.toLowerCase(Locale.getDefault())).anyTimes();
        // Location
        propertyName = Config.SFTP_UPLOAD_LOCATION;
        List<XhbConfigPropDao> locationList = getXhbConfigPropDaoList(propertyName);
        EasyMock.expect(mockXhbConfigPropRepository.findByPropertyName(propertyName))
            .andReturn(locationList).anyTimes();
    }


    private void validateSavedValues(List<Capture<XhbPddaMessageDao>> capturedSaves,
        Map<String, String> expectedStatusMap) {
        for (Capture<XhbPddaMessageDao> capturedSave : capturedSaves) {
            XhbPddaMessageDao savedValue = capturedSave.getValue();
            String filename = savedValue.getCpDocumentName();
            String expectedStatus = expectedStatusMap.get(filename);
            String actualStatus = savedValue.getCpDocumentStatus();
            assertSame(expectedStatus, actualStatus, SAME);
        }
    }

    private List<FileResults> getDummyFileMap(boolean isXhibit) {
        List<FileResults> result = new ArrayList<>();

        // All all valid CP files
        for (FileResults fileResult : DummyFileUtil.getAllValidCpFiles(!isXhibit)) {
            result.add(fileResult);
        }
        // All all valid Xhibit files
        for (FileResults fileResult : DummyFileUtil.getAllValidXhibitFiles(isXhibit)) {
            result.add(fileResult);
        }

        FileResults fileResult;

        // Invalid filename parts
        fileResult = new FileResults();
        fileResult.filename = INVALID_FILENAME;
        fileResult.fileContents = INVALID_FILENAME;
        result.add(fileResult);

        // Invalid Extension
        fileResult = new FileResults();
        fileResult.filename = INVALID_FILENAME_EXT;
        fileResult.fileContents = INVALID_FILENAME_EXT;
        result.add(fileResult);

        // CP validation only
        if (!isXhibit) {
            String messageType;
            // Invalid Date format
            fileResult = new FileResults();
            messageType = VALID_CP_MESSAGE_TYPE[0];
            fileResult.filename = messageType + "_453_202299999999.xml";
            fileResult.fileContents = messageType + FILE_CONTENTS;
            result.add(fileResult);

            // Invalid Date Year
            fileResult = new FileResults();
            fileResult.filename = messageType + "_453_17220825090400.xml";
            fileResult.fileContents = messageType + FILE_CONTENTS;
            result.add(fileResult);

            // Invalid title
            fileResult = new FileResults();
            messageType = "WeeklyList";
            fileResult.filename = messageType + "_453_20220825090400.xml";
            fileResult.fileContents = messageType + FILE_CONTENTS;
            result.add(fileResult);
        }
        return result;
    }


    private List<XhbConfigPropDao> getXhbConfigPropDaoList(String propertyName) {
        List<XhbConfigPropDao> result = new ArrayList<>();
        result.add(DummyServicesUtil.getXhbConfigPropDao(propertyName,
            propertyName.toLowerCase(Locale.getDefault())));
        return result;
    }
}
