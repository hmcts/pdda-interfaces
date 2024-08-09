package uk.gov.hmcts.pdda.business.services.formatting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import uk.gov.hmcts.framework.services.CsServices;
import uk.gov.hmcts.framework.services.XslServices;
import uk.gov.hmcts.framework.xml.sax.ForkContentHandler;
import uk.gov.hmcts.pdda.business.vos.formatting.FormattingValue;
import uk.gov.hmcts.pdda.business.xmlbinding.formatting.FormattingConfig;
import uk.gov.hmcts.pdda.business.xmlbinding.hmcts.pdda.types.MimeTypeType;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.sax.SAXResult;

/**
 * TransformerUtils.
 */

public final class TransformerUtils {

    private static final Logger LOG = LoggerFactory.getLogger(TransformerUtils.class);
    private static final String IWP = "IWP";

    private TransformerUtils() {

    }

    public static Transformer createTransformer(FormattingValue formattingValue,
        Map<String, String> parameterMap) throws TransformerFactoryConfigurationError {

        // Create a new transformer with the parameters
        Transformer transformer =
            CsServices.getXslServices().getTransformer(formattingValue.getLocale(), parameterMap);
        if (IWP.equals(formattingValue.getDocumentType())) {

            if (LOG.isDebugEnabled()) {
                LOG.debug("Setting transformer output properties for XHTML generation");
            }
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,
                "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd");
            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC,
                "-//W3C//DTD XHTML 1.0 Strict//EN");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Created transformer " + transformer.getClass().getName() + " "
                + transformer.getURIResolver() + ".");
            Properties properties = transformer.getOutputProperties();
            Enumeration<?> enumeration = properties.elements();

            while (enumeration.hasMoreElements()) {
                String key = enumeration.nextElement().toString();
                LOG.debug(key + " -- " + properties.getProperty(key));
            }
        }
        return transformer;
    }

    public static Writer transformIwp(Writer buffer)
        throws IOException {
        if (buffer == null) {
            LOG.warn("IWP: buffer is null; pages will NOT be generated.");
            return null;
        } else {
            StringBuilder sb = new StringBuilder(
                "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/"
                    + "TR/xhtml1/DTD/xhtml1-strict.dtd\">\r\n");
            String updatedPage = FormattingServiceUtils.amendGeneratedPage(buffer.toString());
            sb.append(updatedPage);
            LOG.debug("updated page is {}", sb);
            Writer bufferToUse = new StringWriter();
            for (int i = 0; i < sb.length(); i++) {
                bufferToUse.append(sb.charAt(i));
            }
            return bufferToUse;
        }
    }

    public static Writer transform(XslServices xslServices, FormattingConfig formattingConfig,
        FormattingValue formattingValue, String translationXml, Writer buffer)
        throws TransformerException, SAXException, ParserConfigurationException, IOException {
        Writer bufferToUse = buffer;

        // Transform the output stream then flush to ensure all data has been
        // written
        Map<String, String> parameterMap = FormattingServiceUtils.createParameterMap();

        Source source = SaxUtils.createSource(xslServices, formattingConfig, formattingValue,
            translationXml, parameterMap);
        Result result = createResult(formattingValue.getOutputStream(),
            formattingValue.getMimeType(), bufferToUse);
        Transformer transformer = createTransformer(formattingValue, parameterMap);
        transformer.transform(source, result);
        if (IWP.equals(formattingValue.getDocumentType())) {
            // Format the internet web page with a new html header
            return transformIwp(bufferToUse);
        }

        formattingValue.getOutputStream().flush();
        return bufferToUse;
    }

    public static Result createResult(OutputStream out, String mimeType, Writer buffer)
        throws IOException {
        if (LOG.isDebugEnabled()) {
            LOG.debug(
                "createResult(" + toDebug(out) + "," + mimeType + "," + toDebug(buffer) + ")");
        }

        MimeTypeType mimeTypeType = MimeTypeType.fromString(mimeType);
        if (mimeTypeType != null
            && Integer.valueOf(MimeTypeType.HTM_TYPE).equals(mimeTypeType.getType())) {
            return createHtmlResult(out, buffer);
        } else {
            return createXmlResult(out, buffer);
        }
    }

    public static Result createHtmlResult(OutputStream out, Writer buffer) throws IOException {
        if (buffer != null) {
            return new SAXResult(new ForkContentHandler(SaxUtils.createHtmlSerializer(out),
                SaxUtils.createHtmlSerializer(buffer)));
        } else {
            return new SAXResult(SaxUtils.createHtmlSerializer(out));
        }
    }

    public static Result createXmlResult(OutputStream out, Writer buffer) throws IOException {
        if (buffer != null) {
            return new SAXResult(new ForkContentHandler(SaxUtils.createXmlSerializer(out),
                SaxUtils.createXmlSerializer(buffer)));
        } else {
            return new SAXResult(SaxUtils.createXmlSerializer(out));
        }
    }

    private static String toDebug(Writer writer) {
        return writer == null ? "null" : writer.getClass().getName();
    }

    private static String toDebug(OutputStream out) {
        return out == null ? "null" : out.getClass().getName();
    }
}
