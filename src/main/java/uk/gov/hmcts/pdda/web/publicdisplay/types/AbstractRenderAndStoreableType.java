package uk.gov.hmcts.pdda.web.publicdisplay.types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.common.publicdisplay.data.Data;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.Identifiable;
import uk.gov.hmcts.pdda.common.publicdisplay.util.Debuggable;
import uk.gov.hmcts.pdda.web.publicdisplay.rendering.Renderable;
import uk.gov.hmcts.pdda.web.publicdisplay.rendering.Renderer;
import uk.gov.hmcts.pdda.web.publicdisplay.rendering.RendererFactory;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.priv.impl.DisplayStoreControllerBean;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.Storeable;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.StoredObject;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.Storer;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.StorerFactory;
import uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.Createable;
import uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.Removeable;

import java.io.Serializable;

/**
 * <p/>
 * Title: An abstract convenience class for objects that can be both rendererd and stored.
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
 * @version $Revision: 1.6 $
 */
public abstract class AbstractRenderAndStoreableType implements Renderable, Storeable, Identifiable,
    Serializable, Debuggable, Createable, Removeable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(AbstractRenderAndStoreableType.class);

    protected Data data;

    private final Renderer renderer = RendererFactory.getInstanceForType(this);

    private Storer storer;

    private String renderedString;

    private boolean beenStored;

    private DisplayStoreControllerBean displayStoreControllerBean;

    private static final int MAXIMUM_DEBUG_TEXT = 80;

    protected AbstractRenderAndStoreableType(
        DisplayStoreControllerBean displayStoreControllerBean) {
        this.displayStoreControllerBean = displayStoreControllerBean;
    }

    // For unit tests
    protected AbstractRenderAndStoreableType(DisplayStoreControllerBean displayStoreControllerBean,
        Storer storer) {
        this(displayStoreControllerBean);
        this.storer = storer;
    }

    /**
     * Sets the rendered string.
     * 
     * @param renderedString the rendered version of the document as a String.
     * @pre renderedString != null
     */
    @Override
    public void setRenderedString(final String renderedString) {
        this.renderedString = renderedString;
    }

    /**
     * Gets the string containing the rendered version of this object.
     * 
     * @return the rendered version as a String.
     */
    @Override
    public String getRenderedString() {
        return renderedString;
    }

    /**
     * remove.
     * 
     * @pre storer != null
     * @see uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.Removeable#remove()
     */
    @Override
    public void remove() {
        LOG.info("remove()");
        debug(LOG);
        getStorer().remove(getDisplayStoreControllerBean(), this);
        debug(LOG);
        LOG.info("remove() finished.");
    }

    /**
     * Render this object in the context supplied.
     * 
     * @pre renderer != null
     */
    @Override
    public void render() {
        LOG.info("render().");
        debug(LOG);
        renderer.render(this);
        debug(LOG);
        LOG.info("render() finished.");
    }

    /**
     * Obtain a refrence to the stored version of this object.
     * 
     * @return the stored reference.
     * @pre storer != null
     */
    public StoredObject retrieve() {
        LOG.info("retrieve()");
        debug(LOG);
        StoredObject storedObject = getStorer().retrieve(getDisplayStoreControllerBean(), getUri());
        debug(LOG);
        LOG.info("retrieve() finished.");

        return storedObject;
    }

    /**
     * Store this object in the context supplied.
     * 
     * @pre storer != null
     */
    @Override
    public void store() {
        LOG.info("store()");
        debug(LOG);
        getStorer().store(getDisplayStoreControllerBean(), this);
        beenStored = true;
        debug(LOG);
        LOG.info("store() finished.");
    }

    /**
     * Log debugging info for this object.
     */
    @Override
    public void debug(Logger logger) {
        if (!logger.isDebugEnabled()) {
            return;
        }

        logger.debug("Debugging: {}", this.getClass());

        if (data != null) {
            logger.debug("This render and storeable has data.");
        }

        if (renderedString != null) {
            logger.debug("This renderable and storeable object has been rendered.");

            if (renderedString.length() < MAXIMUM_DEBUG_TEXT) {
                logger.debug("renderedString= '{}'", renderedString.length());
            } else {
                logger.debug("renderedString= '{}...'",
                    renderedString.substring(0, MAXIMUM_DEBUG_TEXT));
            }
        }

        if (beenStored) {
            logger.debug("This renderable and storeable object has been stored.");
        }
    }

    private DisplayStoreControllerBean getDisplayStoreControllerBean() {
        return displayStoreControllerBean;
    }

    private Storer getStorer() {
        if (storer == null) {
            storer = StorerFactory.getInstance();
        }
        return storer;
    }
}
