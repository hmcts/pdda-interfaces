
package uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import uk.gov.hmcts.framework.services.TranslationServices;
import uk.gov.hmcts.pdda.business.vos.translation.TranslationBundle;
import uk.gov.hmcts.pdda.business.vos.translation.TranslationEnum;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.AbstractUri;
import uk.gov.hmcts.pdda.web.publicdisplay.types.document.DisplayDocument;

import java.io.IOException;
import java.io.StringReader;
import java.util.Locale;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public final class DocumentUtils {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentUtils.class);

    private DocumentUtils() {

    }

    public static DocumentBuilderFactory getDocumentBuilderFactory() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        return factory;
    }

    public static Document createInputDocument(String xml)
        throws SAXException, IOException, ParserConfigurationException {
        return createInputDocument(getDocumentBuilderFactory(), xml);
    }
    
    public static Document createInputDocument(DocumentBuilderFactory factory, String xml)
        throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilder builder = factory.newDocumentBuilder();
        return createInputDocument(builder, xml);
    }
    
    public static Document createInputDocument(DocumentBuilder builder, String xml)
        throws SAXException, IOException {
        return builder.parse(new InputSource(new StringReader(xml)));
    }
    
    /**
     * Get the resouce bundle to use, this is added to the velocity context in
     * AbstractTemplateRenderer. Should be called once per render.
     * 
     * @return the resource bundle
     */
    public static TranslationBundle geDocumentI18n(DisplayDocument displayDocument) {
        TranslationBundle translationBundle = null;
        try {
            Locale locale = getLocale(displayDocument);
            translationBundle = TranslationServices.getInstance()
                .getTranslationBundle(TranslationEnum.PUBLICDISPLAYDOCUMENTRESOURCES, locale);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Using bundle " + translationBundle + " for document \""
                    + displayDocument.getUri() + "\" in Locale \"" + locale + "\".");
            }
        } catch (Exception e) {
            LOG.error("Error creating translationBundle: {} ", e.getMessage());
        }
        return translationBundle;
    }

    private static Locale getLocale(DisplayDocument displayDocument) {
        AbstractUri uri;
        Locale locale = null;
        if (displayDocument != null) {
            uri = displayDocument.getUri();
            if (uri != null) {
                locale = uri.getLocale();
            }
        }

        if (locale != null) {
            return locale;
        } else {
            return Locale.getDefault();
        }
    }

    /**
     * Get the row type, even if even otherwise odd.
     */
    public static String getRowType(String rowType) {
        // WDF: This will give the first row as even as per original then
        // alternate! Logically the first row should be odd, depends if the
        // table header is included.
        return "evenRow".equals(rowType) ? "oddRow" : "evenRow";
    }
}