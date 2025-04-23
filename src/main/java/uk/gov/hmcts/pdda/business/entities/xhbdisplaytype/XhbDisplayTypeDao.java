package uk.gov.hmcts.pdda.business.entities.xhbdisplaytype;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import uk.gov.hmcts.pdda.business.entities.AbstractVersionedDao;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity(name = "XHB_DISPLAY_TYPE")
public class XhbDisplayTypeDao extends AbstractVersionedDao implements Serializable {

    private static final long serialVersionUID = -2723700446890851397L;

    @Id
    @GeneratedValue(generator = "xhb_display_type_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "xhb_display_type_seq", sequenceName = "xhb_display_type_seq",
        allocationSize = 1)
    @Column(name = "DISPLAY_TYPE_ID")
    private Integer displayTypeId;

    @Column(name = "DESCRIPTION_CODE")
    private String descriptionCode;

    public XhbDisplayTypeDao() {
        super();
    }

    public XhbDisplayTypeDao(final Integer displayTypeId, final String descriptionCode,
        final LocalDateTime lastUpdateDate, final LocalDateTime creationDate,
        final String lastUpdatedBy, final String createdBy, final Integer version) {
        this();
        setDisplayTypeId(displayTypeId);
        setDescriptionCode(descriptionCode);
        super.setLastUpdateDate(lastUpdateDate);
        super.setCreationDate(creationDate);
        super.setLastUpdatedBy(lastUpdatedBy);
        super.setCreatedBy(createdBy);
        super.setVersion(version);
    }

    public XhbDisplayTypeDao(final XhbDisplayTypeDao otherData) {
        this();
        setDisplayTypeId(otherData.getDisplayTypeId());
        setDescriptionCode(otherData.getDescriptionCode());
        super.setLastUpdateDate(otherData.getLastUpdateDate());
        super.setCreationDate(otherData.getCreationDate());
        super.setLastUpdatedBy(otherData.getLastUpdatedBy());
        super.setCreatedBy(otherData.getCreatedBy());
        super.setVersion(otherData.getVersion());
    }

    @Override
    public Integer getPrimaryKey() {
        return getDisplayTypeId();
    }

    public Integer getDisplayTypeId() {
        return displayTypeId;
    }

    private void setDisplayTypeId(final Integer displayTypeId) {
        this.displayTypeId = displayTypeId;
    }

    public String getDescriptionCode() {
        return descriptionCode;
    }

    private void setDescriptionCode(final String descriptionCode) {
        this.descriptionCode = descriptionCode;
    }

}
