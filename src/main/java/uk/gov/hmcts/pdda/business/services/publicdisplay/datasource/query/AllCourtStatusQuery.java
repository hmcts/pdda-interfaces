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
import java.util.List;
import java.util.Optional;

/**
 * This class wraps the stored procedure that provides the data for the court list document.

 * @author pznwc5
 */
@SuppressWarnings({"PMD.ExcessiveParameterList", "PMD.CouplingBetweenObjects"})
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

     * @return Suumary by name data for the specified court rooms
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

    private List<AllCourtStatusValue> getScheduleData(XhbSittingDao sittingDao,
        List<XhbScheduledHearingDao> scheduledHearingDaos, int... courtRoomIds) {
        List<AllCourtStatusValue> results = new ArrayList<>();
        for (XhbScheduledHearingDao scheduledHearingDao : scheduledHearingDaos) {

            // Check if this courtroom has been selected
            // ... and is active
            if (!isSelectedCourtRoom(courtRoomIds, sittingDao.getCourtRoomId(),
                scheduledHearingDao.getMovedFromCourtRoomId())
                || !YES.equals(scheduledHearingDao.getIsCaseActive())) {
                continue;
            }

            boolean isCaseHidden = false;
            AllCourtStatusValue result = getAllCourtStatusValue();
            populateData(result, sittingDao.getCourtSiteId(), sittingDao.getCourtRoomId(),
                scheduledHearingDao.getMovedFromCourtRoomId(),
                scheduledHearingDao.getNotBeforeTime());

            // Get the hearing
            Optional<XhbHearingDao> hearingDao =
                getXhbHearingDao(scheduledHearingDao.getHearingId());
            if (hearingDao.isPresent()) {
                result.setReportingRestricted(isReportingRestricted(hearingDao.get().getCaseId()));

                // Get the case
                Optional<XhbCaseDao> caseDao =
                    getXhbCaseRepository().findByIdSafe(hearingDao.get().getCaseId());
                if (caseDao.isPresent()) {
                    result
                        .setCaseNumber(caseDao.get().getCaseType() + caseDao.get().getCaseNumber());
                    result.setCaseTitle(caseDao.get().getCaseTitle());
                    isCaseHidden = YES.equals(caseDao.get().getPublicDisplayHide());

                    // Populate the event
                    populateEventData(result, hearingDao.get().getCaseId());
                }
            }

            // Loop the schedHearingDefendants
            List<XhbSchedHearingDefendantDao> schedDaos =
                getSchedHearingDefendantDaos(scheduledHearingDao.getScheduledHearingId());
            if (!schedDaos.isEmpty()) {
                populateScheduleDefendantData(result, schedDaos, isCaseHidden);
            }

            results.add(result);
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
}
