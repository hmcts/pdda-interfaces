package uk.gov.hmcts.pdda.web.publicdisplay.rendering.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.common.publicdisplay.data.Data;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.DisplayDocumentUri;
import uk.gov.hmcts.pdda.web.publicdisplay.rendering.Renderable;
import uk.gov.hmcts.pdda.web.publicdisplay.rendering.Renderer;
import uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled.AllCaseStatusCompiledRendererDelegate;
import uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled.AllCourtStatusCompiledRendererDelegate;
import uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled.CourtDetailCompiledRendererDelegate;
import uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled.CourtListCompiledRendererDelegate;
import uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled.DailytListCompiledRendererDelegate;
import uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled.DisplayDocumentCompiledRendererDelegate;
import uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled.JuryCurrentStatusCompiledRendererDelegate;
import uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled.SummaryByNameCompiledRendererDelegate;
import uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled.exceptions.DelegateNotFoundException;
import uk.gov.hmcts.pdda.web.publicdisplay.rendering.exceptions.DocumentHasNotHadDataAddedException;
import uk.gov.hmcts.pdda.web.publicdisplay.rendering.exceptions.RenderingException;
import uk.gov.hmcts.pdda.web.publicdisplay.types.document.DisplayDocument;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p/>
 * Title: DisplayDocumentCompiledRenderer is the renderer supplied by the RenderFactory for a
 * display document if template rendering is NOT being used.
 * </p>
 * <p/>
 * <p/>
 * Description:
 * </p>
 * <p/>
 * <p/>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p/>
 * <p/>
 * Company: Electronic Data Systems
 * </p>
 * 
 * @author Will Fardell
 * @version $Revision: 1.3 $
 */
public class DisplayDocumentCompiledRenderer implements Renderer, Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG =
        LoggerFactory.getLogger(DisplayDocumentCompiledRenderer.class);
    private final Map<Object, Object> delegateMap = new ConcurrentHashMap<>();

    /**
     * Instantiate the delegates.
     */
    public DisplayDocumentCompiledRenderer() {
        this.delegateMap.put("allcasestatus", new AllCaseStatusCompiledRendererDelegate());
        this.delegateMap.put("allcourtstatus", new AllCourtStatusCompiledRendererDelegate());
        this.delegateMap.put("courtdetail", new CourtDetailCompiledRendererDelegate());
        this.delegateMap.put("courtlist", new CourtListCompiledRendererDelegate());
        this.delegateMap.put("dailylist", new DailytListCompiledRendererDelegate());
        this.delegateMap.put("jurycurrentstatus", new JuryCurrentStatusCompiledRendererDelegate());
        this.delegateMap.put("summarybyname", new SummaryByNameCompiledRendererDelegate());
    }

    /**
     * Render the display document.
     * 
     * @param renderable the rotation set (cast to Renderable) to render.
     * 
     * @throws RenderingException if the renderer fails to render.
     * @see uk.gov.hmcts.pdda.web.publicdisplay.rendering.Renderer#render(
     *      uk.gov.hmcts.pdda.web.publicdisplay.rendering.Renderable)
     */
    @Override
    public void render(final Renderable renderable) {
        renderDisplayDocument((DisplayDocument) renderable);
    }

    /**
     * Process a display document set and provide a RenderedRotationSet. Please note the template
     * name is not supplied this is decided by the engine itself.
     * </p>
     * 
     * @param displayDocument set the rotation set to be processed.
     */
    private void renderDisplayDocument(final DisplayDocument displayDocument) {
        LOG.debug("renderDisplayDocument()");
        Data data = displayDocument.getData();
        if (data == null) {
            throw new DocumentHasNotHadDataAddedException(displayDocument);
        }

        LOG.debug("Rendering document Html...");
        String renderedString =
            getDelegate(displayDocument).getDisplayDocumentHtml(displayDocument);
        LOG.debug("Rendered document Html");
        displayDocument.setRenderedString(renderedString);
    }

    //
    // Utilities
    //

    private DisplayDocumentCompiledRendererDelegate getDelegate(
        final DisplayDocument displayDocument) {
        String documentType =
            ((DisplayDocumentUri) displayDocument.getUri()).getDocumentTypeAsLowerCaseString();
        DisplayDocumentCompiledRendererDelegate delegate =
            (DisplayDocumentCompiledRendererDelegate) delegateMap.get(documentType);
        if (delegate != null) {
            return delegate;
        }
        throw new DelegateNotFoundException(documentType);
    }
}
