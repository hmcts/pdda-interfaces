package uk.gov.hmcts.pdda.web.publicdisplay.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.services.publicdisplay.data.ejb.PdDataControllerBean;
import uk.gov.hmcts.pdda.common.publicdisplay.events.types.CourtRoomIdentifier;
import uk.gov.hmcts.pdda.common.publicdisplay.types.document.DisplayDocumentType;
import uk.gov.hmcts.pdda.common.publicdisplay.types.rotationset.DisplayRotationSetData;
import uk.gov.hmcts.pdda.common.publicdisplay.types.rotationset.RotationSetDisplayDocument;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.DisplayDocumentUri;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.priv.impl.DisplayStoreControllerBean;
import uk.gov.hmcts.pdda.web.publicdisplay.types.RenderChanges;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * Title: Display Document reference managing class.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * This class is responsible for the management of references to Display Documents as represented as
 * <code>DisplayDocumentURI</code>.
 * </p>
 * <p>
 * For data changes it provides information about which documents need to be rerendered.
 * </p>
 * <p>
 * For configuration changes it keeps track of how many Display Rotation Sets refer to the document
 * and provides information about which Display Document need to either be rendered for the first
 * time or stop being rendered at all. It does this by accumulating both information on the
 * reference counts and the fact that it has not yet been asked to populate a RenderChanges. The
 * RenderChanges is populated with all documents that need to stop rendering and all documents that
 * are marked as 'new', when this population is done, the documents marked as new are unmarked.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: EDS
 * </p>
 * 
 * @author Bob Boothby
 * @version 1.0
 */

public class DisplayDocumentReferenceManager {
    private static final Logger LOG =
        LoggerFactory.getLogger(DisplayDocumentReferenceManager.class);

    private final PdDataControllerBean pdDataControllerBean;

    private final DisplayStoreControllerBean displayStoreControllerBean;

    // Store the Display Documents by court room and document type.
    private final Map displayDocumentUrisByCourtRoomAndDocumentType = new ConcurrentHashMap();

    // Store reference (usage) counts for the given Display Document.
    private final Map displayDocumentUriCounts = new ConcurrentHashMap();
    
    private static final String UNCHECKED = "unchecked";

    /**
     * Construct an instance of the class.
     */
    public DisplayDocumentReferenceManager(PdDataControllerBean pdDataControllerBean,
        DisplayStoreControllerBean displayStoreControllerBean) {
        this.pdDataControllerBean = pdDataControllerBean;
        this.displayStoreControllerBean = displayStoreControllerBean;
    }

    /**
     * Return the set of Display Documents currently held in configuration for the given combination
     * of document types and court room.
     * 
     * @param documentTypes The different types of Display Document to find.
     * @param courtRoom The court room for which to find relevant Display Documents.
     * @return A RenderChanges instance containing the relevant Display Documents.
     */
    @SuppressWarnings(UNCHECKED)
    public RenderChanges getRenderChanges(DisplayDocumentType[] documentTypes,
        CourtRoomIdentifier courtRoom) {
        LOG.debug("getRenderChanges()");
        // The object to return.
        RenderChanges returnRenderChanges = new RenderChanges();

        // Get all the display documents stored against this key.
        List displayDocumentUris = new ArrayList();
        for (int i = documentTypes.length - 1; i >= 0; i--) {
            displayDocumentUris.addAll(getDdUrisForCourtRoomAndDocumentType(documentTypes[i],
                courtRoom.getCourtRoomId().longValue()));
        }

        // Add the documents to the RenderChanges object.
        Iterator ddUrisIterator = displayDocumentUris.iterator();
        while (ddUrisIterator.hasNext()) {
            returnRenderChanges.addStartDocument((DisplayDocumentUri) ddUrisIterator.next(),
                getPdDataControllerBean(), getDisplayStoreControllerBean());
        }
        return returnRenderChanges;
    }

    /**
     * This method is used to register a an 'interest' in the Display Documents of a Display
     * Rotation Set.
     * 
     * <p>This increments the internal count of references against the display document as long as the
     * count remains above zero it means that the document is relevant in the current configuration.
     * 
     * <p>Please note that the counts are the only thing that are updated as part of this method. Until
     * the fillInRenderChanges() method is called any new Display Documents are not available for
     * address by the
     * <code>getRenderChanges(DisplayDocumentType[] documentTypes, CourtRoomIdentifier courtRoom)</code>
     * method.
     * 
     * @param displayRotationSetData The Display Rotation Set whose Display Documents must be
     *        registered.
     */
    public void addDisplayDocumentReferences(DisplayRotationSetData displayRotationSetData) {
        LOG.debug("addDisplayDocumentReferences({})", displayRotationSetData);
        RotationSetDisplayDocument[] rsDds =
            displayRotationSetData.getRotationSetDisplayDocuments();
        for (int i = rsDds.length - 1; i >= 0; i--) {
            getDisplayDocumentReferenceCounter(rsDds[i].getDisplayDocumentUri()).addReference();
        }
    }

    /**
     * This method is used to deregister a an 'interest' in the Display Documents of a Display
     * Rotation Set.
     * 
     * <p>This decrements the internal count of references against the display document as long as the
     * count remains above zero it means that the document is relevant in the current configuration.
     * 
     * @param displayRotationSetData The Display Rotation Set whose Display Documents must be
     *        deregistered.
     */
    public void removeDisplayDocumentReferences(DisplayRotationSetData displayRotationSetData) {
        LOG.debug("removeDisplayDocumentReferences({})", displayRotationSetData);
        RotationSetDisplayDocument[] rsDds =
            displayRotationSetData.getRotationSetDisplayDocuments();
        for (int i = rsDds.length - 1; i >= 0; i--) {
            getDisplayDocumentReferenceCounter(rsDds[i].getDisplayDocumentUri()).removeReference();
        }
    }

    /**
     * This method is used after the <code>addDisplayDocumentReferences</code> and
     * <code>removeDisplayDocumentReferences</code> have been used, in order to populate the
     * <code>RenderChanges</code> with Display Documents that need to be rendered for the first time
     * or no longer be rendered.
     * 
     * @param renderChanges RenderChanges
     */
    @SuppressWarnings(UNCHECKED)
    public void fillInRenderChanges(RenderChanges renderChanges) {
        // Accumulate dead Display Documents in this set.
        // We are doing this so we dont change the displayDocumentURICounts
        // map while iterating over it (fail-fast iterators.).
        LOG.debug("fillInRenderChanges({})", renderChanges);
        HashSet deadDisplayDocumentUris = new HashSet();

        // Iterate over all the display documents finding those that now
        // have a reference count of zero for stop rendering, and those
        // that are newly created in order to start rendering.
        Set displayDocumentUris = displayDocumentUriCounts.keySet();
        Iterator ddUriIterator = displayDocumentUris.iterator();
        while (ddUriIterator.hasNext()) {
            DisplayDocumentUri displayDocumentUri = (DisplayDocumentUri) ddUriIterator.next();
            DisplayDocumentReferenceCounter counter =
                getDisplayDocumentReferenceCounter(displayDocumentUri);
            if (counter.isNeedsInitialRender()) {
                if (counter.isRelevant()) {
                    // The document is new and relevant so add it..
                    renderChanges.addStartDocument(displayDocumentUri, getPdDataControllerBean(),
                        getDisplayStoreControllerBean());
                    storeDocumentByTypeAndCourts(displayDocumentUri);
                }
                // Else do nothing.
            } else if (!counter.isRelevant()) {
                // The document is no longer relevant so get rid of it.
                renderChanges.addStopDocument(displayDocumentUri, getPdDataControllerBean(),
                    getDisplayStoreControllerBean());
                removeDocumentByTypeAndCourts(displayDocumentUri);
                // Accumulate dead URIs.
                deadDisplayDocumentUris.add(displayDocumentUri);
            }
        }

        // Now we are out of the map iterator, we can remove the URIs
        // from the map.
        ddUriIterator = deadDisplayDocumentUris.iterator();
        while (ddUriIterator.hasNext()) {
            displayDocumentUriCounts.remove(ddUriIterator.next());
        }
    }

    /**
     * This method is used to store a Display Document for later retrieval by the combination of
     * court room and document type. If a document is of type "Court List" for court rooms 2 and 3
     * it will be stored by "Court List for court room 2" and by "Court List for court room 3".
     * 
     * @param displayDocumentUri The Display Document to store.
     */
    @SuppressWarnings({"rawtypes", UNCHECKED})
    private void storeDocumentByTypeAndCourts(DisplayDocumentUri displayDocumentUri) {
        int[] courtRoomIDs = displayDocumentUri.getCourtRoomIds();
        for (int i = courtRoomIDs.length - 1; i >= 0; i--) {
            Set displayDocumentUris = getDdUrisForCourtRoomAndDocumentType(
                displayDocumentUri.getDocumentType(), courtRoomIDs[i]);
            displayDocumentUris.add(displayDocumentUri);
        }
    }

    /**
     * This document is used to remove a Display Document that has been stored by the combination of
     * court room and document type.
     * 
     * @param displayDocumentUri The Display Document to remove.
     */
    private void removeDocumentByTypeAndCourts(DisplayDocumentUri displayDocumentUri) {
        int[] courtRoomIDs = displayDocumentUri.getCourtRoomIds();
        for (int i = courtRoomIDs.length - 1; i >= 0; i--) {
            Set displayDocumentUris = getDdUrisForCourtRoomAndDocumentType(
                displayDocumentUri.getDocumentType(), courtRoomIDs[i]);
            displayDocumentUris.remove(displayDocumentUri);
        }
    }

    /**
     * This method gets the set of Display Documents of a given type and stored against a particular
     * court room.
     * 
     * @param documentType The type of the Display Documents we are after.
     * @param courtRoomId The court rooms to which the relevant to.
     * @return A Set containing <code>DisplayDocumentURI</code>s representing the Display Documents
     *         relevant to the type and court room.
     */
    @SuppressWarnings(UNCHECKED)
    private Set getDdUrisForCourtRoomAndDocumentType(DisplayDocumentType documentType,
        long courtRoomId) {
        String key = getCourtRoomAndDocumentTypeKey(documentType, courtRoomId);
        Set displayDocumentUris = (Set) displayDocumentUrisByCourtRoomAndDocumentType.get(key);

        // Does the set exist for this key?
        if (displayDocumentUris == null) {
            // No, remove and store it.
            displayDocumentUris = new HashSet();
            displayDocumentUrisByCourtRoomAndDocumentType.put(key, displayDocumentUris);
        }
        return displayDocumentUris;
    }

    /**
     * Generate a String key from the document type and court room id.
     * 
     * @param documentType The document type.
     * @param courtRoomId The ID of the court room/
     * @return A String key for use in a map.
     */
    private String getCourtRoomAndDocumentTypeKey(DisplayDocumentType documentType,
        long courtRoomId) {
        StringBuilder returnBuffer =
            new StringBuilder(documentType.toString()).append('-').append(courtRoomId);
        return returnBuffer.toString();
    }

    /**
     * Get the reference counter for a Display Document.
     * 
     * @param displayDocumentUri The Display Document for which to retrieve the counter.
     * @return the relevant reference counter.
     */
    @SuppressWarnings(UNCHECKED)
    private DisplayDocumentReferenceCounter getDisplayDocumentReferenceCounter(
        DisplayDocumentUri displayDocumentUri) {
        DisplayDocumentReferenceCounter counter =
            (DisplayDocumentReferenceCounter) displayDocumentUriCounts.get(displayDocumentUri);

        // Does the counter exist?
        if (counter == null) {
            // Then remove and store it.
            counter = new DisplayDocumentReferenceCounter();
            displayDocumentUriCounts.put(displayDocumentUri, counter);
        }

        return counter;
    }

    private PdDataControllerBean getPdDataControllerBean() {
        return pdDataControllerBean;
    }

    private DisplayStoreControllerBean getDisplayStoreControllerBean() {
        return displayStoreControllerBean;
    }

    /**
     * <p>
     * Title: Display document reference counting class.
     * </p>
     * <p>
     * Description:
     * </p>
     * <p>
     * This class is used to keep count of registered references to a display document. When the
     * references drop to zero, the document is no longer relevant.
     * </p>
     * <p>
     * This class is also responsible for keeping track of the fact of initial creation until
     * queried about it. This allows the reference counter to be used to keep track of references
     * before any decision has to be made about initial rendering.
     * </p>
     * <p>
     * Copyright: Copyright (c) 2003
     * </p>
     * <p>
     * Company: EDS
     * </p>
     * 
     * @author Bob Boothby
     * @version 1.0
     */
    private class DisplayDocumentReferenceCounter {
        // Keep track of the reference count.
        private int count;

        // When first created the counter marks
        // that the Display Document it is tied to
        // needs an initial rendering.
        private boolean needsInitialRender = true;

        /**
         * Add to the reference count.
         */
        private void addReference() {
            count++;
        }

        /**
         * Subtract from the reference count.
         */
        private void removeReference() {
            count--;
        }

        /**
         * Checks whether the document needs an initial rendering. This is effectively an one way
         * flip- flop, when first called this method will return true to signal that the Display
         * Document needs a first render, thereafter it will return false.
         * 
         * @return true if a document needs an initial render.
         */
        private boolean isNeedsInitialRender() {
            if (needsInitialRender) {
                needsInitialRender = false;
                return true;
            } else {
                return false;
            }
        }

        /**
         * Used to check whether there are any outstanding references to the document.
         * 
         * @return true is the Display Document is still relevant for rendering, false otherwise.
         */
        private boolean isRelevant() {
            return count > 0;
        }
    }
}
