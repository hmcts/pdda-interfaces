package uk.gov.hmcts.pdda.web.publicdisplay.types.rotationset;

import uk.gov.hmcts.pdda.common.publicdisplay.types.rotationset.DisplayRotationSetData;
import uk.gov.hmcts.pdda.common.publicdisplay.types.rotationset.RotationSetDisplayDocument;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.AbstractUri;
import uk.gov.hmcts.pdda.web.publicdisplay.rendering.Renderable;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.priv.impl.DisplayStoreControllerBean;
import uk.gov.hmcts.pdda.web.publicdisplay.types.AbstractRenderAndStoreableType;

/**
 * <p/>
 * Title: A concrete rotation set linked to a display.

 * <p/>
 * <p/>
 * Description: A DisplayRotationSet encapsulates the list of concrete DisplayDocuments and their
 * timings. It is uniquely described by the DisplayURI contained within the DisplayRotationSetData.
 * The DisplayRotationSetData contained within this class actually contains the DisplayDocumentURIs
 * and timings whereas this class provides all the lifecycle functionality so that it can be passed
 * through a workflow.


 * Note that there is no direct relationship between a DisplayDocument and a DisplayRotationSet.
 * This may seem unusual however they never interact directly it is indirectly through their
 * 'primary keys' the URIs that there is a relationship.


 * The lifecycle of a DisplayRotationSet is simpler than it's counterpart the DisplayDocument, it is
 * first rendered through the render() method and then stored through the store() method. The
 * workflow methods create() and remove() are implemented, the create() method calls the render()
 * and store() methods; the remove() method is in the superclass as this is common to
 * DisplayDocuments and DisplayRotationSets.

 * <p/>
 * Copyright: Copyright (c) 2004

 * <p/>
 * <p/>
 * Company: Electronic Data Systems

 * @author Neil Ellis
 * @version $Revision: 1.9 $
 */
public class DisplayRotationSet extends AbstractRenderAndStoreableType {

    private static final long serialVersionUID = 1L;

    private static final String EOL = "\n";
    private final DisplayRotationSetData displayRotationSetData;

    /**
     * Creates a new DisplayRotationSet object.

     * @param displayRotationSetData the actual data that pertains to the rotation set.
     */
    public DisplayRotationSet(final DisplayRotationSetData displayRotationSetData,
        DisplayStoreControllerBean displayStoreControllerBean) {
        super(displayStoreControllerBean);
        this.displayRotationSetData = displayRotationSetData;
    }

    /**
     * getRenderableType.

     * @see uk.gov.hmcts.pdda.web.publicdisplay.rendering.Renderable#getRenderableType()
     */
    @Override
    public String getRenderableType() {
        return Renderable.ROTATION_SET;
    }

    /**
     * Returns an array of RotationSetDisplayDocuments for this rotation set. These are coupling of
     * a DisplayDocument with it's delay.

     * @return the RotationSetDisplayDocuments within this DisplayRotationSet.
     */
    public RotationSetDisplayDocument[] getRotationSetDisplayDocuments() {
        return displayRotationSetData.getRotationSetDisplayDocuments();
    }

    /**
     * getDisplayType - Get the type of the display.

     * @return a string representing the type of the display.
     */
    public String getDisplayType() {
        return displayRotationSetData.getDisplayType();
    }

    /**
     * getUri.

     * @see uk.gov.hmcts.pdda.common.publicdisplay.types.uri.Identifiable#getUri()
     */
    @Override
    public AbstractUri getUri() {
        return displayRotationSetData.getDisplayUri();
    }

    /**
     * create.

     * @see uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.Createable#create()
     */
    @Override
    public void create() {
        render();
        store();
    }

    /**
     * Returns the contents of the rotation set in a debuggable format.

     * @return the contents of the rotation set in a debuggable format.
     */
    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();

        return buffer.toString();
    }

    /**
     * For the purposes of more efficient debugging, appends the string representation to the
     * supplied buffer.

     * @param buffer The buffer to append to.
     * @param lineIndent The indent to prepend to the string representation.
     */
    public void appendToBuffer(StringBuilder buffer, String lineIndent) {
        String lineIndentToUse = lineIndent;
        buffer.append(lineIndentToUse).append(getUri().toString()).append(EOL);
        RotationSetDisplayDocument[] rsDisplayDocuments = getRotationSetDisplayDocuments();
        lineIndentToUse += "\t";
        for (RotationSetDisplayDocument rsDisplayDocument : rsDisplayDocuments) {
            rsDisplayDocument.appendToBuffer(buffer, lineIndentToUse);
        }

    }
}
