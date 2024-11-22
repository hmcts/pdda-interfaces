package uk.gov.hmcts.pdda.courtlog.xsl;

import org.w3c.dom.Document;

import java.time.LocalDateTime;
import java.util.Locale;

/**
 * Translator.
 * @author pznwc5
 * 
 *         Interface for court log translation
 */
public abstract class Translator {

    protected static final String[] XSL_TYPE =
        {"client", "public_display", "public_notice", "cjse", "internet"};

    protected int type;

    public Translator(int type) {
        this.type = type;
    }

    public abstract String translate(TranslationContext context, Locale locale, Document input,
        LocalDateTime entryDate, Integer eventType);

}
