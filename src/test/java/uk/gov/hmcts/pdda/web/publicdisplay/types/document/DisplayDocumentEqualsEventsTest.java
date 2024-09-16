package uk.gov.hmcts.pdda.web.publicdisplay.types.document;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.DummyDataUtil;
import uk.gov.hmcts.DummyDisplayDocumentUtil;
import uk.gov.hmcts.pdda.business.services.publicdisplay.data.ejb.PdDataControllerBean;
import uk.gov.hmcts.pdda.common.publicdisplay.types.document.DisplayDocumentType;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.DisplayDocumentUri;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.priv.impl.DatabaseStorer;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.priv.impl.DisplayStoreControllerBean;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.Storer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DisplayDocumentEqualsEventsTest {

    private static final String TRUE = "Result is not True";

    @Mock
    private PdDataControllerBean mockPdDataControllerBean;

    @Mock
    private DisplayStoreControllerBean mockDisplayStoreControllerBean;

    @Mock
    private Storer mockStorer;

    @InjectMocks
    private final DisplayDocument classUnderTest =
        new DisplayDocument(DummyDisplayDocumentUtil.getUri(DisplayDocumentType.DAILY_LIST),
            DummyDataUtil.getDataDelegate(), mockDisplayStoreControllerBean, mockStorer);

    @BeforeAll
    public static void setUp() {
        // Do nothing
    }

    @AfterAll
    public static void tearDown() {
        // Do nothing
    }

    @Test
    void testEqualsSuccess() {
        DisplayDocument clone =
            new DisplayDocument(DummyDisplayDocumentUtil.getUri(DisplayDocumentType.DAILY_LIST),
                mockPdDataControllerBean, mockDisplayStoreControllerBean, mockStorer);
        // Run
        boolean result = classUnderTest.equals(clone);
        // Checks
        assertTrue(result, TRUE);
    }

    @Test
    void testEqualsInvalidUriFailure() {
        DisplayDocumentUri dummyUri =
            DummyDisplayDocumentUtil.getUri(DisplayDocumentType.COURT_LIST);
        DisplayDocument another = new DisplayDocument(dummyUri, mockPdDataControllerBean,
            mockDisplayStoreControllerBean, mockStorer);
        // Run
        boolean result = classUnderTest.equals(another);
        // Checks
        assertFalse(result, "Result is not False");
    }

    @Test
    void testEqualsTypeFailure() {
        // Run
        boolean result = classUnderTest.equals(new DatabaseStorer());
        // Checks
        assertFalse(result, "Result is not False");
    }
}
