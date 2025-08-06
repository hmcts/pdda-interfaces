package uk.gov.hmcts.pdda.common.publicdisplay.setup.drilldown;

/**
 * <p/>
 * Title: CourtSiteDrillDown.

 * <p/>
 * <p/>
 * Description:

 * <p/>
 * <p/>
 * Copyright: Copyright (c) 2003

 * <p/>
 * <p/>
 * Company: Electronic Data Systems

 * @author Neil Ellis
 * @version $Revision: 1.3 $
 */
public class CourtSiteDrillDown extends DrillDown {

    static final long serialVersionUID = 2205146003548441564L;

    public CourtSiteDrillDown(String courtSiteName) {
        super(courtSiteName);
    }

    public void addCourtRoom(DisplayLocationDrillDown courtRoomDrillDown) {
        add(courtRoomDrillDown);
    }
}
