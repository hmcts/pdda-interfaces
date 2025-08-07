
package uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.vos.translation.TranslationBundle;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.AllCaseStatusValue;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.AllCourtStatusValue;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.DefendantName;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.nodes.BranchEventXmlNode;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.nodes.LeafEventXmlNode;
import uk.gov.hmcts.pdda.web.publicdisplay.types.document.DisplayDocument;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public abstract class DisplayDocumentCompiledRendererDelegate
    extends DisplayDocumentCompiledRendererDelegateEvents {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(DisplayDocumentCompiledRendererDelegate.class);

    private Date dateToUse;

    protected DisplayDocumentCompiledRendererDelegate() {
        super();
    }

    // Junit constructor
    protected DisplayDocumentCompiledRendererDelegate(Date dateToUse) {
        this();
        this.dateToUse = dateToUse;
    }

    /**
     * This method is implemented by the concret documents to create the html.
     * 
     * @param buffer StringBuilder
     * @param displayDocument DisplayDocument
     */
    protected abstract void appendDisplayDocumentHtml(StringBuilder buffer,
        DisplayDocument displayDocument, TranslationBundle documentI18n, Date date);

    /**
     * Get the html for the display document.
     * 
     * @param displayDocument DisplayDocument
     * @return the html
     */
    public String getDisplayDocumentHtml(DisplayDocument displayDocument) {
        Long starttime = LOG.isDebugEnabled() ? System.currentTimeMillis() : null;
        String html = getHtmlDisplayDocumentHtml(displayDocument);
        LOG.debug(getLoggedTimeToRender(displayDocument.getUri(), starttime, html));
        return html;
    }

    private String getHtmlDisplayDocumentHtml(DisplayDocument displayDocument) {
        StringBuilder buffer = new StringBuilder(1024);
        appendDisplayDocumentHtml(buffer, displayDocument,
            DocumentUtils.geDocumentI18n(displayDocument),
            dateToUse != null ? dateToUse : DateUtils.getDate());
        return buffer.toString();
    }
    
    protected void appendEvent(StringBuilder buffer, Object item, TranslationBundle documentI18n) {
        Object event = null;
        Collection<DefendantName> defendantNames = null;
        // Retrieve event
        if (item instanceof AllCourtStatusValue) {
            AllCourtStatusValue value = (AllCourtStatusValue) item;
            event = value.getEvent();
            defendantNames = ((AllCourtStatusValue) item).getDefendantNames();
        } else if (item instanceof AllCaseStatusValue) {
            LOG.debug("All Case Status item");
            AllCaseStatusValue value = (AllCaseStatusValue) item;
            event = value.getEvent();
            defendantNames = new ArrayList<>();
            defendantNames.add(value.getDefendantName());
            LOG.debug("Event: " + event);
        }

        // Process event
        if (event instanceof BranchEventXmlNode) {
            BranchEventXmlNode branchNode = (BranchEventXmlNode) event;
            // Check event type
            if (branchNode.get("type") != null) {
                String type = ((LeafEventXmlNode) branchNode.get("type")).getValue();

                LOG.debug("Type: " + type);
                appendEvents(buffer, branchNode, documentI18n, defendantNames, type);
            }
        }
    }
}
