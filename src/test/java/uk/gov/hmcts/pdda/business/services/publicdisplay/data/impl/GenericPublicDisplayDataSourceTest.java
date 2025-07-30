package uk.gov.hmcts.pdda.business.services.publicdisplay.data.impl;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.framework.business.exceptions.CourtNotFoundException;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.query.PublicDisplayQuery;
import uk.gov.hmcts.pdda.common.publicdisplay.types.document.DisplayDocumentType;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.DisplayDocumentUri;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GenericPublicDisplayDataSourceTest {

    private static final String TRUE = "Result is not True";
    
    private final EntityManager mockEntityManager = Mockito.mock(EntityManager.class);

    private final DisplayDocumentUri mockDisplayDocumentUri = Mockito.mock(DisplayDocumentUri.class);

    private final PublicDisplayQuery mockPublicDisplayQuery = Mockito.mock(PublicDisplayQuery.class);

    private final XhbCourtRepository mockXhbCourtRepository = Mockito.mock(XhbCourtRepository.class);

    @InjectMocks
    private final GenericPublicDisplayDataSource classUnderTest =
        new GenericPublicDisplayDataSource(mockDisplayDocumentUri, mockPublicDisplayQuery, mockXhbCourtRepository);

    @BeforeAll
    public static void setUp() {
        // Do nothing
    }

    @AfterAll
    public static void tearDown() {
        // Do nothing
    }

    @Test
    void testRetrieveSuccess() {
        try (MockedStatic<EntityManagerUtil> staticMock = mockStatic(EntityManagerUtil.class)) {
            staticMock.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);
            boolean result = testRetrieve(Optional.of(getDummyXhbCourtDao()));
            assertTrue(result, TRUE);
        }
    }

    @Test
    void testRetrieveNoCourt() {
        try (MockedStatic<EntityManagerUtil> staticMock = mockStatic(EntityManagerUtil.class)) {
            staticMock.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);
            Assertions.assertThrows(CourtNotFoundException.class, () -> {
                testRetrieve(Optional.empty());
            });
        }
    }

    private boolean testRetrieve(Optional<XhbCourtDao> xhbCourtDao) {
        Mockito.when(mockDisplayDocumentUri.getDocumentType()).thenReturn(DisplayDocumentType.ALL_COURT_STATUS);
        Mockito.when(mockXhbCourtRepository.findByIdSafe(Mockito.isA(Integer.class)))
            .thenReturn(xhbCourtDao);
        classUnderTest.retrieve(mockEntityManager);
        return true;
    }

    private XhbCourtDao getDummyXhbCourtDao() {
        XhbCourtDao result = new XhbCourtDao();
        result.setCourtId(-1);
        result.setCrestCourtId("456");
        return new XhbCourtDao(result);
    }
}
