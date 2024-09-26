package uk.gov.hmcts.pdda.common.publicdisplay.types.uri;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.common.publicdisplay.types.document.DisplayDocumentType;
import uk.gov.hmcts.pdda.common.publicdisplay.types.document.DisplayDocumentTypeUtils;
import uk.gov.hmcts.pdda.common.publicdisplay.types.document.exceptions.NoSuchDocumentTypeException;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.exceptions.InvalidUriFormatException;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class DisplayDocumentUriUtils  {
    
    private static final Logger LOG = LoggerFactory.getLogger(DisplayDocumentUriUtils.class);

    private DisplayDocumentUriUtils() {
        
    }
    
    public static void validateCourtRoomId(Integer courtRoomId, String uri) {
        if (courtRoomId == null) {
            throw new InvalidUriFormatException(uri, "Invalid courtId.");
        }
    }
    
    public static void validateUri(Locale locale, int courtId, DisplayDocumentType documentType,
        int... sortedCourtRoomIds) {
        String errorMsg = null;
        if (locale == null) {
            errorMsg = "Invalid locale supplied.";
        } else if (courtId < 0) {
            errorMsg = "Invalid court id supplied.";
        } else if (documentType == null) {
            errorMsg = "Invalid type supplied.";
        } else if (sortedCourtRoomIds == null || sortedCourtRoomIds.length == 0) {
            errorMsg = "Invalid court room ids supplied.";
        }
        if (errorMsg != null) {
            throw new InvalidUriFormatException(errorMsg);
        }
    }
    
    public static int[] getCourtRoomIdsArray(List<Integer> courtIdList) {
        int[] courtRoomIds = new int[courtIdList.size()];
        for (int i = 0; i < courtRoomIds.length; i++) {
            courtRoomIds[i] = courtIdList.get(i);
        }
        Arrays.sort(courtRoomIds);
        return courtRoomIds;
    }
    
    public static Integer parseCourtId(String uri, int start, int end, int length) {
        if (end == -1 || length == 0) {
            throw new InvalidUriFormatException(uri, "Invalid court id.");
        }
        char[] chars = uri.toCharArray();
        try {
            return Integer.parseInt(new String(chars, start, length));
        } catch (NumberFormatException nfe) {
            throw new InvalidUriFormatException(uri, "Invalid court id.", nfe);
        }
    }
    
    public static DisplayDocumentType parseDocumentType(String uri, int start, int end, int length) {
        char[] chars = uri.toCharArray();
        if (end == -1 || length == 0 || !isValidDocumentTypeCharacters(chars, start, length)) {
            throw new InvalidUriFormatException(uri, "Invalid type.");
        }
        try {
            return DisplayDocumentTypeUtils.getDisplayDocumentType(new String(chars, start, length));
        } catch (NoSuchDocumentTypeException nsdte) {
            throw new InvalidUriFormatException(uri, "Invalid type.", nsdte);
        }
    }
    
    public static Locale parseLocale(char[] chars, int end) {
        int start = end + 1;
        int newEnd = indexOf(chars, start, '_');

        // check if language exists
        if (newEnd == -1) {
            return Locale.getDefault();
        }

        start = newEnd + 1;
        newEnd = indexOf(chars, start, '_');

        // check country exists
        if (newEnd == -1) {
            // If language exists, country should too.
            LOG.error("Language set but no Country set.  Using default locale: {}", Locale.getDefault());
            return Locale.getDefault();
        }
        final String language = new String(chars, start, 2);
        final String country = new String(chars, newEnd + 1, 2);
        
        return new Locale(language, country);
    }
    
    public static int indexOf(char[] source, int offset, char delimiter) {
        for (int i = offset; i < source.length; i++) {
            if (source[i] == delimiter) {
                return i;
            }
        }
        return -1;
    }
    
    private static boolean isValidDocumentTypeCharacters(char[] actual, int offset, int length) {
        for (int i = 0; i < length; i++) {
            if (!isValidDocumentTypeCharacter(actual[offset + i])) {
                return false;
            }
        }
        return true;
    }

    private static boolean isValidDocumentTypeCharacter(char actual) {
        return Character.isLetter(actual) || Character.isDigit(actual) || actual == ' ' || actual == '_';
    }
}
