package uk.gov.hmcts.framework.client;

import java.util.List;
import java.util.Set;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;

/*

 * Ref Date Author Description.

 * PRE00121 06-10-2003 AW Daley isUserInGroup method added

 */
public interface CsUserSession {

    /**
     * Returns whether the user has access to the specified functionality.

     * @param functionality String
     * @return boolean
     */
    boolean hasAccess(String functionality);

    /**
     * Returns whether the user has access to specified list of functionalities.

     * @param funtionality List
     * @return boolean
     */
    boolean hasAccess(List funtionality);

    /**
     * Returns the list of functionalities.

     * @return List
     */
    List getFunctionality();

    /**
     * Logs in the user.
     */
    void login();

    /**
     * Logs out the user.
     */
    void logout();

    /**
     * Returns whether the user is logged in.

     * @return boolean
     */
    boolean isLoggedIn();

    /**
     * Gets a unique session id.

     * @return String
     */
    String getSessionID();

    /**
     * Gets a session property.

     * @param key SessionPropertyKey
     * @return String
     */
    String getSessionProperty(SessionPropertyKey key);

    /**
     * Reloads the session information.
     */
    void updateSessionInfo();

    /**
     * Gets a user profile property.

     * @param key String
     * @return String
     */
    String getUserProfileProperty(String key);

    /**
     * Gets the currently authenticated server name.

     * @return String
     */
    String getServer();

    /**
     * Sets the currently authenticated server name.

     * @param serverName String
     */
    void setServer(String serverName);

    /**
     * Gets the list of available servers.

     * @return Set
     */
    Set getServers();

    /**
     * Gets the currently authenticated subject.

     * @return Subject
     */
    Subject getUserSubject();

    /**
     * Gets the user name for the currently authenticated subject.

     * @return String
     */
    String getUserName();

    /**
     * Sets the login config.

     * @param loginConfig String
     */
    void setLoginConfig(String loginConfig);

    /**
     * Sets the callback handler for login.

     * @param callbackHandler CallbackHandler
     */
    void setCallbackHandler(CallbackHandler callbackHandler);

    /**
     * Determines if the user is a member of the specified group.

     * @param group String
     * @return boolean
     */
    boolean isUserInGroup(String group);

    /**
     * Returns the country of the court, where the users terminal is located.

     * @return String
     */
    String getCountry();

    /**
     * Returns the language of the court, where the users terminal is located.

     * @return String
     */
    String getLanguage();

    Long getUserLoginId();

    void setUserLoginId(Long userLoginId);
}
