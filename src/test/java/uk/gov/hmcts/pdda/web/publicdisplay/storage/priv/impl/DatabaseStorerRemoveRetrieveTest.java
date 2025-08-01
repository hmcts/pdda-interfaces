package uk.gov.hmcts.pdda.web.publicdisplay.storage.priv.impl;

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
import uk.gov.hmcts.pdda.business.services.publicdisplay.data.ejb.PdDataControllerBean;
import uk.gov.hmcts.pdda.common.publicdisplay.types.document.DisplayDocumentType;
import uk.gov.hmcts.pdda.common.publicdisplay.types.rotationset.DisplayRotationSetData;
import uk.gov.hmcts.pdda.common.publicdisplay.types.rotationset.RotationSetDisplayDocument;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.DisplayDocumentUri;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.Storeable;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.StoredObject;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.Storer;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.exceptions.ObjectDoesNotExistException;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.exceptions.RemovalException;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.exceptions.RetrievalException;
import uk.gov.hmcts.pdda.web.publicdisplay.types.rotationset.DisplayRotationSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DatabaseStorerRemoveRetrieveTest {

    protected static final String NOTNULL = "Result is Null";

    @Mock
    protected PdDataControllerBean mockPdDataControllerBean;

    @Mock
    protected DisplayStoreControllerBean mockDisplayStoreControllerBean;

    @Mock
    protected Storer mockStorer;

    @InjectMocks
    protected final DatabaseStorer classUnderTest = new DatabaseStorer();

    @BeforeAll
    public static void setUp() {
        // Do nothing
    }

    @AfterAll
    public static void tearDown() {
        // Do nothing
    }

    @Test
    void testRemoveAbstractUri() {
        DisplayDocumentUri displayDocumentUri =
            DummyDisplayDocumentUtil.getUri(DisplayDocumentType.DAILY_LIST);
        Mockito.when(mockDisplayStoreControllerBean.doesEntryExist(Mockito.isA(String.class)))
            .thenReturn(true);
        mockDisplayStoreControllerBean.deleteEntry(Mockito.isA(String.class));

        classUnderTest.remove(mockDisplayStoreControllerBean, displayDocumentUri);
        assertNotNull(displayDocumentUri, NOTNULL);
    }

    @Test
    void testRemoveExeptionFailure() {
        Assertions.assertThrows(RemovalException.class, () -> {
            DisplayDocumentUri displayDocumentUri =
                DummyDisplayDocumentUtil.getUri(DisplayDocumentType.DAILY_LIST);
            Mockito.when(mockDisplayStoreControllerBean.doesEntryExist(Mockito.isA(String.class)))
                .thenReturn(true);
            Mockito.doThrow(RemovalException.class).when(mockDisplayStoreControllerBean)
                .deleteEntry(Mockito.isA(String.class));

            classUnderTest.remove(mockDisplayStoreControllerBean, displayDocumentUri);
        });
    }

    @Test
    void testRemoveStoreable() {
        Storeable storeable = DummyStoredObjectUtil.getStoreable(mockPdDataControllerBean,
            mockDisplayStoreControllerBean, mockStorer);
        Mockito.when(mockDisplayStoreControllerBean.doesEntryExist(Mockito.isA(String.class)))
            .thenReturn(true);
        mockDisplayStoreControllerBean.deleteEntry(Mockito.isA(String.class));

        classUnderTest.remove(mockDisplayStoreControllerBean, storeable);
        assertNotNull(storeable, NOTNULL);
    }


    @Test
    void testRemoveStoreableFailure() {
        Assertions.assertThrows(ObjectDoesNotExistException.class, () -> {
            Storeable storeable = getDisplayRotationSet();
            Mockito.when(mockDisplayStoreControllerBean.doesEntryExist(Mockito.isA(String.class)))
                .thenReturn(false);

            classUnderTest.remove(mockDisplayStoreControllerBean, storeable);
        });
    }

    @Test
    void testRetrieve() {
        String content = "Test Content";
        Mockito.when(mockDisplayStoreControllerBean.doesEntryExist(Mockito.isA(String.class)))
            .thenReturn(true);
        Mockito.when(mockDisplayStoreControllerBean.readFromDatabase(Mockito.isA(String.class)))
            .thenReturn(content);

        StoredObject result = classUnderTest.retrieve(mockDisplayStoreControllerBean,
            DummyDisplayDocumentUtil.getUri(DisplayDocumentType.DAILY_LIST));

        assertEquals(content, result.getText(), "Test Content");
    }

    @Test
    void testRetrieveFailure() {
        Assertions.assertThrows(ObjectDoesNotExistException.class, () -> {
            Mockito.when(mockDisplayStoreControllerBean.doesEntryExist(Mockito.isA(String.class)))
                .thenReturn(false);

            classUnderTest.retrieve(mockDisplayStoreControllerBean,
                DummyDisplayDocumentUtil.getUri(DisplayDocumentType.DAILY_LIST));
        });
    }

    @Test
    void testRetrieveExceptionFailure() {
        Assertions.assertThrows(RetrievalException.class, () -> {
            DisplayDocumentUri displayDocumentUri =
                DummyDisplayDocumentUtil.getUri(DisplayDocumentType.DAILY_LIST);
            Mockito.when(mockDisplayStoreControllerBean.doesEntryExist(Mockito.isA(String.class)))
                .thenReturn(true);
            Mockito.when(mockDisplayStoreControllerBean.readFromDatabase(Mockito.isA(String.class)))
                .thenReturn(null);

            classUnderTest.retrieve(mockDisplayStoreControllerBean, displayDocumentUri);
        });
    }

    protected DisplayRotationSetData getDisplayRotationSetData() {
        return new DisplayRotationSetData(DummyDisplayUtil.getDisplayUriWithParams(),
            new RotationSetDisplayDocument[] {}, 0, 0, "42in");
    }

    protected DisplayRotationSet getDisplayRotationSet() {
        return new DisplayRotationSet(getDisplayRotationSetData(), null);
    }
}
