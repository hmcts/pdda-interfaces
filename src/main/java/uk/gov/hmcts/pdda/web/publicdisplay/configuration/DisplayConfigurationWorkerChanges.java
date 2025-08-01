package uk.gov.hmcts.pdda.web.publicdisplay.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.CourtRoomIdentifier;
import uk.gov.courtservice.xhibit.common.publicdisplay.types.configuration.CourtConfigurationChange;
import uk.gov.courtservice.xhibit.common.publicdisplay.types.configuration.CourtDisplayConfigurationChange;
import uk.gov.courtservice.xhibit.common.publicdisplay.types.configuration.CourtRotationSetConfigurationChange;
import uk.gov.hmcts.framework.business.exceptions.CourtNotFoundException;
import uk.gov.hmcts.pdda.business.services.publicdisplay.PdConfigurationControllerBean;
import uk.gov.hmcts.pdda.business.services.publicdisplay.data.ejb.PdDataControllerBean;
import uk.gov.hmcts.pdda.common.publicdisplay.exceptions.PublicDisplayRuntimeException;
import uk.gov.hmcts.pdda.common.publicdisplay.types.document.DisplayDocumentType;
import uk.gov.hmcts.pdda.common.publicdisplay.types.rotationset.DisplayRotationSetData;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.priv.impl.DisplayStoreControllerBean;
import uk.gov.hmcts.pdda.web.publicdisplay.types.RenderChanges;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("PMD.LawOfDemeter")
public abstract class DisplayConfigurationWorkerChanges {

    private static final Logger LOG = LoggerFactory.getLogger(DisplayConfigurationWorkerChanges.class);
    
    // The court ID of the instance.
    protected Integer courtId;

    // Lock class.
    protected RwLock readWriteLock = new RwLock();

    // Used to store and manage Display Documents.
    protected DisplayDocumentReferenceManager displayDocumentReferenceManager;

    protected PdConfigurationControllerBean pdConfigurationController;

    // Stores current Display Rotation Sets by display ID.
    protected Map<Integer, DisplayRotationSetData> displayRotationSetsByDisplayId =
        new ConcurrentHashMap<>();
   
    protected DisplayStoreControllerBean displayStoreControllerBean;

    protected PdDataControllerBean pdDataControllerBean;

    protected DisplayConfigurationWorkerChanges(Integer courtId, PdConfigurationControllerBean bean,
        PdDataControllerBean pdDataControllerBean,
        DisplayStoreControllerBean displayStoreControllerBean) {
        this.courtId = courtId;
        this.pdConfigurationController = bean;
        this.pdDataControllerBean = pdDataControllerBean;
        this.displayStoreControllerBean = displayStoreControllerBean;
        this.displayDocumentReferenceManager =
            new DisplayDocumentReferenceManager(pdDataControllerBean, displayStoreControllerBean);
    }

    protected abstract Set<DisplayRotationSetData> getRotationSetDisplayRotationSets(Integer rotationSetId);
    
    protected abstract void addDisplayRotationSetData(RenderChanges renderChanges,
        DisplayRotationSetData displayRotationSetData, boolean forceRecreate);
    
    protected abstract void removeDisplayRotationSetData(RenderChanges renderChanges,
        DisplayRotationSetData displayRotationSetData, boolean removeDisplayRotationSet);
    
    protected abstract void replaceDisplayRotationSetData(RenderChanges renderChanges,
        DisplayRotationSetData oldDisplayRotationSetData,
        DisplayRotationSetData newDisplayRotationSetData, boolean forceRecreate);
    
    /**
     * Returns the <code>RenderChanges</code> that need re-rendering after the
     * <code>CourtConfigurationChange</code>.

     * @param change The change to the court configuration that has ooccured.

     * @return The RenderChanges caused by the court configuration changed.
     */
    public RenderChanges getRenderChanges(CourtConfigurationChange change) {
        LOG.debug("getRenderChanges({})", change);
        if (!change.getCourtId().equals(courtId)) {
            throw new PublicDisplayRuntimeException(
                "Problem getting render changes fopr a configuration change: "
                    + "This instance is configured for court: " + courtId + " and not for court: "
                    + change.getCourtId());
        }
        try {
            this.readWriteLock.isWriteLockObtained();
            ConfigurationTuple configurations = new ConfigurationTuple();
            if (change instanceof CourtDisplayConfigurationChange) {
                setDisplayConfigurationChanges(configurations, change);
            } else if (change instanceof CourtRotationSetConfigurationChange) {
                setRotationSetConfigurationChanges(configurations, change);
            } else { // treat it as a general CourtConfigurationChange
                setCourtConfigurationChanges(configurations);
            }

            // Work through the old and new configurations working out what
            // has
            // changed.
            return processRenderingChanges(configurations, change.isForceRecreate());
        } finally {
            this.readWriteLock.isWriteLockReleased();
        }
    }
    
    /**
     * Return the set of Display Documents currently held in configuration for the given combination
     * of document types and court room.

     * @param documentTypes The different types of Display Document to find.
     * @param courtRoom The court room for which to find relevant Display Documents.
     * @return A RenderChanges instance containing the relevant Display Documents.
     */
    public RenderChanges getRenderChanges(DisplayDocumentType[] documentTypes,
        CourtRoomIdentifier courtRoom) {
        if (!courtRoom.getCourtId().equals(courtId)) {
            throw new PublicDisplayRuntimeException(
                "Problem getting render changes for a court room and display documents: "
                    + "This instance is configured for court: " + courtId + " and not for court: "
                    + courtRoom.getCourtId());
        }
        try {
            readWriteLock.isReadLockObtained();
            return displayDocumentReferenceManager.getRenderChanges(documentTypes, courtRoom);
        } finally {
            readWriteLock.isReadLockReleased();
        }
    }

    /**
     * Look up the old and the new configuration data for a display.

     * @param configurations ConfigurationTuple to populate with the old and new configurations.
     * @param change The display change object.
     */
    private void setDisplayConfigurationChanges(ConfigurationTuple configurations,
        CourtConfigurationChange change) {
        CourtDisplayConfigurationChange courtDisplayConfigurationChange =
            (CourtDisplayConfigurationChange) change;

        // Get the DisplayRotationSetData from the middle tier.
        Integer displayId = courtDisplayConfigurationChange.getDisplayId();

        configurations.newConfigurationData =
            pdConfigurationController.getUpdatedDisplay(courtId, displayId);

        // Get old configuration data.
        DisplayRotationSetData displayRotationSetData =
            displayRotationSetsByDisplayId.get(displayId);

        if (displayRotationSetData != null) {
            configurations.oldConfigurationData =
                new DisplayRotationSetData[] {displayRotationSetData};
        }
    }

    /**
     * Look up the old and the new configuration data for a rotation set.

     * @param configurations ConfigurationTuple to populate with the old and new configurations.
     * @param change The Rotation Set change object.
     */
    private void setRotationSetConfigurationChanges(ConfigurationTuple configurations,
        CourtConfigurationChange change) {
        CourtRotationSetConfigurationChange courtRotationSetConfigurationChange =
            (CourtRotationSetConfigurationChange) change;

        // Get the DisplayRotationSetData from the middle tier.
        Integer rotationSetId = courtRotationSetConfigurationChange.getRotationSetId();
        configurations.newConfigurationData =
            pdConfigurationController.getUpdatedRotationSet(courtId, rotationSetId);

        // Get old configuration data.
        DisplayRotationSetData[] displayRotationSetData =
            getRotationSetDisplayRotationSets(rotationSetId).toArray(new DisplayRotationSetData[0]);

        if (displayRotationSetData != null) {
            configurations.oldConfigurationData = displayRotationSetData;
        }
    }

    /**
     * Look up the old and the new configuration data for a court.

     * @param configurations ConfigurationTuple to populate with the old and new configurations.
     * @throws CourtNotFoundException When the court is not found in the middle tier.
     */
    private void setCourtConfigurationChanges(ConfigurationTuple configurations) {
        // Get the DisplayRotationSetData from the middle tier.
        configurations.newConfigurationData =
            pdConfigurationController.getCourtConfiguration(courtId);

        // Get old configuration data.
        Collection<DisplayRotationSetData> displayRotationSets =
            displayRotationSetsByDisplayId.values();

        configurations.oldConfigurationData =
            displayRotationSets.toArray(configurations.oldConfigurationData);
    }

    /**
     * This method takes the old and new configurations, compares them, stores the changes and
     * returns a digest of render changes.

     * @param configurationChange The old and new configurations.
     * @param forceRecreate Whether to return all documents for rendering or only those that need to
     *        start and stop rendering.
     * @return An instance of RenderChanges populated with everything that needs rendering.
     */
    private RenderChanges processRenderingChanges(ConfigurationTuple configurationChange,
        boolean forceRecreate) {
        LOG.debug("processRenderingChanges({},{})", configurationChange, forceRecreate);
        // Sort the changes.
        configurationChange.sort();

        // Simplify reference to the two arrays.
        DisplayRotationSetData[] oldConfig = configurationChange.oldConfigurationData;
        DisplayRotationSetData[] newConfig = configurationChange.newConfigurationData;

        // Get the comparator now, because we WILL be using it extensively.
        DisplayRotationSetDataByDisplayComparator comparator =
            DisplayRotationSetDataByDisplayComparator.getInstance();

        // Set up variables for stepping through arrays
        int oldConfigIndex = 0; // position in the old configuration array.
        int newConfigIndex = 0; // position in the new configuration array.
        int oldConfigLength = oldConfig.length;
        int newConfigLength = newConfig.length;

        RenderChanges renderChanges = new RenderChanges();
        // Now step through the arrays in parallel.
        // This kind of remove is a ladder remove as we work our way through
        // The sorted arrays.
        while (oldConfigIndex < oldConfigLength && newConfigIndex < newConfigLength) {
            // We check at the current indexes in the configuration arrays
            // the
            // relationship between the display ids for the
            // DisplayRotationSetData.
            int comparison =
                comparator.compare(oldConfig[oldConfigIndex], newConfig[newConfigIndex]);
            if (comparison > 0) {
                // The DisplayRotationSetData in the old configuration has a
                // display ID greater than that of the one in the new
                // configuration, this means that a display has been added.
                // Scenarios:
                // * When a display has been added to the court.
                // * At first time of initialisation.
                addDisplayRotationSetData(renderChanges, newConfig[newConfigIndex], forceRecreate);

                // Step up to next new display configuration data.
                newConfigIndex++;
            } else if (comparison < 0) {
                // the DisplayRotationSetData in the old configuration has
                // a display ID less than that of the one in the new
                // configuration.
                // This means that a display has been removed.
                // Scenarios:
                // * When a display has been removed from a court.
                // * In event of a failure, when a display has changed it's
                // rotation set but the CourtDisplayChangeEvent has not
                // occurred. For now we will remove it completely.
                removeDisplayRotationSetData(renderChanges, oldConfig[oldConfigIndex], true);
                // Step up to next old display configuration data.
                oldConfigIndex++;
            } else if (comparison == 0) {
                // The DisplayRotationSetData in the old configuration is for
                // the same display as the one in the new configuration.
                // This means that the display may have been updated.
                // Scenarios:
                // * When a Display has been altered.
                // * When a Rotation Set has been altered.
                replaceDisplayRotationSetData(renderChanges, oldConfig[oldConfigIndex],
                    newConfig[newConfigIndex], forceRecreate);

                // Step up to next new and old display configuration data.
                oldConfigIndex++;
                newConfigIndex++;
            }
        }

        // Do mopping up here of remaining uncontested configuration data.
        // First remove displays that are not in the current configuration.
        for (int i = oldConfigIndex; i < oldConfigLength; i++) {
            removeDisplayRotationSetData(renderChanges, oldConfig[i], true);
        }
        // Next add displays that are new.
        for (int i = newConfigIndex; i < newConfigLength; i++) {
            addDisplayRotationSetData(renderChanges, newConfig[i], forceRecreate);
        }

        // Populate the RenderChanges with the accumulated information about
        // which display documents need re-rendering.
        displayDocumentReferenceManager.fillInRenderChanges(renderChanges);

        return renderChanges;
    }
    
    /**

     * Title: Configuration Data Tuple.


     * Description:


     * This class is used to encapsulate the old and new configuration data for a given court,
     * display or rotation set and provides the ability to sort it by display ID.


     * Copyright: Copyright (c) 2003


     * Company: EDS

     * @author Bob Boothby
     * @version 1.0
     */
    protected class ConfigurationTuple {
        private DisplayRotationSetData[] oldConfigurationData = new DisplayRotationSetData[0];

        private DisplayRotationSetData[] newConfigurationData = new DisplayRotationSetData[0];

        /**
         * Sort the encapsulated old and new configuration data by display ID.
         */
        private void sort() {
            Arrays.sort(oldConfigurationData,
                DisplayRotationSetDataByDisplayComparator.getInstance());
            Arrays.sort(newConfigurationData,
                DisplayRotationSetDataByDisplayComparator.getInstance());
        }
    }

}
