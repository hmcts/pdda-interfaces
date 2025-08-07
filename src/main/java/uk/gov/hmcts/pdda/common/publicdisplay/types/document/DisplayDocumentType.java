package uk.gov.hmcts.pdda.common.publicdisplay.types.document;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * Title: DisplayDocumentType.
 * </p>
 * 
 * <p>
 * Description: An identifier that can be used to get the appropriate column from the
 * DISPLAY_DOCUMENT table
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * 
 * <p>
 * Company: Electronic Data Systems
 * </p>
 * 
 * @author Neil Ellis, Rakesh Lakhani
 * @version $Revision: 1.7 $
 */
public final class DisplayDocumentType implements Serializable {

    static final long serialVersionUID = -1560434537049491517L;

    private static final String EMPTY_STR = "";
    private static final String ALLCASESTATUS = "AllCaseStatus";
    private static final String ALL_CASE_STATUS_STR = "All Case Status";
    private static final String ALL_COURT_STATUS_STR = "All Court Status";
    private static final String ALLCOURTSTATUS = "AllCourtStatus";
    private static final String COURTDETAIL = "CourtDetail";
    private static final String COURT_DETAIL_STR = "Court Detail";
    private static final String COURTLIST = "CourtList";
    private static final String COURT_LIST_STR = "Court List";
    private static final String DAILYLIST = "DailyList";
    private static final String DAILY_LIST_STR = "Daily List";
    private static final String JURYCURRENTSTATUS = "JuryCurrentStatus";
    private static final String JURY_CURRENT_STATUS_STR = "Jury Current Status";
    private static final String SUMMARYBYNAME = "SummaryByName";
    private static final String SUMMARY_BY_NAME_STR = "Summary By Name";
    private static final char UNDERSCORE = '_';


    public static final DisplayDocumentType COURT_DETAIL = new DisplayDocumentType(COURTDETAIL,
        COURT_DETAIL_STR, CasesRequired.ACTIVE, EMPTY_STR, EMPTY_STR);

    public static final DisplayDocumentType COURT_LIST =
        new DisplayDocumentType(COURTLIST, COURT_LIST_STR, CasesRequired.ALL, EMPTY_STR, EMPTY_STR);

    public static final DisplayDocumentType DAILY_LIST =
        new DisplayDocumentType(DAILYLIST, DAILY_LIST_STR, CasesRequired.ALL, EMPTY_STR, EMPTY_STR);

    public static final DisplayDocumentType ALL_COURT_STATUS = new DisplayDocumentType(
        ALLCOURTSTATUS, ALL_COURT_STATUS_STR, CasesRequired.ACTIVE, EMPTY_STR, EMPTY_STR);

    public static final DisplayDocumentType SUMMARY_BY_NAME = new DisplayDocumentType(SUMMARYBYNAME,
        SUMMARY_BY_NAME_STR, CasesRequired.ALL, EMPTY_STR, EMPTY_STR);

    public static final DisplayDocumentType JURY_CURRENT_STATUS = new DisplayDocumentType(
        JURYCURRENTSTATUS, JURY_CURRENT_STATUS_STR, CasesRequired.ALL, EMPTY_STR, EMPTY_STR);

    public static final DisplayDocumentType ALL_CASE_STATUS = new DisplayDocumentType(ALLCASESTATUS,
        ALL_CASE_STATUS_STR, CasesRequired.ALL, EMPTY_STR, EMPTY_STR);

    public static final DisplayDocumentType COURT_DETAIL_CY_GB =
        new DisplayDocumentType(COURTDETAIL, COURT_DETAIL_STR, CasesRequired.ACTIVE, "cy", "GB");

    public static final DisplayDocumentType COURT_LIST_CY_GB =
        new DisplayDocumentType(COURTLIST, COURT_LIST_STR, CasesRequired.ALL, "cy", "GB");

    public static final DisplayDocumentType DAILY_LIST_CY_GB =
        new DisplayDocumentType(DAILYLIST, DAILY_LIST_STR, CasesRequired.ALL, "cy", "GB");

    public static final DisplayDocumentType ALL_COURT_STATUS_CY_GB = new DisplayDocumentType(
        ALLCOURTSTATUS, ALL_COURT_STATUS_STR, CasesRequired.ACTIVE, "cy", "GB");

    public static final DisplayDocumentType SUMMARY_BY_NAME_CY_GB =
        new DisplayDocumentType(SUMMARYBYNAME, SUMMARY_BY_NAME_STR, CasesRequired.ALL, "cy", "GB");

    public static final DisplayDocumentType JURY_CURRENT_STATUS_CY_GB = new DisplayDocumentType(
        JURYCURRENTSTATUS, JURY_CURRENT_STATUS_STR, CasesRequired.ALL, "cy", "GB");

    public static final DisplayDocumentType ALL_CASE_STATUS_CY_GB =
        new DisplayDocumentType(ALLCASESTATUS, ALL_CASE_STATUS_STR, CasesRequired.ALL, "cy", "GB");

    public static final DisplayDocumentType COURT_DETAIL_CY = new DisplayDocumentType(COURTDETAIL,
        COURT_DETAIL_STR, CasesRequired.ACTIVE, "cy", EMPTY_STR);

    public static final DisplayDocumentType COURT_LIST_CY =
        new DisplayDocumentType(COURTLIST, COURT_LIST_STR, CasesRequired.ALL, "cy", EMPTY_STR);

    public static final DisplayDocumentType DAILY_LIST_CY =
        new DisplayDocumentType(DAILYLIST, DAILY_LIST_STR, CasesRequired.ALL, "cy", EMPTY_STR);

    public static final DisplayDocumentType ALL_COURT_STATUS_CY = new DisplayDocumentType(
        ALLCOURTSTATUS, ALL_COURT_STATUS_STR, CasesRequired.ACTIVE, "cy", EMPTY_STR);

    public static final DisplayDocumentType SUMMARY_BY_NAME_CY = new DisplayDocumentType(
        SUMMARYBYNAME, SUMMARY_BY_NAME_STR, CasesRequired.ALL, "cy", EMPTY_STR);

    public static final DisplayDocumentType JURY_CURRENT_STATUS_CY = new DisplayDocumentType(
        JURYCURRENTSTATUS, JURY_CURRENT_STATUS_STR, CasesRequired.ALL, "cy", EMPTY_STR);

    public static final DisplayDocumentType ALL_CASE_STATUS_CY = new DisplayDocumentType(
        ALLCASESTATUS, ALL_CASE_STATUS_STR, CasesRequired.ALL, "cy", EMPTY_STR);

    private static final DisplayDocumentType[] TYPES =
        {COURT_DETAIL, COURT_LIST, DAILY_LIST, ALL_COURT_STATUS, SUMMARY_BY_NAME,
            JURY_CURRENT_STATUS, ALL_CASE_STATUS, COURT_DETAIL_CY, COURT_LIST_CY, DAILY_LIST_CY,
            ALL_COURT_STATUS_CY, SUMMARY_BY_NAME_CY, JURY_CURRENT_STATUS_CY, ALL_CASE_STATUS_CY,
            COURT_DETAIL_CY_GB, COURT_LIST_CY_GB, DAILY_LIST_CY_GB, ALL_COURT_STATUS_CY_GB,
            SUMMARY_BY_NAME_CY_GB, JURY_CURRENT_STATUS_CY_GB, ALL_CASE_STATUS_CY_GB};

    private static Map<String, DisplayDocumentType[]> typesMap; // Doesn't like final setting

    static {
        buildTypeMap();
        buildTypesMap();
    }

    private final CasesRequired casesRequired;

    private final String shortName;

    private final String longName;

    private final String language;

    private final String country;

    public static Map<Object, Object> buildTypeMap() {
        Map<Object, Object> typeMap = new ConcurrentHashMap<>(TYPES.length);
        for (DisplayDocumentType type : TYPES) {
            if (EMPTY_STR.equals(type.getLanguage())) {
                typeMap.put(type.shortName, type);
            } else if (!EMPTY_STR.equals(type.getLanguage())
                && EMPTY_STR.equals(type.getCountry())) {
                typeMap.put(type.shortName + "_" + type.language, type);
            } else {
                typeMap.put(type.shortName + "_" + type.language + "_" + type.country, type);

            }
        }
        return typeMap;
    }

    private static void buildTypesMap() {
        DisplayDocumentType[] courtDetailTypes = new DisplayDocumentType[3];
        courtDetailTypes[0] = COURT_DETAIL;
        courtDetailTypes[1] = COURT_DETAIL_CY;
        courtDetailTypes[2] = COURT_DETAIL_CY_GB;

        DisplayDocumentType[] courtListTypes = new DisplayDocumentType[3];
        courtListTypes[0] = COURT_LIST;
        courtListTypes[1] = COURT_LIST_CY;
        courtListTypes[2] = COURT_LIST_CY_GB;

        DisplayDocumentType[] dailyListTypes = new DisplayDocumentType[3];
        dailyListTypes[0] = DAILY_LIST;
        dailyListTypes[1] = DAILY_LIST_CY;
        dailyListTypes[2] = DAILY_LIST_CY_GB;

        DisplayDocumentType[] allCourtStatusTypes = new DisplayDocumentType[3];
        allCourtStatusTypes[0] = ALL_COURT_STATUS;
        allCourtStatusTypes[1] = ALL_COURT_STATUS_CY;
        allCourtStatusTypes[2] = ALL_COURT_STATUS_CY_GB;

        DisplayDocumentType[] summaryByNameTypes = new DisplayDocumentType[3];
        summaryByNameTypes[0] = SUMMARY_BY_NAME;
        summaryByNameTypes[1] = SUMMARY_BY_NAME_CY;
        summaryByNameTypes[2] = SUMMARY_BY_NAME_CY_GB;

        DisplayDocumentType[] juryCurrentStatuses = new DisplayDocumentType[3];
        juryCurrentStatuses[0] = JURY_CURRENT_STATUS;
        juryCurrentStatuses[1] = JURY_CURRENT_STATUS_CY;
        juryCurrentStatuses[2] = JURY_CURRENT_STATUS_CY_GB;

        DisplayDocumentType[] allCaseStatuses = new DisplayDocumentType[3];
        allCaseStatuses[0] = ALL_CASE_STATUS;
        allCaseStatuses[1] = ALL_CASE_STATUS_CY;
        allCaseStatuses[2] = ALL_CASE_STATUS_CY_GB;

        typesMap = new HashMap<>(7);
        typesMap.put(COURTDETAIL, courtDetailTypes);
        typesMap.put(COURTLIST, courtListTypes);
        typesMap.put(DAILYLIST, dailyListTypes);
        typesMap.put(ALLCOURTSTATUS, allCourtStatusTypes);
        typesMap.put(SUMMARYBYNAME, summaryByNameTypes);
        typesMap.put(JURYCURRENTSTATUS, juryCurrentStatuses);
        typesMap.put(ALLCASESTATUS, allCaseStatuses);
    }

    private DisplayDocumentType(final String name, final String longName,
        CasesRequired casesRequired, final String language, final String country) {
        this.shortName = name;
        this.casesRequired = casesRequired;
        this.longName = longName;
        this.language = language;
        this.country = country;
    }

    /**
     * Return an array of DisplayDocumentType(contain all variances) for a basic Display document
     * type.
     * 
     * @param documentId is the shortName of the description_code from the DISPLAY_DOCUMENT table
     * 
     * @return DisplayDocumentTypeArray
     * 
     * @pre typeMaps.get(documentId) instanceof DisplayDocumentType[]
     */
    public static DisplayDocumentType[] getDisplayDocumentTypes(final String documentId) {
        return typesMap.get(documentId);
    }

    /**
     * getCasesRequired.
     * 
     * @return CasesRequired
     * 
     * @post return != null
     */
    public CasesRequired getCasesRequired() {
        return casesRequired;
    }

    /**
     * Equals implementation.
     * 
     * @param anObject object (of type DisplayDocumentType) to compare with.
     * 
     * @return true if the same.
     * 
     * @pre shortName != null
     * @pre anObject instanceof DisplayDocumentType
     */
    @Override
    public boolean equals(final Object anObject) {
        if (anObject == null) {
            return false;
        }
        if (!(anObject instanceof DisplayDocumentType)) {
            return false;
        }
        DisplayDocumentType displayDocumentType = (DisplayDocumentType) anObject;
        return shortName.equals(displayDocumentType.shortName)
            && longName.equals(displayDocumentType.longName)
            && isBothSame(language, displayDocumentType.language)
            && isBothSame(country, displayDocumentType.country);
    }

    private boolean isBothSame(String s1, String s2) {
        return s1 == null && s2 == null || s2.equals(s1);
    }

    /**
     * Hashcode implementation.
     * 
     * @return hashcode.
     * @pre shortName != null
     */
    @Override
    public int hashCode() {
        return shortName.hashCode();
    }

    /**
     * toString() implementation.
     * 
     * @return the string shortName+language_country of the DisplayDocumentType
     * @post return != null
     */
    @Override
    public String toString() {
        if (language != null && !EMPTY_STR.equals(language) && country != null
            && !EMPTY_STR.equals(country)) {
            StringBuilder sb = new StringBuilder();
            return sb.append(shortName).append(UNDERSCORE).append(language).append(UNDERSCORE)
                .append(country).toString();
        }
        return shortName;
    }

    /**
     * getShortName.
     * 
     * @return the string shortName of the DisplayDocumentType
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * Returns the long name of the document type.
     * 
     * @post return != null
     * @return the long name of the document type.
     */
    public String getLongName() {
        if (language != null && !EMPTY_STR.equals(language) && country != null
            && !EMPTY_STR.equals(country)) {
            StringBuilder sb = new StringBuilder();
            return sb.append(longName).append(UNDERSCORE).append(language).append(UNDERSCORE)
                .append(country).toString();
        }
        return longName;
    }

    /**
     * Returns the lower case version of the shortname. This is useful for building filenames from.
     * 
     * @return the lower cased short name.
     * @pre shortName != null
     */
    public String toLowerCaseString() {
        return shortName.toLowerCase(Locale.getDefault());
    }

    /**
     * Returns the language of the document type.
     * 
     * @post return != null
     * @return the language of the document type.
     */
    public String getLanguage() {
        if (language == null) {
            return EMPTY_STR;
        }
        return language;
    }

    /**
     * Returns the country of the document type.
     * 
     * @post return != null
     * @return the country of the document type.
     */
    public String getCountry() {
        if (country == null) {
            return EMPTY_STR;
        }
        return country;
    }
}
