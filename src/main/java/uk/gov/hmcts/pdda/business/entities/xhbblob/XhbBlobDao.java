package uk.gov.hmcts.pdda.business.entities.xhbblob;

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
@Entity(name = "XHB_BLOB")
public class XhbBlobDao extends AbstractVersionedDao implements Serializable {

    private static final long serialVersionUID = 5371189666683221161L;

    @Id
    @GeneratedValue(generator = "xhb_blob_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "xhb_blob_seq", sequenceName = "xhb_blob_seq", allocationSize = 1)
    @Column(name = "BLOB_ID")
    private Long blobId;

    // Cannot use Bytes[] from Hibernate 6.2+, please use lowercase byte[]
    @Column(name = "BLOB_DATA")
    private byte[] blobData;

    public XhbBlobDao() {
        super();
    }

    public XhbBlobDao(Long blobId, byte[] blobData, LocalDateTime lastUpdateDate,
        LocalDateTime creationDate, String lastUpdatedBy, String createdBy, Integer version) {
        this();
        setBlobId(blobId);
        setBlobData(blobData);
        setLastUpdateDate(lastUpdateDate);
        setCreationDate(creationDate);
        setLastUpdatedBy(lastUpdatedBy);
        setCreatedBy(createdBy);
        setVersion(version);
    }

    public XhbBlobDao(XhbBlobDao otherData) {
        this();
        setBlobId(otherData.getBlobId());
        setBlobData(otherData.getBlobData());
        setLastUpdateDate(otherData.getLastUpdateDate());
        setCreationDate(otherData.getCreationDate());
        setLastUpdatedBy(otherData.getLastUpdatedBy());
        setCreatedBy(otherData.getCreatedBy());
        setVersion(otherData.getVersion());
    }

    @Override
    public Long getPrimaryKey() {
        return getBlobId();
    }

    public Long getBlobId() {
        return blobId;
    }

    private void setBlobId(Long blobId) {
        this.blobId = blobId;
    }

    public byte[] getBlobData() {
        return blobData.clone();
    }

    public void setBlobData(byte[] blobData) {
        this.blobData = blobData.clone();
    }

}
