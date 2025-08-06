package uk.gov.hmcts.pdda.business.services.pdda;

import ch.qos.logback.classic.Level;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.DummyFileUtil;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**

 * Title: Pdda SFTP Helper Test.


 * Description:


 * Copyright: Copyright (c) 2022


 * Company: CGI

 * @author Mark Harris
 */
@ExtendWith(EasyMockExtension.class)
@SuppressWarnings({"PMD"})
class PddaSftpHelperTest {

    private static final String NOTNULL = "Result is Null";
    private static final String TRUE = "Result is not True";
    private static final String SFTP = "sftp";
    private static final String LINUX_REMOTE_FOLDER = "remotefolder/";
    private static final String WINDOWS_REMOTE_FOLDER = "remotefolder\\";
    private static final String INVALID_REMOTE_FOLDER = "remotefolder";
    private static final String THREW_EXCEPTION = "callUnderTest threw an exception";
    private static final String MOCKS_THREW_EXCEPTION = "mocks threw an exception";


    @Mock
    private Session mockSession;

    @Mock
    private ChannelSftp mockChannelSftp;

    @Mock
    private JSch mockJSch;

    @TestSubject
    private final PddaSftpHelper classUnderTest = new PddaSftpHelper();

    @BeforeAll
    public static void setUp() {
        // Do nothing
    }

    @AfterAll
    public static void tearDown() {
        // Do nothing
    }

    @Test
    void testSftpFilesSuccess() {
        // Setup
        String remoteFolder = LINUX_REMOTE_FOLDER;
        Map<String, InputStream> files = DummyFileUtil.getFiles(DummyFileUtil.getFilenames());
        try {
            EasyMock.expect(mockSession.openChannel(SFTP)).andReturn(mockChannelSftp);
            mockChannelSftp.connect();
            EasyMock.expect(mockChannelSftp.stat(remoteFolder)).andReturn(null);
            mockChannelSftp.put(EasyMock.isA(InputStream.class), EasyMock.isA(String.class));
            EasyMock.expectLastCall().times(files.size());
            mockChannelSftp.exit();
        } catch (JSchException | SftpException ex) {
            fail(MOCKS_THREW_EXCEPTION);
        }
        EasyMock.replay(mockSession);
        EasyMock.replay(mockChannelSftp);
        // Run
        try {
            classUnderTest.sftpFiles(mockSession, remoteFolder, files);
        } catch (JSchException | SftpException ex) {
            fail(THREW_EXCEPTION);
        }
        // Checks
        EasyMock.verify(mockSession);
        EasyMock.verify(mockChannelSftp);
    }

    @Test
    void testSftpFilesFailure() {
        // Setup
        String remoteFolder = INVALID_REMOTE_FOLDER;
        Map<String, InputStream> files = DummyFileUtil.getFiles(DummyFileUtil.getFilenames());
        try {
            EasyMock.expect(mockSession.openChannel(SFTP)).andReturn(mockChannelSftp);
            mockChannelSftp.connect();
            EasyMock.expect(mockChannelSftp.stat(remoteFolder)).andReturn(null);
        } catch (JSchException | SftpException ex) {
            fail(MOCKS_THREW_EXCEPTION);
        }
        EasyMock.replay(mockSession);
        EasyMock.replay(mockChannelSftp);
        // Run
        boolean result = false;
        try {
            classUnderTest.sftpFiles(mockSession, remoteFolder, files);
        } catch (SftpException ex) {
            fail("callUnderTest threw an invalid exception");
        } catch (JSchException ex) {
            // This is the exception we are expecting with a noslash remote folder
            result = true;
        }
        // Checks
        EasyMock.verify(mockSession);
        EasyMock.verify(mockChannelSftp);
        assertTrue(result, TRUE);
    }

    @Test
    void testCreateSession() {
        // Setup
        String username = "username";
        String password = "password";
        String host = "host";
        int port = 2;
        try {
            EasyMock.expect(mockJSch.getSession(username, host, port)).andReturn(mockSession);
            mockSession.setConfig("StrictHostKeyChecking", "no");
            mockSession.setPassword(password);
            mockSession.connect();
        } catch (JSchException e) {
            fail(MOCKS_THREW_EXCEPTION);
        }
        EasyMock.replay(mockJSch);
        EasyMock.replay(mockSession);
        // Run
        Session actualResult = null;
        try {
            actualResult = classUnderTest.createSession(username, password, host, port);
        } catch (JSchException e) {
            fail(THREW_EXCEPTION);
        }
        // Checks
        EasyMock.verify(mockJSch);
        EasyMock.verify(mockSession);
        assertNotNull(actualResult, NOTNULL);
    }

    @Test
    void testDisconnectSession() {
        // Setup
        mockSession.disconnect();
        EasyMock.replay(mockSession);
        // Run
        boolean result = false;
        try {
            PddaSftpUtil.disconnectSession(mockSession);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        // Checks
        EasyMock.verify(mockSession);
        assertTrue(result, TRUE);
    }

    @Test
    void testSftpDeleteFileSuccess() {
        // Setup
        String remoteFolder = WINDOWS_REMOTE_FOLDER;
        String filename = "filename.xml";
        try {
            EasyMock.expect(mockSession.openChannel(SFTP)).andReturn(mockChannelSftp);
            mockChannelSftp.connect();
            EasyMock.expect(mockChannelSftp.stat(remoteFolder)).andReturn(null);
            mockChannelSftp.rm(EasyMock.isA(String.class));
            mockChannelSftp.exit();
        } catch (JSchException | SftpException ex) {
            fail(MOCKS_THREW_EXCEPTION);
        }
        EasyMock.replay(mockSession);
        EasyMock.replay(mockChannelSftp);
        // Run
        boolean result = false;
        try {
            classUnderTest.sftpDeleteFile(mockSession, remoteFolder, filename);
            result = true;
        } catch (JSchException | SftpException ex) {
            fail(THREW_EXCEPTION);
        }
        // Checks
        EasyMock.verify(mockSession);
        EasyMock.verify(mockChannelSftp);
        assertTrue(result, TRUE);
    }

    @Test
    void testSftpDeleteFileFailure() {
        // Setup
        String remoteFolder = INVALID_REMOTE_FOLDER;
        String filename = "filename.xml";
        try {
            EasyMock.expect(mockSession.openChannel(SFTP)).andReturn(mockChannelSftp);
            mockChannelSftp.connect();
            EasyMock.expect(mockChannelSftp.stat(remoteFolder)).andReturn(null);
        } catch (JSchException | SftpException ex) {
            fail(MOCKS_THREW_EXCEPTION);
        }
        EasyMock.replay(mockSession);
        EasyMock.replay(mockChannelSftp);
        // Run
        boolean result = false;
        try {
            classUnderTest.sftpDeleteFile(mockSession, remoteFolder, filename);
        } catch (SftpException ex) {
            fail("callUnderTest threw an invalid exception");
        } catch (JSchException ex) {
            // This is the exception we are expecting with a noslash remote folder
            result = true;
        }
        // Checks
        EasyMock.verify(mockSession);
        EasyMock.verify(mockChannelSftp);
        assertTrue(result, TRUE);
    }

    @Test
    void testSftpFetchSuccess() {
        // Setup
        Logger root = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        ((ch.qos.logback.classic.Logger) root).setLevel(Level.DEBUG);
        String remoteFolder = LINUX_REMOTE_FOLDER;
        List<String> filenamesList = DummyFileUtil.getFilenames();
        Map<String, InputStream> files = DummyFileUtil.getFiles(filenamesList);
        List<String> filenames = new Vector<>(filenamesList);
        try {
            EasyMock.expect(mockSession.openChannel(SFTP)).andReturn(mockChannelSftp);
            mockChannelSftp.connect();
            EasyMock.expect(mockChannelSftp.stat(remoteFolder)).andReturn(null);
            EasyMock.expect(mockChannelSftp.ls(EasyMock.isA(String.class))).andReturn((Vector) filenames);
            for (Map.Entry<String, InputStream> entry : files.entrySet()) {
                EasyMock.expect(mockChannelSftp.get(EasyMock.isA(String.class))).andReturn(entry.getValue());
            }
            mockChannelSftp.exit();
        } catch (JSchException | SftpException ex) {
            fail(MOCKS_THREW_EXCEPTION);
        }
        EasyMock.replay(mockSession);
        EasyMock.replay(mockChannelSftp);
        // Run
        boolean result = false;
        try {
            classUnderTest.sftpFetch(mockSession, remoteFolder, new LocalValidation(false));
            result = true;
        } catch (JSchException | SftpException ex) {
            fail(THREW_EXCEPTION);
        }
        // Checks
        EasyMock.verify(mockSession);
        EasyMock.verify(mockChannelSftp);
        assertTrue(result, TRUE);
    }

    @Test
    void testSftpFetchFailure() {
        // Setup
        String remoteFolder = INVALID_REMOTE_FOLDER;
        try {
            EasyMock.expect(mockSession.openChannel(SFTP)).andReturn(mockChannelSftp);
            mockChannelSftp.connect();
            EasyMock.expect(mockChannelSftp.stat(remoteFolder)).andReturn(null);
        } catch (JSchException | SftpException ex) {
            fail(MOCKS_THREW_EXCEPTION);
        }
        EasyMock.replay(mockSession);
        EasyMock.replay(mockChannelSftp);
        // Run
        boolean result = false;
        try {
            classUnderTest.sftpFetch(mockSession, remoteFolder, new LocalValidation(false));
        } catch (SftpException ex) {
            fail("callUnderTest threw an invalid exception");
        } catch (JSchException ex) {
            // This is the exception we are expecting with a noslash remote folder
            result = true;
        }
        // Checks
        EasyMock.verify(mockSession);
        EasyMock.verify(mockChannelSftp);
        assertTrue(result, TRUE);
    }

    class LocalValidation extends SftpValidation {
        public LocalValidation(boolean includeDirs) {
            super(includeDirs);
        }

        @Override
        public String getFilename(Object obj) {
            return (String) obj;
        }
    }
}
