package uk.gov.hmcts.pdda.business.services.cppstaginginboundejb3;

public enum CppDocumentTypes {
    WP("WP"), PD("PD"), DL("DL"), FL("FL"), WL("WL");

    private final String label;

    CppDocumentTypes(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return this.label;
    }
    
    public static CppDocumentTypes fromString(String value) {
        for (CppDocumentTypes enumValue : values()) {
            if (enumValue.getLabel().equalsIgnoreCase(value)) {
                return enumValue;
            }
        }
        return null;
    }
}
