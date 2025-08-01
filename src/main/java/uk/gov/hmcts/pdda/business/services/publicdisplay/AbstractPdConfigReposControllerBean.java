package uk.gov.hmcts.pdda.business.services.publicdisplay;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.AbstractControllerBean;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcppformatting.XhbCppFormattingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplay.XhbDisplayRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaydocument.XhbDisplayDocumentRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaylocation.XhbDisplayLocationRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaytype.XhbDisplayTypeRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsetdd.XhbRotationSetDdRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsets.XhbRotationSetsRepository;
import uk.gov.hmcts.pdda.business.services.publicdisplay.database.query.VipDisplayCourtRoomQuery;
import uk.gov.hmcts.pdda.business.services.publicdisplay.database.query.VipDisplayDocumentQuery;
import uk.gov.hmcts.pdda.common.publicdisplay.jms.PublicDisplayNotifier;


@SuppressWarnings({"PMD.ExcessiveParameterList", "PMD.TooManyMethods", "PMD.NullAssignment"})
public abstract class AbstractPdConfigReposControllerBean extends AbstractControllerBean {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractPdConfigReposControllerBean.class);

    private PublicDisplayNotifier publicDisplayNotifier;
    private XhbCourtSiteRepository xhbCourtSiteRepository;
    private XhbCourtRoomRepository xhbCourtRoomRepository;
    private XhbDisplayRepository xhbDisplayRepository;
    private XhbRotationSetsRepository xhbRotationSetsRepository;
    private XhbRotationSetDdRepository xhbRotationSetDdRepository;
    private XhbDisplayDocumentRepository xhbDisplayDocumentRepository;
    private XhbDisplayTypeRepository xhbDisplayTypeRepository;
    private XhbDisplayLocationRepository xhbDisplayLocationRepository;
    private DisplayRotationSetDataHelper displayRotationSetDataHelper;
    private VipDisplayDocumentQuery vipDisplayDocumentQuery;
    private VipDisplayCourtRoomQuery vipDisplayCourtRoomQuery;
    private VipCourtRoomsQuery vipCourtRoomsQuery;

    protected AbstractPdConfigReposControllerBean() {
        super();
    }

    protected AbstractPdConfigReposControllerBean(EntityManager entityManager) {
        super(entityManager);
    }

    protected AbstractPdConfigReposControllerBean(EntityManager entityManager, XhbClobRepository xhbClobRepository,
        XhbCourtRepository xhbCourtRepository, XhbConfigPropRepository xhbConfigPropRepository,
        XhbCppFormattingRepository xhbCppFormattingRepository, XhbRotationSetsRepository xhbRotationSetsRepository,
        XhbRotationSetDdRepository xhbRotationSetDdRepository, XhbDisplayTypeRepository xhbDisplayTypeRepository,
        XhbDisplayRepository xhbDisplayRepository, XhbDisplayLocationRepository xhbDisplayLocationRepository,
        XhbCourtSiteRepository xhbCourtSiteRepository, XhbCourtRoomRepository xhbCourtRoomRepository,
        PublicDisplayNotifier publicDisplayNotifier, VipDisplayDocumentQuery vipDisplayDocumentQuery,
        VipDisplayCourtRoomQuery vipDisplayCourtRoomQuery) {
        super(entityManager, xhbClobRepository, null, xhbCourtRepository, xhbConfigPropRepository,
            xhbCppFormattingRepository);
        this.xhbRotationSetsRepository = xhbRotationSetsRepository;
        this.xhbRotationSetDdRepository = xhbRotationSetDdRepository;
        this.xhbDisplayTypeRepository = xhbDisplayTypeRepository;
        this.xhbDisplayDocumentRepository = null;
        this.xhbDisplayRepository = xhbDisplayRepository;
        this.xhbCourtSiteRepository = xhbCourtSiteRepository;
        this.xhbCourtRoomRepository = xhbCourtRoomRepository;
        this.xhbDisplayLocationRepository = xhbDisplayLocationRepository;
        this.publicDisplayNotifier = publicDisplayNotifier;
        this.vipDisplayDocumentQuery = vipDisplayDocumentQuery;
        this.vipDisplayCourtRoomQuery = vipDisplayCourtRoomQuery;
    }

    @Override
    protected void clearRepositories() {
        LOG.info("clearRepositories()");
        xhbRotationSetsRepository = null;
        xhbRotationSetDdRepository = null;
        xhbDisplayTypeRepository = null;
        xhbDisplayRepository = null;
        xhbCourtSiteRepository = null;
        xhbCourtRoomRepository = null;
        xhbDisplayLocationRepository = null;
        xhbDisplayDocumentRepository = null;
        super.clearRepositories();
    }

    /**
     * Returns the publicDisplayNotifier object, initialising if currently null.

     * @return PublicDisplayNotifier
     */
    protected PublicDisplayNotifier getPublicDisplayNotifier() {
        if (publicDisplayNotifier == null) {
            publicDisplayNotifier = new PublicDisplayNotifier();
        }
        return publicDisplayNotifier;
    }

    /**
     * Returns the xhbRotationSetsRepository object, initialising if currently null.

     * @return XhbRotationSetsRepository
     */
    protected XhbRotationSetsRepository getXhbRotationSetsRepository() {
        if (xhbRotationSetsRepository == null || !isEntityManagerActive()) {
            xhbRotationSetsRepository = new XhbRotationSetsRepository(getEntityManager());
        }
        return xhbRotationSetsRepository;
    }

    /**
     * Returns the xhbRotationSetDdRepository object, initialising if currently null.

     * @return XhbRotationSetDdRepository
     */
    protected XhbRotationSetDdRepository getXhbRotationSetDdRepository() {
        if (xhbRotationSetDdRepository == null || !isEntityManagerActive()) {
            xhbRotationSetDdRepository = new XhbRotationSetDdRepository(getEntityManager());
        }
        return xhbRotationSetDdRepository;
    }

    /**
     * Returns the xhbDisplayDocumentRepository object, initialising if currently null.

     * @return XhbDisplayDocumentRepository
     */
    protected XhbDisplayDocumentRepository getXhbDisplayDocumentRepository() {
        if (xhbDisplayDocumentRepository == null || !isEntityManagerActive()) {
            xhbDisplayDocumentRepository = new XhbDisplayDocumentRepository(getEntityManager());
        }
        return xhbDisplayDocumentRepository;
    }

    /**
     * Returns the xhbDisplayTypeRepository object, initialising if currently null.

     * @return XhbDisplayTypeRepository
     */
    protected XhbDisplayTypeRepository getXhbDisplayTypeRepository() {
        if (xhbDisplayTypeRepository == null || !isEntityManagerActive()) {
            xhbDisplayTypeRepository = new XhbDisplayTypeRepository(getEntityManager());
        }
        return xhbDisplayTypeRepository;
    }

    /**
     * Returns the xhbDisplayRepository object, initialising if currently null.

     * @return XhbDisplayRepository
     */
    protected XhbDisplayRepository getXhbDisplayRepository() {
        if (xhbDisplayRepository == null || !isEntityManagerActive()) {
            xhbDisplayRepository = new XhbDisplayRepository(getEntityManager());
        }
        return xhbDisplayRepository;
    }

    /**
     * Returns the xhbCourtSiteRepository object, initialising if currently null.

     * @return XhbCourtSiteRepository
     */
    protected XhbCourtSiteRepository getXhbCourtSiteRepository() {
        if (xhbCourtSiteRepository == null || !isEntityManagerActive()) {
            xhbCourtSiteRepository = new XhbCourtSiteRepository(getEntityManager());
        }
        return xhbCourtSiteRepository;
    }

    /**
     * Returns the xhbCourtRoomRepository object, initialising if currently null.

     * @return XhbCourtRoomRepository
     */
    protected XhbCourtRoomRepository getXhbCourtRoomRepository() {
        if (xhbCourtRoomRepository == null || !isEntityManagerActive()) {
            xhbCourtRoomRepository = new XhbCourtRoomRepository(getEntityManager());
        }
        return xhbCourtRoomRepository;
    }

    /**
     * Returns the xhbDisplayLocationRepository object, initialising if currently null.

     * @return XhbDisplayLocationRepository
     */
    protected XhbDisplayLocationRepository getXhbDisplayLocationRepository() {
        if (xhbDisplayLocationRepository == null || !isEntityManagerActive()) {
            xhbDisplayLocationRepository = new XhbDisplayLocationRepository(getEntityManager());
        }
        return xhbDisplayLocationRepository;
    }

    /**
     * Returns the displayRotationSetDataHelper object, initialising if currently null.

     * @return DisplayRotationSetDataHelper
     */
    protected DisplayRotationSetDataHelper getDisplayRotationSetDataHelper() {
        if (displayRotationSetDataHelper == null) {
            displayRotationSetDataHelper = new DisplayRotationSetDataHelper();
        }
        return displayRotationSetDataHelper;
    }

    protected VipDisplayDocumentQuery getVipDisplayDocumentQuery() {
        if (vipDisplayDocumentQuery == null) {
            return new VipDisplayDocumentQuery(getEntityManager());
        }
        return vipDisplayDocumentQuery;
    }

    protected VipDisplayCourtRoomQuery getVipDisplayCourtRoomQuery() {
        if (vipDisplayCourtRoomQuery == null) {
            return new VipDisplayCourtRoomQuery(getEntityManager());
        }
        return vipDisplayCourtRoomQuery;
    }

    protected VipCourtRoomsQuery getVipCourtRoomsQuery(boolean multiSite) {
        if (vipCourtRoomsQuery == null) {
            return new VipCourtRoomsQuery(getEntityManager(), multiSite);
        }
        return vipCourtRoomsQuery;
    }
}
