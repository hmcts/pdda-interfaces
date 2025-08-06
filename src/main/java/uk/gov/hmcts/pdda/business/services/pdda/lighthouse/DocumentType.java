package uk.gov.hmcts.pdda.business.services.pdda.lighthouse;

public enum DocumentType {

    DL("DailyList"), WL("WarnedList"), FL("FirmList"), WP("WebPage"), PD("PublicDisplay");

    private final String docName;

    DocumentType(String docName) {
        this.docName = docName;
    }

    public String getDocName() {
        return docName;
    }
    
    public static DocumentType fromString(String value) {
        for (DocumentType docType : values()) {
            if (docType.getDocName().equalsIgnoreCase(value)) {
                return docType;
            }
        }
        return null;
    }
}
