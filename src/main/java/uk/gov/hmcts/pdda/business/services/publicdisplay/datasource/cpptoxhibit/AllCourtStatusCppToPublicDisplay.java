package uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.cpptoxhibit;

import jakarta.persistence.EntityManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.services.cppformatting.CppFormattingHelper;
import uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.query.EventXmlNodeHelper;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.AllCourtStatusValue;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.DefendantName;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.nodes.BranchEventXmlNode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

public class AllCourtStatusCppToPublicDisplay extends AbstractCppToPublicDisplay {

    public AllCourtStatusCppToPublicDisplay(Date date, int courtId, int... courtRoomIds) {
        super(date, courtId, courtRoomIds);
    }

    // Use only in unit test
    public AllCourtStatusCppToPublicDisplay(Date date, int courtId, int[] courtRoomIds,
        XhbCourtRepository xhbCourtRepository, XhbCourtSiteRepository xhbCourtSiteRepository,
        XhbClobRepository xhbClobRepository, CppFormattingHelper cppFormattingHelper) {
        super(date, courtId, courtRoomIds, xhbCourtRepository, xhbCourtSiteRepository, xhbClobRepository,
            cppFormattingHelper);
    }

    /**
     * Returns a collection of AllCourtStatusValue CPP Data.
     * 
     * @throws XPathExpressionException Exception
     */
    @Override
    public Collection<?> getCppData(final EntityManager entityManager) {
        LOG.debug("getCppData({})", entityManager);
        List<AllCourtStatusValue> cppData = new ArrayList<>();

        // Check the court is CPP enabled and then retrieve data
        if (isCourtCppEnabled(entityManager)) {

            // Retrieve XML Document of CPP data from the latest XHB_CPP_FORMATTING row for the
            // court
            // supplied
            Document doc = getCppClobAsDocument(entityManager);
            if (doc != null) {
                for (int roomId : courtRoomIds) {
                    // Loop through all the Court Room Ids supplied and create a new
                    // AllCourtStatusValue for each
                    cppData.addAll(getCourtRoomData(doc, roomId));
                }
            }
        }

        return cppData;
    }

    private List<AllCourtStatusValue> getCourtRoomData(Document doc, int roomId) {
        List<AllCourtStatusValue> cppData = new ArrayList<>();
        XhbCourtRoomDao courtRoomValue;
        XhbCourtSiteDao courtSiteValue;
        try {

            // Using court room id, get the XhbCourtRoomDAO so we can lookup the CPP
            // <courtroom>
            // node using Court Room Name
            courtRoomValue = getCourtRoomObjectById(roomId);
            if (null != courtRoomValue) {
                courtSiteValue = getCourtSiteObjectById(courtRoomValue.getCourtSiteId());

                // Search for matching nodes in the Document based upon the court room
                // name
                NodeList nodes = getCourtSiteNodeList(courtRoomValue, courtSiteValue, doc);
                if (nodes.getLength() > 0 && null != nodes.item(0)) {
                    // Using the <courtroom> node from the XML Document, use the
                    // information inside to
                    // populate the AllCourtStatusValue
                    // Retrieve the active case node and populate the
                    // AllCourtStatusValue with the case
                    // data
                    NodeList caseNodes = (NodeList) getXPath().evaluate("cases/caseDetails[activecase=1]",
                        nodes.item(0), XPathConstants.NODESET);
                    cppData.addAll(getCaseData(caseNodes, courtRoomValue, courtSiteValue));
                }
            }

        } catch (XPathExpressionException e) {
            LOG.error("AllCourtStatusCppToPublicDisplay.getCppData() - XPathExpressionException  - {}", e.toString());
        } catch (Exception e) {
            LOG.error("AllCourtStatusCppToPublicDisplay.getCppData() - Exception  - {}", e.toString());
        }
        return cppData;
    }

    private List<AllCourtStatusValue> getCaseData(NodeList caseNodes, XhbCourtRoomDao courtRoomValue,
        XhbCourtSiteDao courtSiteValue) throws XPathExpressionException {
        List<AllCourtStatusValue> cppData = new ArrayList<>();
        AllCourtStatusValue allCourtStatusValue;
        if (caseNodes.getLength() != 0 && null != caseNodes.item(0)) {
            Node caseNode = caseNodes.item(0);
            allCourtStatusValue = getAllCourtStatusValue();

            // Populate fields Specific to PublicDisplayValue
            populateCourtSiteRoomData(allCourtStatusValue, courtRoomValue, courtSiteValue);

            // Populate the rest of the data
            populateData(allCourtStatusValue, (Element) caseNode);

            // Add the populated AllCourtStatusValue to cppData
            cppData.add(allCourtStatusValue);
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("AllCourtStatusCppToPublicDisplay.getCppData() - " + "no active case data for court room: "
                    + courtRoomValue.getCourtRoomName());
            }
        }
        return cppData;
    }

    private AllCourtStatusValue getAllCourtStatusValue() {
        return new AllCourtStatusValue();
    }

    /**
     * Populates the AllCourtStatusValue object with data from the courtroom XML element.
     * 
     * @param value AllCourtStatusValue
     * @param caseNode XML element for the case
     * @throws XPathExpressionException Exception
     */
    protected void populateData(AllCourtStatusValue value, Element caseNode) throws XPathExpressionException {
        // Set Case Number
        String cppurn = getXPath().evaluate(XPATH_CPPURN, caseNode);
        String caseNumber = getXPath().evaluate(XPATH_CASENUMBER, caseNode);
        String caseType = getXPath().evaluate(XPATH_CASETYPE, caseNode);

        if (EMPTY_STRING.equals(cppurn)) {
            // No CPP URN so check the Xhibit case number and case type fields instead
            if (!EMPTY_STRING.equals(caseNumber) && !EMPTY_STRING.equals(caseType)) {
                value.setCaseNumber(caseType + caseNumber);
            }
        } else {
            // Use the CPP URN as the case number
            value.setCaseNumber(cppurn);
        }

        // Set the Event Node
        value.setEvent((BranchEventXmlNode) EventXmlNodeHelper
            .buildEventNode((Node) getXPath().evaluate(XPATH_EVENT, caseNode, XPathConstants.NODE)));

        // Set the event date/time
        String eventDate = getXPath().evaluate(XPATH_EVENTDATE, caseNode);
        String eventTime = getXPath().evaluate(XPATH_EVENTTIME, caseNode);
        String eventDateTime;
        if (EMPTY_STRING.equals(eventDate) || EMPTY_STRING.equals(eventTime)) {
            // No event date/time so use the timestatusset node
            String timeStatusSet = getXPath().evaluate(XPATH_TIMESTATUSSET, caseNode);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
            String dateString = dateFormat.format(this.date);
            // Combine the time with the date to complete the timestamp otherwise uses 1970
            eventDateTime = dateString + " " + timeStatusSet;
        } else {
            eventDateTime = eventDate + " " + eventTime;
        }
        value.setEventTime(convertStringToTimestamp(eventDateTime));

        // Loop through all Defendants and add them
        NodeList defendantList = getDefendantNodeList(caseNode);
        boolean reportingRestricted = false;
        for (int i = 0; i < defendantList.getLength(); i++) {
            boolean defReportingRestricted = getDefendantReportRestriction((Element) defendantList.item(i));
            if (!reportingRestricted && defReportingRestricted) {
                reportingRestricted = true;
            }
            value.addDefendantName(getDefendantName(getXPath().evaluate(XPATH_DEF_FIRSTNAME, defendantList.item(i)),
                getXPath().evaluate(XPATH_DEF_MIDDLENAME, defendantList.item(i)),
                getXPath().evaluate(XPATH_DEF_SURNAME, defendantList.item(i))));
        }
        // If any of the defendants have reportingrestricted set to 1, then set reporting restricted
        // to
        // true
        value.setReportingRestricted(reportingRestricted);
    }

    private DefendantName getDefendantName(String firstName, String middleName, String surname) {
        return new DefendantName(firstName, middleName, surname, false);
    }
}
