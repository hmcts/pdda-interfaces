package uk.gov.hmcts.pdda.web.publicdisplay.workflow.exceptions;

import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.EventType;
import uk.gov.hmcts.pdda.common.publicdisplay.exceptions.PublicDisplayRuntimeException;

/**
 * <p>
 * Title: UnrecognizedEventException.
 * </p>
 * 
 * <p>
 * Description:
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
 * @version $Revision: 1.3 $
 */
public class UnrecognizedEventException extends PublicDisplayRuntimeException {

    private static final long serialVersionUID = 1L;
 
    /**
     * Creates a new UnrecognizedEventException object.
     * 
     * @param eventType EventType
     */
    public UnrecognizedEventException(EventType eventType, Throwable throwable) {
        super("The rule configuration recieved an unknown event '" + eventType + "'.", throwable);
    }
}
