package uk.gov.hmcts.pdda.business.services.publicnotice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.framework.services.XmlServices;
import uk.gov.hmcts.framework.services.xml.XmlServicesImpl;
import uk.gov.hmcts.pdda.business.vos.services.publicnotice.DefinitivePublicNoticeStatusValue;
import uk.gov.hmcts.pdda.courtlog.vos.CourtLogSubscriptionValue;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**

 * Title: Based on CourtLogSubscription Event Type it's the responsiblity of this class to
 * Manipulate the PublicNotices if Required.

 * Description:This class will manipulate the selection so that the notices being displayed are
 * being correct .


 * Copyright: Copyright (c) 2003


 * Company: Electronic Data Systems

 * @author Pat Fox
 * @created 17 February 2003
 */
public final class PublicNoticeSelectionManipulator {

    private static final Logger LOG =
        LoggerFactory.getLogger(PublicNoticeSelectionManipulator.class);
    
    private static final String LOG_BRACKETS = "{}{}{}";

    private static PublicNoticeXmlHelper publicNoticeXmlHelper =
        PublicNoticeXmlHelper.getInstance();

    // Constants for Prosecution Case Court Log event - Bug 53354
    private static final int EVENT_ID_TRIAL_PROSECUTION_CASE = 20_903;

    private static final int EVENT_ID_TRIAL_PROSECUTION_CASE_TV_LINK = 2_090_301;

    private static final int EVENT_ID_TRIAL_PROSECUTION_CASE_VIDEO_LINK = 2_090_302;

    private static final String XPATH_EVENT_ID_20903 =
        "event/E20903_Prosecution_Case_Options/E20903_PCO_Type/text()";

    private static final String XPATH_EVENT_ID_20903_OPTION_TV_LINK =
        "E20903_Prosecution_Case_TV_Link_in_Progress";

    private static final String XPATH_EVENT_ID_20903_OPTION_VIDEO_LINK =
        "E20903_Prosecution_Case_Video_Being_Played";

    // End of constants for Prosecution Case Court Log event - Bug 53354

    // Constants for Respondent Case Court Log event (Appeal) - Bug 54926 &
    // 54987
    private static final int EVENT_ID_APPEAL_RESPONDENT_CASE = 20_602;

    private static final int EVENT_ID_APPEAL_RESPONDENT_CASE_TV_LINK = 2_060_201;

    private static final int EVENT_ID_APPEAL_RESPONDENT_CASE_VIDEO_LINK = 2_060_202;

    private static final String XPATH_EVENT_ID_20602 =
        "event/E20602_Respondent_Case_Opened/E20602_RCO_Type/text()";

    private static final String XPATH_EVENT_ID_20602_OPTION_TV_LINK = "E20602_TV_Link_In_Progress";

    private static final String XPATH_EVENT_ID_20602_OPTION_VIDEO_LINK =
        "E20602_Video_Being_Played";

    private static final String XPATH_EVENT_ID_20602_OPTION_NONE = "E20602_Respondent_Case_Opened";

    // End of constants for Respondent Case Court Log event (Appeal) - Bug
    // 54926
    // & 54987

    /**
     * Constructor for the PublicNoticeWorkFlow object.
     */
    private PublicNoticeSelectionManipulator() {
        // private constructor
    }

    /**
     * This method manipulates the Configured Public notices Activation Status based on the array of
     * Displayable Public Notices passed in. The Manipulation actions are configured through the XML
     * file.

     * @param courtLogSubscriptionValue Description of the Parameter
     * @return Description of the Return Value
     * @throws PublicNoticeException Description of the Exception
     */
    public static boolean manipulateSelection(CourtLogSubscriptionValue courtLogSubscriptionValue) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("{}{}",
                "10 : Entering manipulateSelection courtLogSubscriptionValue.getCourtRoomId() ",
                    courtLogSubscriptionValue.getCourtRoomId());
        }

        // get the court court room Id
        Integer courtRoomId = courtLogSubscriptionValue.getCourtRoomId();

        // get the actual event type id
        Integer eventType = getCourtLogEventType(courtLogSubscriptionValue);

        // get which definitive notice to update
        if (LOG.isDebugEnabled()) {
            LOG.debug("{}{}{}{}", " 20 : l_xhbCourtRoomId = ", courtRoomId, " l_xhbEventType =", eventType);
        }

        boolean isUpdateCarriedOut = manipulateSelection(courtRoomId, eventType);

        LOG.debug("30 : Exiting manipulateSelection");

        return isUpdateCarriedOut;
    }

    /**
     * This gets the Manipulator map from the XMLHelper . This Hash contains lists of { definitive
     * notice ID, Activation Status } that is keyed on the specified courtlog event. If Courtlog
     * Event Type is not contained in the keys of the hashmap then no manipulation is carried out.
     * If it the Court Log Event Type is within the keys then the manipulation is carried out based
     * on the list of statusChange objects returned for that key.

     * @param courtRoomId Description of the Parameter
     * @param eventType Description of the Parameter
     * @return Description of the Return Value
     * @throws PublicNoticeException Description of the Exception
     */
    private static boolean manipulateSelection(Integer courtRoomId, Integer eventType) {

        LOG.debug("{}{}", "10 : Entering manipulateSelection l_xhbCourtRoomId ", courtRoomId);

        // get the HashMap that contains all the status changes for the
        // courtlogEventType

        Map<Integer, List<DefinitivePublicNoticeStatusValue>> theManipulatorMap =
            publicNoticeXmlHelper.getManipulatorMap();

        // If there is not list for eventType don't do anything
        if (theManipulatorMap.containsKey(eventType)) {

            // Retrieve the list of status changes and carry each one out.
            List<?> theListOfStatusChanges = theManipulatorMap.get(eventType);
            Iterator<?> theListOfStatusChangesIterator = theListOfStatusChanges.iterator();

            if (LOG.isDebugEnabled()) {
                LOG.debug("11 : Number of Status changes :{}", theListOfStatusChanges.size());
            }

            while (theListOfStatusChangesIterator.hasNext()) {

                DefinitivePublicNoticeStatusValue statusChangeValue =
                    (DefinitivePublicNoticeStatusValue) theListOfStatusChangesIterator.next();

                LOG.debug("15 : Changing status of  DefPN :{} to {}",
                    statusChangeValue.getDefinitivePublicNoticeId(),
                    statusChangeValue.isActive());

                updateActivationStatusOnConfiguredPublicNotice(courtRoomId, statusChangeValue);

            }

            LOG.debug("39 : Exiting manipulateSelection with update");

            // updated
            return true;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("40 : Exiting manipulateSelection without update");
        }
        // did not need to update
        return false;
    }

    private static void updateActivationStatusOnConfiguredPublicNotice(Integer courtRoomId,
        DefinitivePublicNoticeStatusValue statusChangeValue) {
        PublicNoticeMaintainer.updateActiveStatus(courtRoomId, statusChangeValue, false);
    }

    /*
     * This method allows any events that have a number of possible option types within the events
     * XML entry to be determined and a unique event id assigned for the purposes of showing the
     * correct public notices.
     */
    private static Integer getCourtLogEventType(CourtLogSubscriptionValue clsValue) {
        String methodName = "getCourtLogEventType - ";
        LOG.debug("{}{}", methodName, "entry");

        int eventType = clsValue.getCourtLogViewValue().getEventType();
        LOG.debug(LOG_BRACKETS, methodName, "eventType: ", eventType);

        // Manipulate any events that have more than one possible option type
        if (eventType == EVENT_ID_TRIAL_PROSECUTION_CASE) {

            XmlServices xmlServices = XmlServicesImpl.getInstance();

            // Obtain the option type using the XPath functionality in the
            // framework
            String optionType = xmlServices.getXpathValueFromXmlString(
                clsValue.getCourtLogViewValue().getLogEntry(), XPATH_EVENT_ID_20903);
            LOG.debug(LOG_BRACKETS, methodName, "optionType: ", optionType);

            if (XPATH_EVENT_ID_20903_OPTION_VIDEO_LINK.equals(optionType)) {
                eventType = EVENT_ID_TRIAL_PROSECUTION_CASE_VIDEO_LINK;
            } else if (XPATH_EVENT_ID_20903_OPTION_TV_LINK.equals(optionType)) {
                eventType = EVENT_ID_TRIAL_PROSECUTION_CASE_TV_LINK;
            } else {
                LOG.warn(LOG_BRACKETS, methodName, " Unexpected option type returned : ", optionType);
            }
        }
        // Bug 54926 & 54987
        if (eventType == EVENT_ID_APPEAL_RESPONDENT_CASE) {

            XmlServices xmlServices = XmlServicesImpl.getInstance();

            // Obtain the option type using the XPath functionality in the
            // framework
            String optionType = xmlServices.getXpathValueFromXmlString(
                clsValue.getCourtLogViewValue().getLogEntry(), XPATH_EVENT_ID_20602);
            LOG.debug(LOG_BRACKETS, methodName, "optionType: ", optionType);

            if (XPATH_EVENT_ID_20602_OPTION_VIDEO_LINK.equals(optionType)) {
                eventType = EVENT_ID_APPEAL_RESPONDENT_CASE_VIDEO_LINK;
            } else if (XPATH_EVENT_ID_20602_OPTION_TV_LINK.equals(optionType)) {
                eventType = EVENT_ID_APPEAL_RESPONDENT_CASE_TV_LINK;
            } else if (XPATH_EVENT_ID_20602_OPTION_NONE.equals(optionType)) {
                LOG.debug("No public notice is required in this case");
            } else {
                LOG.warn(LOG_BRACKETS, methodName, " Unexpected option type returned : ", optionType);
            }
        }

        LOG.debug(LOG_BRACKETS, methodName, "exit - eventType: ", eventType);
        return eventType;
    }
}
