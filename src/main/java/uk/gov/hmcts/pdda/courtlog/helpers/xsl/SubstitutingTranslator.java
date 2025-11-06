package uk.gov.hmcts.pdda.courtlog.helpers.xsl;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uk.gov.hmcts.framework.services.CsServices;
import uk.gov.hmcts.pdda.courtlog.exceptions.CourtLogRuntimeException;

import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.xml.XMLConstants;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

/**
 * This translator does substitution.

 * @author pznwc5
 */
@SuppressWarnings("squid:S5778")
public class SubstitutingTranslator extends Translator {
    /** Parent translator. */
    private final Translator next;

    /**
     * Initialize with the parent.
     */
    public SubstitutingTranslator(int type, Translator next) {
        super(type);
        this.next = next;
    }

    /**
     * Decorate with substitution.
     */
    @Override
    public String translate(TranslationContext context, Locale locale, Document input,
        Date entryDate, Integer eventType) {
        substituteValues(type, locale, input, eventType);
        return next.translate(context, locale, input, entryDate, eventType);
    }

    /**
     * Performs the substitution of generic value related data with end user friendly text.
     */
    private void substituteValues(final int translateForType, final Locale locale, Document input,
            final Integer eventType) {

        try {
            // Get substitution values for this locale...
            String baseName = "CLSubstitution" + "_" + XSL_TYPE[translateForType] + "_" + locale;
            ResourceBundle fileLookUps = CsServices.getConfigServices().getBundle(baseName);
            // Get menu option types for this event...
            String fileName = fileLookUps.getString(String.valueOf(eventType));
            ResourceBundle bundle = CsServices.getConfigServices().getBundle(fileName);

            NodeList textNodes = allTextNodes(input);
            for (int i = 0; i < textNodes.getLength(); i++) {
                substituteXmlValues(textNodes.item(i), eventType, bundle);
            }
        } catch (Exception e) {
            CsServices.getDefaultErrorHandler().handleError(e, getClass(), e.toString());
            throw new CourtLogRuntimeException(e);
        }
    }
    
    public NodeList allTextNodes(Document input) throws XPathFactoryConfigurationException,
        XPathExpressionException {
        XPathFactory xf = XPathFactory.newInstance();
        // (Optional but recommended) secure processing
        xf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

        XPath xpath = xf.newXPath();
        XPathExpression expr = xpath.compile("//text()");
        return (NodeList) expr.evaluate(input, XPathConstants.NODESET);
    }

    /**
     * Substitutes XML values.

     * @param textNode passed in
     * @param eventType passed in
     * @param bundle passed in
     */
    private void substituteXmlValues(Node textNode, Integer eventType, ResourceBundle bundle) {
        String text = textNode.getNodeValue();
        if (text.isEmpty()) {
            return;
        }

        String eventNo = "E" + eventType;
        
        if (text.indexOf(eventNo) == -1) {
            return;
        }
        // Determine a key...
        String newValue = bundle.getString(text);
        // Find start and end index for substitution...
        int start = newValue.indexOf('[');
        int end = newValue.indexOf(']') + 1;
        if (start != -1 && end != -1) {
            // Convert string content into string buffer, before
            // removing (xxx)...
            StringBuilder sb = new StringBuilder(newValue);
            sb.replace(start, end, "");
            newValue = sb.toString().trim();
        }

        if (newValue != null) {
            textNode.setNodeValue(newValue);
        }
    }
}

