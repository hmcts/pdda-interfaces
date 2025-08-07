package uk.gov.hmcts.pdda.web.publicdisplay.messaging.event;

public class EventStoreException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private final String className;

    public EventStoreException(String className, Throwable cause) {
        super("Invalid event store type: " + className, cause);
        this.className = className;
    }

    public String getClassName() {
        return className;
    }
}
