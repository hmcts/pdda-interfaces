package uk.gov.hmcts.pdda.business.entities.xhbdisplaylocation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import uk.gov.hmcts.pdda.business.entities.AbstractVersionedDao;
import uk.gov.hmcts.pdda.business.entities.xhbdisplay.XhbDisplayDao;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "XHB_DISPLAY_LOCATION")
@NamedQuery(name = "XHB_DISPLAY_LOCATION.findByVIPCourtSite",
    query = "SELECT o from XHB_DISPLAY_LOCATION o WHERE o.courtSiteId = :courtSiteId AND o.descriptionCode = 'v_i_p'")
@NamedQuery(name = "XHB_DISPLAY_LOCATION.findByCourtSite",
    query = "SELECT o from XHB_DISPLAY_LOCATION o WHERE o.courtSiteId = :courtSiteId")

public class XhbDisplayLocationDao extends AbstractVersionedDao implements Serializable {

    private static final long serialVersionUID = -71367170402630957L;

    @Id
    @GeneratedValue(generator = "xhb_display_location_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "xhb_display_location_seq", sequenceName = "xhb_display_location_seq",
        allocationSize = 1)
    @Column(name = "DISPLAY_LOCATION_ID")
    private Integer displayLocationId;

    @Column(name = "DESCRIPTION_CODE")
    private String descriptionCode;

    @Column(name = "COURT_SITE_ID", nullable = false)
    private Integer courtSiteId;

    @jakarta.persistence.Transient
    private List<XhbDisplayDao> xhbDisplays = new ArrayList<>();

    public XhbDisplayLocationDao() {
        super();
    }

    public XhbDisplayLocationDao(Integer displayLocationId, String descriptionCode,
        Integer courtSiteId, LocalDateTime lastUpdateDate, LocalDateTime creationDate,
        String lastUpdatedBy, String createdBy, Integer version) {
        this();
        setDisplayLocationId(displayLocationId);
        setDescriptionCode(descriptionCode);
        setCourtSiteId(courtSiteId);
        setLastUpdateDate(lastUpdateDate);
        setCreationDate(creationDate);
        setLastUpdatedBy(lastUpdatedBy);
        setCreatedBy(createdBy);
        setVersion(version);
    }

    public XhbDisplayLocationDao(XhbDisplayLocationDao otherData) {
        this();
        setDisplayLocationId(otherData.getDisplayLocationId());
        setDescriptionCode(otherData.getDescriptionCode());
        setCourtSiteId(otherData.getCourtSiteId());
        setLastUpdateDate(otherData.getLastUpdateDate());
        setCreationDate(otherData.getCreationDate());
        setLastUpdatedBy(otherData.getLastUpdatedBy());
        setCreatedBy(otherData.getCreatedBy());
        setVersion(otherData.getVersion());
    }

    public Integer getPrimaryKey() {
        return getDisplayLocationId();
    }

    public Integer getDisplayLocationId() {
        return displayLocationId;
    }

    private void setDisplayLocationId(Integer displayLocationId) {
        this.displayLocationId = displayLocationId;
    }

    public String getDescriptionCode() {
        return descriptionCode;
    }

    private void setDescriptionCode(String descriptionCode) {
        this.descriptionCode = descriptionCode;
    }

    public Integer getCourtSiteId() {
        return courtSiteId;
    }

    private void setCourtSiteId(Integer courtSiteId) {
        this.courtSiteId = courtSiteId;
    }

    public List<XhbDisplayDao> getXhbDisplays() {
        return xhbDisplays;
    }

    public void setXhbDisplays(List<XhbDisplayDao> xhbDisplays) {
        this.xhbDisplays = xhbDisplays;
    }

}
