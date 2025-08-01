package uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.ruleengine.exceptions;

import uk.gov.hmcts.pdda.common.publicdisplay.exceptions.PublicDisplayRuntimeException;

/**

 * Title: RulesConfigurationException.


 * Description:


 * Copyright: Copyright (c) 2002


 * Company: EDS

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
