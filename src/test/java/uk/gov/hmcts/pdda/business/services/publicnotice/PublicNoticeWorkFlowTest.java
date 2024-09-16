package uk.gov.hmcts.pdda.business.services.publicnotice;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.DummyCourtUtil;
import uk.gov.hmcts.pdda.business.entities.xhbconfiguredpublicnotice.XhbConfiguredPublicNoticeDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbdefinitivepublicnotice.XhbDefinitivePublicNoticeDao;
import uk.gov.hmcts.pdda.business.entities.xhbpublicnotice.XhbPublicNoticeDao;
import uk.gov.hmcts.pdda.business.vos.services.publicnotice.DisplayablePublicNoticeValue;
import uk.gov.hmcts.pdda.courtlog.vos.CourtLogSubscriptionValue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PublicNoticeWorkFlowTest {

    private static final int COURT_ID = 1;
    private static final String TEST1 = "Test1";
    private static final String TEST2 = "Test2";
    private static final String TRUE = "Result is not True";

    @InjectMocks
    private final PublicNoticeWorkFlow classUnderTest = new PublicNoticeWorkFlow();

    @BeforeAll
    public static void setUp() {
        Mockito.mockStatic(PublicNoticeSelectionManipulator.class);
    }

    @AfterAll
    public static void tearDown() {
        Mockito.clearAllCaches();
    }

    @SuppressWarnings("static-access")
    @Test
    void testSetPublicNoticeforCourtRoom() {
        boolean result;
        CourtLogSubscriptionValue courtLogSubscriptionValue = DummyCourtUtil.getCourtLogSubscriptionValue();
        classUnderTest.setPublicNoticeforCourtRoom(courtLogSubscriptionValue);
        result = true;

        assertTrue(result, TRUE);
    }

    @SuppressWarnings("static-access")
    @Test
    void testSetAllPublicNoticesForCourtRoom() {
        boolean result = false;
        try {
            classUnderTest.setAllPublicNoticesForCourtRoom(
                new DisplayablePublicNoticeValue[] {}, COURT_ID);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }

    @SuppressWarnings("static-access")
    @Test
    void testGetAllPublicNoticesForCourtRoom() {
        DisplayablePublicNoticeValue[] result =
            classUnderTest.getAllPublicNoticesForCourtRoom(COURT_ID, Optional.of(getDummyXhbCourtRoomDao()));
        assertNotNull(result, "Result is Null");
    }

    @Test
    void testPublicNoticeInvalidSelectionException() {
        Assertions.assertThrows(PublicNoticeInvalidSelectionException.class, () -> {
            throw new PublicNoticeInvalidSelectionException(1, "");
        });
    }

    private XhbCourtRoomDao getDummyXhbCourtRoomDao() {
        Integer courtRoomId = Integer.valueOf(-1);
        String courtRoomName = "courtRoomName";
        String description = "description";
        Integer crestCourtRoomNo = Integer.valueOf(-1);
        Integer courtSiteId = Integer.valueOf(-1);
        String displayName = "displayName";
        String securityInd = "securityInd";
        String videoInd = "videoInd";
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = TEST2;
        String createdBy = TEST1;
        Integer version = Integer.valueOf(3);
        String obsInd = "N";
        XhbCourtRoomDao result = new XhbCourtRoomDao();
        result.setCourtRoomId(courtRoomId);
        result.setCourtRoomName(courtRoomName);
        result.setDescription(description);
        result.setCrestCourtRoomNo(crestCourtRoomNo);
        result.setCourtSiteId(courtSiteId);
        result.setDisplayName(displayName);
        result.setSecurityInd(securityInd);
        result.setVideoInd(videoInd);
        result.setObsInd(obsInd);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);
        List<XhbConfiguredPublicNoticeDao> xhbConfiguredPublicNoticeDaos = new ArrayList<>();
        xhbConfiguredPublicNoticeDaos.add(getDummyXhbConfiguredPublicNoticeDao("1"));
        xhbConfiguredPublicNoticeDaos.add(getDummyXhbConfiguredPublicNoticeDao("0"));
        xhbConfiguredPublicNoticeDaos.add(getDummyXhbConfiguredPublicNoticeDao(null));
        result.setXhbConfiguredPublicNotices(xhbConfiguredPublicNoticeDaos);
        return result;
    }

    private XhbConfiguredPublicNoticeDao getDummyXhbConfiguredPublicNoticeDao(String isActiveYN) {
        Integer configuredPublicNoticeId = Integer.valueOf(-1);
        Integer courtRoomId = Integer.valueOf(-1);
        Integer publicNoticeId = Integer.valueOf(-1);
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = TEST2;
        String createdBy = TEST1;
        Integer version = Integer.valueOf(3);

        XhbConfiguredPublicNoticeDao result = new XhbConfiguredPublicNoticeDao(configuredPublicNoticeId, isActiveYN,
            courtRoomId, publicNoticeId, lastUpdateDate, creationDate, lastUpdatedBy, createdBy, version);
        result.setXhbPublicNotice(getDummyXhbPublicNoticeDao());
        return result;
    }

    private XhbPublicNoticeDao getDummyXhbPublicNoticeDao() {
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
        result.setXhbDefinitivePublicNotice(getDummyXhbDefinitivePublicNoticeDao());
        return result;
    }

    private XhbDefinitivePublicNoticeDao getDummyXhbDefinitivePublicNoticeDao() {
        Integer definitivePnId = Integer.valueOf(-1);
        String definitivePnDesc = "definitivePnDesc";
        Integer priority = Integer.valueOf(-1);
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = TEST2;
        String createdBy = TEST1;
        Integer version = Integer.valueOf(3);
        return new XhbDefinitivePublicNoticeDao(definitivePnId, definitivePnDesc, priority, lastUpdateDate,
            creationDate, lastUpdatedBy, createdBy, version);
    }
}
