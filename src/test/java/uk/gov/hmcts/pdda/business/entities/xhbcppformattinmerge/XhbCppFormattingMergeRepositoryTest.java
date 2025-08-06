package uk.gov.hmcts.pdda.business.entities.xhbcppformattinmerge;

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
import uk.gov.hmcts.pdda.business.entities.xhbcppformattingmerge.XhbCppFormattingMergeDao;
import uk.gov.hmcts.pdda.business.entities.xhbcppformattingmerge.XhbCppFormattingMergeRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class XhbCppFormattingMergeRepositoryTest extends AbstractRepositoryTest<XhbCppFormattingMergeDao> {

    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private XhbCppFormattingMergeRepository classUnderTest;

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbCppFormattingMergeRepository getClassUnderTest() {
        return classUnderTest;
    }

    @BeforeEach
    void setup() {
        classUnderTest = new XhbCppFormattingMergeRepository(mockEntityManager);
    }

    @Test
    void testFindByIdSuccess() {
        try (MockedStatic<EntityManagerUtil> mockedStatic = mockStatic(EntityManagerUtil.class)) {
            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);

            XhbCppFormattingMergeDao dummyDao = getDummyDao();
            Mockito.when(mockEntityManager.find(XhbCppFormattingMergeDao.class, getDummyId()))
                .thenReturn(dummyDao);

            boolean result = runFindByIdTest(dummyDao);
            assertTrue(result, NOT_TRUE);
        }
    }

    @Override
    protected XhbCppFormattingMergeDao getDummyDao() {
        Integer cppFormattingMergeId = 99;
        Integer cppCppFormattingId = -1;
        Integer formattingId = -1;
        Integer courtId = 81;
        Long xhibitClobId = getDummyLongId();
        String language = "language";
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = "Test2";
        String createdBy = "Test1";
        Integer version = 3;
        String obsInd = "N";
        XhbCppFormattingMergeDao result = new XhbCppFormattingMergeDao();
        result.setCppFormattingMergeId(cppFormattingMergeId);
        result.setCppFormattingId(cppCppFormattingId);
        result.setFormattingId(formattingId);
        result.setCourtId(courtId);
        result.setXhibitClobId(xhibitClobId);
        result.setLanguage(language);
        result.setObsInd(obsInd);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);
        cppFormattingMergeId = result.getPrimaryKey();
        assertNotNull(cppFormattingMergeId, NOTNULLRESULT);
        return new XhbCppFormattingMergeDao(result);
    }

}
