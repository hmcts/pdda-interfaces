package uk.gov.hmcts.pdda.business.services.cppstaginginboundejb3;

/**
 * Enum declaring valid document name prefixes PLEASE NOTE THESE MUST BE MIXED CASE AND NOT CHANGED
 * TO UPPER CASE DESPITE THE CODING STANDARDS MESSAGE.
 */
public enum CppDocumentNameInIncomingDocument {
    WEBPAGE("WEBPAGE"), PUBLICDISPLAY("PUBLICDISPLAY"), DAILYLIST("DAILYLIST"), FIRMLIST("FIRMLIST"), WARNEDLIST(
        "WARNEDLIST");

    private final String label;

    CppDocumentNameInIncomingDocument(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }
}
