package uk.gov.hmcts.pdda.business.entities.xhbcourtellist;

@SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
public class ListJson extends CourtelJson {

    private ListType listType;
    private static final String CONTENT_TYPE = "application/json";
    
    public ListJson() {
        super();
        setArtefactType(ArtefactType.LIST);
        setSensitivity("CLASSIFIED");
    }
    
    @Override
    public ListType getListType() {
        return listType;
    }
    
    public void setListType(ListType listType) {
        this.listType = listType;
    }
    
    // Return content type for Lists.
    @Override
    public String getContentType() {
        return CONTENT_TYPE;
    }
}
