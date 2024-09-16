package uk.gov.hmcts;

import uk.gov.hmcts.pdda.business.services.publicdisplay.data.ejb.PdDataControllerBean;
import uk.gov.hmcts.pdda.common.publicdisplay.data.Data;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.PublicDisplayValue;
import uk.gov.hmcts.pdda.common.publicdisplay.types.document.DisplayDocumentType;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.DisplayDocumentUri;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.priv.impl.DisplayStoreControllerBean;
import uk.gov.hmcts.pdda.web.publicdisplay.types.document.DisplayDocument;

import java.util.Collection;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public final class DummyDisplayDocumentUtil {

    private static final String EQUALS = "Results are not Equal";

    private DummyDisplayDocumentUtil() {
        // Do nothing
    }

    public static DisplayDocumentUri getDisplayDocumentUri(String documentUrl) {
        DisplayDocumentUri result = new DisplayDocumentUri(documentUrl);
        assertEquals("document", result.getType(), EQUALS);
        return result;
    }

    public static DisplayDocumentUri getDisplayDocumentUri() {
        Integer courtId = 10;
        int[] courtRoomIds = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        return new DisplayDocumentUri(Locale.UK, courtId, DisplayDocumentType.DAILY_LIST,
            courtRoomIds);
    }

    public static DisplayDocumentUri getUri(DisplayDocumentType displayDocumentType) {
        Integer courtId = 10;
        int[] courtRoomIds = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        return new DisplayDocumentUri(Locale.UK, courtId, displayDocumentType, courtRoomIds);
    }

    public static DisplayDocument getDisplayDocument(Collection<PublicDisplayValue> table,
        PdDataControllerBean mockPdDataControllerBean,
        DisplayStoreControllerBean mockDisplayStoreControllerBean) {
        DisplayDocument doc = new DisplayDocument(getDisplayDocumentUri(),
            mockPdDataControllerBean, mockDisplayStoreControllerBean) {

            private static final long serialVersionUID = 1L;

            @Override
            public Data getData() {
                return DummyDataUtil.getDataWithTable(table);
            }
        };
        doc.setRenderedString("Test Rendered String");
        assertFalse(doc.equals(new DisplayDocument(getDisplayDocumentUri(),
            mockPdDataControllerBean, mockDisplayStoreControllerBean)), "Result is not False");
        return doc;
    }

}
