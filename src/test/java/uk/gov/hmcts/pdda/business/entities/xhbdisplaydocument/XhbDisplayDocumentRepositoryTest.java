package uk.gov.hmcts.pdda.business.entities.xhbdisplaydocument;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.pdda.business.entities.AbstractRepositoryTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("PMD.TestClassWithoutTestCases")
class XhbDisplayDocumentRepositoryTest extends AbstractRepositoryTest<XhbDisplayDocumentDao> {

    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private XhbDisplayDocumentRepository classUnderTest;

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbDisplayDocumentRepository getClassUnderTest() {
        if (classUnderTest == null) {
            classUnderTest = new XhbDisplayDocumentRepository(getEntityManager());
        }
        return classUnderTest;
    }

    @Override
    protected XhbDisplayDocumentDao getDummyDao() {
        Integer displayDocumentId = getDummyId();
        String descriptionCode = "descriptionCode";
        Integer defaultPageDelay = -1;
        String multipleCourtYn = "multipleCourtYn";
        String country = "country";
        String language = "language";
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = "Test2";
        String createdBy = "Test1";
        Integer version = 3;
        XhbDisplayDocumentDao result = new XhbDisplayDocumentDao();
        result.setDisplayDocumentId(displayDocumentId);
        result.setDescriptionCode(descriptionCode);
        result.setDefaultPageDelay(defaultPageDelay);
        result.setMultipleCourtYn(multipleCourtYn);
        result.setCountry(country);
        result.setLanguage(language);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);
        displayDocumentId = result.getPrimaryKey();
        assertNotNull(displayDocumentId, NOTNULL);
        return new XhbDisplayDocumentDao(result);
    }

}
