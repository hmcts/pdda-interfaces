package uk.gov.hmcts.pdda.web.publicdisplay.types;

import uk.gov.hmcts.pdda.business.services.publicdisplay.data.ejb.PdDataControllerBean;
import uk.gov.hmcts.pdda.common.publicdisplay.types.rotationset.DisplayRotationSetData;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.DisplayDocumentUri;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.priv.impl.DisplayStoreControllerBean;
import uk.gov.hmcts.pdda.web.publicdisplay.types.document.DisplayDocument;
import uk.gov.hmcts.pdda.web.publicdisplay.types.rotationset.DisplayRotationSet;

import java.util.HashSet;
import java.util.Set;

/**

 * Title: Changes to rendering.

 * Description:

 * This class is used to carry details of changes to rendering, usually resulting from changes to
 * the display/rotation set configuration made by the end user.

 * Copyright: Copyright (c) 2003

 * Company: EDS

 * @author Bob Boothby
 * @version 1.0
 */
public class RenderChanges {
    private final Set<DisplayRotationSet> displayRotationSetsToStartRendering = new HashSet<>();

    private final Set<DisplayRotationSet> displayRotationSetsToStopRendering = new HashSet<>();

    private final Set<DisplayDocument> documentsToStartRendering = new HashSet<>();

    private final Set<DisplayDocument> documentsToStopRendering = new HashSet<>();

    /**
     * The new display rotation sets to render.

     * @return an array of DisplayRotationSet-s which need rendering.
     */
    public DisplayRotationSet[] getDisplayRotationSetsToStartRendering() {
        return displayRotationSetsToStartRendering.toArray(new DisplayRotationSet[0]);
    }

    /**
     * The display rotation that no longer need rendering.

     * @return an array of DisplayRotationSet-s which no longer need rendering.
     */
    public DisplayRotationSet[] getDisplayRotationSetsToStopRendering() {
        return displayRotationSetsToStopRendering.toArray(new DisplayRotationSet[0]);
    }

    /**
     * The documents that need to be rendererd.

     * @return The documents that need to be rendererd.
     */
    public DisplayDocument[] getDocumentsToStartRendering() {
        return documentsToStartRendering.toArray(new DisplayDocument[0]);
    }

    /**
     * The documents that need to be removed from the rendering remove.

     * @return an array of DisplayDocument-s that need to be removed from the rendering remove.
     */
    public DisplayDocument[] getDocumentsToStopRendering() {
        return documentsToStopRendering.toArray(new DisplayDocument[0]);
    }

    /**
     * Adds a document to the list of documents that need to be rendered.

     * @param doc document to be rendered.
     * @param pdDataControllerBean PdDataControllerBean

     * @return true if the document was not already in the list.
     */
    public boolean addStartDocument(final DisplayDocumentUri doc,
        PdDataControllerBean pdDataControllerBean,
        DisplayStoreControllerBean displayStoreControllerBean) {
        return documentsToStartRendering
            .add(new DisplayDocument(doc, pdDataControllerBean, displayStoreControllerBean));
    }

    /**
     * Adds a set to the list of rotation sets that need to be rendered.

     * @param set the rotation set.

     * @return true if the set was not already in the list.
     */
    public boolean addStartRotationSet(final DisplayRotationSetData set,
        DisplayStoreControllerBean displayStoreControllerBean) {
        return displayRotationSetsToStartRendering
            .add(new DisplayRotationSet(set, displayStoreControllerBean));
    }

    /**
     * Adds a document to the list of documents that should nolonger be rendered.

     * @param doc document to stop rendereding.

     * @return true if the document was not already in the list.
     */
    public boolean addStopDocument(final DisplayDocumentUri doc,
        PdDataControllerBean pdDataControllerBean,
        DisplayStoreControllerBean displayStoreControllerBean) {
        return documentsToStopRendering
            .add(new DisplayDocument(doc, pdDataControllerBean, displayStoreControllerBean));
    }

    /**
     * Adds a set to the list of rotation sets that should nolonger be rendered.

     * @param set the rotation set.

     * @return true if the set was not already in the list.
     */
    public boolean addStopRotationSet(final DisplayRotationSetData set,
        DisplayStoreControllerBean displayStoreControllerBean) {
        return displayRotationSetsToStopRendering
            .add(new DisplayRotationSet(set, displayStoreControllerBean));
    }

    /**
     * Produce a string representation of the render changes.

     * @return a string representation of the render changes.
     */
    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        appendToBuffer(buffer, "");
        return buffer.toString();
    }

    /**
     * For the purposes of more efficient debugging, appends the string representation to the
     * supplied buffer.

     * @param buffer The buffer to append to.
     * @param lineIndent The indent to prepend to the string representation.
     */
    private void appendToBuffer(StringBuilder buffer, String lineIndent) {
        buffer.append("RenderChanges:\nDisplay documents to start rendering:\n");
        appendDisplayDocuments(buffer, this.getDocumentsToStartRendering(), lineIndent);
        buffer.append("Display documents to stop rendering:\n");
        appendDisplayDocuments(buffer, this.getDocumentsToStopRendering(), lineIndent);
        buffer.append("Display Rotation Sets to start rendering:\n");
        appendDisplayRotationSets(buffer, this.getDisplayRotationSetsToStartRendering(),
            lineIndent);
        buffer.append("Display Rotation Sets to stop rendering:\n");
        appendDisplayRotationSets(buffer, this.getDisplayRotationSetsToStopRendering(), lineIndent);
    }

    private void appendDisplayDocuments(StringBuilder buffer, DisplayDocument[] displayDocuments,
        String lineIndent) {
        String lineIndentToUse = lineIndent;
        lineIndentToUse += "\t";
        for (DisplayDocument displayDocument : displayDocuments) {
            buffer.append(lineIndentToUse).append(displayDocument.getUri().toString()).append('\n');
        }
    }

    private void appendDisplayRotationSets(StringBuilder buffer,
        DisplayRotationSet[] displayRotationSets, String lineIndent) {
        String lineIndentToUse = lineIndent;
        lineIndentToUse += "\t";
        for (DisplayRotationSet dispRotationSet : displayRotationSets) {
            dispRotationSet.appendToBuffer(buffer, lineIndentToUse);
        }
    }

}
