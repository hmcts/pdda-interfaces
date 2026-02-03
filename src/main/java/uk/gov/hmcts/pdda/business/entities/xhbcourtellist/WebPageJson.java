package uk.gov.hmcts.pdda.business.entities.xhbcourtellist;

@SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
public class WebPageJson extends CourtelJson {

    private static final String CONTENT_TYPE = "multipart/form-data";
    
    public WebPageJson() {
        super();
        setArtefactType(ArtefactType.LCSU);
        setSensitivity("PUBLIC");
    }
    
    // Return a null ListType for IWP.
    @Override
    public ListType getListType() {
        return null;
    }
    
    // Return content type for IWP.
    @Override
    public String getContentType() {
        return CONTENT_TYPE;
    }
}