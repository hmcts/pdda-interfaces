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
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.JudgeName;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.JuryStatusDailyListValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

public class JuryCurrentStatusCppToPublicDisplay extends CourtListCppToPublicDisplay {

    public JuryCurrentStatusCppToPublicDisplay(Date date, int courtId, int... courtRoomIds) {
        super(date, courtId, courtRoomIds);
    }

    // Use only in unit test
    public JuryCurrentStatusCppToPublicDisplay(Date date, int courtId, int[] courtRoomIds,
        XhbCourtRepository xhbCourtRepository, XhbClobRepository xhbClobRepository,
        CppFormattingHelper cppFormattingHelper) {
        super(date, courtId, courtRoomIds, xhbCourtRepository, xhbClobRepository, cppFormattingHelper);
    }

    @Override
    public Collection getCppData(final EntityManager entityManager) {
        LOG.debug("getCppData({})", entityManager);
        Collection<JuryStatusDailyListValue> cppData = new ArrayList<>();

        // Check the court is CPP enabled and then retrieve data
        if (isCourtCppEnabled(entityManager)) {
            // Retrieve XML Document of CPP data from the latest XHB_CPP_FORMATTING row for the
            // court
            // supplied
            Document cppDocument = getCppClobAsDocument(entityManager);
            if (cppDocument != null) {
                cppData.addAll(getCourtRoomData(cppDocument));
                cppData.addAll(getCourtSiteData(cppDocument));
            }
        }

        return cppData;
    }

    private List<JuryStatusDailyListValue> getCourtRoomData(Document cppDocument) {
        List<JuryStatusDailyListValue> cppData = new ArrayList<>();
        XhbCourtRoomDao courtRoomValue;
        XhbCourtSiteDao courtSiteValue;
        for (int roomId : courtRoomIds) {
            // Loop through all the Court Room Ids supplied and create a new
            // JuryStatusDailyListValue
            // for each case
            try {
                // Using court room id, get the XhbCourtRoomDAO so we can lookup the CPP
                // <courtroom>
                // node using Court Room Name
                courtRoomValue = getCourtRoomObjectById(roomId);
                if (null != courtRoomValue) {
                    // Using court site id, get the XhbCourtSiteDAO so we can add details to
                    // the object
                    courtSiteValue = getCourtSiteObjectById(courtRoomValue.getCourtSiteId());

                    // Search for matching nodes in the Document based upon the court room
                    // name
                    Node courtRoom = getCourtRoomNode(cppDocument, courtRoomValue);
                    if (courtRoom != null) {
                        // retrieve the list of cases and add them
                        NodeList cases = getCasesNodeList(courtRoom);
                        cppData.addAll(getCaseData(cases, courtRoomValue, courtSiteValue, false));
                    }
                }
            } catch (XPathExpressionException e) {
                LOG.error(
                    "JuryCurrentStatusCppToPublicDisplay.getCppData() - XPathExpressionException  - " + e.toString());
            } catch (Exception e) {
                LOG.error("JuryCurrentStatusCppToPublicDisplay.getCppData() - Exception  - " + e.getMessage());
            }
        }
        return cppData;
    }

    private List<JuryStatusDailyListValue> getCourtSiteData(Document cppDocument) {
        List<JuryStatusDailyListValue> cppData = new ArrayList<>();
        XhbCourtSiteDao courtSiteValue;
        try {
            NodeList courtSiteNodes =
                (NodeList) getXPath().evaluate("//courtsites/courtsite", cppDocument, XPathConstants.NODESET);
            if (courtSiteNodes.getLength() > 0 && null != courtSiteNodes.item(0)) {
                // Loop through all the court sites looking for floating cases at each site
                for (int csi = 0; csi < courtSiteNodes.getLength(); csi++) {

                    // Floating cases
                    NodeList floatingCaseNodes = getFloatingCaseNodeList(courtSiteNodes.item(csi));
                    if (floatingCaseNodes.getLength() > 0 && null != floatingCaseNodes.item(0)) {
                        courtSiteValue = getCourtSiteObjectByName(
                            getXPath().evaluate(XPATH_COURTSITENAME, courtSiteNodes.item(csi)));
                        cppData.addAll(getCaseData(floatingCaseNodes, null, courtSiteValue, true));
                    }
                }
            }
        } catch (XPathExpressionException e) {
            LOG.error("JuryCurrentStatusCppToPublicDisplay.getCppData() for floating cases"
                + " - XPathExpressionException  - " + e.toString());
        } catch (Exception e) {
            LOG.error(
                "JuryCurrentStatusCppToPublicDisplay.getCppData() for floating cases - Exception  - " + e.toString());
        }
        return cppData;
    }

    private List<JuryStatusDailyListValue> getCaseData(NodeList cases, XhbCourtRoomDao courtRoomValue,
        XhbCourtSiteDao courtSiteValue, boolean isFloating) {
        List<JuryStatusDailyListValue> cppData = new ArrayList<>();
        for (int i = 0; i < cases.getLength(); i++) {
            Node caze = cases.item(i);
            if (null != caze) {
                JuryStatusDailyListValue juryStatusDailyListValue = getJuryStatusDailyListValue();

                populateData(juryStatusDailyListValue, caze, courtRoomValue, courtSiteValue);
                if (isFloating) {
                    juryStatusDailyListValue.setFloating("1");
                }

                // add to the collection
                cppData.add(juryStatusDailyListValue);
            }
        }
        return cppData;
    }

    private void populateData(JuryStatusDailyListValue juryStatusDailyListValue, Node caze,
        XhbCourtRoomDao courtRoomValue, XhbCourtSiteDao courtSiteValue) {

        try {
            super.populateData(juryStatusDailyListValue, caze, courtRoomValue, courtSiteValue);

            // Populate fields Specific to JuryStatusDailyListValue
            // Retrieve data from the xml structure
            Element caseElement = (Element) caze;

            String judgeNameStr = getXPath().evaluate(XPATH_JUDGENAME, caseElement);
            juryStatusDailyListValue.setJudgeName(new JudgeName(judgeNameStr));

            juryStatusDailyListValue.setFloating("0");

        } catch (XPathExpressionException e) {
            LOG.error(
                "JuryCurrentStatusCppToPublicDisplay.populateData() - XPathExpressionException  - " + e.toString());
        }
    }

    private JuryStatusDailyListValue getJuryStatusDailyListValue() {
        return new JuryStatusDailyListValue();
    }

}
