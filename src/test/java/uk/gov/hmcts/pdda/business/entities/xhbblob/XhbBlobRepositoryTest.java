package uk.gov.hmcts.pdda.business.entities.xhbblob;


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
class XhbBlobRepositoryTest extends AbstractRepositoryTest<XhbBlobDao> {

    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private XhbBlobRepository classUnderTest;

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbBlobRepository getClassUnderTest() {
        if (classUnderTest == null) {
            classUnderTest = new XhbBlobRepository(getEntityManager());
        }
        return classUnderTest;
    }

    @Override
    protected boolean testfindById(XhbBlobDao dao) {
        Mockito.when(getEntityManager().find(getClassUnderTest().getDaoClass(), getDummyLongId())).thenReturn(dao);
        Optional<XhbBlobDao> result = getClassUnderTest().findById(getDummyLongId());
        assertNotNull(result, "Result is Null");
        if (dao != null) {
            assertSame(dao, result.get(), "Result is not Same");
        } else {
            assertSame(Optional.empty(), result, "Result is not Same");
        }
        return true;
    }

    @Override
    protected XhbBlobDao getDummyDao() {
        Long blobId = getDummyLongId();
        byte[] blobData = {};
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = "Test2";
        String createdBy = "Test1";
        Integer version = 3;

        XhbBlobDao result =
            new XhbBlobDao(blobId, blobData, lastUpdateDate, creationDate, lastUpdatedBy, createdBy, version);
        blobId = result.getPrimaryKey();
        assertNotNull(blobId, NOTNULL);
        return new XhbBlobDao(result);
    }
}
