package uk.gov.hmcts.pdda.business.entities.xhbcourtellist;

import java.time.LocalDateTime;

public class CourtelJson {

    private String json;
    private Integer courtId;
    private LocalDateTime contentDate;
    private String provenance;
    private String language;

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public Integer getCourtId() {
        return courtId;
    }
    
    public void setCourtId(Integer courtId) {
        this.courtId = courtId;
    }
    
    public LocalDateTime getContentDate() {
        return contentDate;
    }
    
    public void setContentDate(LocalDateTime contentDate) {
        this.contentDate = contentDate;
    }
    
    public String getProvenance() {
        return provenance;
    }
    
    public void setProvenance(String provenance) {
        this.provenance = provenance;
    }
    
    public String getLanguage() {
        return language;
    }
    
    public void setLanguage(String language) {
        this.language = language;
    }
}
