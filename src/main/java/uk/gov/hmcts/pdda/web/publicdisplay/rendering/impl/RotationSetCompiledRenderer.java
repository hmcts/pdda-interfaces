package uk.gov.hmcts.pdda.web.publicdisplay.rendering.impl;

import uk.gov.hmcts.pdda.web.publicdisplay.rendering.Renderable;
import uk.gov.hmcts.pdda.web.publicdisplay.rendering.Renderer;
import uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled.RotationSetCompiledRendererDelegate;
import uk.gov.hmcts.pdda.web.publicdisplay.rendering.exceptions.RenderingException;
import uk.gov.hmcts.pdda.web.publicdisplay.types.rotationset.DisplayRotationSet;

import java.io.Serializable;

/**
 * <p/>
 * Title: RotationSetCompiledRenderer is the renderer supplied by the RenderFactory for a rotation
 * set if template rendering is NOT being used.
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
 * @see uk.gov.hmcts.pdda.web.publicdisplay.rendering.Renderer
 * @see uk.gov.hmcts.pdda.web.publicdisplay.rendering.RendererFactory
 *      #getInstanceForType(uk.gov.hmcts.pdda.web.publicdisplay.rendering.Renderable)
 */
public class RotationSetCompiledRenderer implements Renderer, Serializable {

    private static final long serialVersionUID = 1L;
    private final RotationSetCompiledRendererDelegate delegate =
        new RotationSetCompiledRendererDelegate();

    /**
     * Render the renderable document.
     * 
     * @param renderable the rotation set (cast to Renderable) to render.
     * 
     * @throws RenderingException if the renderer fails to render.
     * @see uk.gov.hmcts.pdda.web.publicdisplay.rendering.Renderer#render(
     *      uk.gov.hmcts.pdda.web.publicdisplay.rendering.Renderable)
     */
    @Override
    public void render(final Renderable renderable) {
        renderRotationSet((DisplayRotationSet) renderable);
    }

    /**
     * Render the rotation set document.
     * 
     * @param rotationSet the rotation set to render.
     * 
     * @throws RenderingException if the renderer fails to render.
     */
    public void renderRotationSet(final DisplayRotationSet rotationSet) {
        final String result = delegate.getDisplayRotationSetHtml(rotationSet);
        rotationSet.setRenderedString(result);
    }
}
