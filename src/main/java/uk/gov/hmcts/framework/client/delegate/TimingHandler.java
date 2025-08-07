package uk.gov.hmcts.framework.client.delegate;

import java.lang.reflect.Method;

/**
 * The class provides decoration to time the invocations.
 * 
 * @author pznwc5
 */
public class TimingHandler extends DecoratingHandler {

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method,
     * java.lang.Object[])
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        long now = System.currentTimeMillis();
        Object result = super.invoke(proxy, method, args);
        long duration = System.currentTimeMillis() - now;

        LOG.info("Time taken to execute {} on {}: {}",
            method, getDelegateInfo().getDelegateClass(), duration);
        return result;
    }
}
