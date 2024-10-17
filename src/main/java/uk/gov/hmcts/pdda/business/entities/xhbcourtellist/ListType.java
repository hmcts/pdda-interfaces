package uk.gov.hmcts.pdda.business.entities.xhbcourtellist;

/**
 * An enum which represents the ListType.
 */
public enum ListType {
    SJP_PUBLIC_LIST, SJP_PRESS_LIST, 
    CROWN_DAILY_LIST, CROWN_FIRM_LIST, CROWN_WARNED_LIST, 
    MAGS_PUBLIC_LIST, MAGS_STANDARD_LIST, 
    CIVIL_DAILY_CAUSE_LIST, FAMILY_DAILY_CAUSE_LIST;

    private static final String DAILY_LIST = "DL";
    private static final String FIRM_LIST = "FL";
    private static final String WARN_LIST = "WL";

    public static ListType fromString(String value) {
        if (DAILY_LIST.equals(value)) {
            return CROWN_DAILY_LIST;
        } else if (FIRM_LIST.equals(value)) {
            return CROWN_FIRM_LIST;
        } else if (WARN_LIST.equals(value)) {
            return CROWN_WARNED_LIST;
        }
        return null;
    }
}
