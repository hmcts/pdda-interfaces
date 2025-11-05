package uk.gov.hmcts.pdda.courtlog.helpers.xsl;

import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import uk.gov.courtservice.xhibit.courtlog.vos.CourtLogViewValue;
import uk.gov.hmcts.pdda.courtlog.helpers.xml.CourtLogXmlHelper;

import java.util.Date;
import java.util.Locale;

/**
 * Helper class used to provide several delegate methods that perform XML
 * transformation using XSL.

 * @author tz0d5m
 * @version $Revision: 1.8 $
 * @see uk.gov.courtservice.xhibit.courtlog.helpers.xsl.Translator
 */
public final class CourtLogXslHelper {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(CourtLogXslHelper.class);

    private CourtLogXslHelper() {
        // private constructor to prevent instantiation...
    }

    /**
     * Delegate method to provide a simpler interface for transformations to the
     * calling code.

     * @see #translateEvent(java.lang.String, java.util.Date, java.lang.Integer,
     *      java.util.Locale, uk.gov.courtservice.xhibit
     *      .courtlog.helpers.xsl.TranslationType, uk.gov.courtservice.xhibit
     *      .courtlog.helpers.xsl.TranslationContext, java.lang.String)
     */
    public static String translateEvent(String xml, Date entryDate, Integer eventType, Locale locale,
            TranslationType translationType) {
        return translateEvent(xml, entryDate, eventType, locale, translationType, null, null);
    }

    /**
     * Delegate method to provide a simpler interface for transformations to the
     * calling code.

     * @see #translateEvent(java.lang.String, java.util.Date, java.lang.Integer,
     *      java.util.Locale, uk.gov.courtservice.xhibit
     *      .courtlog.helpers.xsl.TranslationType, uk.gov.courtservice.xhibit
     *      .courtlog.helpers.xsl.TranslationContext, java.lang.String)
     */
    public static String translateEvent(String xml, Date entryDate, Integer eventType, Locale locale,
            TranslationType translationType, TranslationContext context) {
        return translateEvent(xml, entryDate, eventType, locale, translationType, context, null);
    }

    /**
     * Delegate method to provide a simpler interface for transformations to the
     * calling code.

     * @see #translateEvent(java.lang.String, java.util.Date, java.lang.Integer,
     *      java.util.Locale, uk.gov.courtservice.xhibit
     *      .courtlog.helpers.xsl.TranslationType, uk.gov.courtservice.xhibit
     *      .courtlog.helpers.xsl.TranslationContext, java.lang.String)
     */
    public static String translateEvent(String xml, Date entryDate, Integer eventType, Locale locale,
            TranslationType translationType, String xsl) {
        return translateEvent(xml, entryDate, eventType, locale, translationType, null, xsl);
    }

    /**
     * Delegate method to provide a simpler interface for transformations to the
     * calling code. The xml to transform, the entry date and the event type are
     * all taken from the passed in <code>CourtLogViewValue</code>.

     * @see #translateEvent(java.lang.String, java.util.Date, java.lang.Integer,
     *      java.util.Locale, uk.gov.courtservice.xhibit
     *      .courtlog.helpers.xsl.TranslationType, uk.gov.courtservice.xhibit
     *      .courtlog.helpers.xsl.TranslationContext, java.lang.String)
     */
    public static String translateEvent(CourtLogViewValue viewValue, Locale locale, TranslationType translationType) {
        return translateEvent(viewValue, locale, translationType, null, null);
    }

    /**
     * Delegate method to provide a simpler interface for transformations to the
     * calling code. The xml to transform, the entry date and the event type are
     * all taken from the passed in <code>CourtLogViewValue</code>.

     * @see #translateEvent(java.lang.String, java.util.Date, java.lang.Integer,
     *      java.util.Locale, uk.gov.courtservice.xhibit
     *      .courtlog.helpers.xsl.TranslationType, uk.gov.courtservice.xhibit
     *      .courtlog.helpers.xsl.TranslationContext, java.lang.String)
     */
    public static String translateEvent(CourtLogViewValue viewValue, Locale locale, TranslationType translationType,
            TranslationContext context) {
        return translateEvent(viewValue, locale, translationType, context, null);
    }

    /**
     * Delegate method to provide a simpler interface for transformations to the
     * calling code. The xml to transform, the entry date and the event type are
     * all taken from the passed in <code>CourtLogViewValue</code>.

     * @see #translateEvent(java.lang.String, java.util.Date, java.lang.Integer,
     *      java.util.Locale, uk.gov.courtservice.xhibit
     *      .courtlog.helpers.xsl.TranslationType, uk.gov.courtservice.xhibit
     *      .courtlog.helpers.xsl.TranslationContext, java.lang.String)
     */
    public static String translateEvent(CourtLogViewValue viewValue, Locale locale, TranslationType translationType,
            String xsl) {
        return translateEvent(viewValue, locale, translationType, null, xsl);
    }

    /**
     * Delegate method to provide a simpler interface for transformations to the
     * calling code. The xml to transform, the entry date and the event type are
     * all taken from the passed in <code>CourtLogViewValue</code>.

     * @see #translateEvent(java.lang.String, java.util.Date, java.lang.Integer,
     *      java.util.Locale, uk.gov.courtservice.xhibit
     *      .courtlog.helpers.xsl.TranslationType, uk.gov.courtservice.xhibit
     *      .courtlog.helpers.xsl.TranslationContext, java.lang.String)
     */
    public static String translateEvent(CourtLogViewValue viewValue, Locale locale, TranslationType translationType,
            TranslationContext context, String xsl) {
        return translateEvent(viewValue.getLogEntry(), viewValue.getEntryDate(), viewValue.getEventType(), locale,
                translationType, context, xsl);
    }

    /**
     * Method to translate and return the passed in xml using the required xsl
     * stylesheet.

     * @param xml
     *            The xml text we want to transform, cannot be <i>null</i>.
     * @param entryDate
     *            The date the court log event represented by the xml parameter
     *            was created.
     * @param eventType
     *            The court log event type of the xml we are transforming.
     * @param locale
     *            The locale that should be used during transformation, cannot
     *            be <i>null</i>.
     * @param translationType
     *            The type of translation we want to perform, cannot be <i>null</i>.
     * @param context
     *            A context to contain custom properties to be used during the
     *            transformation, if <i>null</i>, then a new one with no
     *            properties set will be created.
     * @param xsl
     *            The path of the XSL file to use for the transform, or <i>null</i>
     *            if could be worked out later.
     * @return The passed in xml translated using a <code>Translator</code>

     * @see uk.gov.courtservice.xhibit.courtlog.helpers.xsl.Translator
     *      #translate(uk.gov.courtservice.xhibit.courtlog.helpers.xsl
     *      .TranslationContext, java.util.Locale, org.w3c.dom.Document,
     *      java.util.Date, java.lang.Integer)
     */
    public static String translateEvent(String xml, Date entryDate, Integer eventType, Locale locale,
            TranslationType translationType, TranslationContext context, String xsl) {
        LOG.debug("translateEvent() - xml = " + xml + "; entryDate = " + entryDate + "; eventType = " + eventType
                + "; locale = " + locale + "; translationType = " + translationType + "; context = " + context
                + "; xsl = " + xsl);
        
        if ((xml == null) || (locale == null) || (translationType == null)) {
            throw new IllegalArgumentException("Required property supplied is null");
        }

        // if the context is null, then create a new one...
        TranslationContext newContext = context;
        if (newContext == null) {
            newContext = new TranslationContext();
        }

        Document document = CourtLogXmlHelper.createDocument(xml);
        Translator translator = getTranslator(translationType, xsl);

        return translator.translate(newContext, locale, document, entryDate, eventType);
    }

    /**
     * Simple extracted method to provide the required <code>Translator</code>
     * for the passed in parameters.

     * @param translationType
     *            The translation type to use.
     * @param xsl
     *            The path of the XSL file to use for the transform.
     * @return The required <code>Translator</code>

     * @see uk.gov.courtservice.xhibit.courtlog.helpers.xsl.TranslatorFactory
     *      #getTranslator(uk.gov.courtservice.xhibit.courtlog.helpers.xsl
     *      .TranslationType)
     * @see uk.gov.courtservice.xhibit.courtlog.helpers.xsl.TranslatorFactory
     *      #getTranslator(uk.gov.courtservice.xhibit.courtlog.helpers.xsl
     *      .TranslationType, java.lang.String)
     */
    private static Translator getTranslator(TranslationType translationType, String xsl) {
        // private method, so we know that translationType will not be null...
        if (xsl == null) {
            return TranslatorFactory.getTranslator(translationType);
        }

        return TranslatorFactory.getTranslator(translationType, xsl);
    }
}