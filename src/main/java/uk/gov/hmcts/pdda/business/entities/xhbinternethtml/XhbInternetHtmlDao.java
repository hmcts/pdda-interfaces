package uk.gov.hmcts.pdda.business.entities.xhbinternethtml;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Version;
import uk.gov.hmcts.pdda.business.entities.AbstractDao;

import java.time.LocalDateTime;

@SuppressWarnings({"PMD"})
@Entity(name = "XHB_INTERNET_HTML")
public class XhbInternetHtmlDao extends AbstractDao implements java.io.Serializable {

    private static final long serialVersionUID = 1L;
    
    private static final String TOSTRING_ID = "Id = ";
    private static final String TOSTRING_STATUS = ", Status=";
    private static final String TOSTRING_COURTID = ", CourtId=";
    private static final String TOSTRING_HTMLBLOBID = ", HtmlBlobId=";
    private static final String TOSTRING_LASTUPDATEDATE = ", LastUpdateDate=";
    private static final String TOSTRING_CREATIONDATE = ", CreationDate=";
    private static final String TOSTRING_LASTUPDATEDBY = ", LastUpdatedBy=";
    private static final String TOSTRING_CREATEDBY = ", CreatedBy=";
    private static final String TOSTRING_VERSION = ", Version=";
    private static final String TOSTRING_EOL = "\n";

    @Id
    @GeneratedValue(generator = "xhb_cpp_staging_inbound_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "xhb_cpp_staging_inbound_seq",
        sequenceName = "xhb_cpp_staging_inbound_seq", allocationSize = 1)
    @Column(name = "INTERNET_HTML_ID")
    private Integer internetHtmlId;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "COURT_ID")
    private Integer courtId;

    @Column(name = "HTML_BLOB_ID")
    private Long htmlBlobId;

    @Column(name = "LAST_UPDATE_DATE")
    private LocalDateTime lastUpdateDate;

    @Column(name = "CREATION_DATE")
    private LocalDateTime creationDate;

    @Column(name = "LAST_UPDATED_BY")
    private String lastUpdatedBy;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Version
    @Column(name = "VERSION")
    private Integer version;

    public XhbInternetHtmlDao() {
        super();
    }

    public XhbInternetHtmlDao(XhbInternetHtmlDao otherData) {
        this();
        setInternetHtmlId(otherData.getInternetHtmlId());
        setCourtId(otherData.getCourtId());
        setStatus(otherData.getStatus());
        setHtmlBlobId(otherData.getHtmlBlobId());
        setLastUpdateDate(otherData.getLastUpdateDate());
        setCreationDate(otherData.getCreationDate());
        setLastUpdatedBy(otherData.getLastUpdatedBy());
        setCreatedBy(otherData.getCreatedBy());
        setVersion(otherData.getVersion());

    }

    public XhbInternetHtmlDao(Integer cppStagingInboundId, Integer version) {
        this();
        setInternetHtmlId(cppStagingInboundId);
        setVersion(version);
    }


    public Integer getInternetHtmlId() {
        return this.internetHtmlId;
    }

    public void setInternetHtmlId(Integer internetHtmlId) {
        this.internetHtmlId = internetHtmlId;
    }

    public Integer getCourtId() {
        return this.courtId;
    }

    public void setCourtId(Integer courtId) {
        this.courtId = courtId;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public Long getHtmlBlobId() {
        return this.htmlBlobId;
    }
    
    public void setHtmlBlobId(Long htmlBlobId) {
        this.htmlBlobId = htmlBlobId;
    }
    
    public LocalDateTime getLastUpdateDate() {
        return this.lastUpdateDate;
    }
    
    public void setLastUpdateDate(LocalDateTime lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }
    
    public LocalDateTime getCreationDate() {
        return this.creationDate;
    }
    
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
    
    public String getLastUpdatedBy() {
        return this.lastUpdatedBy;
    }
    
    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }
       
    public String getCreatedBy() {
        return this.createdBy;
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

    @Override
    public Object getPrimaryKey() {
        return getInternetHtmlId();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(TOSTRING_ID).append(getInternetHtmlId())
            .append(TOSTRING_STATUS).append(getStatus())
            .append(TOSTRING_COURTID).append(getCourtId())
            .append(TOSTRING_HTMLBLOBID).append(getHtmlBlobId())
            .append(TOSTRING_LASTUPDATEDATE).append(getLastUpdateDate())
            .append(TOSTRING_CREATIONDATE).append(getCreationDate())
            .append(TOSTRING_LASTUPDATEDBY).append(getLastUpdatedBy())
            .append(TOSTRING_CREATEDBY).append(getCreatedBy())
            .append(TOSTRING_VERSION).append(getVersion())
            .append(TOSTRING_EOL);
        return sb.toString();
    }

}