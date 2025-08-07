package uk.gov.hmcts.framework.client.delegate;

import uk.gov.hmcts.framework.services.CsServices;

import java.lang.reflect.Proxy;

/**
 * CsBusinessDelegateFactory.
 */
public final class CsBusinessDelegateFactory {

    /**
     * Singleton instance.
     */
    private static CsBusinessDelegateFactory self = new CsBusinessDelegateFactory();

    private static final boolean DECORATE =
        CsServices.getConfigServices().getProperty("invoker.decorate") != null;

    /**
     * Private constructor initalizes the bundle.
     */
    private CsBusinessDelegateFactory() {
    }

    /**
     * Gets the business delegate factory instance.
     * 
     * @return CsBusinessDelegateFactory
     */
    public static CsBusinessDelegateFactory getInstance() {
        return self;
    }

    /**
     * Retrieve a business delegate from supplied delegate and home class names.
     * 
     * @param delegateClass the name of the delegate class eg "com.xyz.thing.MyDelegate"
     * @param homeClass the name of the home class eg "com.xyz.thing.MyDelegate"
     * @return a valid business delegate
     * @throws Throwable Exception
     */
    public CsBusinessDelegate getBusinessDelegate(Class<?> delegateClass, Class<?> homeClass)
        throws Throwable {
        return retrieveDelegateFromInfo(new CsBusinessDelegateInfo(delegateClass, homeClass));
    }

    // Does the bulk of the delegate work.
    private CsBusinessDelegate retrieveDelegateFromInfo(CsBusinessDelegateInfo info) throws Throwable {

        XhibitHandler handler;
        if (DECORATE) {
            handler = getDecoratedHandler(new CsBusinessDelegateHandler(info));
        } else {
            handler = new CsBusinessDelegateHandler(info);
        }
        return (CsBusinessDelegate) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
            new Class[] {info.getDelegateClass()}, handler);
    }

    private XhibitHandler getDecoratedHandler(CsBusinessDelegateHandler handler) {
        return DecoratorFactory.getDecoratedHandler(handler);
    }
}
