package uk.gov.hmcts.framework.exception;

import uk.gov.hmcts.framework.services.ConfigServices;
import uk.gov.hmcts.framework.services.CsServices;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

/**

 * Title: Message.


 * Description: The Message class supports internationalisation in the creation of message texts.
 * Messages are created by providing a key which exists in the external properties file (e.g.
 * errortext.properties). These messages may require additional parameters for the construction of
 * the completed message. see also java.lang.MessageFormat


 * Copyright: Copyright (c) 2002


 * Company: EDS

 * @author Kevin Buckthorpe
 * @version 1.0
 */
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String UNEXPECTED_ERROR = "xhibit.error.unexpected";

    public static final String OPTIMISTIC_LOCK_VIOLATED = "xhibit.error.optimisticlock_violated";

    // private vos
    private String key;

    private Object[] parameters;

    private Locale locale;

    private String errorMessages = ConfigServices.ERROR_MESSAGES;

    /**
     * Constructor.

     * @param key external key in properties file
     * @param parameters paramaters that will be passed into MessageFormat.format(String, Object[])
     *        to format the message
     */
    public Message(String key, Object... parameters) {
        setKey(key);
        setParameters(parameters);
    }

    /**
     * Constructor.

     * @param key external key in properties file
     * @param parameters paramaters that will be passed into MessageFormat.format(String, Object[])
     *        to format the message
     * @param properties file if not using default
     */
    public Message(String key, Object[] parameters, String properties) {
        setKey(key);
        setParameters(parameters);
        setErrorMessages(properties);
    }

    /**
     * Convienience constructor that passes the parameter into Object[] and calls the
     * Message(String, Object[]) constructor.

     * @param key external key in properties file
     * @param parameter paramater that will be passed into MessageFormat.format(String, Object[])
     * @param properties file if not using default
     */
    public Message(String key, Object parameter, String properties) {
        this(key, new Object[] {parameter}, properties);
    }

    /**
     * Constructor.

     * @param key external key in properties file
     */
    public Message(String key) {
        this(key, new ArrayList<>());
    }

    /**
     * Get key.

     * @return external key
     */
    public String getKey() {
        return key;
    }

    /**
     * Formats and returns text associated with the key provided.

     * @return the formated message text
     */
    public String getMessage() {
        ResourceBundle errorText;

        if (locale == null) {
            locale = Locale.getDefault();
        }

        errorText = CsServices.getConfigServices().getBundle(errorMessages, locale);

        String msg = errorText.getString(key);
        return MessageFormat.format(msg, parameters);
    }

    /**
     * Set Locale.

     * @param loc the Locale
     * @throws CSResourceUnavailableException Exception
     */
    public void setLocale(Locale loc) {
        locale = loc;
    }

    /**
     * getPropertiesFileName.

     * @return the propeties file used to match key value pairs
     */
    public String getPropertiesFileName() {
        return errorMessages + ".properties";
    }
    
    /**
     * setKey.
     * @param key String
     */
    private void setKey(String key) {
        this.key = key;
    }
    
    /**
     * setParameters.
     * @param parameters ObjectArray
     */
    private void setParameters(Object... parameters) {
        this.parameters = parameters;
    }

    /**
     * setErrorMessages.
     * @param errorMessages String
     */
    private void setErrorMessages(String errorMessages) {
        this.errorMessages = errorMessages;
    }
}
