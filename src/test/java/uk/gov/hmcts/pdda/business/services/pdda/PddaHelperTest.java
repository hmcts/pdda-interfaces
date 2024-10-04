package uk.gov.hmcts.pdda.business.services.pdda;

import com.jcraft.jsch.Session;
import jakarta.persistence.EntityManager;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.core.env.Environment;
import uk.gov.hmcts.DummyPdNotifierUtil;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundDao;
import uk.gov.hmcts.pdda.business.entities.xhbpddamessage.XhbPddaMessageDao;
import uk.gov.hmcts.pdda.business.services.cppstaginginboundejb3.CppStagingInboundHelper;
import uk.gov.hmcts.pdda.business.services.pdda.sftp.SftpConfig;
import uk.gov.hmcts.pdda.business.services.pdda.sftp.SftpConfigHelper;
import uk.gov.hmcts.pdda.common.publicdisplay.jms.PublicDisplayNotifier;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertNotNull;
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
 * Copyright: Copyright (c) 2022
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @author Mark Harris
 */
@ExtendWith(EasyMockExtension.class)
@SuppressWarnings({"PMD.ExcessiveImports", "PMD.GodClass", "PMD.CouplingBetweenObjects"})
class PddaHelperTest {

    private static final String NOT_NULL = "Result is Null";
    private static final String NOT_TRUE = "Result is not True";
    private static final String TESTUSER = "TestUser";


    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private PddaMessageHelper mockPddaMessageHelper;

    @Mock
    private CppStagingInboundHelper mockCppStagingInboundHelper;

    @Mock
    private PddaSftpHelper mockPddaSftpHelper;

    @Mock
    private XhbConfigPropRepository mockXhbConfigPropRepository;

    @Mock
    private XhbCourtRepository mockXhbCourtRepository;

    @Mock
    private XhbClobRepository mockXhbClobRepository;

    @Mock
    private PublicDisplayNotifier mockPublicDisplayNotifier;

    @Mock
    private SftpConfigHelper mockSftpConfigHelper;

    @Mock
    private Session mockSession;

    @Mock
    private SftpConfig mockSftpConfig;

    @Mock
    private Environment mockEnvironment;

    @Mock
    private final PddaHelper mockPddaHelper = new PddaHelper(mockEntityManager);

    @TestSubject
    private final PddaHelper classUnderTest = new PddaHelper(mockEntityManager);

    @Test
    void testDefaultConstructor() {
        boolean result = true;
        new PddaHelper(mockEntityManager, mockXhbConfigPropRepository, mockEnvironment,
            mockPddaSftpHelper, null, mockPddaMessageHelper, mockXhbClobRepository,
            mockXhbCourtRepository);
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testCheckForCpMessages() throws IOException {
        // Setup
        List<XhbPddaMessageDao> xhbPddaMessageDaoList = new ArrayList<>();
        xhbPddaMessageDaoList.add(DummyPdNotifierUtil.getXhbPddaMessageDao());
        List<XhbCppStagingInboundDao> xhbCppStagingInboundDaoList = new ArrayList<>();
        xhbCppStagingInboundDaoList.add(DummyPdNotifierUtil.getXhbCppStagingInboundDao());

        EasyMock.expect(mockPddaMessageHelper.findUnrespondedCpMessages())
            .andReturn(xhbPddaMessageDaoList);
        EasyMock.expect(mockCppStagingInboundHelper.findUnrespondedCppMessages())
            .andReturn(xhbCppStagingInboundDaoList);

        Map<String, InputStream> filesMap = new ConcurrentHashMap<>();

        if (!xhbPddaMessageDaoList.isEmpty()) {
            EasyMock.expect(mockPddaHelper.respondToPddaMessage(xhbPddaMessageDaoList))
                .andReturn(filesMap);
        }

        if (!xhbCppStagingInboundDaoList.isEmpty()) {
            EasyMock.expect(mockPddaHelper.respondToCppStagingInbound(xhbCppStagingInboundDaoList))
                .andReturn(filesMap);
        }

        InputStream msgContents = new ByteArrayInputStream("Test Message".getBytes());
        filesMap.put("TestEntry", msgContents);

        String userDisplayName = TESTUSER;
        XhbPddaMessageDao xhbPddaMessageDao = DummyPdNotifierUtil.getXhbPddaMessageDao();
        for (XhbPddaMessageDao dao : xhbPddaMessageDaoList) {
            EasyMock.expect(mockPddaMessageHelper.updatePddaMessage(dao, userDisplayName))
                .andReturn(Optional.of(xhbPddaMessageDao));
        }

        for (XhbCppStagingInboundDao xhbCppStagingInboundDao : xhbCppStagingInboundDaoList) {
            EasyMock.expect(mockCppStagingInboundHelper
                .updateCppStagingInbound(xhbCppStagingInboundDao, userDisplayName))
                .andReturn(Optional.of(xhbCppStagingInboundDao));
        }

        EasyMock.replay(mockCppStagingInboundHelper);
        EasyMock.replay(mockPddaMessageHelper);
        EasyMock.replay(mockPddaHelper);
        // Run
        boolean result = false;
        try {
            classUnderTest.checkForCpMessages(TESTUSER);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        // Checks
        EasyMock.verify(mockCppStagingInboundHelper);
        EasyMock.verify(mockPddaMessageHelper);
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testRespondToCppStagingInbound() throws IOException {
        // Setup
        List<XhbCppStagingInboundDao> cppStagingInboundDaoList = new ArrayList<>();
        cppStagingInboundDaoList.add(DummyPdNotifierUtil.getXhbCppStagingInboundDao());
        Map<String, InputStream> filesMap = new ConcurrentHashMap<>();
        EasyMock.expect(mockPddaHelper.respondToCppStagingInbound(cppStagingInboundDaoList))
            .andReturn(filesMap);
        EasyMock.replay(mockPddaHelper);
        // Run
        Map<String, InputStream> actualResult =
            classUnderTest.respondToCppStagingInbound(cppStagingInboundDaoList);
        // Checks
        assertNotNull(actualResult, NOT_NULL);
    }

    // TODO Test needs to be revisited
    // @Test
    // void testSendMessageRepsonses() {
    // // Setup
    // Map<String, InputStream> fileResponses = new HashMap<>();
    // InputStream msgContents = new ByteArrayInputStream("Test Message".getBytes());
    // fileResponses.put("TestEntry", msgContents);
    // try {
    // EasyMock.expect(mockPddaHelper.getSftpConfigsForTest()).andReturn(mockSftpConfig);
    // mockPddaSFTPHelper.sftpFiles(mockSftpConfig.session, mockSftpConfig.remoteFolder,
    // fileResponses);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // EasyMock.replay(mockPddaHelper);
    // EasyMock.replay(mockSftpConfig);
    // EasyMock.replay(mockPddaSFTPHelper);
    // // Run
    // classUnderTest.sendMessageRepsonses(fileResponses);
    // // Checks
    // EasyMock.verify(mockSftpConfig);
    // EasyMock.verify(mockPddaSFTPHelper);
    // }

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
}
