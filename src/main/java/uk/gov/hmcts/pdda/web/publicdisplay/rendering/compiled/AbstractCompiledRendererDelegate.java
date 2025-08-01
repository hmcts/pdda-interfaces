package uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled;

import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.AbstractUri;

import java.io.Serializable;

public abstract class AbstractCompiledRendererDelegate implements Serializable {

    private static final long serialVersionUID = 1L;

    protected static final String SPACE = " ";

    protected static final String SEMI_COLON = ";";

    protected static final String COLON = ":";

    protected static final String DIVEND = "</div>";
    protected static final String SCRIPTEND = "</script>";
    protected static final String TABLE = "<table>";
    protected static final String TABLEEND = "</table>";
    protected static final String TABLEBODY = "<tbody>";
    protected static final String TABLEBODYEND = "</tbody>";
    protected static final String TABLEHEADER = "<thead>";
    protected static final String TABLEHEADEREND = "</thead>";
    protected static final String TABLEDATA = "<td>";
    protected static final String TABLEDATAEND = "</td>";
    protected static final String TABLEROW = "<tr>";
    protected static final String TABLEROWEND = "</tr>";
    protected static final String BLANKSPACE = "&nbsp;";

    protected String getLoggedTimeToRender(AbstractUri uri, Long starttime, String html) {
        return starttime == null ? ""
            : "Rendering \"" + uri + "\"" + " took " + (System.currentTimeMillis() - starttime)
                + " ms to generated " + html.length() + " chars.";
    }
}
