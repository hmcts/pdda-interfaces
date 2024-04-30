package uk.gov.hmcts.pdda.business.services.publicdisplay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.framework.exception.CsUnrecoverableException;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplay.XhbDisplayDao;
import uk.gov.hmcts.pdda.business.entities.xhbdisplay.XhbDisplayRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaydocument.XhbDisplayDocumentDao;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaydocument.XhbDisplayDocumentRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaylocation.XhbDisplayLocationDao;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaylocation.XhbDisplayLocationRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaytype.XhbDisplayTypeDao;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaytype.XhbDisplayTypeRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsetdd.XhbRotationSetDdDao;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsetdd.XhbRotationSetDdRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsets.XhbRotationSetsDao;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsets.XhbRotationSetsRepository;
import uk.gov.hmcts.pdda.common.publicdisplay.types.document.DisplayDocumentType;
import uk.gov.hmcts.pdda.common.publicdisplay.types.document.DisplayDocumentTypeUtils;
import uk.gov.hmcts.pdda.common.publicdisplay.types.rotationset.DisplayRotationSetData;
import uk.gov.hmcts.pdda.common.publicdisplay.types.rotationset.RotationSetDisplayDocument;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.DisplayDocumentUri;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.DisplayUri;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * <p>
 * Title: Helper class used in the mapping of database held data about Displays and Rotation Sets to
 * datatypes usable in the presentation tier.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: EDS
 * </p>
 * 
 * @author Bob Boothby
 * @version 1.0
 */

public class DisplayRotationSetDataHelper extends CsUnrecoverableException {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(DisplayRotationSetDataHelper.class);

    // Default constructor
    DisplayRotationSetDataHelper() {
        super();
    }

    /**
     * Utility method that should be part of the general Sun defined APIs but implemented here for a
     * specific use.
     * 
     * @param array The array to add to the list.
     * @param list The list to add the array to.
     */
    private void addArrayToList(DisplayRotationSetData[] array, List<DisplayRotationSetData> list) {
        for (DisplayRotationSetData displayRotationSetData : array) {
            list.add(displayRotationSetData);
        }
    }

    /**
     * Utility method that gets all the public display configuration data for a court.
     * 
     * @param court The entity bean representing the court.
     * @param xhbDisplayrepository xhbDisplayrepository.
     * @param xhbRotationSetsRepository xhbRotationSetsRepository.
     * @param xhbRotationSetDdRepository xhbRotationSetDdRepository.
     * @param xhbDisplayDocumentRepository xhbDisplayDocumentRepository.
     * @param xhbDisplayTypeRepository xhbDisplayTypeRepository.
     * @param xhbDisplayLocationRepository xhbDisplayLocationRepository.
     * @param xhbCourtSiteRepository xhbCourtSiteRepository
     * @param xhbCourtRoomRepository xhbCourtRoomRepository.
     * @return The full set of public display configuration data for the court.
     */
    public DisplayRotationSetData[] getDataForCourt(XhbCourtDao court, XhbDisplayRepository xhbDisplayrepository,
        XhbRotationSetsRepository xhbRotationSetsRepository, XhbRotationSetDdRepository xhbRotationSetDdRepository,
        XhbDisplayDocumentRepository xhbDisplayDocumentRepository, XhbDisplayTypeRepository xhbDisplayTypeRepository,
        XhbDisplayLocationRepository xhbDisplayLocationRepository, XhbCourtSiteRepository xhbCourtSiteRepository,
        XhbCourtRoomRepository xhbCourtRoomRepository) {
        // Get all court-associated rotation sets.
        List<XhbRotationSetsDao> rotationSetsForCourt = xhbRotationSetsRepository.findByCourtId(court.getCourtId());
        List<DisplayRotationSetData> displayRotationSetDataList = new ArrayList<>();
        if (rotationSetsForCourt != null) {

            // Aggregate the DisplayRotationSetData for each rotation set.
            Iterator<XhbRotationSetsDao> rotationSetIterator = rotationSetsForCourt.iterator();
            while (rotationSetIterator.hasNext()) {
                XhbRotationSetsDao xrs = rotationSetIterator.next();
                List<XhbDisplayDao> xhbDisplays = xhbDisplayrepository.findByRotationSetId(xrs.getRotationSetId());
                addArrayToList(getDataForDisplayRotationSets(court, xrs, xhbDisplays, xhbRotationSetDdRepository,
                    xhbDisplayDocumentRepository, xhbDisplayTypeRepository, xhbDisplayLocationRepository,
                    xhbCourtSiteRepository, xhbCourtRoomRepository), displayRotationSetDataList);
            }
        }

        // Turn the list into a strongly typed array and return.
        DisplayRotationSetData[] returnArray = new DisplayRotationSetData[displayRotationSetDataList.size()];
        displayRotationSetDataList.toArray(returnArray);
        return returnArray;
    }

    /**
     * Utility method that gets the all the Display Rotation Set information for a given Rotation Set.
     * 
     * @param court The court to which the rotation set belongs.
     * @param rotationSet The rotation set to retrieve the data for.
     * @param xhbDisplays The displays for the rotation set.
     * @param xhbRotationSetDdRepository xhbRotationSetDdRepository.
     * @param xhbDisplayDocumentRepository xhbDisplayDocumentRepository.
     * @param xhbDisplayTypeRepository xhbDisplayTypeRepository.
     * @param xhbDisplayLocationRepository xhbDisplayLocationRepository.
     * @param xhbCourtSiteRepository xhbCourtSiteRepository
     * @param xhbCourtRoomRepository xhbCourtRoomRepository.
     * @return An array of type <code>DisplayRotationSetData</code>.
     */
    public DisplayRotationSetData[] getDataForDisplayRotationSets(XhbCourtDao court, XhbRotationSetsDao rotationSet,
        List<XhbDisplayDao> xhbDisplays, XhbRotationSetDdRepository xhbRotationSetDdRepository,
        XhbDisplayDocumentRepository xhbDisplayDocumentRepository, XhbDisplayTypeRepository xhbDisplayTypeRepository,
        XhbDisplayLocationRepository xhbDisplayLocationRepository, XhbCourtSiteRepository xhbCourtSiteRepository,
        XhbCourtRoomRepository xhbCourtRoomRepository) {
        int numberOfDisplays = xhbDisplays.size();

        // Short circuit unnecessary code here.
        if (numberOfDisplays == 0) {
            return new DisplayRotationSetData[0];
        }
        // Set up the return array and populate it.
        DisplayRotationSetData[] returnArray = new DisplayRotationSetData[numberOfDisplays];
        Iterator<XhbDisplayDao> rotationSetDisplayIterator = xhbDisplays.iterator();
        for (int i = 0; rotationSetDisplayIterator.hasNext(); i++) {
            XhbDisplayDao display = rotationSetDisplayIterator.next();
            returnArray[i] = getDisplayRotationSetData(court, display, rotationSet, xhbRotationSetDdRepository,
                xhbDisplayDocumentRepository, xhbDisplayTypeRepository, xhbDisplayLocationRepository,
                xhbCourtSiteRepository, xhbCourtRoomRepository);
        }
        return returnArray;
    }

    /**
     * Utility method that gets the Display Rotation Set information for a given Rotation Set and
     * Display.
     * 
     * @param court The court to which the rotation set belongs.
     * @param display the display entity from which to retrieve the data.
     * @param rotationSet The rotation set entity to retrieve the data for.
     * @param xhbRotationSetDdRepository xhbRotationSetDdRepository.
     * @param xhbDisplayDocumentRepository xhbDisplayDocumentRepository.
     * @param xhbDisplayTypeRepository xhbDisplayTypeRepository.
     * @param xhbDisplayLocationRepository xhbDisplayLocationRepository.
     * @param xhbCourtSiteRepository xhbCourtSiteRepository
     * @param xhbCourtRoomRepository xhbCourtRoomRepository.
     * @return An instance of DisplayRotationSetData.
     */
    public DisplayRotationSetData getDisplayRotationSetData(XhbCourtDao court, XhbDisplayDao display,
        XhbRotationSetsDao rotationSet, XhbRotationSetDdRepository xhbRotationSetDdRepository,
        XhbDisplayDocumentRepository xhbDisplayDocumentRepository, XhbDisplayTypeRepository xhbDisplayTypeRepository,
        XhbDisplayLocationRepository xhbDisplayLocationRepository, XhbCourtSiteRepository xhbCourtSiteRepository,
        XhbCourtRoomRepository xhbCourtRoomRepository) {

        Optional<XhbDisplayLocationDao> xhbDisplayLocation =
            xhbDisplayLocationRepository.findById(display.getDisplayLocationId());
        Optional<XhbCourtSiteDao> xhbCourtSiteDao =
            xhbCourtSiteRepository.findById(xhbDisplayLocation.get().getCourtSiteId());
        List<XhbCourtRoomDao> xhbCourtRooms = xhbCourtRoomRepository.findByDisplayId(display.getDisplayId());

        // Construct the URI representing the Display.
        DisplayUri displayUri =
            getDisplayUri(display, court.getShortName(), xhbDisplayLocation.get(), xhbCourtSiteDao.get());
        int[] courtRoomIds = getCourtRoomIds(xhbCourtRooms, display);

        List<XhbRotationSetDdDao> xhbRotationSetDds =
            xhbRotationSetDdRepository.findByRotationSetId(rotationSet.getRotationSetId());
        // Get the RotationSetDisplayDocument[] that will
        // represent the rotation set for the given display.
        RotationSetDisplayDocument[] rotationSetDDs = getDisplayRotationSetElements(court.getCourtId(),
            xhbDisplayDocumentRepository, xhbRotationSetDds, courtRoomIds);
        Optional<XhbDisplayTypeDao> displayTypeDao = xhbDisplayTypeRepository.findById(display.getDisplayTypeId());

        // Construct the return object.
        return new DisplayRotationSetData(displayUri, rotationSetDDs, display.getDisplayId().intValue(),
            rotationSet.getRotationSetId(), displayTypeDao.get().getDescriptionCode());
    }

    /**
     * Construct an instance of DisplayURI from a <code>XhbDisplay</code> entity bean.
     * 
     * @param display The entity bean representing the display.
     * @param courtShortName court shortName.
     * @param xhbDisplayLocation xhbDisplayLocation.
     * @param courtSite court site.
     * @return A DisplayURI representing the display from the entity.
     */
    private DisplayUri getDisplayUri(XhbDisplayDao display, String courtShortName,
        XhbDisplayLocationDao xhbDisplayLocation, XhbCourtSiteDao courtSite) {
        return new DisplayUri(formatString(courtShortName), formatString(courtSite.getCourtSiteCode()),
            xhbDisplayLocation.getDescriptionCode(), display.getDescriptionCode());
    }

    /**
     * Formats a string for inclusion in an URI. It replaces spaces with underscores and turns it to
     * lower case.
     * 
     * @param toBeFormatted String
     * @return String
     */
    private String formatString(String toBeFormatted) {
        return toBeFormatted.replace(' ', '_').toLowerCase(Locale.getDefault());
    }

    /**
     * Gets a sorted int array of court room IDs from the passed in collection of
     * <code>XhbCourtRoom</code> entity beans. This method will include the unassigned court room id
     * from <code>DisplayDocumentURI</code> if the SHOW_UNASSIGNED_YN field is set on the underlying
     * display entity.
     * 
     * @param courtRooms The collection of court rooms.
     * @param display The display for which to get the array of court rooms, determines whether to use
     *        the unassigned court room.
     * @return The court rooms IDs as an int[].
     * @see uk.gov.hmcts.pdda.common.publicdisplay.types.uri.DisplayDocumentUri
     */
    private int[] getCourtRoomIds(List<XhbCourtRoomDao> courtRooms, XhbDisplayDao display) {
        // Sort the court rooms...
        XhbCourtRoomDao[] courtRoomArray = new XhbCourtRoomDao[courtRooms.size()];
        courtRooms.toArray(courtRoomArray);
        Arrays.sort(courtRoomArray, CourtRoomComparator.getInstance());
        int[] courtRoomIds;
        if ("Y".equals(display.getShowUnassignedYn())) { // Add the unassigned courtroom.
            courtRoomIds = new int[courtRoomArray.length + 1];
            // Get the court room IDs.
            for (int i = courtRoomArray.length - 1; i >= 0; i--) {
                courtRoomIds[i] = courtRoomArray[i].getCourtRoomId().intValue();
            }
            // Add the unassigned court room id to the last element of the
            // array.
            courtRoomIds[courtRoomArray.length] = DisplayDocumentUri.UNASSIGNED;
        } else {
            // We don't care about the unassigned court room.
            courtRoomIds = new int[courtRoomArray.length];
            // Get the court room IDs.
            for (int i = courtRoomArray.length - 1; i >= 0; i--) {
                courtRoomIds[i] = courtRoomArray[i].getCourtRoomId().intValue();
            }
        }
        return courtRoomIds;
    }

    /**
     * This method gathers and sorts the Display Document elements that go to make up Display Rotation
     * Set, resulting in an array of type RotationSetDisplayDocument that represents the Display
     * Documents in a rotation Set.
     *
     * @param courtId The court for to which the RotationSetDisplayDocument belong.
     * @param xhbDisplayDocumentRepository xhbDisplayDocumentRepository.
     * @param rotationSetDisplayDocuments The XhbRotationSetDdDAO entity beans that provide data in the
     *        construction of the return.
     * @param courtRoomIds The court room IDs for which the RotationSetDisplayDocument are to be
     *        constructed.
     * @return A fully instantiated array of RotationSetDisplayDocument representing all Display
     *         Documents that make up a Display Rotation Set.
     */
    private RotationSetDisplayDocument[] getDisplayRotationSetElements(int courtId,
        XhbDisplayDocumentRepository xhbDisplayDocumentRepository,
        Collection<XhbRotationSetDdDao> rotationSetDisplayDocuments, int... courtRoomIds) {
        // First sort the rotation set display documents
        XhbRotationSetDdDao[] rotationSetDdArray = new XhbRotationSetDdDao[rotationSetDisplayDocuments.size()];
        rotationSetDisplayDocuments.toArray(rotationSetDdArray);
        Arrays.sort(rotationSetDdArray, RotationSetDdComparator.getInstance());

        // Create the List that will store the result.
        List<RotationSetDisplayDocument> result = new ArrayList<>();

        // Step over the RotationSetDisplayDocuments.
        for (XhbRotationSetDdDao rotationSetDd : rotationSetDdArray) {
            Optional<XhbDisplayDocumentDao> xhbDisplayDocument =
                xhbDisplayDocumentRepository.findById(rotationSetDd.getDisplayDocumentId());
            XhbDisplayDocumentDao xhbDisplayDocumentDao =
                xhbDisplayDocument.isPresent() ? xhbDisplayDocument.get() : null;
            // Add the one or many RotationSetDisplayDocuments
            result.addAll(getRotationSetDdsForDisplayDocument(courtId, rotationSetDd.getPageDelay().intValue(),
                xhbDisplayDocumentDao, courtRoomIds));
        }

        // Turn the List into the appropriate array type.
        RotationSetDisplayDocument[] returnArray = new RotationSetDisplayDocument[result.size()];
        result.toArray(returnArray);
        return returnArray;
    }

    /**
     * Synthesise a <code>List</code> of <code>RotationSetDisplayDocument</code> from the passed in
     * parameters. The list will either consist of one entry if the Display Document handles multiple
     * court rooms or as many entries as there are courtRoomIds if the DisplayDocument only handles one
     * court room at a time. It is important to note that for now no URI will be returned for the
     * combination of non-multiple court documents and the unassigned court.
     * 
     * @param courtId The ID of the court to which the <code>RotationSetDisplayDocument</code>s belong.
     * @param pageDelay The time for which a page of the DisplayDocument is to appear on the public
     *        display.
     * @param displayDocument The Entity Bean representing the Display Document.
     * @param courtRoomIds The court rooms for which the Rotation Set Display Documents will be
     *        generated.
     * @return list of RotationSetDisplayDocument
     */
    private List<RotationSetDisplayDocument> getRotationSetDdsForDisplayDocument(int courtId, int pageDelay,
        XhbDisplayDocumentDao displayDocument, int... courtRoomIds) {

        Locale documentLocale = createLocale(displayDocument.getLanguage(), displayDocument.getCountry());

        // Get the type of the display document.
        DisplayDocumentType type = DisplayDocumentTypeUtils.getDisplayDocumentType(displayDocument.getDescriptionCode(),
            displayDocument.getLanguage(), displayDocument.getCountry());

        if (LOG.isDebugEnabled()) {
            LOG.debug("getRotationSetDDsForDisplayDocument:: document type :{}", type);
            LOG.debug("getRotationSetDDsForDisplayDocument:: court room ids:{}", getCourtRoomIdString(courtRoomIds));
        }

        List<RotationSetDisplayDocument> results = new ArrayList<>();

        // Check whether we are dealing with a document that copes with
        // multiple courts.
        if ("Y".equalsIgnoreCase(displayDocument.getMultipleCourtYn())) {
            results.add(new RotationSetDisplayDocument(
                new DisplayDocumentUri(documentLocale, courtId, type, courtRoomIds), pageDelay));
        } else {
            // or one that needs to be split into multiple documents dealing each
            // with one court.
            int loopLength = courtRoomIds.length;

            if (loopLength == 0) {
                LOG.error("Found display document with no court rooms", new Exception());
            }
            // If we are dealing with a non multiple courts document, then
            // we
            // do not do unassigned cases for now.
            if (loopLength > 0 && courtRoomIds[loopLength - 1] == DisplayDocumentUri.UNASSIGNED) {
                loopLength--;
            }

            for (int i = 0; i < loopLength; i++) {
                int[] courtRoomIdsArray = {courtRoomIds[i]};
                results.add(getRotationSetDisplayDocument(
                    getDisplayDocumentUri(documentLocale, courtId, type, courtRoomIdsArray), pageDelay));
            }
        }
        return results;
    }

    private String getCourtRoomIdString(int... courtRoomIds) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < courtRoomIds.length; i++) {
            sb.append(courtRoomIds[i]);
            sb.append(i < courtRoomIds.length - 1 ? "," : "");
        }
        return sb.toString();
    }

    private RotationSetDisplayDocument getRotationSetDisplayDocument(final DisplayDocumentUri displayDocumentUri,
        final long pageDelay) {
        return new RotationSetDisplayDocument(displayDocumentUri, pageDelay);
    }

    private DisplayDocumentUri getDisplayDocumentUri(Locale locale, int courtId, DisplayDocumentType name,
        int... courtRoomIds) {
        return new DisplayDocumentUri(locale, courtId, name, courtRoomIds);
    }

    private Locale createLocale(String language, String country) {
        if (language != null) {
            if (country != null) {
                return new Locale(language, country);
            } else {
                return new Locale(language, "");
            }
        }
        return Locale.getDefault();
    }
}
