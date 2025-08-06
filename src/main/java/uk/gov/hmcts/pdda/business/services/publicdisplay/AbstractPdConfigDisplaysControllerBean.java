package uk.gov.hmcts.pdda.business.services.publicdisplay;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcppformatting.XhbCppFormattingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplay.XhbDisplayRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaydocument.XhbDisplayDocumentDao;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaylocation.XhbDisplayLocationRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaytype.XhbDisplayTypeRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsetdd.XhbRotationSetDdRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsets.XhbRotationSetsDao;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsets.XhbRotationSetsRepository;
import uk.gov.hmcts.pdda.business.services.publicdisplay.database.query.VipDisplayCourtRoomQuery;
import uk.gov.hmcts.pdda.business.services.publicdisplay.database.query.VipDisplayDocumentQuery;
import uk.gov.hmcts.pdda.business.services.publicdisplay.exceptions.PublicDisplayCheckedException;
import uk.gov.hmcts.pdda.common.publicdisplay.jms.PublicDisplayNotifier;
import uk.gov.hmcts.pdda.common.publicdisplay.vos.publicdisplay.CourtSitePdComplexValue;
import uk.gov.hmcts.pdda.common.publicdisplay.vos.publicdisplay.DisplayConfiguration;
import uk.gov.hmcts.pdda.common.publicdisplay.vos.publicdisplay.RotationSetComplexValue;

import java.util.Locale;


@SuppressWarnings({"PMD.ExcessiveParameterList", "PMD.CouplingBetweenObjects"})
public abstract class AbstractPdConfigDisplaysControllerBean extends AbstractPdConfigReposControllerBean {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractPdConfigDisplaysControllerBean.class);

    protected AbstractPdConfigDisplaysControllerBean() {
        super();
    }

    protected AbstractPdConfigDisplaysControllerBean(EntityManager entityManager) {
        super(entityManager);
    }

    protected AbstractPdConfigDisplaysControllerBean(EntityManager entityManager, XhbClobRepository xhbClobRepository,
        XhbCourtRepository xhbCourtRepository, XhbConfigPropRepository xhbConfigPropRepository,
        XhbCppFormattingRepository xhbCppFormattingRepository, XhbRotationSetsRepository xhbRotationSetsRepository,
        XhbRotationSetDdRepository xhbRotationSetDdRepository, XhbDisplayTypeRepository xhbDisplayTypeRepository,
        XhbDisplayRepository xhbDisplayRepository, XhbDisplayLocationRepository xhbDisplayLocationRepository,
        XhbCourtSiteRepository xhbCourtSiteRepository, XhbCourtRoomRepository xhbCourtRoomRepository,
        PublicDisplayNotifier publicDisplayNotifier, VipDisplayDocumentQuery vipDisplayDocumentQuery,
        VipDisplayCourtRoomQuery vipDisplayCourtRoomQuery) {
        super(entityManager, xhbClobRepository, xhbCourtRepository, xhbConfigPropRepository, xhbCppFormattingRepository,
            xhbRotationSetsRepository, xhbRotationSetDdRepository, xhbDisplayTypeRepository, xhbDisplayRepository,
            xhbDisplayLocationRepository, xhbCourtSiteRepository, xhbCourtRoomRepository, publicDisplayNotifier,
            vipDisplayDocumentQuery, vipDisplayCourtRoomQuery);
    }

    /**
     * Returns the list of display documents.

     * @return All Display Documents
     */
    public XhbDisplayDocumentDao[] getDisplayDocuments() {
        LOG.debug("getDisplayDocuments()");
        return getXhbDisplayDocumentRepository().findAll().toArray(new XhbDisplayDocumentDao[0]);
    }

    /**
     * Updates the display configuration with changes.
     * <p/>
     * Note: sends a DisplayConfigurationChanged JMS configuration message

     * @param displayConfiguration The updated display configuration to be stored
     */
    public void updateDisplayConfiguration(final DisplayConfiguration displayConfiguration) {
        LOG.debug("updateDisplayConfiguration({})", displayConfiguration);
        DisplayConfigurationHelper.updateDisplayConfiguration(displayConfiguration, getPublicDisplayNotifier(),
            getEntityManager());
    }

    /**
     * Returns the Court Sites, Locations within the Site and Displays within the Site.

     * @param courtId The court being maintained
     * @return Details of site, location and screen within a court
     */
    public CourtSitePdComplexValue[] getDisplaysForCourt(final Integer courtId) {
        LOG.debug("getDisplaysForCourt({})", courtId);
        return DisplayLocationDataHelper.getDisplaysForCourt(courtId, getEntityManager());
    }

    /**
     * The Rotation sets, pages within each set and the screens the rotation sets are assigned to are
     * returned.

     * @param courtId The court the display is in.
     * @return Array of objects containing the rotation set, pages and screens rotation set is assigned
     *         to
     */
    public RotationSetComplexValue[] getRotationSetsDetailForCourt(final Integer courtId, final Locale locale) {
        LOG.debug("getRotationSetsDetailForCourt({},{})", courtId, locale);
        return DisplayLocationDataHelper.getRotationSetsDetailForCourt(courtId, locale, getEntityManager());
    }

    /**
     * Returns the list of RotationSets available in the court.

     * @param courtId The court to which the rotation set belongs
     * @return The rotation sets within a court
     */
    public XhbRotationSetsDao[] getRotationSetsForCourt(final Integer courtId) {
        LOG.debug("getRotationSetsForCourt({})", courtId);
        return getXhbRotationSetsRepository().findByCourtId(courtId).toArray(new XhbRotationSetsDao[0]);
    }

    /**
     * Creates a new rotation set with associated documents.

     * @param newRotationSet This object must contain a RotationSetBasic value with court Id populated
     *        and a list of RotationSetDDComplex values with RotationSetDDBasicValues and valid
     *        DisplayDocumentBasicValues
     */
    public void createRotationSets(final RotationSetComplexValue newRotationSet) {
        LOG.debug("createRotationSets({}", newRotationSet);
        RotationSetMaintainHelper.createRotationSets(newRotationSet, getEntityManager());
    }

    /**
     * Delete a rotation set with associated documents Note the rotation set must not be assigned to any
     * displays.

     * @param rotationSet The rotation set to be deleted
     */
    public void deleteRotationSets(final RotationSetComplexValue rotationSet) {
        LOG.debug("deleteRotationSets({})", rotationSet);
        RotationSetMaintainHelper.deleteRotationSet(rotationSet, getEntityManager());
    }

    /**
     * Updates the rotation set with display documents that have been selected.
     * <p/>
     * Note: sends a RotationSet changed JMS configuration message

     * @param rotationSet The rotation set being updated and an array of display documents with ordering
     *        and delay information
     * @throws PublicDisplayCheckedException Thrown if rotation set does not exist in the DB
     */
    public void setDisplayDocumentsForRotationSet(final RotationSetComplexValue rotationSet) {
        LOG.debug("setDisplayDocumentsForRotationSet({})", rotationSet);
        RotationSetMaintainHelper.setDisplayDocumentsForRotationSet(rotationSet, getPublicDisplayNotifier(),
            getEntityManager());
    }

    /**
     * Returns the display, the rotations set assigned and an array of court rooms assigned.

     * @param displayId the display (ie physical screen) being queried
     * @return An object containing details of the display, the rotation set and the courtrooms assigned
     */
    public DisplayConfiguration getDisplayConfiguration(final Integer displayId) {
        LOG.debug("getDisplayConfiguration({})", displayId);
        return DisplayConfigurationHelper.getDisplayConfiguration(displayId, getEntityManager());
    }
}
