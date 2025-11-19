package uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.query;

import uk.gov.hmcts.pdda.business.entities.xhbdefendant.XhbDefendantDao;
import uk.gov.hmcts.pdda.business.entities.xhbdefendantoncase.XhbDefendantOnCaseDao;
import uk.gov.hmcts.pdda.business.entities.xhbhearing.XhbHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbrefhearingtype.XhbRefHearingTypeDao;
import uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant.XhbSchedHearingDefendantDao;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingDao;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.DefendantName;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Shared helpers extracted to remove duplication between queries.
 */
@SuppressWarnings("PMD")
public final class PublicDisplayQueryHelpers {

    private PublicDisplayQueryHelpers() {
        /* prevent instantiation */
    }

    /**
     * Deduplicate scheduled hearings by hearingId preferring one with non-null hearingProgress.
     * Keeps insertion order (LinkedHashMap).
     */
    public static Map<Integer, XhbScheduledHearingDao>
            pickBestByHearing(List<XhbScheduledHearingDao> scheduledHearingDaos) {
        Map<Integer, XhbScheduledHearingDao> bestByHearing = new LinkedHashMap<>();
        for (XhbScheduledHearingDao sh : scheduledHearingDaos) {
            XhbScheduledHearingDao current = bestByHearing.get(sh.getHearingId());
            if (current == null) {
                bestByHearing.put(sh.getHearingId(), sh);
            } else {
                boolean currentHasProgress = current.getHearingProgress() != null;
                boolean candidateHasProgress = sh.getHearingProgress() != null;
                if (!currentHasProgress && candidateHasProgress) {
                    bestByHearing.put(sh.getHearingId(), sh);
                }
                // otherwise keep existing
            }
        }
        return bestByHearing;
    }

    /**
     * Returns chosen XhbSchedHearingDefendantDao based on preference for non-OBS (obsInd != 'Y').
     * If none non-OBS found, returns first element. Returns null for empty list.
     */
    public static XhbSchedHearingDefendantDao chooseOneDefendant(List<XhbSchedHearingDefendantDao> shdList,
            java.util.function.Function<Integer, Optional<XhbDefendantOnCaseDao>> findDefOnCase) {
        
        if (shdList == null || shdList.isEmpty()) {
            return null;
        }
        XhbSchedHearingDefendantDao chosen = null;
        for (XhbSchedHearingDefendantDao shd : shdList) {
            Optional<XhbDefendantOnCaseDao> doc = findDefOnCase.apply(shd.getDefendantOnCaseId());
            if (doc.isPresent() && !"Y".equals(doc.get().getObsInd())) {
                return shd; // prefer non-OBS
            }
            if (chosen == null) {
                chosen = shd; // fallback first
            }
        }
        return chosen;
    }

    /**
     * Build DefendantName (safe fallback).
     * findDefById and findDefOnCase should be repository findByIdSafe functions passed in as lambdas.
     */
    public static DefendantName buildDefendantNameForChosen(
            XhbSchedHearingDefendantDao chosen,
            boolean isHidden,
            java.util.function.Function<Integer, Optional<XhbDefendantOnCaseDao>> findDefOnCase,
            java.util.function.Function<Integer, Optional<XhbDefendantDao>> findDef) {

        if (chosen == null) {
            return new DefendantName("", "", "", true);
        }

        Optional<XhbDefendantOnCaseDao> doc = findDefOnCase.apply(chosen.getDefendantOnCaseId());
        if (doc.isPresent() && !"Y".equals(doc.get().getObsInd())) {
            Optional<XhbDefendantDao> def = findDef.apply(doc.get().getDefendantId());
            if (def.isPresent()) {
                boolean hide = isHidden
                        || (doc.isPresent() && "Y".equals(doc.get().getPublicDisplayHide()))
                        || (def.isPresent() && "Y".contentEquals(def.get().getPublicDisplayHide()));
                return new DefendantName(def.get().getFirstName(), def.get().getMiddleName(),
                        def.get().getSurname(), hide);
            }
        }
        return new DefendantName("", "", "", true);
    }

    /**
     * Resolve hearing type description safely.
     */
    public static String resolveHearingTypeDesc(Optional<XhbHearingDao> hearingDao,
            java.util.function.Function<Integer, Optional<XhbRefHearingTypeDao>> findRefHearingType) {
        if (hearingDao.isPresent()) {
            Optional<XhbRefHearingTypeDao> ref = findRefHearingType.apply(hearingDao.get().getRefHearingTypeId());
            if (ref.isPresent()) {
                return ref.get().getHearingTypeDesc();
            }
        }
        return null;
    }
}
