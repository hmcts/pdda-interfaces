package uk.gov.hmcts.pdda.business.entities.xhbclob;

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
        return classUnderTest;
    }

    @BeforeEach
    void setup() {
        classUnderTest = new XhbClobRepository(mockEntityManager);
    }

    @Test
    void testFindByIdSuccess() {
        try (MockedStatic<EntityManagerUtil> mockedStatic =
            Mockito.mockStatic(EntityManagerUtil.class)) {
            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);

            XhbClobDao dummyDao = getDummyDao();
            Mockito.when(mockEntityManager.find(XhbClobDao.class, getDummyId()))
                .thenReturn(dummyDao);

            boolean result = runFindByIdTest(dummyDao);
            assertTrue(result, NOT_TRUE);
        }
    }

    @Override
    protected boolean testFindById(XhbClobDao dao) {
        Mockito.when(getEntityManager().find(XhbClobDao.class, getDummyLongId())).thenReturn(dao);
        Optional<XhbClobDao> result = getClassUnderTest().findByIdSafe(getDummyLongId());
        assertNotNull(result, "Result is Null");
        if (dao != null) {
            assertSame(dao, result.get(), NOTSAMERESULT);
        } else {
            assertSame(Optional.empty(), result, NOTSAMERESULT);
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
        assertNotNull(clobId, NOTNULLRESULT);
        return new XhbClobDao(result);
    }

}
