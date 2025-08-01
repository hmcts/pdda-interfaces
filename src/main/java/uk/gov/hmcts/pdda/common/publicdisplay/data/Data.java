package uk.gov.hmcts.pdda.common.publicdisplay.data;

import java.io.Serializable;
import java.util.Collection;

/**
 * <p/>
 * Title: Data.

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
 * @version $Revision: 1.10 $
 */
@SuppressWarnings("PMD.ShortClassName")
public class Data implements Serializable {

    static final long serialVersionUID = 8053449481352920474L;

    private Collection<?> table;

    private String courtName;

    private String courtSiteShortName;

    private String courtRoomName;

    /**
     * Returns true if there is no data in this instance.

     * @return true if there is no data in this instance.
     */
    public boolean isEmpty() {
        return table == null || table.isEmpty();
    }

    /**
     * Clears out the data so the object can be reused.
     */
    public void clear() {
        table.clear();
        setCourtName(null);
        setCourtRoomName(null);
    }

    /**
     * Adds a collection to the data item using the standard key. This method should be used if
     * there is only one iterative data item to store. An example is for the Daily List.

     * @param table the Collection to store as the simple table.
     */
    public void setTable(Collection<?> table) {
        this.table = table;
    }

    /**
     * Returns a Collection used to hold simple iterative data.

     * @return a Collection.

     * @see #setTable
     */
    public Collection<?> getTable() {
        return table;
    }

    public String getCourtName() {
        return this.courtName;
    }

    public void setCourtName(String courtName) {
        this.courtName = courtName;
    }

    public void setCourtSiteShortName(String courtSiteShortName) {
        this.courtSiteShortName = courtSiteShortName;
    }

    public String getCourtSiteShortName() {
        return courtSiteShortName;
    }

    public void setCourtRoomName(String courtRoomName) {
        this.courtRoomName = courtRoomName;
    }

    public String getCourtRoomName() {
        return courtRoomName;
    }

    @Override
    public String toString() {
        return "uk.gov.hmcts.pdda.common.publicdisplay.data.Data{" + "table="
            + (table == null ? null : "size:" + table.size() + table) + ", courtName='" + courtName
            + "'" + ", courtSiteShortName='" + courtSiteShortName + "'" + ", courtRoomName='"
            + courtRoomName + "'" + "}";
    }

}
