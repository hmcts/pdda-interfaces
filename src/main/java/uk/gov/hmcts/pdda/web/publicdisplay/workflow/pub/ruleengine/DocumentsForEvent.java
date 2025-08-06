package uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.ruleengine;

import uk.gov.hmcts.pdda.common.publicdisplay.types.document.DisplayDocumentType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**

 * Title: DocumentsForEvent.


 * Description: Object used to return the effected documents from the rule engine


 * Copyright: Copyright (c) 2003


 * Company: EDS

 * @author Rakesh Lakhani
 * @version $Id: DocumentsForEvent.java,v 1.4 2006/06/05 12:32:37 bzjrnl Exp $
 */

public class DocumentsForEvent {
    // Internal array to store the Documents effected by the event
    private final List<DisplayDocumentType> documents = new ArrayList<>();

    /**
     * Returns the DisplayDocumentTypes that require re-rendering for the event created. Note: These
     * need to be mapped to specific renders for specific courts.

     * @return DisplayDocumentTypeArray
     */
    public DisplayDocumentType[] getDisplayDocumentTypes() {
        return documents.toArray(new DisplayDocumentType[0]);
    }

    /**
     * Add an effected Display document type to the return set.

     * @param displayDocumentType DisplayDocumentType
     */
    public void addDisplayDocumentType(DisplayDocumentType displayDocumentType) {
        documents.add(displayDocumentType);
    }

    /**
     * Add effected Display document types to the return set.

     * @param displayDocumentTypes DisplayDocumentTypeArray
     */
    public void addDisplayDocumentTypes(DisplayDocumentType... displayDocumentTypes) {
        for (DisplayDocumentType displayDocumentType : Arrays.asList(displayDocumentTypes)) {
            addDisplayDocumentType(displayDocumentType);
        }
    }
}
