package uk.gov.hmcts.pdda.business.services.pdda.data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.DummyCaseUtil;
import uk.gov.hmcts.DummyCourtUtil;
import uk.gov.hmcts.DummyDefendantUtil;
import uk.gov.hmcts.DummyDisplayUtil;
import uk.gov.hmcts.DummyHearingUtil;
import uk.gov.hmcts.DummyJudgeUtil;
import uk.gov.hmcts.pdda.business.entities.xhbcase.XhbCaseDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import uk.gov.hmcts.pdda.business.entities.xhbcrlivedisplay.XhbCrLiveDisplayDao;
import uk.gov.hmcts.pdda.business.entities.xhbdefendant.XhbDefendantDao;
import uk.gov.hmcts.pdda.business.entities.xhbdefendantoncase.XhbDefendantOnCaseDao;
import uk.gov.hmcts.pdda.business.entities.xhbhearing.XhbHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbhearinglist.XhbHearingListDao;
import uk.gov.hmcts.pdda.business.entities.xhbrefhearingtype.XhbRefHearingTypeDao;
import uk.gov.hmcts.pdda.business.entities.xhbrefjudge.XhbRefJudgeDao;
import uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant.XhbSchedHearingDefendantDao;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingDao;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings({"PMD.TooManyMethods", "PMD.UseObjectForClearerAPI",
    "PMD.CouplingBetweenObjects"})
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
        boolean result = testValidateCourtSite(dao, true);
        assertTrue(result, TRUE);
    }

    private boolean testValidateCourtSite(XhbCourtSiteDao dao, boolean isPresent) {
        classUnderTest.isPresent = isPresent;
        Optional<XhbCourtSiteDao> result =
            classUnderTest.validateCourtSite(dao.getCourtSiteName(), dao.getCourtSiteCode());
        return result.isPresent();
    }

    /**
     * validateCourtRoom.
     */
    @Test
    void testValidateCourtRoom() {
        XhbCourtRoomDao dao = DummyCourtUtil.getXhbCourtRoomDao();
        boolean result = testValidateCourtRoom(dao, true);
        assertTrue(result, TRUE);
    }

    private boolean testValidateCourtRoom(XhbCourtRoomDao dao, boolean isPresent) {
        classUnderTest.isPresent = isPresent;
        Optional<XhbCourtRoomDao> result =
            classUnderTest.validateCourtRoom(dao.getCourtRoomId(), dao.getCrestCourtRoomNo());
        return result.isPresent();
    }

    /**
     * validateHearingType.
     */
    @Test
    void testValidateHearingType() {
        XhbRefHearingTypeDao dao = DummyHearingUtil.getXhbRefHearingTypeDao();
        boolean result = testValidateHearingType(dao, false);
        assertTrue(result, TRUE);
        result = testValidateHearingType(dao, true);
        assertTrue(result, TRUE);
    }

    private boolean testValidateHearingType(XhbRefHearingTypeDao dao, boolean isPresent) {
        classUnderTest.isPresent = isPresent;
        Optional<XhbRefHearingTypeDao> result = classUnderTest.validateHearingType(dao.getCourtId(),
            dao.getHearingTypeCode(), dao.getHearingTypeDesc(), dao.getCategory());
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
        Optional<XhbHearingListDao> result =
            classUnderTest.validateHearingList(dao.getCourtId(), dao.getCrestListId(),
                dao.getListType(), dao.getStatus(), dao.getStartDate(), dao.getPublishedTime(),
                dao.getPrintReference(), dao.getEditionNo(), dao.getListCourtType());
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
            dao.getCourtRoomId(), dao.getIsFloating(), dao.getSittingTime(), dao.getListId());
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
     * validateJudge.
     */
    @Test
    void testValidateJudge() {
        XhbRefJudgeDao dao = DummyJudgeUtil.getXhbRefJudgeDao();
        boolean result = testValidateJudge(dao, false);
        assertTrue(result, TRUE);
        result = testValidateJudge(dao, true);
        assertTrue(result, TRUE);
    }

    private boolean testValidateJudge(XhbRefJudgeDao dao, boolean isPresent) {
        classUnderTest.isPresent = isPresent;
        Optional<XhbRefJudgeDao> result =
            classUnderTest.validateJudge(dao.getCourtId(), dao.getTitle(),
                dao.getFirstname(), dao.getSurname());
        return result.isPresent();
    }
    
    /**
     * validateDefendantOnCase.
     */
    @Test
    void testValidateDefendantOnCase() {
        XhbDefendantOnCaseDao dao = DummyDefendantUtil.getXhbDefendantOnCaseDao();
        boolean result = testValidateDefendantOnCase(dao, false);
        assertTrue(result, TRUE);
        result = testValidateDefendantOnCase(dao, true);
        assertTrue(result, TRUE);
    }

    private boolean testValidateDefendantOnCase(XhbDefendantOnCaseDao dao, boolean isPresent) {
        classUnderTest.isPresent = isPresent;
        Optional<XhbDefendantOnCaseDao> result =
            classUnderTest.validateDefendantOnCase(dao.getCaseId(), dao.getDefendantId(),
                dao.getPublicDisplayHide());
        return result.isPresent();
    }

    /**
     * validateDefendant.
     */
    @Test
    void testValidateDefendant() {
        XhbDefendantDao dao = DummyDefendantUtil.getXhbDefendantDao();
        boolean result = testValidateDefendant(dao, false);
        assertTrue(result, TRUE);
        result = testValidateDefendant(dao, true);
        assertTrue(result, TRUE);
    }

    private boolean testValidateDefendant(XhbDefendantDao dao, boolean isPresent) {
        classUnderTest.isPresent = isPresent;
        Optional<XhbDefendantDao> result =
            classUnderTest.validateDefendant(dao.getCourtId(), dao.getFirstName(),
                dao.getMiddleName(), dao.getSurname(), dao.getGender(), dao.getDateOfBirth(),
                dao.getPublicDisplayHide());
        return result.isPresent();
    }

    /**
     * validateHearing.
     */
    @Test
    void testValidateHearing() {
        XhbHearingDao dao = DummyHearingUtil.getXhbHearingDao();
        boolean result = testValidateHearing(dao, false);
        assertTrue(result, TRUE);
        result = testValidateHearing(dao, true);
        assertTrue(result, TRUE);
    }

    private boolean testValidateHearing(XhbHearingDao dao, boolean isPresent) {
        classUnderTest.isPresent = isPresent;
        Optional<XhbHearingDao> result =
            classUnderTest.validateHearing(dao.getCourtId(), dao.getCaseId(),
                dao.getRefHearingTypeId(), dao.getHearingStartDate(), dao.getHearingEndDate());
        return result.isPresent();
    }

    /**
     * validateScheduledHearing.
     */
    @Test
    void testValidateScheduledHearing() {
        XhbScheduledHearingDao dao = DummyHearingUtil.getXhbScheduledHearingDao();
        boolean result = testValidateScheduledHearing(dao, false);
        assertTrue(result, TRUE);
        result = testValidateScheduledHearing(dao, true);
        assertTrue(result, TRUE);
    }

    private boolean testValidateScheduledHearing(XhbScheduledHearingDao dao, boolean isPresent) {
        classUnderTest.isPresent = isPresent;
        Optional<XhbScheduledHearingDao> result = classUnderTest.validateScheduledHearing(
            dao.getSittingId(), dao.getHearingId(), dao.getNotBeforeTime());
        return result.isPresent();
    }

    /**
     * validateSchedHearingDefendant.
     */
    @Test
    void testValidateSchedHearingDefendant() {
        XhbSchedHearingDefendantDao dao = DummyHearingUtil.getXhbSchedHearingDefendantDao();
        boolean result = testValidateSchedHearingDefendant(dao, false);
        assertTrue(result, TRUE);
        result = testValidateSchedHearingDefendant(dao, true);
        assertTrue(result, TRUE);
    }

    private boolean testValidateSchedHearingDefendant(XhbSchedHearingDefendantDao dao,
        boolean isPresent) {
        classUnderTest.isPresent = isPresent;
        Optional<XhbSchedHearingDefendantDao> result = classUnderTest
            .validateSchedHearingDefendant(dao.getScheduledHearingId(), dao.getDefendantOnCaseId());
        return result.isPresent();
    }

    /**
     * ` validateCrLiveDisplay.
     */
    @Test
    void testValidateCrLiveDisplay() {
        XhbCrLiveDisplayDao dao = DummyDisplayUtil.getXhbCrLiveDisplayDao();
        boolean result = testValidateCrLiveDisplay(dao, false);
        assertTrue(result, TRUE);
        result = testValidateCrLiveDisplay(dao, true);
        assertTrue(result, TRUE);
    }

    private boolean testValidateCrLiveDisplay(XhbCrLiveDisplayDao dao, boolean isPresent) {
        classUnderTest.isPresent = isPresent;
        Optional<XhbCrLiveDisplayDao> result = classUnderTest.validateCrLiveDisplay(
            dao.getCourtRoomId(), dao.getScheduledHearingId(), dao.getTimeStatusSet());
        return result.isPresent();
    }

    /**
     * Local test version of the DataHelper.
     */
    public class LocalDataHelper extends DataHelper {

        private static final long serialVersionUID = 1L;
        public boolean isPresent;

        /**
         * validateCourtSite overrides.
         */
        @Override
        public Optional<XhbCourtSiteDao> findCourtSite(final String courtHouseName,
            final String courtHouseCode) {
            return this.isPresent ? Optional.of(new XhbCourtSiteDao()) : Optional.empty();
        }

        @Override
        public Optional<XhbCourtSiteDao> createCourtSite(final Integer courtId,
            final String courteName, final String courtHouseCode) {
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
            final LocalDateTime startDate, final LocalDateTime publishedTime,
            final String printReference, final Integer editionNo, final String listCourtType) {
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
            final Integer courtRoomId, final String isFloating, final LocalDateTime sittingTime,
            final Integer listId) {
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
        
        /**
         * validateJudge overrides.
         */
        @Override
        public Optional<XhbRefJudgeDao> findJudge(final Integer courtId, final String firstname,
            final String surname) {
            return this.isPresent ? Optional.of(new XhbRefJudgeDao()) : Optional.empty();
        }

        @Override
        public Optional<XhbRefJudgeDao> createRefJudge(final Integer courtId, final String title,
            final String firstname, final String surname) {
            return Optional.of(new XhbRefJudgeDao());
        }

        /**
         * validateDefendantOnCase overrides.
         */
        @Override
        public Optional<XhbDefendantOnCaseDao> findDefendantOnCase(final Integer caseId,
            final Integer defendantId) {
            return this.isPresent ? Optional.of(new XhbDefendantOnCaseDao()) : Optional.empty();
        }

        @Override
        public Optional<XhbDefendantOnCaseDao> createDefendantOnCase(final Integer caseId,
            final Integer defendantId, final String publicDisplayHide) {
            return Optional.of(new XhbDefendantOnCaseDao());
        }

        /**
         * validateDefendant overrides.
         */
        @Override
        public Optional<XhbDefendantDao> findDefendant(final Integer courtId,
            final String firstName, final String middleName, final String surname,
            final Integer gender, final LocalDateTime dateOfBirth) {
            return this.isPresent ? Optional.of(new XhbDefendantDao()) : Optional.empty();
        }

        @Override
        public Optional<XhbDefendantDao> createDefendant(final Integer courtId,
            final String firstName, final String middleName, final String surname,
            final Integer gender, final LocalDateTime dateOfBirth, final String publicDisplayHide) {
            return Optional.of(new XhbDefendantDao());
        }

        /**
         * validateHearingType overrides.
         */
        @Override
        public Optional<XhbRefHearingTypeDao> findHearingType(final Integer courtId,
            final String hearingTypeCode, final String hearingTypeDesc, final String category) {
            return this.isPresent ? Optional.of(new XhbRefHearingTypeDao()) : Optional.empty();
        }

        @Override
        public Optional<XhbRefHearingTypeDao> createHearingType(final Integer courtId,
            final String hearingTypeCode, final String hearingTypeDesc, final String category) {
            return Optional.of(new XhbRefHearingTypeDao());
        }

        /**
         * validateHearing overrides.
         */
        @Override
        public Optional<XhbHearingDao> findHearing(final Integer courtId, final Integer caseId,
            final LocalDateTime hearingStartDate) {
            return this.isPresent ? Optional.of(new XhbHearingDao()) : Optional.empty();
        }

        @Override
        public Optional<XhbHearingDao> createHearing(final Integer courtId, final Integer caseId,
            final Integer refHearingTypeId, final LocalDateTime hearingStartDate, final LocalDateTime hearingEndDate) {
            return Optional.of(new XhbHearingDao());
        }

        /**
         * validateScheduledHearing overrides.
         */
        @Override
        public Optional<XhbScheduledHearingDao> findScheduledHearing(final Integer courtId,
            final Integer caseId, final LocalDateTime hearingStartDate) {
            return this.isPresent ? Optional.of(new XhbScheduledHearingDao()) : Optional.empty();
        }

        @Override
        public Optional<XhbScheduledHearingDao> createScheduledHearing(final Integer sittingId,
            final Integer hearingId, final LocalDateTime notBeforeTime) {
            return Optional.of(new XhbScheduledHearingDao());
        }

        /**
         * validateSchedHearingDefendant overrides.
         */
        @Override
        public Optional<XhbSchedHearingDefendantDao> findSchedHearingDefendant(
            final Integer scheduledHearingId, final Integer defendantOnCaseId) {
            return this.isPresent ? Optional.of(new XhbSchedHearingDefendantDao())
                : Optional.empty();
        }

        @Override
        public Optional<XhbSchedHearingDefendantDao> createSchedHearingDefendant(
            final Integer scheduledHearingId, final Integer defendantOnCaseId) {
            return Optional.of(new XhbSchedHearingDefendantDao());
        }

        /**
         * validateCrLiveDisplay overrides.
         */
        @Override
        public Optional<XhbCrLiveDisplayDao> findCrLiveDisplay(final Integer courtRoomId) {
            return this.isPresent ? Optional.of(new XhbCrLiveDisplayDao()) : Optional.empty();
        }

        @Override
        public Optional<XhbCrLiveDisplayDao> createCrLiveDisplay(final Integer courtRoomId,
            final Integer scheduledHearingId, final LocalDateTime timeStatusSet) {
            return Optional.of(new XhbCrLiveDisplayDao());
        }
        
        @Override
        public Optional<XhbCrLiveDisplayDao> updateCrLiveDisplay(final XhbCrLiveDisplayDao dao) {
            return Optional.of(new XhbCrLiveDisplayDao());
        }
    }
}
