package uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.vos.translation.TranslationBundle;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.CourtListValue;
import uk.gov.hmcts.pdda.web.publicdisplay.types.document.DisplayDocument;

import java.util.Date;
import java.util.Iterator;

public class CourtListCompiledRendererDelegate extends DisplayDocumentCompiledRendererDelegate {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG =
        LoggerFactory.getLogger(CourtListCompiledRendererDelegate.class);

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
        String courtSiteRoomName =
            RendererCourtSiteUtils.getCourtSiteRoomName(displayDocument, documentI18n);

        // Below left as ${document.data.courtRoomName} because court site short
        // name can be null
        String heading =
            (courtSiteRoomName == null ? "${document.data.courtRoomName}" : courtSiteRoomName) + " "
                + TranslationUtils.translate(documentI18n, "List", "Court List");
        AppendFieldsUtils.appendHtmlHeader(buffer, displayDocument, documentI18n, date, "banner-dl",
            heading);

        if (RendererUtils.isEmpty(displayDocument)) {
            AppendAttributeFieldsUtils.appendNoInformation(buffer, documentI18n);
        } else {
            AppendFieldsUtils.appendTableHeaders(buffer);
            AppendFieldsUtils.appendTableData(buffer, documentI18n, "Case_No", "15");
            AppendFieldsUtils.appendTableData(buffer, documentI18n, "Name", "45");
            AppendFieldsUtils.appendTableData(buffer, documentI18n, "Type", "15");
            AppendFieldsUtils.appendTableData(buffer, documentI18n, "Not_Before", "10");
            AppendFieldsUtils.appendTableData(buffer, documentI18n, "Status", "15");
            AppendFieldsUtils.appendTableHeadersEnd(buffer);

            addBody(buffer, displayDocument, documentI18n);
        }

        AppendUtils.appendFooter(buffer);

    }

    private void addBody(StringBuilder buffer, DisplayDocument displayDocument,
        TranslationBundle documentI18n) {
        String rowType = DocumentUtils.getRowType(null);
        boolean restrictionsApplyToThisList = false;

        Iterator<?> items = AttributesUtils.getTable(displayDocument).iterator();
        while (items.hasNext()) {
            final Object item = items.next();

            AppendUtils.append(buffer, "<tr class=\"");
            AppendUtils.append(buffer, rowType);
            AppendUtils.appendln(buffer, "\">");

            AppendAttributeFieldsUtils.appendCaseNumber(buffer, item);
            AppendAttributeFieldsExtensionUtils.appendDefendantsOrTitle(buffer, documentI18n, item);
            AppendAttributeFieldsUtils.appendHearingDescription(buffer,
                AttributesUtils.getHearingDescription(documentI18n, item));
            AppendAttributeFieldsUtils.appendNotBeforeTime(buffer,
                DateUtils.getNotBeforeTimeAsString(item));

            String movedFromCourtSiteRoomName =
                RendererCourtSiteUtils.getMovedFromCourtSiteRoomName(item, documentI18n);
            if (movedFromCourtSiteRoomName != null) {
                AppendUtils.append(buffer, "<td>");
                AppendUtils.append(buffer, "<div class=\"hearing-progress\">");
                AppendAttributeFieldsUtils.appendHearingProgressText(buffer, documentI18n,
                    AttributesUtils.getHearingProgress(item));
                AppendUtils.append(buffer, DIVEND);
                AppendUtils.append(buffer, "<div class=\"moved-highlight\">");
                if (isListedInThisCourtRoom(item)) {
                    AppendUtils.append(buffer,
                        TranslationUtils.translate(documentI18n, "Moved_from"));
                    AppendUtils.append(buffer, " ");
                    AppendUtils.append(buffer, movedFromCourtSiteRoomName);
                } else {
                    AppendUtils.append(buffer,
                        TranslationUtils.translate(documentI18n, "Moved_to"));
                    AppendUtils.append(buffer, " ");
                    // Below left as ${item.courtRoomName} because court
                    // site short name can be null
                    AppendUtils.append(buffer,
                        RendererCourtSiteUtils.getCourtSiteRoomName(item, documentI18n),
                        "${item.courtRoomName}");
                }
                AppendUtils.append(buffer, DIVEND);
                AppendUtils.appendln(buffer, TABLEDATAEND);
            } else {
                AppendAttributeFieldsUtils.appendHearingProgress(buffer, documentI18n,
                    AttributesUtils.getHearingProgress(item));
            }
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

    private boolean isListedInThisCourtRoom(Object item) {
        return item instanceof CourtListValue courtListValue && courtListValue.isListedInThisCourtRoom();
    }
}
