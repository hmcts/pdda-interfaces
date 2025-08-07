package uk.gov.hmcts.framework.security.login;

import uk.gov.hmcts.framework.services.CsServices;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

/**
 * <p>
 * Title: JAASLoginHelper.
 * </p>
 * <p>
 * This is a helper class for performing JAAS login
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: EDS
 * </p>
 * 
 * @author Meeraj Kunnumpurath
 * @version 1.0
 */

public class JaasLoginHelper {

    /**
     * This is the key under which the default login configuration name is stored.
     */
    public static final String DEFAULT_CONFIG_KEY = "defaultLoginConfig";

    /**
     * Login context to use.
     */
    private LoginContext loginContext;

    /**
     * Initializes the callback handler and JAAS configuration.
     */
    public JaasLoginHelper() throws LoginException {
        this(null, CsServices.getConfigServices().getProperty(DEFAULT_CONFIG_KEY));
    }

    /**
     * Initializes the callback handler and JAAS configuration file.
     * 
     * @param callbackHandler CallbackHandler
     */
    public JaasLoginHelper(CallbackHandler callbackHandler) throws LoginException {
        this(callbackHandler, CsServices.getConfigServices().getProperty(DEFAULT_CONFIG_KEY));
    }

    /**
     * Initializes the callback handler and JAAS configuration file.
     * 
     * @param callbackHandler CallbackHandler
     * @param loginConfig String
     */
    public JaasLoginHelper(CallbackHandler callbackHandler, String loginConfig)
        throws LoginException {

        if (callbackHandler != null) {
            loginContext = new LoginContext(loginConfig, callbackHandler);
        } else {
            loginContext = new LoginContext(loginConfig);
        }
    }

    /**
     * The method performs a login and returns the authenticated subject.
     * 
     * @return Subject
     * @throws LoginException Exception
     */
    public Subject login() throws LoginException {

        loginContext.login();
        return loginContext.getSubject();

    }

    /**
     * The method performs a logout.
     * 
     * @throws LoginException Exception
     */
    public void logout() throws LoginException {
        loginContext.logout();
    }

}
