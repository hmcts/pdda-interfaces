package uk.gov.hmcts.pdda.business.entities.xhbcourtellist;

public class XhbCourtelListJson {

    private byte[] blobData;

    public byte[] getBlobData() {
        return blobData.clone();
    }

    public void setBlobData(byte[] blobData) {
        this.blobData = blobData.clone();
    }
}
