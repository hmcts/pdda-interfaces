package uk.gov.hmcts.pdda.business.entities.xhbcourtlogeventdesc;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import uk.gov.hmcts.pdda.business.entities.AbstractDao;

import java.io.Serializable;
import java.time.LocalDateTime;

@SuppressWarnings({"PMD.TooManyFields", "PMD.ConstructorCallsOverridableMethod"})
@Entity(name = "XHB_COURT_LOG_EVENT_DESC")
public class XhbCourtLogEventDescDao extends AbstractDao implements Serializable {
    private static final long serialVersionUID = -6788003970955114552L;
    @Id
    @GeneratedValue(generator = "xhb_court_log_event_desc_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "xhb_court_log_event_desc_seq",
        sequenceName = "xhb_court_log_event_desc_seq", allocationSize = 1)
    @Column(name = "EVENT_DESC_ID")
    private Integer eventDescId;

    @Column(name = "FLAGGED_EVENT")
    private Integer flaggedEvent;

    @Column(name = "EDITABLE")
    private Integer editable;

    @Column(name = "SEND_TO_MERCATOR")
    private Integer sendToMercator;

    @Column(name = "UPDATE_LINKED_CASES")
    private Integer updateLinkedCases;

    @Column(name = "PUBLISH_TO_SUBSCRIBERS")
    private Integer publishToSubscribers;

    @Column(name = "CLEAR_PUBLIC_DISPLAYS")
    private Integer clearPublicDisplays;

    @Column(name = "E_INFORM")
    private Integer electronicInform;

    @Column(name = "PUBLIC_DISPLAY")
    private Integer publicDisplay;

    @Column(name = "LINKED_CASE_TEXT")
    private String linkedCaseText;

    @Column(name = "EVENT_DESCRIPTION")
    private String eventDescription;

    @Column(name = "EVENT_TYPE")
    private Integer eventType;

    @Column(name = "PUBLIC_NOTICE")
    private Integer publicNotice;

    @Column(name = "SHORT_DESCRIPTION")
    private String shortDescription;

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

    public XhbCourtLogEventDescDao() {
        super();
    }

    public XhbCourtLogEventDescDao(XhbCourtLogEventDescDao otherData) {
        this();
        setEventDescId(otherData.getEventDescId());
        setFlaggedEvent(otherData.getFlaggedEvent());
        setEditable(otherData.getEditable());
        setSendToMercator(otherData.getSendToMercator());
        setUpdateLinkedCases(otherData.getUpdateLinkedCases());
        setPublishToSubscribers(otherData.getPublishToSubscribers());
        setClearPublicDisplays(otherData.getClearPublicDisplays());
        setElectronicInform(otherData.getElectronicInform());
        setPublicDisplay(otherData.getPublicDisplay());
        setLinkedCaseText(otherData.getLinkedCaseText());
        setEventDescription(otherData.getEventDescription());
        setEventType(otherData.getEventType());
        setPublicNotice(otherData.getPublicNotice());
        setShortDescription(otherData.getShortDescription());
        setLastUpdateDate(otherData.getLastUpdateDate());
        setCreationDate(otherData.getCreationDate());
        setLastUpdatedBy(otherData.getLastUpdatedBy());
        setCreatedBy(otherData.getCreatedBy());
        setVersion(otherData.getVersion());
    }

    @Override
    public Integer getPrimaryKey() {
        return getEventDescId();
    }

    public Integer getEventDescId() {
        return eventDescId;
    }

    public void setEventDescId(Integer eventDescId) {
        this.eventDescId = eventDescId;
    }

    public Integer getFlaggedEvent() {
        return flaggedEvent;
    }

    public void setFlaggedEvent(Integer flaggedEvent) {
        this.flaggedEvent = flaggedEvent;
    }

    public Integer getEditable() {
        return editable;
    }

    public void setEditable(Integer editable) {
        this.editable = editable;
    }

    public Integer getSendToMercator() {
        return sendToMercator;
    }

    public void setSendToMercator(Integer sendToMercator) {
        this.sendToMercator = sendToMercator;
    }

    public Integer getUpdateLinkedCases() {
        return updateLinkedCases;
    }

    public void setUpdateLinkedCases(Integer updateLinkedCases) {
        this.updateLinkedCases = updateLinkedCases;
    }

    public Integer getPublishToSubscribers() {
        return publishToSubscribers;
    }

    public void setPublishToSubscribers(Integer publishToSubscribers) {
        this.publishToSubscribers = publishToSubscribers;
    }

    public Integer getClearPublicDisplays() {
        return clearPublicDisplays;
    }

    public void setClearPublicDisplays(Integer clearPublicDisplays) {
        this.clearPublicDisplays = clearPublicDisplays;
    }

    public Integer getElectronicInform() {
        return electronicInform;
    }

    public void setElectronicInform(Integer electronicInform) {
        this.electronicInform = electronicInform;
    }

    public Integer getPublicDisplay() {
        return publicDisplay;
    }

    public void setPublicDisplay(Integer publicDisplay) {
        this.publicDisplay = publicDisplay;
    }

    public String getLinkedCaseText() {
        return linkedCaseText;
    }

    public void setLinkedCaseText(String linkedCaseText) {
        this.linkedCaseText = linkedCaseText;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public Integer getEventType() {
        return eventType;
    }

    public void setEventType(Integer eventType) {
        this.eventType = eventType;
    }

    public Integer getPublicNotice() {
        return publicNotice;
    }

    public void setPublicNotice(Integer publicNotice) {
        this.publicNotice = publicNotice;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public LocalDateTime getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(LocalDateTime lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public Integer getVersion() {
        return version;
    }

    @Override
    public void setVersion(Integer version) {
        this.version = version;
    }

}
