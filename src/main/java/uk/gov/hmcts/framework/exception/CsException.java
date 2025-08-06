package uk.gov.hmcts.framework.exception;

/**

 * Title: CSException.


 * Description: Interface for all CS Hub framework exceptions


 * Copyright: Copyright (c) 2002


 * Company: EDS

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
