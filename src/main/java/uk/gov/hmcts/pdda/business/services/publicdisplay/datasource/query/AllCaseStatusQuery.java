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
import java.util.List;
import java.util.Optional;

/**
 * This class wraps the stored procedure that provides the data for the all case status document.
 * 
 * @author Rakesh Lakhani
 */
@SuppressWarnings({"PMD.ExcessiveParameterList", "PMD.CouplingBetweenObjects"})
public class AllCaseStatusQuery extends PublicDisplayQuery {

    /**
     * Constructor compiles the query (originally called XHB_PUBLIC_DISPLAY_PKG.GET_ALL_CASE_STATUS).
     */
    public AllCaseStatusQuery(EntityManager entityManager) {
        super(entityManager);
        log.debug("Query object created");
    }

    public AllCaseStatusQuery(EntityManager entityManager, XhbCaseRepository xhbCaseRepository,
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

    /**
     * Returns an array of CourtListValue.
     * 
     * @param date Id
     * @param courtId room ids for which the data is required
     * @param courtRoomIds Court room ids
     * 
     * @return Suumary by name data for the specified court rooms
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

    private List<AllCaseStatusValue> getSittingData(List<XhbSittingDao> sittingDaos, int... courtRoomIds) {
        List<AllCaseStatusValue> results = new ArrayList<>();
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

    private List<AllCaseStatusValue> getScheduleHearingData(XhbSittingDao sittingDao,
        List<XhbScheduledHearingDao> scheduledHearingDaos, String floating, int... courtRoomIds) {
        List<AllCaseStatusValue> results = new ArrayList<>();
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
                results.addAll(
                    getSchedHearingDefendantData(sittingDao, scheduledHearingDao, schedHearingDefDaos, floating));
            }
        }
        return results;
    }

    private List<AllCaseStatusValue> getSchedHearingDefendantData(XhbSittingDao sittingDao,
        XhbScheduledHearingDao scheduledHearingDao, List<XhbSchedHearingDefendantDao> schedHearingDefDaos,
        String floating) {
        List<AllCaseStatusValue> results = new ArrayList<>();
        for (XhbSchedHearingDefendantDao schedHearingDefendantDao : schedHearingDefDaos) {

            boolean isHidden = false;
            AllCaseStatusValue result = getAllCaseStatusValue();
            populateData(result, sittingDao.getCourtSiteId(), sittingDao.getCourtRoomId(),
                scheduledHearingDao.getMovedFromCourtRoomId(), scheduledHearingDao.getNotBeforeTime());
            result.setFloating(floating);
            result.setNotBeforeTime(scheduledHearingDao.getNotBeforeTime());
            result.setHearingProgress(
                scheduledHearingDao.getHearingProgress() != null ? scheduledHearingDao.getHearingProgress() : 0);
            result.setListCourtRoomId(sittingDao.getCourtRoomId());

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
                    isHidden = YES.equals(caseDao.get().getPublicDisplayHide());

                    // Populate the event
                    populateEventData(result, hearingDao.get().getCaseId());
                }

                // Get the ref hearing type
                result.setHearingDescription(getRefHearingTypeDesc(hearingDao));
            }

            // Get the defendant on case
            Optional<XhbDefendantOnCaseDao> defendantOnCaseDao =
                getXhbDefendantOnCaseRepository()
                    .findByIdSafe(schedHearingDefendantDao.getDefendantOnCaseId());
            if (defendantOnCaseDao.isPresent() && !YES.equals(defendantOnCaseDao.get().getObsInd())) {

                // Get the defendant
                Optional<XhbDefendantDao> defendantDao =
                    getXhbDefendantRepository()
                        .findByIdSafe(defendantOnCaseDao.get().getDefendantId());
                if (defendantDao.isPresent()) {
                    DefendantName defendantName = getDefendantName(defendantDao.get().getFirstName(),
                        defendantDao.get().getMiddleName(), defendantDao.get().getSurname(),
                        isDefendantHidden(defendantDao, defendantOnCaseDao, isHidden));
                    result.setDefendantName(defendantName);
                }
            }

            results.add(result);
        }
        return results;
    }

    private boolean isDefendantHidden(Optional<XhbDefendantDao> defendantDao,
        Optional<XhbDefendantOnCaseDao> defendantOnCaseDao, boolean isHidden) {
        return isHidden
            || defendantOnCaseDao.isPresent() && YES.equals(defendantOnCaseDao.get().getPublicDisplayHide())
            || defendantDao.isPresent() && YES.contentEquals(defendantDao.get().getPublicDisplayHide());
    }

    private String getRefHearingTypeDesc(Optional<XhbHearingDao> hearingDao) {
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

    protected boolean isFloatingIncluded() {
        // Only show isFloating = '0'
        return false;
    }

    private AllCaseStatusValue getAllCaseStatusValue() {
        return new AllCaseStatusValue();
    }

    private DefendantName getDefendantName(String firstName, String middleName, String surname, boolean hide) {
        return new DefendantName(firstName, middleName, surname, hide);
    }
}
