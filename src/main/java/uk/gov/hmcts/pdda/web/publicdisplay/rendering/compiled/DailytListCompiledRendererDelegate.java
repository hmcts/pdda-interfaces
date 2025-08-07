package uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.vos.translation.TranslationBundle;
import uk.gov.hmcts.pdda.web.publicdisplay.types.document.DisplayDocument;

import java.util.Date;
import java.util.Iterator;

public class DailytListCompiledRendererDelegate extends DisplayDocumentCompiledRendererDelegate {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG =
        LoggerFactory.getLogger(DailytListCompiledRendererDelegate.class);
    
    /**
     * Append the html for the display document.
     * 
     * @param buffer the buffer to append to
     * @param displayDocument the document containing the information to render
     */
    @Override
    protected void appendDisplayDocumentHtml(StringBuilder buffer, DisplayDocument displayDocument,
        TranslationBundle documentI18n, Date date) {
        LOG.debug("appendDisplayDocumentHtml()");

        String heading = TranslationUtils.translate(documentI18n, "Daily_List");
        AppendFieldsUtils.appendHtmlHeader(buffer, displayDocument, documentI18n, date, "banner-dl",
            heading);

        if (RendererUtils.isEmpty(displayDocument)) {
            AppendAttributeFieldsUtils.appendNoInformation(buffer, documentI18n);
        } else {
            AppendFieldsUtils.appendTableHeaders(buffer);
            AppendFieldsUtils.appendTableData(buffer, documentI18n, "Court", "10");
            AppendFieldsUtils.appendTableData(buffer, documentI18n, "Judge", "15");
            AppendFieldsUtils.appendTableData(buffer, documentI18n, "Name/Case_No.", "40");
            AppendFieldsUtils.appendTableData(buffer, documentI18n, "Type", "15");
            AppendFieldsUtils.appendTableData(buffer, documentI18n, "Not_Before", "10");
            AppendFieldsUtils.appendTableHeadersEnd(buffer);

            String rowType = DocumentUtils.getRowType(null);
            boolean restrictionsApplyToThisList = false;

            Iterator<?> items = AttributesUtils.getTable(displayDocument).iterator();
            while (items.hasNext()) {
                final Object item = items.next();

                AppendUtils.append(buffer, "<tr class=\"");
                AppendUtils.append(buffer, rowType);
                AppendUtils.appendln(buffer, "\">");

                AppendAttributeFieldsExtensionUtils.appendCourtRoomNameOrUnassigned(buffer,
                    documentI18n, item);
                AppendAttributeFieldsUtils.appendJudgeNameRestrictedWidth(buffer,
                    AttributesUtils.getJudgeName(documentI18n, item));
                appendDefendantAndCaseNumber(buffer, documentI18n, item);

                // Append Type Start
                AppendUtils.append(buffer, "<td class=\"hearing-description\">");
                AppendUtils.appendSpace(buffer,
                    AttributesUtils.getHearingDescription(documentI18n, item));
                String movedFromCourtSiteRoomName =
                    RendererCourtSiteUtils.getMovedFromCourtSiteRoomName(item, documentI18n);
                if (movedFromCourtSiteRoomName != null) {
                    AppendUtils.append(buffer, "<div class=\"moved-highlight\">");
                    AppendUtils.append(buffer,
                        TranslationUtils.translate(documentI18n, "Moved_from"));
                    AppendUtils.append(buffer, " ");
                    AppendUtils.append(buffer, movedFromCourtSiteRoomName);
                    AppendUtils.append(buffer, "<div>");
                }
                AppendUtils.appendln(buffer, TABLEDATAEND);
                // Append Type End

                AppendAttributeFieldsUtils.appendNotBeforeTime(buffer,
                    DateUtils.getNotBeforeTimeAsString(item));

                AppendUtils.appendln(buffer, TABLEROWEND);

                AppendAttributeFieldsExtensionUtils.appendDefendantOverspill(buffer, item, rowType);

                // Update loop variables
                rowType = DocumentUtils.getRowType(rowType);
                if (RendererUtils.isReportingRestricted(item)) {
                    restrictionsApplyToThisList = true;
                }
            }

            AppendUtils.appendln(buffer, TABLEBODYEND);
            AppendUtils.appendln(buffer, TABLEEND);
            AppendUtils.appendln(buffer, DIVEND);

            AppendFieldsUtils.appendShowRestrictions(buffer, documentI18n,
                restrictionsApplyToThisList);
        }

        AppendUtils.appendFooter(buffer);
    }

    private void appendDefendantAndCaseNumber(StringBuilder buffer, TranslationBundle documentI18n,
        Object item) {
        AppendUtils.append(buffer, "<td class=\"defendant-and-case-number\">");

        if (RendererUtils.hasDefendants(item)) {
            AppendUtils.appendln(buffer, "<span class=\"defendant-names\">");
            AppendAttributeFieldsExtensionUtils.appendDefendants(buffer, documentI18n,
                AttributesUtils.getDefendantNames(item));
            AppendUtils.append(buffer, "</span> / ");
        } else {
            AppendUtils.append(buffer, "<span class=\"case-title\">");
            AppendUtils.append(buffer, AttributesUtils.getCaseTitle(item), "${item.caseTitle}");
            AppendUtils.append(buffer, "</span> / ");
        }

        AppendUtils.append(buffer, "<span class=\"case-number\">");
        if (RendererUtils.isReportingRestricted(item)) {
            AppendUtils.appendAsterix(buffer, AttributesUtils.getCaseNumber(item));
        } else {
            AppendUtils.append(buffer, AttributesUtils.getCaseNumber(item), "${item.caseNumber}");
        }
        AppendUtils.append(buffer, "</span>");
        AppendUtils.appendln(buffer, TABLEDATAEND);
    }
}
