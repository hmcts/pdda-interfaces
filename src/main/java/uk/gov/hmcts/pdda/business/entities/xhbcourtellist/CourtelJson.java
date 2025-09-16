package uk.gov.hmcts.pdda.business.entities.xhbcourtellist;

import java.time.ZonedDateTime;

public abstract class CourtelJson {

    private String json;
    private String crestCourtId;
    private ZonedDateTime contentDate;
    private ZonedDateTime endDate;
    private String sensitivity;
    private String provenance;
    private Language language;
    private ArtefactType artefactType;
    private String token;
    private String documentName;

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getCrestCourtId() {
        return crestCourtId;
    }
    
    public void setCrestCourtId(String crestCourtId) {
        this.crestCourtId = crestCourtId;
    }
    
    public ZonedDateTime getContentDate() {
        return contentDate;
    }
    
    public void setContentDate(ZonedDateTime contentDate) {
        this.contentDate = contentDate;
    }
    
    public ZonedDateTime getEndDate() {
        return endDate;
    }
    
    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }
    
    public String getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(String sensitivity) {
        this.sensitivity = sensitivity;
    }

    public String getProvenance() {
        return provenance;
    }
    
    public void setProvenance(String provenance) {
        this.provenance = provenance;
    }
    
    public Language getLanguage() {
        return language;
    }
    
    public void setLanguage(Language language) {
        this.language = language;
    }
    
    public ArtefactType getArtefactType() {
        return this.artefactType;
    }
    
    public void setArtefactType(ArtefactType artefactType) {
        this.artefactType = artefactType;
    }
  
    public String getToken() {
        return this.token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getDocumentName() {
        return documentName;
    }
    
    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }
    
    public abstract ListType getListType();
}
