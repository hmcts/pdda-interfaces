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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    private static final DisplayDocumentType CLASSUNDERTEST = DisplayDocumentType.DAILY_LIST;

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
        CasesRequired result = CLASSUNDERTEST.getCasesRequired();
        assertNotNull(result, NOTNULL);
        assertEquals(getDummyCasesRequired(), result, "Results are not Equal");
    }

    @Test
    void testGetDisplayDocumentTypes() {
        assertNotNull(DisplayDocumentType.getDisplayDocumentTypes("DailyList"), NOTNULL);
        assertNotNull(DisplayDocumentType.getDisplayDocumentTypes("CourtList"), NOTNULL);
        assertNotNull(CLASSUNDERTEST.getLongName(), NOTNULL);
        assertNotNull(CLASSUNDERTEST.toLowerCaseString(), NOTNULL);
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
        assertNotEquals(DisplayDocumentType.DAILY_LIST_CY, CLASSUNDERTEST, "Result is Equal");
        assertEquals(DisplayDocumentType.DAILY_LIST, CLASSUNDERTEST, "Result is not Equal");
        assertNotNull(CLASSUNDERTEST.toString(), NOTNULL);
        assertNotNull(CLASSUNDERTEST.getCountry(), NOTNULL);
    }

    private CasesRequired getDummyCasesRequired() {
        CasesRequired result = CasesRequired.ALL;
        assertNotNull(result.toString(), NOTNULL);
        return result;
    }
}
