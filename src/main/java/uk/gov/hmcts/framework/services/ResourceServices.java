package uk.gov.hmcts.framework.services;

import uk.gov.hmcts.framework.exception.CsUnrecoverableException;
import uk.gov.hmcts.framework.exception.Message;

import java.io.InputStream;
import java.util.Locale;

/**

 * Title: Resource Services.


 * Description: Loads a resource (xsl, xml, images etc) for a given locale.


 * Use: Use ResourceServices.getInstance() to get an instance of the resource services

 * Copyright: Copyright (c) 2002


 * Company: EDS

 * @author William Fardell (Xdevelopment 2003)
 * @version 1.0
 */

@SuppressWarnings("PMD.LawOfDemeter")
public final class ResourceServices {

    private static final char FULLSTOP = '.';
    /**
     * Lookup error key.
     */

    private static final String LOOKUP_ERROR_KEY = "resourceservices.lookuperror";

    /**
     * The singleton instance.
     */
    private static final ResourceServices INSTANCE = new ResourceServices();

    /**
     * Get the singleton.

     * @return the ResourceServices singleton instance
     */
    public static ResourceServices getInstance() {
        return INSTANCE;
    }

    /**
     * Stop external construction of the singleton.
     */
    private ResourceServices() {
        super();
    }

    /**
     * Get the resource for the default locale.

     * @param name the name of the resource to retrieve
     * @return an input stream to read the resource
     * @throws IllegalArgumentException if parameter is null
     */
    public InputStream getResourceAsStream(String name) {
        if (name == null) {
            throw new IllegalArgumentException("name: " + name);
        }
        return getResourceAsStream(name, Locale.getDefault());
    }

    /**
     * Get the resource for the specified locale.

     * @param name the name of the resource to retrieve
     * @param locale the locale to use
     * @return an input stream to read the resource
     */
    public InputStream getResourceAsStream(String name, Locale locale) {
        validateResourceAsStream(name, locale);

        InputStream is;

        if (name.lastIndexOf(FULLSTOP) == -1) {
            is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(name + "_" + locale.getLanguage() + "_" + locale.getCountry());
            if (is != null) {
                return is;
            }
            is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(name + "_" + locale.getLanguage());
            if (is != null) {
                return is;
            }
        } else {
            String nameWithoutExtension = name.substring(0, name.lastIndexOf(FULLSTOP));
            String extension = name.substring(name.lastIndexOf(FULLSTOP));
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(nameWithoutExtension
                + "_" + locale.getLanguage() + "_" + locale.getCountry() + extension);
            if (is != null) {
                return is;
            }
            is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(nameWithoutExtension + "_" + locale.getLanguage() + extension);
            if (is != null) {
                return is;
            }
        }
        is = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
        if (is != null) {
            return is;
        }

        throw new CsUnrecoverableException(
            new Message(LOOKUP_ERROR_KEY, name, locale),
            "Could not find resource \"" + name + "\" in locale \"" + locale + "\"");
    }
    
    private void validateResourceAsStream(String name, Locale locale) {
        if (name == null) {
            throw new IllegalArgumentException("name: " + name);
        }
        if (locale == null) {
            throw new IllegalArgumentException("locale: " + locale);
        }
    }
}
