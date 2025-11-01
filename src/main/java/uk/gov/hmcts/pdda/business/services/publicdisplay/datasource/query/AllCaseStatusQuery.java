package uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.query;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.framework.util.DateTimeUtilities;
import uk.gov.hmcts.pdda.business.entities.xhbcase.XhbCaseDao;
import uk.gov.hmcts.pdda.business.entities.xhbcase.XhbCaseRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcasereference.XhbCaseReferenceRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtlogentry.XhbCourtLogEntryRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdefendant.XhbDefendantDao;
import uk.gov.hmcts.pdda.business.entities.xhbdefendant.XhbDefendantRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdefendantoncase.XhbDefendantOnCaseDao;
import uk.gov.hmcts.pdda.business.entities.xhbdefendantoncase.XhbDefendantOnCaseRepository;
import uk.gov.hmcts.pdda.business.entities.xhbhearing.XhbHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbhearing.XhbHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbhearinglist.XhbHearingListDao;
import uk.gov.hmcts.pdda.business.entities.xhbhearinglist.XhbHearingListRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrefhearingtype.XhbRefHearingTypeDao;
import uk.gov.hmcts.pdda.business.entities.xhbrefhearingtype.XhbRefHearingTypeRepository;
import uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant.XhbSchedHearingDefendantDao;
import uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant.XhbSchedHearingDefendantRepository;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingDao;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingRepository;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.AllCaseStatusValue;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.DefendantName;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This class wraps the stored procedure that provides the data for the all case status document.
 * @author Rakesh Lakhani
 */
@SuppressWarnings({"PMD"})
public class AllCaseStatusQuery extends PublicDisplayQuery {

    private Logger log = LoggerFactory.getLogger(AllCaseStatusQuery.class);

    /**
     * Constructor compiles the query (originally called
     * XHB_PUBLIC_DISPLAY_PKG.GET_ALL_CASE_STATUS).
     */
    public AllCaseStatusQuery(EntityManager entityManager) {
        super(entityManager);
        log.debug("Query object created");
    }

    public AllCaseStatusQuery(EntityManager entityManager, XhbCaseRepository xhbCaseRepository,
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
        XhbRefHearingTypeRepository xhbRefHearingTypeRepository) {
        super(entityManager, xhbCaseRepository, xhbCaseReferenceRepository,
            xhbHearingListRepository, xhbSittingRepository, xhbScheduledHearingRepository,
            xhbCourtSiteRepository, xhbCourtRoomRepository, xhbSchedHearingDefendantRepository,
            xhbHearingRepository, xhbDefendantOnCaseRepository, xhbDefendantRepository,
            xhbCourtLogEntryRepository, xhbRefHearingTypeRepository, null);
    }

    /**
     * Returns an array of CourtListValue.
     * @param date Id
     * @param courtId room ids for which the data is required
     * @param courtRoomIds Court room id.
     * @return Summary by name data for the specified court rooms
     */
    @Override
    public Collection<?> getData(LocalDateTime date, int courtId, int... courtRoomIds) {

        LocalDateTime startDate = DateTimeUtilities.stripTime(date);

        List<AllCaseStatusValue> results = new ArrayList<>();

        // Loop the hearing lists
        List<XhbHearingListDao> hearingListDaos = getHearingListDaos(courtId, startDate);
        if (hearingListDaos.isEmpty()) {
            log.debug("AllCaseStatusQuery - No Hearing Lists found for today");
        } else {
            for (XhbHearingListDao hearingListDao : hearingListDaos) {
                // Loop the sittings
                List<XhbSittingDao> sittingDaos;
                if (isFloatingIncluded()) {
                    sittingDaos = getSittingListDaos(hearingListDao.getListId());
                } else {
                    sittingDaos = getNonFloatingSittingListDaos(hearingListDao.getListId());
                }
                if (!sittingDaos.isEmpty()) {
                    results.addAll(getSittingData(sittingDaos, courtRoomIds));
                }
            }
        }

        return results;
    }

    private List<AllCaseStatusValue> getSittingData(List<XhbSittingDao> sittingDaos,
        int... courtRoomIds) {
        List<AllCaseStatusValue> results = new ArrayList<>();
        for (XhbSittingDao sittingDao : sittingDaos) {
            // Is this a floating sitting
            String floating = getIsFloating(sittingDao.getIsFloating());
            // Loop the scheduledHearings
            List<XhbScheduledHearingDao> scheduledHearingDaos =
                getScheduledHearingDaos(sittingDao.getSittingId());
            if (!scheduledHearingDaos.isEmpty()) {
                results.addAll(getScheduleHearingData(sittingDao, scheduledHearingDaos, floating,
                    courtRoomIds));
            }
        }
        return results;
    }


    private List<AllCaseStatusValue> getScheduleHearingData(XhbSittingDao sittingDao,
        List<XhbScheduledHearingDao> scheduledHearingDaos, String floating, int... courtRoomIds) {

        List<AllCaseStatusValue> results = new ArrayList<>();

        // Pick one scheduled hearing per hearingId, preferring non-null hearingProgress
        Map<Integer, XhbScheduledHearingDao> bestByHearing = new LinkedHashMap<>();
        for (XhbScheduledHearingDao sh : scheduledHearingDaos) {

            // Skip if not in selected rooms
            if (!isSelectedCourtRoom(courtRoomIds, sittingDao.getCourtRoomId(),
                sh.getMovedFromCourtRoomId())) {
                continue;
            }

            XhbScheduledHearingDao current = bestByHearing.get(sh.getHearingId());
            if (current == null) {
                bestByHearing.put(sh.getHearingId(), sh);
            } else {
                // Prefer the one with non-null hearingProgress
                boolean currentHasProgress = current.getHearingProgress() != null;
                boolean candidateHasProgress = sh.getHearingProgress() != null;
                if (!currentHasProgress && candidateHasProgress) {
                    bestByHearing.put(sh.getHearingId(), sh);
                }
                // (tie-breakers are optional; keep existing if both null or both non-null)
            }
        }

        // Now only process the chosen scheduled hearing per hearingId
        for (XhbScheduledHearingDao sh : bestByHearing.values()) {
            List<XhbSchedHearingDefendantDao> shdList =
                getSchedHearingDefendantDaos(sh.getScheduledHearingId());
            if (!shdList.isEmpty()) {
                results.addAll(getSchedHearingDefendantData(sittingDao, sh, shdList, floating));
            }
        }

        return results;
    }


    private List<AllCaseStatusValue> getSchedHearingDefendantData(XhbSittingDao sittingDao,
        XhbScheduledHearingDao scheduledHearingDao,
        List<XhbSchedHearingDefendantDao> schedHearingDefDaos, String floating) {

        List<AllCaseStatusValue> results = new ArrayList<>();
        if (schedHearingDefDaos == null || schedHearingDefDaos.isEmpty()) {
            return results;
        }

        // Choose exactly ONE defendant to display:
        // Prefer the first non-observed (OBS_IND != 'Y') defendant-on-case; otherwise the first
        // item.
        XhbSchedHearingDefendantDao chosen = null;
        for (XhbSchedHearingDefendantDao shd : schedHearingDefDaos) {
            Optional<XhbDefendantOnCaseDao> doc =
                getXhbDefendantOnCaseRepository().findByIdSafe(shd.getDefendantOnCaseId());
            if (doc.isPresent() && !YES.equals(doc.get().getObsInd())) {
                chosen = shd; // prefer a non-OBS defendant
                break; // <-- conditional, allowed; we exit the selection loop only when found
            }
            if (chosen == null) {
                chosen = shd; // fallback to the first item if no better candidate appears
            }
        }

        // Build ONE row using the chosen defendant
        AllCaseStatusValue result = getAllCaseStatusValue();
        populateData(result, sittingDao.getCourtSiteId(), sittingDao.getCourtRoomId(),
            scheduledHearingDao.getMovedFromCourtRoomId(), scheduledHearingDao.getNotBeforeTime());
        result.setFloating(floating);
        result.setNotBeforeTime(scheduledHearingDao.getNotBeforeTime());
        result.setHearingProgress(scheduledHearingDao.getHearingProgress() != null
            ? scheduledHearingDao.getHearingProgress()
            : 0);
        result.setListCourtRoomId(sittingDao.getCourtRoomId());

        boolean isHidden = false;
        Optional<XhbHearingDao> hearingDao = getXhbHearingDao(scheduledHearingDao.getHearingId());
        if (hearingDao.isPresent()) {
            result.setReportingRestricted(isReportingRestricted(hearingDao.get().getCaseId()));

            Optional<XhbCaseDao> caseDao =
                getXhbCaseRepository().findByIdSafe(hearingDao.get().getCaseId());
            if (caseDao.isPresent()) {
                result.setCaseNumber(caseDao.get().getCaseType() + caseDao.get().getCaseNumber());
                result.setCaseTitle(caseDao.get().getCaseTitle());
                isHidden = YES.equals(caseDao.get().getPublicDisplayHide());
                populateEventData(result, hearingDao.get().getCaseId());
            }

            result.setHearingDescription(getRefHearingTypeDesc(hearingDao));
        }

        // Populate chosen defendant's name (if available)
        if (chosen != null) {
            result.setDefendantName(getDefendantName(chosen, isHidden));
        } else {
            log.debug("No defendant found for scheduledHearingId: {}", scheduledHearingDao.getScheduledHearingId());
            result.setDefendantName(
                new DefendantName(EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, true));
        }

        results.add(result);
        return results;
    }


    /**
     * Returns the DefendantName for the chosen scheduled hearing defendant.
     */
    private DefendantName getDefendantName(XhbSchedHearingDefendantDao chosen, boolean isHidden) {
        Optional<XhbDefendantOnCaseDao> doc =
            getXhbDefendantOnCaseRepository().findByIdSafe(chosen.getDefendantOnCaseId());
        if (doc.isPresent() && !YES.equals(doc.get().getObsInd())) {
            Optional<XhbDefendantDao> def =
                getXhbDefendantRepository().findByIdSafe(doc.get().getDefendantId());
            if (def.isPresent()) {
                return getDefendantName(def.get().getFirstName(), def.get().getMiddleName(),
                    def.get().getSurname(), isDefendantHidden(def, doc, isHidden));
            }
        }
        
        return new DefendantName(EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, true);
    }
    
    private DefendantName getDefendantName(String firstName, String middleName, String surname,
        boolean hide) {
        return new DefendantName(firstName, middleName, surname, hide);
    }

    private boolean isDefendantHidden(Optional<XhbDefendantDao> defendantDao,
        Optional<XhbDefendantOnCaseDao> defendantOnCaseDao, boolean isHidden) {
        return isHidden
            || defendantOnCaseDao.isPresent()
                && YES.equals(defendantOnCaseDao.get().getPublicDisplayHide())
            || defendantDao.isPresent()
                && YES.contentEquals(defendantDao.get().getPublicDisplayHide());
    }

    private String getRefHearingTypeDesc(Optional<XhbHearingDao> hearingDao) {
        if (hearingDao.isPresent()) {
            Optional<XhbRefHearingTypeDao> refHearingTypeDao = getXhbRefHearingTypeRepository()
                .findByIdSafe(hearingDao.get().getRefHearingTypeId());
            if (refHearingTypeDao.isPresent()) {
                return refHearingTypeDao.get().getHearingTypeDesc();
            }
        }
        return null;
    }

    protected boolean isFloatingIncluded() {
        // Only show isFloating = '0'
        return false;
    }

    private AllCaseStatusValue getAllCaseStatusValue() {
        return new AllCaseStatusValue();
    }

}
