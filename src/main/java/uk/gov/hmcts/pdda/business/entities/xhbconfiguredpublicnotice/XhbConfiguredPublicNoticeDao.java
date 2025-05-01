package uk.gov.hmcts.pdda.business.entities.xhbconfiguredpublicnotice;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Transient;
import uk.gov.hmcts.pdda.business.entities.AbstractVersionedDao;
import uk.gov.hmcts.pdda.business.entities.xhbpublicnotice.XhbPublicNoticeDao;

import java.io.Serializable;
import java.time.LocalDateTime;

@SuppressWarnings({"PMD.LinguisticNaming", "PMD.ConstructorCallsOverridableMethod"})
@Entity(name = "XHB_CONFIGURED_PUBLIC_NOTICE")
@NamedQuery(name = "XHB_CONFIGURED_PUBLIC_NOTICE.findByDefinitivePNCourtRoomValue",
    query = "SELECT o from XHB_CONFIGURED_PUBLIC_NOTICE o WHERE o.courtRoomId = :courtRoomId "
        + "AND o.publicNoticeId = :publicNoticeId ")
@NamedQuery(name = "XHB_CONFIGURED_PUBLIC_NOTICE.findActiveCourtRoomNotices",
    query = "SELECT o from XHB_CONFIGURED_PUBLIC_NOTICE o WHERE o.courtRoomId = :courtRoomId AND o.isActive = '1'")
public class XhbConfiguredPublicNoticeDao extends AbstractVersionedDao implements Serializable {

    private static final long serialVersionUID = -2723700446890851397L;

    @Id
    @GeneratedValue(generator = "xhb_configured_public_notice_seq",
        strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "xhb_configured_public_notice_seq",
        sequenceName = "xhb_configured_public_notice_seq", allocationSize = 1)
    @Column(name = "CONFIGURED_PUBLIC_NOTICE_ID")
    private Integer configuredPublicNoticeId;

    @Column(name = "IS_ACTIVE")
    private String isActive;

    @Column(name = "COURT_ROOM_ID")
    private Integer courtRoomId;

    @Column(name = "PUBLIC_NOTICE_ID")
    private Integer publicNoticeId;

    // Non-columns
    @Transient
    private XhbPublicNoticeDao xhbPublicNotice;

    public XhbConfiguredPublicNoticeDao() {
        super();
    }

    public XhbConfiguredPublicNoticeDao(Integer configuredPublicNoticeId, String isActive,
        Integer courtRoomId, Integer publicNoticeId, LocalDateTime lastUpdateDate,
        LocalDateTime creationDate, String lastUpdatedBy, String createdBy, Integer version) {
        this();
        setConfiguredPublicNoticeId(configuredPublicNoticeId);
        setIsActive(isActive);
        setCourtRoomId(courtRoomId);
        setPublicNoticeId(publicNoticeId);
        setLastUpdateDate(lastUpdateDate);
        setCreationDate(creationDate);
        setLastUpdatedBy(lastUpdatedBy);
        setCreatedBy(createdBy);
        setVersion(version);
    }

    public XhbConfiguredPublicNoticeDao(XhbConfiguredPublicNoticeDao otherData) {
        this();
        setConfiguredPublicNoticeId(otherData.getConfiguredPublicNoticeId());
        setIsActive(otherData.getIsActive());
        setCourtRoomId(otherData.getCourtRoomId());
        setPublicNoticeId(otherData.getPublicNoticeId());
        setLastUpdateDate(otherData.getLastUpdateDate());
        setCreationDate(otherData.getCreationDate());
        setLastUpdatedBy(otherData.getLastUpdatedBy());
        setCreatedBy(otherData.getCreatedBy());
        setVersion(otherData.getVersion());
    }

    @Override
    public Integer getPrimaryKey() {
        return getConfiguredPublicNoticeId();
    }

    public Integer getConfiguredPublicNoticeId() {
        return configuredPublicNoticeId;
    }

    private void setConfiguredPublicNoticeId(Integer configuredPublicNoticeId) {
        this.configuredPublicNoticeId = configuredPublicNoticeId;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public Integer getCourtRoomId() {
        return courtRoomId;
    }

    private void setCourtRoomId(Integer courtRoomId) {
        this.courtRoomId = courtRoomId;
    }

    public Integer getPublicNoticeId() {
        return publicNoticeId;
    }

    private void setPublicNoticeId(Integer publicNoticeId) {
        this.publicNoticeId = publicNoticeId;
    }

    public XhbPublicNoticeDao getXhbPublicNotice() {
        return xhbPublicNotice;
    }

    public void setXhbPublicNotice(XhbPublicNoticeDao xhbPublicNotice) {
        this.xhbPublicNotice = xhbPublicNotice;
    }

}
