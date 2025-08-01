package uk.gov.hmcts.pdda.web.publicdisplay.storage.pub;

import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.Identifiable;

/**
 * <p/>
 * Title: Any object that a Storer can store.

 * <p/>
 * <p/>
 * Description: A Storeable object is one that is Identifiable, that is it has an AbstractURI
 * associated with it and which contains textual content that can be persisted.


 * To encourage cleaner interfaces Storeable objects are required to implement the store() method.
 * This is <b>not</b> a callback method.#


 * Copyright: Copyright (c) 2003

 * <p/>
 * <p/>
 * Company: Electronic Data Systems

 * @author Neil Ellis
 * @version $Revision: 1.6 $
 */
public interface Storeable extends Identifiable {
    String DOCUMENT = "document";

    String ROTATION_SET = "rotationset";

    /**
     * Returns the contents of the Storeable object which we wish to persist.

     * @return the textual content of the object.
     */
    String getRenderedString();

    /**
     * The store() method is included here and typically this would call
     * StorerFactory.getInstance().store(this). It exists so that objects not related to the
     * persistence of Storeables can request the storing of the object.
     */
    void store();
}
