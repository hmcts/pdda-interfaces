package uk.gov.hmcts.pdda.business.services.formatting;

public enum Tag {
    LIST_HEADER("cs:ListHeader"), CROWN_COURT("cs:CrownCourt"), START_DATE(
        "cs:StartDate"), END_DATE("cs:EndDate"), COURTHOUSE_CODE(
            "cs:CourtHouseCode"), COURTHOUSE_NAME("cs:CourtHouseName"), COURTHOUSE(
                "cs:CourtHouse"), COURTLISTS("cs:CourtLists"), COURTLIST(
                    "cs:CourtList"), COURTROOM_NUMBER("cs:CourtRoomNumber"), SITTINGS(
                        "cs:Sittings"), SITTING("cs:Sitting"), SITTING_AT("cs:SittingAt");

    String value;

    Tag(String value) {
        this.value = value;
    }
    
    private String getValue() {
        return value;
    }
    
    public static Tag fromString(String value) {
        for (Tag tag : values()) {
            if (tag.getValue().equalsIgnoreCase(value)) {
                return tag;
            }
        }
        return null;
    }
}