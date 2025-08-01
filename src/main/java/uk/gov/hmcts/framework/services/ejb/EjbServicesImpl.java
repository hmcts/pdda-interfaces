package uk.gov.hmcts.framework.services.ejb;

import jakarta.ejb.EJBException;
import jakarta.ejb.EJBHome;
import jakarta.ejb.EJBLocalHome;
import jakarta.ejb.EJBLocalObject;
import jakarta.ejb.EJBObject;
import jakarta.ejb.Handle;
import jakarta.ejb.ObjectNotFoundException;
import jakarta.ejb.RemoveException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.framework.exception.CsUnrecoverableException;
import uk.gov.hmcts.framework.services.CsServices;
import uk.gov.hmcts.framework.services.EjbServices;
import uk.gov.hmcts.framework.services.ServiceLocator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**

 * Title: EJBServicesImpl.


 * Description: Implementation of the <code>EJBServices</code> interface. Provides a range of
 * Utilities for finding and using EJBs. In certain circumstances this can be used instead of the
 * framework ServiceLocator and is designed to provided additional functionality to the
 * erviceLocator.


 * Copyright: Copyright (c) 2002


 * Company: EDS

 * @author Pete Raymond much code taken from the work of Nick Lawson
 * @version 1.0
 */
public final class EjbServicesImpl implements EjbServices {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EjbServicesImpl.class);

    /** Singleton instance. */
    private static EjbServicesImpl instance = new EjbServicesImpl();

    /** Service locator used by this EJB service. */
    private final ServiceLocator locator;

    /**
     * Private constructor.
     */
    private EjbServicesImpl() {
        locator = CsServices.getServiceLocator();
        LOGGER.info("EJBServicesImpl created");
    }

    /**
     * Singleton accessor.

     */
    public static EjbServicesImpl getInstance() {
        return instance;
    }

    /**
     * Creates a remote session.
     */
    @Override
    public EJBObject createRemoteSession(Class<?> klass) {
        try {
            EJBHome home = locator.getRemoteHome(klass);

            Class<?>[] classes = {};
            Method createMethod = home.getClass().getMethod("create", classes);
            Object[] args = {};
            return (EJBObject) createMethod.invoke(home, args);

        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            throw handleException(e);
        }
    }

    /**
     * createLocalSession.
     */
    @Override
    public EJBLocalObject createLocalSession(Class<?> klass) {
        try {
            EJBLocalHome home = locator.getLocalHome(klass);

            Class<?>[] classes = {};
            Method createMethod = home.getClass().getMethod("create", classes);
            Object[] args = {};
            return (EJBLocalObject) createMethod.invoke(home, args);

        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            throw handleException(e);
        }
    }

    /**
     * findLocalEntityByPrimaryKey.
     */
    @Override
    public EJBLocalObject findLocalEntityByPrimaryKey(Class<?> ejbLocalHome, Integer primaryKey)
        throws ObjectNotFoundException {
        try {
            EJBLocalHome home = locator.getLocalHome(ejbLocalHome);

            // find an instance:
            Class<?> ejbHomeClass = home.getClass();
            Class<?>[] methodArgs = {Integer.class};
            Method findMethod = ejbHomeClass.getMethod("findByPrimaryKey", methodArgs);

            Object[] args = {primaryKey};
            EJBLocalObject entity = (EJBLocalObject) findMethod.invoke(home, args);
            LOGGER.debug("Returning entity=" + entity);
            return entity;
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof ObjectNotFoundException) {
                throw (ObjectNotFoundException) e.getTargetException();
            } else {
                throw handleException(e);
            }
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw handleException(e);
        }
    }

    /**
     * deleteLocalEntity.
     */
    @Override
    public void deleteLocalEntity(Class<?> ejbLocalHomeClass, Integer primaryKey) {
        try {
            // get a home and remove
            EJBLocalHome home = locator.getLocalHome(ejbLocalHomeClass);
            home.remove(primaryKey);
        } catch (EJBException | RemoveException e) {
            throw handleException(e);
        }
    }

    /**
     * Calculates the jndi name for a class base don the convention that each class is bound to the jndi
     * tree with the same name as its class.

     * @param klass the Class to find the name for
     * @returns the String for the JNDI name
     */
    @Override
    public String getJndiName(Class<?> klass) {
        String fullClassName = klass.getName();
        int lastDotIndex = fullClassName.lastIndexOf('.');
        String jndiName = fullClassName.substring(lastDotIndex + 1, fullClassName.length());
        log("Class=" + klass + " JNDIName=" + jndiName);
        return jndiName;
    }

    /**
     * getSerializedEjbObject.
     */
    @Override
    public String getSerializedEjbObject(EJBObject session) {
        try {
            Handle handle = session.getHandle();
            ByteArrayOutputStream fo = new ByteArrayOutputStream();
            ObjectOutputStream so = new ObjectOutputStream(fo);
            so.writeObject(handle);
            so.flush();
            so.close();
            return new String(fo.toByteArray());
        } catch (IOException ex) {
            throw handleException(ex);
        }
    }

    /**
     * getEjbObject.
     */
    @Override
    public EJBObject getEjbObject(String id) {
        if (id == null) {
            throw new CsUnrecoverableException();
        }
        try {
            byte[] bytes = String.valueOf(id).getBytes();

            try (InputStream io = new ByteArrayInputStream(bytes)) {
                try (ObjectInputStream os = new ObjectInputStream(io)) {
                    Handle handle = (Handle) os.readObject();
                    return handle.getEJBObject();
                }
            }
        } catch (Exception ex) {
            CsUnrecoverableException exception = new CsUnrecoverableException(ex);
            CsServices.getDefaultErrorHandler().handleError(exception, EjbServicesImpl.class);
            throw exception;
        }
    }

    /**
     * Utility method for logging.

     * @param msg String
     */
    private void log(String msg) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(msg);
        }
    }

    /**
     * Utility method for handling exception.

     * @param th Throwable
     * @return CsUnrecoverableException Exception
     */
    private static CsUnrecoverableException handleException(Throwable th) {
        CsUnrecoverableException exception = new CsUnrecoverableException(th);
        CsServices.getDefaultErrorHandler().handleError(exception, EjbServicesImpl.class);
        return exception;
    }

}
