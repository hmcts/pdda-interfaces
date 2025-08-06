package uk.gov.hmcts.pdda.business.services.validation;

public class ValidationException extends Exception {

    /**
     * Serialization id, increment if class structure changes.
     */
    private static final long serialVersionUID = 1L;

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

}
