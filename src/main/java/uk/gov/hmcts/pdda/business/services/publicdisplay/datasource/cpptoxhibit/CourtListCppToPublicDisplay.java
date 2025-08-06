
package uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.cpptoxhibit;

import jakarta.persistence.EntityManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.services.cppformatting.CppFormattingHelper;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.CourtListValue;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.DefendantName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.xml.xpath.XPathExpressionException;

public class CourtListCppToPublicDisplay extends AbstractCppToPublicDisplay {


    public CourtListCppToPublicDisplay(Date date, int courtId, int... courtRoomIds) {
        super(date, courtId, courtRoomIds);
    }

    // Use only in unit test
    public CourtListCppToPublicDisplay(Date date, int courtId, int[] courtRoomIds,
        XhbCourtRepository xhbCourtRepository, XhbCourtSiteRepository xhbCourtSiteRepository,
        XhbCourtRoomRepository xhbCourtRoomRepository, XhbClobRepository xhbClobRepository,
        CppFormattingHelper cppFormattingHelper) {
        super(date, courtId, courtRoomIds, xhbCourtRepository, xhbCourtSiteRepository, xhbCourtRoomRepository,
            xhbClobRepository, cppFormattingHelper);
    }

    @Override
    public Collection<?> getCppData(final EntityManager entityManager) {
        LOG.debug("getCppData({})", entityManager);
        Collection<CourtListValue> cppData = new ArrayList<>();

        LOG.info("CourtListCppToPublicDisplay.getCppData()");

        // Check the court is CPP enabled and then retrieve data
        if (isCourtCppEnabled(entityManager)) {
            LOG.info("CourtListCppToPublicDisplay.getCppData() - courtid: " + courtId + " - is a CPP enabled court");

            // get the public display document as a Document for manipulation
            Document cppDocument = getCppClobAsDocument(entityManager);
            if (null != cppDocument) {
                cppData = getCppDataFromDocument(cppDocument);
            } else {
                LOG.debug("CourtListCppToPublicDisplay.getCppData() - latest xml not retrieved"
                    + " for today - i.e. getCppClobAsDocument() returned null");
            }
        }
        return cppData;
    }

    private Collection<CourtListValue> getCppDataFromDocument(Document cppDocument) {
        Collection<CourtListValue> cppData = new ArrayList<>();
        for (int roomId : courtRoomIds) {
            // Retrieve the required court room - will only be one
            // Using court room id, get the XhbCourtRoomDAO so we can lookup the CPP <courtroom>
            // node
            // using Court Room Name
            XhbCourtRoomDao courtRoomValue = getCourtRoomObjectById(roomId);
            if (courtRoomValue == null) {
                continue;
            }
            // Search for matching nodes in the Document based upon the court room name
            Node courtRoom = getCourtRoomNode(cppDocument, courtRoomValue);
            if (null != courtRoom) {
                // retrieve the list of cases
                try {
                    NodeList cases = getCasesNodeList(courtRoom);
                    cppData.addAll(getCaseData(cases, courtRoomValue));
                } catch (XPathExpressionException e) {
                    LOG.error(
                        "CourtListCppToPublicDisplay.populateCourtListDataFromCppXml() - XPathExpressionException  - "
                            + e.toString());
                }
            }
        }
        return cppData;
    }

    private List<CourtListValue> getCaseData(NodeList cases, XhbCourtRoomDao courtRoomValue) {
        List<CourtListValue> cppData = new ArrayList<>();
        for (int i = 0; i < cases.getLength(); i++) {
            Node caze = cases.item(i);
            if (null != caze) {
                CourtListValue courtListValue = getCourtListValue();
                // Using court site id, get the XhbCourtSiteDAO so we can add details to
                // the object
                XhbCourtSiteDao courtSiteValue = getCourtSiteObjectById(courtRoomValue.getCourtSiteId());

                populateData(courtListValue, caze, courtRoomValue, courtSiteValue);

                // add to the collection
                cppData.add(courtListValue);
            }
        }
        return cppData;
    }

    private CourtListValue getCourtListValue() {
        return new CourtListValue();
    }

    protected Node getCourtRoomNode(Document cppDocument, XhbCourtRoomDao courtRoomValue) {
        NodeList courtRoomNodeList;
        Node courtRoom = null;
        try {
            XhbCourtSiteDao courtSiteValue = getCourtSiteObjectById(courtRoomValue.getCourtSiteId());
            courtRoomNodeList = getCourtSiteNodeList(courtRoomValue, courtSiteValue, cppDocument);
            if (courtRoomNodeList != null) {
                courtRoom = courtRoomNodeList.item(0);
            }
        } catch (XPathExpressionException e) {
            LOG.error("CourtListCppToPublicDisplay.getCourtRoomNode() - latest xml not retrieved for today - i.e. "
                + "getCppClobAsDocument() returned null");
        }
        return courtRoom;
    }

    protected void populateData(CourtListValue courtListValue, Node caze, XhbCourtRoomDao courtRoomValue,
        XhbCourtSiteDao courtSiteValue) {

        try {
            // Populate fields Specific to PublicDisplayValue
            populateCourtSiteRoomData(courtListValue, courtRoomValue, courtSiteValue);

            // Retrieve data from the xml structure
            Element caseElement = (Element) caze;
            super.populateData(courtListValue, caseElement);
            // set the case number correctly.
            // if xhibit casenumber exists use it, if not use the cppurn
            String caseNumber;
            String caseNumberCpp = getXPath().evaluate(XPATH_CPPURN, caseElement);
            if (null != caseNumberCpp && !"".equals(caseNumberCpp)) {
                caseNumber = caseNumberCpp;
            } else {
                caseNumber = getXPath().evaluate(XPATH_CASETYPE, caseElement)
                    + getXPath().evaluate(XPATH_CASENUMBER, caseElement);

            }
            populateCaseData(courtListValue, caseNumber, caseElement, courtRoomValue);
        } catch (XPathExpressionException e) {
            LOG.error(
                "CourtListCppToPublicDisplay.populateCourtListDataFromCppXml() - " + "XPathExpressionException  - {}",
                e.toString());
        }
    }

    private void populateCaseData(CourtListValue courtListValue, String caseNumber, Element caseElement,
        XhbCourtRoomDao courtRoomValue) throws XPathExpressionException {

        String hearingProgress = getXPath().evaluate(XPATH_HEARINGPROGRESS, caseElement);
        String hearingDescription = getXPath().evaluate(XPATH_HEARINGTYPE, caseElement);

        // set courtListValues from data retrieved from xml
        if (caseNumber != null && !"".equals(caseNumber)) {
            courtListValue.setCaseNumber(caseNumber);
        } else {
            LOG.error("CourtListCppToPublicDisplay.populateCourtListDataFromCppXml() - "
                + "no case number provided for - court site id" + courtRoomValue.getCourtSiteId().toString()
                + " and court room id - " + courtRoomValue.getCourtRoomId().toString());
        }

        if (hearingProgress != null && !"".equals(hearingProgress)) {
            courtListValue.setHearingProgress(Integer.parseInt(hearingProgress));
        }

        if (hearingDescription != null) {
            courtListValue.setHearingDescription(hearingDescription);
        }

        // get the defendants
        // Loop through all Defendants and add them
        NodeList defendantList = getDefendantNodeList(caseElement);
        populateDefendantData(defendantList, courtListValue);

        // Set the List Court Room Id
        String listCourtRoomName = getXPath().evaluate(XPATH_LISTCOURTROOM, caseElement);
        XhbCourtRoomDao listCourtRoomValue = getCourtRoomObjectByName(listCourtRoomName);
        if (null != listCourtRoomValue) {
            courtListValue.setListCourtRoomId(listCourtRoomValue.getCourtRoomId());
        }
    }

    private void populateDefendantData(NodeList defendantList, CourtListValue courtListValue)
        throws XPathExpressionException {
        boolean reportingRestrictions = false;
        for (int i = 0; i < defendantList.getLength(); i++) {

            courtListValue
                .addDefendantName(getDefendantName(getXPath().evaluate(XPATH_DEF_FIRSTNAME, defendantList.item(i)),
                    getXPath().evaluate(XPATH_DEF_MIDDLENAME, defendantList.item(i)),
                    getXPath().evaluate(XPATH_DEF_SURNAME, defendantList.item(i))));

            boolean defReportingRestricted = getDefendantReportRestriction((Element) defendantList.item(i));
            if (!reportingRestrictions && defReportingRestricted) {
                reportingRestrictions = true;
            }
        }
        // If reporting restrictions on any defendants set here
        courtListValue.setReportingRestricted(reportingRestrictions);

    }

    private DefendantName getDefendantName(String firstName, String middleName, String surname) {
        return new DefendantName(firstName, middleName, surname, false);
    }
}
