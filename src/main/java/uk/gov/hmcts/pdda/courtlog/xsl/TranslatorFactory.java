package uk.gov.hmcts.pdda.courtlog.xsl;

/**
 * Translator factory.
 * 
 * @author pznwc5
 * 
 *         Factory that creates a translator
 */
public final class TranslatorFactory {

    private TranslatorFactory() {
        // prevent external instantiation
    }

    /**
     * Get the translator for the specified type.
     * 
     * @param translationType Translation type
     * @return Translator
     */
    public static Translator getTranslator(TranslationType translationType) {

        int type = translationType.getType();
        BasicTranslator basicTranslator = new BasicTranslator(type);

        if (translationType == TranslationType.PUBLIC_DISPLAY) {
            return basicTranslator;
        } else if (translationType == TranslationType.INTERNET) {
            // Internet
            return new MaskingTranslator(type, basicTranslator);
        } else {
            // Public notice, thick client and CJSE
            return new SubstitutingTranslator(type, basicTranslator);
        }

    }

    /**
     * Get the translator for the specified type.
     * 
     * @param translationType Translation type
     * @param xsl xsl
     * @return Translator
     */
    public static Translator getTranslator(TranslationType translationType, String xsl) {

        int type = translationType.getType();
        BasicTranslator basicTranslator = new BasicTranslator(type, xsl);

        if (translationType == TranslationType.PUBLIC_DISPLAY) {
            return basicTranslator;
        } else if (translationType == TranslationType.INTERNET) {
            // Internet
            return new MaskingTranslator(type, basicTranslator);
        } else {
            // Public notice, thick client and CJSE
            return new SubstitutingTranslator(type, basicTranslator);
        }

    }
}
