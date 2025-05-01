package uk.gov.hmcts.pdda.business.entities.xhbrefjudge;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import uk.gov.hmcts.pdda.business.entities.AbstractVersionedDao;

import java.io.Serializable;

@SuppressWarnings({"PMD.TooManyFields", "PMD.ConstructorCallsOverridableMethod"})
@Entity(name = "XHB_REF_JUDGE")
@NamedQuery(name = "XHB_REF_JUDGE.findScheduledAttendeeJudge",
    query = "SELECT o from XHB_REF_JUDGE o WHERE "
        + "o.refJudgeId IN (SELECT x1.refJudgeId FROM XHB_SCHED_HEARING_ATTENDEE x1 WHERE x1.scheduledHearingId = "
        + ":scheduledHearingId AND x1.attendeeType = 'J' AND "
        + "x1.shAttendeeId = (SELECT MAX(x2.shAttendeeId) FROM XHB_SCHED_HEARING_ATTENDEE x2 WHERE "
        + "x2.scheduledHearingId = :scheduledHearingId AND x2.attendeeType = 'J'))")
@NamedQuery(name = "XHB_REF_JUDGE.findScheduledSittingJudge",
    query = "SELECT o from XHB_REF_JUDGE o WHERE "
        + "o.refJudgeId IN (SELECT x1.refJudgeId FROM XHB_SITTING x1, XHB_SCHEDULED_HEARING x2 WHERE x1.sittingId "
        + "= x2.sittingId AND x2.scheduledHearingId = :scheduledHearingId)")
public class XhbRefJudgeDao extends AbstractVersionedDao implements Serializable {
    private static final long serialVersionUID = -6788003970955114552L;
    @Id
    @GeneratedValue(generator = "xhb_ref_judge_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "xhb_ref_judge_seq", sequenceName = "xhb_ref_judge_seq",
        allocationSize = 1)

    @Column(name = "REF_JUDGE_ID")
    private Integer refJudgeId;

    @Column(name = "JUDGE_TYPE")
    private String judgeType;

    @Column(name = "CREST_JUDGE_ID")
    private Integer crestJudgeId;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "FIRST_NAME")
    private String firstname;

    @Column(name = "MIDDLE_NAME")
    private String middleName;

    @Column(name = "SURNAME")
    private String surname;

    @Column(name = "FULL_LIST_TITLE1")
    private String fullListTitle1;

    @Column(name = "FULL_LIST_TITLE2")
    private String fullListTitle2;

    @Column(name = "FULL_LIST_TITLE3")
    private String fullListTitle3;

    @Column(name = "STATS_CODE")
    private String statsCode;

    @Column(name = "INITIALS")
    private String initials;

    @Column(name = "HONOURS")
    private String honours;

    @Column(name = "JUD_VERS")
    private String judVers;

    @Column(name = "OBS_IND")
    private String obsInd;

    @Column(name = "SOURCE_TABLE")
    private String sourceTable;

    @Column(name = "COURT_ID")
    private Integer courtId;

    public XhbRefJudgeDao() {
        super();
    }

    public XhbRefJudgeDao(XhbRefJudgeDao otherData) {
        this();
        setRefJudgeId(otherData.getRefJudgeId());
        setJudgeType(otherData.getJudgeType());
        setCrestJudgeId(otherData.getCrestJudgeId());
        setTitle(otherData.getTitle());
        setFirstname(otherData.getFirstname());
        setMiddleName(otherData.getMiddleName());
        setSurname(otherData.getSurname());
        setFullListTitle1(otherData.getFullListTitle1());
        setFullListTitle2(otherData.getFullListTitle2());
        setFullListTitle3(otherData.getFullListTitle3());
        setStatsCode(otherData.getStatsCode());
        setInitials(otherData.getInitials());
        setHonours(otherData.getHonours());
        setJudVers(otherData.getJudVers());
        setObsInd(otherData.getObsInd());
        setSourceTable(otherData.getSourceTable());
        setCourtId(otherData.getCourtId());
        setLastUpdateDate(otherData.getLastUpdateDate());
        setCreationDate(otherData.getCreationDate());
        setLastUpdatedBy(otherData.getLastUpdatedBy());
        setCreatedBy(otherData.getCreatedBy());
        setVersion(otherData.getVersion());
    }

    @Override
    public Integer getPrimaryKey() {
        return getRefJudgeId();
    }

    public Integer getRefJudgeId() {
        return refJudgeId;
    }

    public void setRefJudgeId(Integer refJudgeId) {
        this.refJudgeId = refJudgeId;
    }

    public String getJudgeType() {
        return judgeType;
    }

    public void setJudgeType(String judgeType) {
        this.judgeType = judgeType;
    }

    public Integer getCrestJudgeId() {
        return crestJudgeId;
    }

    public void setCrestJudgeId(Integer crestJudgeId) {
        this.crestJudgeId = crestJudgeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getFullListTitle1() {
        return fullListTitle1;
    }

    public void setFullListTitle1(String fullListTitle1) {
        this.fullListTitle1 = fullListTitle1;
    }

    public String getFullListTitle2() {
        return fullListTitle2;
    }

    public void setFullListTitle2(String fullListTitle2) {
        this.fullListTitle2 = fullListTitle2;
    }

    public String getFullListTitle3() {
        return fullListTitle3;
    }

    public void setFullListTitle3(String fullListTitle3) {
        this.fullListTitle3 = fullListTitle3;
    }

    public String getStatsCode() {
        return statsCode;
    }

    public void setStatsCode(String statsCode) {
        this.statsCode = statsCode;
    }

    public String getInitials() {
        return initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public String getHonours() {
        return honours;
    }

    public void setHonours(String honours) {
        this.honours = honours;
    }

    public String getJudVers() {
        return judVers;
    }

    public void setJudVers(String judVers) {
        this.judVers = judVers;
    }

    public String getObsInd() {
        return obsInd;
    }

    public void setObsInd(String obsInd) {
        this.obsInd = obsInd;
    }

    public String getSourceTable() {
        return sourceTable;
    }

    public void setSourceTable(String sourceTable) {
        this.sourceTable = sourceTable;
    }

    public Integer getCourtId() {
        return courtId;
    }

    public void setCourtId(Integer courtId) {
        this.courtId = courtId;
    }
}
