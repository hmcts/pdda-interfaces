package uk.gov.hmcts.pdda.business.vos.services.court;

import uk.gov.hmcts.framework.business.vos.CsAbstractValue;
import uk.gov.hmcts.framework.util.Sorter;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CourtStructureValue extends CsAbstractValue {
    private final Map<Object, Object> courtRoomsMap = new ConcurrentHashMap<>();

    private XhbCourtSiteDao[] courtSites;

    private XhbCourtDao court;

    private static final long serialVersionUID = 1617472985745156130L;
    
    private static final Integer ONE = 1;

    public void setCourt(XhbCourtDao court) {
        this.court = court;
    }

    public XhbCourtDao getCourt() {
        return court;
    }

    public void setCourtSites(XhbCourtSiteDao... courtSites) {
        this.courtSites = courtSites.clone();
    }

    public XhbCourtSiteDao[] getCourtSites() {
        return courtSites.clone();
    }

    public void addCourtRooms(Integer courtSiteId, XhbCourtRoomDao... courtRooms) {
        courtRoomsMap.put(courtSiteId, courtRooms);
    }

    public XhbCourtRoomDao[] getCourtRoomsForSite(Integer courtSiteId) {
        return (XhbCourtRoomDao[]) courtRoomsMap.get(courtSiteId);
    }

    /**
     * Return a sorted list of a court rooms across all sites.
     */
    public XhbCourtRoomDao[] getAllCourtRooms() {
        if (getCourtSites().length == ONE) {
            XhbCourtRoomDao[] allCourtRooms =
                getCourtRoomsForSite(getCourtSites()[0].getCourtSiteId());
            Sorter.sort(allCourtRooms, new String[] {"courtSiteId", "crestCourtRoomNo"},
                Sorter.ASCENDING);
            return allCourtRooms;

        } else if (getCourtSites().length > ONE) {
            int numberCourtRooms = 0;
            for (XhbCourtSiteDao courtSite : courtSites) {
                numberCourtRooms += getCourtRoomsForSite(courtSite.getCourtSiteId()).length;
            }
            XhbCourtRoomDao[] allCourtRooms = new XhbCourtRoomDao[numberCourtRooms];

            int lastIndex = 0;
            int arrayLength;
            for (XhbCourtSiteDao courtSite : courtSites) {
                arrayLength = getCourtRoomsForSite(courtSite.getCourtSiteId()).length;
                System.arraycopy(getCourtRoomsForSite(courtSite.getCourtSiteId()), 0,
                    allCourtRooms, lastIndex, arrayLength);
                lastIndex += arrayLength;
            }

            Sorter.sort(allCourtRooms, new String[] {"courtSiteId", "crestCourtRoomNo"},
                Sorter.ASCENDING);

            return allCourtRooms;
        } else {
            return new XhbCourtRoomDao[] {};
        }
    }

}
