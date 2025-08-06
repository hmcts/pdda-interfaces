package uk.gov.hmcts.pdda.web.publicdisplay.types.document;

import uk.gov.hmcts.pdda.business.services.publicdisplay.data.ejb.PdDataControllerBean;
import uk.gov.hmcts.pdda.common.publicdisplay.data.Data;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.AbstractUri;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.DisplayDocumentUri;
import uk.gov.hmcts.pdda.web.publicdisplay.rendering.Renderable;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.priv.impl.DisplayStoreControllerBean;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.Storeable;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.Storer;
import uk.gov.hmcts.pdda.web.publicdisplay.types.AbstractRenderAndStoreableType;

/**
 * <p/>
 * Title: DisplayDocument.

 * <p/>
 * <p/>
 * Description: The DisplayDocument class encapsulates a concrete instance of a Document. Or in the
 * domain langauge: it is a specific list associated with a court house and one or many courtrooms.


 * A DisplayDocument is described by the DisplayDocumentURI, this can be considered to be a primary
 * key for the DisplayDocument.


 * The DisplayDocument extends AbstractRenderAndStoreableType which means that it can be accepted by
 * a Renderer for rendering and a Storer for storing. Before rendering the DisplayDocument will need
 * to have data added to it this can be done through the fetchData() method. Once the data is
 * fetched then the render() method is called which results in the rendered text being stored within
 * the document ready for the store() method to be called which then stores the rendered document().
 * Since the DisplayDocument implements Creatable through the AbstractRenderAndStoreableType
 * superclass we provide a create() method that will create a concrete, rendered and stored display
 * document, this method calls all the methods mentioned above.


 * Copyright: Copyright (c) 2003

 * <p/>
 * <p/>
 * Company: Electronic Data Systems

 * @author Neil Ellis
 * @version $Revision: 1.8 $
 */
public class DisplayDocument extends AbstractRenderAndStoreableType {

    private static final long serialVersionUID = -4924908892058833100L;

    private final PdDataControllerBean dataDelegate;

    private final DisplayDocumentUri uri;

    /**
     * Creates the DisplayDocument from it's uri.

     * @param uri the URI for the document.

     * @pre uri != null
     */
    public DisplayDocument(final DisplayDocumentUri uri, PdDataControllerBean pdDataControllerBean,
        DisplayStoreControllerBean displayStoreControllerBean) {
        super(displayStoreControllerBean);
        this.uri = uri;
        this.dataDelegate = pdDataControllerBean;
    }

    // For unit tests
    public DisplayDocument(final DisplayDocumentUri uri, PdDataControllerBean pdDataControllerBean,
        DisplayStoreControllerBean displayStoreControllerBean, Storer storer) {
        super(displayStoreControllerBean, storer);
        this.uri = uri;
        this.dataDelegate = pdDataControllerBean;
    }

    /**
     * Returns the data of any associated with the document.

     * @return Returns the data.
     */
    public Data getData() {
        return data;
    }

    /**
     * Returns the renderable type.

     * @return the renderable type.
     */
    @Override
    public String getRenderableType() {
        return Renderable.DOCUMENT;
    }

    /**
     * Returns the storeable type.

     * @return the storeable type.
     */
    public String getStoreableType() {
        return Storeable.DOCUMENT;
    }

    /**
     * Returns the URI of this object.

     * @return the URI of this object.

     * @post return != null
     */
    @Override
    public AbstractUri getUri() {
        return uri;
    }

    /**
     * Run the document through it's lifecycle.

     * @see uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.Createable
     */
    @Override
    public void create() {
        fetchData();
        render();
        store();
    }

    /**
     * Fetch the data for this document.
     */
    public void fetchData() {

        Data newData = getPdDataControllerBean().getData(uri);
        setData(newData);
    }

    /**
     * Set the data associated with the document.

     * @param data the data associated with the document.

     * @pre data != null
     */
    private void setData(final Data data) {
        this.data = data;
    }

    /**
     * Checks the equality of this instance of DisplayDocument with that of the passed in object.

     * @param obj The object to check equality with.

     * @return True if the passed in object is equal to this DisplayDocument.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DisplayDocument) {
            DisplayDocument testable = (DisplayDocument) obj;
            if (testable.getUri().equals(this.getUri())) {
                return isSame(testable);
            }
        }
        return false;
    }

    private boolean isSame(DisplayDocument testable) {
        if (testable.getData() != null && this.getData() != null) {
            return testable.getData().equals(this.getData());
        } else if (testable.getData() == null && this.getData() == null) {
            return true;
        }
        return false;
    }

    /**
     * Gets the hashcode for this instance, dependent solely on URI, not on contained data.

     * @return The hashcode.
     */
    @Override
    public int hashCode() {
        return this.uri.hashCode();
    }

    private PdDataControllerBean getPdDataControllerBean() {
        return dataDelegate;
    }
}
