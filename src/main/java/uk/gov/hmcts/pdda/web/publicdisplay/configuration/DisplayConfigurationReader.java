package uk.gov.hmcts.pdda.web.publicdisplay.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.CourtRoomIdentifier;
import uk.gov.hmcts.pdda.common.publicdisplay.types.configuration.CourtConfigurationChange;
import uk.gov.hmcts.pdda.common.publicdisplay.types.document.DisplayDocumentType;
import uk.gov.hmcts.pdda.web.publicdisplay.types.RenderChanges;

/**
 * <p>
 * Title: DisplayConfigurationReader.
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * 
 * <p>
 * Company: Electronic Data Systems
 * </p>
 * 
 * @author Bob Boothby
 * @version $Revision: 1.5 $
 */
public abstract class DisplayConfigurationReader {
    private static final Logger LOG = LoggerFactory.getLogger(DisplayConfigurationReader.class);
    private static DisplayConfigurationReader instance;

    /**
     * Get the singleton instance of DisplayConfigurationReader.
     * 
     * @return The appropriate instance of DisplayConfigurationReader.
     */
    public static DisplayConfigurationReader getInstance() {
        synchronized (DisplayConfigurationReader.class) {
            // This construct provides the ability to recover if the constructor
            // fails.
            LOG.debug("DisplayConfigurationReader : 1");
            if (instance == null) {
                instance = new DefaultDisplayConfigurationReader();
            }
            return instance;
        }
    }

    /**
     * Returns the <code>Renderable</code>s that need re-rendering after the
     * <code>CourtConfigurationChange</code>.
     * 
     * @param change The change to the court configuration that has ooccured.
     * 
     * @return The RenderChanges caused by the court configuration changed.
     */
    public abstract RenderChanges getRenderChanges(CourtConfigurationChange change);

    /**
     * Returns the <code>Renderable</code>s that need re-rendering after a subclass of a
     * <code>CourtRoomEvent</code>.
     * 
     * 
     * @return The RenderChanges caused by the event.
     */
    public abstract RenderChanges getRenderChanges(DisplayDocumentType[] displayDocumentTypes,
        CourtRoomIdentifier courtRoomIdentifier);

    /**
     * Get the configured court IDs.
     * 
     * @return intArray
     */
    public abstract int[] getConfiguredCourtIds();

}
