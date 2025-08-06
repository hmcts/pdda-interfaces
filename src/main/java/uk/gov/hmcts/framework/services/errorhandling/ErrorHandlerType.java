package uk.gov.hmcts.framework.services.errorhandling;

/**

 * Title: ErrorHandlerType.


 * Description:


 * Copyright: Copyright (c) 2002


 * Company: EDS

 * @author Pete Raymond
 * @version 1.0
 */

public class ErrorHandlerType {
    String type;

    ErrorHandlerType(String typeDescription) {
        type = typeDescription;
    }

    @Override
    public final String toString() {
        return type;
    }
}
