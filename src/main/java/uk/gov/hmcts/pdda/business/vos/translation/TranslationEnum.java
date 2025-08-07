package uk.gov.hmcts.pdda.business.vos.translation;

public enum TranslationEnum {

    ERRORTEXT("config/bundles/errorText.properties"), PUBLICDISPLAYDOCUMENTRESOURCES(
        "config/bundles/XHIBITPublicDisplayDocumentResources.properties");

    String resourceName;

    TranslationEnum(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getResourceName() {
        return this.resourceName;
    }
}
