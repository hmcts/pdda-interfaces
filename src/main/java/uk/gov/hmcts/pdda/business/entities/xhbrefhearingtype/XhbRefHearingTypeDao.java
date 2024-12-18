package uk.gov.hmcts.pdda.business.entities.xhbrefhearingtype;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import uk.gov.hmcts.pdda.business.entities.AbstractVersionedDao;

import java.io.Serializable;

@SuppressWarnings({"PMD.ConstructorCallsOverridableMethod"})
@Entity(name = "XHB_REF_HEARING_TYPE")
@NamedQuery(name = "XHB_REF_HEARING_TYPE.findByHearingType",
    query = "SELECT o from XHB_REF_HEARING_TYPE o WHERE o.courtId = :courtId AND o.hearingTypeCode = :hearingTypeCode "
        + "AND o.category = :category")
public class XhbRefHearingTypeDao extends AbstractVersionedDao implements Serializable {

    private static final long serialVersionUID = -6788003970955114552L;

    @Id
    @GeneratedValue(generator = "xhb_ref_hearing_type_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "xhb_ref_hearing_type_seq", sequenceName = "xhb_ref_hearing_type_seq",
        allocationSize = 1)
    @Column(name = "REF_HEARING_TYPE_ID")
    private Integer refHearingTypeId;

    @Column(name = "HEARING_TYPE_CODE")
    private String hearingTypeCode;

    @Column(name = "HEARING_TYPE_DESC")
    private String hearingTypeDesc;

    @Column(name = "CATEGORY")
    private String category;

    @Column(name = "SEQ_NO")
    private Integer seqNo;

    @Column(name = "LIST_SEQUENCE")
    private Integer listSequence;

    @Column(name = "COURT_ID")
    private Integer courtId;

    @Column(name = "OBS_IND")
    private String obsInd;

    public XhbRefHearingTypeDao() {
        super();
    }

    public XhbRefHearingTypeDao(XhbRefHearingTypeDao otherData) {
        this();
        setRefHearingTypeId(otherData.getRefHearingTypeId());
        setHearingTypeCode(otherData.getHearingTypeCode());
        setHearingTypeDesc(otherData.getHearingTypeDesc());
        setCategory(otherData.getCategory());
        setSeqNo(otherData.getSeqNo());
        setListSequence(otherData.getListSequence());
        setCourtId(otherData.getCourtId());
        setObsInd(otherData.getObsInd());
        setLastUpdateDate(otherData.getLastUpdateDate());
        setCreationDate(otherData.getCreationDate());
        setLastUpdatedBy(otherData.getLastUpdatedBy());
        setCreatedBy(otherData.getCreatedBy());
        setVersion(otherData.getVersion());
    }

    public Integer getPrimaryKey() {
        return getRefHearingTypeId();
    }

    public Integer getRefHearingTypeId() {
        return refHearingTypeId;
    }

    public void setRefHearingTypeId(Integer refHearingTypeId) {
        this.refHearingTypeId = refHearingTypeId;
    }

    public String getHearingTypeCode() {
        return hearingTypeCode;
    }

    public void setHearingTypeCode(String hearingTypeCode) {
        this.hearingTypeCode = hearingTypeCode;
    }

    public String getHearingTypeDesc() {
        return hearingTypeDesc;
    }

    public void setHearingTypeDesc(String hearingTypeDesc) {
        this.hearingTypeDesc = hearingTypeDesc;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(Integer seqNo) {
        this.seqNo = seqNo;
    }

    public Integer getListSequence() {
        return listSequence;
    }

    public void setListSequence(Integer listSequence) {
        this.listSequence = listSequence;
    }

    public Integer getCourtId() {
        return courtId;
    }

    public void setCourtId(Integer courtId) {
        this.courtId = courtId;
    }

    public String getObsInd() {
        return obsInd;
    }

    public void setObsInd(String obsInd) {
        this.obsInd = obsInd;
    }

}
