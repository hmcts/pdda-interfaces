package uk.gov.hmcts.pdda.business.services.formatting;

public enum WarnedTag {
    CASE_NUMBER("cs:CaseNumber"), FIXED_DATE("cs:FixedDate"), FIXTURE(
        "cs:Fixture"), WITH_FIXED_DATE(
            "cs:WithFixedDate"), WITHOUT_FIXED_DATE("cs:WithoutFixedDate");

    String value;

    WarnedTag(String value) {
        this.value = value;
    }
    
    private String getValue() {
        return value;
    }
    
    public static WarnedTag fromString(String value) {
        for (WarnedTag tag : values()) {
            if (tag.getValue().equalsIgnoreCase(value)) {
                return tag;
            }
        }
        return null;
    }
}