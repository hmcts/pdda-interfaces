package uk.gov.hmcts.pdda.common.publicdisplay.types.uri;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.common.publicdisplay.types.document.DisplayDocumentType;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.exceptions.InvalidUriFormatException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * <p/>
 * Title: An immutable class that provides URI functionality for Public Display.
 * </p>
 * <p/>
 * <p/>
 * The DisplayDocumentURI can then be constructed from it's components as the usual Java primitives
 * ie. int courtId, String documentType, int[] courtRoomIds. The DisplayDocumentURI also supports
 * conversion to and from a String. The format of a display document URI is:
 * 
 * <pre>
 *         pd://display/&lt;courthouseName&gt;/&lt;courtSiteCode&gt;/&lt;location&gt;/&lt;display&gt;
 * </pre>
 * 
 * </p>
 * <p/>
 * <p/>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p/>
 * <p/>
 * Company: Electronic Data Systems
 * </p>
 * 
 * @author Neil Ellis
 * @version $Revision: 1.21 $
 */
public final class DisplayDocumentUri extends AbstractUri {

    /**
     * The serial version number.
     */
    private static final long serialVersionUID = 2L;

    public static final int UNASSIGNED = Integer.MAX_VALUE;

    public static final String UNASSIGNED_STRING = "u";

    private static final String DOCUMENT_URI_TYPE = "document";

    private static final String DOCUMENT_URI_PREFIX = URI_PREFIX + DOCUMENT_URI_TYPE + ':';

    private static final char[] DOCUMENT_URI_PREFIX_CHARS = DOCUMENT_URI_PREFIX.toCharArray();

    private final Locale locale;

    private final int courtId;

    private final DisplayDocumentType documentType;

    private final int[] courtRoomIds;

    private static final Logger LOG = LoggerFactory.getLogger(DisplayDocumentUri.class);

    /**
     * Build a DisplayDocumentURI from it's component parts.
     *
     * @param locale Locale
     * @param courtId int
     * @param name DisplayDocumentType
     * @param courtRoomIds a sorted array of court room ids.
     * @pre courtRoomIds.length <= 60
     * @post courtId >= 0
     * @post courtRoomIds != null
     * @post courtRoomIds.length > 0
     * @post
     */
    public DisplayDocumentUri(Locale locale, int courtId, DisplayDocumentType name, int... courtRoomIds) {
        super(createUri(locale, courtId, name, courtRoomIds));
        this.locale = locale;
        this.courtId = courtId;
        this.documentType = name;
        this.courtRoomIds = copySort(courtRoomIds);
    }

    /**
     * Construct a URI from the String passed.
     * 
     * @param uri String
     * @pre uri != null
     * @post courtId >= 0
     * @post courtRoomIds != null
     * @post courtRoomIds.length > 0
     * @post forall int crt in courtRoomIds | crt >= 0
     */
    public DisplayDocumentUri(final String uri) {
        super(uri);

        final char[] chars = uri.toCharArray();

        // Prefix
        validatePrefix(uri);

        // Court Id
        int start = DOCUMENT_URI_PREFIX_CHARS.length;
        int end = indexOf(uri.toCharArray(), start, '/');
        int length = end - start;
        this.courtId = DisplayDocumentUriUtils.parseCourtId(uri, start, end, length);

        // Document Type
        start = end + 1;
        end = indexOf(chars, start, ':');
        length = end - start;
        this.documentType = DisplayDocumentUriUtils.parseDocumentType(uri, start, end, length);
        LOG.debug("Display Document: {}", this.documentType);

        // Locale
        this.locale = DisplayDocumentUriUtils.parseLocale(chars, end);

        // Court Ids
        List<Integer> courtIdList = new ArrayList<>();

        // Court Ids - Initialise Loop Variables
        start = end + 1;
        end = indexOf(chars, start, ',');
        if (end == -1) {
            end = chars.length;
        }
        length = end - start;
        while (length > 0) {
            // Court Ids - Process Entry
            Integer courtRoomId = parseCourtRoomId(chars, start, length);
            DisplayDocumentUriUtils.validateCourtRoomId(courtRoomId,uri);

            courtIdList.add(courtRoomId);
            // Court Ids - Update Loop Variables
            start = end + 1;
            end = indexOf(chars, start, ',');
            if (end == -1) {
                end = chars.length;
            }
            length = end - start;
        }
        // Court Ids - Convert into array.
        this.courtRoomIds = DisplayDocumentUriUtils.getCourtRoomIdsArray(courtIdList);
    }
    
    private static void validatePrefix(String uri) {
        char[] chars = uri.toCharArray();
        if (!equals(DOCUMENT_URI_PREFIX_CHARS, 0, chars, 0, DOCUMENT_URI_PREFIX_CHARS.length)) {
            throw new InvalidUriFormatException(uri, "Invalid prefix.");
        }
    }

    /**
     * Returns the locale for this URI.
     * 
     * @return the Locale for this uri
     */
    @Override
    public Locale getLocale() {
        return locale;
    }

    /**
     * Returns the type of uri this is as a String.
     * 
     * @return the type of URI this is.
     * @post return != null
     */
    @Override
    public String getType() {
        return DOCUMENT_URI_TYPE;
    }

    /**
     * Returns the courtId.
     * 
     * @return Returns the courtId.
     */
    public int getCourtId() {
        return courtId;
    }

    /**
     * This returns a copy of the courtRoomIds. We return a copy to make sure the object remains
     * immutable.
     * 
     * @return returns a copy of the courtRoomIds.
     */
    public int[] getCourtRoomIds() {
        return courtRoomIds.clone();
    }

    /**
     * This returns a copy of the courtRoomIds. We return a copy to make sure the object remains
     * immutable.
     * 
     * @return returns a copy of the courtRoomIds.
     */
    public int[] getCourtRoomIdsWithoutUnassigned() {
        if (isUnassignedRequired()) {
            int[] returnArray = new int[courtRoomIds.length - 1];
            System.arraycopy(courtRoomIds, 0, returnArray, 0, courtRoomIds.length - 1);
            return returnArray;
        } else {
            return courtRoomIds.clone();
        }
    }

    /**
     * Returns the DocumentTupe from the URI.
     * 
     * @return the documentType.
     */
    public DisplayDocumentType getDocumentType() {
        return documentType;
    }

    /**
     * Returns the DocumentTupe from the URI.
     * 
     * @return the documentType.
     */
    public String getSimpleDocumentType() {
        return documentType.getShortName();
    }

    /**
     * Returns the DocumentTupe from the URI converted to lowercase.
     * 
     * @return the documentType.
     */
    public String getDocumentTypeAsLowerCaseString() {
        return documentType.toLowerCaseString();
    }

    /**
     * Returns true if the URI has unassigned cases.
     * 
     * @return the documentType.
     */
    public boolean isUnassignedRequired() {
        return courtRoomIds.length > 0 && courtRoomIds[courtRoomIds.length - 1] == UNASSIGNED;
    }

    //
    // Utils
    //
   

    private static String createUri(Locale locale, int courtId, DisplayDocumentType documentType,
        int... sortedCourtRoomIds) {
        // Validate the uri
        DisplayDocumentUriUtils.validateUri(locale, courtId, documentType, sortedCourtRoomIds);
        // Create the uri
        StringBuilder sb = new StringBuilder();
        sb.append(DOCUMENT_URI_PREFIX_CHARS).append(courtId).append('/').append(documentType).append(':');
        if (0 < sortedCourtRoomIds.length) {
            appendCourtRoomId(sb, sortedCourtRoomIds[0]);
            for (int i = 1; i < sortedCourtRoomIds.length; i++) {
                sb.append(',');
                appendCourtRoomId(sb, sortedCourtRoomIds[i]);
            }
        }
        return sb.toString();
    }

    private static void appendCourtRoomId(StringBuilder sb, int courtRoomId) {
        if (courtRoomId == UNASSIGNED) {
            sb.append(UNASSIGNED_STRING);
        } else {
            sb.append(courtRoomId);
        }
    }

    private static Integer parseCourtRoomId(char[] chars, int start, int length) {
        try {
            String courtId = new String(chars, start, length);
            if (UNASSIGNED_STRING.equals(courtId)) {
                return UNASSIGNED;
            }
            return Integer.valueOf(courtId);
        } catch (Exception nfe) {
            return null;
        }
    }

    private static int[] copySort(int... values) {
        if (values != null) {
            int[] buffer = new int[values.length];
            System.arraycopy(values, 0, buffer, 0, values.length);
            Arrays.sort(values);
        }
        return values;
    }

}
