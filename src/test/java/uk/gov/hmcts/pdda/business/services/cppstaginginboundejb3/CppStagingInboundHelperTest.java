package uk.gov.hmcts.pdda.business.services.cppstaginginboundejb3;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.AfterEach;
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
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropDao;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundDao;
import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;

/**
 * CppStagingInboundHelperTest.
 */

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CppStagingInboundHelperTest {

    private static final String NULL = "Result is Null";
    private static final String TEST = "TEST";

    @Mock
    private static EntityManager mockEntityManager;

    @Mock
    private XhbCppStagingInboundRepository xhbCppStagingInboundRepository;

    @Mock
    private Query mockQuery;

    @InjectMocks
    private final CppStagingInboundHelper classUnderTest =
        new CppStagingInboundHelper(mockEntityManager);

    @AfterEach
    public void resetMocks() {
        Mockito.reset(mockQuery);
    }
    
    @Test
    void testFindNextDocumentByStatus() {
        // Setup
        List<XhbCppStagingInboundDao> xhbCppStagingInboundDaos = new ArrayList<>();
        xhbCppStagingInboundDaos.add(DummyPdNotifierUtil.getXhbCppStagingInboundDao());

        Mockito.when(mockEntityManager.createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(xhbCppStagingInboundDaos);

        Mockito.when(xhbCppStagingInboundRepository.findByIdSafe(isA(Integer.class)))
            .thenReturn(Optional.of(DummyPdNotifierUtil.getXhbCppStagingInboundDao()));

        // Run
        List<XhbCppStagingInboundDao> result = classUnderTest.findNextDocumentByStatus(TEST, TEST);

        // Checks
        assertNotNull(result, NULL);
    }

    @Test
    void testFindNextDocumentByStatusNullDocs() {
        // Setup
        Mockito.when(mockEntityManager.createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(null);

        // Run
        List<XhbCppStagingInboundDao> result = classUnderTest.findNextDocumentByStatus(TEST, TEST);

        // Checks
        assertNotNull(result, NULL);
    }

    @Test
    void testFindUnrespondedCppMessages() {
        // Setup
        List<XhbCppStagingInboundDao> xhbCppStagingInboundDaos = new ArrayList<>();
        xhbCppStagingInboundDaos.add(DummyPdNotifierUtil.getXhbCppStagingInboundDao());

        Mockito.when(mockEntityManager.createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(xhbCppStagingInboundDaos);

        // Run
        List<XhbCppStagingInboundDao> result = classUnderTest.findUnrespondedCppMessages();

        // Checks
        assertNotNull(result, NULL);
    }

    @Test
    void testUpdateCppStagingInbound() {
        // Setup
        XhbCppStagingInboundDao xhbCppStagingInboundDao =
            DummyPdNotifierUtil.getXhbCppStagingInboundDao();

        Mockito.when(xhbCppStagingInboundRepository.update(isA(XhbCppStagingInboundDao.class)))
            .thenReturn(Optional.of(xhbCppStagingInboundDao));

        // Run
        Optional<XhbCppStagingInboundDao> result =
            classUnderTest.updateCppStagingInbound(xhbCppStagingInboundDao, "TestUsername");

        // Checks
        assertNotNull(result, NULL);
    }

    @Test
    void testFindNextDocumentByStatusEmptyDocs() {
        Mockito.when(mockEntityManager.createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(new ArrayList<>());

        List<XhbCppStagingInboundDao> result = classUnderTest.findNextDocumentByStatus(TEST, TEST);
        assertNotNull(result, NULL);
        assertTrue(result.isEmpty(), "Expected empty list when no docs found");
    }

    @Test
    void testFindNextDocumentByStatusFindByIdEmpty() {
        List<XhbCppStagingInboundDao> xhbCppStagingInboundDaos = new ArrayList<>();
        xhbCppStagingInboundDaos.add(DummyPdNotifierUtil.getXhbCppStagingInboundDao());

        Mockito.when(mockEntityManager.createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(xhbCppStagingInboundDaos);

        Mockito.when(xhbCppStagingInboundRepository.findByIdSafe(isA(Integer.class)))
            .thenReturn(Optional.empty());

        List<XhbCppStagingInboundDao> result = classUnderTest.findNextDocumentByStatus(TEST, TEST);
        assertNotNull(result, NULL);
        assertTrue(result.isEmpty(),
            "Expected empty list when findByIdSafe returns empty Optional");
    }

    @Test
    void testFindNextDocumentByStatusMultipleDocs() {
        // Mocks
        EntityManager mockEm = Mockito.mock(EntityManager.class);
        XhbCppStagingInboundRepository mockStagingRepo =
            Mockito.mock(XhbCppStagingInboundRepository.class);
        XhbConfigPropRepository mockConfigRepo = Mockito.mock(XhbConfigPropRepository.class);

        // Dummy Config DAO to control numberOfDocsToProcess
        XhbConfigPropDao configDao = new XhbConfigPropDao();
        configDao.setPropertyValue("2");
        Mockito.when(mockConfigRepo.findByPropertyNameSafe("STAGING_DOCS_TO_PROCESS"))
            .thenReturn(List.of(configDao));

        // Create two dummy DAOs with different IDs
        XhbCppStagingInboundDao dao1 = DummyPdNotifierUtil.getXhbCppStagingInboundDao();
        dao1.setCppStagingInboundId(1);
        XhbCppStagingInboundDao dao2 = DummyPdNotifierUtil.getXhbCppStagingInboundDao();
        dao2.setCppStagingInboundId(2);

        List<XhbCppStagingInboundDao> returnedDocs = List.of(dao1, dao2);

        // Manually create helper with mocked dependencies
        CppStagingInboundHelper helper = new CppStagingInboundHelper(mockEm, mockConfigRepo);
        ReflectionTestUtils.setField(helper, "cppStagingInboundRepository", mockStagingRepo);

        // Mock repo methods
        Mockito
            .when(mockStagingRepo.findNextDocumentByValidationAndProcessingStatusSafe(Mockito.any(),
                Mockito.any(), Mockito.any()))
            .thenReturn(returnedDocs);

        Mockito.when(mockStagingRepo.findByIdSafe(1)).thenReturn(Optional.of(dao1));
        Mockito.when(mockStagingRepo.findByIdSafe(2)).thenReturn(Optional.of(dao2));

        // Act
        List<XhbCppStagingInboundDao> result = helper.findNextDocumentByStatus(TEST, TEST);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(2, result.size(), "Expected two documents in result");
    }

    @Test
    void testFindNextDocumentByStatusFallbackToUnsafeConfigLookup() {
        // Mocks
        EntityManager mockEm = Mockito.mock(EntityManager.class);
        XhbConfigPropRepository mockConfigRepo = Mockito.mock(XhbConfigPropRepository.class);
        XhbCppStagingInboundRepository mockStagingRepo =
            Mockito.mock(XhbCppStagingInboundRepository.class);

        // Config: simulate fallback logic
        Mockito.when(mockConfigRepo.findByPropertyNameSafe("STAGING_DOCS_TO_PROCESS"))
            .thenReturn(null);
        XhbConfigPropDao configDao = new XhbConfigPropDao();
        configDao.setPropertyValue("2");
        Mockito.when(mockConfigRepo.findByPropertyName("STAGING_DOCS_TO_PROCESS"))
            .thenReturn(List.of(configDao));

        // DAO data
        XhbCppStagingInboundDao dao1 = DummyPdNotifierUtil.getXhbCppStagingInboundDao();
        dao1.setCppStagingInboundId(1);
        XhbCppStagingInboundDao dao2 = DummyPdNotifierUtil.getXhbCppStagingInboundDao();
        dao2.setCppStagingInboundId(2);
        List<XhbCppStagingInboundDao> dummyDocs = List.of(dao1, dao2);

        // Repo mocks
        Mockito
            .when(mockStagingRepo.findNextDocumentByValidationAndProcessingStatusSafe(Mockito.any(),
                Mockito.any(), Mockito.any()))
            .thenReturn(dummyDocs);
        Mockito.when(mockStagingRepo.findByIdSafe(1)).thenReturn(Optional.of(dao1));
        Mockito.when(mockStagingRepo.findByIdSafe(2)).thenReturn(Optional.of(dao2));

        // Inject everything
        CppStagingInboundHelper helper =
            new CppStagingInboundHelper(mockEm, mockConfigRepo, mockStagingRepo);

        // Execute
        List<XhbCppStagingInboundDao> result = helper.findNextDocumentByStatus(TEST, TEST);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(2, result.size(), "Expected 2 documents returned");
    }


}
