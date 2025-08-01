package uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.DummyDisplayDocumentUtil;
import uk.gov.hmcts.DummyDisplayUtil;
import uk.gov.hmcts.DummyStoredObjectUtil;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.AbstractUri;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.UriFactory;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.exceptions.InvalidUriFormatException;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.priv.impl.DisplayStoreControllerBean;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.StoredObject;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.Storer;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.StorerFactory;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.exceptions.ObjectDoesNotExistException;

import java.io.IOException;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings("PMD")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FileServletTest {

    private static final String EQUALS = "Results are not Equal";
    private static final String TRUE = "Result is not True";
    private static final String HEADER = "Header";
    private static final String DISPLAY_URL = "pd://display/snaresbrook/453/reception/mainscreen";
    private static final String DOCUMENT_URL = "pd://document:81/DailyList:";

    @Mock
    private HttpServletRequest mockHttpServletRequest;

    @Mock
    private HttpServletResponse mockHttpServletResponse;

    @Mock
    private Storer mockStorer;

    @Mock
    private PrintWriter mockPrintWriter;

    @InjectMocks
    private final FileServlet classUnderTest = new FileServlet();

    @BeforeAll
    public static void setUp() {
        Mockito.mockStatic(StorerFactory.class);
        Mockito.mockStatic(UriFactory.class);
    }

    @AfterAll
    public static void tearDown() {
        Mockito.clearAllCaches();
    }

    @Test
    void testDoGetNoHeader() throws ServletException, IOException {
        Assertions.assertThrows(InvalidUriFormatException.class, () -> {
            testDoGet(null, null, null, null);
        });
    }

    @Test
    void testDoGetNoUri() throws ServletException, IOException {
        Assertions.assertThrows(InvalidUriFormatException.class, () -> {
            testDoGet(HEADER, null, null, null);
        });
    }

    @Test
    void testInit() throws ServletException, IOException {
        boolean result = false;
        try {
            classUnderTest.init();
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }

    @Test
    void testDoGetOutcomes() throws ServletException, IOException {
        boolean result = testDoGet(HEADER, DISPLAY_URL, DummyDisplayUtil.getDisplayUri(), null);
        assertTrue(result, TRUE);

        result = testDoGet(HEADER, DISPLAY_URL, DummyDisplayUtil.getDisplayUri(),
            DummyStoredObjectUtil.getStoredObject());
        assertTrue(result, TRUE);

        result = testDoGet(HEADER, DOCUMENT_URL, DummyDisplayDocumentUtil.getDisplayDocumentUri(),
            DummyStoredObjectUtil.getStoredObject());
        assertTrue(result, TRUE);
    }

    private boolean testDoGet(String header, String url, AbstractUri uri, StoredObject storedObject)
        throws ServletException, IOException {
        // Expects
        Mockito.when(mockHttpServletRequest.getHeader(Mockito.isA(String.class)))
            .thenReturn(header);
        if (header != null) {
            Mockito.when(mockHttpServletRequest.getParameter(Mockito.isA(String.class)))
                .thenReturn(url);
        }
        if (header != null && uri != null) {
            Mockito.when(UriFactory.create(url)).thenReturn(uri);
            Mockito.when(StorerFactory.getInstance()).thenReturn(mockStorer);
            if (storedObject == null) {
                Mockito.doThrow(ObjectDoesNotExistException.class).when(mockStorer).retrieve(
                    Mockito.isA(DisplayStoreControllerBean.class), Mockito.isA(AbstractUri.class));
            } else {
                Mockito.when(mockStorer.retrieve(Mockito.isA(DisplayStoreControllerBean.class),
                    Mockito.isA(AbstractUri.class))).thenReturn(storedObject);

                Mockito.when(mockHttpServletResponse.getWriter()).thenReturn(mockPrintWriter);
            }
        }
        // Run
        classUnderTest.doGet(mockHttpServletRequest, mockHttpServletResponse);
        return true;
    }

    @Test
    void testGetLastModifiedOutcomes() {
        boolean result = testgetLastModified(null, null);
        assertTrue(result, TRUE);

        result = testgetLastModified(DISPLAY_URL, DummyDisplayUtil.getDisplayUri());
        assertTrue(result, TRUE);
    }

    private boolean testgetLastModified(String url, AbstractUri uri) {
        // Setup
        long lastModified = url != null ? 1L : Long.MAX_VALUE;
        // Expects
        Mockito.when(mockHttpServletRequest.getParameter(Mockito.isA(String.class)))
            .thenReturn(url);
        if (url != null) {
            Mockito.when(UriFactory.create(url)).thenReturn(uri);
            Mockito.when(StorerFactory.getInstance()).thenReturn(mockStorer);
            Mockito.when(mockStorer.lastModified(Mockito.isA(DisplayStoreControllerBean.class),
                Mockito.isA(AbstractUri.class))).thenReturn(lastModified);
        }
        // Run
        long result = classUnderTest.getLastModified(mockHttpServletRequest);
        // Checks
        assertEquals(lastModified, result, EQUALS);
        return true;
    }
}
