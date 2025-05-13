package uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.common.publicdisplay.types.rotationset.RotationSetDisplayDocument;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.AbstractUri;
import uk.gov.hmcts.pdda.web.publicdisplay.types.rotationset.DisplayRotationSet;

public class RotationSetCompiledRendererDelegate extends AbstractCompiledRendererDelegate {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG =
        LoggerFactory.getLogger(RotationSetCompiledRendererDelegate.class);

    /**
     * Get the html for the rotation set.
     * 
     * @param rotationSet DisplayRotationSet
     * @return the html
     */
    public String getDisplayRotationSetHtml(DisplayRotationSet rotationSet) {
        Long starttime = LOG.isDebugEnabled() ? System.currentTimeMillis() : null;
        String html = getHtmlDisplayRotationSetHtml(rotationSet);
        LOG.debug(getLoggedTimeToRender(rotationSet.getUri(), starttime, html));
        return html;
    }

    private String getHtmlDisplayRotationSetHtml(DisplayRotationSet rotationSet) {
        StringBuilder buffer = new StringBuilder(1024);
        appendDisplayRotationSetHtml(buffer, rotationSet);
        return buffer.toString();
    }

    /**
     * Append the html generated for the rotation set to the buffer.
     * 
     * @param buffer the buffer to populate
     * @param rotationSet the rotation set to process
     * 
     */
    protected void appendDisplayRotationSetHtml(StringBuilder buffer,
        DisplayRotationSet rotationSet) {
        // AppendUtils.appendln(buffer, "<!DOCTYPE html>");
        AppendUtils.appendln(buffer,
            "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
        AppendUtils.appendln(buffer, "<html>");
        AppendUtils.appendln(buffer, "<head>");
        AppendUtils.appendln(buffer, "<script type=\"text/javascript\" src=\"js/common.js\">");
        AppendUtils.appendln(buffer, SCRIPTEND);
        AppendUtils.appendln(buffer,
            "<script type=\"text/javascript\" src=\"js/rotation_set.js\">");
        AppendUtils.appendln(buffer, SCRIPTEND);
        AppendUtils.appendln(buffer, "<script type=\"text/javascript\">");
        // The new rotation set.
        AppendUtils.appendln(buffer, "var rotationSet = null;");
        // This is used by other frames to check if the manager is present.
        AppendUtils.appendln(buffer, "var allOkay=true;");
        AppendUtils.appendln(buffer, "function initialize(){");
        AppendUtils.appendln(buffer, "var controllerFrame;");
        AppendUtils.appendln(buffer, "log('Manager initializing...');");
        AppendUtils.appendln(buffer, "initializeRotationSet();");
        AppendUtils.appendln(buffer, "setTimeout('reloadPage()',managerReload);");
        AppendUtils.appendln(buffer, "log('Manager initialized.');");
        AppendUtils.appendln(buffer, "controllerFrame = getFrameElement('lhs.controller');");
        AppendUtils.appendln(buffer, "controllerFrame.contentWindow.rotator.serverOkay();");
        AppendUtils.appendln(buffer, "}");
        // This is used to constantly check for new rotation sets.
        AppendUtils.appendln(buffer, "function reloadPage(){");
        AppendUtils.appendln(buffer, "window.location.href=\"/FileServlet?uri=\"+getDisplayId();");
        AppendUtils.appendln(buffer, "}");
        AppendUtils.appendln(buffer, "window.onload = initialize;");
        AppendUtils.appendln(buffer, SCRIPTEND);
        AppendUtils.appendln(buffer, "<script type=\"text/javascript\">");
        AppendUtils.appendln(buffer, "function initializeRotationSet(){");
        AppendUtils.appendln(buffer, "var controllerFrame;");
        AppendUtils.appendln(buffer, "var rotationSet = new RotationSet();");
        AppendUtils.appendln(buffer, "var tempDocument = null;");
        AppendUtils.appendln(buffer, "log('Initializing RotationSet');");
        AppendUtils.append(buffer, "rotationSet.displayType= '");
        AppendUtils.append(buffer, getDisplayType(rotationSet), "${set.displayType}");
        AppendUtils.appendln(buffer, "';");

        RotationSetDisplayDocument[] documents = getRotationSetDisplayDocuments(rotationSet);
        for (RotationSetDisplayDocument document: documents) {
            AppendUtils.append(buffer, "tempDocument = new DocumentReference('");
            AppendUtils.append(buffer, getDisplayDocumentUri(document),
                "${doc.displayDocumentURI}");
            AppendUtils.append(buffer, "',");
            AppendUtils.append(buffer, getPageDelay(document), "${doc.pageDelay}");
            AppendUtils.appendln(buffer, ");");

            AppendUtils.appendln(buffer, "rotationSet.add(tempDocument);");
        }

        AppendUtils.appendln(buffer, "controllerFrame = getFrameElement('lhs.controller');");
        AppendUtils.appendln(buffer, "if(controllerFrame.contentWindow.rotator){");
        AppendUtils.appendln(buffer,
            "controllerFrame.contentWindow.rotator.changeRotationSet(rotationSet);");
        AppendUtils.appendln(buffer, "log('Initialized RotationSet');");
        AppendUtils.appendln(buffer, "}else{");
        AppendUtils.appendln(buffer,
            "log('Could not initialize rotator as controller frame not present.');");
        AppendUtils.appendln(buffer, "}");
        AppendUtils.appendln(buffer, "}");
        AppendUtils.appendln(buffer, SCRIPTEND);
        AppendUtils.appendln(buffer, "<title>manager</title>");
        AppendUtils.appendln(buffer, "</head>");
        AppendUtils.appendln(buffer, "<body bgcolor=\"lightgreen\">");
        AppendUtils.appendln(buffer, "<h3>Manager</h3>");
        AppendUtils.appendln(buffer, "<pre id=\"log\"></pre>");
        AppendUtils.appendln(buffer, "</body>");
        AppendUtils.appendln(buffer, "</html>");
    }

    //
    // Utilites to imitate the behaviour of the template engine. All values
    // need
    // to
    // be checked for null!
    //

    private RotationSetDisplayDocument[] getRotationSetDisplayDocuments(
        DisplayRotationSet rotationSet) {
        if (rotationSet != null) {
            RotationSetDisplayDocument[] documents = rotationSet.getRotationSetDisplayDocuments();
            if (documents != null) {
                return documents;
            }
        }
        return new RotationSetDisplayDocument[0];
    }

    private String getDisplayType(DisplayRotationSet rotationSet) {
        if (rotationSet != null) {
            String displayType = rotationSet.getDisplayType();
            if (displayType != null) {
                return rotationSet.getDisplayType();
            }
        }
        return null;
    }

    private String getDisplayDocumentUri(RotationSetDisplayDocument document) {
        if (document != null) {
            AbstractUri uri = document.getDisplayDocumentUri();
            if (uri != null) {
                return uri.toString();
            }
        }
        return null;
    }

    private String getPageDelay(RotationSetDisplayDocument document) {
        if (document != null) {
            return String.valueOf(document.getPageDelay());
        }
        return null;
    }
}
