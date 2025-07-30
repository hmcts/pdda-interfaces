package uk.gov.hmcts.pdda.business.entities.xhbcppformattingmerge;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import uk.gov.hmcts.pdda.business.entities.AbstractVersionedDao;

import java.io.Serializable;

@SuppressWarnings({"PMD.ConstructorCallsOverridableMethod"})
@Entity(name = "XHB_CPP_FORMATTING_MERGE")
public class XhbCppFormattingMergeDao extends AbstractVersionedDao implements Serializable {

    private static final long serialVersionUID = -2723700446890851397L;

    @Id
    @GeneratedValue(generator = "xhb_cpp_formatting_merge_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "xhb_cpp_formatting_merge_seq",
        sequenceName = "xhb_cpp_formatting_merge_seq", allocationSize = 1)
    @Column(name = "CPP_FORMATTING_MERGE_ID")
    private Integer cppFormattingMergeId;

    @Column(name = "CPP_FORMATTING_ID")
    private Integer cppFormattingId;

    @Column(name = "FORMATTING_ID")
    private Integer formattingId;

    @Column(name = "XHIBIT_CLOB_ID")
    private Long xhibitClobId;

    @Column(name = "COURT_ID")
    private Integer courtId;

    @Column(name = "LANGUAGE")
    private String language;

    @Column(name = "OBS_IND")
    private String obsInd;

    public XhbCppFormattingMergeDao() {
        super();
    }

    public XhbCppFormattingMergeDao(final XhbCppFormattingMergeDao otherData) {
        this();
        setCppFormattingMergeId(otherData.getCppFormattingMergeId());
        setCppFormattingId(otherData.getCppFormattingId());
        setFormattingId(otherData.getFormattingId());
        setCourtId(otherData.getCourtId());
        setXhibitClobId(otherData.getXhibitClobId());
        setLanguage(otherData.getLanguage());
        setObsInd(otherData.getObsInd());
        setLastUpdateDate(otherData.getLastUpdateDate());
        setCreationDate(otherData.getCreationDate());
        setLastUpdatedBy(otherData.getLastUpdatedBy());
        setCreatedBy(otherData.getCreatedBy());
        setVersion(otherData.getVersion());
    }

    @Override
    public Integer getPrimaryKey() {
        return getCppFormattingMergeId();
    }

    public Integer getCppFormattingMergeId() {
        return cppFormattingMergeId;
    }

    public void setCppFormattingMergeId(Integer cppFormattingMergeId) {
        this.cppFormattingMergeId = cppFormattingMergeId;
    }

    public Integer getCppFormattingId() {
        return cppFormattingId;
    }

    public void setCppFormattingId(final Integer cppFormattingId) {
        this.cppFormattingId = cppFormattingId;
    }

    public Integer getFormattingId() {
        return formattingId;
    }

    public void setFormattingId(Integer formattingId) {
        this.formattingId = formattingId;
    }

    public Integer getCourtId() {
        return courtId;
    }

    public void setCourtId(final Integer courtId) {
        this.courtId = courtId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Long getXhibitClobId() {
        return xhibitClobId;
    }

    public void setXhibitClobId(final Long xhibitClobId) {
        this.xhibitClobId = xhibitClobId;
    }

    public String getObsInd() {
        return obsInd;
    }

    public void setObsInd(final String obsInd) {
        this.obsInd = obsInd;
    }

}
