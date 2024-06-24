package uk.gov.hmcts;

import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.XhbCourtelListDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.XhbCourtelListJson;
import java.time.LocalDateTime;

public final class DummyCourtelUtil {

    private static final String TEST1 = "Test1";
    private static final String TEST2 = "Test2";


    private DummyCourtelUtil() {
        // Do nothing
    }

    public static XhbCourtelListDao getXhbCourtelListDao() {
        Integer courtelListId = Integer.valueOf(-1);
        Integer xmlDocumentId = Integer.valueOf(-2);
        Long clobId = Long.valueOf(-3);
        Long blobId = Long.valueOf(-3);
        String messageText = "messageText";
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = TEST2;
        String createdBy = TEST1;
        Integer version = Integer.valueOf(3);
        XhbCourtelListDao result = new XhbCourtelListDao();
        result.setCourtelListId(courtelListId);
        result.setXmlDocumentId(xmlDocumentId);
        result.setXmlDocumentClobId(clobId);
        result.setBlobId(blobId);
        result.setMessageText(messageText);
        result.setSentToCourtel("N");
        result.setNumSendAttempts(0);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);
        return new XhbCourtelListDao(result);
    }
    
    public static XhbCourtelListJson getXhbCourtelListJson() {
        XhbCourtelListJson result = new XhbCourtelListJson();
        return result;
    }

}
