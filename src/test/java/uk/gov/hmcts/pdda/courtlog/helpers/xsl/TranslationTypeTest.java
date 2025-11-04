package uk.gov.hmcts.pdda.courtlog.helpers.xsl;

import org.easymock.EasyMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(EasyMockExtension.class)
@SuppressWarnings("PMD")
class TranslationTypeTest {
    
    private static final String TYPE_GUI = "GUI";
    private static final String TYPE_DISPLAY = "DISPLAY";
    private static final String TYPE_NOTCE = "NOTCE";
    private static final String TYPE_CJSE = "CJSE";
    private static final String TYPE_INTERNET = "INTERNET";
    private static final String INVALID_TYPE = "INVALID_TYPE";
    
    private static final String NOT_EQUAL = "Result is not equal";
    
    @Test
    void testTranslationType() {
        assertEquals(TranslationType.GUI, TranslationType.valueOf(TYPE_GUI), NOT_EQUAL);
        assertEquals(TranslationType.PUBLIC_DISPLAY, TranslationType.valueOf(TYPE_DISPLAY), NOT_EQUAL);
        assertEquals(TranslationType.PUBLIC_NOTCE, TranslationType.valueOf(TYPE_NOTCE), NOT_EQUAL);
        assertEquals(TranslationType.CJSE, TranslationType.valueOf(TYPE_CJSE), NOT_EQUAL);
        assertEquals(TranslationType.INTERNET, TranslationType.valueOf(TYPE_INTERNET), NOT_EQUAL);
    }
    
    @Test
    void testTranslationTypeInvalid() {
        assertThrows(IllegalArgumentException.class, () -> {
            TranslationType.valueOf(INVALID_TYPE);
        });
    }
    
    @Test
    void testGetType() {
        TranslationType translationType = TranslationType.GUI;
        assertEquals(0, translationType.getType(), NOT_EQUAL);
    }
    
    @Test
    void testToString() {
        TranslationType translationType = TranslationType.GUI;
        assertEquals(TYPE_GUI, translationType.toString(), NOT_EQUAL);
        translationType = TranslationType.PUBLIC_DISPLAY;
        assertEquals(TYPE_DISPLAY, translationType.toString(), NOT_EQUAL);
        translationType = TranslationType.PUBLIC_NOTCE;
        assertEquals(TYPE_NOTCE, translationType.toString(), NOT_EQUAL);
        translationType = TranslationType.CJSE;
        assertEquals(TYPE_CJSE, translationType.toString(), NOT_EQUAL);
        translationType = TranslationType.INTERNET;
        assertEquals(TYPE_INTERNET, translationType.toString(), NOT_EQUAL);
    }
}
