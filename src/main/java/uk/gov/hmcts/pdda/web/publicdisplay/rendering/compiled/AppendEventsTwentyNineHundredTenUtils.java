
package uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled;

import uk.gov.hmcts.pdda.business.vos.translation.TranslationBundle;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.DefendantName;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.nodes.BranchEventXmlNode;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.nodes.LeafEventXmlNode;

import java.util.Collection;

public final class AppendEventsTwentyNineHundredTenUtils {

    private static final String SPACE = " ";
    private static final String INTERPRETER_SWORN = "Interpreter_Sworn";
    private static final String DEFENDANT_ON_CASE_ID = "defendant_on_case_id";
    private static final String DEFENCE = "Defence";
    private static final String LEGAL_SUBMISSIONS = "Legal_Submissions";

    private AppendEventsTwentyNineHundredTenUtils() {
    }

    // OK
    public static void appendEvent20910(StringBuilder buffer, BranchEventXmlNode node,
        TranslationBundle documentI18n, Collection<DefendantName> nameCollection) {
        AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, DEFENCE));
        AppendUtils.append(buffer, SPACE);

        Integer defOnCaseId =
            Integer.valueOf(((LeafEventXmlNode) node.get(DEFENDANT_ON_CASE_ID)).getValue());
        if (!RendererUtils.isHideInPublicDisplay(defOnCaseId, nameCollection)) {
            AppendUtils.append(buffer,
                ((LeafEventXmlNode) node.get("E20910_Defence_CC_Name")).getValue());
            AppendUtils.append(buffer, SPACE);
        }

        AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "Case_Closed"));
    }

    // OK
    public static void appendEvent20911(StringBuilder buffer, TranslationBundle documentI18n) {
        AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "Summing_Up"));
    }

    // OK
    public static void appendEvent20912(StringBuilder buffer, TranslationBundle documentI18n) {
        AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, LEGAL_SUBMISSIONS));
    }

    // OK
    public static void appendEvent20914(StringBuilder buffer, TranslationBundle documentI18n) {
        AppendUtils.append(buffer,
            TranslationUtils.translate(documentI18n, "Jury_retire_to_consider_verdict"));
    }

    public static void appendEvent20916(StringBuilder buffer, TranslationBundle documentI18n) {
        AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, LEGAL_SUBMISSIONS));
    }

    // OK
    public static void appendEvent20917(StringBuilder buffer, TranslationBundle documentI18n) {
        AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, INTERPRETER_SWORN));
    }

    // OK
    public static void appendEvent20918(StringBuilder buffer, TranslationBundle documentI18n) {
        AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "Trial_Ineffective"));
    }

    // OK
    public static void appendEvent20919(StringBuilder buffer, TranslationBundle documentI18n) {
        AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "Verdict_to_be_taken"));
    }
}
