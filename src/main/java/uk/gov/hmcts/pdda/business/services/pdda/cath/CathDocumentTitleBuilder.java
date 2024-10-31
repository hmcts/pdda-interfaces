package uk.gov.hmcts.pdda.business.services.pdda.cath;

public final class CathDocumentTitleBuilder {

    private String fromDate;
    private String toDate;
    private String version;
    private String publishedDateTime;
    
    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
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
