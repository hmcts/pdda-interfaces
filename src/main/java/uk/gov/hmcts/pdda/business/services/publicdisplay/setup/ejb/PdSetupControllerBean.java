package uk.gov.hmcts.pdda.business.services.publicdisplay.setup.ejb;

import jakarta.ejb.ApplicationException;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.pdda.business.AbstractControllerBean;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplay.XhbDisplayDao;
import uk.gov.hmcts.pdda.business.entities.xhbdisplay.XhbDisplayRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaylocation.XhbDisplayLocationDao;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaylocation.XhbDisplayLocationRepository;
import uk.gov.hmcts.pdda.common.publicdisplay.setup.drilldown.CourtDrillDown;
import uk.gov.hmcts.pdda.common.publicdisplay.setup.drilldown.CourtSiteDrillDown;
import uk.gov.hmcts.pdda.common.publicdisplay.setup.drilldown.DisplayLocationDrillDown;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.DisplayUri;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Stateless
@Service
@Transactional
@LocalBean
@ApplicationException(rollback = true)
public class PdSetupControllerBean extends AbstractControllerBean implements Serializable {

    private static final long serialVersionUID = -1482124759093214736L;

    private static final Logger LOG = LoggerFactory.getLogger(PdSetupControllerBean.class);

    private static final String ENTERED = " : entered";


    private XhbCourtSiteRepository xhbCourtSiteRepository;

    private XhbDisplayLocationRepository xhbDisplayLocationRepository;

    private XhbDisplayRepository xhbDisplayRepository;

    public PdSetupControllerBean(EntityManager entityManager) {
        super(entityManager);
    }

    public PdSetupControllerBean() {
        super();
    }

    /**
     * Returns a CourtDrillDown object for the court Id supplied.
     * 
     * @param courtId Integer
     * @return CourtDrillDown object
     * @throws uk.gov.hmcts.framework.business.exceptions.CourtNotFoundException Exception
     */
    public CourtDrillDown getDrillDownForCourt(final Integer courtId) {
        String methodName = "getDrillDownForCourt(" + courtId + ") - ";
        LOG.debug(methodName + ENTERED);

        Optional<XhbCourtDao> court = getXhbCourtRepository().findById(courtId);
        if (!court.isPresent()) {
            throw new uk.gov.hmcts.framework.business.exceptions.CourtNotFoundException(courtId);
        }

        ArrayList<XhbCourtSiteDao> xhbCourtSites = (ArrayList<XhbCourtSiteDao>) getXhbCourtSiteRepository()
            .findByCrestCourtIdValue(court.get().getCrestCourtId());

        CourtDrillDown courtDrillDown = new CourtDrillDown(court.get().getDisplayName());
        for (XhbCourtSiteDao xhbCourtSite : xhbCourtSites) {

            ArrayList<XhbDisplayLocationDao> xhbDisplayLocations =
                (ArrayList<XhbDisplayLocationDao>) getXhbDisplayLocationRepository()
                    .findByCourtSite(xhbCourtSite.getCourtSiteId());

            CourtSiteDrillDown courtSiteDrillDown = getCourtSiteDrillDown(xhbCourtSite.getDisplayName());

            for (XhbDisplayLocationDao xhbDisplayLocation : xhbDisplayLocations) {

                ArrayList<XhbDisplayDao> xhbDisplays = (ArrayList<XhbDisplayDao>) getXhbDisplayRepository()
                    .findByDisplayLocationId(xhbDisplayLocation.getDisplayLocationId());

                DisplayLocationDrillDown displayLocationDrillDown =
                    getDisplayLocationDrillDown(xhbDisplayLocation.getDescriptionCode());

                for (XhbDisplayDao xhbDisplay : xhbDisplays) {

                    DisplayUri displayUri = getDisplayUri(court.get().getShortName().toLowerCase(Locale.getDefault()),
                        xhbCourtSite.getCourtSiteCode().toLowerCase(Locale.getDefault()),
                        xhbDisplayLocation.getDescriptionCode().toLowerCase(Locale.getDefault()),
                        xhbDisplay.getDescriptionCode().toLowerCase(Locale.getDefault()));

                    displayLocationDrillDown.addDisplay(displayUri);

                    if (LOG.isDebugEnabled()) {
                        LOG.debug(methodName + "Drilldown added for URI '" + displayUri);
                    }
                }

                courtSiteDrillDown.addCourtRoom(displayLocationDrillDown);

            }

            courtDrillDown.addCourtSite(courtSiteDrillDown);
        }

        return courtDrillDown;
    }
    
    private CourtSiteDrillDown getCourtSiteDrillDown(String courtSiteName) {
        return new CourtSiteDrillDown(courtSiteName);
    }
    
    private DisplayLocationDrillDown getDisplayLocationDrillDown(String descriptionCode) {
        return new DisplayLocationDrillDown(descriptionCode);
    }
    
    private DisplayUri getDisplayUri(final String courthouseName, final String courtsiteCode,
        final String location, final String display) {
        return new DisplayUri(courthouseName, courtsiteCode, location, display);
    }
    
    /**
     * getAllCourts.
     * 
     * @return XhbCourtDaoArray
     */
    public XhbCourtDao[] getAllCourts() {
        String methodName = "getAllCourts() - ";
        LOG.debug(methodName + ENTERED);

        List<XhbCourtDao> courts = getXhbCourtRepository().findAll();
        courts.sort(Comparator.comparing(XhbCourtDao::getCourtName));
        return courts.toArray(new XhbCourtDao[0]);
    }

    /**
     * Returns the xhbCourtSiteRepository object, initialising if currently null.
     * 
     * @return XhbCourtSiteRepository
     */
    private XhbCourtSiteRepository getXhbCourtSiteRepository() {
        if (xhbCourtSiteRepository == null) {
            xhbCourtSiteRepository = new XhbCourtSiteRepository(getEntityManager());
        }
        return xhbCourtSiteRepository;
    }

    /**
     * Returns the xhbDisplayLocationRepository object, initialising if currently null.
     * 
     * @return XhbDisplayLocationRepository
     */
    private XhbDisplayLocationRepository getXhbDisplayLocationRepository() {
        if (xhbDisplayLocationRepository == null) {
            xhbDisplayLocationRepository = new XhbDisplayLocationRepository(getEntityManager());
        }
        return xhbDisplayLocationRepository;
    }

    /**
     * Returns the xhbDisplayLocationRepository object, initialising if currently null.
     * 
     * @return XhbDisplayLocationRepository
     */
    private XhbDisplayRepository getXhbDisplayRepository() {
        if (xhbDisplayRepository == null) {
            xhbDisplayRepository = new XhbDisplayRepository(getEntityManager());
        }
        return xhbDisplayRepository;
    }
}
