package uk.gov.hmcts.pdda.business.entities.xhbpublicnotice;

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
class XhbPublicNoticeRepositoryTest extends AbstractRepositoryTest<XhbPublicNoticeDao> {

    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private XhbPublicNoticeRepository classUnderTest;

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbPublicNoticeRepository getClassUnderTest() {
        return classUnderTest;
    }

    @BeforeEach
    void setup() {
        classUnderTest = new XhbPublicNoticeRepository(mockEntityManager);
    }

    @Test
    void testFindByIdSuccess() {
        try (MockedStatic<EntityManagerUtil> mockedStatic =
            Mockito.mockStatic(EntityManagerUtil.class)) {
            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);

            XhbPublicNoticeDao dummyDao = getDummyDao();
            Mockito.when(mockEntityManager.find(XhbPublicNoticeDao.class, getDummyId()))
                .thenReturn(dummyDao);

            boolean result = runFindByIdTest(dummyDao);
            assertTrue(result, NOT_TRUE);
        }
    }

    @Override
    protected XhbPublicNoticeDao getDummyDao() {
        Integer publicNoticeId = getDummyId();
        String publicNoticeDesc = "publicNoticeDesc";
        Integer courtId = -1;
        Integer definitivePnId = getDummyId();
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = "Test2";
        String createdBy = "Test1";
        Integer version = 3;
        XhbPublicNoticeDao result = new XhbPublicNoticeDao(publicNoticeId, publicNoticeDesc, courtId, lastUpdateDate,
            creationDate, lastUpdatedBy, createdBy, version, definitivePnId);
        publicNoticeId = result.getPrimaryKey();
        assertNotNull(publicNoticeId, NOTNULLRESULT);
        result.setXhbDefinitivePublicNotice(result.getXhbDefinitivePublicNotice());
        return new XhbPublicNoticeDao(result);
    }

}
