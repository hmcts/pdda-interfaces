package uk.gov.hmcts.pdda.web.publicdisplay.storage.priv.impl;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.DummyStoredObjectUtil;
import uk.gov.hmcts.framework.exception.CsBusinessException;
import uk.gov.hmcts.pdda.business.services.publicdisplay.data.ejb.PdDataControllerBean;
import uk.gov.hmcts.pdda.common.publicdisplay.data.Data;
import uk.gov.hmcts.pdda.common.publicdisplay.exceptions.PublicDisplayFailureException;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.DisplayDocumentUri;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.Storeable;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.Storer;
import uk.gov.hmcts.pdda.web.publicdisplay.types.document.DisplayDocument;

import java.net.URLEncoder;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("PMD")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DatabaseStorerDisplayTest {

    protected static final String NOTNULL = "Result is Null";
    protected static final String TRUE = "Result is not True";
    @Mock
    protected PdDataControllerBean mockPdDataControllerBean;

    @Mock
    protected DisplayStoreControllerBean mockDisplayStoreControllerBean;

    @Mock
    protected Storer mockStorer;

    @BeforeAll
    public static void setUp() {
        // Do nothing
    }

    @AfterAll
    public static void tearDown() {
        // Do nothing
    }

    @Test
    @SuppressWarnings("deprecation")
    void testDisplayDocumentCreate() {
        // Expects
        Mockito.mockStatic(URLEncoder.class);
        Mockito.when(URLEncoder.encode(Mockito.isA(String.class))).thenReturn("test");
        Mockito.when(mockPdDataControllerBean.getData(Mockito.isA(DisplayDocumentUri.class))).thenReturn(new Data());
        // Setup
        DisplayDocument storeable = (DisplayDocument) DummyStoredObjectUtil.getStoreable(mockPdDataControllerBean,
            mockDisplayStoreControllerBean, mockStorer);

        // Run
        storeable.create();
        storeable.getData();
        assertNotNull(storeable, NOTNULL);
    }

    @Test
    void testDisplayDocumentStore() {
        // Setup
        DisplayDocument storeable = (DisplayDocument) DummyStoredObjectUtil.getStoreable(mockPdDataControllerBean,
            mockDisplayStoreControllerBean, mockStorer);
        // Expects
        mockStorer.store(Mockito.isA(DisplayStoreControllerBean.class), Mockito.isA(Storeable.class));
        // Run
        storeable.store();
        assertNotNull(storeable, NOTNULL);
    }

    @Test
    void testDisplayDocumentRemove() {
        // Setup
        DisplayDocument storeable = (DisplayDocument) DummyStoredObjectUtil.getStoreable(mockPdDataControllerBean,
            mockDisplayStoreControllerBean, mockStorer);
        // Expects
        mockStorer.remove(Mockito.isA(DisplayStoreControllerBean.class), Mockito.isA(Storeable.class));
        // Run
        storeable.remove();
        assertNotNull(storeable, NOTNULL);
    }

    @Test
    void testDisplayDocumentEquals() {
        DisplayDocument storeable = (DisplayDocument) DummyStoredObjectUtil.getStoreable(mockPdDataControllerBean,
            mockDisplayStoreControllerBean, mockStorer);
        storeable.getStoreableType();
        boolean isEqual = storeable.equals(
            DummyStoredObjectUtil.getStoreable(mockPdDataControllerBean, mockDisplayStoreControllerBean, mockStorer));
        assertTrue(isEqual, "Result is not True");
    }

    @Test
    void testPublicDisplayFailureException() {
        Assertions.assertThrows(PublicDisplayFailureException.class, () -> {
            throw new PublicDisplayFailureException(new CsBusinessException());
        });
        Assertions.assertThrows(PublicDisplayFailureException.class, () -> {
            throw new PublicDisplayFailureException("Test");
        });
        Assertions.assertThrows(PublicDisplayFailureException.class, () -> {
            throw new PublicDisplayFailureException("Test", new CsBusinessException());
        });
    }

}
