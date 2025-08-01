package uk.gov.hmcts.pdda.web.publicdisplay.configuration;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.services.publicdisplay.PdConfigurationControllerBean;
import uk.gov.hmcts.pdda.business.services.publicdisplay.data.ejb.PdDataControllerBean;
import uk.gov.hmcts.pdda.common.publicdisplay.types.rotationset.DisplayRotationSetData;
import uk.gov.hmcts.pdda.common.publicdisplay.types.rotationset.RotationSetDisplayDocument;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.priv.impl.DisplayStoreControllerBean;
import uk.gov.hmcts.pdda.web.publicdisplay.types.RenderChanges;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**

 * Title: Display Configuration Worker.


 * Description:

 * A Display Configuration Worker is concerned with managing the configuration on the thin client
 * side of the displays in the court room. It is aware of Displays, Rotation Sets and is responsible
 * for populating <code>RenderChanges</code> instances with details of documents that need to start
 * rendering, stop rendering or be re-rendered in reponse to configuration changes or in response to
 * data changes.

 * This class defers knowledge of Display Documents to an internally held instance of
 * DisplayDocumentReferenceManager that has specialist knowledge of Display Documents.


 * Please note that this class is locked such that according to the semantic of the
 * <code>RWLock</code> class, to ensure the internal consistency of the data.



 * Copyright: Copyright (c) 2003


 * Company: Electronic Data Systems

 * @author Neil Ellis, Bob Boothby.
 * @version $Revision: 1.6 $
 * @see uk.gov.hmcts.pdda.web.publicdisplay.configuration.RwLock
 */
public class DisplayConfigurationWorker extends DisplayConfigurationWorkerChanges {

    private static final Logger LOG = LoggerFactory.getLogger(DisplayConfigurationWorker.class);

    private EntityManager entityManager;

    // Stores current Display Rotation Sets by rotation set ID.
    private final Map<Integer, Set<DisplayRotationSetData>> displayRotationSetsByRotationSetId =
        new ConcurrentHashMap<>();
    
    protected DisplayConfigurationWorker(Integer courtId, PdConfigurationControllerBean bean,
        PdDataControllerBean pdDataControllerBean,
        DisplayStoreControllerBean displayStoreControllerBean) {
        super(courtId, bean, pdDataControllerBean, displayStoreControllerBean);
    }

    /**
     * This method updates the cached configuration data by adding an old Display Rotation Set and
     * populates the RenderChanges in order to start rendering of the Display Rotation Set.

     * @param renderChanges The instance of <code>RenderChanges</code> to populate.
     * @param displayRotationSetData The Display Rotation Set to add.
     * @param forceRecreate Whether all the documents in the new Display Rotation Set should be
     *        rendered regardless of whether they have been rendered before.
     */
    @Override
    protected void addDisplayRotationSetData(RenderChanges renderChanges,
        DisplayRotationSetData displayRotationSetData, boolean forceRecreate) {
        displayRotationSetsByDisplayId.put(displayRotationSetData.getDisplayId(),
            displayRotationSetData);
        getRotationSetDisplayRotationSets(displayRotationSetData.getRotationSetId())
            .add(displayRotationSetData);
        renderChanges.addStartRotationSet(displayRotationSetData, getDisplayStoreControllerBean());
        if (forceRecreate) {
            addAllDisplayDocumentsToRenderChanges(renderChanges, displayRotationSetData);
        }
        // Add Display Document References.
        displayDocumentReferenceManager.addDisplayDocumentReferences(displayRotationSetData);
    }

    /**
     * This method updates the cached configuration data by removing an old Display Rotation Set and
     * populates the RenderChanges in order to finish rendering of the Display Rotation Set.

     * @param renderChanges The instance of <code>RenderChanges</code> to populate.
     * @param displayRotationSetData The Display Rotation Set to remove.
     * @param removeDisplayRotationSet Whether to stop rendering the display rotation set. Only to
     *        be used in scenarios where the display has been remove from the configuration.
     */
    @Override
    protected void removeDisplayRotationSetData(RenderChanges renderChanges,
        DisplayRotationSetData displayRotationSetData, boolean removeDisplayRotationSet) {
        displayRotationSetsByDisplayId.remove(displayRotationSetData.getDisplayId());
        getRotationSetDisplayRotationSets(displayRotationSetData.getRotationSetId())
            .remove(displayRotationSetData);
        // Remove Display Document References.
        displayDocumentReferenceManager.removeDisplayDocumentReferences(displayRotationSetData);
        if (removeDisplayRotationSet) {
            renderChanges.addStopRotationSet(displayRotationSetData,
                getDisplayStoreControllerBean());
        }
    }

    /**
     * This method intelligently handles the replacement of a Display Rotation Set with a new one
     * and copes with the force remove flag.

     * @param renderChanges The instance of <code>RenderChanges</code> to populate.
     * @param oldDisplayRotationSetData The old Display Rotation Set.
     * @param newDisplayRotationSetData The new Display Rotation Set.
     * @param forceRecreate Whether all the documents in the new Display Rotation Set should be
     *        rendered regardless of whether they have been rendered before.
     */
    @Override
    protected void replaceDisplayRotationSetData(RenderChanges renderChanges,
        DisplayRotationSetData oldDisplayRotationSetData,
        DisplayRotationSetData newDisplayRotationSetData, boolean forceRecreate) {
        if (oldDisplayRotationSetData.equals(newDisplayRotationSetData)) {
            // No change but we must re-render.
            addDisplayRotationSetData(renderChanges, newDisplayRotationSetData, forceRecreate);
        } else if (!forceRecreate) { 
            // There has been a change.
            removeDisplayRotationSetData(renderChanges, oldDisplayRotationSetData, false);
            addDisplayRotationSetData(renderChanges, newDisplayRotationSetData, forceRecreate);
        }
    }

    /**
     * Used as part of force remove to add all display documents in a Display Rotatoin Set to the
     * set of documents to render regardless of whether they are currently rendered.

     * @param renderChanges The instance of <code>RenderChanges</code> to populate.
     * @param displayRotationSetData The Display Rotation Set whose Display Documents <i>must</i> be
     *        rendered.
     */
    private void addAllDisplayDocumentsToRenderChanges(RenderChanges renderChanges,
        DisplayRotationSetData displayRotationSetData) {
        RotationSetDisplayDocument[] rsDDs =
            displayRotationSetData.getRotationSetDisplayDocuments();
        for (int i = rsDDs.length - 1; i >= 0; i--) {
            renderChanges.addStartDocument(rsDDs[i].getDisplayDocumentUri(),
                getPdDataControllerBean(), getDisplayStoreControllerBean());
        }
    }

    /**
     * This method is written to encompass the logic around retrieving the set of Display Rotation
     * Sets for a given Rotation Set.

     * @param rotationSetId The ID of the Rotation Set.
     * @return A Set of types <code>DisplayRotationSetData</code> representing all the Display
     *         Rotation Sets associated with the Rotation Set.
     */
    @Override
    protected Set<DisplayRotationSetData> getRotationSetDisplayRotationSets(Integer rotationSetId) {
        return displayRotationSetsByRotationSetId.computeIfAbsent(rotationSetId,
            k -> new HashSet<>());
    }

    private PdDataControllerBean getPdDataControllerBean() {
        if (pdDataControllerBean == null) {
            pdDataControllerBean = new PdDataControllerBean(getEntityManager());
        }
        return pdDataControllerBean;
    }

    private DisplayStoreControllerBean getDisplayStoreControllerBean() {
        if (displayStoreControllerBean == null) {
            displayStoreControllerBean = new DisplayStoreControllerBean(getEntityManager());
        }
        return displayStoreControllerBean;
    }

    private EntityManager getEntityManager() {
        if (entityManager == null) {
            LOG.debug("getEntityManager() - Creating new entityManager");
            entityManager = EntityManagerUtil.getEntityManager();
        }
        return entityManager;
    }
}
