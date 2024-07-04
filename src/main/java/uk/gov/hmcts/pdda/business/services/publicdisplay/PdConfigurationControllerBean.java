package uk.gov.hmcts.pdda.business.services.publicdisplay;

import jakarta.ejb.ApplicationException;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.framework.business.exceptions.CourtNotFoundException;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplay.XhbDisplayDao;
import uk.gov.hmcts.pdda.business.entities.xhbdisplay.XhbDisplayRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaydocument.XhbDisplayDocumentDao;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaylocation.XhbDisplayLocationRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaytype.XhbDisplayTypeRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsetdd.XhbRotationSetDdDao;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsetdd.XhbRotationSetDdRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsets.XhbRotationSetsDao;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsets.XhbRotationSetsRepository;
import uk.gov.hmcts.pdda.business.services.publicdisplay.database.query.VipDisplayCourtRoomQuery;
import uk.gov.hmcts.pdda.business.services.publicdisplay.database.query.VipDisplayDocumentQuery;
import uk.gov.hmcts.pdda.business.services.publicdisplay.exceptions.RotationSetNotFoundCheckedException;
import uk.gov.hmcts.pdda.common.publicdisplay.events.ConfigurationChangeEvent;
import uk.gov.hmcts.pdda.common.publicdisplay.jms.PublicDisplayNotifier;
import uk.gov.hmcts.pdda.common.publicdisplay.types.configuration.CourtConfigurationChange;
import uk.gov.hmcts.pdda.common.publicdisplay.types.configuration.CourtDisplayConfigurationChange;
import uk.gov.hmcts.pdda.common.publicdisplay.types.rotationset.DisplayRotationSetData;
import uk.gov.hmcts.pdda.common.publicdisplay.vos.publicdisplay.RotationSetComplexValue;
import uk.gov.hmcts.pdda.common.publicdisplay.vos.publicdisplay.RotationSetDdComplexValue;
import uk.gov.hmcts.pdda.common.publicdisplay.vos.publicdisplay.VipDisplayConfiguration;
import uk.gov.hmcts.pdda.common.publicdisplay.vos.publicdisplay.VipDisplayConfigurationCourtRoom;
import uk.gov.hmcts.pdda.common.publicdisplay.vos.publicdisplay.VipDisplayConfigurationDisplayDocument;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Stateless
@Service
@Transactional
@LocalBean
@ApplicationException(rollback = true)
@SuppressWarnings({"PMD.ExcessiveImports", "PMD.ExcessiveParameterList"})
public class PdConfigurationControllerBean extends PublicDisplayControllerBean
    implements Serializable {

    private static final long serialVersionUID = -1482124759093214736L;

    private static final Logger LOG = LoggerFactory.getLogger(PdConfigurationControllerBean.class);

    private static final String ENTERED = "{} : entered";
    private static final String METHOD_SUFFIX = ") - ";

    private static final Integer ONE = 1;

    public PdConfigurationControllerBean(EntityManager entityManager) {
        super(entityManager);
    }

    public PdConfigurationControllerBean(EntityManager entityManager,
        XhbCourtRepository xhbCourtRepository, XhbRotationSetsRepository xhbRotationSetsRepository,
        XhbRotationSetDdRepository xhbRotationSetDdRepository,
        XhbDisplayTypeRepository xhbDisplayTypeRepository,
        XhbDisplayRepository xhbDisplayRepository,
        XhbDisplayLocationRepository xhbDisplayLocationRepository,
        XhbCourtSiteRepository xhbCourtSiteRepository,
        XhbCourtRoomRepository xhbCourtRoomRepository, PublicDisplayNotifier publicDisplayNotifier,
        VipDisplayDocumentQuery vipDisplayDocumentQuery,
        VipDisplayCourtRoomQuery vipDisplayCourtRoomQuery) {
        super(entityManager, null, xhbCourtRepository, null, null, xhbRotationSetsRepository,
            xhbRotationSetDdRepository, xhbDisplayTypeRepository, xhbDisplayRepository,
            xhbDisplayLocationRepository, xhbCourtSiteRepository, xhbCourtRoomRepository,
            publicDisplayNotifier, vipDisplayDocumentQuery, vipDisplayCourtRoomQuery);
    }

    public PdConfigurationControllerBean() {
        super();
    }

    /**
     * Gets all the courts that are to be rendered for Public Displays.
     *
     * @return an array of IDs of courts to be included in the Public Display.
     */
    public int[] getCourtsForPublicDisplay() {
        final String methodName = "getCourtsForPublicDisplay() - ";
        LOG.debug(ENTERED, methodName);
        ArrayList<XhbCourtDao> courts = (ArrayList<XhbCourtDao>) getXhbCourtRepository().findAll();
        int[] courtArray = new int[courts.size()];
        Iterator<XhbCourtDao> courtIterator = courts.iterator();
        for (int i = 0; i < courtArray.length; i++) {
            courtArray[i] = courtIterator.next().getCourtId().intValue();
        }
        return courtArray;
    }

    /**
     * Gets the full set of public display configuration data for a given court.
     *
     * @param courtId The court for which to get the configuration information.
     * @return An array of <code>DisplayRotationSetData</code>, one for every display in the court.
     * @throws CourtNotFoundException When the court ID passed in is not valid.
     */
    public DisplayRotationSetData[] getCourtConfiguration(final Integer courtId) {
        final String methodName = "getCourtConfiguration(" + courtId + METHOD_SUFFIX;
        LOG.debug(ENTERED, methodName);

        synchronized (this) {
            Optional<XhbCourtDao> court = getXhbCourtRepository().findById(courtId);
            if (!court.isPresent()) {
                throw new CourtNotFoundException(courtId);
            }

            return getDisplayRotationSetDataHelper().getDataForCourt(court.get(),
                getXhbDisplayRepository(), getXhbRotationSetsRepository(),
                getXhbRotationSetDdRepository(), getXhbDisplayDocumentRepository(),
                getXhbDisplayTypeRepository(), getXhbDisplayLocationRepository(),
                getXhbCourtSiteRepository(), getXhbCourtRoomRepository());
        }
    }

    /**
     * Gets the configuration data for all displays using the rotation set.
     *
     * @param courtId       The court that the rotation set belongs to.
     * @param rotationSetId The rotation set for which to get the data.
     * @return An array of <code>DisplayRotationSetData</code>, one for every display using the
     *     rotation set.
     */
    public DisplayRotationSetData[] getUpdatedRotationSet(final int courtId,
        final int rotationSetId) {
        final String methodName =
            "getUpdatedRotationSet(" + courtId + "," + rotationSetId + METHOD_SUFFIX;
        LOG.debug(ENTERED, methodName);

        DisplayRotationSetData[] returnArray;

        Optional<XhbRotationSetsDao> rotationSet =
            getXhbRotationSetsRepository().findById(Long.valueOf(rotationSetId));
        if (rotationSet.isPresent()) {
            // Set the court object on the XhbRotationSetsDAO
            Optional<XhbCourtDao> court = getXhbCourtRepository().findById(courtId);
            if (!court.isPresent()) {
                throw new CourtNotFoundException(courtId);
            }
            List<XhbDisplayDao> xhbDisplays =
                getXhbDisplayRepository().findByRotationSetId(rotationSetId);

            returnArray =
                getDisplayRotationSetDataHelper().getDataForDisplayRotationSets(court.get(),
                    rotationSet.get(), xhbDisplays, getXhbRotationSetDdRepository(),
                    getXhbDisplayDocumentRepository(), getXhbDisplayTypeRepository(),
                    getXhbDisplayLocationRepository(), getXhbCourtSiteRepository(),
                    getXhbCourtRoomRepository());
        } else {
            returnArray = new DisplayRotationSetData[0];
        }
        return returnArray;
    }

    /**
     * Gets the configuration data for a given display in a court.
     *
     * @param courtId   The court that the rotation set belongs to.
     * @param displayId The display for which to get the data
     * @return A <code>DisplayRotationSetData</code> array representing the configuration of the
     *     display. If no display exists of this ID then it returns a zero length array.
     */
    @SuppressWarnings("checkstyle:JavadocTagContinuationIndentation")
    public DisplayRotationSetData[] getUpdatedDisplay(final int courtId, final int displayId) {
        final String methodName = "getUpdatedDisplay(" + courtId + "," + displayId + METHOD_SUFFIX;
        LOG.debug(ENTERED, methodName);

        DisplayRotationSetData[] returnArray;

        Optional<XhbDisplayDao> display = getXhbDisplayRepository().findById(displayId);
        if (display.isPresent()) {
            Optional<XhbCourtDao> court = getXhbCourtRepository().findById(courtId);
            if (!court.isPresent()) {
                throw new CourtNotFoundException(courtId);
            }

            DisplayRotationSetData displayRotationSetData = null;
            Optional<XhbRotationSetsDao> rotationSet = getXhbRotationSetsRepository().findById(
                Long.valueOf(display.get().getRotationSetId()));
            if (rotationSet.isPresent()) {
                displayRotationSetData =
                    getDisplayRotationSetDataHelper().getDisplayRotationSetData(court.get(),
                        display.get(), rotationSet.get(), getXhbRotationSetDdRepository(),
                        getXhbDisplayDocumentRepository(), getXhbDisplayTypeRepository(),
                        getXhbDisplayLocationRepository(), getXhbCourtSiteRepository(),
                        getXhbCourtRoomRepository());
            }
            if (displayRotationSetData != null) {
                returnArray = new DisplayRotationSetData[] {displayRotationSetData};
            } else {
                returnArray = new DisplayRotationSetData[0];
            }
        } else {
            returnArray = new DisplayRotationSetData[0];
        }
        return returnArray;
    }

    /**
     * Returns a rotation set with an array of the display documents that are assigned in to the
     * rotation set.
     *
     * @param rotationSetId The rotation set being queried
     * @return RotationSetComplexValue
     */
    public RotationSetComplexValue getRotationSet(final Integer rotationSetId) {
        final String methodName = "getRotationSet(" + rotationSetId + METHOD_SUFFIX;
        LOG.debug(ENTERED, methodName);

        Optional<XhbRotationSetsDao> rotationSetLocal =
            getXhbRotationSetsRepository().findById(Long.valueOf(rotationSetId));
        if (!rotationSetLocal.isPresent()) {
            throw new RotationSetNotFoundCheckedException(rotationSetId);
        }
        RotationSetComplexValue returnValue = new RotationSetComplexValue();
        returnValue.setRotationSetDao(rotationSetLocal.get());

        List<XhbRotationSetDdDao> rotationSetDdCol =
            getXhbRotationSetDdRepository().findByRotationSetId(rotationSetId);
        List<RotationSetDdComplexValue> results = new ArrayList<>();
        RotationSetDdComplexValue ddComplex;
        Iterator<XhbRotationSetDdDao> rotationSetDdIter = rotationSetDdCol.iterator();
        while (rotationSetDdIter.hasNext()) {
            XhbRotationSetDdDao rotationSetDdLocal = rotationSetDdIter.next();
            Optional<XhbDisplayDocumentDao> xhbDisplayDocument =
                getXhbDisplayDocumentRepository().findById(
                    rotationSetDdLocal.getDisplayDocumentId());
            XhbDisplayDocumentDao xhbDisplayDocumentDao =
                xhbDisplayDocument.isPresent() ? xhbDisplayDocument.get() : null;
            ddComplex = getRotationSetDdComplexValue(rotationSetDdLocal, xhbDisplayDocumentDao);
            results.add(ddComplex);
        }
        returnValue.setRotationSetDdComplexValues(
            results.toArray(new RotationSetDdComplexValue[0]));
        return returnValue;
    }

    /**
     * Requests that the rotation set and display documents within the rotation set are all
     * re-rendered from scratch.
     *
     * <p>Note: sends a RenderEntireDisplayRotationSet JMS configuration message
     *
     * @param displayId the display that will have its pages and rotation set re-rendered
     */
    public void initialiseDisplay(final Integer courtId, final Integer displayId) {
        final String methodName = "initialiseDisplay(" + courtId + "," + displayId + METHOD_SUFFIX;
        LOG.debug(ENTERED, methodName);

        CourtConfigurationChange ccc =
            new CourtDisplayConfigurationChange(courtId.intValue(), displayId.intValue(), true);
        ConfigurationChangeEvent ccEvent = new ConfigurationChangeEvent(ccc);
        LOG.debug("Sending Display initialisation message.");
        getPublicDisplayNotifier().sendMessage(ccEvent);
        LOG.debug("Display initialisation message sent.");
    }

    /**
     * Requests that all rotation sets and display documents are all re-rendered from scratch.
     *
     * <p>Note: sends a RenderEntireCourt JMS configuration message
     *
     * @param courtId the court to be completely re-rendered.
     */
    public void initialiseCourt(final Integer courtId) {
        LOG.debug("initialiseCourt({})", courtId);

        CourtConfigurationChange ccc = new CourtConfigurationChange(courtId.intValue(), true);
        ConfigurationChangeEvent ccEvent = new ConfigurationChangeEvent(ccc);
        LOG.debug("Sending Court initialisation message.");
        getPublicDisplayNotifier().sendMessage(ccEvent);
        LOG.debug("Court initialisation message sent.");
    }

    /**
     * Requests all courtrooms for a court house.
     *
     * @param courtId the court house
     * @return array of XhbCourtRoomDAO
     */
    public XhbCourtRoomDao[] getCourtRoomsForCourt(final Integer courtId) {
        final String methodName = "getCourtRoomsForCourt(" + courtId + METHOD_SUFFIX;
        LOG.debug(ENTERED, methodName);
        ArrayList<XhbCourtRoomDao> al = new ArrayList<>();
        ArrayList<XhbCourtSiteDao> courtSites =
            (ArrayList<XhbCourtSiteDao>) getXhbCourtSiteRepository().findByCourtId(courtId);
        int numCourtSites = courtSites.size();

        Iterator<XhbCourtSiteDao> iter = courtSites.iterator();
        while (iter.hasNext()) {
            XhbCourtSiteDao site = iter.next();
            XhbCourtRoomDao[] courtRooms = getXhbCourtRoomDaoArray(site.getXhbCourtRooms());

            for (XhbCourtRoomDao courtRoom : courtRooms) {
                // If a multi site court, set the multi site display name to '<Court Site Short
                // Name>-<Court Room Display Name>'
                if (numCourtSites > ONE) {
                    courtRoom.setMultiSiteDisplayName(
                        site.getShortName() + "-" + courtRoom.getDisplayName());
                }
                al.add(courtRoom);
            }
        }

        return getXhbCourtRoomDaoArray(al);
    }

    /**
     * Requests courtrooms assigned to the VIP screen for a court house If none are found, falls
     * back to returning all court rooms.
     *
     * @param courtId the court house
     * @return array of XhbCourtRoomDAO
     */
    public XhbCourtRoomDao[] getVipCourtRoomsForCourt(final Integer courtId) {
        final String methodName = "getVipCourtRoomsForCourt(" + courtId + METHOD_SUFFIX;
        LOG.debug(ENTERED, methodName);

        boolean multiSite;
        ArrayList<XhbCourtSiteDao> courtSites =
            (ArrayList<XhbCourtSiteDao>) getXhbCourtSiteRepository().findByCourtId(courtId);
        multiSite = courtSites.size() > 1;

        VipCourtRoomsQuery vipQuery = getVipCourtRoomsQuery(multiSite);
        XhbCourtRoomDao[] results = vipQuery.getData(courtId);
        if (results.length > 0) {
            return results;
        } else {
            return getCourtRoomsForCourt(courtId);
        }
    }

    /**
     * Returns a composite value object containing display document, court room and unassigned cases
     * information for the court site.
     *
     * @return VIPDisplayConfiguration
     */
    public VipDisplayConfiguration getVipDisplayConfiguration(final Integer courtSiteId) {
        final String methodName = "getVIPDisplayConfiguration(" + courtSiteId + METHOD_SUFFIX;
        LOG.debug(ENTERED, methodName);

        VipDisplayConfigurationCourtRoom[] courtRoomArray = null;
        VipDisplayConfigurationDisplayDocument[] displayDocArray = null;

        // Retrieve display documents information for court site VIP
        VipDisplayDocumentQuery vipDisplayDocumentQuery = getVipDisplayDocumentQuery();
        Collection<VipDisplayConfigurationDisplayDocument> vipDisplayDocumentCol =
            vipDisplayDocumentQuery.getData(courtSiteId);
        if (vipDisplayDocumentCol != null) {
            displayDocArray =
                new VipDisplayConfigurationDisplayDocument[vipDisplayDocumentCol.size()];
            vipDisplayDocumentCol.toArray(displayDocArray);
        }

        // Retrieve assigned court room information for court site VIP
        VipDisplayCourtRoomQuery vipDisplayCourtRoomQuery = getVipDisplayCourtRoomQuery();
        Collection<?> vipDisplayCourtRoomCol = vipDisplayCourtRoomQuery.getData(courtSiteId);
        if (vipDisplayCourtRoomCol != null) {
            courtRoomArray = new VipDisplayConfigurationCourtRoom[vipDisplayCourtRoomCol.size()];
            vipDisplayCourtRoomCol.toArray(courtRoomArray);
        }

        // create a composite value of display documents,
        // court rooms and unassigned information for the Court site VIP.
        return new VipDisplayConfiguration(displayDocArray, courtRoomArray,
            vipDisplayCourtRoomQuery.isShowUnassignedCases());
    }

    protected XhbCourtRoomDao[] getXhbCourtRoomDaoArray(List<XhbCourtRoomDao> array) {
        return array.toArray(new XhbCourtRoomDao[0]);
    }

    protected RotationSetDdComplexValue getRotationSetDdComplexValue(
        XhbRotationSetDdDao rotationSetDdDao, XhbDisplayDocumentDao displayDocumentDao) {
        return new RotationSetDdComplexValue(rotationSetDdDao, displayDocumentDao);
    }
}
