package uk.gov.hmcts.framework.exception;

@SuppressWarnings("PMD.AvoidSynchronizedStatement")
public abstract class AbstractCsException extends RuntimeException implements CsException {

    private static final long serialVersionUID = 1L;

    private Throwable cause;

    private boolean logged;

    private final CsExceptionImpl csExceptionImpl = new CsExceptionImpl();

    public AbstractCsException() {
        super();
    }

    public AbstractCsException(String newLogMsg) {
        super(newLogMsg);
    }

    /**
     * Sets the cause.
     */
    @Override
    public void setCause(Throwable cause) {
        synchronized (this) {
            this.cause = cause;
        }
    }

    /**
     * Gets the Cause.

     * @return Throwable: exception wrapped by this instance.
     */
    @Override
    public Throwable getCause() { // NOSONAR
        synchronized (this) {
            return cause;
        }
    }

    /**
     * Gets the logged flag.

     * @return boolean: true indicates that the exception has been logged
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
     * getCsExceptionImpl.
     */
    protected CsExceptionImpl getCsExceptionImpl() {
        return csExceptionImpl;
    }

    /**
     * Gets the user message.

     * @return String the message for user of application
     */
    @Override
    public String getUserMessage() {
        return getCsExceptionImpl().getUserMessage();
    }

    /**
     * Gets the user message.

     * @return String[] the message for user of application
     */
    @Override
    public String[] getUserMessages() {
        return getCsExceptionImpl().getUserMessages();
    }


    /**
     * Gets the user message as a message.

     * @return Message the message for user of application or null if not present
     */
    @Override
    public Message getUserMessageAsMessage() {
        return getCsExceptionImpl().getUserMessageAsMessage();
    }

    /**
     * Gets the user message as a message.

     * @return Message[] the messages for user of application
     */
    @Override
    public Message[] getUserMessagesAsMessages() {
        return getCsExceptionImpl().getUserMessagesAsMessages();
    }

    /**
     * Gets error Id.

     * @return the id number of this exception instance
     */
    @Override
    public String getErrorID() {
        return getCsExceptionImpl().getErrorID();
    }


    /**
     * Add additional message.

     * @param message Message
     */
    public void addMessage(Message message) {
        getCsExceptionImpl().addMessage(message);
    }

    /**
     * Add additional message.

     * @param errorKey String
     */
    public void addMessage(String errorKey) {
        getCsExceptionImpl().addMessage(new Message(errorKey));
    }

    /**
     * Add additional message.

     * @param errorKey String
     * @param parameters ObjectArray
     */
    public void addMessage(String errorKey, Object... parameters) {
        getCsExceptionImpl().addMessage(new Message(errorKey, parameters));
    }
}
