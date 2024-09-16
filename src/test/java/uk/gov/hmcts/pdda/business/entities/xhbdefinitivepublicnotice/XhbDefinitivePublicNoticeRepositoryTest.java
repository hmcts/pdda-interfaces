package uk.gov.hmcts.pdda.business.entities.xhbdefinitivepublicnotice;

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
        if (classUnderTest == null) {
            classUnderTest = new XhbDefinitivePublicNoticeRepository(getEntityManager());
        }
        return classUnderTest;
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
        assertNotNull(definitivePnId, NOTNULL);
        return new XhbDefinitivePublicNoticeDao(result);
    }

}
