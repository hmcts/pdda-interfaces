package uk.gov.hmcts.pdda.business.entities.xhbcpplist;

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

@SuppressWarnings({"PMD.TooManyFields", "PMD.ConstructorCallsOverridableMethod"})
@Entity(name = "XHB_CPP_LIST")
@NamedQuery(name = "XHB_CPP_LIST.findByClobId",
    query = "SELECT o from XHB_CPP_LIST o WHERE o.listClobId = :listClobId ORDER BY o.creationDate DESC")
@NamedQuery(name = "XHB_CPP_LIST.findByCourtCodeAndListTypeAndListDate",
    query = "SELECT o from XHB_CPP_LIST o WHERE o.courtCode = :courtCode AND o.listType = :listType AND "
        + "o.listStartDate = :listStartDate AND (o.obsInd IS NULL OR o.obsInd = 'N') AND (o.status IS NULL "
        + "OR o.status <> 'MF') ORDER BY o.creationDate DESC")
@NamedQuery(name = "XHB_CPP_LIST.findByCourtCodeAndListTypeAndListStartDateAndListEndDate",
    query = "SELECT o from XHB_CPP_LIST o WHERE o.courtCode = :courtCode AND o.listType = :listType AND "
        + "o.listStartDate = :listStartDate AND o.listEndDate = :listEndDate AND (o.obsInd IS NULL OR o.obsInd = 'N') "
        + "ORDER BY o.creationDate DESC")
public class XhbCppListDao extends AbstractDao implements Serializable {

    private static final long serialVersionUID = -2723700446890851397L;

    @Id
    @GeneratedValue(generator = "xhb_cpp_list_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "xhb_cpp_list_seq", sequenceName = "xhb_cpp_list_seq",
        allocationSize = 1)
    @Column(name = "CPP_LIST_ID")
    private Integer cppListId;

    @Column(name = "COURT_CODE")
    private Integer courtCode;

    @Column(name = "LIST_TYPE")
    private String listType;

    @Column(name = "TIME_LOADED")
    private LocalDateTime timeLoaded;

    @Column(name = "LIST_START_DATE")
    private LocalDateTime listStartDate;

    @Column(name = "LIST_END_DATE")
    private LocalDateTime listEndDate;

    @Column(name = "LIST_CLOB_ID")
    private Long listClobId;

    @Column(name = "MERGED_CLOB_ID")
    private Long mergedClobId;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "ERROR_MESSAGE")
    private String errorMessage;

    @Column(name = "OBS_IND")
    private String obsInd;

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

    public XhbCppListDao() {
        super();
    }

    public XhbCppListDao(final XhbCppListDao otherData) {
        this();
        setCppListId(otherData.getCppListId());
        setCourtCode(otherData.getCourtCode());
        setListType(otherData.getListType());
        setTimeLoaded(otherData.getTimeLoaded());
        setListStartDate(otherData.getListStartDate());
        setListEndDate(otherData.getListEndDate());
        setListClobId(otherData.getListClobId());
        setMergedClobId(otherData.getMergedClobId());
        setStatus(otherData.getStatus());
        setErrorMessage(otherData.getErrorMessage());
        setObsInd(otherData.getObsInd());
        setLastUpdateDate(otherData.getLastUpdateDate());
        setCreationDate(otherData.getCreationDate());
        setLastUpdatedBy(otherData.getLastUpdatedBy());
        setCreatedBy(otherData.getCreatedBy());
        setVersion(otherData.getVersion());
    }

    @Override
    public Integer getPrimaryKey() {
        return getCppListId();
    }

    public Integer getCppListId() {
        return cppListId;
    }

    public void setCppListId(final Integer cppListId) {
        this.cppListId = cppListId;
    }

    public Integer getCourtCode() {
        return courtCode;
    }

    public void setCourtCode(final Integer courtCode) {
        this.courtCode = courtCode;
    }

    public String getListType() {
        return listType;
    }

    public void setListType(final String listType) {
        this.listType = listType;
    }

    public LocalDateTime getTimeLoaded() {
        return timeLoaded;
    }

    public void setTimeLoaded(final LocalDateTime timeLoaded) {
        this.timeLoaded = timeLoaded;
    }

    public LocalDateTime getListStartDate() {
        return listStartDate;
    }

    public void setListStartDate(final LocalDateTime listStartDate) {
        this.listStartDate = listStartDate;
    }

    public LocalDateTime getListEndDate() {
        return listEndDate;
    }

    public void setListEndDate(final LocalDateTime listEndDate) {
        this.listEndDate = listEndDate;
    }

    public Long getListClobId() {
        return listClobId;
    }

    public void setListClobId(final Long listClobId) {
        this.listClobId = listClobId;
    }

    public Long getMergedClobId() {
        return mergedClobId;
    }

    public void setMergedClobId(final Long mergedClobId) {
        this.mergedClobId = mergedClobId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getObsInd() {
        return obsInd;
    }

    public void setObsInd(final String obsInd) {
        this.obsInd = obsInd;
    }

    public LocalDateTime getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(final LocalDateTime lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(final LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(final String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public Integer getVersion() {
        return version;
    }

    @Override
    public void setVersion(final Integer version) {
        this.version = version;
    }
}
