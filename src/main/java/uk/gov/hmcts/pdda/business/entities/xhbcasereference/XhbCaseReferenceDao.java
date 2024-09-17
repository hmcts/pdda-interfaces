package uk.gov.hmcts.pdda.business.entities.xhbcasereference;

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


@Entity(name = "XHB_CASE_REFERENCE")
@NamedQuery(name = "XHB_CASE_REFERENCE.findByCaseId",
    query = "SELECT o from XHB_CASE_REFERENCE o WHERE o.caseId = :caseId ")
public class XhbCaseReferenceDao extends AbstractVersionedDao implements Serializable {

    private static final long serialVersionUID = 5203521612570811544L;

    @Id
    @GeneratedValue(generator = "xhb_case_reference_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "xhb_case_reference_seq", sequenceName = "xhb_case_reference_seq",
        allocationSize = 1)
    @Column(name = "CASE_REFERENCE_ID")
    private Integer caseReferenceId;

    @Column(name = "REPORTING_RESTRICTIONS")
    private Integer reportingRestrictions;

    @Column(name = "CASE_ID")
    private Integer caseId;

    public XhbCaseReferenceDao() {
        super();
    }

    public XhbCaseReferenceDao(Integer caseReferenceId, Integer reportingRestrictions,
        Integer caseId, LocalDateTime lastUpdateDate, LocalDateTime creationDate,
        String lastUpdatedBy, String createdBy, Integer version) {
        this();
        setCaseReferenceId(caseReferenceId);
        setReportingRestrictions(reportingRestrictions);
        setCaseId(caseId);
        super.setLastUpdateDate(lastUpdateDate);
        super.setCreationDate(creationDate);
        super.setLastUpdatedBy(lastUpdatedBy);
        super.setCreatedBy(createdBy);
        super.setVersion(version);
    }

    public XhbCaseReferenceDao(XhbCaseReferenceDao otherData) {
        this();
        setCaseReferenceId(otherData.getCaseReferenceId());
        setReportingRestrictions(otherData.getReportingRestrictions());
        setCaseId(otherData.getCaseId());
        super.setLastUpdateDate(otherData.getLastUpdateDate());
        super.setCreationDate(otherData.getCreationDate());
        super.setLastUpdatedBy(otherData.getLastUpdatedBy());
        super.setCreatedBy(otherData.getCreatedBy());
        super.setVersion(otherData.getVersion());
    }

    public Integer getPrimaryKey() {
        return getCaseReferenceId();
    }

    public Integer getCaseReferenceId() {
        return caseReferenceId;
    }

    private void setCaseReferenceId(Integer caseReferenceId) {
        this.caseReferenceId = caseReferenceId;
    }

    public Integer getReportingRestrictions() {
        return reportingRestrictions;
    }

    private void setReportingRestrictions(Integer reportingRestrictions) {
        this.reportingRestrictions = reportingRestrictions;
    }

    public Integer getCaseId() {
        return caseId;
    }

    private void setCaseId(Integer caseId) {
        this.caseId = caseId;
    }
}
