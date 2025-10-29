package uk.gov.hmcts.pdda.courtlog.helpers.xsl;

import java.time.LocalDateTime;
import java.util.Locale;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author pznwc5
 * 
 * This translator performs masking
 */
public class MaskingTranslator extends Translator {

    private static final String DEFENDANT_MASKED_NAME = "defendant_masked_name";

    private static final String DEFENDANT_MASKED_FLAG = "defendant_masked_flag";

    /** Property for masked name */
    public static final String MASKED_NAME = "MASKED_NAME";

    /** Property for masked flag */
    public static final String MASKED_FLAG = "MASKED_FLAG";

    /** Parent translator */
    private Translator next;

    /**
     * Initialize with the parent
     * 
     * @param parent
     *            Parent translator
     */
    public MaskingTranslator(int type, BasicTranslator parent) {
        super(type);
        this.next = parent;
    }

    /**
     * Decorate with masking
     */
    public String translate(TranslationContext context, Locale locale, Document input,
        LocalDateTime entryDate, Integer eventType) {

        String maskedName = (String) context.get(MASKED_NAME);
        String maskedFlag = (String) context.get(MASKED_FLAG);
        resetDefendantDetails(input, maskedName, maskedFlag);

        return next.translate(context, locale, input, entryDate, eventType);
    }

    /**
     * This will reset the defendants masked name and masked flag. This because
     * the application is a multi-user app and this causes problems when/if two
     * or more users are updating the same case at the same time and if one of
     * them is changing the masked details. Therefore we have to make sure this
     * is reset so that no confidential details are being displayed on the
     * Internet page.
     * 
     * @param xmlString
     *            String the courtlog entry
     * @return String the new courtlog entry with the reset values.
     */
    private void resetDefendantDetails(final Document dom, final String maskedName, final String maskedFlag) {

        resetValue(dom, DEFENDANT_MASKED_NAME, maskedName);
        resetValue(dom, DEFENDANT_MASKED_FLAG, maskedFlag);
    }

    /**
     * Method to reset the value of a node in an XML document.
     * 
     * @param doc
     *            Document that will be changed
     * @param tagname
     *            String the element by tag name
     * @param value
     *            String the value that the document will be updated with
     * @return Document the same as passed in.
     */
    private void resetValue(final Document doc, final String tagname, String value) {
        final NodeList nodelist = doc.getElementsByTagName(tagname);
        if (nodelist.getLength() == 0)
            return;
        final Node node = nodelist.item(0);
        if (node == null)
            return;

        final Node firstChild = node.getFirstChild();
        if (firstChild != null)
            firstChild.setNodeValue(value);
        else
            node.appendChild(doc.createTextNode(value));
    }
}

