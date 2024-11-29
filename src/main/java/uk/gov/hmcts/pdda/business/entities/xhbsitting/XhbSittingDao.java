package uk.gov.hmcts.pdda.business.entities.xhbsitting;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import uk.gov.hmcts.pdda.business.entities.AbstractVersionedDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;

import java.io.Serializable;
import java.time.LocalDateTime;

@SuppressWarnings({"PMD.TooManyFields", "PMD.LinguisticNaming",
    "PMD.ConstructorCallsOverridableMethod"})
@Entity(name = "XHB_SITTING")
@NamedQuery(name = "XHB_SITTING.findBySittingSequenceNo",
    query = "SELECT o from XHB_SITTING o WHERE o.sittingSequenceNo = :sittingSequenceNo ")
@NamedQuery(name = "XHB_SITTING.findByIsFloating",
    query = "SELECT o from XHB_SITTING o WHERE o.isFloating = :isFloating ")
@NamedQuery(name = "XHB_SITTING.findByNonFloatingHearingList",
    query = "SELECT o from XHB_SITTING o WHERE o.listId = :listId AND o.isFloating = '0'")
@NamedQuery(name = "XHB_SITTING.findByListId",
    query = "SELECT o from XHB_SITTING o WHERE o.listId = :listId")
@NamedQuery(name = "XHB_SITTING.findByCourtRoomAndSittingTime",
    query = "SELECT o from XHB_SITTING o WHERE o.courtSiteId = :courtSiteId "
        + "AND o.courtRoomId = :courtRoomId AND o.sittingTime = :sittingTime")
public class XhbSittingDao extends AbstractVersionedDao implements Serializable {


    private static final long serialVersionUID = -3329273791187256261L;

    @Id
    @GeneratedValue(generator = "xhb_sitting_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "xhb_sitting_seq", sequenceName = "xhb_sitting_seq",
        allocationSize = 1)
    @Column(name = "SITTING_ID")
    private Integer sittingId;

    @Column(name = "SITTING_SEQUENCE_NO")
    private Integer sittingSequenceNo;

    @Column(name = "IS_SITTING_JUDGE")
    private String isSittingJudge;

    @Column(name = "SITTING_TIME")
    private LocalDateTime sittingTime;

    @Column(name = "SITTING_NOTE")
    private String sittingNote;

    @Column(name = "REF_JUSTICE1_ID")
    private Integer refJustice1Id;

    @Column(name = "REF_JUSTICE2_ID")
    private Integer refJustice2Id;

    @Column(name = "REF_JUSTICE3_ID")
    private Integer refJustice3Id;

    @Column(name = "REF_JUSTICE4_ID")
    private Integer refJustice4Id;

    @Column(name = "IS_FLOATING")
    private String isFloating;

    @Column(name = "LIST_ID")
    private Integer listId;

    @Column(name = "REF_JUDGE_ID")
    private Integer refJudgeId;

    @Column(name = "COURT_ROOM_ID")
    private Integer courtRoomId;

    @Column(name = "COURT_SITE_ID")
    private Integer courtSiteId;


    // Non-columns
    @jakarta.persistence.Transient
    private XhbCourtRoomDao xhbCourtRoom;

    @jakarta.persistence.Transient
    private XhbCourtSiteDao xhbCourtSite;

    public XhbSittingDao() {
        super();
    }

    public XhbSittingDao(XhbSittingDao otherData) {
        this();
        setSittingId(otherData.getSittingId());
        setSittingSequenceNo(otherData.getSittingSequenceNo());
        setIsSittingJudge(otherData.getIsSittingJudge());
        setSittingTime(otherData.getSittingTime());
        setSittingNote(otherData.getSittingNote());
        setRefJustice1Id(otherData.getRefJustice1Id());
        setRefJustice2Id(otherData.getRefJustice2Id());
        setRefJustice3Id(otherData.getRefJustice3Id());
        setRefJustice4Id(otherData.getRefJustice4Id());
        setIsFloating(otherData.getIsFloating());
        setListId(otherData.getListId());
        setRefJudgeId(otherData.getRefJudgeId());
        setCourtRoomId(otherData.getCourtRoomId());
        setCourtSiteId(otherData.getCourtSiteId());
        setLastUpdateDate(otherData.getLastUpdateDate());
        setCreationDate(otherData.getCreationDate());
        setLastUpdatedBy(otherData.getLastUpdatedBy());
        setCreatedBy(otherData.getCreatedBy());
        setVersion(otherData.getVersion());
    }

    public Integer getPrimaryKey() {
        return getSittingId();
    }

    public Integer getSittingId() {
        return sittingId;
    }

    public void setSittingId(Integer sittingId) {
        this.sittingId = sittingId;
    }

    public Integer getSittingSequenceNo() {
        return sittingSequenceNo;
    }

    public void setSittingSequenceNo(Integer sittingSequenceNo) {
        this.sittingSequenceNo = sittingSequenceNo;
    }

    public String getIsSittingJudge() {
        return isSittingJudge;
    }

    public void setIsSittingJudge(String isSittingJudge) {
        this.isSittingJudge = isSittingJudge;
    }

    public LocalDateTime getSittingTime() {
        return sittingTime;
    }

    public void setSittingTime(LocalDateTime sittingTime) {
        this.sittingTime = sittingTime;
    }

    public String getSittingNote() {
        return sittingNote;
    }

    public void setSittingNote(String sittingNote) {
        this.sittingNote = sittingNote;
    }

    public Integer getRefJustice1Id() {
        return refJustice1Id;
    }

    public void setRefJustice1Id(Integer refJustice1Id) {
        this.refJustice1Id = refJustice1Id;
    }

    public Integer getRefJustice2Id() {
        return refJustice2Id;
    }

    public void setRefJustice2Id(Integer refJustice2Id) {
        this.refJustice2Id = refJustice2Id;
    }

    public Integer getRefJustice3Id() {
        return refJustice3Id;
    }

    public void setRefJustice3Id(Integer refJustice3Id) {
        this.refJustice3Id = refJustice3Id;
    }

    public Integer getRefJustice4Id() {
        return refJustice4Id;
    }

    public void setRefJustice4Id(Integer refJustice4Id) {
        this.refJustice4Id = refJustice4Id;
    }

    public String getIsFloating() {
        return isFloating;
    }

    public void setIsFloating(String isFloating) {
        this.isFloating = isFloating;
    }

    public Integer getListId() {
        return listId;
    }

    public void setListId(Integer listId) {
        this.listId = listId;
    }

    public Integer getRefJudgeId() {
        return refJudgeId;
    }

    public void setRefJudgeId(Integer refJudgeId) {
        this.refJudgeId = refJudgeId;
    }

    public Integer getCourtRoomId() {
        return courtRoomId;
    }

    public void setCourtRoomId(Integer courtRoomId) {
        this.courtRoomId = courtRoomId;
    }

    public Integer getCourtSiteId() {
        return courtSiteId;
    }

    public void setCourtSiteId(Integer courtSiteId) {
        this.courtSiteId = courtSiteId;
    }

    public XhbCourtRoomDao getXhbCourtRoom() {
        return xhbCourtRoom;
    }

    public void setXhbCourtRoom(XhbCourtRoomDao xhbCourtRoom) {
        this.xhbCourtRoom = xhbCourtRoom;
    }

    public XhbCourtSiteDao getXhbCourtSite() {
        return xhbCourtSite;
    }

    public void setXhbCourtSite(XhbCourtSiteDao xhbCourtSite) {
        this.xhbCourtSite = xhbCourtSite;
    }

}
