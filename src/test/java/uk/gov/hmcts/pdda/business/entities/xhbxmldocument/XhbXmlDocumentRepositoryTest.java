package uk.gov.hmcts.pdda.business.entities.xhbxmldocument;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.pdda.business.entities.AbstractRepositoryTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class XhbXmlDocumentRepositoryTest extends AbstractRepositoryTest<XhbXmlDocumentDao> {

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
        if (classUnderTest == null) {
            classUnderTest = new XhbXmlDocumentRepository(getEntityManager());
        }
        return classUnderTest;
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

    private boolean testFindListByClobId(XhbXmlDocumentDao dao) {
        List<XhbXmlDocumentDao> list = new ArrayList<>();
        if (dao != null) {
            list.add(dao);
        }
        Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(list);
        List<XhbXmlDocumentDao> result = getClassUnderTest()
            .findListByClobId(getDummyDao().getXmlDocumentClobId(), LocalDateTime.now());
        assertNotNull(result, "Result is Null");
        if (dao != null) {
            assertSame(dao, result.get(0), "Result is not Same");
        } else {
            assertSame(0, result.size(), "Result is not Same");
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
        assertNotNull(xmlDocumentId, NOTNULL);
        return new XhbXmlDocumentDao(result);
    }

}
