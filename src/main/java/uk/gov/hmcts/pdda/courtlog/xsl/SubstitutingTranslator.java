package uk.gov.hmcts.pdda.courtlog.xsl;

import org.eclipse.tags.shaded.org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uk.gov.hmcts.framework.services.CsServices;
import uk.gov.hmcts.pdda.courtlog.exceptions.CourtLogRuntimeException;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * This translator does substitution.
 *
 * @author pznwc5
 */
@SuppressWarnings({"PMD.AvoidCatchingThrowable"})
public class SubstitutingTranslator extends Translator {
    /** Parent translator. */
    private final Translator next;

    /**
     * Initialize with the parent.
     *
     * @param type type
     * @param next next
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
        LocalDateTime entryDate, Integer eventType) {
        // System.out.println("Before:" +
        // CSServices.getXMLServices().getStringXML(input));
        substituteValues(type, locale, input, eventType);
        // System.out.println("After:" +
        // CSServices.getXMLServices().getStringXML(input));
        return next.translate(context, locale, input, entryDate, eventType);
    }

    /**
     * Performs the substitution of generic value related data with end user friendly text.
     *
     * @throws CourtLogException Exception
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

            NodeList textNodes = XPathAPI.selectNodeList(input, "//text()");
            for (int i = 0; i < textNodes.getLength(); i++) {
                // System.out.println("Before ===>" +
                // textNodes.item(i).getNodeValue());
                substituteXmlValues(textNodes.item(i), eventType, bundle);
                // System.out.println("After ===>" +
                // textNodes.item(i).getNodeValue());
            }
        } catch (Throwable e) {
            CsServices.getDefaultErrorHandler().handleError(e, getClass(), e.toString());
            throw new CourtLogRuntimeException(e);
        }
    }

    /**
     * Substitute XML values.
     * 
     * @param textNode text node
     * @param eventType event type
     * @param bundle bundle
     */
    private void substituteXmlValues(Node textNode, Integer eventType, ResourceBundle bundle) {
        // @disclaimer -> Meeraj: I have copied this from existing code
        String text = textNode.getNodeValue();
        if (text.length() <= 0) {
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
            StringBuffer sb = new StringBuffer(newValue);
            sb.replace(start, end, "");
            newValue = sb.toString().trim();
        }

        if (newValue != null) {
            textNode.setNodeValue(newValue);
        }
    }
}
