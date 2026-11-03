package uk.gov.hmcts.pdda.business.entities.xhbxmldocument;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.pdda.business.entities.AbstractRepositoryTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class XhbXmlDocumentRepositoryTest extends AbstractRepositoryTest<XhbXmlDocumentDao> {

    private static final String RESULT_NOT_SAME = "Result is not Same";
    
    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private XhbXmlDocumentRepository classUnderTest;

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbXmlDocumentRepository getClassUnderTest() {
        return classUnderTest;
    }

    @BeforeEach
    void setup() {
        classUnderTest = new XhbXmlDocumentRepository(mockEntityManager);
    }

    @Test
    void testFindByIdSuccess() {
        try (MockedStatic<EntityManagerUtil> mockedStatic =
            Mockito.mockStatic(EntityManagerUtil.class)) {
            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);

            XhbXmlDocumentDao dummyDao = getDummyDao();
            Mockito.when(mockEntityManager.find(XhbXmlDocumentDao.class, getDummyId()))
                .thenReturn(dummyDao);

            boolean result = runFindByIdTest(dummyDao);
            assertTrue(result, NOT_TRUE);
        }
    }

    @Test
    void testFindListByClobIdSuccess() {
        boolean result = testFindListByClobId(getDummyDao());
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testFindListByClobIdFailure() {
        boolean result = testFindListByClobId(null);
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testFindJsonDocumentsUsesLocalEntityManager() {
        try (MockedStatic<EntityManagerUtil> mockedStatic =
            Mockito.mockStatic(EntityManagerUtil.class)) {
            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);
            List<XhbXmlDocumentDao> documents = List.of(getDummyDao());
            Mockito.when(mockEntityManager.createNamedQuery("XHB_XML_DOCUMENT.findJsonDocuments"))
                .thenReturn(mockQuery);
            Mockito.when(mockQuery.getResultList()).thenReturn(documents);

            List<XhbXmlDocumentDao> result = classUnderTest.findJsonDocumentsSafe("ND");

            assertSame(documents, result, RESULT_NOT_SAME);
        }
    }

    @Test
    void testFindJsonDocumentsReturnsEmptyListOnFailure() {
        try (MockedStatic<EntityManagerUtil> mockedStatic =
            Mockito.mockStatic(EntityManagerUtil.class)) {
            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);
            Mockito.when(mockEntityManager.createNamedQuery("XHB_XML_DOCUMENT.findJsonDocuments"))
                .thenThrow(new IllegalStateException("Connection is closed"));

            List<XhbXmlDocumentDao> result = classUnderTest.findJsonDocumentsSafe("ND");

            assertTrue(result.isEmpty(), "Result is not empty");
        }
    }

    @Test
    void testFindByXmlDocumentClobIdSafeUsesLocalEntityManager() {
        try (MockedStatic<EntityManagerUtil> mockedStatic =
            Mockito.mockStatic(EntityManagerUtil.class)) {
            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);
            XhbXmlDocumentDao document = getDummyDao();
            Mockito.when(mockEntityManager.createNamedQuery(
                "XHB_XML_DOCUMENT.findByXmlDocumentClobId")).thenReturn(mockQuery);
            Mockito.when(mockQuery.getResultList()).thenReturn(List.of(document));

            Optional<XhbXmlDocumentDao> result =
                classUnderTest.findByXmlDocumentClobIdSafe(document.getXmlDocumentClobId());

            assertSame(document, result.orElseThrow(), RESULT_NOT_SAME);
        }
    }

    @Test
    void testFindByXmlDocumentClobIdSafeReturnsEmptyOnFailure() {
        try (MockedStatic<EntityManagerUtil> mockedStatic =
            Mockito.mockStatic(EntityManagerUtil.class)) {
            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);
            Mockito.when(mockEntityManager.createNamedQuery(
                "XHB_XML_DOCUMENT.findByXmlDocumentClobId"))
                .thenThrow(new IllegalStateException("Connection is closed"));

            Optional<XhbXmlDocumentDao> result =
                classUnderTest.findByXmlDocumentClobIdSafe(getDummyLongId());

            assertTrue(result.isEmpty(), "Result is not empty");
        }
    }

    private boolean testFindListByClobId(XhbXmlDocumentDao dao) {
        List<XhbXmlDocumentDao> list = new ArrayList<>();
        if (dao != null) {
            list.add(dao);
        }
        Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(list);
        List<XhbXmlDocumentDao> result = getClassUnderTest()
            .findDocumentByClobId(getDummyDao().getXmlDocumentClobId(), LocalDateTime.now());
        assertNotNull(result, "Result is Null");
        if (dao != null) {
            assertSame(dao, result.get(0), RESULT_NOT_SAME);
        } else {
            assertSame(0, result.size(), RESULT_NOT_SAME);
        }
        return true;
    }

    @Override
    protected XhbXmlDocumentDao getDummyDao() {
        Integer xmlDocumentId = getDummyId();
        LocalDateTime dateCreated = LocalDateTime.now();
        String documentTitle = "documentTitle";
        Long xmlDocumentClobId = getDummyLongId();
        String status = "status";
        LocalDateTime expiryDate = LocalDateTime.now();
        String documentType = "documentType";
        Integer courtId = -1;
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = "Test2";
        String createdBy = "Test1";
        Integer version = 3;
        XhbXmlDocumentDao result = new XhbXmlDocumentDao();
        result.setXmlDocumentId(xmlDocumentId);
        result.setDateCreated(dateCreated);
        result.setDocumentTitle(documentTitle);
        result.setXmlDocumentClobId(xmlDocumentClobId);
        result.setStatus(status);
        result.setExpiryDate(expiryDate);
        result.setDocumentType(documentType);
        result.setCourtId(courtId);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);
        xmlDocumentId = result.getPrimaryKey();
        assertNotNull(xmlDocumentId, NOTNULLRESULT);
        return new XhbXmlDocumentDao(result);
    }

}
