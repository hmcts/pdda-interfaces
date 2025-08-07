package uk.gov.hmcts.pdda.web.publicdisplay.rendering;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.web.publicdisplay.rendering.impl.DisplayDocumentCompiledRenderer;
import uk.gov.hmcts.pdda.web.publicdisplay.rendering.impl.RotationSetCompiledRenderer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p/>
 * Title: The Factory class used to obtain a Renderer.
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
 * @author Neil Ellis
 * @version $Revision: 1.7 $
 * @see Renderer
 */
public class RendererFactory {
    private static final Logger LOG = LoggerFactory.getLogger(RendererFactory.class);

    private static Map<Object, Object> rendererInstances = new ConcurrentHashMap<>();

    // Initialisation
    static {
        rendererInstances.put("document", new DisplayDocumentCompiledRenderer());
        rendererInstances.put("rotationset", new RotationSetCompiledRenderer());

    }

    protected RendererFactory() {
        // Protected constructor
    }

    /**
     * Obtain a renderer for a particular Renderable.
     * 
     * @param type a Renderable document.
     * 
     * @return a renderer.
     * 
     * @pre type instanceof DisplayDocument || type instanceof DisplayRotationSet
     * @post type instanceof DisplayDocument implies return instanceof DisplayDocumentRenderer
     * @post type instanceof DisplayRotationSet implies return instanceof RotationSetRenderer
     * @post return != null
     */
    public static Renderer getInstanceForType(final Renderable type) {
        LOG.debug("getInstanceForType()");
        return (Renderer) rendererInstances.get(type.getRenderableType());
    }
}
