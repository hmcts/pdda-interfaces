package uk.gov.hmcts.pdda.business.entities.xhbclob;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import uk.gov.hmcts.pdda.business.entities.AbstractVersionedDao;

import java.io.Serializable;
import java.time.LocalDateTime;

@SuppressWarnings({"PMD.ConstructorCallsOverridableMethod"})
@Entity(name = "XHB_CLOB")
public class XhbClobDao extends AbstractVersionedDao implements Serializable {

    private static final long serialVersionUID = -2723700446890851397L;

    @Id
    @GeneratedValue(generator = "xhb_clob_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "xhb_clob_seq", sequenceName = "xhb_clob_seq", allocationSize = 1)
    @Column(name = "CLOB_ID")
    private Long clobId;

    @Column(name = "CLOB_DATA")
    private String clobData;

    public XhbClobDao() {
        super();
    }

    public XhbClobDao(Long clobId, String clobData, LocalDateTime lastUpdateDate,
        LocalDateTime creationDate, String lastUpdatedBy, String createdBy, Integer version) {
        this();
        setClobId(clobId);
        setClobData(clobData);
        setLastUpdateDate(lastUpdateDate);
        setCreationDate(creationDate);
        setLastUpdatedBy(lastUpdatedBy);
        setCreatedBy(createdBy);
        setVersion(version);
    }

    public XhbClobDao(XhbClobDao otherData) {
        this();
        setClobId(otherData.getClobId());
        setClobData(otherData.getClobData());
        setLastUpdateDate(otherData.getLastUpdateDate());
        setCreationDate(otherData.getCreationDate());
        setLastUpdatedBy(otherData.getLastUpdatedBy());
        setCreatedBy(otherData.getCreatedBy());
        setVersion(otherData.getVersion());
    }

    @Override
    public Long getPrimaryKey() {
        return getClobId();
    }

    public Long getClobId() {
        return clobId;
    }

    private void setClobId(Long clobId) {
        this.clobId = clobId;
    }

    public String getClobData() {
        return clobData;
    }

    public void setClobData(String clobData) {
        this.clobData = clobData;
    }

}
