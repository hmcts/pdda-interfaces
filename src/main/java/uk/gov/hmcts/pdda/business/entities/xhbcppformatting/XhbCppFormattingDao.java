package uk.gov.hmcts.pdda.business.entities.xhbcppformatting;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import uk.gov.hmcts.pdda.business.entities.AbstractDao;

import java.io.Serializable;
import java.time.LocalDateTime;

@SuppressWarnings({"PMD.ConstructorCallsOverridableMethod"})
@Entity(name = "XHB_CPP_FORMATTING")
@NamedQuery(name = "XHB_CPP_FORMATTING.findByCourtAndDocType",
    query = "SELECT o from XHB_CPP_FORMATTING o WHERE o.courtId = :courtId AND o.documentType "
        + "= :documentType AND o.creationDate >= :creationDate AND (o.obsInd IS NULL OR o.obsInd = 'N') "
        + "AND (o.formatStatus IS NULL OR o.formatStatus <> 'MF') ORDER BY o.creationDate DESC")
@NamedQuery(name = "XHB_CPP_FORMATTING.findLatestByCourtDateInDoc",
    query = "SELECT o from XHB_CPP_FORMATTING o WHERE o.cppFormattingId = (SELECT MAX(o2.cppFormattingId) "
        + "FROM XHB_CPP_FORMATTING AS o2 WHERE o2.courtId = :courtId AND o2.documentType = :documentType AND o2.dateIn "
        + "= :dateIn AND (o2.obsInd IS NULL OR o2.obsInd = 'N'))")
@NamedQuery(name = "XHB_CPP_FORMATTING.findAllNewByDocType",
    query = "SELECT o from XHB_CPP_FORMATTING o WHERE o.documentType >= :documentType AND o.formatStatus = 'ND' AND "
        + "o.creationDate >= :creationDate ORDER BY o.creationDate")
public class XhbCppFormattingDao extends AbstractDao implements Serializable {

    private static final long serialVersionUID = -2723700446890851397L;

    @Id
    @GeneratedValue(generator = "xhb_cpp_formatting_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "xhb_cpp_formatting_seq", sequenceName = "xhb_cpp_formatting_seq",
        allocationSize = 1)
    @Column(name = "CPP_FORMATTING_ID")
    private Integer cppFormattingId;

    @Column(name = "STAGING_TABLE_ID")
    private Integer stagingTableId;

    @Column(name = "DATE_IN")
    private LocalDateTime dateIn;

    @Column(name = "FORMAT_STATUS")
    private String formatStatus;

    @Column(name = "DOCUMENT_TYPE")
    private String documentType;

    @Column(name = "COURT_ID")
    private Integer courtId;

    @Column(name = "XML_DOCUMENT_CLOB_ID")
    private Long xmlDocumentClobId;

    @Column(name = "ERROR_MESSAGE")
    private String errorMessage;

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

    public XhbCppFormattingDao() {
        super();
    }

    public XhbCppFormattingDao(final XhbCppFormattingDao otherData) {
        this();
        setCppFormattingId(otherData.getCppFormattingId());
        setStagingTableId(otherData.getStagingTableId());
        setDateIn(otherData.getDateIn());
        setFormatStatus(otherData.getFormatStatus());
        setDocumentType(otherData.getDocumentType());
        setCourtId(otherData.getCourtId());
        setXmlDocumentClobId(otherData.getXmlDocumentClobId());
        setErrorMessage(otherData.getErrorMessage());
        setObsInd(otherData.getObsInd());
        setLastUpdateDate(otherData.getLastUpdateDate());
        setCreationDate(otherData.getCreationDate());
        setLastUpdatedBy(otherData.getLastUpdatedBy());
        setCreatedBy(otherData.getCreatedBy());
        setVersion(otherData.getVersion());
    }

    public Integer getPrimaryKey() {
        return getCppFormattingId();
    }

    public Integer getCppFormattingId() {
        return cppFormattingId;
    }

    public void setCppFormattingId(final Integer cppFormattingId) {
        this.cppFormattingId = cppFormattingId;
    }

    public Integer getStagingTableId() {
        return stagingTableId;
    }

    public void setStagingTableId(Integer stagingTableId) {
        this.stagingTableId = stagingTableId;
    }

    public LocalDateTime getDateIn() {
        return dateIn;
    }

    public void setDateIn(final LocalDateTime dateIn) {
        this.dateIn = dateIn;
    }

    public String getFormatStatus() {
        return formatStatus;
    }

    public void setFormatStatus(final String formatStatus) {
        this.formatStatus = formatStatus;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(final String documentType) {
        this.documentType = documentType;
    }

    public Integer getCourtId() {
        return courtId;
    }

    public void setCourtId(final Integer courtId) {
        this.courtId = courtId;
    }

    public Long getXmlDocumentClobId() {
        return xmlDocumentClobId;
    }

    public void setXmlDocumentClobId(final Long xmlDocumentClobId) {
        this.xmlDocumentClobId = xmlDocumentClobId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getObsInd() {
        return obsInd;
    }

    public void setObsInd(final String obsInd) {
        this.obsInd = obsInd;
    }

    public LocalDateTime getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(final LocalDateTime lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(final LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(final String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public Integer getVersion() {
        return version;
    }

    @Override
    public void setVersion(final Integer version) {
        this.version = version;
    }

}
