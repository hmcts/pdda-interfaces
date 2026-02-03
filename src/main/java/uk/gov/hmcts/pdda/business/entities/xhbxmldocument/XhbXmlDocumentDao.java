package uk.gov.hmcts.pdda.business.entities.xhbxmldocument;

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
@Entity(name = "XHB_XML_DOCUMENT")
@NamedQuery(name = "XHB_XML_DOCUMENT.findDocumentByClobId",
    query = "SELECT o from XHB_XML_DOCUMENT o WHERE o.xmlDocumentClobId = "
        + ":xmlDocumentClobId AND (SUBSTR(o.documentType,1,2) IN ('DL','FL','WL') "
        + "OR SUBSTR(o.documentType,1,3) IN ('IWP') ) "
        + "AND (cast(:timeDelay as timestamp) IS NULL OR o.creationDate <= :timeDelay) "
        + "ORDER BY o.xmlDocumentId DESC")
@NamedQuery(name = "XHB_XML_DOCUMENT.findByXmlDocumentClobId",
    query = "SELECT o from XHB_XML_DOCUMENT o WHERE o.xmlDocumentClobId = :xmlDocumentClobId "
        + "ORDER BY o.xmlDocumentId DESC")
@NamedQuery(name = "XHB_XML_DOCUMENT.findListByClobId",
    query = "SELECT o from XHB_XML_DOCUMENT o WHERE o.xmlDocumentClobId = "
        + ":xmlDocumentClobId AND SUBSTR(o.documentType,1,2) IN ('DL','FL','WL') "
        + "AND (cast(:timeDelay as timestamp) IS NULL OR o.creationDate <= :timeDelay) "
        + "ORDER BY o.xmlDocumentId DESC")
@NamedQuery(name = "XHB_XML_DOCUMENT.findJsonDocuments",
    query = "SELECT o from XHB_XML_DOCUMENT o WHERE o.documentType IN ('JSN', 'IWP') "
        + "AND o.status = :status")
public class XhbXmlDocumentDao extends AbstractDao implements Serializable {

    private static final long serialVersionUID = -2723700446890851397L;

    @Id
    @GeneratedValue(generator = "xhb_xml_document_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "xhb_xml_document_seq", sequenceName = "xhb_xml_document_seq", allocationSize = 1)

    @Column(name = "XML_DOCUMENT_ID")
    private Integer xmlDocumentId;

    @Column(name = "DATE_CREATED")
    private LocalDateTime dateCreated;

    @Column(name = "DOCUMENT_TITLE")
    private String documentTitle;

    @Column(name = "XML_DOCUMENT_CLOB_ID")
    private Long xmlDocumentClobId;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "DOCUMENT_TYPE")
    private String documentType;

    @Column(name = "EXPIRY_DATE")
    private LocalDateTime expiryDate;

    @Column(name = "COURT_ID")
    private Integer courtId;

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

    public XhbXmlDocumentDao() {
        super();
    }

    public XhbXmlDocumentDao(final XhbXmlDocumentDao otherData) {
        this();
        setXmlDocumentId(otherData.getXmlDocumentId());
        setDateCreated(otherData.getDateCreated());
        setDocumentTitle(otherData.getDocumentTitle());
        setXmlDocumentClobId(otherData.getXmlDocumentClobId());
        setStatus(otherData.getStatus());
        setExpiryDate(otherData.getExpiryDate());
        setDocumentType(otherData.getDocumentType());
        setCourtId(otherData.getCourtId());
        setLastUpdateDate(otherData.getLastUpdateDate());
        setCreationDate(otherData.getCreationDate());
        setLastUpdatedBy(otherData.getLastUpdatedBy());
        setCreatedBy(otherData.getCreatedBy());
        setVersion(otherData.getVersion());
    }

    @Override
    public Integer getPrimaryKey() {
        return getXmlDocumentId();
    }

    public Integer getXmlDocumentId() {
        return xmlDocumentId;
    }

    public void setXmlDocumentId(Integer xmlDocumentId) {
        this.xmlDocumentId = xmlDocumentId;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    public Long getXmlDocumentClobId() {
        return xmlDocumentClobId;
    }

    public void setXmlDocumentClobId(Long xmlDocumentClobId) {
        this.xmlDocumentClobId = xmlDocumentClobId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Integer getCourtId() {
        return courtId;
    }

    public void setCourtId(Integer courtId) {
        this.courtId = courtId;
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

}
