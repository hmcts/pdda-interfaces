
package uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled;

import uk.gov.hmcts.pdda.business.vos.translation.TranslationBundle;

import javax.xml.XMLConstants;
import javax.xml.transform.TransformerFactory;

public final class TranslationUtils {

    private static final String MISSING_TRANSLATION_MARKER = "**";
    private static final String EMPTY_STRING = "";

    private TranslationUtils() {
    }
    
    public static TransformerFactory getTransformerFactory() {
        TransformerFactory transFactory = TransformerFactory.newInstance();
        // to be compliant, prohibit the use of all protocols by external entities:
        transFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        transFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        return transFactory;
    }

    private static String translate(TranslationBundle translationBundle, String key, String context,
        boolean isData) {
        if (key == null || EMPTY_STRING.equals(key)) {
            return null;
        }
        if (translationBundle != null) {
            String value = null;
            if (context != null) {
                value = translationBundle.getTranslation(key, context);
            }
            if (context == null || value == null) {
                value = translationBundle.getTranslation(key);
            }
            if (value != null) {
                return value;
            }
        }
        if (isData) {
            return key;
        }
        // Default behaviour is to return the key with missing translation
        // marker
        return key + MISSING_TRANSLATION_MARKER;
    }

    public static String translate(TranslationBundle translationBundle, String key,
        String context) {
        return translate(translationBundle, key, context, false);
    }

    public static String translate(TranslationBundle translationBundle, String key) {
        return translate(translationBundle, key, null, false);
    }

    public static String translateData(TranslationBundle translationBundle, String key) {
        return translate(translationBundle, key, null, true);
    }
}
