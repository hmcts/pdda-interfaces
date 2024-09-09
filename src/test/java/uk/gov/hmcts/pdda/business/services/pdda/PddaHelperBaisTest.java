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
    private EntityManager mockEntityManager;

    @Mock
    private PddaMessageHelper mockPddaMessageHelper;

    @Mock
    private PddaSftpHelper mockPddaSftpHelper;

    @Mock
    private XhbConfigPropRepository mockXhbConfigPropRepository;

    @Mock
    private XhbCourtRepository mockXhbCourtRepository;

    @Mock
    private XhbClobRepository mockXhbClobRepository;

    @Mock
    private Session mockSession;

    @TestSubject
    private final PddaHelper classUnderTest = new PddaHelper(mockEntityManager);
 
    private static class Config {
        static final String SFTP_HOST = "PDDA_BAIS_SFTP_HOSTNAME";
        static final String SFTP_PASSWORD = "PDDA_BAIS_SFTP_PASSWORD";
        static final String SFTP_UPLOAD_LOCATION = "PDDA_BAIS_SFTP_UPLOAD_LOCATION";
        static final String SFTP_USERNAME = "PDDA_BAIS_SFTP_USERNAME";
        static final String CP_SFTP_USERNAME = "PDDA_BAIS_CP_SFTP_USERNAME";
        static final String CP_SFTP_PASSWORD = "PDDA_BAIS_CP_SFTP_PASSWORD";
        static final String CP_SFTP_UPLOAD_LOCATION = "PDDA_BAIS_CP_SFTP_UPLOAD_LOCATION";
    }
    
    @Test
    void testRetrieveFromBaisCpFailure() {
        // Setup
        testGetBaisCpConfigs(Config.SFTP_HOST);
        EasyMock.replay(mockXhbConfigPropRepository);
        // Run
        boolean result = false;
        try {
            classUnderTest.retrieveFromBaisCp();
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }

        // Checks
        EasyMock.verify(mockXhbConfigPropRepository);
        assertTrue(result, TRUE);
    }
    
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
            String expectedStatus =
                (dummyFile.isValid ? CpDocumentStatus.VALID_NOT_PROCESSED : CpDocumentStatus.INVALID).status;
            expectedStatusMap.put(dummyFile.filename, expectedStatus);
        }
        testGetBaisCpConfigs(null);
        List<XhbCourtDao> courtDaos = new ArrayList<>();
        courtDaos.add(DummyCourtUtil.getXhbCourtDao(-453, "Court1"));
        try {
            EasyMock.expect(mockPddaSftpHelper.createSession(EasyMock.isA(String.class), EasyMock.isA(String.class),
                EasyMock.isA(String.class), EasyMock.isA(Integer.class))).andReturn(mockSession);
            EasyMock.expect(mockPddaSftpHelper.sftpFetch(EasyMock.isA(Session.class), EasyMock.isA(String.class),
                EasyMock.isA(PddaHelper.BaisCpValidation.class))).andReturn(dummyMap);
            EasyMock.expectLastCall().times(2);
            mockSession.disconnect();
            EasyMock.expect(mockXhbCourtRepository.findByCrestCourtIdValue(EasyMock.isA(String.class)))
                .andReturn(courtDaos);
            EasyMock.expectLastCall().anyTimes();
            for (FileResults entry : dummyFiles) {
                EasyMock.expect(mockPddaMessageHelper.findByCpDocumentName(entry.filename))
                    .andReturn(entry.alreadyProcessedTest ? Optional.of(DummyPdNotifierUtil.getXhbPddaMessageDao())
                        : Optional.empty());
                if (!entry.alreadyProcessedTest) {
                    EasyMock.expect(mockXhbClobRepository.update(EasyMock.isA(XhbClobDao.class)))
                        .andReturn(Optional.of(DummyFormattingUtil.getXhbClobDao(Long.valueOf(1), "clobData")));
                    EasyMock.expect(mockPddaMessageHelper.findByMessageType(EasyMock.isA(String.class)))
                        .andReturn(Optional.empty());
                    EasyMock
                        .expect(mockPddaMessageHelper.savePddaMessageType(EasyMock.isA(XhbRefPddaMessageTypeDao.class)))
                        .andReturn(Optional.of(DummyPdNotifierUtil.getXhbRefPddaMessageTypeDao()));
                    Capture<XhbPddaMessageDao> capturedSave = EasyMock.newCapture();
                    capturedSaves.add(capturedSave);
                    EasyMock
                        .expect(mockPddaMessageHelper.savePddaMessage(
                            EasyMock.and(EasyMock.capture(capturedSave), EasyMock.isA(XhbPddaMessageDao.class))))
                        .andReturn(Optional.of(DummyPdNotifierUtil.getXhbPddaMessageDao()));
                    mockPddaSftpHelper.sftpDeleteFile(EasyMock.isA(Session.class), EasyMock.isA(String.class),
                        EasyMock.isA(String.class));
                }
            }
        } catch (JSchException | SftpException e) {
            fail("Failed in pddaSFTPHelper.sftpFetch");
        }
        EasyMock.replay(mockXhbConfigPropRepository);
        EasyMock.replay(mockPddaSftpHelper);
        EasyMock.replay(mockSession);
        EasyMock.replay(mockPddaMessageHelper);
        EasyMock.replay(mockXhbCourtRepository);
        EasyMock.replay(mockXhbClobRepository);
        // Run
        classUnderTest.retrieveFromBaisCp();

        // Checks
        EasyMock.verify(mockXhbConfigPropRepository);
        EasyMock.verify(mockPddaSftpHelper);
        EasyMock.verify(mockSession);
        EasyMock.verify(mockPddaMessageHelper);
        EasyMock.verify(mockXhbCourtRepository);
        EasyMock.verify(mockXhbClobRepository);
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
            String expectedStatus =
                (dummyFile.isValid ? CpDocumentStatus.VALID_NOT_PROCESSED : CpDocumentStatus.INVALID).status;
            expectedStatusMap.put(dummyFile.filename, expectedStatus);
        }
        testGetBaisConfigs(null);
        try {
            EasyMock.expect(mockPddaSftpHelper.createSession(EasyMock.isA(String.class), EasyMock.isA(String.class),
                EasyMock.isA(String.class), EasyMock.isA(Integer.class))).andReturn(mockSession);
            EasyMock.expect(mockPddaSftpHelper.sftpFetch(EasyMock.isA(Session.class), EasyMock.isA(String.class),
                EasyMock.isA(PddaHelper.BaisXhibitValidation.class))).andReturn(dummyMap);
            EasyMock.expectLastCall().times(2);
            mockSession.disconnect();
            for (FileResults entry : dummyFiles) {
                LOG.debug("Test:{}",entry);
                EasyMock.expect(mockXhbClobRepository.update(EasyMock.isA(XhbClobDao.class)))
                    .andReturn(Optional.of(DummyFormattingUtil.getXhbClobDao(Long.valueOf(1), "clobData")));
                EasyMock.expect(mockPddaMessageHelper.findByMessageType(EasyMock.isA(String.class)))
                    .andReturn(Optional.empty());
                EasyMock.expect(mockPddaMessageHelper.savePddaMessageType(EasyMock.isA(XhbRefPddaMessageTypeDao.class)))
                    .andReturn(Optional.of(DummyPdNotifierUtil.getXhbRefPddaMessageTypeDao()));
                Capture<XhbPddaMessageDao> capturedSave = EasyMock.newCapture();
                capturedSaves.add(capturedSave);
                EasyMock
                    .expect(mockPddaMessageHelper.savePddaMessage(
                        EasyMock.and(EasyMock.capture(capturedSave), EasyMock.isA(XhbPddaMessageDao.class))))
                    .andReturn(Optional.of(DummyPdNotifierUtil.getXhbPddaMessageDao()));
                mockPddaSftpHelper.sftpDeleteFile(EasyMock.isA(Session.class), EasyMock.isA(String.class),
                    EasyMock.isA(String.class));
            }
        } catch (JSchException | SftpException e) {
            fail("Failed in pddaSFTPHelper.sftpFetch");
        }
        EasyMock.replay(mockXhbConfigPropRepository);
        EasyMock.replay(mockPddaSftpHelper);
        EasyMock.replay(mockSession);
        EasyMock.replay(mockPddaMessageHelper);
        EasyMock.replay(mockXhbCourtRepository);
        EasyMock.replay(mockXhbClobRepository);

        // Run
        boolean result = false;
        try {
            classUnderTest.retrieveFromBaisXhibit();
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }

        // Checks
        EasyMock.verify(mockXhbConfigPropRepository);
        EasyMock.verify(mockPddaSftpHelper);
        EasyMock.verify(mockSession);
        EasyMock.verify(mockPddaMessageHelper);
        EasyMock.verify(mockXhbCourtRepository);
        EasyMock.verify(mockXhbClobRepository);
        assertTrue(result, TRUE);
        validateSavedValues(capturedSaves, expectedStatusMap);
    }

    @Test
    void testRetrieveFromBaisXhibitFailure() {
        // Setup
        testGetBaisConfigs(Config.SFTP_HOST);
        EasyMock.replay(mockXhbConfigPropRepository);
        // Run
        boolean result = false;
        try {
            classUnderTest.retrieveFromBaisXhibit();
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }

        // Checks
        EasyMock.verify(mockXhbConfigPropRepository);
        assertTrue(result, TRUE);
    }
    
    private void testGetBaisCpConfigs(String failOn) {
        // Username
        String propertyName = Config.CP_SFTP_USERNAME;
        List<XhbConfigPropDao> userNameList = getXhbConfigPropDaoList(propertyName);
        EasyMock.expect(mockXhbConfigPropRepository.findByPropertyName(propertyName)).andReturn(userNameList);
        // Password
        propertyName = Config.CP_SFTP_PASSWORD;
        List<XhbConfigPropDao> passwordList = getXhbConfigPropDaoList(propertyName);
        EasyMock.expect(mockXhbConfigPropRepository.findByPropertyName(propertyName)).andReturn(passwordList);
        // Location
        propertyName = Config.CP_SFTP_UPLOAD_LOCATION;
        List<XhbConfigPropDao> locationList = getXhbConfigPropDaoList(propertyName);
        EasyMock.expect(mockXhbConfigPropRepository.findByPropertyName(propertyName)).andReturn(locationList);
        // Host
        propertyName = Config.SFTP_HOST;
        List<XhbConfigPropDao> hostList = getXhbConfigPropDaoList(propertyName);
        if (failOn == null || !propertyName.equals(failOn)) {
            hostList.get(0).setPropertyValue(hostList.get(0).getPropertyValue() + ":22");
        }
        EasyMock.expect(mockXhbConfigPropRepository.findByPropertyName(propertyName)).andReturn(hostList);
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
    
    private void testGetBaisConfigs(String failOn) {
        // Username
        String propertyName = Config.SFTP_USERNAME;
        List<XhbConfigPropDao> userNameList = getXhbConfigPropDaoList(propertyName);
        EasyMock.expect(mockXhbConfigPropRepository.findByPropertyName(propertyName)).andReturn(userNameList);
        // Password
        propertyName = Config.SFTP_PASSWORD;
        List<XhbConfigPropDao> passwordList = getXhbConfigPropDaoList(propertyName);
        EasyMock.expect(mockXhbConfigPropRepository.findByPropertyName(propertyName)).andReturn(passwordList);
        // Location
        propertyName = Config.SFTP_UPLOAD_LOCATION;
        List<XhbConfigPropDao> locationList = getXhbConfigPropDaoList(propertyName);
        EasyMock.expect(mockXhbConfigPropRepository.findByPropertyName(propertyName)).andReturn(locationList);
        // Host
        propertyName = Config.SFTP_HOST;
        List<XhbConfigPropDao> hostList = getXhbConfigPropDaoList(propertyName);
        if (failOn == null || !propertyName.equals(failOn)) {
            hostList.get(0).setPropertyValue(hostList.get(0).getPropertyValue() + ":22");
        }
        EasyMock.expect(mockXhbConfigPropRepository.findByPropertyName(propertyName)).andReturn(hostList);
    }
    
    
    private List<XhbConfigPropDao> getXhbConfigPropDaoList(String propertyName) {
        List<XhbConfigPropDao> result = new ArrayList<>();
        result.add(DummyServicesUtil.getXhbConfigPropDao(propertyName, propertyName.toLowerCase(Locale.getDefault())));
        return result;
    }
}
