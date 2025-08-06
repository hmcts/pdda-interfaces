package uk.gov.hmcts.framework.services;

import jakarta.ejb.EJBHome;
import jakarta.ejb.EJBLocalHome;
import jakarta.transaction.UserTransaction;

import javax.naming.InitialContext;
import javax.sql.DataSource;

/**

 * Title: LocatorServices.


 * Description: Provides a base implementation of services which locate EJBs from the naming
 * conventions for binding these objects into the JNDI tree. A number of additonal services are alss
 * provided by the EJBServices interface. It is expected that this interface will primarily be used
 * by the BusinessDelegate to locate sessin beans


 * Copyright: Copyright (c) 2002


 * Company: EDS

 * @author Pete Raymond
 * @version 1.0
 */
public interface ServiceLocator {

    /**
     * getInitialContext.

     * @returns the InitialContext.
     */
    InitialContext getInitialContext();

    /**
     * getDataSource.

     * @returns the DataSource
     * @throws CsServicesException Exception
     */
    DataSource getDataSource();

    /**
     * getDataSource.

     * @returns the DataSource
     * @throws CsServicesException Exception
     */
    DataSource getDataSource(String name);

    /**
     * Gets an EJBLocalHome home object for the home associated with the class So for the Defendant
     * Entity Bean the DefendantRemote interface is passed in and the jhe JNDI name DefendantRemote
     * will be used for the lookkup. If DefendantController.class is passed in the JNDI name
     * DefendantContoller will be used for the lookup. The JNDI lookup will be performed only from
     * the configured subcontext for this framework application.

     * @param homeClass the Class to perform the lookup with.
     * @returns the EJBLocalHome for the found ejb
     * @throws a CSResourceUnavailableException if the ejb cannot be located
     */
    EJBLocalHome getLocalHome(Class<?> homeClass);

    /**
     * Gets an EJBHome home object for the home associated with the class So for the Defendant
     * Entity Bean the DefendantRemote interface is passed in and the jhe JNDI name DefendantRemote
     * will be used for the lookkup. If DefendantController.class is passed in the JNDI name
     * DefendantContoller will be used for the lookup. The JNDI lookup will be performed only from
     * the configured subcontext for this framework application.

     * @param homeClass the Class to perform the lookup with.
     * @returns the EJBHome for the found ejb
     * @throws a CSResourceUnavailableException if the ejb cannot be located
     */
    EJBHome getRemoteHome(Class<?> homeClass);

    /**
     * Gets a user transaction.

     * @return User Transaction
     */
    UserTransaction getUserTx();

}
