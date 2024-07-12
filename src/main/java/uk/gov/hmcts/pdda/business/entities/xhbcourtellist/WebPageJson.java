package uk.gov.hmcts.pdda.business.entities.xhbcourtellist;

public class WebPageJson extends CourtelJson {

    public WebPageJson() {
        super();
        setArtefactType(ArtefactType.GENERAL_PUBLICATION);
    }
    
    public ListType getListType() {
        return ListType.SJP_PUBLIC_LIST;
    }
}
