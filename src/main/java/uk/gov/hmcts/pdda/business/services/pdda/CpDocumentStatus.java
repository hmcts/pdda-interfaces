package uk.gov.hmcts.pdda.business.services.pdda;

public enum CpDocumentStatus {
    VALID_NOT_PROCESSED("VN"), INVALID("INV"), ON_HOLD("OH");

    public final String status;

    CpDocumentStatus(String status) {
        this.status = status;
    }
}
