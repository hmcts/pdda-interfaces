
package uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled;

public final class AppendUtils {

    private static final String NL = "\n";
    private static final String EMPTY_STRING = "";
    private static final String BLANKSPACE = "&nbsp;";
    private static final String DIVEND = "</div>";

    private AppendUtils() {
    }

    /**
     * Append the value.
     */
    public static void append(StringBuilder buffer, Object value) {
        buffer.append(value);
    }

    /**
     * Append the value.
     */
    public static void append(StringBuilder buffer, int value) {
        buffer.append(value);
    }

    /**
     * Append the value or the alternative if value is null. Calls to this method are made where the
     * template makes an unguarded write of an object from the velocity context.
     */
    public static void append(StringBuilder buffer, Object value, Object alternative) {
        if (value != null) {
            append(buffer, value);
        } else {
            append(buffer, alternative);
        }
    }

    /**
     * Append a new line.
     */
    public static void appendln(StringBuilder buffer) {
        append(buffer, NL);
    }

    /**
     * Append the value as a new line.
     */
    public static void appendln(StringBuilder buffer, Object value) {
        append(buffer, value);
        append(buffer, NL);
    }

    /**
     * Append the space if text is null.
     */
    public static void appendSpace(StringBuilder buffer, Object text) {
        if (text != null) {
            String actualText = text.toString();
            if (EMPTY_STRING.equals(actualText)) {
                append(buffer, BLANKSPACE);
            } else {
                append(buffer, actualText);
            }
        } else {
            append(buffer, "&lt;null&gt;");
        }
    }

    /**
     * Append the text followed by an asterix.
     * 
     * @param buffer StringBuilder
     * @param text Object
     */
    public static void appendAsterix(StringBuilder buffer, Object text) {
        append(buffer, text, "${text}");
        appendln(buffer, "<span class=\"asterix\">*</span>");
    }

    /**
     * Emphasize the text.
     */
    public static void appendEmphasize(StringBuilder buffer, String text) {
        append(buffer, "<div class=\"emphasized\">");
        append(buffer, text);
        append(buffer, DIVEND);
    }

    /**
     * Append the document header.
     * 
     * @param buffer StringBuilder
     */
    public static void appendHeader(StringBuilder buffer, String heading) {
        appendln(buffer, "<html>");
        appendln(buffer, "<head>");
        appendln(buffer,
            "<meta http-equiv=\"Page-Enter\" content=\"revealTrans(Duration=0.1,Transition=5)\"/>");

        append(buffer, "<link rel=\"stylesheet\" type=\"text/css\" href=\"");
        appendln(buffer, "css/general.css\"/>");

        append(buffer, "<link rel=\"stylesheet\" type=\"text/css\" href=\"");
        appendln(buffer, "css/table.css\"/>");

        appendln(buffer, "<script type=\"text/javascript\" src=\"js/common.js\">");
        appendln(buffer, "</script>");
        appendln(buffer,
            "<script type=\"text/javascript\" src=\"js/display_document.js\">");
        appendln(buffer, "</script>");

        append(buffer, "<title>Public Display: ");
        append(buffer, heading);
        appendln(buffer, "</title>");
        appendln(buffer, "</head>");
        appendln(buffer, "<body>");
    }

    /**
     * Append the document footer.
     * 
     * @param buffer StringBuilder
     */
    public static void appendFooter(StringBuilder buffer) {
        appendln(buffer, "</body>");
        appendln(buffer, "</html>");
    }

}
