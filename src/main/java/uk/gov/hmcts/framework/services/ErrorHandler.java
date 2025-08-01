package uk.gov.hmcts.framework.services;

/**

 * Title: ErrorHandler.


 * Description:


 * Copyright: Copyright (c) 2002


 * Company: EDS

 * @author Pete Raymond
 * @version 1.0
 */
public interface ErrorHandler {
    String handleError(Throwable exception, Class<?> klass, String errMsg);

    String handleError(Throwable exception, Class<?> klass);
}
