package uk.gov.hmcts.pdda.business.entities.xhbdisplay;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import uk.gov.hmcts.pdda.business.entities.AbstractVersionedDao;

import java.io.Serializable;

@SuppressWarnings({"PMD.ConstructorCallsOverridableMethod"})
@Entity(name = "XHB_DISPLAY")
@NamedQuery(name = "XHB_DISPLAY.findByRotationSetId",
    query = "SELECT o from XHB_DISPLAY o WHERE o.rotationSetId = :rotationSetId ")
@NamedQuery(name = "XHB_DISPLAY.findByDisplayLocationId",
    query = "SELECT o from XHB_DISPLAY o WHERE o.displayLocationId = :displayLocationId ")
public class XhbDisplayDao extends AbstractVersionedDao implements Serializable {

    private static final long serialVersionUID = -2723700446890851397L;

    @Id
    @GeneratedValue(generator = "xhb_display_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "xhb_display_seq", sequenceName = "xhb_display_seq",
        allocationSize = 1)
    @Column(name = "DISPLAY_ID")
    private Integer displayId;

    @Column(name = "DISPLAY_TYPE_ID", nullable = false)
    private Integer displayTypeId;

    @Column(name = "DISPLAY_LOCATION_ID", nullable = false)
    private Integer displayLocationId;

    @Column(name = "ROTATION_SET_ID")
    private Integer rotationSetId;

    @Column(name = "DESCRIPTION_CODE")
    private String descriptionCode;

    @Column(name = "LOCALE")
    private String locale;

    @Column(name = "SHOW_UNASSIGNED_YN")
    private String showUnassignedYn;

    public XhbDisplayDao() {
        super();
    }

    public XhbDisplayDao(XhbDisplayDao otherData) {
        this();
        setDisplayId(otherData.getDisplayId());
        setDisplayTypeId(otherData.getDisplayTypeId());
        setDisplayLocationId(otherData.getDisplayLocationId());
        setRotationSetId(otherData.getRotationSetId());
        setDescriptionCode(otherData.getDescriptionCode());
        setLocale(otherData.getLocale());
        setLastUpdateDate(otherData.getLastUpdateDate());
        setCreationDate(otherData.getCreationDate());
        setLastUpdatedBy(otherData.getLastUpdatedBy());
        setCreatedBy(otherData.getCreatedBy());
        setVersion(otherData.getVersion());
        setShowUnassignedYn(otherData.getShowUnassignedYn());
    }

    @Override
    public Integer getPrimaryKey() {
        return getDisplayId();
    }

    public Integer getDisplayId() {
        return displayId;
    }

    public void setDisplayId(Integer displayId) {
        this.displayId = displayId;
    }

    public Integer getDisplayTypeId() {
        return displayTypeId;
    }

    public void setDisplayTypeId(Integer displayTypeId) {
        this.displayTypeId = displayTypeId;
    }

    public Integer getDisplayLocationId() {
        return displayLocationId;
    }

    public void setDisplayLocationId(Integer displayLocationId) {
        this.displayLocationId = displayLocationId;
    }

    public Integer getRotationSetId() {
        return rotationSetId;
    }

    public void setRotationSetId(Integer rotationSetId) {
        this.rotationSetId = rotationSetId;
    }

    public String getDescriptionCode() {
        return descriptionCode;
    }

    public void setDescriptionCode(String descriptionCode) {
        this.descriptionCode = descriptionCode;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getShowUnassignedYn() {
        return showUnassignedYn;
    }

    public void setShowUnassignedYn(String showUnassignedYn) {
        this.showUnassignedYn = showUnassignedYn;
    }

}
