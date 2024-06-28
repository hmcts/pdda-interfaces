package uk.gov.hmcts.pdda.business.entities.xhbpddamessage;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import uk.gov.hmcts.pdda.business.entities.AbstractVersionedDao;

import java.io.Serializable;
import java.time.LocalDateTime;

@SuppressWarnings({"PMD.ConstructorCallsOverridableMethod"})
@Entity(name = "XHB_PDDA_MESSAGE")
@NamedQuery(name = "XHB_PDDA_MESSAGE.findByLighthouse",
    query = "SELECT o from XHB_PDDA_MESSAGE o WHERE o.cpDocumentName is not null AND o.cpDocumentStatus = 'VN' AND "
        + "(o.obsInd is null OR o.obsInd = 'N' OR o.obsInd = ' ')")
@NamedQuery(name = "XHB_PDDA_MESSAGE.findByCpDocumentName",
    query = "SELECT o from XHB_PDDA_MESSAGE o WHERE o.cpDocumentName = :cpDocumentName AND (o.obsInd is null OR "
        + "o.obsInd = 'N' OR o.obsInd = ' ') ORDER BY o.pddaMessageId")
@NamedQuery(name = "XHB_PDDA_MESSAGE.findUnrespondedCPMessages",
    query = "SELECT o from XHB_PDDA_MESSAGE o WHERE o.cpResponseGenerated = 'N' AND (o.obsInd is null OR "
        + "o.obsInd = 'N' OR o.obsInd = ' ') ORDER BY o.pddaMessageId")
public class XhbPddaMessageDao extends AbstractVersionedDao implements Serializable {

    private static final long serialVersionUID = 3149855566341480919L;

    @Id
    @GeneratedValue(generator = "xhb_pdda_message_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "xhb_pdda_message_seq", sequenceName = "xhb_pdda_message_seq",
        allocationSize = 1)
    @Column(name = "PDDA_MESSAGE_ID")
    private Integer pddaMessageId;

    @Column(name = "COURT_ID")
    private Integer courtId;

    @Column(name = "COURT_ROOM_ID")
    private Integer courtRoomId;

    @Column(name = "PDDA_MESSAGE_GUID")
    private String pddaMessageGuid;

    @Column(name = "PDDA_MESSAGE_TYPE_ID")
    private Integer pddaMessageTypeId;

    @Column(name = "PDDA_MESSAGE_DATA_ID")
    private Long pddaMessageDataId;

    @Column(name = "PDDA_BATCH_ID")
    private Integer pddaBatchId;

    @Column(name = "TIME_SENT")
    private LocalDateTime timeSent;

    @Column(name = "CP_DOCUMENT_NAME")
    private String cpDocumentName;

    @Column(name = "CP_DOCUMENT_STATUS")
    private String cpDocumentStatus;

    @Column(name = "CP_RESPONSE_GENERATED")
    private String cpResponseGenerated;

    @Column(name = "ERROR_MESSAGE")
    private String errorMessage;

    @Column(name = "CPP_STAGING_INBOUND_ID")
    private Integer cppStagingInboundId;

    @Column(name = "OBS_IND")
    private String obsInd;

    public XhbPddaMessageDao() {
        super();
    }

    public XhbPddaMessageDao(XhbPddaMessageDao otherData) {
        this();
        setPddaMessageId(otherData.getPddaMessageId());
        setCourtId(otherData.getCourtId());
        setCourtRoomId(otherData.getCourtRoomId());
        setPddaMessageGuid(otherData.getPddaMessageGuid());
        setPddaMessageTypeId(otherData.getPddaMessageTypeId());
        setPddaMessageDataId(otherData.getPddaMessageDataId());
        setPddaBatchId(otherData.getPddaBatchId());
        setTimeSent(otherData.getTimeSent());
        setCpDocumentName(otherData.getCpDocumentName());
        setCpDocumentStatus(otherData.getCpDocumentStatus());
        setCpResponseGenerated(otherData.getCpResponseGenerated());
        setErrorMessage(otherData.getErrorMessage());
        setCppStagingInboundId(otherData.getCppStagingInboundId());
        setObsInd(otherData.getObsInd());
        setLastUpdateDate(otherData.getLastUpdateDate());
        setCreationDate(otherData.getCreationDate());
        setLastUpdatedBy(otherData.getLastUpdatedBy());
        setCreatedBy(otherData.getCreatedBy());
        setVersion(otherData.getVersion());
    }

    public Integer getPrimaryKey() {
        return getPddaMessageId();
    }

    public Integer getPddaMessageId() {
        return pddaMessageId;
    }

    public void setPddaMessageId(Integer pddaMessageId) {
        this.pddaMessageId = pddaMessageId;
    }

    public Integer getCourtId() {
        return courtId;
    }

    public void setCourtId(Integer courtId) {
        this.courtId = courtId;
    }

    public Integer getCourtRoomId() {
        return courtRoomId;
    }

    public void setCourtRoomId(Integer courtRoomId) {
        this.courtRoomId = courtRoomId;
    }

    public String getPddaMessageGuid() {
        return pddaMessageGuid;
    }

    public void setPddaMessageGuid(String pddaMessageGuid) {
        this.pddaMessageGuid = pddaMessageGuid;
    }

    public Integer getPddaMessageTypeId() {
        return pddaMessageTypeId;
    }

    public void setPddaMessageTypeId(Integer pddaMessageTypeId) {
        this.pddaMessageTypeId = pddaMessageTypeId;
    }

    public Long getPddaMessageDataId() {
        return pddaMessageDataId;
    }

    public void setPddaMessageDataId(Long pddaMessageDataId) {
        this.pddaMessageDataId = pddaMessageDataId;
    }

    public Integer getPddaBatchId() {
        return pddaBatchId;
    }

    public void setPddaBatchId(Integer pddaBatchId) {
        this.pddaBatchId = pddaBatchId;
    }

    public LocalDateTime getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(LocalDateTime timeSent) {
        this.timeSent = timeSent;
    }

    public String getCpDocumentName() {
        return cpDocumentName;
    }

    public void setCpDocumentName(String cpDocumentName) {
        this.cpDocumentName = cpDocumentName;
    }

    public String getCpDocumentStatus() {
        return cpDocumentStatus;
    }

    public void setCpDocumentStatus(String cpDocumentStatus) {
        this.cpDocumentStatus = cpDocumentStatus;
    }

    public String getCpResponseGenerated() {
        return cpResponseGenerated;
    }

    public void setCpResponseGenerated(String cpResponseGenerated) {
        this.cpResponseGenerated = cpResponseGenerated;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getCppStagingInboundId() {
        return cppStagingInboundId;
    }

    public void setCppStagingInboundId(Integer cppStagingInboundId) {
        this.cppStagingInboundId = cppStagingInboundId;
    }

    public String getObsInd() {
        return obsInd;
    }

    public void setObsInd(String obsInd) {
        this.obsInd = obsInd;
    }

}
