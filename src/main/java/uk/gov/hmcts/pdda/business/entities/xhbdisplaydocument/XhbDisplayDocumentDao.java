package uk.gov.hmcts.pdda.business.entities.xhbdisplaydocument;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import uk.gov.hmcts.pdda.business.entities.AbstractVersionedDao;

import java.io.Serializable;

@SuppressWarnings({"PMD.ConstructorCallsOverridableMethod"})
@Entity(name = "XHB_DISPLAY_DOCUMENT")
public class XhbDisplayDocumentDao extends AbstractVersionedDao implements Serializable {

    private static final long serialVersionUID = -2723700446890851397L;

    @Id
    @GeneratedValue(generator = "xhb_display_document_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "xhb_display_document_seq", sequenceName = "xhb_display_document_seq",
        allocationSize = 1)
    @Column(name = "DISPLAY_DOCUMENT_ID")
    private Integer displayDocumentId;

    @Column(name = "DESCRIPTION_CODE")
    private String descriptionCode;

    @Column(name = "DEFAULT_PAGE_DELAY")
    private Integer defaultPageDelay;

    @Column(name = "MULTIPLE_COURT_YN")
    private String multipleCourtYn;

    @Column(name = "COUNTRY")
    private String country;

    @Column(name = "LANGUAGE")
    private String language;

    public XhbDisplayDocumentDao() {
        super();
    }

    public XhbDisplayDocumentDao(final XhbDisplayDocumentDao otherData) {
        this();
        setDisplayDocumentId(otherData.getDisplayDocumentId());
        setDescriptionCode(otherData.getDescriptionCode());
        setDefaultPageDelay(otherData.getDefaultPageDelay());
        setMultipleCourtYn(otherData.getMultipleCourtYn());
        setCountry(otherData.getCountry());
        setLanguage(otherData.getLanguage());
        setLastUpdateDate(otherData.getLastUpdateDate());
        setCreationDate(otherData.getCreationDate());
        setLastUpdatedBy(otherData.getLastUpdatedBy());
        setCreatedBy(otherData.getCreatedBy());
        setVersion(otherData.getVersion());
    }

    public Integer getPrimaryKey() {
        return getDisplayDocumentId();
    }

    public Integer getDisplayDocumentId() {
        return displayDocumentId;
    }

    public void setDisplayDocumentId(final Integer displayDocumentId) {
        this.displayDocumentId = displayDocumentId;
    }

    public String getDescriptionCode() {
        return descriptionCode;
    }

    public void setDescriptionCode(final String descriptionCode) {
        this.descriptionCode = descriptionCode;
    }

    public Integer getDefaultPageDelay() {
        return defaultPageDelay;
    }

    public void setDefaultPageDelay(final Integer defaultPageDelay) {
        this.defaultPageDelay = defaultPageDelay;
    }

    public String getMultipleCourtYn() {
        return multipleCourtYn;
    }

    public void setMultipleCourtYn(final String multipleCourtYn) {
        this.multipleCourtYn = multipleCourtYn;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(final String country) {
        this.country = country;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(final String language) {
        this.language = language;
    }

}
