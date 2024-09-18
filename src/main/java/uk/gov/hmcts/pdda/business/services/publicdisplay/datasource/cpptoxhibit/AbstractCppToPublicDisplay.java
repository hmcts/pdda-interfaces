
package uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.cpptoxhibit;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobDao;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcppformatting.XhbCppFormattingDao;
import uk.gov.hmcts.pdda.business.services.cppformatting.CppFormattingHelper;
import uk.gov.hmcts.pdda.business.services.formatting.MergeDocumentUtils;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.PublicDisplayValue;
import uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled.DocumentUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

/**
 * The AbstractCppToPublicDisplay class is used to retrieve CPP XML, extract data from the CPP XML
 * and update the XHB_CPP_FORMATTING table's STATUS column.
 * 
 * @author groenm
 *
 */
@SuppressWarnings({"PMD.GodClass", "PMD.CouplingBetweenObjects", "PMD.NullAssignment"})
public abstract class AbstractCppToPublicDisplay extends AbstractCppToPublicDisplayRepos {

    protected static final Logger LOG = LoggerFactory.getLogger(AbstractCppToPublicDisplay.class);
    private static final String YES = "Y";

    protected static final String XPATH_COURTSITENAME = "courtsitename";
    protected static final String XPATH_CASE = "cases/caseDetails";
    protected static final String XPATH_CPPURN = "cppurn";
    protected static final String XPATH_CASENUMBER = "casenumber";
    protected static final String XPATH_CASETYPE = "casetype";
    protected static final String XPATH_FLOATINGCASEDETAILS = "floating/cases/caseDetails";
    protected static final String XPATH_JUDGENAME = "judgename";
    protected static final String XPATH_HEARINGTYPE = "hearingtype";
    protected static final String XPATH_HEARINGPROGRESS = "hearingprogress";
    protected static final String XPATH_NOTBEFORETIME = "notbeforetime";
    protected static final String XPATH_TIMESTATUSSET = "timestatusset";
    protected static final String XPATH_MOVEDFROMCOURTSITE = "movedfromcourtsitename";
    protected static final String XPATH_MOVEDFROMCOURTROOM = "movedfromcourtroomname";
    protected static final String XPATH_LISTCOURTROOM = "listcourtroomname";

    protected static final String XPATH_EVENT = "currentstatus/event";
    protected static final String XPATH_EVENTDATE = "currentstatus/event/date";
    protected static final String XPATH_EVENTTIME = "currentstatus/event/time";

    protected static final String XPATH_DEFENDANT = "defendants/defendant";
    protected static final String XPATH_DEF_FIRSTNAME = "firstname";
    protected static final String XPATH_DEF_MIDDLENAME = "middlename";
    protected static final String XPATH_DEF_SURNAME = "lastname";
    protected static final String XPATH_DEF_RESTRICTIONS = "reportingrestrictions";

    protected static final String XPATH_PUBLICNOTICE = "publicnotices/publicnotice";

    protected final Date date;
    protected final int courtId;
    protected String courtName;
    protected final int[] courtRoomIds;
    protected XhbClobDao cppClob;
    protected XhbCppFormattingDao cppFormattBV;

    protected AbstractCppToPublicDisplay(final Date date, final int courtId,
        final int... courtRoomIds) {
        super();
        this.date = date;
        this.courtId = courtId;
        this.courtRoomIds = courtRoomIds != null ? courtRoomIds.clone() : null;
    }

    // Use only in unit test
    protected AbstractCppToPublicDisplay(final Date date, final int courtId,
        final int[] courtRoomIds, XhbCourtRepository xhbCourtRepository,
        XhbCourtSiteRepository xhbCourtSiteRepository,
        XhbCourtRoomRepository xhbCourtRoomRepository, XhbClobRepository xhbClobRepository,
        CppFormattingHelper cppFormattingHelper) {
        super(xhbCourtRepository, xhbCourtSiteRepository, xhbCourtRoomRepository, xhbClobRepository,
            cppFormattingHelper);
        this.date = date;
        this.courtId = courtId;
        this.courtRoomIds = courtRoomIds != null ? courtRoomIds.clone() : null;
    }

    /**
     * isCourtCppEnabled.
     * 
     * @param entityManager EntityManager
     * @return boolean representing if the court id defined as being CPP ready
     */
    protected boolean isCourtCppEnabled(final EntityManager entityManager) {
        boolean isCppSite = false;

        Optional<XhbCourtDao> xhbCourt = getXhbCourtRepository(entityManager).findById(courtId);
        if (xhbCourt.isPresent() && YES.equals(xhbCourt.get().getCppCourt())) {
            isCppSite = true;

            // set the court name as will be continuing with the process and have this information
            // to hand
            this.setCourtName(xhbCourt.get().getCourtName());
            retrieveCourtStructure(entityManager, xhbCourt.get());
        }

        return isCppSite;
    }

    /**
     * Populates the PublicDisplayValue object with data from the courtroom XML element.
     * 
     * @param value PublicDisplayValue
     * @param caseNode XML element for the case
     * @throws XPathExpressionException Exception
     */
    protected void populateData(PublicDisplayValue value, Element caseNode)
        throws XPathExpressionException {
        LOG.debug("populateData({},{})", value, caseNode);
        String movedFromCourtRoomName = getXPath().evaluate(XPATH_MOVEDFROMCOURTROOM, caseNode);
        if (!EMPTY_STRING.equals(movedFromCourtRoomName)) {
            value.setMovedFromCourtRoomName(movedFromCourtRoomName);
        }

        String movedFromCourtSiteName = getXPath().evaluate(XPATH_MOVEDFROMCOURTSITE, caseNode);
        if (!EMPTY_STRING.equals(movedFromCourtSiteName)) {
            value.setMovedFromCourtSiteShortName(movedFromCourtSiteName);
        }

        String notBeforeTime = getXPath().evaluate(XPATH_NOTBEFORETIME, caseNode);
        if (!EMPTY_STRING.equals(notBeforeTime)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
            String dateString = dateFormat.format(this.date);
            // Combine the time with the date to complete the timestamp otherwise uses 1970
            value.setNotBeforeTime(convertStringToTimestamp(dateString + " " + notBeforeTime));
        }
    }

    /**
     * getCppClobAsDocument.
     * 
     * @return the cppClob as a document for manipulation.
     */
    public Document getCppClobAsDocument(final EntityManager entityManager) {
        LOG.debug("getCppClobAsDocument({})", entityManager);
        String methodName = "getCppClobAsDocument - ";
        Document cppXml = null;
        if (cppClob == null) {
            this.getCppClob(entityManager);
        }

        DocumentBuilder docBuilder = null;
        try {
            docBuilder = MergeDocumentUtils.getDocumentBuilder();
        } catch (ParserConfigurationException e) {
            LOG.error(methodName + "ParserConfigurationException - Unable to retrieve CPP XML - "
                + e.getMessage());
        }

        try {
            if (docBuilder != null && cppClob != null) {
                cppXml = DocumentUtils.createInputDocument(docBuilder, cppClob.getClobData());
            }
        } catch (SAXException e) {
            LOG.error(methodName + "SAXException - Unable to retrieve CPP XML - " + e.getMessage());
        } catch (IOException e) {
            LOG.error(methodName + "IOException - Unable to retrieve CPP XML - " + e.getMessage());
        }

        return cppXml;
    }

    /**
     * getCppData.
     * 
     * @return Collection of the relevant display objects.
     * @throws XPathExpressionException Exception
     */
    public abstract Collection getCppData(EntityManager entityManager)
        throws XPathExpressionException;

    /**
     * getDate.
     * 
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * getCourtId.
     * 
     * @return the courtId
     */
    public int getCourtId() {
        return courtId;
    }

    /**
     * getCourtRoomIds.
     * 
     * @return the courtRoomIds
     */
    public int[] getCourtRoomIds() {
        return courtRoomIds.clone();
    }

    /**
     * getCourtName.
     * 
     * @return the courtName
     */
    public String getCourtName() {
        return courtName;
    }

    /**
     * setCourtName.
     * 
     * @param courtName the courtName to set
     */
    public void setCourtName(String courtName) {
        this.courtName = courtName;
    }

    /**
     * getCppClob.
     * 
     * @return the cppClob
     */
    public XhbClobDao getCppClob(final EntityManager entityManager) {
        LOG.debug("getCppClob({})", entityManager);
        Optional<XhbClobDao> xhbClobDao = Optional.empty();
        if (cppClob == null) {
            // retrieve from the database
            cppFormattBV =
                getCppFormattingHelper().getLatestPublicDisplayDocument(courtId, entityManager);
            if (null != cppFormattBV && null != cppFormattBV.getXmlDocumentClobId()) {
                xhbClobDao = getXhbClobRepository(entityManager)
                    .findById(cppFormattBV.getXmlDocumentClobId());
            }
        }
        if (xhbClobDao.isPresent()) {
            cppClob = xhbClobDao.get();
        }

        return cppClob;
    }

    /**
     * Returns a XhbCourtRoomDAO object that matches the courtRoomId specified.
     * 
     * @param courtRoomId Court Room Id to search for
     * @return XhbCourtRoomDAO
     */
    protected XhbCourtRoomDao getCourtRoomObjectById(int courtRoomId) {
        LOG.debug("getCourtRoomObjectById({})", courtRoomId);
        XhbCourtRoomDao roomValue = null;
        for (XhbCourtRoomDao room : xhbCourtStructure.getAllCourtRooms()) {
            if (room.getCourtRoomId().equals(courtRoomId)) {
                roomValue = getXhbCourtRoomDao(room);
                roomValue.setDisplayName(room.getDisplayName());
                break;
            }
        }
        return roomValue;
    }

    /**
     * Returns a XhbCourtRoomDAO object that matches the courtRoomName specified.
     * 
     * @param courtRoomName Court Room Name to search for
     * @return XhbCourtRoomDAO
     */
    protected XhbCourtRoomDao getCourtRoomObjectByName(String courtRoomName) {
        LOG.debug("getCourtRoomObjectByName({})", courtRoomName);
        XhbCourtRoomDao roomValue = null;
        if (null != courtRoomName && !EMPTY_STRING.equals(courtRoomName)) {
            for (XhbCourtRoomDao room : xhbCourtStructure.getAllCourtRooms()) {
                if (room.getCourtRoomName().equals(courtRoomName)) {
                    roomValue = getXhbCourtRoomDao(room);
                    roomValue.setDisplayName(room.getDisplayName());
                    break;
                }
            }
        }
        return roomValue;
    }

    /**
     * Returns a XhbCourtSiteDAO object that matches the courtSiteId specified.
     * 
     * @param courtSiteId Court Site Id to search for
     * @return XhbCourtSiteDAO
     */
    protected XhbCourtSiteDao getCourtSiteObjectById(int courtSiteId) {
        LOG.debug("getCourtSiteObjectById({})", courtSiteId);
        XhbCourtSiteDao siteValue = null;
        for (XhbCourtSiteDao site : xhbCourtStructure.getCourtSites()) {
            if (site.getCourtSiteId().equals(courtSiteId)) {
                siteValue = getXhbCourtSiteDao(site);
                break;
            }
        }
        return siteValue;
    }

    protected NodeList getCourtSiteNodeList(XhbCourtRoomDao courtRoomValue,
        XhbCourtSiteDao courtSiteValue, Document doc) throws XPathExpressionException {
        return (NodeList) getXPath().evaluate("//courtsite[courtsitename='"
            + courtSiteValue.getCourtSiteName() + "']/courtrooms/courtroom[courtroomname='"
            + courtRoomValue.getCourtRoomName() + "']", doc, XPathConstants.NODESET);
    }

    protected NodeList getCasesNodeList(Node node) throws XPathExpressionException {
        return (NodeList) getXPath().evaluate(XPATH_CASE, node, XPathConstants.NODESET);
    }

    protected NodeList getFloatingCaseNodeList(Node node) throws XPathExpressionException {
        return (NodeList) getXPath().evaluate(XPATH_FLOATINGCASEDETAILS, node,
            XPathConstants.NODESET);
    }

    protected NodeList getDefendantNodeList(Node node) throws XPathExpressionException {
        return (NodeList) getXPath().evaluate(XPATH_DEFENDANT, node, XPathConstants.NODESET);
    }


    /**
     * Returns a XhbCourtSiteDAO object that matches the court site name specified.
     * 
     * @param courtSiteName Court Site Name to search for
     * @return XhbCourtSiteDAO
     */
    protected XhbCourtSiteDao getCourtSiteObjectByName(String courtSiteName) {
        XhbCourtSiteDao siteValue = null;
        if (null != courtSiteName && !EMPTY_STRING.equals(courtSiteName)) {

            for (XhbCourtSiteDao site : xhbCourtStructure.getCourtSites()) {
                if (site.getCourtSiteName().equals(courtSiteName)) {
                    siteValue = new XhbCourtSiteDao(site);
                    break;
                }
            }
        }
        return siteValue;
    }

    /**
     * Populate court details on the PublicDisplayValue.
     * 
     * @param value PublicDisplayValue
     * @param room XhbCourtRoomDao
     * @param site XhbCourtSiteDao
     */
    protected void populateCourtSiteRoomData(PublicDisplayValue value, XhbCourtRoomDao room,
        XhbCourtSiteDao site) {
        // Court room data
        if (null == room) {
            value.setCourtRoomName(EMPTY_STRING);
            value.setCourtRoomId(-1);
            value.setCrestCourtRoomNo(99);
        } else {
            value.setCourtRoomName(room.getDisplayName());
            value.setCourtRoomId(room.getCourtRoomId());
            value.setCrestCourtRoomNo(room.getCrestCourtRoomNo());
        }

        // Court Site data
        if (null == site) {
            value.setCourtSiteName(EMPTY_STRING);
            value.setCourtSiteShortName(EMPTY_STRING);
            value.setCourtSiteCode("Z");
        } else {
            value.setCourtSiteName(site.getCourtSiteName());
            value.setCourtSiteShortName(site.getShortName());
            value.setCourtSiteCode(site.getCourtSiteCode());
        }
    }

    /**
     * Retrieves the boolean value of the Defendant reportingrestriction node which should be 1 if
     * restricted, else 0.
     * 
     * @param defNode Defendant parent node
     * @return true if the reporting is restricted else false.
     * @throws XPathExpressionException Exception
     */
    protected boolean getDefendantReportRestriction(Element defNode)
        throws XPathExpressionException {
        String methodName = "getDefendantReportRestriction(defNode=>" + defNode + ") ";
        boolean reportingRestricted = false;
        String nodeValue = getXPath().evaluate(XPATH_DEF_RESTRICTIONS, defNode);
        if (null != nodeValue && !EMPTY_STRING.equals(nodeValue)) {
            // Convert the String to an Integer and use it to set the boolean flag
            try {
                Integer defReportingRestriction = Integer.parseInt(nodeValue);
                if (defReportingRestriction.equals(1)) {
                    // Reporting is restricted if the node value is 1
                    reportingRestricted = true;
                }
            } catch (NumberFormatException e) {
                LOG.error(methodName + "Exception - " + e.getMessage());
            }
        }
        return reportingRestricted;
    }
}
