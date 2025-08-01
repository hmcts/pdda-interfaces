package uk.gov.hmcts.pdda.common.publicdisplay.types.document;

import uk.gov.hmcts.pdda.common.publicdisplay.types.document.exceptions.NoSuchDocumentTypeException;

import java.util.Locale;
import java.util.Map;

public final class DisplayDocumentTypeUtils  {

    private static final String EMPTY_STR = "";
    private static final char UNDERSCORE = '_';
    
    private DisplayDocumentTypeUtils() {
        
    }
    
    /**
     * Return a single instance of DisplayDocumentType.

     * @param documentId is the shortName of the description_code from the DISPLAY_DOCUMENT table

     * @return DisplayDocumentType

     * @pre typeMap.get(documentId) instanceof DisplayDocumentType
     * @pre exists DisplayDocumentType dt inarray types | dt.shortName.equals(documentId)
     * @post return.shortName.equals(documentId)
     * @post return.casesRequired.equals(CasesRequired.ALL) ||
     *       return.casesRequired.equals(CasesRequired.ACTIVE)
     */
    public static DisplayDocumentType getDisplayDocumentType(final String documentId) {
        return getDisplayDocumentType(documentId, EMPTY_STR, EMPTY_STR);
    }

    /**
     * Return a single instance of DisplayDocumentType.

     * @param documentId is the shortName of the description_code from the DISPLAY_DOCUMENT table

     * @return DisplayDocumentType

     * @pre typeMap.get(documentId) instanceof DisplayDocumentType
     * @pre exists DisplayDocumentType dt inarray types | dt.shortName.equals(documentId)
     * @post return.shortName.equals(documentId)
     * @post return.casesRequired.equals(CasesRequired.ALL) ||
     *       return.casesRequired.equals(CasesRequired.ACTIVE)
     */
    public static DisplayDocumentType getDisplayDocumentType(final String documentId,
        final String language, final String country) {
        DisplayDocumentType type = null;
        StringBuilder sb = new StringBuilder();
        if (country == null) {
            sb.append(documentId).append(UNDERSCORE).append(language);
        } else {
            sb.append(documentId).append(UNDERSCORE).append(language).append(UNDERSCORE)
                .append(country);
        }
        String criteria = sb.toString();

        type = getActualDisplayDocType(documentId, type, criteria, language, country);

        if (type == null) {
            throw new NoSuchDocumentTypeException(criteria);
        }

        return type;
    }

    private static DisplayDocumentType getActualDisplayDocType(final String documentId,
        DisplayDocumentType type, String criteria, final String language, final String country) {
        
        Map<Object, Object> typeMap = DisplayDocumentType.buildTypeMap();
        
        if (isDefaultLanguageAndCountry(language,country)) {
            return (DisplayDocumentType) typeMap.get(documentId);
        } else if (language != null && isDefaultCountry(country)) {
            return (DisplayDocumentType) typeMap.get(criteria);
        } else if (documentId != null && language != null && country != null) {
            return (DisplayDocumentType) typeMap.get(criteria);
        }

        return type;
    }
    
    private static boolean isDefaultLanguageAndCountry(final String language, final String country) {
        return Locale.getDefault().getLanguage().equals(language)
            || (language == null || EMPTY_STR.equals(language))
                && isDefaultCountry(country);
    }
    
    private static boolean isDefaultCountry(final String country) {
        return country == null || EMPTY_STR.equals(country);
    }
}
