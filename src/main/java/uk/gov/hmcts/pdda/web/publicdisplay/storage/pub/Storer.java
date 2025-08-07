package uk.gov.hmcts.pdda.web.publicdisplay.storage.pub;

import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.AbstractUri;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.priv.impl.DisplayStoreControllerBean;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.exceptions.RemovalException;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.exceptions.RetrievalException;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.exceptions.StoreException;

import java.io.Serializable;

/**
 * <p/>
 * Title: The abstraction of a store for PublicDisplay.
 * </p>
 * <p/>
 * <p/>
 * Description: A Store is used to persist the rendered products of the Public Display component.
 * This interface defines the contract of a Storer that can be returned from the StorerFactory. The
 * Storer works with three objects: AbstractURI which are the classes that act as primary keys for
 * our storeable objects, Storeables which are any object that implements the Storeable interface
 * and whoose contents will be store and finally StoredObjects which are the results of retreiving
 * data from a URI and contain the serialized data from a Storeable.
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
public interface Storer extends Serializable {
    /**
     * Removes a document from the store.
     * 
     * @param uri the URI of the object to remove
     * 
     * @throws RemovalException if the object cannot be removed.
     */
    void remove(DisplayStoreControllerBean displayStoreControllerBean, AbstractUri uri);

    /**
     * Removes a document from the store.
     * 
     * @param storeable the object to remove.
     * 
     * @throws RemovalException if the object cannot be removed.
     */
    void remove(DisplayStoreControllerBean displayStoreControllerBean, Storeable storeable);

    /**
     * Returns a reference to the object supplied as a URI. The reference can be used to directly
     * access the stored object.
     * 
     * @param uri the URI to which the object relates.
     * 
     * @return a reference to the object stored.
     * 
     * @throws RetrievalException if a reference could not be retrieved.
     * @pre uri != null
     * @post return != null
     * @post return.getText() != null
     */
    StoredObject retrieve(DisplayStoreControllerBean displayStoreControllerBean,
        AbstractUri uri);

    /**
     * Stores a document.
     * 
     * @param storeable a RenderedDisplayDocument to place in the store
     * 
     * @throws StoreException if the object could not be stored.
     * @pre storeable != null
     * @pre storeable.getUri() != null
     */
    void store(DisplayStoreControllerBean displayStoreControllerBean, Storeable storeable);

    long lastModified(DisplayStoreControllerBean displayStoreControllerBean,
        AbstractUri uri);

}
