package uk.gov.hmcts.framework.client.delegate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.framework.exception.CsUnrecoverableException;
import uk.gov.hmcts.framework.services.CsServices;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Properties;
import java.util.Stack;
import java.util.StringTokenizer;


/**
 *         To change the template for this generated type comment go to Window - Preferences - Java
 *         - Code Generation - Code and Comments.
 * @author pznwc5
 * 
 */
@SuppressWarnings("PMD.LawOfDemeter")
public final class DecoratorFactory {
    private static final Logger LOG = LoggerFactory.getLogger(DecoratorFactory.class);

    /** Decorator properties. */
    private static Properties prop;

    /** Decorator prototype. */
    private static DecoratingHandler decoratorPrototype;

    /**
     * Populates the decorators.
     */
    static {
        createDecorators();
    }

    /**
     * Private constructor to stop instantiation.
     * 
     */
    private DecoratorFactory() {

    }

    /**
     * Creates the decorator prototype.
     */
    private static void createDecorators() {
        prop = CsServices.getConfigServices().getProperties("invoker");
        LOG.debug("createDecorators()");

        Stack<DecoratingHandler> handlers = new Stack<>();

        StringTokenizer decoratorList = new StringTokenizer(prop.getProperty("decorators"), ",");

        while (decoratorList.hasMoreTokens()) {
            String decorator = decoratorList.nextToken().trim();
            String decoratorClass = prop.getProperty(decorator);
            LOG.debug("Decorator==>>" + decorator + ":" + decoratorClass);

            DecoratingHandler handler = createDecorator(decorator, decoratorClass);
            if (!handlers.isEmpty()) {
                DecoratingHandler lastHandler = handlers.peek();
                lastHandler.setNextHandler(handler);
            }
            handlers.push(handler);
        }

        decoratorPrototype = handlers.firstElement();

    }

    /**
     * createDecorator.
     * 
     * @param decorator String
     * @param decoratorClass String
     * @return DecoratingHandler
     */
    private static DecoratingHandler createDecorator(String decorator, String decoratorClass) {
        try {
            DecoratingHandler handler =
                (DecoratingHandler) Class.forName(decoratorClass).newInstance();

            PropertyDescriptor[] pds =
                Introspector.getBeanInfo(handler.getClass()).getPropertyDescriptors();

            for (PropertyDescriptor pd : pds) {
                // All properties are treated as Strings for the time being
                String val = System.getProperty(decorator + "." + pd.getName());
                if (val == null) {
                    val = prop.getProperty(decorator + "." + pd.getName());
                }
                if (val == null) {
                    continue;
                }
                LOG.debug(pd.getName() + ":" + val);
                Object[] args = {val};
                pd.getWriteMethod().invoke(handler, args);
            }
            return handler;
        } catch (Exception ex) {
            throw new CsUnrecoverableException(ex);
        }
    }

    /**
     * Gets a decorated invocation handler.
     * 
     * @param base Base invocation handler
     * @return Decorated invocation handler
     */
    public static XhibitHandler getDecoratedHandler(CsBusinessDelegateHandler base) {
        DecoratingHandler copy = decoratorPrototype.deepCopy();
        copy.setTarget(base);
        return copy;
    }

}
