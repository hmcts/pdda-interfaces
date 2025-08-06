package uk.gov.hmcts.framework.services.locator;

import jakarta.ejb.EJBHome;
import jakarta.ejb.EJBLocalHome;
import jakarta.transaction.UserTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.framework.exception.CsUnrecoverableException;
import uk.gov.hmcts.framework.security.ServerRepository;
import uk.gov.hmcts.framework.services.ConfigServices;
import uk.gov.hmcts.framework.services.CsServices;
import uk.gov.hmcts.framework.services.ServiceLocator;

import java.security.PrivilegedActionException;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import javax.sql.DataSource;

/**

 * Title: LocatorServicesImpl.


 * Description: Implementation of LocatorServices provides basic lookup


 * Copyright: Copyright (c) 2002


 * Company: EDS

 * @author Pete Raymond
 * @version 1.0
 */
public abstract class ServiceLocatorImpl implements ServiceLocator {

    /** Property set service locator eager loading. */
    private static final String LOCATOR_EAGER_LOAD = "locator.eager.load";

    /** Config services. */
    private static ConfigServices config = CsServices.getConfigServices();

    /** Default server. */
    private static final String DEFAULT_SERVER = config.getProperty("defaultServer");

    /** Default datasource JNDI name. */
    private static final String DS_NAME =
        CsServices.getConfigServices().getProperty("xhibit.datasourcename");

    /** A reference to the logger. */
    private static Logger log = LoggerFactory.getLogger(ServiceLocatorImpl.class);

    /** Cache for service locators. */
    private static ServiceLocator self = createNewInstance();

    public static ServiceLocatorImpl createNewInstance() {
        boolean eagerLoad = config.getProperty(LOCATOR_EAGER_LOAD) != null;
        if (eagerLoad) {
            log.info("Eager loading service locator");
            return new EagerLoadingServiceLocatorImpl();
        } else {
            log.info("Lazy loading service locator");
            return new LazyLoadingServiceLocatorImpl();
        }
    }

    /**
     * Returns the service locator for unauthenticated access to the default server.

     * @return the instance of ServiceLocator class
     */
    public static ServiceLocator getInstance() {
        return self;
    }

    /**
     * Gets the initial context.

     * @return InitialContext
     */
    @Override
    @SuppressWarnings("finally")
    public InitialContext getInitialContext() {
        InitialContext ic = null;
        try {
            ic = new InitialContext(getEnv());
        } catch (NamingException e) {
            log.error("Error: {}", e.getMessage());
        }
        return ic;
    }

    /**
     * Gets the default datasource.

     * @return DataSource
     */
    @Override
    public DataSource getDataSource() {
        return getDataSource(null);
    }

    /**
     * Gets the default datasource.

     * @return DataSource
     */
    @Override
    public DataSource getDataSource(String databaseName) {
        String dsJndiName;
        log.info("databaseName::{}", databaseName);
        if (databaseName != null) {
            dsJndiName =
                CsServices.getConfigServices().getProperty(databaseName + ".datasourcename");
            if (dsJndiName == null) {
                throw new CsUnrecoverableException(
                    "Could not find datasource name for database \"" + databaseName + "\".");
            }
        } else {
            dsJndiName = DS_NAME;
        }

        log.info("dsJndiname::{}", dsJndiName);
        return (DataSource) lookup(dsJndiName);
    }

    /**
     * Gets a remote EJB.

     * @param homeClass Class
     * @return EJBHome
     */
    @Override
    public EJBHome getRemoteHome(Class<?> homeClass) {
        Object homeObject = lookup(getHomeJndiName(homeClass));
        return (EJBHome) PortableRemoteObject.narrow(homeObject, homeClass);
    }

    /**
     * Gets a local EJB.

     * @param homeClass Class
     * @return EJBLocalHome
     */
    @Override
    public EJBLocalHome getLocalHome(Class<?> homeClass) {
        return (EJBLocalHome) lookup(getHomeJndiName(homeClass));
    }

    /**
     * Gets a user transaction.

     * @return UserTransaction
     */
    @Override
    public UserTransaction getUserTx() {
        Context ctx = this.getInitialContext();
        try {
            return (UserTransaction) ctx.lookup("java:comp/UserTransaction");
        } catch (NamingException ex) {
            throw new CsUnrecoverableException(ex);
        } finally {
            try {
                ctx.close();
            } catch (NamingException e1) {
                log.error(e1.getMessage(), e1);
            }
        }
    }

    /**
     * Utility method to populate the JNDI properties.

     * @return Properties
     */
    protected Properties getEnv() {
        Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY,
            ServerRepository.getInitialContextFactory(DEFAULT_SERVER));
        env.put(Context.PROVIDER_URL, ServerRepository.getProviderUrl(DEFAULT_SERVER));

        return env;
    }

    /**
     * Utility method to lookup objects.

     * @param jndiName String
     * @return Object
     */
    protected abstract Object lookup(String jndiName);

    /**
     * Handles exception.

     * @param ex PrivilegedActionException
     */
    protected static CsUnrecoverableException handleException(PrivilegedActionException ex) {
        CsServices.getDefaultErrorHandler().handleError(ex, ServiceLocatorImpl.class);
        return new CsUnrecoverableException(ex.getException());
    }

    /**
     * Gets the home JNDI name.

     * @param clazz Class
     * @return String
     */
    private static String getHomeJndiName(Class<?> clazz) {
        String fullClassName = clazz.getName();
        int lastDotIndex = fullClassName.lastIndexOf('.');

        return fullClassName.substring(lastDotIndex + 1, fullClassName.length());
    }
}
