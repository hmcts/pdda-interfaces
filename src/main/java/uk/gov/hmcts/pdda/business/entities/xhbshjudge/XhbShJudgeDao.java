package uk.gov.hmcts.pdda.business.entities.xhbshjudge;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import uk.gov.hmcts.pdda.business.entities.AbstractVersionedDao;

import java.io.Serializable;

@SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
@Entity(name = "XHB_SH_JUDGE")
public class XhbShJudgeDao extends AbstractVersionedDao implements Serializable {
    private static final long serialVersionUID = 3144389037216278510L;
    @Id
    @GeneratedValue(generator = "xhb_sh_judge_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "xhb_sh_judge_seq", sequenceName = "xhb_sh_judge_seq",
        allocationSize = 1)

    @Column(name = "SH_JUDGE_ID")
    private Integer shJudgeId;

    @Column(name = "DEPUTY_HCJ")
    private String deputyHcj;
    
    @Column(name = "REF_JUDGE_ID")
    private Integer refJudgeId;

    @Column(name = "SH_ATTENDEE_ID")
    private Integer shAttendeeId;

    public XhbShJudgeDao() {
        super();
    }

    public XhbShJudgeDao(XhbShJudgeDao otherData) {
        this();
        setShJudgeId(otherData.getShJudgeId());
        setDeputyHcj(otherData.getDeputyHcj());
        setRefJudgeId(otherData.getRefJudgeId());
        setShAttendeeId(otherData.getShAttendeeId());
        setLastUpdateDate(otherData.getLastUpdateDate());
        setCreationDate(otherData.getCreationDate());
        setLastUpdatedBy(otherData.getLastUpdatedBy());
        setCreatedBy(otherData.getCreatedBy());
        setVersion(otherData.getVersion());
    }

    @Override
    public Integer getPrimaryKey() {
        return getShJudgeId();
    }

    public Integer getShJudgeId() {
        return shJudgeId;
    }

    public void setShJudgeId(Integer shJudgeId) {
        this.shJudgeId = shJudgeId;
    }

    public String getDeputyHcj() {
        return deputyHcj;
    }

    public void setDeputyHcj(String deputyHcj) {
        this.deputyHcj = deputyHcj;
    }

    public Integer getRefJudgeId() {
        return refJudgeId;
    }

    public void setRefJudgeId(Integer refJudgeId) {
        this.refJudgeId = refJudgeId;
    }
    
    public Integer getShAttendeeId() {
        return shAttendeeId;
    }

    public void setShAttendeeId(Integer shAttendeeId) {
        this.shAttendeeId = shAttendeeId;
    }
}