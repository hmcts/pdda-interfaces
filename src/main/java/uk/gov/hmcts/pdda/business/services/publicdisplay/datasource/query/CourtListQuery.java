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
import uk.gov.hmcts.pdda.business.entities.xhbrefhearingtype.XhbRefHearingTypeDao;
import uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant.XhbSchedHearingDefendantDao;
import uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant.XhbSchedHearingDefendantRepository;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingDao;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingRepository;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.CourtListValue;
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
public class CourtListQuery extends PublicDisplayQuery {

    /**
     * Constructor compiles the query (originally called XHB_PUBLIC_DISPLAY_PKG.GET_COURT_LIST).
     */
    public CourtListQuery(EntityManager entityManager) {
        super(entityManager);
        log.debug("Entering CourtListQuery(EntityManager)");
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
        log.debug("Entering CourtListQuery(EntityManager, ...repositories)");
    }

    /**
     * Returns an array of CourtListValue.

     * @param date LocalDateTime
     * @param courtId room ids for which the data is required
     * @param courtRoomIds Court room ids

     * @return Summary by name data for the specified court rooms
     */
    @Override
    public Collection<?> getData(LocalDateTime date, int courtId, int... courtRoomIds) {
        log.debug("Entering getData(LocalDateTime, int, int...)");

        LocalDateTime startDate = DateTimeUtilities.stripTime(date);

        List<CourtListValue> results = new ArrayList<>();

        // Loop the hearing lists
        List<XhbHearingListDao> hearingListDaos = getHearingListDaos(courtId, startDate);
        if (hearingListDaos.isEmpty()) {
            log.debug("CourtListQuery - No Hearing Lists found for today");
        } else {
            results.addAll(getHearingData(hearingListDaos, courtRoomIds));
        }

        return results;
    }

    private List<CourtListValue> getHearingData(List<XhbHearingListDao> hearingListDaos, int... courtRoomIds) {
        log.debug("Entering getHearingData(List<XhbHearingListDao>, int...)");
        List<CourtListValue> results = new ArrayList<>();
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

    private List<CourtListValue> getScheduledHearingData(XhbSittingDao sittingDao,
        List<XhbScheduledHearingDao> scheduledHearingDaos, int... courtRoomIds) {
        log.debug("Entering getScheduledHearingData(XhbSittingDao, List<XhbScheduledHearingDao>, int...)");
        List<CourtListValue> results = new ArrayList<>();
        for (XhbScheduledHearingDao scheduledHearingDao : scheduledHearingDaos) {

            // Check if this courtroom has been selected
            if (!isSelectedCourtRoom(courtRoomIds, sittingDao.getCourtRoomId(),
                scheduledHearingDao.getMovedFromCourtRoomId())) {
                continue;
            }

            CourtListValue result =
                getCourtListValue(sittingDao, scheduledHearingDao);
            results.add(result);

        }
        return results;
    }

    private DefendantName getDefendantName(String firstName, String middleName, String surname, boolean hide) {
        log.debug("Entering getDefendantName(String, String, String, boolean)");
        return new DefendantName(firstName, middleName, surname, hide);
    }


    /**
     * Populates a CourtListValue from the DAOs.
     * @param sittingDao The sitting DAO
     * @param scheduledHearingDao The scheduled hearing DAO
     * @return Populated CourtListValue
     */
    @SuppressWarnings("PMD.CognitiveComplexity")
    private CourtListValue getCourtListValue(XhbSittingDao sittingDao,
        XhbScheduledHearingDao scheduledHearingDao) {

        CourtListValue result = new CourtListValue();
        boolean isHidden = false;

        populateData(result, sittingDao.getCourtSiteId(), sittingDao.getCourtRoomId(),
            scheduledHearingDao.getMovedFromCourtRoomId(), scheduledHearingDao.getNotBeforeTime());

        // Normalise possible null Optional from mocked repository
        Optional<XhbHearingDao> hearingDao =
            Optional.ofNullable(getXhbHearingDao(scheduledHearingDao.getHearingId()))
                    .orElse(Optional.empty());

        if (hearingDao.isPresent()) {
            result.setReportingRestricted(isReportingRestricted(hearingDao.get().getCaseId()));

            Optional<XhbCaseDao> caseDao = getXhbCaseRepository().findByIdSafe(hearingDao.get().getCaseId());
            if (caseDao.isPresent()) {
                isHidden = YES.equals(caseDao.get().getPublicDisplayHide());
                result.setCaseNumber(caseDao.get().getCaseType() + caseDao.get().getCaseNumber());
            }
        }

        // Get the ref hearing type (pass a non-null Optional)
        result.setHearingDescription(getRefHearingTypeDesc(hearingDao));

        // Loop the schedHearingDefendants
        List<XhbSchedHearingDefendantDao> schedHearingDefDaos =
            getSchedHearingDefendantDaos(scheduledHearingDao.getScheduledHearingId());

        populateResultWithDefendants(result, scheduledHearingDao, schedHearingDefDaos, isHidden);

        return result;
    }

    /**
     * Reducing the complexity of getCourtListValue by moving defendant population to its own method.
     * @param result The CourtListValue being populated
     * @param scheduledHearingDao The scheduled hearing DAO
     * @param schedHearingDefDaos The scheduled hearing defendant DAOs
     * @param isHidden True if the case is hidden
     * @return Populated CourtListValue
     */
    private CourtListValue populateResultWithDefendants(CourtListValue result,
        XhbScheduledHearingDao scheduledHearingDao,
        List<XhbSchedHearingDefendantDao> schedHearingDefDaos, boolean isHidden) {

        if (!schedHearingDefDaos.isEmpty()) {
            for (XhbSchedHearingDefendantDao schedHearingDefendantDao : schedHearingDefDaos) {

                // Get the defendant on case
                Optional<XhbDefendantOnCaseDao> defendantOnCaseDao =
                    getXhbDefendantOnCaseRepository()
                        .findByIdSafe(schedHearingDefendantDao.getDefendantOnCaseId());
                if (defendantOnCaseDao.isPresent() && !YES.equals(defendantOnCaseDao.get().getObsInd())) {

                    // Populate the single defendant and accumulate hidden state
                    result = populateResultWithDefendant(result, defendantOnCaseDao, isHidden);

                    // Ensure hearing progress is set (null-safe)
                    result.setHearingProgress(
                        scheduledHearingDao.getHearingProgress() != null
                            ? scheduledHearingDao.getHearingProgress()
                            : 0);
                }
            }
        }

        return result;
    }

    /**
     * Populates a CourtListValue with a defendant.
     * @param result The CourtListValue being populated
     * @param defendantOnCaseDao The defendant on case DAO
     * @param isHidden True if the case is hidden
     * @return Populated CourtListValue
     */
    private CourtListValue populateResultWithDefendant(CourtListValue result,
        Optional<XhbDefendantOnCaseDao> defendantOnCaseDao, boolean isHidden) {

        // Get the defendant
        if (!defendantOnCaseDao.isPresent()) {
            return result;
        }
        Optional<XhbDefendantDao> defendantDao =
            getXhbDefendantRepository().findByIdSafe(defendantOnCaseDao.get().getDefendantId());
        if (defendantDao.isPresent()) {
            boolean tmpIsHidden = isHidden
                || YES.equals(defendantOnCaseDao.get().getPublicDisplayHide())
                || YES.contentEquals(defendantDao.get().getPublicDisplayHide());

            DefendantName defendantName = getDefendantName(defendantDao.get().getFirstName(),
                defendantDao.get().getMiddleName(), defendantDao.get().getSurname(), tmpIsHidden);

            result.setReportingRestricted(tmpIsHidden);
            result.addDefendantName(defendantName);
        }
        return result;
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
}
