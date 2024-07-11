package uk.gov.hmcts.pdda.business.entities.xhbcourtellist;

import uk.gov.hmcts.pdda.business.services.pdda.cath.ArtefactType;

public class ListJson extends CourtelJson {

    private String listType;
    
    public ListJson() {
        super();
        setArtefactType(ArtefactType.LIST);
    }
    
    public String getListType() {
        return listType;
    }
    
    public void setListType(String listType) {
        this.listType = listType;
    }
}
