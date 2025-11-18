package uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.query;

import jakarta.persistence.EntityManager;
import uk.gov.hmcts.framework.util.DateTimeUtilities;
import uk.gov.hmcts.pdda.business.entities.xhbcase.XhbCaseDao;
import uk.gov.hmcts.pdda.business.entities.xhbcase.XhbCaseRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcasereference.XhbCaseReferenceRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtlogentry.XhbCourtLogEntryRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
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
import uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant.XhbSchedHearingDefendantDao;
import uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant.XhbSchedHearingDefendantRepository;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingDao;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingRepository;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.AllCourtStatusValue;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.DefendantName;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * This class wraps the stored procedure that provides the data for the court list document.

 * @author pznwc5
 */
@SuppressWarnings("PMD")
public class AllCourtStatusQuery extends PublicDisplayQuery {

    /**
     * Constructor compiles the query (originally called)
     * XHB_PUBLIC_DISPLAY_PKG.GET_ALL_COURT_STATUS.
     */
    public AllCourtStatusQuery(EntityManager entityManager) {
        super(entityManager);
        log.debug("Query object created");
    }

    public AllCourtStatusQuery(EntityManager entityManager, XhbCaseRepository xhbCaseRepository,
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
        XhbCourtLogEntryRepository xhbCourtLogEntryRepository) {
        super(entityManager, xhbCaseRepository, xhbCaseReferenceRepository,
            xhbHearingListRepository, xhbSittingRepository, xhbScheduledHearingRepository,
            xhbCourtSiteRepository, xhbCourtRoomRepository, xhbSchedHearingDefendantRepository,
            xhbHearingRepository, xhbDefendantOnCaseRepository, xhbDefendantRepository,
            xhbCourtLogEntryRepository, null, null);
    }

    /**
     * Returns an array of CourtListValue.

     * @param date localdateTime
     * @param courtId room ids for which the data is required
     * @param courtRoomIds Court room ids

     * @return Summary by name data for the specified court rooms
     */
    @Override
    public Collection<?> getData(LocalDateTime date, int courtId, int... courtRoomIds) {

        LocalDateTime startDate = DateTimeUtilities.stripTime(date);

        List<AllCourtStatusValue> results = new ArrayList<>();

        // Loop the hearing lists
        List<XhbHearingListDao> hearingListDaos = getHearingListDaos(courtId, startDate);
        if (hearingListDaos.isEmpty()) {
            log.debug("AllCourtStatusQuery - No Hearing Lists found for today");
        } else {
            for (XhbHearingListDao hearingListDao : hearingListDaos) {
                // Loop the sittings (floating and non-floating)
                List<XhbSittingDao> sittingDaos = getSittingListDaos(hearingListDao.getListId());
                if (!sittingDaos.isEmpty()) {
                    results.addAll(getSittingData(sittingDaos, courtRoomIds));
                }
            }
        }
        
        // Ensure every courtroom appears, even if it had no hearings today
        addEmptyCourtroomsIfMissing(results, courtId);

        // (optional) keep a stable sort by courtroom name/number
        // keep a stable numeric order by court room id (nulls last), then a harmless tie-break
        results.sort(
            Comparator.<AllCourtStatusValue>comparingInt(
                v -> v.getCourtRoomId() == null ? Integer.MAX_VALUE : v.getCourtRoomId()
            ).thenComparing(
                v -> v.getCaseNumber() == null ? "" : v.getCaseNumber()
            )
        );

        return results;
    }

    private List<AllCourtStatusValue> getSittingData(List<XhbSittingDao> sittingDaos,
        int... courtRoomIds) {
        List<AllCourtStatusValue> results = new ArrayList<>();
        for (XhbSittingDao sittingDao : sittingDaos) {

            // Loop the scheduledHearings
            List<XhbScheduledHearingDao> scheduledHearingDaos =
                getScheduledHearingDaos(sittingDao.getSittingId());
            if (!scheduledHearingDaos.isEmpty()) {
                results.addAll(getScheduleData(sittingDao, scheduledHearingDaos, courtRoomIds));
            }
        }
        return results;
    }

    private List<AllCourtStatusValue> getScheduleData(
        XhbSittingDao sittingDao,
        List<XhbScheduledHearingDao> scheduledHearingDaos,
        int... courtRoomIds) {

        List<AllCourtStatusValue> results = new ArrayList<>();
    
        // Deduplicate: one scheduled hearing per hearingId, prefer non-null hearingProgress.
        // Still require room selection and active cases.
        Map<Integer, XhbScheduledHearingDao> bestByHearing = new LinkedHashMap<>();
    
        for (XhbScheduledHearingDao sh : scheduledHearingDaos) {
            // Filter by selected courtroom (unless movedFrom applies) and active flag
            if (!isSelectedCourtRoom(courtRoomIds, sittingDao.getCourtRoomId(), sh.getMovedFromCourtRoomId())
                    || !YES.equals(sh.getIsCaseActive())) {
                continue;
            }
    
            XhbScheduledHearingDao current = bestByHearing.get(sh.getHearingId());
            if (current == null) {
                bestByHearing.put(sh.getHearingId(), sh);
            } else {
                boolean currHasProg = current.getHearingProgress() != null;
                boolean candHasProg = sh.getHearingProgress() != null;
    
                if (!currHasProg && candHasProg) {
                    // Prefer the row that shows progress
                    bestByHearing.put(sh.getHearingId(), sh);
                }
            }
        }
    
        // Emit only the chosen scheduled hearing per hearingId
        for (XhbScheduledHearingDao sh : bestByHearing.values()) {
            boolean isCaseHidden = false;
    
            AllCourtStatusValue row = getAllCourtStatusValue();
            populateData(row, sittingDao.getCourtSiteId(), sittingDao.getCourtRoomId(),
                    sh.getMovedFromCourtRoomId(), sh.getNotBeforeTime());
    
            // Hearing / case details
            Optional<XhbHearingDao> hearingDao = getXhbHearingDao(sh.getHearingId());
            if (hearingDao.isPresent()) {
                row.setReportingRestricted(isReportingRestricted(hearingDao.get().getCaseId()));
                Optional<XhbCaseDao> caseDao = getXhbCaseRepository().findByIdSafe(hearingDao.get().getCaseId());
                if (caseDao.isPresent()) {
                    row.setCaseNumber(caseDao.get().getCaseType() + caseDao.get().getCaseNumber());
                    row.setCaseTitle(caseDao.get().getCaseTitle());
                    isCaseHidden = YES.equals(caseDao.get().getPublicDisplayHide());
                    populateEventData(row, hearingDao.get().getCaseId());
                }
            }
    
            // Defendants for the chosen scheduled hearing
            List<XhbSchedHearingDefendantDao> shdList = getSchedHearingDefendantDaos(sh.getScheduledHearingId());
            if (!shdList.isEmpty()) {
                populateScheduleDefendantData(row, shdList, isCaseHidden);
            }
    
            results.add(row);
        }

        return results;
    }


    private void populateScheduleDefendantData(AllCourtStatusValue result,
        List<XhbSchedHearingDefendantDao> schedDaos, boolean isCaseHidden) {
        for (XhbSchedHearingDefendantDao schedDao : schedDaos) {

            // Get the defendant on case
            Optional<XhbDefendantOnCaseDao> defendantOnCaseDao =
                getXhbDefendantOnCaseRepository().findByIdSafe(schedDao.getDefendantOnCaseId());
            if (defendantOnCaseDao.isPresent()
                && !YES.equals(defendantOnCaseDao.get().getObsInd())) {

                // Get the defendant
                Optional<XhbDefendantDao> defendantDao =
                    getXhbDefendantRepository()
                        .findByIdSafe(defendantOnCaseDao.get().getDefendantId());
                if (defendantDao.isPresent()) {
                    boolean isHidden =
                        isCaseHidden || YES.equals(defendantOnCaseDao.get().getPublicDisplayHide())
                            || YES.contentEquals(defendantDao.get().getPublicDisplayHide());
                    DefendantName defendantName = getDefendantName(
                        defendantDao.get().getFirstName(), defendantDao.get().getMiddleName(),
                        defendantDao.get().getSurname(), isHidden);
                    result.addDefendantName(defendantName);
                }
            }
        }
    }

    private DefendantName getDefendantName(String firstName, String middleName, String surname,
        boolean hide) {
        return new DefendantName(firstName, middleName, surname, hide);
    }

    private AllCourtStatusValue getAllCourtStatusValue() {
        return new AllCourtStatusValue();
    }
    
    private List<AllCourtStatusValue> addEmptyCourtroomsIfMissing(
        List<AllCourtStatusValue> rows, int courtId) {

        // 1) room ids already present
        Set<Integer> presentRoomIds = new LinkedHashSet<>();
        for (AllCourtStatusValue v : rows) {
            if (v.getCourtRoomId() != null) {
                presentRoomIds.add(v.getCourtRoomId());
            }
        }
    
        // 2) resolve the court_site_id(s) we should consider
        Set<Integer> siteIds = resolveSiteIdsFromRowsOrCourt(rows, courtId);
    
        // 3) fetch rooms for each site and add placeholders if missing
        for (Integer siteId : siteIds) {
            var allRooms = Optional.ofNullable(getXhbCourtRoomRepository().findByCourtSiteIdSafe(siteId))
                                   .orElse(List.of());
            for (var room : allRooms) {
                Integer roomId = room.getCourtRoomId();
                if (roomId != null && !presentRoomIds.contains(roomId)) {
                    rows.add(buildEmptyAllCourtStatusRow(room));
                }
            }
        }
        return rows;
    }
    
    /**
     * Try to identify the court_site_id(s) to use.
     * If rows already carry site *code/name*, match those against the sites for this court.
     * If there are no rows (or no codes/names), fall back to *all* sites for the court.
     */
    private Set<Integer> resolveSiteIdsFromRowsOrCourt(List<AllCourtStatusValue> rows, int courtId) {
        // load all sites for the court
        var allSites = Optional.ofNullable(getXhbCourtSiteRepository().findByCourtIdSafe(courtId))
                               .orElse(List.of());

        // map by (code,name) for quick lookup (case-insensitive, trimmed)
        record SiteKey(String code, String name) {}
        
        Map<SiteKey, Integer> siteKeyToId = new LinkedHashMap<>();
        for (var s : allSites) {
            String code = s.getCourtSiteCode() == null ? "" : s.getCourtSiteCode().trim().toUpperCase();
            String name = s.getCourtSiteName() == null ? "" : s.getCourtSiteName().trim().toUpperCase();
            siteKeyToId.put(new SiteKey(code, name), s.getCourtSiteId());
        }

        // collect site keys present in rows (if available)
        Set<Integer> resolved = new LinkedHashSet<>();
        boolean foundAnyKeys = false;
        for (AllCourtStatusValue v : rows) {
            // Adjust getters to whatever your DTO exposes:
            String code = v.getCourtSiteCode();   // may be null
            String name = v.getCourtSiteName();   // may be null
            if (code != null || name != null) {
                foundAnyKeys = true;
                String c = code == null ? "" : code.trim().toUpperCase();
                String n = name == null ? "" : name.trim().toUpperCase();
                Integer siteId = siteKeyToId.get(new SiteKey(c, n));
                if (siteId != null) {
                    resolved.add(siteId);
                }
            }
        }

        // Fallback: if we didn’t find any keys in rows, include *all* sites for the court.
        if (!foundAnyKeys) {
            for (var s : allSites) {
                resolved.add(s.getCourtSiteId());
            }
        }

        return resolved;
    }
    
    private AllCourtStatusValue buildEmptyAllCourtStatusRow(XhbCourtRoomDao room) {
        AllCourtStatusValue v = new AllCourtStatusValue();
        // carry site/room identity into the row so the renderer prints the court name
        v.setCourtRoomId(room.getCourtRoomId());
        v.setCourtRoomName(room.getCourtRoomName());
        // leave case/status fields null -> hasInformationForDisplay() returns false,
        // and the renderer will show “No information to display” for that row.
        return v;
    }

}