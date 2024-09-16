package uk.gov.hmcts.pdda.web.publicdisplay.types.document;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.slf4j.Logger;
import uk.gov.hmcts.pdda.business.services.publicdisplay.data.ejb.PdDataControllerBean;
import uk.gov.hmcts.pdda.common.publicdisplay.types.document.CasesRequired;
import uk.gov.hmcts.pdda.common.publicdisplay.types.document.DisplayDocumentType;
import uk.gov.hmcts.pdda.common.publicdisplay.types.document.DisplayDocumentTypeUtils;
import uk.gov.hmcts.pdda.common.publicdisplay.types.document.exceptions.NoSuchDocumentTypeException;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.priv.impl.DisplayStoreControllerBean;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.Storer;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DisplayDocumentTypeTest {

    private static final String NOTNULL = "Result is Null";

    @Mock
    private PdDataControllerBean mockPdDataControllerBean;

    @Mock
    private DisplayStoreControllerBean mockDisplayStoreControllerBean;

    @Mock
    private Storer mockStorer;

    @Mock
    private Logger mockLogger;

    @InjectMocks
    private final DisplayDocumentType classUnderTest = DisplayDocumentType.DAILY_LIST;

    @BeforeAll
    public static void setUp() {
        // Do nothing
    }

    @AfterAll
    public static void tearDown() {
        // Do nothing
    }

    @Test
    void testGetCasesRequired() {
        CasesRequired result = classUnderTest.getCasesRequired();
        assertNotNull(result, NOTNULL);
        assertEquals(getDummyCasesRequired(), result, "Results are not Equal");
    }

    @Test
    void testGetDisplayDocumentTypes() {
        assertNotNull(DisplayDocumentType.getDisplayDocumentTypes("DailyList"), NOTNULL);
        assertNotNull(DisplayDocumentType.getDisplayDocumentTypes("CourtList"), NOTNULL);
        assertNotNull(classUnderTest.getLongName(), NOTNULL);
        assertNotNull(classUnderTest.toLowerCaseString(), NOTNULL);
        assertNotNull(classUnderTest.hashCode(), NOTNULL);
    }

    @Test
    void testGetDisplayDocumentTypeFailure() {
        Assertions.assertThrows(NoSuchDocumentTypeException.class, () -> {
            // Run
            DisplayDocumentTypeUtils.getDisplayDocumentType("DocumentId", Locale.FRANCE.getLanguage(), null);
        });
        Assertions.assertThrows(NoSuchDocumentTypeException.class, () -> {
            // Run
            DisplayDocumentTypeUtils.getDisplayDocumentType("DocumentId", Locale.FRANCE.getLanguage(),
                Locale.FRANCE.getCountry());
        });
        Assertions.assertThrows(NoSuchDocumentTypeException.class, () -> {
            // Run
            DisplayDocumentTypeUtils.getDisplayDocumentType("DocumentId");
        });
    }

    @Test
    void testEquals() {
        assertFalse(Boolean.valueOf(classUnderTest.equals(DisplayDocumentType.DAILY_LIST_CY)), "Result is not False");
        assertTrue(Boolean.valueOf(classUnderTest.equals(DisplayDocumentType.DAILY_LIST)), "Result is not True");
        assertNotNull(classUnderTest.toString(), NOTNULL);
        assertNotNull(classUnderTest.getCountry(), NOTNULL);
    }

    private CasesRequired getDummyCasesRequired() {
        CasesRequired result = CasesRequired.ALL;
        assertNotNull(result.toString(), NOTNULL);
        assertNotNull(result.hashCode(), NOTNULL);
        return result;
    }
}
