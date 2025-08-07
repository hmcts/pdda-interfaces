
package uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled;

import uk.gov.hmcts.pdda.business.vos.translation.TranslationBundle;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.nodes.BranchEventXmlNode;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.nodes.LeafEventXmlNode;

public final class AppendEventsTwentySixHundredTenUtils {

    private static final String SPACE = " ";
    private static final String INTERPRETER_SWORN = "Interpreter_Sworn";
    private static final String WITNESS_NUMBER = "Witness_Number";
    private static final String LEGAL_SUBMISSIONS = "Legal_Submissions";

    private AppendEventsTwentySixHundredTenUtils() {
    }

    // OK
    public static void appendEvent20610(StringBuilder buffer, TranslationBundle documentI18n) {
        AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "Judgement"));
    }

    public static void appendEvent20611(StringBuilder buffer, TranslationBundle documentI18n) {
        AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, LEGAL_SUBMISSIONS));
    }

    public static void appendEvent20612(StringBuilder buffer, TranslationBundle documentI18n) {
        AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, INTERPRETER_SWORN));
    }

    // OK
    public static void appendEvent20613(StringBuilder buffer, BranchEventXmlNode node,
        TranslationBundle documentI18n) {
        AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, WITNESS_NUMBER));
        AppendUtils.append(buffer, SPACE);
        AppendUtils.append(buffer,
            ((LeafEventXmlNode) node.get("E20613_Witness_Number")).getValue());
        AppendUtils.append(buffer, SPACE);
        AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "Continues"));
    }
}
