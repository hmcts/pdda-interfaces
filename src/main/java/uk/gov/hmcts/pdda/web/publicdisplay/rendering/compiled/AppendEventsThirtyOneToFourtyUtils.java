
package uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled;

import uk.gov.hmcts.pdda.business.vos.translation.TranslationBundle;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.nodes.BranchEventXmlNode;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.nodes.LeafEventXmlNode;

public final class AppendEventsThirtyOneToFourtyUtils {

    private static final String SPACE = " ";
    private static final String WITNESS_NUMBER = "Witness_Number";

    private AppendEventsThirtyOneToFourtyUtils() {
    }

    // OK
    public static void appendEvent31000(StringBuilder buffer, BranchEventXmlNode node,
        TranslationBundle documentI18n) {
        AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, WITNESS_NUMBER));
        AppendUtils.append(buffer, SPACE);
        AppendUtils.append(buffer,
            ((LeafEventXmlNode) node.get("E31000_Witness_Number")).getValue());
        AppendUtils.append(buffer, SPACE);
        AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "Cross_Examination"));
    }

    // OK
    public static void appendEvent32000(StringBuilder buffer, BranchEventXmlNode node,
        TranslationBundle documentI18n) {
        AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, WITNESS_NUMBER));
        AppendUtils.append(buffer, SPACE);
        AppendUtils.append(buffer,
            ((LeafEventXmlNode) node.get("E32000_Witness_Number")).getValue());
        AppendUtils.append(buffer, SPACE);
        AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "Re-examination"));
    }

    public static void appendEvent40601(StringBuilder buffer, TranslationBundle documentI18n) {
        AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "Judge's_directions"));
    }

    public static void appendEventCpp(StringBuilder buffer, BranchEventXmlNode node,
        TranslationBundle documentI18n) {
        String freeText = String.valueOf(((LeafEventXmlNode) node.get("free_text")).getValue());
        AppendUtils.append(buffer, TranslationUtils.translateData(documentI18n, freeText));
    }
}
