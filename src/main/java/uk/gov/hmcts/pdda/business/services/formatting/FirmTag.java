package uk.gov.hmcts.pdda.business.services.formatting;

public enum FirmTag {
    HEARING("cs:Hearing"), HEARING_DATE("cs:HearingDate"), RESERVE_LIST(
        "cs:ReserveList"), TOP_NODE("cs:FirmList");

    String value;

    FirmTag(String value) {
        this.value = value;
    }
    
    private String getValue() {
        return value;
    }
    
    public static FirmTag fromString(String value) {
        for (FirmTag tag : values()) {
            if (tag.getValue().equalsIgnoreCase(value)) {
                return tag;
            }
        }
        return null;
    }
}