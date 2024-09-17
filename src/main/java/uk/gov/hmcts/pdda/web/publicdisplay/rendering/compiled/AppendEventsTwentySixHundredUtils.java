
package uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled;

import uk.gov.hmcts.pdda.business.vos.translation.TranslationBundle;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.DefendantName;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.nodes.BranchEventXmlNode;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.nodes.LeafEventXmlNode;

import java.util.Collection;

public final class AppendEventsTwentySixHundredUtils {

    private static final String EMPTY_STRING = "";
    private static final String SPACE = " ";
    private static final String INTERPRETER_SWORN = "Interpreter_Sworn";
    private static final String WITNESS_NUMBER = "Witness_Number";
    private static final String DEFENDANT_ON_CASE_ID = "defendant_on_case_id";
    private static final String E20603_INTERPRETER_SWORN = "E20603_Interpreter_Sworn";
    private static final String E20603_APPELLANT_SWORN = "E20603_Appellant_Sworn";

    private AppendEventsTwentySixHundredUtils() {
    }

    // OK
    public static void appendEvent20602(StringBuilder buffer, TranslationBundle documentI18n) {
        AppendUtils.append(buffer,
            TranslationUtils.translate(documentI18n, "Respondent_Case_Opened"));
    }

    public static void appendEvent20603(StringBuilder buffer, BranchEventXmlNode node,
        TranslationBundle documentI18n) {
        BranchEventXmlNode wsOptions =
            (BranchEventXmlNode) node.get("E20603_Witness_Sworn_Options");
        String wsList = ((LeafEventXmlNode) wsOptions.get("E20603_WS_List")).getValue();

        if (E20603_APPELLANT_SWORN.equals(wsList)) {
            AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "Appellant_Sworn"));
        } else if (E20603_INTERPRETER_SWORN.equals(wsList)) {
            AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, INTERPRETER_SWORN));
        } else if (EMPTY_STRING
            .equals(((LeafEventXmlNode) wsOptions.get("E20603_Witness_No")).getValue())) {
            AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "Witness_Sworn"));
        } else {
            AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, WITNESS_NUMBER));
            AppendUtils.append(buffer, SPACE);
            AppendUtils.append(buffer,
                ((LeafEventXmlNode) wsOptions.get("E20603_Witness_No")).getValue());
            AppendUtils.append(buffer, SPACE);
            AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "Sworn"));
        }
    }

    // OK
    public static void appendEvent20604(StringBuilder buffer, TranslationBundle documentI18n) {
        AppendUtils.append(buffer,
            TranslationUtils.translate(documentI18n, "Witness_evidence_concluded"));
    }

    // OK
    public static void appendEvent20605(StringBuilder buffer, TranslationBundle documentI18n) {
        AppendUtils.append(buffer,
            TranslationUtils.translate(documentI18n, "Respondent_Case_Closed"));
    }

    // OK
    public static void appendEvent20606(StringBuilder buffer, BranchEventXmlNode node,
        TranslationBundle documentI18n, Collection<DefendantName> nameCollection) {
        AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "Appellant"));
        AppendUtils.append(buffer, SPACE);

        Integer defOnCaseId =
            Integer.valueOf(((LeafEventXmlNode) node.get(DEFENDANT_ON_CASE_ID)).getValue());
        if (!RendererUtils.isHideInPublicDisplay(defOnCaseId, nameCollection)) {
            AppendUtils.append(buffer,
                ((LeafEventXmlNode) node.get("E20606_Appellant_CO_Name")).getValue());
        }
        AppendUtils.append(buffer, SPACE);
        AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "Case_Opened"));
    }

    // OK
    public static void appendEvent20607(StringBuilder buffer, TranslationBundle documentI18n) {
        AppendUtils.append(buffer,
            TranslationUtils.translate(documentI18n, "Appellant_Submissions"));
    }

    // OK
    public static void appendEvent20608(StringBuilder buffer, TranslationBundle documentI18n) {
        AppendUtils.append(buffer,
            TranslationUtils.translate(documentI18n, "Appellant_Case_Closed"));
    }

    // OK
    public static void appendEvent20609(StringBuilder buffer, TranslationBundle documentI18n) {
        AppendUtils.append(buffer,
            TranslationUtils.translate(documentI18n, "Bench_Retire_to_consider_Judgment"));
    }
}
