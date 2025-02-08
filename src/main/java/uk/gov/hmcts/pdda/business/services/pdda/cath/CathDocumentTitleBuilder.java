package uk.gov.hmcts.pdda.business.services.pdda.cath;

public final class CathDocumentTitleBuilder {

    private String startDate;
    private String endDate;
    private String version;
    private String publishedDateTime;
    
    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String fromDate) {
        this.startDate = fromDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String toDate) {
        this.endDate = toDate;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPublishedDateTime() {
        return publishedDateTime;
    }

    public void setPublishedDateTime(String publishedDateTime) {
        this.publishedDateTime = publishedDateTime;
    }
}
