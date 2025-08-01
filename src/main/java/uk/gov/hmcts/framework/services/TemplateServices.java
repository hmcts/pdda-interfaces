package uk.gov.hmcts.framework.services;

import org.xml.sax.XMLFilter;
import uk.gov.hmcts.framework.exception.CsUnrecoverableException;
import uk.gov.hmcts.framework.exception.Message;
import uk.gov.hmcts.framework.services.xsl.LocaleUriResolver;
import uk.gov.hmcts.framework.xml.transform.ParameterTemplatesAdapter;
import uk.gov.hmcts.framework.xml.transform.UriResolverTemplatesAdapter;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.XMLConstants;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXTransformerFactory;

/**

 * Title: XSL Services.


 * Description: Insulates the application components from knowedge of XSL.


 * Copyright: Copyright (c) 2002


 * Company: EDS

 * @author William Fardell (Xdevelopment 2003)
 * @version 1.0
 */
@SuppressWarnings("PMD.AvoidSynchronizedStatement")
public class TemplateServices {

    protected static final String RESOLVED_BY = "\" resolved by \"";

    /**
     * Templates cache this map contains maps indexed by systemId, the indexed maps contain the
     * Templates indexed by resolvers.
     */
    private final Map<Object, Object> templatesCache = new ConcurrentHashMap<>();

    /**
     * TransformerFactory cache this map contains TransformerFactory's indexed by resolvers.
     */
    private final Map<Object, Object> transformerFactoryCache = new ConcurrentHashMap<>();

    /**
     * Get XMLFilter array pipeline for templates array passed in.

     * @param templates TemplatesArray
     * @return XMLFilterArray
     * @throws TransformerException Exception
     */
    public XMLFilter[] getFilters(Templates... templates) throws TransformerException {
        if (templates == null) {
            throw new IllegalArgumentException("templates: null");
        }

        XMLFilter[] filters = new XMLFilter[templates.length];
        SAXTransformerFactory stf = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
        for (int i = 0; i < filters.length; i++) {
            filters[i] = stf.newXMLFilter(templates[i]);

        }
        return filters;
    }

    //
    // Templates Cache
    //

    /**
     * Get an Templates array for String array of xsl names and locale passed in.

     * @param xslNames StringArray
     * @param locale Locale
     * @return TemplatesArray
     */
    public Templates[] getTemplatesArray(String[] xslNames, Locale locale, Map<?, ?> parameterMap) {
        if (xslNames == null) {
            throw new IllegalArgumentException("xslNames: null");
        }
        URIResolver resolver = getResolver(locale);

        Templates[] templates = new Templates[xslNames.length];
        for (int i = 0; i < templates.length; i++) {
            templates[i] = getTemplates(getSystemId(xslNames[i], locale), resolver, parameterMap);
        }

        return templates;
    }

    /**
     * Gets the templates for the xsl name and resolver, private because xsl classes should not be used
     * outside this class.

     * @param systemId the name of the xsl
     * @param resolver the resolver used to create the transform
     * @param parameterMap Map

     * @return the templates
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected Templates getTemplates(String systemId, URIResolver resolver, Map<?, ?> parameterMap) {
        // Cached in a map of maps the first indexed by systemId the second by
        // resolver
        Templates templates;
        synchronized (templatesCache) {
            Map<Object, Object> resolverCache = (Map) templatesCache.get(systemId);
            if (resolverCache != null) {
                templates = (Templates) resolverCache.get(resolver);
                if (templates == null) {
                    templates = createTemplates(systemId, resolver);
                    resolverCache.put(resolver, templates);
                }
            } else {
                resolverCache = new ConcurrentHashMap<>();
                templatesCache.put(systemId, resolverCache);
                templates = createTemplates(systemId, resolver);
                resolverCache.put(resolver, templates);
            }
        }

        // If parameter map not equal to null decorate templates
        if (parameterMap != null) {
            return new ParameterTemplatesAdapter(templates, parameterMap);
        } else {
            return templates;
        }
    }

    /**
     * Create the templates for the xsl name and resolver, private because xsl classes should not be
     * used outside this class.

     * @param systemId the name of the xsl
     * @param resolver the resolver used to create the transform
     * @return the templates for the xsl using the given resolver
     * @throws CsUnrecoverableException if an error occures creating the transform
     */
    protected Templates createTemplates(String systemId, final URIResolver resolver) {
        try {
            return new UriResolverTemplatesAdapter(
                getTransformerFactory(resolver).newTemplates(resolver.resolve(systemId, null)), resolver);
        } catch (TransformerException te) {
            Message userMessage = new Message("xslservices.templateserror", systemId, resolver);
            String logMessage = "Could not create templates from xsl \"" + systemId + RESOLVED_BY + resolver + "\"";
            throw new CsUnrecoverableException(userMessage, te, logMessage);
        }
    }

    //
    // TransformerFactory Cache
    //

    /**
     * Get the transformer factory for the resolver, private because xsl classes should not be used
     * outside this class.

     * @param resolver the resolver used to by the transform factory to create templates
     * @return the transformer factory for the resolver
     * @throws CsUnrecoverableException if an error occures creating the transform
     */
    protected TransformerFactory getTransformerFactory(URIResolver resolver) {
        TransformerFactory factory = (TransformerFactory) transformerFactoryCache.get(resolver);
        if (factory == null) {
            factory = createTransformerFactory(resolver);
            transformerFactoryCache.put(resolver, factory);
        }
        return factory;
    }

    //
    // Utilities
    //

    /**
     * This is more complex than desirable, we need to take the resource name and resolve it in a
     * localised way to get the actual URL of the resource inside the jar, we then need to take this url
     * and strip out any localisation that was added so when it gets passed into the
     * LocalisedURIResolver it behaves properly, the reason we cant just create a stream from the
     * resource we find is we need the URI for the system id to work out any relative uri's.

     * @param locale Locale
     * @return the uri of the resource name
     */
    protected static String getSystemId(String xslName, Locale locale) {
        Locale localeToUse = getLocaleToUse(locale);
        return CsServices.getLocaleServices().getBaseName(localeToUse,
            LocaleServices.getInstance().getResource(localeToUse, xslName));
    }

    protected static Locale getLocaleToUse(Locale locale) {
        return locale == null ? Locale.getDefault() : locale;
    }

    /**
     * Get the URI resolver for the locale.

     * @param locale if null use default
     * @return the resolver
     */
    protected static final URIResolver getResolver(Locale locale) {
        Locale localeToUse = getLocaleToUse(locale);
        return LocaleUriResolver.getResolver(localeToUse);
    }

    /**
     * Get the transformer factory for the resolver, private because xsl classes should not be used
     * outside this class.

     * @param resolver the resolver used by the transform factory
     * @return the transformer factory for the resolver
     * @throws CsUnrecoverableException if an error occures creating the transform
     */
    protected static TransformerFactory createTransformerFactory(URIResolver resolver) {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        // to be compliant, prohibit the use of all protocols by external entities:
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        transformerFactory.setURIResolver(resolver);
        return transformerFactory;
    }
}
