package uk.gov.hmcts.pdda.business.entities.xhbpublicnotice;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Transient;
import uk.gov.hmcts.pdda.business.entities.AbstractVersionedDao;
import uk.gov.hmcts.pdda.business.entities.xhbdefinitivepublicnotice.XhbDefinitivePublicNoticeDao;

import java.io.Serializable;
import java.time.LocalDateTime;


@Entity(name = "XHB_PUBLIC_NOTICE")
public class XhbPublicNoticeDao extends AbstractVersionedDao implements Serializable {

    private static final long serialVersionUID = -2723700446890851397L;

    @Id
    @GeneratedValue(generator = "xhb_public_notice_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "xhb_public_notice_seq", sequenceName = "xhb_public_notice_seq",
        allocationSize = 1)
    @Column(name = "PUBLIC_NOTICE_ID")
    private Integer publicNoticeId;

    @Column(name = "PUBLIC_NOTICE_DESC")
    private String publicNoticeDesc;

    @Column(name = "COURT_ID")
    private Integer courtId;

    @Column(name = "DEFINITIVE_PN_ID")
    private Integer definitivePnId;

    // Non-columns
    @Transient
    private XhbDefinitivePublicNoticeDao xhbDefinitivePublicNotice;

    public XhbPublicNoticeDao() {
        super();
    }

    public XhbPublicNoticeDao(Integer publicNoticeId, String publicNoticeDesc, Integer courtId,
        LocalDateTime lastUpdateDate, LocalDateTime creationDate, String lastUpdatedBy,
        String createdBy, Integer version, Integer definitivePnId) {
        this();
        setPublicNoticeId(publicNoticeId);
        setPublicNoticeDesc(publicNoticeDesc);
        setCourtId(courtId);
        setDefinitivePnId(definitivePnId);
        super.setLastUpdateDate(lastUpdateDate);
        super.setCreationDate(creationDate);
        super.setLastUpdatedBy(lastUpdatedBy);
        super.setCreatedBy(createdBy);
        super.setVersion(version);
    }

    public XhbPublicNoticeDao(XhbPublicNoticeDao otherData) {
        this();
        setPublicNoticeId(otherData.getPublicNoticeId());
        setPublicNoticeDesc(otherData.getPublicNoticeDesc());
        setCourtId(otherData.getCourtId());
        setDefinitivePnId(otherData.getDefinitivePnId());
        super.setLastUpdateDate(otherData.getLastUpdateDate());
        super.setCreationDate(otherData.getCreationDate());
        super.setLastUpdatedBy(otherData.getLastUpdatedBy());
        super.setCreatedBy(otherData.getCreatedBy());
        super.setVersion(otherData.getVersion());
    }

    public Integer getPrimaryKey() {
        return getPublicNoticeId();
    }

    public Integer getPublicNoticeId() {
        return publicNoticeId;
    }

    private void setPublicNoticeId(Integer publicNoticeId) {
        this.publicNoticeId = publicNoticeId;
    }

    public String getPublicNoticeDesc() {
        return publicNoticeDesc;
    }

    private void setPublicNoticeDesc(String publicNoticeDesc) {
        this.publicNoticeDesc = publicNoticeDesc;
    }

    public Integer getCourtId() {
        return courtId;
    }

    private void setCourtId(Integer courtId) {
        this.courtId = courtId;
    }

    public Integer getDefinitivePnId() {
        return definitivePnId;
    }

    private void setDefinitivePnId(Integer definitivePnId) {
        this.definitivePnId = definitivePnId;
    }

    public XhbDefinitivePublicNoticeDao getXhbDefinitivePublicNotice() {
        return xhbDefinitivePublicNotice;
    }

    public void setXhbDefinitivePublicNotice(
        XhbDefinitivePublicNoticeDao xhbDefinitivePublicNotice) {
        this.xhbDefinitivePublicNotice = xhbDefinitivePublicNotice;
    }


}
