
package uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.cpptoxhibit;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.services.cppformatting.CppFormattingHelper;
import uk.gov.hmcts.pdda.business.vos.services.court.CourtStructureValue;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

/**
 * The AbstractCppToPublicDisplay class is used to retrieve CPP XML, extract data from the CPP XML
 * and update the XHB_CPP_FORMATTING table's STATUS column.
 * 
 * @author groenm
 *
 */
@SuppressWarnings("PMD.GodClass")
public class AbstractCppToPublicDisplayRepos {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractCppToPublicDisplayRepos.class);
    private static final Integer ONE = 1;
    private static final String DATE_MASK = "dd/MM/yy HH:mm";
    protected static final String EMPTY_STRING = "";

    private XhbCourtRepository xhbCourtRepository;
    private XhbCourtSiteRepository xhbCourtSiteRepository;
    private XhbCourtRoomRepository xhbCourtRoomRepository;
    private CppFormattingHelper cppFormattingHelper;
    private XhbClobRepository xhbClobRepository;
    protected CourtStructureValue xhbCourtStructure;

    protected XPath xp;

    protected AbstractCppToPublicDisplayRepos() {
        super();
    }

    // Use only in unit test
    protected AbstractCppToPublicDisplayRepos(XhbCourtRepository xhbCourtRepository,
        XhbCourtSiteRepository xhbCourtSiteRepository, XhbCourtRoomRepository xhbCourtRoomRepository,
        XhbClobRepository xhbClobRepository, CppFormattingHelper cppFormattingHelper) {
        this();
        this.xhbCourtRepository = xhbCourtRepository;
        this.xhbCourtSiteRepository = xhbCourtSiteRepository;
        this.xhbCourtRoomRepository = xhbCourtRoomRepository;
        this.xhbClobRepository = xhbClobRepository;
        this.cppFormattingHelper = cppFormattingHelper;
    }

    /**
     * Converts a String in the format dd-MM-yyyy HH:mm to a Timestamp object.
     * 
     * @param dateTime Timestamp string
     * @return Timestamp
     */
    protected LocalDateTime convertStringToTimestamp(String dateTime) {
        LocalDateTime timestamp = null;
        String methodName = "convertStringToTimestamp(dateTime=>" + dateTime + ", mask=>" + DATE_MASK + ") ";
        try {
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATE_MASK);
            timestamp = LocalDateTime.parse(dateTime, dateFormat);
        } catch (Exception e) {
            LOG.error(methodName + "Exception - " + e.getMessage());
        }
        return timestamp;
    }

    /**
     * Returns the XPath object reference.
     * 
     * @return XPath
     */
    protected XPath getXPath() {
        if (xp == null) {
            xp = XPathFactory.newInstance().newXPath();
        }
        return xp;
    }


    /**
     * getXhbCourtStructure.
     * 
     * @return the xhbCourtStructure
     */
    public CourtStructureValue getXhbCourtStructure() {
        return xhbCourtStructure;
    }

    /**
     * Get a court structure object to use with retrieving the CPP data from the XML.
     */
    protected void retrieveCourtStructure(XhbCourtDao court) {
        CourtStructureValue courtStructureValue = new CourtStructureValue();

        courtStructureValue.setCourt(court);

        XhbCourtRoomDao[] courtRoomArray = new XhbCourtRoomDao[0];
        List<XhbCourtSiteDao> courtSitesList = new ArrayList<>();
        List<XhbCourtSiteDao> allCourtSitesForCourt = xhbCourtSiteRepository.findByCourtId(court.getCourtId());
        for (XhbCourtSiteDao courtSite : allCourtSitesForCourt) {
            if ("Y".equals(courtSite.getObsInd())) {
                LOG.debug("Ignored Obsolete CourtSite");
            } else {
                courtSitesList.add(courtSite);

                List<XhbCourtRoomDao> courtRoomList = getCourtRoomList(courtSite, allCourtSitesForCourt);

                courtStructureValue.addCourtRooms(courtSite.getCourtSiteId(), courtRoomList.toArray(courtRoomArray));
            }
        }

        courtStructureValue.setCourtSites(courtSitesList.toArray(new XhbCourtSiteDao[0]));

        xhbCourtStructure = courtStructureValue;
    }

    private List<XhbCourtRoomDao> getCourtRoomList(XhbCourtSiteDao courtSite, List<XhbCourtSiteDao> xhbCourtSites) {
        List<XhbCourtRoomDao> courtRoomList = new ArrayList<>();
        List<XhbCourtRoomDao> allCourtRooms = xhbCourtRoomRepository.findByCourtSiteId(courtSite.getCourtSiteId());
        for (XhbCourtRoomDao courtRoom : allCourtRooms) {
            if ("Y".equals(courtRoom.getObsInd())) {
                LOG.debug("Ignored Obsolete CourtRoom");
            } else {
                if (xhbCourtSites.size() > ONE) {
                    courtRoom.setMultiSiteDisplayName(courtSite.getShortName() + "-" + courtRoom.getDisplayName());
                }

                courtRoomList.add(courtRoom);
            }
        }
        return courtRoomList;
    }

    /**
     * setXhbCourtStructure.
     * 
     * @param xhbCourtStructure the xhbCourtStructure to set
     */
    public void setXhbCourtStructure(CourtStructureValue xhbCourtStructure) {
        this.xhbCourtStructure = xhbCourtStructure;
    }

    protected XhbCourtRoomDao getXhbCourtRoomDao(XhbCourtRoomDao room) {
        return new XhbCourtRoomDao(room);
    }

    protected XhbCourtSiteDao getXhbCourtSiteDao(XhbCourtSiteDao site) {
        return new XhbCourtSiteDao(site);
    }

    protected XhbCourtRepository getXhbCourtRepository(EntityManager entityManager) {
        if (xhbCourtRepository == null) {
            return new XhbCourtRepository(entityManager);
        }
        return xhbCourtRepository;
    }

    protected XhbClobRepository getXhbClobRepository(EntityManager entityManager) {
        if (xhbClobRepository == null) {
            return new XhbClobRepository(entityManager);
        }
        return xhbClobRepository;
    }

    protected CppFormattingHelper getCppFormattingHelper() {
        if (cppFormattingHelper == null) {
            return new CppFormattingHelper();
        }
        return cppFormattingHelper;
    }
}
