package uk.gov.hmcts.pdda.courtlog.helpers.xsl;

import org.w3c.dom.Document;
import uk.gov.courtservice.xhibit.courtlog.vos.CourtLogCrudValue;
import uk.gov.hmcts.framework.services.CsServices;
import uk.gov.hmcts.framework.services.XmlServices;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Basic translator.

 * @author pznwc5

 */
public class BasicTranslator extends Translator {

    private static final String XML_TAG_COURT_LOG_EVENT = "event";

    private static final String XSL_SOURCE_PATH = "config/courtlog/transformer/";

    private String xsl;

    public BasicTranslator(int type) {
        super(type);
    }

    /**
     * Constructor with xsl.

     * @param type passed in
     * @param xsl passed in
     */
    public BasicTranslator(int type, String xsl) {
        super(type);
        this.xsl = xsl;
    }

    @Override
    public String translate(TranslationContext context, Locale locale, Document input,
        Date entryDate, Integer eventType) {
        formatDateAndTime(locale, input, entryDate);

        if (xsl == null) {
            xsl = XSL_SOURCE_PATH + XSL_TYPE[type] + "/" + eventType + ".xsl";
        }
        return transform(input, xsl);
    }

    /**
     * Translates a date and a time into the required locale format.

     * @throws CourtLogException exception
     */
    private void formatDateAndTime(final Locale locale, final Document input, final Date entryDate) {

        XmlServices xmlServices = CsServices.getXmlServices();

        // Format the date and time, for the current locale, prior to
        // adding to the xml entry.
        final String displayDate = DateFormat.getDateInstance(DateFormat.SHORT, locale).format(entryDate);
        final String displayTime = DateFormat.getTimeInstance(DateFormat.SHORT, locale).format(entryDate);

        xmlServices.addElementByTagName(input, CourtLogCrudValue.ENTRY_DATE, displayDate, XML_TAG_COURT_LOG_EVENT);
        xmlServices.addElementByTagName(input, CourtLogCrudValue.ENTRY_TIME, displayTime, XML_TAG_COURT_LOG_EVENT);
    }

    /**
     * Applies an xsl template to xml and send result to requested stream.

     * @param input
     *            The document to transform

     * @param xslFileName
     *            the transformation to apply

     * @return The transformed document as a string

     * @throws EDSException
     *             if anything fails.
     */
    private static String transform(final Document input, final String xslFileName) {
        return CsServices.getXslServices().transform(input, xslFileName, null, null);
    }

}

