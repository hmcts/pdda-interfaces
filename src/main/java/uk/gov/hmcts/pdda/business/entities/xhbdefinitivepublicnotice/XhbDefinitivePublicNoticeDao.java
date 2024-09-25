package uk.gov.hmcts.pdda.business.entities.xhbdefinitivepublicnotice;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import uk.gov.hmcts.pdda.business.entities.AbstractVersionedDao;

import java.io.Serializable;
import java.time.LocalDateTime;


@Entity(name = "XHB_DEFINITIVE_PUBLIC_NOTICE")
public class XhbDefinitivePublicNoticeDao extends AbstractVersionedDao implements Serializable {

    private static final long serialVersionUID = -2723700446890851397L;

    @Id
    @GeneratedValue(generator = "xhb_definitive_public_notice_seq",
        strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "xhb_definitive_public_notice_seq",
        sequenceName = "xhb_definitive_public_notice_seq", allocationSize = 1)
    @Column(name = "DEFINITIVE_PN_ID")
    private Integer definitivePnId;

    @Column(name = "DEFINITIVE_PN_DESC")
    private String definitivePnDesc;

    @Column(name = "PRIORITY")
    private Integer priority;

    public XhbDefinitivePublicNoticeDao() {
        super();
    }

    public XhbDefinitivePublicNoticeDao(Integer definitivePnId, String definitivePnDesc,
        Integer priority, LocalDateTime lastUpdateDate, LocalDateTime creationDate,
        String lastUpdatedBy, String createdBy, Integer version) {
        this();
        setDefinitivePnId(definitivePnId);
        setDefinitivePnDesc(definitivePnDesc);
        setPriority(priority);
        super.setLastUpdateDate(lastUpdateDate);
        super.setCreationDate(creationDate);
        super.setLastUpdatedBy(lastUpdatedBy);
        super.setCreatedBy(createdBy);
        super.setVersion(version);
    }

    public XhbDefinitivePublicNoticeDao(XhbDefinitivePublicNoticeDao otherData) {
        this();
        setDefinitivePnId(otherData.getDefinitivePnId());
        setDefinitivePnDesc(otherData.getDefinitivePnDesc());
        setPriority(otherData.getPriority());
        super.setLastUpdateDate(otherData.getLastUpdateDate());
        super.setCreationDate(otherData.getCreationDate());
        super.setLastUpdatedBy(otherData.getLastUpdatedBy());
        super.setCreatedBy(otherData.getCreatedBy());
        super.setVersion(otherData.getVersion());
    }

    public Integer getPrimaryKey() {
        return getDefinitivePnId();
    }

    public Integer getDefinitivePnId() {
        return definitivePnId;
    }

    private void setDefinitivePnId(Integer definitivePnId) {
        this.definitivePnId = definitivePnId;
    }

    public String getDefinitivePnDesc() {
        return definitivePnDesc;
    }

    private void setDefinitivePnDesc(String definitivePnDesc) {
        this.definitivePnDesc = definitivePnDesc;
    }

    public Integer getPriority() {
        return priority;
    }

    private void setPriority(Integer priority) {
        this.priority = priority;
    }

}
