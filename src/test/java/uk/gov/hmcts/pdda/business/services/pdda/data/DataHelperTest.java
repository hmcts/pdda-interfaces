package uk.gov.hmcts.pdda.business.services.pdda.data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.DummyCaseUtil;
import uk.gov.hmcts.DummyCourtUtil;
import uk.gov.hmcts.DummyHearingUtil;
import uk.gov.hmcts.pdda.business.entities.xhbcase.XhbCaseDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import uk.gov.hmcts.pdda.business.entities.xhbhearinglist.XhbHearingListDao;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingDao;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("PMD.TooManyMethods")
class DataHelperTest {

    private static final String TRUE = "Result is False";

    @InjectMocks
    private final LocalDataHelper classUnderTest = new LocalDataHelper();

    /**
     * validateCourtSite.
     */
    @Test
    void testValidateCourtSite() {
        XhbCourtSiteDao dao = DummyCourtUtil.getXhbCourtSiteDao();
        boolean result = testValidateCourtSite(dao, false);
        assertTrue(result, TRUE);
        result = testValidateCourtSite(dao, true);
        assertTrue(result, TRUE);
    }

    private boolean testValidateCourtSite(XhbCourtSiteDao dao, boolean isPresent) {
        classUnderTest.isPresent = isPresent;
        Optional<XhbCourtSiteDao> result = classUnderTest.validateCourtSite(dao.getCourtSiteId(),
            dao.getCourtSiteName(), dao.getCourtSiteCode());
        return result.isPresent();
    }

    /**
     * validateCourtRoom.
     */
    @Test
    void testValidateCourtRoom() {
        XhbCourtRoomDao dao = DummyCourtUtil.getXhbCourtRoomDao();
        boolean result = testValidateCourtRoom(dao, false);
        assertTrue(result, TRUE);
        result = testValidateCourtRoom(dao, true);
        assertTrue(result, TRUE);
    }

    private boolean testValidateCourtRoom(XhbCourtRoomDao dao, boolean isPresent) {
        classUnderTest.isPresent = isPresent;
        Optional<XhbCourtRoomDao> result = classUnderTest.validateCourtRoom(dao.getCourtRoomId(),
            dao.getCourtRoomName(), dao.getDescription(), dao.getCrestCourtRoomNo());
        return result.isPresent();
    }

    /**
     * validateHearingList.
     */
    @Test
    void testValidateHearingList() {
        XhbHearingListDao dao = DummyHearingUtil.getXhbHearingListDao();
        boolean result = testValidateHearingList(dao, false);
        assertTrue(result, TRUE);
        result = testValidateHearingList(dao, true);
        assertTrue(result, TRUE);
    }

    private boolean testValidateHearingList(XhbHearingListDao dao, boolean isPresent) {
        classUnderTest.isPresent = isPresent;
        Optional<XhbHearingListDao> result = classUnderTest.validateHearingList(dao.getCourtId(),
            dao.getCrestListId(), dao.getListType(), dao.getStatus(), dao.getStartDate());
        return result.isPresent();
    }

    /**
     * validateSitting.
     */
    @Test
    void testValidateSitting() {
        XhbSittingDao dao = DummyHearingUtil.getXhbSittingDao();
        boolean result = testValidateSitting(dao, false);
        assertTrue(result, TRUE);
        result = testValidateSitting(dao, true);
        assertTrue(result, TRUE);
    }

    private boolean testValidateSitting(XhbSittingDao dao, boolean isPresent) {
        classUnderTest.isPresent = isPresent;
        Optional<XhbSittingDao> result = classUnderTest.validateSitting(dao.getCourtSiteId(),
            dao.getCourtRoomId(), dao.getIsFloating(), dao.getSittingTime());
        return result.isPresent();
    }

    /**
     * validateCase.
     */
    @Test
    void testValidateCase() {
        XhbCaseDao dao = DummyCaseUtil.getXhbCaseDao();
        boolean result = testValidateCase(dao, false);
        assertTrue(result, TRUE);
        result = testValidateCase(dao, true);
        assertTrue(result, TRUE);
    }

    private boolean testValidateCase(XhbCaseDao dao, boolean isPresent) {
        classUnderTest.isPresent = isPresent;
        Optional<XhbCaseDao> result =
            classUnderTest.validateCase(dao.getCourtId(), dao.getCaseType(), dao.getCaseNumber());
        return result.isPresent();
    }

    /**
     * Local test version of the DataHelper.
     */
    public class LocalDataHelper extends DataHelper {

        public boolean isPresent;

        /**
         * validateCourtSite overrides.
         */
        @Override
        public Optional<XhbCourtSiteDao> findCourtSite(final Integer courtId,
            final String courtSiteName) {
            return this.isPresent ? Optional.of(new XhbCourtSiteDao()) : Optional.empty();
        }

        @Override
        public Optional<XhbCourtSiteDao> createCourtSite(final Integer courtId,
            final String courtSiteName, final String courtSiteCode) {
            return Optional.of(new XhbCourtSiteDao());
        }

        /**
         * validateCourtRoom overrides.
         */
        @Override
        public Optional<XhbCourtRoomDao> findCourtRoom(final Integer courtId,
            final Integer crestCourtRoomNo) {
            return this.isPresent ? Optional.of(new XhbCourtRoomDao()) : Optional.empty();
        }

        @Override
        public Optional<XhbCourtRoomDao> createCourtRoom(final Integer courtSiteId,
            final String courtRoomName, final String description, final Integer crestCourtRoomNo) {
            return Optional.of(new XhbCourtRoomDao());
        }


        /**
         * validateHearingList overrides.
         */
        @Override
        public Optional<XhbHearingListDao> findHearingList(final Integer courtId,
            final String status, final LocalDateTime startDate) {
            return this.isPresent ? Optional.of(new XhbHearingListDao()) : Optional.empty();
        }

        @Override
        public Optional<XhbHearingListDao> createHearingList(final Integer courtId,
            final Integer crestListId, final String listType, final String status,
            final LocalDateTime startDate) {
            return Optional.of(new XhbHearingListDao());
        }

        /**
         * validateSitting overrides.
         */
        @Override
        public Optional<XhbSittingDao> findSitting(final Integer courtSiteId,
            final Integer courtRoomId, final LocalDateTime sittingTime) {
            return this.isPresent ? Optional.of(new XhbSittingDao()) : Optional.empty();
        }

        @Override
        public Optional<XhbSittingDao> createSitting(final Integer courtSiteId,
            final Integer courtRoomId, final String isFloating, final LocalDateTime sittingTime) {
            return Optional.of(new XhbSittingDao());
        }

        /**
         * validateCase overrides.
         */
        @Override
        public Optional<XhbCaseDao> findCase(final Integer courtId, final String caseType,
            final Integer caseNumber) {
            return this.isPresent ? Optional.of(new XhbCaseDao()) : Optional.empty();
        }

        @Override
        public Optional<XhbCaseDao> createCase(final Integer courtId, final String caseType,
            final Integer caseNumber) {
            return Optional.of(new XhbCaseDao());
        }
    }
}
