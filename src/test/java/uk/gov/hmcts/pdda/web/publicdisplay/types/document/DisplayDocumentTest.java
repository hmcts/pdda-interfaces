package uk.gov.hmcts.pdda.web.publicdisplay.types.document;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.slf4j.Logger;
import uk.gov.hmcts.DummyDataUtil;
import uk.gov.hmcts.DummyDisplayDocumentUtil;
import uk.gov.hmcts.DummyStoredObjectUtil;
import uk.gov.hmcts.pdda.business.services.publicdisplay.data.ejb.PdDataControllerBean;
import uk.gov.hmcts.pdda.common.publicdisplay.types.document.DisplayDocumentType;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.DisplayDocumentUri;
import uk.gov.hmcts.pdda.web.publicdisplay.rendering.exceptions.DocumentHasNotHadDataAddedException;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.priv.impl.DisplayStoreControllerBean;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.StoredObject;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.Storer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings("PMD")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DisplayDocumentTest {

    private static final String EQUALS = "Results are not Equal";
    private static final String TRUE = "Result is not True";
    
    private DisplayStoreControllerBean mockDisplayStoreControllerBean;
    
    private PdDataControllerBean mockPdDataControllerBean;

    private Storer mockStorer;

    private Logger mockLogger;

    private final DisplayDocument classUnderTest = getClassUnderTest();

    @BeforeAll
    public static void setUp() {
        // Do nothing
    }

    @AfterAll
    public static void tearDown() {
        // Do nothing
    }

    private DisplayDocument getClassUnderTest() {
        mockDisplayStoreControllerBean = Mockito.mock(DisplayStoreControllerBean.class);
        mockPdDataControllerBean = Mockito.mock(PdDataControllerBean.class);
        mockStorer = Mockito.mock(Storer.class);
        mockLogger = Mockito.mock(Logger.class);
        
        return new DisplayDocument(DummyDisplayDocumentUtil.getUri(DisplayDocumentType.DAILY_LIST),
            mockPdDataControllerBean, mockDisplayStoreControllerBean, mockStorer);
    }

    @Test
    void testRemove() {
        mockStorer.remove(mockDisplayStoreControllerBean, classUnderTest);
        boolean result = false;
        try {
            classUnderTest.remove();
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }

    @Test
    void testRender() {
        Assertions.assertThrows(DocumentHasNotHadDataAddedException.class, () -> {
            classUnderTest.render();
        });
    }

    @Test
    void testRetrieve() {
        StoredObject storedObject = DummyStoredObjectUtil.getStoredObject();
        Mockito.when(mockStorer.retrieve(mockDisplayStoreControllerBean, classUnderTest.getUri()))
            .thenReturn(storedObject);

        StoredObject result = classUnderTest.retrieve();

        assertEquals(storedObject, result, EQUALS);
    }

    @Test
    void testStore() {
        mockStorer.store(mockDisplayStoreControllerBean, classUnderTest);
        boolean result = false;
        try {
            classUnderTest.store();
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }

    @Test
    void testDebug() {
        // Setup
        Mockito.when(mockPdDataControllerBean.getData((DisplayDocumentUri) classUnderTest.getUri()))
            .thenReturn(DummyDataUtil.getData());
        Mockito.when(mockLogger.isDebugEnabled()).thenReturn(true);
        mockLogger.debug(Mockito.isA(String.class));
        String dummyRenderedString = "Rendered String";
        // Run
        classUnderTest.fetchData();
        classUnderTest.setRenderedString(dummyRenderedString);
        classUnderTest.debug(mockLogger);

        assertEquals(dummyRenderedString, classUnderTest.getRenderedString(), EQUALS);
    }

    @Test
    void testDebugOff() {
        // Setup
        Mockito.when(mockLogger.isDebugEnabled()).thenReturn(false);
        // Run
        boolean result = false;
        try {
            classUnderTest.debug(mockLogger);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }

    @Test
    void testStoredObjectDebugSucess() {
        StoredObject storedObject = DummyStoredObjectUtil.getStoredObject();
        Mockito.when(mockLogger.isDebugEnabled()).thenReturn(true);
        boolean result = false;
        try {
            mockLogger.debug(Mockito.isA(String.class));
            storedObject.debug(mockLogger);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }

    @Test
    void testStoredObjectDebugFailure() {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            text.append("1234567890");
        }
        text.append('.');
        StoredObject storedObject = new StoredObject(text.toString());
        Mockito.when(mockLogger.isDebugEnabled()).thenReturn(true);
        boolean result = false;
        try {
            mockLogger.debug(Mockito.isA(String.class));
            storedObject.debug(mockLogger);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }
}
