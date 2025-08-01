package uk.gov.hmcts.framework.client.delegate;

import jakarta.ejb.EJBObject;
import jakarta.transaction.TransactionRolledbackException;
import uk.gov.hmcts.framework.exception.CsBusinessException;
import uk.gov.hmcts.framework.exception.CsUnrecoverableException;
import uk.gov.hmcts.framework.services.CsServices;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;

/**
 * CsBusinessDelegateHandler.
 */
public class CsBusinessDelegateHandler extends XhibitHandler {

    /**
     * EJBObject associated with this business delegate.
     */
    private final EJBObject remote;

    /**
     * Number of retries.
     */
    private static final int MAX_ATTEMPTS = 3;

    /**
     * Business delegate information.
     */
    protected final CsBusinessDelegateInfo delegateInfo;

    /**
     * Constructor initailizes delegate and access information.


     * @param delegateInfo CSBusinessDelegateInfo
     * @throws Throwable Exception
     */
    public CsBusinessDelegateHandler(CsBusinessDelegateInfo delegateInfo)
        throws Throwable {
        super();
        if (delegateInfo == null) {
            throw new IllegalArgumentException("delegateInfo: null");
        }
        this.delegateInfo = delegateInfo;

        try {
            // Get the EJB remote object
            LOG.debug("CsBusinessDelegateHandler({}), getting remote object for", delegateInfo.getClass());
            remote = getRemote(delegateInfo);
        } catch (Exception ex) {
            // This is an unexpected exception
            CsUnrecoverableException exception = new CsUnrecoverableException(ex);
            CsServices.getDefaultErrorHandler().handleError(ex, CsBusinessDelegateHandler.class);
            throw exception;

        }

    }

    protected CsBusinessDelegateInfo getDelegateInfo() {
        return delegateInfo;
    }

    /**
     * Invocation handler implementation that maps a method invocation on the dynamic proxy to
     * session facade invocation.
     */
    @Override
    public Object invoke(Object obj, Method method, Object[] args) {

        // Business method
        Method businessMethod;
        Throwable lastException = null;
        int attempts = 0;
        while (attempts < MAX_ATTEMPTS) {
            try {

                attempts++;

                // Get the business method
                businessMethod = matchMethod(method, remote);

                // Create a remote invoker
                RemoteInvoker invoker = getRemoteInvoker(businessMethod, args);

                return invoker.invoke();
            } catch (InvocationTargetException ex) {
                // RemoteException is bubbled up
                lastException = processInvocationTargetExceptions(ex);
            } catch (CsUnrecoverableException ex) {
                if (ex.getCause() instanceof TransactionRolledbackException) {
                    lastException = ex;
                    LOG.warn(ex.getMessage());
                } else {
                    rethrow(ex);
                }
            } catch (Exception ex) {
                // This is unexpected and cased by the signature of
                // PrivilegedActionException.getException
                rethrow(ex);
            }
        }
        final String message = "Method " + method.getName() + " was invoked " + MAX_ATTEMPTS
            + " times and still failed.";
        LOG.error(message, lastException);
        throw new CsUnrecoverableException(message, lastException);
    }
    
    private RemoteInvoker getRemoteInvoker(Method businessMethod, Object... args) {
        return new RemoteInvoker(businessMethod, remote, args);
    }

    private Throwable processInvocationTargetExceptions(InvocationTargetException ex) {
        if (ex.getTargetException() instanceof RemoteException) {
            rethrow(ex);
        }
        // TransactionRolledBackException triggers a retry
        InvocationTargetException lastException;
        if (ex.getTargetException() instanceof TransactionRolledbackException) {
            lastException = ex;
            LOG.warn(ex.getMessage());
        } else {
            // If we reach here it is a business exception
            if (ex.getTargetException() instanceof CsBusinessException) {
                LOG.debug("Exception is instance of CsBusinessException. {}", ex.getMessage());
                throw (CsBusinessException) ex.getTargetException();
            } else {
                LOG.error(
                    "Incorrect Exception thrown from session facade, please consult the developer guide.",
                    ex.getTargetException());
                throw new CsUnrecoverableException(ex.getTargetException());
            }
        }
        return lastException;
    }

    private static void rethrow(Exception ex) {
        CsUnrecoverableException exception = new CsUnrecoverableException(ex);
        CsServices.getDefaultErrorHandler().handleError(exception, CsBusinessDelegateHandler.class);
        throw exception;
    }

}
