package uk.gov.hmcts.pdda.business.entities.xhbcourtellist;

public class ListJson extends CourtelJson {

    private ListType listType;
    
    public ListJson() {
        super();
        setArtefactType(ArtefactType.LIST);
    }
    
    @Override
    public ListType getListType() {
        return listType;
    }
    
    public void setListType(ListType listType) {
        this.listType = listType;
    }
}
