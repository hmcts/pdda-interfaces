package uk.gov.hmcts.pdda.business.services.pdda;

import com.jcraft.jsch.Session;
import jakarta.persistence.EntityManager;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicDisplayEvent;
import uk.gov.hmcts.DummyEventUtil;
import uk.gov.hmcts.DummyPdNotifierUtil;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbpddamessage.XhbPddaMessageDao;
import uk.gov.hmcts.pdda.business.entities.xhbrefpddamessagetype.XhbRefPddaMessageTypeDao;
import uk.gov.hmcts.pdda.business.services.cppstaginginboundejb3.CppStagingInboundHelper;
import uk.gov.hmcts.pdda.common.publicdisplay.jms.PublicDisplayNotifier;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * <p>
 * Title: PPddaHelperMessagesTest.
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
class PddaHelperMessagesTest {
    
    private static final String NOTNULL = "Result is Null";
    private static final String TRUE = "Result is not True";
    private static final String SAME = "Result is not Same";
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
    private Session mockSession;

    @Mock
    private SftpConfig mockSftpConfig;
    
    @Mock
    private final PddaHelper mockPddaHelper = new PddaHelper(mockEntityManager);

    @TestSubject
    private final PddaHelper classUnderTest = new PddaHelper(mockEntityManager);
    
    @BeforeAll
    public static void setUp() throws Exception {
        // Do nothing
    }

    @AfterAll
    public static void tearDown() throws Exception {
        // Do nothing
    }
    
    @Test
    void testSendMessage() {
        // Setup
        PublicDisplayEvent event = DummyEventUtil.getMoveCaseEvent();
        mockPublicDisplayNotifier.sendMessage(event);
        EasyMock.expectLastCall();
        EasyMock.replay(mockPublicDisplayNotifier);
        // Run
        classUnderTest.sendMessage(event);
        // Checks
        EasyMock.verify(mockPublicDisplayNotifier);
        assertNotNull(event, NOTNULL);
    }

    @Test
    void testCreateMessageSuccess() {
        // Setup
        XhbPddaMessageDao xhbPddaMessageDao = DummyPdNotifierUtil.getXhbPddaMessageDao();
        Optional<XhbPddaMessageDao> expectedResult = Optional.of(xhbPddaMessageDao);
        // Run
        Optional<XhbPddaMessageDao> actualResult = testCreateMessage(xhbPddaMessageDao, expectedResult);
        // Checks
        assertTrue(actualResult.isPresent(), TRUE);
        assertSame(expectedResult, actualResult, SAME);
        assertSame(expectedResult.get().getPrimaryKey(), actualResult.get().getPrimaryKey(), SAME);
    }

    private Optional<XhbPddaMessageDao> testCreateMessage(XhbPddaMessageDao xhbPddaMessageDao,
        Optional<XhbPddaMessageDao> expectedResult) {
        // Setup
        EasyMock.expect(mockPddaMessageHelper.savePddaMessage(EasyMock.isA(XhbPddaMessageDao.class)))
            .andReturn(expectedResult);
        EasyMock.replay(mockPddaMessageHelper);
        // Run
        Optional<XhbPddaMessageDao> actualResult = PddaMessageUtil.createMessage(mockPddaMessageHelper,
            xhbPddaMessageDao.getCourtId(), xhbPddaMessageDao.getCourtRoomId(),
            xhbPddaMessageDao.getPddaMessageTypeId(), xhbPddaMessageDao.getPddaMessageDataId(),
            xhbPddaMessageDao.getPddaBatchId(), xhbPddaMessageDao.getCpDocumentName(),
            xhbPddaMessageDao.getCpResponseGenerated(), xhbPddaMessageDao.getErrorMessage());
        // Checks
        EasyMock.verify(mockPddaMessageHelper);
        assertNotNull(actualResult, NOTNULL);
        assertSame(expectedResult, actualResult, SAME);
        return actualResult;
    }

    @Test
    void testCreateMessageFailure() {
        // Setup
        XhbPddaMessageDao xhbPddaMessageDao = DummyPdNotifierUtil.getXhbPddaMessageDao();
        Optional<XhbPddaMessageDao> expectedResult = Optional.empty();
        // Run
        Optional<XhbPddaMessageDao> actualResult = testCreateMessage(xhbPddaMessageDao, expectedResult);
        // Checks
        assertTrue(actualResult.isEmpty(), "Result is not empty");
    }

    @Test
    void testCreateMessageTypeSuccess() {
        // Setup
        XhbRefPddaMessageTypeDao xhbRefPddaMessageTypeDao = DummyPdNotifierUtil.getXhbRefPddaMessageTypeDao();
        Optional<XhbRefPddaMessageTypeDao> expectedResult = Optional.of(xhbRefPddaMessageTypeDao);
        // Run
        Optional<XhbRefPddaMessageTypeDao> actualResult =
            testCreateMessageType(xhbRefPddaMessageTypeDao, expectedResult);
        // Checks
        assertTrue(actualResult.isPresent(), "Result is empty");
        assertSame(xhbRefPddaMessageTypeDao, actualResult.get(), SAME);
        assertSame(expectedResult.get().getPrimaryKey(), actualResult.get().getPrimaryKey(), SAME);
    }

    private Optional<XhbRefPddaMessageTypeDao> testCreateMessageType(XhbRefPddaMessageTypeDao xhbRefPddaMessageTypeDao,
        Optional<XhbRefPddaMessageTypeDao> expectedResult) {
        // Setup
        EasyMock.expect(mockPddaMessageHelper.savePddaMessageType(EasyMock.isA(XhbRefPddaMessageTypeDao.class)))
            .andReturn(expectedResult);
        EasyMock.replay(mockPddaMessageHelper);
        // Run
        Optional<XhbRefPddaMessageTypeDao> actualResult = PddaMessageUtil.createMessageType(mockPddaMessageHelper,
            xhbRefPddaMessageTypeDao.getPddaMessageType(), LocalDateTime.now());
        // Checks
        EasyMock.verify(mockPddaMessageHelper);
        assertNotNull(actualResult, NOTNULL);
        assertSame(expectedResult, actualResult, SAME);
        return actualResult;
    }

    @Test
    void testCreateMessageTypeFailure() {
        // Setup
        XhbRefPddaMessageTypeDao xhbRefPddaMessageTypeDao = DummyPdNotifierUtil.getXhbRefPddaMessageTypeDao();
        Optional<XhbRefPddaMessageTypeDao> expectedResult = Optional.empty();
        // Run
        Optional<XhbRefPddaMessageTypeDao> actualResult =
            testCreateMessageType(xhbRefPddaMessageTypeDao, expectedResult);
        // Checks
        assertTrue(actualResult.isEmpty(), "Result is not empty");
    }
    
    @Test
    void testRespondToPddaMessage() throws IOException {
        // Setup
        List<XhbPddaMessageDao> pddaMessageDaoList = new ArrayList<>();
        pddaMessageDaoList.add(DummyPdNotifierUtil.getXhbPddaMessageDao());
        Map<String, InputStream> filesMap = new ConcurrentHashMap<>();
        EasyMock.expect(mockPddaHelper.respondToPddaMessage(pddaMessageDaoList)).andReturn(filesMap);
        EasyMock.replay(mockPddaHelper);
        // Run
        Map<String, InputStream> actualResult = classUnderTest.respondToPddaMessage(pddaMessageDaoList);
        // Checks
        assertNotNull(actualResult, NOTNULL);
    }
    
    @Test
    void testUpdatePddaMessageRecords() {
        // Setup
        XhbPddaMessageDao xhbPddaMessageDao = DummyPdNotifierUtil.getXhbPddaMessageDao();
        List<XhbPddaMessageDao> pddaMessageDaoList = new ArrayList<>();
        pddaMessageDaoList.add(xhbPddaMessageDao);
        String userDisplayName = TESTUSER;
        for (XhbPddaMessageDao dao : pddaMessageDaoList) {
            EasyMock.expect(mockPddaMessageHelper.updatePddaMessage(dao, userDisplayName))
                .andReturn(Optional.of(xhbPddaMessageDao));
        }
        EasyMock.replay(mockPddaMessageHelper);
        // Run
        boolean result = false;
        try {
            PddaMessageUtil.updatePddaMessageRecords(mockPddaMessageHelper, pddaMessageDaoList, userDisplayName);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        // Checks
        EasyMock.verify(mockPddaMessageHelper);
        assertTrue(result, TRUE);
    }
}
