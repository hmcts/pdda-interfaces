package uk.gov.hmcts.pdda.common.publicdisplay.setup.drilldown;

/**
 * <p/>
 * Title: CourtDrillDown.

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
public class CourtDrillDown extends DrillDown {

    static final long serialVersionUID = -4562013298092609816L;

    public CourtDrillDown(String shortName) {
        super(shortName);
    }

    public void addCourtSite(CourtSiteDrillDown courtSiteDrillDown) {
        add(courtSiteDrillDown);
    }
}
