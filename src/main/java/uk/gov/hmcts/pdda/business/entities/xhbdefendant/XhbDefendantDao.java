package uk.gov.hmcts.pdda.business.entities.xhbdefendant;

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
@Entity(name = "XHB_DEFENDANT")
@NamedQuery(name = "XHB_DEFENDANT.findByDefendantName",
    query = "SELECT o from XHB_DEFENDANT o WHERE o.courtId = :courtId "
        + "AND o.firstName = :firstName AND o.middleName = :middleName AND o.surname = :surname "
        + "AND o.gender = :gender AND o.dateOfBirth = :dateOfBirth AND o.courtId = :courtId")
public class XhbDefendantDao extends AbstractVersionedDao implements Serializable {

    private static final long serialVersionUID = -6788003970955114552L;

    @Id
    @GeneratedValue(generator = "xhb_defendant_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "xhb_defendant_seq", sequenceName = "xhb_defendant_seq",
        allocationSize = 1)
    @Column(name = "DEFENDANT_ID")
    private Integer defendantId;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "MIDDLE_NAME")
    private String middleName;

    @Column(name = "SURNAME")
    private String surname;
    
    @Column(name = "COURT_ID")
    private Integer courtId;
    
    @Column(name = "GENDER")
    private String gender;
    
    @Column(name = "DATE_OF_BIRTH")
    private LocalDateTime dateOfBirth;

    @Column(name = "PUBLIC_DISPLAY_HIDE")
    private String publicDisplayHide;

    public XhbDefendantDao() {
        super();
    }

    public XhbDefendantDao(XhbDefendantDao otherData) {
        this();
        setDefendantId(otherData.getDefendantId());
        setFirstName(otherData.getFirstName());
        setMiddleName(otherData.getMiddleName());
        setSurname(otherData.getSurname());
        setPublicDisplayHide(otherData.getPublicDisplayHide());
        setLastUpdateDate(otherData.getLastUpdateDate());
        setCreationDate(otherData.getCreationDate());
        setLastUpdatedBy(otherData.getLastUpdatedBy());
        setCreatedBy(otherData.getCreatedBy());
        setVersion(otherData.getVersion());
    }

    public Integer getPrimaryKey() {
        return getDefendantId();
    }

    public Integer getDefendantId() {
        return defendantId;
    }

    public void setDefendantId(Integer defendantId) {
        this.defendantId = defendantId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
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

    public String getPublicDisplayHide() {
        return publicDisplayHide;
    }

    public void setPublicDisplayHide(String publicDisplayHide) {
        this.publicDisplayHide = publicDisplayHide;
    }

    public Integer getCourtId() {
        return courtId;
    }

    public void setCourtId(Integer courtId) {
        this.courtId = courtId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDateTime getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
