package uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.vos.translation.TranslationBundle;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.CourtDetailValue;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.DefendantName;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.PublicNoticeValue;
import uk.gov.hmcts.pdda.web.publicdisplay.types.document.DisplayDocument;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

public class CourtDetailCompiledRendererDelegate extends DisplayDocumentCompiledRendererDelegate {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG =
        LoggerFactory.getLogger(CourtDetailCompiledRendererDelegate.class);
    private static final String DIV_WITH_PADDING = "<div class=\"inter-table-padding\"/>";

    /**
     * Append the html for the display document.

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
                + TranslationUtils.translate(documentI18n, "Detail");

        AppendFieldsUtils.appendHtmlHeader(buffer, displayDocument, documentI18n, date,
            "banner-cdtl", heading);

        if (RendererUtils.isEmpty(displayDocument)) {
            AppendAttributeFieldsUtils.appendNoInformation(buffer, documentI18n);
        } else {
            AppendUtils.append(buffer, "<div class=\"court-detail\">");
            AppendUtils.appendln(buffer, "<div id=\"bodyArea\" class=\"body-area\">");

            Iterator<?> items = AttributesUtils.getTable(displayDocument).iterator();
            while (items.hasNext()) {
                final Object item = items.next();

                AppendUtils.appendln(buffer, "<table class=\"non-scrolling-results\">");
                AppendUtils.appendln(buffer, TABLEHEADER);
                AppendUtils.appendln(buffer, "<tr class=\"column-headers\">");
                AppendUtils.append(buffer, TABLEDATA);
                AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "Judge"));
                AppendUtils.appendln(buffer, TABLEDATAEND);
                AppendUtils.appendln(buffer, TABLEROW);
                AppendUtils.appendln(buffer, TABLEHEADER);
                AppendUtils.appendln(buffer, TABLEBODY);
                AppendUtils.appendln(buffer, "<tr class=\"oddRow\">");
                AppendAttributeFieldsUtils.appendJudgeName(buffer,
                    AttributesUtils.getJudgeName(documentI18n, item));
                AppendUtils.appendln(buffer, TABLEROWEND);
                AppendUtils.appendln(buffer, TABLEBODYEND);
                AppendUtils.appendln(buffer, TABLEEND);

                AppendUtils.appendln(buffer, DIV_WITH_PADDING);

                AppendUtils.appendln(buffer,
                    "<table id=\"initialResultTable\" class=\"non-scrolling-results\">");
                AppendUtils.appendln(buffer, TABLEHEADER);
                AppendUtils.appendln(buffer, "<tr class=\"column-headers\">");

                AppendUtils.append(buffer, TABLEDATA);
                AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "Case_No."));
                AppendUtils.appendln(buffer, TABLEDATAEND);

                AppendUtils.append(buffer, TABLEDATA);
                AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "Name"));
                AppendUtils.appendln(buffer, TABLEDATAEND);

                AppendUtils.append(buffer, TABLEDATA);
                AppendUtils.append(buffer,
                    TranslationUtils.translate(documentI18n, "Hearing_Type"));
                AppendUtils.appendln(buffer, TABLEDATAEND);

                AppendUtils.appendln(buffer, TABLEROWEND);
                AppendUtils.appendln(buffer, "</thead>");
                AppendUtils.appendln(buffer, TABLEBODY);
                AppendUtils.appendln(buffer, "<tr class=\"oddRow\">");

                AppendAttributeFieldsUtils.appendCaseNumberNoRestrictions(buffer, item);
                AppendUtils.append(buffer, "<td class=\"defendant-names\">");
                AppendUtils.appendln(buffer, "<div class=\"defendant-place-holder\">");
                appendScrollingDefendantNames(buffer, AttributesUtils.getDefendantNames(item));
                AppendUtils.append(buffer, DIVEND);
                AppendUtils.appendln(buffer, TABLEDATAEND);
                AppendAttributeFieldsUtils.appendHearingDescription(buffer,
                    AttributesUtils.getHearingDescription(documentI18n, item));
                AppendUtils.appendln(buffer, TABLEROWEND);
                AppendUtils.appendln(buffer, TABLEBODYEND);
                AppendUtils.appendln(buffer, TABLEEND);

                checkAndAppendIfHasEvents(item, buffer, documentI18n);
                
                checkAndAppendIfHasPublicNotice(item, buffer, documentI18n);
            }
            AppendUtils.appendln(buffer, DIVEND + DIVEND);
        }
        appendEndLines(buffer);
    }

    private void checkAndAppendIfHasEvents(Object item, StringBuilder buffer, TranslationBundle documentI18n) {
        if (RendererUtils.hasEvent(item)) {
            AppendUtils.appendln(buffer, DIV_WITH_PADDING);

            AppendUtils.appendln(buffer, "<table class=\"case-progress\">");
            AppendUtils.appendln(buffer, TABLEBODY);
            AppendUtils.appendln(buffer, TABLEROW);

            AppendUtils.append(buffer, "<td class=\"case-progress-time\">");
            AppendUtils.append(buffer,
                TranslationUtils.translate(documentI18n, "Case_progress_as_of"));
            AppendUtils.append(buffer, "<br/>");

            AppendUtils.append(buffer, DateUtils.getEventTimeAsString(item),
                "${item.eventTimeAsString}");
            AppendUtils.appendln(buffer, TABLEDATAEND);

            AppendUtils.append(buffer, "<td class=\"case-progress-status\">");
            appendEvent(buffer, item, documentI18n);
            AppendUtils.appendln(buffer);

            AppendUtils.appendln(buffer, TABLEDATAEND);

            AppendUtils.appendln(buffer, TABLEROWEND);
            AppendUtils.appendln(buffer, TABLEBODYEND);
            AppendUtils.appendln(buffer, TABLEEND);
        }
    }
    
    private void checkAndAppendIfHasPublicNotice(Object item, StringBuilder buffer, TranslationBundle documentI18n) {
        if (hasPublicNotices(item)) {
            AppendUtils.appendln(buffer, DIV_WITH_PADDING);

            AppendUtils.appendln(buffer, "<table id=\"resultTable\" class=\"results\">");
            AppendUtils.appendln(buffer, "<thead><tr class=\"column-headers\">");

            AppendUtils.append(buffer, "<td width=\"100%\">");
            AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "Notices"));
            AppendUtils.appendln(buffer, TABLEDATAEND);
            AppendUtils.appendln(buffer, TABLEROWEND + TABLEHEADEREND);
            AppendUtils.appendln(buffer, TABLEBODY);

            PublicNoticeValue[] notices = getPublicNotices(item);
            for (PublicNoticeValue notice : notices) {
                AppendUtils.appendln(buffer, "<tr class=\"public-notice\">");
                AppendUtils.append(buffer, "<td class=\"public-notice\">");
                AppendUtils.append(buffer, getPublicNoticeDesc(documentI18n, notice),
                    "${notice.publicNoticeDesc}");
                AppendUtils.appendln(buffer, TABLEDATAEND);
                AppendUtils.appendln(buffer, TABLEROWEND);
            }

            AppendUtils.appendln(buffer, TABLEBODYEND);
            AppendUtils.appendln(buffer, TABLEEND);
        }
    }
    
    private void appendEndLines(StringBuilder buffer) {
        AppendUtils.appendln(buffer, "<script language=\"JavaScript\">");
        AppendUtils.appendln(buffer, "var defendantDocumentScroller;");
        AppendUtils.appendln(buffer, "function overriddenInitialise() {");
        AppendUtils.appendln(buffer, "initialise();");
        AppendUtils.appendln(buffer, "try {");
        AppendUtils.appendln(buffer, "defendantsTable.style.visibility='hidden';");
        AppendUtils.appendln(buffer,
            "defendantDocumentScroller = new ScrollingDocument(defendantScroller, "
                + "defendantDisplayArea, defendantsTable);");
        AppendUtils.appendln(buffer, "defendantDocumentScroller.cycle();");
        AppendUtils.appendln(buffer, "defendantsTable.style.visibility='visible';");
        AppendUtils.appendln(buffer, "setInterval('defendantDocumentScroller.cycle()',3000);");
        AppendUtils.appendln(buffer, "} catch(e) {");
        AppendUtils.appendln(buffer, "error(e.message);");
        AppendUtils.appendln(buffer, "}");
        AppendUtils.appendln(buffer, "}");
        AppendUtils.appendln(buffer, "window.onload= overriddenInitialise;");
        AppendUtils.appendln(buffer, "</script>");

        AppendUtils.appendFooter(buffer);
    }

    private void appendScrollingDefendantNames(StringBuilder buffer,
        Collection<DefendantName> nameCollection) {
        AppendUtils.appendln(buffer,
            "<div class=\"defendant-scrolling-area\" id=\"defendantScroller\">");
        AppendUtils.appendln(buffer,
            "<div class=\"defendant-display-area\" id=\"defendantDisplayArea\">");
        AppendUtils.appendln(buffer, "<table class=\"defendant-names\" id=\"defendantsTable\">");
        AppendUtils.appendln(buffer, TABLEBODY);

        if (nameCollection != null) {
            for (Object name : nameCollection) {
                if (RendererUtils.isHideInPublicDisplay(name)) {
                    continue;
                }
                AppendUtils.append(buffer,
                    "<tr><td id=\"defendant\" class=\"scrolling-defendant-name\">");
                AppendUtils.append(buffer, "<div class=\"defendant-name-restricted-size\">");
                AppendUtils.append(buffer, name == null ? "${name}" : name.toString());
                AppendUtils.append(buffer, DIVEND);
                AppendUtils.appendln(buffer, TABLEDATAEND + TABLEROWEND);
            }
        }

        AppendUtils.appendln(buffer, TABLEBODYEND);
        AppendUtils.appendln(buffer, TABLEEND);
        AppendUtils.appendln(buffer, DIVEND);
        AppendUtils.appendln(buffer, DIVEND);
    }

    //
    // Utils
    //

    private String getPublicNoticeDesc(TranslationBundle documeni18n, PublicNoticeValue value) {
        if (value != null) {
            String desc = value.getPublicNoticeDesc();
            if (desc != null) {
                return TranslationUtils.translateData(documeni18n, desc);
            } else {
                LOG.debug("Null Public Notice Description entered.");
            }
        } else {
            LOG.debug("Null PublicNoticeValue entered.");
        }

        return "";
    }

    private PublicNoticeValue[] getPublicNotices(Object item) {
        if (item instanceof CourtDetailValue courtDetailValue) {
            PublicNoticeValue[] notices = courtDetailValue.getPublicNotices();
            if (notices != null) {
                return notices;
            }
        }
        return new PublicNoticeValue[0];
    }

    private boolean hasPublicNotices(Object item) {
        return item instanceof CourtDetailValue courtDetailValue && courtDetailValue.hasPublicNotices();
    }
}
