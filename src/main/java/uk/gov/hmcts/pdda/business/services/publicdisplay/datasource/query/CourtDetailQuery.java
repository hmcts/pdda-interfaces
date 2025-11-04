package uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.query;

import jakarta.persistence.EntityManager;
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
import uk.gov.hmcts.pdda.business.entities.xhbrefjudge.XhbRefJudgeDao;
import uk.gov.hmcts.pdda.business.entities.xhbrefjudge.XhbRefJudgeRepository;
import uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant.XhbSchedHearingDefendantDao;
import uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant.XhbSchedHearingDefendantRepository;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingDao;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingRepository;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.CourtDetailValue;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.DefendantName;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.JudgeName;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.PublicNoticeValue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This class wraps the stored procedure that provides the data for the court detail document.

 * @author
 */
@SuppressWarnings({"PMD"})
public class CourtDetailQuery extends PublicDisplayQuery {

    private PublicNoticeQuery mockPublicNoticeQuery;

    /**
     * Registers the parameter types and SQL (originally called
     * XHB_PUBLIC_DISPLAY_PKG.GET_COURT_DETAIL).

     */
    public CourtDetailQuery(EntityManager entityManager) {
        super(entityManager);
    }

    public CourtDetailQuery(EntityManager entityManager, XhbCaseRepository xhbCaseRepository,
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
        XhbRefJudgeRepository xhbRefJudgeRepository, PublicNoticeQuery mockPublicNoticeQuery) {
        super(entityManager, xhbCaseRepository, xhbCaseReferenceRepository,
            xhbHearingListRepository, xhbSittingRepository, xhbScheduledHearingRepository,
            xhbCourtSiteRepository, xhbCourtRoomRepository, xhbSchedHearingDefendantRepository,
            xhbHearingRepository, xhbDefendantOnCaseRepository, xhbDefendantRepository,
            xhbCourtLogEntryRepository, xhbRefHearingTypeRepository, xhbRefJudgeRepository);
        this.mockPublicNoticeQuery = mockPublicNoticeQuery;
    }

    /**
     * Common interface.
     */
    @Override
    public Collection<CourtDetailValue> getData(LocalDateTime date, int courtId,
        int... courtRoomIds) {

        LocalDateTime startDate = DateTimeUtilities.stripTime(date);

        List<CourtDetailValue> results = new ArrayList<>();

        // Loop the hearing lists
        List<XhbHearingListDao> hearingListDaos = getHearingListDaos(courtId, startDate);
        if (hearingListDaos.isEmpty()) {
            log.debug("CourtDetailQuery - No Hearing Lists found for today");
        } else {
            results.addAll(getHearingData(hearingListDaos, courtRoomIds));
        }

        return results;
    }

    private List<CourtDetailValue> getHearingData(List<XhbHearingListDao> hearingListDaos,
        int... courtRoomIds) {
        List<CourtDetailValue> results = new ArrayList<>();
        for (XhbHearingListDao hearingListDao : hearingListDaos) {
            // Loop the sittings (floating and non-floating)
            List<XhbSittingDao> sittingDaos = getSittingListDaos(hearingListDao.getListId());
            if (!sittingDaos.isEmpty()) {
                results.addAll(getSittingData(sittingDaos, courtRoomIds));
            }
        }
        return results;
    }

    private List<CourtDetailValue> getSittingData(List<XhbSittingDao> sittingDaos, int... courtRoomIds) {
        List<CourtDetailValue> results = new ArrayList<>();

        for (XhbSittingDao sittingDao : sittingDaos) {
            // Fetch all scheduled hearings for this sitting
            List<XhbScheduledHearingDao> shDaos = getScheduledHearingDaos(sittingDao.getSittingId());
            if (shDaos.isEmpty()) {
                continue;
            }

            // Deduplicate: one scheduled hearing per hearingId,
            // keeping the one with non-null hearingProgress.
            Map<Integer, XhbScheduledHearingDao> bestByHearing = new LinkedHashMap<>();

            for (XhbScheduledHearingDao sh : shDaos) {
                // Keep existing filters (room selection and active flag)
                if (!isSelectedCourtRoom(courtRoomIds, sittingDao.getCourtRoomId(), sh.getMovedFromCourtRoomId())
                        || !YES.equals(sh.getIsCaseActive())) {
                    continue;
                }

                XhbScheduledHearingDao current = bestByHearing.get(sh.getHearingId());
                if (current == null) {
                    bestByHearing.put(sh.getHearingId(), sh);
                } else {
                    boolean currHasProgress = current.getHearingProgress() != null;
                    boolean candHasProgress = sh.getHearingProgress() != null;
                    if (!currHasProgress && candHasProgress) {
                        bestByHearing.put(sh.getHearingId(), sh); // prefer the one with progress
                    }
                }
            }

            // Now emit exactly one row per hearing
            for (XhbScheduledHearingDao chosen : bestByHearing.values()) {
                CourtDetailValue row = getCourtDetailValue(sittingDao, chosen);
                results.add(row);
            }
        }

        return results;
    }


    private boolean isDefendantHidden(Optional<XhbDefendantDao> defendantDao,
        Optional<XhbDefendantOnCaseDao> defendantOnCaseDao, boolean isCaseHidden) {
        return isCaseHidden
            || defendantOnCaseDao.isPresent()
                && YES.equals(defendantOnCaseDao.get().getPublicDisplayHide())
            || defendantDao.isPresent()
                && YES.contentEquals(defendantDao.get().getPublicDisplayHide());
    }

    private void populateJudgeData(CourtDetailValue result,
        XhbScheduledHearingDao scheduledHearingDao) {
        Optional<XhbRefJudgeDao> refJudgeDao =
            getXhbRefJudgeDao(scheduledHearingDao.getScheduledHearingId());
        if (refJudgeDao.isPresent()) {
            JudgeName judgeName = new JudgeName(refJudgeDao.get().getFullListTitle1(),
                refJudgeDao.get().getSurname());
            result.setJudgeName(judgeName);
        }
    }

    private String getHearingTypeDesc(Optional<XhbHearingDao> hearingDao) {
        if (hearingDao.isPresent()) {
            Optional<XhbRefHearingTypeDao> refHearingTypeDao =
                getXhbRefHearingTypeRepository()
                    .findByIdSafe(hearingDao.get().getRefHearingTypeId());
            if (refHearingTypeDao.isPresent()) {
                return refHearingTypeDao.get().getHearingTypeDesc();
            }
        }
        return null;
    }

    private PublicNoticeQuery getPublicNoticeQuery() {
        if (mockPublicNoticeQuery == null) {
            return new PublicNoticeQuery(getEntityManager());
        }
        return mockPublicNoticeQuery;
    }

    private CourtDetailValue getCourtDetailValue(XhbSittingDao sittingDao,
        XhbScheduledHearingDao scheduledHearingDao) {
        CourtDetailValue result = new CourtDetailValue();
        boolean isCaseHidden = false;
        populateData(result, sittingDao.getCourtSiteId(), sittingDao.getCourtRoomId(),
            scheduledHearingDao.getMovedFromCourtRoomId(), scheduledHearingDao.getNotBeforeTime());

        // Get the hearing
        Optional<XhbHearingDao> hearingDao = getXhbHearingDao(scheduledHearingDao.getHearingId());
        if (hearingDao.isPresent()) {
            result.setReportingRestricted(isReportingRestricted(hearingDao.get().getCaseId()));

            // Get the case
            Optional<XhbCaseDao> caseDao =
                getXhbCaseRepository().findByIdSafe(hearingDao.get().getCaseId());
            if (caseDao.isPresent()) {
                result.setCaseNumber(caseDao.get().getCaseType() + caseDao.get().getCaseNumber());
                result.setCaseTitle(caseDao.get().getCaseTitle());
                isCaseHidden = YES.equals(caseDao.get().getPublicDisplayHide());

                // Populate the event
                populateEventData(result, hearingDao.get().getCaseId());
            }

            // Get the ref hearing type
            result.setHearingDescription(getHearingTypeDesc(hearingDao));
        }

        // Loop the schedHearingDefendants
        List<XhbSchedHearingDefendantDao> schedHearingDefDaos =
            getSchedHearingDefendantDaos(scheduledHearingDao.getScheduledHearingId());
        if (!schedHearingDefDaos.isEmpty()) {
            for (XhbSchedHearingDefendantDao schedHearingDefendantDao : schedHearingDefDaos) {

                // Get the defendant on case
                Optional<XhbDefendantOnCaseDao> defendantOnCaseDao =
                    getXhbDefendantOnCaseRepository()
                        .findByIdSafe(schedHearingDefendantDao.getDefendantOnCaseId());
                if (defendantOnCaseDao.isPresent()
                    && !YES.equals(defendantOnCaseDao.get().getObsInd())) {

                    // Get the defendant
                    Optional<XhbDefendantDao> defendantDao = getXhbDefendantRepository()
                        .findByIdSafe(defendantOnCaseDao.get().getDefendantId());
                    if (defendantDao.isPresent()) {
                        DefendantName defendantName =
                            getDefendantName(defendantDao.get().getFirstName(),
                                defendantDao.get().getMiddleName(), defendantDao.get().getSurname(),
                                isDefendantHidden(defendantDao, defendantOnCaseDao, isCaseHidden));
                        result.addDefendantName(defendantName);
                    }
                }
            }
        }

        // Populate the judge
        populateJudgeData(result, scheduledHearingDao);

        // Populate the public notices for the court room
        PublicNoticeQuery pnQuery = getPublicNoticeQuery();
        PublicNoticeValue[] publicNotices = pnQuery.execute(result.getCourtRoomId());
        result.setPublicNotices(publicNotices);
        return result;
    }

    private DefendantName getDefendantName(String firstName, String middleName, String surname,
        boolean hide) {
        return new DefendantName(firstName, middleName, surname, hide);
    }
}
