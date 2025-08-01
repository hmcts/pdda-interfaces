package uk.gov.hmcts.pdda.web.publicdisplay.storage.pub;

import org.slf4j.Logger;
import uk.gov.hmcts.pdda.common.publicdisplay.util.Debuggable;

/**
 * <p/>
 * Title: Textual context of a Storeable that has been stored.

 * <p/>
 * <p/>
 * Description: StoredObjects are any text based (at the moment) objects which have been persisted
 * using a Storer. They are returned by the retrieve() method on a Storer and contain the persisted
 * contents of a Storeable object. They do not implement Storeable themselves as they have no
 * associated AbstractURI.

 * A potential change is to associate an AbstractURI with them at the time of retrieval so
 * that they can implement Storeable themselves. Although there is no functional requirement it
 * would improve the symmetry of the code as a StoredObject itself would be Storeable.

 * <p/>
 * Copyright: Copyright (c) 2003

 * <p/>
 * <p/>
 * Company: Electronic Data Systems

 * @author Neil Ellis
 * @version $Revision: 1.6 $
 */
public class StoredObject implements Debuggable {
    private static final int MAXIMUM_DEBUG_TEXT = 80;

    private final String text;

    /**
     * Creates a new StoredObject object.

     * @pre text !=null
     */
    public StoredObject(String text) {
        this.text = text;
    }

    /**
     * Returns the textual content of a stored object.

     * @return the text of the stored object.
     * @post return != null
     */
    public String getText() {
        return text;
    }

    /**
     * debug.

     * @see uk.gov.hmcts.pdda.common.publicdisplay.util.Debuggable#debug(org.apache.log4j.Logger)
     */
    @Override
    public void debug(Logger logger) {
        if (logger.isDebugEnabled()) {
            if (text.length() < MAXIMUM_DEBUG_TEXT) {
                logger.debug("text= '" + text.length() + "'");
            } else {
                logger.debug("text= '" + text.substring(0, MAXIMUM_DEBUG_TEXT) + "...'");
            }
        }
    }
}
