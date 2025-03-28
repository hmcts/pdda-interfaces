package uk.gov.hmcts.pdda.business.services.pdda;


import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;
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
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings({"PMD.TooManyMethods", "PMD.LawOfDemeter"})
class PddaMessageHelperTest {

    private static final String NOTNULL = "Result is Null";
    private static final String TRUE = "Result is not True";
    private static final String SAME = "Result is not Same";

    @Mock
    private EntityTransaction mockEntityTransaction;

    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private XhbPddaMessageRepository mockXhbPddaMessageRepository;

    @Mock
    private XhbRefPddaMessageTypeRepository mockXhbRefPddaMessageTypeRepository;

    @InjectMocks
    private PddaMessageHelper classUnderTest;

    @BeforeAll
    public static void setUp() {
        Mockito.mockStatic(EntityManagerUtil.class);
    }

    @AfterAll
    public static void tearDown() {
        Mockito.clearAllCaches();
    }

    @BeforeEach
    public void setUpEach() {
        mockEntityManager = Mockito.mock(EntityManager.class);
        mockXhbPddaMessageRepository = Mockito.mock(XhbPddaMessageRepository.class);
        mockXhbRefPddaMessageTypeRepository = Mockito.mock(XhbRefPddaMessageTypeRepository.class);
        classUnderTest = new PddaMessageHelper(mockEntityManager);

        ReflectionTestUtils.setField(classUnderTest, "pddaMessageRepository",
            mockXhbPddaMessageRepository);
        ReflectionTestUtils.setField(classUnderTest, "refPddaMessageTypeRepository",
            mockXhbRefPddaMessageTypeRepository);
    }

    @AfterEach
    public void tearDownEach() {
        // Do nothing
    }

    @Test
    void testGetPddaMessageRepository() {
        XhbPddaMessageRepository result = classUnderTest.getPddaMessageRepository();
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testFindByPddaMessageId() {
        // Setup
        XhbPddaMessageDao xhbPddaMessageDao = DummyPdNotifierUtil.getXhbPddaMessageDao();
        Mockito.when(mockXhbPddaMessageRepository.findById(xhbPddaMessageDao.getPrimaryKey()))
            .thenReturn(Optional.of(xhbPddaMessageDao));
        mockTheEntityManager(true);
        // Run
        Optional<XhbPddaMessageDao> actualResult =
            classUnderTest.findByPddaMessageId(xhbPddaMessageDao.getPrimaryKey());
        // Checks
        assertNotNull(actualResult, NOTNULL);
        assertTrue(actualResult.isPresent(), TRUE);
        assertSame(xhbPddaMessageDao, actualResult.get(), SAME);
    }

    @Test
    void testGetRefPddaMessageTypeRepository() {
        XhbRefPddaMessageTypeRepository result = classUnderTest.getRefPddaMessageTypeRepository();
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testFindByMessageType() {
        // Setup
        List<XhbRefPddaMessageTypeDao> xhbRefPddaMessageTypeDaoList = new ArrayList<>();
        xhbRefPddaMessageTypeDaoList.add(DummyPdNotifierUtil.getXhbRefPddaMessageTypeDao());
        Mockito
            .when(mockXhbRefPddaMessageTypeRepository
                .findByMessageType(xhbRefPddaMessageTypeDaoList.get(0).getPddaMessageType()))
            .thenReturn(xhbRefPddaMessageTypeDaoList);
        mockTheEntityManager(true);
        // Run
        Optional<XhbRefPddaMessageTypeDao> actualResult = classUnderTest
            .findByMessageType(xhbRefPddaMessageTypeDaoList.get(0).getPddaMessageType());
        // Checks
        assertNotNull(actualResult, NOTNULL);
        assertTrue(actualResult.isPresent(), TRUE);
        assertSame(xhbRefPddaMessageTypeDaoList.get(0), actualResult.get(), SAME);
    }

    @Test
    void testFindByCpDocumentName() {
        // Setup
        List<XhbPddaMessageDao> xhbPddaMessageDaoList = new ArrayList<>();
        xhbPddaMessageDaoList.add(DummyPdNotifierUtil.getXhbPddaMessageDao());
        Mockito
            .when(mockXhbPddaMessageRepository
                .findByCpDocumentName(xhbPddaMessageDaoList.get(0).getCpDocumentName()))
            .thenReturn(xhbPddaMessageDaoList);
        mockTheEntityManager(true);
        // Run
        Optional<XhbPddaMessageDao> actualResult =
            classUnderTest.findByCpDocumentName(xhbPddaMessageDaoList.get(0).getCpDocumentName());
        // Checks
        assertNotNull(actualResult, NOTNULL);
        assertTrue(actualResult.isPresent(), TRUE);
        assertSame(xhbPddaMessageDaoList.get(0), actualResult.get(), SAME);
    }

    @Test
    void testFindUnrespondedCpMessages() {
        // Setup
        List<XhbPddaMessageDao> xhbPddaMessageDaoList = new ArrayList<>();
        xhbPddaMessageDaoList.add(DummyPdNotifierUtil.getXhbPddaMessageDao());
        Mockito.when(mockXhbPddaMessageRepository.findUnrespondedCpMessages())
            .thenReturn(xhbPddaMessageDaoList);
        mockTheEntityManager(true);
        // Run
        List<XhbPddaMessageDao> actualResult = classUnderTest.findUnrespondedCpMessages();
        // Checks
        assertNotNull(actualResult, NOTNULL);
        assertSame(xhbPddaMessageDaoList, actualResult, SAME);
    }

    @Test
    void testSavePddaMessage() {
        // Setup
        XhbPddaMessageDao xhbPddaMessageDao = DummyPdNotifierUtil.getXhbPddaMessageDao();
        Mockito.when(mockXhbPddaMessageRepository.findByCpDocumentName(Mockito.isA(String.class)))
            .thenReturn(new ArrayList<>());
        mockXhbPddaMessageRepository.save(xhbPddaMessageDao);
        mockTheEntityManager(true);
        boolean result = true;
        // Run
        classUnderTest.savePddaMessage(xhbPddaMessageDao);
        // Checks
        assertTrue(result, TRUE);
    }

    @Test
    void testSavePddaMessageExistingEntry() {
        // Setup
        XhbPddaMessageDao xhbPddaMessageDao = DummyPdNotifierUtil.getXhbPddaMessageDao();
        List<XhbPddaMessageDao> xhbPddaMessageDaos = new ArrayList<>();
        xhbPddaMessageDaos.add(xhbPddaMessageDao);
        Mockito.when(mockXhbPddaMessageRepository.findByCpDocumentName(Mockito.isA(String.class)))
            .thenReturn(xhbPddaMessageDaos);
        mockTheEntityManager(true);
        boolean result = true;
        // Run
        classUnderTest.savePddaMessage(xhbPddaMessageDao);
        // Checks
        assertTrue(result, TRUE);
    }

    @Test
    void testUpdatePddaMessage() {
        // Setup
        XhbPddaMessageDao xhbPddaMessageDao = DummyPdNotifierUtil.getXhbPddaMessageDao();
        Mockito.when(mockXhbPddaMessageRepository.update(xhbPddaMessageDao))
            .thenReturn(Optional.of(xhbPddaMessageDao));
        mockTheEntityManager(true);
        // Run
        Optional<XhbPddaMessageDao> actualResult =
            classUnderTest.updatePddaMessage(xhbPddaMessageDao, "TestUser");
        // Checks
        assertNotNull(actualResult, NOTNULL);
        assertTrue(actualResult.isPresent(), TRUE);
        assertSame(xhbPddaMessageDao.getPrimaryKey(), actualResult.get().getPrimaryKey(), SAME);
    }

    @Test
    void testSavePddaMessageType() {
        // Setup
        XhbRefPddaMessageTypeDao xhbRefPddaMessageTypeDao =
            DummyPdNotifierUtil.getXhbRefPddaMessageTypeDao();
        Mockito.when(mockXhbRefPddaMessageTypeRepository.update(xhbRefPddaMessageTypeDao))
            .thenReturn(Optional.of(xhbRefPddaMessageTypeDao));
        mockTheEntityManager(true);
        // Run
        Optional<XhbRefPddaMessageTypeDao> actualResult =
            classUnderTest.savePddaMessageType(xhbRefPddaMessageTypeDao);
        // Checks
        assertNotNull(actualResult, NOTNULL);
        assertTrue(actualResult.isPresent(), TRUE);
        assertSame(xhbRefPddaMessageTypeDao.getPrimaryKey(), actualResult.get().getPrimaryKey(),
            SAME);
    }

    private void mockTheEntityManager(boolean result) {
        Mockito.when(EntityManagerUtil.getEntityManager()).thenReturn(mockEntityManager);
        Mockito.when(EntityManagerUtil.isEntityManagerActive(mockEntityManager)).thenReturn(result);
        Mockito.when(mockEntityManager.getTransaction()).thenReturn(mockEntityTransaction);
    }
}
