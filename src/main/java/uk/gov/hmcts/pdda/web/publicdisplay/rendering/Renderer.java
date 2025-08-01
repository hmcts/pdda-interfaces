package uk.gov.hmcts.pdda.web.publicdisplay.rendering;

import java.io.Serializable;

/**
 * <p/>
 * Title: A class that is capable of rendering implemnets this interface.

 * <p/>
 * <p/>
 * Description:

 * <p/>
 * <p/>
 * Copyright: Copyright (c) 2003

 * <p/>
 * <p/>
 * Company: Electronic Data Systems

 * @author Neil Ellis
 * @version $Revision: 1.6 $
 */
@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface Renderer extends Serializable {

    String BASE_URL_PROPERTY = "publicdisplay.web.base_url";

    /**
     * Renders the renderable object passed to it.

     * @pre renderable != null
     * @pre renderable.getUri() != null
     */
    void render(Renderable renderable);

}
