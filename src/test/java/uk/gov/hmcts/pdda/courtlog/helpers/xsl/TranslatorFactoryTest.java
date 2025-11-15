package uk.gov.hmcts.pdda.courtlog.helpers.xsl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("PMD")
class TranslatorFactoryTest {

    @Test
    void testGetTranslatorReturnsBasicTranslatorForPublicDisplay() {
        TranslationType type = TranslationType.PUBLIC_DISPLAY;

        Translator translator = TranslatorFactory.getTranslator(type);

        assertNotNull(translator);
        assertTrue(translator instanceof BasicTranslator);
    }

    @Test
    void testGetTranslatorReturnsMaskingTranslatorForInternet() {
        TranslationType type = TranslationType.INTERNET;

        Translator translator = TranslatorFactory.getTranslator(type);

        assertNotNull(translator);
        assertTrue(translator instanceof MaskingTranslator);
    }

    @Test
    void testGetTranslatorReturnsSubstitutingTranslatorForOtherTypes() {
        TranslationType type = TranslationType.CJSE;

        Translator translator = TranslatorFactory.getTranslator(type);

        assertNotNull(translator);
        assertTrue(translator instanceof SubstitutingTranslator);
    }

    @Test
    void testGetTranslatorWithXslReturnsBasicTranslatorForPublicDisplay() {
        TranslationType type = TranslationType.PUBLIC_DISPLAY;
        String xslPath = "custom/path.xsl";

        Translator translator = TranslatorFactory.getTranslator(type, xslPath);

        assertNotNull(translator);
        assertTrue(translator instanceof BasicTranslator);
    }

    @Test
    void testGetTranslatorWithXslReturnsMaskingTranslatorForInternet() {
        TranslationType type = TranslationType.INTERNET;
        String xslPath = "internet/path.xsl";

        Translator translator = TranslatorFactory.getTranslator(type, xslPath);

        assertNotNull(translator);
        assertTrue(translator instanceof MaskingTranslator);
    }

    @Test
    void testGetTranslatorWithXslReturnsSubstitutingTranslatorForOtherTypes() {
        TranslationType type = TranslationType.PUBLIC_NOTCE;
        String xslPath = "notice/path.xsl";

        Translator translator = TranslatorFactory.getTranslator(type, xslPath);

        assertNotNull(translator);
        assertTrue(translator instanceof SubstitutingTranslator);
    }
}
