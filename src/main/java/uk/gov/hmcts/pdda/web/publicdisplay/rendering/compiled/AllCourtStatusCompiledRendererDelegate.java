package uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.vos.translation.TranslationBundle;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.AllCourtStatusValue;
import uk.gov.hmcts.pdda.web.publicdisplay.types.document.DisplayDocument;

import java.util.Date;
import java.util.Iterator;

public class AllCourtStatusCompiledRendererDelegate
    extends DisplayDocumentCompiledRendererDelegate {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG =
        LoggerFactory.getLogger(AllCourtStatusCompiledRendererDelegate.class);

    public AllCourtStatusCompiledRendererDelegate() {
        super();
    }
    
    public AllCourtStatusCompiledRendererDelegate(Date dateToUse) {
        super(dateToUse);
    }    
    
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

        String heading = TranslationUtils.translate(documentI18n, "All_Court_Status");
        AppendFieldsUtils.appendHtmlHeader(buffer, displayDocument, documentI18n, date,
            "banner-acs", heading);

        if (RendererUtils.isEmpty(displayDocument)) {
            AppendAttributeFieldsUtils.appendNoInformation(buffer, documentI18n);
        } else {
            AppendFieldsUtils.appendTableHeaders(buffer);
            AppendFieldsUtils.appendTableData(buffer, documentI18n, "Court", "25");
            AppendFieldsUtils.appendTableData(buffer, documentI18n, "Case_No", "10");
            AppendFieldsUtils.appendTableData(buffer, documentI18n, "Name", "45");
            AppendFieldsUtils.appendTableData(buffer, documentI18n, "Status", "20");
            AppendFieldsUtils.appendTableHeadersEnd(buffer);

            appendBody(buffer, displayDocument, documentI18n);
        }

        AppendUtils.appendFooter(buffer);
    }

    private void appendBody(StringBuilder buffer, DisplayDocument displayDocument,
        TranslationBundle documentI18n) {
        String rowType = DocumentUtils.getRowType(null);
        boolean restrictionsApplyToThisList = false;

        Iterator<?> items = AttributesUtils.getTable(displayDocument).iterator();
        while (items.hasNext()) {
            final Object item = items.next();

            AppendUtils.append(buffer, "<tr class=\"");
            AppendUtils.append(buffer, rowType);
            AppendUtils.appendln(buffer, "\">");

            AppendAttributeFieldsExtensionUtils.appendCourtRoomName(buffer,
                RendererCourtSiteUtils.getCourtSiteRoomName(item, documentI18n));

            if (hasInformationForDisplay(item)) {
                AppendAttributeFieldsUtils.appendCaseNumber(buffer, item);
                AppendAttributeFieldsExtensionUtils.appendDefendantNamesRestrictedSize(buffer,
                    documentI18n,
                    AttributesUtils.getDefendantNames(item));

                AppendUtils.append(buffer, "<td class=\"live-status\">");

                if (RendererUtils.hasEvent(item)) {
                    AppendUtils.append(buffer, "<div class=\"liveStatusRestrictedWidth\">");
                    appendEvent(buffer, item, documentI18n);
                    AppendUtils.append(buffer, " ");
                    AppendUtils.append(buffer,
                        TranslationUtils.translate(documentI18n, "at", "time"));
                    AppendUtils.append(buffer, " ");
                    AppendUtils.append(buffer, DateUtils.getEventTimeAsString(item),
                        "${item.eventTimeAsString}");
                    AppendUtils.append(buffer, DIVEND);
                } else {
                    AppendUtils.appendSpace(buffer, "");
                }
                AppendUtils.appendln(buffer, TABLEDATAEND);
            } else {
                AppendUtils.append(buffer, "<td>");
                AppendUtils.appendSpace(buffer, "");
                AppendUtils.appendln(buffer, TABLEDATAEND);

                AppendAttributeFieldsExtensionUtils.appendNoInfomationRow(buffer, documentI18n);

                AppendUtils.append(buffer, "<td>");
                AppendUtils.appendSpace(buffer, "");
                AppendUtils.appendln(buffer, TABLEDATAEND);
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

    private boolean hasInformationForDisplay(Object item) {
        return item instanceof AllCourtStatusValue allCourtStatusValue
            && allCourtStatusValue.hasInformationForDisplay();
    }

}
