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
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.CourtDetailValue;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.JudgeName;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.PublicNoticeValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

public class CourtDetailCppToPublicDisplay extends AllCourtStatusCppToPublicDisplay {

    public CourtDetailCppToPublicDisplay(Date date, int courtId, int... courtRoomIds) {
        super(date, courtId, courtRoomIds);
    }

    // Use only in unit test
    public CourtDetailCppToPublicDisplay(Date date, int courtId, int[] courtRoomIds,
        XhbCourtRepository xhbCourtRepository, XhbCourtSiteRepository xhbCourtSiteRepository,
        XhbClobRepository xhbClobRepository, CppFormattingHelper cppFormattingHelper) {
        super(date, courtId, courtRoomIds, xhbCourtRepository, xhbCourtSiteRepository, xhbClobRepository,
            cppFormattingHelper);
    }

    /**
     * Returns a collection of CourtDetailValue CPP Data.
     */
    @Override
    public Collection<?> getCppData(final EntityManager entityManager) {
        LOG.debug("getCppData({})", entityManager);
        List<CourtDetailValue> cppData = new ArrayList<>();

        // Retrieve XML Document of CPP data from the latest XHB_CPP_FORMATTING row for the
        // court
        // supplied
        Document doc = isCourtCppEnabled(entityManager) ? getCppClobAsDocument(entityManager) : null;
        if (doc != null) {
            CourtDetailValue courtDetailValue;
            for (int roomId : courtRoomIds) {
                courtDetailValue = getCourtDetail(roomId, doc);
                if (courtDetailValue != null) {
                    cppData.add(courtDetailValue);
                }
            }
        }
        return cppData;
    }

    private CourtDetailValue getCourtDetail(int roomId, Document doc) {
        CourtDetailValue courtDetailValue;
        // Loop through all the Court Room Ids supplied and create a new
        // CourtDetailValue for each
        try {
            // Using court room id, get the XhbCourtRoomDAO so we can lookup the CPP
            // <courtroom>
            // node using Court Room Name
            XhbCourtRoomDao courtRoomValue = getCourtRoomObjectById(roomId);
            if (null != courtRoomValue) {
                XhbCourtSiteDao courtSiteValue = getCourtSiteObjectById(courtRoomValue.getCourtSiteId());

                // Search for matching nodes in the Document based upon the court room
                // name
                NodeList nodes = getCourtSiteNodeList(courtRoomValue, courtSiteValue, doc);
                if (nodes.getLength() > 0 && null != nodes.item(0)) {
                    // Using the <courtroom> node from the XML Document, use the
                    // information inside to
                    // populate the CourtDetailValue
                    // Retrieve the active case node and populate the CourtDetailValue
                    // with the case
                    // data
                    NodeList caseNodes = (NodeList) getXPath().evaluate("cases/caseDetails[activecase=1]",
                        nodes.item(0), XPathConstants.NODESET);
                    if (caseNodes.getLength() != 0 && null != caseNodes.item(0)) {
                        Node caseNode = caseNodes.item(0);
                        courtDetailValue = getCourtDetailValue();

                        // Populate fields Specific to PublicDisplayValue
                        populateCourtSiteRoomData(courtDetailValue, courtRoomValue, courtSiteValue);

                        // Populate the rest of the data
                        populateData(courtDetailValue, (Element) caseNode);

                        return courtDetailValue;
                    } else {

                        LOG.debug(
                            "CourtDetailCppToPublicDisplay.getCppData() - " + "no active case data for court room: {}",
                            courtRoomValue.getCourtRoomName());
                    }
                }
            }
        } catch (XPathExpressionException e) {
            LOG.error("CourtDetailCppToPublicDisplay.getCppData() - XPathExpressionException  - " + e.toString());
        } catch (Exception e) {
            LOG.error("CourtDetailCppToPublicDisplay.getCppData() - Exception  - " + e.toString());
        }

        return null;
    }

    private CourtDetailValue getCourtDetailValue() {
        return new CourtDetailValue();
    }

    /**
     * Populates the CourtDetailValue object with data from the courtroom XML element.
     * 
     * @param value CourtDetailValue
     * @param caseNode XML element for the case
     * @throws XPathExpressionException Exception
     */
    protected void populateData(CourtDetailValue value, Element caseNode) throws XPathExpressionException {
        super.populateData(value, caseNode);

        // Set Data Specific to CourtDetailValue
        String judgeName = getXPath().evaluate(XPATH_JUDGENAME, caseNode);
        value.setJudgeName(new JudgeName(judgeName));
        String hearingDesc = getXPath().evaluate(XPATH_HEARINGTYPE, caseNode);
        value.setHearingDescription(hearingDesc);
        populatePublicNotices(value, caseNode);
    }

    /**
     * Adds all public notices to the CourtDetailValue.
     * 
     * @param value CourtDetailValue
     * @param caseNode XML element for the case
     * @throws XPathExpressionException Exception
     */
    protected void populatePublicNotices(CourtDetailValue value, Element caseNode) throws XPathExpressionException {
        // Loop through all Public Notices
        ArrayList<PublicNoticeValue> publicNotices = new ArrayList<>();
        NodeList publicNoticeList =
            (NodeList) getXPath().evaluate(XPATH_PUBLICNOTICE, caseNode, XPathConstants.NODESET);
        PublicNoticeValue pnValue;
        for (int i = 0; i < publicNoticeList.getLength(); i++) {
            pnValue = getPublicNoticeValue();
            pnValue.setPublicNoticeDesc(getXPath().evaluate(".", publicNoticeList.item(i)));
            publicNotices.add(pnValue);
        }
        value.setPublicNotices(publicNotices.toArray(new PublicNoticeValue[0]));
    }

    private PublicNoticeValue getPublicNoticeValue() {
        return new PublicNoticeValue();
    }

}
