package uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.query;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbcase.XhbCaseRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcasereference.XhbCaseReferenceDao;
import uk.gov.hmcts.pdda.business.entities.xhbcasereference.XhbCaseReferenceRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtlogentry.XhbCourtLogEntryRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdefendant.XhbDefendantRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdefendantoncase.XhbDefendantOnCaseRepository;
import uk.gov.hmcts.pdda.business.entities.xhbhearing.XhbHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbhearing.XhbHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbhearinglist.XhbHearingListDao;
import uk.gov.hmcts.pdda.business.entities.xhbhearinglist.XhbHearingListRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrefhearingtype.XhbRefHearingTypeRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrefjudge.XhbRefJudgeDao;
import uk.gov.hmcts.pdda.business.entities.xhbrefjudge.XhbRefJudgeRepository;
import uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant.XhbSchedHearingDefendantDao;
import uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant.XhbSchedHearingDefendantRepository;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingDao;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingRepository;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.PublicDisplayValue;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Abstract query class used by public display.
 * 
 * @author pznwc5
 */
@SuppressWarnings({"PMD.ExcessiveParameterList", "PMD.TooManyFields", "PMD.GodClass",
    "PMD.ExcessiveImports", "PMD.CouplingBetweenObjects"})
public abstract class PublicDisplayQuery extends PublicDisplayQueryRepo {

    protected static final String EMPTY_STRING = "";
    protected static final String YES = "Y";
    protected static final String IS_FLOATING = "1";
    protected static final String NOT_FLOATING = "0";

    /** Logger object. */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected PublicDisplayQuery(EntityManager entityManager) {
        super(entityManager);
    }

    protected PublicDisplayQuery(EntityManager entityManager, XhbCaseRepository xhbCaseRepository,
        XhbCaseReferenceRepository xhbCaseReferenceRepository,
        XhbHearingListRepository xhbHearingListRepository,
        XhbSittingRepository xhbSittingRepository,
        XhbScheduledHearingRepository xhbScheduledHearingRepository,
        XhbCourtSiteRepository xhbCourtSiteRepository,
        XhbCourtRoomRepository xhbCourtRoomRepository,
        XhbSchedHearingDefendantRepository xhbSchedHearingDefendantRepository,
        XhbHearingRepository xhbHearingRepository,
        XhbDefendantOnCaseRepository xhbDefendantOnCaseRepository,
        XhbDefendantRepository xhbDefendantRepository,
        XhbCourtLogEntryRepository xhbCourtLogEntryRepository,
        XhbRefHearingTypeRepository xhbRefHearingTypeRepository,
        XhbRefJudgeRepository xhbRefJudgeRepository) {
        super(entityManager, xhbCourtLogEntryRepository, null);
        this.xhbCaseRepository = xhbCaseRepository;
        this.xhbCaseReferenceRepository = xhbCaseReferenceRepository;
        this.xhbHearingListRepository = xhbHearingListRepository;
        this.xhbSittingRepository = xhbSittingRepository;
        this.xhbScheduledHearingRepository = xhbScheduledHearingRepository;
        this.xhbCourtSiteRepository = xhbCourtSiteRepository;
        this.xhbCourtRoomRepository = xhbCourtRoomRepository;
        this.xhbSchedHearingDefendantRepository = xhbSchedHearingDefendantRepository;
        this.xhbHearingRepository = xhbHearingRepository;
        this.xhbDefendantOnCaseRepository = xhbDefendantOnCaseRepository;
        this.xhbDefendantRepository = xhbDefendantRepository;
        this.xhbRefHearingTypeRepository = xhbRefHearingTypeRepository;
        this.xhbRefJudgeRepository = xhbRefJudgeRepository;
    }

    /**
     * Returns an array of SummaryByNameValue.
     * 
     * @param date localDateTime
     * @param courtId room ids for which the data is required
     * @param courtRoomIds Court room ids
     * 
     * @return Summary by name data for the specified court rooms
     */
    public abstract Collection<?> getData(LocalDateTime date, int courtId, int... courtRoomIds);

    protected void populateData(PublicDisplayValue result, Integer courtSiteId, Integer courtRoomId,
        Integer movedFromCourtRoomId, LocalDateTime notBeforeTime) {
        log.debug("populateData({},{},{},{},{})", result, courtSiteId, courtRoomId,
            movedFromCourtRoomId, notBeforeTime);
        // Set not before time
        result.setNotBeforeTime(notBeforeTime);

        // Get the court site
        Optional<XhbCourtSiteDao> courtSite = getXhbCourtSiteRepository().findByIdSafe(courtSiteId);
        if (courtSite.isPresent()) {
            result.setCourtSiteCode(courtSite.get().getCourtSiteCode());
            result.setCourtSiteName(courtSite.get().getCourtSiteName());
            result.setCourtSiteShortName(courtSite.get().getShortName());
        } else {
            result.setCourtSiteCode("Z");
            result.setCourtSiteName("");
            result.setCourtSiteShortName("");
        }

        // Get courtRoom
        Optional<XhbCourtRoomDao> courtRoom = getXhbCourtRoomRepository().findByIdSafe(courtRoomId);
        if (courtRoom.isPresent()) {
            result.setCourtRoomId(courtRoom.get().getCourtRoomId());
            result.setCourtRoomName(courtRoom.get().getDisplayName());
            result.setCrestCourtRoomNo(courtRoom.get().getCrestCourtRoomNo());
        } else {
            result.setCourtRoomId(-1);
            result.setCourtRoomName("");
            result.setCrestCourtRoomNo(99);
        }

        // Moved from courtroom
        result.setMovedFromCourtRoomId(-1);
        result.setMovedFromCourtRoomName("");
        result.setMovedFromCourtSiteShortName("");
        if (movedFromCourtRoomId != null) {
            Optional<XhbCourtRoomDao> movedCourtRoom =
                getXhbCourtRoomRepository().findByIdSafe(movedFromCourtRoomId);
            if (movedCourtRoom.isPresent()) {
                result.setMovedFromCourtRoomId(movedCourtRoom.get().getCourtRoomId());
                result.setMovedFromCourtRoomName(movedCourtRoom.get().getDisplayName());
                Optional<XhbCourtSiteDao> movedCourtSite =
                    getXhbCourtSiteRepository().findByIdSafe(movedCourtRoom.get().getCourtSiteId());
                if (movedCourtSite.isPresent()) {
                    result.setMovedFromCourtSiteShortName(movedCourtSite.get().getShortName());
                }
            }
        }
    }

    protected List<XhbHearingListDao> getHearingListDaos(int courtId, LocalDateTime startDate) {
        return getXhbHearingListRepository().findByCourtIdAndDateSafe(courtId, startDate);
    }

    protected List<XhbSittingDao> getSittingListDaos(Integer listId) {
        return getXhbSittingRepository().findByListIdSafe(listId);
    }

    protected List<XhbSittingDao> getNonFloatingSittingListDaos(Integer listId) {
        return getXhbSittingRepository().findByNonFloatingHearingListSafe(listId);
    }

    protected List<XhbScheduledHearingDao> getScheduledHearingDaos(Integer sittingId) {
        return getXhbScheduledHearingRepository().findBySittingIdSafe(sittingId);
    }

    protected List<XhbSchedHearingDefendantDao> getSchedHearingDefendantDaos(
        Integer scheduledHearingId) {
        return getXhbSchedHearingDefendantRepository()
            .findByScheduledHearingIdSafe(scheduledHearingId);
    }

    protected Optional<XhbRefJudgeDao> getXhbRefJudgeDao(Integer scheduledHearingId) {
        log.debug("getXhbRefJudgeDao({})", scheduledHearingId);
        // Get the judge from the scheduled hearing attendees
        Optional<XhbRefJudgeDao> xhbRefJudgeDao =
            getXhbRefJudgeRepository().findScheduledAttendeeJudge(scheduledHearingId);
        if (xhbRefJudgeDao.isPresent()) {
            log.debug("Found Judge {} in scheduledHearingAttendees",
                xhbRefJudgeDao.get().getRefJudgeId());
        } else {
            xhbRefJudgeDao =
                getXhbRefJudgeRepository().findScheduledSittingJudgeSafe(scheduledHearingId);
            if (xhbRefJudgeDao.isPresent()) {
                log.debug("Found Judge {} in sitting", xhbRefJudgeDao.get().getRefJudgeId());
            } else {
                log.debug("No Judge found");
            }
        }
        return xhbRefJudgeDao;
    }

    protected boolean isSelectedCourtRoom(int[] courtRoomIds, Integer sittingCourtRoomId,
        Integer movedFromCourtRoomId) {
        for (Integer courtRoomId : courtRoomIds) {
            if (courtRoomId.equals(sittingCourtRoomId)
                || courtRoomId.equals(movedFromCourtRoomId)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isReportingRestricted(Integer caseId) {
        boolean result = false;
        List<XhbCaseReferenceDao> caseReferenceDaos =
            getXhbCaseReferenceRepository().findByCaseIdSafe(caseId);
        Integer trueInt = 1;
        if (!caseReferenceDaos.isEmpty()) {
            for (XhbCaseReferenceDao caseReferenceDao : caseReferenceDaos) {
                if (trueInt.equals(caseReferenceDao.getReportingRestrictions())) {
                    result = true;
                    break;
                }
            }
        }
        log.debug("isReportingRestricted({}) = {}", caseId, result ? "True" : "False");
        return result;
    }

    protected boolean isEventDataRequired() {
        return false;
    }

    protected String getIsFloating(String isFloating) {
        return IS_FLOATING.equals(isFloating) ? IS_FLOATING : NOT_FLOATING;
    }

    protected Optional<XhbHearingDao> getXhbHearingDao(Integer hearingId) {
        return getXhbHearingRepository().findByIdSafe(hearingId);
    }
}
