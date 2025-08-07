package uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.query;

import jakarta.persistence.EntityManager;
import uk.gov.hmcts.framework.util.DateTimeUtilities;
import uk.gov.hmcts.pdda.business.entities.xhbcase.XhbCaseDao;
import uk.gov.hmcts.pdda.business.entities.xhbcase.XhbCaseRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcasereference.XhbCaseReferenceRepository;
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
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.DefendantName;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.SummaryByNameValue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * This class wraps the stored procedure that provides the data for the court list document.
 * 
 * @author pznwc5
 */
@SuppressWarnings({"PMD.ExcessiveParameterList", "PMD.CouplingBetweenObjects"})
public class CourtListQuery extends PublicDisplayQuery {

    /**
     * Constructor compiles the query (originally called XHB_PUBLIC_DISPLAY_PKG.GET_COURT_LIST)
     */
    public CourtListQuery(EntityManager entityManager) {
        super(entityManager);
        log.debug("Query object created");
    }

    public CourtListQuery(EntityManager entityManager, XhbCaseRepository xhbCaseRepository,
        XhbCaseReferenceRepository xhbCaseReferenceRepository, XhbHearingListRepository xhbHearingListRepository,
        XhbSittingRepository xhbSittingRepository, XhbScheduledHearingRepository xhbScheduledHearingRepository,
        XhbCourtSiteRepository xhbCourtSiteRepository, XhbCourtRoomRepository xhbCourtRoomRepository,
        XhbSchedHearingDefendantRepository xhbSchedHearingDefendantRepository,
        XhbHearingRepository xhbHearingRepository, XhbDefendantOnCaseRepository xhbDefendantOnCaseRepository,
        XhbDefendantRepository xhbDefendantRepository) {
        super(entityManager, xhbCaseRepository, xhbCaseReferenceRepository, xhbHearingListRepository,
            xhbSittingRepository, xhbScheduledHearingRepository, xhbCourtSiteRepository, xhbCourtRoomRepository,
            xhbSchedHearingDefendantRepository, xhbHearingRepository, xhbDefendantOnCaseRepository,
            xhbDefendantRepository, null, null, null);
    }

    /**
     * Returns an array of CourtListValue.
     * 
     * @param date LocalDateTime
     * @param courtId room ids for which the data is required
     * @param courtRoomIds Court room ids
     * 
     * @return Summary by name data for the specified court rooms
     */
    @Override
    public Collection<?> getData(LocalDateTime date, int courtId, int... courtRoomIds) {

        LocalDateTime startDate = DateTimeUtilities.stripTime(date);

        List<SummaryByNameValue> results = new ArrayList<>();

        // Loop the hearing lists
        List<XhbHearingListDao> hearingListDaos = getHearingListDaos(courtId, startDate);
        if (hearingListDaos.isEmpty()) {
            log.debug("CourtListQuery - No Hearing Lists found for today");
        } else {
            results.addAll(getHearingData(hearingListDaos, courtRoomIds));
        }

        return results;
    }

    private List<SummaryByNameValue> getHearingData(List<XhbHearingListDao> hearingListDaos, int... courtRoomIds) {
        List<SummaryByNameValue> results = new ArrayList<>();
        for (XhbHearingListDao hearingListDao : hearingListDaos) {
            // Loop the sittings
            List<XhbSittingDao> sittingDaos = getNonFloatingSittingListDaos(hearingListDao.getListId());
            if (!sittingDaos.isEmpty()) {
                for (XhbSittingDao sittingDao : sittingDaos) {

                    // Loop the scheduledHearings
                    List<XhbScheduledHearingDao> scheduledHearingDaos =
                        getScheduledHearingDaos(sittingDao.getSittingId());
                    if (!scheduledHearingDaos.isEmpty()) {
                        results.addAll(getScheduledHearingData(sittingDao, scheduledHearingDaos, courtRoomIds));
                    }
                }
            }
        }
        return results;
    }

    private List<SummaryByNameValue> getScheduledHearingData(XhbSittingDao sittingDao,
        List<XhbScheduledHearingDao> scheduledHearingDaos, int... courtRoomIds) {
        List<SummaryByNameValue> results = new ArrayList<>();
        for (XhbScheduledHearingDao scheduledHearingDao : scheduledHearingDaos) {

            // Check if this courtroom has been selected
            if (!isSelectedCourtRoom(courtRoomIds, sittingDao.getCourtRoomId(),
                scheduledHearingDao.getMovedFromCourtRoomId())) {
                continue;
            }

            // Loop the schedHearingDefendants
            List<XhbSchedHearingDefendantDao> schedHearingDefDaos =
                getSchedHearingDefendantDaos(scheduledHearingDao.getScheduledHearingId());
            if (!schedHearingDefDaos.isEmpty()) {
                for (XhbSchedHearingDefendantDao schedHearingDefendantDao : schedHearingDefDaos) {

                    SummaryByNameValue result =
                        getSummaryByNameValue(sittingDao, scheduledHearingDao, schedHearingDefendantDao);
                    results.add(result);
                }
            }
        }
        return results;
    }

    private DefendantName getDefendantName(String firstName, String middleName, String surname, boolean hide) {
        return new DefendantName(firstName, middleName, surname, hide);
    }

    private SummaryByNameValue getSummaryByNameValue(XhbSittingDao sittingDao,
        XhbScheduledHearingDao scheduledHearingDao, XhbSchedHearingDefendantDao schedHearingDefendantDao) {
        SummaryByNameValue result = new SummaryByNameValue();
        boolean isHidden = false;
        populateData(result, sittingDao.getCourtSiteId(), sittingDao.getCourtRoomId(),
            scheduledHearingDao.getMovedFromCourtRoomId(), scheduledHearingDao.getNotBeforeTime());
        result.setFloating(NOT_FLOATING);

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
