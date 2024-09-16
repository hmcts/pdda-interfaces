package uk.gov.hmcts.pdda.business.services.pdda;


import jakarta.persistence.EntityManager;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.DummyPdNotifierUtil;
import uk.gov.hmcts.pdda.business.entities.xhbpddamessage.XhbPddaMessageDao;
import uk.gov.hmcts.pdda.business.entities.xhbpddamessage.XhbPddaMessageRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrefpddamessagetype.XhbRefPddaMessageTypeDao;
import uk.gov.hmcts.pdda.business.entities.xhbrefpddamessagetype.XhbRefPddaMessageTypeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * <p>
 * Title: PddaMessageHelperTest.
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
class PddaMessageHelperTest {

    private static final String NOTNULL = "Result is Null";
    private static final String TRUE = "Result is not True";
    private static final String SAME = "Result is not Same";

    @Mock
    private XhbPddaMessageRepository mockXhbPddaMessageRepository;

    @Mock
    private XhbRefPddaMessageTypeRepository mockXhbRefPddaMessageTypeRepository;

    @TestSubject
    private final PddaMessageHelper classUnderTest =
        new PddaMessageHelper(EasyMock.createMock(EntityManager.class));

    @BeforeAll
    public static void setUp() {
        // Do nothing
    }

    @AfterAll
    public static void tearDown() {
        // Do nothing
    }

    @Test
    void testFindByPddaMessageId() {
        // Setup
        XhbPddaMessageDao xhbPddaMessageDao = DummyPdNotifierUtil.getXhbPddaMessageDao();
        EasyMock.expect(mockXhbPddaMessageRepository.findById(xhbPddaMessageDao.getPrimaryKey()))
            .andReturn(Optional.of(xhbPddaMessageDao));
        EasyMock.replay(mockXhbPddaMessageRepository);
        // Run
        Optional<XhbPddaMessageDao> actualResult =
            classUnderTest.findByPddaMessageId(xhbPddaMessageDao.getPrimaryKey());
        // Checks
        EasyMock.verify(mockXhbPddaMessageRepository);
        assertNotNull(actualResult, NOTNULL);
        assertTrue(actualResult.isPresent(), TRUE);
        assertSame(xhbPddaMessageDao, actualResult.get(), SAME);
    }

    @Test
    void testFindByMessageType() {
        // Setup
        List<XhbRefPddaMessageTypeDao> xhbRefPddaMessageTypeDaoList = new ArrayList<>();
        xhbRefPddaMessageTypeDaoList.add(DummyPdNotifierUtil.getXhbRefPddaMessageTypeDao());
        EasyMock
            .expect(mockXhbRefPddaMessageTypeRepository
                .findByMessageType(xhbRefPddaMessageTypeDaoList.get(0).getPddaMessageType()))
            .andReturn(xhbRefPddaMessageTypeDaoList);
        EasyMock.replay(mockXhbRefPddaMessageTypeRepository);
        // Run
        Optional<XhbRefPddaMessageTypeDao> actualResult = classUnderTest
            .findByMessageType(xhbRefPddaMessageTypeDaoList.get(0).getPddaMessageType());
        // Checks
        EasyMock.verify(mockXhbRefPddaMessageTypeRepository);
        assertNotNull(actualResult, NOTNULL);
        assertTrue(actualResult.isPresent(), TRUE);
        assertSame(xhbRefPddaMessageTypeDaoList.get(0), actualResult.get(), SAME);
    }

    @Test
    void testFindByCpDocumentName() {
        // Setup
        List<XhbPddaMessageDao> xhbPddaMessageDaoList = new ArrayList<>();
        xhbPddaMessageDaoList.add(DummyPdNotifierUtil.getXhbPddaMessageDao());
        EasyMock
            .expect(mockXhbPddaMessageRepository
                .findByCpDocumentName(xhbPddaMessageDaoList.get(0).getCpDocumentName()))
            .andReturn(xhbPddaMessageDaoList);
        EasyMock.replay(mockXhbPddaMessageRepository);
        // Run
        Optional<XhbPddaMessageDao> actualResult =
            classUnderTest.findByCpDocumentName(xhbPddaMessageDaoList.get(0).getCpDocumentName());
        // Checks
        EasyMock.verify(mockXhbPddaMessageRepository);
        assertNotNull(actualResult, NOTNULL);
        assertTrue(actualResult.isPresent(), TRUE);
        assertSame(xhbPddaMessageDaoList.get(0), actualResult.get(), SAME);
    }

    @Test
    void testFindUnrespondedCpMessages() {
        // Setup
        List<XhbPddaMessageDao> xhbPddaMessageDaoList = new ArrayList<>();
        xhbPddaMessageDaoList.add(DummyPdNotifierUtil.getXhbPddaMessageDao());
        EasyMock.expect(mockXhbPddaMessageRepository.findUnrespondedCpMessages())
            .andReturn(xhbPddaMessageDaoList);
        EasyMock.replay(mockXhbPddaMessageRepository);
        // Run
        List<XhbPddaMessageDao> actualResult = classUnderTest.findUnrespondedCpMessages();
        // Checks
        EasyMock.verify(mockXhbPddaMessageRepository);
        assertNotNull(actualResult, NOTNULL);
        assertSame(xhbPddaMessageDaoList, actualResult, SAME);
    }

    @Test
    void testSavePddaMessage() {
        // Setup
        XhbPddaMessageDao xhbPddaMessageDao = DummyPdNotifierUtil.getXhbPddaMessageDao();
        EasyMock.expect(mockXhbPddaMessageRepository.update(xhbPddaMessageDao))
            .andReturn(Optional.of(xhbPddaMessageDao));
        EasyMock.replay(mockXhbPddaMessageRepository);
        // Run
        Optional<XhbPddaMessageDao> actualResult =
            classUnderTest.savePddaMessage(xhbPddaMessageDao);
        // Checks
        EasyMock.verify(mockXhbPddaMessageRepository);
        assertNotNull(actualResult, NOTNULL);
        assertTrue(actualResult.isPresent(), TRUE);
        assertSame(xhbPddaMessageDao.getPrimaryKey(), actualResult.get().getPrimaryKey(), SAME);
    }

    @Test
    void testUpdatePddaMessage() {
        // Setup
        XhbPddaMessageDao xhbPddaMessageDao = DummyPdNotifierUtil.getXhbPddaMessageDao();
        EasyMock.expect(mockXhbPddaMessageRepository.update(xhbPddaMessageDao))
            .andReturn(Optional.of(xhbPddaMessageDao));
        EasyMock.replay(mockXhbPddaMessageRepository);
        // Run
        Optional<XhbPddaMessageDao> actualResult =
            classUnderTest.updatePddaMessage(xhbPddaMessageDao, "TestUser");
        // Checks
        EasyMock.verify(mockXhbPddaMessageRepository);
        assertNotNull(actualResult, NOTNULL);
        assertTrue(actualResult.isPresent(), TRUE);
        assertSame(xhbPddaMessageDao.getPrimaryKey(), actualResult.get().getPrimaryKey(), SAME);
    }

    @Test
    void testSavePddaMessageType() {
        // Setup
        XhbRefPddaMessageTypeDao xhbRefPddaMessageTypeDao =
            DummyPdNotifierUtil.getXhbRefPddaMessageTypeDao();
        EasyMock.expect(mockXhbRefPddaMessageTypeRepository.update(xhbRefPddaMessageTypeDao))
            .andReturn(Optional.of(xhbRefPddaMessageTypeDao));
        EasyMock.replay(mockXhbRefPddaMessageTypeRepository);
        // Run
        Optional<XhbRefPddaMessageTypeDao> actualResult =
            classUnderTest.savePddaMessageType(xhbRefPddaMessageTypeDao);
        // Checks
        EasyMock.verify(mockXhbRefPddaMessageTypeRepository);
        assertNotNull(actualResult, NOTNULL);
        assertTrue(actualResult.isPresent(), TRUE);
        assertSame(xhbRefPddaMessageTypeDao.getPrimaryKey(), actualResult.get().getPrimaryKey(),
            SAME);
    }
}
