package uk.gov.hmcts.pdda.business.entities.xhbcathdocumentlink;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import uk.gov.hmcts.pdda.business.entities.AbstractVersionedDao;

import java.io.Serializable;

@SuppressWarnings({"PMD.ConstructorCallsOverridableMethod"})
@Entity(name = "XHB_CATH_DOCUMENT_LINK")
public class XhbCathDocumentLinkDao extends AbstractVersionedDao implements Serializable {

    private static final long serialVersionUID = -6459950623691642510L;

    @Id
    @GeneratedValue(generator = "xhb_cath_document_link_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "xhb_cath_document_link_seq",
        sequenceName = "xhb_cath_document_link_seq", allocationSize = 1)
    @Column(name = "CATH_DOCUMENT_LINK_ID")
    private Integer cathDocumentLinkId;

    @Column(name = "ORIG_COURTEL_LIST_DOC_ID")
    private Integer origCourtelListDocId;

    @Column(name = "CATH_XML_ID")
    private Integer cathXmlId;

    @Column(name = "CATH_JSON_ID")
    private Integer cathJsonId;

    public XhbCathDocumentLinkDao() {
        super();
    }

    public XhbCathDocumentLinkDao(Integer cathDocumentLinkId, Integer origCourtelListDocId,
        Integer cathXmlId, Integer cathJsonId) {
        this();
        setCathDocumentLinkId(cathDocumentLinkId);
        setOrigCourtelListDocId(origCourtelListDocId);
        setCathXmlId(cathXmlId);
        setCathJsonId(cathJsonId);
    }

    public XhbCathDocumentLinkDao(XhbCathDocumentLinkDao otherData) {
        this();
        setCathDocumentLinkId(otherData.getCathDocumentLinkId());
        setOrigCourtelListDocId(otherData.getOrigCourtelListDocId());
        setCathXmlId(otherData.getCathXmlId());
        setCathJsonId(otherData.getCathJsonId());
    }

    public Integer getPrimaryKey() {
        return getCathDocumentLinkId();
    }

    public Integer getCathDocumentLinkId() {
        return cathDocumentLinkId;
    }

    public void setCathDocumentLinkId(Integer cathDocumentLinkId) {
        this.cathDocumentLinkId = cathDocumentLinkId;
    }

    public Integer getOrigCourtelListDocId() {
        return origCourtelListDocId;
    }

    public void setOrigCourtelListDocId(Integer origCourtelListDocId) {
        this.origCourtelListDocId = origCourtelListDocId;
    }

    public Integer getCathXmlId() {
        return cathXmlId;
    }

    public void setCathXmlId(Integer cathXmlId) {
        this.cathXmlId = cathXmlId;
    }

    public Integer getCathJsonId() {
        return cathJsonId;
    }

    public void setCathJsonId(Integer cathJsonId) {
        this.cathJsonId = cathJsonId;
    }
}
