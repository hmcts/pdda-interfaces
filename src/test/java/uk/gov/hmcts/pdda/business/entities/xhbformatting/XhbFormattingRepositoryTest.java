package uk.gov.hmcts.pdda.business.entities.xhbformatting;

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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class XhbFormattingRepositoryTest extends AbstractRepositoryTest<XhbFormattingDao> {

    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private XhbFormattingRepository classUnderTest;

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbFormattingRepository getClassUnderTest() {
        return classUnderTest;
    }

    @BeforeEach
    void setup() {
        classUnderTest = new XhbFormattingRepository(mockEntityManager);
    }

    @Test
    void testFindByIdSuccess() {
        try (MockedStatic<EntityManagerUtil> mockedStatic =
            Mockito.mockStatic(EntityManagerUtil.class)) {
            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);

            XhbFormattingDao dummyDao = getDummyDao();
            Mockito.when(mockEntityManager.find(XhbFormattingDao.class, getDummyId()))
                .thenReturn(dummyDao);

            boolean result = runFindByIdTest(dummyDao);
            assertTrue(result, NOT_TRUE);
        }
    }

    @Test
    void testFindByFormatStatusSuccess() {
        boolean result = testFindByFormatStatus(getDummyDao());
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testFindByFormatStatusFailure() {
        boolean result = testFindByFormatStatus(null);
        assertTrue(result, NOT_TRUE);
    }

    private boolean testFindByFormatStatus(XhbFormattingDao dao) {
        List<XhbFormattingDao> list = new ArrayList<>();
        if (dao != null) {
            list.add(dao);
        }
        Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(list);
        List<XhbFormattingDao> result =
            getClassUnderTest().findByFormatStatus(getDummyDao().getFormatStatus());
        assertNotNull(result, "Result is Null");
        if (dao != null) {
            assertSame(dao, result.get(0), NOTSAMERESULT);
        } else {
            assertSame(0, result.size(), NOTSAMERESULT);
        }
        return true;
    }

    @Test
    void testFindByDocumentAndClobSuccess() {
        boolean result = testFindByDocumentAndClob(getDummyDao());
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testFindByDocumentAndClobFailure() {
        boolean result = testFindByDocumentAndClob(null);
        assertTrue(result, NOT_TRUE);
    }

    private boolean testFindByDocumentAndClob(XhbFormattingDao dao) {
        List<XhbFormattingDao> list = new ArrayList<>();
        if (dao != null) {
            list.add(dao);
        }
        Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(list);
        List<XhbFormattingDao> result =
            getClassUnderTest().findByDocumentAndClob(getDummyDao().getCourtId(),
                getDummyDao().getDocumentType(), getDummyDao().getLanguage(), "courtSiteName");
        assertNotNull(result, "Result is Null");
        if (dao != null) {
            assertSame(dao, result.get(0), NOTSAMERESULT);
        } else {
            assertSame(0, result.size(), NOTSAMERESULT);
        }
        return true;
    }

    @Override
    protected XhbFormattingDao getDummyDao() {
        Integer formattingId = getDummyId();
        LocalDateTime dateIn = LocalDateTime.now();
        String formatStatus = "formatStatus";
        String distributionType = "distributionType";
        String mimType = "mimType";
        String documentType = "documentType";
        Integer courtId = -1;
        Long formattedDocumentBlobId = getDummyLongId();
        Long xmlDocumentClobId = getDummyLongId();
        String language = "language";
        String country = "country";
        Integer majorSchemaVersion = -1;
        Integer minorSchemaVersion = -1;
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = "Test2";
        String createdBy = "Test1";
        Integer version = 3;
        XhbFormattingDao result = new XhbFormattingDao();
        result.setFormattingId(formattingId);
        result.setDateIn(dateIn);
        result.setFormatStatus(formatStatus);
        result.setDistributionType(distributionType);
        result.setMimeType(mimType);
        result.setDocumentType(documentType);
        result.setCourtId(courtId);
        result.setFormattedDocumentBlobId(formattedDocumentBlobId);
        result.setXmlDocumentClobId(xmlDocumentClobId);
        result.setLanguage(language);
        result.setCountry(country);
        result.setMajorSchemaVersion(majorSchemaVersion);
        result.setMinorSchemaVersion(minorSchemaVersion);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);
        formattingId = result.getPrimaryKey();
        assertNotNull(formattingId, NOTNULLRESULT);
        return new XhbFormattingDao(result);
    }

}
