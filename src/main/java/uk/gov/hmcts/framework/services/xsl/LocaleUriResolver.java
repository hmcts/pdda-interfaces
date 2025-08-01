package uk.gov.hmcts.framework.services.xsl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.framework.exception.CsUnrecoverableException;
import uk.gov.hmcts.framework.services.CsServices;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;


/**

 * Title: Locale URI Resover.


 * Description: Resolves names for a given locale


 * Copyright: Copyright (c) 2002


 * Company: EDS

 * @author William Fardell (Xdevelopment 2003)
 * @version 1.0
 */
@SuppressWarnings("PMD.AvoidSynchronizedStatement")
public final class LocaleUriResolver extends UriResolver implements URIResolver {
    private static Logger log = LoggerFactory.getLogger(LocaleUriResolver.class);
    private static final String EMPTY_STRING = "";

    /**
     * Resolver cache.
     */
    private static final Map<Locale, LocaleUriResolver> CACHE = new ConcurrentHashMap<>();

    /**
     * The locale.
     */
    private final Locale locale;

    /**
     * Get a resolver for a given locale.
     */
    public static LocaleUriResolver getResolver(Locale locale) {
        synchronized (LocaleUriResolver.class) {
            if (locale == null) {
                throw new IllegalArgumentException("locale: null");
            }

            LocaleUriResolver resolver = CACHE.get(locale);
            if (resolver == null) {
                resolver = new LocaleUriResolver(locale);
                CACHE.put(locale, resolver);
            }
            return resolver;
        }
    }

    /**
     * Construct a URI resolver for the given Locale.

     * @throws IllegalArgumentException if the locale is null
     */
    private LocaleUriResolver(Locale locale) {
        super();
        if (locale == null) {
            throw new IllegalArgumentException("locale: " + locale);
        }
        this.locale = locale;
    }

    /**
     * Returns the locale.

     * @return the locale
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * URIResolver Implementation, resolve the name to the source.

     * @param href An href attribute, which may be relative or absolute.
     * @param base The base URI in effect when the href attribute was encountered.
     * @return A Source object, or null if the href cannot be resolved, and the processor should try to
     *         resolve the URI itself.
     * @throws TransformerException - if an error occurs when trying to resolve the URI.
     */
    @Override
    public Source resolve(String href, String base) throws TransformerException {
        log.debug("resolve({},{})", href, base);
        try {
            String uri = resolveName(href, base);
            StreamSource source = new StreamSource(CsServices.getLocaleServices().openStream(locale, uri));
            // System id needed for subsequent name resolutions
            source.setSystemId(uri);
            return source;
        } catch (CsUnrecoverableException csue) {
            log.error("An error occured resolving href \"" + href + "\" from base \"" + base + "\".", csue);
            return null;
        }
    }

    /**
     * Resolve the name if it is relative against the given base, this is an interface into some code
     * stripped out of XALAN (did not want the dependency).

     * @return the resolved base
     */
    public static String resolveName(String urlString, String base) throws TransformerException {
        String resolvedUrl = base == null ? urlString : getAbsoluteUri(urlString, base);
        log.debug("Resolved url \"" + urlString + "\"" + " to \"" + resolvedUrl + "\" using base \"" + base + "\".");
        return resolvedUrl;
    }

    /**
     * Returns a <code>String</code> representation of this object.

     * @return a <code>String</code> representation of this object
     */
    @Override
    public String toString() {
        return locale.toString();
    }

    //
    // START OF BLOCK COPIED FROM XALAN
    // org.apache.xml.utils.SystemIDResolver
    //

    /**
     * Get absolute URI from a given relative URI.
     * <p/>

     * The URI is resolved relative to the system property "user.dir" if it is available; if not (i.e.
     * in an Applet perhaps which throws SecurityException) then it is currently resolved relative to ""
     * or a blank string. Also replaces all backslashes with forward slashes.

     * @param uri Relative URI to resolve
     * @return Resolved absolute URI or the input relative URI if it could not be resolved.
     */
    public static String getAbsoluteUriFromRelative(String uri) {
        String uriToUse = uri;
        String curdir = EMPTY_STRING;
        try {
            curdir = System.getProperty("user.dir");
        } catch (SecurityException se) {
            log.error(se.getMessage());
        } // user.dir not accessible from applet

        if (null != curdir) {
            String base;
            if (curdir.startsWith(File.separator)) {
                base = getFile(curdir);
            } else {
                base = getFile(curdir, true);
            }
            if (uriToUse != null) {
                // Note: this should arguably stick in a '/' forward
                // slash character instead of the file separator,
                // since we're effectively assuming it's a hierarchical
                // URI and adding in the abs_path separator -sc
                uriToUse = base + System.getProperty("file.separator") + uriToUse;
            } else {
                uriToUse = base + System.getProperty("file.separator");
            }
        }

        return replaceDoubleBackslash(uriToUse);
    }

    //
    // CUT - public static String getAbsoluteURI(String url) As not required
    //

    /**
     * Take a SystemID string and try and turn it into a good absolute URL.

     * @param urlString SystemID string
     * @param base Base URI to use to resolve the given systemID
     * @return The resolved absolute URI
     * @throws TransformerException thrown if the string can't be turned into a URL.
     */
    public static String getAbsoluteUri(String urlString, String base) throws TransformerException {
        String urlStringToUse = urlString;
        String baseToUse = base;
        boolean isAbsouteUrl = false;

        if (isAbsoluteUrl(urlStringToUse, baseToUse)) {
            urlStringToUse = baseToUse.substring(0, baseToUse.indexOf('!') + 1) + urlStringToUse;
            isAbsouteUrl = true;
        }

        if (urlStringToUse.indexOf(':') > 0) {
            // If there is a colon to separate the scheme from the rest,
            // it should be an absolute URL
            isAbsouteUrl = true;
        } else if (urlStringToUse.startsWith(File.separator)) {
            // If the url starts with a path separator, we assume it's
            // a reference to a file: scheme (why do we do this? -sc)
            urlStringToUse = getFile(urlStringToUse);
            isAbsouteUrl = true;
        }

        if (!isAbsouteUrl && (null == baseToUse || baseToUse.indexOf(':') < 0)) {
            if (baseToUse != null && baseToUse.startsWith(File.separator)) {
                baseToUse = getFile(baseToUse);
            } else {
                baseToUse = getAbsoluteUriFromRelative(baseToUse);
            }
        }

        URI uri = getUri(replaceDoubleBackslash(baseToUse), replaceDoubleBackslash(urlStringToUse), isAbsouteUrl);

        return getUriStr(uri);
    }

    private static String getUriStr(URI uri) {
        String uriStr = uri.toString();

        // Not so sure if this is good. But, for now, I'll try it. We really
        // must
        // make sure the return from this function is a URL!
        if (Character.isLetter(uriStr.charAt(0)) && uriStr.charAt(1) == ':' && uriStr.charAt(2) == '/'
            && (uriStr.length() == 3 || uriStr.charAt(3) != '/')
            || uriStr.charAt(0) == '/' && (uriStr.length() == 1 || uriStr.charAt(1) != '/')) {
            uriStr = getFile(uriStr, true);
        }
        return uriStr;
    }



    private static String getPath(String url) {
        String path = EMPTY_STRING;
        if (url != null) {
            final String windowsSlash = "\\";
            final String linuxSlash = "/";
            int lastPos = url.contains(linuxSlash) ? url.lastIndexOf(linuxSlash) : url.lastIndexOf(windowsSlash);
            if (lastPos > 0) {
                path = url.substring(0, lastPos + 1);
            }
        }
        log.debug("getPath({}) = {}", url, path);
        return path;
    }

    private static URI getUri(String base, String urlString, boolean isAbsouteUrl) throws TransformerException {
        try {
            if (null == base || base.length() == 0 || isAbsouteUrl) {
                return new URI(urlString);
            } else {
                URI baseUri = new URI(base);
                String path = getPath(baseUri.getRawPath());
                return new URI(baseUri.getScheme(), path + urlString, baseUri.getFragment());
            }
        } catch (URISyntaxException mue) {
            throw new TransformerException(mue);
        }
    }

    //
    // END OF BLOCK COPIED FROM XALAN org.apache.xml.utils.SystemIDResolver
    //

}
