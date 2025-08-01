package uk.gov.hmcts.pdda.web.publicdisplay.storage.priv.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.common.publicdisplay.exceptions.PublicDisplayFailureException;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.AbstractUri;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.DisplayDocumentUri;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.DisplayUri;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.Storeable;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.StoredObject;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.Storer;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.exceptions.DocumentNotYetRenderedException;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.exceptions.ObjectDoesNotExistException;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.exceptions.RemovalException;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.exceptions.RetrievalException;

public class DatabaseStorer implements Storer {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseStorer.class);

    private static final String RETRIEVAL_CODE_DELIM = "-";

    private static DatabaseStorer instance = new DatabaseStorer();

    /**
     * Returns a DatabaseStorer.

     * @return a DatabaseStorer.
     */
    public static DatabaseStorer getInstance() {
        return instance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(DisplayStoreControllerBean displayStoreControllerBean, AbstractUri uri) {
        LOG.info("remove({})", uri);

        String retrievalCode = toRetrievalCode(uri);

        if (!displayStoreControllerBean.doesEntryExist(retrievalCode)) {
            throw new ObjectDoesNotExistException(uri, retrievalCode);
        }

        try {
            displayStoreControllerBean.deleteEntry(retrievalCode);
        } catch (Exception e) {
            throw new RemovalException("File removal error.", e);
        }

        LOG.info("End remove()");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(DisplayStoreControllerBean displayStoreControllerBean, Storeable storeable) {
        remove(displayStoreControllerBean, storeable.getUri());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StoredObject retrieve(DisplayStoreControllerBean displayStoreControllerBean, AbstractUri uri) {
        LOG.info("retrieve()");

        String retrievalCode = toRetrievalCode(uri);

        if (!displayStoreControllerBean.doesEntryExist(retrievalCode)) {
            throw new ObjectDoesNotExistException(uri, retrievalCode);
        }

        String content = displayStoreControllerBean.readFromDatabase(retrievalCode);
        if (content == null || content.length() == 0) {
            throw new RetrievalException("Found nothing when retrieving the entry for the uri '" + uri + "'.");
        }

        StoredObject result = new StoredObject(content);
        result.debug(LOG);

        LOG.info("End retrieve()");

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store(DisplayStoreControllerBean displayStoreControllerBean, Storeable storeable) {
        LOG.info("store()");

        if (storeable.getRenderedString() == null) {
            throw new DocumentNotYetRenderedException(storeable);
        }

        final AbstractUri uri = storeable.getUri();
        final String retrievalCode = toRetrievalCode(uri);

        LOG.debug("Writing to database, retrievalCode: '{}'", retrievalCode);
        displayStoreControllerBean.writeToDatabase(retrievalCode, storeable.getRenderedString());

        LOG.info("End store()");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long lastModified(DisplayStoreControllerBean displayStoreControllerBean, AbstractUri uri) {
        LOG.info("lastModified()");

        String retrievalCode = toRetrievalCode(uri);

        if (!displayStoreControllerBean.doesEntryExist(retrievalCode)) {
            return System.currentTimeMillis();
        }

        long lastModified = displayStoreControllerBean.getLastModified(retrievalCode);

        LOG.info("End lastModified()");

        return lastModified;
    }

    /**
     * Accepts a DisplayDocumentURI object and constructs a retrieval code from its values. The form
     * will depend on the subclass of the input.

     * @param uri The AbstractURI to convert
     * @return The retrieval code.
     * @throws PublicDisplayFailureException The input was of an unknown AbstractURI subclass.
     */
    private String toRetrievalCode(AbstractUri uri) {
        if (uri instanceof DisplayDocumentUri) {
            return toRetrievalCode((DisplayDocumentUri) uri);
        } else if (uri instanceof DisplayUri) {
            return toRetrievalCode((DisplayUri) uri);
        } else {
            // Have no idea how to create it so throw an exception.
            throw new PublicDisplayFailureException(
                "Uri '" + uri + "' was not an instance of DisplayDocumentURI or DisplayURI.");
        }
    }

    /**
     * Accepts a DisplayDocumentURI object and constructs a retrieval code from its values. Will be of
     * the form:

     * <pre>
     * "documents-court&lt;courtId&gt;[-&lt;courtRoomId&gt;|u]+-&lt;documentType&gt;"
     * </pre>

     * @param uri The DisplayDocumentURI to convert
     * @return The retrieval code.
     */
    private String toRetrievalCode(DisplayDocumentUri uri) {
        StringBuilder sb = new StringBuilder();

        sb.append("documents").append(RETRIEVAL_CODE_DELIM).append("court").append(uri.getCourtId());

        for (int courtRoomId : uri.getCourtRoomIds()) {
            sb.append(RETRIEVAL_CODE_DELIM);
            if (courtRoomId == DisplayDocumentUri.UNASSIGNED) {
                sb.append(DisplayDocumentUri.UNASSIGNED_STRING);
            } else {
                sb.append(courtRoomId);
            }
        }

        sb.append(RETRIEVAL_CODE_DELIM).append(uri.getDocumentType().toString());

        return sb.toString();
    }

    /**
     * Accepts a DisplayURI object and constructs a retrieval code from its values. Will be of the form:

     * <pre>
     * "displays-&lt;courthouseName&gt;-&lt;courtSiteCode&gt;-&lt;location&gt;-&lt;display&gt;"
     * </pre>

     * @param uri The DisplayURI to convert
     * @return The retrieval code.
     */
    private String toRetrievalCode(DisplayUri uri) {
        StringBuilder sb = new StringBuilder();

        sb.append("displays").append(RETRIEVAL_CODE_DELIM).append(uri.getCourthouseName()).append(RETRIEVAL_CODE_DELIM)
            .append(uri.getCourtsiteCode()).append(RETRIEVAL_CODE_DELIM).append(uri.getLocation())
            .append(RETRIEVAL_CODE_DELIM).append(uri.getDisplay());

        return sb.toString();
    }
}
