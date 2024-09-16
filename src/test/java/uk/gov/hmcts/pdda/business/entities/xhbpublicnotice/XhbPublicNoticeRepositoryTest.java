package uk.gov.hmcts.pdda.business.entities.xhbpublicnotice;

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
        if (classUnderTest == null) {
            classUnderTest = new XhbPublicNoticeRepository(getEntityManager());
        }
        return classUnderTest;
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
        assertNotNull(publicNoticeId, NOTNULL);
        result.setXhbDefinitivePublicNotice(result.getXhbDefinitivePublicNotice());
        return new XhbPublicNoticeDao(result);
    }

}
