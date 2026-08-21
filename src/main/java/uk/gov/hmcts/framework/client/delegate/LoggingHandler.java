package uk.gov.hmcts.framework.client.delegate;

import java.lang.reflect.Method;

/**
 * The class provides decoration to log the invocations.

 * @author pznwc5
 */
public class LoggingHandler extends DecoratingHandler {

    /*
     * (non-Javadoc)

     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method,
     * java.lang.Object[])
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        LOG.debug("Home class: {}", getDelegateInfo().getHomeClass());
        LOG.debug("Delegate class: {}", getDelegateInfo().getDelegateClass());
        LOG.debug("Method: {}", method);
        LOG.debug("Arguments\\");
        for (int i = 0; args != null && i < args.length; i++) {
            LOG.debug("\t{}", args[i]);
        }
        Object result = super.invoke(proxy, method, args);
        LOG.debug("Return value: {}", result);
        return result;
    }
}
