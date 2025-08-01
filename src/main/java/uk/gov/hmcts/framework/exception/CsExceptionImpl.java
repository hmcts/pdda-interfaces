package uk.gov.hmcts.framework.exception;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**

 * Title: CSExceptionImpl.


 * Description: Implementation class for CSExeption interface. Base exception class for
 * CSRecoverableException and CSUnrecoverableException in CS Hub framework. Contains implementations
 * for interface methods which can be overidden by exception sub classes. Allows additional messages
 * to be provided both for the user (use of Message class) and the developer (String errorMessage).


 * Copyright: Copyright (c) 2003


 * Company: Electronic Data Systems

 * @author Bal Bhamra
 * @version 1.0
 */

public class CsExceptionImpl implements CsException, Serializable {

    private static final long serialVersionUID = 1L;

    protected static final Message UNEXPECTED_ERROR_MESSAGE = new Message(Message.UNEXPECTED_ERROR);

    protected final List<Message> userMessages = new ArrayList<>();

    protected Throwable cause;

    protected Message userMessage;

    protected boolean logged;

    protected String idNum;

    private int count;

    /**
     * This requires implementation but not called. Potential calling classes extend Exception so
     * deal with cause differently.

     * @return Throwable
     */
    @Override
    public Throwable getCause() {
        return cause;
    }
    
    /**
     * Set cause.
     */
    @Override
    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    /**
     * Get message.

     * @return String: This is a temp implement and will be overridden
     */
    @Override
    public String getMessage() {
        return "";
    }

    /**
     * Get user message.

     * @return String the message for user of application
     */
    @Override
    public String getUserMessage() {
        return getUserMessageAsMessage().getMessage();
    }

    /**
     * Get user message.

     * @return String[] the message for user of application
     */
    @Override
    public String[] getUserMessages() {

        Message[] messages = getUserMessagesAsMessages();
        String[] strings = new String[messages.length];
        for (int i = 0; i < strings.length; i++) {
            strings[i] = messages[i].getMessage();
        }
        return strings;
    }

    /**
     * Get user message.

     * @return Message the message for user of application or null if not present
     */
    @Override
    public Message getUserMessageAsMessage() {
        if (userMessages.isEmpty()) {
            return UNEXPECTED_ERROR_MESSAGE;
        }
        return userMessages.get(0);
    }

    /**
     * Get user message.

     * @return Message[] the messages for user of application
     */
    @Override
    public Message[] getUserMessagesAsMessages() {
        if (userMessages.isEmpty()) {
            return new Message[] {UNEXPECTED_ERROR_MESSAGE};
        }
        // convert the ArrayList to an Array to return
        return userMessages.toArray(new Message[0]);        
    }

    /**
     * Add additional message.

     * @param message Message
     */
    public void addMessage(Message message) {
        userMessages.add(message);
    }

    /**
     * Add additional message.

     * @param errorKey String
     */
    public void addMessage(String errorKey) {
        userMessages.add(new Message(errorKey));
    }

    /**
     * Add additional message.

     * @param errorKey String
     * @param parameters ObjectArray
     */
    public void addMessage(String errorKey, Object... parameters) {
        userMessages.add(new Message(errorKey, parameters));
    }

    /**
     * Gets the logged flag.

     * @return boolean
     */
    @Override
    public boolean isLogged() {
        return logged;
    }

    /**
     * Sets the logged flag.

     * @param isLogged true indicates that the exception has been logged
     */
    @Override
    public void setIsLogged(boolean isLogged) {
        this.logged = isLogged;
    }

    /**
     * Get error Id.

     * @return the error ID of this exception instance
     */
    @Override
    public String getErrorID() {
        return this.idNum;
    }

    /**
     * THIS IS A PROTECTED METHOD. It is not in CSException interface however (cannot have protected
     * methods)
     */
    protected void createId() {
        idNum = "[error: " + System.currentTimeMillis() + "." + count++ + " ] ";
    }
}
