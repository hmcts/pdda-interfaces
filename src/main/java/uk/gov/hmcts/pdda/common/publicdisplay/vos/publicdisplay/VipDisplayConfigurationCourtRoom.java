package uk.gov.hmcts.pdda.common.publicdisplay.vos.publicdisplay;

import uk.gov.hmcts.framework.business.vos.CsAbstractValue;

/**

 * Title: VIP Display Configuration Display Document.

 * Description: A VIPDisplayConfigurationCourtRoom defines a court room assigned to the VIP screen.

 * Copyright: Copyright (c) 2003

 * Company: EDS

 * @author Bal Bhamra
 * @version $Id: VIPDisplayConfigurationCourtRoom.java,v 1.1 2005/08/02 13:37:11 szfnvt Exp $
 */
public class VipDisplayConfigurationCourtRoom extends CsAbstractValue {

    static final long serialVersionUID = 3097526474802171720L;

    private final Integer courtRoomId;

    private final String courtSiteShortName;

    private final String courtRoomDisplayName;

    /**
     * Constructor taked in descriptionCode and multipleCourt values.
     */
    public VipDisplayConfigurationCourtRoom(Integer courtRoomId, String courtSiteShortName,
        String courtRoomDisplayName) {
        super();
        this.courtRoomId = courtRoomId;
        this.courtSiteShortName = courtSiteShortName;
        this.courtRoomDisplayName = courtRoomDisplayName;
    }

    /**
     * Get courtRoomId of the court room.
     */
    public Integer getCourtRoomId() {
        return courtRoomId;
    }

    /**
     * Get courtSiteShortName of the court site.
     */
    public String getCourtSiteShortName() {
        return courtSiteShortName;
    }

    /**
     * Get courtRoomDisplayName of the court room.
     */
    public String getCourtRoomDisplayName() {
        return courtRoomDisplayName;
    }
}
