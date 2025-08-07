package uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.cpptoxhibit;

import jakarta.persistence.EntityManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.services.cppformatting.CppFormattingHelper;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.DefendantName;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.SummaryByNameValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

public class SummaryByNameCppToPublicDisplay extends AbstractCppToPublicDisplay {

    public SummaryByNameCppToPublicDisplay(Date date, int courtId, int... courtRoomIds) {
        super(date, courtId, courtRoomIds);
    }

    // Use only in unit test
    public SummaryByNameCppToPublicDisplay(Date date, int courtId, int[] courtRoomIds,
        XhbCourtRepository xhbCourtRepository, XhbCourtSiteRepository xhbCourtSiteRepository,
        XhbCourtRoomRepository xhbCourtRoomRepository, XhbClobRepository xhbClobRepository,
        CppFormattingHelper cppFormattingHelper) {
        super(date, courtId, courtRoomIds, xhbCourtRepository, xhbCourtSiteRepository, xhbCourtRoomRepository,
            xhbClobRepository, cppFormattingHelper);
    }

    @Override
    public Collection<?> getCppData(final EntityManager entityManager) {
        LOG.debug("getCppData({})", entityManager);
        List<SummaryByNameValue> cppData = new ArrayList<>();

        // Check the court is CPP enabled and then retrieve data
        if (isCourtCppEnabled(entityManager)) {

            // Retrieve XML Document of CPP data from the latest XHB_CPP_FORMATTING row for the
            // court
            // supplied
            Document doc = getCppClobAsDocument(entityManager);
            if (doc != null) {
                cppData.addAll(getCourtRoomData(doc));
                cppData.addAll(getCourtSiteData(doc));
            }
        }

        return cppData;
    }

    private List<SummaryByNameValue> getCourtRoomData(Document doc) {
        List<SummaryByNameValue> cppData = new ArrayList<>();
        XhbCourtRoomDao courtRoomValue;
        XhbCourtSiteDao courtSiteValue;

        for (int roomId : courtRoomIds) {
            // Loop through all the Court Room Ids supplied and create a new
            // SummaryByNameValue for each
            // Using court room id, get the XhbCourtRoomDAO so we can lookup the CPP
            // <courtroom> node using Court Room Name
            try {
                courtRoomValue = getCourtRoomObjectById(roomId);
                if (null != courtRoomValue) {
                    courtSiteValue = getCourtSiteObjectById(courtRoomValue.getCourtSiteId());

                    // Search for matching nodes in the Document based upon the court room name
                    NodeList nodes = getCourtSiteNodeList(courtRoomValue, courtSiteValue, doc);
                    if (nodes.getLength() > 0 && null != nodes.item(0)) {

                        // Return all the cases present in that court room
                        NodeList caseNodes = getCasesNodeList(nodes.item(0));
                        cppData.addAll(getCaseData(caseNodes, courtRoomValue, courtSiteValue));
                    }
                }
            } catch (XPathExpressionException e) {
                LOG.error("SummaryByNameCppToPublicDisplay.getCppData() - XPathExpressionException  - " + e.toString());
            } catch (Exception e) {
                LOG.error("SummaryByNameCppToPublicDisplay.getCppData() - Exception  - " + e.toString());
            }
        }
        return cppData;
    }

    private List<SummaryByNameValue> getCaseData(NodeList caseNodes, XhbCourtRoomDao courtRoomValue,
        XhbCourtSiteDao courtSiteValue) throws XPathExpressionException {
        List<SummaryByNameValue> cppData = new ArrayList<>();
        if (caseNodes.getLength() > 0 && null != caseNodes.item(0)) {

            for (int ci = 0; ci < caseNodes.getLength(); ci++) {
                // For each case, extract all the defendants for that case node
                NodeList defendantNodes = getDefendantNodeList(caseNodes.item(ci));
                cppData.addAll(getDefendantData(caseNodes, defendantNodes, courtRoomValue, courtSiteValue, ci, false));
            }
        }
        return cppData;
    }

    private List<SummaryByNameValue> getCourtSiteData(Document doc) {
        List<SummaryByNameValue> cppData = new ArrayList<>();
        XhbCourtSiteDao courtSiteValue;
        try {
            NodeList courtSiteNodes =
                (NodeList) getXPath().evaluate("//courtsites/courtsite", doc, XPathConstants.NODESET);
            if (courtSiteNodes.getLength() > 0 && null != courtSiteNodes.item(0)) {
                // Loop through all the court sites looking for floating cases at each site
                for (int csi = 0; csi < courtSiteNodes.getLength(); csi++) {

                    // Floating cases
                    NodeList floatingCaseNodes = getFloatingCaseNodeList(courtSiteNodes.item(csi));
                    if (floatingCaseNodes.getLength() > 0 && null != floatingCaseNodes.item(0)) {
                        courtSiteValue = getCourtSiteObjectByName(
                            getXPath().evaluate(XPATH_COURTSITENAME, courtSiteNodes.item(csi)));
                        for (int fci = 0; fci < floatingCaseNodes.getLength(); fci++) {
                            // For each case, extract all the defendants for that case node
                            NodeList defendantNodes = (NodeList) getXPath().evaluate(XPATH_DEFENDANT,
                                floatingCaseNodes.item(fci), XPathConstants.NODESET);

                            cppData.addAll(
                                getDefendantData(floatingCaseNodes, defendantNodes, null, courtSiteValue, fci, true));
                        }
                    }
                }
            }
        } catch (XPathExpressionException e) {
            LOG.error("SummaryByNameCppToPublicDisplay.getCppData() for floating cases - XPathExpressionException  - "
                + e.toString());
        } catch (Exception e) {
            LOG.error("SummaryByNameCppToPublicDisplay.getCppData() for floating cases - Exception  - " + e.toString());
        }
        return cppData;
    }

    private List<SummaryByNameValue> getDefendantData(NodeList caseNodes, NodeList defendantNodes,
        XhbCourtRoomDao courtRoomValue, XhbCourtSiteDao courtSiteValue, int ci, boolean isFloating)
        throws XPathExpressionException {
        List<SummaryByNameValue> cppData = new ArrayList<>();
        SummaryByNameValue summaryByNameValue;
        if (defendantNodes.getLength() > 0 && null != defendantNodes.item(0)) {
            for (int di = 0; di < defendantNodes.getLength(); di++) {
                // Create a new SummaryByNameValue for each Defendant
                summaryByNameValue = getSummaryByNameValue();

                // Populate fields Specific to PublicDisplayValue
                populateCourtSiteRoomData(summaryByNameValue, courtRoomValue, courtSiteValue);

                // Populate the SummaryByNameValue specific fields
                populateData(summaryByNameValue, (Element) defendantNodes.item(di), (Element) caseNodes.item(ci));
                if (isFloating) {
                    summaryByNameValue.setFloating("1");
                }

                // Add the populated SummaryByNameValue to cppData
                cppData.add(summaryByNameValue);
            }
        }
        return cppData;
    }

    /**
     * Populates the SummaryByNameValue object with data from the courtroom XML element.
     * 
     * @param value SummaryByNameValue
     * @param defNode XML element for the defendant
     * @throws XPathExpressionException Exception
     */
    protected void populateData(SummaryByNameValue value, Element defNode, Element caseNode)
        throws XPathExpressionException {
        // Populate PublicDisplayValue specific fields
        super.populateData(value, caseNode);

        // Populate the SummaryByNameValue specific fields
        value.setDefendantName(new DefendantName(getXPath().evaluate(XPATH_DEF_FIRSTNAME, defNode),
            getXPath().evaluate(XPATH_DEF_MIDDLENAME, defNode), getXPath().evaluate(XPATH_DEF_SURNAME, defNode),
            false));

        value.setReportingRestricted(getDefendantReportRestriction(defNode));
        value.setFloating("0");
        // Field needs to be set otherwise the SummaryByNameValue won't appear
        // in the Public Display
    }

    private SummaryByNameValue getSummaryByNameValue() {
        return new SummaryByNameValue();
    }
}
