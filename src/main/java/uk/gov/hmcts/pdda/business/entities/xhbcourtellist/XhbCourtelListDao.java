package uk.gov.hmcts.pdda.business.entities.xhbcourtellist;

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

@SuppressWarnings("PMD.ExcessiveParameterList")
@Entity(name = "XHB_COURTEL_LIST")
@NamedQuery(name = "XHB_COURTEL_LIST.findByXmlDocumentId",
    query = "SELECT o from XHB_COURTEL_LIST o WHERE o.xmlDocumentId = :xmlDocumentId "
        + "ORDER BY o.xmlDocumentId DESC")
public class XhbCourtelListDao extends AbstractVersionedDao implements Serializable {

    private static final long serialVersionUID = -2723700446890851398L;

    @Id
    @GeneratedValue(generator = "xhb_courtel_list_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "xhb_courtel_list_seq", sequenceName = "xhb_courtel_list_seq", allocationSize = 1)
    @Column(name = "COURTEL_LIST_ID")
    private Integer courtelListId;

    @Column(name = "XML_DOCUMENT_ID")
    private Integer xmlDocumentId;

    @Column(name = "XML_DOCUMENT_CLOB_ID")
    private Long xmlDocumentClobId;

    @Column(name = "BLOB_ID")
    private Long blobId;

    @Column(name = "SENT_TO_COURTEL")
    private String sentToCourtel;

    @Column(name = "NUM_SEND_ATTEMPTS")
    private Integer numSendAttempts;

    @Column(name = "LAST_ATTEMPT_DATETIME")
    private LocalDateTime lastAttemptDatetime;

    @Column(name = "MESSAGE_TEXT")
    private String messageText;

    public XhbCourtelListDao() {
        super();
    }

    public XhbCourtelListDao(Integer courtelListId, Integer xmlDocumentId, Long xmlDocumentClobId, Long blobId,
        String sentToCourtel, Integer numSendAttempts, LocalDateTime lastAttemptDatetime, String messageText,
        LocalDateTime lastUpdateDate, LocalDateTime creationDate, String lastUpdatedBy, String createdBy,
        Integer version) {
        this();
        setCourtelListId(courtelListId);
        setXmlDocumentId(xmlDocumentId);
        setXmlDocumentClobId(xmlDocumentClobId);
        setBlobId(blobId);
        setSentToCourtel(sentToCourtel);
        setNumSendAttempts(numSendAttempts);
        setLastAttemptDatetime(lastAttemptDatetime);
        setMessageText(messageText);
        setLastUpdateDate(lastUpdateDate);
        setCreationDate(creationDate);
        setLastUpdatedBy(lastUpdatedBy);
        setCreatedBy(createdBy);
        setVersion(version);
    }

    public XhbCourtelListDao(XhbCourtelListDao otherData) {
        this();
        setCourtelListId(otherData.getCourtelListId());
        setXmlDocumentId(otherData.getXmlDocumentId());
        setXmlDocumentClobId(otherData.getXmlDocumentClobId());
        setBlobId(otherData.getBlobId());
        setSentToCourtel(otherData.getSentToCourtel());
        setNumSendAttempts(otherData.getNumSendAttempts());
        setLastAttemptDatetime(otherData.getLastAttemptDatetime());
        setMessageText(otherData.getMessageText());
        setLastUpdateDate(otherData.getLastUpdateDate());
        setCreationDate(otherData.getCreationDate());
        setLastUpdatedBy(otherData.getLastUpdatedBy());
        setCreatedBy(otherData.getCreatedBy());
        setVersion(otherData.getVersion());
    }

    public Integer getPrimaryKey() {
        return getCourtelListId();
    }

    public Integer getCourtelListId() {
        return courtelListId;
    }

    public final void setCourtelListId(Integer courtelListId) {
        this.courtelListId = courtelListId;
    }

    public Integer getXmlDocumentId() {
        return xmlDocumentId;
    }

    public final void setXmlDocumentId(Integer xmlDocumentId) {
        this.xmlDocumentId = xmlDocumentId;
    }

    public Long getXmlDocumentClobId() {
        return xmlDocumentClobId;
    }

    public final void setXmlDocumentClobId(Long xmlDocumentClobId) {
        this.xmlDocumentClobId = xmlDocumentClobId;
    }

    public Long getBlobId() {
        return blobId;
    }

    public final void setBlobId(Long blobId) {
        this.blobId = blobId;
    }

    public String getSentToCourtel() {
        return sentToCourtel;
    }

    public final void setSentToCourtel(String sentToCourtel) {
        this.sentToCourtel = sentToCourtel;
    }

    public Integer getNumSendAttempts() {
        return numSendAttempts;
    }

    public final void setNumSendAttempts(Integer numSendAttempts) {
        this.numSendAttempts = numSendAttempts;
    }

    private LocalDateTime getLastAttemptDatetime() {
        return lastAttemptDatetime;
    }

    public final void setLastAttemptDatetime(LocalDateTime lastAttemptDatetime) {
        this.lastAttemptDatetime = lastAttemptDatetime;
    }

    public String getMessageText() {
        return messageText;
    }

    public final void setMessageText(String messageText) {
        this.messageText = messageText;
    }
}