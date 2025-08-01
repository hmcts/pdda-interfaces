package uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.exceptions;

import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicDisplayEvent;
import uk.gov.hmcts.pdda.common.publicdisplay.exceptions.PublicDisplayRuntimeException;

/**

 * Title: UnrecognizedEventException.

 * Description:

 * Copyright: Copyright (c) 2003

 * Company: Electronic Data Systems

 * @author Neil Ellis
 * @version $Revision: 1.3 $
 */
public class UnrecognizedEventException extends PublicDisplayRuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new UnrecognizedEventException object.

     * @param event PublicDisplayEvent
     */
    public UnrecognizedEventException(PublicDisplayEvent event) {
        super("Event not recognized: " + event.getClass());
    }
}
