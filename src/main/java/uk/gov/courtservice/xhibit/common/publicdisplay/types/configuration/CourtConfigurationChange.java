package uk.gov.courtservice.xhibit.common.publicdisplay.types.configuration;

import java.io.Serializable;

/**

 * Title: A general public display configuration change for a court.

 * Description:

 * This class is used to signal a configuration change to the public displays for a given court.
 * Subtypes are used to identify specific areas of configuration that has changed.

 * Copyright: Copyright (c) 2003

 * Company: EDS

 * @author Bob Boothby
 * @version 1.0
 */
public class CourtConfigurationChange implements Serializable {

    static final long serialVersionUID = -3442982487610476239L;

    private final Integer courtId;

    private boolean forceRecreate = Boolean.TRUE;

    /**
     * Construct a general CourtConfigurationChange that results in a complete recreate of all
     * documents associated with the court.

     * @param courtId The court for which the configuration has changed.
     */
    public CourtConfigurationChange(final Integer courtId) {
        this(courtId, Boolean.TRUE);
    }

    /**
     * Construct a general CourtConfigurationChange.

     * @param courtId The court for which the configuration has changed.
     * @param forceRecreate this flag is used to indicate whether all the court's display documents
     *        will have to be rerendered.
     */
    public CourtConfigurationChange(final Integer courtId, final Boolean forceRecreate) {
        this.courtId = courtId;
        setForceRecreate(forceRecreate);
    }

    /**
     * Get the identifier of the court for which the configuration has changed.

     * @return the court ID.
     */
    public Integer getCourtId() {
        return courtId;
    }

    /**
     * isForceRecreate.

     * @return boolean
     */
    public Boolean isForceRecreate() {
        return forceRecreate;
    }
    
    /**
     * setForceRecreate.

     * @param forceRecreate Boolean
     */
    private void setForceRecreate(Boolean forceRecreate) {
        this.forceRecreate = forceRecreate;
    }
}
