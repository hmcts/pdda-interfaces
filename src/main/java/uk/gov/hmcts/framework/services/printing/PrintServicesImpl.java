package uk.gov.hmcts.framework.services.printing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import uk.gov.hmcts.framework.business.vos.CsValueObject;
import uk.gov.hmcts.framework.exception.CsConfigurationException;
import uk.gov.hmcts.framework.exception.CsUnrecoverableException;
import uk.gov.hmcts.framework.services.CsServices;
import uk.gov.hmcts.framework.services.PrintServices;
import uk.gov.hmcts.framework.services.xml.CsXmlServicesException;

import java.util.Locale;
import java.util.Properties;

/**
 * <p>
 * Title: PrintServicesImpl.
 * </p>
 * <p>
 * Description: Implementation of the print service
 * </p>
 * 
 * <p>This is a replacement for the PrintController
 * 
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: EDS
 * </p>
 * 
 * @author Will Fardell (Xdevelopment 2003)
 * @version 1.0
 */

public final class PrintServicesImpl implements PrintServices {
    /**
     * Print Service properties used to map class names to xsl transforms.
     */
    private static final String PRINT_SERVICE_PROPERTIES = "printxslschemas";

    /**
     * Log4j logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(PrintServicesImpl.class);

    /**
     * Singleton Instance.
     */
    private static final PrintServicesImpl INSTANCE = new PrintServicesImpl();

    /**
     * Get the singleton.
     * 
     * @return the singleton instance
     */
    public static PrintServicesImpl getInstance() {
        return INSTANCE;
    }

    /**
     * Guard against external creation of this class.
     */
    private PrintServicesImpl() {

    }

    /**
     * Converts an object implementing the <code>CSValueObject</code> interface (subclasses
     * <code>CSAbstractValue</code>) into an xml document (saved to D:\temp.xml if debug is enabled
     * ) and transforms the resulting xml according to the associated xsl file. The corresponding
     * xsl should be specified in the \config\components\printxslschemas.properties file. The key in
     * printxslschemas.properties should be the fully qualified class name of the value object. The
     * value in printxslschemas.properties is the filename of the xsl file (without the .xsl
     * extention).
     * 
     * @param data contains the data required to generate the document. To work effectively the
     *        CSValueObject requires a default public constructor and public get methods for all vos
     *        that are required in the resulting xml document.
     * 
     * @return String: transformed document of value object
     * @throws PrintServiceException if an error occurs
     */
    @Override
    public String getFormattedDocument(CsValueObject data, Locale locale) {
        return formatDocument(getXmlDocument(data), getXslTransformName(data), locale);
    }

    /**
     * Performs a similar function to 'getFormattedDocument'. Instead of looking up the
     * corresponding xsl file for a Document, it is specified by the caller.
     * 
     * @param xml the document to be transformed
     * @param xslName XSL to use for transform
     * @return transformed document, as a string
     */
    @Override
    public String getFormattedDoc(Document xml, String xslName, Locale locale) {
        return formatDocument(xml, xslName, locale);
    }

    /**
     * Format the Document.
     * 
     * @param document the document to format
     * @param xslName the name of the transform to use
     * @param locale Locale
     * @return return the formated document
     */
    private static String formatDocument(Document document, String xslName, Locale locale) {
        try {
            LOG.debug("formatDocument({},{},{})", document, xslName, locale);
            return CsServices.getXslServices().transform(document, xslName, locale, null);
        } catch (CsUnrecoverableException e) {
            CsServices.getDefaultErrorHandler().handleError(e, PrintServicesImpl.class);
            throw new PrintServiceException(e);
        }
    }

    /**
     * Create an XML document from the data.
     * 
     * @param data the data to generate the document from
     */
    private static Document getXmlDocument(CsValueObject data)  {
        try {
            LOG.debug("getXmlDocument({})", data);
            return CsServices.getXmlServices().createDocFromValue(data);
        } catch (CsXmlServicesException e) {
            CsServices.getDefaultErrorHandler().handleError(e, PrintServicesImpl.class);
            throw new PrintServiceException(e);
        }
    }

    /**
     * Get the name of the xsl transform to use for the data.
     * 
     * @param data the object to look up the transform form
     * @return the (resource) name of the transform
     * @throws CsConfigurationException if an error occures
     */
    private static String getXslTransformName(CsValueObject data) {
        return getXslTransformName(data.getClass().getName().toLowerCase(Locale.getDefault()));
    }

    /**
     * Get the name of the xsl transform to use for the data.
     * 
     * @param dataClassName String
     * @return the (resource) name of the transform
     * @throws CsConfigurationException if an error occures
     */
    private static String getXslTransformName(String dataClassName) {
        String xslNameProperty = getProperties().getProperty(dataClassName);
        if (xslNameProperty != null) {
            String xslName = xslNameProperty.trim() + ".xsl"; // Add .xsl
            // extension to
            // returned name
            if (LOG.isDebugEnabled()) {
                LOG.debug("Resolved " + dataClassName + " to transform " + xslName);
            }
            return xslName;
        } else {
            throw new CsConfigurationException("Could not find xsl transform for " + dataClassName
                + " in " + PRINT_SERVICE_PROPERTIES);
        }
    }

    /**
     * Get the PrintServiceProperties (class xsl map).
     * 
     * @return the properties collection
     * @throws CsConfigurationException if an error occures
     */
    private static Properties getProperties() {
        return CsServices.getConfigServices().getProperties(PRINT_SERVICE_PROPERTIES);
    }

}
