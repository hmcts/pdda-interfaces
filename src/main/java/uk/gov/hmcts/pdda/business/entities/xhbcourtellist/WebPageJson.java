package uk.gov.hmcts.pdda.business.entities.xhbcourtellist;

@SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
public class WebPageJson extends CourtelJson {

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
}
