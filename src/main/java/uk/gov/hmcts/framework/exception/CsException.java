package uk.gov.hmcts.framework.exception;

/**
 * <p>
 * Title: CSException.
 * </p>
 * <p>
 * Description: Interface for all CS Hub framework exceptions
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: EDS
 * </p>
 * 
 * @author Kevin Buckthorpe
 * @version 1.0
 */

public interface CsException {
    Throwable getCause();
    
    void setCause(Throwable cause);

    String getMessage();

    String getUserMessage();

    Message getUserMessageAsMessage();

    String[] getUserMessages();

    Message[] getUserMessagesAsMessages();

    boolean isLogged();

    void setIsLogged(boolean isLogged);

    String getErrorID();
}
