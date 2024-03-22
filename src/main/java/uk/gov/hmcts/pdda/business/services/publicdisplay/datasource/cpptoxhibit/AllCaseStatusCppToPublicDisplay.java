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
import uk.gov.hmcts.pdda.business.services.cppformatting.CppFormattingHelper;
import uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.query.EventXmlNodeHelper;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.AllCaseStatusValue;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.nodes.BranchEventXmlNode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

public class AllCaseStatusCppToPublicDisplay extends SummaryByNameCppToPublicDisplay {

    public AllCaseStatusCppToPublicDisplay(Date date, int courtId, int... courtRoomIds) {
        super(date, courtId, courtRoomIds);
    }

    // Use only in unit test
    public AllCaseStatusCppToPublicDisplay(Date date, int courtId, int[] courtRoomIds,
        XhbCourtRepository xhbCourtRepository, XhbClobRepository xhbClobRepository,
        CppFormattingHelper cppFormattingHelper) {
        super(date, courtId, courtRoomIds, xhbCourtRepository, xhbClobRepository, cppFormattingHelper);
    }

    @Override
    public Collection<?> getCppData(final EntityManager entityManager) {
        List<AllCaseStatusValue> cppData = new ArrayList<>();

        // Check the court is CPP enabled and then retrieve data
        if (isCourtCppEnabled(entityManager)) {

            // Retrieve XML Document of CPP data from the latest XHB_CPP_FORMATTING row for the
            // court
            // supplied
            Document doc = getCppClobAsDocument(entityManager);
            if (doc != null) {
                cppData.addAll(getCaseStatusCourtRoomData(doc));
                cppData.addAll(getCaseStatusCourtSiteData(doc));
            }
        }

        return cppData;
    }

    private List<AllCaseStatusValue> getCaseStatusCourtRoomData(Document doc) {
        LOG.debug("getCaseStatusCourtRoomData({})", doc);
        List<AllCaseStatusValue> cppData = new ArrayList<>();
        XhbCourtRoomDao courtRoomValue;
        XhbCourtSiteDao courtSiteValue;
        for (int roomId : courtRoomIds) {
            // Loop through all the Court Room Ids supplied and create a new
            // AllCaseStatusValue for
            // each
            // Using court room id, get the XhbCourtRoomDAO so we can lookup the CPP
            // <courtroom> node
            // using Court Room Name
            try {
                courtRoomValue = getCourtRoomObjectById(roomId);
                if (null != courtRoomValue) {
                    courtSiteValue = getCourtSiteObjectById(courtRoomValue.getCourtSiteId());

                    // Search for matching nodes in the Document based upon the court room
                    // name
                    NodeList nodes = getCourtSiteNodeList(courtRoomValue, courtSiteValue, doc);
                    if (nodes.getLength() > 0 && null != nodes.item(0)) {

                        // Return all the cases present in that court room
                        cppData.addAll(getCourtRoomCaseData(nodes, courtRoomValue, courtSiteValue));
                    }
                }
            } catch (XPathExpressionException e) {
                LOG.error("AllCaseStatusCppToPublicDisplay.getCppData() - XPathExpressionException  - {}",
                    e.toString());
            } catch (Exception e) {
                LOG.error("AllCaseStatusCppToPublicDisplay.getCppData() - Exception  - {}", e.toString());
            }
        }
        return cppData;
    }

    private List<AllCaseStatusValue> getCourtRoomCaseData(NodeList nodes, XhbCourtRoomDao courtRoomValue,
        XhbCourtSiteDao courtSiteValue) throws XPathExpressionException {
        List<AllCaseStatusValue> cppData = new ArrayList<>();
        NodeList caseNodes = (NodeList) getXPath().evaluate("cases/caseDetails", nodes.item(0), XPathConstants.NODESET);
        if (caseNodes.getLength() > 0 && null != caseNodes.item(0)) {

            for (int ci = 0; ci < caseNodes.getLength(); ci++) {
                // For each case, extract all the defendants for that case node
                cppData.addAll(getDefendantData(caseNodes, ci, courtRoomValue, courtSiteValue));
            }
        }
        return cppData;
    }

    private List<AllCaseStatusValue> getCaseStatusCourtSiteData(Document doc) {
        LOG.debug("getCaseStatusCourtSiteData({})", doc);
        List<AllCaseStatusValue> cppData = new ArrayList<>();
        XhbCourtSiteDao courtSiteValue;
        try {
            NodeList courtSiteNodes =
                (NodeList) getXPath().evaluate("//courtsites/courtsite", doc, XPathConstants.NODESET);
            if (courtSiteNodes.getLength() > 0 && null != courtSiteNodes.item(0)) {
                // Loop through all the court sites looking for floating cases at each site
                for (int csi = 0; csi < courtSiteNodes.getLength(); csi++) {

                    // Floating cases
                    NodeList floatingCaseNodes = getFloatingCaseNodeList(courtSiteNodes.item(csi));
                    if (isFloatingCases(floatingCaseNodes)) {
                        courtSiteValue = getCourtSiteObjectByName(
                            getXPath().evaluate(XPATH_COURTSITENAME, courtSiteNodes.item(csi)));
                        for (int fci = 0; fci < floatingCaseNodes.getLength(); fci++) {
                            // For each case, extract all the defendants for that case node
                            cppData.addAll(getDefendantData(floatingCaseNodes, fci, null, courtSiteValue));
                        }
                    }
                }
            }
        } catch (XPathExpressionException e) {
            LOG.error(
                "AllCaseStatusCppToPublicDisplay.getCppData() for floating cases - XPathExpressionException  - {}",
                e.toString());
        } catch (Exception e) {
            LOG.error("AllCaseStatusCppToPublicDisplay.getCppData() for floating cases - Exception  - {}",
                e.toString());
        }
        return cppData;
    }

    private List<AllCaseStatusValue> getDefendantData(NodeList nodeList, int rowNo, XhbCourtRoomDao courtRoomValue,
        XhbCourtSiteDao courtSiteValue) throws XPathExpressionException {
        LOG.debug("getDefendantData({},{},{},{})", nodeList, rowNo, courtRoomValue, courtSiteValue);
        List<AllCaseStatusValue> cppData = new ArrayList<>();
        AllCaseStatusValue allCaseStatusValue;
        NodeList defendantNodes = getDefendantNodeList(nodeList.item(rowNo));
        if (defendantNodes.getLength() > 0 && null != defendantNodes.item(0)) {
            for (int di = 0; di < defendantNodes.getLength(); di++) {
                // Create a new AllCaseStatusValue for each Defendant
                allCaseStatusValue = getAllCaseStatusValue();

                // Populate fields Specific to PublicDisplayValue
                populateCourtSiteRoomData(allCaseStatusValue, courtRoomValue, courtSiteValue);

                // Populate the AllCaseStatusValue specific fields
                populateData(allCaseStatusValue, (Element) defendantNodes.item(di), (Element) nodeList.item(rowNo));
                allCaseStatusValue.setFloating("1");

                // Add the populated AllCaseStatusValue to cppData
                cppData.add(allCaseStatusValue);
            }
        }
        return cppData;
    }

    private boolean isFloatingCases(NodeList floatingCaseNodes) {
        return floatingCaseNodes.getLength() > 0 && null != floatingCaseNodes.item(0);
    }

    private AllCaseStatusValue getAllCaseStatusValue() {
        return new AllCaseStatusValue();
    }

    /**
     * Populates the AllCaseStatusValue object with data from the courtroom XML element.
     * 
     * @param value AllCaseStatusValue
     * @param defNode XML element for the defendant
     * @throws XPathExpressionException Exception
     */
    protected void populateData(AllCaseStatusValue value, Element defNode, Element caseNode)
        throws XPathExpressionException {
        // Populate the SummaryByNameValue specific fields
        super.populateData(value, defNode, caseNode);

        // Set Case Number
        String cppurn = getXPath().evaluate(XPATH_CPPURN, caseNode);
        String caseNumber = getXPath().evaluate(XPATH_CASENUMBER, caseNode);
        String caseType = getXPath().evaluate(XPATH_CASETYPE, caseNode);

        if (EMPTY_STRING.equals(cppurn)) {
            if (!EMPTY_STRING.equals(caseNumber) && !"".equals(caseType)) {
                value.setCaseNumber(caseType + caseNumber);
            }
        } else {
            value.setCaseNumber(cppurn);
        }

        // Set the Event Node
        value.setEvent((BranchEventXmlNode) EventXmlNodeHelper
            .buildEventNode((Node) getXPath().evaluate(XPATH_EVENT, caseNode, XPathConstants.NODE)));

        // Set the event date/time
        String eventDate = getXPath().evaluate(XPATH_EVENTDATE, caseNode);
        String eventTime = getXPath().evaluate(XPATH_EVENTTIME, caseNode);
        String eventDateTime;
        if (EMPTY_STRING.equals(eventDate) && !EMPTY_STRING.equals(eventTime)) {
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

        String hearingDesc = getXPath().evaluate(XPATH_HEARINGTYPE, caseNode);
        value.setHearingDescription(hearingDesc);

        String hearingProgress = getXPath().evaluate(XPATH_HEARINGPROGRESS, caseNode);
        if (null != hearingProgress && !"".equals(hearingProgress)) {
            value.setHearingProgress(Integer.parseInt(hearingProgress));
        }

        // Set the List Court Room Id
        String listCourtRoomName = getXPath().evaluate(XPATH_LISTCOURTROOM, caseNode);
        XhbCourtRoomDao listCourtRoomValue = getCourtRoomObjectByName(listCourtRoomName);
        if (null != listCourtRoomValue) {
            value.setListCourtRoomId(listCourtRoomValue.getCourtRoomId());
        }
    }
}
