package uk.gov.hmcts.framework.client.delegate;

import jakarta.ejb.EJBHome;
import jakarta.ejb.EJBObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.framework.services.CsServices;
import uk.gov.hmcts.framework.services.ServiceLocator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.PrivilegedExceptionAction;

/**
 * The class provides decoration to log the invocations.

 * @author pznwc5
 * @version $Revision: 1.7 $
 */
public abstract class XhibitHandler implements InvocationHandler {
    // Logger
    protected static final Logger LOG = LoggerFactory.getLogger(XhibitHandler.class);

    protected XhibitHandler() {
        // Protected constructor
    }

    /**
     * matchMethod.

     * @param in method on the business delegate
     * @param remote Target of invocation
     * @return Target method
     * @throws NoSuchMethodException Exception
     */
    protected static Method matchMethod(Method in, EJBObject remote) throws NoSuchMethodException {
        Method[] declaredMethods = remote.getClass().getDeclaredMethods();

        // Iterate through the declared methods
        for (Method declaredMethod : declaredMethods) {
            // Match the method named ad parameters
            if (isMethodNameValid(in, declaredMethod) && isMethodParamsValid(in, declaredMethod)) {
                // We have found a match
                return declaredMethod;
            }
        }

        // No match found
        throw new NoSuchMethodException(
            "Method " + in.toString() + " not found on remote object " + remote.getClass().getName());
    }

    private static boolean isMethodNameValid(Method in, Method declaredMethod) {
        // Match the method named
        return in.getName().equals(declaredMethod.getName());
    }

    private static boolean isMethodParamsValid(Method in, Method declaredMethod) {
        Class<?>[] inTypes = in.getParameterTypes();
        Class<?>[] declaredTypes = declaredMethod.getParameterTypes();

        // Check the number of arguments match
        boolean isValid = inTypes.length == declaredTypes.length;

        if (isValid) {
            // Check each argument is assignable
            for (int j = 0; j < inTypes.length; j++) {
                if (!inTypes[j].isAssignableFrom(declaredTypes[j])) {
                    isValid = false;
                    break;
                }
            }
        }

        return isValid;
    }

    /**
     * Utility method to get the remote object.

     * @return EJBObject
     * @throws SecurityException exception
     * @throws NoSuchMethodException exception
     * @throws InvocationTargetException exception
     * @throws IllegalAccessException exception
     */
    protected static EJBObject getRemote(CsBusinessDelegateInfo delegateInfo) throws NoSuchMethodException,
        IllegalAccessException, InvocationTargetException {
        ServiceLocator sl = CsServices.getServiceLocator();
        EJBHome home = sl.getRemoteHome(delegateInfo.getHomeClass());
        Method createMethod = home.getClass().getMethod("create", new Class<?>[0]);
        return (EJBObject) createMethod.invoke(home, (Object[]) null);
    }

    /**
     * An inner class for performing remote invocation.

     * @author Meeraj Kunnumpurath
     */
    protected static class RemoteInvoker implements PrivilegedExceptionAction<Object> {
        /** The business method that needs to be invoked. */
        private Method businessMethod;

        /** Target remote object. */
        private Object target;

        /** Invocation arguments. */
        private Object[] args;

        /**
         * Constructor initializes the required information for invocation.

         * @param businessMethod Method
         * @param target Object
         * @param args ObjectArray
         */
        public RemoteInvoker(Method businessMethod, Object target, Object... args) {
            setBusinessMethod(businessMethod);
            setTarget(target);
            setArgs(args);
        }

        /**
         * PrivilegedExceptionACtion implementation.

         * @return Object
         * @throws InvocationTargetException Exception
         * @throws IllegalAccessException Exception
         */
        @Override
        public Object run() throws InvocationTargetException, IllegalAccessException {
            return invoke();
        }

        /**
         * This invokes the method.

         * @return Object
         * @throws InvocationTargetException Exception
         * @throws IllegalAccessException Exception
         */
        public Object invoke() throws InvocationTargetException, IllegalAccessException {
            return businessMethod.invoke(target, args);
        }

        /**
         * setBusinessMethod.
         */
        private void setBusinessMethod(Method businessMethod) {
            this.businessMethod = businessMethod;
        }

        /**
         * setTarget.
         */
        private void setTarget(Object target) {
            this.target = target;
        }

        /**
         * setArgs.
         */
        private void setArgs(Object... args) {
            this.args = args;
        }
    }
}
