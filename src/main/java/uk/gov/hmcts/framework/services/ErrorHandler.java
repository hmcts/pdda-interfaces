package uk.gov.hmcts.framework.services;

/**
 * <p>
 * Title: ErrorHandler.
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
 * @author Pete Raymond
 * @version 1.0
 */
public interface ErrorHandler {
    String handleError(Throwable exception, Class<?> klass, String errMsg);

    String handleError(Throwable exception, Class<?> klass);
}
