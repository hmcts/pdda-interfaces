package uk.gov.hmcts.flyway;


public class PendingMigrationScriptException extends RuntimeException {

    private static final long serialVersionUID = -3462829663149517530L;

    public PendingMigrationScriptException(String script) {
        super("Found migration not yet applied: " + script);
    }
}