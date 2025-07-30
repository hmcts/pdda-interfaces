package uk.gov.hmcts.pdda.business.entities.xhbrefpddamessagetype;

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
@Entity(name = "XHB_REF_PDDA_MESSAGE_TYPE")
@NamedQuery(name = "XHB_REF_PDDA_MESSAGE_TYPE.findByMessageType",
    query = "SELECT o from XHB_REF_PDDA_MESSAGE_TYPE o WHERE o.pddaMessageType = "
        + ":pddaMessageType AND (o.obsInd is null OR o.obsInd = 'N' OR o.obsInd = ' ') "
        + "ORDER BY o.pddaMessageTypeId")
public class XhbRefPddaMessageTypeDao extends AbstractVersionedDao implements Serializable {


    private static final long serialVersionUID = -4837160887822045992L;

    @Id
    @GeneratedValue(generator = "xhb_ref_pdda_message_type_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "xhb_ref_pdda_message_type_seq",
        sequenceName = "xhb_ref_pdda_message_type_seq", allocationSize = 1)
    @Column(name = "PDDA_MESSAGE_TYPE_ID")
    private Integer pddaMessageTypeId;

    @Column(name = "PDDA_MESSAGE_TYPE")
    private String pddaMessageType;

    @Column(name = "PDDA_MESSAGE_DESCRIPTION")
    private String pddaMessageDescription;

    @Column(name = "OBS_IND")
    private String obsInd;


    public XhbRefPddaMessageTypeDao() {
        super();
    }

    public XhbRefPddaMessageTypeDao(Integer pddaMessageTypeId, String pddaMessageType,
        String pddaMessageDescription, LocalDateTime lastUpdateDate, LocalDateTime creationDate,
        String lastUpdatedBy, String createdBy, Integer version, String obsInd) {
        this();
        setPddaMessageTypeId(pddaMessageTypeId);
        setPddaMessageType(pddaMessageType);
        setPddaMessageDescription(pddaMessageDescription);
        setObsInd(obsInd);
        setLastUpdateDate(lastUpdateDate);
        setCreationDate(creationDate);
        setLastUpdatedBy(lastUpdatedBy);
        setCreatedBy(createdBy);
        setVersion(version);
    }

    public XhbRefPddaMessageTypeDao(XhbRefPddaMessageTypeDao otherData) {
        this();
        setPddaMessageTypeId(otherData.getPddaMessageTypeId());
        setPddaMessageType(otherData.getPddaMessageType());
        setPddaMessageDescription(otherData.getPddaMessageDescription());
        setObsInd(otherData.getObsInd());
        setLastUpdateDate(otherData.getLastUpdateDate());
        setCreationDate(otherData.getCreationDate());
        setLastUpdatedBy(otherData.getLastUpdatedBy());
        setCreatedBy(otherData.getCreatedBy());
        setVersion(otherData.getVersion());
    }

    @Override
    public Integer getPrimaryKey() {
        return getPddaMessageTypeId();
    }

    public Integer getPddaMessageTypeId() {
        return pddaMessageTypeId;
    }

    private void setPddaMessageTypeId(Integer pddaMessageTypeId) {
        this.pddaMessageTypeId = pddaMessageTypeId;
    }

    public String getPddaMessageType() {
        return pddaMessageType;
    }

    public void setPddaMessageType(String pddaMessageType) {
        this.pddaMessageType = pddaMessageType;
    }

    public String getPddaMessageDescription() {
        return pddaMessageDescription;
    }

    public void setPddaMessageDescription(String pddaMessageDescription) {
        this.pddaMessageDescription = pddaMessageDescription;
    }

    public String getObsInd() {
        return obsInd;
    }

    private void setObsInd(String obsInd) {
        this.obsInd = obsInd;
    }

}
