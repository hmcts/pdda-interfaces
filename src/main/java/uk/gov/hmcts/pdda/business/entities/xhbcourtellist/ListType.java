package uk.gov.hmcts.pdda.business.entities.xhbcourtellist;

/**
 * An enum which represents the ListType.
 */
public enum ListType {
    SJP_PUBLIC_LIST, SJP_PRESS_LIST, CROWN_DAILY_LIST, CROWN_FIRM_LIST, CROWN_WARNED_LIST, MAGS_PUBLIC_LIST, MAGS_STANDARD_LIST, CIVIL_DAILY_CAUSE_LIST, FAMILY_DAILY_CAUSE_LIST;


    public static ListType fromString(String value) {
        if ("DL".equals(value)) {
            return CROWN_DAILY_LIST;
        } else if ("FL".equals(value)) {
            return CROWN_FIRM_LIST;
        } else if ("WL".equals(value)) {
            return CROWN_WARNED_LIST;
        }
        return null;
    }
}
