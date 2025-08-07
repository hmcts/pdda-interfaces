package uk.gov.hmcts.pdda.web.publicdisplay.rendering;

import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.Identifiable;
import uk.gov.hmcts.pdda.common.publicdisplay.util.Debuggable;

/**
 * <p/>
 * Title: Something that implements Renderable can be rendered by the rendering component.
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
 * @version $Revision: 1.4 $
 * @see Renderer
 */
public interface Renderable extends Identifiable, Debuggable {
    String DOCUMENT = "document";

    String ROTATION_SET = "rotationset";

    /**
     * Returns a string name for the type of the renderable.
     * 
     * @return a string name for they type of the renderable.
     * 
     * @post return != null
     * @post return.equals(DOCUMENT) || return.equals(ROTATION_SET)
     */
    String getRenderableType();

    /**
     * A callback for renderers.
     * 
     * @param renderedString the result of the rendering.
     * 
     * @pre renderedString != null
     */
    void setRenderedString(String renderedString);

    /**
     * The callback method that any renderable object should support.
     */
    void render();
}
