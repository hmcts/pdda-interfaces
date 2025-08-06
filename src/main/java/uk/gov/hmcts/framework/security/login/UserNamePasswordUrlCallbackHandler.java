package uk.gov.hmcts.framework.security.login;

/**

 * Title: UserNamePasswordURLCallbackHandler.


 * Description: This class provides a user, password, URL based callback


 * Copyright: Copyright (c) 2002


 * Company: EDS

 * @author Meeraj Kunnumpurath
 * @version 1.0
 */

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

public class UserNamePasswordUrlCallbackHandler implements CallbackHandler {

    /**
     * User name.
     */
    private final String userName;

    /**
     * Password.
     */
    private final String password;

    /**
     * Constructor initialises the user name and password.

     * @param userName String
     * @param password String
     */
    public UserNamePasswordUrlCallbackHandler(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    /**
     * Handles the callback.

     * @param callbacks CallbackArray
     * @throws UnsupportedCallbackException Exception
     */
    @Override
    public void handle(Callback[] callbacks) throws UnsupportedCallbackException {

        for (Callback callback : callbacks) {
            // User name
            if (callback instanceof NameCallback) {
                NameCallback nc = (NameCallback) callback;
                nc.setName(userName);
            // Authentication URL
            } else if (callback instanceof PasswordCallback) {
                PasswordCallback pc = (PasswordCallback) callback;
                pc.setPassword(password.toCharArray());
            } else {
                throw new UnsupportedCallbackException(callback, "Unrecognized Callback");
            }

        }

    }

}
