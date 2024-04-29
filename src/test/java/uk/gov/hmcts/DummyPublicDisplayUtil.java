package uk.gov.hmcts;

import uk.gov.hmcts.pdda.business.entities.xhbdefinitivepublicnotice.XhbDefinitivePublicNoticeDao;
import uk.gov.hmcts.pdda.business.entities.xhbdisplay.XhbDisplayDao;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaycourtroom.XhbDisplayCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaydocument.XhbDisplayDocumentDao;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaylocation.XhbDisplayLocationDao;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaytype.XhbDisplayTypeDao;
import uk.gov.hmcts.pdda.business.entities.xhbpublicnotice.XhbPublicNoticeDao;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsetdd.XhbRotationSetDdDao;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsets.XhbRotationSetsDao;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public final class DummyPublicDisplayUtil {
    
    private static final String NOTNULL = "Result is Null";
    private static final String TEST1 = "Test1";
    private static final String TEST2 = "Test2";
    private static final String DESCRIPTIONCODE = "descriptionCode";
    
    private DummyPublicDisplayUtil() {
        // Do nothing
    }

    public static XhbPublicNoticeDao getXhbPublicNoticeDao() {
        Integer publicNoticeId = Integer.valueOf(-1);
        String publicNoticeDesc = "publicNoticeDesc";
        Integer courtId = Integer.valueOf(-1);
        Integer definitivePnId = Integer.valueOf(-1);
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = TEST2;
        String createdBy = TEST1;
        Integer version = Integer.valueOf(3);
        XhbPublicNoticeDao result = new XhbPublicNoticeDao(publicNoticeId, publicNoticeDesc, courtId, lastUpdateDate,
            creationDate, lastUpdatedBy, createdBy, version, definitivePnId);
        publicNoticeId = result.getPrimaryKey();
        assertNotNull(publicNoticeId, NOTNULL);
        result.setXhbDefinitivePublicNotice(result.getXhbDefinitivePublicNotice());
        return new XhbPublicNoticeDao(result);
    }

    public static XhbDefinitivePublicNoticeDao getXhbDefinitivePublicNoticeDao() {
        Integer definitivePnId = Integer.valueOf(-1);
        String definitivePnDesc = "definitivePnDesc";
        Integer priority = Integer.valueOf(-1);
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = TEST2;
        String createdBy = TEST1;
        Integer version = Integer.valueOf(3);
        XhbDefinitivePublicNoticeDao result = new XhbDefinitivePublicNoticeDao(definitivePnId, definitivePnDesc,
            priority, lastUpdateDate, creationDate, lastUpdatedBy, createdBy, version);
        definitivePnId = result.getPrimaryKey();
        assertNotNull(definitivePnId, NOTNULL);
        return new XhbDefinitivePublicNoticeDao(result);
    }
    
    public static XhbDisplayLocationDao getXhbDisplayLocationDao() {
        Integer displayLocationId = Integer.valueOf(-1);
        String descriptionCode = DESCRIPTIONCODE;
        Integer courtSiteId = Integer.valueOf(-1);
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = TEST2;
        String createdBy = TEST1;
        Integer version = Integer.valueOf(3);
        XhbDisplayLocationDao result = new XhbDisplayLocationDao(displayLocationId, descriptionCode, courtSiteId,
            lastUpdateDate, creationDate, lastUpdatedBy, createdBy, version);
        displayLocationId = result.getPrimaryKey();
        assertNotNull(displayLocationId, NOTNULL);
        result.setXhbCourtSite(DummyCourtUtil.getXhbCourtSiteDao());
        return result;
    }

    public static XhbDisplayDao getXhbDisplayDao() {
        Integer displayId = Integer.valueOf(-1);
        Integer displayTypeId = Integer.valueOf(-1);
        Integer displayLocationId = Integer.valueOf(-1);
        Integer rotationSetId = Integer.valueOf(-1);
        String descriptionCode = DESCRIPTIONCODE;
        String locale = "GB_en";
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = TEST2;
        String createdBy = TEST1;
        Integer version = Integer.valueOf(3);
        String showUnassignedYN = "N";
        XhbDisplayDao result = new XhbDisplayDao();
        result.setDisplayId(displayId);
        result.setDisplayTypeId(displayTypeId);
        result.setDisplayLocationId(displayLocationId);
        result.setRotationSetId(rotationSetId);
        result.setDescriptionCode(descriptionCode);
        result.setLocale(locale);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);
        result.setShowUnassignedYn(showUnassignedYN);
        displayId = result.getPrimaryKey();
        assertNotNull(displayId, NOTNULL);
        result.setDisplayTypeId(result.getDisplayTypeId());
        result.setDisplayLocationId(result.getDisplayLocationId());
        return result;
    }

    public static XhbDisplayTypeDao getXhbDisplayTypeDao() {
        Integer displayTypeId = Integer.valueOf(-1);
        String descriptionCode = DESCRIPTIONCODE;
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = TEST2;
        String createdBy = TEST1;
        Integer version = Integer.valueOf(3);
        XhbDisplayTypeDao result = new XhbDisplayTypeDao(displayTypeId, descriptionCode, lastUpdateDate, creationDate,
            lastUpdatedBy, createdBy, version);
        return new XhbDisplayTypeDao(result);
    }
    
    public static XhbDisplayDocumentDao getXhbDisplayDocumentDao() {
        Integer displayDocumentId = Integer.valueOf(-1);
        Integer defaultPageDelay = 10;
        String descriptionCode = DESCRIPTIONCODE;
        String multipleCourtYn = "N";
        String country = "GB";
        String language = "en";
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(15);
        String lastUpdatedBy = TEST2;
        String createdBy = TEST1;
        Integer version = Integer.valueOf(2);

        XhbDisplayDocumentDao result = new XhbDisplayDocumentDao();
        result.setDisplayDocumentId(displayDocumentId);
        result.setDescriptionCode(descriptionCode);
        result.setDefaultPageDelay(defaultPageDelay);
        result.setMultipleCourtYn(multipleCourtYn);
        result.setCountry(country);
        result.setLanguage(language);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);
        return new XhbDisplayDocumentDao(result);
    }

    public static XhbRotationSetDdDao getXhbRotationSetDdDao() {
        Integer rotationSetDdId = Integer.valueOf(-1);
        Integer rotationSetId = Integer.valueOf(-1);
        Integer displayDocumentId = Integer.valueOf(-1);
        Integer pageDelay = Integer.valueOf(-1);
        Integer ordering = Integer.valueOf(-1);
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = TEST2;
        String createdBy = TEST1;
        Integer version = Integer.valueOf(3);
        XhbRotationSetDdDao result = new XhbRotationSetDdDao();
        result.setRotationSetDdId(rotationSetDdId);
        result.setRotationSetId(rotationSetId);
        result.setDisplayDocumentId(displayDocumentId);
        result.setPageDelay(pageDelay);
        result.setOrdering(ordering);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);
        rotationSetDdId = result.getPrimaryKey();
        assertNotNull(rotationSetDdId, NOTNULL);
        result.setDisplayDocumentId(result.getDisplayDocumentId());
        return new XhbRotationSetDdDao(result);
    }
    
    public static XhbDisplayCourtRoomDao getXhbDisplayCourtRoomDao() {
        XhbDisplayCourtRoomDao result = new XhbDisplayCourtRoomDao();
        result.setDisplayId(Integer.valueOf(-1));
        result.setCourtRoomId(Integer.valueOf(-1));
        return new XhbDisplayCourtRoomDao(result);
    }
    
    public static XhbRotationSetsDao getXhbRotationSetsDao() {
        Integer rotationSetsId = Integer.valueOf(-1);
        Integer courtId = Integer.valueOf(-1);
        String description = "description";
        String defaultYn = "defaultYn";
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = TEST2;
        String createdBy = TEST1;
        Integer version = Integer.valueOf(3);
        XhbRotationSetsDao result = new XhbRotationSetsDao();
        result.setRotationSetId(rotationSetsId);
        result.setCourtId(courtId);
        result.setDescription(description);
        result.setDefaultYn(defaultYn);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);
        return result;
    }
}
