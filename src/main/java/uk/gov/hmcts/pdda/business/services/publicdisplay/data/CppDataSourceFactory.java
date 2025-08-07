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
import java.util.List;

@SuppressWarnings("PMD.GodClass")
public final class CppDataSourceFactory {

    private static final String EMPTY_STRING = "";
    private static final Integer ONE = 1;

    protected CppDataSourceFactory() {
        // Protected constructor
    }

    /**
     * Returns a DataSource for the document specified.
     * 
     * @param shortName String
     * @param date Date
     * @param courtId int
     * @param courtRoomIds intArray
     * @return a DataSource.
     * 
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
            log.error("AbstractCppToPublicDisplay.getDataSource - {} - not known as a document type.", shortName);
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
            return getSortedList((List<CourtListValue>) data);
        } else if (CppDataType.DAILYLIST_TYPE == CppDataType.fromString(shortName)) {
            return getSortedList((List<JuryStatusDailyListValue>) data);
        } else if (CppDataType.ALLCOURTSTATUS_TYPE == CppDataType.fromString(shortName)) {
            return getSortedAllCourtStatusValueList((List<AllCourtStatusValue>) data);
        } else if (CppDataType.SUMMARYBYNAME_TYPE == CppDataType.fromString(shortName)) {
            return getSortedList((List<SummaryByNameValue>) data);
        } else if (CppDataType.ALLCASETSTATUS_TYPE == CppDataType.fromString(shortName)) {
            return getSortedList((List<AllCaseStatusValue>) data);
        } else if (CppDataType.JURYCURRENTSTATUS_TYPE == CppDataType.fromString(shortName)) {
            return getSortedList((List<JuryStatusDailyListValue>) data);
        } else {
            // document type is either null or not known
            log.error("AbstractCppToPublicDisplay.getDataSource - {}" + " - not known as a document type.", shortName);
        }
        return new ArrayList<>();
    }
    
    private static List<CourtDetailValue> getSortedCourtDetailValueList(List<CourtDetailValue> data) {
        // Sort the collection by court site, court room and then event time descending
        Collections.sort(data);

        // Remove Duplicates
        List<CourtDetailValue> newList = new ArrayList<>();
        int previousRoomNo = -1;
        String previousCourtSiteCode = "";
        for (CourtDetailValue value : data) {
            // First check we're not processing the same court room at the same court site
            // multiple
            // times
            if (previousRoomNo != value.getCrestCourtRoomNo() || previousRoomNo == value.getCrestCourtRoomNo()
                && !previousCourtSiteCode.equals(value.getCourtSiteCode())) {

                // Get all matching elements
                List<CourtDetailValue> matchingList =
                    findAllObjects(value, data);
                if (matchingList.size() == ONE) {
                    newList.add(value);
                } else if (matchingList.size() > ONE) {
                    // Multiple matching courtroom records found. The collection is sorted to
                    // have the most
                    // recent first
                    // Find the most recent record with information to display
                    CourtDetailValue matchingValue = getMatchedCourtDetailValue(matchingList);

                    // If no matching record with information to display, then just use the
                    // first in the
                    // list
                    if (matchingValue == null) {
                        newList.add(matchingList.get(0));
                    } else {
                        newList.add(matchingValue);
                    }
                }

                // Update the previous room number and court site code
                previousRoomNo = value.getCrestCourtRoomNo();
                previousCourtSiteCode = value.getCourtSiteCode();
            }
        }
        return newList;
    }
    
    private static List<AllCourtStatusValue> getSortedAllCourtStatusValueList(List<AllCourtStatusValue> data) {
        // Sort the collection by court site, court room and then event time descending
        Collections.sort(data);

        // Remove Duplicates
        List<AllCourtStatusValue> newList = new ArrayList<>();
        int previousRoomNo = -1;
        String previousCourtSiteCode = EMPTY_STRING;
        for (AllCourtStatusValue value : data) {
            // First check we're not processing the same court room at the same court site
            // multiple
            // times
            if (previousRoomNo != value.getCrestCourtRoomNo() || previousRoomNo == value.getCrestCourtRoomNo()
                && !previousCourtSiteCode.equals(value.getCourtSiteCode())) {

                // Get all matching elements
                List<AllCourtStatusValue> matchingList =
                    findAllObjects(value, data);
                if (matchingList.size() == ONE) {
                    newList.add(value);
                } else if (matchingList.size() > ONE) {
                    // Multiple matching courtroom records found. The collection is sorted to
                    // have the most
                    // recent first
                    // Find the most recent record with information to display
                    AllCourtStatusValue matchingValue = getMatchedAllCourtStatusValue(matchingList);

                    // If no matching record with information to display, then just use the
                    // first in the
                    // list
                    if (matchingValue == null) {
                        newList.add(matchingList.get(0));
                    } else {
                        newList.add(matchingValue);
                    }
                }

                // Update the previous room number and court site code
                previousRoomNo = value.getCrestCourtRoomNo();
                previousCourtSiteCode = value.getCourtSiteCode();
            }
        }
        return newList;   
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

    private static CourtDetailValue getMatchedCourtDetailValue(List<CourtDetailValue> matchingList) {
        for (CourtDetailValue matchingValue : matchingList) {
            if (matchingValue.hasInformationForDisplay()) {
                return matchingValue;
            }
        }
        return null;
    }

    private static AllCourtStatusValue getMatchedAllCourtStatusValue(List<AllCourtStatusValue> matchingList) {
        for (AllCourtStatusValue matchingValue : matchingList) {
            if (matchingValue.hasInformationForDisplay()) {
                return matchingValue;
            }
        }
        return null;
    }
}
