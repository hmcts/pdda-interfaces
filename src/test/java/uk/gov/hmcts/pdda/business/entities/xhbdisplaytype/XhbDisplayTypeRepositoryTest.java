package uk.gov.hmcts.pdda.business.entities.xhbdisplaytype;

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
class XhbDisplayTypeRepositoryTest extends AbstractRepositoryTest<XhbDisplayTypeDao> {

    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private XhbDisplayTypeRepository classUnderTest;

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbDisplayTypeRepository getClassUnderTest() {
        if (classUnderTest == null) {
            classUnderTest = new XhbDisplayTypeRepository(getEntityManager());
        }
        return classUnderTest;
    }

    @Override
    protected XhbDisplayTypeDao getDummyDao() {
        Integer displayTypeId = getDummyId();
        String descriptionCode = "descriptionCode";
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = "Test2";
        String createdBy = "Test1";
        Integer version = 3;
        XhbDisplayTypeDao result = new XhbDisplayTypeDao(displayTypeId, descriptionCode, lastUpdateDate, creationDate,
            lastUpdatedBy, createdBy, version);
        displayTypeId = result.getPrimaryKey();
        assertNotNull(displayTypeId, NOTNULL);
        return new XhbDisplayTypeDao(result);
    }

}
