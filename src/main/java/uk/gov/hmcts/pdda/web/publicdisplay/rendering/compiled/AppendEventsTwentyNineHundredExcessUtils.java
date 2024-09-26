
package uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled;

import uk.gov.hmcts.pdda.business.vos.translation.TranslationBundle;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.nodes.BranchEventXmlNode;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.nodes.LeafEventXmlNode;

public final class AppendEventsTwentyNineHundredExcessUtils {

    private static final String SPACE = " ";
    private static final String EMPTY_STRING = "";
    private static final String WITNESS_NUMBER = "Witness_Number";
    private static final String E20935_DEFENDANT_READ = "E20935_Defendant_Read";
    private static final String E20904_INTERPRETER_SWORN = "E20904_Interpreter_sworn";

    private AppendEventsTwentyNineHundredExcessUtils() {
    }

    // OK
    public static void appendEvent20920(StringBuilder buffer, BranchEventXmlNode node,
        TranslationBundle documentI18n) {
        AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, WITNESS_NUMBER));
        AppendUtils.append(buffer, SPACE);
        AppendUtils.append(buffer,
            ((LeafEventXmlNode) node.get("E20920_Witness_Number")).getValue());
        AppendUtils.append(buffer, SPACE);
        AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "Continues"));
    }

    // OK
    public static void appendEvent20931(StringBuilder buffer, BranchEventXmlNode node,
        TranslationBundle documentI18n) {
        AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, WITNESS_NUMBER));
        AppendUtils.append(buffer, SPACE);
        AppendUtils.append(buffer,
            ((LeafEventXmlNode) node.get("E20931_Witness_Number")).getValue());
        AppendUtils.append(buffer, SPACE);
        AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "Cross_Examination"));
    }

    // OK
    public static void appendEvent20932(StringBuilder buffer, BranchEventXmlNode node,
        TranslationBundle documentI18n) {
        AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, WITNESS_NUMBER));
        AppendUtils.append(buffer, SPACE);
        AppendUtils.append(buffer,
            ((LeafEventXmlNode) node.get("E20932_Witness_Number")).getValue());
        AppendUtils.append(buffer, SPACE);
        AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "Re-examination"));
    }

    // OK
    public static void appendEvent20935(StringBuilder buffer, BranchEventXmlNode node,
        TranslationBundle documentI18n) {
        BranchEventXmlNode wsOptions = (BranchEventXmlNode) node.get("E20935_Witness_Read_Options");
        String wsoType = ((LeafEventXmlNode) wsOptions.get("E20935_WR_Type")).getValue();

        if (E20935_DEFENDANT_READ.equals(wsoType)) {
            AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "Defendant_Read"));
        } else if (E20904_INTERPRETER_SWORN.equals(wsoType)) {
            AppendUtils.append(buffer,
                TranslationUtils.translate(documentI18n, "Interpreter_Read"));
        } else {
            if (!EMPTY_STRING
                .equals(((LeafEventXmlNode) wsOptions.get("E20935_WR_Number")).getValue())) {
                AppendUtils.append(buffer,
                    TranslationUtils.translate(documentI18n, WITNESS_NUMBER));
                AppendUtils.append(buffer, SPACE);
                AppendUtils.append(buffer,
                    ((LeafEventXmlNode) wsOptions.get("E20935_WR_Number")).getValue());
                AppendUtils.append(buffer, SPACE);
                AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "Read"));
            }
        }
    }
}
