package uk.gov.hmcts.pdda.courtlog.helpers.xsl;

/**
 * TranslatorFactory that creates a translator.

 * @author pznwc5

 */
public final class TranslatorFactory {

    private TranslatorFactory() {
        super();
    }
    
    /**
     * Get the translator for the specified type.

     * @param translationType Translation type.
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

     * @param translationType the translation type.
     * @param xsl to use.
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

