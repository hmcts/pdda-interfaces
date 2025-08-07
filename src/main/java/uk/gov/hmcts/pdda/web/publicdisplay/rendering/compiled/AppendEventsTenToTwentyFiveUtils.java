
package uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled;

import uk.gov.hmcts.pdda.business.vos.translation.TranslationBundle;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.nodes.BranchEventXmlNode;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.nodes.LeafEventXmlNode;

public final class AppendEventsTenToTwentyFiveUtils {

    private static final String SPACE = " ";

    private AppendEventsTenToTwentyFiveUtils() {
    }

    // OK
    public static void appendEvent10100(StringBuilder buffer, TranslationBundle documentI18n) {
        AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "Case_Started"));
    }

    // OK
    public static void appendEvent10500(StringBuilder buffer, TranslationBundle documentI18n) {
        AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "Resume"));
    }

    // Not used . Not tested
    public static void appendEvent20502(StringBuilder buffer, BranchEventXmlNode node,
        TranslationBundle documentI18n) {
        AppendUtils.append(buffer,
            TranslationUtils.translate(documentI18n, "Pleas_and_Directions_Hearing_on"));
        AppendUtils.append(buffer, SPACE);
        AppendAttributeFieldsExtensionUtils.appendFormattedDate(buffer,
            (LeafEventXmlNode) node.get("E20502_P_And_D_Hearing_On_Date"), documentI18n);
    }
}