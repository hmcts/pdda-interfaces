package uk.gov.hmcts.pdda.business.services.pdda;

public enum DlNotifierStatusEnum {

    SUCCESS("SUCCESS"), FAILURE("FAILURE"), RUNNING("RUNNING");

    String status;

    DlNotifierStatusEnum(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }
}
