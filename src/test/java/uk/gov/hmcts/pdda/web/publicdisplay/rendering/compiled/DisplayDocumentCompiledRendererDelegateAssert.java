package uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled;

import org.mockito.Mockito;
import uk.gov.hmcts.DummyDisplayDocumentUtil;
import uk.gov.hmcts.framework.services.TranslationServices;
import uk.gov.hmcts.pdda.business.services.publicdisplay.data.ejb.PdDataControllerBean;
import uk.gov.hmcts.pdda.business.vos.translation.TranslationBundle;
import uk.gov.hmcts.pdda.business.vos.translation.TranslationEnum;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.PublicDisplayValue;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.priv.impl.DisplayStoreControllerBean;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.Storeable;
import uk.gov.hmcts.pdda.web.publicdisplay.types.document.DisplayDocument;

import java.util.Collection;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public final class DisplayDocumentCompiledRendererDelegateAssert {

    private static final String NOTNULL = "Result is Null";
    private static final String EQUALS = "Results are not Equal";
    private static final Locale DUMMY_LOCALE = new Locale("en", "GB");

    private DisplayDocumentCompiledRendererDelegateAssert() {
        // Do nothing
    }

    public static boolean testCompiledRendererDelegate(
        DisplayDocumentCompiledRendererDelegate classUnderTest,
        Collection<PublicDisplayValue> table, PdDataControllerBean mockPdDataControllerBean,
        DisplayStoreControllerBean mockDisplayStoreControllerBean,
        TranslationServices mockTranslationServices, TranslationBundle mockTranslationBundle) {
        // Setup
        DisplayDocument displayDocument = DummyDisplayDocumentUtil.getDisplayDocument(table,
            mockPdDataControllerBean, mockDisplayStoreControllerBean);
        Mockito
            .when(mockTranslationServices
                .getTranslationBundle(TranslationEnum.PUBLICDISPLAYDOCUMENTRESOURCES, DUMMY_LOCALE))
            .thenReturn(mockTranslationBundle);
        String result = classUnderTest.getDisplayDocumentHtml(displayDocument);

        assertNotNull(result, NOTNULL);
        assertEquals(Storeable.DOCUMENT, displayDocument.getStoreableType(), EQUALS);
        assertFalse(displayDocument.equals(DummyDisplayDocumentUtil.getDisplayDocument(table,
            mockPdDataControllerBean, mockDisplayStoreControllerBean)), "Result is not False");
        return true;
    }
}
