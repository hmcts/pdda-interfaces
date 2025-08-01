
package uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.vos.translation.TranslationBundle;

public final class AppendAttributeFieldsUtils {

    private static final Logger LOG =
        LoggerFactory.getLogger(AppendAttributeFieldsUtils.class);

    private static final String NO_INFORMATION_TO_DISPLAY = "No_Information_To_Display";
    private static final String TABLEDATAEND = "</td>";
    private static final String DIVEND = "</div>";
    private static final String EMPTY_STRING = "";

    private AppendAttributeFieldsUtils() {

    }

    /**
     * Append no information text to buffer.
     */
    public static void appendNoInformation(StringBuilder buffer, TranslationBundle documentI18n) {
        AppendUtils.append(buffer, "<div class=\"no-information\">");
        AppendUtils.append(buffer,
            TranslationUtils.translate(documentI18n, NO_INFORMATION_TO_DISPLAY));
        AppendUtils.appendln(buffer, DIVEND);
    }

    /**
     * Append the judges name.
     */
    public static void appendJudgeName(StringBuilder buffer, String name) {
        AppendUtils.append(buffer, "<td class=\"judge\">");
        AppendUtils.appendSpace(buffer, name);
        AppendUtils.appendln(buffer, TABLEDATAEND);
    }

    /**
     * Append the case number.
     */
    public static void appendCaseNumberNoRestrictions(StringBuilder buffer, Object item) {
        AppendUtils.append(buffer, "<td class=\"case-number\">");
        AppendUtils.appendSpace(buffer, AttributesUtils.getCaseNumber(item));
        AppendUtils.appendln(buffer, TABLEDATAEND);
    }


    public static void appendCaseNumber(StringBuilder buffer, Object item) {
        AppendUtils.append(buffer, "<td class=\"case-number\">");
        appendCaseNumberText(buffer, item);
        AppendUtils.appendln(buffer, TABLEDATAEND);
    }

    public static void appendCaseNumberText(StringBuilder buffer, Object item) {
        // Note this does not update the global $restrictionsApplyToThisList
        // This functionality should be implemented in the calling method
        if (RendererUtils.isReportingRestricted(item)) {
            AppendUtils.appendAsterix(buffer, AttributesUtils.getCaseNumber(item));
        } else {
            AppendUtils.appendSpace(buffer, AttributesUtils.getCaseNumber(item));
        }
    }

    /**
     * Append the hearing description.
     */
    public static void appendHearingDescription(StringBuilder buffer, String description) {
        AppendUtils.append(buffer, "<td class=\"hearing-description\">");
        AppendUtils.appendSpace(buffer, description);
        AppendUtils.appendln(buffer, TABLEDATAEND);
    }

    /**
     * Append the not before time text.

     * @param buffer StringBuilder
     * @param time String
     */
    public static void appendNotBeforeTime(StringBuilder buffer, String time) {
        AppendUtils.append(buffer, "<td class=\"not-before-time\">");
        AppendUtils.appendSpace(buffer, time);
        AppendUtils.appendln(buffer, TABLEDATAEND);
    }

    /**
     * Append the not before judges name.

     * @param buffer StringBuilder
     * @param name String
     */
    public static void appendJudgeNameRestrictedWidth(StringBuilder buffer, String name) {
        AppendUtils.append(buffer,
            "<td class=\"judge\"><div class=\"judge-name-restricted-size\">");
        AppendUtils.appendSpace(buffer, name);
        AppendUtils.appendln(buffer, "</div></td>");
    }

    public static void appendHearingProgress(StringBuilder buffer, TranslationBundle documentI18n,
        int hearingProgress) {
        AppendUtils.append(buffer, "<td class=\"hearing-progress\">");
        appendHearingProgressText(buffer, documentI18n, hearingProgress);
        AppendUtils.appendln(buffer, TABLEDATAEND);
    }

    /**
     * Append the hearing progress.

     * @param buffer StringBuilder
     * @param hearingProgress int
     */
    public static void appendHearingProgressText(StringBuilder buffer,
        TranslationBundle documentI18n,
        int hearingProgress) {
        switch (hearingProgress) {
            case 0:
                AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "To_be_heard"));
                break;
            case 5:
                AppendUtils.appendEmphasize(buffer,
                    TranslationUtils.translate(documentI18n, "In_Progress"));
                break;
            case 8:
                AppendUtils.appendEmphasize(buffer,
                    TranslationUtils.translate(documentI18n, "Adjourned"));
                break;
            case 9:
                AppendUtils.appendEmphasize(buffer,
                    TranslationUtils.translate(documentI18n, "Finished"));
                break;
            default:
                LOG.debug("Invalid hearing progress of " + hearingProgress + " specified.");
                AppendUtils.appendSpace(buffer, EMPTY_STRING);
                break;
        }
    }
}
