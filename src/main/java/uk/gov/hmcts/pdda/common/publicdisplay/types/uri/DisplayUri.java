package uk.gov.hmcts.pdda.common.publicdisplay.types.uri;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.exceptions.InvalidUriFormatException;

import java.util.Locale;

/**

 * Title: DisplayURI.


 * Description: The following is the format of the DisplayURI:

 * <pre>
 *         pd://display/&lt;courthouseName&gt;/&lt;courtSiteCode&gt;/&lt;location&gt;/&lt;display&gt;
 * </pre>

 * <p/>
 * <p/>
 * Copyright: Copyright (c) 2003

 * <p/>
 * <p/>
 * Company: Electronic Data Systems

 * @author Neil Ellis
 * @version $Revision: 1.11 $
 */
public class DisplayUri extends AbstractUri {

    private static final Logger LOG = LoggerFactory.getLogger(DisplayUri.class);

    /**
     * The serial version number.
     */
    private static final long serialVersionUID = 2L;

    /**
     * This relates to the limit on the window title of internet explorer (PR55731). Ultramon uses
     * the title to identify the window.
     */
    public static final int MAX_DISPLAY_URI_LENGTH = 85;

    private static final String DISPLAY_URI_TYPE = "display";

    private static final String DISPLAY_URI = "Display Uri \"";

    private static final String DISPLAY_URI_PREFIX = URI_PREFIX + DISPLAY_URI_TYPE + '/';

    private static final char[] DISPLAY_URI_PREFIX_CHARS = DISPLAY_URI_PREFIX.toCharArray();

    // The data

    private final String courthouseName;

    private final String courtsiteCode;

    private final String location;

    private final String display;

    /**
     * Creates a new DisplayURI object.

     * @param uri the displayURI as a string (see class comments).

     * @pre uri != null
     * @post getCourthouseName() != null && getCourtsiteCode() != null && getLocation() != null &&
     *       getDisplay() != null
     * @post toString() != null
     */
    public DisplayUri(final String uri) {
        super(uri);

        if (uri.length() > MAX_DISPLAY_URI_LENGTH) {
            throw new InvalidUriFormatException(uri,
                "The URI supplied exceeds the maximum limit of " + MAX_DISPLAY_URI_LENGTH
                    + " characters.");
        }

        char[] chars = uri.toCharArray();

        // Prefix
        if (!equals(DISPLAY_URI_PREFIX_CHARS, 0, chars, 0, DISPLAY_URI_PREFIX_CHARS.length)) {
            throw new InvalidUriFormatException(DISPLAY_URI + uri + "\" has invalid prefix.");
        }
        // Court House Name
        int start = DISPLAY_URI_PREFIX_CHARS.length;
        int end = indexOf(chars, start, '/');
        int length = end - start;
        if (end == -1 || length == 0 || !isValidDisplayUriCharacters(chars, start, length)) {
            throw new InvalidUriFormatException(
                DISPLAY_URI + uri + "\" has invalid court house name.");
        }
        this.courthouseName = new String(chars, start, length);

        // Court Site Code
        start = end + 1;
        end = indexOf(chars, start, '/');
        length = end - start;
        this.courtsiteCode = poppulateCourtSiteCode(chars, uri, start, end, length);

        // Location
        start = end + 1;
        end = indexOf(chars, start, '/');
        length = end - start;
        this.location = poppulateLocation(chars, uri, start, end, length);

        // Display
        start = end + 1;
        end = chars.length;
        length = end - start;
        this.display = poppulateDisplay(chars, uri, start, length);
    }

    private String poppulateCourtSiteCode(char[] chars, String uri, int start, int end,
        int length) {
        if (end == -1 || length == 0 || !isValidDisplayUriCharacters(chars, start, length)) {
            throw new InvalidUriFormatException(
                DISPLAY_URI + uri + "\" has invalid court site code.");
        }
        return new String(chars, start, length);
    }

    private String poppulateLocation(char[] chars, String uri, int start, int end, int length) {
        if (end == -1 || length == 0 || !isValidDisplayUriCharacters(chars, start, length)) {
            throw new InvalidUriFormatException(DISPLAY_URI + uri + "\" has invalid location.");
        }
        return new String(chars, start, length);
    }

    private String poppulateDisplay(char[] chars, String uri, int start, int length) {
        if (length == 0 || !isValidDisplayUriCharacters(chars, start, length)) {
            throw new InvalidUriFormatException(DISPLAY_URI + uri + "\" has invalid display.");
        }
        return new String(chars, start, length);
    }

    /**
     * Creates a new DisplayURI object.

     * @pre courthouseName != null
     * @pre courtsiteCode != null
     * @pre location != null
     * @pre display != null
     * @post getCourthouseName() != null && getCourtsiteCode() != null && getLocation() != null &&
     *       getDisplay() != null
     * @post toString() != null
     */
    public DisplayUri(final String courthouseName, final String courtsiteCode,
        final String location, final String display) {
        // Note the create uri validates the courthouseName, courtsiteCode, location, display
        super(createUri(courthouseName, courtsiteCode, location, display));

        this.courthouseName = courthouseName;
        this.courtsiteCode = courtsiteCode;
        this.location = location;
        this.display = display;
    }

    /**
     * Displays do not support Localization so they just return the current locale.

     * @return the default Locale.
     */
    @Override
    public Locale getLocale() {
        return Locale.getDefault();
    }

    /**
     * Returns the type of uri this is as a String.

     * @return the type of URI this is.
     * @post return != null
     */
    @Override
    public String getType() {
        return DISPLAY_URI_TYPE;
    }

    /**
     * Returns the court house name from the URI.

     * @return the court house name from the URI.
     */
    public String getCourthouseName() {
        return courthouseName;
    }

    /**
     * Returns the court site name from the URI.

     * @return the court site name from the URI.
     */
    public String getCourtsiteCode() {
        return courtsiteCode;
    }

    /**
     * Returns the display name from the URI.

     * @return the display name from the URI.
     */
    public String getDisplay() {
        return display;
    }

    /**
     * Returns the location name from the URI.

     * @return the location name from the URI.
     */
    public String getLocation() {
        return location;
    }

    //
    // Utilities
    //

    private static String createUri(final String courthouseName, final String courtsiteCode,
        final String location, final String display) {
        if (!isValidDisplayUriString(courthouseName)) {
            throw new InvalidUriFormatException("Invalid courthouseName supplied.");
        }
        if (!isValidDisplayUriString(courtsiteCode)) {
            throw new InvalidUriFormatException("Invalid courtsiteCode supplied.");
        }
        if (!isValidDisplayUriString(location)) {
            throw new InvalidUriFormatException("Invalid location supplied.");
        }
        if (!isValidDisplayUriString(display)) {
            throw new InvalidUriFormatException("Invalid display supplied.");
        }
        String uri = DISPLAY_URI_PREFIX + courthouseName + '/' + courtsiteCode + '/' + location
            + '/' + display;
        checkUriLength(uri);
        return uri;
    }

    private static void checkUriLength(String uri) {
        if (uri.length() > MAX_DISPLAY_URI_LENGTH) {
            throw new InvalidUriFormatException(uri,
                "The URI supplied exceeds the maximum limit of " + MAX_DISPLAY_URI_LENGTH
                    + " characters.");
        }
    }
    
    private static boolean isValidDisplayUriString(String value) {
        return value != null && value.length() != 0
            && isValidDisplayUriCharacters(value.toCharArray(), 0, value.length());
    }

    private static boolean isValidDisplayUriCharacters(char[] actual, int offset, int length) {
        for (int i = 0; i < length; i++) {
            if (!isValidDisplayUriCharacter(actual[offset + i])) {
                return false;
            }
        }
        return true;
    }

    private static boolean isValidDisplayUriCharacter(char actual) {
        return Character.isDigit(actual) || Character.isLetter(actual) || actual == '_';
    }

    @Override
    public boolean equals(Object obj) {
        LOG.debug("equals()");
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        LOG.debug("hashCode()");
        return super.hashCode();
    }

}
