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
import uk.gov.hmcts.pdda.business.entities.xhbrefhearingtype.XhbRefHearingTypeRepository;
import uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant.XhbSchedHearingDefendantDao;
import uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant.XhbSchedHearingDefendantRepository;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingDao;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingRepository;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.DefendantName;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.SummaryByNameValue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This class wraps the stored procedure that provides the data for the summary by name document.
 * Even though the instance is not thread safe, you can still cache this in a session bean as the
 * container serializes invocations on a given session bean instance. In short, stateless session
 * bean instances are pooled and wouldn't allow concurrent invocations.

 * @author pznwc5
 */
@SuppressWarnings({"PMD"})
public class SummaryByNameQuery extends PublicDisplayQuery {

    /**
     * Constructor compiles the query (originally called XHB_PUBLIC_DISPLAY_PKG.GET_SUMMARY_BY_NAME).
     */
    public SummaryByNameQuery(EntityManager entityManager) {
        super(entityManager);
        log.debug("Query object created");
    }

    public SummaryByNameQuery(EntityManager entityManager, XhbCaseRepository xhbCaseRepository,
        XhbCaseReferenceRepository xhbCaseReferenceRepository, XhbHearingListRepository xhbHearingListRepository,
        XhbSittingRepository xhbSittingRepository, XhbScheduledHearingRepository xhbScheduledHearingRepository,
        XhbCourtSiteRepository xhbCourtSiteRepository, XhbCourtRoomRepository xhbCourtRoomRepository,
        XhbSchedHearingDefendantRepository xhbSchedHearingDefendantRepository,
        XhbHearingRepository xhbHearingRepository, XhbDefendantOnCaseRepository xhbDefendantOnCaseRepository,
        XhbDefendantRepository xhbDefendantRepository, XhbCourtLogEntryRepository xhbCourtLogEntryRepository,
        XhbRefHearingTypeRepository xhbRefHearingTypeRepository) {
        super(entityManager, xhbCaseRepository, xhbCaseReferenceRepository, xhbHearingListRepository,
            xhbSittingRepository, xhbScheduledHearingRepository, xhbCourtSiteRepository, xhbCourtRoomRepository,
            xhbSchedHearingDefendantRepository, xhbHearingRepository, xhbDefendantOnCaseRepository,
            xhbDefendantRepository, xhbCourtLogEntryRepository, xhbRefHearingTypeRepository, null);
    }

    @Override
    public Collection<?> getData(LocalDateTime date, int courtId, int... courtRoomIds) {

        LocalDateTime startDate = DateTimeUtilities.stripTime(date);

        List<SummaryByNameValue> results = new ArrayList<>();

        // Loop the hearing lists
        List<XhbHearingListDao> hearingListDaos = getHearingListDaos(courtId, startDate);
        if (hearingListDaos.isEmpty()) {
            log.debug("SummaryByNameQuery - No Hearing Lists found for today");
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

    protected boolean isFloatingIncluded() {
        // Only show isFloating = '0'
        return false;
    }

    private List<SummaryByNameValue> getSittingData(List<XhbSittingDao> sittingDaos, int... courtRoomIds) {
        List<SummaryByNameValue> results = new ArrayList<>();
        for (XhbSittingDao sittingDao : sittingDaos) {

            // Is this a floating sitting
            String floating = getIsFloating(sittingDao.getIsFloating());

            // Loop the scheduledHearings
            List<XhbScheduledHearingDao> scheduledHearingDaos = getScheduledHearingDaos(sittingDao.getSittingId());
            if (!scheduledHearingDaos.isEmpty()) {
                results.addAll(getScheduleHearingData(sittingDao, scheduledHearingDaos, floating, courtRoomIds));
            }
        }
        return results;
    }

    private List<SummaryByNameValue> getScheduleHearingData(
        XhbSittingDao sittingDao,
        List<XhbScheduledHearingDao> scheduledHearingDaos,
        String floating,
        int... courtRoomIds) {

        List<SummaryByNameValue> results = new ArrayList<>();

        // Deduplicate: one scheduled hearing per hearingId, prefer non-null hearingProgress
        Map<Integer, XhbScheduledHearingDao> bestByHearing = new LinkedHashMap<>();
        for (XhbScheduledHearingDao sh : scheduledHearingDaos) {

            // respect courtroom selection (unless floating means include all)
            if (!isSelectedCourtRoom(courtRoomIds, sittingDao.getCourtRoomId(), sh.getMovedFromCourtRoomId())) {
                continue;
            }

            XhbScheduledHearingDao current = bestByHearing.get(sh.getHearingId());
            if (current == null) {
                bestByHearing.put(sh.getHearingId(), sh);
            } else {
                boolean currHasProg = current.getHearingProgress() != null;
                boolean candHasProg = sh.getHearingProgress() != null;

                if (!currHasProg && candHasProg) {
                    bestByHearing.put(sh.getHearingId(), sh); // prefer the one with progress
                }
                // Optional tie-breaker if both equal on progress:
                // else if (sh.getCreationDate() != null && (current.getCreationDate() == null
                //        || sh.getCreationDate().isBefore(current.getCreationDate()))) {
                //     bestByHearing.put(sh.getHearingId(), sh);
                // }
            }
        }

        // Now emit defendants only for the chosen scheduled hearing per hearingId
        for (XhbScheduledHearingDao sh : bestByHearing.values()) {
            List<XhbSchedHearingDefendantDao> shdList =
                getSchedHearingDefendantDaos(sh.getScheduledHearingId());
            if (!shdList.isEmpty()) {
                for (XhbSchedHearingDefendantDao shd : shdList) {
                    SummaryByNameValue row =
                        getSummaryByNameValue(sittingDao, sh, shd, floating);
                    results.add(row);
                }
            }
        }

        return results;
    }


    private DefendantName getDefendantName(String firstName, String middleName, String surname, boolean hide) {
        return new DefendantName(firstName, middleName, surname, hide);
    }

    private SummaryByNameValue getSummaryByNameValue(XhbSittingDao sittingDao,
        XhbScheduledHearingDao scheduledHearingDao, XhbSchedHearingDefendantDao schedHearingDefendantDao,
        String floating) {
        SummaryByNameValue result = new SummaryByNameValue();
        boolean isHidden = false;
        populateData(result, sittingDao.getCourtSiteId(), sittingDao.getCourtRoomId(),
            scheduledHearingDao.getMovedFromCourtRoomId(), scheduledHearingDao.getNotBeforeTime());
        result.setFloating(floating);

        // Get the hearing
        Optional<XhbHearingDao> hearingDao = getXhbHearingDao(scheduledHearingDao.getHearingId());
        if (hearingDao.isPresent()) {
            result.setReportingRestricted(isReportingRestricted(hearingDao.get().getCaseId()));

            // Get the case
            Optional<XhbCaseDao> caseDao =
                getXhbCaseRepository().findByIdSafe(hearingDao.get().getCaseId());
            if (caseDao.isPresent()) {
                isHidden = YES.equals(caseDao.get().getPublicDisplayHide());
            }
        }

        // Get the defendant on case
        Optional<XhbDefendantOnCaseDao> defendantOnCaseDao =
            getXhbDefendantOnCaseRepository()
                .findByIdSafe(schedHearingDefendantDao.getDefendantOnCaseId());
        if (defendantOnCaseDao.isPresent() && !YES.equals(defendantOnCaseDao.get().getObsInd())) {

            // Get the defendant
            Optional<XhbDefendantDao> defendantDao =
                getXhbDefendantRepository().findByIdSafe(defendantOnCaseDao.get().getDefendantId());
            if (defendantDao.isPresent()) {
                isHidden = isHidden || YES.equals(defendantOnCaseDao.get().getPublicDisplayHide())
                    || YES.contentEquals(defendantDao.get().getPublicDisplayHide());
                DefendantName defendantName = getDefendantName(defendantDao.get().getFirstName(),
                    defendantDao.get().getMiddleName(), defendantDao.get().getSurname(), isHidden);
                result.setDefendantName(defendantName);
            }
        }
        return result;
    }
}
