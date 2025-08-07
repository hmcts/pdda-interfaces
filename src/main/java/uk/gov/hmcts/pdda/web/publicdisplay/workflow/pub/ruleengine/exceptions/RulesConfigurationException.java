package uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.ruleengine.exceptions;

import uk.gov.hmcts.pdda.common.publicdisplay.exceptions.PublicDisplayRuntimeException;

/**
 * <p>
 * Title: RulesConfigurationException.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: EDS
 * </p>
 * 
 * @author Rakesh Lakhani
 * @version 1.0
 */

public class RulesConfigurationException extends PublicDisplayRuntimeException {

    private static final long serialVersionUID = 1L;

    public RulesConfigurationException(String message) {
        super(message);
    }

    public RulesConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
