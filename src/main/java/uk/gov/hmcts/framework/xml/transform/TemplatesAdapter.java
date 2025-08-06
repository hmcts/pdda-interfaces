package uk.gov.hmcts.framework.xml.transform;

import java.util.Properties;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;

/**
 * Allows the values of a template to be adapted.

 * @author Will Fardell
 */
public class TemplatesAdapter implements Templates {
    private final Templates delegate;

    /**
     * Construct a new adapter.

     * @author Will Fardell
     */
    public TemplatesAdapter(Templates delegate) {
        this.delegate = delegate;
    }

    /**
     * Templates implemenation.
     */
    @Override
    public Transformer newTransformer() throws TransformerConfigurationException {
        if (delegate != null) {
            Transformer transformer = delegate.newTransformer();
            if (transformer != null) {
                configureTransformer(transformer);
                return transformer;
            }
        }
        return null;
    }

    /**
     * Overriden by child classes to configure the transformer.
     */
    protected void configureTransformer(Transformer transformer)
        throws TransformerConfigurationException {
        // do nothing
    }

    /**
     * Templates implemenation.
     */
    @Override
    public Properties getOutputProperties() {
        if (delegate != null) {
            Properties properties = delegate.getOutputProperties();
            if (properties != null) {
                configureOutputProperties(properties);
                return properties;
            }
        }
        return new Properties();
    }

    /**
     * Overriden by child classes to configure the output properties.
     */
    protected void configureOutputProperties(Properties properties) {
        // do nothing
    }

}
