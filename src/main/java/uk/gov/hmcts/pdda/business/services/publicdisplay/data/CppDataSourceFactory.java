package uk.gov.hmcts.pdda.business.services.publicdisplay.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.services.publicdisplay.data.impl.GenericPublicDisplayDataSource;
import uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.cpptoxhibit.AbstractCppToPublicDisplay;
import uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.cpptoxhibit.AllCaseStatusCppToPublicDisplay;
import uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.cpptoxhibit.AllCourtStatusCppToPublicDisplay;
import uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.cpptoxhibit.CourtDetailCppToPublicDisplay;
import uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.cpptoxhibit.CourtListCppToPublicDisplay;
import uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.cpptoxhibit.DailyListCppToPublicDisplay;
import uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.cpptoxhibit.JuryCurrentStatusCppToPublicDisplay;
import uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.cpptoxhibit.SummaryByNameCppToPublicDisplay;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.AllCaseStatusValue;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.AllCourtStatusValue;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.CourtDetailValue;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.CourtListValue;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.JuryStatusDailyListValue;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.PublicDisplayValue;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.SummaryByNameValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings({"PMD", "squid:S3776"})
public final class CppDataSourceFactory {

    private static final String GET_NOT_BEFORE_TIME = "getNotBeforeTime";
    private static final String GET_LIST_COURT_ROOM_ID = "getListCourtRoomId";
    private static final String GET_JUDGE_NAME = "getJudgeName";
    private static final String GET_DEFENDANT_NAMES = "getDefendantNames";
    private static final String HAS_INFORMATION_FOR_DISPLAY = "hasInformationForDisplay";
    private static final String GET_CASE_NUMBER = "getCaseNumber";
    
    private static final Integer ONE = 1;

    protected CppDataSourceFactory() {
        // Protected constructor
    }

    /**
     * Returns a DataSource for the document specified.

     * @param shortName String
     * @param date Date
     * @param courtId int
     * @param courtRoomIds intArray
     * @return a DataSource.

     * @post return != null
     * @pre uri != null
     * @pre uri.getDocumentType() != null
     */
    public static AbstractCppToPublicDisplay getDataSource(String shortName, Date date, int courtId,
        int... courtRoomIds) {

        final Logger log = LoggerFactory.getLogger(GenericPublicDisplayDataSource.class);
        if (shortName == null) {
            log.debug("AbstractCppToPublicDisplay.getDataSource - Null shortname");
            return null;
        }

        if (CppDataType.COURTDETAIL_TYPE == CppDataType.fromString(shortName)) {
            return new CourtDetailCppToPublicDisplay(date, courtId, courtRoomIds);
        } else if (CppDataType.COURTLIST_TYPE == CppDataType.fromString(shortName)) {
            return new CourtListCppToPublicDisplay(date, courtId, courtRoomIds);
        } else if (CppDataType.DAILYLIST_TYPE == CppDataType.fromString(shortName)) {
            return new DailyListCppToPublicDisplay(date, courtId, courtRoomIds);
        } else if (CppDataType.ALLCOURTSTATUS_TYPE == CppDataType.fromString(shortName)) {
            return new AllCourtStatusCppToPublicDisplay(date, courtId, courtRoomIds);
        } else if (CppDataType.SUMMARYBYNAME_TYPE == CppDataType.fromString(shortName)) {
            return new SummaryByNameCppToPublicDisplay(date, courtId, courtRoomIds);
        } else if (CppDataType.ALLCASETSTATUS_TYPE == CppDataType.fromString(shortName)) {
            return new AllCaseStatusCppToPublicDisplay(date, courtId, courtRoomIds);
        } else if (CppDataType.JURYCURRENTSTATUS_TYPE == CppDataType.fromString(shortName)) {
            return new JuryCurrentStatusCppToPublicDisplay(date, courtId, courtRoomIds);
        } else {
            // document type is either null or not known
            log.error(
                "AbstractCppToPublicDisplay.getDataSource - {} - not known as a document type.",
                shortName);
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public static Collection<?> postProcessing(String shortName, Collection<?> data) {

        final Logger log = LoggerFactory.getLogger(GenericPublicDisplayDataSource.class);
        if (shortName == null) {
            log.debug("AbstractCppToPublicDisplay.postProcessing - Null shortname");
            return new ArrayList<>();
        }

        if (CppDataType.COURTDETAIL_TYPE == CppDataType.fromString(shortName)) {
            return getSortedCourtDetailValueList((List<CourtDetailValue>) data);
        } else if (CppDataType.COURTLIST_TYPE == CppDataType.fromString(shortName)) {
            List<CourtListValue> sorted = getSortedList((List<CourtListValue>) data);
            @SuppressWarnings("unchecked")
            List<CourtListValue> deduped =
                (List<CourtListValue>) (List<?>) dedupeByCoreIdentityCourtList(sorted);
            return deduped;
        } else if (CppDataType.DAILYLIST_TYPE == CppDataType.fromString(shortName)) {
            List<JuryStatusDailyListValue> sorted =
                getSortedList((List<JuryStatusDailyListValue>) data);
            return dedupeByCoreIdentity(sorted);
        } else if (CppDataType.ALLCOURTSTATUS_TYPE == CppDataType.fromString(shortName)) {
            return getSortedAllCourtStatusValueList((List<AllCourtStatusValue>) data);
        } else if (CppDataType.SUMMARYBYNAME_TYPE == CppDataType.fromString(shortName)) {
            return getSortedList((List<SummaryByNameValue>) data);
        } else if (CppDataType.ALLCASETSTATUS_TYPE == CppDataType.fromString(shortName)) {
            return getSortedList((List<AllCaseStatusValue>) data);
        } else if (CppDataType.JURYCURRENTSTATUS_TYPE == CppDataType.fromString(shortName)) {
            List<JuryStatusDailyListValue> sorted =
                getSortedList((List<JuryStatusDailyListValue>) data);
            return dedupeByCoreIdentity(sorted);
        } else {
            // document type is either null or not known
            log.error("AbstractCppToPublicDisplay.getDataSource - {}"
                + " - not known as a document type.", shortName);
        }
        return new ArrayList<>();
    }

    private static List<CourtDetailValue> getSortedCourtDetailValueList(
        List<CourtDetailValue> data) {
        // Sort the collection by court site, court room and then event time descending
        Collections.sort(data);

        // Remove Duplicates
        List<CourtDetailValue> newList = new ArrayList<>();

        // Track which (site, room) pairs we've already handled to avoid processing the same
        // courtroom multiple times (this localised guard addresses the observed 'first court'
        // duplicate symptom).
        final java.util.Set<String> processedKeys = new java.util.HashSet<>();

        int previousRoomNo = -1;
        String previousCourtSiteCode = "";

        for (CourtDetailValue value : data) {
            // Build a stable key for this room + site pair
            String site = value.getCourtSiteCode() == null ? "" : value.getCourtSiteCode();
            Integer roomNoObj = value.getCrestCourtRoomNo();
            String roomKey = roomNoObj == null ? "null" : String.valueOf(roomNoObj);
            String pairKey = site + ":" + roomKey;

            // If we've already processed this site+room, skip it.
            if (processedKeys.contains(pairKey)) {
                continue;
            }

            // First check we're not processing the same court room at the same court site multiple
            // times
            if (previousRoomNo != value.getCrestCourtRoomNo()
                || previousRoomNo == value.getCrestCourtRoomNo()
                    && !previousCourtSiteCode.equals(value.getCourtSiteCode())) {

                // Get all matching elements (existing helper)
                List<CourtDetailValue> matchingList = findAllObjects(value, data);
                if (matchingList.size() == ONE) {
                    newList.add(value);
                } else if (matchingList.size() > ONE) {
                    // Multiple matching courtroom records found. The collection is sorted to have
                    // the most recent first
                    // Find the most recent record with information to display
                    CourtDetailValue matchingValue = getMatchedCourtDetailValue(matchingList);

                    // If no matching record with information to display, then just use the first in
                    // the list
                    if (matchingValue == null) {
                        newList.add(matchingList.get(0));
                    } else {
                        newList.add(matchingValue);
                    }
                }

                // Update the previous room number and court site code
                previousRoomNo = value.getCrestCourtRoomNo();
                previousCourtSiteCode = value.getCourtSiteCode();

                // Mark this site+room as processed to avoid reprocessing later (fixes the "first
                // court" duplicate)
                processedKeys.add(pairKey);
            }
        }
        return newList;
    }


    private static List<AllCourtStatusValue> getSortedAllCourtStatusValueList(
        List<AllCourtStatusValue> data) {

        // 1. Ensure deterministic sort so candidates for the same room are contiguous
        // (sort by extracted room number, site code, eventTime desc)
        java.util.Comparator<AllCourtStatusValue> roomComparator = new java.util.Comparator<>() {
            private String nullSafe(final String s) {
                return s == null ? "" : s;
            }

            @Override
            public int compare(final AllCourtStatusValue a, final AllCourtStatusValue b) {
                Integer ra = getCourtRoomNumberFromNameOrCrest(a);
                Integer rb = getCourtRoomNumberFromNameOrCrest(b);
                int ia = ra == null ? Integer.MAX_VALUE : ra;
                int ib = rb == null ? Integer.MAX_VALUE : rb;
                int cmp = Integer.compare(ia, ib);
                if (cmp != 0) {
                    return cmp;
                }

                cmp = nullSafe(a.getCourtSiteCode()).compareTo(nullSafe(b.getCourtSiteCode()));
                if (cmp != 0) {
                    return cmp;
                }

                // eventTime descending (nulls last)
                java.time.LocalDateTime ta = a.getEventTime();
                java.time.LocalDateTime tb = b.getEventTime();
                if (ta == null && tb == null) {
                    return 0;
                }
                if (ta == null) {
                    return 1;
                }
                if (tb == null) {
                    return -1;
                }
                return tb.compareTo(ta);
            }
        };

        if (data != null && data.size() > 1) {
            data.sort(roomComparator);
        }

        // 2. Build one representative per (site,room) key
        Map<String, AllCourtStatusValue> chosenByKey = new LinkedHashMap<>();
        for (AllCourtStatusValue value : data) {
            Integer roomNo = getCourtRoomNumberFromNameOrCrest(value);
            String site = value.getCourtSiteCode() == null ? ""
                : value.getCourtSiteCode().trim().toUpperCase();
            String key = site + ":" + (roomNo == null ? "null" : roomNo.toString());

            AllCourtStatusValue existing = chosenByKey.get(key);
            if (existing == null) {
                chosenByKey.put(key, value);
                continue;
            }

            // prefer one that has information for display
            boolean hasInfo = safeHasInfo(value);
            boolean existingHasInfo = safeHasInfo(existing);
            if (hasInfo && !existingHasInfo) {
                chosenByKey.put(key, value);
                continue;
            } else if (!hasInfo && existingHasInfo) {
                continue;
            }

            // tie-break: prefer non-null case number
            String valueCase = value.getCaseNumber() == null ? "" : value.getCaseNumber();
            String existingCase = existing.getCaseNumber() == null ? "" : existing.getCaseNumber();
            if (!valueCase.isEmpty() && existingCase.isEmpty()) {
                chosenByKey.put(key, value);
                continue;
            } else if (valueCase.isEmpty() && !existingCase.isEmpty()) {
                continue;
            }

            // fallback: prefer non-null judge name if available (reflection helper used elsewhere)
            Object judgeObj = invokeGetter(value, GET_JUDGE_NAME);
            Object existingJudgeObj = invokeGetter(existing, GET_JUDGE_NAME);
            if (judgeObj != null && existingJudgeObj == null) {
                chosenByKey.put(key, value);
                continue;
            }

            // otherwise keep existing (first encountered from sorted list)
        }

        return new ArrayList<>(chosenByKey.values());
    }

    // Helper small utilities used above
    private static boolean safeHasInfo(final AllCourtStatusValue v) {
        try {
            return v.hasInformationForDisplay();
        } catch (Exception e) {
            return false;
        }
    }

    private static Integer getCourtRoomNumberFromNameOrCrest(final AllCourtStatusValue v) {
        if (v.getCrestCourtRoomNo() != null) {
            return v.getCrestCourtRoomNo();
        }
        String name = v.getCourtRoomName();
        return getCourtRoomNumberFromName(name); // existing helper in this class
    }


    private static int getCourtRoomNumberFromName(String courtRoomName) {
        String[] parts = courtRoomName.trim().split(" ");
        for (String part : parts) {
            try {
                return Integer.parseInt(part);
            } catch (NumberFormatException e) {
                // Not a number, continue
            }
        }
        return 1; // Default to 1 if no number found
    }

    private static <T extends PublicDisplayValue> List<T> getSortedList(List<T> data) {
        if (null != data && data.size() > ONE) {
            Collections.sort(data);
        }
        return data;
    }

    private static <T> List<T> findAllObjects(T obj, List<T> list) {
        final List<T> matchingList = new ArrayList<>();
        for (T match : list) {
            if (obj.equals(match)) {
                matchingList.add(match);
            }
        }
        return matchingList;
    }

    private static CourtDetailValue getMatchedCourtDetailValue(
        List<CourtDetailValue> matchingList) {
        for (CourtDetailValue matchingValue : matchingList) {
            if (matchingValue.hasInformationForDisplay()) {
                return matchingValue;
            }
        }
        return null;
    }

    private static AllCourtStatusValue getMatchedAllCourtStatusValue(
        List<AllCourtStatusValue> matchingList) {
        for (AllCourtStatusValue matchingValue : matchingList) {
            if (matchingValue.hasInformationForDisplay()) {
                return matchingValue;
            }
        }
        return null;
    }

    private static <T extends PublicDisplayValue> List<T> dedupeByCoreIdentity(List<T> data) {
        List<T> result = new ArrayList<>();

        for (T value : data) {
            // find group matches using core identity criteria
            List<T> matching = new ArrayList<>();
            for (T candidate : data) {
                boolean sameSite =
                    Objects.equals(value.getCourtSiteCode(), candidate.getCourtSiteCode());
                boolean sameRoom =
                    Objects.equals(value.getCrestCourtRoomNo(), candidate.getCrestCourtRoomNo());
                boolean sameFloating = Objects.equals(
                    (value instanceof JuryStatusDailyListValue
                        ? ((JuryStatusDailyListValue) value).getFloating()
                        : null),
                    (candidate instanceof JuryStatusDailyListValue
                        ? ((JuryStatusDailyListValue) candidate).getFloating()
                        : null));
                // compare caseNumber if present (via reflection/known getters) - assume T has
                // getCaseNumber()
                String caseA = null;
                String caseB = null;
                try {
                    caseA = (String) value.getClass().getMethod(GET_CASE_NUMBER).invoke(value);
                    caseB =
                        (String) candidate.getClass().getMethod(GET_CASE_NUMBER).invoke(candidate);
                } catch (Exception ignored) {
                    // ignore - treat as nulls
                }

                boolean sameCase =
                    (caseA != null && caseA.equals(caseB)) || (caseA == null && caseB == null
                        && Objects.equals(
                            // fall back to defendant names if case numbers absent
                            invokeGetter(value, GET_DEFENDANT_NAMES),
                            invokeGetter(candidate, GET_DEFENDANT_NAMES)));

                if (sameSite && sameRoom && sameFloating && sameCase) {
                    matching.add(candidate);
                }
            }

            if (!matching.isEmpty()) {
                // pick the best one
                T chosen = matching.get(0);
                for (T m : matching) {
                    // prefer one that has information for display
                    try {
                        java.lang.reflect.Method mth =
                            m.getClass().getMethod(HAS_INFORMATION_FOR_DISPLAY);
                        Boolean hasInfo = (Boolean) mth.invoke(m);
                        Boolean chosenHasInfo = (Boolean) chosen.getClass()
                            .getMethod(HAS_INFORMATION_FOR_DISPLAY).invoke(chosen);
                        if (hasInfo != null && hasInfo
                            && (chosenHasInfo == null || !chosenHasInfo)) {
                            chosen = m;
                        }
                    } catch (Exception ignored) {
                        // ignore
                    }
                    // prefer non-null judge if available
                    try {
                        Object judge = m.getClass().getMethod(GET_JUDGE_NAME).invoke(m);
                        Object chosenJudge =
                            chosen.getClass().getMethod(GET_JUDGE_NAME).invoke(chosen);
                        if (judge != null && chosenJudge == null) {
                            chosen = m;
                        }
                    } catch (Exception ignored) {
                        // ignore
                    }
                }

                if (!result.contains(chosen)) {
                    result.add(chosen);
                }
            }
        }
        return result;
    }


    private static <T extends PublicDisplayValue> List<T> dedupeByCoreIdentityCourtList(
        List<T> data) {
        // 1) Quick return for empty input
        if (data == null || data.isEmpty()) {
            return new ArrayList<>();
        }

        // 2) Remove exact duplicates" block with this ----
        List<T> unique = new ArrayList<>();
        for (T value : data) {
            boolean alreadypresent = false;

            for (T existing : unique) {

                // Same reference → definite duplicate
                if (existing == value) {
                    alreadypresent = true;
                    break;
                }

                // If equals() says they're equal, check key identity fields
                if (existing != null && existing.equals(value)) {
                    try {
                        Object existinglistroomid = invokeGetter(existing, GET_LIST_COURT_ROOM_ID);
                        Object valuelistroomid = invokeGetter(value, GET_LIST_COURT_ROOM_ID);
                        boolean samelistroom = Objects.equals(existinglistroomid, valuelistroomid);

                        Object existingnotbefore = invokeGetter(existing, GET_NOT_BEFORE_TIME);
                        Object valuenotbefore = invokeGetter(value, GET_NOT_BEFORE_TIME);
                        boolean samenotbefore = Objects.equals(existingnotbefore, valuenotbefore);

                        Object existingdefendants = invokeGetter(existing, GET_DEFENDANT_NAMES);
                        Object valuedefendants = invokeGetter(value, GET_DEFENDANT_NAMES);
                        boolean samedefendants =
                            Objects.equals(existingdefendants, valuedefendants);

                        if (samelistroom && samenotbefore && samedefendants) {
                            alreadypresent = true;
                            break;
                        }

                    } catch (Exception e) {
                        // If reflection fails, keep the item (conservative)
                    }
                }
            }

            if (!alreadypresent) {
                unique.add(value);
            }
        }


        // 3) Group conservatively into logical identities
        // - If BOTH items have a non-empty caseNumber -> group by caseNumber (strict)
        // - Otherwise, fall back to grouping only when site/room/floating AND listCourtRoomId
        // - AND notBeforeTime AND defendantNames match
        java.util.LinkedHashMap<String, java.util.List<T>> groups = new java.util.LinkedHashMap<>();

        for (T v : unique) {
            String site = String.valueOf(invokeGetter(v, "getCourtSiteCode")); // may be "null"
            Object crestObj = invokeGetter(v, "getCrestCourtRoomNo");
            String crest = crestObj == null ? "null" : String.valueOf(crestObj);
            String floating = null;
            if (v instanceof uk.gov.hmcts.pdda.common.publicdisplay.renderdata.JuryStatusDailyListValue) {
                Object flo = invokeGetter(v, "getFloating");
                floating = flo == null ? "null" : String.valueOf(flo);
            } else {
                floating = "null";
            }

            // gather identity components
            String caseNumber = (String) invokeGetter(v, GET_CASE_NUMBER);
            Object listCourtRoomIdObj = invokeGetter(v, GET_LIST_COURT_ROOM_ID);
            String listCourtRoomId =
                listCourtRoomIdObj == null ? "null" : String.valueOf(listCourtRoomIdObj);
            Object notBeforeObj = invokeGetter(v, GET_NOT_BEFORE_TIME);
            String notBefore = notBeforeObj == null ? "null" : String.valueOf(notBeforeObj);
            Object defNamesObj = invokeGetter(v, GET_DEFENDANT_NAMES);
            String defNames = defNamesObj == null ? "null" : defNamesObj.toString();

            // Build key:
            // - If caseNumber present, be strict: include caseNumber + listCourtRoomId + notBefore
            // + defendant names
            // - Otherwise use the conservative fallback including defendant
            // names/listRoom/notBefore
            String key;
            if (caseNumber != null && !caseNumber.trim().isEmpty()) {
                key = "CASE:" + caseNumber.trim() + ":LCR:" + listCourtRoomId + ":NB:" + notBefore
                    + ":DEF:" + defNames + ":SITE:" + site + ":ROOM:" + crest + ":FLOAT:"
                    + floating;
            } else {
                key = "FALLBACK:" + site + ":" + crest + ":" + floating + ":DEF:" + defNames
                    + ":LCR:" + listCourtRoomId + ":NB:" + notBefore;
            }
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(v);


            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(v);
        }

        // 4) For each group choose the best representative (existing preference rules)
        List<T> result = new ArrayList<>();
        java.util.Set<String> usedGroupKeys = new java.util.HashSet<>();

        for (java.util.Map.Entry<String, java.util.List<T>> entry : groups.entrySet()) {
            String groupKey = entry.getKey();
            java.util.List<T> group = entry.getValue();

            if (group == null || group.isEmpty()) {
                continue;
            }

            T chosen = group.get(0);

            for (T candidate : group) {
                // prefer hasInformationForDisplay()
                try {
                    java.lang.reflect.Method hasInfoM =
                        candidate.getClass().getMethod(HAS_INFORMATION_FOR_DISPLAY);
                    Boolean candHasInfo = (Boolean) hasInfoM.invoke(candidate);

                    java.lang.reflect.Method chosenHasInfoM =
                        chosen.getClass().getMethod(HAS_INFORMATION_FOR_DISPLAY);
                    Boolean chosenHasInfo = (Boolean) chosenHasInfoM.invoke(chosen);

                    if (candHasInfo != null && candHasInfo
                        && (chosenHasInfo == null || !chosenHasInfo)) {
                        chosen = candidate;
                    }
                } catch (Exception ignored) {
                    // ignore - method may not exist for some types
                }

                // prefer one with a non-null judge name
                try {
                    Object candJudge = invokeGetter(candidate, GET_JUDGE_NAME);
                    Object chosenJudge = invokeGetter(chosen, GET_JUDGE_NAME);
                    if (candJudge != null && chosenJudge == null) {
                        chosen = candidate;
                    }
                } catch (Exception ignored) {
                    // ignore
                }
            }

            // Use the group key to ensure exactly one chosen per group (avoid equals() semantics)
            if (!usedGroupKeys.contains(groupKey)) {
                result.add(chosen);
                usedGroupKeys.add(groupKey);
            }
        }


        return result;
    }


    private static Object invokeGetter(Object target, String method) {
        try {
            return target.getClass().getMethod(method).invoke(target);
        } catch (Exception e) {
            return null;
        }
    }

}
