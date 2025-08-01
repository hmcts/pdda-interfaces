package uk.gov.hmcts.pdda.common.publicdisplay.types.uri;

import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.exceptions.InvalidUriFormatException;

import java.io.Serializable;
import java.util.Locale;

/**

 * Title: AbstractURI.


 * Description: A Unique Resource Identifier is used for accessing a specific item of data in a
 * manner which can be identified as a String. In the case of Public Displays this string is of the
 * format (in extended BNF notation):

 * <pre>
 *         pd://document:&lt;courtId&gt;/&lt;documentType&gt;/&lt;locale&gt;:[&lt;courtRoomId&gt;,]+
 *         pd://display/&lt;courthouseName&gt;/&lt;courtSiteCode&gt;/&lt;location&gt;/&lt;display&gt;
 * </pre>

 * <p/>
 * <p/>
 * Copyright: Copyright (c) 2003

 * <p/>
 * <p/>
 * Company: Electronic Data Systems

 * @author Neil Ellis
 * @version $Revision: 1.9 $
 */
public abstract class AbstractUri implements Serializable, Comparable<Object> {

    static final long serialVersionUID = 111750358058789340L;

    protected static final String URI_PREFIX = "pd://";

    private final String cachedUri;

    /**
     * Creates a new AbstractURI object.

     * @param cachedUri String
     * @pre uriType != null
     */
    protected AbstractUri(String cachedUri) {
        if (cachedUri == null) {
            throw new InvalidUriFormatException("Uri can not be null.");
        }
        this.cachedUri = cachedUri;
    }

    /**
     * Returns the cached String representation of this URI.

     * @return the cached String representation of this URI.
     */
    protected String getCachedUri() {
        return cachedUri;
    }

    /**
     * Returns the type of uri this is as a String.

     * @return the type of URI this is.
     * @post return != null
     */
    public abstract String getType();

    /**
     * Returns the locale if any of the URI. Subclasses should either return the default locale or
     * the locale relevant to the URI.

     * @return the localization of the object this URI points to.
     */
    public abstract Locale getLocale();

    /**
     * Comparison method.

     * @param object object to compare to.
     * @return see Object.compareTo()
     * @pre cachedURI != null
     */
    @Override
    public int compareTo(Object object) {
        return getCachedUri().compareTo(String.valueOf(object));
    }

    /**
     * Returns a hashcode for the URI.

     * @return a hashcode for the URI.
     * @pre cachedURI != null
     */
    @Override
    public int hashCode() {
        return cachedUri.hashCode();
    }

    /**
     * Returns the URI in String format.

     * @return the URI as a string.
     * @pre cachedURI != null
     */
    @Override
    public String toString() {
        return cachedUri;
    }

    /**
     * Provides object comparison.

     * @param obj the object to compare with.
     * @return true if they are semantically equal.
     * @pre obj instanceof AbstractURI
     * @pre cachedURI != null
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof AbstractUri && cachedUri.equals(((AbstractUri) obj).cachedUri);
    }

    //
    // Utilities
    //

    protected static boolean equals(char[] expected, int expectedOffset, char[] actual,
        int actualOffset, int length) {
        try {
            for (int i = 0; i < length; i++) {
                if (expected[expectedOffset + i] != actual[actualOffset + i] && actual[actualOffset + i] != 0) {
                    return false;
                }
            }
            return true;
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    protected static int indexOf(char[] source, int offset, char delimiter) {
        return DisplayDocumentUriUtils.indexOf(source, offset, delimiter);
    }

}
