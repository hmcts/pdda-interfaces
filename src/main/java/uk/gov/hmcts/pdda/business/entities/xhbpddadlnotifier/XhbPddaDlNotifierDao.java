package uk.gov.hmcts.pdda.business.entities.xhbpddadlnotifier;

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
@Entity(name = "XHB_PDDA_DL_NOTIFIER")
@NamedQuery(name = "XHB_PDDA_DL_NOTIFIER.findByCourtAndLastRunDate",
    query = "SELECT o from XHB_PDDA_DL_NOTIFIER o WHERE o.courtId = :courtId AND o.lastRunDate = :lastRunDate "
        + "AND (o.obsInd is null OR o.obsInd = 'N' OR o.obsInd = ' ')")
public class XhbPddaDlNotifierDao extends AbstractVersionedDao implements Serializable {

    private static final long serialVersionUID = 45050600421177267L;

    @Id
    @GeneratedValue(generator = "xhb_pdda_dl_notifier_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "xhb_pdda_dl_notifier_seq", sequenceName = "xhb_pdda_dl_notifier_seq",
        allocationSize = 1)
    @Column(name = "PDDA_DL_NOTIFIER_ID")
    private Integer pddaDlNotifierId;

    @Column(name = "COURT_ID")
    private Integer courtId;

    @Column(name = "LAST_RUN_DATE")
    private LocalDateTime lastRunDate;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "ERROR_MESSAGE")
    private String errorMessage;

    @Column(name = "OBS_IND")
    private String obsInd;


    public XhbPddaDlNotifierDao() {
        super();
    }

    public XhbPddaDlNotifierDao(XhbPddaDlNotifierDao otherData) {
        this();
        setPddaDlNotifierId(otherData.getPddaDlNotifierId());
        setCourtId(otherData.getCourtId());
        setLastRunDate(otherData.getLastRunDate());
        setStatus(otherData.getStatus());
        setErrorMessage(otherData.getErrorMessage());
        setObsInd(otherData.getObsInd());
        setLastUpdateDate(otherData.getLastUpdateDate());
        setCreationDate(otherData.getCreationDate());
        setLastUpdatedBy(otherData.getLastUpdatedBy());
        setCreatedBy(otherData.getCreatedBy());
        setVersion(otherData.getVersion());
    }

    public Integer getPrimaryKey() {
        return getPddaDlNotifierId();
    }

    public Integer getPddaDlNotifierId() {
        return pddaDlNotifierId;
    }

    public void setPddaDlNotifierId(Integer pddaDlNotifierId) {
        this.pddaDlNotifierId = pddaDlNotifierId;
    }

    public Integer getCourtId() {
        return courtId;
    }

    public void setCourtId(Integer courtId) {
        this.courtId = courtId;
    }

    public LocalDateTime getLastRunDate() {
        return lastRunDate;
    }

    public void setLastRunDate(LocalDateTime lastRunDate) {
        this.lastRunDate = lastRunDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getObsInd() {
        return obsInd;
    }

    public void setObsInd(String obsInd) {
        this.obsInd = obsInd;
    }

}
