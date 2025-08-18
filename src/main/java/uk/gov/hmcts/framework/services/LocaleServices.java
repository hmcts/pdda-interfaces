package uk.gov.hmcts.framework.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.framework.exception.CsUnrecoverableException;
import uk.gov.hmcts.framework.exception.Message;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**

 * Title: Locale Services.


 * Description: Prvoides services for localising URL and resource streams

 * <p/>

 * Copyright: Copyright (c) 2003


 * Company: EDS

 * @author William Fardell (Xdevelopment 2003)
 * @version 1.0
 */

@SuppressWarnings("PMD.LawOfDemeter")
public final class LocaleServices {

    /**
     * logger.
     */

    private static final Logger LOG = LoggerFactory.getLogger(LocaleServices.class);
    
    private static final String EMPTY_STRING = "";

    /**
     * Lookup error key.
     */

    private static final String LOOKUP_RESOURCE_ERROR_KEY = "localeservices.resource.lookuperror";

    private static final String LOOKUP_URL_ERROR_KEY = "localeservices.url.lookuperror";

    /**
     * The file extension seperator.
     */
    private static final String FILE_EXTENSION_SEPERATOR = ".";

    /**
     * The singleton instance.
     */
    private static final LocaleServices INSTANCE = new LocaleServices();

    /**
     * Get the singleton.

     * @return the ResourceServices singleton instance
     */
    public static LocaleServices getInstance() {
        return INSTANCE;
    }

    /**
     * Stop external construction of the singleton.
     */
    private LocaleServices() {        
    }

    /**
     * Get the url for the resource using the given locale for name resolution see getCandidates,
     * this is a localised version of ClassLoader.getResource(String).

     * @param locale the required locale
     * @param name the name to find
     */
    public String getResource(Locale locale, String name) {
        // we only handle absolute resources so / (absolute to indicate root in
        // Class.getResource) prefix needs to be removed
        Iterator<Object> candidates =
            getCandidates(locale, name.startsWith("/") ? name.substring(1) : name);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Getting resource for {} in locale {}", name, locale);
        }
        while (candidates.hasNext()) {
            URL url = Thread.currentThread().getContextClassLoader().getResource((String) candidates.next());
            if (url != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Resource url: {}", url);
                }
                return url.toString();
            }
        }
        throw new CsUnrecoverableException(
            new Message(LOOKUP_RESOURCE_ERROR_KEY, name, locale),
            "Could not open resource \"" + name + "\" in locale \"" + locale + "\"");
    }

    /**
     * Opens the stream for the url in the given locale for name resolution see getCandidates, this
     * is a localised version of URL.openStream().

     * @param locale the required locale
     * @param url the url to open
     */
    public InputStream openStream(Locale locale, String url) {
        Iterator<?> candidates = getCandidates(locale, url);
        String candidate;
        if (LOG.isDebugEnabled()) {
            LOG.debug("Opening stream for {} in locale {}", url, locale);
        }
        while (candidates.hasNext()) {
            try {

                candidate = (String) candidates.next();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Opening stream for candidate: {}", candidate);
                }
                return createNewUrl(candidate).openStream();
            } catch (IOException ex) {
                // Could not open stream try next
                LOG.debug("Ignored - IOException");
            }
        }
        throw new CsUnrecoverableException(
            new Message(LOOKUP_URL_ERROR_KEY, url, locale),
            "Could not open url \"" + url + "\" in locale \"" + locale + "\"");
    }
    
    private URL createNewUrl(String candidate) throws MalformedURLException {
        return new URL(candidate);
    }

    /**
     * Get the base name, strip any localise info from the name, this requires the locale as
     * otherwise not possible to determine reliably what localisation was added.

     * @param locale the locale the name was localised with
     * @param name the name
     * @return the base name
     */
    public String getBaseName(Locale locale, String name) {
        int extensionIndex = name.lastIndexOf(FILE_EXTENSION_SEPERATOR);
        if (extensionIndex == -1) {
            return getBaseName(locale, name, EMPTY_STRING);
        } else {
            return getBaseName(locale, name.substring(0, extensionIndex),
                name.substring(extensionIndex));
        }
    }

    private String getBaseName(Locale locale, String base, String extension) {
        Iterator<?> localisations = getLocalisations(locale);
        while (localisations.hasNext()) {
            String localisation = (String) localisations.next();
            if (base.endsWith(localisation)) {
                return base.substring(0, base.length() - localisation.length()) + extension;
            }
        }
        return base + extension;
    }

    /**
     * Get a list of possible names given the locale and name.

     * The LAST file extension (if any) is seperated from the base, the extension is the part after
     * and including the file extension seperator '.'
     * The resource bundle lookup searches for classes with various paterns on the basis of (1) the
     * desired locale and (2) the current default locale as returned by Locale.getDefault(), and (3)
     * the root resource bundle (base), in the following order from lower-level (more specific) to
     * parent-level (less specific):
     * <ul>
     * <li>base + "_" + language1 + "_" + country1 + "_" + variant1 + extension
     * <li>base + "_" + language1 + "_" + country1 + extension
     * <li>base + "_" + language1 + extension
     * <li>base + "_" + language2 + "_" + country2 + "_" + variant2 + extension
     * <li>base + "_" + language2 + "_" + country2 + extension
     * <li>base + "_" + language2 + extension
     * <li>base + extension
     * </ul>

     * @param locale the locale to use (the desired locale)
     * @param name the name to get candidates for
     * @return the list of names
     */

    public Iterator<Object> getCandidates(Locale locale, String name) {
        int extensionIndex = name.lastIndexOf(FILE_EXTENSION_SEPERATOR);
        if (extensionIndex == -1) {
            return getCandidates(locale, name, EMPTY_STRING);
        } else {
            return getCandidates(locale, name.substring(0, extensionIndex),
                name.substring(extensionIndex));
        }
    }

    //
    // This has been written to return an Iterator so it can be optimised
    // later if required
    // the construction of the candidates should be delayed until the next()
    // call on the
    // iterator!
    //
    private Iterator<Object> getCandidates(Locale locale, String base, String extension) {
        List<Object> candidateList = new ArrayList<>();
        Iterator<Object> localisations = getLocalisations(locale);
        while (localisations.hasNext()) {
            candidateList.add(base + (String) localisations.next() + extension);
        }
        return candidateList.iterator();
    }

    //
    // This has been written to return an Iterator so it can be optimised
    // later if required
    // the construction of the localisations should be delayed until the
    // next() call
    // on the iterator!
    //
    private Iterator<Object> getLocalisations(Locale locale) {
        List<Object> candidateExtensionsList = new ArrayList<>();

        // Specific

        String language01 = locale.getLanguage();
        String country01 = locale.getCountry();
        String variant01 = locale.getVariant();

        if (!EMPTY_STRING.equals(language01)) {
            if (!EMPTY_STRING.equals(country01)) {
                if (!EMPTY_STRING.equals(variant01)) {
                    candidateExtensionsList
                        .add("_" + language01 + "_" + country01 + "_" + variant01);
                }
                candidateExtensionsList.add("_" + language01 + "_" + country01);
            }
            candidateExtensionsList.add("_" + language01);
        }

        // Default

        Locale def = Locale.getDefault();

        String language02 = def.getLanguage();
        String country02 = def.getCountry();
        String variant02 = def.getVariant();

        if (!EMPTY_STRING.equals(language02)) {
            if (!EMPTY_STRING.equals(country02)) {
                if (!EMPTY_STRING.equals(variant02)) {
                    candidateExtensionsList
                        .add("_" + language02 + "_" + country02 + "_" + variant02);
                }
                candidateExtensionsList.add("_" + language02 + "_" + country02);
            }
            candidateExtensionsList.add("_" + language02);
        }

        // Base

        candidateExtensionsList.add(EMPTY_STRING);

        return candidateExtensionsList.iterator();
    }

}
