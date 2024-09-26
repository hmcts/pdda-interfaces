package uk.gov.hmcts.pdda.business.entities.xhbcourtellist;

@SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
public class WebPageJson extends CourtelJson {

    public WebPageJson() {
        super();
        setArtefactType(ArtefactType.GENERAL_PUBLICATION);
    }
    
    @Override
    public ListType getListType() {
        return ListType.SJP_PUBLIC_LIST;
    }
}
