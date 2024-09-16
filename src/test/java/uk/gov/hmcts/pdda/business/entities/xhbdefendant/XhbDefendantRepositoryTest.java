package uk.gov.hmcts.pdda.business.entities.xhbdefendant;

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
class XhbDefendantRepositoryTest extends AbstractRepositoryTest<XhbDefendantDao> {

    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private XhbDefendantRepository classUnderTest;

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbDefendantRepository getClassUnderTest() {
        if (classUnderTest == null) {
            classUnderTest = new XhbDefendantRepository(getEntityManager());
        }
        return classUnderTest;
    }

    @Override
    protected XhbDefendantDao getDummyDao() {
        Integer defendantId = getDummyId();
        String firstName = "firstName";
        String middleName = "middleName";
        String surname = "surname";
        String publicDisplayHide = "publicDisplayHide";
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = "Test2";
        String createdBy = "Test1";
        Integer version = 3;
        XhbDefendantDao result = new XhbDefendantDao();
        result.setDefendantId(defendantId);
        result.setFirstName(firstName);
        result.setMiddleName(middleName);
        result.setSurname(surname);
        result.setPublicDisplayHide(publicDisplayHide);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);
        defendantId = result.getPrimaryKey();
        assertNotNull(defendantId, NOTNULL);
        return new XhbDefendantDao(result);
    }

}
