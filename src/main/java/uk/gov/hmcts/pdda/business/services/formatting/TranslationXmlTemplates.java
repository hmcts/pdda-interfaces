package uk.gov.hmcts.pdda.business.services.formatting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
import java.util.Properties;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

/**
 * TranslationXmlTemplates.
 */

public class TranslationXmlTemplates implements Templates {

    private static final Logger LOG = LoggerFactory.getLogger(TranslationXmlTemplates.class);
    private static final String TRANSLATION_XML = "translation.xml";
    private final String translationXml;

    private final Templates delegate;

    /**
     * Constuct a new Translation XML Template wrapper.
     * 
     * @param translationXml (optional)
     * @param delegate Templates
     */
    public TranslationXmlTemplates(String translationXml, Templates delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("delegate: null");
        }
        this.translationXml = translationXml;
        this.delegate = delegate;
    }

    /**
     * Create a new transformer wrapping the resolver with a resolver that returns the translations.
     */
    @Override
    public Transformer newTransformer() throws TransformerConfigurationException {
        Transformer transformer = delegate.newTransformer();
        if (transformer != null) {
            transformer.setURIResolver(new TranslationXmlUriResolver(translationXml, transformer.getURIResolver()));
        }
        return transformer;
    }

    /**
     * Get the output properties for the delegate.
     */
    @Override
    public Properties getOutputProperties() {
        return delegate.getOutputProperties();
    }

    /**
     * Resolve the translation xml entity to the translation XML.
     */
    private static class TranslationXmlUriResolver implements URIResolver {

        private final String translationXml;

        private final URIResolver delegate;

        /**
         * Construct a new instance.
         * 
         * @param translationXml (optional)
         * @param delegate (optional)
         */
        public TranslationXmlUriResolver(String translationXml, URIResolver delegate) {
            this.translationXml = translationXml;
            this.delegate = delegate;
        }

        /**
         * URIResolver Implementation, resolve the name to the source.
         * 
         * @param href An href attribute, which may be relative or absolute.
         * @param base The base URI in effect when the href attribute was encountered.
         * @return A Source object, or null if the href cannot be resolved, and the processor should try to
         *         resolve the URI itself.
         * @throws TransformerException - if an error occurs when trying to resolve the URI.
         */
        @Override
        public Source resolve(String href, String base) throws TransformerException {
            if (TRANSLATION_XML.equals(href)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Resolved url \"translation.xml\" to cached translation xml.");
                }

                // If we have translation xml return it
                if (translationXml != null) {
                    return new StreamSource(new StringReader(translationXml), TRANSLATION_XML);
                }
            } else if (delegate != null) {
                // If we have a delegate allow it to try
                return delegate.resolve(href, base);
            }
            // Allow system to have a go
            return null;
        }
    }
}
