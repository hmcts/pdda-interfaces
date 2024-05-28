package uk.gov.hmcts.pdda.business.services.formatting;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import uk.gov.hmcts.framework.services.CsServices;
import uk.gov.hmcts.framework.services.XslServices;
import uk.gov.hmcts.pdda.business.vos.formatting.FormattingValue;
import uk.gov.hmcts.pdda.business.xmlbinding.formatting.FormattingConfig;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

/**
 * TransformerUtils.
 */

public final class SaxUtils {

    private SaxUtils() {

    }

    public static ContentHandler createXmlSerializer(OutputStream out) throws IOException {
        return CsServices.getXmlServices().createXmlSerializer(out);
    }

    public static ContentHandler createXmlSerializer(Writer writer) throws IOException {
        return CsServices.getXmlServices().createXmlSerializer(writer);
    }

    public static ContentHandler createHtmlSerializer(OutputStream out) throws IOException {
        return CsServices.getXmlServices().createHtmlSerializer(out);
    }

    public static ContentHandler createHtmlSerializer(Writer writer) throws IOException {
        return CsServices.getXmlServices().createHtmlSerializer(writer);
    }

    public static Source createSource(XslServices xslServices, Reader reader,
        Templates... templatesArray)
        throws TransformerException, SAXException, ParserConfigurationException {
        if (templatesArray.length == 0) {
            // No transforms to perform so use in directly
            return new StreamSource(reader);
        } else {
            // Create and link the filters
            XMLFilter[] filters = xslServices.getFilters(templatesArray);
            if (0 < filters.length) {
                filters[0].setParent(FormattingServiceUtils.createReader());
                for (int i = 1; i < filters.length; i++) {
                    filters[i].setParent(filters[i - 1]);
                }
            }
            XMLFilter xmlFilter = filters[filters.length - 1];
            return new SAXSource(xmlFilter, new InputSource(reader));
        }
    }

    public static Source createSource(XslServices xslServices, FormattingConfig formattingConfig,
        FormattingValue formattingValue, String translationXml, Map<String, String> parameterMap)
        throws TransformerException, SAXException, ParserConfigurationException {
        String[] xsltNames = formattingConfig.getXslTransforms(formattingValue);
        // Get Templates
        Templates[] templatesArray =
            xslServices.getTemplatesArray(xsltNames, formattingValue.getLocale(), parameterMap);
        // Wrap Templates
        for (int i = 0; i < templatesArray.length; i++) {
            templatesArray[i] = new TranslationXmlTemplates(translationXml, templatesArray[i]);
        }
        // Use Templates
        return createSource(xslServices, formattingValue.getReader(), templatesArray);
    }
}
