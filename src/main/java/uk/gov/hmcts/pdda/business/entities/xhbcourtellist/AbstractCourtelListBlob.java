package uk.gov.hmcts.pdda.business.entities.xhbcourtellist;

import uk.gov.hmcts.pdda.business.entities.AbstractVersionedDao;
import uk.gov.hmcts.pdda.business.entities.xhbblob.XhbBlobDao;

public abstract class AbstractCourtelListBlob extends AbstractVersionedDao {

    private static final long serialVersionUID = 1L;
    
    private XhbBlobDao blob;

    public XhbBlobDao getBlob() {
        return blob;
    }

    public void setBlob(XhbBlobDao blob) {
        this.blob = blob;
    }
}
