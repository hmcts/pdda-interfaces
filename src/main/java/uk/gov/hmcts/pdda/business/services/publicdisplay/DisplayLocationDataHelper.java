package uk.gov.hmcts.pdda.business.services.publicdisplay;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.framework.services.config.ConfigServicesImpl;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplay.XhbDisplayDao;
import uk.gov.hmcts.pdda.business.entities.xhbdisplay.XhbDisplayRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaydocument.XhbDisplayDocumentDao;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaylocation.XhbDisplayLocationDao;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsetdd.XhbRotationSetDdDao;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsets.XhbRotationSetsDao;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsets.XhbRotationSetsRepository;
import uk.gov.hmcts.pdda.common.publicdisplay.util.StringUtilities;
import uk.gov.hmcts.pdda.common.publicdisplay.vos.publicdisplay.CourtSitePdComplexValue;
import uk.gov.hmcts.pdda.common.publicdisplay.vos.publicdisplay.DisplayBasicValueSortAdapter;
import uk.gov.hmcts.pdda.common.publicdisplay.vos.publicdisplay.DisplayLocationComplexValue;
import uk.gov.hmcts.pdda.common.publicdisplay.vos.publicdisplay.RotationSetComplexValue;
import uk.gov.hmcts.pdda.common.publicdisplay.vos.publicdisplay.RotationSetDdComplexValue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * <p>
 * Title: Display Location Helper.
 * </p>
 * <p>
 * Description: Helper methods to get relevant information regarding locations and displays
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: EDS
 * </p>
 * 
 * @author Rakesh Lakhani
 * @version $Id: DisplayLocationDataHelper.java,v 1.9 2005/11/17 10:55:46 bzjrnl Exp $
 */

public class DisplayLocationDataHelper {
    private static final String PRE_DISPLAY = "pd.displaydescription.";

    private static final String PUBLICDISPLAYCONFIGURATION =
        "XHIBITPublicDisplayConfigurationResources";

    private static final Logger LOG = LoggerFactory.getLogger(DisplayLocationDataHelper.class);

    protected DisplayLocationDataHelper() {
        // Protected constructor
    }

    /**
     * Returns the Court Sites, Locations within the Site and Displays within the Site.
     * 
     * @param courtId The court being maintained
     * @return Details of site, location and screen within a court
     */
    public static CourtSitePdComplexValue[] getDisplaysForCourt(final Integer courtId,
        final EntityManager entityManager) {
        XhbCourtSiteRepository xhbCourtSiteRepository = new XhbCourtSiteRepository(entityManager);
        return getDisplaysForCourt(courtId, xhbCourtSiteRepository);
    }

    public static CourtSitePdComplexValue[] getDisplaysForCourt(final Integer courtId,
        final XhbCourtSiteRepository xhbCourtSiteRepository) {
        LOG.debug("getDisplaysForCourt called for courtId {}", courtId);
        ArrayList<CourtSitePdComplexValue> results = new ArrayList<>();

        CourtSitePdComplexValue sitePdComplex;
        List<XhbCourtSiteDao> siteCol = xhbCourtSiteRepository.findByCourtId(courtId);
        Iterator<XhbCourtSiteDao> siteIter = siteCol.iterator();
        while (siteIter.hasNext()) {
            // Create an instance of the court site complex value
            sitePdComplex = getCourtSitePdComplexValue();

            XhbCourtSiteDao siteLocal = siteIter.next();

            // Set the court site data
            sitePdComplex.setCourtSiteDao(siteLocal);

            // Add display locations
            addDisplayLocation(sitePdComplex, siteLocal);

            results.add(sitePdComplex);
        }

        LOG.debug("getDisplaysForCourt exitted");
        return results.toArray(new CourtSitePdComplexValue[0]);
    }
    
    private static CourtSitePdComplexValue getCourtSitePdComplexValue() {
        return new CourtSitePdComplexValue();
    }

    /**
     * The Rotation sets, pages within each set and the screens the rotation sets are assigned to
     * are returned.
     */
    public static RotationSetComplexValue[] getRotationSetsDetailForCourt(final Integer courtId,
        final Locale locale, final EntityManager entityManager) {
        ResourceBundle rb =
            ConfigServicesImpl.getInstance().getBundle(PUBLICDISPLAYCONFIGURATION, locale);
        XhbRotationSetsRepository xhbRotationSetsRepository =
            new XhbRotationSetsRepository(entityManager);
        XhbDisplayRepository xhbDisplayRepository = new XhbDisplayRepository(entityManager);
        return getRotationSetsDetailForCourt(courtId, rb, xhbRotationSetsRepository,
            xhbDisplayRepository);
    }

    public static RotationSetComplexValue[] getRotationSetsDetailForCourt(final Integer courtId,
        final ResourceBundle rb, final XhbRotationSetsRepository xhbRotationSetsRepository,
        final XhbDisplayRepository xhbDisplayRepository) {

        LOG.debug("getRotationSetsDetailForCourt called for courtId {}", courtId);
        List<RotationSetComplexValue> results = new ArrayList<>();

        RotationSetComplexValue complex;

        // Get all the rotation sets for the court
        List<XhbRotationSetsDao> rotationSetCol = xhbRotationSetsRepository.findByCourtId(courtId);
        Iterator<XhbRotationSetsDao> rotationSetIter = rotationSetCol.iterator();
        while (rotationSetIter.hasNext()) {
            complex = getRotationSetComplexValue();

            XhbRotationSetsDao rotationSetLocal = rotationSetIter.next();

            // Set the rotation set data
            complex.setRotationSetDao(rotationSetLocal);
            // Set the display data
            complex.setDisplayDaos(getDisplayAdapters(rb, rotationSetLocal, xhbDisplayRepository));

            // Add the rotation set dds for this rotation set
            addRotationSetDd(complex, rotationSetLocal);

            // Add the rotation set to the list
            results.add(complex);
        }

        LOG.debug("getRotationSetsDetailForCourt exitted");
        // Return the list of rotation sets
        return results.toArray(new RotationSetComplexValue[0]);
    }
    
    private static RotationSetComplexValue getRotationSetComplexValue() {
        return new RotationSetComplexValue();
    }

    private static DisplayBasicValueSortAdapter[] getDisplayAdapters(final ResourceBundle rb,
        final XhbRotationSetsDao rotationSetLocal,
        final XhbDisplayRepository xhbDisplayRepository) {
        LOG.debug("getDisplayAdapters({},{},{})", rb, rotationSetLocal, xhbDisplayRepository);
        // Wrap the XhbDisplays in the sort adapter. getting the display text
        // from the resource bundle.
        XhbDisplayDao[] displays = xhbDisplayRepository
            .findByRotationSetId(rotationSetLocal.getRotationSetId()).toArray(new XhbDisplayDao[0]);

        List<DisplayBasicValueSortAdapter> displayAdapterArray = new ArrayList<>();
        for (XhbDisplayDao display : displays) {
            String displayText;
            try {
                displayText = rb.getString(PRE_DISPLAY.concat(display.getDescriptionCode()));
            } catch (MissingResourceException ex) {
                String key = display.getDescriptionCode();
                String temp = key.substring(key.lastIndexOf('.') + 1);
                temp = temp.replace('_', ' ');
                displayText = StringUtilities.toSentenceCase(temp);
            }
            displayAdapterArray.add(new DisplayBasicValueSortAdapter(display, displayText));
        }
        return displayAdapterArray
            .toArray(new DisplayBasicValueSortAdapter[0]);
    }

    /**
     * Adds display locations.
     * 
     * @param sitePdComplex CourtSitePDComplexValue
     * @param siteLocal XhbCourtSiteDao
     */
    private static void addDisplayLocation(final CourtSitePdComplexValue sitePdComplex,
        final XhbCourtSiteDao siteLocal) {
        LOG.debug("addDisplayLocation called");
        DisplayLocationComplexValue displayLocationComplex;

        // get the locations
        Iterator<XhbDisplayLocationDao> iterLocation =
            siteLocal.getXhbDisplayLocations().iterator();

        while (iterLocation.hasNext()) {
            // for every location, find the displays and
            // create a complex VO
            displayLocationComplex = getDisplayLocationComplexValue();

            XhbDisplayLocationDao locationLocal = iterLocation.next();

            displayLocationComplex.setDisplayLocationDao(locationLocal);
            displayLocationComplex
                .setDisplayDaos(getXhbDisplayDaoArray(locationLocal));

            sitePdComplex.addDisplayLocationComplexValue(displayLocationComplex);
        }
        LOG.debug("addDisplayLocation exitted");
    }
    
    private static DisplayLocationComplexValue getDisplayLocationComplexValue() {
        return new DisplayLocationComplexValue();
    }
    
    private static XhbDisplayDao[] getXhbDisplayDaoArray(XhbDisplayLocationDao locationLocal) {
        return locationLocal.getXhbDisplays().toArray(new XhbDisplayDao[0]);
    }

    /**
     * Adds rotation set dd to the rotation set.
     * 
     * @param complex RotationSetComplexValue
     * @param rotationSetLocal XhbRotationSetsDao
     */
    private static void addRotationSetDd(final RotationSetComplexValue complex,
        final XhbRotationSetsDao rotationSetLocal) {
        LOG.debug("addRotationSetDd called");
        RotationSetDdComplexValue ddComplex;

        // Get the rotation set dds for the current rotation set
        Iterator<XhbRotationSetDdDao> rotationSetDdIter =
            rotationSetLocal.getXhbRotationSetDds().iterator();

        while (rotationSetDdIter.hasNext()) {
            // for every rotation set DD, get the display document and
            // create a complex VO
            XhbRotationSetDdDao rotationSetDdLocal = rotationSetDdIter.next();
            ddComplex = getRotationSetDdComplexValue(rotationSetDdLocal,
                rotationSetDdLocal.getXhbDisplayDocument());
            complex.addRotationSetDdComplexValue(ddComplex);
        }
        LOG.debug("addRotationSetDd exitted");
    }
    
    private static RotationSetDdComplexValue getRotationSetDdComplexValue(XhbRotationSetDdDao rotationSetDdDao,
        XhbDisplayDocumentDao displayDocumentDao) {
        return new RotationSetDdComplexValue(rotationSetDdDao,displayDocumentDao);
    }
}
