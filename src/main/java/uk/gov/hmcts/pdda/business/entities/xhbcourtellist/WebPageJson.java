package uk.gov.hmcts.pdda.business.entities.xhbcourtellist;

import uk.gov.hmcts.pdda.business.services.pdda.cath.ArtefactType;

public class WebPageJson extends CourtelJson {

    public WebPageJson() {
        super();
        setArtefactType(ArtefactType.GENERAL_PUBLICATION);
    }
}
