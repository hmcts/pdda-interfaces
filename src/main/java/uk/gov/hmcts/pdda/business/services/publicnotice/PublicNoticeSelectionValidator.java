package uk.gov.hmcts.pdda.business.services.publicnotice;

import jakarta.ejb.EJBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.courtservice.xhibit.business.vos.services.publicnotice.DisplayablePublicNoticeValue;
import uk.gov.hmcts.framework.exception.CsConfigurationException;
import uk.gov.hmcts.framework.services.CsServices;

import java.util.Properties;

/**

 * Title: Takes care of Validation rules for the Public Notices.


 * Description: Currently there is only one rule to do with the number of configured Displayable
 * public notices that can be selected to be at Activation Level "true" at once. But in the future
 * any other rules can be plugged in here.


 * Copyright: Copyright (c) 2003


 * Company: Electronic Data Systems

 * @author: Pat Fox.
 */

public final class PublicNoticeSelectionValidator {

    private static final Logger LOG = LoggerFactory.getLogger(PublicNoticeSelectionValidator.class);
    
    // Max number of Notices that can be active(displayed at anytime)
    static final String MAX_NUMBER_ALLOWED_ACTIVE = "MAX_NUMBER_ALLOWED_ACTIVE";
    // Properties file for general public notice configuration
    private static final String PROPERTIESFILENAME = "publicnotice";

    private static Properties configProperties;

    static {
        // loads the properties
        setPublicNoticeProperties();
    }

    /**
     * Constructor for the PublicNoticeWorkFlow object.
     */
    private PublicNoticeSelectionValidator() {
        // private constructor
    }

    /**
     * This method will check against a set of rules that specify which Public notices can be
     * selected at once. If the rule is broken then PublicNoticeInvalidSelectionException is thrown.

     * @param displayablePublicNoticeValues Description of the Parameter
     * @exception PublicNoticeInvalidSelectionException Description of the Exception
     */
    public static void validateSelection(
        DisplayablePublicNoticeValue... displayablePublicNoticeValues) {
        int maximumNoticesAllowedActive =
            Integer.parseInt(configProperties.getProperty(MAX_NUMBER_ALLOWED_ACTIVE));

        if (LOG.isDebugEnabled()) {
            LOG.debug("{}{}", "10 : Entering validateSelection  ", displayablePublicNoticeValues.length);
        }

        int numberSelectedAsAcive = 0;

        // walk through and see if number selected is greater the
        for (int i = displayablePublicNoticeValues.length - 1; i >= 0; i--) {

            if (displayablePublicNoticeValues[i].getIsActive()) {
                numberSelectedAsAcive++;
            }
        }

        // if number exceeded throw an exception.
        if (numberSelectedAsAcive > maximumNoticesAllowedActive) {

            PublicNoticeInvalidSelectionException publicNoticeInvalidSelectionException =
                new PublicNoticeInvalidSelectionException(
                    maximumNoticesAllowedActive,
                    "Too Many PublicNotices Selected" + numberSelectedAsAcive);

            CsServices.getDefaultErrorHandler().handleError(publicNoticeInvalidSelectionException,
                PublicNoticeSelectionValidator.class);
            throw publicNoticeInvalidSelectionException;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("{}{}", "50 : Exiting validateSelection number selected", numberSelectedAsAcive);
        }
    }

    /**
     * Gets the publicNoticeProperties attribute of the PublicNoticeSelectionValidator object.
     */
    private static void setPublicNoticeProperties() {

        if (LOG.isDebugEnabled()) {
            LOG.debug("Entering getPublicNoticeProperties() ");
        }

        try {
            configProperties = CsServices.getConfigServices().getProperties(PROPERTIESFILENAME);
        } catch (CsConfigurationException e) {
            CsServices.getDefaultErrorHandler().handleError(e, PublicNoticeSelectionValidator.class,
                "Cannot find properties file: " + PROPERTIESFILENAME);
            throw new EJBException("Xhibit_Messaging.Unable_To_Locate_Properties" + e.getMessage(),
                e);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Exiting getPublicNoticeProperties() ");
        }
    }

}
