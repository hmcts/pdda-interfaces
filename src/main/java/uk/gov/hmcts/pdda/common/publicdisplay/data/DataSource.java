package uk.gov.hmcts.pdda.common.publicdisplay.data;

import jakarta.persistence.EntityManager;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.DisplayDocumentUri;

/**
 * <p>
 * Title: DataSource.
 * </p>
 * 
 * <p>
 * Description:
 * 
 * A DataSource can be used more than once for a given uri.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * 
 * <p>
 * Company: Electronic Data Systems
 * </p>
 * 
 * @author Neil Ellis
 * @version $Revision: 1.5 $
 * 
 * @invariant uri != null
 * @invariant data != null
 */
public abstract class DataSource {
    private final Data data = new Data();

    private DataContext context;

    private final DisplayDocumentUri uri;

    /**
     * Creates a new DataSource object.
     * 
     * @param uri the URI of the DisplayDocument we are retrieving data for.
     * @pre uri != null
     */
    protected DataSource(DisplayDocumentUri uri) {
        this.uri = uri;
    }

    /**
     * Returns the URI of the DisplayDocument we are retrieving data for.
     * 
     * @return the URI of the DisplayDocument we are retrieving data for.
     * @post return != null
     */
    public DisplayDocumentUri getUri() {
        return uri;
    }

    /**
     * Retrieve the data and store internally. Use getData() to access the data afterwards.
     * 
     * @pre uri != null
     * @post getData() != null
     * @see #getData
     */
    public abstract void retrieve(EntityManager entityManager);

    /**
     * Sets the optional DataContext.
     * 
     * @param context the DataContext.
     * @pre context != null
     */
    public void setContext(DataContext context) {
        this.context = context;
    }

    /**
     * Returns the optional data context.
     * 
     * @return the optional data context.
     */
    public DataContext getContext() {
        return context;
    }

    /**
     * Returns the retrieved data.
     * 
     * @return the retrieved data.
     * @post return != null
     */
    public Data getData() {
        return data;
    }

    /**
     * Reset the DataSource so it can be used again. This should be called by the subclasses
     * retrieve() implementation before retrieving data.
     * 
     * @post data.isEmpty()
     */
    public void reset() {
        if (!data.isEmpty()) {
            data.clear();
        }
    }
}
