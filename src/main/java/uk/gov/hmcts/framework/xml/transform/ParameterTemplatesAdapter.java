package uk.gov.hmcts.framework.xml.transform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;



/**
 * Allows the values of a template to be adapted.
 * 
 * @author Will Fardell
 */
public class ParameterTemplatesAdapter extends TemplatesAdapter {
    private static Logger log = LoggerFactory.getLogger(ParameterTemplatesAdapter.class);

    private final Map<?, ?> parameterMap;

    /**
     * Construct a new adapter.
     * 
     * @author Will Fardell
     */
    public ParameterTemplatesAdapter(Templates delegate, Map<?, ?> parameterMap) {
        super(delegate);
        this.parameterMap = parameterMap;
    }

    /**
     * Overriden by child classes to configure the transformer.
     */
    @Override
    protected void configureTransformer(Transformer transformer)
        throws TransformerConfigurationException {
        Iterator<?> keys = parameterMap.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            Object value = parameterMap.get(key);
            if (log.isDebugEnabled()) {
                log.debug("Changeing parameter " + key + " from " + transformer.getParameter(key)
                    + " to " + value + " for transformer " + transformer + ".");
            }
            transformer.setParameter(key, value);
        }
    }
}
