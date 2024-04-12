package uk.gov.hmcts.framework.services;

import uk.gov.hmcts.framework.services.audittrail.AuditTrailService;
import uk.gov.hmcts.framework.services.config.ConfigServicesImpl;
import uk.gov.hmcts.framework.services.errorhandling.DefaultErrorHandler;
import uk.gov.hmcts.framework.services.locator.ServiceLocatorImpl;
import uk.gov.hmcts.framework.services.xml.XmlServicesImpl;

/**
 * <p>
 * Title: CSServices.
 * </p>
 * <p>
 * Description: CSServices is the factory class through which all access to framework services is
 * obtained
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: EDS
 * </p>
 * 
 * @author Pete Raymond
 * @version $Id: CSServices.java,v 1.28 2009/05/27 08:58:01 powellja Exp $
 *          <p>
 *          Paul Grove added user session methods 11/11/02
 *          </p>
 */
public final class CsServices {

    /**
     * Private constructor.
     */
    private CsServices() {
    }

    /**
     * Gets the service locator for unauthenticated access to the default server.
     * 
     * @return ServiceLocator
     * @throws CSResourceUnavailableException Exception
     */
    public static ServiceLocator getServiceLocator() {
        return ServiceLocatorImpl.getInstance();
    }

    /**
     * Gets the config services.
     * 
     * @return ConfigServices
     */
    public static ConfigServices getConfigServices() {
        return ConfigServicesImpl.getInstance();
    }

    /**
     * Gets the default error handler.
     * 
     * @return ErrorHandler
     */
    public static ErrorHandler getDefaultErrorHandler() {
        return DefaultErrorHandler.getInstance();
    }

    /**
     * Get an instance of XMLServices.
     * 
     * @return XMLServices
     */
    public static XmlServices getXmlServices() {
        return XmlServicesImpl.getInstance();
    }

    /**
     * Get an instance of XSLServices.
     * 
     * @return XSLServices singleton
     */
    public static XslServices getXslServices() {
        return XslServices.getInstance();
    }

    /**
     * Get an instance of the locale services.
     * 
     * @return LocaleServices the LocaleServices singleton
     */
    public static LocaleServices getLocaleServices() {
        return LocaleServices.getInstance();
    }

    /**
     * Discover the concreten instance of the service to use.
     * 
     * @return DiscoveryServices the discovered service
     */
    public static DiscoveryServices getDiscoveryServices() {
        return DiscoveryServices.getInstance();
    }

    public static AuditTrailService getAuditTrailService() {
        return AuditTrailService.getInstance();
    }
}
