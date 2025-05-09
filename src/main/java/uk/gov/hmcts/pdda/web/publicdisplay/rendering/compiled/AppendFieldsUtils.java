
package uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled;

import uk.gov.hmcts.framework.exception.CsUnrecoverableException;
import uk.gov.hmcts.pdda.business.vos.translation.TranslationBundle;
import uk.gov.hmcts.pdda.web.publicdisplay.types.document.DisplayDocument;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public final class AppendFieldsUtils {

    private static final String END_TAG = "\">";
    private static final String TABLEDATAEND = "</td>";
    private static final String TABLEROWEND = "</tr>";
    private static final String DIVEND = "</div>";
    private static final String TABLEEND = "</table>";
    private static final String SPACE = " ";
    private static final String COLON = ":";
    private static final String TABLEHEADER = "<thead>";
    private static final String TABLEHEADEREND = "</thead>";
    private static final String TABLEBODY = "<tbody>";

    private AppendFieldsUtils() {

    }

    public static void appendHtmlHeader(StringBuilder buffer, DisplayDocument displayDocument,
        TranslationBundle documentI18n, Date date, String headerImageName, String heading) {
        AppendUtils.appendln(buffer, "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2//EN\">");
        AppendUtils.appendHeader(buffer, heading);
        appendHeading(buffer, displayDocument, documentI18n, date, headerImageName, heading);
    }

    /**
     * Append the document heading.
     * 
     * @param buffer StringBuilder
     */
    public static void appendHeading(StringBuilder buffer, DisplayDocument displayDocument,
        TranslationBundle documentI18n, Date date, String headerImageName, String heading) {
        AppendUtils.appendln(buffer, "<form name=\"displayform\">");

        AppendUtils.append(buffer, "<input type=\"hidden\" name=\"listTitleText\" value=\"");
        AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "List"));
        AppendUtils.appendln(buffer, END_TAG);

        AppendUtils.append(buffer, "<input type=\"hidden\" name=\"ofTitleText\" value=\"");
        AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "of"));
        AppendUtils.appendln(buffer, END_TAG);

        AppendUtils.append(buffer, "<input type=\"hidden\" name=\"pageTitleText\" value=\"");
        AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "Page"));
        AppendUtils.appendln(buffer, END_TAG);

        AppendUtils.appendln(buffer, "</form>");
        AppendUtils.appendln(buffer,
            "<table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">");

        AppendUtils.append(buffer,
            "<tr class=\"header-image\" height=\"58\" valign=\"middle\" style=\"background-image: url(");

        // Start headerImage inline

        AppendUtils.append(buffer, "header-image/images/");
        AppendUtils.append(buffer, headerImageName);
        AppendUtils.append(buffer, ".gif?x=-1&y=45&size=50&text=");
        try {
            AppendUtils.append(buffer, URLEncoder
                .encode(TranslationUtils.translate(documentI18n, "The_Crown_Court_at"), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // UTF-8 will always be supported
            throw new CsUnrecoverableException("Unsupported encode type UTF-8", e);
        }
        AppendUtils.append(buffer, "+");
        try {
            AppendUtils.append(buffer,
                URLEncoder.encode(
                    RendererCourtSiteUtils.getCourtName(displayDocument, documentI18n), "UTF-8"),
                "${document.data.courtName}");
        } catch (UnsupportedEncodingException e) {
            // UTF-8 will always be supported
            throw new CsUnrecoverableException("Unsupported encode type UTF-8", e);
        }
        AppendUtils.append(buffer, "&width=1280");

        // End headerImage inline

        AppendUtils.appendln(buffer, ");margin: 0px\">");

        // AppendUtils.appendln(buffer, "<td colspan=\"3\">&nbsp;" + TABLEDATAEND);
        AppendUtils.appendln(buffer,
            "<td colspan=\"3\" style=\"height:58px; width:1280px; background-repeat: no-repeat; "
                + "background-size: contain; background-position: center;\">&nbsp;"
                + TABLEDATAEND);

        AppendUtils.appendln(buffer, TABLEROWEND);
        AppendUtils.appendln(buffer, "<tr class=\"navbar\">");

        AppendUtils.append(buffer,
            "<td align=\"left\" width=\"33.33%\" class=\"last-updated-date\">");
        AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "Last_Updated"));
        AppendUtils.append(buffer, " ");

        appendLastUpdatedDate(buffer, documentI18n, date);

        AppendUtils.appendln(buffer, TABLEDATAEND);

        AppendUtils.append(buffer,
            "<td align=\"center\" width=\"33.33%\" class=\"list-info\" id=\"listInfo\">");
        AppendUtils.append(buffer, "<span class=\"list-info\" id=\"listText\">&nbsp;</span>");
        AppendUtils.append(buffer, "<span class=\"list-info\" id=\"listInfoDoc\">&nbsp;</span>");
        AppendUtils.append(buffer, "<span class=\"list-info\" id=\"ofText\">&nbsp;</span>");
        AppendUtils.append(buffer,
            "<span class=\"list-info\" id=\"listInfoDocTotal\">&nbsp;</span>");
        AppendUtils.append(buffer, TABLEDATAEND);

        AppendUtils.append(buffer, "<td align=\"right\" width=\"33.33%\" class=\"pageInfo\">");
        AppendUtils.append(buffer, "<div align=\"right\" id=\"pageInfo\">");
        AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "Page"));
        AppendUtils.append(buffer, " 1 ");
        AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "of"));
        AppendUtils.append(buffer, " 1" + DIVEND);
        AppendUtils.appendln(buffer, TABLEDATAEND);

        AppendUtils.appendln(buffer, "<td width=\"10\">&nbsp;</td>");
        AppendUtils.appendln(buffer, TABLEROWEND);
        AppendUtils.appendln(buffer, TABLEEND);
        AppendUtils.appendln(buffer, "<br/>");
        AppendUtils.appendln(buffer, "<center>");

        // Start headingText Inline

        AppendUtils.append(buffer, "<div class=\"heading\">");
        AppendUtils.append(buffer, heading);
        AppendUtils.appendln(buffer, DIVEND);

        // End headingText Inline

        AppendUtils.appendln(buffer, "</center>");
        AppendUtils.appendln(buffer, "<br/>");
        AppendUtils.appendln(buffer, DIVEND);
    }

    private static void appendLastUpdatedDate(StringBuilder buffer, TranslationBundle documentI18n,
        Date date) {
        // Example format Thu Nov 17 12:33:51 GMT 2005
        if (date != null) {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(date);

            // Append Day
            appendDay(buffer, documentI18n, calendar);
            AppendUtils.append(buffer, SPACE);
            appendMonth(buffer, documentI18n, calendar);
            AppendUtils.append(buffer, SPACE);
            AppendUtils.append(buffer, calendar.get(Calendar.DAY_OF_MONTH));
            AppendUtils.append(buffer, SPACE);
            AppendUtils.append(buffer, calendar.get(Calendar.HOUR_OF_DAY));
            AppendUtils.append(buffer, COLON);
            AppendUtils.append(buffer, DateUtils.get2digits(calendar.get(Calendar.MINUTE)));
            AppendUtils.append(buffer, COLON);
            AppendUtils.append(buffer, DateUtils.get2digits(calendar.get(Calendar.SECOND)));
            AppendUtils.append(buffer, SPACE);
            AppendUtils.append(buffer, TranslationUtils.translateData(documentI18n,
                calendar.getTimeZone().getDisplayName(false, TimeZone.SHORT)));
            AppendUtils.append(buffer, SPACE);
            AppendUtils.append(buffer, calendar.get(Calendar.YEAR));
        }
    }

    // Append translated Day
    private static void appendDay(StringBuilder buffer, TranslationBundle documentI18n,
        Calendar calendar) {
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "Mon"));
                break;
            case Calendar.TUESDAY:
                AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "Tue"));
                break;
            case Calendar.WEDNESDAY:
                AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "Wed"));
                break;
            case Calendar.THURSDAY:
                AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "Thu"));
                break;
            case Calendar.FRIDAY:
                AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "Fri"));
                break;
            case Calendar.SATURDAY:
                AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "Sat"));
                break;
            case Calendar.SUNDAY:
                AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "Sun"));
                break;
            default:
        }
    }

    // Append translated Month
    private static void appendMonth(StringBuilder buffer, TranslationBundle documentI18n,
        Calendar calendar) {
        String month = DateUtils.getMonth(calendar.get(Calendar.MONTH));
        AppendUtils.append(buffer,
            "???".equals(month) ? month : TranslationUtils.translate(documentI18n, month));
    }

    public static void appendTableHeaders(StringBuilder buffer) {
        AppendUtils.appendln(buffer, "<div id=\"bodyArea\" class=\"body-area\">");
        AppendUtils.appendln(buffer,
            "<table id=\"resultTable\" class=\"results\" border=\"0\" cellpadding=\"3\" cellspacing=\"0\">");

        AppendUtils.appendln(buffer, TABLEHEADER);
        AppendUtils.appendln(buffer, "<tr class=\"column-headers\">");
    }

    public static void appendTableHeadersEnd(StringBuilder buffer) {
        AppendUtils.appendln(buffer, TABLEROWEND);
        AppendUtils.appendln(buffer, TABLEHEADEREND);
        AppendUtils.appendln(buffer, TABLEBODY);
    }

    public static void appendTableData(StringBuilder buffer, TranslationBundle documentI18n,
        String dataName, String dataWidth) {
        AppendUtils.append(buffer, "<td width=\"" + dataWidth + "%\">");
        AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, dataName));
        AppendUtils.appendln(buffer, TABLEDATAEND);
    }

    public static void appendDefendantOverspillName(StringBuilder buffer, Object name) {
        AppendUtils.append(buffer, name, "${name}");
    }

    /**
     * Append the show restrictions text if flag set.
     */
    public static void appendShowRestrictions(StringBuilder buffer, TranslationBundle documentI18n,
        boolean restrictionsApplyToThisList) {
        if (restrictionsApplyToThisList) {
            AppendUtils.append(buffer,
                "<div id=\"reportingRestrictions\" class=\"reporting-restrictions\">* ");
            AppendUtils.append(buffer, TranslationUtils.translate(documentI18n,
                "Reporting_Restrictions_apply_see_Court_Manager_for_details."));
            AppendUtils.appendln(buffer, DIVEND);
        }
    }
}
