package uk.gov.hmcts.pdda.courtlog.helpers.xsl;

import org.w3c.dom.Document;

import java.util.Date;
import java.util.Locale;

/**
 * Translator Interface for court log translations.

 * @author pznwc5
 */
public abstract class Translator {

    protected static final String[] XSL_TYPE = { "client", "public_display", "public_notice", "cjse", "internet" };

    protected int type;

    protected Translator(int type) {
        this.type = type;
    }

    public abstract String translate(TranslationContext context, Locale locale, Document input, Date entryDate,
            Integer eventType);

}
