
package uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.vos.translation.TranslationBundle;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.DefendantName;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.nodes.BranchEventXmlNode;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.nodes.LeafEventXmlNode;

import java.util.Collection;

public final class AppendEventsTwentyOneHundredToThirtyUtils {

    private static final Logger LOG =
        LoggerFactory.getLogger(AppendEventsTwentyOneHundredToThirtyUtils.class);

    private static final String SPACE = " ";
    private static final String SEMI_COLON = ";";
    private static final String EMPTY_STRING = "";
    private static final String LEGAL_SUBMISSIONS = "Legal_Submissions";
    private static final String DEFENDANT_NAME = "defendant_name";
    private static final String E30200_LAO_JUDGE_NAME = "E30200_LAO_Reserved_To_Judge_Name";
    private static final String E30100_CASE_RELEASED_UNTIL = "E30100_Case_released_until";
    private static final String E30100_CASE_ADJOURNED_UNTIL = "E30100_Case_adjourned_until";
    private static final String E30200_LAO_PSR_REQUIRED = "E30200_LAO_PSR_Required";
    private static final String E30200_LAO_NOT_RESERVED = "E30200_LAO_Not_Reserved";
    private static final String TRUE = "true";

    private AppendEventsTwentyOneHundredToThirtyUtils() {
    }


    public static void appendEvent21100(StringBuilder buffer, TranslationBundle documentI18n) {
        AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, LEGAL_SUBMISSIONS));
    }

    // OK
    public static void appendEvent21200(StringBuilder buffer, TranslationBundle documentI18n) {
        AppendUtils.append(buffer, TranslationUtils.translate(documentI18n,
            "Reporting_Restrictions._For_details_please_contact_the_Court_Manager"));
    }

    // OK
    public static void appendEvent21201(StringBuilder buffer, TranslationBundle documentI18n) {
        AppendUtils.append(buffer,
            TranslationUtils.translate(documentI18n, "Reporting_Restrictions_Lifted"));
    }

    // OK
    public static void appendEvent30100(StringBuilder buffer, BranchEventXmlNode node,
        TranslationBundle documentI18n) {
        BranchEventXmlNode saOptions =
            (BranchEventXmlNode) node.get("E30100_Short_Adjourn_Options");
        String saoType = ((LeafEventXmlNode) saOptions.get("E30100_SAO_Type")).getValue();

        if (E30100_CASE_RELEASED_UNTIL.equals(saoType)) {
            AppendUtils.append(buffer,
                TranslationUtils.translate(documentI18n, "Case_released_until"));
            AppendUtils.append(buffer, SPACE);
            AppendUtils.append(buffer,
                ((LeafEventXmlNode) saOptions.get("E30100_SAO_Time")).getValue());
        } else if (E30100_CASE_ADJOURNED_UNTIL.equals(saoType)) {
            AppendUtils.append(buffer,
                TranslationUtils.translate(documentI18n, "Case_adjourned_until"));
            AppendUtils.append(buffer, SPACE);
            AppendUtils.append(buffer,
                ((LeafEventXmlNode) saOptions.get("E30100_SAO_Time")).getValue());
        } else {
            AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "Case_Adjourned"));
        }
    }

    // OK
    public static void appendEvent30200(StringBuilder buffer, BranchEventXmlNode node,
        TranslationBundle documentI18n, Collection<DefendantName> nameCollection) {
        
        Integer defOnCaseId = RendererUtils.getDefendantOnCaseId(node);

        if (defOnCaseId == null) {
            LOG.warn("DEFENDANT_ON_CASE_ID is missing or invalid in event node.");
        } else {
            if (!RendererUtils.isHideInPublicDisplay(defOnCaseId, nameCollection)) {
                // Add Defendant name
                AppendUtils.append(buffer, ((LeafEventXmlNode) node.get(DEFENDANT_NAME)).getValue());
                AppendUtils.append(buffer, SEMI_COLON);
                AppendUtils.append(buffer, SPACE);
            }
        }
        
        BranchEventXmlNode laoOptions =
            (BranchEventXmlNode) node.get("E30200_Long_Adjourn_Options");
        String laoType = ((LeafEventXmlNode) laoOptions.get("E30200_LAO_Type")).getValue();

        LOG.debug("{}{}", "laoType: ", laoType);

        // Add LAO Type text
        AppendEventsThirtyTwoHundredLaoUtils.appendLaoTypeText(laoType, documentI18n, buffer,
            laoOptions);

        // Separate listing text from below
        AppendUtils.append(buffer, SEMI_COLON);
        AppendUtils.append(buffer, SPACE);

        // PSR Required
        if (laoOptions.get(E30200_LAO_PSR_REQUIRED) != null
            && TRUE.equalsIgnoreCase(((LeafEventXmlNode) laoOptions.get(E30200_LAO_PSR_REQUIRED)).getValue())) {
            AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "PSR_Required"));
            AppendUtils.append(buffer, SEMI_COLON);
            AppendUtils.append(buffer, SPACE);
        }

        // Reserved?
        if (laoOptions.get(E30200_LAO_NOT_RESERVED) != null
            && TRUE.equalsIgnoreCase(((LeafEventXmlNode) laoOptions.get(E30200_LAO_NOT_RESERVED)).getValue())) {
            AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "Not_Reserved"));
            AppendUtils.append(buffer, SEMI_COLON);
            AppendUtils.append(buffer, SPACE);
        } else if (laoOptions.get(E30200_LAO_JUDGE_NAME) != null && !EMPTY_STRING
            .equals(((LeafEventXmlNode) laoOptions.get(E30200_LAO_JUDGE_NAME)).getValue())) {
            AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "Reserved_to"));
            AppendUtils.append(buffer, SPACE);
            AppendUtils.append(buffer, TranslationUtils.translateData(documentI18n,
                ((LeafEventXmlNode) laoOptions.get(E30200_LAO_JUDGE_NAME)).getValue()));
            AppendUtils.append(buffer, SEMI_COLON);
            AppendUtils.append(buffer, SPACE);
        }
    }

    // OK
    public static void appendEvent30300(StringBuilder buffer, TranslationBundle documentI18n) {
        AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "Case_Closed"));
    }

    // OK
    public static void appendEvent30400(StringBuilder buffer, TranslationBundle documentI18n) {
        AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "Court_Closed"));
    }

    // OK
    public static void appendEvent30500(StringBuilder buffer, TranslationBundle documentI18n) {
        AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "Hearing_finished"));
    }

    public static void appendEvent30600(StringBuilder buffer, BranchEventXmlNode node,
        TranslationBundle documentI18n, Collection<DefendantName> nameCollection) {
        
        Integer defOnCaseId = RendererUtils.getDefendantOnCaseId(node);

        if (defOnCaseId == null) {
            LOG.warn("DEFENDANT_ON_CASE_ID is missing or invalid in event node.");
            AppendUtils.append(buffer,
                TranslationUtils.translate(documentI18n, "Hearing_finished"));
        } else {
            if (RendererUtils.isHideInPublicDisplay(defOnCaseId, nameCollection)) {
                // don't add defendant name
                AppendUtils.append(buffer,
                    TranslationUtils.translate(documentI18n, "Hearing_finished"));
            } else {
                // Add Defendant name
                AppendUtils.append(buffer,
                    TranslationUtils.translate(documentI18n, "Hearing_finished_for"));
                AppendUtils.append(buffer, SPACE);
                AppendUtils.append(buffer, ((LeafEventXmlNode) node.get(DEFENDANT_NAME)).getValue());
            }
        }
    }

}
