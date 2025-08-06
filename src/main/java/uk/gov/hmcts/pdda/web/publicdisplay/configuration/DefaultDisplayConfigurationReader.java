
package uk.gov.hmcts.pdda.web.publicdisplay.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.CourtRoomIdentifier;
import uk.gov.courtservice.xhibit.common.publicdisplay.types.configuration.CourtConfigurationChange;
import uk.gov.hmcts.pdda.business.services.publicdisplay.PdConfigurationControllerBean;
import uk.gov.hmcts.pdda.business.services.publicdisplay.data.ejb.PdDataControllerBean;
import uk.gov.hmcts.pdda.common.publicdisplay.types.document.DisplayDocumentType;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.priv.impl.DisplayStoreControllerBean;
import uk.gov.hmcts.pdda.web.publicdisplay.types.RenderChanges;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**

 * Title: DefaultDisplayConfigurationReader.

 * Description:

 * Copyright: Copyright (c) 2003

 * Company: EDS

 * @author Bob Boothby
 * @version 1.0
 */
public class DefaultDisplayConfigurationReader extends DisplayConfigurationReader {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultDisplayConfigurationReader.class);

    private static final Map<Integer, DisplayConfigurationWorker> WORKER_INSTANCES = new ConcurrentHashMap<>();

    private PdConfigurationControllerBean pdConfigurationController;

    private PdDataControllerBean pdDataControllerBean;

    private DisplayStoreControllerBean displayStoreControllerBean;

    private int[] courtsForPublicDisplay;

    private DisplayConfigurationWorker mockWorker;

    public DefaultDisplayConfigurationReader() {
        super();
        setupWorkerInstances();
    }

    public DefaultDisplayConfigurationReader(PdConfigurationControllerBean pdConfigurationController,
        PdDataControllerBean pdDataControllerBean, DisplayStoreControllerBean displayStoreControllerBean,
        DisplayConfigurationWorker worker) {
        super();
        this.pdConfigurationController = pdConfigurationController;
        this.pdDataControllerBean = pdDataControllerBean;
        this.displayStoreControllerBean = displayStoreControllerBean;
        this.mockWorker = worker;
        setupWorkerInstances();
    }

    private void setupWorkerInstances() {
        LOG.debug("setupWorkerInstances : 1");
        PdConfigurationControllerBean pdConfigurationControllerBean = getPdConfigurationControllerBean();
        courtsForPublicDisplay = pdConfigurationControllerBean.getCourtsForPublicDisplay();
        LOG.debug("setupWorkerInstances : 2");
        for (int i = courtsForPublicDisplay.length - 1; i > -1; i--) {
            WORKER_INSTANCES.put(courtsForPublicDisplay[i], getDisplayConfigurationWorker(courtsForPublicDisplay[i],
                pdConfigurationControllerBean, getPdDataControllerBean(), getDisplayStoreControllerBean()));
        }
        LOG.debug("DefaultDisplayConfigurationReader : 3");
    }

    /**
     * getRenderChanges.

     * @param change CourtConfigurationChange

     * @return renderChanges:
     */
    @Override
    public RenderChanges getRenderChanges(CourtConfigurationChange change) {
        DisplayConfigurationWorker worker = getDisplayConfigurationWorker(change.getCourtId());
        return worker.getRenderChanges(change);
    }

    /**
     * getRenderChanges.

     * @param documentTypes DisplayDocumentTypeArray
     * @param courtRoom CourtRoomIdentifier
     * @return RenderChanges
     */
    @Override
    public RenderChanges getRenderChanges(DisplayDocumentType[] documentTypes, CourtRoomIdentifier courtRoom) {
        DisplayConfigurationWorker worker = getDisplayConfigurationWorker(courtRoom.getCourtId());
        if (worker != null) {
            return worker.getRenderChanges(documentTypes, courtRoom);
        } else {
            return new RenderChanges();
        }
    }

    /**
     * Get the configured court IDs.

     * @return intArray
     */
    @Override
    public int[] getConfiguredCourtIds() {
        return courtsForPublicDisplay.clone();
    }

    /**
     * Returns a reference to the pdConfigurationController object.

     * @return PDConfigurationControllerBean
     */
    private PdConfigurationControllerBean getPdConfigurationControllerBean() {
        if (pdConfigurationController == null) {
            return new PdConfigurationControllerBean();
        }
        return pdConfigurationController;
    }

    /**
     * Returns a reference to the PDDataControllerBean object.

     * @return PDDataControllerBean
     */
    private PdDataControllerBean getPdDataControllerBean() {
        if (pdDataControllerBean == null) {
            return new PdDataControllerBean();
        }
        return pdDataControllerBean;
    }

    private DisplayStoreControllerBean getDisplayStoreControllerBean() {
        if (displayStoreControllerBean == null) {
            return new DisplayStoreControllerBean();
        }
        return displayStoreControllerBean;
    }

    private Map<Integer, DisplayConfigurationWorker> getWorkerInstances() {
        return WORKER_INSTANCES;
    }

    public DisplayConfigurationWorker getDisplayConfigurationWorker(Integer courtId) {
        if (mockWorker == null) {
            return getWorkerInstances().get(courtId);
        }
        return mockWorker;
    }
    
    private DisplayConfigurationWorker getDisplayConfigurationWorker(Integer courtId,
        PdConfigurationControllerBean bean, PdDataControllerBean pdDataControllerBean,
        DisplayStoreControllerBean displayStoreControllerBean) {
        return new DisplayConfigurationWorker(courtId, bean, pdDataControllerBean, displayStoreControllerBean);
    }
}
