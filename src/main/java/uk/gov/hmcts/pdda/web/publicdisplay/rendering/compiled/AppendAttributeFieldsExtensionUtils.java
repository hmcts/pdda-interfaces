
package uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled;

import uk.gov.hmcts.pdda.business.vos.translation.TranslationBundle;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.DefendantName;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.nodes.LeafEventXmlNode;

import java.util.Collection;

public final class AppendAttributeFieldsExtensionUtils {

    private static final String NO_INFORMATION_TO_DISPLAY = "No_Information_To_Display";
    private static final String TABLEDATAEND = "</td>";
    private static final String DIVEND = "</div>";
    private static final String EMPTY_STRING = "";
    private static final String TABLEROWEND = "</tr>";
    private static final String BLANKSPACE = "&nbsp;";
    private static final String END_TAG = "\">";
    private static final String TABLEDATACOURTROOM = "<td class=\"court-room-name\">";

    private AppendAttributeFieldsExtensionUtils() {

    }

    /**
     * Append the court room name if assigned else append unassigned text.
     * 
     * @param buffer StringBuilder
     * @param documentI18n TranslationBundle
     * @param item Object
     */
    public static void appendCourtRoomNameOrUnassigned(StringBuilder buffer,
        TranslationBundle documentI18n, Object item) {
        if (RendererUtils.isFloating(item)) {
            AppendUtils.append(buffer, TABLEDATACOURTROOM);
            AppendUtils.appendSpace(buffer, TranslationUtils.translate(documentI18n, "Unassigned"));
            AppendUtils.appendln(buffer, TABLEDATAEND);
        } else {
            AppendUtils.append(buffer, TABLEDATACOURTROOM);
            AppendUtils.appendln(buffer, "<div style=\"word-wrap:break-word;overflow:auto\" class"
                + "=\"court_room_name_restricted_size\">");
            AppendUtils.appendSpace(buffer,
                RendererCourtSiteUtils.getCourtSiteRoomName(item, documentI18n));
            AppendUtils.appendln(buffer, DIVEND);
            AppendUtils.appendln(buffer, TABLEDATAEND);
        }
    }

    /**
     * Append defendant overspill if required.
     * 
     * @param buffer StringBuilder
     * @param item Object
     * @param rowType String
     */
    public static void appendDefendantOverspill(StringBuilder buffer, Object item, String rowType) {
        Collection<DefendantName> nameCollection = AttributesUtils.getDefendantNames(item);
        if (RendererUtils.isDefendantNamesShouldOverspill(nameCollection)) {
            AppendUtils.append(buffer, "<tr class=\"");
            AppendUtils.append(buffer, rowType);
            AppendUtils.appendln(buffer, END_TAG);
            AppendUtils.append(buffer, "<td class=\"defendant-overspill\" colspan=\"100\">");
            boolean firstDefendant = true;
            for (DefendantName defendantName : nameCollection) {
                if (defendantName.isHideInPublicDisplay()) {
                    continue;
                }
                if (firstDefendant) {
                    firstDefendant = false;
                } else {
                    AppendUtils.append(buffer, ", ");
                }
                AppendFieldsUtils.appendDefendantOverspillName(buffer, defendantName);
            }
            AppendUtils.appendln(buffer, TABLEDATAEND);
            AppendUtils.appendln(buffer, TABLEROWEND);
        }
    }

    /**
     * Append the court room name.
     * 
     * @param buffer StringBuilder
     * @param name String
     */
    public static void appendCourtRoomName(StringBuilder buffer, String name) {

        AppendUtils.append(buffer, TABLEDATACOURTROOM);
        AppendUtils.appendln(buffer,
            "<div style=\"word-wrap:break-word;overflow:auto\" class=\"court_room_name-restricted-size\">");
        AppendUtils.appendSpace(buffer, name);
        AppendUtils.appendln(buffer, DIVEND);
        AppendUtils.appendln(buffer, TABLEDATAEND);
    }

    /**
     * Append the no information row.
     * 
     * @param buffer StringBuilder
     * @param documentI18n TranslationBundle
     */
    public static void appendNoInfomationRow(StringBuilder buffer, TranslationBundle documentI18n) {
        AppendUtils.append(buffer, "<td class=\"no-information-row\">");
        AppendUtils.appendSpace(buffer,
            TranslationUtils.translate(documentI18n, NO_INFORMATION_TO_DISPLAY));
        AppendUtils.appendln(buffer, TABLEDATAEND);
    }

    public static void appendDefendantName(StringBuilder buffer, Object item) {
        // Name is passed in template, but it then checks item for reporting
        // restrictions!
        String name = AttributesUtils.getNameWithSurnameFirst(item);

        // Note this method does not set $defendantNamesRequireOverspill this
        // should be delt with by the calling method
        AppendUtils.append(buffer, "<td class=\"defendant-name\">");
        if (RendererUtils.isHideInPublicDisplay(item)) {
            AppendUtils.append(buffer, BLANKSPACE);
        } else if (RendererUtils.isReportingRestricted(item)) {
            AppendUtils.appendAsterix(buffer, name);
        } else {
            AppendUtils.appendSpace(buffer, name);
        }
        AppendUtils.appendln(buffer, TABLEDATAEND);
    }

    public static void appendDefendantNamesRestrictedSize(StringBuilder buffer,
        TranslationBundle documentI18n, Collection<DefendantName> nameCollection) {
        AppendUtils.append(buffer, "<td>");
        appendDefendants(buffer, documentI18n, nameCollection);
        AppendUtils.appendln(buffer, TABLEDATAEND);
    }

    public static void appendDefendants(StringBuilder buffer, TranslationBundle documentI18n,
        Collection<DefendantName> nameCollection) {
        // Note this method does not set $defendantNamesRequireOverspill this
        // should be delt with by the calling method
        if (nameCollection != null) {
            if (RendererUtils.isDefendantNamesShouldOverspill(nameCollection)) {
                AppendUtils.appendEmphasize(buffer,
                    TranslationUtils.translate(documentI18n, "SEE BELOW"));
                AppendUtils.appendln(buffer);
            } else {
                appendDefendantNameNoOverspill(nameCollection, buffer);
            }
        } else {
            AppendUtils.appendln(buffer, BLANKSPACE);
        }
    }

    private static void appendDefendantNameNoOverspill(Collection<DefendantName> nameCollection,
        StringBuilder buffer) {
        int displayedNames = 0;
        for (DefendantName name : nameCollection) {
            if (name.isHideInPublicDisplay()) {
                continue;
            }
            displayedNames++;
            AppendUtils.append(buffer, "<div align= 'left' style=\"word-wrap:break-word;"
                + "overflow:auto\" class=\"defendant-name-restricted-size-250\">");
            AppendUtils.append(buffer, name, "${name}");
            AppendUtils.appendln(buffer, DIVEND);
        }
        if (displayedNames == 0) {
            AppendUtils.appendln(buffer, BLANKSPACE);
        }
    }

    public static void appendDefendantsOrTitle(StringBuilder buffer, TranslationBundle documentI18n,
        Object item) {
        if (RendererUtils.hasDefendants(item)) {
            appendDefendantNamesRestrictedSize(buffer, documentI18n,
                AttributesUtils.getDefendantNames(item));
        } else {
            String caseTitle = AttributesUtils.getCaseTitle(item);
            AppendUtils.append(buffer, "<td class=\"case-title\">");
            AppendUtils.appendSpace(buffer, caseTitle != null ? caseTitle : EMPTY_STRING);
            AppendUtils.appendln(buffer, TABLEDATAEND);
        }
    }

    public static void appendFormattedDate(StringBuilder buffer, LeafEventXmlNode leaf,
        TranslationBundle documentI18n) {
        AppendUtils.append(buffer, leaf.getDay());
        AppendUtils.append(buffer, "-");
        AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, leaf.getMonth()));
        AppendUtils.append(buffer, "-");
        AppendUtils.append(buffer, leaf.getYear());
    }
}
