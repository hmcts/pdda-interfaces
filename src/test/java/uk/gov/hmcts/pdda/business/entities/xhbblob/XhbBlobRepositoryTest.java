package uk.gov.hmcts.pdda.business.entities.xhbblob;

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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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
        return classUnderTest;
    }

    @BeforeEach
    void setup() {
        classUnderTest = new XhbBlobRepository(mockEntityManager);
    }

    @Test
    void testFindByIdSuccess() {
        try (MockedStatic<EntityManagerUtil> mockedStatic =
            Mockito.mockStatic(EntityManagerUtil.class)) {
            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);

            XhbBlobDao dummyDao = getDummyDao();
            Mockito.when(mockEntityManager.find(XhbBlobDao.class, getDummyId()))
                .thenReturn(dummyDao);

            boolean result = runFindByIdTest(dummyDao);
            assertTrue(result, NOT_TRUE);
        }
    }


    @Override
    protected boolean testFindById(XhbBlobDao dao) {
        Long id = getDummyLongId(); // store ID to ensure consistency

        Mockito.when(getEntityManager().find(getClassUnderTest().getDaoClass(), id))
            .thenReturn(dao);

        Optional<XhbBlobDao> result = getClassUnderTest().findByIdSafe(id);
        assertNotNull(result, NULLRESULT);

        if (dao != null) {
            assertTrue(result.isPresent(), "Expected result to be present");
            assertSame(dao, result.get(), NOTSAMERESULT);
        } else {
            assertTrue(result.isEmpty(), "Expected result to be empty");
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
        assertNotNull(blobId, NOTNULLRESULT);
        return new XhbBlobDao(result);
    }
}
