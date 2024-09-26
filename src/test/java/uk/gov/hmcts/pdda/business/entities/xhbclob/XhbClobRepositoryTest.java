package uk.gov.hmcts.pdda.business.entities.xhbclob;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.pdda.business.entities.AbstractRepositoryTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("PMD.TestClassWithoutTestCases")
class XhbClobRepositoryTest extends AbstractRepositoryTest<XhbClobDao> {

    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private XhbClobRepository classUnderTest;

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbClobRepository getClassUnderTest() {
        if (classUnderTest == null) {
            classUnderTest = new XhbClobRepository(getEntityManager());
        }
        return classUnderTest;
    }

    @Override
    protected boolean testfindById(XhbClobDao dao) {
        Mockito.when(getEntityManager().find(getClassUnderTest().getDaoClass(), getDummyLongId())).thenReturn(dao);
        Optional<XhbClobDao> result = getClassUnderTest().findById(getDummyLongId());
        assertNotNull(result, "Result is Null");
        if (dao != null) {
            assertSame(dao, result.get(), SAME);
        } else {
            assertSame(Optional.empty(), result, SAME);
        }
        return true;
    }

    @Override
    protected XhbClobDao getDummyDao() {
        Long clobId = getDummyLongId();
        String clobData = "clobData";
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = "Test2";
        String createdBy = "Test1";
        Integer version = 3;

        XhbClobDao result =
            new XhbClobDao(clobId, clobData, lastUpdateDate, creationDate, lastUpdatedBy, createdBy, version);
        clobId = result.getPrimaryKey();
        assertNotNull(clobId, NOTNULL);
        return new XhbClobDao(result);
    }

}
