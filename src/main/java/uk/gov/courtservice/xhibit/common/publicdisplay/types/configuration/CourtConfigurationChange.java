package uk.gov.courtservice.xhibit.common.publicdisplay.types.configuration;

import java.io.Serializable;

/**
 * <p>
 * Title: A general public display configuration change for a court.
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * This class is used to signal a configuration change to the public displays for a given court.
 * Subtypes are used to identify specific areas of configuration that has changed.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * 
 * <p>
 * Company: EDS
 * </p>
 * 
 * @author Bob Boothby
 * @version 1.0
 */
public class CourtConfigurationChange implements Serializable {

    static final long serialVersionUID = -3442982487610476239L;

    private final Integer courtId;

    private Boolean forceRecreate = Boolean.TRUE;

    /**
     * Construct a general CourtConfigurationChange that results in a complete recreate of all
     * documents associated with the court.
     * 
     * @param courtId The court for which the configuration has changed.
     */
    public CourtConfigurationChange(final Integer courtId) {
        this(courtId, Boolean.TRUE);
    }

    /**
     * Construct a general CourtConfigurationChange.
     * 
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
     * 
     * @return the court ID.
     */
    public Integer getCourtId() {
        return courtId;
    }

    /**
     * isForceRecreate.
     * 
     * @return boolean
     */
    public Boolean isForceRecreate() {
        return forceRecreate;
    }
    
    /**
     * setForceRecreate.
     * 
     * @param forceRecreate Boolean
     */
    private void setForceRecreate(Boolean forceRecreate) {
        this.forceRecreate = forceRecreate;
    }
}
