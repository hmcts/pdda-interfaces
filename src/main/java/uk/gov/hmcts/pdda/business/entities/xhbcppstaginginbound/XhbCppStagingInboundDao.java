package uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import uk.gov.hmcts.pdda.business.entities.AbstractDao;

import java.time.LocalDateTime;

@SuppressWarnings({"PMD.TooManyFields", "PMD.ConstructorCallsOverridableMethod"})
@Entity(name = "XHB_CPP_STAGING_INBOUND")
@NamedQuery(name = "XHB_CPP_STAGING_INBOUND.findNextDocumentByValidationStatus",
    query = "SELECT o from XHB_CPP_STAGING_INBOUND o WHERE o.timeLoaded >= :timeLoaded "
        + "AND (o.obsInd IS NULL OR o.obsInd='N') "
        + "AND ((o.validationStatus IS NULL AND :validationStatus IS NULL) OR (o.validationStatus = "
        + ":validationStatus)) ORDER by o.timeLoaded")
@NamedQuery(name = "XHB_CPP_STAGING_INBOUND.findNextDocumentByProcessingStatus",
    query = "SELECT o from XHB_CPP_STAGING_INBOUND o WHERE o.timeLoaded >= :timeLoaded "
        + "AND (o.obsInd IS NULL OR o.obsInd='N') "
        + "AND ((o.processingStatus IS NULL AND :processingStatus IS NULL) OR (o.processingStatus = "
        + ":processingStatus)) ORDER by o.timeLoaded")
@NamedQuery(name = "XHB_CPP_STAGING_INBOUND.findNextDocumentByValidationAndProcessingStatus",
    query = "SELECT o from XHB_CPP_STAGING_INBOUND o WHERE o.timeLoaded >= :timeLoaded "
        + "AND (o.obsInd IS NULL OR o.obsInd='N') "
        + "AND ((o.validationStatus IS NULL AND :validationStatus IS NULL) OR (o.validationStatus = "
        + ":validationStatus)) "
        + "AND ((o.processingStatus IS NULL AND :processingStatus IS NULL) OR (o.processingStatus = "
        + ":processingStatus)) ORDER by o.timeLoaded")
@NamedQuery(name = "XHB_CPP_STAGING_INBOUND.findNextDocument",
    query = "SELECT o from XHB_CPP_STAGING_INBOUND o WHERE "
        + "((o.validationStatus IS NULL AND :validationStatus IS NULL) OR (o.validationStatus = "
        + ":validationStatus)) "
        + "AND ((o.processingStatus IS NULL AND :processingStatus IS NULL) OR (o.processingStatus = "
        + ":processingStatus)) ORDER by o.timeLoaded")
@NamedQuery(name = "XHB_CPP_STAGING_INBOUND.findUnrespondedCPPMessages",
    query = "SELECT o from XHB_CPP_STAGING_INBOUND o WHERE "
        + "(o.validationStatus = 'VF' OR o.validationStatus = 'VS') "
        + "AND o.acknowledgmentStatus IS NULL AND (o.obsInd IS NULL OR o.obsInd='N') "
        + "ORDER by o.cppStagingInboundId")
@NamedQuery(name = "XHB_CPP_STAGING_INBOUND.findDocumentByDocumentName",
    query = "SELECT o from XHB_CPP_STAGING_INBOUND o WHERE "
        + "(o.documentName = :documentName) AND (o.obsInd IS NULL OR o.obsInd='N')")
public class XhbCppStagingInboundDao extends AbstractDao implements java.io.Serializable {

    private static final long serialVersionUID = 6619741714613299473L;
    private static final String TOSTRING_ID = "Id = ";
    private static final String TOSTRING_DOCUMENT = ", Document Name=";
    private static final String TOSTRING_COURTCODE = ", CourtCode=";
    private static final String TOSTRING_DOCUMENTTYPE = ", Document Type=";
    private static final String TOSTRING_TIMELOADED = ", Time Loaded=";
    private static final String TOSTRING_CLOBID = ", Clob Id=";
    private static final String TOSTRING_VALIDATIONSTATUS = ", Validation Status=";
    private static final String TOSTRING_ACKNOWLEDGEMENTSTATUS = ", Acknowledgment Status=";
    private static final String TOSTRING_PROCESSINGERROR = ", Processing Status=";
    private static final String TOSTRING_VALIDATIONERROR = ", Validation Error Message=";
    private static final String TOSTRING_VERSION = ", Version=";
    private static final String TOSTRING_EOL = "\n";

    @Id
    @GeneratedValue(generator = "xhb_cpp_staging_inbound_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "xhb_cpp_staging_inbound_seq",
        sequenceName = "xhb_cpp_staging_inbound_seq", allocationSize = 1)
    @Column(name = "CPP_STAGING_INBOUND_ID")
    private Integer cppStagingInboundId;

    @Column(name = "DOCUMENT_NAME")
    private String documentName;

    @Column(name = "COURT_CODE")
    private String courtCode;

    @Column(name = "DOCUMENT_TYPE")
    private String documentType;

    @Column(name = "TIME_LOADED")
    private LocalDateTime timeLoaded;

    @Column(name = "CLOB_ID")
    private Long clobId;

    @Column(name = "VALIDATION_STATUS")
    private String validationStatus;

    @Column(name = "ACKNOWLEDGMENT_STATUS")
    private String acknowledgmentStatus;

    @Column(name = "PROCESSING_STATUS")
    private String processingStatus;

    @Column(name = "VALIDATION_ERROR_MESSAGE")
    private String validationErrorMessage;

    @Column(name = "OBS_IND")
    private String obsInd;

    @Column(name = "LAST_UPDATE_DATE")
    private LocalDateTime lastUpdateDate;

    @Column(name = "CREATION_DATE")
    private LocalDateTime creationDate;

    @Column(name = "LAST_UPDATED_BY")
    private String lastUpdatedBy;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "VERSION")
    private Integer version;

    public XhbCppStagingInboundDao() {
        super();
    }

    public XhbCppStagingInboundDao(XhbCppStagingInboundDao otherData) {
        this();
        setCppStagingInboundId(otherData.getCppStagingInboundId());
        setDocumentName(otherData.getDocumentName());
        setCourtCode(otherData.getCourtCode());
        setDocumentType(otherData.getDocumentType());
        setTimeLoaded(otherData.getTimeLoaded());
        setClobId(otherData.getClobId());
        setValidationStatus(otherData.getValidationStatus());
        setAcknowledgmentStatus(otherData.getAcknowledgmentStatus());
        setProcessingStatus(otherData.getProcessingStatus());
        setValidationErrorMessage(otherData.getValidationErrorMessage());
        setObsInd(otherData.getObsInd());
        setLastUpdateDate(otherData.getLastUpdateDate());
        setCreationDate(otherData.getCreationDate());
        setLastUpdatedBy(otherData.getLastUpdatedBy());
        setCreatedBy(otherData.getCreatedBy());
        setVersion(otherData.getVersion());

    }

    public XhbCppStagingInboundDao(Integer cppStagingInboundId, Integer version) {
        this();
        setCppStagingInboundId(cppStagingInboundId);
        setVersion(version);
    }

    @Override
    public Integer getPrimaryKey() {
        return getCppStagingInboundId();
    }

    // default getters and setters

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public Integer getCppStagingInboundId() {
        return cppStagingInboundId;
    }

    public String getCourtCode() {
        return courtCode;
    }

    public void setCourtCode(String courtCode) {
        this.courtCode = courtCode;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public LocalDateTime getTimeLoaded() {
        return timeLoaded;
    }

    public void setTimeLoaded(LocalDateTime timeLoaded) {
        this.timeLoaded = timeLoaded;
    }

    public Long getClobId() {
        return clobId;
    }

    public void setClobId(Long clobId) {
        this.clobId = clobId;
    }

    public String getValidationStatus() {
        return validationStatus;
    }

    public void setValidationStatus(String validationStatus) {
        this.validationStatus = validationStatus;
    }

    public String getAcknowledgmentStatus() {
        return acknowledgmentStatus;
    }

    public void setAcknowledgmentStatus(String acknowledgmentStatus) {
        this.acknowledgmentStatus = acknowledgmentStatus;
    }

    public String getProcessingStatus() {
        return processingStatus;
    }

    public void setProcessingStatus(String processingStatus) {
        this.processingStatus = processingStatus;
    }

    public String getValidationErrorMessage() {
        return validationErrorMessage;
    }

    public void setValidationErrorMessage(String validationErrorMessage) {
        this.validationErrorMessage = validationErrorMessage;
    }

    public String getObsInd() {
        return obsInd;
    }

    public void setObsInd(String obsInd) {
        this.obsInd = obsInd;
    }

    public LocalDateTime getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(LocalDateTime lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public Integer getVersion() {
        return version;
    }

    @Override
    public void setVersion(Integer version) {
        this.version = version;
    }

    public void setCppStagingInboundId(Integer cppStagingInboundId) {
        this.cppStagingInboundId = cppStagingInboundId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(TOSTRING_ID).append(this.getCppStagingInboundId()).append(TOSTRING_DOCUMENT)
            .append(this.getDocumentName()).append(TOSTRING_COURTCODE).append(this.getCourtCode())
            .append(TOSTRING_DOCUMENTTYPE).append(this.getDocumentType())
            .append(TOSTRING_TIMELOADED).append(this.getTimeLoaded()).append(TOSTRING_CLOBID)
            .append(this.getClobId()).append(TOSTRING_VALIDATIONSTATUS)
            .append(this.getValidationStatus()).append(TOSTRING_ACKNOWLEDGEMENTSTATUS)
            .append(this.getAcknowledgmentStatus()).append(TOSTRING_PROCESSINGERROR)
            .append(this.getProcessingStatus()).append(TOSTRING_VALIDATIONERROR)
            .append(this.getValidationErrorMessage()).append(TOSTRING_VERSION)
            .append(this.getVersion()).append(TOSTRING_EOL);
        return sb.toString();
    }

}
