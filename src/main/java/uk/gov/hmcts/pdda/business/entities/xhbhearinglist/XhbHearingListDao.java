package uk.gov.hmcts.pdda.business.entities.xhbhearinglist;

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
@Entity(name = "XHB_HEARING_LIST")
@NamedQuery(name = "XHB_HEARING_LIST.findByCourtIdAndDate",
    query = "SELECT o from XHB_HEARING_LIST o WHERE o.courtId = :courtId AND o.startDate = :startDate")
@NamedQuery(name = "XHB_HEARING_LIST.findByCourtIdStatusAndDate",
    query = "SELECT o from XHB_HEARING_LIST o WHERE o.courtId = :courtId AND o.status = :status "
        + "AND o.startDate = :startDate")
public class XhbHearingListDao extends AbstractVersionedDao implements Serializable {

    private static final long serialVersionUID = -6844793990175522946L;

    @Id
    @GeneratedValue(generator = "xhb_hearing_list_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "xhb_hearing_list_seq", sequenceName = "xhb_hearing_list_seq",
        allocationSize = 1)
    @Column(name = "LIST_ID")
    private Integer listId;

    @Column(name = "LIST_TYPE")
    private String listType;

    @Column(name = "START_DATE")
    private LocalDateTime startDate;

    @Column(name = "END_DATE")
    private LocalDateTime endDate;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "EDITION_NO")
    private Integer editionNo;

    @Column(name = "PUBLISHED_TIME")
    private LocalDateTime publishedTime;

    @Column(name = "PRINT_REFERENCE")
    private String printReference;

    @Column(name = "CREST_LIST_ID")
    private Integer crestListId;

    @Column(name = "COURT_ID")
    private Integer courtId;

    public XhbHearingListDao() {
        super();
    }

    public XhbHearingListDao(XhbHearingListDao otherData) {
        this();
        setListId(otherData.getListId());
        setListType(otherData.getListType());
        setStartDate(otherData.getStartDate());
        setEndDate(otherData.getEndDate());
        setStatus(otherData.getStatus());
        setEditionNo(otherData.getEditionNo());
        setPublishedTime(otherData.getPublishedTime());
        setPrintReference(otherData.getPrintReference());
        setCrestListId(otherData.getCrestListId());
        setCourtId(otherData.getCourtId());
        setLastUpdateDate(otherData.getLastUpdateDate());
        setCreationDate(otherData.getCreationDate());
        setLastUpdatedBy(otherData.getLastUpdatedBy());
        setCreatedBy(otherData.getCreatedBy());
        setVersion(otherData.getVersion());
    }

    public Integer getPrimaryKey() {
        return getListId();
    }

    public Integer getListId() {
        return listId;
    }

    public void setListId(Integer listId) {
        this.listId = listId;
    }

    public String getListType() {
        return listType;
    }

    public void setListType(String listType) {
        this.listType = listType;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getEditionNo() {
        return editionNo;
    }

    public void setEditionNo(Integer editionNo) {
        this.editionNo = editionNo;
    }

    public LocalDateTime getPublishedTime() {
        return publishedTime;
    }

    public void setPublishedTime(LocalDateTime publishedTime) {
        this.publishedTime = publishedTime;
    }

    public String getPrintReference() {
        return printReference;
    }

    public void setPrintReference(String printReference) {
        this.printReference = printReference;
    }

    public Integer getCrestListId() {
        return crestListId;
    }

    public void setCrestListId(Integer crestListId) {
        this.crestListId = crestListId;
    }

    public Integer getCourtId() {
        return courtId;
    }

    public void setCourtId(Integer courtId) {
        this.courtId = courtId;
    }

}
