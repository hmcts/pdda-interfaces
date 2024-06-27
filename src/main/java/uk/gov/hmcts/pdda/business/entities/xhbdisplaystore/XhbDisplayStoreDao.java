package uk.gov.hmcts.pdda.business.entities.xhbdisplaystore;

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
@Entity(name = "XHB_DISPLAY_STORE")
@NamedQuery(name = "XHB_DISPLAY_STORE.findByRetrievalCode",
    query = "SELECT o from XHB_DISPLAY_STORE o WHERE o.retrievalCode = :retrievalCode AND (o.obsInd is null OR "
        + "o.obsInd = 'N' OR o.obsInd = ' ')")
public class XhbDisplayStoreDao extends AbstractVersionedDao implements Serializable {

    private static final long serialVersionUID = 3149855566341480919L;

    @Id
    @GeneratedValue(generator = "xhb_display_store_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "xhb_display_store_seq", sequenceName = "xhb_display_store_seq",
        allocationSize = 1)
    @Column(name = "DISPLAY_STORE_ID")
    private Long displayStoreId;

    @Column(name = "RETRIEVAL_CODE")
    private String retrievalCode;

    @Column(name = "CONTENT")
    private String content;

    @Column(name = "OBS_IND")
    private String obsInd;

    public XhbDisplayStoreDao() {
        super();
    }

    public XhbDisplayStoreDao(Long displayStoreId, String retrievalCode, String content,
        LocalDateTime lastUpdateDate, LocalDateTime creationDate, String lastUpdatedBy,
        String createdBy, Integer version, String obsInd) {
        this();
        setDisplayStoreId(displayStoreId);
        setRetrievalCode(retrievalCode);
        setContent(content);
        setObsInd(obsInd);
        setLastUpdateDate(lastUpdateDate);
        setCreationDate(creationDate);
        setLastUpdatedBy(lastUpdatedBy);
        setCreatedBy(createdBy);
        setVersion(version);
    }

    public XhbDisplayStoreDao(XhbDisplayStoreDao otherData) {
        this();
        setDisplayStoreId(otherData.getDisplayStoreId());
        setRetrievalCode(otherData.getRetrievalCode());
        setContent(otherData.getContent());
        setObsInd(otherData.getObsInd());
        setLastUpdateDate(otherData.getLastUpdateDate());
        setCreationDate(otherData.getCreationDate());
        setLastUpdatedBy(otherData.getLastUpdatedBy());
        setCreatedBy(otherData.getCreatedBy());
        setVersion(otherData.getVersion());
    }

    public Long getPrimaryKey() {
        return getDisplayStoreId();
    }

    public Long getDisplayStoreId() {
        return displayStoreId;
    }

    private void setDisplayStoreId(Long displayStoreId) {
        this.displayStoreId = displayStoreId;
    }

    public String getRetrievalCode() {
        return retrievalCode;
    }

    public void setRetrievalCode(String retrievalCode) {
        this.retrievalCode = retrievalCode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getObsInd() {
        return obsInd;
    }

    private void setObsInd(String obsInd) {
        this.obsInd = obsInd;
    }

}
