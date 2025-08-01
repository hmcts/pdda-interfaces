package uk.gov.hmcts.pdda.business.services.publicdisplay.data;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.framework.exception.CsBusinessException;
import uk.gov.hmcts.pdda.common.publicdisplay.data.DataSource;
import uk.gov.hmcts.pdda.common.publicdisplay.data.exceptions.DataSourceException;
import uk.gov.hmcts.pdda.common.publicdisplay.data.exceptions.NoSuchDataSourceException;
import uk.gov.hmcts.pdda.common.publicdisplay.types.document.DisplayDocumentType;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.DisplayDocumentUri;

import java.net.MalformedURLException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings("PMD")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DataSourceFactoryTest {

    private static final String TRUE = "Result is not True";

    private final EntityManager mockEntityManager = Mockito.mock(EntityManager.class);

    private final DisplayDocumentUri mockDisplayDocumentUri = Mockito.mock(DisplayDocumentUri.class);

    @BeforeAll
    public static void setUp() {
        // Do nothing
    }

    @AfterAll
    public static void tearDown() {
        // Do nothing
    }

    private DataSource getClassUnderTest(String simpleDocumentType) {
        Mockito.when(mockDisplayDocumentUri.getSimpleDocumentType()).thenReturn(simpleDocumentType);
        Mockito.when(mockDisplayDocumentUri.isUnassignedRequired()).thenReturn(true);
        return DataSourceFactory.getDataSource(mockEntityManager, mockDisplayDocumentUri);
    }

    @Test
    void testGetDataSourceSuccess() {
        boolean result = false;
        try {
            DisplayDocumentType docType = DisplayDocumentType.ALL_COURT_STATUS;
            getClassUnderTest(docType.getShortName());
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }

    @Test
    void testGetDataSourceFailure() {
        Assertions.assertThrows(NoSuchDataSourceException.class, () -> {
            getClassUnderTest("");
        });
    }

    @Test
    void testDataSourceException() throws MalformedURLException {
        Assertions.assertThrows(DataSourceException.class, () -> {
            throw new DataSourceException("Test");
        });
        Assertions.assertThrows(DataSourceException.class, () -> {

            throw new DataSourceException(new CsBusinessException());
        });
    }
}
