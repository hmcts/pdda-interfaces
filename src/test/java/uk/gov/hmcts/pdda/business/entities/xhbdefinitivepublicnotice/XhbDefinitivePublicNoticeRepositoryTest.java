package uk.gov.hmcts.pdda.business.entities.xhbdefinitivepublicnotice;

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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class XhbDefinitivePublicNoticeRepositoryTest extends AbstractRepositoryTest<XhbDefinitivePublicNoticeDao> {

    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private XhbDefinitivePublicNoticeRepository classUnderTest;

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbDefinitivePublicNoticeRepository getClassUnderTest() {
        return classUnderTest;
    }

    @BeforeEach
    void setup() {
        classUnderTest = new XhbDefinitivePublicNoticeRepository(mockEntityManager);
    }

    @Test
    void testFindByIdSuccess() {
        try (MockedStatic<EntityManagerUtil> mockedStatic =
            Mockito.mockStatic(EntityManagerUtil.class)) {
            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);

            XhbDefinitivePublicNoticeDao dummyDao = getDummyDao();
            Mockito.when(mockEntityManager.find(XhbDefinitivePublicNoticeDao.class, getDummyId()))
                .thenReturn(dummyDao);

            boolean result = runFindByIdTest(dummyDao);
            assertTrue(result, NOT_TRUE);
        }
    }

    @Override
    protected XhbDefinitivePublicNoticeDao getDummyDao() {
        Integer definitivePnId = getDummyId();
        String definitivePnDesc = "definitivePnDesc";
        Integer priority = -1;
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = "Test2";
        String createdBy = "Test1";
        Integer version = 3;
        XhbDefinitivePublicNoticeDao result = new XhbDefinitivePublicNoticeDao(definitivePnId, definitivePnDesc,
            priority, lastUpdateDate, creationDate, lastUpdatedBy, createdBy, version);
        definitivePnId = result.getPrimaryKey();
        assertNotNull(definitivePnId, NOTNULLRESULT);
        return new XhbDefinitivePublicNoticeDao(result);
    }

}
