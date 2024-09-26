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
import uk.gov.hmcts.DummyStoredObjectUtil;
import uk.gov.hmcts.pdda.business.services.publicdisplay.data.ejb.PdDataControllerBean;
import uk.gov.hmcts.pdda.common.publicdisplay.types.document.DisplayDocumentType;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.Storeable;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.Storer;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.exceptions.DocumentNotYetRenderedException;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.exceptions.StoreException;
import uk.gov.hmcts.pdda.web.publicdisplay.types.document.DisplayDocument;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * <p>
 * Title: DatabaseStorer Test.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2022
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @author Chris Vincent
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DatabaseStorerTest {

    protected static final String NOTNULL = "Result is Null";
    protected static final String TRUE = "Result is not True";

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
    void testGetInstance() {
        boolean result = false;
        try {
            DatabaseStorer.getInstance();
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }

    @Test
    void testStore() {
        Storeable storeable =
            DummyStoredObjectUtil.getStoreable(mockPdDataControllerBean, mockDisplayStoreControllerBean, mockStorer);
        mockDisplayStoreControllerBean.writeToDatabase(Mockito.isA(String.class), Mockito.isA(String.class));

        classUnderTest.store(mockDisplayStoreControllerBean, storeable);
        assertNotNull(storeable, NOTNULL);
    }

    @Test
    void testStoreExceptionFailure() {
        Assertions.assertThrows(StoreException.class, () -> {
            Storeable storeable = DummyStoredObjectUtil.getStoreable(mockPdDataControllerBean,
                mockDisplayStoreControllerBean, mockStorer);
            Mockito.doThrow(StoreException.class).when(mockDisplayStoreControllerBean)
                .writeToDatabase(Mockito.isA(String.class), Mockito.isA(String.class));

            classUnderTest.store(mockDisplayStoreControllerBean, storeable);
        });
    }

    @Test
    void testStoreNoRenderedString() {
        Assertions.assertThrows(DocumentNotYetRenderedException.class, () -> {
            DisplayDocument storeable = (DisplayDocument) DummyStoredObjectUtil.getStoreable(mockPdDataControllerBean,
                mockDisplayStoreControllerBean, mockStorer);
            storeable.setRenderedString(null);

            classUnderTest.store(mockDisplayStoreControllerBean, storeable);
        });
    }

    @Test
    void testLastModified() {
        Long lastModified = Long.valueOf(12_345);
        Mockito.when(mockDisplayStoreControllerBean.doesEntryExist(Mockito.isA(String.class))).thenReturn(true);
        Mockito.when(mockDisplayStoreControllerBean.getLastModified(Mockito.isA(String.class)))
            .thenReturn(lastModified);

        Long result = classUnderTest.lastModified(mockDisplayStoreControllerBean,
            DummyDisplayDocumentUtil.getUri(DisplayDocumentType.DAILY_LIST));

        assertEquals(lastModified, result, "Results are not Equal");
    }

    @Test
    void testLastModifiedFailure() {
        Mockito.when(mockDisplayStoreControllerBean.doesEntryExist(Mockito.isA(String.class))).thenReturn(false);

        Long result = classUnderTest.lastModified(mockDisplayStoreControllerBean,
            DummyDisplayDocumentUtil.getUri(DisplayDocumentType.DAILY_LIST));

        assertNotNull(result, NOTNULL);
    }
}
