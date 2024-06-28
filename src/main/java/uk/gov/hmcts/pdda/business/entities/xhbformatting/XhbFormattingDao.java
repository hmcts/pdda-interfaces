package uk.gov.hmcts.pdda.business.entities.xhbformatting;

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

@SuppressWarnings({"PMD.TooManyFields", "PMD.ConstructorCallsOverridableMethod"})
@Entity(name = "XHB_FORMATTING")
@NamedQuery(name = "XHB_FORMATTING.findByDocumentType",
    query = "SELECT o from XHB_FORMATTING o WHERE o.documentType = :docType ORDER BY o.formattingId")
@NamedQuery(name = "XHB_FORMATTING.findByDocTypeCourtIdDateRange",
    query = "SELECT o from XHB_FORMATTING o WHERE o.documentType = :docType AND o.courtId = :courtId AND "
        + "o.lastUpdateDate BETWEEN :startDate AND :endDate ORDER BY o.formattingId")
@NamedQuery(name = "XHB_FORMATTING.findByFormatStatus",
    query = "SELECT o from XHB_FORMATTING o WHERE o.formatStatus = :formatStatus ORDER BY o.formattingId")
@NamedQuery(name = "XHB_FORMATTING.findByDocumentAndClob",
    query = "SELECT o from XHB_FORMATTING o, XHB_CLOB c WHERE o.xmlDocumentClobId = c.clobId AND o.documentType "
        + "= :docType AND o.courtId = :courtId AND o.formatStatus!='ND' AND o.language =:language AND c.clobData "
        + "like concat('%<courtsitename>',concat(:courtSiteName,'</courtsitename>%')) ORDER BY o.dateIn DESC")
public class XhbFormattingDao extends AbstractDao implements Serializable {

    private static final long serialVersionUID = -2723700446890851397L;

    @Id
    @GeneratedValue(generator = "xhb_formatting_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "xhb_formatting_seq", sequenceName = "xhb_formatting_seq",
        allocationSize = 1)
    @Column(name = "FORMATTING_ID")
    private Integer formattingId;

    @Column(name = "DATE_IN")
    private LocalDateTime dateIn;

    @Column(name = "FORMAT_STATUS")
    private String formatStatus;

    @Column(name = "DISTRIBUTION_TYPE")
    private String distributionType;

    @Column(name = "MIME_TYPE")
    private String mimeType;

    @Column(name = "DOCUMENT_TYPE")
    private String documentType;

    @Column(name = "COURT_ID")
    private Integer courtId;

    @Column(name = "FORMATTED_DOCUMENT_BLOB_ID")
    private Long formattedDocumentBlobId;

    @Column(name = "XML_DOCUMENT_CLOB_ID")
    private Long xmlDocumentClobId;

    @Column(name = "LANGUAGE")
    private String language;

    @Column(name = "COUNTRY")
    private String country;

    @Column(name = "MAJOR_SCHEMA_VERSION")
    private Integer majorSchemaVersion;

    @Column(name = "MINOR_SCHEMA_VERSION")
    private Integer minorSchemaVersion;

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

    public XhbFormattingDao() {
        super();
    }

    public XhbFormattingDao(final XhbFormattingDao otherData) {
        this();
        setFormattingId(otherData.getFormattingId());
        setDateIn(otherData.getDateIn());
        setFormatStatus(otherData.getFormatStatus());
        setDistributionType(otherData.getDistributionType());
        setMimeType(otherData.getMimeType());
        setDocumentType(otherData.getDocumentType());
        setCourtId(otherData.getCourtId());
        setFormattedDocumentBlobId(otherData.getFormattedDocumentBlobId());
        setXmlDocumentClobId(otherData.getXmlDocumentClobId());
        setLanguage(otherData.getLanguage());
        setCountry(otherData.getCountry());
        setMajorSchemaVersion(otherData.getMajorSchemaVersion());
        setMinorSchemaVersion(otherData.getMinorSchemaVersion());
        setLastUpdateDate(otherData.getLastUpdateDate());
        setCreationDate(otherData.getCreationDate());
        setLastUpdatedBy(otherData.getLastUpdatedBy());
        setCreatedBy(otherData.getCreatedBy());
        setVersion(otherData.getVersion());
    }

    public Integer getPrimaryKey() {
        return getFormattingId();
    }

    public Integer getFormattingId() {
        return formattingId;
    }

    public void setFormattingId(final Integer formattingId) {
        this.formattingId = formattingId;
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

    public String getDistributionType() {
        return distributionType;
    }

    public void setDistributionType(final String distributionType) {
        this.distributionType = distributionType;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(final String mimeType) {
        this.mimeType = mimeType;
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

    public Long getFormattedDocumentBlobId() {
        return formattedDocumentBlobId;
    }

    public void setFormattedDocumentBlobId(final Long formattedDocumentBlobId) {
        this.formattedDocumentBlobId = formattedDocumentBlobId;
    }

    public Long getXmlDocumentClobId() {
        return xmlDocumentClobId;
    }

    public void setXmlDocumentClobId(final Long xmlDocumentClobId) {
        this.xmlDocumentClobId = xmlDocumentClobId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(final String language) {
        this.language = language;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(final String country) {
        this.country = country;
    }

    public Integer getMajorSchemaVersion() {
        return majorSchemaVersion;
    }

    public void setMajorSchemaVersion(final Integer majorSchemaVersion) {
        this.majorSchemaVersion = majorSchemaVersion;
    }

    public Integer getMinorSchemaVersion() {
        return minorSchemaVersion;
    }

    public void setMinorSchemaVersion(final Integer minorSchemaVersion) {
        this.minorSchemaVersion = minorSchemaVersion;
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
