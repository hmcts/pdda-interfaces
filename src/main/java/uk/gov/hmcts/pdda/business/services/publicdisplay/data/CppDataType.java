package uk.gov.hmcts.pdda.business.services.publicdisplay.data;

public enum CppDataType {

    COURTDETAIL_TYPE("CourtDetail"),
    COURTLIST_TYPE("CourtList"),
    DAILYLIST_TYPE("DailyList"),
    ALLCOURTSTATUS_TYPE("AllCourtStatus"),
    SUMMARYBYNAME_TYPE("SummaryByName"),
    ALLCASETSTATUS_TYPE("AllCaseStatus"),
    JURYCURRENTSTATUS_TYPE("JuryCurrentStatus");

    String value;

    CppDataType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
    
    public static CppDataType fromString(String value) {
        for (CppDataType dataType : values()) {
            if (dataType.getValue().equalsIgnoreCase(value)) {
                return dataType;
            }
        }
        return null;
    }
}
