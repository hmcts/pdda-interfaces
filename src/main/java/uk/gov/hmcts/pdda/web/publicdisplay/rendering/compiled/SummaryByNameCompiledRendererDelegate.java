package uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.vos.translation.TranslationBundle;
import uk.gov.hmcts.pdda.web.publicdisplay.types.document.DisplayDocument;

import java.util.Date;

public class SummaryByNameCompiledRendererDelegate extends DisplayDocumentCompiledRendererDelegate {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG =
        LoggerFactory.getLogger(SummaryByNameCompiledRendererDelegate.class);

    /**
     * Append the html for the display document.

     * @param buffer the buffer to append to
     * @param displayDocument the document containing the information to render
     */
    @Override
    protected void appendDisplayDocumentHtml(StringBuilder buffer, DisplayDocument displayDocument,
        TranslationBundle documentI18n, Date date) {
        LOG.debug("appendDisplayDocumentHtml()");

        String heading = TranslationUtils.translate(documentI18n, "Summary_By_Name");
        AppendFieldsUtils.appendHtmlHeader(buffer, displayDocument, documentI18n, date,
            "banner-sbn", heading);

        if (RendererUtils.isEmpty(displayDocument)) {
            AppendAttributeFieldsUtils.appendNoInformation(buffer, documentI18n);
        } else {
            AppendFieldsUtils.appendTableHeaders(buffer);
            AppendFieldsUtils.appendTableData(buffer, documentI18n, "Name", "60");
            AppendFieldsUtils.appendTableData(buffer, documentI18n, "In_Court", "25");
            AppendFieldsUtils.appendTableData(buffer, documentI18n, "Not_Before", "15");
            AppendFieldsUtils.appendTableHeadersEnd(buffer);

            String rowType = DocumentUtils.getRowType(null);
            boolean restrictionsApplyToThisList = false;

            for (Object item : AttributesUtils.getTable(displayDocument)) {

                if (RendererUtils.isHideInPublicDisplay(item)) {
                    continue;
                }

                AppendUtils.append(buffer, "<tr class=\"");
                AppendUtils.append(buffer, rowType);
                AppendUtils.appendln(buffer, "\">");
                AppendAttributeFieldsExtensionUtils.appendDefendantName(buffer, item);

                String movedFromCourtSiteRoomName =
                    RendererCourtSiteUtils.getMovedFromCourtSiteRoomName(item, documentI18n);
                if (movedFromCourtSiteRoomName != null) {
                    AppendUtils.append(buffer, "<td>");
                    // Below left as ${item.courtRoomName} because court
                    // site short name can be null
                    AppendUtils.append(buffer,
                        RendererCourtSiteUtils.getCourtSiteRoomName(item, documentI18n),
                        "${item.courtRoomName}");
                    AppendUtils.append(buffer, "<div class=\"moved-highliht\">");
                    AppendUtils.append(buffer,
                        TranslationUtils.translate(documentI18n, "Moved_from"));
                    AppendUtils.append(buffer, " ");
                    AppendUtils.append(buffer, movedFromCourtSiteRoomName);
                    AppendUtils.appendln(buffer, DIVEND + TABLEDATAEND);
                } else {
                    AppendAttributeFieldsExtensionUtils.appendCourtRoomNameOrUnassigned(buffer,
                        documentI18n, item);
                }

                AppendAttributeFieldsUtils.appendNotBeforeTime(buffer,
                    DateUtils.getNotBeforeTimeAsString(item));

                AppendUtils.appendln(buffer, TABLEROWEND);

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

}
