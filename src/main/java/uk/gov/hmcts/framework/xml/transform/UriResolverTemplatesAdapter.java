package uk.gov.hmcts.framework.xml.transform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.URIResolver;



/**
 * Allows the values of a template to be adapted.

 * @author Will Fardell
 */
@SuppressWarnings("PMD.LawOfDemeter")
public class UriResolverTemplatesAdapter extends TemplatesAdapter {
    private static Logger log = LoggerFactory.getLogger(UriResolverTemplatesAdapter.class);

    private final URIResolver resolver;

    /**
     * Construct a new adapter.

     * @author Will Fardell
     */
    public UriResolverTemplatesAdapter(Templates delegate, URIResolver resolver) {
        super(delegate);
        this.resolver = resolver;
    }

    /**
     * Overriden by child classes to configure the transformer.
     */
    @Override
    protected void configureTransformer(Transformer transformer)
        throws TransformerConfigurationException {
        if (log.isDebugEnabled()) {
            log.debug("Changeing resolver from " + transformer.getURIResolver() + " to revolver "
                + resolver + " for transformer " + transformer + " .");
        }
        transformer.setURIResolver(resolver);
    }
}
