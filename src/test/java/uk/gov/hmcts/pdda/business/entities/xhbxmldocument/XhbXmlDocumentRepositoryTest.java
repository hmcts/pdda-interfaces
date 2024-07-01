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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class XhbXmlDocumentRepositoryTest extends AbstractRepositoryTest<XhbXmlDocumentDao> {

    private static final String QUERY_CLOBID = "clobId";
    private static final String QUERY_XMLDOCUMENTCLOBID = "xmlDocumentClobId";

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
    void testFindDocumentByClobId() {
        boolean result = testDocumentListBy(QUERY_CLOBID, getDummyDao());
        assertTrue(result, NOT_TRUE);
        result = testDocumentListBy(QUERY_CLOBID, null);
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testFindListByXmlDocumentClobId() {
        boolean result = testDocumentListBy(QUERY_XMLDOCUMENTCLOBID, getDummyDao());
        assertTrue(result, NOT_TRUE);
        result = testDocumentListBy(QUERY_XMLDOCUMENTCLOBID, null);
        assertTrue(result, NOT_TRUE);
    }

    private boolean testDocumentListBy(String query, XhbXmlDocumentDao dao) {
        List<XhbXmlDocumentDao> list = new ArrayList<>();
        if (dao != null) {
            list.add(dao);
        }
        Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(list);
        Optional<XhbXmlDocumentDao> result = Optional.empty();
        if (QUERY_CLOBID.equals(query)) {
            List<XhbXmlDocumentDao> resultList = (List<XhbXmlDocumentDao>) getClassUnderTest()
                .findDocumentByClobId(getDummyDao().getXmlDocumentClobId(), LocalDateTime.now());
            assertNotNull(resultList, NOTNULL);
            result = resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.get(0));
        } else if (QUERY_XMLDOCUMENTCLOBID.equals(query)) {
            Mockito
                .when(getEntityManager().find(getClassUnderTest().getDaoClass(), getDummyDao().getXmlDocumentClobId()))
                .thenReturn(dao);
            result = (Optional<XhbXmlDocumentDao>) getClassUnderTest()
                .findByXmlDocumentClobId(getDummyDao().getXmlDocumentClobId());
        }
        assertNotNull(result, NOTNULL);
        if (dao != null) {
            assertSame(dao, result.get(), SAME);
        } else {
            assertSame(Optional.empty(), result, SAME);
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
        Integer courtId = Integer.valueOf(-1);
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = "Test2";
        String createdBy = "Test1";
        Integer version = Integer.valueOf(3);
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
