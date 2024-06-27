package uk.gov.hmcts.pdda.business.entities.xhbdisplaycourtroom;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import uk.gov.hmcts.pdda.business.entities.AbstractDao;

import java.io.Serializable;

@SuppressWarnings({"PMD.ConstructorCallsOverridableMethod"})
@Entity(name = "XHB_DISPLAY_COURT_ROOM")
@NamedQuery(name = "XHB_DISPLAY_COURT_ROOM.findByDisplayId",
    query = "SELECT o from XHB_DISPLAY_COURT_ROOM o WHERE o.displayId = :displayId")
public class XhbDisplayCourtRoomDao extends AbstractDao implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "DISPLAY_ID")
    private Integer displayId;

    @Column(name = "COURT_ROOM_ID")
    private Integer courtRoomId;

    public XhbDisplayCourtRoomDao() {
        super();
    }

    public XhbDisplayCourtRoomDao(Integer displayId, Integer courtRoomId) {
        this();
        setDisplayId(displayId);
        setCourtRoomId(courtRoomId);
    }

    public XhbDisplayCourtRoomDao(XhbDisplayCourtRoomDao otherData) {
        this();
        setDisplayId(otherData.getDisplayId());
        setCourtRoomId(otherData.getCourtRoomId());
    }

    public Integer getDisplayId() {
        return displayId;
    }

    public void setDisplayId(Integer displayId) {
        this.displayId = displayId;
    }

    public Integer getCourtRoomId() {
        return courtRoomId;
    }

    public void setCourtRoomId(Integer courtRoomId) {
        this.courtRoomId = courtRoomId;
    }

    @Override
    public Integer getVersion() {
        return null;
    }

    @Override
    public void setVersion(Integer version) {
    }
}
